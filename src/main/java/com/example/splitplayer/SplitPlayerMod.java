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
import net.minecraft.util.math.Box;
import java.util.List;
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
                    
                    // 检查周围 10 格内有没有我们的假人
                    Box searchBox = player.getBoundingBox().expand(10.0);
                    List<ArmorStandEntity> existingStands = world.getEntitiesByClass(ArmorStandEntity.class, searchBox, entity -> entity.hasCustomName() && entity.getCustomName().getString().contains("第二玩家"));

                    if (!existingStands.isEmpty()) {
                        player.sendMessage(Text.literal("§e[双人模组] 检测到世界中已存在第二玩家，跳过生成。"), false);
                        continue;
                    }

                    // 如果没有，才生成新的
                    ArmorStandEntity secondPlayer = EntityType.ARMOR_STAND.create(world);
                    if (secondPlayer != null) {
                        secondPlayer.refreshPositionAndAngles(player.getX() + 2, player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                        secondPlayer.setCustomName(Text.literal("§b第二玩家(玩家2)"));
                        secondPlayer.setCustomNameVisible(true);
                        secondPlayer.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
                        secondPlayer.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
                        secondPlayer.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
                        secondPlayer.equipStack(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
                        secondPlayer.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
                        secondPlayer.setInvulnerable(true);
                        world.spawnEntity(secondPlayer);
                        player.sendMessage(Text.literal("§a[双人模组] 第二玩家实体生成成功！"), false);
                    }
                }
            }
        });
    }
}
