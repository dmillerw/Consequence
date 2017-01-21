package me.dmillerw.consequence.proxy;

import me.dmillerw.consequence.Consequence;
import me.dmillerw.consequence.event.EventHandler;
import me.dmillerw.consequence.lua.javatolua.JavaToLua;
import me.dmillerw.consequence.script.ScriptRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;

/**
 * @author dmillerw
 */
public class CommonProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        JavaToLua.initializeAdapterRegistry(Consequence.INSTANCE.adapterDir);
        ScriptRegistry.initialize(Consequence.INSTANCE.scriptDir);

        MinecraftForge.EVENT_BUS.register(EventHandler.class);
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public void buildLuaGlobals(Globals globals) {
        globals.load(new JseBaseLib());
        globals.load(new PackageLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new JseMathLib());
    }
}
