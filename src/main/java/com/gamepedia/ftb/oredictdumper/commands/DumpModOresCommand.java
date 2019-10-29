package com.gamepedia.ftb.oredictdumper.commands;

import com.gamepedia.ftb.oredictdumper.misc.OreDictOutputFormat;
import com.google.common.collect.ImmutableList;
import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DumpModOresCommand extends OreDumpCommandBase {
    private static final ImmutableList<String> FORMATS = ImmutableList.of("csv", "json", "wiki");

    @Override
    public String getName() {
        return "dumpmodores";
    }

    @Override
    protected String getUnlocalizedCommandUsage() {
        return "commands.dumpmodores.usage";
    }

    @Override
    protected int getFormatArgumentPosition() {
        return 1;
    }

    @Override
    protected ImmutableList<String> getValidFormats() {
        return FORMATS;
    }

    @Override
    protected int getRequiredNumberOfArguments() {
        // 2 required for non-wiki formats, 3 required for wiki format
        return 2;
    }

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
                    return null;
                }
                return new OreDictOutputFormat.WikiOutputFormat(args[2]);
            }
            default: {
                return null;
            }
        }
    }
}
