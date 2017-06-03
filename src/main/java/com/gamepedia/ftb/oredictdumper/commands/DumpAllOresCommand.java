package com.gamepedia.ftb.oredictdumper.commands;

import com.gamepedia.ftb.oredictdumper.misc.OreDictOutputFormat;
import com.google.common.collect.ImmutableList;
import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DumpAllOresCommand extends OreDumpCommandBase {
    private static final ImmutableList<String> FORMATS = ImmutableList.of("csv", "json");

    @Override
    public String getName() {
        return "dumpallores";
    }

    @Override
    protected String getUnlocalizedCommandUsage() {
        return "commands.dumpallores.usage";
    }

    @Override
    protected int getFormatArgumentPosition() {
        return 0;
    }

    @Override
    protected ImmutableList<String> getValidFormats() {
        return FORMATS;
    }

    @Override
    protected int getRequiredNumberOfArguments() {
        return 1;
    }

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
