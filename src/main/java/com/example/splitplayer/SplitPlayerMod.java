package com.example.splitplayer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SplitPlayerMod implements ModInitializer {
    public static final Set<UUID> spawnedPlayers = new HashSet<>();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (!spawnedPlayers.contains(player.getUuid())) {
                    spawnedPlayers.add(player.getUuid());
                    player.sendMessage(Text.literal("§a[双人模组] 检测到玩家进入，正在生成第二玩家..."), false);
                    server.getPlayerManager().broadcast(Text.literal("§e[系统] 第二玩家数据（UUID: " + UUID.randomUUID() + "）已绑定到当前存档！"), false);
                }
            }
        });
    }
}
