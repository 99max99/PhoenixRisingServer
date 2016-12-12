package net.kagani.game.npc.others;

import net.kagani.game.Entity;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;

@SuppressWarnings("serial")
public class RockCrabs extends NPC {

	private int realId;

	public RockCrabs(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		realId = id;
		setForceAgressive(true); // to ignore combat lvl
	}

	@Override
	public void setTarget(Entity entity) {
		if (realId == getId())
			this.setNextNPCTransformation(realId - 1);
		super.setTarget(entity);
	}

	@Override
	public void reset() {
		setNPC(realId);
		super.reset();
	}

}
