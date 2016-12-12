package net.kagani.game.player.dialogues.impl;

import net.kagani.game.minigames.CastleWars;
import net.kagani.game.player.dialogues.Dialogue;

public class CastleWarsScoreboard extends Dialogue {

	@Override
	public void start() {
		CastleWars.viewScoreBoard(player);

	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();

	}

	@Override
	public void finish() {

	}

}
