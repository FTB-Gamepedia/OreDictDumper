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

import javax.annotation.Nonnull;
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

    /**
     * Returns an array of all the entries that are in the given mod ID.
     * @param id The mod ID. Null if we don't want to narrow it down by mod ID (dumpallores)
     * @return An array of OreDictEntries. Can be empty. Never null.
     */
    @Nonnull
    public static ImmutableList<OreDictEntry> getEntries(@Nullable String id) {
        List<OreDictEntry> entries = new ArrayList<>();
        for (String name : OreDictionary.getOreNames()) {
            for (ItemStack item : OreDictionary.getOres(name)) {
                ResourceLocation itemRL = Item.REGISTRY.getNameForObject(item.getItem());
                if (itemRL == null) {
                    continue;
                }
                String modid = itemRL.getResourceDomain();

                if (id != null && !id.equals(modid)) {
                    continue;
                }

                if (item.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                    NonNullList<ItemStack> list = NonNullList.create();
                    item.getItem().getSubItems(item.getItem(), null, list);
                    for (ItemStack is : list) {
                        entries.add(new OreDictEntry(name, is.getDisplayName(), is.getItemDamage(), modid));
                    }
                } else {
                    entries.add(new OreDictEntry(name, item.getDisplayName(), item.getItemDamage(), modid));
                }
            }
        }

        return ImmutableList.copyOf(entries);
    }
}
