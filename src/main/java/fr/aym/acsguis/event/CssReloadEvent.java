package fr.aym.acsguis.event;

import fr.aym.acsguis.utils.CssReloadOrigin;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired on css reload on MinecraftForge event bus
 */
public class CssReloadEvent extends Event
{
    protected CssReloadOrigin reloadOrigin;

    public CssReloadEvent(CssReloadOrigin reloadOrigin) {
        this.reloadOrigin = reloadOrigin;
    }

    public CssReloadOrigin getReloadOrigin() {
        return reloadOrigin;
    }

    @Cancelable
    public static class Pre extends CssReloadEvent
    {
        public Pre(CssReloadOrigin reloadOrigin) {
            super(reloadOrigin);
        }

        public void setReloadOrigin(CssReloadOrigin reloadOrigin) {
            this.reloadOrigin = reloadOrigin;
        }
    }

    public static class Post extends CssReloadEvent
    {
        public Post(CssReloadOrigin reloadOrigin) {
            super(reloadOrigin);
        }
    }
}
