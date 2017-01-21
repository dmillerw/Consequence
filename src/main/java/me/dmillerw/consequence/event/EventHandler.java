package me.dmillerw.consequence.event;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Map;

/**
 * @author dmillerw
 */
public class EventHandler {

    private static Map<Class<? extends Event>, String> supportedEvents = Maps.newHashMap();
    static {
        supportedEvents.put(PlayerInteractEvent.RightClickBlock.class, "player.interact.block.right_click");
        supportedEvents.put(PlayerInteractEvent.LeftClickBlock.class, "player.interact.block.left_click");
        supportedEvents.put(PlayerInteractEvent.RightClickEmpty.class, "player.interact.empty.right_click");
        supportedEvents.put(PlayerInteractEvent.LeftClickEmpty.class, "player.interact.empty.left_click");
    }

    public static String getEventTag(Class<? extends Event> clazz) {
        return supportedEvents.get(clazz);
    }

    public static void fireEvent(String eventTag, JsonObject data) {
//        for (Script script : ScriptRegistry.getScriptsForEvent(eventTag)) {
//            if (script.canScriptExecute(data)) {
//                System.out.println(eventTag + " script ran successfully");
//            }
//        }
    }
}
