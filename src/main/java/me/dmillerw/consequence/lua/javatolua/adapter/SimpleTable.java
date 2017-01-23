package me.dmillerw.consequence.lua.javatolua.adapter;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * @author dmillerw
 */
public class SimpleTable extends LuaTable {

    public boolean setValue(String key, LuaValue value) {
        return false;
    }

    public LuaValue getValue(String key) {
        return null;
    }

    /* GETTERS */

    @Override
    public final LuaValue get(int key) {
        LuaValue value = getValue(String.valueOf(key));
        return value == null ? super.get(key) : value;
    }

    @Override
    public final LuaValue get(LuaValue key) {
        LuaValue value = getValue(key.tojstring());
        return value == null ? super.get(key) : value;
    }

    @Override
    public final LuaValue get(String key) {
        LuaValue value = getValue(key);
        return value == null ? super.get(key) : value;
    }

    /* END GETTERS */

    /* SETTERS */



    /* END SETTERS */
}
