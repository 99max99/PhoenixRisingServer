package net.kagani.game.npc.fightcaves;

import java.util.ArrayList;
import java.util.List;

import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;

@SuppressWarnings("serial")
public class FightCavesNPC extends NPC {

	public FightCavesNPC(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
		setNoDistanceCheck(true);
	}

	@Override
	public void sendDeath(Entity source) {
		setNextGraphics(new Graphics(2924 + getSize()));
		super.sendDeath(source);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>(1);
		List<Integer> playerIndexes = World.getRegion(getRegionId())
				.getPlayerIndexes();
		if (playerIndexes != null) {
			for (int npcIndex : playerIndexes) {
				Player player = World.getPlayers().get(npcIndex);
				if (player == null || player.getAppearence().isHidden()
						|| player.isDead() || player.hasFinished()
						|| !player.isRunning())
					continue;
				possibleTarget.add(player);
			}
		}
		return possibleTarget;
	}
}
