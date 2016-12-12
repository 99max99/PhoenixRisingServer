package net.kagani.game.npc.others;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.WorldTile;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.npc.NPC;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class MirrorBackSpider extends NPC {

	private Entity owner;

	public MirrorBackSpider(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, true);
	}

	public void setOwner(Entity owner) {
		this.owner = owner;
		playAnimation();
	}

	@Override
	public void processNPC() {
		if (owner == null) {
			finish();
			return;
		} else if (owner.isDead() || owner.hasFinished()) {
			owner.getEffectsManager()
					.removeEffect(EffectType.MIRRORBACK_SPIDER);
			return;
		} else if (!withinDistance(owner, 12)) {
			setNextWorldTile(owner);
			return;
		} else if (!getCombat().process())
			sendFollow();
	}

	private void sendFollow() {
		if (getLastFaceEntity() != owner.getClientIndex())
			setNextFaceEntity(owner);
		if (isBound() || isStunned())
			return;
		int size = getSize();
		int targetSize = owner.getSize();
		if (Utils.colides(getX(), getY(), size, owner.getX(), owner.getY(),
				targetSize) && !owner.hasWalkSteps()) {
			resetWalkSteps();
			if (!addWalkSteps(owner.getX() + targetSize, getY())) {
				resetWalkSteps();
				if (!addWalkSteps(owner.getX() - size, getY())) {
					resetWalkSteps();
					if (!addWalkSteps(getX(), owner.getY() + targetSize)) {
						resetWalkSteps();
						if (!addWalkSteps(getX(), owner.getY() - size)) {
							return;
						}
					}
				}
			}
			return;
		}
		resetWalkSteps();
		if (!clipedProjectile(owner, true)
				|| !Utils.isOnRange(getX(), getY(), size, owner.getX(),
						owner.getY(), targetSize, 0))
			calcFollow(owner, 2, true, false);
	}

	private void playAnimation() {
		setNextAnimation(new Animation(24054));
		setNextGraphics(new Graphics(4982));
	}
}
