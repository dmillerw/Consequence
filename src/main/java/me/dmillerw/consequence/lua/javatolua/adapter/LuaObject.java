package me.dmillerw.consequence.lua.javatolua.adapter;

import me.dmillerw.consequence.lua.javatolua.JavaToLua;
import me.dmillerw.consequence.lua.luatojava.LuaToJava;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author dmillerw
 */
public class LuaObject extends SimpleTable {

    private final Adapter adapter;
    private final Object object;

    public LuaObject(Adapter adapter, Object object) {
        super();

        this.adapter = adapter;
        this.object = object;

        this.rawset("_java_class", object.getClass().getName());
    }

    public final Adapter getAdapter() {
        return adapter;
    }

    public final Object getObject() {
        return object;
    }

    @Override
    public LuaValue getValue(String key) {
        String java = adapter.luaToJavaMap.get(key);
        if (java != null) {
            if (java.endsWith("()")) {
                Method method = adapter.methods.get(java);
                return new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        Object[] data = new Object[method.getParameters().length];
                        for (int i = 0; i < data.length; i++) {
                            data[i] = LuaToJava.convert(method.getParameters()[i].getType(), args.arg(i + 1));
                        }

                        try {
                            return JavaToLua.convert(method.invoke(LuaObject.this.object, data));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return NIL;
                        }
                    }
                };
            } else {
                try {
                    return JavaToLua.convert(adapter.variables.get(java).get(this.object));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return NIL;
                }
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean setValue(String key, LuaValue value) {
        String java = adapter.luaToJavaMap.get(key);
        if (java != null) {
            if (java.endsWith("()")) {
                return true;
            } else {
                Field field = adapter.variables.get(java);
                try {field.set(this.object, LuaToJava.convert(field.getType(), value));} catch (Exception ignore) { ignore.printStackTrace(); }
                return true;
            }
        } else {
            return false;
        }
    }
}
