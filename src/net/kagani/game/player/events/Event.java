package net.kagani.game.player.events;

import net.kagani.game.player.Player;

public abstract class Event {

	/**
	 * @author: Dylan Page
	 */

	protected Player player;

	public final void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public abstract void start();

	public abstract void stop();

	public final void stopEvent() {
		player.getControlerManager().removeControlerWithoutCheck();
	}
}