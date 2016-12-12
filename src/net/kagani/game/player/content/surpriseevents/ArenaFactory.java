package net.kagani.game.player.content.surpriseevents;

import net.kagani.game.player.content.surpriseevents.arenaimpl.CastleArena;
import net.kagani.game.player.content.surpriseevents.arenaimpl.ElvenArena;
import net.kagani.utils.Utils;

public class ArenaFactory {

	public static EventArena randomEventArena(boolean multi) {
		int rnd = Utils.random(2);
		switch (rnd) {
		case 0:
		default:
			return new ElvenArena(multi);
		case 1:
			return new CastleArena(multi);

		}

	}

}
