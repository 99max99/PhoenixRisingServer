package net.kagani.game.player.content;

import net.kagani.cache.loaders.ClientScriptMap;
import net.kagani.cache.loaders.GeneralRequirementMap;
import net.kagani.game.Animation;
import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.player.Appearence;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

public final class PlayerLook {

	public static void openCharacterCustomizing(Player player) {
		player.getPackets().sendAppearenceLook();
		player.getInterfaceManager().setRootInterface(1420, false);
		player.getPackets().sendHideIComponent(1420, 159, false);

		player.getPackets()
				.sendUnlockIComponentOptionSlots(1420, 163, 0, 46, 0); // colors
		player.getPackets()
				.sendUnlockIComponentOptionSlots(1420, 185, 0, 55, 0); // style
		player.getPackets()
				.sendUnlockIComponentOptionSlots(1420, 102, -1, 0, 0);// random
		// button
	}

	public static void randomizeLook(Appearence appearence) {

		ClientScriptMap skinMap = ClientScriptMap.getMap(7724);

		// dont allow skins out of default skins
		appearence.setSkinColor(ClientScriptMap.getMap(748).getIntValue(
				skinMap.getIntValueAtIndex(Utils.random(skinMap.getSize()))));

		ClientScriptMap hairColor = ClientScriptMap.getMap(2345);
		appearence.setHairColor(hairColor.getIntValueAtIndex(Utils
				.random(hairColor.getSize())));

		ClientScriptMap topColor = ClientScriptMap.getMap(3282);
		appearence.setTopColor(topColor.getIntValueAtIndex(Utils
				.random(topColor.getSize())));
		appearence.setLegsColor(topColor.getIntValueAtIndex(Utils
				.random(topColor.getSize())));

		ClientScriptMap bootsColor = ClientScriptMap.getMap(3297);
		appearence.setBootsColor(bootsColor.getIntValueAtIndex(Utils
				.random(bootsColor.getSize())));

		boolean male = appearence.isMale();

		ClientScriptMap hairStyle = ClientScriptMap.getMap(male ? 3304 : 3302);
		GeneralRequirementMap map = GeneralRequirementMap.getMap(hairStyle
				.getIntValueAtIndex(Utils.random(hairStyle.getSize())));
		appearence.setHairStyle(map.getIntValue(788));

		ClientScriptMap topStyle = ClientScriptMap.getMap(male ? 3287 : 3299);
		appearence.setTopStyle(topStyle.getIntValueAtIndex(Utils
				.random(topStyle.getSize())));
		for (int i = 0; i < SET_COUNT; i++) {
			int[] set = getSet(i);
			if (set[0] == appearence.getTopStyle()) {
				appearence.setArmsStyle(set[1]);
				appearence.setHandsStyle(set[2]);
				break;
			}
		}

		ClientScriptMap legsStyle = ClientScriptMap.getMap(male ? 3289 : 3301);
		appearence.setLegsStyle(legsStyle.getIntValueAtIndex(Utils
				.random(legsStyle.getSize())));

		ClientScriptMap bootsStyle = ClientScriptMap.getMap(male ? 3290 : 3293);
		appearence.setBootsStyle(bootsStyle.getIntValueAtIndex(Utils
				.random(bootsStyle.getSize())));

		if (male) {
			ClientScriptMap beardStyle = ClientScriptMap.getMap(3307);
			appearence.setBeardStyle(beardStyle.getIntValueAtIndex(Utils
					.random(beardStyle.getSize())));
		}
	}

	public static int SET_COUNT = 96;

	public static int[] getSet(int index) {
		GeneralRequirementMap map = GeneralRequirementMap
				.getMap((index >= 64 ? (28114 - 64) : 1048) + index);
		int[] parts = new int[5];
		for (int i = 1182; i <= 1186; i++)
			parts[i - 1182] = map.getIntValue(i);
		return parts;
	}

	public static void setSet(Player player, int id) {
		int[] set = getSet(id);
		if (set == null) {
			player.getPackets().sendGameMessage("This set doesnt exist :)");
			return;
		}
		for (int i = 0; i < 5; i++)
			player.getAppearence().setLook(2 + i, set[i]);
		player.getAppearence().generateAppearenceData();
	}

