package teal.drawme.command.video;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.IOUtils;
import teal.drawme.Drawme;
import teal.drawme.command.argument.BetterStringArgument;

import java.io.File;
import java.nio.charset.Charset;

import static teal.drawme.Drawme.logger;
import static teal.drawme.command.suggestion.SuggestVideoWidth.findFFmpegTool;

public interface Processor extends Command<FabricClientCommandSource> {

    @Override
    default int run(CommandContext<FabricClientCommandSource> context) {
        String videoFile = BetterStringArgument.getString(context, "video");
        String folderName = BetterStringArgument.getString(context, "folder");
        final File output = new File(Drawme.base + "/" + folderName);
        final File audio = new File(Drawme.base + "/" + folderName + ".wav");
        if ((output.exists() && (output.isDirectory() && output.listFiles().length > 0)) || audio.exists()) {
            context.getSource().sendError(Text.literal("Video folder and/or audio file already exists."));
            return -1;
        }

        output.mkdirs();
        int width = 0;
        double fps = Drawme.config.getFps();
        try {
            width = IntegerArgumentType.getInteger(context, "width");
            fps = DoubleArgumentType.getDouble(context, "fps");
        } catch (IllegalArgumentException ignored) {
        }

        final String ffmpeg = findFFmpegTool("ffmpeg");
        final CommandLine converter = new CommandLine(ffmpeg)
            .addArgument("-i").addArgument(videoFile)
            .addArgument("-r").addArgument(Double.toString(fps));

        if (width > 0) converter.addArgument("-vf").addArgument("scale=" + width + ":-1");
        converter.addArgument("-compression_level").addArgument("100")
            .addArgument("-q:v").addArgument("5")
            .addArgument("-crf").addArgument("24")
            .addArgument("./" + folderName + "/%06d.jpg");

        final CommandLine audioMuxer = new CommandLine(ffmpeg)
            .addArgument("-i").addArgument(videoFile)
            .addArgument("-q:a").addArgument("0")
            .addArgument("-vn")
            .addArgument("./" + folderName + ".wav");

        new Thread(() -> {
            try {
                logger.info("Converting video");
                logger.info("Working directory: {}", Drawme.base.getAbsolutePath());
                logger.info("Video Args: {}", String.join(" ", converter.toStrings()));
                logger.info("Audio Args: {}", String.join(" ", audioMuxer.toStrings()));
                context.getSource().sendFeedback(Text.literal("Writing image sequence to ")
                    .append(
                        Text.literal(output.getAbsolutePath())
                            .setStyle(Style.EMPTY
                                .withUnderline(true)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, output.getAbsolutePath()))
                            )
                    )
                );

                Process vid = new ProcessBuilder(converter.toStrings()).directory(Drawme.base).start();
                logger.info("Log for video\n{}", IOUtils.toString(vid.getErrorStream(), Charset.defaultCharset()));
                Process aud = new ProcessBuilder(audioMuxer.toStrings()).directory(Drawme.base).start();
                logger.info("Log for audio\n{}", IOUtils.toString(aud.getErrorStream(), Charset.defaultCharset()));
                context.getSource().sendFeedback(Text.literal("Finished writing audio and images."));
            } catch (Exception e) {
                logger.error("Error with FFmpeg/FFprobe processes", e);
                context.getSource().sendError(Text.literal("An error occurred with FFmpeg/FFprobe."));
            }
        }).start();

        return 0;
    }

    static Processor get() {
        return new Processor() {
        };
    }
}
