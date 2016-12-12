package net.kagani.game.player.content;

import java.io.Serializable;
import java.util.HashMap;

import net.kagani.game.item.Item;
import net.kagani.game.player.Bank;
import net.kagani.game.player.Player;

public class PresetSetups implements Serializable {

	private static final long serialVersionUID = -8227328154529000367L;

	private String name;
	private Item[] equipment, inventory;
	private int spellBook, prayers;

	// TODO
	// - Level checks before equipping items [DONE]
	// - Bank worn equipment and inventory items prior to loading the setup, to
	// prevent deleting items. [DONE]
	// - If unable to bank, return and notify the user. [DONE (partially, not
	// properly; items will still be safe)]
	// - If item id noted, add item in non-noted form [DONE (in Bank class,
	// addItem method)]

	/**
	 * @param name
	 *            - The name of the preset; presets are called via the name.
	 * @param equipment
	 *            - The players current equipment setup; sets the players
	 *            equipment
	 * @param inventory
	 *            - The players current inventory setup; sets the players
	 *            inventory
	 * @param spellBook
	 *            - The players current spellbook; sets the players spellbook
	 * @param prayers
	 *            - The players current prayer book; sets the players prayer
	 *            book
	 */
	public PresetSetups(String name, Item[] equipment, Item[] inventory,
			int spellBook, int prayers) {
		this.name = name;
		this.equipment = equipment;
		this.inventory = inventory;
		this.spellBook = spellBook;
		this.prayers = prayers;
	}

