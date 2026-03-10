package ru.npp.extension.client;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;
import ru.npp.extension.common.CommonProxy;

public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        KeyInputHandler.register();
        FMLCommonHandler.instance().bus().register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new TimerOverlayRenderer());
    }
}
