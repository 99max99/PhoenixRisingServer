package net.kagani.game.player.content.slayer;

import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public class SlayerShop {

	/**
	 * @author: Dylan Page
	 */

	public static int INTER = 1308;

	public static void handleButtons(Player player, int componentId) {
		switch (componentId) {
		case 529:
		case 554:
		case 969:
		case 985:
			player.getPackets().sendGameMessage("You have reset your task.");
			player.getSlayerManager().skipCurrentTask(false);
			break;
		case 252:
			if (player.getSlayerManager().getPoints() >= 75) {
				if (player.getInventory().getFreeSlots() < 1) {
					player.getPackets().sendGameMessage(
							"Not enough space in your inventory.");
					return;
				}
				player.getSlayerManager().slayerPoints -= 75;
				player.getPackets().sendGameMessage(
						"You have purchased Ring of Slaying.");
				player.getInventory().addItem(13281, 1);
			} else {
				player.getPackets()
						.sendGameMessage(
								"You do not have enough Slayer Points to purchase Ring of Slaying!");
			}
			break;
		case 131:
			if (player.getSlayerManager().getPoints() >= 400) {
				if (player.getSkills().getXp(18) > 199999999) {
					player.getPackets()
							.sendGameMessage(
									"You can't purchase XP in a skill where you already have maximum amount of XP.");
					return;
				}
				player.getSlayerManager().slayerPoints -= 400;
				player.getPackets().sendGameMessage(
						"You have purchased Slayer XP.");
				player.getSkills().addXp(Skills.SLAYER, 20000);
			} else {
				player.getPackets()
						.sendGameMessage(
								"You do not have enough Slayer Points to purchase Slayer XP!");
			}
			break;
		case 273:
			if (player.getSlayerManager().getPoints() >= 35) {
				if (player.getInventory().getFreeSlots() < 1) {
					player.getPackets().sendGameMessage(
							"Not enough space in your inventory.");
					return;
				}
				player.getSlayerManager().slayerPoints -= 35;
				player.getPackets().sendGameMessage(
						"You have purchased Broad arrows.");
				player.getInventory().addItem(13278, 1000);
			} else {
				player.getPackets()
						.sendGameMessage(
								"You do not have enough Slayer Points to purchase Broad arrows!");
			}
			break;
		case 265:
			if (player.getSlayerManager().getPoints() >= 35) {
				if (player.getInventory().getFreeSlots() < 1) {
					player.getPackets().sendGameMessage(
							"Not enough space in your inventory.");
					return;
				}
				player.getSlayerManager().slayerPoints -= 35;
				player.getPackets().sendGameMessage(
						"You have purchased Broad bolts.");
				player.getInventory().addItem(13279, 1000);
			} else {
				player.getPackets()
						.sendGameMessage(
								"You do not have enough Slayer Points to purchase Broad bolts!");
			}
			break;
		case 143:
		case 147:
			break;
		case 706:
			if (player.getSlayerManager().getPoints() >= 400) {
				if (player.getInventory().getFreeSlots() < 1) {
					player.getPackets().sendGameMessage(
							"Not enough space in your inventory.");
					return;
				}
				player.getSlayerManager().slayerPoints -= 400;
				player.getPackets().sendGameMessage(
						"You have purchased Slayer helmet.");
				player.getInventory().addItem(13263, 1);
			} else {
				player.getPackets()
						.sendGameMessage(
								"You do not have enough Slayer Points to purchase Slayer helmet!");
			}
			break;
		default:
			player.getPackets().sendGameMessage(
					"This feature has not been added.");
			break;
		}
	}

	public static void sendInterface(final Player player) {
		player.getInterfaceManager().sendCentralInterface(INTER);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getPackets().sendIComponentText(INTER, 436,
						"  " + player.getSlayerManager().getPoints());
				player.getPackets().sendIComponentText(INTER, 703,
						"Slayer Helmet");
				player.getPackets()
						.sendIComponentText(INTER, 704, "400 points");
				player.getPackets().sendIComponentText(INTER, 723, "");
				player.getPackets().sendIComponentText(INTER, 724, "");
				player.getPackets().sendIComponentText(INTER, 743, "");
				player.getPackets().sendIComponentText(INTER, 744, "");

				// assignment
				player.getPackets().sendIComponentText(INTER, 438, "");
				player.getPackets().sendIComponentText(INTER, 439, "");
				player.getPackets().sendIComponentText(INTER, 113, "");
				player.getPackets().sendIComponentText(INTER, 460, "");
				stop();
			}
		}, 0, 1);
		player.getPackets().sendHideIComponent(INTER, 145, true);
		player.getPackets().sendHideIComponent(INTER, 149, true);
		player.getPackets().sendHideIComponent(INTER, 726, true);
		player.getPackets().sendHideIComponent(INTER, 746, true);
	}
}