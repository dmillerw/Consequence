package me.dmillerw.consequence.lua.javatolua;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import me.dmillerw.consequence.Consequence;
import me.dmillerw.consequence.lua.javatolua.adapter.Adapter;
import me.dmillerw.consequence.lua.javatolua.adapter.LuaObject;
import me.dmillerw.consequence.lua.javatolua.adapter.special.ListAdapter;
import me.dmillerw.consequence.lua.transform.TransformerRegistry;
import me.dmillerw.consequence.util.GsonUtil;
import org.luaj.vm2.*;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.luaj.vm2.LuaValue.NIL;

/**
 * @author dmillerw
 */
public class JavaToLua {

    public static LuaValue convert(Object object) {
        if (object == null)
            return NIL;

        if (LuaValue.class.isAssignableFrom(object.getClass())) {
            return (LuaValue) object;
        }

        if (object.getClass().isEnum()) {
            return primitiveToLua(((Enum)object).name());
        }

        //TODO: This is NOT teh way to handle this
        if (object instanceof List) {
            return new ListAdapter((List) object);
        }

        if (isPrimitive(object)) {
            return primitiveToLua(object);
        } else {
            LuaObject luaObject = adaptObject(object);
            if (luaObject != null) {
                return luaObject;
            } else {
                LuaValue value = TransformerRegistry.transform(object);
                if (value != null) {
                    return value;
                } else {
                    return NIL;
                }
            }
        }
    }

    /* ADAPTERS */

    private static Map<Class, Adapter> registeredAdapters = Maps.newHashMap();

    private static void loadFiles(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                loadFiles(file);
                continue;
            }

            if (!Files.getFileExtension(file.getName()).equalsIgnoreCase("json"))
                continue;

            Adapter.Data data;
            try {
                data = GsonUtil.gson().fromJson(new FileReader(file), Adapter.Data.class);
            } catch (Exception ex) {
                Consequence.INSTANCE.logger.warn("Failed to load type adapter from " + file.getName());
                Consequence.INSTANCE.logger.warn(ex);
                continue;
            }

            data.filename = file.getName();

            registerAdapter(new Adapter(data));
        }
    }

    public static void initializeAdapterRegistry(File directory) {
        loadFiles(directory);

        for (Class clazz : registeredAdapters.keySet()) {
            // First, for each class we have registered, merge in any adapters that have to do with
            // interfaces this class implements
            Adapter base = registeredAdapters.get(clazz);

            for (Class iface : clazz.getInterfaces()) {
                Adapter info = registeredAdapters.get(iface);
                if (info != null) {
                    base = Adapter.merge(base, info);
                }
            }

            //Then travel up its superclass chain, merging any data
            // we find, so that a class adapter has all the defined information of its parent classes as well
            Class parent = clazz.getSuperclass();
            while (parent != null && parent != Object.class) {
                Adapter info = registeredAdapters.get(parent);
                if (info != null) {
                    base = Adapter.merge(base, info);
                }

                parent = parent.getSuperclass();
            }

            registeredAdapters.put(clazz, base);
        }
    }

    public static void registerAdapter(Adapter info) {
        if (info.clazz == null) return;
        registeredAdapters.put(info.clazz, info);
    }

    public static Adapter getAdapter(Class clazz) {
        if (clazz == Object.class)
            return Adapter.BLANK;

        if (registeredAdapters.containsKey(clazz)) {
            return registeredAdapters.get(clazz);
        } else {
            return getAdapter(clazz.getSuperclass());
        }
    }

    public static LuaObject adaptObject(Object object) {
        Adapter adapter = JavaToLua.getAdapter(object.getClass());
        if (adapter == null)
            return null;
        else
            return new LuaObject(adapter, object);
    }

    /* END ADAPTERS */

    /* PRIMITIVES */

    private static Map<Class, PrimitiveToLua> primitiveMap = new HashMap<>();

    static {
        map(byte.class, JavaToLua::byteToLua);
        map(Byte.class, JavaToLua::byteToLua);
        map(boolean.class, JavaToLua::booleanToLua);
        map(Boolean.class, JavaToLua::booleanToLua);
        map(short.class, JavaToLua::shortToLua);
        map(Short.class, JavaToLua::shortToLua);
        map(int.class, JavaToLua::intToLua);
        map(Integer.class, JavaToLua::intToLua);
        map(float.class, JavaToLua::floatToLua);
        map(Float.class, JavaToLua::floatToLua);
        map(double.class, JavaToLua::doubleToLua);
        map(Double.class, JavaToLua::doubleToLua);
        map(String.class, JavaToLua::stringToLua);
    }

    private static <T extends Object> void map(Class<T> type, PrimitiveToLua<T> primitiveToLua) {
        JavaToLua.primitiveMap.put(type, primitiveToLua);
    }

    /* TO LUA */
    private static LuaInteger byteToLua(byte b) {
        return LuaInteger.valueOf(b);
    }

    private static LuaBoolean booleanToLua(boolean bool) {
        return LuaBoolean.valueOf(bool);
    }

    private static LuaInteger shortToLua(short s) {
        return LuaInteger.valueOf(s);
    }

    private static LuaInteger intToLua(int i) {
        return LuaInteger.valueOf(i);
    }

    private static LuaNumber floatToLua(float f) {
        return LuaDouble.valueOf(f);
    }

    private static LuaNumber doubleToLua(double d) {
        return LuaDouble.valueOf(d);
    }

    private static LuaString stringToLua(String string) {
        return LuaString.valueOf(string);
    }

    private static boolean isPrimitive(Class clazz) {
        return primitiveMap.containsKey(clazz);
    }

    private static boolean isPrimitive(Object object) {
        return primitiveMap.containsKey(object.getClass());
    }

    private static LuaValue primitiveToLua(Object object) {
        PrimitiveToLua primitiveToLua = JavaToLua.primitiveMap.get(object.getClass());
        if (primitiveToLua == null)
            return NIL;
        else
            return primitiveToLua.call(object);
    }

    private static interface PrimitiveToLua<T extends Object> {
        public LuaValue call(T object);
    }
    
    /* END PRIMITIVES */
}
