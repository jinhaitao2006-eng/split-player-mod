package com.example.splitplayer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class SplitPlayerModClient implements ClientModInitializer {
    private String lastMove = "";

    private void sendAction(String action) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.networkHandler.sendCommand("player shixiebushixie " + action);
        }
    }

    @Override
    public void onInitializeClient() {
        KeyBinding forward   = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.forward", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UP, "category.splitplayer"));
        KeyBinding back      = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.back", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_DOWN, "category.splitplayer"));
        KeyBinding left      = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.left", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT, "category.splitplayer"));
        KeyBinding right     = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.right", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT, "category.splitplayer"));
        KeyBinding jump      = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.jump", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.splitplayer"));
        KeyBinding attack    = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.attack", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.splitplayer"));
        KeyBinding use       = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.use", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_L, "category.splitplayer"));
        KeyBinding inventory = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.inventory", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "category.splitplayer"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // ===== 移动类：按住保持，松开停止 =====
            String currentMove = "stop";
            if (forward.isPressed()) currentMove = "move forward";
            else if (back.isPressed()) currentMove = "move backward";
            else if (left.isPressed()) currentMove = "turn left";
            else if (right.isPressed()) currentMove = "turn right";

            if (!currentMove.equals(lastMove)) {
                sendAction(currentMove);
                lastMove = currentMove;
            }

            // ===== 瞬间动作：每次按下都触发一次 =====
            if (jump.wasPressed()) sendAction("jump");
            if (attack.wasPressed()) sendAction("attack");
            if (use.wasPressed()) sendAction("use");

            // ===== 背包 =====
            if (inventory.wasPressed()) {
                client.player.networkHandler.sendCommand("player shixiebushixie inventory");
            }
        });
    }
}
