package me.dmillerw.consequence.lua.library.client;

import me.dmillerw.consequence.lua.javatolua.JavaToLua;
import me.dmillerw.consequence.lua.library.Library;
import net.minecraft.client.Minecraft;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

/**
 * @author dmillerw
 */
public class ClientLib extends Library {

    @Override
    public String name() {
        return "client";
    }

    @Override
    public void register(LuaValue library) {
        library.set("get_player", new get_player());
        library.set("get_world", new get_world());
        library.set("get_font_renderer", new get_font_renderer());
    }

    static class get_player extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            return JavaToLua.convert(Minecraft.getMinecraft().player);
        }
    }

    static class get_world extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            return Minecraft.getMinecraft().world == null ? NIL : new LuaTable();
        }
    }

    static class get_font_renderer extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            return JavaToLua.convert(Minecraft.getMinecraft().fontRendererObj);
        }
    }
}
