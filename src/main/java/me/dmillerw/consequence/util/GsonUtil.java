package me.dmillerw.consequence.util;

import com.google.gson.*;
import me.dmillerw.consequence.lua.javatolua.adapter.Adapter;

import java.lang.reflect.Type;

/**
 * @author dmillerw
 */
public class GsonUtil {

    private static Gson gson;
    public static Gson gson() {
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();

            builder.registerTypeAdapter(Adapter.Name.class, new JsonDeserializer<Adapter.Name>() {

                @Override
                public Adapter.Name deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    String[] array;
                    if (json.isJsonArray()) {
                        JsonArray jarray = json.getAsJsonArray();
                        array = new String[jarray.size()];
                        for (int i=0; i<array.length; i++) array[i] = jarray.get(i).getAsString();
                    } else {
                        array = new String[] { json.getAsString() };
                    }

                    Adapter.Name name = new Adapter.Name();
                    name.name = array;

                    return name;
                }
            });

            gson = builder.create();
        }
        return gson;
    }
}
