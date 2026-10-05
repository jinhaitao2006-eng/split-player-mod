package com.example.splitplayer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class SplitPlayerModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 注册 6 个虚拟按键
        KeyBinding forward = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.forward", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UP, "category.splitplayer"));
        KeyBinding back = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.back", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_DOWN, "category.splitplayer"));
        KeyBinding left = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.left", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT, "category.splitplayer"));
        KeyBinding right = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.right", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT, "category.splitplayer"));
        KeyBinding jump = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.jump", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.splitplayer"));
        KeyBinding attack = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.attack", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.splitplayer"));

        // 监听按键，只要按下，就发送指令给服务器
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            while (forward.wasPressed()) client.player.networkHandler.sendCommand("player shixiebushixie move forward");
            while (back.wasPressed()) client.player.networkHandler.sendCommand("player shixiebushixie move backward");
            while (left.wasPressed()) client.player.networkHandler.sendCommand("player shixiebushixie turn left");
            while (right.wasPressed()) client.player.networkHandler.sendCommand("player shixiebushixie turn right");
            while (jump.wasPressed()) client.player.networkHandler.sendCommand("player shixiebushixie jump");
            while (attack.wasPressed()) client.player.networkHandler.sendCommand("player shixiebushixie attack");
        });
    }
}
