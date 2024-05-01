package teal.drawme.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.IOUtils;
import org.spongepowered.include.com.google.common.base.Charsets;

import java.io.*;

import static teal.drawme.Drawme.logger;

public class Config {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File config = FabricLoader.getInstance().getConfigDir().resolve("drawme.config").toFile();

    private double fps;
    private float volume;

    private boolean displayPitch;

    private boolean doWatermark;
    private boolean validateSuggestions;

    private boolean titleScreenEasterEgg;
    private boolean titleScreenOnly;

    private boolean summonEntitiesSP;
    private boolean ignoreMetadata;

    private boolean squishText;
    private float glyphAdvanceOffset;

    public Config() {
        this.fps = 30.0;
        this.volume = 1.0f;
        this.displayPitch = false;
        this.doWatermark = false;
        this.validateSuggestions = false;
        this.titleScreenEasterEgg = false;
        this.titleScreenOnly = false;
        this.summonEntitiesSP = false;
        this.ignoreMetadata = false;
        this.squishText = false;
        this.glyphAdvanceOffset = 0.f;
    }

    public void setFps(double fps) {
        this.fps = fps;
    }

    public double getFps() {
        return fps;
    }

    public void setVol(float volume) {
        this.volume = volume;
    }

    public float getVol() {
        return volume;
    }

    public boolean useDisplayPitch() {
        return displayPitch;
    }

    public void setDisplayPitch(boolean displayPitch) {
        this.displayPitch = displayPitch;
    }

    public boolean useTitleScreenEasterEgg() {
        return titleScreenEasterEgg;
    }

    public void setTitleScreenEasterEgg(boolean titleScreenEasterEgg) {
        this.titleScreenEasterEgg = titleScreenEasterEgg;
    }

    public boolean useTitleScreenOnly() {
        return titleScreenOnly;
    }

    public void setTitleScreenOnly(boolean titleScreenOnly) {
        this.titleScreenOnly = titleScreenOnly;
    }

    public boolean useWatermark() {
        return doWatermark;
    }

    public void setUseWatermark(boolean doWatermark) {
        this.doWatermark = doWatermark;
    }

    public boolean doValidateSuggestions() {
        return validateSuggestions;
    }

    public void setValidateSuggestions(boolean validateSuggestions) {
        this.validateSuggestions = validateSuggestions;
    }

    public boolean doSummonSP() {
        return summonEntitiesSP;
    }

    public void setSummonSP(boolean summonEntitiesSP) {
        this.summonEntitiesSP = summonEntitiesSP;
    }

    public boolean doIgnoreMetadata() {
        return ignoreMetadata;
    }

    public void setIgnoreMetadata(boolean ignoreMetadata) {
        this.ignoreMetadata = ignoreMetadata;
    }

    public boolean doSquishText() {
        return squishText;
    }

    public void setSquishText(boolean squishText) {
        this.squishText = squishText;
    }

    public float getGlyphAdvanceOffset() {
        return glyphAdvanceOffset;
    }

    public void setGlyphAdvanceOffset(float glyphAdvanceOffset) {
        this.glyphAdvanceOffset = glyphAdvanceOffset;
    }

    public void write() {
        try {
            Writer writer = new FileWriter(config);
            gson.toJson(this, writer);
            writer.flush();
            writer.close();
        } catch (IOException IOE) {
            logger.error("Error writing to config.", IOE);
        }
    }

    public static Config get() {
        Config obj = new Config();
        try {
            try {
                FileInputStream FIS = new FileInputStream(config);
                obj = gson.fromJson(IOUtils.toString(FIS, Charsets.UTF_8), Config.class);
                if (obj == null) {
                    obj = new Config();
                    throw new JsonSyntaxException("");
                }
            } catch (JsonSyntaxException e) {
                Writer writer = new FileWriter(config);
                gson.toJson(new Config(), writer);
                writer.flush();
                writer.close();
            }
        } catch (IOException ignored) {
        }
        return obj;
    }
}