	public static void giveSet(Player player, PresetSetups set) {
		/**
		 * Ensures that both the player and the set are not null to prevent
		 * deadlocks
		 */
		if (set == null || player == null)
			return;
		/**
		 * Only allows for sets to be loaded whilst banking unless the player is
		 * an owner
		 */
		if (!player.getInterfaceManager().containsInterface(762)
				&& player.getRights() != 4) {
			player.getPackets().sendGameMessage(
					"You can only load a preset setup while banking.");
			return;
		}
		/**
		 * The players current inventory (copy to prevent editing the items)
		 */
		Item inventory[] = player.getInventory().getItems().getItemsCopy();
		if (inventory != null) {
			if (Bank.MAX_BANK_SIZE - player.getBank().getBankSize() < player
					.getInventory().getItems().getSize()) {
				player.getPackets()
						.sendGameMessage(
								"Preset cancelled as there was not enough space in your bank to load the configuration.");
				return;
			}
			for (int i = 0; i < inventory.length; i++) {
				if (inventory[i] == null
						|| inventory[i].getAmount() < 1
						|| !player.getInventory().containsItem(
								inventory[i].getId(), inventory[i].getAmount()))
					continue;
				player.getBank().addItem(inventory[i].getId(),
						inventory[i].getAmount(), true);
				player.getInventory().deleteItem(inventory[i].getId(),
						inventory[i].getAmount());
			}
			player.getPackets()
					.sendGameMessage(
							"All items in your inventory have been added to your bank.",
							true);
		}
		/**
		 * The players current equipment (copy to prevent editing the items)
		 */
		Item equipment[] = player.getEquipment().getItems().getItemsCopy();
		if (equipment != null) {
			if (Bank.MAX_BANK_SIZE - player.getBank().getBankSize() < player
					.getEquipment().getItems().getSize()) {
				player.getPackets()
						.sendGameMessage(
								"Preset cancelled as there was not enough space in your bank to load the configuration.");
				return;
			}
			for (int i = 0; i < equipment.length; i++) {
				if (equipment[i] == null || equipment[i].getAmount() < 1)
					continue;
				player.getBank().addItem(equipment[i].getId(),
						equipment[i].getAmount(), true);
				player.getEquipment().deleteItem(equipment[i].getId(),
						equipment[i].getAmount());
				player.getEquipment().refresh(i);
			}
			player.getPackets().sendGameMessage(
					"All of your equipment has been added to your bank.", true);
		}
		/**
		 * Sets the players spellbook to their saved spellbook (ancient /
		 * standard / lunar) - Utilises an int
		 */
		player.getCombatDefinitions().setSpellBook(set.getSpellBook());
		/**
		 * Sets the players prayer book to their saved prayer book (curses /
		 * standard) - Utilises a boolean
		 */
		player.getPrayer().setPrayerBook(set.getPrayers() == 1);
		/**
		 * Checks that the player's saved equipment isn't equal to null (null
		 * check and speed optimisation) Then, if it's not null, proceeds to
		 * loop through the size of the array, with null checks for items and
		 * set the players equipment to the alloted slots
		 */
		if (set.getEquipment() != null) {
			for (int i = 0; i < set.getEquipment().length; i++) {
				if (set.getEquipment()[i] == null
						|| player.getBank().getItem(
								set.getEquipment()[i].getId()) == null) {
					continue;
				}
				int amount = set.getEquipment()[i].getAmount();
				if (amount > player.getBank()
						.getItem(set.getEquipment()[i].getId()).getAmount())
					amount = player.getBank()
							.getItem(set.getEquipment()[i].getId()).getAmount();
				if (amount <= 0) {
					continue;
				}
				/**
				 * The requirements to equip the selected item (will include
				 * comp cape etc, etc)
				 */
				HashMap<Integer, Integer> requirements = set.getEquipment()[i]
						.getDefinitions().getWearingSkillRequiriments();
				boolean hasRequirements = true;
				if (requirements != null) {
					for (int skillId : requirements.keySet()) {
						/**
						 * If the skill is an invalid id; continue
						 */
						if (skillId > 24 || skillId < 0)
							continue;
						int level = requirements.get(skillId);
						/**
						 * If the level is an invalid level; continue
						 */
						if (level < 0 || level > 120)
							continue;
						/**
						 * If the player doesn't meet the wearing requirements
						 * of the item; continue
						 */
						if (player.getSkills().getLevelForXp(skillId) < level) {
							player.getPackets()
									.sendGameMessage(
											"You were unable to equip your "
													+ set.getEquipment()[i]
															.getName()
															.toLowerCase()
													+ ", as you don't meet the requirements to wear them.",
											true);
							hasRequirements = false;
							continue;
						}
					}
				}
				/**
				 * If the player doesn't meet the requirements; continue
				 */
				if (!hasRequirements)
					continue;
				player.getBank().removeItem(
						player.getBank().getItemSlot(
								set.getEquipment()[i].getId()), amount, true,
						false);
				player.getEquipment()
						.getItems()
						.set(i, new Item(set.getEquipment()[i].getId(), amount));
				player.getEquipment().refresh(i);
			}
		}
		/**
		 * Checks that the player's saved inventory isn't equal to null (null
		 * check and speed optimisation) Then, if it's not null, proceeds to
		 * loop through the size of the array, with null checks for items and
		 * set the players inventory to the alloted slots
		 * 
		 * Item requirements do not need to be checked for the inventory, as
		 * there are no requirements to place something in your inventory.
		 */
		if (set.getInventory() != null) {
			for (int i = 0; i < set.getInventory().length; i++) {
				if (set.getInventory()[i] == null
						|| player.getBank().getItem(
								set.getInventory()[i].getId()) == null) {
					continue;
				}
				int amount = set.getInventory()[i].getAmount();
				if (amount > player.getBank()
						.getItem(set.getInventory()[i].getId()).getAmount())
					amount = player.getBank()
							.getItem(set.getInventory()[i].getId()).getAmount();
				if (amount <= 0) {
					continue;
				}
				player.getBank().removeItem(
						player.getBank().getItemSlot(
								set.getInventory()[i].getId()), amount, true,
						false);
				player.getInventory()
						.getItems()
						.set(i, new Item(set.getInventory()[i].getId(), amount));
			}
		}
		/**
		 * Refreshes the players inventory (all slots at once, rather than doing
		 * it each loop - used for optimisation and speed)
		 */
		player.getInventory().refresh();
		/**
		 * Generates the players appearance data block, so that they and all
		 * others around them see the change in gear / appearance
		 */
		player.getAppearence().generateAppearenceData();
		/**
		 * Notifies the player that they were successful in loading their setup
		 */
		player.getPackets().sendGameMessage("Loaded setup: " + set.name + ".");
	}

	/**
	 * @return - Returns the name of the preset
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return - Returns the equipment saved in the preset
	 */
	public Item[] getEquipment() {
		return equipment;
	}

	/**
	 * @return - Returns the inventory saved in the preset
	 */
	public Item[] getInventory() {
		return inventory;
	}

	/**
	 * @return - Returns the prayer book saved in the preset
	 */
	public int getPrayers() {
		return prayers;
	}

	/**
	 * @return - Returns the spellbook saved in the preset
	 */
	public int getSpellBook() {
		return spellBook;
	}

}