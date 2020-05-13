package com.d3t.klplugin.anticheat;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import com.d3t.klplugin.KLPlugin;

public class CommandInterceptor {
	
	public static void onCommandServer(ServerCommandEvent event) {
		KLPlugin.anticheat.onCommandServer(event);
	}
	
	public static void onCommand(PlayerCommandPreprocessEvent event) {
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
