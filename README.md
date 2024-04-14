# Draw me.

Mod that draws thing.

## Downloads

See [releases](https://github.com/tealreal/grip/releases).

## How to use

To use: 
1) Open drawme folder using [`drawme open`](#drawme-open).
2) Place images and videos inside
3) To place...
    - Images: Know what image you want to place and refer to [`draw`](#draw-image-billboard_mode-resize_x-resize_y-scale-interval-yaw-pitch-nr-ng-nb-fill).
      - Placing images on servers can be aborted using [`drawme abort`](#drawme-abort). DO NOT USE [`drawme stop`](#drawme-stop), it is for aborting videos.
    - Videos: Find the video, [`drawme process`](#drawme-process-video-folder-width-fps) it if necessary, then [`play`](#play-image-billboard_mode-mute-resize_x-resize_y-scale-fps-yaw-pitch-nr-ng-nb-fill) it.
      - Pause the video using [`drawme pause`](#drawme-pause).
      - Stop the video using [`drawme stop`](#drawme-stop).
4) Get help with `drawme help`.

### See the video demo

[![drawing and videos in minecraft](https://img.youtube.com/vi/O5T0OrR37fw/maxres1.jpg)](https://www.youtube.com/watch?v=O5T0OrR37fw)

### Some Images

![VLC Icon](https://i.ibb.co/7nQBdLW/vlc.png)
![Flowers](https://i.ibb.co/PCvLQry/flowers.png)
![Sad Face](https://i.ibb.co/MMyt2Pf/sad-face.png)
![Commonplace AL](https://i.ibb.co/PQ1xL98/commonplace-dupe-pixels-highlighted.png)

## Commands

### `draw <image> [<billboard_mode>] [<resize_x>] [<resize_y>] [<scale>] [<interval>] [<yaw>] [<pitch>] [<nr>] [<ng>] [<nb>] [<fill>]`
Draw an image using text displays.
- `<image>` is the path to your image.
- `<billboard_mode>` determines how the display will rotate as you move yourself around it. Check the manual or README to see how the different modes affect your view.
- `<resize_x>` resizes the width of the image. You may see 3 numbers: The original width, a number smaller than 480 (max recommended height is 480), or 480 (max recommended width is 480).
- `<resize_y>` resizes the height of the image. Leaving this blank will scale the height down appropriately. Suggestions may be generated based off of <resize_x>.
- `<scale>` affects how big/small the image will be, but not the actual resolution of it.
- `<interval>` indicates how frequently to place an entity. Not needed for singleplayer, default is your ping. The higher this value is, the more likely the entire image will be placed.
- `<yaw>` changes the x rotation of the image.
- `<pitch>` changes the y rotation of the image.
- `<nr>` indicates whether to disable the RED channel
- `<ng>` indicates whether to disable the GREEN channel
- `<nb>` indicates whether to disable the BLUE channel
- `<fill>` indicates what character to fill the names with.

### `play <image> [<billboard_mode>] [<mute>] [<resize_x>] [<resize_y>] [<scale>] [<fps>] [<yaw>] [<pitch>] [<nr>] [<ng>] [<nb>] [<fill>]`
Play back video using text displays.
The command parameters are similar to draw, the only difference being...
- `<image>` is the name of the folder with images inside. See the manual on how to set it up.
- `<mute>` will determine whether the attached video audio should be played. Audio will play by default, and volume is independent of Minecraft's sound system.
- `<pos>` indicates where the bottom of the video is placed.
- `<fps>` is the frame rate to play the image sequence at. The default frame rate is 30 FPS.

### `drawme abort`
Stops drawing an image.

### `drawme pause`
Pauses a video.

### `drawme stop`
Stops a video from playing.

### `drawme open`
Simple text to open the drawme folder.

### `drawme process <video> <folder> [<width>] [<fps>]`
Processes a video in the drawme folder to then be played. FFmpeg is required to use this command.
Video frames AND audio are both extracted.
- `<video>` is the name of the video in your drawme folder.
- `<folder>` is the name of the folder AND audio file to dump video and audio to.
- `<width>` is the new width of the image sequence.
- `<fps>` is the frame rate to write the image sequence as.
You can view your FFmpeg log in the Minecraft log to debug.

## Manual and Technical Details
### Mod Menu Configuration
If you need to fine-tune things like glyph gaps or compatibility with the Replay Mod, you can customize options in the mod menu (only available w/ mod menu and cloth config). Available configurations:

|                      Option                      |   Type    |                                                                               Description                                                                                | Default |
|:------------------------------------------------:|:---------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-------:|
|                     **FPS**                      | `Double`  |                                                   Sets the default playback speed for things, i.e. title screen video.                                                   |  30.0   |
|                 **Video Volume**                 |  `Float`  |                               Sets the volume for videos. 1.0 is the default value, and anything louder than 2.0 will blast your ears off.                               |   1.0   |
|              **Display Text Pitch**              | `Boolean` |                                     Whether the default display pitch should be based off of the player's pitch, or be 0 by default.                                     | `false` |
|               **Embed watermark**                | `Boolean` |                                        Sets a tag on every text display called "drawme" to be removed via /kill for easy removal.                                        | `false` |
|             **Validate suggestions**             | `Boolean` |                                      Check command suggestions to make sure an image or video can be read. May include lag spikes.                                       | `false` |
|         **Run title screen Easter egg**          | `Boolean` |                             Special title screen Easter egg, random chance of a video appearing (Put it under the ./drawme/_splash/ folder!)                             | `false` |
|       **Run videos on title screen only**        | `Boolean` |                                Chooses a random folder from the drawme folder, and plays that video. (Negates above option if this is on)                                | `false` |
| **Summon entities in singleplayer for playback** | `Boolean` | This summons in real text displays for the server edit instead of mixins. As a result, this is only for integrated servers [singleplayer] and may cause choppy playback. | `false` |
|                 **Squish text**                  | `Boolean` |          Used to make video playback appear better by removing gaps in between characters, affects all text displays regardless if they are created by drawme.           | `false` |
|             **Glyph Advance Offset**             |  `Float`  |                          Changes spacing in between individual characters for every text in the game. Use -1 to perfectly join all characters.                           |  30.0   |

### Creating images
This mod does not make any network requests to receive images. All files must be under the drawme folder in your minecraft folder.

### Video folders
Video folders are a sequence of images. Use ffmpeg on a video to extract the frames into a folder, OR use the `drawme process` command. The command that drawme uses looks like this:

```
ffmpeg -i <video> -r <framerate> -vf scale=<desired image width>:-1 -compression_level 100 -q:v 5 ./folder/%06d.jpg
```

This command exports frames in a compressed format that do not take up too much disk space. The files are sorted using [`Arrays.sort()`](https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html#sort-java.lang.Object:A-), so format your filenames as numbers. Please play videos at a width/length LESS than 480, maybe even 240 because of lag.

#### Audio file
When you allow audio on the [`play`](#play-image-billboard_mode-mute-resize_x-resize_y-scale-fps-yaw-pitch-nr-ng-nb-fill) command, it will play an audio file with the same name of the folder that contains the image sequence. For example, if your folder is located at `./folder/music/` where music is a folder containing an image sequence, then the audio file should be located at `./folder/music.wav`. The wav extension should be lowercase, and the filename is case-sensitive. Drawme uses this ffmpeg command to extract audio:

```
ffmpeg -i <video> -qscale 0 -vn ./folder/<name>.wav
``` 

This command exports the audio at the same quality while removing the video codec. WAV is an uncompressed lossless format, so the file size will be larger. Audio is played using javax libraries rather than Minecraft's internal sound system, so moving away from the video will not affect the volume of the audio.

### Servers
Draw is usable on servers, while play is clientside. If implemented serverside, play requires serverside access to manipulate the armorstands' names. Since Minecraft is not intended to play videos, the end result is astoundingly laggy even on a server running on localhost. An interval is recommended for servers when using draw, otherwise there will be missing lines of the image. This mod cannot remove [`draw`](#draw-image-billboard_mode-resize_x-resize_y-scale-interval-yaw-pitch-nr-ng-nb-fill) entities. [`play`](#play-image-billboard_mode-mute-resize_x-resize_y-scale-fps-yaw-pitch-nr-ng-nb-fill) entities' names are removed after playback is complete or `stop` is called, but are not removed if the [`Summon entities in singleplayer for playback`](#mod-menu-configuration) switch is on, and you leave the world before ending.

### Multitasking
It is not possible to play multiple videos at once given the current code, nor is it possible to draw multiple images at once. Drawing an image relies on spamming a spawn egg, which would not be easy to code for on multiplayer. Playing a video requires a good chunk of resources that will drop your FPS.

### Processing Folders
Videos are processed by processing each frame when needed rather than preprocessing an entire folder. Preprocessing may allow faster playback but ultimately sacrifices memory. When an individual frame is called, less memory is needed while sacrificing the CPU. Therefore, it is good to compress your images and to downscale them since you really would not want to see a 1920x1080 (1080p 16:9) video played in Minecraft.

### Compatibility with Replay Mod
[`play`](#play-image-billboard_mode-mute-resize_x-resize_y-scale-fps-yaw-pitch-nr-ng-nb-fill) will only work with Replay Mod in singleplayer. Compatibility is established by summoning the text displays on the server. Then, the bottommost entity contains metadata that gets changed for every frame so a mixin reads the metadata, reads the image given, and creates a list of Text that can be assigned to a given line. All lines above the bottommost entity only have an index, so they can find their line of Text that should be rendered. Playback may be more laggy than not allowing for compatibility with the Replay Mod because of networking. Replay Mod Compatibility can be enabled in the mod menu config.

### Process Command Relies on FFmpeg
Since Java does not like me, you can't play a video directly in game. You first have to convert the video by using the [`drawme process`](#drawme-process-video-folder-width-fps) command. This command requires FFmpeg, and takes the same steps that the ReplayMod does to find it, so chances are if ReplayMod can render videos for you, then this command will work. This command automates how videos would be created based on [*Video Folders*](#video-folders).

### Text Display VS Armorstands
Initially, this mod utilized both text displays and armorstands, but now only uses text displays. Text displays are only a feature for 1.19.4+, but are more versatile than armorstands. Here is a table that outlines the benefits of both entities:

|          Property          |                        Description                         |                         Armorstand                          |                                Text Display                                 |
|:--------------------------:|:----------------------------------------------------------:|:-----------------------------------------------------------:|:---------------------------------------------------------------------------:|
|    **Static Rotation**     |                       Fixed rotation                       |                              ✘                              |                                      ✔                                      |
|  **Horizontal Rotation**   |     Rotates based on yaw (think of shaking left/right)     |                              ✘                              |                                      ✔                                      |
|   **Vertical Rotation**    |     Rotates based on pitch (think of nodding up/down)      |                              ✘                              |                                      ✔                                      |
|     **Full Rotation**      |        Rotates with camera, think of 1.8 holograms.        |                              ✔                              |                                      ✔                                      |
|     **Version Added**      |                             -                              |                            1.8+                             |                                   1.19.4+                                   |
|    **Pitch Adjustable**    |                 Can be summoned with tilt                  |                              ✘                              |                                      ✔                                      |
|        **Scalable**        |          Can be scaled down to take up less space          |                              ✘                              |                                      ✔                                      |
|     **Removable Gaps**     |           Remove gaps in between each character            |                      Only with mixins                       |                              Only with mixins                               |
|       **Fullbright**       |                             -                              |                              ✘                              |                                      ✔                                      |
| **Transparent Background** |                             -                              |                              ✘                              |                                      ✔                                      |
|         **Image**          | ![Control Image](https://i.ibb.co/GcsKwRj/commonplace.jpg) | ![48x48 AST](https://i.ibb.co/b7Z6hvr/armorstand-48x48.png) | ![48x48 TDE](https://i.ibb.co/cCr9HMW/text-display-default-scale-48x48.png) |

### Billboard Modes
Here is a comprehensive table of what each billboard mode does.

|                            Detail                            |                                             `FIXED`                                             |                                              `VERTICAL`                                               |                                               `HORIZONTAL`                                                |                                             `CENTER`                                              |
|:------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------:|
| **Image**<br/>*(Each image taken with 190 yaw and 10 pitch)* | ![FIXED](https://i.ibb.co/Dpstc0H/FIXED-text-display-scale-0-14-480x480-space-remover-post.png) | ![VERTICAL](https://i.ibb.co/zJPt51J/VERTICAL-text-display-scale-0-14-480x480-space-remover-post.png) | ![HORIZONTAL](https://i.ibb.co/0Qmrr5j/HORIZONTAL-text-display-scale-0-14-480x480-space-remover-post.png) | ![CENTER](https://i.ibb.co/KFSkxHd/CENTER-text-display-scale-0-14-480x480-space-remover-post.png) |
|                       **Description**                        |                           Rotation is static, backside is invisible.                            |                            Rotates as you move around it, has no backside.                            |               Does not "rotate" as you move around it. May have gaps in-between each line.                |   Allows both vertical and horizontal rotation; behavior is analogous to armorstand holograms.    |
|                       **Good for...**                        |                     Literal billboards, images, videos with fixed rotation                      |                                                Videos                                                 |                                                     -                                                     |                                                 -                                                 |