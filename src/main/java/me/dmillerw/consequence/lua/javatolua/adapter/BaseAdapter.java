package me.dmillerw.consequence.lua.javatolua.adapter;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * @author dmillerw
 */
public class BaseAdapter extends LuaTable {

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

    @Override
    public final void set(int key, LuaValue value) {
        if (!setValue(String.valueOf(key), value)) super.set(key, value);
    }

    @Override
    public final void set(LuaValue key, LuaValue value) {
        if (!setValue(key.tojstring(), value)) super.set(key, value);
    }

    @Override
    public final void set(int key, String value) {
        if (!setValue(String.valueOf(key), LuaValue.valueOf(value))) super.set(key, value);
    }

    @Override
    public final void set(String key, LuaValue value) {
        if (!setValue(key, value)) super.set(key, value);
    }

    @Override
    public final void set(String key, double value) {
        if (!setValue(key, LuaValue.valueOf(value))) super.set(key, value);
    }

    @Override
    public final void set(String key, int value) {
        if (!setValue(key, LuaValue.valueOf(value))) super.set(key, value);
    }

    @Override
    public final void set(String key, String value) {
        if (!setValue(key, LuaValue.valueOf(value))) super.set(key, value);
    }

    /* END SETTERS */
}
