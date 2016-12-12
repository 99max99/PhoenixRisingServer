package net.kagani.game.npc.kalphite;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.EffectsManager.Effect;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.player.TimersManager.RecordKey;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class KalphiteKing extends NPC {
	public int spawnCount;
	private boolean usedImmortality;
	private int phase;
	private NPC king;
	private boolean started = false;
	public boolean isShieldActive = false;
	private boolean siphon = false;

	public KalphiteKing(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(Integer.MAX_VALUE);
		setRun(true);
		setIntelligentRouteFinder(true);
		start();
		phase = 0;
		king = this;
	}

	public int getSpawnCount() {
		return spawnCount;
	}

	@Override
	public boolean isStunImmune() {
		return false;
	}

	@Override
	public void spawn() {
		super.spawn();
		start();
	}

	@Override
	public void sendDeath(final Entity source) {
		if (!usedImmortality && Utils.random(100) < 15) {
			Entity target = getCombat().getTarget();
			setCantInteract(true);
			resetReceivedHits();
			setHitpoints((int) (getMaxHitpoints() * 0.40));
			usedImmortality = true;
			setNextAnimation(new Animation(19483));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (getBossInstance() != null
							&& !getBossInstance().isInstanceReady())
						return;
					setCantInteract(false);
					if (target != null)
						setTarget(target);
				}

			}, 7);
			return;
		}
		if (getBossInstance() != null) { // wont save if wasnt active
			for (Player player : getBossInstance().getPlayers()) {
				player.getTimersManager().removeTimer(RecordKey.KALPHITE_KING);
				increaseKills(RecordKey.KALPHITE_KING, false);
			}
		}
		super.sendDeath(source);
	}

	private void start() {
		spawnCount = 0;
		usedImmortality = false;
		getOutsideEarth(null);
		setDirection(Utils.getAngle(0, -1));
		addTimer();
	}

	public void addTimer() {
		if (getBossInstance() != null) {
			for (Player player : getBossInstance().getPlayers())
				player.getTimersManager().sendTimer();
		}
	}

	public int getHPPercentage() {
		return getHitpoints() * 100 / getMaxHitpoints();
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		if (isShieldActive) {
			setNextGraphics(new Graphics(siphon ? 3737 : 3736));
		}

		if (!isCantInteract() && this.getCombat().getCombatDelay() == 0) // Damage
		// from
		// being
		// under
		// KK
		{
			for (Entity t : getPossibleTargets()) {
				if (Utils.colides(this, t)) {
					t.applyHit(new Hit(this, Utils.random(1280) + 110,
							HitLook.REGULAR_DAMAGE));
				}
			}
		}
		super.processNPC();
	}

	public void battleCry() // Minion spawn
	{
		setNextAnimation(new Animation(19462));
		for (Entity t : getPossibleTargets()) {
			if (Utils.isOnRange(this, t, 3)) {
				t.setStunDelay(5); // stuns for 3 sec
			}
		}

		if (getBossInstance() == null)
			return;
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				if (!getBossInstance().isInstanceReady())
					return;
				for (int i = 0; i < 6; i++) {
					NPC minion = World.spawnNPC(
							16706,
							getBossInstance().getTile(2963 + Utils.random(20),
									1749 + Utils.random(20), 0), -1, true, true);
					minion.setForceTargetDistance(64);
					minion.setNextAnimation(new Animation(19492));
					minion.setNextGraphics(new Graphics(3748));
					minion.setBossInstance(getBossInstance());
					for (Entity target : minion.getPossibleTargets())
						if (Utils.colides(minion, target))
							target.applyHit(new Hit(minion, Utils.random(400,
									401), HitLook.MELEE_DAMAGE));
				}
			}

		}, 5);
	}

	public void switchPhase() {
		int currentPhase = getCurrentPhase();
		int nextPhase = (currentPhase + Utils.random(2) + 1) % 3;
		setNextNPCTransformation(16697 + nextPhase);
		setNextGraphics(new Graphics(nextPhase == 0 ? 3750
				: nextPhase == 1 ? 3749 : 3751));
		phase = 0;
	}

	public int getCurrentPhase() // Melee mage or range NOT attack cycle phase!
	{
		return getId() - 16697;
	}

	@Override
	public void reset() {
		setNPC(16697 + Utils.random(3));
		super.reset();
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (isShieldActive && siphon && hit.getDamage() > 0)
			hit.setHealHit();
		else {
			Entity source = hit.getSource();
			if (source != null) {
				if (source.getEffectsManager().hasActiveEffect(
						EffectType.UNLOAD)
						|| source.getEffectsManager().hasActiveEffect(
								EffectType.FRENZY))
					useBarricade();
			}
		}
		if (!siphon)
			super.handleIngoingHit(hit);
	}

	private void useBarricade() {
		if (!getEffectsManager().hasActiveEffect(EffectType.BARRICADE))
			getEffectsManager().startEffect(
					new Effect(EffectType.BARRICADE, 17));
	}

	@Override
	public Hit handleOutgoingHit(Hit hit, Entity target) {
		if (isShieldActive && !siphon && hit.getDamage() > 0) {
			this.healShield(hit.getDamage());
		}
		return hit;
	}

	private void healShield(int dmg) {
		Hit h = new Hit(null, dmg, HitLook.HEALED_DAMAGE);
		h.setHealHit();
		this.applyHit(h);
		this.heal(dmg);
	}

	public void dig(final Entity target) {
		setNextAnimation(new Animation(19453));
		setNextGraphics(new Graphics(3746));
		setCantInteract(true);
		WorldTasksManager.schedule(new WorldTask() {

			boolean part1 = true;

			@Override
			public void run() {
				if (getBossInstance() != null
						&& !getBossInstance().isInstanceReady() || isDead()) {
					stop();
					return;
				}
				if (part1) {
					setFinished(true);
					part1 = false;
				} else {
					stop();
					if (target instanceof Player
							&& (getBossInstance() == null || getBossInstance()
									.isPlayerInside((Player) target))) {
						WorldTile loc = new WorldTile(target.getX()
								- (getSize() / 2), target.getY()
								- (getSize() / 2), target.getPlane());
						if (World.isFloorFree(loc.getPlane(), loc.getX(),
								loc.getY(), getSize()))
							setLocation(loc);
					}
					setFinished(false);
					getOutsideEarth(target);
				}
			}
		}, 6, 5);

	}

	private void getOutsideEarth(Entity target) {
		setNextAnimation(new Animation(19451));
		setNextGraphics(new Graphics(3745));
		setCantInteract(true);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				if (getBossInstance() != null
						&& !getBossInstance().isInstanceReady())
					return;
				setCantInteract(false);
				if (target != null)
					setTarget(target);
				getCombat().setCombatDelay(5);
				setNextAnimation(new Animation(-1));
				for (Entity target : getPossibleTargets()) {
					if (Utils.colides(KalphiteKing.this, target)) {
						target.applyHit(new Hit(KalphiteKing.this, Utils
								.random(3000, 5001), HitLook.REGULAR_DAMAGE));
						if (target instanceof Player)
							target.setNextAnimation(new Animation(10070));
					}
				}
				if (king.getId() == 16699)
					phase = 0;
				else if (king.getId() == 16698 && started) {
					phase = 5;
					started = true;
				}
			}

		}, 5);
	}

	public void activateShield() {
		setNextAnimation(new Animation(19462));
		isShieldActive = true;
		if (Utils.random(4) == 1)
			siphon = true;
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				isShieldActive = false;
				siphon = false;
			}
		}, 17);

		for (Entity e : this.getPossibleTargets()) {

		}
	}

	public int getPhase() {
		return phase;
	}

	public void nextPhase() {
		phase++;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}
}