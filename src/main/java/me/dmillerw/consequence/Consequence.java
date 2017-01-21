package me.dmillerw.consequence;

import me.dmillerw.consequence.command.CommandReloadScript;
import me.dmillerw.consequence.lib.ModInfo;
import me.dmillerw.consequence.proxy.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * @author dmillerw
 */
@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.MOD_VERSION)
public class Consequence {

    @Mod.Instance(ModInfo.MOD_ID)
    public static Consequence INSTANCE;
    
    @SidedProxy(serverSide = "me.dmillerw.consequence.proxy.CommonProxy", clientSide = "me.dmillerw.consequence.proxy.ClientProxy")
    public static IProxy PROXY;
    
    public File adapterDir;
    public File scriptDir;

    public Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Consequence.INSTANCE.INSTANCE.adapterDir = new File(event.getModConfigurationDirectory(), "consequence/adapters");
        if (!Consequence.INSTANCE.adapterDir.exists()) Consequence.INSTANCE.adapterDir.mkdirs();
        Consequence.INSTANCE.scriptDir = new File(event.getModConfigurationDirectory(), "consequence/scripts");
        if (!Consequence.INSTANCE.scriptDir.exists()) Consequence.INSTANCE.scriptDir.mkdirs();

        Consequence.INSTANCE.logger = event.getModLog();

        PROXY.preInit(event);
    }

    @Mod.EventHandler
    public void serverStartingEvent(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandReloadScript());
    }
}
