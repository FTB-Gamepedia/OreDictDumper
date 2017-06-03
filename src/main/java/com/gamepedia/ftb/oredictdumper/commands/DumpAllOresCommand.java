package com.gamepedia.ftb.oredictdumper.commands;

import com.gamepedia.ftb.oredictdumper.OreDictDumperMod;
import com.gamepedia.ftb.oredictdumper.misc.ChatStyleColored;
import com.gamepedia.ftb.oredictdumper.misc.OreDictEntry;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DumpAllOresCommand implements ICommand {
    public static final ArrayList<String> FORMATS = new ArrayList<String>() {{
        add("json");
        add("csv");
    }};

    @Override
    public String getCommandName() {
        return "dumpallores";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return I18n.format("commands.dumpallores.usage", Joiner.on(',').join(FORMATS));
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.emptyList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!sender.getEntityWorld().isRemote) {
            return;
        }

        if (args.length != 1) {
            throw new WrongUsageException("commands.dumpallores.usage", Joiner.on(',').join(FORMATS));
        }

        String format = args[0].toLowerCase();

        if (!FORMATS.contains(format)) {
            throw new WrongUsageException("commands.dumpallores.usage", Joiner.on(',').join(FORMATS));
        }

        ImmutableList<OreDictEntry> entries = OreDictDumperMod.getEntries(null);

        File dir = new File(Minecraft.getMinecraft().mcDataDir, String.format("oredump.%s", format));
        StringBuilder string = new StringBuilder("");

        if (format.equals("json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            string.append(gson.toJson(entries));
        } else if (format.equals("csv")) {
            string.append("Tag,ItemName,Metadata,ModID\n");
            for (OreDictEntry entry : entries) {
                string.append(String.format("%s,%s,%s,%s\n", entry.getTagName(), entry.getDisplayName(), entry.getMetadata(),
                  entry.getModID()));
            }
        }

        IChatComponent msg;
        try {
            FileWriter writer = new FileWriter(dir);
            writer.write(string.toString());
            writer.close();
            msg = new ChatComponentTranslation("commands.oredictdumpgeneric.success", entries.size(), "oredump", format)
              .setChatStyle(new ChatStyleColored(EnumChatFormatting.GREEN));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(entries.toString());
            msg = new ChatComponentTranslation("commands.oredictdumpgeneric.ioexception")
              .setChatStyle(new ChatStyleColored(EnumChatFormatting.RED));
        }

        sender.addChatMessage(msg);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
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
