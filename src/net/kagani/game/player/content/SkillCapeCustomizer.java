package net.kagani.game.player.content;

import java.util.Arrays;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.player.Player;

public final class SkillCapeCustomizer {

	private SkillCapeCustomizer() {

	}

	public static void resetSkillCapes(Player player) {
		player.setMaxedCapeCustomized(Arrays.copyOf(
				ItemDefinitions.getItemDefinitions(20767).originalModelColors,
				4));
		player.setCompletionistCapeCustomized(Arrays.copyOf(
				ItemDefinitions.getItemDefinitions(20769).originalModelColors,
				4));
	}

	public static void startCustomizing(Player player, int itemId) {
		player.getTemporaryAttributtes().put("SkillcapeCustomizeId", itemId);
		int[] skillCape = itemId == 20767 || itemId == 32151 ? player
				.getMaxedCapeCustomized() : player
				.getCompletionistCapeCustomized();
		player.getInterfaceManager().sendCentralInterface(20);
		for (int i = 0; i < 4; i++)
			player.getVarsManager().sendVarBit(1039 + i, skillCape[i]);
		player.getPackets().sendIComponentModel(
				20,
				55,
				player.getAppearence().isMale() ? ItemDefinitions
						.getItemDefinitions(itemId).getMaleWornModelId1()
						: ItemDefinitions.getItemDefinitions(itemId)
								.getFemaleWornModelId1());
	}

	public static void costumeColorCustomize(Player player) {
		player.getTemporaryAttributtes().put(Key.COSTUME_COLOR_CUSTOMIZE,
				Boolean.TRUE);
		player.getInterfaceManager().sendCentralInterface(19);
		player.getVarsManager().sendVar(426,
				player.getEquipment().getCostumeColor());
	}

	public static void handleCostumeColor(Player player, int color) {
		player.closeInterfaces();
		player.getEquipment().setCostumeColor(color);
	}

	public static int getCapeId(Player player) {
		Integer id = (Integer) player.getTemporaryAttributtes().get(
				"SkillcapeCustomizeId");
		if (id == null)
			return -1;
		return id;
	}

	public static void handleSkillCapeCustomizerColor(Player player, int colorId) {
		int capeId = getCapeId(player);
		if (capeId == -1)
			return;
		Integer part = (Integer) player.getTemporaryAttributtes().get(
				"SkillcapeCustomize");
		if (part == null)
			return;
		int[] skillCape = capeId == 20767 || capeId == 32151 ? player
				.getMaxedCapeCustomized() : player
				.getCompletionistCapeCustomized();
		skillCape[part] = colorId;
		player.getVarsManager().sendVarBit(1039 + part, colorId);
		player.getInterfaceManager().sendCentralInterface(20);
	}

	public static void handleSkillCapeCustomizer(Player player, int buttonId) {
		int capeId = getCapeId(player);
		if (capeId == -1)
			return;
		int[] skillCape = capeId == 20767 || capeId == 32151 ? player
				.getMaxedCapeCustomized() : player
				.getCompletionistCapeCustomized();
		if (buttonId == 58) { // reset
			if (capeId == 20767 || capeId == 32151)
				player.setMaxedCapeCustomized(Arrays.copyOf(
						ItemDefinitions.getItemDefinitions(capeId).originalModelColors,
						4));
			else
				player.setCompletionistCapeCustomized(Arrays.copyOf(
						ItemDefinitions.getItemDefinitions(capeId).originalModelColors,
						4));
			for (int i = 0; i < 4; i++)
				player.getVarsManager().sendVarBit(1039 + i, skillCape[i]);
		} else if (buttonId == 10) { // detail top
			player.getTemporaryAttributtes().put("SkillcapeCustomize", 0);
			player.getInterfaceManager().sendCentralInterface(19);
			player.getVarsManager().sendVar(426, skillCape[0]);
		} else if (buttonId == 7) { // background top
			player.getTemporaryAttributtes().put("SkillcapeCustomize", 1);
			player.getInterfaceManager().sendCentralInterface(19);
			player.getVarsManager().sendVar(426, skillCape[1]);
		} else if (buttonId == 4) { // detail button
			player.getTemporaryAttributtes().put("SkillcapeCustomize", 2);
			player.getInterfaceManager().sendCentralInterface(19);
			player.getVarsManager().sendVar(426, skillCape[2]);
		} else if (buttonId == 1) { // background button
			player.getTemporaryAttributtes().put("SkillcapeCustomize", 3);
			player.getInterfaceManager().sendCentralInterface(19);
			player.getVarsManager().sendVar(426, skillCape[3]);
		} else if (buttonId == 111 || buttonId == 34) { // done / close
			player.getAppearence().generateAppearenceData();
			player.closeInterfaces();
		}
	}
}