package net.kagani.game.player.content;

import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;

public class SpiritTree {

	/**
	 * @author: Dylan Page
	 */

	private static final WorldTile[] LOCATIONS = {

	new WorldTile(2554, 3255, 0),

	new WorldTile(3187, 3507, 0),

	new WorldTile(2416, 2852, 0),

	new WorldTile(2339, 3108, 0),

	new WorldTile(2541, 3170, 0),

	new WorldTile(2462, 3445, 0)

	};

	public static void openInterface(Player player, boolean isMini) {
		player.getVarsManager().sendVarBit(3959, 3);
		player.getInterfaceManager().sendCentralInterface(1145);
		player.getPackets().sendUnlockIComponentOptionSlots(1145, 6, 0, 7, 0);
		if (player.getRegionId() == 10033 || player.getRegionId() == 12102)
			player.getVarsManager().sendVar(1469, 0x27b8c61);
		else if (player.getRegionId() == 9781)
			player.getVarsManager().sendVar(1469, 0x2678d74);
		else {
			sendTeleport(player, LOCATIONS[4]);
		}
	}

	private static void sendTeleport(Player player, WorldTile tile) {
		player.getPackets()
				.sendGameMessage(
						"You place your hands on the dry tough bark of the spirit tree, and feel a surge of energy run through your veins.");
		Magic.sendTeleportSpell(player, 7082, 7084, 1229, 1229, 1, 0, tile, 4,
				true, Magic.OBJECT_TELEPORT);
	}

	public static void handleSpiritTree(Player player, int componentId) {
		switch (componentId) {
		case 17:
			sendTeleport(player, LOCATIONS[1]);
			break;
		case 19:
			sendTeleport(player, LOCATIONS[2]);
			break;
		case 15:
			sendTeleport(player, LOCATIONS[3]);
			break;
		case 0:
			sendTeleport(player, LOCATIONS[4]);
			break;
		case 13:
			sendTeleport(player, LOCATIONS[5]);
			break;
		default:
			player.getPackets().sendGameMessage(
					componentId + " is an unknown id.");
			break;
		}
	}
}