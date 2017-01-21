package me.dmillerw.consequence.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * @author dmillerw
 */
public class GsonUtil {

    private static Gson gson;
    public static Gson gson() {
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();

            gson = builder.create();
        }
        return gson;
    }

    public static boolean arrayContains(JsonArray array, JsonElement element) {
        for (int i=0; i<array.size(); i++) {
            if (array.get(i).equals(element)) {
                return true;
            }
        }
        return false;
    }
}
