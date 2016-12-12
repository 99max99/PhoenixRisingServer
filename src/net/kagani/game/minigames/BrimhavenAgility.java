package net.kagani.game.minigames;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import net.kagani.executor.GameExecutorManager;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;
import net.kagani.game.player.controllers.Controller;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public final class BrimhavenAgility extends Controller {

	private static final List<Player> players = new ArrayList<Player>();
	private static PlayingGame currentGame;
	private static BladesManager bladesManager;

	private static void removePlayer(Player player) {
		synchronized (players) {
			players.remove(player);
			if (player.getSize() == 0)
				cancelGame();
		}
		player.getHintIconsManager().removeUnsavedHintIcon();
		if (player.getTemporaryAttributtes().remove("BrimhavenAgility") != null)
			player.getVarsManager().sendVarBit(4456, 0);
		player.getInterfaceManager().removeWindowInterface(
				player.getInterfaceManager().hasRezizableScreen() ? 1 : 11);
	}

	private void addPlayer(Player player) {
		synchronized (players) {
			players.add(player);
			if (players.size() == 1)
				startGame();
			else
				PlayingGame.addIcon(player);
		}
		sendInterfaces();
	}

	private static void startGame() {
		// starts at 0 so that it selects a taggedDispenser
		GameExecutorManager.fastExecutor.scheduleAtFixedRate(
				currentGame = new PlayingGame(), 0, 60000);
		GameExecutorManager.fastExecutor.scheduleAtFixedRate(
				bladesManager = new BladesManager(), 5000, 5000);
	}

	private static void cancelGame() {
		currentGame.cancel();
		bladesManager.cancel();
		PlayingGame.taggedDispenser = null;
		currentGame = null;
		bladesManager = null;
	}

	private static class BladesManager extends TimerTask {

		@Override
		public void run() {

		}
	}

	private static class PlayingGame extends TimerTask {

		private static WorldTile taggedDispenser;

		private static WorldTile getNextDispenser() {
			while (true) {
				WorldTile tile = new WorldTile(2761 + 11 * Utils.random(5),
						9546 + 11 * Utils.random(5), 3);
				if (!(tile.getX() == 2805 && tile.getY() == 9590)
						&& !(taggedDispenser != null && tile
								.equals(taggedDispenser)))
					return tile;
			}
		}

		private static void addIcon(Player player) {
			Integer stage = (Integer) player.getTemporaryAttributtes().get(
					"BrimhavenAgility");
			if (stage != null)
				if (stage == -1) {
					player.getTemporaryAttributtes().remove("BrimhavenAgility"); // didnt
					// click
					player.getVarsManager().sendVarBit(4456, 0);
				} else
					player.getTemporaryAttributtes()
							.put("BrimhavenAgility", -1); // clicked
			if (taggedDispenser == null)
				return;
			player.getHintIconsManager().addHintIcon(taggedDispenser.getX(),
					taggedDispenser.getY(), taggedDispenser.getPlane(), 65, 2,
					0, -1, false);
		}

		@Override
		public void run() { // selects dispenser
			try {
				taggedDispenser = getNextDispenser();
				synchronized (players) {
					for (Player player : players)
						addIcon(player);
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}

	}

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (object.getId() == 3581 || object.getId() == 3608) {
			if (PlayingGame.taggedDispenser == null
					|| PlayingGame.taggedDispenser.getTileHash() != object
							.getTileHash()) {
				return false;
			}
			Integer stage = (Integer) player.getTemporaryAttributtes().get(
					"BrimhavenAgility");
			if (stage == null) {
				player.getTemporaryAttributtes().put("BrimhavenAgility", 0); // clicked
				player.getVarsManager().sendVarBit(4456, 1); // ready to get
				// tickets
				player.getPackets()
						.sendGameMessage(
								"You get tickets by tagging more than one pillar in a row. Tag the next pillar!");
			} else if (stage == 0) {
				player.getPackets()
						.sendGameMessage(
								"You have already tagged this pillar, wait until the arrow moves again.");
			} else {
				if (!player.getInventory().hasFreeSlots()
						&& !player.getInventory().containsOneItem(2996)) {
					player.getPackets().sendGameMessage(
							"Not enough space in your inventory.");
					return false;
				}
				player.getTemporaryAttributtes().put("BrimhavenAgility", 0); // clicked
				player.getInventory().addItem(2996, 1);
			}
			return false;
		}
		return true;
	}

	@Override
	public void start() {
		addPlayer(player);
	}

	@Override
	public boolean logout() {
		removePlayer(player);
		return false;
	}

	@Override
	public boolean login() {
		addPlayer(player);
		return false; // so doesnt remove script
	}

	@Override
	public void magicTeleported(int type) {
		removePlayer(player);
		removeControler();
	}

	@Override
	public void forceClose() {
		removePlayer(player);
	}

	@Override
	public boolean sendDeath() {
		removePlayer(player);
		removeControler();
		return true;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().setWindowInterface(
				player.getInterfaceManager().hasRezizableScreen() ? 1 : 11, 5);
	}
}
