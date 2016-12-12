package net.kagani.game.player.dialogues.impl.cities.draynor;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Diango extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY, "Howdy there, partner.");
		stage = 0;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 0:
			sendOptionsDialogue(DEFAULT, "Show me vote shop 1.",
					"Show me vote shop 2.", "How many vote points do I have?",
					"Nevermind");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 190);
				sendOptionsDialogue(DEFAULT, "Normal Auras.", "Greater Auras.",
						"Master Auras.", "Supreme Auras", "Nevermind");
				stage = 2;
				break;
			case OPTION_2:
				ShopsHandler.openShop(player, 195);
				end();
				break;
			case OPTION_3:
				sendNPCDialogue(npcId, HAPPY,
						"You have <col=FF0000>" + player.getVoteCount()
								+ "</col> vote points.");
				stage = -1;
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 190);
				sendOptionsDialogue(DEFAULT, "Normal Auras.", "Greater Auras.",
						"Master Auras.", "Supreme Auras", "Nevermind");
				stage = 2;
				break;
			case OPTION_2:
				ShopsHandler.openShop(player, 191);
				sendOptionsDialogue(DEFAULT, "Normal Auras.", "Greater Auras.",
						"Master Auras.", "Supreme Auras", "Nevermind");
				stage = 2;
				break;
			case OPTION_3:
				ShopsHandler.openShop(player, 192);
				sendOptionsDialogue(DEFAULT, "Normal Auras.", "Greater Auras.",
						"Master Auras.", "Supreme Auras", "Nevermind");
				stage = 2;
				break;
			case OPTION_4:
				ShopsHandler.openShop(player, 193);
				sendOptionsDialogue(DEFAULT, "Normal Auras.", "Greater Auras.",
						"Master Auras.", "Supreme Auras", "Nevermind");
				stage = 2;
				break;
			case OPTION_5:
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {

	}
}