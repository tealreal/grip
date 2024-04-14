package teal.drawme.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.ThreadExecutor;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import teal.drawme.util.Images;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static teal.drawme.Drawme.*;

public class VideoPlayer extends TimerTask {

    private final int resX;
    private final int resY;
    private final boolean replayMod;
    private final boolean[] RGB;
    private final String fill;
    private final File[] images;
    @Nullable
    private final Clip clip;
    @Nullable
    private ServerWorld sw = null;

    public void setServer(ServerWorld server) {
        this.sw = server;
    }

    private final ThreadExecutor<? extends Runnable> runner;

    private int frame = 0;

    private final long interval;

    public long getInterval() {
        return interval;
    }

    private static List<Entity> ens = new ArrayList<>();

    public static List<Entity> getEns() {
        return ens;
    }

    private boolean fuckoff = false;

    public boolean shouldFuckoff() {
        return fuckoff;
    }

    public void forceFuckOff() {
        this.fuckoff = true;
    }

    @Nullable
    public static VideoPlayer instance;
    public static final String nbtTag = "drawer-ig-play";

    public VideoPlayer(DisplayEntity.BillboardMode billboardMode, int resX, int resY, double scale, boolean[] RGB, Vec3d location, float[] yp, String fill, File[] images, @Nullable Clip audio, long interval, boolean replayMod) {
        assert client.player != null;
        this.resX = resX;
        this.resY = resY;
        this.RGB = RGB;
        this.fill = fill;
        this.images = images;
        this.interval = interval;
        this.replayMod = replayMod;
        this.clip = audio;

        try {
            sw = Objects.requireNonNull(client.getServer()).getWorld(Objects.requireNonNull(client.world).getRegistryKey());
        } catch (NullPointerException NPE) {
            // \_(tsu)_/
        } finally {
            replayMod = replayMod && sw != null;
        }

        runner = replayMod ? sw.getServer() : client;
        final double height = scale * ((double) client.textRenderer.getWidth(fill) - (config.doSquishText() ? fill.length() : fill.length() * config.getGlyphAdvanceOffset())) / 40.d;
        final double offsetY = height * Math.cos(Math.toRadians(yp[1]));
        final double offsetZ = height * Math.sin(Math.toRadians(yp[1])) * Math.cos(Math.toRadians(yp[0]));
        final double offsetX = height * Math.sin(Math.toRadians(yp[1])) * -Math.sin(Math.toRadians(yp[0]));
        assert client.world != null;
        for (int i = 0; i < resY; i++) {
            DisplayEntity.TextDisplayEntity en = EntityType.TEXT_DISPLAY.create(replayMod ? sw : client.world);
            en.setBackground(0x1A);
            en.setLineWidth(Integer.MAX_VALUE);
            en.setBrightness(Brightness.FULL);
            en.setBillboardMode(billboardMode);
            en.getDataTracker().set(DisplayEntity.SCALE, new Vector3f((float) scale));
            en.setYaw(yp[0]);
            en.setPitch(yp[1]);
            en.setPosition(location);
            en.resetPosition();
            if (replayMod && i > 0) en.setText(Text.literal(nbtTag + i));
            location = location.add(offsetX, offsetY, offsetZ);
            ens.add(en);
        }
        instance = this;
        playing = true;
        if (replayMod)
            sw.getServer().executeSync(() -> {
                assert sw != null;
                sw.addEntities(ens.stream());
            });
        else
            se = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this, 0L, interval, TimeUnit.NANOSECONDS);
    }

    public void cleanup() {
        se.cancel(true);
        instance = null;
        playing = false;
        paused = false;
        if (clip != null) clip.stop();
        if (replayMod)
            for (Entity en : ens)
                runner.executeSync(() -> en.remove(Entity.RemovalReason.DISCARDED));
        ens = new ArrayList<>();
        frame = 0;
    }

    @Override
    public void run() {
        if (!playing || client.world == null || frame >= images.length) {
            cleanup();
            return;
        }
        if (paused || client.isPaused()) {
            if (clip != null) clip.stop();
            return;
        } else if (clip != null) clip.start();
        try {
            List<MutableText> lines = null;
            if (!replayMod)
                lines = Images.readImage(ImageIO.read(images[frame]), resX, resY, fill, RGB);
            for (int x = 0; x < ens.size(); x++) {
                DisplayEntity.TextDisplayEntity en = (DisplayEntity.TextDisplayEntity) ens.get(x);
                if (en.isRemoved() || (replayMod && x > 0)) continue;
                Text t = replayMod ?
                    Text.literal(nbtTag + x + '|' +
                                 (RGB[0] ? "X" : "-") +
                                 (RGB[1] ? "X" : "-") +
                                 (RGB[2] ? "X" : "-") + '|' +
                                 resX + '|' + resY + '|' + fill.length() + '|' + fill +
                                 images[frame].getAbsolutePath().substring(new File("").getAbsolutePath().length() + 1))
                    : lines.get(x);
                // Prevents MC from crashing when ticking entities : id rather NOT concurrentmodificationexception.
                runner.executeSync(() -> en.setText(t));
            }
        } catch (Exception e) {
            // bad frame, neglect error
            // e.printStackTrace();
        }
        ++frame;
    }

}