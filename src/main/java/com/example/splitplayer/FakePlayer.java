package com.example.splitplayer;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.SyncedClientOptions;
import net.minecraft.server.world.ServerWorld;

public class FakePlayer extends ServerPlayerEntity {
    public FakePlayer(MinecraftServer server, ServerWorld world, GameProfile profile) {
        // 核心修复：传入默认的客户端配置，而不是 null
        super(server, world, profile, SyncedClientOptions.createDefault());
    }
}
