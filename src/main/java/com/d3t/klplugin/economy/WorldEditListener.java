package com.d3t.klplugin.economy;

import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;

public class WorldEditListener {

	@Subscribe(priority = EventHandler.Priority.VERY_EARLY)
	public void onEditSessionEvent(EditSessionEvent event) {
		Actor actor = event.getActor();
		if (actor != null && actor.isPlayer()
				&& event.getWorld().getName().equalsIgnoreCase(EconomyConfiguration.worldName)) {
			event.setExtent(new WorldEditEvents(event.getWorld(), event.getExtent(), (Player) actor));
		}
	}
}
