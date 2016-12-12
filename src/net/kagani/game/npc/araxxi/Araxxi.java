package net.kagani.game.npc.araxxi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.item.Item;
import net.kagani.game.item.ItemsContainer;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.player.TimersManager.RecordKey;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class Araxxi extends NPC {

	// private Bladed blade;
	// private Imbued imbude;
	// private Spitting spit;

	private List<NPC> minions = new ArrayList<NPC>();

	@SuppressWarnings("unused")
	private Iterator<NPC> iter = minions.iterator();

	public boolean canFollow = false;

	private int blade = 19458;
	private int imbude = 19459;
	private int spit = 19460;

	public int attackNumber = 0;
	public boolean canSpecial = false;

	public double damageMulti = 0.0;
	public double healingMulti = 0.0;
	public int minionNumber = 0;
	public int startingHp = 0;

	public int playerEnrageLevel = 0;

	private Player attacker;

	public Araxxi(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned, Player attacker) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setCapDamage(10000);
		setForceAgressive(true);
		setForceMultiArea(true);
		isFinished = false;
		attackNumber = 0;
		canSpecial = false;
		canFollow = false;
		this.attacker = attacker;
		// setCantFollowUnderCombat(true);
		setIntelligentRouteFinder(true);
		setForceFollowClose(false);
		// prepareRewards();
	}

	public void EnrageNumbers() {
		// default values - if there is 0 enrage
		// multi values are 1 because x *1 = x;
		damageMulti = 1.0;
		healingMulti = 1.0;
		minionNumber = 3;
		startingHp = 100000;
		if (playerEnrageLevel != 0) {
			for (int i = 0; i < playerEnrageLevel; i++) {
				damageMulti += 0.15;
				healingMulti += 0.15;
				minionNumber += 1;
				startingHp += 2500;
			}
		}
	}

	public void spawnSpiders() {
		WorldTile tile = new WorldTile(attacker, 1);
		int value = Utils.random(3);
		if (minions.size() < minionNumber) {
			switch (value) {
			case 0:
				NPC spider = new NPC(blade, tile, -1, true, true);
				spider.setForceMultiArea(true);
				spider.getCombat().setTarget(this.getCombat().getTarget());
				spider.setNoClipWalking(true);
				spider.setForceFollowClose(true);
				spider.removeClipping();
				minions.add(spider);
				break;
			case 1:
				spider = new NPC(imbude, tile, -1, true, true);
				spider.setForceMultiArea(true);
				spider.getCombat().setTarget(this.getCombat().getTarget());
				spider.setNoClipWalking(true);
				spider.removeClipping();
				minions.add(spider);
				break;
			case 2:
				spider = new NPC(spit, tile, -1, true, true);
				spider.setForceMultiArea(true);
				spider.getCombat().setTarget(this.getCombat().getTarget());
				spider.setNoClipWalking(true);
				spider.removeClipping();
				minions.add(spider);
				break;
			}
		}
	}

	public void removeSpider() {
		for (Iterator<NPC> iter = minions.iterator(); iter.hasNext();) {
			NPC npc = iter.next();
			npc.finish();
			iter.remove();
		}
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		Entity player = hit.getSource();
		if (player instanceof Player) {
			Player p = (Player) player;
			if (p.araxxorHeal) {
				player.applyHit(new Hit(player, hit.getDamage(),
						HitLook.REFLECTED_DAMAGE));
			}
		}
		super.handleIngoingHit(hit);
	}

	public ItemsContainer<Item> getRewards() {
		return rewards;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		for (Iterator<NPC> iter = minions.iterator(); iter.hasNext();) {
			NPC npc = iter.next();
			if (npc.hasFinished() == true)
				iter.remove();
		}
		int maxhp = getMaxHitpoints();
		if (maxhp > getHitpoints() && getPossibleTargets().isEmpty())
			setHitpoints(maxhp);
		// should never happen in an instance
		// unless the player dies
		if (getPossibleTargets().isEmpty()) {
			removeSpider();
			// finish();
		}
	}

	public int AraxDeathZ;
	public int AraxDeathY;
	public int AraxDeathX;

	@Override
	public void sendDeath(Entity source) {
		removeSpider();
		AraxDeathX = this.getX() + 1;
		AraxDeathY = this.getY();
		AraxDeathZ = this.getPlane();
		setCantInteract(false);
		setLocked(true);
		prepareRewards();
		if (attacker.araxxiEnrage == 0) {
			attacker.araxxiEnrageTimer = Utils.currentTimeMillis();
		}
		attacker.araxxiEnrage++;
		startDeath(attacker);
		// super.sendDeath(source);

	}

	/**
	 * The rewards container.
	 */
	private final ItemsContainer<Item> rewards = new ItemsContainer<>(10, true);

	private static final int[][] REWARDS = { { 31718, 1, 1, 3 }, // spider leg
																	// middle
			{ 31719, 1, 1, 3 }, // spider leg top
			{ 31720, 1, 1, 3 }, // spider leg bottom
			{ 1514, 125, 300, 57 }, // magic logs
			{ 1516, 300, 600, 57 }, // yew logs
			{ 454, 300, 600, 57 }, // coal
			{ 29863, 1, 2, 15 }, // sirenic scale
			{ 6572, 1, 2, 15 }, // uncut onyx
			{ 450, 100, 100, 57 }, // uncut onyx
			{ 1748, 50, 100, 57 }, // black d hide
			{ 26750, 2, 6, 57 }, // overload flasks (6)
			{ 23400, 2, 6, 57 }, // full restore flasks(6)
			{ 23352, 2, 6, 57 }, // saradomin brew flasks
			{ 15273, 25, 35, 57 }, // rocktails
			{ 31737, 25, 35, 85 }, // aracite arrow
			{ 452, 50, 125, 57 }, // rune ore
			{ 9245, 150, 250, 57 }, // onyx bolt(e)
			{ 1392, 25, 50, 57 }, // battlestaff
			{ 5316, 5, 15, 57 }, // magic seed
			{ 5303, 10, 15, 57 }, // dwarf weed seed
			{ 212, 15, 25, 57 }, // grimy avatoe
			{ 218, 15, 25, 57 }, // grimy dwarfweed
			{ 2486, 15, 25, 57 }, // Grimy lantadyme
			{ 31722, 1, 1, 1 }, // fang
			{ 31724, 1, 1, 1 }, // web
			{ 31723, 1, 1, 1 }, // eye
			{ 33870, 1, 1, 10 } // pheromone
	};

	public void prepareRewards() {
		rewards.add(new Item(995, Utils.random(50000, 150000)));
		List<Item> rewardTable = new ArrayList<Item>();
		for (int[] reward : REWARDS) {
			Item item = new Item(reward[0], reward[1]
					+ Utils.random(reward[2] - reward[1]));
			for (int i = 0; i < reward[3]; i++) {
				if (item.getId() == 31722 || item.getId() == 31724
						|| item.getId() == 31723) {
					if (Utils.random(100) > 65) {
						rewardTable.add(item);
					}
				} else if (item.getId() == 31718 || item.getId() == 31719
						|| item.getId() == 31720) {
					if (Utils.random(100) > 50) {
						rewardTable.add(item);
					}
				} else {
					rewardTable.add(item);
				}
			}
		}
		Collections.shuffle(rewardTable);
		for (int i = 0; i < 1 + 3; i++) {
			rewards.add(rewardTable.get(Utils.random(rewardTable.size())));
		}
	}

	private void startDeath(Player player) {
		WorldTasksManager.schedule(new WorldTask() {
			int time;

			@Override
			public void run() {
				time++;
				if (time == 1) {
					DeathAnim();

				}
				if (time == 9) {
					Death();
					final WorldObject AraxxorBody = new WorldObject(91673, 10,
							0, AraxDeathX, AraxDeathY, AraxDeathZ, player);
					World.spawnTemporaryDivineObject(AraxxorBody, 120000,
							player); // time object will stay in miliseconds
					player.getPackets().sendGameMessage(
							"Araxxi's Corpse will only last 2 minutes.");
					stop();
				}
			}
		}, 0, 0);
	}

	public void DeathAnim() {
		this.setLocked(true);
		this.setNextAnimation(new Animation(24106));

	}

	public boolean isFinished = false;;

	public void Death() {
		isFinished = true;
		this.finish();
	}

	public void openRewardChest(Player player) {
		increaseKills(RecordKey.ARAXXI, false);
		player.getInterfaceManager().sendCentralInterface(1284);
		player.getPackets().sendIComponentText(1284, 28, "Araxxi's Corpse");
		player.getPackets().sendInterSetItemsOptionsScript(1284, 7, 100, 8, 3,
				"Take", "Bank", "Discard", "Examine");
		player.getPackets().sendUnlockIComponentOptionSlots(1284, 7, 0, 10, 0,
				1, 2, 3);
		player.getPackets().sendItems(100, rewards);
	}
}