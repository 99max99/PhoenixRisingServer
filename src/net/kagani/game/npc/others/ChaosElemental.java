package net.kagani.game.npc.others;

import net.kagani.game.Entity;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.TimersManager.RecordKey;

@SuppressWarnings("serial")
public class ChaosElemental extends NPC {

	public ChaosElemental(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(0);
		setIntelligentRouteFinder(true);
	}

	@Override
	public void sendDeath(Entity source) {
		increaseKills(RecordKey.CHAOS_ELEMENTAL, false);
		super.sendDeath(source);
	}
}