	public static void handleCharacterCustomizingButtons(Player player,
			int buttonId, int slotId) {
		if (buttonId == 18 || buttonId == 19) {
			boolean male = buttonId == 19;
			if (male != player.getAppearence().isMale()) {
				if (!male)
					player.getAppearence().female();
				else
					player.getAppearence().male();
				randomizeLook(player.getAppearence());
				player.getPackets().sendAppearenceLook();
			}
		} else if (buttonId == 102) {
			randomizeLook(player.getAppearence());
			player.getPackets().sendAppearenceLook();
		} else if (buttonId == 163) {
			Integer tab = (Integer) player.getTemporaryAttributtes().get(
					Key.PLAYER_CUSTOMIZATION_TAB);
			if (tab == null || tab == 0) {
				player.getAppearence().setSkinColor(
						ClientScriptMap.getMap(748).getIntValue(
								ClientScriptMap.getMap(7724).getIntValue(
										slotId / 2)));
				player.getPackets().sendAppearenceLook();
			} else if (tab == 1
					|| (tab == 5 && player.getAppearence().isMale())) { // hair/bear
				player.getAppearence().setHairColor(
						ClientScriptMap.getMap(2345).getIntValue(
								ClientScriptMap.getMap(7723).getIntValue(
										slotId / 2)));
				player.getPackets().sendAppearenceLook();
			} else if (tab == 2) { // top
				player.getAppearence().setTopColor(
						ClientScriptMap.getMap(3282).getIntValue(
								ClientScriptMap.getMap(7721).getIntValue(
										slotId / 2)));
				player.getPackets().sendAppearenceLook();
			} else if (tab == 3) { // legs
				player.getAppearence().setLegsColor(
						ClientScriptMap.getMap(3282).getIntValue(
								ClientScriptMap.getMap(7721).getIntValue(
										slotId / 2)));
				player.getPackets().sendAppearenceLook();
			} else if (tab == 4) { // boot
				player.getAppearence().setBootsColor(
						ClientScriptMap.getMap(3297).getIntValue(
								ClientScriptMap.getMap(7722).getIntValue(
										slotId / 2)));
				player.getPackets().sendAppearenceLook();
			}
		} else if (buttonId == 185) {
			Integer tab = (Integer) player.getTemporaryAttributtes().get(
					Key.PLAYER_CUSTOMIZATION_TAB);
			if (tab == null || tab == 0)
				return;
			boolean male = player.getAppearence().isMale();
			if (tab == 1) { // hair/bear
				int map1 = ClientScriptMap.getMap(male ? 3304 : 3302)
						.getIntValue(slotId / 2);
				if (map1 == 0)
					return;
				GeneralRequirementMap map = GeneralRequirementMap.getMap(map1);
				player.getAppearence().setHairStyle(map.getIntValue(788));
				player.getPackets().sendAppearenceLook();
			} else if (tab == 2) { // top
				player.getAppearence().setTopStyle(
						ClientScriptMap.getMap(male ? 3287 : 3299).getIntValue(
								slotId / 2));
				for (int i = 0; i < SET_COUNT; i++) {
					int[] set = getSet(i);
					if (set[0] == player.getAppearence().getTopStyle()) {
						player.getAppearence().setArmsStyle(set[1]);
						player.getAppearence().setHandsStyle(set[2]);
						break;
					}
				}
				player.getPackets().sendAppearenceLook();
			} else if (tab == 3) { // legs
				player.getAppearence().setLegsStyle(
						ClientScriptMap.getMap(male ? 3289 : 3301).getIntValue(
								slotId / 2));
				player.getPackets().sendAppearenceLook();
			} else if (tab == 4) { // boot
				player.getAppearence().setBootsStyle(
						ClientScriptMap.getMap(male ? 3290 : 3293).getIntValue(
								slotId / 2));
				player.getPackets().sendAppearenceLook();
			} else if (tab == 5 && male) {
				player.getAppearence().setBeardStyle(
						ClientScriptMap.getMap(3307).getIntValue(slotId / 2));
				player.getPackets().sendAppearenceLook();
			}
		} else if (buttonId >= 219 && buttonId <= 223)
			player.getTemporaryAttributtes().put(Key.PLAYER_CUSTOMIZATION_TAB,
					buttonId - 218);
		else if (buttonId == 192)
			player.getTemporaryAttributtes().remove(
					Key.PLAYER_CUSTOMIZATION_TAB);
		else if (buttonId == 432) {
			player.getTemporaryAttributtes().remove(
					Key.PLAYER_CUSTOMIZATION_TAB);
			if (player.isLobby()) {
				player.getInterfaceManager().sendLobbyInterfaces();

			} else {
				player.getInterfaceManager().setDefaultRootInterface();
				player.getAppearence().generateAppearenceData();
			}
		}
	}

