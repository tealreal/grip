package teal.drawme.command.video;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static teal.drawme.Drawme.playing;

public interface Stop extends Command<FabricClientCommandSource> {
    @Override
    default int run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        if (!playing) throw new SimpleCommandExceptionType(Text.literal("Not playing a video")).create();
        playing = false;
        context.getSource().sendFeedback(Text.literal("Video playback was stopped."));
        return 0;
    }

    static Stop get() {
        return new Stop() {
        };
    }
}