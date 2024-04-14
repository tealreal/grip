package teal.drawme.mixin;

import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import teal.drawme.Drawme;
import teal.drawme.network.VideoPlayer;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin {

    @Inject(
        method = "stop",
        at = @At("HEAD")
    )
    private void stop(boolean waitForShutdown, CallbackInfo ci) {
        VideoPlayer v = VideoPlayer.instance;
        if (v != null && Drawme.playing) {
            Drawme.playing = false;
        }
    }

}
