package me.dmillerw.consequence;

import me.dmillerw.consequence.event.handler.ItemTooltipHandler;
import me.dmillerw.consequence.lib.ModInfo;
import me.dmillerw.consequence.lua.javatolua.JavaToLua;
import me.dmillerw.consequence.script.ScriptRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

/**
 * @author dmillerw
 */
@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.MOD_VERSION)
public class Consequence {

    public static File adapterDir;
    public static File scriptDir;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Consequence.adapterDir = new File(event.getModConfigurationDirectory(), "consequence/adapters");
        if (!Consequence.adapterDir.exists()) Consequence.adapterDir.mkdirs();
        Consequence.scriptDir = new File(event.getModConfigurationDirectory(), "consequence/scripts");
        if (!Consequence.scriptDir.exists()) Consequence.scriptDir.mkdirs();

        JavaToLua.initializeAdapterRegistry(Consequence.adapterDir);
        ScriptRegistry.initialize(Consequence.scriptDir);

        MinecraftForge.EVENT_BUS.register(new ItemTooltipHandler());
    }
}
