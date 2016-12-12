package net.kagani.game.player.bot;

import java.util.ArrayList;
import java.util.List;

import net.kagani.Settings;
import net.kagani.game.player.Player;
import net.kagani.utils.Logger;

public class BotWatch {

	/**
	 * @author: Dylan Page
	 * @author: 
	 */

	private static List<int[]> mousePositions = new ArrayList<int[]>();

	public static int[] readMousePosition(Player player, int x, int y,
			long clickRate, int packetId) {
		int[] position = { x, y };
		mousePositions.add(position);
		if (Settings.DEBUG)
			Logger.log("BotWatch", x + ", " + y);
		return position;
	}

	public void processActions() {
		for (int i = 1; i < mousePositions.size(); i++) {

		}
	}
}