package net.kagani.game.npc.others;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Combat;
import net.kagani.game.player.content.Consumables;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class MaxTheMaxed extends NPC {

	private boolean castedVeng;
	private long vengCooldown;
	private NPC familiar;
	private long eatCooldown;

	public MaxTheMaxed(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setRun(true);
		setIntelligentRouteFinder(true);
		resetData();
		spawnFamiliar();
		setName("Dragonkk the Best");
	}

	private void resetData() {
		castedVeng = false;
		vengCooldown = 0;

	}

	@Override
	public void sendDeath(Entity source) {
		familiar.sendDeath(source);
		familiar = null;
		super.sendDeath(source);
	}

	@Override
	public void spawn() {
		resetData();
		spawnFamiliar();
		super.spawn();
	}

	public void spawnFamiliar() {
		familiar = new NPC(17240, this, -1, true, true);
		familiar.setNextGraphics(new Graphics(1315));
		familiar.setRun(true);
		familiar.setNextFaceEntity(this);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		checkFamiliar();
		if (isUnderCombat()) {
			checkCombat();
		}

		// prayer renewall :p
		if (Utils.currentTimeMillis() % 25 == 0)
			setNextGraphics(new Graphics(1295));
	}

	public void checkCombat() {
		Entity target = getCombat().getTarget();
		if (!(target instanceof Player)) {
			if (target instanceof Familiar) {
				setTarget(((Familiar) target).getOwner());
				target = getCombat().getTarget();
			} else
				return;
		}
		Player targetP = (Player) target;
		if ((getHitpoints() <= 6000 || (targetP.getCombatDefinitions()
				.isUsingSpecialAttack() && getHitpoints() <= 8000))
				&& targetP.getHitpoints() > 4500
				&& Utils.currentWorldCycle() >= eatCooldown) {
			setNextAnimation(Consumables.EAT_ANIM);
			if (getHitpoints() < 2500)
				heal(1500, 0, 0, true);
			heal(2400, 0, 0, true);
			getCombat().addCombatDelay(3);
			eatCooldown = Utils.currentWorldCycle() + 3;
		}
		if ((target.getHitpoints() <= 5000 || (targetP.getCombatDefinitions()
				.isUsingSpecialAttack() && targetP.getHitpoints() <= 7000))
				&& Utils.currentWorldCycle() >= vengCooldown) {
			setNextGraphics(new Graphics(726, 0, 100));
			setNextAnimation(new Animation(4411));
			castedVeng = true;
			vengCooldown = Utils.currentWorldCycle() + 50;
		}

		int attackStyle = getId() - 15976;
		int weaknessType = targetP.getCombatDefinitions().getWeaknessType();

		/*
		 * if(targetP.getPrayer().isMeleeProtecting() && attackStyle == 0)
		 * setAttackStyle(weaknessType == Combat.MAGIC_TYPE ? 2 : 1); else
		 * if(targetP.getPrayer().isMageProtecting() && attackStyle == 0)
		 * setAttackStyle(weaknessType == Combat.MAGIC_TYPE ? 2 : 1);
		 */

		/*
		 * if(targetP.getPrayer().isMeleeProtecting() && attackStyle == 0) {
		 * if(attackStyle != 1) setAttackStyle(1); return; }
		 * if(targetP.getPrayer().isRangeProtecting() && attackStyle == 1) {
		 * if(attackStyle != 2) setAttackStyle(2); return; }
		 * if(targetP.getPrayer().isMageProtecting() && attackStyle == 2) {
		 * if(attackStyle != 0) setAttackStyle(0); return; }
		 */
		if (targetP.getPrayer().isMeleeProtecting()) {
			if (attackStyle != 2)
				setAttackStyle(2);
			return;
		}
		if (targetP.getPrayer().isRangeProtecting()) {
			if (attackStyle != 0)
				setAttackStyle(0);
			return;
		}
		if (targetP.getPrayer().isMageProtecting()) {
			if (attackStyle != 1)
				setAttackStyle(1);
			return;
		}
		if (weaknessType != Combat.ALL_TYPE && weaknessType != attackStyle) {
			setAttackStyle(weaknessType);
		}

		// weakness = weaknessType == Combat.MELEE_TYPE ? 197 : weaknessType ==
		// Combat.RANGE_TYPE ? 200 : weaknessType == Combat.MAGIC_TYPE ? 202 :
		// 9286;

	}

	public void setAttackStyle(int style) {
		this.setNextNPCTransformation(15976 + style);
	}

	public void checkFamiliar() {
		if (familiar != null
				&& (!Utils.isOnRange(this, familiar, 0) || Utils.colides(this,
						familiar))) {
			if (Utils.getDistance(this, familiar) >= 10) {
				familiar.setNextWorldTile(this);
				familiar.setNextGraphics(new Graphics(1315));
			} else
				familiar.calcFollow(this, true);
		}
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		if (hit.getLook() != HitLook.MELEE_DAMAGE
				&& hit.getLook() != HitLook.RANGE_DAMAGE
				&& hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		final Entity source = hit.getSource();
		if (source == null)
			return;
		if (castedVeng && hit.getDamage() >= 4) {
			castedVeng = false;
			setNextForceTalk(new ForceTalk("Taste vengeance!"));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					source.applyHit(new Hit(MaxTheMaxed.this, (int) (hit
							.getDamage() * 0.75), HitLook.REGULAR_DAMAGE));
				}
			});
		}
	}

}
