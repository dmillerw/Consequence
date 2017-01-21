package me.dmillerw.consequence.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * @author dmillerw
 */
public class Script {

    public String author;
    public String mainFile;

    public transient Globals globals;

    public transient LuaValue luaChunk;
}
