package net.kagani.game.npc.others;

import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;

@SuppressWarnings("serial")
public class MercenaryMage extends NPC {

	public MercenaryMage(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(0);
		setCapDamage(12000);
		setCombatLevel(65000);
		setName("Mercenary Mage");
		setRun(true);
		setForceAgressive(true);
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		super.handleIngoingHit(hit);
		if (hit.getLook() != HitLook.MELEE_DAMAGE
				&& hit.getLook() != HitLook.RANGE_DAMAGE
				&& hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		if (hit.getSource() != null) {
			int recoil = hit.getDamage();
			if (recoil > 0) {
				hit.getSource().applyHit(
						new Hit(this, recoil, HitLook.REFLECTED_DAMAGE));
				setNextGraphics(new Graphics(2180));
			}
		}
	}

}
