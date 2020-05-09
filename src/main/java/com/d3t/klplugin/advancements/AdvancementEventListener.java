package com.d3t.klplugin.advancements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.d3t.klplugin.KLPlugin;

public class AdvancementEventListener {
	
	public static void onJoin(PlayerJoinEvent event) {
		KLPlugin.advancements.addPlayer(event.getPlayer());
		KLPlugin.advancements.load(event.getPlayer());
	}
	
	public static void onQuit(PlayerQuitEvent event) {
		KLPlugin.advancements.removePlayer(event.getPlayer());
		KLPlugin.advancements.save(event.getPlayer());
	}
	
	@EventHandler
	public static void onPlaceBlock(BlockPlaceEvent event) {
		AdvancementHandler adv = KLPlugin.advancements;
		if(adv != null && event != null && event.getPlayer() != null) adv.grantAdvancement(event.getPlayer(), adv.firstBlockSet);
	}
}
