package net.kagani.game.player.controllers.trollinvasion;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import net.kagani.executor.GameExecutorManager;
import net.kagani.game.Animation;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.map.MapBuilder;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.FadingScreen;
import net.kagani.game.player.controllers.Controller;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class TrollInvasion extends Controller {

	/**
	 * variables.
	 */
	private int[] boundChuncks;

	private int wave;

	private int uses;

	private boolean complexity;

	private boolean spawned;

	private boolean tableDestroyed;

	private boolean archerAttack;

	private final WorldTile OUTSIDE_LOCATION = new WorldTile(2877, 3561, 0);

	/**
	 * Refrence of Stages class.
	 */
	private Stages stage;

	/**
	 * Npc id's for each npc used in this class.
	 */
	private final int TROLL_RUNTS = 12902, TROLL_FATHER = 13689,
			TROLL_RANGER = 13661, MOUNTAIN_TROLL = 11406, PCK = 13669,
			SUMMONER = 13667, DYNAMITE = 13720, WIZARD = 12435, CLIFF = 13381,
			TROLL_GENERAL = 12291, EOHRIC = 13700;

	/**
	 * Holds the npc's for the standard 20 waves.
	 */
	public final int STANDARD_WAVES[][] = {
			{ MOUNTAIN_TROLL },
			{ TROLL_RANGER },
			{ MOUNTAIN_TROLL, TROLL_RANGER },
			{ TROLL_RANGER, MOUNTAIN_TROLL, MOUNTAIN_TROLL },
			{ PCK },
			{ MOUNTAIN_TROLL, PCK },
			{ PCK, MOUNTAIN_TROLL, TROLL_RANGER },
			{ TROLL_GENERAL },
			{ TROLL_GENERAL, TROLL_RANGER, MOUNTAIN_TROLL },
			{ TROLL_GENERAL, TROLL_GENERAL, TROLL_RANGER },
			{ WIZARD },
			{ WIZARD, MOUNTAIN_TROLL, PCK },
			{ WIZARD, MOUNTAIN_TROLL, TROLL_RANGER, PCK },
			{ DYNAMITE, TROLL_FATHER, TROLL_RUNTS, TROLL_RUNTS, TROLL_RUNTS },
			{ TROLL_FATHER, TROLL_RUNTS, TROLL_RUNTS, TROLL_RUNTS,
					TROLL_GENERAL, MOUNTAIN_TROLL, PCK },
			{ WIZARD, TROLL_FATHER, TROLL_RUNTS, TROLL_RUNTS, TROLL_RUNTS,
					TROLL_RANGER, MOUNTAIN_TROLL, PCK },
			{ SUMMONER, DYNAMITE },
			{ WIZARD, DYNAMITE, SUMMONER, TROLL_FATHER, TROLL_RUNTS,
					TROLL_RUNTS, TROLL_RUNTS, TROLL_RANGER, MOUNTAIN_TROLL,
					PCK, },
			{ DYNAMITE, WIZARD, TROLL_FATHER, TROLL_RUNTS, TROLL_RUNTS,
					TROLL_RUNTS, TROLL_RUNTS, TROLL_RANGER, MOUNTAIN_TROLL, PCK },
			{ CLIFF } };

	/**
	 * Holds the npc's for the advanced 7 waves.
	 */
	public final int ADVANCED_WAVES[][] = {
			{ TROLL_RANGER, TROLL_RANGER, TROLL_RANGER, MOUNTAIN_TROLL,
					MOUNTAIN_TROLL, MOUNTAIN_TROLL, MOUNTAIN_TROLL },
			{ PCK, PCK, PCK, TROLL_RANGER, MOUNTAIN_TROLL, MOUNTAIN_TROLL },
			{ WIZARD, TROLL_GENERAL, TROLL_GENERAL, TROLL_GENERAL,
					MOUNTAIN_TROLL },
			{ WIZARD, WIZARD, TROLL_GENERAL, TROLL_FATHER, TROLL_RUNTS,
					TROLL_RUNTS, TROLL_RUNTS, PCK, PCK, TROLL_RANGER,
					MOUNTAIN_TROLL },
			{ TROLL_FATHER, TROLL_RUNTS, TROLL_RUNTS, TROLL_RUNTS,
					TROLL_FATHER, TROLL_RUNTS, TROLL_RUNTS, TROLL_RUNTS,
					WIZARD, TROLL_GENERAL, PCK, PCK, TROLL_RANGER,
					MOUNTAIN_TROLL, MOUNTAIN_TROLL },
			{ SUMMONER, SUMMONER, DYNAMITE, TROLL_FATHER, TROLL_RUNTS,
					TROLL_RUNTS, TROLL_RUNTS, WIZARD, PCK, TROLL_RANGER,
					MOUNTAIN_TROLL },
			{ CLIFF, DYNAMITE, TROLL_FATHER, TROLL_RUNTS, TROLL_RUNTS,
					TROLL_RUNTS, WIZARD, PCK, TROLL_RANGER, MOUNTAIN_TROLL } };

	@Override
	public void start() {
		complexity = (boolean) getArguments()[0];
		commenceGame();
	}

	/**
	 * Method used to return the wave id.
	 * 
	 * @return wave the WaveId;
	 */
	public int getWave() {
		return wave;
	}

	/**
	 * Method used to set the wave id.
	 * 
	 * @param wave
	 *            the wave.
	 */
	public void setWave(int wave) {
		this.wave = wave;
	}

	/**
	 * Method used to return the amoutn of uses.
	 * 
	 * @return uses the Uses.
	 */
	public int getUses() {
		return uses;
	}

	/**
	 * Method used to set the amount of uses.
	 * 
	 * @param amt
	 *            The uses.
	 */
	public void setUses(int amt) {
		this.uses = amt;
	}

	/**
	 * 
	 * @return complexity the Complexity.
	 */
	public boolean getComplexity() {
		if (complexity == true)
			return true;
		return false;
	}

	public boolean isHard() {
		return (complexity = true);
	}

	public int getComplexLength() {
		if (complexity)
			return ADVANCED_WAVES.length;
		else
			return STANDARD_WAVES.length;

	}

	/**
	 * Method used to add increment a troll kill.
	 */
	public void addKill() {
		player.addTrollKill();
	}

	/**
	 * type : 1 teleporting to spot. type 2: teleport from spot.
	 * 
	 * @param type
	 *            the type.
	 */
	public void setEohric(int type) {
		NPC eohric = World.getNPC(13700);
		if (eohric == null && type == 2)
			return;
		if (type == 1) {
			World.spawnNPC(13700, new WorldTile(getWorldTile(43, 29)), -1,
					true, true);

		} else if (type == 2) {
			eohric.faceObject(new WorldObject(9270, 10, 0, new WorldTile(
					getWorldTile(42, 29))));
			eohric.setNextAnimationNoPriority(new Animation(8939));
			eohric.setNextGraphics(new Graphics(1576));

		}

	}

	/**
	 * Method used to switch the supplytable type: 1 switches supplytabble
	 * empty. type 2: switches supplytable full.
	 * 
	 * @param type
	 *            the type.
	 */
	public void switchObject(int type) {
		if (type == 1)
			World.spawnObject(
					new WorldObject(9271, 10, 0, getWorldTile(42, 29)), true);
		else {
			World.spawnObject(
					new WorldObject(9270, 10, 0, getWorldTile(42, 29)), true);

		}
	}

	/**
	 * gets the world tile inside the map.
	 * 
	 * @param mapX
	 * @param mapY
	 * @return
	 */
	public WorldTile getWorldTile(int mapX, int mapY) {
		return new WorldTile(boundChuncks[0] * 8 + mapX, boundChuncks[1] * 8
				+ mapY, 0);
	}

	/**
	 * Method used to return the wave's we are using for the game.
	 * 
	 * @return the standard or advanced wave.
	 */
	public int[][] getComplex() {
		if (complexity) {
			return ADVANCED_WAVES;
		} else {
			return STANDARD_WAVES;
		}
	}

	/**
	 * 
	 * @return the WorldTile.
	 */
	public WorldTile getSpawnTile() {

		return getWorldTile(22, 35);

	}

	public WorldTile getCliffTile() {
		return getWorldTile(39, 21);
	}

	public void nextWave() {
		if (stage != Stages.PLAYING)
			return;

		player.setTrollsKilled(0);
		player.setTrollsToKill(0);
		setUses(5);
		setWave(getWave() + 1);
		setWaveEvent();
		WorldTasksManager.schedule(new WorldTask() {
			int loop = 0;

			@Override
			public void run() {
				if (loop == 0) {
					setEohric(1);
				} else if (loop == 2) {
					if (tableDestroyed == true) {
						if (World.getNPC(EOHRIC) != null)
							World.getNPC(EOHRIC).finish();
						stop();
					} else {
						if (World.getNPC(EOHRIC) != null) {
							World.getNPC(EOHRIC).faceObject(
									new WorldObject(9270, 10, 0, new WorldTile(
											getWorldTile(42, 29))));
							World.getNPC(EOHRIC).setNextAnimation(
									new Animation(1559));
						}
					}
				} else if (loop == 3) {
					switchObject(2);
					setEohric(2);
				} else if (loop == 4) {
					if (World.getNPC(EOHRIC) != null)
						World.getNPC(EOHRIC).finish();
					stop();
				}
				loop++;
			}
		}, 0, 1);

	}

	public void archerWait() {
		if (stage != Stages.PLAYING)
			return;
		WorldTasksManager.schedule(new WorldTask() {
			int loop = 0;

			@Override
			public void run() {
				if (loop == 0) {
					archerAttack = true;
				} else if (loop == 7) {
					sendArcherAttack();

					archerAttack = false;
					stop();
				}
				loop++;
			}
		}, 0, 1);

	}

	@Override
	public void process() {
		if (archerAttack == true) {

		} else if (archerAttack == false) {
			archerWait();
		}
		sendRockAttack();
		processDynamite();
		processTrollRunts();
		if (spawned) {
			if (player.getTrollsKilled() == player.getTrollsToKill()) {
				nextWave();
				spawned = false;
			}
		}
	}

	public void setWaveEvent() {
		GameExecutorManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					if (stage != Stages.PLAYING)
						return;
					startWave();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 600);
	}

	/**
	 * Method used to start the waves.
	 */
	public void startWave() {
		int wave = getWave();
		if (wave > getComplexLength()) {
			win();
			return;
		}
		int complexKills;
		if (getComplexity() == true)
			complexKills = ADVANCED_WAVES[wave - 1].length;
		else
			complexKills = STANDARD_WAVES[wave - 1].length;
		player.getVarsManager().sendVar(2215, getWave());
		if (getComplexity() == true)
			player.getPackets().sendGameMessage("Wave: " + getWave() + "/7.");
		else
			player.getPackets().sendGameMessage("Wave: " + getWave() + "/20.");
		if (stage != Stages.PLAYING)
			return;
		for (int id : getComplex()[wave - 1]) {
			if (id == 12921)
				new Cliff_Troll(id, getCliffTile(), this);
			new TrollInvasionNPC(id, getSpawnTile(), this);
		}
		player.setTrollsToKill(complexKills);
		spawned = true;
	}

	@Override
	public boolean processNPCClick2(NPC npc) {
		if (npc.getId() == 13699) {
			leaveGame(1);
			return true;
		}
		return false;
	}

	/**
	 * Method used to destroy the map and end the game.
	 * 
	 * @param type
	 *            the type.
	 */
	public void leaveGame(int type) {
		stage = Stages.DESTROYING;
		WorldTile outside = new WorldTile(OUTSIDE_LOCATION);
		if (type == 0 || type == 2)
			player.setLocation(outside);
		else {
			player.setForceMultiArea(false);
			player.getPackets().closeInterface(
					player.getInterfaceManager().hasRezizableScreen() ? 11 : 0);
			if (type == 1 || type == 4) {
				player.setNextWorldTile(outside);
				if (type == 4) {
					player.trollWins++;
					player.reset();
					final long time = FadingScreen.fade(player);
					FadingScreen.unfade(player, time, new Runnable() {
						@Override
						public void run() {
							player.setNextWorldTile(new WorldTile(
									OUTSIDE_LOCATION));
							player.getInterfaceManager()
									.removeMinigameInterface();
							stage = Stages.DESTROYING;
							player.setForceMultiArea(false);
							if (getComplexity() == true) {
								player.getDialogueManager()
										.startDialogue("SimpleMessage",
												"You made it to wave 7 and completed the hard mode!");
								player.getInventory().addItem(20935, 1);
							} else {
								player.getDialogueManager()
										.startDialogue("SimpleMessage",
												"You made it to wave 20 and completed the standard mode!");
								player.getInventory().addItem(20935, 1);
								removeControler();
							}
						}
					});
				}

			}
			removeControler();
		}
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
			}
		}, 1200, TimeUnit.MILLISECONDS);
	}

	public enum Stages {
		LOADING, PLAYING, DESTROYING
	}

	/**
	 * Starts the game.
	 */
	public void commenceGame() {
		stage = Stages.LOADING;
		setWave(1);
		setUses(5);
		boundChuncks = MapBuilder.findEmptyChunkBound(20, 20);
		MapBuilder.copyAllPlanesMap(353, 443, boundChuncks[0], boundChuncks[1],
				60);
		final long time = FadingScreen.fade(player);
		FadingScreen.unfade(player, time, new Runnable() {
			@Override
			public void run() {
				player.setNextWorldTile(new WorldTile(getWorldTile(39, 21)));
				player.getInterfaceManager().sendMinigameInterface(447);
				stage = Stages.PLAYING;
				loadSettings();
				player.setForceMultiArea(true);
				player.canSpawn();
				startWave();
			}
		});
	}

	/**
	 * Loads data for the start of the game.
	 */
	public void loadSettings() {
		tableDestroyed = false;
		archerAttack = false;
		WorldObject object;
		object = new WorldObject(9270, 10, 0, getWorldTile(42, 29));
		World.spawnObject(object, false);
		player.setTrollsToKill(0);
		player.setTrollsKilled(0);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(44, 20)),
				true);
		World.spawnObject(new WorldObject(34662, 10, 0, getWorldTile(21, 35)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(43, 20)),
				true);
		World.spawnObject(new WorldObject(34584, 10, 0, getWorldTile(42, 20)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 19)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 18)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 17)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 16)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 15)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 14)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 13)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 12)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 11)),
				true);
		// secondobjects
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(44, 24)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(43, 24)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 24)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 25)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 26)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 27)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 28)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 30)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 31)),
				true);
		World.spawnObject(new WorldObject(9242, 10, 0, getWorldTile(42, 32)),
				true);
		// npcs
		World.spawnNPC(13699, new WorldTile(getWorldTile(42, 19)), -1, true,
				true);
		World.spawnNPC(13701, new WorldTile(getWorldTile(44, 25)), -1, true,
				true);
	}

	/**
	 * Process the dynamite process.
	 */
	public void processDynamite() {
		NPC dynamite = World.getNPC(DYNAMITE);
		if (dynamite == null)
			return;
		if (tableDestroyed == false) {

			dynamite.addWalkSteps(boundChuncks[0] * 8 + 41,
					boundChuncks[1] * 8 + 29, 25, false);
			WorldObject table = new WorldObject(9270, 10, 0, new WorldTile(
					getWorldTile(42, 29)));
			if (dynamite.withinDistance(table, 1)) {
				tableDestroyed = true;
				dynamite.setNextGraphics(new Graphics(1028));
				switchObject(1);
			}
		} else if (tableDestroyed == true) {
			dynamite.addWalkSteps(player.getX() - 1, player.getY(), 100, false);
		}
	}

	/**
	 * Processes the troll runts.
	 */
	public void processTrollRunts() {
		NPC runt = World.getNPC(TROLL_RUNTS);
		NPC father = World.getNPC(TROLL_FATHER);
		if (runt == null)
			return;
		if (father == null)
			return;
		if (runt.getHitpoints() == 0)
			return;
		int hpPercent = (runt.getMaxHitpoints() / runt.getHitpoints());
		System.out.println(hpPercent);
		if (!father.isDead()) {
			if (hpPercent < 15) {
				runt.setHitpoints(runt.getMaxHitpoints());
			}
		} else {
		}

	}

	public void sendArcherAttack() {
		int wave = getWave();
		int complex = getComplexLength();

		if (stage != Stages.PLAYING)
			return;

		NPC archer = World.getNPC(13701);
		WorldObject object = new WorldObject(9242, 10, 0, getWorldTile(44, 24));

		if (archer == null)
			return;
		if (wave > complex) {
			return;
		}

		if (complexity) {
			for (int enemy : ADVANCED_WAVES[wave - 1]) {
				if (World.getNPC(enemy) == null)
					continue;
				NPC closeEnemies = World.getNPC(enemy);
				if (closeEnemies == null || closeEnemies.isDead())
					continue;
				int hpPercent = (closeEnemies.getMaxHitpoints() / closeEnemies
						.getHitpoints());
				if (closeEnemies.withinDistance(object, 6) && hpPercent < 5
						|| object.withinDistance(closeEnemies, 6)
						&& hpPercent < 5) {
					archer.faceEntity(closeEnemies);
					archer.setNextAnimation(new Animation(12658));
					World.sendProjectile(archer, closeEnemies, 1120, 41, 16,
							31, 35, 16, 0);

					closeEnemies.applyHit(new Hit(archer,
							Utils.random(50, 200), HitLook.RANGE_DAMAGE));
					archer.setNextForceTalk(new ForceTalk("VOLLEY!"));
				}
			}
		} else {
			for (int enemy : STANDARD_WAVES[wave - 1]) {
				if (World.getNPC(enemy) == null)
					continue;
				NPC closeEnemies = World.getNPC(enemy);
				if (closeEnemies == null || closeEnemies.isDead())
					continue;
				int hpPercent = (closeEnemies.getMaxHitpoints() / closeEnemies
						.getHitpoints());
				if (closeEnemies.withinDistance(object, 6) && hpPercent < 5
						|| object.withinDistance(closeEnemies, 6)
						&& hpPercent < 5) {
					archer.faceEntity(closeEnemies);
					archer.setNextAnimation(new Animation(12658));
					World.sendProjectile(archer, closeEnemies, 1120, 41, 16,
							31, 35, 16, 0);
					closeEnemies.applyHit(new Hit(archer,
							Utils.random(20, 140), HitLook.RANGE_DAMAGE));
					archer.setNextForceTalk(new ForceTalk("VOLLEY!"));
				}
			}
		}
	}

	public void sendRockAttack() {
		WorldObject object = new WorldObject(9270, 10, 0, getWorldTile(21, 35));

		if (player.withinDistance(object, 3))
			WorldTasksManager.schedule(new WorldTask() {

				int loop = 0;

				@Override
				public void run() {
					if (loop == 0)
						player.getPackets()
								.sendGameMessage(
										"<col=c81414>The trolls have a great defence at this passage with rock hurlers!</col>");
					else if (loop == 1) {
						player.setNextGraphics(new Graphics(406));
						World.sendProjectile(player, player, 406, 41, 16, 25,
								35, 16, 0);
						player.applyHit(new Hit(player, Utils.random(5, 40),
								HitLook.REGULAR_DAMAGE));
					} else if (loop == 3) {
						player.setNextGraphics(new Graphics(405));
						World.sendProjectile(player, player, 405, 41, 16, 25,
								35, 16, 0);
						player.applyHit(new Hit(player, Utils.random(5, 25),
								HitLook.REGULAR_DAMAGE));
						stop();
					}
					loop++;
				}

			}, 0, 1);
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.reset();
					player.getPackets().sendGameMessage(
							"The guards pull you back just before you die.");
					player.getPackets().sendGameMessage(
							"You made it to wave " + getWave() + ".");
					leaveGame(1);
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 1) {
					player.getPackets().sendMusicEffect(90, 0);
					removeControler();
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	/**
	 * return's the process.
	 */
	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 9270) {
			useSupplyTable();
			return false;
		}
		return true;
	}

	@Override
	public void forceClose() {
		if (stage != Stages.PLAYING)
			return;
		leaveGame(2);
	}

	/**
	 * Method used to restore player health,run,special.
	 */
	public void useSupplyTable() {
		setUses(getUses() - 1);
		player.getHintIconsManager().removeUnsavedHintIcon();
		player.getPackets().sendGameMessage(
				"You quickly enjoy the hearty supplies and feel invigorated. The table has "
						+ uses + " uses left");
		player.getPackets().sendGameMessage("for this wave.");
		player.setNextAnimation(new Animation(829));
		player.setNextGraphics(new Graphics(111));
		player.heal(player.getMaxHitpoints());
		int restoredEnergy = (int) (player.getRunEnergy() * 1.3);
		player.setRunEnergy(restoredEnergy > 100 ? 100 : restoredEnergy);
		player.getPrayer().restorePrayer(
				(int) ((int) (Math.floor(player.getSkills().getLevelForXp(
						Skills.PRAYER) * 2.5) + 70) * player.getAuraManager()
						.getPrayerPotsRestoreMultiplier()));
		player.getCombatDefinitions().restoreSpecialAttack(100);
		if (uses == 0) {
			switchObject(1);
		}
	}

	/**
	 * Method used to finish the game and reward the player.
	 */
	public void win() {
		if (stage != Stages.PLAYING)
			return;
		leaveGame(4);
	}

	public void giveReward(int skill) {
		int l = player.getSkills().getLevel(skill);
		@SuppressWarnings("unused")
		int xp = (int) (8 * (wave / (complexity ? 7 : 20)) * (Math.pow(l, 2)
				- (2 * l) + 100));
	}
}
