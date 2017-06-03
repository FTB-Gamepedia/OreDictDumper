package com.gamepedia.ftb.oredictdumper;

import com.gamepedia.ftb.oredictdumper.commands.DumpAllOresCommand;
import com.gamepedia.ftb.oredictdumper.commands.DumpModOresCommand;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.client.ClientCommandHandler;

@Mod(modid = "oredictdumper", name = "OreDictDumper", version = "1.1.3")
public class OreDictDumperMod {
    @Mod.EventHandler
    public void registerCommand(FMLPostInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new DumpModOresCommand());
        ClientCommandHandler.instance.registerCommand(new DumpAllOresCommand());
    }
}
