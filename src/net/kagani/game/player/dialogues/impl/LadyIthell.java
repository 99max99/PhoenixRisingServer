package net.kagani.game.player.dialogues.impl;

import net.kagani.cache.loaders.NPCDefinitions;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;
import net.kagani.utils.sql.Gero;

public class LadyIthell extends Dialogue {

	/**
	 * @author: 99max99 M
	 */

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		stage = 1;
		sendNPCDialogue(npcId, ASKING, "Good day, " + player.getDisplayName()
				+ ". Would you like to set up some personal settings for your account?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			stage = 2;
			sendPlayerDialogue(HAPPY, "Sure.");
			break;
		case 2:
			stage = 25;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Lootbeam settings.", "Teleport animations.", "Rest animations.",
					"Custom title.", "More.");
			break;
		case 40:
			switch (componentId) {
			case OPTION_1:
				stage = -1;
				sendDialogue("You have successfully set lootbeam.");
				player.rainbow = player.rainbow ? false : true;
				break;
			case OPTION_2:
				stage = 26;
				sendDialogue(
						"Note: Prices are straight from the cache, meaning prices has been set as default value by Jagex.",
						"", "Your current minimum value is " + Utils.format(player.getLootbeamAmount()) + " gp.");
				break;
			case OPTION_3:
				end();
				break;
			}
			break;
		case 25:
			switch (componentId) {
			case OPTION_1:
				stage = 40;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, player.rainbow ? "Normal lootbeam." : "Rainbow lootbeam.",
						"Set lootbeam minimum value.", "Nevermind.");
				break;
			case OPTION_2:
				stage = 15;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Set normal teleport animation.",
						"Set pegasus teleport animation.", "Set gnome teleport animation.",
						"Set assasian teleport animation.", "More.");
				break;
			case OPTION_3:
				stage = 45;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Set normal resting animation.", "Set arcane animation.",
						"Set zen resting animation.", "Nevermind.");
				break;
			case OPTION_4:
				if (player.isPlatinumMember() || player.isDiamondMember()) {
					stage = 56;
					if (!player.getCustomTitleActive())
						sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Set a custom title.", "Set a custom title color.",
								"Activate custom title.", "Capitalize custom title.", "Nevermind.");
					else
						sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Set a custom title.", "Set a custom title color.",
								"Deactivate custom title.", "Capitalize custom title.", "Nevermind.");
				} else {
					stage = -1;
					sendNPCDialogue(npcId, SAD, "Sorry, but you are not a platinum member.");
				}
				break;
			case OPTION_5:
				stage = 55;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Membership.", "Secure Code.", "Nevermind.");
				break;
			}
			break;
		case 55:
			switch (componentId) {
			case OPTION_1:
				stage = 60;
				sendPlayerDialogue(QUESTIONS, "Can I claim membership that I had in MaxScape830 1.0?");
				break;
			case OPTION_2:
				stage = 65;
				if (player.getSecureCode() == 0) {
					sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
							player.getSecureCode() == 0 ? "Set a Secure Code." : "Change Secure Code.",
							"What is a Secure Code?", "Nevermind.");
				} else {
					sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
							player.getSecureCode() == 0 ? "Set a Secure Code." : "Change Secure Code.",
							"Remove Secure Code.", "What is a Secure Code?", "Nevermind.");
				}
				break;
			case OPTION_3:
				end();
				break;
			}
			break;
		case 65:
			switch (componentId) {
			case OPTION_1:
				player.getTemporaryAttributtes().put("SetSecureCode", 0);
				player.getPackets().sendInputIntegerScript(player.getSecureCode() == 0
						? "Please enter your new Secure Code:" : "Please enter your current Secure Code:");
				end();
				break;
			case OPTION_2:
				if (player.getSecureCode() == 0) {
					stage = 66;
					sendNPCDialogue(npcId, HAPPY,
							"A Secure Code, will help to protect your account from hackers. I strongly recommend setting one.<br>Everytime you attempt to login, a prompt will force you to enter the secure code.");
				} else {
					player.getTemporaryAttributtes().put("DeleteSecureCode", 0);
					player.getPackets().sendInputIntegerScript("Please enter your current Secure Code:");
					end();
				}
				break;
			case OPTION_3:
				if (player.getSecureCode() != 0) {
					stage = 66;
					sendNPCDialogue(npcId, HAPPY,
							"A Secure Code, will help to protect your account from hackers. I strongly recommend setting one.<br>Everytime you attempt to login, a prompt will force you to enter the secure code.");
				} else
					end();
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		case 66:
			stage = -1;
			sendNPCDialogue(npcId, HAPPY,
					"If you type the Secure Code wrong 5 times, an administrator will be noticed, and your account will automatically be locked.");
			break;
		case 60:
			stage = 61;
			sendNPCDialogue(npcId, HAPPY, "Ofcourse... Let me check if you have any.");
			break;
		case 61:
			player.MaxScape830V1Membership = Gero.getMember(player.getUsername());
			int getRank = getMember(player);
			if (getRank != player.MaxScape830V1Membership) {
				if (player.MaxScape830V1Membership == 0) {
					stage = -1;
					sendNPCDialogue(npcId, NORMAL,
							"Sorry, you don't seem to have membership to claim from MaxScape830 1.0.");
					return;
				}
				stage = 62;
				sendNPCDialogue(npcId, HAPPY, "You have " + getName() + " Membership to claim.");
			} else {
				stage = -1;
				sendNPCDialogue(npcId, NORMAL, "Sorry, you don't seem to have membership to claim from MaxScape830 1.0.");
			}
			break;
		case 62:
			stage = -1;
			setMember();
			sendNPCDialogue(npcId, HAPPY, "There you go, you are now a " + player.getMemberTitle() + ".");
			break;
		case 56:
			switch (componentId) {
			case OPTION_1:
				if (player.isPlatinumMember() || player.isDiamondMember()) {
					player.getPackets().sendInputLongTextScript("Please enter your new custom title:");
					player.getTemporaryAttributtes().put("customTitle", Boolean.TRUE);
					end();
				} else {
					stage = -1;
					sendNPCDialogue(npcId, SAD, "Sorry, but you are not a platinum member.");
				}
				break;
			case OPTION_2:
				if (player.isPlatinumMember() || player.isDiamondMember()) {
					stage = 57;
					sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Set to blue.", "Set to red.", "Set to purple.",
							"Set to black.", "Set to yellow.");
				} else {
					stage = -1;
					sendNPCDialogue(npcId, SAD, "Sorry, but you are not a platinum member.");
				}
				break;
			case OPTION_3:
				if (player.isPlatinumMember() || player.isDiamondMember()) {
					if (player.getCustomTitleActive() == true) {
						stage = -1;
						player.setCustomTitleActive(false);
						sendDialogue("Your custom title has been deactivated.");
						player.getAppearence().generateAppearenceData();
					} else if (player.getCustomTitleActive() == false) {
						stage = -1;
						player.setCustomTitleActive(true);
						sendDialogue("Your custom title has been activated.");
						player.getAppearence().generateAppearenceData();
					}
				} else {
					stage = -1;
					sendNPCDialogue(npcId, SAD, "Sorry, but you are not a platinum member.");
				}
				break;
			case OPTION_4:
				stage = -1;
				if (player.getCustomTitleCapitalize() == false) {
					player.setCustomTitleCapitalize(true);
					sendDialogue("You have enabled capitalizing of your custom title.");
				} else {
					player.setCustomTitleCapitalize(false);
					sendDialogue("You have disabled capitalizing of your custom title.");
				}
				player.getAppearence().generateAppearenceData();
				break;
			case OPTION_5:
				end();
				break;
			}
			break;
		case 57:
			switch (componentId) {
			case OPTION_1:
				stage = -1;
				player.setCustomTitleColor("0000ff");
				sendDialogue("You have set custom title to '<col=" + player.getCustomTitleColor() + ">"
						+ player.getCustomTitle() + "</col>'.");
				player.getAppearence().generateAppearenceData();
				break;
			case OPTION_2:
				stage = -1;
				player.setCustomTitleColor("ff0000");
				sendDialogue("You have set custom title to '<col=" + player.getCustomTitleColor() + ">"
						+ player.getCustomTitle() + "</col>'.");
				player.getAppearence().generateAppearenceData();
				break;
			case OPTION_3:
				stage = -1;
				player.setCustomTitleColor("551A8B");
				sendDialogue("You have set custom title to '<col=" + player.getCustomTitleColor() + ">"
						+ player.getCustomTitle() + "</col>'.");
				player.getAppearence().generateAppearenceData();
				break;
			case OPTION_4:
				stage = -1;
				player.setCustomTitleColor("000000");
				sendDialogue("You have set custom title to '<col=" + player.getCustomTitleColor() + ">"
						+ player.getCustomTitle() + "</col>'.");
				player.getAppearence().generateAppearenceData();
				break;
			case OPTION_5:
				stage = -1;
				player.setCustomTitleColor("ffff00");
				sendDialogue("You have set custom title to '<col=" + player.getCustomTitleColor() + ">"
						+ player.getCustomTitle() + "</col>'.");
				player.getAppearence().generateAppearenceData();
				break;
			}
			break;
		case 15:
			switch (componentId) {
			case OPTION_1:
				stage = -1;
				sendDialogue("You have successfully set teleport animation.");
				player.teleportType = "";
				break;
			case OPTION_2:
				stage = -1;
				sendDialogue("You have successfully set teleport animation.");
				player.teleportType = "pegasus";
				break;
			case OPTION_3:
				stage = -1;
				sendDialogue("You have successfully set teleport animation.");
				player.teleportType = "gnome";
				break;
			case OPTION_4:
				stage = -1;
				sendDialogue("You have successfully set teleport animation.");
				player.teleportType = "assasian";
				break;
			case OPTION_5:
				stage = 16;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Set demon teleport animation.",
						"Set sky teleport animation.", "Nevermind.");
				break;
			}
			break;
		case 16:
			switch (componentId) {
			case OPTION_1:
				stage = -1;
				sendDialogue("You have successfully set teleport animation.");
				player.teleportType = "demon";
				break;
			case OPTION_2:
				stage = -1;
				sendDialogue("You have successfully set teleport animation.");
				player.teleportType = "sky";
				break;
			case OPTION_3:
				end();
				break;
			}
			break;
		case 26:
			player.getPackets()
					.sendInputLongTextScript("Please enter minimum amount a lootbeam should appear in coins:");
			player.getTemporaryAttributtes().put("lootbeamMinValue", Boolean.TRUE);
			end();
			break;
		case 45:
			switch (componentId) {
			case OPTION_1:
				stage = -1;
				sendDialogue("You have successfully set resting animation.");
				player.setRestAnimation(1);
				break;
			case OPTION_2:
				stage = -1;
				sendDialogue("You have successfully set resting animation.");
				player.setRestAnimation(2);
				break;
			case OPTION_3:
				stage = -1;
				sendDialogue("You have successfully set resting animation.");
				player.setRestAnimation(3);
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		}
	}

	private int getMember(Player player) {
		if (!player.isAMember())
			return 0;
		else if (player.isBronzeMember())
			return 1;
		else if (player.isSilverMember())
			return 2;
		else if (player.isGoldMember())
			return 3;
		else if (player.isPlatinumMember())
			return 4;
		else if (player.isDiamondMember())
			return 5;
		return 0;
	}

	private void setMember() {
		switch (player.MaxScape830V1Membership) {
		case 1:
			player.setBronzeMember(true);
			player.setSilverMember(false);
			player.setGoldMember(false);
			player.setPlatinumMember(false);
			player.setDiamondMember(false);
			break;
		case 2:
			player.setBronzeMember(false);
			player.setSilverMember(true);
			player.setGoldMember(false);
			player.setPlatinumMember(false);
			player.setDiamondMember(false);
			break;
		case 3:
			player.setBronzeMember(false);
			player.setSilverMember(false);
			player.setGoldMember(true);
			player.setPlatinumMember(false);
			player.setDiamondMember(false);
			break;
		case 4:
			player.setBronzeMember(false);
			player.setSilverMember(false);
			player.setGoldMember(false);
			player.setPlatinumMember(true);
			player.setDiamondMember(false);
			break;
		case 5:
			player.setBronzeMember(false);
			player.setSilverMember(false);
			player.setGoldMember(false);
			player.setPlatinumMember(false);
			player.setDiamondMember(true);
			break;
		}
		player.setNextGraphics(new Graphics(1765));
		World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName() + " has bought " + getName()
				+ " Membership. Thank-you!", false);
		player.getPackets().sendGameMessage("Thank you, your order has been delivered.");
	}

	private String getName() {
		switch (player.MaxScape830V1Membership) {
		case 1:
			return "Bronze";
		case 2:
			return "Silver";
		case 3:
			return "Gold";
		case 4:
			return "Platinum";
		case 5:
			return "Diamond";
		}
		return null;
	}

	@Override
	public void finish() {

	}
}