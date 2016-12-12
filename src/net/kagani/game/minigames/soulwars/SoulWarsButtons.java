/**
 * @author Dylan Page - Ventyz Productions
 *
 * SoulWarsButtons.java created on 31.des.2014.
 */
package net.kagani.game.minigames.soulwars;

import net.kagani.game.player.Player;

// TODO: Auto-generated Javadoc
/**
 * The Class SoulWarsButtons.
 */
public class SoulWarsButtons {

	/**
	 * Handle rewards.
	 *
	 * @param player
	 *            the player
	 * @param componentId
	 *            the component id
	 * @param packetId
	 *            the packet id
	 */
	public static void handleRewards(final Player player, int componentId,
			int packetId) {
		switch (componentId) {

		case 21:

		case 5:
			break;

		case 27:// Gold Charm
			switch (packetId) {
			case 61:
				player.getPackets().sendGameMessage(
						"Gold Charm is worth 2 Zeal Points for 5 Charms.");
				player.getPackets().sendIComponentText(276, 59,
						"Zeal: " + player.zeal);
				break;
			case 64:
				if (player.zeal >= 2) {
					player.zeal -= 2;
					player.getInventory().addItem(12158, 5);
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				if (player.zeal <= 1) {
					player.getPackets().sendGameMessage(
							"You don't have enough zeal for this..");
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			}
			break;

		case 26:// Green Charm
			switch (packetId) {
			case 61:
				player.getPackets().sendGameMessage(
						"Green Charm is worth 2 Zeal Points for 5 Charms.");
				player.getPackets().sendIComponentText(276, 59,
						"Zeal: " + player.zeal);
				player.getPackets().sendIComponentText(276, 59,
						"Zeal: " + player.zeal);
				break;
			case 64:
				if (player.zeal >= 2) {
					player.zeal -= 2;
					player.getInventory().addItem(12159, 5);
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				if (player.zeal <= 1) {
					player.getPackets().sendGameMessage(
							"You don't have enough zeal for this..");
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			}
			break;

		case 25:// Red Charm
			switch (packetId) {
			case 61:
				player.getPackets().sendGameMessage(
						"Red Charm is worth 2 Zeal Points for 5 Charms.");
				player.getPackets().sendIComponentText(276, 59,
						"Zeal: " + player.zeal);
				break;
			case 64:
				if (player.zeal >= 2) {
					player.zeal -= 2;
					player.getInventory().addItem(12160, 5);
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				if (player.zeal <= 1) {
					player.getPackets().sendGameMessage(
							"You don't have enough zeal for this..");
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			}
			break;

		case 24:// Blue Charm
			switch (packetId) {
			case 61:
				player.getPackets().sendGameMessage(
						"Blue Charm is worth 2 Zeal Points for 5 Charms.");
				player.getPackets().sendIComponentText(276, 59,
						"Zeal: " + player.zeal);
				break;
			case 64:
				if (player.zeal >= 2) {
					player.zeal -= 2;
					player.getInventory().addItem(12163, 5);
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				if (player.zeal <= 1) {
					player.getPackets().sendGameMessage(
							"You don't have enough zeal for this..");
				}
				break;
			}
			break;

		case 6:// Creeping Hand
			player.getPackets()
					.sendGameMessage("Unavailable Soul Wars Reward.");
			if (player.getRights() >= 2) {
				player.getPackets().sendGameMessage(
						"B: " + componentId + " ][ P: " + packetId);
			}
			break;
		case 14:// Minitrice
			player.getPackets()
					.sendGameMessage("Unavailable Soul Wars Reward.");
			if (player.getRights() >= 2) {
				player.getPackets().sendGameMessage(
						"B: " + componentId + " ][ P: " + packetId);
			}
			break;
		case 10:// Baby Basilisk
			player.getPackets()
					.sendGameMessage("Unavailable Soul Wars Reward.");
			if (player.getRights() >= 2) {
				player.getPackets().sendGameMessage(
						"B: " + componentId + " ][ P: " + packetId);
			}
			break;
		case 12:// Baby Kurask
			player.getPackets()
					.sendGameMessage("Unavailable Soul Wars Reward.");
			if (player.getRights() >= 2) {
				player.getPackets().sendGameMessage(
						"B: " + componentId + " ][ P: " + packetId);
			}
			break;
		case 16:// Abyssal Minion
			player.getPackets()
					.sendGameMessage("Unavailable Soul Wars Reward.");
			if (player.getRights() >= 2) {
				player.getPackets().sendGameMessage(
						"B: " + componentId + " ][ P: " + packetId);
			}
			break;
		case 8:// GAMBLE!
			player.getPackets()
					.sendGameMessage("Unavailable Soul Wars Reward.");
			if (player.getRights() >= 2) {
				player.getPackets().sendGameMessage(
						"B: " + componentId + " ][ P: " + packetId);
			}
			break;

		case 37:// Attack..
			switch (packetId) {
			case 61:
				player.getPackets().sendGameMessage(
						"Attack Reward: 2 Zeal is 50K XP.");
				player.getPackets().sendIComponentText(276, 59,
						"Zeal: " + player.zeal);
				break;
			case 64:
				if (player.zeal <= 1) {
					player.getPackets().sendGameMessage("You need more zeal!");
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				if (player.zeal >= 2) {
					player.getPackets().sendGameMessage(
							"You spend 2 Zeal on 50,000 Attack XP.");
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
					player.getSkills().addXp(0, 50000);
					player.zeal -= 2;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			case 4:
				if (player.zeal <= 19) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 20) {
					player.getPackets().sendGameMessage(
							"You spend 20 Zeal on 500,000 Attack XP.");
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
					player.getSkills().addXp(0, 500000);
					player.zeal -= 20;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			case 52:
				if (player.zeal <= 199) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 200) {
					player.getPackets().sendGameMessage(
							"You spend 200 Zeal on 5,000,000 Attack XP.");
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
					player.getSkills().addXp(0, 5000000);
					player.zeal -= 200;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			}
			break;

		case 32:// Slayer Reward
			switch (packetId) {
			case 61:
				player.getPackets().sendGameMessage(
						"Slayer Reward: 2 Zeal is 40K XP.");
				break;
			case 64:
				if (player.zeal <= 1) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 2) {
					player.getPackets().sendGameMessage(
							"You spend 2 Zeal on 40,000 Slayer XP.");
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
					player.getSkills().addXp(18, 40000);
					player.zeal -= 2;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			case 4:
				if (player.zeal <= 19) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 20) {
					player.getPackets().sendGameMessage(
							"You spend 20 Zeal on 400,000 Slayer XP.");
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
					player.getSkills().addXp(18, 400000);
					player.zeal -= 20;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			case 52:
				if (player.zeal <= 199) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 200) {
					player.getPackets().sendGameMessage(
							"You spend 200 Zeal on 4,000,000 Slayer XP.");
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
					player.getSkills().addXp(18, 4000000);
					player.zeal -= 200;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);

				}
				break;
			}
			break;

		case 38:// Prayer Reward
			switch (packetId) {
			case 61:
				player.getPackets().sendGameMessage(
						"Prayer Reward: 2 Zeal is 40K XP.");
				break;
			case 64:
				if (player.zeal <= 1) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 2) {
					player.getPackets().sendGameMessage(
							"You spend 2 Zeal on 40,000 Prayer XP.");
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
					player.getSkills().addXp(5, 40000);
					player.zeal -= 2;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			case 4:
				if (player.zeal <= 19) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 20) {
					player.getPackets().sendGameMessage(
							"You spend 20 Zeal on 400,000 Prayer XP.");
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
					player.getSkills().addXp(5, 400000);
					player.zeal -= 20;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			case 52:
				if (player.zeal <= 199) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 200) {
					player.getPackets().sendGameMessage(
							"You spend 200 Zeal on 4,000,000 Prayer XP.");
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
					player.getSkills().addXp(5, 4000000);
					player.zeal -= 200;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			}
			break;

		case 39:// Mage Reward
			switch (packetId) {
			case 61:
				player.getPackets().sendGameMessage(
						"Magic Reward: 2 Zeal is 50K XP.");
				break;
			case 64:
				if (player.zeal <= 1) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 2) {
					player.getPackets().sendGameMessage(
							"You spend 2 Zeal on 50,000 Magic XP.");
					player.getSkills().addXp(6, 50000);
					player.zeal -= 2;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			case 4:
				if (player.zeal <= 19) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 20) {
					player.getPackets().sendGameMessage(
							"You spend 20 Zeal on 500,000 Magic XP.");
					player.getSkills().addXp(6, 500000);
					player.zeal -= 20;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			case 52:
				if (player.zeal <= 199) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 200) {
					player.getPackets().sendGameMessage(
							"You spend 200 Zeal on 5,000,000 Magic XP.");
					player.getSkills().addXp(6, 5000000);
					player.zeal -= 200;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			}
			break;

		case 36:// Range Reward
			switch (packetId) {
			case 61:
				player.getPackets().sendGameMessage(
						"Range Reward: 2 Zeal is 50K XP.");
				break;
			case 64:
				if (player.zeal <= 1) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 2) {
					player.getPackets().sendGameMessage(
							"You spend 2 Zeal on 50,000 Ranging XP.");
					player.getSkills().addXp(4, 50000);
					player.zeal -= 2;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			case 4:
				if (player.zeal <= 19) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 20) {
					player.getPackets().sendGameMessage(
							"You spend 20 Zeal on 500,000 Ranging XP.");
					player.getSkills().addXp(4, 500000);
					player.zeal -= 20;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			case 52:
				if (player.zeal <= 199) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 200) {
					player.getPackets().sendGameMessage(
							"You spend 200 Zeal on 5,000,000 Ranging XP.");
					player.getSkills().addXp(4, 5000000);
					player.zeal -= 200;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			}
			break;

		case 33:// Hitpoints Reward
			switch (packetId) {
			case 61:
				player.getPackets().sendGameMessage(
						"HitPoints Reward: 2 Zeal is 50K XP.");
				break;
			case 64:
				if (player.zeal <= 1) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 2) {
					player.getPackets().sendGameMessage(
							"You spend 2 Zeal on 50,000 HitPoints XP.");
					player.getSkills().addXp(3, 50000);
					player.zeal -= 2;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			case 4:
				if (player.zeal <= 19) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 20) {
					player.getPackets().sendGameMessage(
							"You spend 20 Zeal on 500,000 HitPoints XP.");
					player.getSkills().addXp(3, 500000);
					player.zeal -= 20;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			case 52:
				if (player.zeal <= 199) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 200) {
					player.getPackets().sendGameMessage(
							"You spend 200 Zeal on 5,000,000 HitPoints XP.");
					player.getSkills().addXp(3, 5000000);
					player.zeal -= 200;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			}
			break;

		case 34:// Defence Reward
			switch (packetId) {
			case 61:
				player.getPackets().sendGameMessage(
						"Defence Reward: 2 Zeal is 50K XP.");
				break;
			case 64:
				if (player.zeal <= 1) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 2) {
					player.getPackets().sendGameMessage(
							"You spend 2 Zeal on 50,000 Defence XP.");
					player.getSkills().addXp(1, 50000);
					player.zeal -= 2;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			case 4:
				if (player.zeal <= 19) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 20) {
					player.getPackets().sendGameMessage(
							"You spend 20 Zeal on 500,000 Defence XP.");
					player.getSkills().addXp(1, 500000);
					player.zeal -= 20;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			case 52:
				if (player.zeal <= 199) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 200) {
					player.getPackets().sendGameMessage(
							"You spend 200 Zeal on 5,000,000 Defence XP.");
					player.getSkills().addXp(1, 5000000);
					player.zeal -= 200;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			}
			break;

		case 35:// Strength Reward

			switch (packetId) {
			case 61:
				player.getPackets().sendGameMessage(
						"Strength Reward: 2 Zeal is 50K XP.");
				break;
			case 64:
				if (player.zeal <= 1) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 2) {
					player.getPackets().sendGameMessage(
							"You spend 2 Zeal on 50,000 Strength XP.");
					player.getSkills().addXp(2, 50000);
					player.zeal -= 2;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			case 4:
				if (player.zeal <= 19) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 20) {
					player.getPackets().sendGameMessage(
							"You spend 20 Zeal on 500,000 Strength XP.");
					player.getSkills().addXp(2, 500000);
					player.zeal -= 20;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			case 52:
				if (player.zeal <= 199) {
					player.getPackets().sendGameMessage("You need more zeal!");
				}
				if (player.zeal >= 200) {
					player.getPackets().sendGameMessage(
							"You spend 200 Zeal on 5,000,000 Strength XP.");
					player.getSkills().addXp(2, 5000000);
					player.zeal -= 200;
					player.getPackets().sendIComponentText(276, 59,
							"Zeal: " + player.zeal);
				}
				break;
			}
			break;

		/*
		 * //case 37://Attack Reward 130K XP each
		 * player.getgetPackets().sendGameMessage
		 * ("Unavailable Soul Wars Reward."); if(player.getRights() >= 2) {
		 * player.getgetPackets().sendGameMessage("B: "+componentId+
		 * " ][ P: "+packet); } break;
		 */
		default:
			if (player.getRights() >= 2) {
				player.getPackets().sendGameMessage(
						"Button ID: " + componentId + "  Button2ID: "
								+ packetId);
			}
			break;
		}
	}
}