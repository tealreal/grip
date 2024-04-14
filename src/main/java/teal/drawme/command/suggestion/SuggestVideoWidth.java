package teal.drawme.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.versioning.ComparableVersion;
import teal.drawme.command.argument.BetterStringArgument;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static teal.drawme.Drawme.base;

public class SuggestVideoWidth implements SuggestionProvider<FabricClientCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        String videoFile = BetterStringArgument.getString(context, "video");
        int width = getWidth(videoFile);
        if (width != -1) builder.suggest(width);
        return builder.buildFuture();
    }

    public static int getWidth(String videoFile) {
        final CommandLine probe = new CommandLine(findFFmpegTool("ffprobe"))
            .addArgument("-v").addArgument("error")
            .addArgument("-select_streams").addArgument("v")
            .addArgument("-show_entries").addArgument("stream=width")
            .addArgument("-of").addArgument("csv=p=0:s=x")
            .addArguments(videoFile);
        try {
            Process process = new ProcessBuilder(probe.toStrings()).directory(base).start();
            String by = IOUtils.toString(process.getInputStream(), Charset.defaultCharset());
            return Integer.parseInt(by.trim());
        } catch (IOException | NumberFormatException ignored) {
        }
        return -1;
    }

    public static SuggestVideoWidth get() {
        return new SuggestVideoWidth();
    }

    // This is ReplayMod code. It's GPL v3.0, so fuck it I can probably use it. thx u replay mod.
    // https://github.com/ReplayMod/ReplayMod/blob/stable/src/main/java/com/replaymod/render/RenderSettings.java#L316
    public static String findFFmpegTool(String name) {
        switch (Util.getOperatingSystem()) {
            case WINDOWS:
                File dotMinecraft = MinecraftClient.getInstance().runDirectory;
                File inDotMinecraft = new File(dotMinecraft, "ffmpeg/bin/" + name + ".exe");
                if (inDotMinecraft.exists()) {
                    return inDotMinecraft.getAbsolutePath();
                }
                try {
                    Path[] result = new Path[1];
                    Files.walkFileTree(dotMinecraft.toPath(), new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            if ((name + ".exe").equals(file.getFileName().toString())) {
                                result[0] = file;
                                return FileVisitResult.TERMINATE;
                            }
                            return super.visitFile(file, attrs);
                        }
                    });
                    if (result[0] != null) {
                        return result[0].toAbsolutePath().toString();
                    }
                } catch (IOException ignored) {
                }
                break;
            case OSX:
                // The PATH doesn't seem to be set as expected on OSX, therefore we check some common locations ourselves
                for (String path : new String[]{"/usr/local/bin/" + name, "/usr/bin/" + name}) {
                    File file = new File(path);
                    if (file.exists()) return path;
                }
                // Homebrew doesn't seem to reliably symlink its installed binaries either
                // and there's multiple locations for where Homebrew is.
                for (String path : new String[]{"/usr/local", "/opt/homebrew"}) {
                    File homebrewFolder = new File(path + "/Cellar/ffmpeg");
                    String[] homebrewVersions = homebrewFolder.list();
                    if (homebrewVersions == null) {
                        continue;
                    }
                    Optional<File> latestOpt = Arrays.stream(homebrewVersions)
                        .map(ComparableVersion::new) // Convert file name to comparable version
                        .sorted(Comparator.reverseOrder()) // Sort for latest version
                        .map(ComparableVersion::toString) // Convert back to file name
                        .map(v -> new File(new File(homebrewFolder, v), "bin/" + name)) // Convert to binary files
                        .filter(File::exists) // Filter invalid installations (missing executable)
                        .findFirst(); // Take first one
                    if (latestOpt.isPresent()) {
                        File latest = latestOpt.get();
                        return latest.getAbsolutePath();
                    }
                }
                break;
        }
        return name;
    }
}