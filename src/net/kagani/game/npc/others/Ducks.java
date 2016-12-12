package net.kagani.game.npc.others;

import net.kagani.game.ForceTalk;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.utils.Utils;

/**
 * @Author Frostbite
 * @Contact<frostbitersps@gmail.com;skype:frostbitersps>
 */
public class Ducks extends NPC {

	public static final String[] noises = { "Quack!", "Quack?" };

	public Ducks(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public void processNPC() {
		int ticks = 0;
		if (ticks == 45) {
			String noise = noises[Utils.random(noises.length)];
			setNextForceTalk(new ForceTalk(noise));
		}

	}
}
