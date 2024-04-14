package teal.drawme.command.video;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import teal.drawme.command.argument.BetterStringArgument;
import teal.drawme.network.Messages;
import teal.drawme.network.VideoPlayer;
import teal.drawme.util.Images;

import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static teal.drawme.Drawme.*;
import static teal.drawme.util.Images.shittyInput;

public interface Play extends Command<FabricClientCommandSource> {

    @Override
    default int run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        if (client.player == null || client.world == null)
            throw new SimpleCommandExceptionType(Text.literal("Player not found")).create();
        if (drawing || playing)
            throw new SimpleCommandExceptionType(Text.literal("Wait for other drawing to finish.")).create();

        File[] imgs = new File(baseName + '/' + BetterStringArgument.getString(context, "image")).listFiles();
        @Nullable File audioFile = new File(base.getAbsolutePath() + '/' + BetterStringArgument.getString(context, "image") + ".wav");
        if (imgs == null || imgs.length == 0) throw new SimpleCommandExceptionType(shittyInput).create();
        BufferedImage image = Images.getSeq(imgs[0]);

        DisplayEntity.BillboardMode billboard = Images.getBillboardMode(context);
        int[] res = Images.getRes(context, image);
        double scale = Images.getScale(context);

        double fps = config.getFps();
        try {
            fps = DoubleArgumentType.getDouble(context, "fps");
        } catch (IllegalArgumentException ignored) {
        }
        fps = 1000 / fps;
        boolean[] RGB = Images.getRGB(context);

        Vec3d location = Images.getPosition(context);
        float[] yp = Images.getYawPitch(context);
        String fill = Images.getFill(context);

        Clip audio = null;
        if (Images.playAudio(context)) {
            try {
                if (audioFile.exists() && audioFile.isFile()) {
                    audio = AudioSystem.getClip();
                    AudioInputStream as = AudioSystem.getAudioInputStream(audioFile);
                    audio.open(as);
                    ((FloatControl) audio.getControl(FloatControl.Type.MASTER_GAIN))
                        .setValue(20f * (float) Math.log10(config.getVol()));
                }
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ignored) {
                context.getSource().sendError(Text.literal("Could not load audio."));
            }
        }

        Arrays.sort(imgs);
        boolean doServer = config.doSummonSP() && client.isIntegratedServerRunning();
        context.getSource().sendFeedback(Text.literal("Starting playback..."));
        new VideoPlayer(billboard, res[0], res[1], scale, RGB, location, yp, fill, imgs, audio, Math.round(fps * 1E6), doServer);
        if (doServer) {
            ClientPlayNetworking.send(Messages.PLAY,
                PacketByteBufs.create()
            );
        }
        return 0;
    }

    static Play get() {
        return new Play() {
        };
    }
}