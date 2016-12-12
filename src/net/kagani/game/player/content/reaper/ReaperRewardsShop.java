package net.kagani.game.player.content.reaper;

import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public class ReaperRewardsShop {

	/**
	 * @author: Dylan Page
	 */

	public static int INTER = 754;

	public static void handleButtons(Player player, int componentId) {
		switch (componentId) {
		default:
			player.getPackets().sendGameMessage(
					"This feature has not been added.");
			break;
		}
	}

	public static void openShop(Player player) {
		player.getInterfaceManager().sendCentralInterface(INTER);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getPackets().sendIComponentText(INTER, 43,
						player.getReaperPoints() + "");
				player.getPackets().sendIComponentText(INTER, 45,
						player.getSlayerManager().getPoints() + "");
				stop();
			}
		}, 0, 1);
	}
}