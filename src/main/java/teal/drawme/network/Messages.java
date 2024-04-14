package teal.drawme.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class Messages {
    public static final Identifier PLAY = new Identifier("drawme", "play");

    public static void C2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(PLAY, PlayPacket::receivePlay);
    }
}
