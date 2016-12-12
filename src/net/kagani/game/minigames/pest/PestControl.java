package net.kagani.game.minigames.pest;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import net.kagani.Settings;
import net.kagani.cache.loaders.NPCDefinitions;
import net.kagani.executor.GameExecutorManager;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.map.MapBuilder;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.pest.PestMonsters;
import net.kagani.game.npc.pest.PestPortal;
import net.kagani.game.npc.pest.Shifter;
import net.kagani.game.npc.pest.Spinner;
import net.kagani.game.npc.pest.Splatter;
import net.kagani.game.player.Player;
import net.kagani.game.player.controllers.pestcontrol.PestControlGame;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class PestControl {

	private final static int[][] PORTAL_LOCATIONS = { { 4, 56, 45, 21, 32 },
			{ 31, 28, 10, 9, 32 } };
	private final static int[] KNIGHT_IDS = { 3782, 3784, 3785 };

	private int[] boundChunks;
	private int[] pestCounts = new int[5];

	private List<Player> team;
	private List<NPC> brawlers = new LinkedList<NPC>();
	private PestPortal[] portals = new PestPortal[4];

	private PestPortal knight;
	private PestData data;

	private byte lockedPortals = 5;

	private class PestGameTimer extends TimerTask {

		int seconds = 1200;

		@Override
		public void run() {
			try {
				updateTime(seconds / 60);
				if (seconds == 0 || canFinish()) {
					endGame();
					cancel();
					return;
				}
				if (seconds % 10 == 0)
					sendPortalInterfaces();
				if (seconds % 20 == 0)
					unlockPortal();
				seconds--;
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}
	}

	public PestControl(List<Player> team, PestData data) {
		this.team = Collections.synchronizedList(team);
		this.data = data;
	}

	public PestControl create() {
		final PestControl instance = this;
		GameExecutorManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					boundChunks = MapBuilder.findEmptyChunkBound(8, 8);
					MapBuilder.copyAllPlanesMap(328, 320, boundChunks[0],
							boundChunks[1], 8);
					sendBeginningWave();
					unlockPortal();
					for (Player player : team) {
						player.getControlerManager()
								.removeControlerWithoutCheck();
						player.useStairs(
								-1,
								getWorldTile(35 - Utils.random(4),
										54 - (Utils.random(3))), 1, 2);
						player.getControlerManager().startControler(
								"PestControlGame", instance);
					}
					GameExecutorManager.fastExecutor.schedule(
							new PestGameTimer(), 1000, 1000);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		});
		return instance;
	}

	private void sendBeginningWave() {
		knight = new PestPortal(KNIGHT_IDS[Utils.random(KNIGHT_IDS.length)],
				true, getWorldTile(32, 32), this);
		knight.unlock();
		for (int index = 0; index < portals.length; index++) {
			PestPortal portal = portals[index] = new PestPortal(6146 + index,
					true, getWorldTile(PORTAL_LOCATIONS[0][index],
							PORTAL_LOCATIONS[1][index]), this);
			portal.setHitpoints(data.ordinal() == 0 ? 2000 : 2500);
		}
	}

	public boolean createPestNPC(int index) {
		if (pestCounts[index] >= (index == 4 ? 4
				: (portals[index] != null && portals[index].isLocked()) ? 5
						: 15))
			return false;
		pestCounts[index]++;
		WorldTile baseTile = getWorldTile(PORTAL_LOCATIONS[0][index],
				PORTAL_LOCATIONS[1][index]);
		WorldTile teleTile = baseTile;
		int npcId = index == 4 ? data.getShifters()[Utils.random(data
				.getShifters().length)] : data.getPests()[Utils.random(data
				.getPests().length)];
		NPCDefinitions defs = NPCDefinitions.getNPCDefinitions(npcId);
		for (int trycount = 0; trycount < 10; trycount++) {
			teleTile = new WorldTile(baseTile, 5);
			if (World.isTileFree(baseTile.getPlane(), teleTile.getX(),
					teleTile.getY(), defs.size))
				break;
			teleTile = baseTile;
		}
		String name = defs.getName().toLowerCase();
		if (name.contains("shifter"))
			new Shifter(npcId, teleTile, -1, true, true, index, this);
		else if (name.contains("splatter"))
			new Splatter(npcId, teleTile, -1, true, true, index, this);
		else if (name.contains("spinner"))
			new Spinner(npcId, teleTile, -1, true, true, index, this);
		else if (name.contains("brawler"))
			brawlers.add(new PestMonsters(npcId, teleTile, -1, true, true,
					index, this));
		else
			new PestMonsters(npcId, teleTile, -1, true, true, index, this);
		return true;
	}

	public void endGame() {
		final List<Player> team = new LinkedList<Player>();
		team.addAll(this.team);
		this.team.clear();
		for (final Player player : team) {
			final int zeal = (int) ((PestControlGame) player
					.getControlerManager().getControler()).getPoints();
			player.getControlerManager().forceStop();
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					distributeReward(player, zeal);
				}
			}, 1);
		}
		/*
		 * 6sec is a waste of time lo
		 */
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				MapBuilder.destroyMap(boundChunks[0], boundChunks[1], 8, 8);
				boundChunks = null;
			}
		}, 1200, TimeUnit.MILLISECONDS);
	}

	private void distributeReward(Player player, int knightZeal) {
		if (knight.isDead())
			player.getDialogueManager()
					.startDialogue("SimpleMessage",
							"You failed to protect the void knight and have not been awarded any points.");
		else if (knightZeal < 5000)
			player.getDialogueManager()
					.startDialogue(
							"SimpleMessage",
							"The knights notice your lack of zeal in that battle and have not presented you with any points.");
		else {
			int commendation = data.getReward()
					* (Settings.getDropQuantityRate(player) + 1);
			player.getDialogueManager()
					.startDialogue(
							"SimpleMessage",
							"Congradulations! You have successfully kept the lander safe and have been awarded: "
									+ commendation + " commendation points.");
			player.setCommendation(player.getCommendation() + commendation);
		}
	}

	private void sendPortalInterfaces() {
		for (Player player : team) {
			for (int count = 0; count < portals.length; count++) {
				PestPortal npc = portals[count];
				if (npc != null) {
					player.getPackets().sendIComponentText(408, count + 9,
							npc.getHitpoints() + "");
					if (npc.isDead()) {
						player.getPackets().sendHideIComponent(408, count + 25,
								false);
						player.getPackets().sendHideIComponent(408, count + 20,
								true);
					}
				}
			}
			player.getPackets().sendIComponentText(408, 5,
					"" + knight.getHitpoints());
		}
	}

	public void unlockPortal() {
		do {
			int count = Utils.random(portals.length);
			PestPortal portal = portals[count];
			if (portal == null || portal.isDead())
				continue;
			portal.unlock();
		} while (true);
	}

	public boolean isBrawlerAt(WorldTile tile) {
		for (Iterator<NPC> it = brawlers.iterator(); it.hasNext();) {
			NPC npc = it.next();
			if (npc.isDead() || npc.hasFinished()) {
				it.remove();
				continue;
			}
			if (npc.getX() == tile.getX() && npc.getY() == tile.getY()
					&& tile.getPlane() == tile.getPlane())
				return true;
		}
		return false;
	}

	private void updateTime(int minutes) {
		for (Player player : team)
			player.getPackets().sendIComponentText(408, 4, minutes + " min");
	}

	public void sendTeamMessage(String message) {
		for (Player player : team)
			player.getPackets().sendGameMessage(message, true);
	}

	private boolean canFinish() {
		if (knight == null || knight.isDead())
			return true;
		return lockedPortals == 0;
	}

	public WorldTile getWorldTile(int mapX, int mapY) {
		if (boundChunks == null) // temporary fix..
			return Settings.HOME_LOCATION;
		return new WorldTile(boundChunks[0] * 8 + mapX, boundChunks[1] * 8
				+ mapY, 0);
	}

	public PestPortal[] getPortals() {
		return portals;
	}

	public List<Player> getPlayers() {
		return team;
	}

	public NPC getKnight() {
		return knight;
	}

	public enum PestData {

		NOVICE(new int[] { /* Shifters */
		3732, 3733, 3734, 3735, /* Ravagers */
		3742, 3743, 3744, /* Brawler */
		3772, 3773, /* Splatter */
		3727, 3728, 3729, /* Spinner */
		3747, 3748, 3749, /* Torcher */
		3752, 3753, 3754, 3755, /* Defiler */
		3762, 3763, 3764, 3765 }, new int[] { 3732, 3733, 3734, 3735 }, 2),

		INTERMEDIATE(new int[] { /* Shifters */
		3734, 3735, 3736, 3737, 3738, 3739/* Ravagers */, 3744, 3743, 3745, /* Brawler */
		3773, 3775, 3776, /* Splatter */
		3728, 3729, 3730, /* Spinner */
		3748, 3749, 3750, 3751, /* Torcher */
		3754, 3755, 3756, 3757, 3758, 3759, /* Defiler */
		3764, 3765, 3766, 3768, 3769 }, new int[] { 3734, 3735, 3736, 3737,
				3738, 3739 }, 3),

		VETERAN(new int[] { /* Shifters */
		3736, 3737, 3738, 3739, 3740, 3741 /* Ravagers */, 3744, 3745, 3746, /* Brawler */
		3776, 3774, /* Splatter */
		3729, 3730, 3731, /* Spinner */
		3749, 3750, 3751, /* Torcher */
		3758, 3759, 3760, 3761, /* Defiler */
		3770, 3771 }, new int[] { 3736, 3737, 3738, 3739, 3740, 3741 }, 4);

		private int[] waves, shifters;
		private int reward;

		private PestData(int[] pests, int[] shifters, int reward) {
			this.waves = pests;
			this.shifters = shifters;
			this.reward = reward;
		}

		public int[] getShifters() {
			return shifters;
		}

		public int[] getPests() {
			return waves;
		}

		public int getReward() {
			return reward;
		}
	}

	public int[] getPestCounts() {
		return pestCounts;
	}

	public PestData getPestData() {
		return data;
	}

	public int getPortalCount() {
		return lockedPortals;
	}
}
