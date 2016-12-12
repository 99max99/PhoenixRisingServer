package net.kagani.game.player.dialogues.impl.cities.falador;

import net.kagani.game.player.dialogues.Dialogue;

/**
 * The class that represents the dialogue for the NPC - Ikis Krum
 * 
 * @author Mod Austin
 * @version 1.0 3/5/2015
 * @contact@deviouscoding@gmail.com
 */

public class IkisKrum extends Dialogue {

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
			sendNPCDialogue(npcId, CONFUSED,
					"Good day, sir. What brings you to this end of town?");
			stage = 0;
			break;
		case 0:
			sendPlayerDialogue(NORMAL, "Well, what is there to do around here?");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"If you're into Mining, plenty! The dwarves have one of the",
					"largest mines in the world just under our feet. There's an",
					"entrance in the building just north-east of my house.");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(
					npcId,
					MILDLY_ANGRY,
					"If you're one of these young, loud, hipster sorts you could",
					"visit the Party Room, just north of here. The blasted",
					"Pete parties all night and I never get any sleep!");
			stage = 3;
			break;
		case 3:
			sendPlayerDialogue(CONFUSED, "Thanks.");
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
