package me.dmillerw.consequence.lua.luatojava;

import me.dmillerw.consequence.lua.javatolua.adapter.ObjectAdapter;
import org.luaj.vm2.LuaValue;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dmillerw
 */
public class LuaToJava {

    public static Object convert(Class javaType, LuaValue value) {
        if (value.isnil())
            return null;

        if (javaType.isEnum()) {
            return Enum.valueOf(javaType, value.tojstring());
        }

        if (isPrimitive(javaType)) {
            return luaToPrimitive(javaType, value);
        } else {
            if (LuaValue.class.isAssignableFrom(javaType)) {
                return value;
            } else {
                if (value instanceof ObjectAdapter) {
                    if (((ObjectAdapter) value).getObject().getClass().isAssignableFrom(javaType)) {
                        return ((ObjectAdapter) value).getObject();
                    } else {
                        return null;
                    }
                } else {
                    throw new NotImplementedException();
                }
            }
        }
    }

    /* PRIMITIVES */

    private static Map<Class, LuaToPrimitive> primitiveMap = new HashMap<>();

    static {
        map(byte.class, LuaToJava::luaToByte);
        map(Byte.class, LuaToJava::luaToByte);
        map(boolean.class, LuaToJava::luaToBoolean);
        map(Boolean.class, LuaToJava::luaToBoolean);
        map(short.class, LuaToJava::luaToShort);
        map(Short.class, LuaToJava::luaToShort);
        map(int.class, LuaToJava::luaToInt);
        map(Integer.class, LuaToJava::luaToInt);
        map(float.class, LuaToJava::luaToFloat);
        map(Float.class, LuaToJava::luaToFloat);
        map(double.class, LuaToJava::luaToDouble);
        map(Double.class, LuaToJava::luaToDouble);
        map(String.class, LuaToJava::luaToString);
    }

    private static <T extends Object> void map(Class<T> type, LuaToPrimitive<T> luaToPrimitive) {
        primitiveMap.put(type, luaToPrimitive);
    }

    private static byte luaToByte(LuaValue value) {
        return value.tobyte();
    }

    private static boolean luaToBoolean(LuaValue value) {
        return value.toboolean();
    }

    private static short luaToShort(LuaValue value) {
        return value.toshort();
    }

    private static int luaToInt(LuaValue value) {
        return value.toint();
    }

    private static float luaToFloat(LuaValue value) {
        return value.tofloat();
    }

    private static double luaToDouble(LuaValue value) {
        return value.todouble();
    }

    private static String luaToString(LuaValue value) {
        return value.tojstring();
    }

    public static boolean isPrimitive(Class clazz) {
        return primitiveMap.containsKey(clazz);
    }

    public static <T> T luaToPrimitive(Class<T> clazz, LuaValue value) {
        LuaToPrimitive<T> luaToPrimitive = LuaToJava.primitiveMap.get(clazz);
        if (luaToPrimitive == null) {
            return null;
        } else {
            return luaToPrimitive.call(value);
        }
    }

    private static interface LuaToPrimitive<T extends Object> {
        public T call(LuaValue value);
    }

    /* END PRIMITIVES*/
}
