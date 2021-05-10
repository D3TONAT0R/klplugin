package com.d3t.klplugin.economy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.d3t.klplugin.FileUtil;
import com.d3t.klplugin.KLPlugin;
import com.sk89q.worldedit.WorldEdit;

import net.milkbowl.vault.economy.EconomyResponse;

public class BuildCostHandler {

	public static BuildCostHandler INSTANCE;
	
	public String worldName = "Kings_Landing";
	public HashMap<String, Float> baseBlockCosts;
	public ArrayList<BuildCostSet> regions;
	
	public float valueToCostRatio = 0.1f;
	public float handMultiplier = 1f;
	public float worldEditMultiplier = 2.5f;
	
	public BuildCostHandler() {
		INSTANCE = this;
		loadConfig();
		WorldEdit.getInstance().getEventBus().register(new WorldEditListener());
	}
	
	
	private void loadConfig() {
		baseBlockCosts = new HashMap<String, Float>();
		regions = new ArrayList<BuildCostSet>();
		File filePath = new File(KLPlugin.INSTANCE.getDataFolder() + "/blockeconomy/buildcosts.cfg");
		if (filePath.exists()) {
			FileUtil file = FileUtil.createFromFile(filePath);
			try {
				worldName = file.GetString("mainworldname");
				valueToCostRatio = file.GetFloat("valueratio");
				handMultiplier = file.GetFloat("handmultiplier");
				worldEditMultiplier = file.GetFloat("worldeditmultiplier");
				String[] array = file.GetArray("baseworthlist");
				for(String s : array) {
					if(s.contains("=")) {
						String[] split = s.split("=");
						String id = split[0];
						float worth = Float.parseFloat(split[1]);
						baseBlockCosts.put(id, worth);
					}
				}
			}
			catch(Exception e) {
				KLPlugin.log.log(Level.SEVERE, "Failed to read build cost config");
				e.printStackTrace();
			}
		} else {
			KLPlugin.log.log(Level.WARNING, "Build cost config not found! Creating a new one...");
			SaveDefaultConfig();
		}
		try {
			for(File f : new File(KLPlugin.INSTANCE.getDataFolder() + "/buildregions/").listFiles()) {
				String[] lines = readAllLines(new FileInputStream(f));
				for(String ln : lines) {
					if(!ln.contains("=")) continue;
					
				}
			}
		} catch(Exception e) {
			
		}
	}
	
	private String[] readAllLines(InputStream is) {
	    StringBuilder sb = new StringBuilder(512);
	    try {
	        Reader r = new InputStreamReader(is, "UTF-8");
	        int c = 0;
	        while ((c = r.read()) != -1) {
	            sb.append((char) c);
	        }
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	    return sb.toString().split("\n");
	}

	private void SaveDefaultConfig() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("<block-id>=<value>");
		list.add("minecraft:stone=1.0");
		FileUtil file = new FileUtil();
		file.SetValue("mainworldname", "Kings_Landing", "The world's name where build costs are applied");
		file.SetValue("valueratio", 0.1f, "Block value to cost ratio (0.1 = 0.10$ per block with 1 unit worth)");
		file.SetValue("handmultiplier", 1.0f, "Cost multiplier for blocks placed by hand");
		file.SetValue("worldeditmultiplier", 2.5f, "Cost multiplier for blocks placed using WorldEdit");
		file.SetArrayList("baseworthlist", list);
		file.Save("", "anticheat.cfg");
	}
	
	public void applyBlockPlacementCost(Player p, Vector loc, String block, boolean isWorldEdit) {
		float amount = getCostForBlock(loc, block, isWorldEdit);
		EconomyResponse r = KLPlugin.econ.withdrawPlayer(p, amount);
		if (r.transactionSuccess()) {
			// Transaction successful!
		} else {
			KLPlugin.log.warning("Failed to withdraw from player! " + r.errorMessage);
		}
	}
	
	public float getCostForBlock(Vector loc, String block, boolean isWorldEdit) {
		float cost = baseBlockCosts.getOrDefault(block, 1f);
		for(BuildCostSet r : regions) {
			if(r.affectsLocation(loc)) {
				cost *= r.getCostMultiplier(block);
			}
		}
		if(isWorldEdit) {
			cost *= worldEditMultiplier;
		} else {
			cost *= handMultiplier;
		}
		return cost * valueToCostRatio;
	}
}
