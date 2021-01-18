package com.d3t.klplugin.anticheat;

import java.io.File;
import java.util.ArrayList;
import com.d3t.klplugin.FileUtil;
import com.d3t.klplugin.KLPlugin;

public class AntiCheatConfig {

	public static ArrayList<String> protectedWorlds = new ArrayList<String>();

	public static int aggressionLevel = 0;

	public static void LoadConfig() {
		File filePath = new File(KLPlugin.INSTANCE.getDataFolder() + "/anticheat.cfg");
		if (filePath.exists()) {
			FileUtil file = FileUtil.createFromFile(filePath);
		} else {
			SaveDefaultConfig();
		}
	}

	public static void SaveDefaultConfig() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("WORLD_NAME_HERE");
		FileUtil file = new FileUtil();
		file.SetArrayList("worldnames", list, "List of worlds where AntiCheat is active");
		file.SetValue("aggressionlevel", 0, "The AntiCheat System's aggression level:\n0: silent/harmless\n1: notify violator\n2: notify everyone\n3: light punishments (effects)\n4: death sentence");
	}
}
