package com.d3t.klplugin;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.d3t.klplugin.advancements.AdvancementEventListener;
import com.d3t.klplugin.advancements.AdvancementHandler;
import com.d3t.klplugin.anticheat.AntiCheatHandler;
import com.d3t.klplugin.anticheat.CommandInterceptor;
import com.d3t.klplugin.stocks.StockMarketHandler;

import net.milkbowl.vault.economy.Economy;

public final class KLPlugin extends JavaPlugin {

	public List<String> playerList;

	public static KLPlugin INSTANCE;
	public static final Logger log = Logger.getLogger("Minecraft");
	public static Economy econ = null;
	// private static Permission perms = null;
	// private static Chat chat = null;

	public static BukkitTask task;

	public static Commands commandHandler;
	public static TemperatureMechanic tempHandler;
	public static AntiCheatHandler anticheat;
	public static PVPCommands pvp;
	public static OperatorHandler tempOPs;
	public static AdvancementHandler advancements;

	@Override
	public void onEnable() {
		INSTANCE = this;
		log.info("test");
		/*
		 * for (Player player : Bukkit.getServer().getOnlinePlayers()) {
		 * playerList.add(player.getName()); }
		 */
		setupEconomy();
		// this.getServer().getScheduler().sched.runTaskTimer(this, new PluginLoop(), 0,
		// 20);
		// task = new PluginLoop().runTaskTimer(this, 40, 40);
		commandHandler = new Commands(getServer());
		tempHandler = new TemperatureMechanic();
		anticheat = new AntiCheatHandler();
		pvp = new PVPCommands();
		tempOPs = new OperatorHandler();
		advancements = new AdvancementHandler(getServer().getPluginManager());
		getServer().getPluginManager().registerEvents(new AdvancementEventListener(), this);
		getServer().getScheduler().runTaskTimer(this, new PluginLoop(), 20, 1);
		getServer().getPluginManager().registerEvents(new CommandInterceptor(), this);
		StockMarketHandler.load();
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	@Override
	public void onDisable() {
		StockMarketHandler.save();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return commandHandler.onCommand(sender, cmd, label, args);
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		return commandHandler.onTabComplete(sender, cmd, alias, args);
	}

	public static OfflinePlayer getOfflinePlayer(String name) {
		for (OfflinePlayer p : INSTANCE.getServer().getOfflinePlayers()) {
			if (p.getName().equalsIgnoreCase(name))
				return p;
		}
		log.warning(String.format("Player named '%s' not found!", name));
		return null;
	}

	public static Player toOnlinePlayer(OfflinePlayer offPlayer) {
		return INSTANCE.getServer().getPlayer(offPlayer.getUniqueId());
	}
	
	public static File getDataFolderPath() {
		return INSTANCE.getDataFolder();
	}
}