	public static void setGender(Player player, boolean male) {
		if (male == player.getAppearence().isMale())
			return;
		if (!male)
			player.getAppearence().female();
		else
			player.getAppearence().male();
		Integer index1 = (Integer) player.getTemporaryAttributtes().get(
				"ViewWearDesign");
		Integer index2 = (Integer) player.getTemporaryAttributtes().get(
				"ViewWearDesignD");
		setDesign(player, index1 != null ? index1 : 0, index2 != null ? index2
				: 0);
		player.getAppearence().generateAppearenceData();
		player.getVarsManager().sendVarBit(8093, male ? 0 : 1);
	}

	public static void setDesign(Player player, int index1, int index2) {
		int map1 = ClientScriptMap.getMap(3278).getIntValue(index1);
		if (map1 == 0)
			return;
		boolean male = player.getAppearence().isMale();
		int map2Id = GeneralRequirementMap.getMap(map1).getIntValue(
				(male ? 1169 : 1175) + index2);
		if (map2Id == 0)
			return;
		GeneralRequirementMap map = GeneralRequirementMap.getMap(map2Id);
		for (int i = 1182; i <= 1186; i++) {
			int value = map.getIntValue(i);
			// dont stop at -1 else bugs
			player.getAppearence().setLook(i - 1180, value);
		}
		for (int i = 1187; i <= 1190; i++) {
			int value = map.getIntValue(i);
			// dont stop at -1 else bugs
			player.getAppearence().setColor(i - 1186, value);
		}

	}

	static boolean unnecessary_debug = true;
	static boolean genderMale = true;
	static int skin = 0;

	public static void handleMageMakeOverButtons(Player player, int buttonId) {
		if (buttonId >= 5 && buttonId <= 16) {
			skin = buttonId - 5;
			player.getTemporaryAttributtes().put("MageMakeOverSkin", skin);
		}

		switch (buttonId) {
		case 28:
			genderMale = true;

			break;

		case 29:
			genderMale = false;
			break;

		case 63:
			player.closeInterfaces();
			if (genderMale == player.getAppearence().isMale()
					&& skin == player.getAppearence().getSkinColor()) {
				player.getDialogueManager().startDialogue("MakeOverMage", 2676,
						1);
				return;
			}

			player.getDialogueManager().startDialogue("MakeOverMage", 2676, 2);
			if (player.getAppearence().isMale() != genderMale) {
				if (player.getEquipment().wearingArmour()) {
					player.getDialogueManager()
							.startDialogue("SimpleMessage",
									"You cannot have armor on while changing your gender.");
					return;
				}
				if (genderMale)
					player.getAppearence().male();
				else
					player.getAppearence().female();

			}
			player.getAppearence().setSkinColor(skin);
			player.getAppearence().generateAppearenceData();
			break;
		}

	}

	public static void handleHairdresserSalonButtons(Player player,
			int buttonId, int slotId) {// Hair
		if (buttonId == 6)
			player.getTemporaryAttributtes().put("hairSaloon", true);
		else if (buttonId == 7)
			player.getTemporaryAttributtes().put("hairSaloon", false);
		else if (buttonId == 18) {
			player.closeInterfaces();
		} else if (buttonId == 10) {
			Boolean hairSalon = (Boolean) player.getTemporaryAttributtes().get(
					"hairSaloon");
			if (hairSalon != null && hairSalon) {
				int value = (int) ClientScriptMap.getMap(
						player.getAppearence().isMale() ? 2339 : 2342)
						.getKeyForValue(slotId / 2);
				if (value == -1)
					return;
				player.getAppearence().setHairStyle(value);
			} else if (player.getAppearence().isMale()) {
				int value = ClientScriptMap.getMap(703).getIntValue(slotId / 2);
				if (value == -1)
					return;
				player.getAppearence().setBeardStyle(value);
			}
		} else if (buttonId == 16) {
			int value = ClientScriptMap.getMap(2345).getIntValue(slotId / 2);
			if (value == -1)
				return;
			player.getAppearence().setHairColor(value);
		}
	}

	public static void openMageMakeOver(Player player) {
		player.getInterfaceManager().sendCentralInterface(900);
		player.getPackets().sendIComponentText(900, 33, "Confirm");
		player.getVarsManager().sendVarBit(6098,
				player.getAppearence().isMale() ? 0 : 1);
		player.getVarsManager().sendVarBit(6099,
				player.getAppearence().getSkinColor());
		player.getTemporaryAttributtes().put("MageMakeOverGender",
				player.getAppearence().isMale());
		player.getTemporaryAttributtes().put("MageMakeOverSkin",
				player.getAppearence().getSkinColor());
	}

