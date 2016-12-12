package net.kagani.game.npc.others;

import java.util.concurrent.TimeUnit;

import net.kagani.executor.GameExecutorManager;
import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.HeadIcon;
import net.kagani.game.Hit;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public final class TormentedDemon extends NPC {

	private boolean[] demonPrayer;
	private int[] cachedDamage;
	private int shieldTimer, fixedAmount, prayerTimer, fixedCombatType,
			lastType;

	private static final HeadIcon[][] ICONS = { { new HeadIcon(440, 0) },// MELEE
			{ new HeadIcon(440, 1) },// RANGE
			{ new HeadIcon(440, 2) },// /MAGIC
	};

	public TormentedDemon(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		shieldTimer = 0;
		cachedDamage = new int[3];
		demonPrayer = new boolean[3];
		switchPrayers(Utils.random(3));
	}

	public void switchPrayers(int type) {
		if (type == 1)
			type = 2;
		else if (type == 2)
			type = 1;
		setNextNPCTransformation(8349 + type);
		demonPrayer[type] = true;
		resetPrayerTimer();
		requestIconRefresh();
	}

	@Override
	public HeadIcon[] getIcons() {
		return ICONS[getId() - 8349];
	}

	private void resetPrayerTimer() {
		prayerTimer = 27;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		if (Utils.random(100) <= 2)
			sendRandomProjectile();
		if (getCombat().process()) {// no point in processing
			if (shieldTimer > 0)
				shieldTimer--;
			if (prayerTimer > 0)
				prayerTimer--;
			if (prayerTimer == 0) {
				for (int i = 0; i < cachedDamage.length; i++) {
					if (cachedDamage[i] >= 3100) {
						demonPrayer = new boolean[3];
						switchPrayers(i);
						cachedDamage = new int[3];
					}
				}
			}
			for (int i = 0; i < cachedDamage.length; i++) {
				if (cachedDamage[i] >= 3100) {
					demonPrayer = new boolean[3];
					switchPrayers(i);
					cachedDamage = new int[3];
				}
			}
		}
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		if (hit.getSource() instanceof Player) {// darklight
			Player player = (Player) hit.getSource();
			int id = player.getEquipment().getWeaponId();
			if ((id == 6746 || id == 2402 || id == 732)
					&& (hit.getLook() == HitLook.MELEE_DAMAGE || hit.getLook() == HitLook.RANGE_DAMAGE)
					&& hit.getDamage() > 0) {
				shieldTimer = 60;
				player.getPackets().sendGameMessage(
						"The demon is temporarily weakened by your weapon.");
			}
		}
		for (int attackType = 0; attackType < demonPrayer.length; attackType++) {
			if (hit.getLook().getMark() == 133 + (attackType * 3)) {
				lastType = attackType;
				if (demonPrayer[attackType])
					hit.setDamage((int) (hit.getDamage() * .4));
				cachedDamage[attackType] += hit.getDamage();
			} else if (hit.getLook() == HitLook.MISSED) {
				cachedDamage[lastType] += 200;
			}
		}
		if (shieldTimer <= 0) {// 75% of damage is absorbed
			hit.setDamage((int) (hit.getDamage() * 0.25));
			setNextGraphics(new Graphics(1885));
		}
		super.handleIngoingHit(hit);
	}

	private void sendRandomProjectile() {
		WorldTile tile = new WorldTile(getX() + Utils.random(7), getY()
				+ Utils.random(7), getPlane());
		setNextAnimation(new Animation(10918));
		Projectile projectile = World.sendProjectileNew(this, tile, 1884, 34,
				0, 40, 1, 16, 0);
		for (Entity t : getPossibleTargets()) {
			if (t.withinDistance(tile, 1)) {
				if (t instanceof Player)
					((Player) t).getPackets().sendGameMessage(
							"The demon's magical attack splashes on you.");
				t.applyHit(new Hit(this, 2810, HitLook.MAGIC_DAMAGE, projectile
						.getEndTime()));
			}
		}
		World.sendGraphics(this, new Graphics(1883, projectile.getEndTime(), 0,
				0, true), tile);
	}

	@Override
	public void setRespawnTask() {
		if (!hasFinished()) {
			reset();
			setLocation(getRespawnTile());
			finish();
		}
		final NPC npc = this;
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				setFinished(false);
				World.addNPC(npc);
				npc.setLastRegionId(0);
				World.updateEntityRegion(npc);
				loadMapRegions();
				shieldTimer = 0;
				fixedCombatType = 0;
				fixedAmount = 0;
				demonPrayer = new boolean[3];
				cachedDamage = new int[3];
			}
		}, getCombatDefinitions().getRespawnDelay() * 600,
				TimeUnit.MILLISECONDS);
	}

	public static boolean atTD(WorldTile tile) {
		if ((tile.getX() >= 2560 && tile.getX() <= 2630)
				&& (tile.getY() >= 5710 && tile.getY() <= 5753))
			return true;
		return false;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0;
	}

	public int getFixedCombatType() {
		return fixedCombatType;
	}

	public void setFixedCombatType(int fixedCombatType) {
		this.fixedCombatType = fixedCombatType;
	}

	public int getFixedAmount() {
		return fixedAmount;
	}

	public void setFixedAmount(int fixedAmount) {
		this.fixedAmount = fixedAmount;
	}
}
