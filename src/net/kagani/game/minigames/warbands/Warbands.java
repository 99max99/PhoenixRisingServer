package net.kagani.game.minigames.warbands;

import java.util.HashMap;
import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

public final class Warbands {

	private int resource = 0;

	public enum WarbandEvent {
		CAMP_1(new int[] { 3026, 3040, 3586, 3599 }, 0, new WorldTile(3037,
				3592, 0)),

		CAMP_2(new int[] { 3296, 3313, 3762, 3777 }, 1, new WorldTile(3308,
				3772, 0)),

		CAMP_3(new int[] { 3128, 3141, 3836, 3852 }, 2, new WorldTile(3132,
				3839, 0));

		private int[] base;
		private int event;
		public WorldTile tile;

		private WarbandEvent(int[] base, int event, WorldTile tile) {
			this.base = base;
			this.event = event;
			this.tile = tile;
		}

		public static WarbandEvent getEvent(int event) {
			for (WarbandEvent e : values()) {
				if (e.event == event)
					return e;
			}
			return null;
		}
	}

	public WarbandsMinion[] minions;
	public WarbandsReinforcement[] reinforcements;

	public void spawnCombatants() {
		if (warband == null)
			return;
		WorldTile tile = WarbandEvent.getEvent(warband.warbandEvent).tile;
		WorldTile[] tiles = new WorldTile[8];
		for (int i = 0; i < tiles.length; i++) {
			int tryCount = 0;
			while (tiles[i] == null) {
				WorldTile spawn = new WorldTile(tile.getX()
						+ Utils.random(-2, 2), tile.getY()
						+ Utils.random(-2, 2), 0);
				if (!World.canMoveNPC(spawn.getPlane(), spawn.getX(),
						spawn.getY(), 1)
						&& tryCount < 10) {
					tryCount++;
					continue;
				}
				tiles[i] = spawn;
				break;
			}
		}
		int count = 0;
		for (int i = 0; i < WarbandsConstants.MINIONS[warband.type].length; i++) {
			for (int x = 0; x < (i == WarbandsConstants.MINIONS[warband.type].length - 1 ? 3
					: 2); x++) {
				minions[i] = new WarbandsMinion(
						WarbandsConstants.MINIONS[warband.type][i],
						new WorldTile(tiles[count++]), -1, true, true);
				minions[i].warband = this;
				minions[i].attackable = true;
				minions[i].setNextGraphics(new Graphics(3032));
				minions[i].setRandomWalk(0);
				minions[i].setNextFaceWorldTile(tile);
			}
		}
		this.remainingOccupants = count;
	}

	public void spawnReinforcements() {
		if (warband == null)
			return;
		WorldTile tile = WarbandEvent.getEvent(warband.warbandEvent).tile;
		WorldTile[] tiles = new WorldTile[6];
		for (int i = 0; i < tiles.length; i++) {
			int tryCount = 0;
			while (tiles[i] == null) {
				WorldTile spawn = new WorldTile(tile.getX()
						+ Utils.random(-3, 3), tile.getY()
						+ Utils.random(-3, 3), 0);
				if (!World.canMoveNPC(spawn.getPlane(), spawn.getX(),
						spawn.getY(), 1)
						&& tryCount < 10) {
					tryCount++;
					continue;
				}
				tiles[i] = spawn;
				break;
			}
		}
		int count = 0;
		skip: for (int i = 0; i < WarbandsConstants.MINIONS[warband.type == 0 ? 1
				: 0].length; i++) {
			for (int x = 0; x < 2; x++) {
				reinforcements[i] = new WarbandsReinforcement(
						WarbandsConstants.MINIONS[warband.type == 0 ? 1 : 0][i],
						new WorldTile(tiles[count++]), -1, true, true);
				reinforcements[i].warband = this;
				reinforcements[i].setNextGraphics(new Graphics(3032));
				if (count >= tiles.length)
					break skip;
			}
		}
	}

	/**
	 * Time at which event was started
	 */
	public long time;
	/**
	 * Awarded the player a rare reward
	 */
	public boolean awarded;
	/**
	 * The warband event; used to be called in other classes; static so only
	 * once instance of it exists
	 */
	public static Warbands warband;
	/**
	 * warbandEvent - Determines which event is taking place (0, 1, 2) each in
	 * different wilderness levels type - the NPC index to be spawned
	 */
	public int warbandEvent, type;
	/**
	 * The minx, maxx, miny, maxy of the event.
	 */
	public int base[] = new int[4];
	/**
	 * The base amount of resources
	 */
	public int resources, charges, remainingOccupants;

