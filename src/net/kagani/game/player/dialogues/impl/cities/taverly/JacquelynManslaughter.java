package net.kagani.game.player.dialogues.impl.cities.taverly;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class JacquelynManslaughter extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING,
				"Let's talk of monsters and their defeating. Let me teach",
				"you the way of the slayer.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Trolls are one thing, but there are stranger monsters in",
					"the world. Slayers seek them out, and put an end to them.",
					"Are you interested?");
			stage = 0;
			break;
		case 0:
			sendOptionsDialogue(DEFAULT, "I need slayer supplies.",
					"I want to train slayer.", "Tell me more about slayer.");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 54);
				end();
				break;
			case OPTION_2:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"A slayer master will give you an assignment. Completeing",
						"that assignment will improve your abilities. As your ability",
						"improves, you can complete more difficult assignments.");
				stage = 50;
				break;
			case OPTION_3:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"Not every monster can be killed with just a sword.",
						"Sometimes it takes special knowledge and equipment to",
						"face and defeat them.");
				stage = 4;
				break;
			}
			break;
		case 4:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"As a slayer you'll learn how to defeat more and more",
					"powerful monsters. Many of them guard secret and",
					"ancient treasures.");
			stage = 0;
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
