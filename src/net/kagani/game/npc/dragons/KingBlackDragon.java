package net.kagani.game.npc.dragons;

import net.kagani.game.Entity;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.TimersManager.RecordKey;

@SuppressWarnings("serial")
public class KingBlackDragon extends NPC {

	public KingBlackDragon(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(0);
		setDropRateFactor(2); // double chance
		setIntelligentRouteFinder(true);
	}

	@Override
	public void sendDeath(final Entity source) {
		increaseKills(RecordKey.KING_BLACK_DRAGON, false);
		super.sendDeath(source);
	}

}
