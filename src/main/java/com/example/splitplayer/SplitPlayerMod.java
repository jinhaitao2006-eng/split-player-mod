package com.example.splitplayer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class SplitPlayerMod implements ModInitializer {
    private static final double COS_HALF_FOV = Math.cos(Math.toRadians(60));
    private static final double MAX_VIEW_DIST = 10.0;
    private static final double PUSH_BACK_DIST = 4.0;

    @Override
    public void onInitialize() {
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
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.getName().getString().equals("shixiebushixie")) continue;

                ServerPlayerEntity fake = server.getPlayerManager().getPlayer("shixiebushixie");
                if (fake == null) {
                    server.getCommandManager().executeWithPrefix(
                        player.getCommandSource(),
                        "player shixiebushixie spawn at " + player.getX() + " " + player.getY() + " " + player.getZ()
                    );
                    continue;
                }

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
                    // 1.21.1 需要传 ServerWorld
                    fake.teleport((ServerWorld) fake.getWorld(), tx, fake.getY(), tz, fake.getYaw(), fake.getPitch());
                }
            }
        });
    }
}
