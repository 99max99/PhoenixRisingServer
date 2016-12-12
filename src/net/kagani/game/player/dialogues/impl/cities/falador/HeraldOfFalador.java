package net.kagani.game.player.dialogues.impl.cities.falador;

import net.kagani.Settings;
import net.kagani.game.player.dialogues.Dialogue;

/**
 * The class that represents the dialogue for the NPC - Herald of Falador
 * 
 * @author Mod Austin
 * @version 1.0 3/5/2015
 * @contact@deviouscoding@gmail.com
 */

public class HeraldOfFalador extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING,
				"A fine day to be visiting Falador is it not? Welcome, ally!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendPlayerDialogue(CONFUSED,
					"Hello to you as well! What do you do here?");
			stage = 0;
			break;
		case 0:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"As the Falador herald, I've been put in command of",
					"keeping Falador a strong and mighty region within",
					Settings.SERVER_NAME + ".");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"My fighting days are behind me, but I can still conquer the",
					"hearts of the people with marching parades and our",
					"herald capes.");
			stage = 2;
			break;
		case 2:
			if (player.getEquipment().getCapeId() == 21435
					|| player.getInventory().containsItem(21435, 1)
					|| player.getBank().getItem(21435).getAmount() == 1) {
				sendPlayerDialogue(NORMAL, "Oh, I already have one of those.");
				stage = 10;
			} else {
				sendPlayerDialogue(CONFUSED, "Herald capes? What are those?");
				stage = 3;
			}
			break;
		case 3:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Much as the White Knights dress alike for battle, we",
					"provide capes adorned with the lion of Falador to those we",
					"trust to show our might and valour to all of "
							+ Settings.SERVER_NAME + "!");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"We even allow you to customise your cape if you have",
					"been helpful to Falador in the past.");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(npcId, CONFUSED,
					"How about it, would you like to take a cape?");
			stage = 6;
			break;
		case 6:
			sendOptionsDialogue(DEFAULT, "Yes", "No");
			stage = 7;
			break;
		case 7:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "Sure, I'll take it!");
				stage = 8;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "No thanks.");
				break;
			}
			break;
		case 8:
			sendNPCDialogue(npcId, HAPPY,
					"Stupendous! Let's take a look at the customisations we",
					"can do...");
			player.getInventory().addItem(21435, 1);
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Well, look at that! You have some extra crests to unlock",
					"too. Get a move on!");
			stage = 25;
			break;
		case 10:
			sendNPCDialogue(npcId, HAPPY,
					"Splendid! You're doing Falador proud with this great",
					"service. Thank you, noble sir! Would you like to customise your cape further?");
			stage = 11;
			break;
		case 11:
			sendOptionsDialogue(DEFAULT, "Yes", "No");
			stage = 12;
			break;
		case 12:
			switch (componentId) {
			case OPTION_1:
				end();
				break;
			case OPTION_2:
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
