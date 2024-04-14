package teal.drawme.shrink;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Style;
import org.joml.Matrix4f;
import teal.drawme.Drawme;

public class DrawerExt extends TextRenderer.Drawer {

    public DrawerExt(TextRenderer drawer, VertexConsumerProvider vertexConsumers, float x, float y, int color, boolean shadow, Matrix4f matrix, TextRenderer.TextLayerType layerType, int light) {
        drawer.super(vertexConsumers, x, y, color, shadow, matrix, layerType, light);
    }

    @Override
    public boolean accept(int i, Style style, int j) {
        boolean a = super.accept(i, style, j);
        if (Drawme.config.doSquishText()) super.x -= 1;
        return a;
    }

}