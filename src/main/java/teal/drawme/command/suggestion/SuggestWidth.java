package teal.drawme.command.suggestion;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import teal.drawme.command.argument.BetterStringArgument;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.CompletableFuture;

import static teal.drawme.Drawme.base;

public class SuggestWidth implements SuggestionProvider<FabricClientCommandSource> {

    public static final int max = 480;

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        String imgLoc = BetterStringArgument.getString(context, "image");
        try {
            File f = new File(base.getAbsolutePath() + '/' + imgLoc);
            if (f.isDirectory() && f.listFiles().length > 0) f = f.listFiles()[0];
            BufferedImage image = ImageIO.read(f);
            if (image == null) return builder.buildFuture();
            int width = image.getWidth();
            int height = image.getHeight();
            builder.suggest(width);

            if (width > max) {
                builder.suggest(max);
            }
            if (height > max) {
                float factor = (float) height / max;
                builder.suggest(Math.round(width / factor));
            }

        } catch (Exception ignored) {
        }
        return builder.buildFuture();
    }

    public static SuggestWidth getW() {
        return new SuggestWidth();
    }

    public static SuggestHeight getH() {
        return new SuggestHeight();
    }
}

class SuggestHeight implements SuggestionProvider<FabricClientCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        String imgLoc = BetterStringArgument.getString(context, "image");
        int widthArg = IntegerArgumentType.getInteger(context, "resize_x");
        try {
            File f = new File(base.getAbsolutePath() + '/' + imgLoc);
            if (f.isDirectory() && f.listFiles().length > 0) f = f.listFiles()[0];
            BufferedImage image = ImageIO.read(f);
            if (image == null) return builder.buildFuture();
            int width = image.getWidth();
            int height = image.getHeight();
            // one less option since the user is unlikely to skip width, then set height
            // builder.suggest(height);

            float factor = (float) width / widthArg;
            builder.suggest(Math.round(height / factor));

        } catch (Exception ignored) {
        }
        return builder.buildFuture();
    }
}