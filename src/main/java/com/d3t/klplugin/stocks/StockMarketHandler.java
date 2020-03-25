package com.d3t.klplugin.stocks;

import org.bukkit.command.CommandSender;

public class StockMarketHandler {
	
	public static void requestStockInfo(String symbol, CommandSender sender) {
		new StockGetter(symbol, sender, false);
	}
}
