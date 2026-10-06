package com.example.splitplayer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class SplitPlayerMod implements ModInitializer {
    // 视野半角：60° → 水平视野 120°
    private static final double COS_HALF_FOV = Math.cos(Math.toRadians(60));
    // 最远能看到 10 格
    private static final double MAX_VIEW_DIST = 10.0;
    // 超出后弹回玩家正前方 4 格
    private static final double PUSH_BACK_DIST = 4.0;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            // 删除了不存在的 fakePlayerAutoPickup 规则
            server.getCommandManager().executeWithPrefix(server.getCommandSource(),
                "carpet openFakePlayerInventory true");
            server.getCommandManager().executeWithPrefix(server.getCommandSource(),
                "carpet fakePlayerAutoRespawn true");
            server.getCommandManager().executeWithPrefix(server.getCommandSource(),
                "carpet fakePlayerResident true");
            server.getCommandManager().executeWithPrefix(server.getCommandSource(),
                "gamerule sendCommandFeedback false");
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // 核心修复：复制一份玩家列表，防止遍历时崩溃
            List<ServerPlayerEntity> players = new ArrayList<>(server.getPlayerManager().getPlayerList());

            for (ServerPlayerEntity player : players) {
                if (player.getName().getString().equals("shixiebushixie")) continue;

                ServerPlayerEntity fake = server.getPlayerManager().getPlayer("shixiebushixie");

                // 假人不存在就生成
                if (fake == null) {
                    server.getCommandManager().executeWithPrefix(
                        player.getCommandSource(),
                        "player shixiebushixie spawn at " + player.getX() + " " + player.getY() + " " + player.getZ()
                    );
                    continue; // 本轮循环跳过，等下一tick再处理
                }

                // ===== 视野检测 =====
                Vec3d look = player.getRotationVec(1.0F);
                double dx = fake.getX() - player.getX();
                double dz = fake.getZ() - player.getZ();
                double dist = Math.sqrt(dx * dx + dz * dz);

                if (dist < 0.001) continue;

                double fx = dx / dist;
                double fz = dz / dist;
                double dot = fx * look.x + fz * look.z;

                boolean inView = dot > COS_HALF_FOV && dist < MAX_VIEW_DIST;

                if (!inView) {
                    double tx = player.getX() + look.x * PUSH_BACK_DIST;
                    double tz = player.getZ() + look.z * PUSH_BACK_DIST;
                    fake.teleport(tx, fake.getY(), tz);
                }
            }
        });
    }
}
