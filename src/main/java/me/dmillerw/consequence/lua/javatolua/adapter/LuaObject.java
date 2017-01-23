package me.dmillerw.consequence.lua.javatolua.adapter;

import me.dmillerw.consequence.lua.javatolua.JavaToLua;
import me.dmillerw.consequence.lua.luatojava.LuaToJava;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author dmillerw
 */
public class LuaObject extends LuaTable {

    private final Adapter adapter;
    private final Object object;

    public LuaObject(Adapter adapter, Object object) {
        super();

        this.adapter = adapter;
        this.object = object;

        this.rawset("_java_class", object.getClass().getName());

        this.fillTable();
    }

    public final Adapter getAdapter() {
        return adapter;
    }

    public final Object getObject() {
        return object;
    }

    private final void fillTable() {
        // Variables
        for (String variable : adapter.variables.keySet()) {
            Field field = adapter.variables.get(variable);
            LuaValue value = NIL;
            try {
                value = JavaToLua.convert(field.get(object));
            } catch (Exception ignore) {
            }

            rawset(adapter.luaToJavaMap.inverse().get(variable), value);
        }

        // Methods
        for (String function : adapter.methods.keySet()) {
            Method method = adapter.methods.get(function);

            VarArgFunction luaFunction = adapter.luaMethodCalls.get(function);
            if (luaFunction == null) {
                if (!adapter.staticMethods.contains(function)) {
                    luaFunction = new VarArgFunction() {

                        @Override
                        public Varargs invoke(Varargs args) {
                            LuaValue self = args.arg(1);
                            if (self instanceof LuaObject && ((LuaObject) self).adapter.clazz.equals(adapter.clazz)) {
                                Object[] data = new Object[method.getParameters().length];
                                for (int i=0; i < data.length; i++) {
                                    data[i] = LuaToJava.convert(method.getParameters()[i].getType(), args.arg(i + 2));
                                }

                                try {
                                    return JavaToLua.convert(method.invoke(((LuaObject) self).object, data));
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    return NIL;
                                }
                            } else {
                                return NIL;
                            }
                        }
                    };
                }

                adapter.luaMethodCalls.put(function, luaFunction);
            }

            rawset(adapter.luaToJavaMap.inverse().get(function), luaFunction);
        }
    }

    private boolean setValue(String key, LuaValue value) {
        String java = adapter.luaToJavaMap.get(key);
        if (java != null) {
            if (java.endsWith("()")) {
                return false;
            } else {
                Field field = adapter.variables.get(java);
                try {
                    field.set(this.object, LuaToJava.convert(field.getType(), value));
                    return true;
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }

                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public final void set(int key, LuaValue value) {
        if (setValue(String.valueOf(key), value)) super.set(key, value);
    }

    @Override
    public final void set(LuaValue key, LuaValue value) {
        if (setValue(key.tojstring(), value)) super.set(key, value);
    }

    @Override
    public final void set(int key, String value) {
        if (setValue(String.valueOf(key), LuaValue.valueOf(value))) super.set(key, value);
    }

    @Override
    public final void set(String key, LuaValue value) {
        if (setValue(key, value)) super.set(key, value);
    }

    @Override
    public final void set(String key, double value) {
        if (setValue(key, LuaValue.valueOf(value))) super.set(key, value);
    }

    @Override
    public final void set(String key, int value) {
        if (setValue(key, LuaValue.valueOf(value))) super.set(key, value);
    }

    @Override
    public final void set(String key, String value) {
        if (setValue(key, LuaValue.valueOf(value))) super.set(key, value);
    }
}
