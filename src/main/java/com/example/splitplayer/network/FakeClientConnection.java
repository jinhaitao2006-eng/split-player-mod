package com.example.splitplayer.network;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.Nullable;

/**
 * 伪造的客户端连接，用于让服务器认为假玩家是一个真实的客户端。
 * 参考自 Carpet Mod 的实现。
 */
public class FakeClientConnection extends Connection {

    public FakeClientConnection(PacketFlow packetFlow) {
        super(packetFlow);
        // 使用 EmbeddedChannel 让 #isOpen() 返回 true，
        // 这样服务器就会认为连接是活跃的，同时会静默丢弃所有发往客户端的数据包。
        this.channel = new EmbeddedChannel();
    }

    @Override
    public void setReadOnly() {
        // 不执行任何操作
    }

    @Override
    public void send(Packet<?> packet, @Nullable ChannelFutureListener listener, boolean flush) {
        // 不实际发送数据包，直接忽略
    }

    @Override
    public void handleDisconnection() {
        // 不处理断开连接
    }

    @Override
    public void setListenerForServerboundHandshake(PacketListener packetListener) {
        // 不设置监听器
    }

    @Override
    public <T extends PacketListener> void setupInboundProtocol(ProtocolInfo<T> protocolInfo, T packetListener) {
        // 不设置入站协议
    }
}
