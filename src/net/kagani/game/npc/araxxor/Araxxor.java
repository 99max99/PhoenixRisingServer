package net.kagani.game.npc.araxxor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kagani.Settings;
import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
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
import net.kagani.game.player.content.Combat;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public final class Araxxor extends NPC {
	int attackStage;
	public boolean canLoot;
	public int xDifference;

	boolean attackComplete;
	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -7709946348178377601L;

	/**
	 * The attacks for the first phase.
	 */
	private static final AraxxorAttack[] PHASE_1_ATTACKS = { // new Cacoon() new
																// RangeAttack(),
																// new
																// MeleeAttack()
																// new Cleave()
			new MeleeAttack(), new MagicAttack(), new RangeAttack()

	};

	/**
	 * The attacks for the second phase.
	 */
	private static final AraxxorAttack[] PHASE_2_ATTACKS = {
			// new Cleave(),
			new WebShield(), new Cacoon() };

	/**
	 * The attacks for the third phase.
	 */
	private static final AraxxorAttack[] PHASE_3_ATTACKS = { new MeleeAttack(),
			new MagicAttack(), new RangeAttack(), new EggAttack() };

	/**
	 * The attacks for the last phase.
	 */
	private static final AraxxorAttack[] PHASE_4_ATTACKS = { // Super fire
			// attack twice,
			// to make it
			// more often.
			new MeleeAttack(), new RangeAttack(), new WebShield(),
			new Cacoon(), new AcidicSpider() };

	/**
	 * The rewards
	 */
	private static final int[][] REWARDS = { { 31718, 1, 1, 4 }, // spider leg
																	// middle
			{ 31719, 1, 1, 4 }, // spider leg top
			{ 31720, 1, 1, 4 }, // spider leg bottom
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
			{ 31722, 1, 1, 3 }, // fang
			{ 31724, 1, 1, 3 }, // web
			{ 31723, 1, 1, 3 }, // eye
	};

	/**
	 * The waking up animation.
	 */
	private static final Animation WAKE_UP_ANIMATION = new Animation(24076);

	/**
	 * The sleeping animation.
	 */
	@SuppressWarnings("unused")
	private static final Animation SLEEP_ANIMATION = new Animation(16742);

	/**
	 * The player.
	 */
	private final Player attacker;

	/**
	 * The Araxxor state.
	 */
	private SpiderState state = SpiderState.MELEE;

	/**
	 * The amount of ticks passed.
	 */
	private int ticks;

	/**
	 * The next attack tick count.
	 */
	private int nextAttack;

	/**
	 * The current attacks.
	 */
	private AraxxorAttack[] attacks;

	/**
	 * The current phase.
	 */
	private int phase;

	/**
	 * The region base location.
	 */
	private final WorldTile base;

	/**
	 * The list of spiders.
	 */
	private final List<NPC> spiders = new ArrayList<NPC>();

	/**
	 * The current active artifact.
	 */
	private WorldObject activeArtifact;

	/**
	 * The rewards container.
	 */
	private final ItemsContainer<Item> rewards = new ItemsContainer<>(10, true);

	/**
	 * The last amount of hitpoints.
	 */
	private int lastHitpoints = -1;
	@SuppressWarnings("unused")
	private boolean fightStarted;
	private int AcidLevel;
	private int platformStand;
	private int FloorAcidLevel;
	private int RampedRemoved;
	private int TransformedIntoAcid;
	private boolean npcLocked;

	/**
	 * Constructs a new {@code Araxxor} {@code Object}.
	 * 
	 * @param attacker
	 *            The player.
	 * @param tile
	 *            The world tile to set the spider on.
	 * @param base
	 *            The dynamic region's base location.
	 */
	public static int npcid = 19457;// 19462;

	public Araxxor(Player attacker, WorldTile tile, WorldTile base) {
		super(npcid, tile, -1, true, true);
		super.setForceMultiArea(true);
		// setForceTargetDistance(5);
		this.base = base;
		this.canLoot = true;
		this.attacker = attacker;
		this.nextAttack = 10;
		this.setCanBeAttackFromOutOfArea(true);
		this.setSpawned(true);
		setHitpoints(getMaxHitpoints());
		// activeArtifact = new WorldObject(70776, 10, 0, base.transform(33, 31,
		// 0));
		this.setPhase(1);
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (hit != null && hit.getDamage() > 10000)
			hit.setDamage(10000);
		Entity player = hit.getSource();
		if (player instanceof Player) {
			Player p = (Player) player;
			if (p.araxxorHeal)
				player.applyHit(new Hit(player, hit.getDamage(),
						HitLook.REFLECTED_DAMAGE));
		}
		super.handleIngoingHit(hit);
	}

	@Override
	public void setHitpoints(int hitpoints) {
		super.setHitpoints(hitpoints);
		if (attacker == null) {
			return;
		}
		if (lastHitpoints != hitpoints) {
			attacker.getPackets().sendExecuteScript(1923,
					getMaxHitpoints() - hitpoints);
			lastHitpoints = hitpoints;
		}
	}

	public int AraxDeathZ;
	public int AraxDeathY;
	public int AraxDeathX;

	@Override
	public void sendDeath(Entity source) {
		if (attacker.AraxxorLastState == true
				&& attacker.AraxxorThirdStage == false) {
			// attacker.getPackets().sendGameMessage("KILLCOUNT SENT");
			AraxDeathX = this.getX() + 1;
			AraxDeathY = this.getY();
			AraxDeathZ = this.getPlane();
			setCantInteract(false);
			setLocked(true);
			npcLocked = true;
			attacker.AraxxorPause = true;
			prepareRewards();
			startDeath(Arraxi, attacker);
			// attacker.fightEnded = true;

		} else if (attacker.AraxxorThirdStage == true
				&& attacker.AraxxorLastState == false) {
			setCantInteract(false);
			startStageFour(this, attacker);
			attacker.AraxxorThirdStage = false;
		} else
			this.setHitpoints(10000);

	}

	private void startDeath(final NPC araxxor, final Player attacker) {
		WorldTasksManager.schedule(new WorldTask() {
			int time;

			@Override
			public void run() {
				time++;
				if (time == 1) {
					npcLocked = true;
					DeathAnim();

				}
				if (time == 9) {
					npcLocked = true;
					Death();
					final WorldObject AraxxorBody = new WorldObject(91673, 10,
							0, AraxDeathX, AraxDeathY, AraxDeathZ, attacker); // object
																				// with
																				// animation
																				// that
																				// player
																				// will
																				// mine
					World.spawnTemporaryDivineObject(AraxxorBody, 120000,
							attacker); // time
										// object
										// will
										// stay
										// in
										// miliseconds
					// World.spawnObject(new WorldObject (91673, 10, 0,
					// AraxDeathX, AraxDeathY, AraxDeathZ), true);
					// World.spawnObject(AraxxorBody);
					attacker.getPackets().sendGameMessage(
							"Araxxor's Corpse will only last 2 minutes.");
					// attacker.AraxxorBodyX = araxxor.getX() + 1;
					// attacker.AraxxorBodyY = araxxor.getY();
					// attacker.AraxxorBodyZ = araxxor.getPlane();
					attacker.AraxDeathX = AraxDeathX;
					attacker.AraxDeathY = AraxDeathY;
					attacker.AraxDeathZ = AraxDeathZ;
					stop();
				}
			}
		}, 0, 0);
	}

	public void DeathAnim() {
		this.setLocked(true);
		npcLocked = true;
		attacker.AraxxorPause = true;
		this.setNextAnimation(new Animation(24106));

	}

	public void Death() {
		this.finish();
	}

	@Override
	public int getMaxHitpoints() {
		return 75000;
	}

	@Override
	public void processNPC() {
		attacker.EGGX = attacker.getX() - base.getX();
		attacker.EGGY = attacker.getY() - base.getY();
		attacker.FINALAGGX = attacker.EGGX + base.getX();
		attacker.FINALAGGY = attacker.EGGY + base.getY();
		if (this.getX() >= base.getX() + (187 - 64)
				&& this.getX() <= base.getX() + (201 - 64)) {
			platformStand++;
			if (platformStand == 3) {
				this.AcidLevel += 2;
				attacker.AcidLevel = AcidLevel;
				if (TransformedIntoAcid == 0) {
					switchState(SpiderState.MELEE_ACID);
					this.TransformedIntoAcid = 1;
				}
				if (!attacker.AraxxorEggBurst)
					setPhase(3);
				if (Settings.DEBUG)
					attacker.getPackets().sendGameMessage(
							"Araxxor Acid Level: " + AcidLevel + "");
				if (AcidLevel >= 100)
					attacker.getPackets().sendGameMessage(
							"Acid Level is now 100.");
				platformStand = 0;
			}
		} else if (this.getX() >= base.getX() + (202 - 64)
				&& this.getX() <= base.getX() + (216 - 64)) {
			if (this.AcidLevel >= 1) {
				if (this.RampedRemoved == 0) {
					World.removeObject(new WorldObject(91520, 10, 0, base
							.transform(135, 20, 0))); // burn
														// option
					World.spawnObject(new WorldObject(91521, 10, 0, base
							.transform(135, 20, 0))); // Huge
														// Ramp
														// in
														// center
														// of
														// map
														// (
														// stage
														// 0
														// no
														// acid
														// )
					this.RampedRemoved = 1;
				}
				if (Settings.DEBUG)
					attacker.getPackets().sendGameMessage(
							"Ramp acid level: " + FloorAcidLevel + "");
				if (FloorAcidLevel >= 100)
					attacker.getPackets().sendGameMessage(
							"Ramp Acid Level is now 100.");
				AcidLevel--;
				FloorAcidLevel += 2;
			} else if (this.FloorAcidLevel >= 15) {
				if (this.RampedRemoved == 1) {
					WorldTasksManager.schedule(new WorldTask() {
						int time;

						@Override
						public void run() {
							time++;
							// attacker.getPackets().sendGameMessage(""+time+"");
							if (time == 1 && RampedRemoved == 1) {
								switchState(SpiderState.MELEE);
								// attacker.getPackets().sendGameMessage("acid
								// floor 1");
								World.spawnObject(new WorldObject(91670, 10, 0,
										base.transform(217 - 64, 86 - 64, 0)));
								World.spawnObject(new WorldObject(91526, 10, 0,
										base.transform(135, 20, 0)));
								FloorAcidLevel = 0;
								RampedRemoved = 2;
								stop();
							}

						}
					}, 0, 0);
					this.startStageThree(this, attacker);
				}
			}
		}

		// if(fightStarted = true) { // process for fight
		sendFollow(); // DONT REMOVE OR ARAXOOR CANNOT WALK
		if (ticks > 5 && !attacker.isAtDynamicRegion()) {
			finish();
			return;
		}

		if (ticks > nextAttack) {
			canLoot = false;
			attacker.getPackets()
					.sendGameMessage(
							"An error as occurred and you will no longer receive loot. Please teleport out and try again.");
		}

		if (ticks == nextAttack) {
			if (npcLocked)
				return;
			AraxxorAttack attack;
			if (attacker.AraxxorAttackCount < 6) {
				// attacker.getPackets().sendGameMessage(""+attacker.AraxxorAttackCount+"");
				while (!(attack = attacks[Utils.random(attacks.length)])
						.canAttack(this, attacker))
					;
				this.setNextAttack(attack.attack(this, attacker));
				this.setPhase(1);
			} else if (attacker.AraxxorAttackCount >= 6) {
				this.setPhase(2);
				while (!(attack = attacks[Utils.random(attacks.length)])
						.canAttack(this, attacker))
					;
				this.setNextAttack(attack.attack(this, attacker));
			} else {
				// attacker.getPackets().sendGameMessage("else");
				// attacker.getPackets().sendGameMessage("else");
				// attacker.getPackets().sendGameMessage("else");
				// attacker.getPackets().sendGameMessage("else");
				// attacker.getPackets().sendGameMessage("else");
				while (!(attack = attacks[Utils.random(attacks.length)])
						.canAttack(this, attacker))
					;
				this.setNextAttack(attack.attack(this, attacker));
			}

			// / AraxxorAttack attack;
			// while (!(attack =
			// / attacks[Utils.random(attacks.length)]).canAttack(this,
			// / attacker));
			// this.setNextAttack(attack.attack(this, attacker));
		} else { // / start of fight
			if (ticks > 5 && !attacker.isAtDynamicRegion()) {
				finish();
				return;
			}
			if (ticks == -1) {
				return;
			}
		}
		ticks++;
		if (ticks == 1) {
			super.setNextAnimation(WAKE_UP_ANIMATION);
		} else if (ticks == 5) {
			setCantInteract(false);
			switchState(SpiderState.MELEE);
			setLocked(false);
			npcLocked = false;
			attacker.AraxxorPause = false;
			setForceTargetDistance(5);
			fightStarted = true;
		} else if (ticks == nextAttack) {
			if (!isLocked()) {
				if (npcLocked) {
					if (attackStage >= 0 && attackStage != 6) {
						MeleeAttack(this, attacker);
						attackStage++;
						// attacker.getPackets().sendGameMessage("attack stage2:
						// "+attackStage+"");
					} else {
						if (attackStage == 6) {
							attackStage = 0;
							// WebShield(this, attacker);
							// attacker.getPackets().sendGameMessage("attack
							// stage2:
							// "+attackStage+"");
						}
					}
				}
			}
		}
		// }

	}

	@Override
	public void finish() {
		// for (Spiders s : spiders) {
		// s.finish();
		// }
		super.finish();
	}

	@Override
	public int getDirection() {
		return 0;
	}

	/**
	 * Gets the attacker.
	 * 
	 * @return The attacker.
	 */
	public Player getAttacker() {
		return attacker;
	}

	/**
	 * Gets the state.
	 * 
	 * @return The state.
	 */
	public SpiderState getState() {
		return state;
	}

	/**
	 * Switches the spider state.
	 * 
	 * @param state
	 *            The state.
	 */
	@SuppressWarnings("incomplete-switch")
	public void switchState(SpiderState state) {
		this.state = state;
		if (state.getMessage() != null) {
			String[] messages = state.getMessage().split("(nl)");
			for (String message : messages) {
				attacker.getPackets().sendGameMessage(
						message.replace("(", "").replace(")", ""));
			}
		}
		super.setNextNPCTransformation(state.getNpcId());
		switch (state) {
		}
	}

	/**
	 * Opens the reward chest.
	 * 
	 * @param replace
	 *            If the chest should be replaced with an opened one.
	 */
	public void openRewardChest(boolean replace) {
		attacker.getInterfaceManager().sendCentralInterface(1284);
		attacker.getPackets().sendIComponentText(1284, 28, "Araxxi's Corpse");
		attacker.getPackets().sendInterSetItemsOptionsScript(1284, 7, 100, 8,
				3, "Take", "Bank", "Discard", "Examine");
		attacker.getPackets().sendUnlockIComponentOptionSlots(1284, 7, 0, 10,
				0, 1, 2, 3);
		attacker.getPackets().sendItems(100, rewards);

		if (replace) {
			World.spawnObject(new WorldObject(70817, 10, 0, base.transform(30,
					28, -1)));
		}
	}

	/**
	 * Araxxor Goes into the air and lands again to start stage 3.
	 * 
	 */
	public void startStageThree(final NPC Araxxor, final Player attacker) {
		WorldTasksManager.schedule(new WorldTask() {
			int time;

			@Override
			public void run() {
				time++;
				if (time == 1) {
					setLocked(true);
					npcLocked = true;
					attacker.AraxxorPause = true;
					Araxxor.setLocked(true);
					setCantInteract(true);
					Araxxor.setNextAnimation(new Animation(24056));
				}
				if (time == 7) {
					Araxxor.setNextAnimation(new Animation(24076));
				}
				if (time == 7) {
					Araxxor.setHitpoints(10000);
					attacker.AraxxorThirdStage = true;
					Araxxor.setNextWorldTile(base.transform(231 - 64, 90 - 64,
							0));
				}
				if (time == 9) {
					setCantInteract(false);
					Araxxor.setNextAnimation(new Animation(24076));
					setLocked(false);
					npcLocked = false;
					Araxxor.setLocked(false);
					stop();
				}

			}
		}, 0, 0);
	}

	NPC Arraxi; // Used to spawn a temp Araxxi for the beginning of last stage

	public void startStageFour(final NPC Araxxor, final Player attacker) {
		WorldTasksManager.schedule(new WorldTask() {
			int time;

			@Override
			public void run() {
				time++;
				if (time == 1) {
					Araxxor.setNextWorldTile(base.transform(252 - 64, 88 - 64,
							0));
					// finalWalk(Araxxor, attacker);
					// attacker.setNextWorldTile(base.transform(110, 38, 0));
					attacker.setNextWorldTile(base.transform(247 - 64, 86 - 64,
							0));
				}
				if (time == 4) {
					// finalWalk(Araxxor, attacker);
					setCantInteract(true);
					setLocked(true);
					npcLocked = true;
					// Araxxor.setLocked(true);
					attacker.AraxxorPause = true;
					// Araxxor.setNextWorldTile(base.transform(115, 38, 0));
					// Araxxor.setFreezeDelay(15);
					// attacker.lock(15);
				}
				if (time == 7) {
					Araxxor.setHitpoints(50000);
					// Araxxor.setFreezeDelay(20);
					Araxxor.setNextAnimation(new Animation(24062));
					// Araxxor.setNextGraphics(new Graphics (4999));
					// NPC npc1 = World.spawnNPC(19464, base.transform(115, 38,
					// 0), -1, true);
					// npc1.setFreezeDelay(10);
					// npc1.setNextAnimation(new Animation(24083));
					// npc1.setNextGraphics(new Graphics (4999));
					// Arraxi = npc1;

					Araxxor.setNextGraphics(new Graphics(5000));
				}
				if (time == 9) {
					Araxxor.setNextGraphics(new Graphics(4999));
				}
				if (time == 16) {

					// Araxxor.setHitpoints(getMaxHitpoints()); // used for
					// final version
					Araxxor.setHitpoints(150000);
					// Araxxor.setNextWorldTile(base.transform(115, 38, 0));
					setCantInteract(false);
					setLocked(false);
					npcLocked = false;
					Araxxor.setLocked(false);
					attacker.AraxxorPause = false;
					attacker.AraxxorThirdStage = false;
					attacker.AraxxorLastState = true;
				}
				if (time == 18) {
					switchState(SpiderState.ARAXXI);
					stop();
				}
			}
		}, 0, 0);
	}

	/**
	 * Opens spider follows player ( HAS TO BE CALLED )
	 * 
	 * @param needed
	 *            this crap just to get the fucking spider to walk ughhh
	 */
	protected void sendFollow() {
		if (getLastFaceEntity() != attacker.getClientIndex()) {
			setNextFaceEntity(attacker);
		}
		// if (getFreezeDelay() >= Utils.currentTimeMillis()) {
		// return;
		// }
		int size = getSize();
		int distanceX = attacker.getX() - getX();
		int distanceY = attacker.getY() - getY();
		if (distanceX < size && distanceX > -1 && distanceY < size
				&& distanceY > -1 && !attacker.hasWalkSteps()
				&& !hasWalkSteps()) {
			resetWalkSteps();
			if (!addWalkSteps(attacker.getX() + 1, getY())) {
				resetWalkSteps();
				if (!addWalkSteps(attacker.getX() - size, getY())) {
					resetWalkSteps();
					if (!addWalkSteps(getX(), attacker.getY() + 1)) {
						resetWalkSteps();
						addWalkSteps(getX(), attacker.getY() - size);
					}
				}
			}
			return;
		}
		if ((!clipedProjectile(attacker, true)) || distanceX > size
				|| distanceX < -1 || distanceY > size || distanceY < -1) {
			resetWalkSteps();
			addWalkStepsInteract(attacker.getX(), attacker.getY(), getRun() ? 2
					: 1, size, true);
			return;
		}
		resetWalkSteps();
	}

	/**
	 * Sets the state.
	 * 
	 * @param state
	 *            The state to set.
	 */
	public void setState(SpiderState state) {
		this.state = state;
	}

	/**
	 * Gets the nextAttack.
	 * 
	 * @return The nextAttack.
	 */
	public int getNextAttack() {
		return nextAttack;
	}

	/**
	 * Sets the nextAttack value (current ticks + the given amount).
	 * 
	 * @param nextAttack
	 *            The amount.
	 */
	public void setNextAttack(int nextAttack) {
		this.nextAttack = ticks + nextAttack;
	}

	/**
	 * Gets the phase.
	 * 
	 * @return The phase.
	 */
	public int getPhase() {
		return phase;
	}

	/**
	 * Sets the phase.
	 * 
	 * @param phase
	 *            The phase to set.
	 */
	public void setPhase(int phase) {
		this.phase = phase;
		switch (phase) {
		case 1:
			this.attacks = PHASE_1_ATTACKS;
			break;
		case 2:
			this.attacks = PHASE_2_ATTACKS;
			break;
		case 3:
			this.attacks = PHASE_3_ATTACKS;
			break;
		case 4:
			this.attacks = PHASE_4_ATTACKS;
			break;
		case 5:
			setCantInteract(true);
			// for (Spiders spiders : spiders) {
			// spiders.finish();
			// }
			ticks = -22;
			prepareRewards();
			attacker.setKilledAraxxor(true);
			// attacker.getPackets().sendSpawnedObject(new WorldObject(91673 ,
			// 10, 0, base.transform(80, 40, -1)));
			attacker.getPackets().sendGameMessage(
					"<col=33FFFF>You have slain the evil spiders.</col>");
			break;
		}
	}

	/**
	 * Prepares the rewards.
	 */
	public void prepareRewards() {
		rewards.add(new Item(995, 150000));
		List<Item> rewardTable = new ArrayList<Item>();
		for (int[] reward : REWARDS) {
			Item item = new Item(reward[0], reward[1]
					+ Utils.random(reward[2] - reward[1]));
			for (int i = 0; i < reward[3]; i++) {
				rewardTable.add(item);
			}
		}
		Collections.shuffle(rewardTable);
		for (int i = 0; i < 1 + 3; i++) {
			rewards.add(rewardTable.get(Utils.random(rewardTable.size())));
		}
	}

	/**
	 * Gets the base.
	 * 
	 * @return The base.
	 */
	public WorldTile getBase() {
		return base;
	}

	/**
	 * Gets the amount of ticks.
	 * 
	 * @return The amount of ticks.
	 */
	public int getTicks() {
		return ticks;
	}

	/**
	 * Gets the souls.
	 * 
	 * @return The souls.
	 */
	// public List<Spiders> getSpiders() {
	// return spiders;
	// }

	/**
	 * Gets the activeArtifact.
	 * 
	 * @return The activeArtifact.
	 */
	public WorldObject getActiveArtifact() {
		return activeArtifact;
	}

	/**
	 * Sets the activeArtifact.
	 * 
	 * @param activeArtifact
	 *            The activeArtifact to set.
	 */
	public void setActiveArtifact(WorldObject activeArtifact) {
		this.activeArtifact = activeArtifact;
	}

	/**
	 * Gets the rewards.
	 * 
	 * @return The rewards.
	 */
	public ItemsContainer<Item> getRewards() {
		return rewards;
	}

	public void TransformAcidForm(Entity source) {
		this.switchState(SpiderState.MELEE_ACID);

	}

	// COMBAT ATTACKS
	public void WebShield(final NPC npc, final Player target) {

		if (target.AraxxorAttackCount >= 6) {
			npc.setLocked(true);
			target.AraxxorCompleteAttack = false;
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					npc.setNextAnimation(new Animation(24075));
					npc.setNextGraphics(new Graphics(4987));
					npc.heal(10000);
					// target.AraxxorCompleteAttack = true;
					target.AraxxorAttackCount = 0;
					stop();
				}

			}, 0, 0);
		}
	}

	public void finalWalk(final NPC npc, final Player p) {
		// p.getPackets().sendGameMessage("STARTED WALKING");
		npc.setForceWalk(base.transform(119, 41, 0));
	}

	public void MeleeAttack(final NPC npc, final Player target) {

		@SuppressWarnings("unused")
		WorldTile npcTile = (new WorldTile(base.transform(19, 41, 0)));
		WorldTasksManager.schedule(new WorldTask() {
			int time;

			@Override
			public void run() {
				time++;
				// target.getPackets().sendGameMessage(""+time+"");
				int hit = 0;
				if (time == 10) {
					if (target.AraxxorAttackCount < 6) {
						target.AraxxorAttackCount++;
						target.AraxxorCompleteAttack = false;
						hit = Utils.random(0 + Utils.random(150), 360);
						npc.setNextAnimation(new Animation(24046));
						target.setNextAnimation(new Animation(Combat
								.getDefenceEmote(target)));
						target.applyHit(new Hit(npc, hit,
								hit == 0 ? HitLook.MISSED
										: HitLook.MELEE_DAMAGE));
					} else {
						WebShield(npc, attacker);
					}
				}

			}
		}, 0, 0);
	}

	public void cacoonTimer(final Player p, final NPC npc) {
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				p.cacoonTime++;
				if (p.cacoonTime == 5) {
					p.getAppearence().transformIntoNPC(-1);
					p.getAppearence().setRenderEmote(-1);
					p.unlock();
					npc.setCantInteract(false);
					p.araxxorCacoonTime = 0;
				}
				if (p.cacoonTime == 6) {
					p.cacoonTime = 0;
					// p.getPackets().sendGameMessage("Cacoon Timer Has RESET");
					stop();
				}

			}
		}, 0, 0);
	}

	NPC spider;

	public void spawnEggSpiders(Player p) {
		final WorldTile SpawnSpiders = base.transform(p.eggSpidersX,
				p.eggSpidersY, p.getPlane());
		attacker.getPackets().sendGameMessage(
				"Worms burrow through her rotting flesh.");
		@SuppressWarnings("unused")
		final WorldTile destination = base.transform(28 + Utils.random(12),
				28 + Utils.random(6), 0);
		// attacker.getPackets().sendProjectile(null, this, destination, 3141,
		// 128, 0, 60, 0, 5, 3, super.getDefinitions().size);
		NPC spider = World.spawnNPC(19468, SpawnSpiders, -1, true);
		// npc1.setFreezeDelay(10);
		// npc1.setNextAnimation(new Animation(24083));
		// npc1.setNextGraphics(new Graphics (4999));
		// Arraxi = npc1;
		// attacker.getPackets().sendGameMessage("SPIDERS SENT");
		// NPC spider = new NPC(19468, SpawnSpiders, -1, true, true);
		spiders.add(spider);
		// spider.setForceMultiArea(true);
		spider.getCombat().setTarget(attacker);
	}

}