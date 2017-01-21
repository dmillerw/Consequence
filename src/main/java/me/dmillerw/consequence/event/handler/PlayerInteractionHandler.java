package me.dmillerw.consequence.event.handler;

import me.dmillerw.consequence.script.ScriptRegistry;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author dmillerw
 */
public class PlayerInteractionHandler {

    @SubscribeEvent
    public static void playerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isRemote) {
            ScriptRegistry.fireEvent("player.interact", PlayerInteractEvent.RightClickBlock.class, event);
        }
    }
}
