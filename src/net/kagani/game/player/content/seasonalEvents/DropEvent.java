package net.kagani.game.player.content.seasonalEvents;

import java.util.TimerTask;

import net.kagani.executor.GameExecutorManager;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class DropEvent {

	private static enum Drops {
		PUMPKIN(1959);

		private int id;

		private Drops(int id) {
			this.id = id;
		}
	}

	private static final Drops CURRENT = null;
	private static final int RATE = 60; // every 60 second

	private static boolean enabled() {
		return CURRENT != null;
	}

	public static void init() {
		if (!enabled())
			return;
		GameExecutorManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					for (int i = 0; i < 1000; i++) {
						int x = 2048 + Utils.random(1855);
						int y = 2496 + Utils.random(1663);
						if (!World.isTileFree(0, x, y, 1))
							continue;
						World.addGroundItem(new Item(CURRENT.id),
								new WorldTile(x, y, 0));
						return;
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, RATE * 1000, RATE * 1000);
	}

	public static String[] HALLOWEEN_MESSAGES = {
			"Have a fun and safe halloween!",
			"Be sure to double check your candy for LSD!",
			"Always have your gaurdian come and pre-examine your candy.",
			"Is it just me, or did it get chilly.", "Don't drink and drive." };
	
	public static int[] HALLOWEEN_NPCS = { 94, 100, 73, 6214, 3073, 3347 };

}