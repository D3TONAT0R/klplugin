package com.d3t.klplugin.advancements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.d3t.klplugin.KLPlugin;

public class AdvancementEventListener implements Listener {

	AdvancementHandler adv;
	
	public AdvancementEventListener() {
		adv = KLPlugin.advancements;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		KLPlugin.advancements.addPlayer(event.getPlayer());
		KLPlugin.advancements.load(event.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		KLPlugin.advancements.removePlayer(event.getPlayer());
		KLPlugin.advancements.save(event.getPlayer());
	}
	
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event) {
		adv.grantAdvancement(event.getPlayer(), adv.firstBlockSet);
	}
}
