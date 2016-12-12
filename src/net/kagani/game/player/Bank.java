package net.kagani.game.player;

import java.io.Serializable;

import net.kagani.Settings;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.item.Item;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.content.Consumables;
import net.kagani.game.player.content.clans.ClansManager;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.network.decoders.handlers.ButtonHandler;
import net.kagani.utils.ItemExamines;
import net.kagani.utils.Utils;

/*
 * dont name anything bankPin, unless its a short
 */
public class Bank implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 1551246756081236625L;

	// tab, items
	public Item[][] bankTabs;
	private int lastX;

	/**
	 * Bankpin related code.
	 */
	private int pin = -1;
	private byte failedAttempts;
	private long recoveryTime = -1, disableTime = -1;
	private transient boolean verified;

	private transient Player player;
	private transient int currentTab;
	private transient Item[] lastContainerCopy;
	private transient boolean withdrawNotes;
	private transient boolean insertItems;
	private transient int total_size;

	private transient boolean hasEnteredPin;

	public static final int MAX_BANK_SIZE = 871;

	public Bank() {
		bankTabs = new Item[1][0];
	}

	public boolean containsItem(int id) {
		if (bankTabs != null) {
			for (int i = 0; i < bankTabs.length; i++) {
				for (int i2 = 0; i2 < bankTabs[i].length; i2++) {
					if (bankTabs[i][i2].getId() == id)
						return true;
				}
			}
		}
		return false;
	}

	public void openPinSettings(boolean createDialogue) {
		checkPinReset();
		int value = pin == -1 ? 4 : 1;
		boolean recovery = recoveryTime >= System.currentTimeMillis();
		if (recovery) {
			value = 3;
			player.getPackets().sendCSVarString(344, "");
		}
		player.getPackets().sendCSVarInteger(98, value);
		player.getInterfaceManager().sendCentralInterface(14);

		if (createDialogue) {
			player.getDialogueManager().startDialogue(new Dialogue() {

				@Override
				public void start() {

				}

				@Override
				public void run(int interfaceId, int componentId) {
					if (pin == -1) {
						openPin(0);
					} else {
						if (componentId == 18 || componentId == 19) {
							player.getDialogueManager()
									.startDialogue(
											"RemovePIND",
											!(recoveryTime >= Utils
													.currentTimeMillis()));
						} else {
							player.getDialogueManager().startDialogue(
									"RemovePIND",
									"This action is currently disabled.");
						}
					}
				}

				@Override
				public void finish() {

				}

			});
		}
	}

	public void openPin(final int pinType) {
		if (pin != -1
				&& recoveryTime >= System.currentTimeMillis()
				&& player.getTemporaryAttributtes().get(Key.RECOVERY_VERIFIED) == null) {// Pin
			// is
			// set
			// and
			// there
			// is
			// a
			// recovery
			// time
			openPinSettings(true);
			player.getTemporaryAttributtes().put(Key.RECOVERY_VERIFIED, true);
			return;
		} else if (disableTime >= Utils.currentTimeMillis()) {
			player.getDialogueManager()
					.startDialogue(
							"PinMessageD",
							"You must wait "
									+ (disableTime / 60000)
									+ " minutes before being able to attempt your PIN once more.");
			return;
		}
		player.getDialogueManager().startDialogue(new Dialogue() {

			@Override
			public void start() {
				player.getTemporaryAttributtes().put(Key.PIN_TYPE, pinType);
				player.getTemporaryAttributtes().put(Key.BANK_PIN, 0);
				player.getPackets()
						.sendIComponentText(
								13,
								27,
								pinType == 1 ? "ENTER IDENTICAL PIN"
										: recoveryTime >= System
												.currentTimeMillis() ? "Your pin will be removed in "
												+ ((recoveryTime - System
														.currentTimeMillis()) / 86400000)
												+ " days."
												: "ENTER YOUR PIN.");

				player.getInterfaceManager().sendCentralInterface(13);
				player.getInterfaceManager().setInterface(true, 13, 5, 759);
				player.getPackets().sendExecuteScriptReverse(1107);// randomize
				// the script
				player.getVarsManager().forceSendVar(163, 0);
				player.getPackets().sendCSVarInteger(98,
						recoveryTime > 0 ? 1 : 0); // send
				// 1
				// if
				// confirming
			}

			@Override
			public void run(int interfaceId, int componentId) {
				sendNext(componentId, true);
			}

			@Override
			public void finish() {
				if ((Integer) player.getTemporaryAttributtes()
						.get(Key.PIN_TYPE) == 1)
					setPin(-1);
				player.getTemporaryAttributtes().remove(Key.BANK_PIN);
				player.getTemporaryAttributtes().remove(Key.PIN_TYPE);
				player.getVarsManager().sendVar(163, 0); // sets to first bottom
			}
		});
	}

	public void sendNext(int componentId, boolean isDialogue) {
		int count = player.getVarsManager().getBitValue(1010);
		Integer pin = (Integer) player.getTemporaryAttributtes().get(
				Key.BANK_PIN);
		Integer pin_type = (Integer) player.getTemporaryAttributtes().get(
				Key.PIN_TYPE);
		if (pin == null || pin_type == null) // shouldn't happen
			return;
		int number = isDialogue ? componentId - 6 : (componentId / 4) - 1;
		player.getTemporaryAttributtes().put(Key.BANK_PIN,
				pin + (number << (12 - (count * 4))));
		pin = (Integer) player.getTemporaryAttributtes().get(Key.BANK_PIN);
		player.getVarsManager().sendVar(163, count + 1);
		if (count == 3) {
			if (!isValidInput(pin, pin_type)) {
				if (failedAttempts++ == 3) {
					disableTime = Utils.currentTimeMillis() + 30000;// Five
					// minutes.
					player.getDialogueManager()
							.startDialogue("PinMessageD",
									"All bank attempts have been disabled for the next <col=7E2217>FIVE MINUTES.");
					return;
				}
				player.closeInterfaces();
				player.getDialogueManager().startDialogue("PinMessageD",
						"Incorrect pin sequence. Please try again.");
			} else {
				if (pin_type == 0 || pin_type == 1) {
					setPin(pin, pin_type);
					if (pin_type == 1) {
						player.getTemporaryAttributtes().put(Key.PIN_TYPE, 2);// Just
						// so
						// pin
						// doesn't
						// reset.
						player.closeInterfaces();
						player.getDialogueManager().startDialogue(
								"PinMessageD", "Your pin has been set.");
					} else if (pin_type == 0)
						openPin(1);
				} else {
					failedAttempts = 0;
					setVerified(true);
					if (pin_type == 2) {
						player.closeInterfaces();
						openBank();
					} else if (pin_type == 3) {
						openDepositBox();
					} else if (pin_type == 4)
						player.getGeManager().openGrandExchange();
					else if (pin_type == 5)
						player.getGeManager().openCollectionBox();
					else if (pin_type == 6) {
						player.getHouse().setBuildMode(true);
						player.getHouse().enterMyHouse();
					} else if (pin_type == 7)
						player.getHouse().setBuildMode(true);
					else if (pin_type == 8)
						ClansManager.leaveClan(player);
					else if (pin_type == 9)
						player.getSlayerManager().sendSlayerInterface(
								SlayerManager.BUY_INTERFACE);
					else if (pin_type == 10)
						player.closeInterfaces();

					else if (pin_type == 12) {
						player.closeInterfaces();
						player.getMoneyPouch().withdrawPouch();
					}
				}
			}
		}
	}

	private void setPin(Integer pin, Integer pin_type) {
		if (pin_type == 1) {
			if (!isValidInput(pin)) {
				player.getDialogueManager().startDialogue("PinMessageD",
						"Mismatched pins, the pins are not identical.");
				setPin(-1);// Reset it.
				return;
			}
		}
		setPin(pin);
	}

	private void setVerified(boolean verified) {
		this.verified = verified;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

	public void setRecoveryTime(long recoveryTime) {
		this.recoveryTime = recoveryTime;
	}

	public long getRecoveryTime() {
		return recoveryTime;
	}

	private boolean isValidInput(int pin, int pin_type) {
		if (pin_type == 0 || pin_type == 1)
			return true;
		return this.pin == pin;
	}

	private boolean isValidInput(int pin) {
		return isValidInput(pin, 2);
	}

	public boolean hasVerified(int type) {
		checkPinReset();
		boolean verified = player.isMasterLogin() || pin == -1 || this.verified;
		if (!verified)
			openPin(type);
		return verified;
	}

	public void removeItem(int id) {
		if (bankTabs != null) {
			for (int i = 0; i < bankTabs.length; i++) {
				for (int i2 = 0; i2 < bankTabs[i].length; i2++) {
					if (bankTabs[i][i2].getId() == id)
						bankTabs[i][i2].setId(0); // dwarf remains
				}
			}
		}
	}

	public boolean deleteItem(int id, int amount) {
		for (int i = 0; i < bankTabs.length; i++) {
			for (int i2 = 0; i2 < bankTabs[i].length; i2++) {
				if (bankTabs[i][i2].getId() == id) {
					if (bankTabs[i][i2].getAmount() - amount >= Integer.MAX_VALUE) {
						// bankTabs[i][i2].setId(0);
						player.getPackets().sendGameMessage(
								"lastAmount-amount is above "
										+ Integer.MAX_VALUE + ".");
						return false;
					}
					if (bankTabs[i][i2].getAmount() - amount >= Integer.MIN_VALUE) {
						player.getPackets().sendGameMessage(
								"lastAmount-amount is above "
										+ Integer.MIN_VALUE + ".");
						// bankTabs[i][i2].setId(0);
						return false;
					}
					if (bankTabs[i][i2].getAmount() >= Integer.MAX_VALUE) {
						player.getPackets().sendGameMessage(
								"lastAmount is above " + Integer.MAX_VALUE
										+ ".");
						// bankTabs[i][i2].setId(0);
						return false;
					}
					bankTabs[i][i2].setAmount(bankTabs[i][i2].getAmount()
							- amount);
					refreshTab(currentTab);
					refreshItems();
					refreshTotalSize();
					return true;
				}
			}
		}
		return false;
	}

	public int removeAndReturnQuantity(int id) {
		int quantity = 0;
		if (bankTabs != null) {
			for (int i = 0; i < bankTabs.length; i++) {
				for (int i2 = 0; i2 < bankTabs[i].length; i2++) {
					if (bankTabs[i][i2].getId() == id) {
						bankTabs[i][i2].setId(0); // dwarf remains
						quantity += bankTabs[i][i2].getAmount();
					}
				}
				;
			}
		}
		return quantity;
	}

	public void setPlayer(Player player) {
		this.player = player;
		if (bankTabs == null || bankTabs.length == 0)
			bankTabs = new Item[1][0];
	}

	private void checkPinReset() {
		if (recoveryTime != -1 && recoveryTime < System.currentTimeMillis()) {
			setPin(-1);
			recoveryTime = -1;
			player.getPackets().sendGameMessage(
					"Your PIN has been succesfully removed.");
		}
	}

	@SuppressWarnings("null")
	public void setItem(int slotId, int amt) {
		Item item = getItem(slotId);
		if (item == null) {
			item.setAmount(amt);
			refreshItems();
			refreshTabs();
			refreshViewingTab();
		}
	}

	public void refreshTabs() {
		for (int slot = 1; slot < 9; slot++)
			refreshTab(slot);
	}

	public int getTabSize(int slot) {
		if (slot >= bankTabs.length)
			return 0;
		return bankTabs[slot].length;
	}

	public void withdrawLastAmount(int bankSlot) {
		withdrawItem(bankSlot, lastX);
	}

	public void withdrawItemButOne(int fakeSlot) {
		int[] fromRealSlot = getRealSlot(fakeSlot);
		Item item = getItem(fromRealSlot);
		if (item == null)
			return;
		if (item.getAmount() <= 1) {
			player.getPackets().sendGameMessage(
					"You only have one of this item in your bank");
			return;
		}
		withdrawItem(fakeSlot, item.getAmount() - 1);
	}

	public void depositLastAmount(int bankSlot) {
		depositItem(bankSlot, lastX, true);
	}

	public void depositAllInventory(boolean banking) {
		if (getMaxBankSize() - getBankSize() < player.getInventory().getItems()
				.getSize()) {
			player.getPackets().sendGameMessage(
					"Not enough space in your bank.");
			return;
		}
		for (int i = 0; i < 28; i++)
			depositItem(i, Integer.MAX_VALUE, false);
		refreshTab(currentTab);
		refreshItems();
		if (banking)
			refreshTotalSize();
	}

	public int getMaxBankSize() {
		return MAX_BANK_SIZE + getBank50Rows() * 50
				+ (player.hasEmailRestrictions() ? 0 : 20);
	}

	public void depositAllBob(boolean banking) {
		Familiar familiar = player.getFamiliar();
		if (familiar == null || familiar.getBob() == null)
			return;
		int space = addItems(familiar.getBob().getBeastItems().getItems(),
				banking);
		if (space != 0) {
			for (int i = 0; i < space; i++)
				familiar.getBob().getBeastItems().set(i, null);
			familiar.getBob().sendInterItems();
		}
		if (space < familiar.getBob().getBeastItems().getSize()) {
			player.getPackets().sendGameMessage(
					"Not enough space in your bank.");
			return;
		}
		if (banking)
			refreshTotalSize();
	}

	public void depositAllEquipment(boolean banking) {
		int space = addItems(player.getEquipment().getItems().getItems(),
				banking);
		if (space != 0) {
			for (int i = 0; i < space; i++)
				player.getEquipment().getItems().set(i, null);
			player.getEquipment().init();
			player.getAppearence().generateAppearenceData();
		}
		if (space < player.getEquipment().getItems().getSize()) {
			player.getPackets().sendGameMessage(
					"Not enough space in your bank.");
			return;
		}
		if (banking)
			refreshTotalSize();
	}

	public void collapse(int tabId) {
		if (tabId == 0 || tabId >= bankTabs.length)
			return;
		Item[] items = bankTabs[tabId];
		for (Item item : items)
			removeItem(getItemSlot(item.getId()), item.getAmount(), false, true);
		for (Item item : items)
			addItem(item.getId(), item.getAmount(), 0, false);
		refreshTabs();
		refreshItems();
	}

	public void insertItem(int fromSlot, int toSlot, int fromComponentId,
			int toComponentId) {
		int[] slot = getRealSlot(fromSlot);
		Item fromItem = getItem(slot);
		if (fromItem == null)
			return;
		int[] toRealSlot = getRealSlot(toSlot);
		Item toItem = getItem(toRealSlot);
		if (toItem == null)
			return;
		Item[] tab = new Item[bankTabs[slot[0]].length + 1];
		System.arraycopy(bankTabs[slot[0]], slot[1], tab, slot[1] - 1,
				bankTabs[slot[0]].length - slot[1] + 1);
		bankTabs[slot[0]][slot[1]] = toItem;
		bankTabs[slot[0]][slot[1] + 1] = fromItem;
		refreshTab(slot[0]);
	}

	public void switchItem(int fromSlot, int toSlot, int fromComponentId,
			int toComponentId) {
		// System.out.println(fromSlot+", "+toSlot+", "+fromComponentId+",
		// "+toComponentId);
		if (toSlot == 65535) {
			int toTab = toComponentId >= 76 ? 8 - (84 - toComponentId)
					: 9 - ((toComponentId - 46) / 2);
			if (toTab < 0 || toTab > 9)
				return;
			if (bankTabs.length == toTab) {
				int[] fromRealSlot = getRealSlot(fromSlot);
				if (fromRealSlot == null)
					return;
				if (toTab == fromRealSlot[0]) {
					switchItem(fromSlot, getStartSlot(toTab));
					return;
				}
				Item item = getItem(fromRealSlot);
				if (item == null)
					return;
				removeItem(fromSlot, item.getAmount(), false, true);
				createTab();
				bankTabs[bankTabs.length] = new Item[] { item };
				refreshTab(fromRealSlot[0]);
				refreshTab(toTab);
				refreshItems();
			} else if (bankTabs.length > toTab) {
				int[] fromRealSlot = getRealSlot(fromSlot);
				if (fromRealSlot == null)
					return;
				if (toTab == fromRealSlot[0]) {
					switchItem(fromSlot, getStartSlot(toTab));
					return;
				}
				Item item = getItem(fromRealSlot);
				if (item == null)
					return;
				boolean removed = removeItem(fromSlot, item.getAmount(), false,
						true);
				if (!removed)
					refreshTab(fromRealSlot[0]);
				else if (fromRealSlot[0] != 0 && toTab >= fromRealSlot[0])
					toTab -= 1;
				refreshTab(fromRealSlot[0]);
				addItem(item.getId(), item.getAmount(), toTab, true);
			}
		} else
			switchItem(fromSlot, toSlot);
	}

	public void switchItem(int fromSlot, int toSlot) {
		int[] fromRealSlot = getRealSlot(fromSlot);
		Item fromItem = getItem(fromRealSlot);
		if (fromItem == null)
			return;
		int[] toRealSlot = getRealSlot(toSlot);
		Item toItem = getItem(toRealSlot);
		if (toItem == null)
			return;
		bankTabs[fromRealSlot[0]][fromRealSlot[1]] = toItem;
		bankTabs[toRealSlot[0]][toRealSlot[1]] = fromItem;
		refreshTab(fromRealSlot[0]);
		if (fromRealSlot[0] != toRealSlot[0])
			refreshTab(toRealSlot[0]);
		refreshItems();
	}

	public void openDepositBox() {
		if (!hasVerified(3))
			return;
		player.getInterfaceManager().sendCentralInterface(11);
		player.getInterfaceManager().sendLockGameTab(
				InterfaceManager.INVENTORY_TAB, true);
		player.getInterfaceManager().sendLockGameTab(
				InterfaceManager.EQUIPMENT_TAB, true);
		// tab
		sendBoxInterItems();
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				player.getInterfaceManager().sendLockGameTab(
						InterfaceManager.INVENTORY_TAB, false);
				player.getInterfaceManager().sendLockGameTab(
						InterfaceManager.EQUIPMENT_TAB, false);
			}
		});
	}

	public void sendBoxInterItems() {
		player.getPackets().sendInterSetItemsOptionsScript(11, 1, 93, 6, 5,
				"Deposit-1", "Deposit-5", "Deposit-10", "Deposit-All",
				"Deposit-X", "Examine");
		player.getPackets().sendUnlockIComponentOptionSlots(11, 1, 0, 27, 0, 1,
				2, 3, 4, 5);
	}

	public void init() {

		player.getVarsManager().sendVar(4145, 2);// Displays items
		refreshViewingTab();
		refreshTabs();
		refreshLastX();
		refreshBank50Rows();
	}

	private int getBank50Rows() {
		return player.isAMemberGreaterThanGold() ? 6
				: player.isSilverMember() ? 8 : player.isBronzeMember() ? 4 : 1;
	}

	public void refreshBank50Rows() {
		player.getVarsManager().sendVarBit(942, getBank50Rows()); // 50 slots
		// per.
	}

	public void openBank() {
		if (!hasVerified(2))
			return;
		if (isHasBankPin() && !isHasEnteredPin()) {
			openBankPinInterface(false, false, true, false);
			return;
		}
		lastContainerCopy = null;
		player.getInterfaceManager().sendBankInterface(762);
		// player.getInterfaceManager().sendInventoryInterface(763);
		player.getInterfaceManager().setInterface(true, 762, 112, 1463);// TODO
		// find
		// comp
		if (!player.hasEmailRestrictions())
			player.getPackets().sendCSVarInteger(1324, 3);
		unlockButtons();
		sendItems();
		if (total_size == 0) {
			for (int i = 0; i < bankTabs.length; i++)
				total_size += getTabSize(i);
		}
		refreshTotalSize();
	}

	public void openPlayerBank(Player victim) {
		if (victim == null)
			return;
		player.getInterfaceManager().sendBankInterface(762);
		player.getInterfaceManager().setInterface(true, 762, 112, 1463);// TODO
		player.getPackets().sendItems(95, victim.getBank().getContainerCopy());
		refreshViewingTab();
		refreshTabs();
		unlockButtons();
	}

	public void refreshTotalSize() {
		int usedFreeSlots = total_size > 403 ? 403 : total_size;
		player.getPackets().sendCSVarInteger(1038, usedFreeSlots);
		player.getPackets().sendCSVarInteger(192,
				total_size < 403 ? usedFreeSlots : total_size);
	}

	public void refreshLastX() {
		player.getVarsManager().sendVar(1249, lastX);
	}

	public void potatoBank() {
		for (int slot = 0; slot < bankTabs.length; slot++) {
			Item[][] tabs = new Item[bankTabs.length - 1][];
			System.arraycopy(bankTabs, 0, tabs, 0, slot);
			System.arraycopy(bankTabs, slot + 1, tabs, slot, bankTabs.length
					- slot - 1);
			bankTabs = tabs;
			if (currentTab != 0 && currentTab >= slot)
				currentTab--;
		}
	}

	public void createTab() {
		int slot = bankTabs.length;
		Item[][] tabs = new Item[slot + 1][];
		System.arraycopy(bankTabs, 0, tabs, 0, slot);
		tabs[slot] = new Item[0];
		bankTabs = tabs;
	}

	public void destroyTab(int slot) {
		Item[][] tabs = new Item[bankTabs.length - 1][];
		System.arraycopy(bankTabs, 0, tabs, 0, slot);
		System.arraycopy(bankTabs, slot + 1, tabs, slot, bankTabs.length - slot
				- 1);
		bankTabs = tabs;
		if (currentTab != 0 && currentTab >= slot)
			currentTab--;
	}

	public boolean hasBankSpace() {
		return getBankSize() < getMaxBankSize();
	}

	public void withdrawItem(int bankSlot, int quanity) {
		withdrawItem(getRealSlot(bankSlot), quanity);
	}

	public void withdrawItem(int[] slots, int quantity) {
		if (quantity < 1)
			return;
		if (slots == null)
			return;
		Item item = getItem(slots);
		if (item == null)
			return;
		if (item.getAmount() < quantity)
			item = new Item(item.getId(), item.getAmount(),
			/* item.getDegrade() */0);
		else
			item = new Item(item.getId(), quantity, /* item.getDegrade() */0);
		boolean noted = false;
		ItemDefinitions defs = item.getDefinitions();
		if (withdrawNotes) {
			if (!defs.isNoted() && defs.getCertId() != -1) {
				item.setId(defs.getCertId());
				noted = true;
			} else
				player.getPackets().sendGameMessage(
						"You cannot withdraw this item as a note.");
		}
		if (noted || defs.isStackable()) {
			if (player.getInventory().getItems().containsOne(item)) {
				int slot = player.getInventory().getItems()
						.getThisItemSlot(item);
				Item invItem = player.getInventory().getItems().get(slot);
				if (invItem.getAmount() + item.getAmount() <= 0) {
					item.setAmount(Integer.MAX_VALUE - invItem.getAmount());
					player.getPackets().sendGameMessage(
							"Not enough space in your inventory.");
				}
			} else if (!player.getInventory().hasFreeSlots()) {
				player.getPackets().sendGameMessage(
						"Not enough space in your inventory.");
				return;
			}
		} else {
			int freeSlots = player.getInventory().getFreeSlots();
			if (freeSlots == 0) {
				player.getPackets().sendGameMessage(
						"Not enough space in your inventory.");
				return;
			}
			if (freeSlots < item.getAmount()) {
				item.setAmount(freeSlots);
				player.getPackets().sendGameMessage(
						"Not enough space in your inventory.");
			}
		}
		removeItem(slots, item.getAmount(), true, false);
		player.getInventory().addItemMoneyPouch(item);
	}

	public void sendWearBank(int bankSlot) {
		int[] slot = getRealSlot(bankSlot);
		if (slot == null)
			return;
		Item item = bankTabs[slot[0]][slot[1]];
		withdrawItem(bankSlot, 1);
		int invSlot = player.getInventory().getItems()
				.getThisItemSlot(item.getId());
		if (invSlot == -1
				|| !ButtonHandler.wear(player, invSlot, item.getId(), false)) {
			refreshItems();
			player.getEquipment().refreshItems(new Item[] {});
		} else {
			player.getInventory().refresh(invSlot);
			player.getAppearence().generateAppearenceData();
		}
	}

	public void sendWearInventory(int slot) {
		Item item = player.getInventory().getItem(slot);
		if (item == null)
			return;
		if (Consumables.eat(player, slot, item))
			return;
		if (ButtonHandler.wear(player, slot, item.getId(), false)) {
			player.getInventory().refresh(slot);
			player.getAppearence().generateAppearenceData();
		}
	}

	public void sendExamineInventory(int slotId) {
		Item item = player.getInventory().getItem(slotId);
		if (item == null)
			return;
		player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
	}

	public void sendExamine(int fakeSlot) {
		int[] slot = getRealSlot(fakeSlot);
		if (slot == null)
			return;
		Item item = bankTabs[slot[0]][slot[1]];
		player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
	}

	public void depositItem(int invSlot, int quantity, boolean refresh) {
		if (quantity < 1 || invSlot < 0 || invSlot > 27)
			return;
		Item item = player.getInventory().getItem(invSlot);
		if (item == null)
			return;
		int amt = player.getInventory().getItems().getNumberOf(item);
		if (amt < quantity)
			item = new Item(item.getId(), amt, /* item.getDegrade() */0);
		else
			item = new Item(item.getId(), quantity, /* item.getDegrade() */0);
		ItemDefinitions defs = item.getDefinitions();
		int originalId = item.getId();
		if (defs.isNoted() && defs.getCertId() != -1)
			item.setId(defs.getCertId());
		Item bankedItem = getItem(item.getId());
		if (bankedItem != null) {
			if (bankedItem.getAmount() + item.getAmount() <= 0) {
				item.setAmount(Integer.MAX_VALUE - bankedItem.getAmount());
				player.getPackets().sendGameMessage(
						"Not enough space in your bank.");
			} else if (bankedItem.getAmount() + item.getAmount() >= Integer.MAX_VALUE) {
				player.getPackets().sendGameMessage(
						"Unable to bank '" + item.getName() + "'.");
				return;
			}
		} else if (!hasBankSpace()) {
			player.getPackets().sendGameMessage(
					"Not enough space in your bank.");
			return;
		}
		player.getInventory().deleteItem(invSlot,
				new Item(originalId, item.getAmount(), /*
														 * item . getDegrade ( )
														 */0));
		addItem(item, refresh);
	}

	public void addItem(Item item, boolean refresh) {
		addItem(item.getId(), item.getAmount(), refresh);
	}

	public int addItems(Item[] items, boolean refresh) {
		int space = getMaxBankSize() - getBankSize();
		if (space != 0) {
			space = (space < items.length ? space : items.length);
			for (int i = 0; i < space; i++) {
				if (items[i] == null)
					continue;
				addItem(items[i], false);
			}
			if (refresh) {
				refreshTabs();
				refreshItems();
			}
		}
		return space;
	}

	public boolean addItem(int id, int quantity, boolean refresh) {
		return addItem(id, quantity, currentTab, refresh);
	}

	public boolean addItem(int id, int quantity, int creationTab,
			boolean refresh) {
		int[] slotInfo = getItemSlot(id);
		if (slotInfo == null) {
			if (creationTab >= bankTabs.length)
				creationTab = bankTabs.length - 1;
			if (creationTab < 0) // fixed now, alex
				creationTab = 0;
			int slot = bankTabs[creationTab].length;
			Item[] tab = new Item[slot + 1];
			System.arraycopy(bankTabs[creationTab], 0, tab, 0, slot);
			tab[slot] = new Item(id, quantity);
			bankTabs[creationTab] = tab;
			if (refresh)
				refreshTab(creationTab);
			total_size++;
		} else {
			Item item = bankTabs[slotInfo[0]][slotInfo[1]];
			bankTabs[slotInfo[0]][slotInfo[1]] = new Item(item.getId(),
					item.getAmount() + quantity,
					/* item.getDegrade() */0);
		}
		if (refresh) {
			refreshItems();
			refreshTotalSize();
		}
		return true;
	}

	public boolean removeItem(int fakeSlot, int quantity, boolean refresh,
			boolean forceDestroy) {
		return removeItem(getRealSlot(fakeSlot), quantity, refresh,
				forceDestroy);
	}

	public boolean removeItem(int[] slot, int quantity, boolean refresh,
			boolean forceDestroy) {
		if (slot == null)
			return false;
		Item item = bankTabs[slot[0]][slot[1]];
		boolean destroyed = false;
		if (quantity >= item.getAmount()) {
			if (bankTabs[slot[0]].length == 1
					&& (forceDestroy || bankTabs.length != 1)) {
				destroyTab(slot[0]);
				if (refresh)
					refreshTabs();
				destroyed = true;
			} else {
				Item[] tab = new Item[bankTabs[slot[0]].length - 1];
				System.arraycopy(bankTabs[slot[0]], 0, tab, 0, slot[1]);
				System.arraycopy(bankTabs[slot[0]], slot[1] + 1, tab, slot[1],
						bankTabs[slot[0]].length - slot[1] - 1);
				bankTabs[slot[0]] = tab;
				if (refresh)
					refreshTab(slot[0]);
			}
			total_size--;
		} else
			bankTabs[slot[0]][slot[1]] = new Item(item.getId(),
					item.getAmount() - quantity,
					/* item.getDegrade() */0);
		if (refresh) {
			refreshItems();
			refreshTotalSize();
		}
		return destroyed;
	}

	public Item getItem(int id) {
		for (int slot = 0; slot < bankTabs.length; slot++) {
			for (Item item : bankTabs[slot])
				if (item.getId() == id)
					return item;
		}
		return null;
	}

	public int[] getItemSlot(int id) {
		for (int tab = 0; tab < bankTabs.length; tab++) {
			for (int slot = 0; slot < bankTabs[tab].length; slot++)
				if (bankTabs[tab][slot].getId() == id)
					return new int[] { tab, slot };
		}
		return null;
	}

	public Item getItem(int[] slot) {
		if (slot == null)
			return null;
		return bankTabs[slot[0]][slot[1]];
	}

	public int getStartSlot(int tabId) {
		int slotId = 0;
		for (int tab = 1; tab < (tabId == 0 ? bankTabs.length : tabId); tab++)
			slotId += bankTabs[tab].length;

		return slotId;

	}

	public int[] getRealSlot(int slot) {
		for (int tab = 1; tab < bankTabs.length; tab++) {
			if (slot >= bankTabs[tab].length)
				slot -= bankTabs[tab].length;
			else
				return new int[] { tab, slot };
		}
		if (slot >= bankTabs[0].length)
			return null;
		return new int[] { 0, slot };
	}

	public void refreshViewingTab() {
		player.getVarsManager().sendVarBit(288, currentTab + 1);
	}

	public void refreshTab(int slot) {
		if (slot == 0)
			return;
		player.getVarsManager().sendVarBit(280 + (slot - 1), getTabSize(slot));
	}

	public void sendItems() {
		player.getPackets().sendItems(95, getContainerCopy());
	}

	public void refreshItems(int[] slots) {
		player.getPackets().sendUpdateItems(95, getContainerCopy(), slots);
	}

	public Item[] getContainerCopy() {
		if (lastContainerCopy == null)
			lastContainerCopy = generateContainer();
		return lastContainerCopy;
	}

	public void refreshItems() {
		refreshItems(generateContainer(), getContainerCopy());
	}

	public void refreshItems(Item[] itemsAfter, Item[] itemsBefore) {
		if (itemsBefore.length != itemsAfter.length) {
			lastContainerCopy = itemsAfter;
			sendItems();
			return;
		}
		int[] changedSlots = new int[itemsAfter.length];
		int count = 0;
		for (int index = 0; index < itemsAfter.length; index++) {
			if (itemsBefore[index] != itemsAfter[index])
				changedSlots[count++] = index;
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		lastContainerCopy = itemsAfter;
		refreshItems(finalChangedSlots);
	}

	public int getBankSize() {
		int size = 0;
		for (int i = 0; i < bankTabs.length; i++)
			size += bankTabs[i].length;
		return size;
	}

	public Item[] generateContainer() {
		Item[] container = new Item[getBankSize()];
		int count = 0;
		for (int slot = 1; slot < bankTabs.length; slot++) {
			System.arraycopy(bankTabs[slot], 0, container, count,
					bankTabs[slot].length);
			count += bankTabs[slot].length;
		}
		System.arraycopy(bankTabs[0], 0, container, count, bankTabs[0].length);
		return container;
	}

	public void unlockButtons() {
		player.getPackets().sendIComponentSettings(762, 128, 0, 18, 15302654);
		player.getPackets().sendIComponentSettings(762, 7, 0, 27, 14682110);
		player.getPackets().sendIComponentSettings(762, 215, 0, 871, 11011582);
		player.getPackets().sendIComponentSettings(762, 272, 0, 871, 2097152);
		for (int componentId = 253; componentId < 262; componentId++)
			player.getPackets().sendIComponentSettings(762, componentId, 0, 1,
					2097152);
	}

	public void searchBank() {
		player.getInterfaceManager().sendInputTextInterface();
		player.getPackets().sendCSVarInteger(190, 1);
	}

	public void switchWithdrawNotes() {
		withdrawNotes = !withdrawNotes;
	}

	public void switchInsertItems() {
		insertItems = !insertItems;
	}

	public void setCurrentTab(int currentTab) {
		if (currentTab >= bankTabs.length)
			return;
		this.currentTab = currentTab;
	}

	public int getLastX() {
		return lastX;
	}

	public void setLastX(int lastX) {
		this.lastX = lastX;
	}

	public void depositAllMoneyPouch(boolean banking) {
		int coinsCount = player.getMoneyPouch().getCoinsAmount();
		int space = 0;
		if (coinsCount != 0)
			space = addItems(new Item[] { new Item(995, coinsCount) }, banking);
		if (space != 0) {
			if (space < 1) {
				player.getPackets().sendGameMessage(
						"Not enough space in your bank.");
				return;
			}
			player.getMoneyPouch().sendDynamicInteraction(coinsCount, true,
					MoneyPouch.TYPE_REMOVE);
		}
	}

	public int getPin() {
		return pin;
	}

	public void openHelpInterface() {
		// find correct one
		// player.getInterfaceManager().sendCentralInterface(767);
	}

	public boolean isInsertItems() {
		return insertItems;
	}

	public int getCurrentTab() {
		return currentTab;
	}

	private transient boolean confirm, awesomecodingbooleanYESjakfnakfnakfnma,
			niouafuhnbuipafbuniopafhiuafbjafbnafnajkfaojnufajhnoafbjnja, ge;

	public boolean getHasBankPin() {
		// TODO Auto-generated method stub
		return isHasBankPin();
	}

	public void openPinSettings(boolean easy, boolean removed) {
		String[] pinBankMessage = {
				"Players are reminded that they should NEVER tell anyone their Bank PINs or passwords, nor should they ever enter their PINs on any website form.",
				"That number wouldn't be very hard to guess. Please try something different!",
				"Your Bank PIN has been removed, Bank of "
						+ Settings.SERVER_NAME
						+ " will no longer protect your bank from thiefs!" };
		player.getInterfaceManager().sendCentralInterface(14);
		player.getPackets().sendConfig(98, isHasBankPin() ? 1 : 0);
		player.getPackets().sendCSVarString(
				344,
				removed ? pinBankMessage[2] : easy ? pinBankMessage[1]
						: pinBankMessage[0]);
	}

	public void openConfirmationBankPinInterface() {
		// TODO Auto-generated method stub
		player.getInterfaceManager().sendCentralInterface(14);
		player.getPackets().sendConfig(98, -1);
		player.getPackets().sendIComponentText(14, 32,
				"Do you really wish to set a PIN on your bank account?");
		player.getPackets().sendIComponentText(14, 34,
				"Yes, I really want a bank PIN. I will never forget it!");
		player.getPackets()
				.sendIComponentText(14, 36, "No, I might forget it!");
	}

	public boolean isHasBankPin() {
		return hasBankPin;
	}

	public void setHasBankPin(boolean hasBankPin) {
		this.hasBankPin = hasBankPin;
	}

	public boolean isHasEnteredPin() {
		return hasEnteredPin;
	}

	public void setHasEnteredPin(boolean hasEnteredPin) {
		this.hasEnteredPin = hasEnteredPin;
	}

	public void openBankPinInterface(boolean delete, boolean confirm,
			boolean bank, boolean geopen) {
		String bankPinString[] = { "Set new PIN", "Confirm new PIN",
				"Bank of " + Settings.SERVER_NAME, "Delete PIN",
				"Open Grand Exchange" };
		awesomecodingbooleanYESjakfnakfnakfnma = delete;
		niouafuhnbuipafbuniopafhiuafbjafbnafnajkfaojnufajhnoafbjnja = bank;
		ge = geopen;
		player.getInterfaceManager().sendCentralInterface(13);
		// player.getPackets().sendConfigByFile(1010, -1);
		if (!confirm) {
			player.getPackets().sendInterface(false, 13, 759);
			resetButtons();
		}
		player.getPackets()
				.sendIComponentText(
						13,
						27,
						delete ? bankPinString[3] : ge ? bankPinString[4]
								: isHasBankPin() ? bankPinString[2]
										: confirm ? bankPinString[1]
												: bankPinString[0]);
		int[] components = { 6, 8, 9, 10, 11, 12, 13, 14, 15 };
		for (int i = 0; i < components.length; i++)
			player.getPackets().sendHideIComponent(13, components[i], false);
	}

	public void resetButtons() {
		temp1 = 0;
		temp2 = 0;
		temp3 = 0;
		temp4 = 0;
	}

	public static int getBankPin1() {
		return temp1;
	}

	public static int getBankPin2() {
		return temp2;
	}

	public static int getBankPin3() {
		return temp3;
	}

	public static int getBankPin4() {
		return temp4;
	}

	private transient static int temp1;

	private transient static int temp2;

	private transient static int temp3;

	private transient static int temp4;

	private short bankPin;

	private boolean hasBankPin;

	private transient int tempCopy1, tempCopy2, tempCopy3, tempCopy4;

	public void setPin(int num1, int num2, int num3, int num4) {
		bankPin = (short) ((num1 << 12) + (num2 << 8) + (num3 << 4) + num4);
	}

	public boolean pinCorrect(int num1, int num2, int num3, int num4) {
		return bankPin == (short) ((num1 << 12) + (num2 << 8) + (num3 << 4) + num4);
	}

	public void handleBankPinButtons(int interfaceId, int componentId) {
		if (interfaceId == 14) {
			if (componentId == 33 || componentId == 18) {
				player.closeInterfaces();
				if (!isHasBankPin())
					openBankPinInterface(false, false, true, false);
			}
			if (componentId == 19) {
				if (isHasBankPin()) {
					if (hasEnteredPin) {
						setHasBankPin(false);
						openPinSettings(false, true);
						return;
					}
					openBankPinInterface(true, false, true, false);
					return;
				}
				player.getPackets().sendGameMessage(
						"This feature has not been added yet.");
				player.closeInterfaces();
			}
			if (componentId == 35 || componentId == 20) {
				player.closeInterfaces();
			}
		}
		if (interfaceId == 759) {
			if (temp1 == 0) {
				if (componentId == 4) {
					temp1 = 0;
				} else if (componentId == 8) {
					temp1 = 1;
				} else if (componentId == 12) {
					temp1 = 2;
				} else if (componentId == 16) {
					temp1 = 3;
				} else if (componentId == 20) {
					temp1 = 4;
				} else if (componentId == 24) {
					temp1 = 5;
				} else if (componentId == 28) {
					temp1 = 6;
				} else if (componentId == 32) {
					temp1 = 7;
				} else if (componentId == 36) {
					temp1 = 8;
				} else if (componentId == 40) {
					temp1 = 9;
				}
				return;
			}
			if (temp2 == 0) {
				if (componentId == 4) {
					temp2 = 0;
				} else if (componentId == 8) {
					temp2 = 1;
				} else if (componentId == 12) {
					temp2 = 2;
				} else if (componentId == 16) {
					temp2 = 3;
				} else if (componentId == 20) {
					temp2 = 4;
				} else if (componentId == 24) {
					temp2 = 5;
				} else if (componentId == 28) {
					temp2 = 6;
				} else if (componentId == 32) {
					temp2 = 7;
				} else if (componentId == 36) {
					temp2 = 8;
				} else if (componentId == 40) {
					temp2 = 9;
				}
				return;
			}
			if (temp3 == 0) {
				if (componentId == 4) {
					temp3 = 0;
				} else if (componentId == 8) {
					temp3 = 1;
				} else if (componentId == 12) {
					temp3 = 2;
				} else if (componentId == 16) {
					temp3 = 3;
				} else if (componentId == 20) {
					temp3 = 4;
				} else if (componentId == 24) {
					temp3 = 5;
				} else if (componentId == 28) {
					temp3 = 6;
				} else if (componentId == 32) {
					temp3 = 7;
				} else if (componentId == 36) {
					temp3 = 8;
				} else if (componentId == 40) {
					temp3 = 9;
				}
				player.getPackets().sendConfigByFile(1010, -1);
				return;
			}
			if (temp4 == 0) {
				if (componentId == 4) {
					temp4 = 0;
				} else if (componentId == 8) {
					temp4 = 1;
				} else if (componentId == 12) {
					temp4 = 2;
				} else if (componentId == 16) {
					temp4 = 3;
				} else if (componentId == 20) {
					temp4 = 4;
				} else if (componentId == 24) {
					temp4 = 5;
				} else if (componentId == 28) {
					temp4 = 6;
				} else if (componentId == 32) {
					temp4 = 7;
				} else if (componentId == 36) {
					temp4 = 8;
				} else if (componentId == 40) {
					temp4 = 9;
				}
				if (!isHasBankPin() && !awesomecodingbooleanYESjakfnakfnakfnma) {
					if (Utils.checkBankPin(temp1, temp2, temp3, temp4)) {
						player.closeInterfaces();
						openPinSettings(true, false);
						return;
					}
					if (tempCopy4 != 0)
						confirm = true;
					if (!confirm) {
						openBankPinInterface(false, true, true, false);
						tempCopy1 = temp1;
						tempCopy2 = temp2;
						tempCopy3 = temp3;
						tempCopy4 = temp4;
						resetButtons();
						return;
					}
					confirm = false;
					setHasBankPin(true);
					setHasEnteredPin(true);
					setPin(tempCopy1, tempCopy2, tempCopy3, tempCopy4);
					openPinSettings(false, false);
					return;
				}
				if (pinCorrect(temp1, temp2, temp3, temp4)) {
					if (awesomecodingbooleanYESjakfnakfnakfnma) {
						setHasBankPin(false);
						awesomecodingbooleanYESjakfnakfnakfnma = false;
						player.closeInterfaces();
						player.getPackets().sendGameMessage(
								"You have sucessfully removed your PIN.");
						return;
					}
					player.closeInterfaces();
					setHasEnteredPin(true);
					if (niouafuhnbuipafbuniopafhiuafbjafbnafnajkfaojnufajhnoafbjnja)
						openBank();
					else if (ge)
						player.getGeManager().openGrandExchange();
					player.getPackets().sendGameMessage(
							"You have correctly entered your PIN.");
				} else {
					player.closeInterfaces();
					player.getPackets().sendGameMessage(
							"You entered the wrong PIN.");
				}
				return;
			}
		}
	}
}