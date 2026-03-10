package ru.npp.extension.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import ru.npp.extension.common.TimerManager;

public class PacketActionRequest implements IMessage {
    private int actionId;

    public PacketActionRequest() {
    }

    public PacketActionRequest(int actionId) {
        this.actionId = actionId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.actionId = ByteBufUtils.readVarInt(buf, 5);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarInt(buf, actionId, 5);
    }

    public static class Handler implements IMessageHandler<PacketActionRequest, IMessage> {
        @Override
        public IMessage onMessage(PacketActionRequest message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) {
                return null;
            }

            if (!canUseAdminActions(player)) {
                player.addChatMessage(new ChatComponentText("[NPP] Нужны права оператора (уровень 2+)."));
                return null;
            }

            handleAction(player, message.actionId);
            return null;
        }

        private boolean canUseAdminActions(EntityPlayerMP player) {
            MinecraftServer server = MinecraftServer.getServer();
            return server != null && server.getConfigurationManager().func_152596_g(player.getGameProfile());
        }

        private void handleAction(EntityPlayerMP player, int actionId) {
            switch (actionId) {
                case 0:
                    toggleDayNight(player);
                    break;
                case 1:
                    toggleGamemode(player);
                    break;
                case 2:
                    toggleDifficulty(player);
                    break;
                case 3:
                    TimerManager.toggleTimer(player);
                    break;
                default:
                    player.addChatMessage(new ChatComponentText("[NPP] Неизвестное действие."));
                    break;
            }
        }

        private void toggleDayNight(EntityPlayerMP actor) {
            MinecraftServer server = MinecraftServer.getServer();
            if (server == null || server.worldServers == null || server.worldServers.length == 0) {
                return;
            }

            WorldServer overworld = server.worldServers[0];
            long current = overworld.getWorldTime() % 24000L;
            long target = current < 12000L ? 13000L : 1000L;
            String name = current < 12000L ? "ночь" : "день";

            for (WorldServer world : server.worldServers) {
                if (world != null) {
                    world.setWorldTime(target);
                }
            }

            server.getConfigurationManager().sendChatMsg(new ChatComponentText("[NPP] " + actor.getDisplayName() + " установил время: " + name));
        }

        private void toggleGamemode(EntityPlayerMP player) {
            if (player.theItemInWorldManager.getGameType().isCreative()) {
                player.setGameType(net.minecraft.world.WorldSettings.GameType.SURVIVAL);
                player.addChatMessage(new ChatComponentText("[NPP] Режим: Выживание"));
            } else {
                player.setGameType(net.minecraft.world.WorldSettings.GameType.CREATIVE);
                player.addChatMessage(new ChatComponentText("[NPP] Режим: Творческий"));
            }
        }

        private void toggleDifficulty(EntityPlayerMP actor) {
            MinecraftServer server = MinecraftServer.getServer();
            if (server == null || server.worldServers == null || server.worldServers.length == 0) {
                return;
            }

            EnumDifficulty current = server.worldServers[0].difficultySetting;
            EnumDifficulty target = current == EnumDifficulty.PEACEFUL ? EnumDifficulty.HARD : EnumDifficulty.PEACEFUL;
            String name = target == EnumDifficulty.PEACEFUL ? "Мирный" : "Сложный";

            for (WorldServer world : server.worldServers) {
                if (world != null) {
                    world.difficultySetting = target;
                }
            }

            server.getConfigurationManager().sendChatMsg(new ChatComponentText("[NPP] " + actor.getDisplayName() + " установил сложность: " + name));
        }
    }
}
