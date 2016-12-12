package net.kagani.game.player.content;

import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.impl.skills.ChooseAToolD;

//TODO convert all to new. for now temporarly fix
public final class SkillsDialogueOld {

	public static final int MAKE = 0, MAKE_SETS = 1, COOK = 2, ROAST = 3,
			OFFER = 4, SELL = 5, BAKE = 6, CUT = 7, DEPOSIT = 8,
			MAKE_NO_ALL_NO_CUSTOM = 9, TELEPORT = 10, SELECT = 11, TAKE = 13;

	public static interface ItemNameFilter {

		public String rename(String name);
	}

	public static void sendSkillsDialogue(Player player, int option,
			String explanation, int maxQuantity, int[] items,
			ItemNameFilter filter) {
		sendSkillsDialogue(player, option, explanation, maxQuantity, items,
				filter, true);
	}

	public static void sendSkillsDialogue(Player player, int option,
			String explanation, int maxQuantity, int[] items,
			ItemNameFilter filter, boolean sendQuantitySelector) {
		// player.getPackets().sendGameMessage("This feature hasn't been converted yet.");
		for (int i = 0; i < 15; i++)
			player.getPackets().sendCSVarInteger(1703 + i,
					i >= items.length ? -1 : items[i]);
		player.getPackets().sendIComponentText(1179, 0, explanation);
		player.getInterfaceManager().sendCentralInterface(1179);

	}

	public static int getMaxQuantity(Player player) {
		return 28;
	}

	public static int getQuantity(Player player) {
		player.getInterfaceManager().removeCentralInterface(); // temporarly fix
		// for skills
		// until all them
		// converted
		return 28;
	}

	public static int getItemSlot(int componentId) {
		return ChooseAToolD.getOptionSlot(componentId);
	}

	private SkillsDialogueOld() {

	}
}