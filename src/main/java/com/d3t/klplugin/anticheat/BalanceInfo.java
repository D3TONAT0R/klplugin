package com.d3t.klplugin.anticheat;

import com.d3t.klplugin.KLPlugin;

public class BalanceInfo {

	public double balanceBetweenLogs;
	public double balanceDaily;
	public double balanceOnJoin;
	public long joinTimestamp;
	
	public BalanceInfo(double start) {
		balanceBetweenLogs = start;
		balanceDaily = start;
		balanceOnJoin = start;
		joinTimestamp = KLPlugin.getTimeTicks();
	}
}
