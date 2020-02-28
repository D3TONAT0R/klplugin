package com.d3t.klplugin;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class PVPCommands {
	
	public final String color = "§3";
	public final String currentClassPointer = "§2 <-";
	public final String[] classNames = new String[] {
			"Infanterie",
			"Airforce",
			"Artillerie"
	};
	public final String[] classes = new String[] {
			color+"0: "+classNames[0],
			color+"1: "+classNames[1],
			color+"2: "+classNames[2]
	};
	
	public int tick;
	
	public boolean selectClass(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player p = (Player)sender;
		if(args.length < 1) {
			Server server = KLPlugin.INSTANCE.getServer();
			int c = getCurrentClass(p);
			sender.sendMessage("§7-------------------------------");
			sender.sendMessage("§7Wähle eine Klasse:");
			server.dispatchCommand(server.getConsoleSender(), "tellraw "+p.getName()+" "+createJSONText(classes[0] + (c == 0 ? currentClassPointer : ""), "/pvpklasse 0"));
			server.dispatchCommand(server.getConsoleSender(), "tellraw "+p.getName()+" "+createJSONText(classes[1] + (c == 1 ? currentClassPointer : ""), "/pvpklasse 1"));
			server.dispatchCommand(server.getConsoleSender(), "tellraw "+p.getName()+" "+createJSONText(classes[2] + (c == 2 ? currentClassPointer : ""), "/pvpklasse 2"));
			sender.sendMessage("§7-------------------------------");
			return true;
		} else {
			int i = Commands.ParseInt(args[0], sender);
			setCurrentClass(p, i);
			return true;
		}
	}

	public void doGameTick() {
		tick++;
	}
	
	public void applyLoadout(CommandSender s, int cl, World w, int x, int y, int z) {
		int i = 0;
		for(Player p : KLPlugin.INSTANCE.getServer().getOnlinePlayers()) {
			if((p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE && hasPVPTag(p)) && getCurrentClass(p) == cl) {
				i++;
				
				Block block = w.getBlockAt(x, y, z);
				if (block.getType() == Material.CHEST) {
					Inventory chest = ((Chest)block.getState()).getBlockInventory();
					copyLoadout(p, chest);
				} else {
					KLPlugin.log.info("No chest was found at the location! "+block.getType().toString());
				}
			}
		}
		if(i == 0) s.sendMessage("No player was changed.");
	}
	
	private void copyLoadout(Player p, Inventory inv) {
		
		for(int i = 0; i < 9; i++) {
			boolean criteriaMet;
			String criteria;
			ItemStack stack = inv.getItem(i);
			if(stack == null || stack.getType() == Material.AIR) {
				criteriaMet = true;
				criteria = "null";
			} else {
				criteria = stack.getItemMeta().getDisplayName();
				criteriaMet = isFeatureActive(criteria);
			}
			if(criteriaMet) {
				stack = inv.getItem(i+9);
				int ticksMod = 1;
				if(stack != null) {
					if(stack.getType() == Material.BARRIER) {
						ticksMod = stack.getAmount();
					} else if(stack.getType() == Material.STRUCTURE_VOID) {
						ticksMod = stack.getAmount()*20;
					}
				}
				if(tick % ticksMod == 0) {
					stack = inv.getItem(i+18);
					if(criteria.equalsIgnoreCase("null")) {
						//Do nothing
					} else if(isAdditiveCriteria(criteria)) {
						if(stack.isSimilar(p.getInventory().getItem(i))) {
							ItemStack s = p.getInventory().getItem(i);
							s.setAmount(Math.min(s.getAmount()+stack.getAmount(), s.getMaxStackSize()));
						} else {
							p.getInventory().setItem(i, stack);
						}
					} else {
						if(stack.isSimilar(p.getInventory().getItem(i))) {
							p.getInventory().getItem(i).setItemMeta(stack.getItemMeta());
						} else {
							p.getInventory().setItem(i, stack);
						}
					}
				}
			}
		}
	}
	
	private boolean isAdditiveCriteria(String crit) {
		if(crit.equalsIgnoreCase("blocks") || crit.equalsIgnoreCase("food")) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean hasPVPTag(Player p) {
		for(String s : p.getScoreboardTags()) {
			if(s.equalsIgnoreCase("pvp")) return true;
		}
		return false;
	}
	
	private boolean isFeatureActive(String featureName) {
		Objective o = KLPlugin.INSTANCE.getServer().getScoreboardManager().getMainScoreboard().getObjective("Int");
		Score s = o.getScore("pvp_"+featureName);
		if(!s.isScoreSet()) {
			s.setScore(0);
		}
		return s.getScore() > 0;
	}
	
	private int getCurrentClass(Player p) {
		Score s = KLPlugin.INSTANCE.getServer().getScoreboardManager().getMainScoreboard().getObjective("pvpKlasse").getScore(p.getName());
		if(s.isScoreSet()) {
			return s.getScore();
		} else {
			s.setScore(0);
			return 0;
		}
	}
	
	private void setCurrentClass(Player p, int newClass) {
		Score s = KLPlugin.INSTANCE.getServer().getScoreboardManager().getMainScoreboard().getObjective("pvpKlasse").getScore(p.getName());
		s.setScore(newClass);
		p.sendMessage("§aDeine neue Klasse: "+classNames[newClass]);
	}
	
	private String createJSONText(String text, String command) {
		return "{\"text\":\""+text+"\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\""+command+"\"}}";
	}
}
