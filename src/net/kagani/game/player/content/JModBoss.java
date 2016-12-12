package net.kagani.game.player.content;

import net.kagani.game.player.Player;
import net.kagani.network.decoders.WorldPacketsDecoder;

public class JModBoss {

	/**
	 * @author: Dylan Page
	 */

	private static final int CONTROL = WorldPacketsDecoder.ACTION_BUTTON1_PACKET,

			ANNOUNCE = WorldPacketsDecoder.ACTION_BUTTON2_PACKET,

			RESOURCE_CHEST = WorldPacketsDecoder.ACTION_BUTTON3_PACKET

			;

	public static void handle(Player player, int packetId) {
		if (player.getRights() < 2) {
			player.getInventory().deleteItem(20428, Integer.MAX_VALUE);
			return;
		}
		switch (packetId) {
		case CONTROL:
			break;
		case ANNOUNCE:
			break;
		case RESOURCE_CHEST:
			break;
		}
	}
}