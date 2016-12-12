package net.kagani.game.player.controllers.trollinvasion;

import java.util.ArrayList;
import java.util.List;

import net.kagani.game.Entity;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;

@SuppressWarnings("serial")
public class TrollInvasionNPC extends NPC {

	private TrollInvasion controler;

	public TrollInvasionNPC(int id, WorldTile tile, TrollInvasion controler) {
		super(id, tile, -1, true, true);
		this.controler = controler;
		setForceMultiArea(true);
		setNoDistanceCheck(true);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		controler.addKill();
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>(1);
		List<Integer> playerIndexes = World.getRegion(getRegionId())
				.getPlayerIndexes();
		if (playerIndexes != null) {
			for (int npcIndex : playerIndexes) {
				Player player = World.getPlayers().get(npcIndex);
				if (player == null || player.isDead() || player.hasFinished()
						|| !player.isRunning())
					continue;
				possibleTarget.add(player);
			}
		}
		return possibleTarget;
	}

}
