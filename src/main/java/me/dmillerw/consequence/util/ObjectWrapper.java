package me.dmillerw.consequence.util;

import com.google.gson.JsonObject;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameData;

/**
 * @author dmillerw
 */
public class ObjectWrapper {

    public static final String KEY_ITEMSTACK_ITEM = "item";
    public static final String KEY_ITEMSTACK_DAMAGE = "damage";
    public static final String KEY_ITEMSTACK_STACK_SIZE = "stack_size";

    public static JsonObject wrapBlock(IBlockState blockState) {
        return ObjectBuilder
                .with("name", GameData.getBlockRegistry().getNameForObject(blockState.getBlock()).toString())
                .get();
    }

    public static JsonObject wrapItemStack(ItemStack itemStack) {
        return ObjectBuilder
                .with(KEY_ITEMSTACK_ITEM, GameData.getItemRegistry().getNameForObject(itemStack.getItem()).toString())
                .and(KEY_ITEMSTACK_DAMAGE, itemStack.getItemDamage())
                .and(KEY_ITEMSTACK_STACK_SIZE, itemStack.getCount())
                .get();
    }
}
