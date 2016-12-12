package net.kagani.game.npc.dragons;

import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class FrostDragon extends NPC {

	private boolean magicOnly, orb;
	private int attackStage;

	public FrostDragon(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setMagicOnly();
	}

	@Override
	public void processHit(Hit hit) {
		if (orb) {
			hit.getSource().applyHit(
					new Hit(null, hit.getDamage(), HitLook.REFLECTED_DAMAGE));// Just
																				// incase
			return;
		}
		super.processHit(hit);
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	public boolean isMagicOnly() {
		return magicOnly;
	}

	private void setMagicOnly() {
		this.magicOnly = Utils.random(2) == 0;
	}

	public void setOrb(boolean orb) {
		this.orb = orb;
	}

	public int getAttackStage() {
		return attackStage;
	}

	public void setAttackStage(int attackStage) {
		this.attackStage = attackStage;
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				setMagicOnly();
				attackStage = 0;
				orb = false;// Just incase idk
				setNextGraphics(new Graphics(1315));
			}
		}, getCombatDefinitions().getRespawnDelay() + 2);
	}

	public boolean getOrb() {
		return orb;
	}
}
