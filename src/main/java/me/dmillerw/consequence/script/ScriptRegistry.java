package me.dmillerw.consequence.script;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.dmillerw.consequence.Consequence;
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

    private static Map<String, Script> loadedScripts = Maps.newHashMap();
    private static Map<String, Set<Script.OwnedFunction>> eventHandlers = Maps.newHashMap();

    public static void initialize(File directory) {
        for (File dir : directory.listFiles()) {
            if (dir.isDirectory()) {
                File info = new File(dir, "info.json");
                if (!info.exists())
                    continue;

                Script script = null;
                try {
                    script = GsonUtil.gson().fromJson(new FileReader(info), Script.class);
                } catch (IOException ex) {
                    Consequence.logger.warn("Failed to load " + dir.getName() + "/info.json. Any attached scripts will not be loaded");
                    Consequence.logger.warn(ex);

                    continue;
                }

                if (script != null) {
                    script.globals = JsePlatform.standardGlobals(); //TODO Our own globals

                    File main = new File(dir, script.mainFile);
                    if (!main.exists()) {
                        Consequence.logger.warn("Couldn't load " + dir.getName() + "/" + script.mainFile + " as it does not exist");
                        continue;
                    }

                    try {
                        script.luaChunk = script.globals.load(new FileReader(main), script.mainFile);
                    } catch (IOException ex) {
                        Consequence.logger.warn("Failed to load script from " + main.getName());
                        Consequence.logger.warn(ex);
                    }

                    if (script.luaChunk != null) {
                        script.luaChunk.call();
                        script.globals.get("main").call(new Registry(script.tag));
                    }

                    loadedScripts.put(script.tag, script);
                }
            }
        }
    }

    public static <T> void fireEvent(String tag, Class<T> clazz, T event) {
        LuaValue eventValue = JavaToLua.convert(event);
        for (Script.OwnedFunction handler : eventHandlers.get(tag)) {
            try {
                handler.function.call(eventValue);
            } catch (Exception ex) {
                Consequence.logger.warn("The script '" + handler.owner + "' into an issue while handling event'" + tag + "'");
                Consequence.logger.warn("To prevent issues and negative effects to gameplay, it will be disabled until the next time the game starts");
                Consequence.logger.warn(ex);
            }
        }
    }

    private static class Registry extends LuaTable {

        private String owner;
        public Registry(String owner) {
            this.owner = owner;

            set("register_event_handler", new RegisterEventHandler());
        }

        private class RegisterEventHandler extends TwoArgFunction {

            @Override
            public LuaValue call(LuaValue event, LuaValue function) {
                if (function.typename().equalsIgnoreCase("function")) {
                    Set<Script.OwnedFunction> set = ScriptRegistry.eventHandlers.get(event.tojstring());
                    if (set == null) set = Sets.newHashSet();

                    Script.OwnedFunction ownedFunction = new Script.OwnedFunction();
                    ownedFunction.owner = Registry.this.owner;
                    ownedFunction.function = function;

                    set.add(ownedFunction);

                    ScriptRegistry.eventHandlers.put(event.tojstring(), set);

                    return LuaBoolean.valueOf(true);
                }

                return LuaBoolean.valueOf(false);
            }
        }
    }
}
