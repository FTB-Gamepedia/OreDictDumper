package com.gamepedia.ftb.oredictdumper;

import com.gamepedia.ftb.oredictdumper.commands.DumpAllTagsCommand;
import com.gamepedia.ftb.oredictdumper.commands.DumpModTagsCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(OreDictDumper.MODID)
public class OreDictDumper {
    public static final String MODID = "oredictdumper";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public OreDictDumper() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(
                () -> "whatever", // If I'm actually on the server, this string is sent but I'm a client only mod, so it won't be
                (remote, isNetwork) -> isNetwork // I accept anything from the server, by returning true if it's asking about the server
        ));
        // IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.addListener(this::registerCommands));
    }

    private void registerCommands(RegisterCommandsEvent event) {
        if (event.getEnvironment() != Commands.EnvironmentType.INTEGRATED)
            return;

        CommandDispatcher<CommandSource> commandDispatcher = event.getDispatcher();
        DumpAllTagsCommand.register(commandDispatcher);
        DumpModTagsCommand.register(commandDispatcher);
    }
}
