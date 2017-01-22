package me.dmillerw.consequence.util;

import com.google.common.collect.*;
import me.dmillerw.consequence.Consequence;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dmillerw
 */
public class MappingLookup {

    private static boolean obfuscated;

    public static Map<String, Map<String, String>> fieldSrgToDeobf;
    public static Map<String, Map<String, String>> methodSrgToDeobf;

    public static void initialize() {
        obfuscated = !(Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

        fieldSrgToDeobf = Maps.newHashMap();
        methodSrgToDeobf = Maps.newHashMap();

        FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;

        Map<String, Map<String, String>> fieldNameMaps = null;
        Map<String, Map<String, String>> methodNameMaps = null;

        Map<String, Set<String>> deobfToSrgFields = Maps.newHashMap();
        Map<String, Set<String>> deobfToSrgMethods = Maps.newHashMap();
        Map<String, String> srgToDeobfFields = Maps.newHashMap();
        Map<String, String> srgToDeobfMethods = Maps.newHashMap();

        try {
            Class clazz = FMLDeobfuscatingRemapper.class;

            Field fields = clazz.getDeclaredField("fieldNameMaps");
            fields.setAccessible(true);
            Field methods = clazz.getDeclaredField("methodNameMaps");
            methods.setAccessible(true);

            fieldNameMaps = (Map<String, Map<String, String>>) fields.get(remapper);
            methodNameMaps = (Map<String, Map<String, String>>) methods.get(remapper);

            fields.setAccessible(false);
            methods.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        /* CSV */

        List<String> list = Lists.newArrayList();
        try {
            list = IOUtils.readLines(Consequence.class.getResourceAsStream("/mcp/fields.csv"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        list.forEach((l) -> {
            String[] split = l.split(",");
            srgToDeobfFields.put(split[0], split[1]);

            Set<String> set = deobfToSrgFields.get(split[1]);
            if (set == null) set = Sets.newHashSet();
            set.add(split[0]);
            deobfToSrgFields.put(split[1], set);
        });

        try {
            list = IOUtils.readLines(Consequence.class.getResourceAsStream("/mcp/methods.csv"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        list.forEach((l) -> {
            String[] split = l.split(",");
            srgToDeobfMethods.put(split[0], split[1]);

            Set<String> set = deobfToSrgMethods.get(split[1]);
            if (set == null) set = Sets.newHashSet();
            set.add(split[0]);
            deobfToSrgMethods.put(split[1], set);
        });

        /* END CSV */

        if (fieldNameMaps == null || methodNameMaps == null)
            return;

        Iterator<String> iterator = fieldNameMaps.keySet().iterator();
        while (iterator.hasNext()) {
            String clazz = iterator.next();

            Map<String, String> map = fieldSrgToDeobf.get(clazz);
            if (map == null) map = Maps.newHashMap();

            for (String string : fieldNameMaps.get(clazz).values()) {
                map.put(srgToDeobfFields.get(string), string);
            }

            fieldSrgToDeobf.put(clazz, map);
        }

        iterator = methodNameMaps.keySet().iterator();
        while (iterator.hasNext()) {
            String clazz = iterator.next();

            Map<String, String> map = methodSrgToDeobf.get(clazz);
            if (map == null) map = Maps.newHashMap();

            for (Map.Entry<String, String> entry : methodNameMaps.get(clazz).entrySet()) {
                String obf = entry.getKey();
                String desc = obf.substring(obf.indexOf("("));
                String srg = entry.getValue();

                map.put(srgToDeobfMethods.get(srg) + desc, srg + desc);
            }

            methodSrgToDeobf.put(clazz, map);
        }
    }

    public static String mapClass(String owner) {
        if (!obfuscated)
            return owner;

        return FMLDeobfuscatingRemapper.INSTANCE.unmap(owner);
    }

    public static String mapField(String owner, String name, String desc) {
        if (!obfuscated)
            return name;

        owner = FMLDeobfuscatingRemapper.INSTANCE.unmap(owner);

        String deobf = fieldSrgToDeobf.get(owner).get(name);

        return deobf == null ? name : deobf;
    }

    public static String mapMethod(String owner, String name, String desc) {
        if (!obfuscated)
            return name;

        owner = FMLDeobfuscatingRemapper.INSTANCE.unmap(owner);

        String deobf = methodSrgToDeobf.get(owner).get(name + desc);

        return deobf == null ? name : deobf.substring(0, deobf.indexOf("("));
    }
}
