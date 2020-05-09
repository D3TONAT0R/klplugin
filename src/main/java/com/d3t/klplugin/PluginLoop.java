package com.d3t.klplugin;

public class PluginLoop implements Runnable {

	int second;
	int tick;
	int lastDayTick;
	
	boolean onPostEnableDone = false;
	
	@Override
	public void run() {
		onTickUpdate();
		boolean time = KLPlugin.mainWorld != null;
		int dayTick = 0;
		if(time) {
			dayTick = (int)(KLPlugin.mainWorld.getTime() % 24000);
			if(lastDayTick > dayTick) {
				//A new day has started
				KLPlugin.econLogger.onNewDay();
			}
		}
		if(tick % 20 == 0) {
			if(!onPostEnableDone) {
				KLPlugin.INSTANCE.onPostEnable();
				onPostEnableDone = true;
			}
			onSecondUpdate();
			second++;
		}
		tick++;
		if(time) lastDayTick = dayTick;
	}
	
	private void onTickUpdate() {
		KLPlugin.tempOPs.update();
	}
	
	private void onSecondUpdate() {
		if(second % 2 == 0) KLPlugin.tempHandler.onUpdate();
		KLPlugin.anticheat.update();
	}
}
