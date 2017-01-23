package me.dmillerw.consequence.lua.transform;

import org.luaj.vm2.LuaValue;

/**
 * @author dmillerw
 */
public interface TransformToJava<T> {

    public T transform(LuaValue value);
}
