package net.kagani.game.minigames.pest;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import net.kagani.Settings;
import net.kagani.executor.GameExecutorManager;
import net.kagani.game.WorldTile;
import net.kagani.game.minigames.pest.PestControl.PestData;
import net.kagani.game.player.Player;
import net.kagani.utils.Logger;

public class Lander {

	public static Lander[] landers = new Lander[3];
	private static final int AUTO_GAME = Settings.DEBUG ? 1 : 15;
	private static final int LANDER_TIME = 90;

	private List<Player> byStanders = Collections
			.synchronizedList(new LinkedList<Player>());
	private LobbyTimer timer;
	private LanderRequirement landerRequirement;

	public Lander(LanderRequirement landerRequirement) {
		this.landerRequirement = landerRequirement;
	}

	public class LobbyTimer extends TimerTask {

		private int seconds = LANDER_TIME;

		@Override
		public void run() {
			try {
				if (seconds == 0 && byStanders.size() >= 5
						|| byStanders.size() >= AUTO_GAME)
					begin();
				else if (seconds == 0)
					seconds = LANDER_TIME;
				else if (byStanders.size() == 0) {
					cancel();
					return;
				}
				seconds--;
				if (seconds % 30 == 0)
					refresh();
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}

		public int getMinutes() {
			return seconds / 60;
		}
	}

	private void begin() {
		final List<Player> playerList = new LinkedList<Player>();
		playerList.addAll(Collections.synchronizedList(byStanders));
		byStanders.clear();
		if (playerList.size() > AUTO_GAME) {
			for (int index = AUTO_GAME; index < playerList.size(); index++) {
				Player player = playerList.get(index);
				if (player == null) {
					playerList.remove(index);
					continue;
				}
				player.getPackets().sendGameMessage(
						"You have received priority over other players.");
				playerList.remove(index);
				byStanders.add(player);
			}
		}
		new PestControl(playerList, PestData.valueOf(landerRequirement.name()))
				.create();
	}

	public void enter(Player player) {
		if (byStanders.size() == 0)
			GameExecutorManager.fastExecutor.schedule(timer = new LobbyTimer(),
					1000, 1000);
		player.getControlerManager().startControler("PestControlLobby",
				landerRequirement.ordinal());
		add(player);
		player.useStairs(-1, landerRequirement.getWorldTile(), 1, 2,
				"You board the lander.");
		if (player.getCommendation() == 1000)
			player.getPackets()
					.sendGameMessage(
							"Warning! Winning the upcomming game will not result in a reward. Please spend some Commendation.");
	}

	public void exit(Player player) {
		player.useStairs(-1, landerRequirement.getExitTile(), 1, 2,
				"You leave the lander.");
		remove(player);
	}

	private void refresh() {
		for (Player teamPlayer : byStanders)
			teamPlayer.getControlerManager().getControler().sendInterfaces();
	}

	public void add(Player player) {
		byStanders.add(player);
		refresh();
	}

	public void remove(Player player) {
		byStanders.remove(player);
		refresh();
	}

	public List<Player> getByStanders() {
		return byStanders;
	}

	public static Lander[] getLanders() {
		return landers;
	}

	public static enum LanderRequirement {

		NOVICE(40, new WorldTile(2661, 2639, 0), new WorldTile(2657, 2639, 0)),

		INTERMEDIATE(70, new WorldTile(2641, 2644, 0), new WorldTile(2644,
				2644, 0)),

		VETERAN(100, new WorldTile(2635, 2653, 0), new WorldTile(2638, 2653, 0));

		public static LanderRequirement forId(int id) {
			for (LanderRequirement reqs : LanderRequirement.values()) {
				if (reqs.ordinal() == id)
					return reqs;
			}
			return null;
		}

		private int requirement;
		private int[] pests;
		private WorldTile tile, exit;

		private LanderRequirement(int requirement, WorldTile tile,
				WorldTile exit) {
			this.requirement = requirement;
			this.tile = tile;
			this.exit = exit;
		}

		public int getRequirement() {
			return requirement;
		}

		public WorldTile getWorldTile() {
			return tile;
		}

		public WorldTile getExitTile() {
			return exit;
		}
	}

	public LanderRequirement getLanderRequirement() {
		return landerRequirement;
	}

	static {
		for (int i = 0; i < landers.length; i++)
			landers[i] = new Lander(LanderRequirement.forId(i));
	}

	@Override
	public String toString() {
		return landerRequirement.name().toLowerCase();
	}

	public static boolean canEnter(Player player, int landerIndex) {
		Lander lander = landers[landerIndex];
		if (player.getSkills().getCombatLevelWithSummoning() < lander
				.getLanderRequirement().requirement) {
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You need a combat level of "
							+ lander.getLanderRequirement().getRequirement()
							+ " or more to enter in boat.");
			return false;
		} else if (player.getPet() != null || player.getFamiliar() != null) {
			player.getPackets()
					.sendGameMessage(
							"You can't take a follower into the lander, there isn't enough room!");
			return false;
		}
		lander.enter(player);
		return true;
	}

	public LobbyTimer getTimer() {
		return timer;
	}
}
