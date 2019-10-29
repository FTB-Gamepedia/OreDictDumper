package com.gamepedia.ftb.oredictdumper.commands;

import com.gamepedia.ftb.oredictdumper.misc.OreDictOutputFormat;
import com.google.common.collect.ImmutableList;
import net.minecraft.command.WrongUsageException;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DumpModOresCommand extends OreDumpCommandBase {
    private static final ImmutableList<String> FORMATS = ImmutableList.of("csv", "json", "wiki");

    @Override
    public String getCommandName() {
        return "dumpmodores";
    }

    @Nonnull
    @Override
    protected String getUnlocalizedCommandUsage() {
        return "commands.dumpmodores.usage";
    }

    @Override
    protected int getFormatArgumentPosition() {
        return 1;
    }

    @Nonnull
    @Override
    protected ImmutableList<String> getValidFormats() {
        return FORMATS;
    }

    @Override
    protected int getRequiredNumberOfArguments() {
        // 2 required for non-wiki formats, 3 required for wiki formats
        return 2;
    }

    @Nonnull
    @Override
    protected String getOutputFileName(String[] args) {
        return args[0];
    }

    @Nullable
    @Override
    protected String getModIDToSearch(String[] args) {
        return args[0];
    }

    @Nullable
    @Override
    protected OreDictOutputFormat getOutputFormat(String[] args) {
        String formatArg = args[getFormatArgumentPosition()];
        switch (formatArg) {
            case "json": {
                return new OreDictOutputFormat.JSONOutputFormat();
            }
            case "csv": {
                return new OreDictOutputFormat.CSVOutputFormat();
            }
            case "wiki": {
                if (args.length != 3) {
                    throw new WrongUsageException(getUnlocalizedCommandUsage(), StringUtils.join(getValidFormats(), ','));
                }
                return new OreDictOutputFormat.WikiOutputFormat(args[2]);
            }
            default: {
                return null;
            }
        }
    }
}
