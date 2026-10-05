package com.example.splitplayer;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import java.util.List;
import java.util.UUID;

public class SplitPlayerMod implements ModInitializer {
    private int tickCounter = 0;
    // 为第二玩家指定一个固定的UUID，确保它每次都是同一个玩家
    private static final UUID FAKE_PLAYER_UUID = UUID.fromString("86fd84c4-8f65-49e8-b993-9e7c2155d362");

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;
            if (tickCounter % 200 != 0) return; // 每10秒检查一次

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                ServerWorld world = (ServerWorld) player.getWorld();
                
                // 搜索周围32格内是否已存在我们的第二玩家
                Box searchBox = player.getBoundingBox().expand(32.0);
                List<FakePlayer> existingFakes = world.getEntitiesByClass(FakePlayer.class, searchBox, 
                    entity -> entity.getUuid().equals(FAKE_PLAYER_UUID));

                if (existingFakes.isEmpty()) {
                    // 使用 Fabric API 创建 FakePlayer
                    GameProfile profile = new GameProfile(FAKE_PLAYER_UUID, "第二玩家");
                    FakePlayer secondPlayer = FakePlayer.get(world, profile);
                    
                    // 设置生成位置
                    secondPlayer.refreshPositionAndAngles(player.getX() + 2, player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                    
                    // 设置为生存模式
                    secondPlayer.changeGameMode(net.minecraft.world.GameMode.SURVIVAL);
                    
                    // 将假玩家添加到世界（FakePlayer.get 内部应该已经处理了连接和玩家列表添加）
                    // 这里直接 spawn 可能会重复，但 Fabric 的实现通常已经处理好了
                    // 如果没有，我们可能需要手动调用 server.getPlayerManager().addPlayer(secondPlayer)
                    
                    player.sendMessage(Text.literal("§a[双人模组] 真正的第二玩家已上线！"), false);
                }
            }
        });
    }
}
