package net.kagani.game.player.dialogues.impl.cities.portsarim;

import net.kagani.game.player.dialogues.Dialogue;

public class Thurgo extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendOptionsDialogue(DEFAULT, "Skillcape of Smithing.",
				"Something else.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL,
						"That's an unusual cape you're wearing, what is it?");
				stage = 0;
				break;
			case OPTION_2:
				sendDialogue("Thurgo doesn't appear to be interested in talking.");
				stage = -2;
				break;
			}
			break;
		case 0:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"It's a Skillcape of Smithing. Shows that I am a master",
					"blacksmith, but of course that's only to be expected. I am",
					"an Imcando dwarf after all and everybody knows we're",
					"the best blacksmiths.");
			stage = -2;
			break;
		case -2:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
