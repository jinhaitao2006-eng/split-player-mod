package com.example.splitplayer;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import java.util.List;
import java.util.UUID;

public class SplitPlayerMod implements ModInitializer {
    private int tickCounter = 0;
    private static final UUID FAKE_PLAYER_UUID = UUID.fromString("86fd84c4-8f65-49e8-b993-9e7c2155d362");

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;
            if (tickCounter % 200 != 0) return; // 每10秒检查一次

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                ServerWorld world = (ServerWorld) player.getWorld();
                
                // 搜索周围 32 格内有没有我们的第二玩家
                Box searchBox = player.getBoundingBox().expand(32.0);
                List<FakePlayer> existingFakes = world.getEntitiesByClass(FakePlayer.class, searchBox, 
                    entity -> entity.getUuid().equals(FAKE_PLAYER_UUID));

                if (existingFakes.isEmpty()) {
                    // 生成真正的第二玩家
                    GameProfile profile = new GameProfile(FAKE_PLAYER_UUID, "第二玩家");
                    FakePlayer secondPlayer = new FakePlayer(server, world, profile);
                    
                    secondPlayer.refreshPositionAndAngles(player.getX() + 2, player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                    
                    // 设置为生存模式
                    secondPlayer.setGameMode(net.minecraft.world.GameMode.SURVIVAL);
                    
                    // 添加到世界
                    world.spawnEntity(secondPlayer);
                    
                    player.sendMessage(Text.literal("§a[双人模组] 真正的第二玩家已上线！"), false);
                }
            }
        });
    }
}
