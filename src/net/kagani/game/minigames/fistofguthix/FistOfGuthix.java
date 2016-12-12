package net.kagani.game.minigames.fistofguthix;

import java.util.Collections;
import java.util.LinkedList;

import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.controllers.FistOfGuthixControler;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class FistOfGuthix {

	public static final int MINIMUM_NUMBER_OF_PLAYERS_NEEDED = 2;
	public static final WorldTile CENTRE = new WorldTile(1664, 5695, 0);
	private LinkedList<Player> caveMembers = new LinkedList<Player>();
	private LinkedList<Player> lobbyMembers = new LinkedList<Player>();
	private LinkedList<Participant> gameMembers = new LinkedList<Participant>();
	private long lobbyTicks;
	private boolean halfTime = false;
	public static final WorldTile[] huntedLocations = {
			new WorldTile(1658, 5665, 0), new WorldTile(1630, 5678, 0),
			new WorldTile(1626, 5710, 0), new WorldTile(1647, 5729, 0), };
	public static final WorldTile[] hunterLocations = {
			new WorldTile(1674, 5729, 0), new WorldTile(1690, 5714, 0),
			new WorldTile(1705, 5692, 0), new WorldTile(1687, 5665, 0), };

	public LinkedList<Player> caveMembers() {
		return caveMembers;
	}

	public LinkedList<Player> lobbyMembers() {
		return lobbyMembers;
	}

	public LinkedList<Participant> gameMembers() {
		return gameMembers;
	}

	public long lobbyTicks() {
		return lobbyTicks;
	}

	public void lobbyTicks(long ticks) {
		this.lobbyTicks = ticks;
	}

	public boolean halfTime() {
		return halfTime;
	}

	public FistOfGuthix(long ticks) {
		this.lobbyTicks = ticks;
	}

	public FistOfGuthix process() {
		for (Player player : World.getPlayers()) {
			if (player == null || player.hasFinished() || !player.hasStarted())
				continue;
			if (isAtCave(player) && !caveMembers().contains(player)
					&& player.hasStarted() && !player.hasFinished())
				caveMembers().add(player);
			if (isAtCave(player) || isInLobby(player))
				player.setCanPvp(false);
		}
		for (Player player : caveMembers()) {
			if (player == null || player.hasFinished() || !player.hasStarted()
					|| !caveMembers().contains(player))
				continue;
			LinkedList<Item> unusableItems = new LinkedList<Item>();
			if (player.hasStarted() && isAtCave(player) && player != null) {
				if (!player.getInterfaceManager().containsInterface(731))
					player.getInterfaceManager()
							.sendMinigameTab(
									player.getInterfaceManager()
											.hasRezizableScreen() ? 11 : 10);
				for (int i = 0; i < 28; i++) {
					if (player.getInventory().getItems().get(i) != null) {
						if (!isUsable(player.getInventory().getItems().get(i)))
							unusableItems.add(player.getInventory().getItems()
									.get(i));
					}
				}
				player.getPackets().sendIComponentText(731, 7,
						"Rating: " + player.fogRating());
				if (unusableItems.size() > 0)
					player.getPackets().sendIComponentText(
							731,
							25,
							"The following item is not allowed into the arena:<br> "
									+ unusableItems.getFirst().getDefinitions()
											.getName());
				player.getPackets().sendHideIComponent(731, 26,
						(!(unusableItems.size() > 0)));
				player.getPackets().sendHideIComponent(731, 17, true);
			}
		}
		for (Player player : caveMembers()) {
			if (player == null || player.hasFinished()
					|| !caveMembers().contains(player) || !player.hasStarted())
				continue;
			if (!isAtCave(player)) {
				if (caveMembers().contains(player))
					caveMembers().remove(player);
				if (player.getInterfaceManager().containsInterface(731)
						&& player != null)
					player.getPackets()
							.closeInterface(
									player.getInterfaceManager()
											.hasRezizableScreen() ? 11 : 10);
			}
		}
		for (Player player : World.getPlayers()) {
			if (player == null || player.hasFinished() || !player.hasStarted())
				continue;
			if (isInLobby(player)
					&& !lobbyMembers().contains(player)
					&& (team(player) == null || !gameMembers
							.contains(team(player))))
				lobbyMembers().add(player);
		}
		for (Player player : lobbyMembers()) {
			if (player == null || player.hasFinished()
					|| !lobbyMembers().contains(player) || !player.hasStarted())
				continue;
			long secs = (lobbyTicks - (((int) lobbyTicks() / 60) * 60));
			if (player.hasStarted() && isInLobby(player) && player != null) {
				if (!player.getInterfaceManager().containsInterface(731))
					player.getInterfaceManager()
							.sendMinigameTab(
									player.getInterfaceManager()
											.hasRezizableScreen() ? 11 : 10);
				player.getPackets().sendHideIComponent(731, 26, true);
				player.getPackets().sendHideIComponent(731, 17, false);
				player.getPackets().sendIComponentText(731, 7,
						"Rating: " + player.fogRating());
				player.getPackets()
						.sendIComponentText(
								731,
								16,
								(lobbyMembers().size() >= MINIMUM_NUMBER_OF_PLAYERS_NEEDED ? ("00:0"
										+ ((int) lobbyTicks() / 60)
										+ ":"
										+ (secs <= 9 ? "0" : "") + secs)
										: "Not enough players"));
			}
		}
		if (lobbyMembers().size() >= MINIMUM_NUMBER_OF_PLAYERS_NEEDED
				|| gameMembers.size() > 1)
			lobbyTicks--;
		if (lobbyTicks == 0) {
			if (lobbyMembers().size() >= MINIMUM_NUMBER_OF_PLAYERS_NEEDED
					|| lobbyMembers.size() > 1) {
				Collections.shuffle(lobbyMembers());
				for (Player player : lobbyMembers()) {
					if (player == null || player.hasFinished()
							|| !lobbyMembers().contains(player)
							|| !player.hasStarted())
						continue;
					for (Player p2 : lobbyMembers()) {
						if (p2 == null || p2 == null
								|| !lobbyMembers().contains(p2)
								|| !p2.hasStarted())
							continue;
						if (p2 != null && p2 != player
								&& gameMembers.size() <= 125) {
							Participant p = new Participant(player, p2);
							gameMembers().add(p);
							if (lobbyMembers().contains(player))
								lobbyMembers().remove(player);
							if (lobbyMembers().contains(p2))
								lobbyMembers().remove(p2);
						}
						for (Participant p : gameMembers()) {
							if (p == null || !gameMembers().contains(p)
									|| p.hunter() == null
									|| p.hunter().hasFinished()
									|| !p.hunter().hasStarted()
									|| p.hunted() == null
									|| p.hunted().hasFinished()
									|| !p.hunted().hasStarted())
								continue;
							int loc = Utils.random(hunterLocations.length);
							p.hunter().setNextWorldTile(hunterLocations[loc]);
							p.hunted().setNextWorldTile(huntedLocations[loc]);
							p.hunter().setCanPvp(true);
							p.hunted().setCanPvp(true);
						}
					}
				}
				for (Player p : lobbyMembers()) {
					if (p == null || p.hasFinished()
							|| !lobbyMembers().contains(p) || !p.hasStarted())
						continue;
					p.getPackets()
							.sendGameMessage(
									gameMembers.size() == 125 ? "You have been left behind because the game is full."
											: "You have been left behind because the server was unable to pair you with a partner.");
				}
				lobbyTicks = 60;
			} else {
				for (Player player : lobbyMembers()) {
					if (player == null || player.hasFinished()
							|| !lobbyMembers().contains(player)
							|| !player.hasStarted())
						continue;
					player.getPackets().sendGameMessage(
							"Not enough players to start a game, you need "
									+ MINIMUM_NUMBER_OF_PLAYERS_NEEDED
									+ " - 250 players to run a game.");
				}
			}
		}
		for (Participant p : gameMembers()) {
			if (p == null || p.hunter() == null || p.hunter().hasFinished()
					|| !p.hunter().hasStarted() || p.hunted() == null
					|| p.hunted().hasFinished() || !p.hunted().hasStarted())
				continue;
			if (p.hunted().getInterfaceManager().containsInterface(731))
				p.hunted()
						.getPackets()
						.closeInterface(
								p.hunted().getInterfaceManager()
										.hasRezizableScreen() ? 11 : 10);
			if (p.hunter().getInterfaceManager().containsInterface(731))
				p.hunter()
						.getPackets()
						.closeInterface(
								p.hunter().getInterfaceManager()
										.hasRezizableScreen() ? 11 : 10);
			if (!p.hunter().getInterfaceManager().containsInterface(730))
				p.hunter()
						.getInterfaceManager()
						.sendMinigameTab(
								p.hunter().getInterfaceManager()
										.hasRezizableScreen() ? 11 : 10);
			if (!p.hunted().getInterfaceManager().containsInterface(730))
				p.hunted()
						.getInterfaceManager()
						.sendMinigameTab(
								p.hunted().getInterfaceManager()
										.hasRezizableScreen() ? 11 : 10);
			p.process();
		}
		return this;
	}

	public Participant team(Player player) {
		for (Participant p : gameMembers()) {
			if (p == null || p.hunted() == null || p.hunter() == null
					|| p.hunter().hasFinished() || !p.hunter().hasStarted()
					|| p.hunted().hasFinished() || !p.hunted().hasStarted())
				continue;
			if (p.hunted() == player || p.hunter() == player)
				return p;
		}
		return null;
	}

	public int calculateDistance(Player player) {
		double y = Math.pow((Math.abs(CENTRE.getY() - player.getY())), 2);
		double x = Math.pow((Math.abs(CENTRE.getX() - player.getX())), 2);
		double distance = Math.sqrt(y + x);
		return (int) (distance + 0.5);
	}

	public void pickUpStone(Player player) {
		boolean canTake = false;
		for (Participant p : gameMembers()) {
			if (p.hunted() == null || p.hunted().hasFinished()
					|| !gameMembers().contains(p) || !p.hunted().hasStarted())
				continue;
			if (p.hunted() == player)
				canTake = true;
		}
		if (player.getInventory().containsItem(12845, 1))
			canTake = false;
		if (player.getEquipment().getWeaponId() == 12845)
			canTake = false;
		if (canTake)
			player.getInventory().addItem(12845, 1);
		else
			player.getPackets().sendGameMessage("You cannot take that");
	}

	public int points(Player player) {
		if (calculateDistance(player) >= 0 && calculateDistance(player) <= 5)
			return 27;
		if (calculateDistance(player) >= 6 && calculateDistance(player) <= 10)
			return 23;
		if (calculateDistance(player) >= 11 && calculateDistance(player) <= 15)
			return 19;
		if (calculateDistance(player) >= 16 && calculateDistance(player) <= 20)
			return 14;
		if (calculateDistance(player) >= 21 && calculateDistance(player) <= 25)
			return 10;
		if (calculateDistance(player) >= 26 && calculateDistance(player) <= 30)
			return 6;
		if (calculateDistance(player) >= 31 && calculateDistance(player) <= 50)
			return 3;
		return 0;
	}

	public void handlePortalEnter(Player player) {
		player.setNextWorldTile(new WorldTile(1677, 5599, 0));
	}

	public void handlePortalExit(Player player) {
		player.setNextWorldTile(new WorldTile(2969, 9672, 0));
	}

	public void enterPassageway(Player player) {
		if (caveMembers().contains(player)) {
			boolean canEnter = true;
			caveMembers().remove(player);
			LinkedList<Item> unusableItems = new LinkedList<Item>();
			for (int i = 0; i < 28; i++) {
				if (player.getInventory().getItems().get(i) != null) {
					if (!isUsable(player.getInventory().getItems().get(i)))
						unusableItems.add(player.getInventory().getItems()
								.get(i));
				}
			}
			if (unusableItems.size() > 0)
				canEnter = false;
			if (!canEnter) {
				player.getPackets()
						.sendGameMessage(
								"An item you are carrying is not allowed into the arena, please bank it.");
				return;
			}
			if (player.getPet() != null || player.getFamiliar() != null) {
				player.getPackets().sendGameMessage(
						"You cannot enter with a pet or familiar.");
				return;
			}
			if (!(player.getControlerManager().getControler() instanceof FistOfGuthixControler))
				player.getControlerManager().startControler(
						"FistOfGuthixControler");
			player.setNextWorldTile(new WorldTile(1653, 5601, 0));
		} else if (lobbyMembers().contains(player)) {
			lobbyMembers().remove(player);
			player.setNextWorldTile(new WorldTile(1718, 5599, 0));
			if ((FistOfGuthixControler) player.getControlerManager()
					.getControler() != null)
				((FistOfGuthixControler) player.getControlerManager()
						.getControler()).exit();
			player.getControlerManager().removeControlerWithoutCheck();
		}
	}

	public boolean isAtCave(Player player) {
		return (player.getX() >= 1672 && player.getX() <= 1720
				&& player.getY() >= 5592 && player.getY() <= 5608);
	}

	public boolean isInLobby(Player location) {
		return (location.getX() >= 1604 && location.getX() <= 1653
				&& location.getY() >= 5579 && location.getY() <= 5627);
	}

	public boolean isUsable(Item item) {
		if (item.getDefinitions().containsOption("Wear")
				|| item.getDefinitions().containsOption("Wield")
				|| item.getDefinitions().getName().contains(" rune")
				|| item.getDefinitions().getName().contains(" guthix token"))// temporary
			return true;
		if (item.getDefinitions().getName().toLowerCase().contains("pouch")
				|| item.getDefinitions().getName().toLowerCase()
						.contains("ruined backpack"))
			return false;
		return false;
	}

	/*
	 * public void switchHidden(Player player) { if (team(player) == null)
	 * return; if (team(player) != null && team(player).hunter() == player) { if
	 * (player.getAppearance().isHidden())
	 * player.getAppearance().switchHidden(); } if (team(player) != null &&
	 * team(player).hunted() == player) { if (insideBarrier(player) &&
	 * !player.getAppearance().isHidden()) {//remove markers
	 * player.getAppearance().switchHidden();
	 * team(player).hunter().getHintIconsManager().removeAll();
	 * team(player).hunter().getHintIconsManager().removeUnsavedHintIcon(); }
	 * else if (!insideBarrier(player) && player.getAppearance().isHidden())
	 * {//add markers player.getAppearance().switchHidden();
	 * team(player).hunter(
	 * ).getHintIconsManager().addHintIcon(team(player).hunted(), 0, -1, false);
	 * } }
	 * 
	 * }
	 */

	public void handleBarriers(WorldObject barrier, Player player) {
		if (team(player) != null && team(player).hunter() == player) {
			player.getPackets().sendGameMessage(
					"The magical barrier prevents you from crossing.");
			return;
		}
		if (team(player) != null
				&& team(player).lastEnteredHouse() != 0
				&& !(Utils.currentTimeMillis()
						- team(player).lastEnteredHouse() >= 4000)
				&& !insideBarrier(player)) {
			player.getPackets().sendGameMessage(
					"You can only enter a house once every 4 seconds.");
			return;
		}
		switch (barrier.getRotation()) {
		case 1:
			if (!insideBarrier(player) && player.getX() == barrier.getX()
					&& player.getY() == barrier.getY()) {
				player.addWalkSteps(player.getX(), player.getY() + 1,
						player.getPlane(), false);
				// switchHidden(player);
				team(player).lastEnteredHouse(Utils.currentTimeMillis());
			} else if (player.getX() == barrier.getX()) {
				player.addWalkSteps(player.getX(), player.getY() - 1,
						player.getPlane(), false);
				// switchHidden(player);
				team(player).lastEnteredHouse(Utils.currentTimeMillis());
			}
			break;
		case 2:
			if (!insideBarrier(player) && player.getX() == barrier.getX()
					&& player.getY() == barrier.getY()) {
				player.addWalkSteps(player.getX() + 1, player.getY(),
						player.getPlane(), false);
				// switchHidden(player);
				team(player).lastEnteredHouse(Utils.currentTimeMillis());
			} else if (player.getY() == barrier.getY()) {
				player.addWalkSteps(player.getX() - 1, player.getY(),
						player.getPlane(), false);
				// switchHidden(player);
				team(player).lastEnteredHouse(Utils.currentTimeMillis());
			}
			break;
		case 0:
			if (!insideBarrier(player) && player.getX() == barrier.getX()
					&& player.getY() == barrier.getY()) {
				player.addWalkSteps(player.getX() - 1, player.getY(),
						player.getPlane(), false);
				// switchHidden(player);
				team(player).lastEnteredHouse(Utils.currentTimeMillis());
			} else if (player.getY() == barrier.getY()) {
				player.addWalkSteps(player.getX() + 1, player.getY(),
						player.getPlane(), false);
				// switchHidden(player);
				team(player).lastEnteredHouse(Utils.currentTimeMillis());
			}
			break;
		case 3:
			if (!insideBarrier(player) && player.getX() == barrier.getX()
					&& player.getY() == barrier.getY()) {
				player.addWalkSteps(player.getX(), player.getY() - 1,
						player.getPlane(), false);
				// switchHidden(player);
				team(player).lastEnteredHouse(Utils.currentTimeMillis());
			} else if (player.getX() == barrier.getX()) {
				player.addWalkSteps(player.getX(), player.getY() + 1,
						player.getPlane(), false);
				// switchHidden(player);
				team(player).lastEnteredHouse(Utils.currentTimeMillis());
			}
			break;
		}
	}

	public void handlePortalEntering(WorldObject portal, final Player player) {
		if (!insideBarrier(player))
			return;
		if (team(player) != null && team(player).hunter() == player)
			return;
		LinkedList<WorldTile> portalWorldTiles = new LinkedList<WorldTile>();
		portalWorldTiles.add(new WorldTile(1651, 5703, 0));
		portalWorldTiles.add(new WorldTile(1656, 5683, 0));
		portalWorldTiles.add(new WorldTile(1667, 5704, 0));
		portalWorldTiles.add(new WorldTile(1676, 5688, 0));
		Collections.shuffle(portalWorldTiles);
		for (final WorldTile l : portalWorldTiles) {
			if (portal.getX() != l.getX() && portal.getY() != l.getY()) {
				player.addWalkSteps(portal.getX(), portal.getY(),
						player.getPlane(), false);
				player.getPackets().sendGameMessage(
						"You step into the portal...");
				player.lock();
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						player.setNextWorldTile(l);
						player.setNextGraphics(new Graphics(2000));
						player.unlock();
						stop();
					}
				}, 1);
				break;
			}
		}
	}

	public boolean insideBarrier(Player location) {
		return ((location.getX() >= 1650 && location.getX() <= 1652
				&& location.getY() >= 5702 && location.getY() <= 5704)
				|| (location.getX() >= 1655 && location.getX() <= 1657
						&& location.getY() >= 5682 && location.getY() <= 5684)
				|| (location.getX() >= 1666 && location.getX() <= 1668
						&& location.getY() >= 5703 && location.getY() <= 5705) || (location
				.getX() >= 1675
				&& location.getX() <= 1677
				&& location.getY() >= 5687 && location.getY() <= 5689));
	}

	public void death(Player player) {
		for (Participant p : gameMembers()) {
			if (p.hunted() == null || p.hunted().hasFinished()
					|| p.hunter() == null || p.hunter().hasFinished()
					|| p == null)
				continue;
			if (p.hunted() == player || p.hunter() == player)
				p.processDeath(player);
		}
	}

	public void serverShutdown() {
		for (Participant p : gameMembers()) {
			if (p == null)
				continue;
			p.shutdownServer();
		}
	}
}