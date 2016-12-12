package net.kagani.game.player.dialogues.impl;

import net.kagani.game.Animation;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Action;
import net.kagani.game.player.content.SkillsDialogueOld;
import net.kagani.game.player.content.SkillsDialogueOld.ItemNameFilter;
import net.kagani.game.player.dialogues.Dialogue;

public class GlassBlowingD extends Dialogue {

	private static final int[][] LEVELS = { { 1, 4, 12, 33, 42, 45, 49, 87 },
			{ 89 } };
	private static final double[][] EXPERIENCE = {
			{ 17.5, 19, 25, 35, 42.5, 52.5, 55, 70 }, { 100 } };
	private static final int[][] PRODUCTS = {
			{ 1919, 4527, 4522, 7534, 567, 4542, 10973 }, { 32843 } };

	private int index;

	@Override
	public void start() {
		index = (int) parameters[0];
		SkillsDialogueOld
				.sendSkillsDialogue(
						player,
						SkillsDialogueOld.MAKE,
						"Choose how many you wish to make,<br>then click on the item to begin.",
						28, PRODUCTS[index], new ItemNameFilter() {

							int count = 0;

							@Override
							public String rename(String name) {
								int levelRequired = LEVELS[index][count++];
								if (player.getSkills()
										.getLevel(Skills.CRAFTING) < levelRequired)
									name = "<col=ff0000>" + name
											+ "<br><col=ff0000>Level "
											+ levelRequired;
								return name;
							}
						});
	}

	@Override
	public void run(int interfaceId, int componentId) {
		final int componentIndex = SkillsDialogueOld.getItemSlot(componentId);
		if (componentIndex > PRODUCTS[index].length) {
			end();
			return;
		}
		player.getActionManager().setAction(new Action() {

			int ticks;
			
			@Override
			public boolean start(final Player player) {
				if (!checkAll(player))
					return false;
				int moltenGlassCount = player.getInventory().getAmountOf(
						index == 0 ? 1775 : 32845);
				int requestedAmount = SkillsDialogueOld.getQuantity(player);
				if (requestedAmount > moltenGlassCount)
					requestedAmount = moltenGlassCount;
				this.ticks = requestedAmount;
				return true;
			}

			public boolean checkAll(Player player) {
				final int levelReq = LEVELS[index][componentIndex];
				if (player.getSkills().getLevel(Skills.CRAFTING) < levelReq) {
					player.getPackets().sendGameMessage(
							"You need a Crafting level of " + levelReq
									+ " to create this.");
					return false;
				} else if (!player.getInventory().containsItemToolBelt(1785)) {
					player.getPackets()
							.sendGameMessage(
									"You need a glassblowing pipe in order to create glass items.");
					return false;
				}
				return true;
			}

			@Override
			public boolean process(Player player) {
				return checkAll(player);
			}

			@Override
			public int processWithDelay(Player player) {
				ticks--;
				player.getInventory().deleteItem(
						new Item(index == 0 ? 1775 : 32845, 1));
				player.getInventory().addItem(
						new Item(PRODUCTS[index][componentIndex]));
				player.getSkills().addXp(Skills.CRAFTING,
						EXPERIENCE[index][componentIndex]);
				if (ticks % 2 == 0)
					player.setNextAnimation(new Animation(884));
				if (ticks > 0)
					return 2;
				return -1;
			}

			@Override
			public void stop(Player player) {
				setActionDelay(player, 3);
			}
		});
		end();
	}

	@Override
	public void finish() {

	}
}