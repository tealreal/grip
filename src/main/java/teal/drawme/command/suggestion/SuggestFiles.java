package teal.drawme.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import teal.drawme.Drawme;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.concurrent.CompletableFuture;

import static teal.drawme.Drawme.base;

public class SuggestFiles implements SuggestionProvider<FabricClientCommandSource> {

    private final boolean checkForVideo;

    public SuggestFiles(boolean checkForVideo) {
        this.checkForVideo = checkForVideo;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        File[] d = base.listFiles();
        if (d == null) return builder.buildFuture();
        for (File f : d) {
            if (
                !(checkForVideo ? f.isDirectory() : f.isFile()) ||
                f.getName().contains(" ") ||
                (Drawme.config.doValidateSuggestions() && !isValid(f, checkForVideo))
            )
                continue;
            builder.suggest(f.getName());
        }
        return builder.buildFuture();
    }

    public static boolean isValid(File file, boolean isVideo) {
        try {
            return ImageIO.read(isVideo ? file.listFiles()[0] : file) != null;
        } catch (Exception ignored) {
        }
        return false;
    }

    public static SuggestFiles get(boolean video) {
        return new SuggestFiles(video);
    }
}
