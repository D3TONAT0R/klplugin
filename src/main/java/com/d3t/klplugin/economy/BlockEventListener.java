package com.d3t.klplugin.economy;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.d3t.klplugin.KLPlugin;

public class BlockEventListener implements Listener {
	
	public BlockEventListener() {
		KLPlugin.INSTANCE.getServer().getPluginManager().registerEvents(this, KLPlugin.INSTANCE);
	}
	
	
	@EventHandler
	public void onBlockPlaced(BlockPlaceEvent event) {
		if(event.getPlayer() != null) {
			Block block = event.getBlockPlaced();
			BuildCostHandler.INSTANCE.applyBlockPlacementCost(event.getPlayer(), block.getLocation().toVector(), getID(block.getBlockData()), false);
		}
	}
	
	private String getID(BlockData data) {
		String s = data.getAsString();
		if(s.contains("[")) {
			s = s.substring(0, s.indexOf('['));
		}
		return s;
	}
}
