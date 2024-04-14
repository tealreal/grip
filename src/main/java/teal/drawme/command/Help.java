package teal.drawme.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface Help extends Command<FabricClientCommandSource> {
    @Override
    default int run(CommandContext<FabricClientCommandSource> context) {
        context.getSource().getPlayer().sendMessage(getType().message, false);
        return 0;
    }

    Guide getType();

    static Help get(Guide type) {
        return () -> type;
    }

    enum Guide {
        Draw(
            "draw <image> [<billboard_mode>] [<resize_x>] [<resize_y>] [<scale>] [<interval>] [<yaw>] [<pitch>] [<nr>] [<ng>] [<nb>] [<fill>]",
            """
                Draw an image using text displays.
                • <image> is the path to your image.
                • <billboard_mode> determines how the display will rotate as you move yourself around it. Check the manual or README to see how the different modes affect your view.
                • <resize_x> resizes the width of the image. You may see 3 numbers: The original width, a number smaller than 480 (max recommended height is 480), or 480 (max recommended width is 480).
                • <resize_y> resizes the height of the image. Leaving this blank will scale the height down appropriately. Suggestions may be generated based off of <resize_x>.
                • <scale> affects how big/small the image will be, but not the actual resolution of it.
                • <interval> indicates how frequently to place an entity. Not needed for singleplayer, default is your ping. The higher this value is, the more likely the entire image will be placed.
                • <yaw> changes the x rotation of the image.
                • <pitch> changes the y rotation of the image.
                • <nr> indicates whether to disable the RED channel
                • <ng> indicates whether to disable the GREEN channel
                • <nb> indicates whether to disable the BLUE channel
                • <fill> indicates what character to fill the names with."""
        ),
        Play(
            "play <image> [<billboard_mode>] [<mute>] [<resize_x>] [<resize_y>] [<scale>] [<fps>] [<yaw>] [<pitch>] [<nr>] [<ng>] [<nb>] [<fill>]",
            """
                Play back video using text displays.
                The command parameters are similar to draw, the only difference being...
                • <image> is the name of the folder with images inside. See the manual on how to set it up.
                • <mute> will determine whether the attached video audio should be played. Audio will play by default, and volume is independent of Minecraft's sound system.
                • <pos> indicates where the bottom of the video is placed.
                • <fps> is the frame rate to play the image sequence at. The default frame rate is 30 FPS.
                """
        ),
        Abort(
            "drawme abort",
            """
                Stops drawing an image.
                """
        ),
        Pause(
            "drawme pause",
            """
                Pauses a video.
                """
        ),
        Stop(
            "drawme stop",
            """
                Stops a video from playing.
                """
        ),
        Open(
            "drawme open",
            """
                Simple text to open the drawme folder.
                """
        ),
        Process(
            "drawme process <video> <folder> [<width>] [<fps>]",
            """
                Processes a video in the drawme folder to then be played. FFmpeg is required to use this command.
                Video frames AND audio are both extracted.
                • <video> is the name of the video in your drawme folder.
                • <folder> is the name of the folder AND audio file to dump video and audio to.
                • <width> is the new width of the image sequence.
                • <fps> is the frame rate to write the image sequence as.
                """
        ),
        Manual(
            "Manual",
            """
                & Creating images:
                    • This mod does not make any network requests to receive images. All files must be local and under the drawme folder in your minecraft folder.
                & Video folders:
                    • Video folders are a sequence of images. Use ffmpeg on a video to extract the frames into a folder, OR use the drawme process command.
                & Audio file:
                    • When you allow audio on the "play" command, it will play an audio file with the same name of the folder that contains the image sequence.
                    • For example, if your folder is located at ./folder/music/ where music is a folder containing an image sequence, then the audio file should be located at ./folder/music.wav.
                    • The drawme process command will also export audio if there is an audio channel.
                & Servers:
                    • Draw is usable on servers, while play is clientside. On an integrated server, play can be used serverside, but tends to be more laggy than the clientside option (see modmenu config).
                    • An interval is recommended for servers when using draw, otherwise there will be missing lines of the image.
                    • This mod cannot remove "draw" armorstands. "play" armorstands' names are removed after playback is complete or "stop" is called.
                & Multitasking:
                    • It is not possible to play multiple videos at once given the current code, nor is it possible to draw multiple images at once.
                & Mod Menu (Cloth Config required):
                    • You can access the configuration for this mod via Mod Menu. Options include changing the Default FPS, Video Volume, Easter Eggs for the Title Screen, and gap-removing toggles.
                & Billboard Modes:
                    • FIXED: Rotation is static, backside is invisible. (Best option for images)
                    • VERTICAL: Rotates as you move around it, has no backside. (Best option for videos)
                    • HORIZONTAL: Does not "rotate" as you move around it. May have gaps in-between each line.
                    • CENTER: Allows both vertical and horizontal rotation; behavior is analogous to armorstand holograms.
                For technical details, see the repository's README.
                """,
            true
        );
        public final MutableText message;

        Guide(String header, String contents) {
            this(header, contents, false);
        }

        Guide(String header, String contents, boolean highlights) {
            message = Text.literal(header + "\n\n").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(true));
            if (highlights) {
                String[] a = contents.split("\\n");
                for (String l : a) {
                    if (l.startsWith("&"))
                        message.append(Text.literal(l + '\n').setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false).withBold(true)));
                    else
                        message.append(Text.literal(l + '\n').setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(true).withBold(false)));
                }
            } else {
                message.append(Text.literal(contents).setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(true).withBold(false)));
            }
        }
    }
}
