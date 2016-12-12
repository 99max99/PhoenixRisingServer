package net.kagani.game.npc.vorago;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kagani.Settings;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.npc.Drop;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.vorago.combat.VoragoCombat;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.FriendsChat;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class Vorago extends NPC {

	/**
	 * TODO List: Make bleed decrease as distance from Vorago increases Add a controller Instance it Make gravity fields actually go towards vorago Bring it down interface bar fill Ground break Interface for waterfalls Change screen view when mauling
	 * Vorago and bringing him down Work on dialogue Finish clone's abilities Find animations for gaps etc Add more ways of getting drops e.g most damage received Balancing and work out other flaws Make weapon pieces fall through too if not picked up & be
	 * instantly visible when dropped 5 Players needed for a fight Stop the walking during bring it down Add other weeks (after working out the end add it into p5 scop)
	 */

	/**
	 * Weeks: 1 - Ceiling collapse 2 - Scopulus [DONE](apart from p5 should have the end) 3 - Vitalis 4 - Green Bomb 5 - Teamsplit 6 - The end
	 */

	private static final long serialVersionUID = 8957425938871772845L;

	/*** Phase Centres and Grave yard ***/
	public WorldTile GRAVE_AREA = new WorldTile(3098, 6165, 0);
	public WorldTile Centre[] = { new WorldTile(3106, 6106, 0), new WorldTile(3104, 6048, 0), new WorldTile(3039, 6048, 0), new WorldTile(3040, 5984, 0), new WorldTile(3100, 5982, 0) };
	public static WorldTile OUTSIDE = new WorldTile(3046, 6124, 0);

	private Player k;
	Player killers[] = { k, k, k, k, k };// Used for drops

	int dtX = Centre[4].getX() + 11;// Drop tile centre x coordinate
	int dtY = Centre[4].getY() + 2;// Drop tile centre y coordinate
	int dtZ = Centre[4].getPlane();// Drop tile centre z coordinate
	private WorldTile dropTiles[] = { new WorldTile(dtX - 1, dtY + 1, dtZ), // Phase
																			// 1
			new WorldTile(dtX + 1, dtY + 1, dtZ), // Phase 2
			new WorldTile(dtX, dtY, dtZ), // Phase 3
			new WorldTile(dtX - 1, dtY - 1, dtZ), // Phase 4
			new WorldTile(dtX + 1, dtY - 1, dtZ) };// Phase 5

	public int phase = 1;
	public int week = 2;// TODO other weeks
	public boolean special = false;
	public int specialType = 0;
	public int smashCount = 0;
	public int moves = 0;
	public boolean redBomb = true;// First attack is a red bomb
	public int fieldCount = 0;
	public boolean canDie = false;
	public Entity Reflectee;// Person that damage is being reflected onto
	public int bringDownCount = 0;
	public boolean isDown = false;
	public int transformCount = 0;
	public int downDamage = 0;
	public int scopDead = 0;
	public boolean canBeAttacked = true;
	public int rotation;
	public int wfCount = 0;
	public WorldTile wfTile;
	public WorldTile[] safeTiles = { wfTile, wfTile, wfTile, wfTile, wfTile, wfTile };// The
																						// safe
																						// tiles
																						// for
																						// behind
																						// the
																						// waterfall
	public List<Player> safePlayers = Collections.synchronizedList(new ArrayList<Player>());// The
																							// players
																							// that
																							// are
																							// in
																							// behind
																							// the
																							// waterfall
	public boolean isReflecting = false;
	public Entity Clonee;// The person being cloned
	public int stoneId;
	public boolean cloneOut = false;
	private int specDrops = 0;
	public int startPushBack = 20000;// This seems to be a decent start point,
										// making it higher makes P5 harder and
										// vice versa
	public int pushBackDamage = startPushBack;

	public Vorago(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		World.spawnObject(new WorldObject(84826, 10, 2, 3099, 6124, 0));// The
																		// gap
																		// that
																		// you
																		// jump
																		// from
																		// onto
																		// vorago
		setNoDistanceCheck(true);
		setIntelligentRouteFinder(true);
	}

	public int getPhase() {
		return phase;
	}

	public WorldTile getCentre() {
		return Centre[phase - 1];
	}

	public void transformRago() {
		if (getId() == 17182) {
			setNextNPCTransformation(17183);
		} else {
			setNextNPCTransformation(17182);
		}
	}

	public void bringDownTransform() {
		if (transformCount == 0) {
			for (Player p : VoragoHandler.getPlayers()) {
				p.getInterfaceManager().sendFadingInterface(1413);
			}
			isDown = true;
			transformCount++;
			setCantInteract(true);
			setNextAnimation(new Animation(20360));
			WorldTasksManager.schedule(new WorldTask() {
				private int count = 0;

				@Override
				public void run() {
					if (count == 1) {
						setNextNPCTransformation(17184);
						stop();
						return;
					}
					count++;
				}
			}, 0, 1);

		} else if (transformCount == 1) {
			for (Player p : VoragoHandler.getPlayers()) {
				p.getInterfaceManager().removeFadingInterface();
				p.getPackets().sendGameMessage("<col=1fe12c>Vorago stumbles! Deal as much damage as possible" + " to loosen the weapon piece!</col>");
			}
			transformCount++;
			setCantInteract(false);
			setForceAgressive(false);
			int damageRequired = 15000 * VoragoHandler.getPlayersCount();
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (downDamage < damageRequired) {// Resets vorago so you
														// have to do another 2
														// fields
						transformCount = 0;
						fieldCount = 2;
						specialType = 0;
						moves = 0;
						setNextNPCTransformation(17182);
						for (Player p : VoragoHandler.getPlayers()) {
							p.getPackets().sendGameMessage("You didn't do enough damage!");
						}
					} else {// Done enough damage so drop weapon piece
						for (Player p : VoragoHandler.getPlayers()) {
							p.getPackets().sendGameMessage("<col=1fe12c>The weapon piece falls from Vorago's body!</col>");
						}
						World.addGroundItem(new Item(28602), getRandomJump());
						bringDownTransform();
					}
					stop();
					return;
				}
			}, 20);

		} else if (transformCount == 2) {// Can now move onto P3
			transformCount++;
			setNextAnimation(new Animation(20362));
			WorldTasksManager.schedule(new WorldTask() {
				private int count = 0;

				@Override
				public void run() {
					if (count == 1) {
						setNextNPCTransformation(17182);
						special = false;
						canDie = true;
						stop();
						return;
					}
					count++;
				}
			}, 0, 1);
		}
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		super.handleIngoingHit(hit);
		if (hit.getSource() != null && isReflecting == true) { // Reflects
																// damage to a
																// random person
			int recoil = (int) (hit.getDamage());
			if (recoil > 0) {
				Hit hit2 = new Hit(this, recoil, hit.getLook());
				hit.setDamage(0);
				if (hit.isCriticalHit()) {
					hit2.setCriticalMark();
				}
				Reflectee.applyHit(hit2);
			}
		}
		if (hit.getSource() != null && getId() == 17184) {// This is when
															// players have to
															// do enough damage
															// to loosen the
															// weapon piece when
															// he's down
			int dmg = (int) (hit.getDamage());
			heal(dmg / 2);
			downDamage = downDamage + dmg;
		}
		if (hit.getSource() != null && !canBeAttacked) {
			hit.setDamage(0);
		}
		if (hit.getSource() != null && cloneOut) {// Damage is 1/8th when clone
													// is out
			hit.setDamage(hit.getDamage() / 8);
		}
		if (getPhase() == 5) {// pushes vorago back
			int dmg = (int) (hit.getDamage());
			pushBackDamage = pushBackDamage - dmg;
		}
		if (isCantInteract()) {
			hit.setDamage(0);
		}
	}

	@Override
	public void processEntity() {
		super.processEntity();
		if (getPhase() == 5) {// Handles the push back
			for (Player t : VoragoHandler.getPlayers()) {
				if (Utils.colides(Vorago.this, t)) {
					t.setNextWorldTile(new WorldTile(Vorago.this.getX() - 1, t.getY(), Vorago.this.getPlane()));
				}
			}
			if (pushBackDamage <= startPushBack / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() + 9, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (2 * startPushBack) / 10 && pushBackDamage > (1 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() + 8, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (3 * startPushBack) / 10 && pushBackDamage > (2 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() + 7, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (4 * startPushBack) / 10 && pushBackDamage > (3 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() + 6, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (5 * startPushBack) / 10 && pushBackDamage > (4 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() + 5, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (6 * startPushBack) / 10 && pushBackDamage > (5 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() + 4, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (7 * startPushBack) / 10 && pushBackDamage > (6 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() + 3, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (8 * startPushBack) / 10 && pushBackDamage > (7 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() + 2, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (9 * startPushBack) / 10 && pushBackDamage > (8 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() + 1, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (11 * startPushBack) / 10 && pushBackDamage > (9 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX(), Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (12 * startPushBack) / 10 && pushBackDamage > (11 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() - 1, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (13 * startPushBack) / 10 && pushBackDamage > (12 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() - 2, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (14 * startPushBack) / 10 && pushBackDamage > (13 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() - 3, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (15 * startPushBack) / 10 && pushBackDamage > (14 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() - 4, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (16 * startPushBack) / 10 && pushBackDamage > (15 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() - 5, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (17 * startPushBack) / 10 && pushBackDamage > (16 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() - 6, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (18 * startPushBack) / 10 && pushBackDamage > (17 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() - 7, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (19 * startPushBack) / 10 && pushBackDamage > (18 * startPushBack) / 10) {
				Vorago.this.setNextWorldTile(new WorldTile(Centre[4].getX() - 8, Centre[4].getY(), Centre[4].getPlane()));
			}
			if (pushBackDamage <= (20 * startPushBack) / 10 && pushBackDamage > (19 * startPushBack) / 10) {
				for (Player t : VoragoHandler.getPlayers()) {
					t.setNextAnimation(new Animation(20388));
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							t.sendDeath(Vorago.this);
							VoragoHandler.removePlayer(t);
						}

					}, 1);
				}
			}

		}
	}

	void resetCounts() {// Used at the end of each phase
		smashCount = 0;
		moves = 0;
		redBomb = false;
	}

	void resetVariables() {// Resets all the variables so can have multiple
							// kills
		smashCount = 0;
		moves = 0;
		redBomb = false;
		phase = 1;
		special = false;
		specialType = 0;
		fieldCount = 0;
		canDie = false;
		bringDownCount = 0;
		isDown = false;
		transformCount = 0;
		downDamage = 0;
		scopDead = 0;
		canBeAttacked = true;
		wfCount = 0;
		isReflecting = false;
		cloneOut = false;
		specDrops = 0;
		startPushBack = 20000;
		pushBackDamage = startPushBack;
	}

	public WorldTile getRandomJump() {// Using Math.random() as I don't feel
										// Utils.random is very random
		int a = (int) (-6 + Math.random() * 12);
		int b = (int) (-6 + Math.random() * 12);
		int c = (int) (-2 + Math.random() * 4);
		int d = (int) (-2 + Math.random() * 4);
		if (getPhase() < 5) {
			return new WorldTile(getCentre().getX() + a, getCentre().getY() + b, 0);
		} else {
			return new WorldTile(getCentre().getX() - 6 + c, getCentre().getY() + 2 + d, 0);
		}
	}

	public void endPhase() {// Don't know why vorago doesnn't appear on all
							// phases apart from phase 3
		resetCounts();
		canDie = false;
		final WorldTile A = getRandomJump();
		final WorldTile B = getRandomJump();
		final WorldTile C = getRandomJump();
		setNextFaceWorldTile(A);
		setNextAnimation(new Animation(20365));
		World.sendGraphics(Vorago.this, new Graphics(4037), A);
		WorldTasksManager.schedule(new WorldTask() {
			int count = 0;

			@Override
			public void run() {
				if (count == 3) {
					setNextWorldTile(A);
					setNextAnimation(new Animation(20367));
					setNextGraphics(new Graphics(4020));
					sendLandHit();
				}
				if (count == 4) {
					setNextFaceWorldTile(B);
					setNextAnimation(new Animation(20365));
					World.sendGraphics(Vorago.this, new Graphics(4037), B);
				}
				if (count == 7) {
					setNextWorldTile(B);
					setNextAnimation(new Animation(20367));
					setNextGraphics(new Graphics(4020));
					sendLandHit();
				}
				if (count == 8) {
					setNextFaceWorldTile(C);
					setNextAnimation(new Animation(20365));
					World.sendGraphics(Vorago.this, new Graphics(4037), C);
				}
				if (count == 11) {
					setNextWorldTile(C);
					setNextAnimation(new Animation(20367));
					setNextGraphics(new Graphics(4020));
					sendLandHit();
					for (Entity p : VoragoHandler.getPlayers()) {
						((Player) p).getPackets().sendGameMessage("The ground breaks from under you!");
					}
					int[] xstart = { 3090, 3026, 3090, 3026 };// Phase start locations for X, 1, 2, 3, 4
					int[] ystart = { 6098, 6034, 5970, 6034 };// Phase start locations for Y, 1, 2, 3, 4
					World.spawnObjectTemporary(new WorldObject(84873, 10, 1, new WorldTile(xstart[phase - 1], ystart[phase - 1], 0)), 5000);
					World.spawnObjectTemporary(new WorldObject(84871, 10, 1, new WorldTile(xstart[phase - 1], ystart[phase - 1] + 9, 0)), 5000);
					World.spawnObjectTemporary(new WorldObject(84873, 10, 2, new WorldTile(xstart[phase - 1], ystart[phase - 1] + 18, 0)), 5000);
					World.spawnObjectTemporary(new WorldObject(84871, 10, 0, new WorldTile(xstart[phase - 1] + 9, ystart[phase - 1], 0)), 5000);
					World.spawnObjectTemporary(new WorldObject(84869, 10, 0, new WorldTile(xstart[phase - 1] + 9, ystart[phase - 1] + 9, 0)), 5000);
					World.spawnObjectTemporary(new WorldObject(84871, 10, 2, new WorldTile(xstart[phase - 1] + 9, ystart[phase - 1] + 18, 0)), 5000);
					World.spawnObjectTemporary(new WorldObject(84873, 10, 0, new WorldTile(xstart[phase - 1] + 18, ystart[phase - 1], 0)), 5000);
					World.spawnObjectTemporary(new WorldObject(84871, 10, 3, new WorldTile(xstart[phase - 1] + 18, ystart[phase - 1] + 9, 0)), 5000);
					World.spawnObjectTemporary(new WorldObject(84873, 10, 3, new WorldTile(xstart[phase - 1] + 18, ystart[phase - 1] + 18, 0)), 5000);
				}
				if (count == 12) {
					phase++;
					setNextAnimation(new Animation(20323));
					for (Entity p : VoragoHandler.getPlayers()) {
						p.setNextAnimation(new Animation(20402));
					}
				}
				if (count == 13) {
					for (Entity p : VoragoHandler.getPlayers()) {
						p.setNextWorldTile(new WorldTile(getRandomJump()));
						p.setNextAnimation(new Animation(20401));
					}
					setHitpoints(getMaxHitpoints());
				}
				if (count == 16) {
					setNextWorldTile(getCentre());
					setNextAnimation(new Animation(20367));
					setNextGraphics(new Graphics(4020));
					setCantInteract(false);
					switch (phase) {
					case 2:
						special = true;
						specialType = 0;
						break;
					case 3:
						transformRago();
						if (week == 2) {
							canBeAttacked = false;
							special = false;
							VoragoCombat.spawnScop();
						}
						break;
					case 4:
						special = false;
						specialType = 1;
						VoragoCombat.sendWaterfall();
						break;
					case 5:
						if (getId() == 17182) {
							transformRago();
						}
						redBomb = true;
						special = false;
						specialType = 0;
						break;
					}
				}
				count++;
			}
		}, 0, 1);
	}

	public void sendLandHit() {// TODO make it so it actually hits when vorago
								// lands
		for (Entity target1 : getPossibleTargets()) {
			if (Utils.colides(Vorago.this, target1)) {
				target1.applyHit(new Hit(Vorago.this, Utils.random(3000, 5001), HitLook.REGULAR_DAMAGE));
				if (target1 instanceof Player)
					target1.setNextAnimation(new Animation(10070));
			}
		}
	}

	private static Drop ENERGIES = new Drop(28627, 0, 2);// TODO work out why
															// the number of
															// energies isn't
															// what I set

	private static final Drop[] COMMON_DROPS = { new Drop(220, 15, 20), new Drop(28264, 10, 35), new Drop(15271, 75, 152), new Drop(28264, 10, 35), new Drop(1128, 3, 9), new Drop(5304, 4, 10), new Drop(452, 5, 28), new Drop(1748, 35, 55),
			new Drop(9245, 20, 55), new Drop(1514, 35, 128), new Drop(31867, 50, 50) };// TODO
																						// add
																						// trisk
																						// fragments

	private static final Drop[] SPECIAL_DROPS = { new Drop(28617, 1, 1), new Drop(28621, 1, 1), new Drop(28626, 1, 1) };

	public Item sendDrop(Player player, Drop drop, WorldTile dropPlace) {
		if ((drop.getItemId() >= 28617 && drop.getItemId() <= 28626) || drop.getItemId() == 33716) {
			player.getPackets().sendGraphics(new Graphics(4422), dropPlace);
			// World.sendGraphics(player, new Graphics(4422), dropPlace);
			World.sendNews(player, player.getDisplayName() + " has received " + ItemDefinitions.getItemDefinitions(drop.getItemId()).getName() + " drop!", 1);
		}
		boolean stackable = ItemDefinitions.getItemDefinitions(drop.getItemId()).isStackable();
		Item item = stackable ? new Item(drop.getItemId(), drop.getMinAmount() + Utils.random(drop.getMaxAmount())) : new Item(drop.getItemId(), drop.getMinAmount() + Utils.random(drop.getMaxAmount()));
		if (!stackable && item.getAmount() > 1) {
			for (int i = 0; i < item.getAmount(); i++)
				World.addGroundItem(new Item(item.getId(), 1), dropPlace, player, true, 60);
		} else
			World.addGroundItem(item, dropPlace, player, true, 60);
		return item;
	}

	public List<Drop> generateDrops(Player killer, double e) {
		List<Drop> d = new ArrayList<Drop>();
		d.add(ENERGIES);
		if (Math.random() * 100 <= 15 * e && specDrops == 0) {// 30% Chance of a
																// special each
																// drop - only
																// one per kill
			specDrops++;
			d.add(SPECIAL_DROPS[Utils.random(SPECIAL_DROPS.length)]);
		} else {
			d.add(COMMON_DROPS[Utils.random(COMMON_DROPS.length)]);
		}
		return d;
	}

	public void drop() {// Handles the drop - most of this is copied from the
						// actual drop in NPC.java

		for (int i = 0; i < dropTiles.length; i++) {
			Player killer = killers[i];
			if (killer == null)
				return;
			List<Player> players = FriendsChat.getLootSharingPeople(killer);

			double dropRate = 0;

			if (players == null || players.size() == 1)
				dropRate = Settings.getDropRate(killer);
			else { // to be fair
				for (Player p2 : players)
					dropRate += Settings.getDropRate(p2);
				dropRate /= players.size();
			}

			List<Drop> dropL = generateDrops(killer, dropRate);

			if (players == null || players.size() == 1) {
				for (Drop drop : dropL) {
					sendDrop(killer, drop, dropTiles[i]);
				}
			} else {
				for (Drop drop : dropL) {
					Player luckyPlayer = players.get(Utils.random(players.size()));
					Item item = sendDrop(luckyPlayer, drop, dropTiles[i]);
					luckyPlayer.getPackets().sendGameMessage("<col=00FF00>You received: " + item.getAmount() + " " + item.getName() + ".");
					for (Player p2 : players) {
						if (p2 == luckyPlayer)
							continue;
						p2.getPackets().sendGameMessage("<col=66FFCC>" + luckyPlayer.getDisplayName() + "</col> received: " + item.getAmount() + " " + item.getName() + ".");
						p2.getPackets().sendGameMessage("Your chance of receiving loot has improved.");
					}
				}
			}

		}
	}

	@Override
	public void sendDeath(Entity p) {
		if (canDie) {
			final int deathDelay = 20;
			if (getPhase() != 3) {// Phase 3 is done through the scopuli
				killers[getPhase() - 1] = getMostDamageReceivedSourcePlayer();
				resetReceivedDamage();
			}
			if (getPhase() < 5) {
				setCantInteract(true);
				getCombat().removeTarget();
				endPhase();
			} else {
				setNextAnimation(new Animation(20352));
				setNextGraphics(new Graphics(4036));
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						World.spawnObject(new WorldObject(84960, 10, 0, new WorldTile(dtX - 2, dtY - 2, dtZ)));// Spawns
																												// exit
																												// field
						drop();
						resetVariables();
						setLocation(new WorldTile(3141, 6132, 0));
						reset();
						finish();
						VoragoHandler.endFight();
						stop();
					}
				}, deathDelay);
			}
		} else {
			setHitpoints(50000);
		}
	}

}