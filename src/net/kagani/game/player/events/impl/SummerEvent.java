package net.kagani.game.player.events.impl;

import net.kagani.Settings;
import net.kagani.game.WorldTile;
import net.kagani.game.player.events.Event;

public class SummerEvent extends Event {

	/**
	 * @author: Dylan Page
	 */

	private WorldTile INSIDE = new WorldTile(0, 0, 0);
	private WorldTile OUTSIDE = Settings.HOME_LOCATION;

	@Override
	public void start() {
		player.setNextWorldTile(INSIDE);
	}

	@Override
	public void stop() {
		player.setNextWorldTile(OUTSIDE);
		stopEvent();
	}
}