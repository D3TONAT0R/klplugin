package com.d3t.klplugin.anticheat;

import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.d3t.klplugin.KLPlugin;

public class AntiCheatHandler {

	Server server;
	
	private String[] protectedWorldNames;
	
	private static final String broadcastTitleLine = "§c§l[King's Landing AntiCheat]";
	
	public AntiCheatHandler() {
		server = KLPlugin.INSTANCE.getServer();
		protectedWorldNames = new String[] {
			"glarus_winter",
			"Survival"
		};
	}
	
	public void update() {
		for(Player p : server.getOnlinePlayers()) {
			if(!bypassAnticheat(p) && isWorldAnticheatEnabled(p.getWorld())) {
				if(p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) {
					p.setGameMode(GameMode.SURVIVAL);
					punishPlayer(p, 2);
					annouceCheating(String.format("%s hat versucht in creative / spectator modus zu wechseln!", p.getName()));
				}
				if(p.isOp()) {
					p.setOp(false);
				}
			}
		}
	}
	
	public void onCommand(PlayerCommandPreprocessEvent event, String cmd) {
		if(cmd.contains("dodaylightcycle")) {
			if(!event.getPlayer().hasPermission("klplugin.changedaylightgamerule")) {
				event.getPlayer().sendMessage("§cNo permission!");
				event.setCancelled(true);
			}
		}
		if(bypassAnticheat(event.getPlayer())) return;
		if(isWorldAnticheatEnabled(event.getPlayer().getWorld())) {
			if(cmd.startsWith("/give")) {
				punishPlayer(event.getPlayer(), 1);
				annouceCheating(String.format("%s hat versucht den /give command auszuführen!", event.getPlayer().getName()));
				event.setCancelled(true);
			}
			if(cmd.startsWith("/kill")) {
				punishPlayer(event.getPlayer(), 2);
				annouceCheating(String.format("%s hat versucht den /kill command auszuführen!", event.getPlayer().getName()));
				event.setCancelled(true);
			}
			if(cmd.startsWith("/effect")) {
				punishPlayer(event.getPlayer(), 0);
				annouceCheating(String.format("%s hat versucht den /effect command auszuführen!", event.getPlayer().getName()));
				event.setCancelled(true);
			}
			if(cmd.startsWith("/op")) {
				punishPlayer(event.getPlayer(), 0);
				annouceCheating(String.format("%s hat versucht sich zum operator zu machen!", event.getPlayer().getName()));
				event.setCancelled(true);
			}
		}
	}
	
	public void onCommandServer(ServerCommandEvent event) {
		CommandSender sender = event.getSender();
		String s = event.getCommand().toLowerCase().replace("/", "");
		if(isConsole(sender)) {
			if(s.startsWith("gamerule") && s.contains("dodaylightcycle")) event.setCancelled(true);
		} else {
			if(s.contains("gamerule") && s.contains("dodaylightcycle")) {
				String passkey = "p25565";
				if(sender instanceof BlockCommandSender) {
					Location loc = ((BlockCommandSender)sender).getBlock().getLocation();
					passkey = "p"+((loc.getBlockX()*loc.getBlockZ()+loc.getBlockY()) % 25565);
				}
				if(!s.contains(passkey)) {
					event.setCancelled(true);
					sender.sendMessage("Execution denied!");
				} else {
					server.getWorld("Kings_Landing").setGameRule(GameRule.DO_DAYLIGHT_CYCLE, s.split(" ")[2].toLowerCase().contains("true"));
					sender.sendMessage("Execution successfull!");
				}
			}
		}
		if (s.contains("time") && !s.contains("ptime")) {
			if (sender instanceof BlockCommandSender) {
				Location loc = ((BlockCommandSender) sender).getBlock().getLocation();
				World w = ((BlockCommandSender) sender).getBlock().getWorld();
				server.getLogger().info(String.format("CommandBlock in world %s @ %s, %s, %s executed command '%s'", w.getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), s));
			} else if (sender instanceof CommandMinecart) {
				Location loc = ((CommandMinecart) sender).getLocation();
				World w = ((CommandMinecart) sender).getWorld();
				server.getLogger().info(String.format("CommandMinecart in world %s @ %s, %s, %s executed command '%s'", w.getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), s));
			} else if (isConsole(sender)) {
				server.getLogger().info(String.format("Console executed command '%s'", s));
			}
		}
	}

	private boolean isConsole(CommandSender s) {
		return ((s instanceof ConsoleCommandSender) || (s instanceof RemoteConsoleCommandSender)
				|| (s instanceof ProxiedCommandSender));
	}
	
	private boolean bypassAnticheat(Player p) {
		boolean b = p.hasPermission("klplugin.bypassanticheat");
		return b;
	}
	
	private void annouceCheating(String s) {
		server.broadcastMessage(broadcastTitleLine);
		server.broadcastMessage("§c"+s);
	}
	
	private void punishPlayer(Player p, int punishmentLevel) {
		if(punishmentLevel <= 0) return;
		p.sendMessage("§oAntiCheat Strafe aktiviert!");
		if(punishmentLevel == 1) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 0), true);
		} else if(punishmentLevel == 2) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1), true);
		} else if(punishmentLevel == 3) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400, 2), true);
		} else if(punishmentLevel >= 4) {
			p.damage(1000);
		}
	}
	
	//Also includes nether and the end
	public boolean isWorldAnticheatEnabled(World w) {
		String n = w.getName();
		for(String s : protectedWorldNames) {
			if(n.equalsIgnoreCase(s) || n.equalsIgnoreCase(s+"_nether") || n.equalsIgnoreCase(s+"_the_end")) {
				return true;
			}
		}
		return false;
	}
}
