package net.kagani.tools;

import java.io.File;
import java.io.IOException;

import net.kagani.cache.Cache;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.player.Player;
import net.kagani.utils.Logger;
import net.kagani.utils.saving.JsonFileManager;

public class MaxScape830ItemChecker {

	/**
	 * @author: 99max99 M
	 */

	public static void main(String[] args) {
		try {
			Cache.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File[] chars = new File("data/accounts").listFiles();
		for (File acc : chars) {
			try {
				Player player = (Player) JsonFileManager.loadPlayerFile(acc);
				for (int i = 1; i < 35000; i++) {
					if (!player.getInventory().containsItem(i, 1000000000)) {
						continue;
					}
					ItemDefinitions itemName = ItemDefinitions
							.getItemDefinitions(i);
					Logger.log("MaxScape830ItemChecker",
							player.getDisplayName() + " has x"
									+ player.getInventory().getAmountOf(i)
									+ " of " + itemName.getName() + " ("
									+ itemName.getId() + ")");
				}
			} catch (Throwable e) {
				Logger.logErr("MaxScape830ItemChecker", "Error while loading '"
						+ acc + "'.");
			}
		}
		Logger.log("MaxScape830ItemChecker", "Completed process - " + chars.length
				+ " accounts.");
	}
}