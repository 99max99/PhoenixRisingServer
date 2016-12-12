package net.kagani.game.player.content.surpriseevents;

import java.util.ArrayList;
import java.util.List;

import net.kagani.game.WorldTile;

public abstract class EventArena {

	/**
	 * All arena's.
	 */
	private static List<EventArena> arenas = new ArrayList<EventArena>();

	/**
	 * Properties of arena.
	 */
	private boolean multicombat;

	public EventArena(boolean multicombat) {
		this.multicombat = multicombat;
	}

	/**
	 * Create's the event arena.
	 */
	public abstract void create();

	/**
	 * Delete's event arena.
	 */
	public abstract void destroy();

	/**
	 * Create's random spawn location.
	 */
	public abstract WorldTile randomSpawn();

	/**
	 * Minimum x bounds.
	 */
	public abstract int minX();

	/**
	 * Minimum y bounds.
	 */
	public abstract int minY();

	/**
	 * Maximum x bounds.
	 */
	public abstract int maxX();

	/**
	 * Maximum y bounds.
	 */
	public abstract int maxY();

	public boolean isMulticombat() {
		return multicombat;
	}

	public static boolean isAtMultiArena(int x, int y) {
		synchronized (arenas) {
			for (EventArena arena : arenas) {
				if (arena.isMulticombat() && x >= arena.minX()
						&& x <= arena.maxX() && y >= arena.minY()
						&& y <= arena.maxY())
					return true;
			}
		}
		return false;
	}

	protected void registerArena() {
		synchronized (arenas) {
			arenas.add(this);
		}
	}

	protected void unregisterArena() {
		synchronized (arenas) {
			arenas.remove(this);
		}
	}

}
