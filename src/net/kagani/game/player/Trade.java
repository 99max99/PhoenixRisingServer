package net.kagani.game.player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.item.Item;
import net.kagani.game.item.ItemsContainer;
import net.kagani.game.player.content.ItemConstants;
import net.kagani.game.player.content.grandExchange.GrandExchange;
import net.kagani.utils.ItemExamines;
import net.kagani.utils.Logger;

public class Trade {

	private Player player, target;
	private ItemsContainer<Item> items;
	private boolean tradeModified;
	private boolean accepted;

	public Trade(Player player) {
		this.player = player; // player reference
		items = new ItemsContainer<Item>(28, false);
	}

	/*
	 * called to both players
	 */
	public void openTrade(final Player target) {
		synchronized (this) {
			player.stopAll();
			Logger.globalLog(player.getUsername(), player.getSession().getIP(),
					new String(" began trading with " + target.getUsername()
							+ "."));
			this.target = target;
			player.getPackets().sendCSVarString(2504, target.getDisplayName());
			sendInterItems();
			sendOptions();
			sendTradeModified();
			refreshFreeInventorySlots();
			refreshTradeWealth();
			refreshStageMessage(true);
			player.getInterfaceManager().sendCentralInterface(335);
			player.getInterfaceManager().sendInventoryInterface(336);
			player.setCloseInterfacesEvent(new Runnable() {
				@Override
				public void run() {
					closeTrade(CloseTradeStage.CANCEL);
				}
			});
		}

	}

	public void removeItem(final int slot, int amount) {
		synchronized (this) {
			if (!isTrading())
				return;

			Item item = items.get(slot);
			if (item == null)
				return;
			Item[] itemsBefore = items.getItemsCopy();
			int maxAmount = items.getNumberOf(item);
			if (amount < maxAmount)
				item = new Item(item.getId(), amount);
			else
				item = new Item(item.getId(), maxAmount);
			items.remove(slot, item);
			player.getInventory().addItemMoneyPouch(item);
			refreshItems(itemsBefore);
			cancelAccepted();
			setTradeModified(true);
		}

	}

	public void sendFlash(int slot) {
		player.getPackets().sendInterFlashScript(335, 25, 4, 7, slot);
		target.getPackets().sendInterFlashScript(335, 28, 4, 7, slot);
	}

	public void cancelAccepted() {
		boolean canceled = false;
		if (accepted) {
			accepted = false;
			canceled = true;
		}
		if (target.getTrade().accepted) {
			target.getTrade().accepted = false;
			canceled = true;
		}
		if (canceled)
			refreshBothStageMessage(canceled);
	}

	public void addItem(Item item) {
		synchronized (this) {
			if (item == null || !isTrading())
				return;
			if (!ItemConstants.isTradeable(item) && player.getRights() < 2) {
				player.getPackets().sendGameMessage(
						"That item isn't tradeable.");
				return;
			}
			Item[] itemsBefore = items.getItemsCopy();
			items.add(item);
			refreshItems(itemsBefore);
			cancelAccepted();
		}
	}

	public void addItem(int slot, int amount) {
		synchronized (this) {
			if (!isTrading())
				return;

			Item item = player.getInventory().getItem(slot);
			if (item == null)
				return;
			if (!ItemConstants.isTradeable(item) && player.getRights() < 2) {
				player.getPackets().sendGameMessage(
						"That item isn't tradeable.");
				return;
			}
			Item[] itemsBefore = items.getItemsCopy();
			int maxAmount = player.getInventory().getItems().getNumberOf(item);
			if (amount < maxAmount)
				item = new Item(item.getId(), amount);
			else
				item = new Item(item.getId(), maxAmount);
			items.add(item);
			player.getInventory().deleteItem(slot, item);
			refreshItems(itemsBefore);
			cancelAccepted();
		}

	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			Item item = items.getItems()[index];
			if (itemsBefore[index] != item) {
				if (itemsBefore[index] != null
						&& (item == null
								|| item.getId() != itemsBefore[index].getId() || item
								.getAmount() < itemsBefore[index].getAmount()))
					sendFlash(index);
				changedSlots[count++] = index;
			}
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
		refreshFreeInventorySlots();
		refreshTradeWealth();
	}

	public void sendOptions() {
		player.getPackets().sendInterSetItemsOptionsScript(336, 0, 93, 4, 7,
				"Offer", "Offer-5", "Offer-10", "Offer-All", "Offer-X",
				"Value<col=FF9040>", "Lend");
		player.getPackets().sendInterSetItemsOptionsScript(335, 24, 90, 4, 7,
				"Remove", "Remove-5", "Remove-10", "Remove-All", "Remove-X",
				"Value");
		player.getPackets().sendInterSetItemsOptionsScript(335, 27, 90, true,
				4, 7, "Value");
		player.getPackets().sendIComponentSettings(335, 24, 0, 27, 1150);
		player.getPackets().sendIComponentSettings(335, 27, 0, 27, 1026);
		player.getPackets().sendIComponentSettings(336, 0, 0, 27, 1278);
		player.getPackets().sendIComponentSettings(335, 55, -1, -1, 1026);
		player.getPackets().sendIComponentSettings(335, 56, -1, -1, 1030);
		player.getPackets().sendIComponentSettings(335, 51, -1, -1, 1024);
	}

