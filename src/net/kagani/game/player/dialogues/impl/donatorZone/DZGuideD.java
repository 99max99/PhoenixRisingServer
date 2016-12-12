package net.kagani.game.player.dialogues.impl.donatorZone;

import net.kagani.game.player.dialogues.Dialogue;

public class DZGuideD extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		sendNPCDialogue(npcId, 9827, "Welcome to Donator Zone!",
				"The Donator zone is splited into 7 parts.",
				"Would you like to know more about it?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes, please.",
					"No, thanks.");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				player.getCutscenesManager().play("DZGuideScene");
				end();
				break;
			default:
				end();
				break;
			}
			break;
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}
}