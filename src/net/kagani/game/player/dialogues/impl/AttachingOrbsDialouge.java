package net.kagani.game.player.dialogues.impl;

import net.kagani.game.Animation;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Action;
import net.kagani.game.player.content.SkillsDialogue;
import net.kagani.game.player.content.SkillsDialogue.SkillDialogueResult;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.network.decoders.handlers.InventoryOptionsHandler;

/*
 * skill dialogue doesnt exist since rs3 it seems. it does automaticaly 1
 */
public class AttachingOrbsDialouge extends Dialogue {

	private final static int[] LEVELS = { 66, 58, 54, 62, 77 }, ORBS = { 573,
			575, 571, 569, 21775 }, STAFFS = { 1397, 1399, 1395, 1393, 21777 };
	private final static double[] EXPERIENCE = { 137.5, 112.5, 100, 125, 150 };
	public static int BATTLESTAFF = 1391;

	@Override
	public void start() {
		int index = (int) parameters[0];
		SkillsDialogue.sendSkillDialogueByProduce(player, STAFFS[index]);
		/*
		 * SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.MAKE,
		 * "Choose how many you wish to make,<br>then click on the item to begin."
		 * , 28, new int[] { ORBS[index] }, new ItemNameFilter() {
		 * 
		 * @Override public String rename(String name) { int levelRequired =
		 * LEVELS[index]; if (player.getSkills().getLevel(Skills.CRAFTING) <
		 * levelRequired) name = "<col=ff0000>" + name +
		 * "<br><col=ff0000>Level " + levelRequired; return name; } });
		 */
	}

	@Override
	public void run(int interfaceId, int componentId) {
		SkillDialogueResult result = SkillsDialogue.getResult(player);
		final int index = getStaffIndex(result.getProduce());
		if (index == -1) {
			end();
			return;
		}
		final int quantity = result.getQuantity();
		player.getActionManager().setAction(new Action() {

			int ticks;

			@Override
			public boolean start(final Player player) {
				if (!checkAll(player))
					return false;
				int orbs = player.getInventory().getAmountOf(ORBS[index]);
				int ticks = quantity;
				if (ticks > orbs)
					ticks = orbs;
				int staffs = player.getInventory().getAmountOf(BATTLESTAFF);
				if (ticks > staffs)
					ticks = staffs;
				this.ticks = ticks;
				return true;
			}

			public boolean checkAll(Player player) {
				final int levelRequired = LEVELS[index];
				if (player.getSkills().getLevel(Skills.CRAFTING) < levelRequired) {
					player.getPackets().sendGameMessage(
							"You need a Magic level of " + levelRequired
									+ " in order to enchant this type of orb.");
					return false;
				}
				return true;
			}

			@Override
			public boolean process(Player player) {
				return checkAll(player) && ticks > 0;
			}

			@Override
			public int processWithDelay(Player player) {
				ticks--;
				player.getInventory().deleteItem(new Item(ORBS[index]));
				player.getInventory().deleteItem(new Item(BATTLESTAFF, 1));
				player.getInventory().addItem(new Item(STAFFS[index], 1));
				player.getSkills().addXp(Skills.CRAFTING, EXPERIENCE[index]);
				player.setNextAnimation(new Animation(16446 + index));
				return 2;
			}

			@Override
			public void stop(Player player) {
				setActionDelay(player, 3);
			}
		});
		end();
	}

	public static boolean isAttachingOrb(Player player, Item item1, Item item2) {
		Item battleStaff = InventoryOptionsHandler.contains(BATTLESTAFF, item1,
				item2);
		if (battleStaff == null)
			return false;
		final int index = getOrbIndex(item1.getId() == BATTLESTAFF ? item2
				.getId() : item1.getId());
		if (index == -1)
			return false;
		player.getDialogueManager().startDialogue("AttachingOrbsDialouge",
				index);
		return true;
	}

	private static int getStaffIndex(int requestedId) {
		for (int index = 0; index < STAFFS.length; index++) {
			if (requestedId == STAFFS[index]) {
				return index;
			}
		}
		return -1;
	}

	private static int getOrbIndex(int requestedId) {
		for (int index = 0; index < ORBS.length; index++) {
			if (requestedId == ORBS[index]) {
				return index;
			}
		}
		return -1;
	}

	@Override
	public void finish() {

	}
}
