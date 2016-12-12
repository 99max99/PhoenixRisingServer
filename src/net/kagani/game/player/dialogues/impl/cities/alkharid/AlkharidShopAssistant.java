package net.kagani.game.player.dialogues.impl.cities.alkharid;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class AlkharidShopAssistant extends Dialogue {

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCChat(ASKING, "Can I help you at all?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Yes please. What are you selling?", "No thanks.",
					"What do you think of Ali Morrisane?");
			stage = 0;
			break;

		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerChat(ASKING, "Yes please. What are you selling?");
				stage = -3;
				break;
			case OPTION_2:
				sendPlayerChat(NORMAL, "No thanks.");
				stage = -2;
				break;
			case OPTION_3:
				sendNPCChat(NORMAL,
						"I don't trust him, not after he's sent his men round to "
								+ "threaten us.");
				stage = 1;
				break;
			}
			break;

		case 1:
			sendNPCChat(NORMAL,
					"Did you know, we'eve had four different thugs come "
							+ "round and threaten us if we didn't join them.");
			stage = 2;
			break;

		case 2:
			sendNPCChat(NORMAL,
					"The owner's had hammers, rakes, swords and even "
							+ "scissors waved at them!");
			stage = 3;
			break;

		case 3:
			sendPlayerChat(ASKING, "What will you do about it?");
			stage = 4;
			break;

		case 4:
			sendNPCChat(NORMAL,
					"We'll carry on as normal with the store. Buisness as "
							+ "usual!");
			stage = 5;
			break;

		case 5:
			sendNPCChat(ASKING, "Can I interest you in anything?");
			stage = 6;
			break;

		case 6:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Yes please. What are you selling?", "No thanks.");
			stage = 7;
			break;

		case 7:
			switch (componentId) {
			case OPTION_1:
				sendPlayerChat(ASKING, "Yes please. What are you selling?");
				stage = -3;
				break;
			case OPTION_2:
				sendPlayerChat(NORMAL, "No thanks.");
				stage = -2;
				break;
			}
			break;

		case -3:
			end();
			ShopsHandler.openShop(player, 173);
			break;

		case -2:
			end();
			break;

		default:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
