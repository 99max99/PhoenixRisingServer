package net.kagani.game.player.content.surpriseevents.arenaimpl;

import net.kagani.game.WorldTile;
import net.kagani.game.map.MapBuilder;
import net.kagani.game.player.content.surpriseevents.EventArena;
import net.kagani.utils.Utils;

public class ElvenArena extends EventArena {

	/**
	 * Contains base positions.
	 */
	private int[] base;

	public ElvenArena(boolean multicombat) {
		super(multicombat);
	}

	@Override
	public void create() {
		if (base != null)
			throw new RuntimeException("Area already created.");
		int b = MapBuilder.findEmptyRegionHash(16, 16);
		base = new int[] { (b >> 8) << 6, (b & 0xFF) << 6 };
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				int chunkX = (base[0] >> 3) + x;
				int chunkY = (base[1] >> 3) + y;
				MapBuilder.copyChunk(272 + x, 408 + y, 0, chunkX, chunkY, 0, 0);
			}
		}
		registerArena();
	}

	@Override
	public void destroy() {
		if (base == null)
			throw new RuntimeException("Area already destroyed.");
		MapBuilder.destroyMap(base[0] >> 3, base[1] >> 3, 16, 16);
		base = null;
		unregisterArena();
	}

	@Override
	public int minX() {
		return base[0];
	}

	@Override
	public int minY() {
		return base[1];
	}

	@Override
	public int maxX() {
		return base[0] + (16 << 3);
	}

	@Override
	public int maxY() {
		return base[1] + (16 << 3);
	}

	@Override
	public WorldTile randomSpawn() {
		return new WorldTile(minX() + 30 + Utils.random(12), minY() + 30
				+ Utils.random(12), 0);

	}

}
