package me.dmillerw.consequence.lua.javatolua.adapter;

import com.google.common.base.Throwables;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

    public transient Data data;

    public static class VariableInfo {

        @SerializedName("java_name")
        public String javaName;
        @SerializedName("lua_name")
        public String luaName;
        public String type;
    }

    public static class MethodInfo {

        @SerializedName("java_name")
        public String javaName;
        @SerializedName("lua_name")
        public String luaName;
        public String[] paramaters = new String[0];
        @SerializedName("return_type")
        public String returnType;
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
                e.printStackTrace();
            }

            this.variables = Maps.newHashMap();
            this.methods = Maps.newHashMap();
            this.luaToJavaMap = HashBiMap.create();

            try {
                initializeReflection(parent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void initializeReflection(AdapterInfo parent) {
            try {
                for (AdapterInfo.VariableInfo variable : parent.variables) {
                    luaToJavaMap.put(variable.luaName, variable.javaName);

                    variables.put(variable.javaName, clazz.getField(variable.javaName));
                }

                for (AdapterInfo.MethodInfo method : parent.methods) {
                    luaToJavaMap.put(method.luaName, method.javaName + "()");

                    Class[] params = new Class[method.paramaters.length];
//                    Class ret = getClassFromString(method.returnType);

                    for (int i=0; i<params.length; i++) {
                        params[i] = getClassFromString(method.paramaters[i]);
                    }

                    methods.put(method.javaName + "()", clazz.getMethod(method.javaName, params));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
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
