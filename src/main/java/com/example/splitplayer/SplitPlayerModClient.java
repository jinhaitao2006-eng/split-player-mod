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

    // 发送指令给假人的工具方法
    private void sendAction(String action) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.networkHandler.sendCommand("player shixiebushixie " + action);
        }
    }

    @Override
    public void onInitializeClient() {
        // 注册键盘按键，绑定到指定键位
        KeyBinding forward = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.forward", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UP, "category.splitplayer"));
        KeyBinding back    = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.back", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_DOWN, "category.splitplayer"));
        KeyBinding left    = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.left", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT, "category.splitplayer"));
        KeyBinding right   = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.right", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT, "category.splitplayer"));
        KeyBinding jump    = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.jump", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.splitplayer"));
        KeyBinding attack  = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.attack", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.splitplayer"));
        KeyBinding use     = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.use", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_L, "category.splitplayer"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            tickCounter++;
            // 每 5 tick（约0.25秒）检测一次，防止指令刷屏导致卡顿
            if (tickCounter % 5 != 0) return;

            // 默认动作是停止
            String currentAction = "stop";

            // 判断当前按下的按键
            if (forward.isPressed()) {
                currentAction = "forward";
            } else if (back.isPressed()) {
                currentAction = "move backward";
            } else if (left.isPressed()) {
                currentAction = "turn left";
            } else if (right.isPressed()) {
                currentAction = "turn right";
            } else if (attack.isPressed()) {
                currentAction = "attack";
            } else if (use.isPressed()) {
                currentAction = "use";
            }

            // 只有当动作发生改变时才发送指令！松开按键时会发 "stop"
            if (!currentAction.equals(lastAction)) {
                sendAction(currentAction);
                lastAction = currentAction;
            }

            // 跳跃是瞬间动作，每次按下都发送一次
            if (jump.wasPressed()) {
                sendAction("jump");
            }
        });
    }
}
