package com.gamepedia.ftb.oredictdumper.commands;

import com.gamepedia.ftb.oredictdumper.OreDictDumperMod;
import com.gamepedia.ftb.oredictdumper.misc.OreDictEntry;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
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

        ArrayList<OreDictEntry> entries = OreDictDumperMod.getEntries(null);
        File dir = new File(Minecraft.getMinecraft().mcDataDir, String.format("oredump.%s", format));
        StringBuilder string = new StringBuilder("");

        if (format.equals("json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            string.append(gson.toJson(entries));
        } else if (format.equals("csv")) {
            string.append("Tag,ItemName,Metadata,ModID\n");
            for (OreDictEntry entry : entries) {
                string.append(String.format("%s,%s,%s,%s\n", entry.tagName, entry.displayName, entry.metadata,
                  entry.modID));
            }
        }

        String msg;
        try {
            FileWriter writer = new FileWriter(dir);
            writer.write(string.toString());
            writer.close();
            msg = TextFormatting.GREEN + I18n.format("commands.oredictdumpgeneric.success", entries.size(), "oredump", format);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(entries.toString());
            msg = TextFormatting.RED + I18n.format("commands.oredictdumpgeneric.ioexception");
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
