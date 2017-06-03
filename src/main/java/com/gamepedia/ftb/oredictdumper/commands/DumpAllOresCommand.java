package com.gamepedia.ftb.oredictdumper.commands;

import com.gamepedia.ftb.oredictdumper.OreDictDumperMod;
import com.gamepedia.ftb.oredictdumper.misc.OreDictEntry;
import com.gamepedia.ftb.oredictdumper.misc.StyleColored;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mcp.MethodsReturnNonnullByDefault;
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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DumpAllOresCommand implements ICommand {
    private static final ImmutableList<String> FORMATS = new ImmutableList.Builder<String>().add("json", "csv").build();

    @Override
    public String getName() {
        return "dumpallores";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return I18n.format("commands.dumpallores.usage", Joiner.on(',').join(FORMATS));
    }

    @Override
    public List<String> getAliases() {
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

        ITextComponent msg;
        try {
            FileWriter writer = new FileWriter(dir);
            writer.write(string.toString());
            writer.close();
            msg = new TextComponentTranslation("commands.oredictdumpgeneric.success", entries.size(), "oredump", format)
              .setStyle(new StyleColored(TextFormatting.GREEN));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(entries.toString());
            msg = new TextComponentTranslation("commands.oredictdumpgeneric.ioexception").setStyle(new StyleColored(TextFormatting.RED));
        }

        sender.sendMessage(msg);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return getName().compareTo(o.getName());
    }
}
