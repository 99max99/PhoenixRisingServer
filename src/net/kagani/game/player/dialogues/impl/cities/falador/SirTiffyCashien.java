package net.kagani.game.player.dialogues.impl.cities.falador;

import net.kagani.game.player.dialogues.Dialogue;

/**
 * The class that represents the dialogue for the NPC - Sir Tiffy Cashien
 * 
 * @author Mod Austin
 * @version 1.0 3/5/2015
 * @contact@deviouscoding@gmail.com
 */

public class SirTiffyCashien extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendPlayerDialogue(NORMAL, "Hello.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(npcId, HAPPY, "What ho, sirrah.",
					"Spiffing day for a walk in the park, what?");
			stage = 0;
			break;
		case 0:
			sendPlayerDialogue(CONFUSED, "Spiffing?");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(npcId, HAPPY, "Absolutely, top-hole!",
					"Well, can't stay and chat all day, dontchaknow!",
					"T-ta for now!");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(CONFUSED, "Erm...goodbye.");
			stage = 25;
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
