package teal.drawme.shrink.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.DisplayEntityRenderer;
import net.minecraft.text.OrderedText;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import teal.drawme.shrink.DrawerExt;

@Mixin(DisplayEntityRenderer.TextDisplayEntityRenderer.class)
public abstract class TextDisplayEntityRenderer {
    @Redirect(
        method = "render(Lnet/minecraft/entity/decoration/DisplayEntity$TextDisplayEntity;Lnet/minecraft/entity/decoration/DisplayEntity$TextDisplayEntity$Data;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"
        )
    )
    private int draw(TextRenderer instance, OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextRenderer.TextLayerType layerType, int backgroundColor, int light) {
        TextRendererAccessor tra = (TextRendererAccessor) instance;
        color = tra.callTweakTransparency(color);
        Matrix4f matrix4f = new Matrix4f(matrix);
        DrawerExt drawer = new DrawerExt(instance, vertexConsumers, x, y, color, shadow, matrix4f, layerType, light);
        text.accept(drawer);
        x = drawer.drawLayer(backgroundColor, x);
        return (int) x + (shadow ? 1 : 0);
    }
}