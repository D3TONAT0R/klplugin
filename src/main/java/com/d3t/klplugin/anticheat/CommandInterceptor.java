package com.d3t.klplugin.anticheat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import com.d3t.klplugin.KLPlugin;

public class CommandInterceptor implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandServer(ServerCommandEvent event) {
		KLPlugin.anticheat.onCommandServer(event);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		String cmd = event.getMessage().toLowerCase();
		if(KLPlugin.tempOPs.isTemporaryOperator(event.getPlayer())) {
			event.getPlayer().sendMessage("§cNo commands allowed in temporary operator mode!");
			event.setCancelled(true);
		}
		if(cmd.startsWith("/scoreboard") && cmd.contains("immo-") && (cmd.contains("set") || cmd.contains("add"))) {
			if(!event.getPlayer().hasPermission("klplugin.editimmoscores")) {
				event.getPlayer().sendMessage("§cYou need permission to edit Immo-Scoreboards");
				event.setCancelled(true);
			}
		}
		KLPlugin.anticheat.onCommand(event, cmd);
	}

}
