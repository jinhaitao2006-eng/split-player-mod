package com.example.splitplayer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class SplitPlayerMod implements ModInitializer {
    private int tickCounter = 0;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            server.getCommandManager().executeWithPrefix(server.getCommandSource(), "carpet openFakePlayerInventory true");
            server.getCommandManager().executeWithPrefix(server.getCommandSource(), "carpet fakePlayerAutoRespawn death");
            server.getCommandManager().executeWithPrefix(server.getCommandSource(), "carpet fakePlayerResident true");
            server.getCommandManager().executeWithPrefix(server.getCommandSource(), "carpet fakePlayerAutoPickup true");
            server.getCommandManager().executeWithPrefix(server.getCommandSource(), "gamerule sendCommandFeedback false");
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;
            if (tickCounter % 10 != 0) return; // 每0.5秒检查一次

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.getName().getString().equals("shixiebushixie")) continue;
                ServerPlayerEntity fakePlayer = server.getPlayerManager().getPlayer("shixiebushixie");

                if (fakePlayer == null) {
                    server.getCommandManager().executeWithPrefix(player.getCommandSource(), "player shixiebushixie spawn at ~ ~ ~");
                    continue;
                }

                // 计算假人相对于玩家的位置
                double dx = fakePlayer.getX() - player.getX();
                double dz = fakePlayer.getZ() - player.getZ();
                double distSq = dx * dx + dz * dz;

                // 玩家视线方向
                float yawRad = (float) Math.toRadians(player.getYaw());
                double lookX = -Math.sin(yawRad);
                double lookZ = Math.cos(yawRad);

                // 夹角余弦值
                double dot = dx * lookX + dz * lookZ;
                double dist = Math.sqrt(distSq);

                // 视野锥：水平FOV约70度，半角35度，cos(35°) ≈ 0.82
                double cosAngle = (dist > 0.01) ? dot / dist : 1.0;

                // 限制条件：夹角超过35度 或 距离超过10格 → 视为超出视野
                boolean outOfView = (cosAngle < 0.82) || (distSq > 100);

                if (outOfView) {
                    // 超出视野边界，立刻停下，不再让它继续走
                    server.getCommandManager().executeWithPrefix(player.getCommandSource(), "player shixiebushixie stop");
                }
            }
        });
    }
}
