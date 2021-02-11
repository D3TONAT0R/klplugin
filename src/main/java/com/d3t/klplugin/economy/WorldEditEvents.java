package com.d3t.klplugin.economy;

import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockStateHolder;

public class WorldEditEvents extends AbstractDelegateExtent {

	protected final World weWorld;

	protected final org.bukkit.entity.Player player;

	public WorldEditEvents(World world, Extent extent, Player player) {

		super(extent);

		this.weWorld = world;

		this.player = Bukkit.getPlayer(player.getUniqueId());
	}

	@Override
	public boolean setBlock(BlockVector3 location, BlockStateHolder block) throws WorldEditException {
		if(player != null) {
			Vector vector = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
			BuildCostHandler.INSTANCE.chargePlayerForBlockPlacement(player, vector, block.getBlockType().getId(), true);
		}
		return super.setBlock(location, block);
	}
}
