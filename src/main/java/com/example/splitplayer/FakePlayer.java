package com.example.splitplayer;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.SyncedClientOptions;
import net.minecraft.server.world.ServerWorld;

public class FakePlayer extends ServerPlayerEntity {
    public FakePlayer(MinecraftServer server, ServerWorld world, GameProfile profile) {
        super(server, world, profile, SyncedClientOptions.createDefault());
    }

    @Override
    public void tick() {
        // 踢掉原本的网络同步逻辑，防止因为没有真实连接而崩溃
        super.baseTick();
    }
}