	/**
	 * Determines whether or not the object has resources to collect; used in
	 * the decreaseRemainingResources method.
	 */
	public boolean objectHasResources(WorldObject object) {
		switch (object.getId()) {
		case 83301:
		case 83310:
		case 83319:
		case 83328:
		case 83336:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Name of warbands event
	 */
	public String getWarbandsEventType() {
		switch (type) {
		case 0:
			return "Armadylean";
		case 1:
		default:
			return "Bandosian";
		}
	}

	/**
	 * Determines whether or not the player is carrying resources from warbands;
	 * will be used in the wilderness controller to skull them.
	 */
	public boolean isCarryingResources(Player player) {
		for (int i = 0; i < 28; i++) {
			if (player.getInventory().getItem(i) == null)
				continue;
			if (player.getInventory().getItem(i).getId() == 27637
					|| player.getInventory().getItem(i).getId() == 27636
					|| player.getInventory().getItem(i).getId() == 27639
					|| player.getInventory().getItem(i).getId() == 27640
					|| player.getInventory().getItem(i).getId() == 27638)
				return true;
		}
		return false;
	}

	/**
	 * Determines whether or not the player is in the designated warbands area;
	 * used to handle the collection of resources.
	 */
	public boolean isInArea(WorldObject object) {
		if (warband == null || object == null)
			return false;
		if (object.getX() < warband.base[0] || object.getX() > warband.base[1]
				|| object.getY() < warband.base[2]
				|| object.getY() > warband.base[3])
			return false;
		return true;
	}

	/**
	 * Tracks the players who have collected resources
	 */
	public HashMap<String, Integer> collectedResources;

	/**
	 * The method used in object handler to decrease the remaining amount of
	 * resources from the game and hence finish it
	 */
	public boolean decreaseRemainingResources(Player player, WorldObject object) {
		if (object == null || player == null || warband == null) {
			return false;
		}
		// Integer collected = collectedResources.remove(player.getUsername());
		Integer collected = collectedResources.remove(player.getSession()
				.getIP());
		if (collected == null) {
			// collected = 25;
			collected = 50;
			// collectedResources.put(player.getUsername(), collected);
			collectedResources.put(player.getSession().getIP(), collected);
		} else {
			collected--;
			// collectedResources.put(player.getUsername(), collected);
			collectedResources.put(player.getSession().getIP(), collected);
			if (collected <= 0) {
				player.getPackets()
						.sendGameMessage(
								"You're only able to collect a total of 50 resources per warband event!");
				return false;
			}
		}
		if (warband.resources >= 1) {
			warband.resources--;
			player.setNextAnimation(new Animation(881));
			switch (object.getId()) {
			case 83301: // herblore
				resource = 27637;
				break;
			case 83310: // construction
				resource = 27636;
				break;
			case 83319: // farming
				resource = 27639;
				break;
			case 83328: // mining
				resource = 27640;
				break;
			case 83336: // smithing
				resource = 27638;
				break;
			}
			player.getInventory().addItem(resource, 1);
			player.getPackets().sendGameMessage("You loot the camp.", true);
			int random = Utils.random(warband.resources);
			if (warband.resources > 10 ? random < 5 && !warband.awarded
					: !warband.awarded) {
				Item item = new Item(27641, 1);
				if (!player.getInventory().addItem(item))
					World.addGroundItem(item, player, player, true, 180);
				sendLocalPlayerMessage(player, player.getDisplayName()
						+ " received " + item.getName().toLowerCase()
						+ " from the " + object.getDefinitions().name + ".!");
				awarded = true;
			}
			return true;
		}
		return false;
	}

	/**
	 * Finishes the warband event and removes the current one, then sets the
	 * next one
	 */
	public void finish() {
		if (warband == null)
			return;
		final int prev = warband.warbandEvent;
		warband = null;
		int i = prev;
		int count = 0;
		while (i == prev) {
			int random = Utils.random(WarbandEvent.values().length);
			if (random == i && count < 10) {
				count++;
				continue;
			}
			warband = new Warbands(random);
			break;
		}
	}

	/**
	 * A new warband event
	 * 
	 * @param event
	 *            determines the location of the warband event (0, 1, 2)
	 */
	public Warbands(int event) {
		if (WarbandEvent.getEvent(event) == null)
			return;
		this.warbandEvent = event;
		for (int i = 0; i < WarbandEvent.getEvent(event).base.length; i++) {
			this.base[i] = WarbandEvent.getEvent(event).base[i];
		}
		this.type = Utils.random(WarbandsConstants.MINIONS.length);
		this.resources = 400;
		this.charges = 150;
		this.minions = new WarbandsMinion[7];
		this.reinforcements = new WarbandsReinforcement[6];
		this.collectedResources = new HashMap<String, Integer>(resources);
		World.sendWarbandsNews("A new " + getWarbandsEventType()
				+ " warband has been spotted somewhere between levels "
				+ getWildLevel(base[2]) + " and " + getWildLevel(base[3])
				+ " of the wilderness!", 2);
		warband = this;
		spawnCombatants();
		this.time = Utils.currentTimeMillis() + 3000 * 3600;
	}

	/**
	 * The wilderness level of the warband event
	 */
	public static int getWildLevel(int y) {
		if (y > 9900)
			return (y - 9912) / 8 + 1;
		return (y - 3520) / 8 + 1;
	}

	public static void sendLocalPlayerMessage(Player player, String message) {
		for (int regionId : player.getMapRegionsIds()) {
			List<Integer> playersIndexes = World.getRegion(regionId)
					.getPlayerIndexes();
			if (playersIndexes == null) {
				continue;
			}
			for (Integer playerIndex : playersIndexes) {
				Player p = World.getPlayers().get(playerIndex);
				if (p == null
						|| !p.hasStarted()
						|| p.hasFinished()
						|| p.getLocalPlayerUpdate().getLocalPlayers()[player
								.getIndex()] == null) {
					continue;
				}
				p.getPackets().sendGameMessage(message);
			}
		}
	}
}