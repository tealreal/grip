package teal.drawme.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.decoration.DisplayEntity;

import java.util.concurrent.CompletableFuture;

public class SuggestBillboardMode implements SuggestionProvider<FabricClientCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        for (DisplayEntity.BillboardMode mode : DisplayEntity.BillboardMode.values())
            builder.suggest(mode.name());
        return builder.buildFuture();
    }

    public static SuggestBillboardMode get() {
        return new SuggestBillboardMode();
    }
}
