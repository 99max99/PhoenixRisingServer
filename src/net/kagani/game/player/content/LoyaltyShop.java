package net.kagani.game.player.content;

import net.kagani.game.player.Player;

public class LoyaltyShop {

	/*
	 * @author: Dylan Page
	 */

	private Player player;

	public LoyaltyShop(Player player) {
		this.player = player;
	}

	public void openShop() {
		player.getInterfaceManager().setFairyRingInterface(false, 1143);
	}
}