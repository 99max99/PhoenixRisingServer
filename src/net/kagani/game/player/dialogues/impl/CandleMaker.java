package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class CandleMaker extends Dialogue {

	int npcId = 562;

	@Override
	public void start() {
		sendNPCDialogue(
				npcId,
				NORMAL,
				"Ah, you again. You're quite a trend settler. Can't beleive the number of black candle requests I've had since you've came. I couldn't pass up a buisness opprotunity like that, bad luck or no. So I'm selling them now. Would you be");
		stage = 0;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 0) {
			sendNPCDialogue(npcId, NORMAL,
					"interested in some of my fine candles?");
			stage = 1;
		} else if (stage == 1) {
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes Please.",
					"No thank you.");
			stage = 2;
		} else if (stage == 2) {
			if (componentId == OPTION_1) {
				sendPlayerDialogue(NORMAL, "Yes Please.");
				stage = 3;
			} else {
				sendPlayerDialogue(NORMAL, "No thank you.");
				stage = 4;
			}
		} else if (stage == 3) {
			end();
			ShopsHandler.openShop(player, 107);
		} else if (stage == 4)
			end();
	}

	@Override
	public void finish() {

	}
}
