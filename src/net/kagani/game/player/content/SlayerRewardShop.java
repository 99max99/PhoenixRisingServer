package net.kagani.game.player.content;

import net.kagani.game.player.Player;

public class SlayerRewardShop {

	/*
	 * Represents Slayer InterfaceId
	 */

	public static final int SLAYER_INTERFACE = 1308;

	public enum Rewards {
		BROAD_ARROWS(), BROAD_BOLTS(), RUNES_FOR_SLAYER_DART(), RING_OF_SLAYING(), FULL_SLAYER_HELMET_UPGRADE_1(), FEROCIOUS_RING_UPGRADE(), FULL_SLAYER_HELMET_UPGRADE_2(), SLAYER_XP(), FULL_SLAYER_HELMET_UPGRADE_3(),
	}

	public static void sendSlayerInterface(Player player) {
		player.getInterfaceManager().sendCentralInterface(SLAYER_INTERFACE);
		player.getPackets().sendIComponentText(SLAYER_INTERFACE, 435,
				"Current points: ");
		player.getPackets().sendIComponentText(SLAYER_INTERFACE, 436,
				Integer.toString(player.getSlayerManager().getPoints()));
	}

	public static void handleComponents(Player player, int componentId) {
		switch (componentId) {
		case 273:// Broad Arrows
			break;
		case 257:// Runes for Slayer Dart
			break;
		case 706:// Full Slayer Helm upgrade (1)
			break;
		case 726:// Full Slayer Helm upgrade (2)
			break;
		case 746:// Full Slayer Helm upgrade (3)
			break;
		case 265:// Broad Bolts
			break;
		case 252:// Slayer Ring
			break;
		case 953:// Ferocius Ring
			break;
		case 131:// Slayer Points
			break;
		}
	}
}