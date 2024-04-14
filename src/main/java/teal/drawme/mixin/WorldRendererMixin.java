package teal.drawme.mixin;

import com.google.common.collect.Iterators;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import teal.drawme.network.VideoPlayer;

import java.util.Iterator;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;"
        )
    )
    private Iterator<Entity> concatEntities(Iterable<Entity> instance) {
        return Iterators.concat(instance.iterator(), VideoPlayer.getEns().iterator());
    }
}
