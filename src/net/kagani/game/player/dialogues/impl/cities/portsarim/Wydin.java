package net.kagani.game.player.dialogues.impl.cities.portsarim;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Wydin extends Dialogue {

	public int getNPCID() {
		return 557;
	}

	@Override
	public void start() {
		stage = (byte) parameters[0];
		switch (stage) {
		case 0:
			npc(HAPPY, "Welcome to my food store! Would you like to buy",
					"anything?");
			stage = -1;
			break;
		case 1:
			npc(ANGRY, "Hey, you can't go in there. Only employees of the "
					+ "grocery store can go in.");
			stage = 8;
			break;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "Yes please.", "No, thank you.",
					"What can you recommend?");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "Yes please.");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(BLANK, "No, thanks.");
				stage = 50;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL, "What can you recommend?");
				stage = 2;
				break;
			}
			break;
		case 1:
			ShopsHandler.openShop(player, 30);
			end();
			break;
		case 2:
			npc(HAPPY,
					"We have this really exotic fruit all the way from Karamja.",
					"It's called a banana.");
			stage = 3;
			break;
		case 3:
			sendOptionsDialogue(DEFAULT, "Hmm, I think I'll try one.",
					"I don't like the sound of that.");
			stage = 4;
			break;
		case 4:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING, "Hmm, I think I'll try one.");
				stage = 5;
				break;
			case OPTION_2:
				sendPlayerDialogue(UPSET, "I don't like the sound of that.");
				stage = 7;
				break;
			}
			break;
		case 5:
			npc(PLAIN_TALKING,
					"Great. You might as well take a look at the rest of my",
					"wares as well.");
			stage = 6;
			break;
		case 6:
			end();
			ShopsHandler.openShop(player, 30);
			break;
		case 7:
			npc(PLAIN_TALKING,
					"Well, it's your choice, but I do recommend them.");
			stage = 50;
			break;

		case 8:
			// if(player.getQuestManager().get(Quests.PIRATES_TREASURE).getStage()
			// > 3) {
			// sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
			// "Well, can I get a job here?", "Sorry, I didn't realise.");
			// stage = 9;
			// } else {
			player(SAD, "Sorry I didn't realise.");
			stage = 50;
			// }
			break;

		case 9:
			switch (componentId) {
			case OPTION_1:
				npc(NORMAL,
						"Well, you're keen, I'll give you that. Okay, I'll give you "
								+ "a go. Have you got your own white apron?");
				stage = 10;
				break;
			case OPTION_2:
				player(SAD, "Sorry, I didn't realise.");
				stage = 50;
				break;
			}
			break;

		case 10:
			if (!player.getInventory().containsItem(1005, 1)
					&& !(player.getEquipment().getChestId() == 1005)) {
				player(SAD, "No, I haven't.");
				stage = 11;
			} else {
				player(HAPPY, "Yes, I have one right here.");
				stage = 15;
			}
			break;

		case 11:
			npc(NORMAL, "Well, you can't work here unless you have a white "
					+ "apron. Health and safety regulations, you understand.");
			stage = 12;
			break;

		case 12:
			player(ASKING, "Where can I get one of those?");
			stage = 13;
			break;

		case 13:
			npc(NORMAL, "Well, I get all of mine over at the clothing shop in "
					+ "Varrock. They sell them cheap there.");
			stage = 14;
			break;

		case 14:
			npc(NORMAL,
					"Oh, and I'm sure that I've seen a spare one over in"
							+ " Gerrant's fish store somewhere. It's the little place just "
							+ "north of here.");
			stage = 50;
			break;

		case 15:
			npc(HAPPY, "Wow - you are well prepared! You're hired. Go through "
					+ "to the back and tidy up for me, please.");
			// player.getQuestManager().get(Quests.PIRATES_TREASURE).setStage(5);
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
