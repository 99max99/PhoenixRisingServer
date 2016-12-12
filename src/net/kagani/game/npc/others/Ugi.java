package net.kagani.game.npc.others;

import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;

@SuppressWarnings("serial")
public class Ugi extends NPC {

	private Player target;

	public Ugi(Player target, int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		this.target = target;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (target.hasFinished() || !withinDistance(target, 10)) {
			target.getTreasureTrailsManager().setPhase(0);
			finish();
			return;
		}
	}

	public Player getTarget() {
		return target;
	}
}
