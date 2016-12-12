package net.kagani.game.player;

import net.kagani.game.item.Item;
import net.kagani.game.item.ItemsContainer;
import net.kagani.game.player.content.ItemConstants;
import net.kagani.game.player.content.grandExchange.GrandExchange;

public class PriceCheckManager {

	private Player player;
	private ItemsContainer<Item> pcInv;

	public PriceCheckManager(Player player) {
		this.player = player;
		pcInv = new ItemsContainer<Item>(28, false);
	}

	public void openPriceCheck() {
		player.getInterfaceManager().sendInventoryInterface(207);
		player.getInterfaceManager().sendCentralInterface(206);
		sendInterItems();
		sendOptions();
		player.getPackets().sendCSVarInteger(728, 0);
		for (int i = 0; i < pcInv.getSize(); i++)
			player.getPackets().sendCSVarInteger(700 + i, 0);
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				player.getInventory().getItems().addAll(pcInv);
				player.getInventory().init();
				pcInv.clear();
			}
		});
	}

	public int getSlotId(int clickSlotId) {
		return clickSlotId / 2;
	}

	public void removeItem(int clickSlotId, int amount) {
		int slot = getSlotId(clickSlotId);
		Item item = pcInv.get(slot);
		if (item == null)
			return;
		Item[] itemsBefore = pcInv.getItemsCopy();
		int maxAmount = pcInv.getNumberOf(item);
		if (amount < maxAmount)
			item = new Item(item.getId(), amount);
		else
			item = new Item(item.getId(), maxAmount);
		pcInv.remove(slot, item);
		player.getInventory().addItemMoneyPouch(item);
		refreshItems(itemsBefore);
	}

	public void addItem(int slot, int amount) {
		Item item = player.getInventory().getItem(slot);
		if (item == null)
			return;
		if (!ItemConstants.isTradeable(item)) {
			player.getPackets().sendGameMessage("That item isn't tradeable.");
			return;
		}
		Item[] itemsBefore = pcInv.getItemsCopy();
		int maxAmount = player.getInventory().getItems().getNumberOf(item);
		if (amount < maxAmount)
			item = new Item(item.getId(), amount);
		else
			item = new Item(item.getId(), maxAmount);
		pcInv.add(item);
		player.getInventory().deleteItem(slot, item);
		refreshItems(itemsBefore);
	}

	public void refreshItems(Item[] itemsBefore) {
		int totalPrice = 0;
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			Item item = pcInv.getItems()[index];
			if (item != null)
				totalPrice += GrandExchange.getPrice(item.getId())
						* item.getAmount();
			if (itemsBefore[index] != item) {
				changedSlots[count++] = index;
				player.getPackets()
						.sendCSVarInteger(
								700 + index,
								item == null ? 0 : GrandExchange.getPrice(item
										.getId()));
			}

		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
		player.getPackets().sendCSVarInteger(728, totalPrice);
	}

	public void refresh(int... slots) {
		player.getPackets().sendUpdateItems(90, pcInv, slots);
	}

	public void sendOptions() {
		player.getPackets().sendUnlockIComponentOptionSlots(206, 5, 0, 54, 0,
				1, 2, 3, 4, 5, 6);
		player.getPackets().sendUnlockIComponentOptionSlots(207, 0, 0, 27, 0,
				1, 2, 3, 4, 5);
		player.getPackets().sendInterSetItemsOptionsScript(207, 0, 93, 4, 7,
				"Add", "Add-5", "Add-10", "Add-All", "Add-X", "Examine");
	}

	public void sendInterItems() {
		player.getPackets().sendItems(90, pcInv);
	}

	public void addAllInventory() {
		Item[] inv = player.getInventory().getItems().getItems();
		for (int slot = 0; slot < inv.length; slot++) {
			Item item = inv[slot];
			if (item == null || !ItemConstants.isTradeable(item))
				continue;
			addItem(slot, item.getAmount());
		}
	}
}
