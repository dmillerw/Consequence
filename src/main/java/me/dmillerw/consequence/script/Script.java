package me.dmillerw.consequence.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

import java.io.File;

/**
 * @author dmillerw
 */
public class Script {

    public String tag;
    public String mainFile;

    public transient File main;
    public transient Globals globals;
    public transient LuaValue luaChunk;

    public static class OwnedFunction {

        public String owner;
        public LuaValue function;

        public boolean disabled = false;
    }
}
