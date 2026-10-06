package com.example.splitplayer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class SplitPlayerMod implements ModInitializer {
    private int tickCounter = 0;

    @Override
    public void onInitialize() {
        // 服务器启动时自动配置 Carpet 规则
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            server.getCommandManager().executeWithPrefix(server.getCommandSource(),
                "carpet openFakePlayerInventory true");
            server.getCommandManager().executeWithPrefix(server.getCommandSource(),
                "carpet fakePlayerAutoRespawn death");
            server.getCommandManager().executeWithPrefix(server.getCommandSource(),
                "carpet fakePlayerResident true");
            server.getCommandManager().executeWithPrefix(server.getCommandSource(),
                "carpet fakePlayerAutoPickup true");
            server.getCommandManager().executeWithPrefix(server.getCommandSource(),
                "gamerule sendCommandFeedback false");
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;
            if (tickCounter % 20 != 0) return; // 每1秒检查一次

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.getName().getString().equals("shixiebushixie")) continue;

                ServerPlayerEntity fakePlayer = server.getPlayerManager().getPlayer("shixiebushixie");

                // 假人不存在就生成
                if (fakePlayer == null) {
                    server.getCommandManager().executeWithPrefix(
                        player.getCommandSource(),
                        "player shixiebushixie spawn at ~ ~ ~"
                    );
                    continue;
                }

                // ===== 视野检测 =====
                double dx = fakePlayer.getX() - player.getX();
                double dz = fakePlayer.getZ() - player.getZ();
                double distSq = dx * dx + dz * dz;

                float yawRad = (float) Math.toRadians(player.getYaw());
                double lookX = -Math.sin(yawRad);
                double lookZ = Math.cos(yawRad);
                double dot = dx * lookX + dz * lookZ;

                // 判定：假人是否离开视野
                boolean behindAndFar = dot < 0 && distSq > 64;   // 在身后且超过8格
                boolean tooFar       = distSq > 400;              // 超过20格

                if (behindAndFar || tooFar) {
                    // 超出视野 → 传送回玩家身边
                    server.getCommandManager().executeWithPrefix(
                        player.getCommandSource(),
                        "player shixiebushixie spawn at ~ ~ ~"
                    );
                }
            }
        });
    }
}
