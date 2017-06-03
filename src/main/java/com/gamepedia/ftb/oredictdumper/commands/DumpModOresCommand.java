package com.gamepedia.ftb.oredictdumper.commands;

import com.gamepedia.ftb.oredictdumper.OreDictDumperMod;
import com.gamepedia.ftb.oredictdumper.misc.OreDictEntry;
import com.gamepedia.ftb.oredictdumper.misc.StyleColored;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!sender.getEntityWorld().isRemote)  {
            return;
        }

        if (args.length < 2) {
            throw new WrongUsageException("commands.dumpmodores.usage", "wiki,csv,json");
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
                    builder.append(String.format("%s!%s!%s!\n", entry.getTagName(), entry.getDisplayName(), abbreviation));
                }
                break;
            }
            case "csv": {
                builder.append("Tag,ItemName,Metadata,ModID\n");
                for (OreDictEntry entry : entries) {
                    builder.append(String.format("%s,%s,%s,%s\n", entry.getTagName(), entry.getDisplayName(), entry.getMetadata(),
                      entry.getModID()));
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

        File dir = new File(Minecraft.getMinecraft().mcDataDir, String.format("%s.%s", abbreviation, extension));

        ITextComponent msg;
        try {
            FileWriter writer = new FileWriter(dir);
            writer.write(builder.toString());
            writer.close();
            msg = new TextComponentTranslation("commands.oredictdumpgeneric.success", entries.size(), abbreviation, extension)
              .setStyle(new StyleColored(TextFormatting.GREEN));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(entries.toString());
            msg = new TextComponentTranslation("commands.oredictdumpgeneric.ioexception").setStyle(new StyleColored(TextFormatting.RED));
        }

        sender.addChatMessage(msg);
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
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
