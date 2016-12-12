package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

public class Benny extends Dialogue {

	private int npcId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.arrow.game.content.dialogues.Dialogue#start()
	 */

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
				"Can I have a newspaper, please?",
				"How much does a paper cost?",
				"Varrock Herald? Never heard of it.",
				"Anything interesting in there?");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.arrow.game.content.dialogues.Dialogue#run(int, int)
	 */

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "Can I have a newspaper, please?");
				stage = 0;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "How much does a paper cost?");
				stage = 5;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL, "Varrock Herald? Never heard of it.");
				stage = 6;
				break;
			case OPTION_4:
				sendPlayerDialogue(NORMAL, "Anything interesting in there?");
				stage = 8;
				break;
			}
			break;
		case 0:
			sendNPCDialogue(npcId, HAPPY,
					"Certainly, Guv. That'll be 50 gold pieces, please.");
			stage = 1;
			break;
		case 1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Sure, here you go.",
					"Uh, no thanks, I've changed my mind.");
			stage = 2;
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				if (player.getInventory().containsItem(995, 50)) {
					player.getInventory().deleteItem(995, 50);
					player.getInventory().addItem(11169, 1);
					end();
				} else {
					sendPlayerDialogue(UPSET,
							"Oh, I'm afraid I don't have enough money with me.");
					stage = 3;
				}
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "No, thanks.");
				stage = 4;
				break;
			}
			break;
		case 3:
			sendNPCDialogue(npcId, UPSET,
					"Well, no cash, no paper. Live in ignorance.");
			stage = 50;
			break;
		case 4:
			sendNPCDialogue(npcId, HAPPY,
					"Ok, suit yourself. Plenty more fish in the sea.");
			stage = 50;
			break;
		case 5:
			sendNPCDialogue(npcId, HAPPY,
					"Just 50 gold pieces! An absolute bargain! Want one?");
			stage = 1;
			break;
		case 6:
			sendNPCDialogue(npcId, MILDLY_ANGRY,
					"For the illiterate amongst us, I shall elucidate. The",
					"Varrock Herald is a new newspaper. It is edited, printed",
					"and published by myself, Benny Gutenberg, and each",
					"edition promises to enthrall the reader with captivating");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(
					npcId,
					MILDLY_ANGRY,
					"material! Now, can I interest you in buying one for a mere",
					"50 gold?");
			stage = 1;
			break;
		case 8:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Of course there is, mate. Packed full of thought provoking",
					"insights, contentious interviews and celebrity",
					"scandalmongering! An excellent read and all for just 50",
					"coins! Want one?");
			stage = 1;
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