	public static void handleThessaliasMakeOverButtons(Player player,
			int buttonId, int slotId) {
		player.getAppearence().generateAppearenceData();
		if (buttonId == 6)
			player.getTemporaryAttributtes().put("ThessaliasMakeOver", 0);
		else if (buttonId == 7) {
			if (ClientScriptMap.getMap(
					player.getAppearence().isMale() ? 690 : 1591)
					.getKeyForValue(player.getAppearence().getTopStyle()) >= 32) {
				player.getTemporaryAttributtes().put("ThessaliasMakeOver", 1);
			} else
				player.getPackets().sendGameMessage(
						"You can't select different arms to go with that top.");
		} else if (buttonId == 8) {
			if (ClientScriptMap.getMap(
					player.getAppearence().isMale() ? 690 : 1591)
					.getKeyForValue(player.getAppearence().getTopStyle()) >= 32) {
				player.getTemporaryAttributtes().put("ThessaliasMakeOver", 2);
			} else
				player.getPackets()
						.sendGameMessage(
								"You can't select different wrists to go with that top.");
		} else if (buttonId == 9)
			player.getTemporaryAttributtes().put("ThessaliasMakeOver", 3);
		else if (buttonId == 19) { // confirm
			player.closeInterfaces();
		} else if (buttonId == 12) { // set part
			Integer stage = (Integer) player.getTemporaryAttributtes().get(
					"ThessaliasMakeOver");
			if (stage == null || stage == 0) {
				player.getAppearence().setTopStyle(
						ClientScriptMap.getMap(
								player.getAppearence().isMale() ? 690 : 1591)
								.getIntValue(slotId / 2));
				boolean found = false;
				for (int i = 0; i < SET_COUNT; i++) {
					int[] set = getSet(i);
					if (set[0] == player.getAppearence().getTopStyle()) {
						player.getAppearence().setArmsStyle(set[1]);
						player.getAppearence().setHandsStyle(set[2]);
						found = true;
						break;
					}
				}
				if (!found) {
					player.getAppearence().setArmsStyle(
							player.getAppearence().isMale() ? 26 : 65); // default
					player.getAppearence().setHandsStyle(
							player.getAppearence().isMale() ? 34 : 68); // default
				}
			} else if (stage == 1) // arms
				player.getAppearence().setArmsStyle(
						ClientScriptMap.getMap(
								player.getAppearence().isMale() ? 711 : 693)
								.getIntValue(slotId / 2));
			else if (stage == 2) // wrists
				player.getAppearence().setHandsStyle(
						ClientScriptMap.getMap(751).getIntValue(slotId / 2));
			else
				player.getAppearence().setLegsStyle(
						ClientScriptMap.getMap(
								player.getAppearence().isMale() ? 1586 : 1607)
								.getIntValue(slotId / 2));

		} else if (buttonId == 17) {// color
			Integer stage = (Integer) player.getTemporaryAttributtes().get(
					"ThessaliasMakeOver");
			if (stage == null || stage == 0 || stage == 1)
				player.getAppearence().setTopColor(
						ClientScriptMap.getMap(3282).getIntValue(slotId / 2));
			else if (stage == 3)
				player.getAppearence().setLegsColor(
						ClientScriptMap.getMap(3284).getIntValue(slotId / 2));
		}

	}

	public static void openThessaliasMakeOver(final Player player) {
		openThessaliasMakeOver(player, false);
	}

