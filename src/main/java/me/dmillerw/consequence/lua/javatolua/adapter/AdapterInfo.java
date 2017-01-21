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
public class AdapterInfo {

    public static final AdapterInfo BLANK = new AdapterInfo();

    @SerializedName("class")
    public String clazz;

    public VariableInfo[] variables = new VariableInfo[0];
    public MethodInfo[] methods = new MethodInfo[0];

    public transient String filename;
    public transient Data data;

    public static class VariableInfo {

        @SerializedName("java_name")
        public String javaName;
        @SerializedName("lua_name")
        public String luaName;
    }

    public static class MethodInfo {

        @SerializedName("java_name")
        public String javaName;
        @SerializedName("lua_name")
        public String luaName;
        public String[] paramaters = new String[0];
    }

    public static class Data {

        // Temporary
        public static Data merge(Data ... data) {
            Data merged = new Data();
            for (Data d : data) {
                merged.variables.putAll(d.variables);
                merged.methods.putAll(d.methods);
                merged.luaToJavaMap.putAll(d.luaToJavaMap);
            }
            return merged;
        }

        public Class clazz;

        public final Map<String, Field> variables;
        public final Map<String, Method> methods;

        public final BiMap<String, String> luaToJavaMap;

        public Data() {
            this.variables = Maps.newHashMap();
            this.methods = Maps.newHashMap();
            this.luaToJavaMap = HashBiMap.create();
        }

        public Data(AdapterInfo parent) {
            try {
                this.clazz = Class.forName(parent.clazz.replace("/", "."));
            } catch (ClassNotFoundException e) {
                this.clazz = null;

                Consequence.logger.warn("Failed to load " + parent.filename + ". " + parent.clazz + " is not a valid Java class");
            }


            this.variables = Maps.newHashMap();
            this.methods = Maps.newHashMap();
            this.luaToJavaMap = HashBiMap.create();

            if (this.clazz == null)
                return;

            try {
                initializeReflection(parent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void initializeReflection(AdapterInfo parent) {
            for (VariableInfo variable : parent.variables) {
                Field field;
                try {
                    field = clazz.getField(variable.javaName);
                } catch (NoSuchFieldException ex) {
                    field = null;

                    Consequence.logger.warn("Ran into an issue while building adapters from " + parent.filename);
                    Consequence.logger.warn("The variable " + variable.javaName + " does not exist within " + parent.clazz + ". It will be ignored");
                }

                if (field != null) {
                    luaToJavaMap.put(variable.luaName, variable.javaName);
                    variables.put(variable.javaName, field);
                }
            }

            for (MethodInfo methodInfo : parent.methods) {
                Class[] params = new Class[methodInfo.paramaters.length];
                for (int i=0; i<params.length; i++) {
                    try {
                        params[i] = getClassFromString(methodInfo.paramaters[i]);
                    } catch (ClassNotFoundException ex) {
                        Consequence.logger.warn("Ran into an issue while building adapters from " + parent.filename);
                        Consequence.logger.warn("The paramater " + methodInfo.paramaters[i] + " is not a valid class. The method " + methodInfo.javaName + " will be ignored");
                    }
                }

                Method method;
                try {
                    method = clazz.getMethod(methodInfo.javaName, params);
                } catch (NoSuchMethodException ex) {
                    method = null;

                    Consequence.logger.warn("Ran into an issue while building adapters from " + parent.filename);
                    Consequence.logger.warn("The method " + methodInfo.javaName + " and parameters " + Arrays.toString(methodInfo.paramaters) + " do not exist within " + parent.clazz + ". It will be ignored");
                }

                if (method != null) {
                    luaToJavaMap.put(methodInfo.luaName, methodInfo.javaName + "()");
                    methods.put(methodInfo.javaName + "()", method);
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
}
