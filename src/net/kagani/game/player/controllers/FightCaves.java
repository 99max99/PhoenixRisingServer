package net.kagani.game.player.controllers;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import net.kagani.Settings;
import net.kagani.executor.GameExecutorManager;
import net.kagani.game.Animation;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.map.MapBuilder;
import net.kagani.game.npc.fightcaves.FightCavesNPC;
import net.kagani.game.npc.fightcaves.TzKekCaves;
import net.kagani.game.npc.fightcaves.TzTok_Jad;
import net.kagani.game.npc.fightcaves.Yt_HurKot;
import net.kagani.game.player.MusicsManager;
import net.kagani.game.player.Player;
import net.kagani.game.player.TimersManager.RecordKey;
import net.kagani.game.player.content.pet.Pets;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class FightCaves extends Controller {

	public static final WorldTile OUTSIDE = new WorldTile(4610, 5130, 0);

	private static final int THHAAR_MEJ_JAL = 2617;

	private static final int[] MUSICS = { 1044, 1050, 1051 }; // updated to rs3

	public void playMusic() { // same here
		player.getMusicsManager().playMusic(
				getCurrentWave() == 63 ? 339 : selectedMusic);
	}

	private static final int[][] WAVES = { { 2734 }, { 2734, 2734 }, { 2736 },
			{ 2736, 2734 }, { 2736, 2734, 2734 }, { 2736, 2736 }, { 2739 },
			{ 2739, 2734 }, { 2739, 2734, 2734 }, { 2739, 2736 },
			{ 2739, 2736, 2734 }, { 2739, 2736, 2734, 2734 },
			{ 2739, 2736, 2736 }, { 2739, 2739 }, { 2741 }, { 2741, 2734 },
			{ 2741, 2734, 2734 }, { 2741, 2736 }, { 2741, 2736, 2734 },
			{ 2741, 2736, 2734, 2734 }, { 2741, 2736, 2736 }, { 2741, 2739 },
			{ 2741, 2739, 2734 }, { 2741, 2739, 2734, 2734 },
			{ 2741, 2739, 2736 }, { 2741, 2739, 2736, 2734 },
			{ 2741, 2739, 2736, 2734, 2734 }, { 2741, 2739, 2736, 2736 },
			{ 2741, 2739, 2739 }, { 2741, 2741 }, { 2743 }, { 2743, 2734 },
			{ 2743, 2734, 2734 }, { 2743, 2736 }, { 2743, 2736, 2734 },
			{ 2743, 2736, 2734, 2734 }, { 2743, 2736, 2736 }, { 2743, 2739 },
			{ 2743, 2739, 2734 }, { 2743, 2739, 2734, 2734 },
			{ 2743, 2739, 2736 }, { 2743, 2739, 2736, 2734 },
			{ 2743, 2739, 2736, 2734, 2734 }, { 2743, 2739, 2736, 2736 },
			{ 2743, 2739, 2739 }, { 2743, 2741 }, { 2743, 2741, 2734 },
			{ 2743, 2741, 2734, 2734 }, { 2743, 2741, 2736 },
			{ 2743, 2741, 2736, 2734 }, { 2743, 2741, 2736, 2734, 2734 },
			{ 2743, 2741, 2736, 2736 }, { 2743, 2741, 2739 },
			{ 2743, 2741, 2739, 2734 }, { 2743, 2741, 2739, 2734, 2734 },
			{ 2743, 2741, 2739, 2736 }, { 2743, 2741, 2739, 2736, 2734 },
			{ 2743, 2741, 2739, 2736, 2734, 2734 },
			{ 2743, 2741, 2739, 2736, 2736 }, { 2743, 2741, 2739, 2739 },
			{ 2743, 2741, 2741 }, { 2743, 2743 }, { 2745 } };

	private int[] boundChuncks;
	private Stages stage;
	private boolean logoutAtEnd;
	private boolean login;
	public boolean spawned;
	public int selectedMusic;

	public static void enterFightCaves(Player player) {
		if (player.getFamiliar() != null || player.getPet() != null
				|| Pets.hasPet(player)) {
			player.getDialogueManager()
					.startDialogue("SimpleNPCMessage", THHAAR_MEJ_JAL,
							"No pets in the pits! This is a fight for YOU, not your friends!");
			return;
		}
		player.getControlerManager().startControler("FightCavesControler",
				Settings.DEBUG ? WAVES.length : 1);
	}

	private static enum Stages {
		LOADING, RUNNING, DESTROYING
	}

	@Override
	public void start() {
		loadCave(false);
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int slotId2, int packetId) {
		if (stage != Stages.RUNNING)
			return false;
		if (interfaceId == 182 && (componentId == 6 || componentId == 13)) {
			if (!logoutAtEnd) {
				logoutAtEnd = true;
				player.getPackets()
						.sendGameMessage(
								"<col=ff0000>You will be logged out automatically at the end of this wave.");
				player.getPackets()
						.sendGameMessage(
								"<col=ff0000>If you log out sooner, you will have to repeat this wave.");
			} else
				player.disconnect(true, true);
			return false;
		}
		return true;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 9357) {
			if (stage != Stages.RUNNING)
				return false;
			exitCave(1);
			return false;
		}
		return true;
	}

	/*
	 * return false so wont remove script
	 */
	@Override
	public boolean login() {
		loadCave(true);
		return false;
	}

	public void loadCave(final boolean login) {
		this.login = login;
		stage = Stages.LOADING;
		player.lock(); // locks player
		GameExecutorManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				// finds empty map bounds
				boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
				// copys real map into the empty map
				// 552 640
				MapBuilder.copyAllPlanesMap(552, 640, boundChuncks[0],
						boundChuncks[1], 8);
				// selects a music
				selectedMusic = MUSICS[Utils.random(MUSICS.length)];
				player.setNextWorldTile(!login ? getWorldTile(46, 61)
						: getWorldTile(32, 32));
				// 1delay because player cant walk while teleing :p, + possible
				// issues avoid
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						if (!login) {
							WorldTile walkTo = getWorldTile(32, 32);
							player.addWalkSteps(walkTo.getX(), walkTo.getY(),
									-1, false);
							player.getTimersManager().sendTimer();
						}
						player.getDialogueManager()
								.startDialogue("SimpleNPCMessage",
										THHAAR_MEJ_JAL,
										"You're on your own now, JalYt.<br>Prepare to fight for your life!");
						playMusic();
						player.unlock(); // unlocks player
						stage = Stages.RUNNING;
					}

				}, 1);
				if (!login) {
					/*
					 * lets stress less the worldthread, also fastexecutor used
					 * for mini stuff
					 */
					GameExecutorManager.fastExecutor.schedule(new TimerTask() {

						@Override
						public void run() {
							if (stage != Stages.RUNNING)
								return;
							try {
								startWave();
							} catch (Throwable t) {
								Logger.handle(t);
							}
						}
					}, 6000);
				}
			}
		});
	}

	public WorldTile getSpawnTile() {
		switch (Utils.random(5)) {
		case 0:
			return getWorldTile(11, 16);
		case 1:
			return getWorldTile(51, 25);
		case 2:
			return getWorldTile(10, 50);
		case 3:
			return getWorldTile(46, 49);
		case 4:
		default:
			return getWorldTile(32, 30);
		}
	}

	@Override
	public void moved() {
		if (stage != Stages.RUNNING || !login)
			return;
		login = false;
		setWaveEvent();
	}

	public void startWave() {
		int currentWave = getCurrentWave();
		if (currentWave > WAVES.length) {
			win();
			return;
		}
		player.getInterfaceManager().sendMinigameInterface(316);
		player.getVarsManager().sendVar(3008, currentWave);
		player.getPackets().sendHideIComponent(316, 1, false);
		player.getPackets().sendHideIComponent(316, 5, false);
		if (stage != Stages.RUNNING)
			return;
		for (int id : WAVES[currentWave - 1]) {
			if (id == 2736)
				new TzKekCaves(id, getSpawnTile());
			else if (id == 2745)
				new TzTok_Jad(id, getSpawnTile(), this);
			else
				new FightCavesNPC(id, getSpawnTile());
		}
		spawned = true;
	}

	public void spawnHealers(TzTok_Jad tzTok_Jad) {
		if (stage != Stages.RUNNING)
			return;
		for (int i = 0; i < 4; i++)
			new Yt_HurKot(tzTok_Jad, 2746, getSpawnTile());// Lets them actually
															// heal jad
	}

	public void win() {
		if (stage != Stages.RUNNING)
			return;
		exitCave(4);
	}

	public void nextWave() {
		setCurrentWave(getCurrentWave() + 1);
		playMusic();
		if (logoutAtEnd) {
			player.disconnect(true, true);
			return;
		}
		setWaveEvent();
	}

	public void setWaveEvent() {
		if (getCurrentWave() == 63)
			player.getDialogueManager().startDialogue("SimpleNPCMessage",
					THHAAR_MEJ_JAL, "Look out, here comes TzTok-Jad!");
		GameExecutorManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					if (stage != Stages.RUNNING)
						return;
					startWave();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 600);
	}

	@Override
	public void process() {
		if (spawned) {
			List<Integer> npcs = World.getRegion(player.getRegionId())
					.getNPCsIndexes();
			if (npcs == null || npcs.isEmpty()) {
				spawned = false;
				nextWave();
			}
		}
	}

	@Override
	public boolean sendDeath() {
		player.lock(8);
		player.stopAll();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(player.getDeathAnimation());
				} else if (loop == 1) {
					player.getPackets().sendGameMessage(
							"You have been defeated!");
				} else if (loop == 3) {
					player.reset();
					exitCave(1);
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.getMusicsManager().playMusicEffect(
							MusicsManager.DEATH_MUSIC_EFFECT);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		exitCave(2);
	}

	/*
	 * logout or not. if didnt logout means lost, 0 logout, 1, normal, 2 tele,
	 * 4win
	 */
	public void exitCave(int type) {
		stage = Stages.DESTROYING;
		WorldTile outside = new WorldTile(OUTSIDE, 2); // radomizes alil
		if (type == 0)
			player.setLocation(outside);
		else {
			player.getInterfaceManager().removeMinigameInterface();
			player.getTimersManager().removeTimer(
					type == 4 ? RecordKey.FIGHT_CAVES : null);
			if (type == 1 || type == 4) {
				player.useStairs(-1, outside, 0, 1);
				if (type == 4) {
					player.setCompletedFightCaves();
					player.reset();
					player.getDialogueManager()
							.startDialogue(
									"SimpleNPCMessage",
									THHAAR_MEJ_JAL,
									"You even defeated Tz Tok-Jad, I am most impressed! Please accept this gift as a reward.");
					player.getPackets()
							.sendGameMessage("You were victorious!!");
					player.getInventory().addItemDrop(6570, 1, outside);
					player.getInventory().addItemDrop(6529,
							16064 * Settings.getDropQuantityRate(player),
							outside);
					player.getTimersManager().increaseKills(
							RecordKey.FIGHT_CAVES, false);
				} else if (getCurrentWave() == 1)
					player.getDialogueManager()
							.startDialogue("SimpleNPCMessage", THHAAR_MEJ_JAL,
									"Well I suppose you tried... better luck next time.");
				else {
					int tokkul = getCurrentWave() * 8032 / WAVES.length;
					tokkul *= Settings.getDropQuantityRate(player); // 10x more
					player.getInventory().addItemDrop(6529, tokkul, outside);
					player.getDialogueManager()
							.startDialogue("SimpleNPCMessage", THHAAR_MEJ_JAL,
									"Well done in the cave, here, take TokKul as reward.");
					// TODO tokens
				}
			}
			removeControler();
		}
		/*
		 * 1200 delay because of leaving
		 */
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
			}
		}, 1200, TimeUnit.MILLISECONDS);
	}

	/*
	 * gets worldtile inside the map
	 */
	public WorldTile getWorldTile(int mapX, int mapY) {
		return new WorldTile(boundChuncks[0] * 8 + mapX, boundChuncks[1] * 8
				+ mapY, 0);
	}

	/*
	 * return false so wont remove script
	 */
	@Override
	public boolean logout() {
		/*
		 * only can happen if dungeon is loading and system update happens
		 */
		if (stage != Stages.RUNNING)
			return false;
		exitCave(0);
		return false;

	}

	public int getCurrentWave() {
		if (getArguments() == null || getArguments().length == 0)
			return 1;
		return (Integer) getArguments()[0];
	}

	public void setCurrentWave(int wave) {
		if (getArguments() == null || getArguments().length == 0)
			this.setArguments(new Object[1]);
		getArguments()[0] = wave;
	}

	@Override
	public void forceClose() {
		/*
		 * shouldnt happen
		 */
		if (stage != Stages.RUNNING)
			return;
		exitCave(2);
	}
}