	public boolean isTrading() {
		return target != null;
	}

	public void setTradeModified(boolean modified) {
		if (modified == tradeModified)
			return;
		tradeModified = modified;
		sendTradeModified();
	}

	public void sendInterItems() {
		player.getPackets().sendItems(90, items);
		target.getPackets().sendItems(90, true, items);
	}

	public void refresh(int... slots) {
		player.getPackets().sendUpdateItems(90, items, slots);
		target.getPackets().sendUpdateItems(90, true, items.getItems(), slots);
	}

	public void accept(boolean firstStage) {
		synchronized (this) {
			if (!isTrading())
				return;
			if (target.getTrade().accepted) {
				if (firstStage) {
					if (nextStage())
						target.getTrade().nextStage();
				} else {
					player.setCloseInterfacesEvent(null);
					player.closeInterfaces();
					closeTrade(CloseTradeStage.DONE);
				}
				return;
			}
			accepted = true;
			refreshBothStageMessage(firstStage);
		}
	}

	public void sendValue(int slot, boolean traders) {
		if (!isTrading())
			return;
		Item item = traders ? target.getTrade().items.get(slot) : items
				.get(slot);
		if (item == null)
			return;
		if (!ItemConstants.isTradeable(item) && player.getRights() < 2) {
			player.getPackets().sendGameMessage("That item isn't tradeable.");
			return;
		}
		int price = GrandExchange.getPrice(item.getId());
		player.getPackets().sendGameMessage(
				item.getDefinitions().getName() + ": market price is " + price
						+ " coins.");
	}

	public void sendValue(int slot) {
		Item item = player.getInventory().getItem(slot);
		if (item == null)
			return;
		if (!ItemConstants.isTradeable(item) && player.getRights() < 2) {
			player.getPackets().sendGameMessage("That item isn't tradeable.");
			return;
		}
		int price = GrandExchange.getPrice(item.getId());
		player.getPackets().sendGameMessage(
				item.getDefinitions().getName() + ": market price is " + price
						+ " coins.");
	}

	public void sendExamine(int slot, boolean traders) {
		if (!isTrading())
			return;
		Item item = traders ? target.getTrade().items.get(slot) : items
				.get(slot);
		if (item == null)
			return;
		player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
	}

	public boolean nextStage() {
		if (!isTrading())
			return false;
		if (player.getInventory().getItems().getUsedSlots()
				+ target.getTrade().items.getUsedSlots() > 28) {
			player.setCloseInterfacesEvent(null);
			player.closeInterfaces();
			closeTrade(CloseTradeStage.NO_SPACE);
			return false;
		}
		accepted = false;
		player.getInterfaceManager().sendCentralInterface(334);
		player.getInterfaceManager().removeInventoryInterface();
		player.getPackets().sendHideIComponent(334, 22,
				!(tradeModified || target.getTrade().tradeModified));
		refreshBothStageMessage(false);
		return true;
	}

	public void refreshBothStageMessage(boolean firstStage) {
		synchronized (target.getTrade()) { // if deadlock happen within a few
			// days, gottam ake sure to rechec
			// this
			refreshStageMessage(firstStage);
			target.getTrade().refreshStageMessage(firstStage);
		}
	}

	public void refreshStageMessage(boolean firstStage) {
		player.getPackets().sendIComponentText(firstStage ? 335 : 334,
				firstStage ? 31 : 15, getAcceptMessage(firstStage));
	}

	public String getAcceptMessage(boolean firstStage) {
		if (accepted)
			return "Waiting for other player...";
		if (target.getTrade().accepted)
			return "Other player has accepted.";
		return firstStage ? "" : "Are you sure you want to make this trade?";
	}

	public void sendTradeModified() {
		player.getVarsManager().sendVar(1826, tradeModified ? 1 : 0);
		target.getVarsManager().sendVar(1827, tradeModified ? 1 : 0);
	}

	public void refreshTradeWealth() {
		int wealth = getTradeWealth();
		player.getPackets().sendCSVarInteger(729, wealth);
		target.getPackets().sendCSVarInteger(697, wealth);
	}

	public void refreshFreeInventorySlots() {
		player.getPackets().sendCSVarString(2519, target.getDisplayName());
		int freeSlots = player.getInventory().getFreeSlots();
		target.getPackets().sendIComponentText(
				335,
				22,
				"has " + (freeSlots == 0 ? "no" : freeSlots) + " free"
						+ "<br>inventory slots");
	}

