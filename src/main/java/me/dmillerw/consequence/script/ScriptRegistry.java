package me.dmillerw.consequence.script;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.dmillerw.consequence.lua.javatolua.JavaToLua;
import me.dmillerw.consequence.util.GsonUtil;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author dmillerw
 */
public class ScriptRegistry {

    private static Set<Script> loadedScripts = Sets.newHashSet();
    private static Map<String, Set<LuaValue>> eventHandlers = Maps.newHashMap();

    public static void initialize(File directory) {
        for (File dir : directory.listFiles()) {
            if (dir.isDirectory()) {
                File info = new File(dir, "info.json");
                if (!info.exists())
                    continue;

                Script script = null;
                try {
                    script = GsonUtil.gson().fromJson(new FileReader(info), Script.class);
                } catch (IOException ignore) {}

                if (script != null) {
                    script.globals = JsePlatform.standardGlobals(); //TODO Our own globals
                    try {
                        script.luaChunk = script.globals.load(new FileReader(new File(dir, script.mainFile)), script.mainFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    if (script.luaChunk != null) {
                        script.luaChunk.call();
                        script.globals.get("main").call(new Registry());
                    }

                    loadedScripts.add(script);
                }
            }
        }
    }

    public static <T> void fireEvent(String tag, Class<T> clazz, T event) {
        LuaValue eventValue = JavaToLua.convert(event);
        for (LuaValue handler : eventHandlers.get(tag)) {
            try {
                handler.call(eventValue);
            } catch (Exception ex) { System.out.println(ex.getMessage()); }
        }
    }

    private static class Registry extends LuaTable {

        public Registry() {
            set("register_event_handler", new RegisterEventHandler());
        }

        private static class RegisterEventHandler extends TwoArgFunction {

            @Override
            public LuaValue call(LuaValue event, LuaValue function) {
                if (function.typename().equalsIgnoreCase("function")) {
                    Set<LuaValue> set = ScriptRegistry.eventHandlers.get(event.tojstring());
                    if (set == null) set = Sets.newHashSet();
                    set.add(function);
                    ScriptRegistry.eventHandlers.put(event.tojstring(), set);
                    return LuaBoolean.valueOf(true);
                }

                return LuaBoolean.valueOf(false);
            }
        }
    }
}
