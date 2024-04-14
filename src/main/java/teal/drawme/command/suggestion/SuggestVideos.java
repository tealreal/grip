package teal.drawme.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import static teal.drawme.Drawme.base;
import static teal.drawme.Drawme.config;

public class SuggestVideos implements SuggestionProvider<FabricClientCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        File[] d = base.listFiles();
        if (d == null) return builder.buildFuture();
        for (File f : d) {
            if (f.isFile()) {
                if (config.doValidateSuggestions() && SuggestVideoWidth.getWidth(f.getAbsolutePath()) == -1) continue;
                builder.suggest(f.getName());
            }
        }
        return builder.buildFuture();
    }

    public static SuggestVideos get() {
        return new SuggestVideos();
    }
}