	public int getTradeWealth() {
		int wealth = 0;
		for (Item item : items.getItems()) {
			if (item == null)
				continue;
			wealth += GrandExchange.getPrice(item.getId()) * item.getAmount();
		}
		return wealth;
	}

	private static enum CloseTradeStage {
		CANCEL, NO_SPACE, DONE
	}

	public void reset() {
		target = null;
		tradeModified = false;
		accepted = false;
	}

	public void closeTrade(CloseTradeStage stage) {
		synchronized (this) {
			if (isTrading() && target.getTrade().isTrading()) {
				Player oldTarget = target;
				reset();
				oldTarget.getTrade().reset();
				oldTarget.setCloseInterfacesEvent(null);
				oldTarget.closeInterfaces();
				if (CloseTradeStage.DONE != stage) {
					for (Item item : items.getItems()) {
						if (item == null)
							continue;
						player.getInventory().addItemMoneyPouch(item);
					}
					for (Item item : oldTarget.getTrade().items.getItems()) {
						if (item == null)
							continue;
						oldTarget.getInventory().addItemMoneyPouch(item);
					}
					oldTarget.getTrade().items.clear();
					items.clear();
				} else {
					CopyOnWriteArrayList<Item[]> containedItems = new CopyOnWriteArrayList<Item[]>();
					Logger.globalLog(
							player.getUsername(),
							player.getSession().getIP(),
							new String(" completed the trade with "
									+ oldTarget.getUsername()
									+ " items are as follows: "
									+ Arrays.toString(items.getShiftedItem())));
					Logger.globalLog(
							oldTarget.getUsername(),
							oldTarget.getSession().getIP(),
							new String(
									" completed the trade with "
											+ player.getUsername()
											+ " items are as follows "
											+ Arrays.toString(oldTarget
													.getTrade().items
													.getShiftedItem()) + "."));
					containedItems.add(oldTarget.getTrade().items
							.getShiftedItem());
					for (Item item : items.getItems()) {
						if (item == null)
							continue;
						oldTarget.getInventory().addItemMoneyPouch(item);
					}
					for (Item item : oldTarget.getTrade().items.getItems()) {
						if (item == null)
							continue;
						player.getInventory().addItemMoneyPouch(item);
					}
					log(player, oldTarget, containedItems);
					oldTarget.getTrade().items.clear();
					items.clear();
					player.getPackets().sendGameMessage(
							"<col=00FF00>Accepted trade.");
					oldTarget.getPackets().sendGameMessage(
							"<col=00FF00>Accepted trade.");
				}
				Logger.globalLog(player.getUsername(), player.getSession()
						.getIP(),
						new String(" trade between " + player.getUsername()
								+ " and " + oldTarget.getUsername()
								+ " has been finished."));
				if (CloseTradeStage.CANCEL == stage)
					oldTarget.getPackets().sendGameMessage(
							"<col=ff0000>Other player declined trade!");
				else if (CloseTradeStage.NO_SPACE == stage) {
					player.getPackets()
							.sendGameMessage(
									"You don't have enough space in your inventory for this trade.");
					oldTarget
							.getPackets()
							.sendGameMessage(
									"Other player doesn't have enough space in their inventory for this trade.");
				}
			}
		}
	}

	public static void log(Player player, Player oldTarget,
			CopyOnWriteArrayList<Item[]> containedItems) {
		// TODO Auto-generated method stub
		try {
			if (containedItems == null) {
				return;
			}
			final String FILE_PATH = "data/logs/trade/";
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH
					+ oldTarget.getUsername() + ".txt", true));
			writer.write("[Trade session started]");
			writer.newLine();
			writer.write("Trader Information: Username: "
					+ oldTarget.getUsername() + ". IP "
					+ oldTarget.getSession().getIP() + ". Location: "
					+ oldTarget.getX() + ", " + oldTarget.getY() + ", "
					+ oldTarget.getPlane() + ".");
			writer.newLine();
			writer.write("Player Information: Username: "
					+ player.getUsername() + ". IP: " + player.getLastGameIp()
					+ ". Location: " + player.getX() + ", " + player.getY()
					+ ", " + player.getPlane() + ".");
			writer.newLine();
			writer.write("Time: [" + dateFormat.format(cal.getTime()) + "]");
			for (Item[] item : containedItems) {
				if (item == null) {
					continue;
				}
				ItemDefinitions defs = ItemDefinitions.getItemDefinitions(item
						.length);
				String name = defs == null ? "" : defs.getName().toLowerCase();
				writer.newLine();
				writer.write(oldTarget.getUsername() + " Gave: " + name
						+ ", amount: " + item.length);
			}
			writer.newLine();
			writer.write("[Trade session ended]");
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(player, e);
		}
	}
}