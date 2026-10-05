package com.example.splitplayer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

                    ServerWorld world = (ServerWorld) player.getWorld();
                    
                    // 生成一个盔甲架代替第二玩家
                    ArmorStandEntity secondPlayer = EntityType.ARMOR_STAND.create(world);

                    if (secondPlayer != null) {
                        secondPlayer.refreshPositionAndAngles(player.getX() + 2, player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                        secondPlayer.setCustomName(Text.literal("§b第二玩家(玩家2)"));
                        secondPlayer.setCustomNameVisible(true);
                        
                        // 给第二玩家穿一套钻石装备，证明它有独立装备栏
                        secondPlayer.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
                        secondPlayer.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
                        secondPlayer.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
                        secondPlayer.equipStack(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
                        secondPlayer.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
                        
                        // 设为无敌且不可破坏（防止掉装备）
                        secondPlayer.setInvulnerable(true);
                        
                        world.spawnEntity(secondPlayer);
                        player.sendMessage(Text.literal("§a[双人模组] 第二玩家实体生成成功！它已经穿好装备啦！"), false);
                    }
                }
            }
        });
    }
}
