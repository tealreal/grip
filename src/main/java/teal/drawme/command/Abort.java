package teal.drawme.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static teal.drawme.Drawme.drawing;

public interface Abort extends Command<FabricClientCommandSource> {

    @Override
    default int run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        if (drawing) {
            drawing = false;
            context.getSource().sendFeedback(Text.literal("Aborting..."));
        } else {
            throw new SimpleCommandExceptionType(Text.literal("You are not currently drawing.")).create();
        }
        return 0;
    }

    static Abort get() {
        return new Abort() {
        };
    }
}
