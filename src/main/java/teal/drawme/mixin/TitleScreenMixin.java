package teal.drawme.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import teal.drawme.Drawme;
import teal.drawme.shrink.DrawerExt;
import teal.drawme.shrink.mixin.TextRendererAccessor;
import teal.drawme.util.Images;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static teal.drawme.util.Images.defFill;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    @Shadow
    @Nullable
    private SplashTextRenderer splashText;
    @Unique
    private File FOI = null;
    @Unique
    private boolean preservedSplash = false;
    @Unique
    private int frameNo = 0;
    @Unique
    private boolean playVideo = false;

    @Unique
    @Nullable
    private List<MutableText> lineCache = null;

    @Unique
    @Nullable
    private File FOICache = null;

    @Unique
    private float SCALE = 0.F;
    @Unique
    private final Random random = new Random();

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(
        method = "init",
        at = @At("HEAD")
    )
    private void init(CallbackInfo ci) {
        if ((!Drawme.config.useTitleScreenEasterEgg() && !Drawme.config.useTitleScreenOnly()) || playVideo || preservedSplash)
            return;
        int size = client.getSplashTextLoader().splashTexts.size();
        if (Drawme.config.useTitleScreenOnly() || random.nextInt(size + 1) == size) preservedSplash = true;
        else return;
        File[] imgs;
        if (Drawme.config.useTitleScreenOnly()) {
            if (!Drawme.base.isDirectory()) return;
            List<File> folders = Arrays.stream(Drawme.base.listFiles()).filter(File::isDirectory).toList();
            imgs = folders.get(random.nextInt(folders.size())).listFiles();
        } else
            imgs = new File(Drawme.base.getAbsolutePath() + "/_splash").listFiles();
        if (FOI != null || imgs == null || imgs.length == 0) return;
        try {
            Arrays.sort(imgs);
            BufferedImage img;
            for (File imf : imgs) {
                try {
                    img = ImageIO.read(imf);
                    if (img != null) break;
                } catch (IOException ignored) {
                }
            }
            double FPS = 1000.00 / Drawme.config.getFps();

            SCALE = this.client.options.getGuiScale().getValue() % 6;
            if (SCALE == 0) SCALE = 1F / 6;
            else SCALE = 1F / SCALE;

            playVideo = true;
            if (Drawme.config.useTitleScreenOnly() || Drawme.config.useTitleScreenEasterEgg())
                this.splashText = new SplashTextRenderer("Draw me.");
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
                if (!playVideo) return;
                if (this.client != null && this.client.currentScreen != this) return;
                if (imgs.length - 1 < frameNo) frameNo = 0;
                FOI = imgs[frameNo];
                frameNo++;
            }, 0, Math.round(FPS * 1E3), TimeUnit.MICROSECONDS);
        } catch (Exception ignore) {

        }
    }

    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/RotatingCubeMapRenderer;render(FF)V"
        )
    )
    private void z(RotatingCubeMapRenderer instance, float delta, float alpha) {
        if (FOI == null || !playVideo)
            instance.render(delta, alpha);
    }

    @Inject(
        method = "render",
        at = @At("TAIL")
    )
    private void s(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (FOI == null || !playVideo) {
            return;
        }
        context.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
        try {
            context.getMatrices().scale(SCALE, SCALE, SCALE);
            List<MutableText> lines;
            if (FOI.equals(FOICache)) {
                lines = lineCache;
            } else {
                FOICache = FOI;
                BufferedImage img = ImageIO.read(FOI);
                // int smallestScreen = Math.min(this.width, this.height);
                lines = Images.readImage(
                    img,
                    (int) Math.ceil((float) this.width / (this.textRenderer.getWidth(defFill) - (defFill.length())) / SCALE),
                    (int) Math.ceil((float) this.height / (this.textRenderer.fontHeight - 1) / SCALE),
                    // (int) Math.ceil((double) smallestScreen / (this.textRenderer.getWidth(defFill) - (defFill.length())) / SCALE),
                    // (int) Math.ceil((double) smallestScreen / (this.textRenderer.fontHeight - 1) / SCALE),
                    defFill,
                    new boolean[]{true, true, true}
                );
                lineCache = lines;
            }
            for (int i = 0; i < lines.size(); i++) {
                MutableText txt = lines.get(lines.size() - i - 1);
                TextRendererAccessor tra = (TextRendererAccessor) textRenderer;
                int color = tra.callTweakTransparency(0xffffff);
                Matrix4f matrix4f = new Matrix4f(context.getMatrices().peek().getPositionMatrix());
                DrawerExt drawer = new DrawerExt(textRenderer, context.getVertexConsumers(), 0, i * (textRenderer.fontHeight - 1), color, false, matrix4f, TextRenderer.TextLayerType.NORMAL, 15728880);
                //DrawerExt drawer = new DrawerExt(textRenderer, context.getVertexConsumers(), context.getScaledWindowWidth() / 2F, i * (textRenderer.fontHeight - 1), color, false, matrix4f, TextRenderer.TextLayerType.NORMAL, 15728880);
                txt.asOrderedText().accept(drawer);
                drawer.drawLayer(0x0, 0);
            }
        } catch (Exception ignored) {
        } finally {
            context.getMatrices().scale(1 / SCALE, 1 / SCALE, 1 / SCALE);
        }
    }

}