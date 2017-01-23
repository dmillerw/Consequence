package me.dmillerw.consequence.lua.library.util;

import me.dmillerw.consequence.lua.javatolua.JavaToLua;
import me.dmillerw.consequence.lua.library.Library;
import me.dmillerw.consequence.lua.transform.TransformerRegistry;
import net.minecraft.item.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

/**
 * @author dmillerw
 */
public class ItemStackLib extends Library {

    @Override
    public String name() {
        return "itemstack";
    }

    @Override
    public void register(LuaValue library) {
        library.set("new", new Construct());
    }

    private static class Construct extends ThreeArgFunction {

        @Override
        public LuaValue call(LuaValue item, LuaValue damage, LuaValue stackSize) {
            LuaTable table = new LuaTable();

            table.set("item", item);
            table.set("damage", damage);
            table.set("stack_size", stackSize);

            return JavaToLua.convert(TransformerRegistry.transform(ItemStack.class, table));
        }
    }
}
