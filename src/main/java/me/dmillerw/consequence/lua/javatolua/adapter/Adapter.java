package me.dmillerw.consequence.lua.javatolua.adapter;

import com.google.common.base.Throwables;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import me.dmillerw.consequence.Consequence;
import me.dmillerw.consequence.util.MappingLookup;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * @author dmillerw
 */
public class Adapter {

    public static final Adapter BLANK = new Adapter();

    public static class Data {

        @SerializedName("class")
        public String clazz;

        public VariableInfo[] variables = new VariableInfo[0];
        public MethodInfo[] methods = new MethodInfo[0];

        public transient String filename;
    }

    public static class VariableInfo {

        private Name name;
        private String type;

        public String lua() {
            return name.name[0];
        }

        public String java() {
            return name.name.length == 1 ? name.name[0] : name.name[1];
        }
    }

    public static class MethodInfo {

        private Name name;
        public String[] parameters = new String[0];
        @SerializedName("return_type")
        public String returnType = "V";

        public String lua() {
            return name.name[0];
        }

        public String java() {
            return name.name.length == 1 ? name.name[0] : name.name[1];
        }
    }

    public static class Name {

        public String[] name;
    }

    // Temporary
    public static Adapter merge(Adapter base, Adapter ... adapter) {
        for (Adapter a : adapter) {
            base.variables.putAll(a.variables);
            base.methods.putAll(a.methods);
            base.luaToJavaMap.putAll(a.luaToJavaMap);
        }

        return base;
    }

    public Class clazz;

    public final Map<String, Field> variables;
    public final Map<String, Method> methods;

    public final Map<String, VarArgFunction> luaMethodCalls;

    public final BiMap<String, String> luaToJavaMap;

    public Adapter() {
        this.variables = Maps.newHashMap();
        this.methods = Maps.newHashMap();
        this.luaMethodCalls = Maps.newHashMap();
        this.luaToJavaMap = HashBiMap.create();
    }

    public Adapter(Data data) {
        try {
            this.clazz = Class.forName(data.clazz.replace("/", "."));
        } catch (ClassNotFoundException e) {
            this.clazz = null;

            Consequence.INSTANCE.logger.warn("Failed to load " + data.filename + ". " + data.clazz + " is not a valid Java class");
        }

        this.variables = Maps.newHashMap();
        this.methods = Maps.newHashMap();
        this.luaMethodCalls = Maps.newHashMap();
        this.luaToJavaMap = HashBiMap.create();

        if (this.clazz == null)
            return;

        initializeReflection(data);
    }

    private void initializeReflection(Data data) {
        String className = data.clazz.replace(".", "/");

        for (VariableInfo variable : data.variables) {
            String name = MappingLookup.mapField(className, variable.java(), variable.type);

            Field field;
            try {
                field = clazz.getField(name);
            } catch (NoSuchFieldException ex) {
                field = null;

                Consequence.INSTANCE.logger.warn("Ran into an issue while building adapters from " + data.filename);
                Consequence.INSTANCE.logger.warn("The variable " + name + " does not exist within " + data.clazz + ". It will be ignored");
            }

            if (field != null) {
                luaToJavaMap.put(variable.lua(), name);
                variables.put(name, field);
            }
        }

        for (MethodInfo methodInfo : data.methods) {
            Class[] params = new Class[methodInfo.parameters.length];
            for (int i=0; i<params.length; i++) {
                try {
                    params[i] = getClassFromString(methodInfo.parameters[i]);
                } catch (ClassNotFoundException ex) {
                    Consequence.INSTANCE.logger.warn("Ran into an issue while building adapters from " + data.filename);
                    Consequence.INSTANCE.logger.warn("The paramater " + methodInfo.parameters[i] + " is not a valid class. The method " + methodInfo.java() + " will be ignored");
                }
            }

            String desc = "(";
            for (int i=0; i<methodInfo.parameters.length; i++) desc = desc + formatClassToASM(methodInfo.parameters[i]);
            desc = desc + ")" + formatClassToASM(methodInfo.returnType);

            String name = MappingLookup.mapMethod(className, methodInfo.java(), desc);

            Method method;
            try {
                method = clazz.getMethod(name, params);
            } catch (NoSuchMethodException ex) {
                method = null;

                Consequence.INSTANCE.logger.warn("Ran into an issue while building adapters from " + data.filename);
                Consequence.INSTANCE.logger.warn("The method " + name + " and parameters " + Arrays.toString(methodInfo.parameters) + " do not exist within " + data.clazz + ". It will be ignored");
            }

            if (method != null) {
                luaToJavaMap.put(methodInfo.lua(), name + "()");
                methods.put(name + "()", method);
            }
        }
    }

    private String formatClassToASM(String string) {
        if (string.length() > 1) {
            return "L" + MappingLookup.mapClass(string.replace(".", "/")) + ";";
        } else {
            return string;
        }
    }

    private Class getClassFromString(String type) throws ClassNotFoundException {
        if (type.length() == 1) {
            return getPrimitiveClassFromString(type);
        } else {
            return Class.forName(type.replace("/", "."));
        }
    }

    private Class getPrimitiveClassFromString(String type) throws ClassNotFoundException {
        if (type.equalsIgnoreCase("B")) {
            return byte.class;
        } else if (type.equals("C")) {
            return char.class;
        } else if (type.equals("D")) {
            return double.class;
        } else if (type.equals("F")) {
            return float.class;
        } else if (type.equals("I")) {
            return int.class;
        } else if (type.equals("J")) {
            return long.class;
        } else if (type.equals("S")) {
            return short.class;
        } else if (type.equals("Z")) {
            return boolean.class;
        } else {
            Throwables.propagate(new ClassNotFoundException());
            return null;
        }
    }
}
