package net.kagani.game.player.content;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;

public class ItemRemover {

	/*
	 * pots price update
	 */
	private static final int[] REMOVE_ITEMS_1 =

	{ 15486, 15487, 7398, 7399, 7400, 6889, 6890, 560, 565, 9075, 21773, 6914,
			6915, 2436, 2437, 2440, 2441, 2442, 2443, 2444, 2445, 3040, 3041,
			6685, 6686, 3024, 3025, 2434, 2435, 21630, 21631 };

	/*
	 * daganoth update
	 */
	private static final int[] REMOVE_ITEMS_2 =

	{ 6733, 6734, 6735, 6736, 6737, 6738, 6739, 6740, 15019, 15020, 15220 };

	private static void removeItems(Player player, int[] items) {
		for (int id : items) {
			int amt = player.getInventory().getAmountOf(id);
			if (amt > 0) {
				player.getInventory().deleteItem(id, amt);
				player.getInventory().addItemMoneyPouch(
						new Item(995,
								ItemDefinitions.getItemDefinitions(id).value
										* amt));
			}
			amt = player.getEquipment().getItems().getNumberOf(id);
			if (amt > 0) {
				player.getEquipment().deleteItem(id, amt);
				player.getInventory().addItemMoneyPouch(
						new Item(995,
								ItemDefinitions.getItemDefinitions(id).value
										* amt));
			}
			amt = player.getBank().removeAndReturnQuantity(id);
			if (amt > 0)
				player.getInventory().addItemMoneyPouch(
						new Item(995,
								ItemDefinitions.getItemDefinitions(id).value
										* amt));
		}
	}

	private static void replaceItems(Player player, int[] items, int[] newItems) {
		if (items.length != newItems.length) {
			System.out.println("ERROR: SOMEONE IS REALLY STUPID.");
			return;
		}
		for (int idx = 0; idx < items.length; idx++) {
			int orgId = items[idx], newId = newItems[idx];
			int amt = player.getInventory().getAmountOf(orgId);
			if (amt > 0) {
				player.getInventory().deleteItem(orgId, amt);
				player.getInventory().addItem(new Item(newItems[idx], amt));
			}
			amt = player.getBank().removeAndReturnQuantity(orgId);
			if (amt > 0)
				player.getBank().addItem(newId, amt, false);
		}
	}

	private static final int[] OLD_TOCKEN = new int[] { 13650 };

	public static void check(Player player) {// WARNING: Don't use else if or
		// else it won't end up well :D
		if (player.getEcoClearStage() > 10) {
			player.setEcoClearStage(10);
		}
	}
}