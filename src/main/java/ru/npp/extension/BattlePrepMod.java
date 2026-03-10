package ru.npp.extension;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import ru.npp.extension.common.CommonProxy;
import ru.npp.extension.common.TimerManager;
import ru.npp.extension.network.PacketActionRequest;
import ru.npp.extension.network.PacketTimerSync;

@Mod(modid = BattlePrepMod.MODID, name = BattlePrepMod.NAME, version = BattlePrepMod.VERSION)
public class BattlePrepMod {
    public static final String MODID = "npp_extension";
    public static final String NAME = "NPP Extension";
    public static final String VERSION = "1.0.0";

    @SidedProxy(clientSide = "ru.npp.extension.client.ClientProxy", serverSide = "ru.npp.extension.common.CommonProxy")
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