	public static void openThessaliasMakeOver(final Player player,
			final boolean house) {
		if (player.getEquipment().wearingArmour()) {
			if (house)
				player.getDialogueManager()
						.startDialogue("SimplePlayerMessage",
								"I can't try cloths on while wearing armour. It's a smart idea take it off.");
			else
				player.getDialogueManager()
						.startDialogue(
								"SimpleNPCMessage",
								548,
								"You're not able to try on my clothes with all that armour. Take it off and then speak to me again.");
			return;
		}
		player.setNextAnimation(new Animation(11623));
		player.getInterfaceManager().sendCentralInterface(729);
		player.getPackets().sendIComponentText(729, 21,
				house ? "Change" : "Free!");
		player.getTemporaryAttributtes().put("ThessaliasMakeOver", 0);
		player.getPackets().sendUnlockIComponentOptionSlots(729, 12, 0, 100, 0);
		player.getPackets().sendUnlockIComponentOptionSlots(729, 17, 0,
				ClientScriptMap.getMap(3282).getSize() * 2, 0);
		if (house)
			player.getPackets().sendIComponentText(729, 3, "Dresser");
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				if (!house)
					player.getDialogueManager().startDialogue(
							"SimpleNPCMessage", 548,
							"A marvellous choise. You look splendid!");
				player.setNextAnimation(new Animation(-1));
				player.getAppearence().getAppeareanceData();
				player.getTemporaryAttributtes().remove("ThessaliasMakeOver");
			}

		});
	}

	public static void openHairdresserSalon(final Player player) {
		openHairdresserSalon(player, false);
	}

	public static void openHairdresserSalon(final Player player,
			final boolean house) {
		if (player.getEquipment().getHatId() != -1) {
			if (house)
				player.getDialogueManager()
						.startDialogue("SimplePlayerMessage",
								"I can't see my own hair. I should take my headgear off.");
			else
				player.getDialogueManager()
						.startDialogue(
								"SimpleNPCMessage",
								598,
								"I'm afraid I can't see your head at the moment. Please remove your headgear first.");
			return;
		}
		if (!house
				&& (player.getEquipment().getWeaponId() != -1 || player
						.getEquipment().getShieldId() != -1)) {
			player.getDialogueManager()
					.startDialogue(
							"SimpleNPCMessage",
							598,
							"I don't feel comfortable cutting hair when you are wielding something. Please remove what you are holding first.");
			return;
		}
		player.setNextAnimation(new Animation(11623));
		player.getInterfaceManager().sendCentralInterface(309);
		player.getPackets().sendUnlockIComponentOptionSlots(
				309,
				10,
				0,
				ClientScriptMap.getMap(
						player.getAppearence().isMale() ? 2339 : 2342)
						.getSize() * 2, 0);
		player.getPackets().sendUnlockIComponentOptionSlots(309, 16, 0,
				ClientScriptMap.getMap(2345).getSize() * 2, 0);
		if (house)
			player.getPackets().sendIComponentText(309, 3, "Dresser");
		player.getPackets().sendIComponentText(309, 20,
				house ? "Change" : "Free!");
		player.getTemporaryAttributtes().put("hairSaloon", true);
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				if (!house)
					player.getDialogueManager().startDialogue(
							"SimpleNPCMessage",
							598,
							"An excellent choise, "
									+ (player.getAppearence().isMale() ? "sir"
											: "lady") + ".");
				player.setNextAnimation(new Animation(-1));
				player.getAppearence().getAppeareanceData();
				player.getTemporaryAttributtes().remove("hairSaloon");
			}

		});
	}

	public static void openYrsaShop(final Player player) {
		if (player.getEquipment().getBootsId() != -1) {
			player.getDialogueManager()
					.startDialogue(
							"SimpleNPCMessage",
							1301,
							"I don't feel comfortable helping you try on new boots when you are wearing some already.",
							"Please remove your boots first.");
			return;
		}
		player.setNextAnimation(new Animation(11623));
		player.getInterfaceManager().sendCentralInterface(728);
		player.getPackets().sendIComponentText(728, 16, "Free");
		player.getTemporaryAttributtes().put("YrsaBoot", 0);
		player.getPackets().sendUnlockIComponentOptionSlots(728, 12, 0, 500, 0);
		player.getPackets().sendUnlockIComponentOptionSlots(728, 7, 0,
				ClientScriptMap.getMap(3297).getSize() * 2, 0);
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				player.getDialogueManager().startDialogue("SimpleNPCMessage",
						548, "Hey, They look great!");
				player.setNextAnimation(new Animation(-1));
				player.getAppearence().getAppeareanceData();
				player.getTemporaryAttributtes().remove("YrsaBoot");
			}
		});
	}

	public static void handleYrsaShoes(Player player, int componentId,
			int slotId) {
		if (componentId == 14)
			player.closeInterfaces();
		else if (componentId == 12) {// setting the colors.
			player.getAppearence().setBootsColor(
					ClientScriptMap.getMap(3297).getIntValue(slotId / 2));
			player.getAppearence().generateAppearenceData();
		} else if (componentId == 7) {// /boot style
			player.getAppearence().setBootsStyle(
					ClientScriptMap.getMap(
							player.getAppearence().isMale() ? 3290 : 3293)
							.getIntValue(slotId / 2));
			player.getAppearence().generateAppearenceData();
		}
	}

	private PlayerLook() {

	}
}