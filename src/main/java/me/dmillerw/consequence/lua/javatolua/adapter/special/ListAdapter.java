package me.dmillerw.consequence.lua.javatolua.adapter.special;

import me.dmillerw.consequence.lua.javatolua.JavaToLua;
import me.dmillerw.consequence.lua.javatolua.adapter.BaseAdapter;
import me.dmillerw.consequence.lua.luatojava.LuaToJava;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.util.List;

/**
 * @author dmillerw
 */
public class ListAdapter extends BaseAdapter {

    private List list;
    private Class type;

    public ListAdapter(List list) {
        this.list = list;
        if (!list.isEmpty()) type = list.get(0).getClass();
    }

    @Override
    public LuaValue getValue(String key) {
        if (key.equals("add")) {
            return new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg) {
                    return JavaToLua.convert(list.add(LuaToJava.convert(type, arg)));
                }
            };
        } else {
            int index = Integer.MIN_VALUE;
            try {
                index = Integer.valueOf(key);
            } catch (NumberFormatException ex) {
                return null;
            }

            return JavaToLua.convert(list.get(index));
        }
    }

    @Override
    public boolean setValue(String key, LuaValue value) {
        int index = Integer.MIN_VALUE;
        try {
            index = Integer.valueOf(key);
        } catch (NumberFormatException ex) {
            return false;
        }

        list.set(index, LuaToJava.convert(type, value));

        return true;
    }
}
