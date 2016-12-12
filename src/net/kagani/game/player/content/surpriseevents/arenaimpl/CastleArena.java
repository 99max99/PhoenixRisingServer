package net.kagani.game.player.content.surpriseevents.arenaimpl;

import net.kagani.game.WorldTile;
import net.kagani.game.map.MapBuilder;
import net.kagani.game.player.content.surpriseevents.EventArena;
import net.kagani.utils.Utils;

public class CastleArena extends EventArena {

	/**
	 * Contains base positions.
	 */
	private int[] base;

	public CastleArena(boolean multicombat) {
		super(multicombat);
	}

	@Override
	public void create() {
		if (base != null)
			throw new RuntimeException("Area already created.");
		int b = MapBuilder.findEmptyRegionHash(8, 8);
		base = new int[] { (b >> 8) << 6, (b & 0xFF) << 6 };
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				int chunkX = (base[0] >> 3) + x;
				int chunkY = (base[1] >> 3) + y;
				// MapBuilder.copyChunk(328 + x, 696 + y, 0, chunkX, chunkY, 0,
				// 0);
				// if (x >= 2 && y >= 2 && x <= 6 && y <= 6)
				// MapBuilder.copyChunk((3222 >> 3) + x, (3222 >> 3) + y, 0,
				// chunkX, chunkY, 0, 0);
				// else
				// MapBuilder.copyChunk((32 << 3) + x, (64 << 3) + y, 0, chunkX,
				// chunkY, 0, 0);
				MapBuilder.copyChunk((44 << 3) + x, (79 << 3) + y, 0, chunkX,
						chunkY, 0, 0);

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
		return base[0] + (8 << 3);
	}

	@Override
	public int maxY() {
		return base[1] + (8 << 3);
	}

	@Override
	public WorldTile randomSpawn() {
		return new WorldTile(minX() + 29 + Utils.random(6), minY() + 12
				+ Utils.random(6), 0);

	}

}
