package me.dmillerw.consequence.lua.transform.impl;

import me.dmillerw.consequence.lua.transform.TransformToJava;
import me.dmillerw.consequence.lua.transform.TransformToLua;
import net.minecraft.util.math.BlockPos;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * @author dmillerw
 */
public class ImplBlockPos {

    public static class ToJava implements TransformToJava<BlockPos> {

        @Override
        public BlockPos transform(LuaValue value) {
            if (value.istable()) {
                LuaValue x = value.get("x");
                LuaValue y = value.get("y");
                LuaValue z = value.get("z");

                if (x != null && y != null && z != null) {
                    return new BlockPos(x.todouble(), y.todouble(), z.todouble());
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public static class ToLua implements TransformToLua<BlockPos> {

        @Override
        public LuaValue transform(BlockPos value) {
            LuaTable table = new LuaTable();
            table.set("x", value.getX());
            table.set("y", value.getY());
            table.set("z", value.getZ());
            return table;
        }
    }
}
