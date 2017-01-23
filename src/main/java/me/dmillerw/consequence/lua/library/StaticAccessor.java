package me.dmillerw.consequence.lua.library;

import me.dmillerw.consequence.lua.javatolua.JavaToLua;
import me.dmillerw.consequence.lua.javatolua.adapter.Adapter;
import me.dmillerw.consequence.lua.luatojava.LuaToJava;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Method;

/**
 * @author dmillerw
 */
public class StaticAccessor extends Library {

    private Adapter adapter;
    public StaticAccessor(Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public String name() {
        return adapter.simpleName;
    }

    @Override
    public void register(LuaValue library) {
        for (String name : adapter.constructors.keySet()) {
            VarArgFunction function = adapter.luaConstructorCalls.get(name);
            if (function == null) {
                java.lang.reflect.Constructor constructor = adapter.constructors.get(name);
                function = new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        Object[] data = new Object[constructor.getParameters().length];
                        for (int i=0; i < data.length; i++) {
                            data[i] = LuaToJava.convert(constructor.getParameters()[i].getType(), args.arg(i + 1));
                        }

                        try {
                            return JavaToLua.convert(constructor.newInstance(data));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return NIL;
                        }
                    }
                };
            }

            library.set(name, function);
        }

        for (String name : adapter.staticMethods) {
            VarArgFunction function = adapter.luaMethodCalls.get(name);
            if (function == null) {
                Method method = adapter.methods.get(name);
                function = new VarArgFunction() {

                    @Override
                    public Varargs invoke(Varargs args) {
                        Object[] data = new Object[method.getParameters().length];
                        for (int i=0; i < data.length; i++) {
                            data[i] = LuaToJava.convert(method.getParameters()[i].getType(), args.arg(i + 1));
                        }

                        try {
                            return JavaToLua.convert(method.invoke(null, data));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return NIL;
                        }
                    }
                };

                adapter.luaMethodCalls.put(name, function);
            }

            library.set(adapter.luaToJavaMap.inverse().get(name), function);
        }
    }
}
