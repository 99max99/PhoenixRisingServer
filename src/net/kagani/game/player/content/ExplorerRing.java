package net.kagani.game.player.content;

import net.kagani.game.Animation;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.network.decoders.handlers.InventoryOptionsHandler;

public class ExplorerRing {

	/**
	 * @author: Dylan Page
	 */

	public static boolean handleOption(Player player, Item item, int slotId,
			int option) {
		switch (item.getId()) {
		case 13560: // Explorer's ring 1
			switch (option) {
			case 1:
				InventoryOptionsHandler.handleWear(player, slotId, item);
				return true;
			case 2:
				return true;
			case 3:
				handleRunEnergy(player, 1, 50);
				return true;
			}
			return false;
		case 13561: // Explorer's ring 2
			switch (option) {
			case 1:
				InventoryOptionsHandler.handleWear(player, slotId, item);
				return true;
			case 2:
				return true;
			case 3:
				handleRunEnergy(player, 2, 50);
				return true;
			}
			return false;
		case 13562: // Explorer's ring 3
			switch (option) {
			case 1:
				InventoryOptionsHandler.handleWear(player, slotId, item);
				return true;
			case 2:
				return true;
			case 3:
				handleRunEnergy(player, 3, 50);
				return true;
			}
			return false;
		case 19760: // Explorer's ring 4
			switch (option) {
			case 1:
				InventoryOptionsHandler.handleWear(player, slotId, item);
				return true;
			case 2:
				return true;
			case 3:
				handleRunEnergy(player, 4, 50);
				return true;
			}
			return false;
		}
		return false;
	}

	private static void handleRunEnergy(Player player, int ringId, int amount) {
		if (System.currentTimeMillis() - player.runEnergyDelay < 43200000) {
			player.getPackets().sendGameMessage(
					"You have used all your charges for today.");
			return;
		}
		int finalRunEnergy = player.getRunEnergy() + amount;
		if (player.getRunEnergy() > 99) {
			player.getPackets().sendGameMessage(
					"You already have full run energy.");
			return;
		}
		if (finalRunEnergy > 100)
			player.setRunEnergy(100);
		else
			player.setRunEnergy(player.getRunEnergy() + amount);
		player.setNextAnimation(new Animation(9988));
		player.getPackets()
				.sendGameMessage(
						"You feel refreshed as the ring revitalises you and a charge is used up.",
						true);
		reset(player, ringId);
		setDelay(player, ringId);
	}

	private static void reset(Player player, int ringId) {
		switch (ringId) {
		case 1:
			if (player.runEnergyCount >= 1)
				player.runEnergyCount = 0;
			break;
		case 2:
			if (player.runEnergyCount >= 2)
				player.runEnergyCount = 0;
			break;
		case 3:
			if (player.runEnergyCount >= 3)
				player.runEnergyCount = 0;
			break;
		case 4:
			if (player.runEnergyCount >= 4)
				player.runEnergyCount = 0;
			break;
		}
	}

	private static void setDelay(Player player, int ringId) {
		switch (ringId) {
		case 1:
			player.runEnergyCount++;
			if (player.runEnergyCount == 1)
				player.runEnergyDelay = System.currentTimeMillis();
			break;
		case 2:
			player.runEnergyCount++;
			if (player.runEnergyCount == 2)
				player.runEnergyDelay = System.currentTimeMillis();
			break;
		case 3:
			player.runEnergyCount++;
			if (player.runEnergyCount == 3)
				player.runEnergyDelay = System.currentTimeMillis();
			break;
		case 4:
			player.runEnergyCount++;
			if (player.runEnergyCount == 4)
				player.runEnergyDelay = System.currentTimeMillis();
			break;
		}
	}
}