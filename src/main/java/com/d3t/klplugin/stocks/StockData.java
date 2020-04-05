package com.d3t.klplugin.stocks;

public class StockData {
	public int amount;
	public String symbol;
	public double worthAtPurchase;
	
	public StockData(String sym, int a, double w) {
		symbol = sym;
		amount = a;
		worthAtPurchase = w;
	}
	
	public String getSaveString() {
		return symbol+","+amount+","+worthAtPurchase;
	}
	
	public static StockData createFromSaveString(String s) {
		String[] split = s.split(",");
		return new StockData(split[0], Integer.parseInt(split[1]), Double.parseDouble(split[2]));
	}
}
