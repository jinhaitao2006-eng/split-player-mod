package com.example.splitplayer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class SplitPlayerModClient implements ClientModInitializer {
    private String lastAction = "";
    private int tickCounter = 0;

    // 快捷指令发送器
    private void sendAction(String action) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.networkHandler.sendCommand("player shixiebushixie " + action);
        }
    }

    @Override
    public void onInitializeClient() {
        // 注册键盘按键（方便电脑测试）
        KeyBinding forward = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.forward", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UP, "category.splitplayer"));
        KeyBinding back = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.back", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_DOWN, "category.splitplayer"));
        KeyBinding left = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.left", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT, "category.splitplayer"));
        KeyBinding right = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.right", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT, "category.splitplayer"));
        KeyBinding jump = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.jump", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.splitplayer"));
        KeyBinding attack = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.splitplayer.attack", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.splitplayer"));

        // 注册屏幕上绘制的悬浮按钮
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.currentScreen != null) return;

            int width = drawContext.getScaledWindowWidth();
            int height = drawContext.getScaledWindowHeight();

            // 右下角：攻击按钮
            drawContext.fill(width - 80, height - 80, width - 40, height - 40, 0x80FF0000);
            drawContext.drawText(client.textRenderer, "攻", width - 65, height - 65, 0xFFFFFF, true);

            // 右下角：跳跃按钮
            drawContext.fill(width - 130, height - 80, width - 90, height - 40, 0x8000FF00);
            drawContext.drawText(client.textRenderer, "跳", width - 115, height - 65, 0xFFFFFF, true);

            // 左下角：前进按钮
            drawContext.fill(20, height - 120, 60, height - 80, 0x800000FF);
            drawContext.drawText(client.textRenderer, "前", 35, height - 105, 0xFFFFFF, true);

            // 左下角：后退按钮
            drawContext.fill(20, height - 60, 60, height - 20, 0x800000FF);
            drawContext.drawText(client.textRenderer, "后", 35, height - 45, 0xFFFFFF, true);

            // 左下角：左转按钮
            drawContext.fill(80, height - 120, 120, height - 80, 0x800000FF);
            drawContext.drawText(client.textRenderer, "左", 95, height - 105, 0xFFFFFF, true);

            // 左下角：右转按钮
            drawContext.fill(80, height - 60, 120, height - 20, 0x800000FF);
            drawContext.drawText(client.textRenderer, "右", 95, height - 45, 0xFFFFFF, true);
        });

        // 监听按键和屏幕触摸
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // 1. 键盘操作逻辑
            tickCounter++;
            if (tickCounter % 3 == 0) {
                String currentAction = "stop";
                if (forward.isPressed()) currentAction = "forward";
                else if (back.isPressed()) currentAction = "backward";
                else if (left.isPressed()) currentAction = "turn left";
                else if (right.isPressed()) currentAction = "turn right";
                else if (attack.isPressed()) currentAction = "attack";

                if (!currentAction.equals(lastAction)) {
                    sendAction(currentAction);
                    lastAction = currentAction;
                }
            }
            if (jump.wasPressed()) sendAction("jump");

            // 2. 屏幕触摸操作逻辑（监听鼠标点击）
            if (client.currentScreen == null && client.mouse.wasLeftButtonClicked()) {
                double mx = client.mouse.getX() * (double)client.getWindow().getScaledWidth() / client.getWindow().getWidth();
                double my = client.mouse.getY() * (double)client.getWindow().getScaledHeight() / client.getWindow().getHeight();
                int width = client.getWindow().getScaledWidth();
                int height = client.getWindow().getScaledHeight();

                // 判断手指是否点在了按钮区域
                if (mx >= width - 80 && mx <= width - 40 && my >= height - 80 && my <= height - 40) {
                    sendAction("attack");
                    client.player.sendMessage(net.minecraft.text.Text.literal("§e[系统] 假人正在攻击！"), true);
                }
                if (mx >= width - 130 && mx <= width - 90 && my >= height - 80 && my <= height - 40) {
                    sendAction("jump");
                }
                if (mx >= 20 && mx <= 60 && my >= height - 120 && my <= height - 80) {
                    lastAction = "forward"; sendAction("forward");
                }
                if (mx >= 20 && mx <= 60 && my >= height - 60 && my <= height - 20) {
                    lastAction = "backward"; sendAction("backward");
                }
                if (mx >= 80 && mx <= 120 && my >= height - 120 && my <= height - 80) {
                    lastAction = "turn left"; sendAction("turn left");
                }
                if (mx >= 80 && mx <= 120 && my >= height - 60 && my <= height - 20) {
                    lastAction = "turn right"; sendAction("turn right");
                }
            }
        });
    }
}
