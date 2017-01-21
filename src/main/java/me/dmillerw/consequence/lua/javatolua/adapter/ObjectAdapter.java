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
public class ObjectAdapter extends BaseAdapter {

    private final AdapterInfo info;
    private final Object object;

    public ObjectAdapter(Object object) {
        super();

        this.info = JavaToLua.getAdapter(object.getClass());
        this.object = object;

        this.rawset("_java_class", this.info.clazz);
    }

    @Override
    public LuaValue getValue(String key) {
        String java = info.data.luaToJavaMap.get(key);
        if (java != null) {
            if (java.endsWith("()")) {
                Method method = info.data.methods.get(java);
                return new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        Object[] data = new Object[method.getParameters().length];
                        for (int i = 0; i < data.length; i++) {
                            data[i] = LuaToJava.convert(method.getParameters()[i].getType(), args.arg(i + 1));
                        }

                        try {
                            return JavaToLua.convert(method.invoke(ObjectAdapter.this.object, data));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return NIL;
                        }
                    }
                };
            } else {
                try {
                    return JavaToLua.convert(info.data.variables.get(java).get(this.object));
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
        String java = info.data.luaToJavaMap.get(key);
        if (java != null) {
            if (java.endsWith("()")) {
                return true;
            } else {
                Field field = info.data.variables.get(java);
                try {field.set(this.object, LuaToJava.convert(field.getType(), value));} catch (Exception ignore) { ignore.printStackTrace(); }
                return true;
            }
        } else {
            return false;
        }
    }
}
