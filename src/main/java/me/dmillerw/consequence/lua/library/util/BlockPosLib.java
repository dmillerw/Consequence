package me.dmillerw.consequence.lua.library.util;

import me.dmillerw.consequence.lua.library.Library;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

/**
 * @author dmillerw
 */
public class BlockPosLib extends Library {

    @Override
    public String name() {
        return "blockpos";
    }

    @Override
    public void register(LuaValue library) {
        library.set("new", new Construct());
    }

    private static class Construct extends ThreeArgFunction {

        @Override
        public LuaValue call(LuaValue x, LuaValue y, LuaValue z) {
            LuaTable table = new LuaTable();

            table.set("x", x);
            table.set("y", y);
            table.set("z", z);

            return table;
        }
    }
}
