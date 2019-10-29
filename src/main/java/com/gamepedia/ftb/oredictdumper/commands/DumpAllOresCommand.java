package com.gamepedia.ftb.oredictdumper.commands;

import com.gamepedia.ftb.oredictdumper.misc.OreDictOutputFormat;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DumpAllOresCommand extends OreDumpCommandBase {
    private static final ImmutableList<String> FORMATS = ImmutableList.of("csv", "json");

    @Override
    public String getCommandName() {
        return "dumpallores";
    }

    @Nonnull
    @Override
    protected String getUnlocalizedCommandUsage() {
        return "commands.dumpallores.usage";
    }

    @Override
    protected int getFormatArgumentPosition() {
        return 0;
    }

    @Nonnull
    @Override
    protected ImmutableList<String> getValidFormats() {
        return FORMATS;
    }

    @Override
    protected int getRequiredNumberOfArguments() {
        return 1;
    }

    @Nonnull
    @Override
    protected String getOutputFileName(String[] args) {
        return "oredump";
    }

    @Nullable
    @Override
    protected String getModIDToSearch(String[] args) {
        return null;
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
            default: {
                return null;
            }
        }
    }
}
