package net.kagani.game.player.dialogues.impl.cities.falador;

import net.kagani.game.player.dialogues.Dialogue;

/**
 * The class that represents the dialogue for the NPC - Gardener
 * 
 * @author Mod Austin
 * @version 1.0 3/5/2015
 * @contact@deviouscoding@gmail.com
 */

public class Gardener extends Dialogue {

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
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"I'm busy. Unless you've got some lily seeds, you can go",
					"find Wyson if you need anything.");
			stage = 0;
			break;
		case 0:
			sendOptionsDialogue(DEFAULT, "Ask about strange plant", "Leave");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(
						CONFUSED,
						"Haven't you noticed that there's a massive fissure in the",
						"park?");
				stage = 2;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "Goodbye.");
				stage = 25;
				break;
			}
			break;
		case 2:
			sendNPCDialogue(npcId, CONFUSED,
					"Oh, yeah. I guess there's some sort of mole problem.",
					"Wyson said he had it under control, so I haven't paid it",
					"much mind.");
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
