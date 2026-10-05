package com.example.splitplayer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SplitPlayerMod implements ModInitializer {
    private final Set<UUID> hasSpawned = new HashSet<>();
    private int tickCounter = 0;

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;
            if (tickCounter % 100 != 0) return; // 每5秒检查一次

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.getName().getString().equals("shixiebushixie")) continue;

                ServerPlayerEntity fakePlayer = server.getPlayerManager().getPlayer("shixiebushixie");

                // 1. 假人不存在就生成
                if (fakePlayer == null) {
                    if (!hasSpawned.contains(player.getUuid())) {
                        hasSpawned.add(player.getUuid());
                        server.getCommandManager().executeWithPrefix(
                            player.getCommandSource(),
                            "player shixiebushixie spawn at ~ ~ ~"
                        );
                        player.sendMessage(Text.literal("§a[双人模组] 第二玩家（shixiebushixie）已上线！"), false);
                    }
                    continue;
                }

                // 2. 视野检查：计算假人是否在玩家前方
                double dx = fakePlayer.getX() - player.getX();
                double dz = fakePlayer.getZ() - player.getZ();

                // 玩家朝向向量（根据yaw角计算）
                float yawRad = (float) Math.toRadians(player.getYaw());
                double lookX = -Math.sin(yawRad);
                double lookZ = Math.cos(yawRad);

                // 点积：判断假人在玩家前方还是后方
                double dot = dx * lookX + dz * lookZ;
                double distSq = dx * dx + dz * dz;

                // 假人不在视野前方，且距离超过 10 格
                if (dot < 0 && distSq > 100) {
                    // 让假人看向玩家，然后朝玩家走来
                    server.getCommandManager().executeWithPrefix(
                        player.getCommandSource(),
                        "player shixiebushixie look at " + player.getName().getString()
                    );
                    server.getCommandManager().executeWithPrefix(
                        player.getCommandSource(),
                        "player shixiebushixie move forward"
                    );
                    player.sendMessage(Text.literal("§e[双人模组] 第二玩家正在走回来……"), true);
                } else {
                    // 在视野内，让它停下
                    server.getCommandManager().executeWithPrefix(
                        player.getCommandSource(),
                        "player shixiebushixie stop"
                    );
                }
            }
        });
    }
}
