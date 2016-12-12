package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.content.PlayerLook;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Thessalia extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY, "Would you like to buy any fine clothes?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(npcId, HAPPY,
					"Or if you're more after fancy dress costumes or",
					"commemorative capes, talk to granny Iffie.");
			stage = 0;
			break;
		case 0:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "What do you have?",
					"No, thank you.");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "What do you have?");
				stage = 2;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING, "No, thank you.");
				stage = 13;
				break;
			}
			break;
		case 2:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Well, I have a number of fine pieces of clothing on sale or,",
					"if you prefer, I can offer you an exclusive, total clothing",
					"makeover?");
			stage = 3;
			break;
		case 3:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Tell me more about this makeover.",
					"I'd just like to buy some clothes.");
			stage = 4;
			break;
		case 4:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "Tell me more about this makeover.");
				stage = 5;
				break;
			case OPTION_2:
				ShopsHandler.openShop(player, 548);
				end();
				break;
			}
			break;
		case 5:
			sendNPCDialogue(npcId, HAPPY, "Certainly!");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Here at Thessalia's Fine Clothing Boutique we offer a",
					"unique service, where we will totally revamp your outfit to",
					"your choosing. Tired of always wearing the same old",
					"outfit, day-in, day-out? Then this is the service for you!");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(npcId, NORMAL, "So, what do you say? Interested?");
			stage = 8;
			break;
		case 8:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"I'd just like to change my outfit, please.",
					"I'd just like to buy some clothes.", "No, thank you.");
			stage = 9;
			break;
		case 9:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY,
						"I'd just like to change my outfit, please.");
				stage = 10;
				break;
			case OPTION_2:
				ShopsHandler.openShop(player, 2);
				end();
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING, "No, thank you.");
				stage = 13;
				break;
			}
			break;
		case 10:
			if (player.getEquipment().wearingArmour() == true) {
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"You can't try them on while wearing armour. Take it off",
						"and speak to me again.");
				stage = 50;
			} else {
				sendNPCDialogue(npcId, HAPPY,
						"Wonderful. Feel free to try on some items and see if",
						"there's anything you would like.");
				stage = 11;
			}
			break;
		case 11:
			sendPlayerDialogue(HAPPY, "Okay, thanks.");
			stage = 12;
			break;
		case 12:
			PlayerLook.openCharacterCustomizing(player);
			end();
			break;
		case 13:
			sendNPCDialogue(npcId, HAPPY,
					"Well, please return if you change your mind.");
			stage = 50;
			break;
		case 50:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}
}