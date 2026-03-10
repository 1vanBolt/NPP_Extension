package ru.npp.extension.common;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import ru.npp.extension.BattlePrepMod;
import ru.npp.extension.network.PacketTimerSync;

public class TimerManager {
    private static final TimerManager INSTANCE = new TimerManager();
    private static final int TIMER_DURATION_SECONDS = 30 * 60;

    private boolean running;
    private int remainingSeconds;
    private int tickCounter;

    private TimerManager() {
    }

    public static void register() {
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    public static void toggleTimer(EntityPlayerMP actor) {
        if (INSTANCE.running) {
            INSTANCE.stop(actor);
        } else {
            INSTANCE.start(actor);
        }
    }

    private void start(EntityPlayerMP actor) {
        running = true;
        remainingSeconds = TIMER_DURATION_SECONDS;
        tickCounter = 0;
        announce("[NPP] " + actor.getDisplayName() + " запустил таймер подготовки: 30:00");
        syncToAll();
    }

    private void stop(EntityPlayerMP actor) {
        running = false;
        remainingSeconds = 0;
        tickCounter = 0;
        announce("[NPP] " + actor.getDisplayName() + " остановил таймер подготовки.");
        syncToAll();
    }

    private void stopByTimeout() {
        running = false;
        remainingSeconds = 0;
        tickCounter = 0;
        announce("[NPP] Таймер подготовки завершен. Можно начинать бой!");
        syncToAll();
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !running) {
            return;
        }

        tickCounter++;
        if (tickCounter < 20) {
            return;
        }

        tickCounter = 0;
        remainingSeconds--;

        if (remainingSeconds <= 0) {
            stopByTimeout();
            return;
        }

        syncToAll();
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            syncToPlayer((EntityPlayerMP) event.player);
        }
    }

    private void announce(String message) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            server.getConfigurationManager().sendChatMsg(new ChatComponentText(message));
        }
    }

    private void syncToAll() {
        BattlePrepMod.NETWORK.sendToAll(new PacketTimerSync(running, remainingSeconds));
    }

    private void syncToPlayer(EntityPlayerMP player) {
        BattlePrepMod.NETWORK.sendTo(new PacketTimerSync(running, remainingSeconds), player);
    }
}
