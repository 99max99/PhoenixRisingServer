package net.kagani.game.player;

import java.io.Serializable;
import java.util.List;

import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.item.ItemsContainer;
import net.kagani.game.player.content.ItemConstants;
import net.kagani.game.player.content.grandExchange.GrandExchange;
import net.kagani.utils.ItemExamines;
import net.kagani.utils.ItemWeights;
import net.kagani.utils.Utils;

public final class Inventory implements Serializable {

	private static final long serialVersionUID = 8842800123753277093L;

	private ItemsContainer<Item> items;

	private transient Player player;
	private transient double inventoryWeight;

	public static final int INVENTORY_INTERFACE = 1473, INVENTORY_INTERFACE_2 = 1474;

	public Inventory() {
		items = new ItemsContainer<Item>(28, false);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void init() {
		refresh();
	}

	public void unlockInventory(boolean menu) {
		for (int i = 0; i < Utils
				.getInterfaceDefinitionsComponentsSize(menu ? INVENTORY_INTERFACE_2 : INVENTORY_INTERFACE); i++) {
			player.getPackets().sendIComponentSettings(menu ? INVENTORY_INTERFACE_2 : INVENTORY_INTERFACE,
					menu ? 15 : 34, -1, -1, 2097152);
			player.getPackets().sendIComponentSettings(menu ? INVENTORY_INTERFACE_2 : INVENTORY_INTERFACE,
					menu ? 15 : 34, 0, 27, 15302030);
			// player.getPackets().sendIComponentSettings(menu ?
			// INVENTORY_INTERFACE_2 : INVENTORY_INTERFACE, menu ? 15 : 34, 0,
			// 27, 1536);
		}
	}

	public void reset() {
		items.reset();
		init(); // as all slots reseted better just send all again
	}

	public void refresh(int... slots) {
		player.getPackets().sendUpdateItems(93, items, slots);
		refreshConfigs(false);
	}

	public boolean addCoins(int amount) {
		if (amount < 0)
			return false;
		final Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(new Item(995, amount))) {
			items.add(new Item(995, items.getFreeSlots()));
			player.getPackets().sendGameMessage("Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public void refreshConfigs(boolean init) {
		double w = 0;
		for (Item item : items.getItems()) {
			if (item == null)
				continue;
			w += ItemWeights.getWeight(item, false);
		}
		inventoryWeight = w;
		player.getPackets().refreshWeight();
	}

	public boolean addItemDrop(int itemId, int amount, WorldTile tile) {
		if (itemId < 0 || amount < 0 || !Utils.itemExists(itemId)
				|| !player.getControlerManager().canAddInventoryItem(itemId, amount))
			return false;
		if (itemId == 995)
			return player.getMoneyPouch().sendDynamicInteraction(amount, false);
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(new Item(itemId, amount)))
			World.addGroundItem(new Item(itemId, amount), tile, player, true, 180);
		else
			refreshItems(itemsBefore);
		return true;
	}

	public boolean addItemDrop(int itemId, int amount) {
		return addItemDrop(itemId, amount, new WorldTile(player));
	}

	public boolean addItemDrop(Item item) {
		return addItemDrop(item.getId(), item.getAmount(), new WorldTile(player));
	}

	public boolean addItem(int itemId, int amount) {
		if (itemId < 0 || amount < 0 || !Utils.itemExists(itemId)
				|| !player.getControlerManager().canAddInventoryItem(itemId, amount))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(new Item(itemId, amount))) {
			items.add(new Item(itemId, items.getFreeSlots()));
			player.getPackets().sendGameMessage("Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public boolean addItemMoneyPouch(Item item) {
		if (item.getId() == 995)
			return player.getMoneyPouch().sendDynamicInteraction(item.getAmount(), false);
		return addItem(item);
	}

	public boolean removeItemMoneyPouch(Item item) {
		if (item.getId() == 995)
			return player.getMoneyPouch().sendDynamicInteraction(item.getAmount(), true);
		return removeItems(item);
	}

	public boolean containsItemToolBelt(int id) {
		return containsOneItem(id) || player.getToolbelt().containsItem(id);
	}

	public boolean containsItemToolBelt(int id, int amount) {
		return containsItem(id, amount) || player.getToolbelt().containsItem(id);
	}

	public boolean addItem(Item item) {
		if (item == null || item.getId() < 0 || item.getAmount() < 0 || !Utils.itemExists(item.getId())
				|| !player.getControlerManager().canAddInventoryItem(item.getId(), item.getAmount()))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(item)) {
			items.add(new Item(item.getId(), items.getFreeSlots()));
			player.getPackets().sendGameMessage("Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public void deleteItem(int slot, Item item) {
		if (!player.getControlerManager().canDeleteInventoryItem(item.getId(), item.getAmount()))
			return;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(slot, item);
		refreshItems(itemsBefore);
	}

	public boolean removeItems(Item... list) {
		for (Item item : list) {
			if (item == null)
				continue;
			deleteItem(item);
		}
		return true;
	}

	public boolean removeItems(List<Item> list) {
		for (Item item : list) {
			if (item == null)
				continue;
			deleteItem(item);
		}
		return true;
	}

	public void deleteItem(int itemId, int amount) {
		if (!player.getControlerManager().canDeleteInventoryItem(itemId, amount))
			return;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(new Item(itemId, amount));
		refreshItems(itemsBefore);
	}

	public void deleteItem(Item item) {
		if (!player.getControlerManager().canDeleteInventoryItem(item.getId(), item.getAmount()))
			return;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(item);
		refreshItems(itemsBefore);
	}

	/*
	 * No refresh needed its client to who does it :p
	 */
	public void switchItem(int fromSlot, int toSlot) {
		Item[] itemsBefore = items.getItemsCopy();
		Item fromItem = items.get(fromSlot);
		Item toItem = items.get(toSlot);
		items.set(fromSlot, toItem);
		items.set(toSlot, fromItem);
		refreshItems(itemsBefore);
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			if (itemsBefore[index] != items.getItems()[index])
				changedSlots[count++] = index;
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
	}

	public ItemsContainer<Item> getItems() {
		return items;
	}

	public boolean hasFreeSlots() {
		return items.getFreeSlot() != -1;
	}

	public int getFreeSlots() {
		return items.getFreeSlots();
	}

	public int getFreeSlots(Item[] item) {
		return items.getFreeSlots(item);
	}

	public int getAmountOf(int itemId) {
		return items.getNumberOf(itemId);
	}

	public Item getItem(int slot) {
		return items.get(slot);
	}

	public int getItemsContainerSize() {
		return items.getSize();
	}

	public boolean containsItems(List<Item> list) {
		for (Item item : list)
			if (!items.contains(item))
				return false;
		return true;
	}

	public boolean containsItems(Item... item) {
		for (int i = 0; i < item.length; i++)
			if (!items.contains(item[i]))
				return false;
		return true;
	}

	public boolean containsItems(int[] itemIds, int[] amounts) {
		int size = itemIds.length > amounts.length ? amounts.length : itemIds.length;
		for (int i = 0; i < size; i++)
			if (!items.contains(new Item(itemIds[i], amounts[i])))
				return false;
		return true;
	}

	public boolean containsItem(int itemId, int amount) {
		return items.contains(new Item(itemId, amount));
	}

	public int getCoinsAmount() {
		int coins = items.getNumberOf(995) + player.getMoneyPouch().getCoinsAmount();
		return coins < 0 ? Integer.MAX_VALUE : coins;
	}

	public boolean containsOneItem(int... itemIds) {
		for (int itemId : itemIds) {
			if (items.containsOne(new Item(itemId, 1)))
				return true;
		}
		return false;
	}

	public void sendExamine(int slotId) {
		if (slotId >= getItemsContainerSize())
			return;
		Item item = items.get(slotId);
		if (item == null)
			return;

		player.getPackets()
				.sendInventoryMessage(0, slotId,
						ItemExamines.getExamine(item) + (ItemConstants.isTradeable(item)
								? "<br>GE guide price: " + Utils.format(GrandExchange.getPrice(item.getId())) + " gp"
								: ""));
	}

	public void refresh() {
		player.getPackets().sendItems(93, items);
		refreshConfigs(true);
	}

	public double getInventoryWeight() {
		return inventoryWeight;
	}

	public void replaceItem(int id, int amount, int slot) {
		Item item = items.get(slot);
		if (item == null)
			return;
		if (id == -1) {
			items.set(slot, null);
		} else {
			item.setId(id);
			item.setAmount(amount);
		}
		refresh(slot);
	}

	public boolean hasCoinAmount(int amount) {
		if (containsItem(995, amount))
			return true;
		else if (player.getMoneyPouch().contains(amount))
			return true;
		return false;
	}

	public void deleteCoinAmount(int amount) {
		if (containsItem(995, amount)) {
			deleteItem(995, amount);
			return;
		} else if (player.getMoneyPouch().contains(amount)) {
			player.getMoneyPouch().setAmount(amount, true);
			return;
		}
		player.getPackets().sendGameMessage("Not enough coins.");
	}

	public boolean containsItem(Item item) {
		if (!items.contains(item))
			return false;
		return true;
	}
}
