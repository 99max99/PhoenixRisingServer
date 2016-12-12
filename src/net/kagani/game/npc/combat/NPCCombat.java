package net.kagani.game.npc.combat;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.ForceMovement;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.npc.godwars.zaros.Nex;
import net.kagani.game.npc.nomad.Nomad;
import net.kagani.game.npc.others.Dreadnip;
import net.kagani.game.npc.pest.PestPortal;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Combat;
import net.kagani.utils.MapAreas;
import net.kagani.utils.Utils;

public final class NPCCombat {

	private NPC npc;
	private int combatDelay;
	private Entity target;
	private int resetDelay;

	public NPCCombat(NPC npc) {
		this.npc = npc;
	}

	public int getCombatDelay() {
		return combatDelay;
	}

	/*
	 * returns if under combat
	 */
	public boolean process() {
		if (combatDelay > 0)
			combatDelay--;
		if (target != null) {
			if (!checkAll()) {
				removeTarget();
				return false;
			}
			if (combatDelay == 0) {
				combatDelay = combatAttack();
				checkStuck();
			}
			return true;
		}
		return false;
	}

	public void checkStuck() {
		if (combatDelay == 0 && npc.isUnderCombat()
				&& npc.getAttackingDelay() <= Utils.currentTimeMillis()
				&& npc.getAttackedByDelay() <= Utils.currentTimeMillis()
				&& !npc.isCantFollowUnderCombat() && !npc.isCantInteract()
				&& !npc.hasWalkSteps() && !npc.isBound() && !npc.isStunned()
				&& !(npc instanceof Nomad)) { // reset
			if (resetDelay++ == 10) {
				resetDelay = 0;
				removeTarget();
				npc.forceWalkRespawnTile();
				npc.setHitpoints(npc.getMaxHitpoints());
			}
		} else
			resetDelay = 0;
	}

	/*
	 * return combatDelay
	 */
	private int combatAttack() {
		Entity target = this.target; // prevents multithread issues
		if (target == null)
			return 0;
		// if hes frooze not gonna attack
		if (npc.isStunned())
			return 0;
		// check if close to target, if not let it just walk and dont attack
		// this gameticket
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		// int attackStyle = npc.getAttackStyle();
		if (target instanceof Familiar
				&& (npc.getLureDelay() == 0 || Utils.random(3) == 0)) {
			Familiar familiar = (Familiar) target;
			Player player = familiar.getOwner();
			if (player != null) {
				target = player;
				npc.setTarget(target);
				addAttackedByDelay(target);
			}
		} else if (target instanceof Dreadnip) {
			Dreadnip dreadnip = (Dreadnip) target;
			Player player = dreadnip.getOwner();
			if (player != null) {
				target = player;
				npc.setTarget(player);
			}
		}
		// MAGE_FOLLOW and RANGE_FOLLOW follow close but can attack far unlike
		// melee
		int maxDistance = npc.isCantFollowUnderCombat() ? 16 : npc
				.getAttackStyle() != Combat.MELEE_TYPE ? 9 : 0;// attackStyle
		// ==
		// NPCCombatDefinitions.MELEE
		// ?
		// 0
		// :
		// npc.isCantFollowUnderCombat()
		// ?
		// 16
		// :
		// 9;
		// player is walking to atm
		if ((!npc.clipedProjectile(target, maxDistance == 0
				&& !forceCheckClipAsRange(target)))
				|| !Utils
						.isOnRange(
								npc,
								target,
								maxDistance
										// correct extra distance for walk. no
										// glitches this way
										// ^^. even if frozen
										+ (npc.hasWalkSteps()
												&& target.hasWalkSteps() ? (npc
												.getRun() && target.getRun() ? 2
												: 1)
												: 0))
				|| (!npc.isCantFollowUnderCombat() && Utils
						.colides(npc, target))) {// doesnt
			// let
			// u
			// attack
			// when
			// u
			// under
			// /
			// while
			// walking
			// out,
			// remove
			// this
			// check
			// if
			// u
			// want
			return 0;

		}
		addAttackedByDelay(target);
		return CombatScriptsHandler.specialAttack(npc, target);
	}

	protected void doDefenceEmote(Entity target, Hit hit) {
		/*
		 * if (target.getNextAnimation() != null) // if has att emote already
		 * return;
		 */
		int defenceEmote = Combat.getDefenceEmote(target);
		if (defenceEmote != -1)
			target.setNextAnimationNoPriority(new Animation(defenceEmote, hit
					.getDelay()));
	}

