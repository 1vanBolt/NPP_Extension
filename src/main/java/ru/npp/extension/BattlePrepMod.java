package ru.npp.extension;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

@Mod(modid = BattlePrepMod.MODID, name = BattlePrepMod.NAME, version = BattlePrepMod.VERSION)
public class BattlePrepMod {
    public static final String MODID = "npp_extension";
    public static final String NAME = "NPP Extension";
    public static final String VERSION = "1.0.0";

    @SidedProxy(clientSide = "ru.npp.extension.ClientProxy", serverSide = "ru.npp.extension.CommonProxy")
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper NETWORK;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        NETWORK.registerMessage(PacketActionRequest.Handler.class, PacketActionRequest.class, 0, Side.SERVER);
        NETWORK.registerMessage(PacketTimerSync.Handler.class, PacketTimerSync.class, 1, Side.CLIENT);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        TimerManager.register();
        proxy.init();
    }
}

class CommonProxy {
    public void init() {
        // Common/server init.
    }
}

class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        KeyInputHandler.register();
        FMLCommonHandler.instance().bus().register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new TimerOverlayRenderer());
    }
}

class KeyInputHandler {
    private static final KeyBinding OPEN_GUI_KEY = new KeyBinding("key.npp_extension.open_gui", Keyboard.KEY_GRAVE, "key.categories.gameplay");

    public static void register() {
        ClientRegistry.registerKeyBinding(OPEN_GUI_KEY);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (OPEN_GUI_KEY.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new PvPControlGui());
        }
    }
}

class PvPControlGui extends GuiScreen {
    @Override
    public void initGui() {
        this.buttonList.clear();
        int centerX = this.width / 2;
        int startY = this.height / 2 - 50;

        this.buttonList.add(new GuiButton(0, centerX - 110, startY, 220, 20, "1) Время: День / Ночь"));
        this.buttonList.add(new GuiButton(1, centerX - 110, startY + 24, 220, 20, "2) Режим: Выживание / Творч"));
        this.buttonList.add(new GuiButton(2, centerX - 110, startY + 48, 220, 20, "3) Сложность: Мирный / Сложный"));
        this.buttonList.add(new GuiButton(3, centerX - 110, startY + 72, 220, 20, "4) Таймер 30 мин: Старт / Стоп"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        BattlePrepMod.NETWORK.sendToServer(new PacketActionRequest(button.id));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Управление подготовкой PvP", this.width / 2, this.height / 2 - 70, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

class TimerOverlayRenderer {
    private static boolean running;
    private static int remainingSeconds;

    public static void setTimerState(boolean isRunning, int secondsLeft) {
        running = isRunning;
        remainingSeconds = secondsLeft;
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (!running) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = mc.fontRenderer;

        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;

        String text = String.format("Подготовка: %02d:%02d", minutes, seconds);
        int x = (event.resolution.getScaledWidth() - font.getStringWidth(text)) / 2;
        int y = 10;
        font.drawStringWithShadow(text, x, y, 0xFFFFFF);
    }
}

class TimerManager {
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

class PacketActionRequest implements IMessage {
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

class PacketTimerSync implements IMessage {
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
