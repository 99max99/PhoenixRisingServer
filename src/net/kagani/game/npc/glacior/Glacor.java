package net.kagani.game.npc.glacior;

import java.util.List;

import net.kagani.game.Entity;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;

@SuppressWarnings("serial")
public class Glacor extends Glacyte {

	private List<Glacyte> glacites;
	private boolean spawnedGlacites = false;
	private boolean rangeAttack;

	public Glacor(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(null, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		// setCapDamage(50000);

		setEffect((byte) -1);
		setGlacor(this);
		setSpawned(false);
		setDropRateFactor(2);
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		/*
		 * if (glacites == null) { if (!spawnedGlacites) { if (getHitpoints() <=
		 * getMaxHitpoints() / 2) { glacites = new ArrayList<Glacyte>(2);
		 * createGlacites(); spawnedGlacites = true; } }
		 * 
		 * } else if (glacites.size() > 0) { Player player = (Player)
		 * super.getAttackedBy(); player.getPackets().sendGameMessage(
		 * "<col=FF0000>The Glacytes prevent you from dealing any damage.</col>"
		 * ); hit.setDamage(0); }
		 */
		super.handleIngoingHit(hit);
	}

	private void createGlacites() {
		for (int index = 0; index < 3; index++) {
			tileLoop: for (int tileAttempt = 0; tileAttempt < 10; tileAttempt++) {
				WorldTile tile = new WorldTile(this, 2);
				if (World.isTileFree(0, tile.getX(), tile.getY(), 1)) {
					glacites.add(new Glacyte(this, 14302 + index, tile, -1,
							true));
					for (NPC gNpc : glacites) {
						gNpc.setForceAgressive(true);
					}
					break tileLoop;
				}
			}
		}
	}

	public void verifyGlaciteEffect(Glacyte glacite) {
		if (glacites.size() == 1)
			setEffect(glacites.get(0).getEffect());
		glacites.remove(glacite);
	}

	@Override
	public void sendDeath(Entity killer) {
		super.sendDeath(killer);
		glacites = null;
		// spawnedGlacites = false;
	}

	public void setRangeAttack(boolean rangeAttack) {
		this.rangeAttack = rangeAttack;
	}

	public boolean isRangeAttack() {
		return rangeAttack;
	}

	public void resetMinions() {
		glacites = null;
		setEffect((byte) -1);
	}
}
