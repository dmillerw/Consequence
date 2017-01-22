package me.dmillerw.consequence.lua.javatolua.adapter;

import com.google.common.base.Throwables;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import me.dmillerw.consequence.Consequence;

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
    public static Adapter merge(Adapter ... adapter) {
        Adapter merged = new Adapter();
        for (Adapter a : adapter) {
            merged.variables.putAll(a.variables);
            merged.methods.putAll(a.methods);
            merged.luaToJavaMap.putAll(a.luaToJavaMap);
        }
        return merged;
    }

    public Class clazz;

    public final Map<String, Field> variables;
    public final Map<String, Method> methods;

    public final BiMap<String, String> luaToJavaMap;

    public Adapter() {
        this.variables = Maps.newHashMap();
        this.methods = Maps.newHashMap();
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
        this.luaToJavaMap = HashBiMap.create();

        if (this.clazz == null)
            return;

        try {
            initializeReflection(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initializeReflection(Data data) {
        for (VariableInfo variable : data.variables) {
            Field field;
            try {
                field = clazz.getField(variable.java());
            } catch (NoSuchFieldException ex) {
                field = null;

                Consequence.INSTANCE.logger.warn("Ran into an issue while building adapters from " + data.filename);
                Consequence.INSTANCE.logger.warn("The variable " + variable.java() + " does not exist within " + data.clazz + ". It will be ignored");
            }

            if (field != null) {
                luaToJavaMap.put(variable.lua(), variable.java());
                variables.put(variable.java(), field);
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

            Method method;
            try {
                method = clazz.getMethod(methodInfo.java(), params);
            } catch (NoSuchMethodException ex) {
                method = null;

                Consequence.INSTANCE.logger.warn("Ran into an issue while building adapters from " + data.filename);
                Consequence.INSTANCE.logger.warn("The method " + methodInfo.java() + " and parameters " + Arrays.toString(methodInfo.parameters) + " do not exist within " + data.clazz + ". It will be ignored");
            }

            if (method != null) {
                luaToJavaMap.put(methodInfo.lua(), methodInfo.java() + "()");
                methods.put(methodInfo.java() + "()", method);
            }
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
