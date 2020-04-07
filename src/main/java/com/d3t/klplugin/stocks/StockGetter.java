package com.d3t.klplugin.stocks;

import org.bukkit.command.CommandSender;
import org.patriques.AlphaVantageConnector;
import org.patriques.TimeSeries;
import org.patriques.input.timeseries.Interval;
import org.patriques.input.timeseries.OutputSize;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.timeseries.Daily;
import org.patriques.output.timeseries.IntraDay;

public abstract class StockGetter implements Runnable {

	public static final String APIKey = "3CZCOG1UBINGMR38";
	public static double stockValueToCurrencyMultiplier = 10;
	
	public Thread stockThread;
	
	public String companySymbol;
	public CommandSender sender;
	
	public double currentClose;
	public double yesterdayClose;

	public StockGetter(String companySymbol, CommandSender sender) {
		this.companySymbol = companySymbol.toUpperCase();
		this.sender = sender;
		if (stockThread == null) {
			stockThread = new Thread(this, "klplugin_stocks");
			stockThread.start();
		}
	}

	public void run() {
		try {
			if(companySymbol.equalsIgnoreCase("TEST")) {
				currentClose = 100;
				yesterdayClose = 100;
			} else {
				if(!companySymbol.endsWith(".SW")) companySymbol += ".SW";
				AlphaVantageConnector connector = new AlphaVantageConnector(APIKey, 5000);
				TimeSeries stockTimeSeries = new TimeSeries(connector);
				IntraDay intraDay = stockTimeSeries.intraDay(companySymbol, Interval.FIFTEEN_MIN, OutputSize.COMPACT);
				Daily daily = stockTimeSeries.daily(companySymbol, OutputSize.COMPACT);
				currentClose = intraDay.getStockData().get(0).getClose()*stockValueToCurrencyMultiplier;
				yesterdayClose = daily.getStockData().get(1).getClose()*stockValueToCurrencyMultiplier;
			}
			func();
		} catch (AlphaVantageException e) {
			sender.sendMessage("§cError: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	protected abstract void func();
}
