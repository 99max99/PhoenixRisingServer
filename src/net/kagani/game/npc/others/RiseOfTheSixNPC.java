package net.kagani.game.npc.others;

import java.util.ArrayList;
import java.util.List;

import net.kagani.game.Entity;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;

@SuppressWarnings("serial")
public class RiseOfTheSixNPC extends NPC {

	public RiseOfTheSixNPC(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
		setForceMultiArea(true);
		setNoDistanceCheck(true);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		final ArrayList<Entity> possibleTarget = new ArrayList<Entity>(1);
		final List<Integer> playerIndexes = World.getRegion(getRegionId()).getPlayerIndexes();
		if (playerIndexes != null) {
			for (final int npcIndex : playerIndexes) {
				final Player player = World.getPlayers().get(npcIndex);
				if (player == null || player.isDead() || player.hasFinished() || !player.isRunning())
					continue;
				possibleTarget.add(player);
			}
		}
		return possibleTarget;
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
	}
}