package com.gamepedia.ftb.oredictdumper.commands;

import com.gamepedia.ftb.oredictdumper.misc.OreDictEntry;
import com.gamepedia.ftb.oredictdumper.misc.OreDictOutputFormat;
import com.gamepedia.ftb.oredictdumper.misc.StyleColored;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.oredict.OreDictionary;

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
public abstract class OreDumpCommandBase implements ICommand {
    /**
     * @return The base unlocalized string for the usage information of the command
     */
    protected abstract String getUnlocalizedCommandUsage();

    /**
     * @return The position in a correctly formatted argument String array for the format argument
     */
    protected abstract int getFormatArgumentPosition();

    /**
     * @return An immutable list of valid format string arguments
     */
    protected abstract ImmutableList<String> getValidFormats();

    /**
     * @return The number of arguments that are required for the command to be successfully executed
     */
    protected abstract int getRequiredNumberOfArguments();

    /**
     * @param args The list of arguments passed to the command
     * @return The name of the file (not including the extension or parent directories) to save as
     */
    protected abstract String getOutputFileName(String[] args);

    /**
     * @param args The list of arguments passed to the command
     * @return The mod ID that is being searched (see {@link #getEntries(String)})
     */
    @Nullable
    protected abstract String getModIDToSearch(String[] args);

    /**
     * @param args The list of arguments passed to the command
     * @return The formatted to use for the arguments passed to the command
     */
    @Nullable
    protected abstract OreDictOutputFormat getOutputFormat(String[] args);

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!sender.getEntityWorld().isRemote)  {
            return;
        }

        if (args.length < getRequiredNumberOfArguments()) {
            throw new WrongUsageException(getUnlocalizedCommandUsage(), Joiner.on(',').join(getValidFormats()));
        }

        String format = args[getFormatArgumentPosition()];
        OreDictOutputFormat outputFormat = getOutputFormat(args);

        if (!getValidFormats().contains(format) || outputFormat == null) {
            throw new WrongUsageException(getUnlocalizedCommandUsage(), Joiner.on(',').join(getValidFormats()));
        }

        ImmutableList<OreDictEntry> entries = getEntries(getModIDToSearch(args));
        String fileName = getOutputFileName(args);
        String ext = outputFormat.getFileExtension();
        File dir = new File(Minecraft.getMinecraft().mcDataDir, String.format("%s.%s", fileName, ext));

        ITextComponent msg;
        try {
            FileWriter writer = new FileWriter(dir);
            writer.write(outputFormat.parseEntries(entries));
            writer.close();
            msg = new TextComponentTranslation("commands.oredictdumpgeneric.success",
              entries.size(), fileName, ext).setStyle(new StyleColored(TextFormatting.GREEN));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(entries.toString());
            msg = new TextComponentTranslation("commands.oredictdumpgeneric.ioexception").setStyle(new StyleColored(TextFormatting.RED));
        }

        sender.sendMessage(msg);
    }

    /**
     * Returns an array of all the entries that are in the given mod ID.
     * @param id The mod ID. Null if we don't want to narrow it down by mod ID (dumpallores)
     * @return An array of OreDictEntries. Can be empty. Never null.
     */
    private ImmutableList<OreDictEntry> getEntries(@Nullable String id) {
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

    @Override
    public String getUsage(ICommandSender sender) {
        return I18n.format(getUnlocalizedCommandUsage(), Joiner.on(',').join(getValidFormats()));
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
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
