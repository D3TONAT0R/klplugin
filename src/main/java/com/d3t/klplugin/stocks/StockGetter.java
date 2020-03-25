package com.d3t.klplugin.stocks;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.patriques.AlphaVantageConnector;
import org.patriques.TimeSeries;
import org.patriques.input.timeseries.Interval;
import org.patriques.input.timeseries.OutputSize;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.timeseries.IntraDay;
import org.patriques.output.timeseries.data.StockData;

public class StockGetter implements Runnable {

	public static final String APIKey = "3CZCOG1UBINGMR38";
	public static double stockValueToCurrencyMultiplier = 10;
	
	public Thread stockThread;
	
	public String companySymbol;
	public CommandSender sender;
	public boolean writeToScores;

	public StockGetter(String companySymbol, CommandSender sender, boolean writeToScores) {
		this.companySymbol = companySymbol;
		this.sender = sender;
		this.writeToScores = writeToScores;
		if (stockThread == null) {
			stockThread = new Thread(this, "klplugin_stocks");
			stockThread.start();
		}
	}

	public void run() {
		try {
			AlphaVantageConnector connector = new AlphaVantageConnector(APIKey, 5000);
			TimeSeries stockTimeSeries = new TimeSeries(connector);
			IntraDay response = stockTimeSeries.intraDay(companySymbol, Interval.ONE_MIN, OutputSize.COMPACT);

			List<StockData> stockData = response.getStockData();
			for (StockData stock : stockData) {
				sender.sendMessage("date:   " + stock.getDateTime());
				sender.sendMessage("open:   " + stock.getOpen());
				sender.sendMessage("high:   " + stock.getHigh());
				sender.sendMessage("low:	" + stock.getLow());
				sender.sendMessage("close:  " + stock.getClose());
				sender.sendMessage("volume: " + stock.getVolume());
			}
		} catch (Exception e) {
			sender.sendMessage("Error!");
			System.out.println("something went wrong");
			e.printStackTrace();
		}
	}
}
