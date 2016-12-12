package net.kagani.game.npc.godwars.zaros;

import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.minigames.ZarosGodwars;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;

@SuppressWarnings("serial")
public class NexMinion extends NPC {

	private boolean hasNoBarrier;

	public NexMinion(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, true, true);
		setCantFollowUnderCombat(true);
	}

	public void breakBarrier() {
		setCapDamage(6000);
		hasNoBarrier = true;
	}

	public boolean isBarrierBroken() {
		return hasNoBarrier;
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (!hasNoBarrier) {
			setNextGraphics(new Graphics(1549));
			if (hit.getSource() instanceof Player)
				((Player) hit.getSource()).getPackets().sendGameMessage(
						"The avatar is not weak enough to damage this minion.");
			hit.setDamage(0);
		} else
			super.handleIngoingHit(hit);
	}

	@Override
	public void processNPC() {
		if (isDead() || !hasNoBarrier)
			return;
		if (!getCombat().process())
			checkAgressivity();
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		ZarosGodwars.incrementStage(ZarosGodwars.nex.getCurrentPhase());
	}
}