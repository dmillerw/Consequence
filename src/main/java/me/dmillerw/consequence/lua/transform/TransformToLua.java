package me.dmillerw.consequence.lua.transform;

import org.luaj.vm2.LuaValue;

/**
 * @author dmillerw
 */
public interface TransformToLua<T> {

    public LuaValue transform(T value);
}
