package com.d3t.klplugin;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class OperatorHandler {

	public static final Material[] tempOpItems = new Material[] {
		Material.COMMAND_BLOCK,
		Material.CHAIN_COMMAND_BLOCK,
		Material.REPEATING_COMMAND_BLOCK,
		Material.DEBUG_STICK,
		Material.STRUCTURE_BLOCK,
		Material.JIGSAW
	};
	
	public static BossBar opDisplayBar;
	
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
			temporaryOps.add(p);
		} else {
			temporaryOps.remove(p);
		}
		setDisplayOpGui(p, set);
	}
	
	private void setDisplayOpGui(Player p, boolean display) {
		if(opDisplayBar == null) {
			//Initialize a new bossbar
			opDisplayBar = Bukkit.getServer().createBossBar("§cTemporärer OP", BarColor.RED, BarStyle.SOLID, new BarFlag[0]);
		}
		opDisplayBar.setVisible(true);
		if(display) {
			opDisplayBar.addPlayer(p);
		} else {
			opDisplayBar.removePlayer(p);
		}
	}
	
	private boolean isOperatorItem(Material mat) {
		for(Material m : tempOpItems) {
			if(m == mat) return true;
		}
		return false;
	}
}
