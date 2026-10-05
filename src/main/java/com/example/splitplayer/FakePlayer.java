package com.example.splitplayer;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class FakePlayer extends ServerPlayerEntity {
    public FakePlayer(MinecraftServer server, ServerWorld world, GameProfile profile) {
        // 直接传 null，绕过找不到的 SyncedClientOptions 类
        super(server, world, profile, null);
    }
}
