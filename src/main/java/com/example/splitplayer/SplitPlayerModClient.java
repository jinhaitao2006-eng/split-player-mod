package com.example.splitplayer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class SplitPlayerModClient implements ClientModInitializer {
    private String lastAction = "";
    private int tickCounter = 0;

    private void sendAction(String action) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.networkHandler.sendCommand("player shixiebushixie " + action);
        }
    }

    @Override
    public void onInitializeClient() {
        KeyBinding forward = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.forward", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UP, "category.splitplayer"));
        KeyBinding back    = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.back", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_DOWN, "category.splitplayer"));
        KeyBinding left    = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.left", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT, "category.splitplayer"));
        KeyBinding right   = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.right", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT, "category.splitplayer"));
        KeyBinding jump    = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.jump", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.splitplayer"));
        KeyBinding attack  = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.attack", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.splitplayer"));
        KeyBinding use     = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.use", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_L, "category.splitplayer"));
        KeyBinding inventory = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.inventory", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "category.splitplayer"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            tickCounter++;
            if (tickCounter % 5 != 0) return;

            String currentAction = "stop";
            if (forward.isPressed()) currentAction = "forward";
            else if (back.isPressed()) currentAction = "move backward";
            else if (left.isPressed()) currentAction = "turn left";
            else if (right.isPressed()) currentAction = "turn right";
            else if (attack.isPressed()) currentAction = "attack";
            else if (use.isPressed()) currentAction = "use";

            if (!currentAction.equals(lastAction)) {
                sendAction(currentAction);
                lastAction = currentAction;
            }

            if (jump.wasPressed()) sendAction("jump");

            // 按 O 键打开假人背包
            if (inventory.wasPressed()) {
                client.player.networkHandler.sendCommand("player shixiebushixie inventory");
            }
        });
    }
}
