package me.dmillerw.consequence.lua.library;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 * @author dmillerw
 */
public abstract class Library extends TwoArgFunction {

    public static void register(Globals globals, Library library) {
        globals.load(library);
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable table = new LuaTable();

        register(table);

        env.set(name(), table);

        return NIL;
    }

    public abstract String name();
    public abstract void register(LuaValue library);
}
