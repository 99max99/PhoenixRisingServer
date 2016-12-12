package net.kagani.game.player.dialogues.impl.cities.falador;

import net.kagani.game.player.Skills;
import net.kagani.game.player.dialogues.Dialogue;

/**
 * The class that represents the dialogue for the NPC - Aksel
 * 
 * @author Mod Austin
 * @version 1.0 3/5/2015
 * @contact@deviouscoding@gmail.com
 */

public class Aksel extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING,
				"Welcome. What brings you to the home of the artisans?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "What should I do here?",
					"What's an artisan?", "Tell me more about yourself.",
					"I've got to go.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "What should I do here?");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY, "What's an artisan?");
				stage = 2;
				break;
			case OPTION_3:
				sendNPCDialogue(
						npcId,
						HAPPY,
						"I built the furnace that rather dominates this room. Took",
						"me four and a half years; carved the rock myself. If you",
						"need any pointers on how to use it, let me know.");
				stage = 11;
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		case 1:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"This is a Smithing workshop where you can do all kinds of Smithing related tasks.");
			stage = -1;
			break;
		case 2:
			sendNPCDialogue(npcId, HAPPY,
					"Artisans are a collective of skilled smiths who work",
					"towards upholding quality as their principle ethos.",
					"Smithing can be seen as a coarse, unloving",
					"undertaking...bashing out platebodies as quickly as you");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(npcId, HAPPY, "can. It's not like that here.");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, HAPPY,
					"Recently, we've opened our doors to humans. Some of our",
					"stauncher members aren't happy about this, but if you",
					"work hard I'm sure you'll earn our respect.");
			stage = 5;
			break;
		case 5:
			sendOptionsDialogue(DEFAULT, "Earn your respect?",
					"What should I do here?", "Tell me more about yourself.",
					"I've got to go.");
			stage = 6;
			break;
		case 6:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "Earn your respect?");
				stage = 7;
				break;
			case OPTION_2:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"This is a Smithing workshop where you can do all kinds of Smithing related tasks.");
				stage = -1;
				break;
			case OPTION_3:
				sendNPCDialogue(
						npcId,
						HAPPY,
						"I built the furnace that rather dominates this room. Took",
						"me four and a half years; carved the rock myself. If you",
						"need any pointers on how to use it, let me know.");
				stage = 11;
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		case 7:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Working in this workshop will show you're a dedicated",
					"smith. Elof, who skulks in his shop downstairs, is in charge",
					"of rewarding those who we feel have earned it.");
			stage = 8;
			break;
		case 8:
			sendNPCDialogue(npcId, HAPPY,
					"Speak with him if you wish to learn more.");
			stage = 9;
			break;
		case 9:
			sendOptionsDialogue(DEFAULT, "What should I do here?",
					"Tell me more about yourself.", "I've go to go.");
			stage = 10;
			break;
		case 10:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"This is a Smithing workshop where you can do all kinds of Smithing related tasks.");
				stage = -1;
				break;
			case OPTION_2:
				sendNPCDialogue(
						npcId,
						HAPPY,
						"I built the furnace that rather dominates this room. Took",
						"me four and a half years; carved the rock myself. If you",
						"need any pointers on how to use it, let me know.");
				stage = 11;
				break;
			case OPTION_3:
				end();
				break;
			}
			break;
		case 11:
			sendNPCDialogue(npcId, SAD,
					"I don't do much actual smithing anymore, I mainly potter",
					"around the workshop, helping where I can and making sure",
					"Suak and Elof aren't being too mean to the humans.");
			stage = 12;
			break;
		case 12:
			sendOptionsDialogue(DEFAULT,
					"Can you tell me more about the furnace?",
					"What should I do here?", "What's an artisan?",
					"I've got to go.");
			stage = 13;
			break;
		case 13:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY,
						"Can you tell me more about the furnace?");
				stage = 14;
				break;
			case OPTION_2:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"This is a Smithing workshop where you can do all kinds of Smithing related tasks.");
				stage = -1;
				break;
			case OPTION_3:
				sendNPCDialogue(npcId, HAPPY,
						"Artisans are a collective of skilled smiths who work",
						"towards upholding quality as their principle ethos.",
						"Smithing can be seen as a coarse, unloving",
						"undertaking...bashing out platebodies as quickly as you");
				stage = 3;
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		case 14:
			if (player.getSkills().getLevel(Skills.SMITHING) < 30) {
				sendNPCDialogue(npcId, PLAIN_TALKING,
						"You aren't experienced enough to know how to use it.",
						"Come back and speak with me when you're more",
						"experienced.");
				stage = 25;
			} else {
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"The furnace is used mainly to forge ingots for ceremonial",
						"swordmaking and burial armour. There are also offshoots",
						"in the cannon room downstairs.");
				stage = 15;
			}
			break;
		case 15:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"It allows you to make different grades of ingots; higher",
					"grades produce finer and purer finished products, but",
					"require more ore.");
			stage = 16;
			break;
		case 16:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"To use the furnace, simply take your ores, preferably",
					"noted to speed things up, to the furnace. Then order the",
					"ingots that you want. You'll be able to collect them",
					"straight away.");
			stage = 17;
			break;
		case 17:
			sendOptionsDialogue(DEFAULT, "What should I do here?",
					"What's an artisan?", "I've got to go.");
			stage = 18;
			break;
		case 18:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"This is a Smithing workshop where you can do all kinds of Smithing related tasks.");
				stage = -1;
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, HAPPY,
						"Artisans are a collective of skilled smiths who work",
						"towards upholding quality as their principle ethos.",
						"Smithing can be seen as a coarse, unloving",
						"undertaking...bashing out platebodies as quickly as you");
				stage = 3;
				break;
			case OPTION_3:
				end();
				break;
			}
			break;
		case 25:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
