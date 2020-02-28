package com.d3t.klplugin;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TemperatureMechanic {

	public static final String tempScore = "temperature";
	public static final String tempTimerScore = "temperatureTimer";
	public static final String worldName = "glarus_winter";
	
	final Material[] leatheritems = new Material[] {Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS};
	final Material[] ironitems = new Material[] {Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS};
	final Material[] golditems = new Material[] {Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS};
	final Material[] diamonditems = new Material[] {Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS};
	final String[] armorTypeNames = new String[] {"Helmet", "Chestplate", "Leggings", "Boots"};
	final String[] armorTypeNamesLeather = new String[] {"Cap", "Tunic", "Pants", "Boots"};

	private World world;
	private Server server;

	private double globalTemp;

	private HashMap<UUID, Float> dic;

	public TemperatureMechanic() {
		server = KLPlugin.INSTANCE.getServer();
		world = server.getWorld(worldName);
		dic = new HashMap<UUID, Float>();
		createCraftingRecipes();
	}

	public void onUpdate() {
		calculateGlobalSources();
		for (Player p : server.getOnlinePlayers()) {
			if (!p.isDead() && p.getTicksLived() > 100 && p.getGameMode() == GameMode.SURVIVAL
					&& p.getWorld().getName().equalsIgnoreCase(worldName)) {
				// server.getLogger().info(p.getName() + " is in glarus!");
				float temp = calculateTemperatureAt(p.getLocation());
				float insulation = getPlayerInsulation(p);
				float finalTemp = temp + insulation;
				float playertemp = getPlayerTemp(p);
				playertemp = lerp(playertemp, finalTemp, 0.3f);
				setPlayerTemp(p, playertemp);
				applyEffects(p, playertemp);

				// temporary debugging
				ItemStack item = p.getInventory().getItemInMainHand();
				if (item != null && item.getType() == Material.DEBUG_STICK) {
					p.sendMessage(String.format("Src: %s   You: %s   Ins: %s", Math.round(temp * 10f) / 10f,
							Math.round(playertemp * 10f) / 10f, Math.round(insulation * 10f) / 10f));
					if (p.isSneaking()) {
						p.sendMessage("heat " + heat);
						p.sendMessage("cold " + cold);
						p.sendMessage("envMultiplier " + envMultiplier);
						p.sendMessage("envsrc " + envsrc);
						p.sendMessage("heightsrc " + heightsrc);
						p.sendMessage("globalcoldmul " + globalColdMul);
					}
				}
			}
		}
	}
	
	private void createCraftingRecipes() {
		for(int i = 0; i < 4; i++) {
			NamespacedKey key = new NamespacedKey(KLPlugin.INSTANCE, "armor_wool_"+i);
			ItemStack result = new ItemStack(leatheritems[i], 1);
			LeatherArmorMeta meta = (LeatherArmorMeta)result.getItemMeta();
			meta.setCustomModelData(1);
			meta.setDisplayName("Wool "+armorTypeNamesLeather[i]);
			meta.setColor(Color.WHITE);
			result.setItemMeta(meta);
			ShapedRecipe r = new ShapedRecipe(key, result);
			if(i == 0) {
				r.shape("xxx","x x","   ");
			} else if(i == 1) {
				r.shape("x x","xxx","xxx");
			} else if(i == 2) {
				r.shape("xxx","x x","x x");
			} else if(i == 3) {
				r.shape("   ","x x","x x");
			}
			r.setIngredient('x', Material.WHITE_WOOL);
			Bukkit.addRecipe(r);
		}
		createCombinedRecipes(ironitems, "Iron");
		createCombinedRecipes(golditems, "Golden");
		createCombinedRecipes(diamonditems, "Diamond");
	}
	
	private void createCombinedRecipes(Material[] items, String name) {
		String nname = name.toLowerCase();
		for(int i = 0; i < 4; i++) {
			NamespacedKey key = new NamespacedKey(KLPlugin.INSTANCE, "armor_"+nname+"_"+i);
			ItemStack result = new ItemStack(items[i], 1);
			ItemMeta meta = result.getItemMeta();
			meta.setCustomModelData(1);
			meta.setDisplayName("§bArctic "+name+" "+armorTypeNames[i]);
			result.setItemMeta(meta);
			ShapelessRecipe r = new ShapelessRecipe(key, result);
			r.addIngredient(leatheritems[i]);
			r.addIngredient(items[i]);
			Bukkit.addRecipe(r);
		}
	}

	private void applyEffects(Player p, float playertemp) {
		if (playertemp < -0.5f) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0), true);
		}
		if (playertemp < -1.5) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0), true);
		}
		if (playertemp < -2.5f) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1), true);
			p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1), true);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 0), true);
		}
		if (playertemp <= -4f) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 2), true);
			p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 2), true);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 1), true);
			p.damage(Math.min((Math.abs(playertemp) - 3) / 2, 5));
		}
	}

	private float getPlayerTemp(Player p) {
		UUID u = p.getUniqueId();
		if (!dic.containsKey(u))
			dic.put(u, 0f);
		return dic.get(u);
	}

	private void setPlayerTemp(Player p, float f) {
		dic.put(p.getUniqueId(), f);
	}

	private float getPlayerInsulation(Player p) {
		float mul = 0.8f;
		float insulation = 0;
		PlayerInventory inv = p.getInventory();
		
		//0 Boot, 1 Leggings, 2 Chestplate, 3 Helmet 
		ItemStack[] armor = inv.getArmorContents();
		
		boolean insulatedArmor[] = new boolean[4];
		
		for(int i = 0; i < 4; i++) {
			if(armor[i] != null) {
				if(armor[i].getType() == leatheritems[3-i]) insulatedArmor[i] = true;
				else if(armor[i].getItemMeta().hasCustomModelData() && armor[i].getItemMeta().getCustomModelData() == 1) insulatedArmor[i] = true;
				else insulatedArmor[i] = false;
			}
		}
	
		//top-to-bottom
		if (insulatedArmor[3])
			insulation += 5 * mul;
		if (insulatedArmor[2])
			insulation += 8 * mul;
		if (insulatedArmor[1])
			insulation += 7 * mul;
		if (insulatedArmor[0])
			insulation += 4 * mul;
		
		return insulation;
	}

	private void calculateGlobalSources() {
		globalTemp = 0;
		if (world.hasStorm())
			globalTemp += -5f;
		globalColdMul = getNightColdnessMultiplier(world.getTime() % 24000L);
		globalTemp += globalColdMul * -7f;
	}

	private float heat;
	private float cold;
	private float envMultiplier;
	private double envsrc;
	private float heightsrc;
	private double globalColdMul;

	private float calculateTemperatureAt(Location loc) {
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		int rh = 4;
		int rc = 2;
		heat = 0;
		cold = 0;

		// Get light level as multiplier to the environmental sources.
		byte lightlevel = 15;
		if (!world.getBlockAt(loc).getType().isSolid()) {
			lightlevel = world.getBlockAt(loc).getLightFromSky();
		}
		envMultiplier = lerp(0.5f, 1.0f, lightlevel / 15f);
		// Calculate environmental sources.
		heightsrc = (y - 64) / 16f + 2f;
		envsrc = envMultiplier * (-globalTemp + heightsrc);

		// Get heat sources from blocks.
		for (int i = x - rh; i <= x + rh; i++) {
			for (int j = y - rh; j <= y + rh; j++) {
				for (int k = z - rh; k <= z + rh; k++) {
					heat += getBlockTemperatureHeat(world.getBlockAt(i, j, k));
				}
			}
		}
		heat = Math.min(16, heat);

		// Get cold sources from blocks.
		for (int i = x - rc; i <= x + rc; i++) {
			for (int j = y - rc; j <= y + rc; j++) {
				for (int k = z - rc; k <= z + rc; k++) {
					cold += getBlockTemperatureCold(world.getBlockAt(i, j, k));
				}
			}
		}
		cold = Math.min(16, cold);
		return (float) (heat - cold - envsrc);
	}

	private float getBlockTemperatureHeat(Block b) {
		Material m = b.getType();
		BlockData d = b.getBlockData();
		if ((m == Material.FURNACE || m == Material.BLAST_FURNACE || m == Material.SMOKER) && ((Lightable) d).isLit())
			return 6;
		else if ((m == Material.CAMPFIRE) && ((Lightable) d).isLit())
			return 8;
		else if (m == Material.TORCH)
			return 4;
		else if (m == Material.FIRE)
			return 8;
		else if (m == Material.LAVA)
			return 10;
		else if (m == Material.GLOWSTONE)
			return 5;
		else if (m == Material.JACK_O_LANTERN)
			return 4;
		else if (m == Material.LANTERN)
			return 2;
		else if (m == Material.MAGMA_BLOCK)
			return 8;
		return 0;
	}

	private float getBlockTemperatureCold(Block b) {
		Material m = b.getType();
		if (m == Material.SNOW)
			return 0.05f;
		else if (m == Material.SNOW_BLOCK)
			return 0.1f;
		else if (m == Material.ICE)
			return 0.15f;
		else if (m == Material.PACKED_ICE)
			return 0.7f;
		else if (m == Material.BLUE_ICE)
			return 2.0f;
		return 0;
	}

	private double getNightColdnessMultiplier(long timeTick) {
		float t = timeTick / 24000f;
		double m = Math.sin(2 * t * Math.PI + Math.PI) * 1.2f;
		return Math.max(0, Math.min(1, m));
	}

	private float lerp(float a, float b, float f) {
		f = Math.max(0, Math.min(1, f));
		return a + f * (b - a);
	}
}
