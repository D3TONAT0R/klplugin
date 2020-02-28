package com.d3t.klplugin;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

public class OperatorHandler {

	public static final Material[] tempOpItems = new Material[] {
		Material.COMMAND_BLOCK,
		Material.CHAIN_COMMAND_BLOCK,
		Material.REPEATING_COMMAND_BLOCK,
		Material.DEBUG_STICK,
		Material.STRUCTURE_BLOCK,
		Material.JIGSAW
	};
	
	private ArrayList<Player> temporaryOps = new ArrayList<Player>();
	
	public void update() {
		for(Player p : KLPlugin.INSTANCE.getServer().getOnlinePlayers()) {
			boolean b = isOperatorItem(p.getInventory().getItemInMainHand().getType()) && p.hasPermission("klplugin.temporaryop");
			boolean permanentOp = p.isOp() && !temporaryOps.contains(p);
			if(permanentOp) {
				continue;
			}
			if(b != p.isOp()) {
				setTemporaryOperatorStatus(p, b);
			}
		}
	}
	
	public boolean isTemporaryOperator(Player p) {
		return temporaryOps.contains(p);
	}
	
	private void setTemporaryOperatorStatus(Player p, boolean set) {
		p.setOp(set);
		if(set) {
			ActionBarAPI.sendActionBar(p, "§bTemporärer Operator zugewiesen", 20);
			temporaryOps.add(p);
		} else {
			ActionBarAPI.sendActionBar(p, "§cTemporärer Operator entfernt", 20);
			temporaryOps.remove(p);
		}
	}
	
	private boolean isOperatorItem(Material mat) {
		for(Material m : tempOpItems) {
			if(m == mat) return true;
		}
		return false;
	}
}
