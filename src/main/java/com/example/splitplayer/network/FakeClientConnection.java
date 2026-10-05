package com.example.splitplayer.network;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.Nullable;

public class FakeClientConnection extends Connection {
    public FakeClientConnection(PacketFlow packetFlow) {
        super(packetFlow);
        // 核心：用 EmbeddedChannel 让 #isOpen() 返回 true，骗过服务器
        this.channel = new EmbeddedChannel();
    }

    @Override
    public void setReadOnly() { }

    @Override
    public void send(Packet<?> packet, @Nullable ChannelFutureListener listener, boolean flush) {
        // 不实际发送数据包，直接忽略，避免内存泄漏和卡顿
    }

    @Override
    public void handleDisconnection() { }

    @Override
    public void setListenerForServerboundHandshake(PacketListener packetListener) { }

    @Override
    public <T extends PacketListener> void setupInboundProtocol(ProtocolInfo<T> protocolInfo, T packetListener) { }
}
