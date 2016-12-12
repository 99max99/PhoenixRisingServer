package net.kagani.game.npc.others;

import net.kagani.game.Entity;
import net.kagani.game.WorldTile;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class GuthixBlessing extends NPC {

	private Player target;
	private int nextHeal;

	public GuthixBlessing(Player target, WorldTile tile) {
		super(16980, tile, -1, true, true);
		this.target = target;
		nextHeal = 0;
	}

	@Override
	public void processNPC() {
		if (target == null || target.isDead() || target.hasFinished()) {
			finish();
			return;
		} else if (isDead()
				|| target.getHitpoints() == target.getMaxHitpoints()
				|| getPlane() != target.getPlane() || !withinDistance(target)) {
			target.getEffectsManager().removeEffect(EffectType.GUTHIX_BLESSING);
			return;
		}
		if (nextHeal > 2 && nextHeal % 3 == 0)// It skips the first cycle ;)
			target.heal((int) (target.getMaxHitpoints() * 0.08), 0, 0, true);
		nextHeal++;
		sendFollow();
	}

	private void sendFollow() {
		if (getLastFaceEntity() != target.getClientIndex())
			setNextFaceEntity(target);
		if (getEffectsManager().hasActiveEffect(EffectType.BOUND))
			return;
		int size = getSize();
		int targetSize = target.getSize();
		if (Utils.colides(getX(), getY(), size, target.getX(), target.getY(),
				targetSize) && !target.hasWalkSteps()) {
			resetWalkSteps();
			if (!addWalkSteps(target.getX() + targetSize, getY())) {
				resetWalkSteps();
				if (!addWalkSteps(target.getX() - size, getY())) {
					resetWalkSteps();
					if (!addWalkSteps(getX(), target.getY() + targetSize)) {
						resetWalkSteps();
						if (!addWalkSteps(getX(), target.getY() - size)) {
							return;
						}
					}
				}
			}
			return;
		}
		resetWalkSteps();
		if (!clipedProjectile(target, true)
				|| !Utils.isOnRange(getX(), getY(), size, target.getX(),
						target.getY(), targetSize, 0))
			calcFollow(target, 2, true, false);
	}

	public Player getTarget() {
		return target;
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
	}
}
