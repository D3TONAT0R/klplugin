package com.d3t.klplugin.anticheat;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.entity.Player;

import com.d3t.klplugin.FileUtil;
import com.d3t.klplugin.KLPlugin;

public class EconomyLogger {

	public HashMap<Player, BalanceInfo> balances;

	public EconomyLogger() {
		balances = new HashMap<Player, BalanceInfo>();

		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			registerPlayer(p);
		}
		System.out.println("EconomyLogger enabled");
	}

	public void registerPlayer(Player p) {
		balances.put(p, new BalanceInfo(KLPlugin.econ.getBalance(p)));
		writeToLog("JOIN", p, 0, "connect / log start");
	}

	public void unregisterPlayer(Player p) {
		BalanceInfo rm = balances.remove(p);
		int sessionLength = (int) (KLPlugin.getTimeTicks() - rm.joinTimestamp);
		double gain = getBalance(p) - rm.balanceOnJoin;
		long gainPerDay = Math.round(gain / sessionLength * 24000);
		writeToLog("QUIT", p, gain, "disconnect / log end (avg. " + gainPerDay + " $/day)");
	}

	public void onNewDay() {
		for (Player p : balances.keySet()) {
			double balance = getBalance(p);
			double lastDayBalance = round(balances.get(p).balanceDaily);
			double dailyGain = balance - lastDayBalance;
			writeToLog("DAILY", p, dailyGain, "Daily gain check");
			balances.get(p).balanceDaily = balance;
		}
	}

	public void onPayImmoPoints(Player p, BlockCommandSender sender, int amount) {
		Location loc = sender.getBlock().getLocation();
		String cmdBlockLoc = String.format("@ %s,%s,%s", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		recordLoggedAction(p, "immopayout " + cmdBlockLoc + " (+" + amount + "$)");
	}

	public void recordLoggedAction(Player p, String actionDesc) {
		double lastLoggedBalance = balances.get(p).balanceBetweenLogs;
		double balance = getBalance(p);
		double gain = round(balance - lastLoggedBalance);
		writeToLog("LOG", p, gain, actionDesc);
		balances.get(p).balanceBetweenLogs = balance;
	}

	public void writeToLog(String logtype, Player p, double gain, String desc) {
		long ticks = Bukkit.getServer().getWorld("Kings_Landing").getFullTime();
		int day = (int) Math.floor(ticks / 24000);
		int dayTicks = (int) (ticks % 24000);
		if (p != null) {
			writeString(String.format("%s\t%s\t%s\t%s\t%s\t%s\t[%s]", day, dayTicks, logtype, p.getName(),
					getBalance(p), gain, desc));
		}
		// TODO: log non-player events
	}

	private void writeString(String s) {
		//System.out.println("Economy log: " + s);
		File path = new File(KLPlugin.getDataFolderPath(), "econlog.csv");
		try {
			if (!path.exists())
				path.createNewFile();
			FileWriter writer = new FileWriter(path, true);
			writer.write(s + "\n");
			writer.close();

		} catch (Exception e) {
			System.out.println("Failed to write to file!");
			e.printStackTrace();
		}
		FileUtil.createFromFile(path);
	}

	private double getBalance(Player p) {
		return round(KLPlugin.econ.getBalance(p));
	}

	private double round(double d) {
		return Math.round(d * 100D) / 100D;
	}
}
