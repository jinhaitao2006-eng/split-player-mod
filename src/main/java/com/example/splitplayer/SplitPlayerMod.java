package com.example.splitplayer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
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

                    // 获取玩家当前所在的服务器世界
                    ServerWorld world = (ServerWorld) player.getWorld();
                    
                    // 在玩家旁边生成一个僵尸作为第二玩家的占位实体
                    ZombieEntity secondPlayer = EntityType.ZOMBIE.create(world);

                    if (secondPlayer != null) {
                        // 坐标：玩家X+2，Y不变，Z不变
                        secondPlayer.refreshPositionAndAngles(player.getX() + 2, player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                        // 设置头顶名字
                        secondPlayer.setCustomName(Text.literal("§b第二玩家(玩家2)"));
                        secondPlayer.setCustomNameVisible(true);
                        // 禁用AI，防止它打你；设置为无敌，防止它死掉
                        secondPlayer.setAiDisabled(true); 
                        secondPlayer.setInvulnerable(true); 
                        
                        // 把它生成到世界里
                        world.spawnEntity(secondPlayer);
                        player.sendMessage(Text.literal("§a[双人模组] 第二玩家实体生成成功！它就在你旁边！"), false);
                    }
                }
            }
        });
    }
}
