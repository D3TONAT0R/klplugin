package com.d3t.klplugin.stocks;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.d3t.klplugin.FileUtil;
import com.d3t.klplugin.KLPlugin;

public class StockMarketHandler {

	public static HashMap<OfflinePlayer, ArrayList<StockData>> purchasedStocks = new HashMap<OfflinePlayer, ArrayList<StockData>>();

	public static void requestStockInfo(String symbol, CommandSender sender) {
		new StockInfoJob(symbol, sender);
	}

	public static StockData[] getStocksForPlayer(OfflinePlayer p) {
		Player onlinePlayer = KLPlugin.toOnlinePlayer(p);
		if (!purchasedStocks.containsKey(p)) {
			if (onlinePlayer != null) {
				onlinePlayer.sendMessage("§cDu hast noch nie mit Aktien gehandelt und hast deshalb auch keine.");
			}
			return null;
		} else {
			ArrayList<StockData> stocklist = purchasedStocks.get(p);
			StockData[] stocks = new StockData[stocklist.size()];
			stocklist.toArray(stocks);
			return stocks;
		}
	}

	public static void buyStocks(Player p, String symbol, int amount) {
		if (amount > 0)
			new StockTradeJob(symbol, p, amount, true);
	}

	public static void registerPurchase(OfflinePlayer p, StockData d) {
		getPlayer(p).add(d);
		System.out.println(String.format("%s has purchased %s stocks from %s, each worth %s", p.getName(), d.amount,
				d.symbol, d.worthAtPurchase));
		save();
	}

	public static void sellStocks(Player p, int index) {
		if (index > 0) {
			StockData d = getStockDataAtIndex(p, index-1);
			new StockTradeJob(d.symbol, p, index-1, false);
		}
	}

	public static boolean registerSale(OfflinePlayer p, StockData d, double worthPerStock) {
		if (getPlayer(p).remove(d)) {
			System.out.println(String.format("%s has sold %s stocks from %s, each worth %s (was %s)", p.getName(),
					d.amount, d.symbol, worthPerStock, d.worthAtPurchase));
			save();
			return true;
		} else {
			System.out.println("Failed to sell stock data!");
			return false;
		}
	}

	private static ArrayList<StockData> getPlayer(OfflinePlayer p) {
		if (!purchasedStocks.containsKey(p)) {
			purchasedStocks.put(p, new ArrayList<StockData>());
		}
		return purchasedStocks.get(p);
	}

	public static StockData getStockDataAtIndex(OfflinePlayer p, int index) {
		StockData[] d = getStocksForPlayer(p);
		if (d != null && index < d.length) {
			return d[index];
		} else {
			return null;
		}
	}

	public static void save() {
		try {
			FileUtil f = new FileUtil();
			for(OfflinePlayer p : purchasedStocks.keySet()) {
				ArrayList<String> list = new ArrayList<String>();
				for(StockData d : purchasedStocks.get(p)) list.add(d.getSaveString());
				f.SetArrayList(p.getName(), list);
			}
			f.Save("", "stocks.txt");
		} catch(Exception e) {
			System.out.println("failed to save stocks list!");
			e.printStackTrace();
		}
	}

	public static void load() {
		try {
			purchasedStocks.clear();
			File file = new File(KLPlugin.getDataFolderPath(), "stocks.txt");
			FileUtil f = FileUtil.createFromFile(file);
			for (String s : f.content.keySet()) {
				ArrayList<StockData> list = new ArrayList<StockData>();
				for (String cont : f.GetArray(s))
					list.add(StockData.createFromSaveString(cont));
				purchasedStocks.put(KLPlugin.getOfflinePlayer(s), list);
			}
		} catch(Exception e) {
			System.out.println("failed to save stocks list!");
			e.printStackTrace();
		}
	}
}
