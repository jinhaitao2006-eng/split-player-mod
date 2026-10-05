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

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                // 只对每个玩家触发一次
                if (!hasSpawned.contains(player.getUuid())) {
                    hasSpawned.add(player.getUuid());

                    // 延迟 100 tick（5秒）等玩家完全进入世界后，在后台执行 Carpet 指令
                    new Thread(() -> {
                        try {
                            Thread.sleep(5000); // 延迟5秒执行，确保 Carpet 已加载完毕
                            server.execute(() -> {
                                // 这行代码相当于帮玩家在后台输入了 /player 第二玩家 spawn at @s
                                server.getCommandManager().executeWithPrefix(server.getCommandSource(), "player 第二玩家 spawn at @s");
                                player.sendMessage(Text.literal("§a[双人模组] 专属第二玩家已上线！"), false);
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        });
    }
}
