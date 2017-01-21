package me.dmillerw.consequence.event.handler;

import me.dmillerw.consequence.script.ScriptRegistry;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author dmillerw
 */
public class ItemTooltipHandler {

    @SubscribeEvent
    public void itemTooltipEvent(ItemTooltipEvent event) {
        ScriptRegistry.fireEvent("player.item.tooltip", ItemTooltipEvent.class, event);
    }
}
