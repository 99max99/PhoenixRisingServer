package net.kagani.game.npc.others;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.kagani.Settings;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.item.Item;
import net.kagani.game.npc.Drop;
import net.kagani.game.npc.Drops;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.NPCDrops;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class TheDarkOracle extends NPC {

	public static final String[] TEXTS = new String[] {
			"Your soul belongs to me!", "Do you fear the dark abyss?",
			"Weak fools!", "The power of darkness is unstoppable!",
			"Even Bo can't stop me!", "I eat chaotics for lunch!",
			"Your foolish prayers making me stronger!" };

	/**
	 * Phase of the boss. 0 - Melee 1 - Ranged 2 - Magic 3 - Ultimate
	 */
	private int phase = 0;
	/**
	 * List of minions
	 */
	private List<NPC> minnions = new ArrayList<NPC>();

	static {
		Drops drops = new Drops(true);
		@SuppressWarnings("unchecked")
		List<Drop>[] dList = new ArrayList[Drops.UNCOMMON + 1];
		for (int i = 0; i < dList.length; i++)
			dList[i] = new ArrayList<Drop>();
		dList[Drops.ALWAYS].add(new Drop(592, 1, 1)); // ashes
		dList[Drops.ALWAYS].add(new Drop(995, 1000000, 5000000));
		/* colored sols */
		dList[Drops.COMMOM].add(new Drop(22207, 1, 1));
		dList[Drops.COMMOM].add(new Drop(22209, 1, 1));
		dList[Drops.COMMOM].add(new Drop(22211, 1, 1));
		dList[Drops.COMMOM].add(new Drop(22213, 1, 1));
		/* colored whips */
		dList[Drops.COMMOM].add(new Drop(15441, 1, 1));
		dList[Drops.COMMOM].add(new Drop(15442, 1, 1));
		dList[Drops.COMMOM].add(new Drop(15443, 1, 1));
		dList[Drops.COMMOM].add(new Drop(15444, 1, 1));

		drops.addDrops(dList);
		NPCDrops.addDrops(15185, drops);
	}

	public TheDarkOracle(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(0);
		// setCapDamage(500);
		setCombatLevel(9001);
		setName("Oracle of Darkness");
		setIntelligentRouteFinder(true);
		setRun(true);
		setForceAgressive(true);
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;

		Iterator<NPC> it$ = minnions.iterator();
		while (it$.hasNext()) {
			NPC npc = it$.next();
			if (npc != null && npc.hasFinished())
				it$.remove();
		}

		if (getRespawnTile() != null) {
			int deltaX = getX() - getRespawnTile().getX();
			int deltaY = getY() - getRespawnTile().getY();
			if (deltaX < -30 || deltaX > 30 || deltaY < -30 || deltaY > 30) {
				setNextWorldTile(getRespawnTile().transform(0, 0, 0));
			}
		}

		if (!getCombat().process()) {
			if (!checkAgressivity() && phase != 0
					&& getHitpoints() >= getMaxHitpoints()) {
				phase = 0;
				setNextNPCTransformation(15186);
			}
		}
		doRegenTransform();

	}

	public void doRegenTransform() {
		int hp = getHitpoints() + calculateRegenPower();
		if (hp > getMaxHitpoints())
			hp = getMaxHitpoints();
		setHitpoints(hp);

		if (phase == 0 && getId() != 15186)
			setNextNPCTransformation(15186);
		else if (phase == 1 && getId() != 15184)
			setNextNPCTransformation(15184);
		else if (phase == 2 && getId() != 15185)
			setNextNPCTransformation(15185);
		else if (phase == 3 && getId() != 15185)
			setNextNPCTransformation(15185);
	}

	public int calculateRegenPower() {
		int hp_percent = (getHitpoints() * 100) / getMaxHitpoints();
		if (hp_percent >= 90)
			return getHitpoints() / 500;
		else if (hp_percent >= 60)
			return getHitpoints() / 300;
		else if (hp_percent >= 30)
			return getHitpoints() / 200;
		else
			return getHitpoints() / 100;
	}

	public int calculateAttackSpeed(int base) {
		int hp_percent = (getHitpoints() * 100) / getMaxHitpoints();
		if (hp_percent >= 90)
			return base;
		else if (hp_percent >= 60)
			return base - 1;
		else if (hp_percent >= 30)
			return base - 2;
		else
			return base - 3;
	}

	public int calculateMaxHit(int base) {
		int hp_percent = (getHitpoints() * 100) / getMaxHitpoints();
		if (hp_percent >= 90)
			return base;
		else if (hp_percent >= 60)
			return base + 40;
		else if (hp_percent >= 30)
			return base + 80;
		else
			return base + 200;
	}

	public int calculateRecoilDamage(int dmg) {
		int hp_percent = (getHitpoints() * 100) / getMaxHitpoints();
		if (hp_percent >= 90)
			return 0;
		else if (hp_percent >= 60)
			return dmg / 4;
		else
			return dmg / 2;
	}

	@Override
	public ArrayList<Entity> getPossibleTargets(boolean checkPlayers,
			boolean checkNpcs) {
		ArrayList<Entity> list = super.getPossibleTargets(checkPlayers,
				checkNpcs);
		for (NPC minnion : minnions)
			list.remove(minnion);
		return list;
	}

	@Override
	public Item sendDrop(Player player, Drop drop) {
		int size = getSize();
		Item item = ItemDefinitions.getItemDefinitions(drop.getItemId())
				.isStackable() ? new Item(drop.getItemId(),
				(drop.getMinAmount() * Settings.getDropQuantityRate(player))
						+ Utils.random(drop.getExtraAmount()
								* Settings.getDropQuantityRate(player)))
				: new Item(drop.getItemId(), drop.getMinAmount()
						+ Utils.random(drop.getExtraAmount()));
		World.sendNews(player, "Oracle of Darkness dropped " + item.getAmount()
				+ " x " + item.getName(), 1);
		World.addGroundItem(item, new WorldTile(getCoordFaceX(size),
				getCoordFaceY(size), getPlane()), null, false, 60);
		return item;
	}

	@Override
	public void sendDeath(final Entity source) {
		for (NPC npc : minnions)
			npc.finish();
		minnions.clear();

		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		setNextGraphics(new Graphics(2929));

		if (phase == 0) {
			setNextForceTalk(new ForceTalk(
					"Uhh.. This is just the beginning..."));
		} else if (phase == 1) {
			setNextForceTalk(new ForceTalk(
					"It's getting annoying, no one will dare to stop me!"));
		} else if (phase == 2) {
			setNextForceTalk(new ForceTalk(
					"THIS IS IT! FACE MY ULTIMATE POWER!!"));
		} else {
			setNextForceTalk(new ForceTalk("Impossible..."));
		}

		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (phase == 0) {
					phase = 1;
					setHitpoints(50000);
					setNextNPCTransformation(15184);
					for (Entity trg : getPossibleTargets(true, true)) {
						if (trg == TheDarkOracle.this
								|| !trg.withinDistance(TheDarkOracle.this, 3))
							continue;
						trg.applyHit(new Hit(TheDarkOracle.this, Utils
								.random(50) + 100, HitLook.DESEASE_DAMAGE));
					}
				} else if (phase == 1) {
					phase = 2;
					setHitpoints(100000);
					setNextNPCTransformation(15185);
					for (Entity trg : getPossibleTargets(true, true)) {
						if (trg == TheDarkOracle.this
								|| !trg.withinDistance(TheDarkOracle.this, 3))
							continue;
						trg.applyHit(new Hit(TheDarkOracle.this, Utils
								.random(200) + 100, HitLook.DESEASE_DAMAGE));
					}
				} else if (phase == 2) {
					phase = 3;
					setHitpoints(100000);
					for (Entity trg : getPossibleTargets(true, true)) {
						if (trg == TheDarkOracle.this
								|| !trg.withinDistance(TheDarkOracle.this, 3))
							continue;
						trg.applyHit(new Hit(TheDarkOracle.this, Utils
								.random(600) + 100, HitLook.DESEASE_DAMAGE));
					}
				} else {
					for (Entity trg : getPossibleTargets(true, true)) {
						if (trg == TheDarkOracle.this
								|| !trg.withinDistance(TheDarkOracle.this, 3))
							continue;
						trg.applyHit(new Hit(TheDarkOracle.this, Utils
								.random(800) + 100, HitLook.DESEASE_DAMAGE));
					}
					World.sendNews("Oracle of Darkness has been killed!",
							World.WORLD_NEWS);
					TheDarkOracle.super.sendDeath(source);
					return;
				}
			}
		}, 3);
	}

	public void registerMinnion(NPC minnion) {
		minnions.add(minnion);
	}

	public List<NPC> getMinnions() {
		return minnions;
	}

	public int getPhase() {
		return phase;
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (minnions.size() > 0) {
			int totalHP = 0;
			for (NPC minnion : minnions)
				totalHP += minnion.getHitpoints();
			if (totalHP > 0) {
				hit.setHealHit();
				super.handleIngoingHit(hit);
				return;
			}
		}

		super.handleIngoingHit(hit);

		if (hit.getSource() != null) {
			int recoil = calculateRecoilDamage(hit.getDamage());
			if (recoil > 0)
				hit.getSource().applyHit(
						new Hit(this, recoil, HitLook.REFLECTED_DAMAGE));
		}
	}

	@Override
	public double getMagePrayerMultiplier() {
		return phase + 1.5;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return phase + 1.5;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return phase + 1.5;
	}
}
