package net.kagani.game.player.content;

import net.kagani.cache.loaders.GeneralRequirementMap;
import net.kagani.game.player.Player;

public class LoyaltyProgram {

	public static final int LOYALTY_INTERFACE = 1143;
	private static final int[] REQUIRMENT_SCRIPTS = { 2586 };

	public static void open(Player player) {
		player.getInterfaceManager().setRootInterface(LOYALTY_INTERFACE, false);
		player.getPackets().sendCSVarInteger(1648, player.getLoyaltyPoints());
	}

	public static void handleButtonClick(Player player, int componentId,
			int slot) {
		if (componentId >= 7 && componentId <= 13)// tabs
			sendTab(player, componentId - 7, slot);
	}

	private static void sendTab(Player player, int selectedTab, int slot) {
		GeneralRequirementMap map = GeneralRequirementMap
				.getMap(REQUIRMENT_SCRIPTS[selectedTab]);
		if (map == null)
			return;
		player.getPackets().sendUnlockIComponentOptionSlots(LOYALTY_INTERFACE,
				0, 0, map.getValues().size(), 0, 1);
	}
}