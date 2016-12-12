package net.kagani.game.player.content;

import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;

public class GnomeGlider {

	/**
	 * @author: Dylan Page
	 */

	public static final WorldTile

	MOUNTAIN = new WorldTile(2846, 3499, 0),

	GRAND_TREE = new WorldTile(2465, 3501, 3),

	CASTLE = new WorldTile(3321, 3427, 0),

	DESERT = new WorldTile(3283, 3213, 0),

	KARAMJA = new WorldTile(2971, 2969, 0);

	public static void sendInterface(Player player) {
		player.getInterfaceManager().sendCentralInterface(138);
	}

	public static void handleButtons(Player player, int componentId) {
		switch (componentId) {
		case 69:
			sendGnomeGliderTeleport(player, MOUNTAIN);
			break;
		case 37:
			sendGnomeGliderTeleport(player, GRAND_TREE);
			break;
		case 77:
			sendGnomeGliderTeleport(player, CASTLE);
			break;
		case 85:
			sendGnomeGliderTeleport(player, DESERT);
			break;
		case 61:
			sendGnomeGliderTeleport(player, KARAMJA);
			break;
		}
	}

	public static void sendGnomeGliderTeleport(Player player,
			final WorldTile tile) {
		player.setNextWorldTile(tile);
		player.getInterfaceManager().removeCentralInterface();
		player.unlock();
		player.getPackets().sendGameMessage(
				"You travel using the gnome glider.", true);
	}
}