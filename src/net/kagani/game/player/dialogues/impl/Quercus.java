package net.kagani.game.player.dialogues.impl;

import net.kagani.game.minigames.warbands.Warbands;
import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;

public class Quercus extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	private int option = 1;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		option = (int) parameters[1];
		switch (option) {
		case 2:
			stage = 15;
			sendPlayerDialogue(NORMAL, "I'd like to turn in my supplies.");
			break;
		case 1:
		default:
			stage = 1;
			sendPlayerDialogue(NORMAL, "Hello.");
			break;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			stage = 2;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Hmm? Oh, pardon me, human. I was tracking the movements of warbands out in the Wilderness.");
			break;
		case 2:
			stage = 3;
			sendOptionsDialogue("What would you like to ask?",
					"Warbands? What do you mean?", "Talk about current event.",
					"Talk about rewards.", "Nothing, actually.");
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				stage = 4;
				sendNPCDialogue(
						npcId,
						HAPPY,
						"Warbands are a wilderness activity that occur hourly, here at Hyperion. The objective of the minigame is to siphon the obelisk in the center of the camp undetected, then once having done so, loot the resources from within the camp.");
				break;
			case OPTION_2:
				stage = 2;
				sendNPCDialogue(
						npcId,
						NORMAL,
						"I sense a camp located somewhere in between levels "
								+ Warbands
										.getWildLevel(Warbands.warband.base[2])
								+ " and "
								+ Warbands
										.getWildLevel(Warbands.warband.base[3])
								+ " of the wilderness.");
				break;
			case OPTION_3:
				stage = 8;
				sendOptionsDialogue("Talk about rewards",
						"Tell me about rewards.",
						"I'd like to turn in my supplies.");
				break;
			case OPTION_4:
				stage = -1;
				sendNPCDialogue(npcId, NORMAL, "Maintain the balance, "
						+ player.getDisplayName() + ".");
				break;
			}
			break;
		case 4:
			stage = 5;
			sendNPCDialogue(
					npcId,
					HAPPY,
					"They are becoming a threat to the balance of this world, and I am sending adventurers to dispatch them.");
			break;
		case 5:
			stage = 6;
			sendPlayerChat(ASKING,
					"You look pretty tough. Couldn't you deal with them youself?");
			break;
		case 6:
			stage = 7;
			sendNPCDialogue(
					npcId,
					NONONO_FACE,
					"In keeping with the teachings of Guthix, I cannot intervene directly. Rather, I seek to influence events by introduction of a balancing force.");
			break;
		case 7:
			stage = 2;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"I have the Sight, which allows me to cast my gaze into the Wilderness, spotting any warbands that may be gathering.");
			break;
		case 8:
			switch (componentId) {
			case OPTION_1:
				stage = 9;
				sendNPCDialogue(
						npcId,
						NORMAL,
						"I sense turmoil and strife on the horizon, and it would behove us all to seek a balance of powers.");
				break;
			case OPTION_2:
				stage = 15;
				sendPlayerDialogue(NORMAL, "I'd like to turn in my supplies.");
				break;
			}
			break;
		case 9:
			stage = 10;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"If you find the camps I sense and bring me back the materials they are hoarding, I shall reward you greatly.");
			break;
		case 10:
			stage = 11;
			sendPlayerDialogue(ASKING,
					"And I can find these rewards in the Wilderness?");
			break;
		case 11:
			stage = 2;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Correct. I can sense the camps, but cannot intervene directly. If you are willing to do so in my place, just return here with the spoils should you make it out alive.");
			break;
		case 15:
			stage = 12;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Turn into gold.",
					"Turn into experience.");
			break;
		case 12:
			int itemId = -1;
			int amount = -1;
			int skill = -1;
			int xp = -1;
			if (player.getInventory().containsItem(27637, 1)
					|| player.getInventory().containsItem(27636, 1)
					|| player.getInventory().containsItem(27639, 1)
					|| player.getInventory().containsItem(27640, 1)
					|| player.getInventory().containsItem(27638, 1)) {
				if (player.getInventory().containsItem(27637, 1)) {
					itemId = 27637;
					skill = 15;
				} else if (player.getInventory().containsItem(27636, 1)) {
					itemId = 27636;
					skill = 22;
				} else if (player.getInventory().containsItem(27639, 1)) {
					itemId = 27639;
					skill = 19;
				} else if (player.getInventory().containsItem(27640, 1)) {
					itemId = 27640;
					skill = 14;
				} else if (player.getInventory().containsItem(27638, 1)) {
					itemId = 27638;
					skill = 13;
				}
				amount = player.getInventory().getAmountOf(itemId);
				xp = getExpModifier(player, skill) * amount;
				player.getInventory().deleteItem(itemId, amount);
				switch (componentId) {
				case OPTION_1:
					player.getMoneyPouch().setAmount(8000 * amount, false);
					break;
				case OPTION_2:
					player.getSkills().addXp(skill, xp, true);
					player.getSkills().refresh(skill);
					break;
				}
				player.getAppearence().generateAppearenceData();
			}
			if (amount > 0) {
				stage = -1;
				sendNPCDialogue(
						npcId,
						HAPPY,
						componentId == OPTION_1 ? "Your supplies have been converted into "
								+ Utils.format(8000 * amount) + " gold."
								: "Your supplies have been converted into xp.");
			} else {
				stage = -1;
				sendNPCDialogue(npcId, SAD,
						"You don't have any supplies to return to me.");
			}
			break;
		}
	}

	private static final int getExpModifier(Player player, int skill) {
		int level = player.getSkills().getLevelForXp(skill);
		return (int) (((level * (level - 2) / 2) + 50)) * 2;
	}

	@Override
	public void finish() {

	}
}