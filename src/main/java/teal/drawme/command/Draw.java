package teal.drawme.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import teal.drawme.command.argument.BetterStringArgument;
import teal.drawme.util.Images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import static teal.drawme.Drawme.*;

public interface Draw extends Command<FabricClientCommandSource> {

    @Override
    default int run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        if (client.player == null || client.interactionManager == null)
            throw new SimpleCommandExceptionType(Text.literal("Player not found")).create();
        if (drawing || playing)
            throw new SimpleCommandExceptionType(Text.literal("Wait for other drawing to finish.")).create();
        if (!context.getSource().getPlayer().isCreative())
            throw new SimpleCommandExceptionType(Text.literal("Must be in creative mode")).create();

        BufferedImage image = Images.getSeq(new File(base.getAbsolutePath() + '/' + BetterStringArgument.getString(context, "image")));
        DisplayEntity.BillboardMode billboard = Images.getBillboardMode(context);
        int[] res = Images.getRes(context, image);

        double scale = Images.getScale(context);
        boolean[] RGB = Images.getRGB(context);
        float[] yp = Images.getYawPitch(context);
        String fill = Images.getFill(context);

        final List<MutableText> lines = Images.readImage(image, res[0], res[1], fill, RGB);
        final double height = scale * ((double) client.textRenderer.getWidth(fill) - (config.doSquishText() ? fill.length() : fill.length() * config.getGlyphAdvanceOffset())) / 40.d;
        final double offsetY = height * Math.cos(Math.toRadians(yp[1]));
        final double offsetZ = height * Math.sin(Math.toRadians(yp[1])) * Math.cos(Math.toRadians(yp[0]));
        final double offsetX = height * Math.sin(Math.toRadians(yp[1])) * -Math.sin(Math.toRadians(yp[0]));
        final int slot = client.player.getInventory().selectedSlot + 36;
        ItemStack prevItem = client.player.getInventory().getStack(slot);
        drawing = client.world != null;
        Vec3d pos = client.player.getPos();

        @Nullable PlayerListEntry ple = client.player.getPlayerListEntry();
        int interval = client.getServer() == null && (client.getCurrentServerEntry() == null || !client.getCurrentServerEntry().isLocal()) ? (ple == null ? 250 : ple.getLatency() + 200) : 0;
        try {
            interval = IntegerArgumentType.getInteger(context, "interval");
        } catch (IllegalArgumentException ignored) {
        }
        int finalInterval = interval;
        new Thread(() -> {
            long st = System.currentTimeMillis();
            double x = pos.x;
            double y = pos.y;
            double z = pos.z;
            boolean error = false;
            if (client.player.getAbilities().allowFlying)
                client.player.getAbilities().flying = true;
            try {
                final ItemStack asi = new ItemStack(Items.SNOW_GOLEM_SPAWN_EGG);
                int i = 0;
                for (MutableText txt : lines) {
                    if (!drawing) break;
                    client.player.sendMessage(Text.literal("Placed: " + i + '/' + lines.size() + " entities."), true);
                    StringBuilder nbt = new StringBuilder("{EntityTag:{id:");
                    // apparently this is faster
                    nbt.append("\"text_display\"").append(',')
                        .append("brightness:{block:15,sky:15},background:26,NoGravity:1b,line_width:")
                        .append(Integer.MAX_VALUE).append(',')
                        .append("Rotation:[")
                        .append(yp[0]).append("f,")
                        .append(yp[1]).append("f],")
                        .append("transformation:{left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f],scale:[")
                        .append(scale).append(",")
                        .append(scale).append(",")
                        .append(scale)
                        .append("],translation:[0.0f,0.0f,0.0f]},")
                        .append("billboard:\"").append(billboard.asString()).append("\",")
                        .append("text:'")
                        .append(Text.Serializer.toJson(txt)).append("',");

                    if (config.useWatermark()) nbt.append("Tags:[\"drawme\"],");
                    nbt.append("Pos:[")
                        .append(x).append(",")
                        .append(y).append(",")
                        .append(z).append("]")
                        .append("}}");

                    asi.setNbt(StringNbtReader.parse(nbt.toString()));

                    // guarantee position of entity
                    BlockPos bp = client.player.getBlockPos().down();
                    x += offsetX;
                    z += offsetZ;
                    y += offsetY;

                    // "guarantee" placement
                    client.player.setPosition(new Vec3d(x, y, z));
                    client.player.getInventory().selectedSlot = slot - 36;
                    client.interactionManager.clickCreativeStack(asi, slot);
                    client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.of(bp), Direction.UP, bp, false));
                    client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
                    i++;
                    if (finalInterval > 0) Thread.sleep(finalInterval);
                }
            } catch (Exception e) {
                logger.error("Error drawing image", e);
                error = true;
            } finally {
                client.player.sendMessage(Text.literal((error ? "Aborted drawing!" : "Finished drawing!") + " Time elapsed: " + (System.currentTimeMillis() - st) / 1000L + "s."), true);
                client.interactionManager.clickCreativeStack(prevItem, slot);
                drawing = false;
            }
        }).start();
        return 0;
    }

    static Draw get() {
        return new Draw() {
        };
    }
}