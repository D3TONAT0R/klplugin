package com.d3t.klplugin.advancements;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import com.d3t.klplugin.KLPlugin;

import eu.endercentral.crazy_advancements.Advancement;
import eu.endercentral.crazy_advancements.AdvancementDisplay;
import eu.endercentral.crazy_advancements.AdvancementDisplay.AdvancementFrame;
import eu.endercentral.crazy_advancements.AdvancementVisibility;
import eu.endercentral.crazy_advancements.CrazyAdvancements;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;

public class AdvancementHandler {
	
	public static final String namespace = "server";
	
	public AdvancementManager manager;
	
	ArrayList<Advancement> allAdvancements = new ArrayList<Advancement>();
	AdvancementDisplay rootDisp;
	Advancement root;
	
	Advancement firstBlockSet;
	
	public AdvancementHandler(PluginManager pm) {
		/*Player[] players = new Player[KLPlugin.INSTANCE.getServer().getOnlinePlayers().size()];
		players = (Player[])KLPlugin.INSTANCE.getServer().getOnlinePlayers().toArray();*/
		manager = CrazyAdvancements.getNewAdvancementManager();
		rootDisp = new AdvancementDisplay(new ItemStack(Material.STONE_BRICKS), "Server achievements", "Gotta get 'em all", AdvancementFrame.TASK, true, true, AdvancementVisibility.ALWAYS);
		rootDisp.setBackgroundTexture("textures/block/stone_bricks.png");
		root = new Advancement(null, new NameKey(namespace, "root"), rootDisp);
		firstBlockSet = addAdvancement(Material.STONE, "The beginning...", "Place your first block in the world!", root, "firstblock", 1, 0);
		for(Player p : KLPlugin.INSTANCE.getServer().getOnlinePlayers()) {
			load(p);
		}
		registerAdvancements();
		System.out.println("Created server advancements!");
	}
	
	private Advancement addAdvancement(ItemStack display, String title, String desc, Advancement parent, String childKey, float x, float y) {
		AdvancementDisplay disp = new AdvancementDisplay(display, title, desc, AdvancementFrame.TASK, true, true, AdvancementVisibility.VANILLA);
		disp.setCoordinates(x, y);
		Advancement adv = new Advancement(parent, new NameKey(namespace, childKey), disp);
		//manager.addAdvancement(adv);
		return adv;
	}
	
	private Advancement addAdvancement(Material display, String title, String desc, Advancement parent, String childKey, float x, float y) {
		return addAdvancement(new ItemStack(display), title, desc, parent, childKey, x, y);
	}
	
	public void addPlayer(Player p) {
		manager.addPlayer(p);
		System.out.println("Added player");
		manager.grantAdvancement(p, root);
	}
	
	public void removePlayer(Player p) {
		manager.removePlayer(p);
	}
	
	public boolean grantAdvancement(Player p, Advancement adv) {
		if(!adv.isGranted(p) && adv.isAnythingGrantedUntil(p)) {
			manager.grantAdvancement(p, adv);
			return true;
		} else {
			return false;
		}
	}
	
	public void saveAll() {
		for(Player p : KLPlugin.INSTANCE.getServer().getOnlinePlayers()) {
			save(p);
		}
	}
	
	public void save(Player p) {
		manager.saveProgress(p, namespace);
	}
	
	public void load(Player p) {
		manager.loadProgress(p);
	}
	
	private void registerAdvancements() {
		manager.addAdvancement(allAdvancements.toArray(new Advancement[allAdvancements.size()]));
	}
}
