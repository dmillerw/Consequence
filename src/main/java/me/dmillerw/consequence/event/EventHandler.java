package me.dmillerw.consequence.event;

import me.dmillerw.consequence.script.ScriptRegistry;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * @author dmillerw
 */
public class EventHandler {

    public static final String EVENT_ITEM_TOOLTIP = "event.player.item.tooltip";

    @SubscribeEvent
    public static void eventItemTooltip(ItemTooltipEvent event) {
        ScriptRegistry.fireEvent(EVENT_ITEM_TOOLTIP, ItemTooltipEvent.class, event);
    }

    /* TICK */
    public static final String EVENT_TICK_SERVER = "event.tick.server";
    public static final String EVENT_TICK_CLIENT = "event.tick.client";
    public static final String EVENT_TICK_WORLD = "event.tick.world";
    public static final String EVENT_TICK_PLAYER = "event.tick.player";
    public static final String EVENT_TICK_RENDER = "event.tick.render";

    @SubscribeEvent
    public static void eventTickServer(TickEvent.ServerTickEvent event) {
        ScriptRegistry.fireEvent(EVENT_TICK_SERVER, TickEvent.ServerTickEvent.class, event);
    }

    @SubscribeEvent
    public static void eventTickClient(TickEvent.ClientTickEvent event) {
        ScriptRegistry.fireEvent(EVENT_TICK_CLIENT, TickEvent.ClientTickEvent.class, event);
    }

    @SubscribeEvent
    public static void eventTickWorld(TickEvent.WorldTickEvent event) {
        ScriptRegistry.fireEvent(EVENT_TICK_WORLD, TickEvent.WorldTickEvent.class, event);
    }

    @SubscribeEvent
    public static void eventTickPlayer(TickEvent.PlayerTickEvent event) {
        ScriptRegistry.fireEvent(EVENT_TICK_PLAYER, TickEvent.PlayerTickEvent.class, event);
    }

    @SubscribeEvent
    public static void eventTickRender(TickEvent.RenderTickEvent event) {
        ScriptRegistry.fireEvent(EVENT_TICK_RENDER, TickEvent.RenderTickEvent.class, event);

    }
}
