package com.gamepedia.ftb.oredictdumper;

import com.gamepedia.ftb.oredictdumper.commands.*;
import com.gamepedia.ftb.oredictdumper.misc.OreDictEntry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = "oredictdumper", name = "OreDictDumper", version = "1.1.2")
public class OreDictDumperMod {
    @Mod.EventHandler
    public void registerCommand(FMLPostInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new DumpModOresCommand());
        ClientCommandHandler.instance.registerCommand(new DumpAllOresCommand());
    }

    /**
     * Returns an array of all the entries that are in the given mod ID.
     * @param id The mod ID. Null if we don't want to narrow it down by mod ID (dumpallores)
     * @return An array of OreDictEntries. Can be empty. Never null.
     */
    public static ArrayList<OreDictEntry> getEntries(@Nullable String id) {
        ArrayList<OreDictEntry> entries = new ArrayList<>();
        for (String name : OreDictionary.getOreNames()) {
            for (ItemStack item : OreDictionary.getOres(name)) {
                @SuppressWarnings("deprecation")
                ModContainer mod = GameData.findModOwner(GameData.getItemRegistry().getNameForObject(item.getItem()));

                String id1 = mod == null ? "minecraft" : mod.getModId();
                if (id != null && !id.equals(id1)) {
                    continue;
                }

                if (item.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                    List list = new ArrayList();
                    item.getItem().getSubItems(item.getItem(), null, list);
                    for (Object obj : list) {
                        // This will never happen assuming people are smart.
                        if (!(obj instanceof ItemStack)) {
                            continue;
                        }
                        ItemStack is = (ItemStack) obj;
                        entries.add(new OreDictEntry(name, is.getDisplayName(), is.getItemDamage(), id1));
                    }
                } else {
                    entries.add(new OreDictEntry(name, item.getDisplayName(), item.getItemDamage(), id1));
                }
            }
        }

        return entries;
    }
}
