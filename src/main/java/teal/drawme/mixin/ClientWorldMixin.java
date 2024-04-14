package teal.drawme.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.EntityList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import teal.drawme.network.VideoPlayer;

import java.util.function.Consumer;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
    @Redirect(
        method = "tickEntities",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/EntityList;forEach(Ljava/util/function/Consumer;)V"
        )
    )
    private void tickMore(EntityList instance, Consumer<Entity> action) {
        instance.forEach(action);
        VideoPlayer.getEns().forEach(Entity::tick);
    }
}
