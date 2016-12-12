package net.kagani.game.player.dialogues.impl;

import net.kagani.game.World;
import net.kagani.game.player.dialogues.Dialogue;

public class VoragoAccChallenge extends Dialogue {

	@Override
	public void start() {
		sendNPCDialogue(17161, NORMAL, World.ChallengerName
				+ " has challenged me, do you wish to join them?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue("Do you accept the challenge?", "Yes", "No");
			break;
		case 0:
			if (componentId == OPTION_1) {
				player.accChallenge = true;
				end();
			} else if (componentId == OPTION_2) {
				player.accChallenge = false;
				end();
			}
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