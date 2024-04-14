package teal.drawme.shrink.mixin;

import net.minecraft.client.font.Glyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import teal.drawme.Drawme;

@Mixin(Glyph.class)
public interface GlyphMixin extends Glyph {
    @Redirect(
        method = "getAdvance(Z)F",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/font/Glyph;getAdvance()F")
    )
    private float getAdvance(Glyph instance) {
        return instance.getAdvance() + Drawme.config.getGlyphAdvanceOffset();
    }
}
