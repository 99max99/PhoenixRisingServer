package net.kagani.game.player.dialogues.impl;

import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.player.content.Bonds;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;
import net.kagani.utils.sql.Gero;

public class BondsD extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public void start() {
		stage = 1;
		sendOptionsDialogue("Convert into what?",
				"Convert into " + Utils.format(Bonds.getValue()) + " gp.",
				"Membership options.", "Treasure Hunter keys.",
				"Display Name change.", "Nothing.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				stage = 2;
				sendDialogue("Are you sure? You can sell a Bond to other players for more gp?");
				break;
			case OPTION_2:
				stage = 4;
				int needed0 = player.isDiamondMember() ? 0 : player
						.isPlatinumMember() ? 0 : player.isGoldMember() ? 0
						: player.isGoldMember() ? 0
								: player.isSilverMember() ? 0 : player
										.isBronzeMember() ? 0 : 2;
				int needed1 = player.isDiamondMember() ? 0 : player
						.isPlatinumMember() ? 0 : player.isGoldMember() ? 0
						: player.isGoldMember() ? 0
								: player.isSilverMember() ? 0 : player
										.isBronzeMember() ? 3 : 5;
				int needed2 = player.isDiamondMember() ? 0 : player
						.isPlatinumMember() ? 0 : player.isGoldMember() ? 0
						: player.isSilverMember() ? 5
								: player.isBronzeMember() ? 8 : 10;
				int needed3 = player.isDiamondMember() ? 0 : player
						.isPlatinumMember() ? 0 : player.isGoldMember() ? 10
						: player.isSilverMember() ? 15 : player
								.isBronzeMember() ? 18 : 20;
				int needed4 = player.isDiamondMember() ? 0 : player
						.isPlatinumMember() ? 10 : player.isGoldMember() ? 20
						: player.isSilverMember() ? 25 : player
								.isBronzeMember() ? 28 : 30;
				sendOptionsDialogue("Select what type of membership",
						"Bronze Membership - " + needed0 + " Bonds.",
						"Silver Membership - " + needed1 + " Bonds.",
						"Gold Membership - " + needed2 + " Bonds.",
						"Platinum Membership - " + needed3 + " Bonds.",
						"Diamond Membership - " + needed4 + " Bonds.");
				break;
			case OPTION_3:
				stage = -1;
				sendDialogue("We do not support this at the moment.");
				break;
			case OPTION_4:
				stage = -1;
				sendDialogue("We do not support this at the moment.");
				break;
			case OPTION_5:
				end();
				break;
			}
			break;
		case 2:
			stage = 3;
			sendOptionsDialogue(
					"Are you sure? You can sell a Bond to other players for more gp?",
					"Yes, convert into " + Utils.format(Bonds.getValue())
							+ " gp.", "No, stop.");
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				player.getInterfaceManager().sendCentralInterface(1255);
				player.getInterfaceManager().removeDialogueInterface();
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						if (player.getInventory().containsItem(29492, 1)) {
							player.getInventory().deleteItem(29492, 1);
							player.getBank().addItem(995, Bonds.getValue(),
									true);
							stage = -1;
							sendDialogue("Congratulations! The "
									+ Utils.format(Bonds.getValue())
									+ " gp has been placed in your bank.");
							player.getInterfaceManager()
									.removeCentralInterface();
							stop();
						} else {
							stage = -1;
							sendDialogue("You don't have enough bonds.");
							stop();
						}
						player.getInterfaceManager().removeCentralInterface();
						stop();
					}
				}, 2, 0);
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		case 4:
			switch (componentId) {
			case OPTION_1:
				giveMembership(0);
				end();
				break;
			case OPTION_2:
				giveMembership(1);
				end();
				break;
			case OPTION_3:
				giveMembership(2);
				end();
				break;
			case OPTION_4:
				giveMembership(3);
				end();
				break;
			case OPTION_5:
				giveMembership(4);
				end();
				break;
			}
			break;
		}
	}

	public void giveMembership(final int type) {
		player.getInterfaceManager().sendCentralInterface(1255);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				switch (type) {
				case 0:
					if (player.getInventory().containsItem(29492, 2)) {
						if (player.isAMember()) {
							stage = -1;
							player.getDialogueManager().startDialogue(
									"SimpleMessage",
									"You are already a "
											+ player.getMemberTitle() + ".");
							player.getInterfaceManager()
									.removeCentralInterface();
							stop();
							return;
						}
						player.getInventory().deleteItem(29492, 2);
						player.setBronzeMember(true);
						player.setSilverMember(false);
						player.setGoldMember(false);
						player.setNextGraphics(new Graphics(1765));
						stage = -1;
						player.getDialogueManager()
								.startDialogue("SimpleMessage",
										"Congratulations! You have bought Bronze Membership!");
						World.sendWorldMessage(
								"<col=FF0000><img=5>" + player.getDisplayName()
										+ " has bought "
										+ player.getMemberTitle()
										+ "ship. Thank-you!", false);
						Gero.setMember(player.getUsername(), 8, true);
					} else {
						player.getDialogueManager().startDialogue(
								"SimpleMessage", " 2 bonds.");
						player.getInterfaceManager().removeCentralInterface();
						stop();
					}
					break;
				case 1:
					int needed = player.isBronzeMember() ? 3 : 5;
					if (player.getInventory().containsItem(29492, needed)) {
						if (player.isSilverMember()
								|| player.isAMemberGreaterThanGold()) {
							stage = -1;
							player.getDialogueManager().startDialogue(
									"SimpleMessage",
									"You are already a "
											+ player.getMemberTitle() + ".");
							player.getInterfaceManager()
									.removeCentralInterface();
							stop();
							return;
						}
						player.getInventory().deleteItem(29492, needed);
						player.setBronzeMember(false);
						player.setSilverMember(true);
						player.setGoldMember(false);
						player.setNextGraphics(new Graphics(1765));
						player.getDialogueManager()
								.startDialogue("SimpleMessage",
										"Congratulations! You have bought Silver Membership!");
						World.sendWorldMessage(
								"<col=FF0000><img=5>" + player.getDisplayName()
										+ " has bought "
										+ player.getMemberTitle()
										+ "ship. Thank-you!", false);
						Gero.setMember(player.getUsername(), 9, true);
					} else {
						player.getDialogueManager().startDialogue(
								"SimpleMessage",
								"You need total of " + needed + " bonds.");
						player.getInterfaceManager().removeCentralInterface();
						stop();
					}
					break;
				case 2:
					int needed2 = player.isSilverMember() ? 5 : player
							.isBronzeMember() ? 8 : 10;
					if (player.getInventory().containsItem(29492, needed2)) {
						if (player.isAMemberGreaterThanGold()) {
							stage = -1;
							player.getDialogueManager().startDialogue(
									"SimpleMessage",
									"You are already a "
											+ player.getMemberTitle() + ".");
							player.getInterfaceManager()
									.removeCentralInterface();
							stop();
							return;
						}
						player.getInventory().deleteItem(29492, needed2);
						player.setBronzeMember(false);
						player.setSilverMember(false);
						player.setGoldMember(true);
						player.setNextGraphics(new Graphics(1765));
						stage = -1;
						player.getDialogueManager()
								.startDialogue("SimpleMessage",
										"Congratulations! You have bought Gold Membership!");
						World.sendWorldMessage(
								"<col=FF0000><img=5>" + player.getDisplayName()
										+ " has bought "
										+ player.getMemberTitle()
										+ "ship. Thank-you!", false);
						Gero.setMember(player.getUsername(), 10, true);
					} else {
						player.getDialogueManager().startDialogue(
								"SimpleMessage",
								"You need total of " + needed2 + " bonds.");
						player.getInterfaceManager().removeCentralInterface();
						stop();
					}
					break;
				case 3:
					int needed3 = player.isGoldMember() ? 10 : player
							.isSilverMember() ? 15
							: player.isBronzeMember() ? 18 : 20;
					if (player.getInventory().containsItem(29492, needed3)) {
						if (player.isPlatinumMember()
								|| player.isDiamondMember()) {
							stage = -1;
							player.getDialogueManager().startDialogue(
									"SimpleMessage",
									"You are already a "
											+ player.getMemberTitle() + ".");
							player.getInterfaceManager()
									.removeCentralInterface();
							stop();
							return;
						}
						player.getInventory().deleteItem(29492, needed3);
						player.setBronzeMember(false);
						player.setSilverMember(false);
						player.setGoldMember(false);
						player.setPlatinumMember(true);
						player.setDiamondMember(false);
						player.setNextGraphics(new Graphics(1765));
						stage = -1;
						player.getDialogueManager()
								.startDialogue("SimpleMessage",
										"Congratulations! You have bought Platinum Membership!");
						World.sendWorldMessage(
								"<col=FF0000><img=5>" + player.getDisplayName()
										+ " has bought "
										+ player.getMemberTitle()
										+ "ship. Thank-you!", false);
						Gero.setMember(player.getUsername(), 15, true);
					} else {
						player.getDialogueManager().startDialogue(
								"SimpleMessage",
								"You need total of " + needed3 + " bonds.");
						player.getInterfaceManager().removeCentralInterface();
						stop();
					}
					break;
				case 4:
					int needed4 = player.isPlatinumMember() ? 10 : player
							.isGoldMember() ? 20 : player.isSilverMember() ? 25
							: player.isBronzeMember() ? 28 : 30;
					if (player.getInventory().containsItem(29492, needed4)) {
						if (player.isDiamondMember()) {
							stage = -1;
							player.getDialogueManager().startDialogue(
									"SimpleMessage",
									"You are already a "
											+ player.getMemberTitle() + ".");
							player.getInterfaceManager()
									.removeCentralInterface();
							stop();
							return;
						}
						player.getInventory().deleteItem(29492, needed4);
						player.setBronzeMember(false);
						player.setSilverMember(false);
						player.setGoldMember(false);
						player.setPlatinumMember(false);
						player.setDiamondMember(true);
						player.setNextGraphics(new Graphics(1765));
						stage = -1;
						player.getDialogueManager()
								.startDialogue("SimpleMessage",
										"Congratulations! You have bought Diamond Membership!");
						World.sendWorldMessage(
								"<col=FF0000><img=5>" + player.getDisplayName()
										+ " has bought "
										+ player.getMemberTitle()
										+ "ship. Thank-you!", false);
						Gero.setMember(player.getUsername(), 16, true);
					} else {
						if (player.isAMember())
							player.getDialogueManager().startDialogue(
									"SimpleMessage",
									"You need total of " + needed4 + " bonds.");
						else
							player.getDialogueManager()
									.startDialogue(
											"SimpleMessage",
											"You need total of "
													+ needed4
													+ " bonds.<br><col=FF0000>You would need to use your two bonds to purchase Bronze Membership, and then upgrade to Diamond Membership.");
						player.getInterfaceManager().removeCentralInterface();
						stop();
					}
					break;
				}
				player.getInterfaceManager().removeCentralInterface();
				stop();
			}
		}, 2, 0);
	}

	@Override
	public void finish() {

	}
}