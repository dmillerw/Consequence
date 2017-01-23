-- Main entry point, which every script MUST have
function main(registry)
    -- Registering the 'render_event' function to be called everytime the 'event.tick.render' event is fired
    registry.register_event_handler("event.tick.render", render_event)
end

function render_event(event)
    -- If the client hasn't loaded a world yet, there's no coords to display, so we duck out early
    if client.get_world() == nil then
        return
    end

    local font_renderer = client.get_font_renderer()
    local player = client.get_player()

    font_renderer:draw_string("X: " .. player.x, 5,  5, 0xFFFFFF)
    font_renderer:draw_string("Y: " .. player.y, 5, 15, 0xFFFFFF)
    font_renderer:draw_string("Z: " .. player.z, 5, 25, 0xFFFFFF)
end