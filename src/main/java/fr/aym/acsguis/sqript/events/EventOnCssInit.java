package fr.aym.acsguis.sqript.events;

import fr.nico.sqript.events.ScriptEvent;
import fr.nico.sqript.meta.Event;

@Event(name = "acsguis load",
        description = "Called when ACsGuis is loaded",
        examples = "on acsguis load:",
        patterns = "acsguis load[ed]",
        accessors = {})
public class EventOnCssInit extends ScriptEvent {
}
