package me.dmillerw.consequence.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * @author dmillerw
 */
public class Script {

    public String tag;
    public String mainFile;

    public transient Globals globals;
    public transient LuaValue luaChunk;

    public static class OwnedFunction {

        public String owner;
        public LuaValue function;

        public boolean disabled = false;
    }
}
