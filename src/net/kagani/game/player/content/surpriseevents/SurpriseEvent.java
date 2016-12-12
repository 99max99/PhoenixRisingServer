package net.kagani.game.player.content.surpriseevents;

import net.kagani.game.player.Player;

public interface SurpriseEvent {

	/**
	 * Start's event.
	 */
	public void start();

	/**
	 * Trie's to join the event.
	 */
	public void tryJoin(Player player);
}
