package com.d3t.klplugin.anticheat;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

import com.d3t.klplugin.FileUtil;
import com.d3t.klplugin.KLPlugin;

public class AntiCheatConfig {

	public static ArrayList<String> protectedWorlds;

	public static String broadcastTitleLine;
	public static int aggressionLevel;
	public static boolean announceCheating;

	public static void LoadConfig() {
		protectedWorlds = new ArrayList<String>();
		aggressionLevel = 1;
		File filePath = new File(KLPlugin.INSTANCE.getDataFolder() + "/anticheat.cfg");
		if (filePath.exists()) {
			FileUtil file = FileUtil.createFromFile(filePath);
			try {
				announceCheating = file.GetBool("announcecheating");
				broadcastTitleLine = file.GetString("broadcasttitle");
				for(String s : file.GetArray("worldnames")) {
					protectedWorlds.add(s);
				}
				aggressionLevel = file.GetInt("aggressionlevel");
				KLPlugin.log.log(Level.INFO, "Config loaded successfully");
			}
			catch(Exception e) {
				KLPlugin.log.log(Level.SEVERE, "Failed to read AntiCheat config");
				e.printStackTrace();
			}
		} else {
			KLPlugin.log.log(Level.WARNING, "Config not found! Creating a new one...");
			SaveDefaultConfig();
		}
	}

	public static void SaveDefaultConfig() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("WORLD_NAME_HERE");
		FileUtil file = new FileUtil();
		file.SetValue("announcecheating", false, "Announce cheating incident to all online players");
		file.SetValue("broadcasttitle", "§c§l[King's Landing AntiCheat]", "Title line used in the cheating announcement");
		file.SetArrayList("worldnames", list, "List of worlds where AntiCheat is active");
		file.SetValue("aggressionlevel", 1, "The AntiCheat System's aggression level:\n0: silent/harmless\n1: notify violator\n2: light punishments (effects)\n3: heavy punishments (poison)\n4: death sentence");
		file.Save("", "anticheat.cfg");
	}
	
	public static int getPunishmentIntensity() {
		if(aggressionLevel < 2) {
			return 0;
		} else if(aggressionLevel == 2) {
			return 1;
		} else if(aggressionLevel == 3) {
			return 2;
		} else {
			return 3;
		}
	}
	
	public static boolean canKill() {
		return aggressionLevel >= 4;
	}
}
