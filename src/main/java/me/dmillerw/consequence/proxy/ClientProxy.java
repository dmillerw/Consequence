package me.dmillerw.consequence.proxy;

import me.dmillerw.consequence.lua.library.Library;
import me.dmillerw.consequence.lua.library.client.ClientLib;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.luaj.vm2.Globals;

/**
 * @author dmillerw
 */
public class ClientProxy extends CommonProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Override
    public void buildLuaGlobals(Globals globals) {
        super.buildLuaGlobals(globals);

        Library.register(globals, new ClientLib());
    }
}
