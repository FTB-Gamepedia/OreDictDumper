package com.gamepedia.ftb.oredictdumper;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.oredict.OreDictionary;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OreDictDumperCommand implements ICommand {
    @Override
    public String getCommandName() {
        return "oredictdump";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "oredictdump <abbreviation> <modid>";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length != 2 || !sender.getEntityWorld().isRemote)  {
            return;
        }
        String abbreviation = args[0].toUpperCase();
        String id = args[1];
        ArrayList<String> entries = new ArrayList<>();
        File dir = new File(Minecraft.getMinecraft().mcDataDir, String.format("%s.txt",
          abbreviation));
        for (String name : OreDictionary.getOreNames()) {
            for (ItemStack item : OreDictionary.getOres(name)) {
                String modid = Item.REGISTRY.getNameForObject(item.getItem())
                  .getResourceDomain();

                if (!id.equals(modid)) {
                    continue;
                }
                entries.add(String.format("%s!%s!%s!!\n", name, item.getDisplayName(),
                  abbreviation));
            }
        }

        String msg;
        try {
            FileWriter writer = new FileWriter(dir);
            for (String s : entries) {
                writer.append(s);
            }
            writer.close();
            msg = TextFormatting.GREEN + String.format("Dumped %d entries to %s.txt", entries
              .size(), abbreviation);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(entries.toString());
            msg = TextFormatting.RED + "IOException! Check logs for raw array!";
        }

        sender.addChatMessage(new TextComponentString(msg));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
