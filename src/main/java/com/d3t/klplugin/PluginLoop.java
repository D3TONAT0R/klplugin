package com.d3t.klplugin;

public class PluginLoop implements Runnable {

	int second;
	int tick;
	
	@Override
	public void run() {
		onTickUpdate();
		if(tick % 20 == 0) {
			onSecondUpdate();
			second++;
		}
		tick++;
	}
	
	private void onTickUpdate() {
		KLPlugin.tempOPs.update();
	}
	
	private void onSecondUpdate() {
		if(second % 2 == 0) KLPlugin.tempHandler.onUpdate();
		KLPlugin.anticheat.update();
	}
}
