package teal.drawme.util;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.DefaultPosArgument;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import teal.drawme.command.argument.BetterStringArgument;
import teal.drawme.command.suggestion.SuggestWidth;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static teal.drawme.Drawme.*;

public final class Images {

    public static final String defFill = "█";

    public static final MutableText shittyInput = Text.literal("Bad image input. Provide image(s) in ")
        .append(
            Text.literal(base.getAbsolutePath())
                .setStyle(Style.EMPTY.withUnderline(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, base.getAbsolutePath()))
                )
        );

    public static BufferedImage getSeq(File file) throws CommandSyntaxException {
        try {
            return Objects.requireNonNull(ImageIO.read(file));
        } catch (NullPointerException | IOException ignored) {
        }
        throw new SimpleCommandExceptionType(shittyInput).create();
    }

    public static boolean playAudio(CommandContext<FabricClientCommandSource> ctx) {
        try {
            return !BoolArgumentType.getBool(ctx, "mute");
        } catch (IllegalArgumentException IAE) {
            return true;
        }
    }

    public static int[] getRes(CommandContext<FabricClientCommandSource> ctx, BufferedImage image) {
        int resX = SuggestWidth.max, resY;
        try {
            resX = IntegerArgumentType.getInteger(ctx, "resize_x");
            resY = IntegerArgumentType.getInteger(ctx, "resize_y");
        } catch (IllegalArgumentException IAE) {
            float factor = image.getWidth() / (float) resX;
            resY = Math.round(image.getHeight() / factor);
        }
        return new int[]{resX, resY};
    }

    public static double getScale(CommandContext<FabricClientCommandSource> ctx) {
        try {
            return DoubleArgumentType.getDouble(ctx, "scale");
        } catch (IllegalArgumentException IAE) {
            return 1D;
        }
    }

    public static float[] getYawPitch(CommandContext<FabricClientCommandSource> ctx) {
        float yaw = client.player.getHeadYaw() + 180F, pitch = config.useDisplayPitch() ? client.player.getPitch() : 0f;
        try {
            yaw = FloatArgumentType.getFloat(ctx, "yaw");
            pitch = FloatArgumentType.getFloat(ctx, "pitch");
        } catch (IllegalArgumentException ignored) {
        }
        return new float[]{yaw % 360F, pitch % 360F};
    }

    public static Vec3d getPosition(CommandContext<FabricClientCommandSource> ctx) {
        Vec3d location = client.player.getPos();
        try {
            DefaultPosArgument def = ctx.getArgument("pos", DefaultPosArgument.class);
            Vec3d vec3d = ctx.getSource().getPosition();
            location = new Vec3d(def.x.toAbsoluteCoordinate(vec3d.x), def.y.toAbsoluteCoordinate(vec3d.y), def.z.toAbsoluteCoordinate(vec3d.z));
        } catch (IllegalArgumentException ignored) {

        }
        return location;
    }

    public static DisplayEntity.BillboardMode getBillboardMode(CommandContext<FabricClientCommandSource> ctx) {
        try {
            return DisplayEntity.BillboardMode.valueOf(BetterStringArgument.getString(ctx, "billboard_mode"));
        } catch (Exception ignored) {
        }
        return DisplayEntity.BillboardMode.FIXED;
    }

    public static boolean[] getRGB(CommandContext<FabricClientCommandSource> ctx) {
        boolean r, g, b;
        r = g = b = true;
        try {
            r = !BoolArgumentType.getBool(ctx, "nr");
            g = !BoolArgumentType.getBool(ctx, "ng");
            b = !BoolArgumentType.getBool(ctx, "nb");
        } catch (IllegalArgumentException ignored) {
        }
        return new boolean[]{r, g, b};
    }

    public static String getFill(CommandContext<FabricClientCommandSource> ctx) {
        String fill;
        try {
            fill = StringArgumentType.getString(ctx, "fill");
        } catch (IllegalArgumentException | NullPointerException EXC) {
            return defFill;
        }
        return fill;
    }

    public static List<MutableText> readImage(final BufferedImage orig, final int x, final int y, final String fill, final boolean[] rgb) {
        BufferedImage scale = new BufferedImage(x, y, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = scale.createGraphics();
        g.drawImage(orig, 0, 0, x, y, null);
        g.dispose();
        byte[] pix = ((DataBufferByte) scale.getRaster().getDataBuffer()).getData();
        int w = scale.getWidth();
        int h = scale.getHeight();
        final int pixelLength = 3;
        List<MutableText> texts = Arrays.asList(new MutableText[h]);
        MutableText temp = Text.empty();
        for (int pos = 0, row = 0, col = 0; pos + 2 < pix.length; pos += pixelLength) {
            temp.append(Text.literal(fill).setStyle(Style.EMPTY.withColor(
                (rgb[2] ? (pix[pos] & 0xff) : 0) +
                (rgb[1] ? ((pix[pos + 1] & 0xff) << 8) : 0) +
                (rgb[0] ? ((pix[pos + 2] & 0xff) << 16) : 0)
            )));
            col++;
            if (col == w) {
                texts.set(h - 1 - row, temp);
                temp = Text.empty();
                col = 0;
                row++;
            }
        }

        return texts;
    }
}
