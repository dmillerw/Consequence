package me.dmillerw.consequence.lua.transform;

import com.google.common.collect.Maps;
import me.dmillerw.consequence.lua.transform.impl.ImplBlockPos;
import me.dmillerw.consequence.lua.transform.impl.ImplItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.luaj.vm2.LuaValue;

import java.util.Map;

/**
 * @author dmillerw
 */
public class TransformerRegistry {

    private static Map<Class<?>, TransformToJava<?>> transformToJavaMap = Maps.newHashMap();
    private static Map<Class<?>, TransformToLua<?>> transformToLuaMap = Maps.newHashMap();

    public static <T> void registerTransformer(Class<T> clazz, TransformToJava<T> toJava, TransformToLua<T> toLua) {
        transformToJavaMap.put(clazz, toJava);
        transformToLuaMap.put(clazz, toLua);
    }

    public static <T> void registerTransformer(Class<T> clazz, TransformToJava<T> transformer) {
        transformToJavaMap.put(clazz, transformer);
    }

    public static <T> void registerTransformer(Class<T> clazz, TransformToLua<T> transformer){
        transformToLuaMap.put(clazz, transformer);
    }

    public static Object transform(Class clazz, LuaValue value) {
        TransformToJava transformer = transformToJavaMap.get(clazz);
        if (transformer == null)
            return null;

        return transformer.transform(value);
    }

    public static LuaValue transform(Object object) {
        TransformToLua transformer = transformToLuaMap.get(object.getClass());
        if (transformer == null)
            return LuaValue.NIL;

        return transformer.transform(object);
    }

    public static void initialize() {
        registerTransformer(BlockPos.class, new ImplBlockPos.ToJava(), new ImplBlockPos.ToLua());
        registerTransformer(ItemStack.class, new ImplItemStack.ToJava());
    }
}
