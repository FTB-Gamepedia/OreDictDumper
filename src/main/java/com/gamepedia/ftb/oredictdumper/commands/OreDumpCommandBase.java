package com.gamepedia.ftb.oredictdumper.commands;

import com.gamepedia.ftb.oredictdumper.misc.ChatStyleColored;
import com.gamepedia.ftb.oredictdumper.misc.OreDictEntry;
import com.gamepedia.ftb.oredictdumper.misc.OreDictOutputFormat;
import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class OreDumpCommandBase implements ICommand {
    /**
     * @return The base unlocalized string for the usage information of the command
     */
    @Nonnull
    protected abstract String getUnlocalizedCommandUsage();

    /**
     * @return The position in a correctly formatted argument String array for the format argument
     */
    protected abstract int getFormatArgumentPosition();

    /**
     * @return An immutable list of valid format string arguments
     */
    @Nonnull
    protected abstract ImmutableList<String> getValidFormats();

    /**
     * @return The number of arguments that are required for the command to be successfully executed
     */
    protected abstract int getRequiredNumberOfArguments();

    /**
     * @param args The list of arguments passed to the command
     * @return The name of the file (not including the extension) to save as.
     */
    @Nonnull
    protected abstract String getOutputFileName(String[] args);

    /**
     * @param args The list of arguments passed to the command
     * @return The mod ID that is being searched (see {@link #getEntries(String)})
     */
    @Nullable
    protected abstract String getModIDToSearch(String[] args);

    /**
     * @param args The list of arguments passed to the command
     * @return The formatter to use for the arguments passed to the command
     */
    @Nullable
    protected abstract OreDictOutputFormat getOutputFormat(String[] args);

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!sender.getEntityWorld().isRemote) {
            return;
        }

        if (args.length != getRequiredNumberOfArguments()) {
            throw new WrongUsageException(getUnlocalizedCommandUsage(), StringUtils.join(getValidFormats(), ','));
        }

        String format = args[getFormatArgumentPosition()];
        OreDictOutputFormat outputFormat = getOutputFormat(args);

        if (!getValidFormats().contains(format) || outputFormat == null) {
            throw new WrongUsageException(getUnlocalizedCommandUsage(), StringUtils.join(getValidFormats(), ','));
        }

        ImmutableList<OreDictEntry> entries = getEntries(getModIDToSearch(args));
        String fileName = getOutputFileName(args);
        String ext = outputFormat.getFileExtension();
        File dir = new File(Minecraft.getMinecraft().mcDataDir, String.format("%s.%s", fileName, ext));

        IChatComponent msg;
        try {
            FileWriter writer = new FileWriter(dir);
            writer.write(outputFormat.parseEntries(entries));
            writer.close();
            msg = new ChatComponentTranslation("commands.oredictdumpgeneric.success",
              entries.size(), fileName, ext).setChatStyle(new ChatStyleColored(EnumChatFormatting.GREEN));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(entries.toString());
            msg = new ChatComponentTranslation("commands.oredictdumpgeneric.ioexception")
              .setChatStyle(new ChatStyleColored(EnumChatFormatting.RED));
        }

        sender.addChatMessage(msg);
    }

    /**
     * Returns an array of all the entries that are in the given mod ID.
     * @param id The mod ID. Null if we don't want to narrow it down by mod ID (get all items)
     * @return An array of OreDictEntries. Can be empty. Never null.
     */
    @Nonnull
    private ImmutableList<OreDictEntry> getEntries(@Nullable String id) {
        List<OreDictEntry> entries = new ArrayList<>();
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

        return ImmutableList.copyOf(entries);
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return StatCollector.translateToLocalFormatted(getUnlocalizedCommandUsage(), StringUtils.join(getValidFormats(), ','));
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] options) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
