package net.kagani.game.events;

import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

public class DropEvent {

	/**
	 * @author: Dylan Page
	 */

	/** The Area. */
	public static String dropName;

	/** The drop. */
	public static int drop;

	/** The get x. */
	public static int getX;

	/** The get y. */
	public static int getY;

	/** the get plane. */
	public static int getPlane;

	/** The spawned item. */
	public static int spawnedItem;

	/** The random places. */
	public static int randomPlaces;

	public static int x, y, z;

	public static String hint;

	/**
	 * Hide & Seek.
	 */
	public static void hideAndSeek(Player player) {
		switch (Utils.random(6)) {
		case 0:
			x = 3222;
			y = 3222;
			z = 0;
			drop = 1961;
			setDropName("Easter egg");
			hint = "The place we all started at once in RuneScape...";
			break;
		case 1:
			x = 3256;
			y = 3425;
			z = 0;
			drop = 7927;
			setDropName("Easter ring");
			hint = "We use to trade here before the Grand Exchange...";
			break;
		case 2:
		case 3:
			x = 3070;
			y = 3498;
			z = 0;
			drop = 1961;
			setDropName("Easter egg");
			hint = "Just over the ditch, is the way to death by another player...";
			break;
		case 4:
			x = 2970;
			y = 3390;
			z = 0;
			drop = 7927;
			setDropName("Easter ring");
			hint = "Here lies the White Knights' Castle...";
			break;
		}
		player.getInventory().addItem(drop, 1);
		player.setNextWorldTile(new WorldTile(x, y, z));
		World.sendWorldMessage(
				"<col=FF0000><img=4>Find " + player.getDisplayName()
						+ " & trade for a " + getDropName() + "!", false);
		World.sendWorldMessage("<col=FF0000><img=4>Hint: " + hint, false);
	}

	/**
	 * Spawn.
	 */
	public static void spawn(Player player) {
		switch (randomPlaces = Utils.random(6)) {
		case 0:
			getX = player.getX() + 1;
			getY = player.getY() - 4;
			getPlane = player.getPlane();
			drop = 1961;
			setDropName("Easter egg");
			break;
		case 1:
			getX = player.getX() - 3;
			getY = player.getY() - 2;
			getPlane = player.getPlane();
			drop = 7927;
			setDropName("Easter ring");
			break;
		case 3:
			getX = player.getX() - 2;
			getY = player.getY() + 4;
			getPlane = player.getPlane();
			drop = 1961;
			setDropName("Easter egg");
			break;
		case 4:
			getX = player.getX() + 3;
			getY = player.getY() + 3;
			getPlane = player.getPlane();
			drop = 7927;
			setDropName("Easter ring");
			break;
		}
		World.sendWorldMessage("<col=FF0000><img=4>A " + getDropName()
				+ " was dropped!", false);
		World.addGroundItem(new Item(drop, 1), getSpawn());
	}

	/**
	 * Gets the dropped area.
	 *
	 * @return the dropped area
	 */
	public static String getDropName() {
		return dropName;
	}

	/**
	 * Gets the spawn.
	 *
	 * @return the spawn
	 */
	public static WorldTile getSpawn() {
		return new WorldTile(getX, getY, getPlane);
	}

	/**
	 * Sets the dropped area.
	 *
	 * @param droppedLocation
	 *            the new dropped area
	 */
	public static void setDropName(String name) {
		dropName = name;
	}
}