	public Entity getTarget() {
		return target;
	}

	public void addAttackedByDelay(Entity target) { // prevents multithread
		// issues

		target.setAttackedBy(npc);
		target.setAttackedByDelay(Utils.currentTimeMillis() + 6000); // 8seconds
		npc.setAttackingDelay(Utils.currentTimeMillis() + 6000);
	}

	public void setTarget(Entity target) {
		this.target = target;
		npc.setNextFaceEntity(target);
		if (!checkAll()) {
			removeTarget();
			return;
		}
	}

	public boolean checkAll() {
		Entity target = this.target; // prevents multithread issues
		if (target == null)
			return false;
		if (npc.isDead() || npc.hasFinished() || npc.isForceWalking()
				|| target.isDead() || target.hasFinished()
				|| npc.getPlane() != target.getPlane())
			return false;
		if (npc instanceof Familiar && target instanceof NPC
				&& ((NPC) target).isCantInteract())
			return false;
		if (npc.isBound() || npc.isStunned())
			return true; // if freeze cant move ofc
		int distanceX = npc.getX() - npc.getRespawnTile().getX();
		int distanceY = npc.getY() - npc.getRespawnTile().getY();
		int size = npc.getSize();
		int maxDistance;
		int agroRatio = npc.getCombatDefinitions().getAgroRatio();
		if (!npc.isNoDistanceCheck() && !npc.isCantFollowUnderCombat()) {
			maxDistance = agroRatio > 12 ? agroRatio : 12; // before 32, but its
			// too much
			if (!(npc instanceof Familiar)) {

				if (npc.getMapAreaNameHash() != -1) {
					// if out his area
					if (!MapAreas.isAtArea(npc.getMapAreaNameHash(), npc)
							|| (!npc.canBeAttackFromOutOfArea() && !MapAreas
									.isAtArea(npc.getMapAreaNameHash(), target))) {
						npc.forceWalkRespawnTile();
						return false;
					}
				} else if (distanceX > size + maxDistance
						|| distanceX < -1 - maxDistance
						|| distanceY > size + maxDistance
						|| distanceY < -1 - maxDistance) {
					// if more than 32 distance from respawn place
					npc.forceWalkRespawnTile();
					return false;
				}
			}
			maxDistance = agroRatio > 16 ? agroRatio : 16;
			distanceX = target.getX() - npc.getX();
			distanceY = target.getY() - npc.getY();
			if (distanceX > size + maxDistance || distanceX < -1 - maxDistance
					|| distanceY > size + maxDistance
					|| distanceY < -1 - maxDistance) {
				return false; // if target distance higher 16
			}
		} else {
			distanceX = target.getX() - npc.getX();
			distanceY = target.getY() - npc.getY();
		}
		// checks for no multi area :)
		if (npc instanceof Familiar) {
			Familiar familiar = (Familiar) npc;
			if (!familiar.canAttack(target))
				return false;
		} /*
		 * else { if (!npc.isForceMultiAttacked()) { if (!target.isAtMultiArea()
		 * || !npc.isAtMultiArea()) { if (npc.getAttackedBy() != target &&
		 * npc.getAttackedByDelay() > Utils.currentTimeMillis()) return false;
		 * if (target.getAttackedBy() != npc && target.getAttackedByDelay() >
		 * Utils.currentTimeMillis()) return false; } } }
		 */
		if (!npc.isCantFollowUnderCombat()) {
			// if is under
			int targetSize = target.getSize();
			/*
			 * if (distanceX < size && distanceX > -targetSize && distanceY <
			 * size && distanceY > -targetSize && !target.hasWalkSteps()) {
			 */
			if (!target.hasWalkSteps()
					&& Utils.colides(npc.getX(), npc.getY(), size,
							target.getX(), target.getY(), targetSize)) {
				npc.resetWalkSteps();
				if (!npc.addWalkSteps(target.getX() + targetSize, npc.getY())) {
					npc.resetWalkSteps();
					if (!npc.addWalkSteps(target.getX() - size, npc.getY())) {
						npc.resetWalkSteps();
						if (!npc.addWalkSteps(npc.getX(), target.getY()
								+ targetSize)) {
							npc.resetWalkSteps();
							if (!npc.addWalkSteps(npc.getX(), target.getY()
									- size)) {
								return true;
							}
						}
					}
				}
				return true;
			}
			if (npc.getAttackStyle() == NPCCombatDefinitions.MELEE
					&& targetSize == 1 && size == 1
					&& Math.abs(npc.getX() - target.getX()) == 1
					&& Math.abs(npc.getY() - target.getY()) == 1
					&& !target.hasWalkSteps()) {
				if (!npc.addWalkSteps(target.getX(), npc.getY(), 1))
					npc.addWalkSteps(npc.getX(), target.getY(), 1);
				return true;
			}

			if (npc instanceof Nex) {
				Nex nex = (Nex) npc;
				maxDistance = nex.isForceFollowClose() ? 0 : 9;
				if (!nex.isFlying()
						&& (!npc.clipedProjectile(target, maxDistance == 0
								&& !forceCheckClipAsRange(target)))
						|| !Utils.isOnRange(npc.getX(), npc.getY(), size,
								target.getX(), target.getY(), targetSize,
								maxDistance)) {
					npc.resetWalkSteps();
					if (!Utils.isOnRange(npc.getX(), npc.getY(), size,
							target.getX(), target.getY(), targetSize, 10)) {
						int[][] dirs = Utils.getCoordOffsetsNear(size);
						for (int dir = 0; dir < dirs[0].length; dir++) {
							final WorldTile tile = new WorldTile(new WorldTile(
									target.getX() + dirs[0][dir], target.getY()
											+ dirs[1][dir], target.getPlane()));
							if (World.isTileFree(tile.getPlane(), tile.getX(),
									tile.getY(), size)) { // if
								// found
								// done
								npc.setNextForceMovement(new ForceMovement(
										new WorldTile(npc), 0, tile, 1, Utils
												.getMoveDirection(
														tile.getX()
																- npc.getX(),
														tile.getY()
																- npc.getY())));
								npc.setNextAnimation(new Animation(17408));
								npc.setNextWorldTile(tile);
								nex.setFlying(false);
								return true;
							}
						}
					} else
						npc.calcFollow(target, 2, true,
								npc.isIntelligentRouteFinder());
					return true;
				} else
					// if doesnt need to move more stop moving
					npc.resetWalkSteps();
			} else {
				// MAGE_FOLLOW and RANGE_FOLLOW follow close but can attack far
				// unlike melee
				maxDistance = npc.isForceFollowClose() ? 0 : (npc
						.getCombatDefinitions().isFollowClose()
						|| npc.getAttackStyle() == Combat.MELEE_TYPE ? 0 : 9);
				npc.resetWalkSteps();
				// is far from target, moves to it till can attack
				if ((!npc.clipedProjectile(target, maxDistance == 0
						&& !forceCheckClipAsRange(target)))
						|| !Utils.isOnRange(npc.getX(), npc.getY(), size,
								target.getX(), target.getY(), targetSize,
								maxDistance)) {
					npc.calcFollow(target, npc.getRun() ? 2 : 1, true,
							npc.isIntelligentRouteFinder());
					return true;
				}
				// if under target, moves

			}
		}
		return true;
	}

	/*
	 * if (!npc.clipedProjectile(target, (!(npc instanceof Nex)) &&
	 * !npc.clipedProjectile(target, maxDistance == 0 &&
	 * !forceCheckClipAsRange(target))) || !Utils.isOnRange(npc, target,
	 * maxDistance //correct extra distance for walk. no glitches this way ^^.
	 * even if frozen + (npc.hasWalkSteps() && target.hasWalkSteps() ?
	 * (npc.getRun() && target.getRun() ? 2 : 1) : 0)) ||
	 * (!npc.isCantFollowUnderCombat() && Utils.colides(npc, target))) //doesnt
	 * let u attack when u under / while walking out, remove this check if u
	 * want return 0;
	 */

	private boolean forceCheckClipAsRange(Entity target) {
		return target instanceof PestPortal;
	}

	public void addCombatDelay(int delay) {
		combatDelay += delay;
	}

	public void setCombatDelay(int delay) {
		combatDelay = delay;
	}

	public boolean underCombat() {
		return target != null;
	}

	public void removeTarget() {
		this.target = null;
		npc.setNextFaceEntity(null);
	}

	public void reset() {
		combatDelay = 0;
		target = null;
	}
}