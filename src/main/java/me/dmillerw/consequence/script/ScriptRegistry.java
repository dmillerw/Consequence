package me.dmillerw.consequence.script;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.dmillerw.consequence.Consequence;
import me.dmillerw.consequence.lua.javatolua.JavaToLua;
import me.dmillerw.consequence.util.GsonUtil;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.TwoArgFunction;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author dmillerw
 */
public class ScriptRegistry {

    private static Map<String, Script> loadedScripts = Maps.newHashMap();
    private static Map<String, Set<Script.OwnedFunction>> eventHandlers = Maps.newHashMap();

    public static boolean reloadScript(String tag) {
        if (!loadedScripts.containsKey(tag)) {
            return false;
        }

        Script script = loadedScripts.get(tag);
        script.globals = null;
        script.luaChunk = null;

        Iterator<Set<Script.OwnedFunction>> iterator = eventHandlers.values().iterator();
        while (iterator.hasNext())
            iterator.next().removeIf(function -> function.owner.equals(tag));

        loadScript(script);

        return true;
    }

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
                    Consequence.INSTANCE.logger.warn("Failed to load " + dir.getName() + "/info.json. Any attached scripts will not be loaded");
                    Consequence.INSTANCE.logger.warn(ex);

                    continue;
                }

                if (script != null) {
                    script.main = new File(dir, script.mainFile);
                    loadScript(script);
                }
            }
        }
    }

    private static void loadScript(Script script) {
        Globals globals = new Globals();
        Consequence.PROXY.buildLuaGlobals(globals);
        JavaToLua.generateStaticAccessors(globals);
        LoadState.install(globals);
        LuaC.install(globals);

        script.globals = globals;

        if (!script.main.exists()) {
            Consequence.INSTANCE.logger.warn("Couldn't load " + script.main.getName() + " as it does not exist");
            return;
        }

        try {
            script.luaChunk = script.globals.load(new FileReader(script.main), script.mainFile);
        } catch (IOException ex) {
            Consequence.INSTANCE.logger.warn("Failed to load script from " + script.main.getName());
            Consequence.INSTANCE.logger.warn(ex);
        }

        if (script.luaChunk != null) {
            script.luaChunk.call();
            script.globals.get("main").call(new Registry(script.tag));
        }

        loadedScripts.put(script.tag, script);
    }

    public static <T extends Event> void fireEvent(String tag, Class<T> clazz, T event) {
        LuaValue eventValue = JavaToLua.convert(event);
        eventValue.rawset("side", JavaToLua.convert(FMLCommonHandler.instance().getSide()));

        if (!eventHandlers.containsKey(tag))
            return;

        for (Script.OwnedFunction handler : eventHandlers.get(tag)) {
            if (handler.disabled)
                continue;

            try {
                handler.function.call(eventValue);
            } catch (Exception ex) {
                handler.disabled = true;

                Consequence.INSTANCE.logger.warn("The script '" + handler.owner + "' into an issue while handling event'" + tag + "'");
                Consequence.INSTANCE.logger.warn("To prevent issues and negative effects to gameplay, it will be disabled until the next time the game starts");
                Consequence.INSTANCE.logger.warn(ex);

                ex.printStackTrace();
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
