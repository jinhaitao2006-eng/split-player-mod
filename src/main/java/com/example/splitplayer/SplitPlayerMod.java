package com.example.splitplayer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class SplitPlayerMod implements ModInitializer {
    private int tickCounter = 0;
    // 记录假人当前状态：0=空闲，1=正在走回来
    private int fakeState = 0;

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

                if (fakePlayer == null) {
                    server.getCommandManager().executeWithPrefix(
                        player.getCommandSource(),
                        "player shixiebushixie spawn at ~ ~ ~"
                    );
                    fakeState = 0;
                    continue;
                }

                // 视野检测
                double dx = fakePlayer.getX() - player.getX();
                double dz = fakePlayer.getZ() - player.getZ();
                double distSq = dx * dx + dz * dz;

                float yawRad = (float) Math.toRadians(player.getYaw());
                double lookX = -Math.sin(yawRad);
                double lookZ = Math.cos(yawRad);
                double dot = dx * lookX + dz * lookZ;

                boolean outOfView = dot < 0 && distSq > 64;  // 在身后且超过8格
                boolean tooClose = distSq < 16;               // 4格内

                // 状态机：只在状态切换时发指令
                if (outOfView && fakeState != 1) {
                    // 从空闲 → 走回来
                    server.getCommandManager().executeWithPrefix(
                        player.getCommandSource(),
                        "player shixiebushixie look at " + player.getName().getString()
                    );
                    server.getCommandManager().executeWithPrefix(
                        player.getCommandSource(),
                        "player shixiebushixie move forward"
                    );
                    fakeState = 1;
                } else if ((!outOfView || tooClose) && fakeState == 1) {
                    // 从走回来 → 空闲
                    server.getCommandManager().executeWithPrefix(
                        player.getCommandSource(),
                        "player shixiebushixie stop"
                    );
                    fakeState = 0;
                }
            }
        });
    }
}
