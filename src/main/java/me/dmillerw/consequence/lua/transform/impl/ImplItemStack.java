package me.dmillerw.consequence.lua.transform.impl;

import me.dmillerw.consequence.lua.transform.TransformToJava;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.luaj.vm2.LuaValue;

/**
 * @author dmillerw
 */
public class ImplItemStack {

    public static class ToJava implements TransformToJava<ItemStack> {

        @Override
        public ItemStack transform(LuaValue value) {
            if (value.istable()) {
                LuaValue item = value.get("item");
                LuaValue damage = value.get("damage");
                LuaValue stackSize = value.get("stack_size");

                return new ItemStack(Item.getByNameOrId(item.tojstring()), stackSize.toint(), damage.toint());
            } else {
                return ItemStack.EMPTY;
            }
        }
    }
}
