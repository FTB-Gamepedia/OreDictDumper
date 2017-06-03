package com.gamepedia.ftb.oredictdumper;

import com.gamepedia.ftb.oredictdumper.commands.DumpAllOresCommand;
import com.gamepedia.ftb.oredictdumper.commands.DumpModOresCommand;
import com.gamepedia.ftb.oredictdumper.misc.OreDictEntry;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = "oredictdumper", name = "OreDictDumper", version = "3.1.3", acceptedMinecraftVersions = "[1.11,1.11.2]")
public class OreDictDumperMod {
    @Mod.EventHandler
    public void registerCommand(FMLPostInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new DumpModOresCommand());
        ClientCommandHandler.instance.registerCommand(new DumpAllOresCommand());
    }

}
