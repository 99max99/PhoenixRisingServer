package net.kagani.game.player.dialogues.impl;

import net.kagani.game.Animation;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.content.FadingScreen;
import net.kagani.game.player.content.Magic;
import net.kagani.game.player.dialogues.Dialogue;

public class GuthixianHighDruid extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	private NPC npc;

	private int option;

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		option = (int) parameters[1];
		npcId = npc.getId();
		switch (option) {
		case 1:
			stage = 1;
			sendNPCDialogue(npcId, BLANK, "Welcome. What can I aid you with?");
			break;
		case 2:
			stage = 1;
			sendNPCDialogue(npcId, QUESTIONS, "It seems you have all you need.");
			break;
		case 3:
			stage = 15;
			sendOptionsDialogue(
					"The rest of the chambers are dangerous.<br>Are you sure you wish to enter?",
					"Yes.", "Actually, no...");
			break;
		case 4:
			sendOptionsDialogue("Which memory would you like to view?",
					"Meeting Crew.", "Zilyana confronting Crew.",
					"Sliske dealing his final blow.", "Saradomin's return.",
					"I don't want to view a memory.");
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
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"What is there to do here?",
					"Can you teleport me to the main chamber?", "Nothing.");
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				stage = 3;
				sendNPCDialogue(
						npcId,
						BLANK,
						"The shrine is a place of mourning. There are activities you can do to pay your respects, which you may find rewarding.");
				break;
			case OPTION_2:
				teleport();
				end();
				break;
			case OPTION_3:
				end();
				break;
			}
			break;
		case 3:
			stage = 4;
			sendNPCDialogue(npcId, NORMAL,
					"We could also use your aid in a threat from the main chamber.");
			break;
		case 4:
			stage = 5;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"What can I do to pay my respects?",
					"What is the threat in the main chamber?",
					"Do you think Guthix would approve of this shrine?");
			break;
		case 5:
			switch (componentId) {
			case OPTION_1:
				stage = 6;
				sendNPCDialogue(
						npcId,
						BLANK,
						"We have established torches around the shrine. If you could light them, we would be grateful.");
				break;
			case OPTION_2:
				stage = 8;
				sendNPCDialogue(
						npcId,
						NORMAL,
						"There is an abundance of automatons loose in the chamber. Since Cre's death, they have become increasingly unstable.");
				break;
			case OPTION_3:
				stage = 9;
				sendNPCDialogue(npcId, NORMAL,
						"The importance here is learning from the events that happened.");
				break;
			}
			break;
		case 6:
			stage = 7;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"What about the threat in the main chamber?",
					"Do you think Guthix would approve of this shrine?",
					"Ask about something else.");
			break;
		case 7:
			switch (componentId) {
			case OPTION_1:
				stage = 8;
				sendNPCDialogue(
						npcId,
						NORMAL,
						"There is an abundance of automatons loose in the chamber. Since Cre's death, they have become increasingly unstable.");
				break;
			case OPTION_2:
				stage = 9;
				sendNPCDialogue(npcId, NORMAL,
						"The importance here is learning from the events that happened.");
				break;
			case OPTION_3:
				stage = 2;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
						"What is there to do here?",
						"Can you teleport me to the main chamber?", "Nothing.");
				break;
			}
			break;
		case 8:
			stage = 14;
			sendNPCDialogue(npcId, SAD,
					"Sad to say, without their master they are turning on anyone who enters.");
			break;
		case 9:
			stage = 10;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"With the knowledge we now have, we are trying to steer away from revering Guthix as a powerful god, but there will always be those who would praise him in this way.");
			break;
		case 10:
			stage = 11;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Here in the shrine, we have the utmost respect for Guthix. n honour of his final words, we are trying only to continue our belief in balance and what Guthix taught, rather than Guthix himself.");
			break;
		case 11:
			stage = 12;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"We cannot ignore the improtance of the event that have passed here, and this space provides us with an enviroment to reflect on them.");
			break;
		case 12:
			stage = 13;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Ask about something else.", "That's all, thanks.");
			break;
		case 13:
			switch (componentId) {
			case OPTION_1:
				stage = 2;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
						"What is there to do here?",
						"Can you teleport me to the main chamber?", "Nothing.");
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		case 14:
			stage = 12;
			sendNPCDialogue(
					npcId,
					BLANK,
					"If you are feeling up to it, we could use your help in thinning their numbers. After all, we need to preserve the sanctity of the shrine.");
			break;
		case 15:
			switch (componentId) {
			case OPTION_1:
				teleport();
				end();
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		}
	}

	private void teleport() {
		Magic.sendNormalTeleportSpell(player, 0, 0,
				new WorldTile(1847, 5986, 0));
		npc.setNextAnimation(new Animation(811));
		FadingScreen.unfade(player,
				FadingScreen.fade(player, FadingScreen.TICK / 2),
				new Runnable() {
					@Override
					public void run() {

					}
				});
	}

	@Override
	public void finish() {

	}
}