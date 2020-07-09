package com.d3t.klplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;

import com.d3t.klplugin.anticheat.CommandInterceptor;

public class EventListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandServer(ServerCommandEvent event) {
		KLPlugin.anticheat.onCommandServer(event);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		CommandInterceptor.onCommand(event);
		KLPlugin.anticheat.onCommand(event, event.getMessage().toLowerCase());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		KLPlugin.econLogger.registerPlayer(event.getPlayer());
		//AdvancementEventListener.onJoin(event);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		KLPlugin.econLogger.unregisterPlayer(event.getPlayer());
		//AdvancementEventListener.onQuit(event);
	}
	
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event) {
		//AdvancementEventListener.onPlaceBlock(event);
	}
}
