package net.kagani.game.player.dialogues.impl.cities.edgeville;

import net.kagani.game.player.dialogues.Dialogue;

public class MrEx extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public void start() {
		npcId = (int) parameters[0];
		if (player.isHardcoreIronman()) {
			stage = 1;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Wilderness hats.",
					"Hardcore mode.", "Ironman mode.", "Skull me.");
		} else if (player.isIronman()) {
			stage = 15;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Wilderness hats.",
					"Ironman mode.", "Skull me.");
		} else {
			stage = 25;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Wilderness hats.",
					"Skull me.");
		}
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
				end();
				break;
			case OPTION_2:
				stage = 2;
				sendNPCDialogue(
						npcId,
						SAD,
						"The gods appear to be turning a blind eye to you. Have you angered them, or was this a path of your choosing.");
				break;
			case OPTION_3:
				end();
				break;
			case OPTION_4:
				if (!player.hasSkull()) {
					player.setWildernessSkull();
					player.getPackets().sendGameMessage(
							"You have been skulled.");
				} else {
					player.getPackets().sendGameMessage(
							"You are already skulled.");
				}
				end();
				break;
			}
			break;
		case 15:
			switch (componentId) {
			case OPTION_1:
				end();
				break;
			case OPTION_2:
				end();
				break;
			case OPTION_3:
				if (!player.hasSkull()) {
					player.setWildernessSkull();
					player.getPackets().sendGameMessage(
							"You have been skulled.");
				} else {
					player.getPackets().sendGameMessage(
							"You are already skulled.");
				}
				end();
				break;
			}
			break;
		case 25:
			switch (componentId) {
			case OPTION_1:
				end();
				break;
			case OPTION_2:
				if (!player.hasSkull()) {
					player.setWildernessSkull();
					player.getPackets().sendGameMessage(
							"You have been skulled.");
				} else {
					player.getPackets().sendGameMessage(
							"You are already skulled.");
				}
				end();
				break;
			}
			break;
		case 2:
			stage = 3;
			sendPlayerDialogue(SAD,
					"That's why I'm here. I've heard you may be able to help me.");
			break;
		case 3:
			stage = 4;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Jar of divine light.",
					"Divine coin.", "Never disable Hardcore Ironman mode.");
			break;
		case 4:
			switch (componentId) {
			case OPTION_1:
				stage = 5;
				sendNPCDialogue(
						npcId,
						NORMAL,
						"Well, I did find this handy jar of light, I could sell it to you for - say - 100,000 coins?");
				break;
			case OPTION_2:
				stage = 20;
				sendNPCDialogue(
						npcId,
						NORMAL,
						"I found this coin. It seems to be exuding divine power. I'd be willing to sell it to you... for a price.");
				break;
			case OPTION_3:
				player.getPackets().sendGameMessage("Under development.");
				end();
				break;
			}
			break;
		case 5:
			stage = 6;
			sendNPCDialogue(
					npcId,
					HAPPY,
					"You can either open it now, or wait until you die. I'll be able to open it for you to save you, even from within your bank!");
			break;
		case 6:
			stage = 7;
			sendDialogue("The jar of divine light, when used will disable Hardcore mode. This cannot be undone once used.");
			break;
		case 7:
			stage = 8;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "That's great, thanks!",
					"No thanks.");
			break;
		case 8:
			switch (componentId) {
			case OPTION_1:
				stage = 9;
				sendNPCDialogue(npcId, HAPPY, "Excellent, let's see...");
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		case 9:
			if (player.getInventory().getFreeSlots() < 1) {
				player.getPackets().sendGameMessage(
						"Not enough space in your inventory.");
				end();
				return;
			}
			if (player.getSkills().getTotalLevel() < 1000) {
				player.getPackets().sendGameMessage(
						"You need a total level of at least 1000.");
				end();
				return;
			}
			if (player.hasMoney(100000)) {
				stage = -1;
				player.takeMoney(100000);
				sendItemDialogue(32335,
						"Mr Ex hands you the jar. It's slightly warm to the touch.");
				player.getInventory().addItem(32335, 1);
			} else {
				stage = -1;
				sendNPCDialogue(npcId, NORMAL, "You need 100,000 coins.");
			}
			break;
		case 10:

			break;
		case 20:
			if (player.getDivineCoin() == false) {
				stage = 21;
				sendNPCDialogue(npcId, HAPPY,
						"Yes, I think 1,000,000 coins will do nicely.");
			} else {
				stage = -1;
				sendNPCDialogue(npcId, NORMAL,
						"You already have a Divine coin.");
			}
			break;
		case 21:
			stage = 22;
			sendNPCDialogue(npcId, NORMAL,
					"You will need to take it with you, wherever you go. Keep it concealed.");
			break;
		case 22:
			stage = 23;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Should you fail, present the coin, to whomever greets you. Meet their gaze without fear, but say nothing... and you shall live once again.");
			break;
		case 23:
			stage = 24;
			sendDialogue("This coin will grant you 1 extra Death in Hardcore mode.");
			break;
		case 24:
			stage = 26;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "That's great, thanks!",
					"No thanks.");
			break;
		case 26:
			switch (componentId) {
			case OPTION_1:
				stage = 27;
				sendNPCDialogue(npcId, HAPPY, "Excellent. Let's see...");
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		case 27:
			if (player.getInventory().getFreeSlots() < 1) {
				player.getPackets().sendGameMessage(
						"Not enough space in your inventory.");
				end();
				return;
			}
			if (player.getSkills().getTotalLevel() < 1000) {
				player.getPackets().sendGameMessage(
						"You need a total level of at least 1000.");
				end();
				return;
			}
			if (player.hasMoney(1000000)) {
				stage = -1;
				player.takeMoney(1000000);
				sendItemDialogue(995,
						"Mr Ex hands you the coin. You quickly stow it away.");
				player.setDivineCoin(true);
			} else {
				stage = -1;
				sendNPCDialogue(npcId, NORMAL, "You need 1,000,000 coins.");
			}
			break;
		}
	}

	private int getValue() {
		switch (player.getMoneyPouch().getCoinsAmount()) {
		case 1:
			return 5;
		}
		return 1;
	}

	@Override
	public void finish() {

	}
}