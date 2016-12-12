package net.kagani.game.npc.evilchicken;

import net.kagani.game.Entity;
import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.player.TimersManager.RecordKey;

@SuppressWarnings("serial")
public class EvilChicken extends NPC {

	/**
	 * @author: Dylan Page
	 */

	public EvilChicken(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(3000);
		setIntelligentRouteFinder(true);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		checkReset();
	}

	public void checkReset() {
		int maxhp = getMaxHitpoints();
		if (maxhp > getHitpoints() && !isUnderCombat() && getPossibleTargets().isEmpty())
			setHitpoints(maxhp);
	}

	@Override
	public void sendDeath(Entity source) {
		increaseKills(RecordKey.EVIL_CHICKEN, true);
		super.sendDeath(source);
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		reduceHit(hit);
		super.handleIngoingHit(hit);
	}

	public void reduceHit(Hit hit) {
		if (!(hit.getSource() instanceof Player) || (hit.getLook() != HitLook.MELEE_DAMAGE
				&& hit.getLook() != HitLook.RANGE_DAMAGE && hit.getLook() != HitLook.MAGIC_DAMAGE))
			return;

	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.8;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.8;
	}

	public static boolean distanceOf(WorldTile tile) {
		if ((tile.getX() >= 2683 && tile.getX() <= 2619) && (tile.getY() >= 10369 && tile.getY() <= 10435))
			return true;
		return false;
	}
}