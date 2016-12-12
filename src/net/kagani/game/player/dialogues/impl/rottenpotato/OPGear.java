package net.kagani.game.player.dialogues.impl.rottenpotato;

import net.kagani.game.item.Item;
import net.kagani.game.player.Equipment;
import net.kagani.game.player.Skills;
import net.kagani.game.player.dialogues.Dialogue;

public class OPGear extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 1:
			switch (componentId) {
			case OPTION_1:
				stage = 2;
				sendOptionsDialogue("Melee", "Pure.", "Max.", "Nevermind.");
				break;
			case OPTION_2:
				stage = 3;
				sendOptionsDialogue("Range", "Pure.", "Max.", "Nevermind.");
				break;
			case OPTION_3:
				stage = 4;
				sendOptionsDialogue("Mage", "Pure.", "Max.", "Nevermind.");
				break;
			case OPTION_4:
				stage = 5;
				sendOptionsDialogue("Tank", "Nothing.", "Full Tank.", "Nevermind.");
				break;
			case OPTION_5:
				stage = 6;
				sendOptionsDialogue("Custom setup", "Load setup.", "Save this setup.", "J-Mod outfit.", "Nevermind.");
				break;
			}
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				setClear(0, false);
				setStats(80, 1, 99, 99, 99, 52, 99, 1);
				setEquipment(2581, 6107, 24379, 25991, 18349, 3105, 24376, 15220, 22298, 23659, 19335, 0);
				setFinish("melee/pure");
				break;
			case OPTION_2:
				setClear(2, true);
				setStats(99, 99, 99, 99, 99, 99, 99, 99);
				setEquipment(30005, 30008, 30011, 0, 31725, 21787, 30213, 15220, 22298, 20771, 19335, 0);
				setFinish("melee/max");
				break;
			case OPTION_3:
				end();
				break;
			}
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				setClear(0, false);
				setStats(1, 1, 99, 99, 1, 52, 1, 1);
				setEquipment(2581, 6107, 24379, 28441, 28437, 2577, 24376, 15019, 22298, 23659, 19335, 28465);
				setFinish("range/pure");
				break;
			case OPTION_2:
				setClear(0, false);
				setStats(99, 99, 99, 99, 99, 99, 99, 99);
				setEquipment(29854, 29857, 29860, 28441, 28437, 20452, 31203, 15019, 22298, 20771, 19335, 28465);
				setFinish("range/max");
				break;
			case OPTION_3:
				end();
				break;
			}
			break;

		case 4:
			switch (componentId) {
			case OPTION_1:
				setClear(1, false);
				setStats(1, 1, 1, 99, 1, 52, 99, 1);
				setEquipment(25825, 25827, 25831, 25802, 28617, 25833, 25829, 15018, 22298, 23659, 19335, 0);
				setFinish("mage/pure");
				break;
			case OPTION_2:
				setClear(1, true);
				setStats(99, 99, 99, 99, 99, 99, 99, 99);
				setEquipment(28608, 28611, 28614, 30022, 25654, 20452, 31189, 15018, 22298, 20771, 19335, 0);
				setFinish("mage/max");
				break;
			case OPTION_3:
				end();
				break;
			}
			break;

		case 5:
			switch (componentId) {
			case OPTION_1:
				setClear(0, false);
				setStats(99, 99, 99, 99, 99, 99, 99, 99);
				setFinish("tank/nothing");
				break;
			case OPTION_2:
				setClear(2, true);
				setStats(99, 99, 99, 99, 99, 99, 99, 99);
				setEquipment(30005, 30008, 30011, 30014, 31725, 21787, 30213, 15220, 22298, 20771, 19335, 0);
				setFinish("tank/full");
				break;
			case OPTION_3:
				end();
				break;
			}
			break;

		case 6:
			switch (componentId) {
			case OPTION_1:
				setClear(0, false);
				setStats(player.saveattack, player.savedefence, player.savestrength, player.savehitpoints,
						player.saverange, player.saveprayer, player.savemagic, player.savesummoning);
				/*
				 * setEquipment(player.savehat, player.savechest,
				 * player.savelegs, player.saveshield, player.saveweapon,
				 * player.saveboots, player.savegloves, player.savering,
				 * player.saveaura, player.savecape, player.saveamulet,
				 * player.saveammo);
				 */

				setFinish("custom");
				end();
				break;
			case OPTION_2:
				/*
				 * int helmId = player.getEquipment().getHatId(); int chestId =
				 * player.getEquipment().getChestId(); int legsId =
				 * player.getEquipment().getLegsId(); int weaponId =
				 * player.getEquipment().getWeaponId(); int bootsId =
				 * player.getEquipment().getBootsId(); int glovesId =
				 * player.getEquipment().getGlovesId(); int auraId =
				 * player.getEquipment().getAuraId(); int capeId =
				 * player.getEquipment().getCapeId(); int shieldId =
				 * player.getEquipment().getShieldId(); int ringId =
				 * player.getEquipment().getRingId(); int ammyId =
				 * player.getEquipment().getAmuletId(); int ammoId =
				 * player.getEquipment().getAmmoId(); player.savehat = helmId;
				 * player.savechest = chestId; player.savelegs = legsId;
				 * player.saveweapon = weaponId; player.saveboots = bootsId;
				 * player.savegloves = glovesId; player.saveaura = auraId;
				 * player.savecape = capeId; player.saveshield = shieldId;
				 * player.savering = ringId; player.saveamulet = ammyId;
				 * player.saveammo = ammoId;
				 */
				player.saveattack = player.getSkills().getLevel(0);
				player.savestrength = player.getSkills().getLevel(2);
				player.savedefence = player.getSkills().getLevel(1);
				player.saverange = player.getSkills().getLevel(4);
				player.saveprayer = player.getSkills().getLevel(5);
				player.savesummoning = player.getSkills().getLevel(23);
				player.savehitpoints = player.getSkills().getLevel(3);
				player.savemagic = player.getSkills().getLevel(6);
				player.getPackets().sendGameMessage("Successfully saved your outfit.");
				end();
				break;
			case OPTION_3:
				setClear(0, false);
				setEquipment(25867, 25868, 25869, 0, 0, 25870, 25871, 0, 0, 25872, 0, 0);
				setFinish("custom/j-mod outfit");
				player.getInventory().reset();
				player.getInventory().addItem(5733, 1);
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void start() {
		stage = 1;
		sendOptionsDialogue("Rotten Potato - Option: Gear Setup", "Melee.", "Range.", "Mage.", "Tank.",
				"Load/save custom setup.");
	}

	public void setClear(int spellbook, boolean curses) {
		player.getEquipment().getItems().clear();
		player.getInventory().reset();
		player.getCombatDefinitions().setSpellBook(spellbook);
		player.getPrayer().setPrayerBook(curses);
		setInventory();
	}

	public void setStats(int attack, int defence, int strength, int hitpoints, int range, int prayer, int magic,
			int summoning) {
		player.getSkills().set(0, attack);
		player.getSkills().setXp(0, Skills.getXPForLevel(attack));

		player.getSkills().set(1, defence);
		player.getSkills().setXp(1, Skills.getXPForLevel(defence));

		player.getSkills().set(2, strength);
		player.getSkills().setXp(2, Skills.getXPForLevel(strength));

		player.getSkills().set(3, hitpoints);
		player.getSkills().setXp(3, Skills.getXPForLevel(hitpoints));

		player.getSkills().set(4, range);
		player.getSkills().setXp(4, Skills.getXPForLevel(range));

		player.getSkills().set(5, prayer);
		player.getSkills().setXp(5, Skills.getXPForLevel(prayer));

		player.getSkills().set(6, magic);
		player.getSkills().setXp(6, Skills.getXPForLevel(magic));

		player.getSkills().set(23, summoning);
		player.getSkills().setXp(23, Skills.getXPForLevel(summoning));
	}

	public void setEquipment(int head, int chest, int legs, int shield, int weapon, int boots, int gloves, int ring,
			int aura, int cape, int amulet, int arrow) {
		if (head != 0)
			player.getEquipment().getItems().set(Equipment.SLOT_HAT, new Item(head, 1));
		if (chest != 0)
			player.getEquipment().getItems().set(Equipment.SLOT_CHEST, new Item(chest, 1));
		if (legs != 0)
			player.getEquipment().getItems().set(Equipment.SLOT_LEGS, new Item(legs, 1));
		if (boots != 0)
			player.getEquipment().getItems().set(Equipment.SLOT_FEET, new Item(boots, 1));
		if (ring != 0)
			player.getEquipment().getItems().set(Equipment.SLOT_RING, new Item(ring, 1));
		if (gloves != 0)
			player.getEquipment().getItems().set(Equipment.SLOT_HANDS, new Item(gloves, 1));
		if (aura != 0)
			player.getEquipment().getItems().set(Equipment.SLOT_AURA, new Item(aura, 1));
		if (cape != 0)
			player.getEquipment().getItems().set(Equipment.SLOT_CAPE, new Item(cape, 1));
		if (shield != 0)
			player.getEquipment().getItems().set(Equipment.SLOT_SHIELD, new Item(shield, 1));
		if (amulet != 0)
			player.getEquipment().getItems().set(Equipment.SLOT_AMULET, new Item(amulet, 1));
		if (weapon != 0)
			player.getEquipment().getItems().set(Equipment.SLOT_WEAPON, new Item(weapon, 1));
		if (arrow != 0)
			player.getEquipment().getItems().set(Equipment.SLOT_ARROWS, new Item(arrow, Integer.MAX_VALUE));
	}

	public void setInventory() {
		player.getInventory().addItem(23531, 2);
		player.getInventory().addItem(23609, 1);
		player.getInventory().addItem(23243, 2);
		player.getInventory().addItem(26313, 19);
	}

	public void setFinish(String equipmentName) {
		if (player.getRights() < 2) {
			end();
			return;
		}
		for (int i = 0; i < 18; i++) {
			player.getEquipment().refresh(i);
		}
		for (int i = 0; i < 26; i++) {
			player.getSkills().refresh(i);
		}
		player.getAppearence().generateAppearenceData();
		player.getInventory().addItem(5733, 1);
		player.getPackets().sendGameMessage("equipment set to: " + equipmentName + ".");
		player.reset();
		end();
	}
}