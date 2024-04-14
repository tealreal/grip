package teal.drawme;

import net.fabricmc.api.ModInitializer;
import teal.drawme.network.Messages;

public class Packet implements ModInitializer {
    @Override
    public void onInitialize() {
        Messages.C2SPackets();
    }
}
