package me.dmillerw.consequence.lua.javatolua;

import com.google.common.collect.Maps;
import me.dmillerw.consequence.lua.javatolua.adapter.AdapterInfo;
import me.dmillerw.consequence.lua.javatolua.adapter.ObjectAdapter;
import me.dmillerw.consequence.lua.javatolua.adapter.special.ListAdapter;
import me.dmillerw.consequence.util.GsonUtil;
import org.luaj.vm2.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dmillerw
 */
public class JavaToLua {

    public static LuaValue convert(Object object) {
        if (object == null)
            return LuaValue.NIL;

        if (LuaValue.class.isAssignableFrom(object.getClass())) {
            return (LuaValue) object;
        }

        if (object.getClass().isEnum()) {
            return primitiveToLua(((Enum)object).name());
        }

        if (object instanceof List) {
            return new ListAdapter((List) object);
        }

        if (isPrimitive(object)) {
            return primitiveToLua(object);
        } else {
            return adaptObject(object);
        }
    }

    /* ADAPTERS */

    private static Map<Class, AdapterInfo> registeredAdapters = Maps.newHashMap();

    public static void initializeAdapterRegistry(File directory) {
        for (File file : directory.listFiles((dir, name) -> name.endsWith("json"))) {
            try {
                AdapterInfo info = GsonUtil.gson().fromJson(new FileReader(file), AdapterInfo.class);
                registerAdapter(info);
            } catch (IOException ex) {
                continue;
            }
        }

        for (Class clazz : registeredAdapters.keySet()) {
            AdapterInfo base = registeredAdapters.get(clazz);
            // For each class we have a type adapter for, we travel up its superclass chain, merging any data
            // we find, so that a class adapter has all the defined information of its parent classes as well

            Class parent = clazz.getSuperclass();
            while (parent != Object.class) {
                AdapterInfo info = registeredAdapters.get(parent);
                if (info != null) {
                    base.data = AdapterInfo.Data.merge(base.data, info.data);
                }

                parent = parent.getSuperclass();
            }

            registeredAdapters.put(clazz, base);
        }
    }

    public static void registerAdapter(AdapterInfo info) {
        if (info.data == null) info.data = new AdapterInfo.Data(info);
        registeredAdapters.put(info.data.clazz, info);
    }

    public static AdapterInfo getAdapter(Class clazz) {
        if (registeredAdapters.containsKey(clazz)) {
            return registeredAdapters.get(clazz);
        } else {
            return AdapterInfo.BLANK;
        }
    }

    public static ObjectAdapter adaptObject(Object object) {
        return new ObjectAdapter(object);
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
            return LuaValue.NIL;
        else
            return primitiveToLua.call(object);
    }

    private static interface PrimitiveToLua<T extends Object> {
        public LuaValue call(T object);
    }
    
    /* END PRIMITIVES */
}
