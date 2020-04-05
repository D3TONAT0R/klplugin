package com.d3t.klplugin.stocks;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.d3t.klplugin.KLPlugin;

import net.milkbowl.vault.economy.Economy;

public class StockTradeJob extends StockGetter {

	public static final float tradeCostPercentage = 0.00f;
	
	static boolean buyStocks;
	static int amountOfStocks;
	static int listIndex;
	
	public StockTradeJob(String companySymbol, CommandSender sender, int amountOrIndex, boolean buy) {
		super(companySymbol, sender);
		buyStocks = buy;
		if(buy) {
			amountOfStocks = amountOrIndex;			
		} else {
			listIndex = amountOrIndex;
		}
	}

	@Override
	protected void func() {
		if(!(sender instanceof OfflinePlayer)) return;
		OfflinePlayer p = (OfflinePlayer)sender;
		Economy econ = KLPlugin.econ;
		if(buyStocks) {
			double cost = currentClose * (1f+tradeCostPercentage) * amountOfStocks;
			if(econ.getBalance(p) >= cost) {
				econ.withdrawPlayer(p, cost);
				StockData d = new StockData(companySymbol, amountOfStocks, currentClose);
				StockMarketHandler.registerPurchase(p, d);
				Player op = KLPlugin.toOnlinePlayer(p);
				if(op != null) {
					op.sendMessage(String.format("§aDu hast %s Aktien von %s gekauft ($ pro Aktie: %s)", d.amount, d.symbol, d.worthAtPurchase));
					op.sendMessage("§eAusgaben: "+cost);
				}
			}
		} else {
			StockData d = StockMarketHandler.getStockDataAtIndex(p, listIndex);
			if(d != null) {
				double amount = d.amount * currentClose;
				if(StockMarketHandler.registerSale(p, d, currentClose)) {
					econ.depositPlayer(p, amount);
					Player op = KLPlugin.toOnlinePlayer(p);
					if(op != null) {
						op.sendMessage(String.format("§aDu hast deine %s Aktien von %s verkauft ($ erhalten pro Aktie: %s)", d.amount, d.symbol, currentClose));
						op.sendMessage("§eGewinn: "+amount);
					}
				}
			}
		}
	}
}
