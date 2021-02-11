package com.d3t.klplugin.economy;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class BuildCostSet {

	public BoundingBox region;
	
	private HashMap<String, Float> blockCostMultiplierList;
	
	public BuildCostSet(Location p1, Location p2) {
		blockCostMultiplierList = new HashMap<String, Float>();
		if(p1 != null && p2 != null) {
			region = new BoundingBox(p1.getBlockX(), p1.getBlockY(),p1.getBlockZ(),p2.getBlockX(), p2.getBlockY(),p2.getBlockZ());
		}
	}
	
	public void setBlockCost(String block, float cost) {
		blockCostMultiplierList.put(block, cost);
	}
	
	private static final String[] colorPrefixes = new String[] {
		"black",
		"red",
		"green",
		"brown",
		"blue",
		"purple",
		"cyan",
		"light_gray",
		"gray",
		"pink",
		"lime",
		"yellow",
		"light_blue",
		"magenta",
		"orange",
		"white"
	};
	
	public void setBlockCostAllColors(String block, float cost) {
		for(String c : colorPrefixes) {
			setBlockCost(c+"_"+block, cost);
		}
	}
	
	public boolean affectsLocation(Vector loc) {
		if(region == null) {
			return true;
		} else {
			return region.contains(loc);
		}
	}
	
	public float getCostMultiplier(String block) {
		return blockCostMultiplierList.getOrDefault(block, 0f);
	}
}
