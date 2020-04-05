package com.d3t.klplugin.stocks;

import org.bukkit.command.CommandSender;

public class StockInfoJob extends StockGetter {

	public StockInfoJob(String companySymbol, CommandSender sender) {
		super(companySymbol, sender);
	}

	@Override
	protected void func() {
		sender.sendMessage("Stock market info for "+companySymbol);
		double rate = Math.round((1D - yesterdayClose / currentClose)*10000D)/100D;
		String percent;
		if(rate >= 0) {
			percent = "§a+";
		} else {
			percent = "§c";
		}
		percent += rate;
		sender.sendMessage(currentClose+"$ ("+percent+"%§f)");
	}
}
