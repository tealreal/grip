package teal.drawme.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static teal.drawme.Drawme.se;

public class PlayPacket {
    public static void receivePlay(
        MinecraftServer server,
        ServerPlayerEntity player,
        ServerPlayNetworkHandler handler,
        PacketByteBuf buf,
        PacketSender responseSender) {
        if (VideoPlayer.instance != null && !VideoPlayer.instance.shouldFuckoff()) {
            VideoPlayer.instance.forceFuckOff();
            VideoPlayer.instance.setServer(player.getServerWorld());
            ServerWorld sw = player.getServerWorld();
            VideoPlayer instance = VideoPlayer.instance;
            instance.setServer(sw);
            se = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(instance, 0, instance.getInterval(), TimeUnit.NANOSECONDS);
        }
    }

}
