package com.gamepedia.ftb.oredictdumper.commands;

import com.gamepedia.ftb.oredictdumper.OreDictDumperMod;
import com.gamepedia.ftb.oredictdumper.misc.ChatStyleColored;
import com.gamepedia.ftb.oredictdumper.misc.OreDictEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DumpModOresCommand implements ICommand {
    @Override
    public String getCommandName() {
        return "dumpmodores";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        // TODO: This is ugly, do something similar to DumpAllOresCommand
        return I18n.format("commands.dumpmodores.usage", "wiki,csv,json");
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.emptyList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!sender.getEntityWorld().isRemote)  {
            return;
        }

        if (args.length < 2) {
            throw new WrongUsageException("commands.dumpmodores.usage", "wiki,csv,json"); // TODO
        }

        String abbreviation = args[0].toUpperCase();
        String id = args[1];
        String format = "wiki";
        if (args.length == 3) {
            if (args[2].equalsIgnoreCase("csv")) {
                format = "csv";
            } else if (args[2].equalsIgnoreCase("json")) {
                format = "json";
            }
        }


        ArrayList<OreDictEntry> entries = OreDictDumperMod.getEntries(id);

        StringBuilder builder = new StringBuilder();
        String extension = "txt";

        switch (format) {
            case "wiki": {
                for (OreDictEntry entry : entries) {
                    builder.append(String.format("%s!%s!%s!\n", entry.tagName, entry
                      .displayName, abbreviation));
                }
                break;
            }
            case "csv": {
                builder.append("Tag,ItemName,Metadata,ModID\n");
                for (OreDictEntry entry : entries) {
                    builder.append(String.format("%s,%s,%s,%s\n", entry.tagName, entry.displayName,
                      entry.metadata, entry.modID));
                }
                extension = "csv";
                break;
            }
            case "json": {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                builder.append(gson.toJson(entries));
                extension = "json";
                break;
            }
            default: {}
        }

        File dir = new File(Minecraft.getMinecraft().mcDataDir, String.format("%s.%s",
          abbreviation, extension));

        IChatComponent msg;
        try {
            FileWriter writer = new FileWriter(dir);
            writer.write(builder.toString());
            writer.close();
            msg = new ChatComponentTranslation("commands.oredictdumpgeneric.success", entries.size(), abbreviation, extension)
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
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
