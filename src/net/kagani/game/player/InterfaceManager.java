package net.kagani.game.player;

import java.util.concurrent.ConcurrentHashMap;

import net.kagani.Engine;
import net.kagani.Settings;
import net.kagani.game.player.CosmeticsManager.CosmeticType;
import net.kagani.game.player.content.PlayerLook;
import net.kagani.game.player.content.clans.ClansManager;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class InterfaceManager {

	public static final int FIXED_WINDOW_ID = 1477;
	public static final int RESIZABLE_WINDOW_ID = 1477;
	public static final int BANK_COMPONENT_ID = 388;
	public static final int CENTRAL_INTERFACE_COMPONENT_ID = 416;
	public static final int INVENTORY_INTERFACE_COMPONENT_ID = 421;
	public static final int EDIT_MODE_COMPONENT_ID = 430;
	public static final int DIALOGUE_COMPONENT_ID = 428;
	public static final int INPUT_TEXT_COMPONENT_ID = 427;
	public static final int FADING_COMPONENT_ID = 18;
	public static final int SCREEN_BACKGROUND_COMPONENT_ID = 8;
	public static final int SCREEN_BACKGROUND_INTER_COMPONENT_ID = 410;
	public static final int GAME_SCREEN_COMPONENT_ID = 12;
	public static final int WORLD_MAP_COMPONENT_ID = 13;
	public static final int LEVEL_UP_COMPONENT_ID = 14;
	public static final int CONFIRM_DIALOGUE_COMPONENT_ID = 777;
	public static final int MINIGAME_HUD_COMPONENT_ID = 368; // overlay
	public static final int PLAYER_EXAMINE_COMPONENT_ID = 395;

	public static final int TIMER_COMPONENT_ID = 378;
	public static final int MINIGAME_TAB_COMPONENT_ID = 209; // tab

	public static final int EXPAND_BUTTONS_COMPONENT_ID = 466;

	/*
	 * 0 - skill inter 1 - active task 2 - inventory 3 - equipment 4 - prayer
	 * book 5 - abilities 9 - emotes 14 - friend list 15 - friend chat info 16 -
	 * clan 18 - chat 19 - friend chat
	 */
	public static final int SKILLS_TAB = 0, ACTIVE_TASK_TAB = 1,
			INVENTORY_TAB = 2, EQUIPMENT_TAB = 3, PRAYER_BOOK_TAB = 4,
			MAGIC_ABILITIES_TAB = 5, MELEE_ABILITIES_TAB = 6,
			RANGE_ABILITIES_TAB = 7, DEFENCE_ABILITIES_TAB = 8, EMOTES_TAB = 9,
			NOTES_TAB = 11, SUMMONING_TAB = 12, MINIGAME_TAB = 17,
			ALL_CHAT_TAB = 18;

	private static final int[] MENU_SLOT_COMPONENTS_ = { 3, 5, 7, 9 };

	private static final int[] MENU_SUBMENU_VARS = { 18995, 18996, 18997,
			18998, 18999, 19000, 19002, 19003, 19001 };

	private Player player;

	private final ConcurrentHashMap<Integer, Integer> openedinterfaces = new ConcurrentHashMap<Integer, Integer>();

	private boolean resizableScreen;
	private int rootInterface;
	private int currentMenu;

	public InterfaceManager(Player player) {
		this.player = player;
		currentMenu = -1;
	}

	public void sendWorldMapInterface(int id) {
		setWindowInterface(WORLD_MAP_COMPONENT_ID, id);
	}

	public void sendGameMapInterface(int id) {
		setWindowInterface(GAME_SCREEN_COMPONENT_ID, id);
	}

	public boolean containsGameMapInterface() {
		return containsWindowInterfaceAtParent(GAME_SCREEN_COMPONENT_ID);
	}

	public void sendExpandOptionsInterface(int id) {
		setWindowInterface(EXPAND_BUTTONS_COMPONENT_ID, id);
	}

	public void sendExpandOptionsInterface(int id, int interfaceId,
			int componentId, int width, int height) {
		setWindowInterface(EXPAND_BUTTONS_COMPONENT_ID, id);
		// width height parentuid borderx, bordery(always -1, 2) x y(always -24,
		// -6).
		player.getPackets().sendExecuteScript(8787, width, height,
				InterfaceManager.getComponentUId(interfaceId, componentId), -1,
				2, -24, -6);
	}

	public void sendTimerInterface() {
		setWindowInterface(TIMER_COMPONENT_ID, 1508);
	}

	public void removeTimerInterface() {
		removeWindowInterface(TIMER_COMPONENT_ID);
	}

	public void sendGameMapInterface() {
		sendGameMapInterface(1482);
	}

	public boolean containsWorldMapInterface() {
		return containsWindowInterfaceAtParent(WORLD_MAP_COMPONENT_ID);
	}

	public void removeWorldMapInterface() {
		removeWindowInterface(WORLD_MAP_COMPONENT_ID);
		sendGameMapInterface();
		refreshInterface(true);
	}

	public void setWindowInterface(int componentId, int interfaceId) {
		setWindowInterface(true, componentId, interfaceId);
	}

	public void setWindowInterface(boolean walkable, int componentId,
			int interfaceId) {
		setInterface(walkable, resizableScreen ? RESIZABLE_WINDOW_ID
				: FIXED_WINDOW_ID, componentId, interfaceId);
	}

	public void removeWindowInterface(int componentId) {
		removeInterfaceByParent(FIXED_WINDOW_ID, componentId);
	}

	public void sendDialogueInterface(int interfaceId) {
		setInterface(false, 1477, DIALOGUE_COMPONENT_ID, interfaceId);
		refreshInterface(false);
	}

	public void removeDialogueInterface() {
		removeWindowInterface(DIALOGUE_COMPONENT_ID);
		refreshInterface(true);
	}

	public boolean containsDialogueInterface() {
		return containsInterfaceAtParent(FIXED_WINDOW_ID, DIALOGUE_COMPONENT_ID);
	}

	public void sendMinigameInterface(int interfaceId) {
		setWindowInterface(MINIGAME_HUD_COMPONENT_ID, interfaceId);
	}

	public void removeMinigameInterface() {
		removeWindowInterface(MINIGAME_HUD_COMPONENT_ID);
	}

	public void sendTreasureHunter() {
		setWindowInterface(368, 1252);
	}

	public void sendInputTextInterface() {
		sendInputTextInterface(1469);
	}

	public void sendInputTextInterface(int interfaceId) {
		setWindowInterface(INPUT_TEXT_COMPONENT_ID, 1418);
		setInterface(true, 1418, 0, interfaceId);
		refreshInterface(false);
	}

	public boolean containsInputTextInterface() {
		return containsWindowInterfaceAtParent(INPUT_TEXT_COMPONENT_ID);
	}

	public void removeInputTextInterface() {
		removeWindowInterface(INPUT_TEXT_COMPONENT_ID);
		refreshInterface(true);
	}

	public void closeTreasureHunter() {
		removeWindowInterface(MINIGAME_HUD_COMPONENT_ID);
		player.getPackets()
				.sendGameMessage(
						"You can access the Treasure Hunter from the side panel, and you can show the button again by logging out and back in.",
						true);
	}

	public void sendCentralInterface(int interfaceId) {
		setInterface(false, resizableScreen ? RESIZABLE_WINDOW_ID
				: FIXED_WINDOW_ID, CENTRAL_INTERFACE_COMPONENT_ID, interfaceId);
		refreshInterface(false);
	}

	public boolean containsScreenInterface() {
		return containsWindowInterfaceAtParent(CENTRAL_INTERFACE_COMPONENT_ID);
	}

	public void removeCentralInterface() {
		removeWindowInterface(CENTRAL_INTERFACE_COMPONENT_ID);
		refreshInterface(true);
	}

	public void refreshInterface(boolean closing) {
		if (!player.getCutscenesManager().hasCutscene())
			player.getPackets().sendExecuteScript(closing ? 8779 : 8697);
	}

	public boolean containsBankInterface() {
		return containsWindowInterfaceAtParent(BANK_COMPONENT_ID);
	}

	public boolean containsTreasureHunterInterface() {
		if (player.getInterfaceManager().containsInterface(1253))
			return true;
		return false;
	}

	public void removeBankInterface() {
		removeWindowInterface(BANK_COMPONENT_ID);
		sendLockGameTab(InterfaceManager.INVENTORY_TAB, false); // unlocks
		// inventory
		sendLockGameTab(InterfaceManager.EQUIPMENT_TAB, false); // unlocks
		// equipment
	}

	public void sendBankInterface(int interfaceId) {
		setInterface(false, resizableScreen ? RESIZABLE_WINDOW_ID
				: FIXED_WINDOW_ID, BANK_COMPONENT_ID, interfaceId);
		sendLockGameTab(InterfaceManager.INVENTORY_TAB, true); // locks
		// inventory
		sendLockGameTab(InterfaceManager.BANK_COMPONENT_ID, true); // locks
		// equipment
		refreshInterface(false);
	}

	public void sendInventoryInterface(int interfaceId) {

		setInterface(false, FIXED_WINDOW_ID, INVENTORY_INTERFACE_COMPONENT_ID,
				interfaceId);
		sendLockGameTab(InterfaceManager.INVENTORY_TAB, true); // locks
		// inventory
		refreshInterface(false);
	}

	public void removeInventoryInterface() {
		removeWindowInterface(INVENTORY_INTERFACE_COMPONENT_ID);
		sendLockGameTab(InterfaceManager.INVENTORY_TAB, false); // unlocks
		// inventory
		refreshInterface(true);
	}

	/*
	 * rs calls it so
	 */
	public void setFamiliarInterface(int interfaceId) {
		setWindowInterface(143, interfaceId);
		player.getInterfaceManager().sendLockGameTab(
				InterfaceManager.SUMMONING_TAB, false);
	}

	public void removeFamiliarInterface() {
		removeWindowInterface(143);
		player.getInterfaceManager().sendLockGameTab(
				InterfaceManager.SUMMONING_TAB, true);
	}

	public final void sendInterfaces() {
		if (player.isLobby()) {
			if (player.getLoginCount() == 0) {
				PlayerLook.openCharacterCustomizing(player);
			} else
				sendLobbyInterfaces();
		} else {
			sendNISScreenInterfaces();
			lockDefaultTabs();
			unlockCustomizationSwitchButton();
			unlockMenu();
			unlockOptionMenu();
			unlockLogout();
			unlockScreen();
			customTab();
			openGameTab(2);
			if (player.getSquealOfFortune().getAllKeys() > 0)
				sendTreasureHunter();
			player.getCombatDefinitions().sendAbilityVars();
			player.getActionbar().unlockActionBar(false);
			player.getCombatDefinitions().unlockSheatheButton();
			player.getEmotesManager().unlockEmotesBook();
			player.getPrayer().unlockPrayerBookButtons();
			player.getMusicsManager().unlockMusicPlayer(false);
			player.getNotes().unlockNotes(false);
			player.getSkills().unlockSkills(false);
			player.getInventory().unlockInventory(false);
			player.getEquipment().unlockEquipment(false);
			player.getFriendsIgnores().unlockFriendsIgnore(false);
			ClansManager.unlockClanBanList(player, false);

			// send familiar details if has familiar
			if (player.getFamiliar() != null && player.isRunning())
				player.getFamiliar().sendFollowerDetails();

			player.getTimersManager().sendInterfaces();
			player.getControlerManager().sendInterfaces();
		}
	}

	private void customTab() {
		long ticks = Engine.currentTime - Utils.currentTimeMillis();
		int minutes = Math.abs((int) ((ticks / (1000 * 60)) % 60));
		int hours = Math.abs((int) ((ticks / (1000 * 60 * 60)) % 24));
		int days = Math.abs((int) ((ticks / (1000 * 60 * 60 * 60)) % 24));

		player.getPackets().sendIComponentText(506, 0, "Control Center");
		player.getPackets().sendIComponentText(506, 2, "Character Settings");
		player.getPackets().sendIComponentText(506, 4, "");
		player.getPackets().sendIComponentText(506, 8, "Claim Vote");
		player.getPackets().sendIComponentText(506, 12, "Claim Store");
		player.getPackets().sendIComponentText(506, 6, "Visit Website");
		player.getPackets().sendIComponentText(506, 10, "Vist Forums");
		player.getPackets().sendIComponentText(506, 14, "Visit Store");

		player.getPackets().sendIComponentText(930, 10, Settings.SERVER_NAME);
		player.getPackets().sendIComponentText(
				930,
				16,
				"<col=FF0000>Uptime:</col> <<col=FFFFFF>"
						+ (days + " d, " + hours + " h, " + minutes + " mins")
						+ "<br>"
						+ ""
						+ "<col=FF0000>Playtime:</col> <col=FFFFFF>"
						+ (player.days + " d, " + player.hours + " h, "
								+ player.minutes + " mins")
						+ "<br><col=FF0000>Vote points:</col> <col=FFFFFF>"
						+ player.getVoteCount() + "<br>");
	}

	/*
	 * those are locked by default. ^^. until inter added
	 */
	private void lockDefaultTabs() {
		player.getInterfaceManager().sendLockGameTab(
				InterfaceManager.SUMMONING_TAB, true);
		player.getInterfaceManager().sendLockGameTab(
				InterfaceManager.MINIGAME_TAB, true);
	}

	// new way of doing menus
	public void unlockOptionMenu() {
		player.getPackets().sendIComponentSettings(1477, 791, 0, 1000, 2);
	}

	public void sendLobbyInterfaces() {
		setRootInterface(906, false);
		setInterface(true, 906, 158, 907);
		setInterface(true, 906, 160, 910);
		setInterface(true, 906, 161, 909);
		setInterface(true, 906, 163, 912);
		setInterface(true, 906, 162, 589);
		setInterface(true, 906, 164, 911);
		setInterface(true, 906, 87, 914);
		setInterface(true, 906, 79, 915);
		setInterface(true, 906, 71, 913);
		setInterface(true, 907, 31, 908);
	}

	public void unlockScreen() {
		player.getPackets().sendIComponentSettings(1477, 18, -1, -1, 2097152); // todo
	}

	public void unlockMenu() {
		player.getPackets().sendUnlockIComponentOptionSlots(1477, 402, 0, 100,
				0); // unlocks
		// menu
		// switching
		player.getPackets().sendUnlockIComponentOptionSlots(1477, 405, 0, 1, 0); // unlocks
		// close
		// menu
		// button
	}

	public void unlockLogout() {
		player.getPackets().sendUnlockIComponentOptionSlots(1477, 42, 0, 1, 0);
	}

	public void unlockCustomizationSwitchButton() {
		player.getPackets().sendIComponentSettings(1477, 30, 1, 1, 2);
	}

	public void setDefaultRootInterface() {
		setRootInterface(FIXED_WINDOW_ID, false);
	}

	public void sendMagicAbilities() {
		sendMagicAbilities(1461);
		player.getCombatDefinitions().unlockMagicAbilities();
	}

	public void sendMagicAbilities(int interfaceId) {
		setWindowInterface(187, interfaceId); // mage spellbook
	}

	public void sendMeleeAbilities() {
		boolean legacyMode = player.getCombatDefinitions().getCombatMode() == CombatDefinitions.LEGACY_COMBAT_MODE;
		setWindowInterface(165, legacyMode ? 1503 : 1460);
		if (!legacyMode)
			player.getCombatDefinitions().unlockMeleeAbilities();
	}

	public void sendRangedAbilities() {
		setWindowInterface(176, 1452); // range abilities
		player.getCombatDefinitions().unlockRangeAbilities();
	}

	public void sendDefenceAbilities() {
		setWindowInterface(198, 1449); // defence abilities
		player.getCombatDefinitions().unlockDefenceAbilities();
	}

	public void sendNISScreenInterfaces() {
		setDefaultRootInterface();
		sendGameMapInterface();// Game Map
		sendMagicAbilities();
		sendMeleeAbilities();
		sendRangedAbilities();
		sendDefenceAbilities();
		sendEpochTimer();
		setWindowInterface(219, 1466);
		setWindowInterface(230, 506);
		setWindowInterface(57, 1473);
		setWindowInterface(132, 1464);
		setWindowInterface(154, 1458);
		setWindowInterface(252, 550);
		setWindowInterface(296, 1427);
		setWindowInterface(263, 1110);
		setWindowInterface(46, 590);
		setWindowInterface(241, 1416);
		setWindowInterface(285, 1417);
		setWindowInterface(307, 231);
		setWindowInterface(274, 930);
		setWindowInterface(29, 1431);
		setWindowInterface(386, 568);
		setWindowInterface(35, 1430);
		setWindowInterface(39, 1465);
		setInterface(true, 1465, 10, 1504);
		setInterface(true, 1465, 12, 1506);
		setInterface(true, 1465, 11, 1505);
		setWindowInterface(486, 1433);
		setWindowInterface(349, 1483);
		setWindowInterface(366, 745);
		setWindowInterface(345, 1485);
		setWindowInterface(471, 1213);
		setWindowInterface(403, 1448);
		setWindowInterface(337, 557);
		setWindowInterface(481, 1484);
		setWindowInterface(68, 137);
		setWindowInterface(78, 1467);
		setWindowInterface(87, 1472);
		setWindowInterface(96, 1471);
		setWindowInterface(105, 1470);
		setWindowInterface(114, 464);
		setWindowInterface(318, 228);
		setWindowInterface(123, 1529);
		setWindowInterface(374, 182);
		setWindowInterface(475, 1488);
		setWindowInterface(333, 1215); // xp counter

	}

	private void sendEpochTimer() {
		setWindowInterface(382, 635); // time
		player.getPackets().sendExecuteScript(7486,
				(int) (Utils.currentTimeMillis() / 60000), (635 << 16) + 1);
	}

	public void sendConfirmDialogue() {
		setWindowInterface(false, CONFIRM_DIALOGUE_COMPONENT_ID, 26);
	}

	public void closeConfirmDialogue() {
		removeWindowInterface(CONFIRM_DIALOGUE_COMPONENT_ID);
	}

	public void sendLockGameTab(int tab, boolean lock) {
		player.getPackets().sendExecuteScriptReverse(8862, lock ? 0 : 1, tab);
	}

	public void sendMinigameTab(int interfaceId) {
		setWindowInterface(MINIGAME_TAB_COMPONENT_ID, interfaceId);
		sendLockGameTab(MINIGAME_TAB, false);
	}

	/*
	 * used for instance for clan citadel
	 */
	public void closeMinigameTab() {
		removeWindowInterface(MINIGAME_TAB_COMPONENT_ID);
		sendLockGameTab(MINIGAME_TAB, true);
	}

	public void sendSettings() {
		sendSettings(261);
	}

	/*
	 * outdated from rs2 inters pretty much
	 */
	public void sendSettings(int interfaceId) {
		setWindowInterface(resizableScreen ? 123 : 183, interfaceId);
	}

	public void sendMagicBook() {

	}

	public void setInterface(boolean clickThrought, int parentInterfaceId,
			int parentInterfaceComponentId, int interfaceId) {
		if (Settings.DEBUG) {
			if (parentInterfaceId != rootInterface
					&& !containsInterface(parentInterfaceId))
				System.out
						.println("The parent interface isnt setted so where are u trying to set it? "
								+ parentInterfaceId
								+ ", "
								+ parentInterfaceComponentId
								+ ", "
								+ interfaceId);
			/*
			 * if(containsInterface(interfaceId)) System.out.println(
			 * "Already have "+interfaceId+" in another component.");
			 */
		}
		// even so lets set it for now
		int parentUID = getComponentUId(parentInterfaceId,
				parentInterfaceComponentId);
		int oldParentUID = getInterfaceParentId(interfaceId);

		Integer oldInterface = openedinterfaces.get(parentUID);
		if (oldInterface != null)
			clearChilds(oldInterface);

		openedinterfaces.put(parentUID, interfaceId); // replaces inter if
		// theres one in that
		// component already
		if (oldParentUID != -1 && oldParentUID != parentUID) {
			openedinterfaces.remove(oldParentUID, interfaceId);
			player.getPackets().moveInterface(oldParentUID, parentUID);
		} else
			player.getPackets().sendInterface(clickThrought, parentUID,
					interfaceId);
	}

	public void removeInterfaceByParent(int parentInterfaceId,
			int parentInterfaceComponentId) {
		removeInterfaceByParent(getComponentUId(parentInterfaceId,
				parentInterfaceComponentId));
	}

	public void removeInterfaceByParent(int parentUID) {
		Integer removedInterface = openedinterfaces.remove(parentUID);
		if (removedInterface != null) {
			clearChilds(removedInterface);
			player.getPackets().closeInterface(parentUID);
		}
	}

	private void clearChilds(int parentInterfaceId) {
		for (int key : openedinterfaces.keySet()) {
			if (key >> 16 == parentInterfaceId)
				openedinterfaces.remove(key);
		}
	}

	public void removeInterface(int interfaceId) {
		int parentUID = getInterfaceParentId(interfaceId);
		if (parentUID == -1)
			return;
		removeInterfaceByParent(parentUID);
	}

	public void setRootInterface(int rootInterface, boolean gc) {
		this.rootInterface = rootInterface;
		player.getPackets().sendRootInterface(rootInterface, gc ? 3 : 0);
	}

	public static int getComponentUId(int interfaceId, int componentId) {
		return interfaceId << 16 | componentId;
	}

	public int getInterfaceParentId(int interfaceId) {
		if (interfaceId == rootInterface)
			return -1;
		for (int key : openedinterfaces.keySet()) {
			int value = openedinterfaces.get(key);
			if (value == interfaceId)
				return key;
		}
		return -1;
	}

	public boolean containsInterfaceAtParent(int parentInterfaceId,
			int parentInterfaceComponentId) {
		return openedinterfaces.containsKey(getComponentUId(parentInterfaceId,
				parentInterfaceComponentId));
	}

	public boolean containsInterface(int interfaceId) {
		if (interfaceId == rootInterface)
			return true;
		for (int value : openedinterfaces.values())
			if (value == interfaceId)
				return true;
		return false;
	}

	public void removeAll() {
		openedinterfaces.clear();
	}

	public boolean containsWindowInterfaceAtParent(int componentId) {
		return containsInterfaceAtParent(resizableScreen ? RESIZABLE_WINDOW_ID
				: FIXED_WINDOW_ID, componentId);
	}

	public boolean containsInventoryInter() {
		return containsWindowInterfaceAtParent(INVENTORY_INTERFACE_COMPONENT_ID);
	}

	public void sendFadingInterface(int backgroundInterface) {
		setWindowInterface(FADING_COMPONENT_ID, backgroundInterface);
	}

	public void removeFadingInterface() {
		removeWindowInterface(FADING_COMPONENT_ID);
	}

	public void setScreenInterface(int backgroundInterface, int interfaceId) {
		setScreenInterface(true, backgroundInterface, interfaceId);
	}

	public void setScreenInterface(boolean walkable, int backgroundInterface,
			int interfaceId) {
		removeCentralInterface();
		setWindowInterface(walkable, SCREEN_BACKGROUND_COMPONENT_ID,
				backgroundInterface);
		setWindowInterface(walkable, SCREEN_BACKGROUND_INTER_COMPONENT_ID,
				interfaceId);

		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				removeScreenInterfaceBG();
			}
		});
	}

	public void setFairyRingInterface(boolean walkable, int interfaceId) {
		removeCentralInterface();
		setWindowInterface(walkable, SCREEN_BACKGROUND_INTER_COMPONENT_ID,
				interfaceId);

		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				removeScreenInterfaceBG();
			}
		});
	}

	public void removeScreenInterfaceBG() {
		removeWindowInterface(SCREEN_BACKGROUND_COMPONENT_ID);
		removeWindowInterface(SCREEN_BACKGROUND_INTER_COMPONENT_ID);
	}

	public boolean hasRezizableScreen() {
		return resizableScreen;
	}

	public void setWindowsPane(int windowsPane) {
		this.rootInterface = windowsPane;
	}

	public int getWindowsPane() {
		return rootInterface;
	}

	public void openEditMode() {
		player.stopAll();
		setWindowInterface(InterfaceManager.EDIT_MODE_COMPONENT_ID, 1475); // Edit
		// menu
		setEditMode(true);
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				removeWindowInterface(InterfaceManager.EDIT_MODE_COMPONENT_ID);
				setEditMode(false);
			}
		});
	}

	private void setEditMode(boolean editMode) {
		player.getPackets().sendCSVarInteger(3477, editMode ? 1 : 0);
	}

	public void gazeOrbOfOculus() {
		player.stopAll();
		setFairyRingInterface(true, 475);
		player.getPackets().sendGameMessage(
				"The Orb of Oculus is not working correctly at the moment.");
	}

	/*
	 * returns lastGameTab
	 */
	public void openGameTab(int tabId) {
		player.getPackets().sendExecuteScript(8310, tabId);
	}

	public void switchMenu(int subMenu) {
		if (!isMenuOpen())
			return;
		openMenu(currentMenu, subMenu);
	}

	public void openExtras() {
		if (player.isUnderCombat()) {
			player.getPackets().sendGameMessage(
					"You can't do that while in combat.");
			return;
		}
		sendCentralInterface(1139);
		player.getPackets().sendIComponentText(1139, 19,
				player.isAMember() ? player.getMemberTitle() : "Not a member");
		player.getPackets().sendIComponentText(1139, 39,
				"" + player.getInventory().getAmountOf(29492));
		player.getPackets().sendIComponentText(1139, 8,
				"" + player.getSquealOfFortune().getAllKeys());
		player.getPackets().sendIComponentText(1139, 9, "0");
	}

	public void openRibbonSetup() {
		if (player.isUnderCombat()) {
			player.getPackets().sendGameMessage(
					"You can't do that while in combat.");
			return;
		}
		sendCentralInterface(567);
	}

	public void openRS3Helper() {
		if (player.isUnderCombat()) {
			player.getPackets().sendGameMessage(
					"You can't do that while in combat.");
			return;
		}
		sendCentralInterface(1496);
	}

	public void openMenu(int menu, int subMenu) {
		if (menu == currentMenu && player.getSubMenus()[menu] + 1 == subMenu) {
			closeMenu();
			return;
		}
		if (player.isLocked()) {
			closeMenu();
			return;
		}
		if (player.isUnderCombat()) {
			closeMenu();
			player.getPackets().sendGameMessage(
					"You can't do that while in combat.");
			return;
		}
		setMenu(menu);
		player.getSubMenus()[menu] = subMenu - 1;

		player.getVarsManager().forceSendVarBit(18994, menu);
		player.getVarsManager().forceSendVarBit(MENU_SUBMENU_VARS[menu],
				subMenu);

		if (Settings.DEBUG)
			Logger.log(InterfaceManager.class, "Menu: " + menu + ", " + subMenu);

		if (menu == 0) { // hero
			if (subMenu == 1) { // Summary
				setMenuInterface(0, 320);
				setMenuInterface(1, 1446);
				player.getPackets().sendIComponentText(1446, 94,
						player.getDisplayName());
				player.getSkills().unlockSkills(true);
			} else if (subMenu == 2) { // Skills
				setMenuInterface(0, 1218);
				setInterface(true, 1218, 58, 1217);
				// sendCSVarInteger(1753, 8);?
			} else if (subMenu == 3) { // Pets
				setMenuInterface(0, 1311);
				player.getCosmeticsManager().open(CosmeticType.PET);
				// sendCSVarInteger(2017, 12);
				// sendCSVarInteger(2018, 0);
				/*
				 * Runscripts: [7422, -1, -1, 0, 0, 0] Runscripts: [7425, ]
				 * sendCSVarInteger(2699, -1); Runscripts: [6874]
				 */
			} else if (subMenu == 4) { // Achievements
				setMenuInterface(0, 917);
				setMenuInterface(1, 1056);
				// ndCSVarInteger(1423, 63);
				// sendCSVarInteger(1424, 57);
				/*
				 * sendCSVarInteger(2017, 12); sendCSVarInteger(2018, 0);
				 * sendCSVarInteger(1963, -1); sendCSVarInteger(1964, -1);
				 * sendCSVarInteger(1966, -1); sendCSVarInteger(1965, -1);
				 * Runscripts: [8862, 2, 1] Runscripts: [8862, 3, 1]
				 */

			}
		} else if (menu == 1) { // gear
			if (subMenu == 1) { // loadout
				setMenuInterface(0, 1474);
				setMenuInterface(1, 1463);
				setMenuInterface(2, 1462);
				player.getPackets().sendIComponentText(1463, 11,
						player.getDisplayName());
				player.getInventory().unlockInventory(true);
				player.getEquipment().unlockEquipment(true);
				player.getEquipment().refreshEquipmentInterfaceBonuses();

			} else if (subMenu == 2) { // wardrobe
				setMenuInterface(0, 1311);
				player.getCosmeticsManager().open(CosmeticType.WARDROBE);
				/*
				 * Runscripts: [8862, 2, 0] Runscripts: [8862, 3, 0]
				 * sendCSVarInteger(2017, 12); sendCSVarInteger(2018, 0);
				 * sendCSVarInteger(2699, -1); Runscripts: [6874]
				 */
			} else if (subMenu == 3) { // titles
				setMenuInterface(0, 1311);
				player.getCosmeticsManager().open(CosmeticType.TITLE);
			} else if (subMenu == 4) { // animations
				setMenuInterface(0, 1311);
				player.getCosmeticsManager().open(CosmeticType.ANIMATION);

				/*
				 * Runscripts: [8862, 2, 0] Runscripts: [8862, 3, 0] Runscripts:
				 * [2716, -1] Runscripts: [6453, , 1] sendCSVarInteger(2017,
				 * 12); sendCSVarInteger(2018, 0); sendCSVarInteger(2017, 12);
				 * sendCSVarInteger(2018, 0); sendCSVarInteger(2699, -1);
				 * Runscripts: [6874]
				 */
			} else if (subMenu == 5) { // appearence
				setMenuInterface(0, 1311);
				player.getCosmeticsManager().open(CosmeticType.APPEARENCE);
			} else if (subMenu == 6) { // presets
				setMenuInterface(0, 579);
				setMenuInterface(1, 577);
				setMenuInterface(2, 627);
				// Runscripts: [9916]
				/*
				 * sendCSVarInteger(3838, 0); sendCSVarInteger(3840, 0);
				 * sendCSVarInteger(3842, -1); sendCSVarInteger(2017, 12);
				 * sendCSVarInteger(2018, 0); sendCSVarInteger(1963, -1);
				 * sendCSVarInteger(1964, -1); sendCSVarInteger(1966, -1);
				 * sendCSVarInteger(1965, -1); Runscripts: [8862, 2, 1]
				 * Runscripts: [8862, 3, 1]
				 */
			}
		} else if (menu == 2) { // powers
			if (subMenu == 1) { // melee
				setMenuInterface(0, 1450);
				setMenuInterface(1, 1454);
				setMenuInterface(2, 1435);
				setMenuInterface(3, 1436);
				player.getActionbar().unlockActionBar(true);
			} else if (subMenu == 2) { // ranged
				setMenuInterface(0, 1456);
				setMenuInterface(1, 1454);
				setMenuInterface(2, 1445);
				setMenuInterface(3, 1436);
				player.getActionbar().unlockActionBar(true);
			} else if (subMenu == 3) { // magic
				setMenuInterface(0, 1459);
				setMenuInterface(1, 1454);
				setMenuInterface(2, 1437);
				setMenuInterface(3, 1436);
				player.getActionbar().unlockActionBar(true);
			} else if (subMenu == 4) { // defensive
				setWindowInterface(292, 1215);
				setMenuInterface(0, 1453);
				setMenuInterface(0, 1453);
				setMenuInterface(1, 1454);
				setMenuInterface(2, 1434);
				setMenuInterface(3, 1436);
				player.getActionbar().unlockActionBar(true);
			} else if (subMenu == 5) { // prayers
				setMenuInterface(0, 1457);
				setMenuInterface(1, 1454);
				setMenuInterface(2, 1439);
				setMenuInterface(3, 1436);
				player.getActionbar().unlockActionBar(true);
			} else if (subMenu == 6) { // combat settings
				/*
				 * sendCSVarInteger(1951, -1); sendCSVarInteger(1952, -1);
				 * Runscripts: [8194, 2, 6] sendCSVarInteger(2911, 2);
				 */
				setMenuInterface(0, 327);
				setMenuInterface(1, 1436);
				player.getActionbar().unlockActionBar(true);
			}
		} else if (menu == 3) { // adventures
			if (subMenu == 1) { // latest content
				setMenuInterface(0, 1345);
				// varbit 16570 - selects the quest
			} else if (subMenu == 2) { // quests
				/*
				 * Runscripts: [8194, 3, 2] sendCSVarInteger(2911, 3);
				 */
				setMenuInterface(0, 190);
				setMenuInterface(1, 1500);
			} else if (subMenu == 3) { // challenges
				setMenuInterface(0, 1343);
				setMenuInterface(1, 1056);
				// Runscripts: [4507, 22, 88014911, 88014900, 88014879]
			} else if (subMenu == 4) { // minigames
				setMenuInterface(0, 1344);
				// Runscripts: [4507, 0, 88080426, 88080415, 88080391]
			} else if (subMenu == 5) { // path
				setMenuInterface(0, 639);
				setMenuInterface(1, 400);
			} else if (subMenu == 6) {// beasts
				setMenuInterface(0, 753);
				player.getPackets().sendIComponentSettings(753, 71, 0, 2, 2);
				player.getPackets().sendIComponentSettings(753, 46, 0, 40, 2);
				player.getTimersManager().setBeastMenu(0);
			}
		} else if (menu == 4) { // community
			if (subMenu == 1) { // vote now
				/*
				 * Runscripts: [8194, 4, 1] sendCSVarInteger(2911, 4);
				 */
				setMenuInterface(0, 1029);
				/*
				 * Runscripts: [1665, 0] Runscripts: [1665, 1] Runscripts:
				 * [1665, 2] Runscripts: [9598, 0]
				 */
			} else if (subMenu == 3) { // chat settings
				setMenuInterface(0, 1440);
				setMenuInterface(1, 1109);
				setMenuInterface(2, 982);
				setMenuInterface(3, 1441);
				player.getFriendsIgnores().unlockFriendsIgnore(true);
				player.getPackets().sendIComponentSettings(982, 7, 0, 19, 2); // chat
				// color
				ClansManager.unlockClanBanList(player, true);
			} else if (subMenu == 4) { // grouping system
				setMenuInterface(0, 1524);
				setMenuInterface(1, 1528);
			} else if (subMenu == 5) { // twitch
				setMenuInterface(0, 232);
			}
		} else if (menu == 8) { // settings
			if (subMenu == 1) { // game settings
				setMenuInterface(0, 34);
				setMenuInterface(1, 1443);
				player.getNotes().unlockNotes(true);
			} else if (subMenu == 2) { // interface
				setMenuInterface(0, 1442);
				setMenuInterface(1, 1214);
				// sendCSVarInteger(944, 22);?
				player.getPackets().sendIComponentSettings(1442, 130, 0, 2, 2); // unlocks
				// close
				// menu
				// button
			} else if (subMenu == 3) { // controls
				setMenuInterface(0, 1444);
			} else if (subMenu == 4) { // graphic settings
				setMenuInterface(0, 1426);
				setInterface(true, 1426, 0, 742);
			} else if (subMenu == 5) { // audio settings
				setMenuInterface(0, 187);
				setMenuInterface(1, 429);
				setWindowInterface(292, 1215);
				player.getMusicsManager().refreshMusicInterface(true);
				player.getMusicsManager().unlockMusicPlayer(true);
			}
		}

	}

	private void setMenuInterface(int slot, int id) {
		setInterface(true, 1448, MENU_SLOT_COMPONENTS_[slot], id);
		player.getPackets().sendHideIComponent(1448,
				MENU_SLOT_COMPONENTS_[slot], false);
	}

	public boolean isMenuOpen() {
		return currentMenu != -1;
	}

	public void closeMenu() {
		setMenu(-1);
	}

	private void setMenu(int menu) {
		currentMenu = menu;
		if (player.getVarsManager().getBitValue(
				CosmeticsManager.COSMETIC_TYPE_MENU_VARBIT) != 0)
			player.getCosmeticsManager().close();
		player.getPackets().sendCSVarInteger(2911, menu);
		for (int slot : MENU_SLOT_COMPONENTS_)
			if (containsInterfaceAtParent(1448, slot))
				removeInterfaceByParent(1448, slot);
		player.getPackets().sendHideIComponent(1448, 1, menu != -1);

	}

	public static int getNextStatus(int status) {
		return status == 2 ? 0 : (status + 1);
	}

	public void sendCustom(Player player) {
		player.getPackets().sendUnlockIComponentOptionSlots(190, 15, 0, 201, 0,
				1, 2, 3);
		player.getPackets().sendConfig(31, 10);
		player.getPackets().sendConfig(160, 1);
		player.getPackets().sendIComponentText(
				190,
				27,
				"Quest Points: " + player.getQuestManager().getQuestPoints()
						+ "/10");
	}
}