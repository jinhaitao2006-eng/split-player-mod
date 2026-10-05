package com.example.splitplayer;

import com.mojang.authlib.GameProfile;
import com.example.splitplayer.network.FakeClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.SyncedClientOptions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.network.protocol.PacketFlow;

public class FakePlayer extends ServerPlayerEntity {
    public FakePlayer(MinecraftServer server, ServerWorld world, GameProfile profile) {
        // 传入默认的客户端配置，避免空指针异常
        super(server, world, profile, SyncedClientOptions.createDefault());
        // 核心：将假网络连接注入到玩家的网络处理器中
        // 这一步是Carpet实现的核心，让服务器认为假玩家有真实的连接
        this.networkHandler = new net.minecraft.server.network.ServerPlayNetworkHandler(
            server, 
            new FakeClientConnection(PacketFlow.SERVERBOUND), 
            this, 
            SyncedClientOptions.createDefault()
        );
    }
}
