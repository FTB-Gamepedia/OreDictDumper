package com.gamepedia.ftb.oredictdumper;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.client.ClientCommandHandler;

@Mod(modid = "oredictdumper", name = "OreDictDumper", version = "1.0.1")
public class OreDictDumperMod {
    @Mod.EventHandler
    public void registerCommand(FMLPostInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new OreDictDumperCommand());
    }
}
