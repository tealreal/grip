package teal.drawme.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import teal.drawme.Drawme;

public class ModMenu implements ModMenuApi {

    private static final Config VirginConfig = new Config();

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (parent) -> {
            ConfigBuilder cb = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Drawme Config"))
                .setSavingRunnable(Drawme.config::write);

            ConfigCategory options = cb.getOrCreateCategory(Text.literal("Me"));
            ConfigEntryBuilder ceb = cb.entryBuilder();
            options.addEntry(
                ceb.startDoubleField(Text.literal("FPS"), Drawme.config.getFps())
                    .setMin(0.1)
                    .setDefaultValue(VirginConfig.getFps())
                    .setTooltip(Text.literal("Sets the default playback speed for things, i.e. title screen video."))
                    .setSaveConsumer(Drawme.config::setFps)
                    .build()
            );

            options.addEntry(
                ceb.startFloatField(Text.literal("Video Volume"), Drawme.config.getVol())
                    .setMin(0.0F)
                    .setDefaultValue(VirginConfig.getVol())
                    .setTooltip(Text.literal("Sets the volume for videos. 1.0 is the default value, and anything louder than 2.0 will blast your ears off."))
                    .setSaveConsumer(Drawme.config::setVol)
                    .build()
            );

            options.addEntry(
                ceb.startBooleanToggle(Text.literal("Display Text Pitch"), Drawme.config.useDisplayPitch())
                    .setDefaultValue(VirginConfig.useDisplayPitch())
                    .setTooltip(Text.literal("Whether the default display pitch should be based off of the player's pitch, or be 0 by default."))
                    .setSaveConsumer(Drawme.config::setDisplayPitch)
                    .build()
            );

            options.addEntry(
                ceb.startBooleanToggle(Text.literal("Embed watermark"), Drawme.config.useWatermark())
                    .setDefaultValue(VirginConfig.useWatermark())
                    .setTooltip(Text.literal("Sets a tag on every text display called \"drawme\" to be removed via /kill for easy removal."))
                    .setSaveConsumer(Drawme.config::setUseWatermark)
                    .build()
            );

            options.addEntry(
                ceb.startBooleanToggle(Text.literal("Validate suggestions"), Drawme.config.doValidateSuggestions())
                    .setDefaultValue(VirginConfig.doValidateSuggestions())
                    .setTooltip(Text.literal("Check command suggestions to make sure an image or video can be read. May include lag spikes."))
                    .setSaveConsumer(Drawme.config::setValidateSuggestions)
                    .build()
            );

            options.addEntry(
                ceb.startBooleanToggle(Text.literal("Run title screen Easter egg"), Drawme.config.useTitleScreenEasterEgg())
                    .setDefaultValue(VirginConfig.useTitleScreenEasterEgg())
                    .setTooltip(Text.literal("Special title screen Easter egg, random chance of a video appearing (Put it under the ./drawme/_splash/ folder!)"))
                    .setSaveConsumer(Drawme.config::setTitleScreenEasterEgg)
                    .build()
            );
            options.addEntry(
                ceb.startBooleanToggle(Text.literal("Run videos on title screen only"), Drawme.config.useTitleScreenOnly())
                    .setDefaultValue(VirginConfig.useTitleScreenOnly())
                    .setTooltip(Text.literal("Chooses a random folder from the drawme folder, and plays that video. (Negates above option if this is on)"))
                    .setSaveConsumer(Drawme.config::setTitleScreenOnly)
                    .build()
            );

            options.addEntry(
                ceb.startBooleanToggle(Text.literal("Summon entities in singleplayer for playback"), Drawme.config.doSummonSP())
                    .setDefaultValue(VirginConfig.doSummonSP())
                    .setTooltip(Text.literal("This summons in real text displays for the server edit instead of mixins. As a result, this is only for integrated servers [singleplayer] and may cause choppy playback."))
                    .setSaveConsumer(Drawme.config::setSummonSP)
                    .build()
            );

            options.addEntry(
                ceb.startBooleanToggle(Text.literal("Ignore metadata"), Drawme.config.doIgnoreMetadata())
                    .setDefaultValue(VirginConfig.doIgnoreMetadata())
                    .setTooltip(Text.literal("When a text display's text begins with a specific string, it will not render the line of the frame provided."))
                    .setSaveConsumer(Drawme.config::setIgnoreMetadata)
                    .build()
            );

            options.addEntry(
                ceb.startBooleanToggle(Text.literal("Squish text ")
                            .append(Text.literal("!").setStyle(Style.EMPTY.withBold(true).withColor(Formatting.RED)))
                        , Drawme.config.doSquishText())
                    .setDefaultValue(VirginConfig.doSquishText())
                    .setTooltip(Text.literal("Used to make video playback appear better by removing gaps in between characters, affects all text displays regardless if they are created by drawme."))
                    .setSaveConsumer(Drawme.config::setSquishText)
                    .build()
            );

            // this one messes with ALL in-game text
            options.addEntry(
                ceb.startFloatField(Text.literal("Glyph Advance Offset ")
                            .append(Text.literal("!").setStyle(Style.EMPTY.withBold(true).withColor(Formatting.RED)))
                        , Drawme.config.getGlyphAdvanceOffset())
                    .setDefaultValue(VirginConfig.getGlyphAdvanceOffset())
                    .setTooltip(Text.literal("Changes spacing in between individual characters for every text in the game. Use -1 to perfectly join all characters."))
                    .setSaveConsumer(Drawme.config::setGlyphAdvanceOffset)
                    .build()
            );

            return cb.build();
        };
    }

}