package net.kagani.game.player.dialogues.impl.cities.alkharid;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;

public class AlkharidCamel extends Dialogue {

	int npcId;

	String[] ollieActions = {
			"The camel turns its head and glares at you.",
			"The camel tries to stamp on your foot, but you pull it back quickly",
			"The camel spits at you, and you jump back hurriedly." },
			playerJudgements = {
					"If I go near that camel, it'll probably bite my hand off.",
					"I wonder if that camel has fleas...",
					"Mmm... Looks like that camel would make a nice kebab." };

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendPlayerDialogue(NORMAL,
				"" + playerJudgements[Utils.random(playerJudgements.length)]);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
		player.getPackets().sendGameMessage(
				"" + ollieActions[Utils.random(ollieActions.length)]);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
