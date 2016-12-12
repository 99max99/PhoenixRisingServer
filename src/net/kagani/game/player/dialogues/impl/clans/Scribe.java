package net.kagani.game.player.dialogues.impl.clans;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.item.Item;
import net.kagani.game.player.dialogues.Dialogue;

public class Scribe extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public void start() {
		npcId = (int) parameters[0];
		int itemId = (int) parameters[1];
		if (player.getClanManager() == null) {
			stage = 1;
			sendNPCDialogue(npcId, HAPPY, "Good day, "
					+ (player.getAppearence().isMale() ? "sir" : "madam")
					+ ". How may I be of assistance?");
			return;
		}
		sendNPCDialogue(npcId, NORMAL, "Enjoy your "
				+ ItemDefinitions.getItemDefinitions(itemId).getName()
						.toLowerCase() + "!");
		player.getInventory().addItem(new Item(itemId));
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			stage = 2;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"I'd like to start a clan.",
					"I'd like more information about clans.",
					"Tell me about yourself.", "Goodbye.");
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				stage = 16;
				sendNPCDialogue(
						npcId,
						HAPPY,
						"Very good, "
								+ (player.getAppearence().isMale() ? "sir"
										: "madam")
								+ ". Would you like a demonstration of the process, or do you simply want register?");
				break;
			case OPTION_2:
				stage = 3;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
						"What are the benefits of being in a clan?",
						"How do I join a clan?", "Can I have a clan charter?",
						"More...", "Back...");
				break;
			case OPTION_3:
				stage = 14;
				sendNPCDialogue(npcId, HAPPY,
						"Clan scribe Arnos Twinly at your service, "
								+ (player.getAppearence().isMale() ? "sir"
										: "madam") + ".");
				break;
			case OPTION_4:
				stage = -1;
				sendNPCDialogue(npcId, NORMAL, "Goodbye, "
						+ (player.getAppearence().isMale() ? "sir" : "madam")
						+ ".");
				break;
			}
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				stage = 11;
				sendNPCDialogue(npcId, HAPPY, "I'm so glad you asked me that, "
						+ (player.getAppearence().isMale() ? "sir" : "madam")
						+ ". Allow me to explain.");
				break;
			case OPTION_2:
				stage = 18;
				sendNPCDialogue(npcId, HAPPY,
						"To join a clan you must be invited to do so by one of the clan's recruiters.");
				break;
			case OPTION_3:
				if (!player.getInventory().containsItem(20707, 1)) {
					stage = 10;
					sendNPCDialogue(npcId, HAPPY, "Certainly. Here you are.");
				} else {
					stage = -1;
					sendNPCDialogue(npcId, NORMAL,
							"You already have one in your inventory.");
				}
				break;
			case OPTION_4:
				stage = 4;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
						"What is a clan cloak?", "What is a vexillum?",
						"Back...");
				break;
			case OPTION_5:
				stage = 2;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
						"I'd like to start a clan.",
						"I'd like more information about clans.",
						"Tell me about yourself.", "Goodbye.");
				break;
			}
			break;
		case 4:
			switch (componentId) {
			case OPTION_1:
				stage = 5;
				sendNPCDialogue(
						npcId,
						HAPPY,
						"A clan cloak identifies you as a member of a clan, and displays your clan's motif.");
				break;
			case OPTION_2:
				stage = 6;
				sendNPCDialogue(npcId, HAPPY,
						"A vexillum is a banner that displays your clan's colours and emblem.");
				break;
			case OPTION_3:
				stage = 2;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
						"I'd like to start a clan.",
						"I'd like more information about clans.",
						"Tell me about yourself.", "Goodbye.");
				break;
			}
			break;
		case 5:
			stage = 1;
			sendNPCDialogue(npcId, NORMAL,
					"The Captain of the guard can provide you with one, if you you wish.");
			break;
		case 6:
			stage = 7;
			sendNPCDialogue(npcId, NORMAL,
					"It can be carried, displayed, or used to recruit new clanmates.");
			break;
		case 7:
			stage = 8;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"If you place the vexillum in the ground, others can use it to find out more about your clan, and contact you if they are interested in joining.");
			break;
		case 8:
			stage = 9;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"You can also use the vexillum directly on other players to invite them to join your clan.");
			break;
		case 9:
			stage = 1;
			sendNPCDialogue(npcId, NORMAL,
					"Finally, you can use the vexillum to teleport directly here.");
			break;
		case 10:
			stage = -1;
			sendItemDialogue(20707, "The scribe gives you a charter.");
			break;
		case 11:
			stage = 12;
			sendNPCDialogue(npcId, NORMAL,
					"A clan is a group of players who have banded together for mutal benefit.");
			break;
		case 12:
			stage = 13;
			sendNPCDialogue(npcId, NORMAL,
					"As a member of a clan, you can do things you couldn't do on your own.");
			break;
		case 13:
			stage = -1;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"You can create your very own clan citadel, and decorate it with your clan motif.");
			break;
		case 14:
			stage = 15;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"I keep a record of all clans, and distribute clan charters to those who wish to start a clan.");
			break;
		case 15:
			stage = 1;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"If you are a member of a clan, I can also provide you with a vexillum - a banner displaying your clan's motif.");
			break;
		case 16:
			stage = 17;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"I'd like a demonstration, please.",
					"Just register, thanks.");
			break;
		case 17:
			switch (componentId) {
			case OPTION_1:
				stage = 12;
				sendNPCDialogue(npcId, NORMAL,
						"A clan is a group of players who have banded together for mutal benefit.");
				break;
			case OPTION_2:
				player.getTemporaryAttributtes().put("setclan", Boolean.TRUE);
				player.getPackets().sendInputNameScript(
						"Enter the clan name you'd like to have.");
				end();
				break;
			}
			break;
		case 18:
			stage = 1;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"You can only be a member of one clan at a time, so joining is a serious commitment.");
			break;
		}
	}

	@Override
	public void finish() {

	}
}
