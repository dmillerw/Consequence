package me.dmillerw.consequence.lua.library.client;

import me.dmillerw.consequence.lua.javatolua.JavaToLua;
import net.minecraft.client.Minecraft;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

/**
 * @author dmillerw
 */
public class ClientLib extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable table = new LuaTable();
        table.set("get_font_renderer", new get_font_renderer());

        env.set("client", table);

        return NIL;
    }

    static class get_font_renderer extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            return JavaToLua.convert(Minecraft.getMinecraft().fontRendererObj);
        }
    }
}
