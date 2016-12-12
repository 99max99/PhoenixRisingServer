package net.kagani.game.npc.dagannoth;

import java.util.ArrayList;

import net.kagani.game.Entity;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;

@SuppressWarnings("serial")
public class DagannothFledeling extends NPC {

	public DagannothFledeling(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> targets = getPossibleTargets(true, false);
		ArrayList<Entity> targetsCleaned = new ArrayList<Entity>();
		for (Entity t : targets) {
			if (!(t instanceof NPC) || ((NPC) t).getId() != 2879)
				continue;
			targetsCleaned.add(t);
		}
		return targetsCleaned;
	}
}
