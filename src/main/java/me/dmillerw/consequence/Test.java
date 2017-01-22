package me.dmillerw.consequence;

import com.google.gson.GsonBuilder;
import me.dmillerw.consequence.lua.javatolua.adapter.Adapter;

/**
 * @author dmillerw
 */
public class Test {

    private static String json = "{\n" +
            "  \"class\": \"net.minecraftforge.fml.common.registry.IForgeRegistryEntry\",\n" +
            "  \"methods\": [\n" +
            "    {\n" +
            "      \"name\": \"get_registry_name\",\n" +
            "      \"parameters\": []\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static void main(String[] args) {
        Adapter.Data data = new GsonBuilder().create().fromJson(json, Adapter.Data.class);
        System.out.println(data);
    }
}
