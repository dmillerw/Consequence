package me.dmillerw.consequence.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.luaj.vm2.Globals;

/**
 * @author dmillerw
 */
public interface IProxy {

    public void preInit(FMLPreInitializationEvent event);
    public void init(FMLInitializationEvent event);
    public void postInit(FMLPostInitializationEvent event);

    public void buildLuaGlobals(Globals globals);
}
