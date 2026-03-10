package ru.npp.extension.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import ru.npp.extension.client.TimerOverlayRenderer;

public class PacketTimerSync implements IMessage {
    private boolean running;
    private int remainingSeconds;

    public PacketTimerSync() {
    }

    public PacketTimerSync(boolean running, int remainingSeconds) {
        this.running = running;
        this.remainingSeconds = remainingSeconds;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        running = buf.readBoolean();
        remainingSeconds = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(running);
        buf.writeInt(remainingSeconds);
    }

    public static class Handler implements IMessageHandler<PacketTimerSync, IMessage> {
        @Override
        public IMessage onMessage(final PacketTimerSync message, MessageContext ctx) {
            Minecraft.getMinecraft().func_152344_a(new Runnable() {
                @Override
                public void run() {
                    TimerOverlayRenderer.setTimerState(message.running, message.remainingSeconds);
                }
            });
            return null;
        }
    }
}
