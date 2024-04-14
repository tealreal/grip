package teal.drawme.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import teal.drawme.network.VideoPlayer;
import teal.drawme.util.Images;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static teal.drawme.Drawme.config;


@Mixin(DisplayEntity.TextDisplayEntity.class)
public abstract class TextDisplayEntityMixin extends DisplayEntity {
    @Shadow
    @Nullable
    private DisplayEntity.TextDisplayEntity.TextLines textLines;
    @Unique
    private final static List<MutableText> lines = new ArrayList<>();
    @Unique
    private final static boolean[] RGB = new boolean[3];
    @Unique
    private final static int[] res = new int[2];
    @Unique
    private static String frameInfo = "";
    @Unique
    private static String videoInfo = "";
    @Unique
    private Boolean isNBT = null;


    @Shadow
    protected abstract Text getText();

    public TextDisplayEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
        method = "splitLines",
        at = @At("HEAD"),
        cancellable = true
    )
    private void splitLines(DisplayEntity.TextDisplayEntity.LineSplitter splitter, CallbackInfoReturnable<DisplayEntity.TextDisplayEntity.TextLines> cir) {
        if (!config.doSummonSP()) return;
        if (isNBT != null && !isNBT) {
            isNBT = getText().getString().startsWith(VideoPlayer.nbtTag);
            return;
        }
        isNBT = true;
        String reference = getText().getString();
        int pipeIndex = reference.indexOf('|');
        try {
            int line = Integer.parseInt(
                pipeIndex < 0 ? reference.substring(VideoPlayer.nbtTag.length()) : reference.substring(VideoPlayer.nbtTag.length(), pipeIndex));
            if (line < 0) return;
            if (line == 0) {
                // RGB
                RGB[0] = reference.charAt(pipeIndex + 1) == 'X';
                RGB[1] = reference.charAt(pipeIndex + 2) == 'X';
                RGB[2] = reference.charAt(pipeIndex + 3) == 'X';

                // Resolution
                pipeIndex = reference.indexOf('|', pipeIndex + 1);
                res[0] = Integer.parseInt(reference.substring(pipeIndex + 1, (pipeIndex = reference.indexOf('|', pipeIndex + 1))));
                res[1] = Integer.parseInt(reference.substring(pipeIndex + 1, (pipeIndex = reference.indexOf('|', pipeIndex + 1))));
                // Fill
                int fillLength = Integer.parseInt(reference.substring(pipeIndex + 1, (pipeIndex = reference.indexOf('|', pipeIndex + 1))));
                String fill = reference.substring(pipeIndex + 1, pipeIndex + fillLength + 1);
                // Image path
                reference = reference.substring(pipeIndex + fillLength + 1);
                if (!frameInfo.equals(reference)) {
                    frameInfo = reference;
                    String folder = new File(frameInfo).getPath();
                    if (!videoInfo.equals(folder)) videoInfo = folder;
                    lines.clear();
                    lines.addAll(Images.readImage(ImageIO.read(new File(frameInfo)), res[0], res[1], fill, RGB));
                }
            }
            textLines = splitter.split(lines.get(line), Integer.MAX_VALUE);
            cir.setReturnValue(textLines);
        } catch (Exception ignored) {
            // Spams log if errors
        }
    }
}

