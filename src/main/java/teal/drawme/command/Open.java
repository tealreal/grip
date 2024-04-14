package teal.drawme.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import static teal.drawme.Drawme.base;

public interface Open extends Command<FabricClientCommandSource> {

    @Override
    default int run(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(
            Text.literal("Click to open the drawme folder.")
                .setStyle(Style.EMPTY
                    .withUnderline(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, base.getAbsolutePath()))
                )
        );
        return 0;
    }

    static Open get() {
        return new Open() {
        };
    }
}
