package com.gamepedia.ftb.oredictdumper.commands;

import com.gamepedia.ftb.oredictdumper.misc.OreDictEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public class DumpAllOresCommand implements ICommand {
    public ArrayList<String> FORMATS = new ArrayList<String>() {{
        add("json");
        add("csv");
    }};

    @Override
    public String getCommandName() {
        return "dumpallores";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "dumpallores <format>";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length != 1 || !sender.getEntityWorld().isRemote) {
            return;
        }

        String format = args[0].toLowerCase();

        if (!FORMATS.contains(format)) {
            return;
        }

        ArrayList<OreDictEntry> entries = new ArrayList<>();
        String[] names = OreDictionary.getOreNames();
        for (String name : names) {
            List<ItemStack> items = OreDictionary.getOres(name);
            for (ItemStack item : items) {
                String id = Item.REGISTRY.getNameForObject(item.getItem()).getResourceDomain();

                OreDictEntry entry = new OreDictEntry(name, item.getDisplayName(),
                  item.getItemDamage(), id);
                entries.add(entry);
            }
        }

        File dir = new File(Minecraft.getMinecraft().mcDataDir, String.format("oredump.%s",
          format));
        StringBuilder string = new StringBuilder("");

        if (format.equals("json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            string.append(gson.toJson(entries));
        } else if (format.equals("csv")) {
            string.append("Tag,ItemName,Metadata,ModID\n");
            for (OreDictEntry entry : entries) {
                string.append(String.format("%s,%s,%s,%s\n", entry.tagName, entry.displayName,
                  entry.metadata, entry.modID));
            }
        }

        String msg;
        try {
            FileWriter writer = new FileWriter(dir);
            writer.write(string.toString());
            writer.close();
            msg = TextFormatting.GREEN +
              String.format("Dumped %d entries to oredump.%s", entries.size(), format);
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
