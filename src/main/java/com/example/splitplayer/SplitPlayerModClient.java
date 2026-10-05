package com.example.splitplayer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class SplitPlayerModClient implements ClientModInitializer {
    private String lastAction = "";
    private int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        KeyBinding forward = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.forward", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UP, "category.splitplayer"));
        KeyBinding back = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.back", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_DOWN, "category.splitplayer"));
        KeyBinding left = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.left", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT, "category.splitplayer"));
        KeyBinding right = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.right", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT, "category.splitplayer"));
        KeyBinding jump = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.jump", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.splitplayer"));
        KeyBinding attack = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.attack", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.splitplayer"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // 每 3 tick（约0.15秒）检测一次，防止指令刷屏
            tickCounter++;
            if (tickCounter % 3 != 0) return;

            String currentAction = "stop";
            
            if (forward.isPressed()) currentAction = "forward";
            else if (back.isPressed()) currentAction = "backward";
            else if (left.isPressed()) currentAction = "turn left";
            else if (right.isPressed()) currentAction = "turn right";
            else if (attack.isPressed()) currentAction = "attack";

            // 只有动作改变时才发送指令！
            if (!currentAction.equals(lastAction)) {
                client.player.networkHandler.sendCommand("player shixiebushixie " + currentAction);
                lastAction = currentAction;
            }

            // 跳跃是瞬间动作，每次按下都发一次
            if (jump.wasPressed()) {
                client.player.networkHandler.sendCommand("player shixiebushixie jump");
            }
        });
    }
}
