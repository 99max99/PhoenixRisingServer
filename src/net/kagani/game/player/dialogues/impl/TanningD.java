package net.kagani.game.player.dialogues.impl;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Action;
import net.kagani.game.player.content.SkillsDialogueOld;
import net.kagani.game.player.content.SkillsDialogueOld.ItemNameFilter;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public class TanningD extends Dialogue {

	public static final int[][] TANNING_PRICES = {
			{ 0, 3, 15, 20, 20, 20, 20, 20, 20 },
			{ 2, 5, 25, 45, 45, 45, 45, 45, 45 } };
	public static final int[] INGREDIENT = { 1739, 1739, 6287, 1753, 1751,
			1749, 1747, 24372 };
	public static final int[] PRODUCT = { 1741, 1743, 6289, 1745, 2505, 2507,
			2509, 24374 };
	public static final int[] LEVELS = { 1, 28, 45, 45, 57, 66, 73, 79, 87 };

	private boolean isCanfis;
	private int npcId;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		isCanfis = npcId == 1041;
		SkillsDialogueOld
				.sendSkillsDialogue(
						player,
						SkillsDialogueOld.MAKE,
						"How many hides would you like to tan?<br>Choose a number, then click the hide to begin.",
						28, PRODUCT, new ItemNameFilter() {
							int count = 0;

							@Override
							public String rename(String name) {
								int levelRequired = LEVELS[count++];
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
	public void run(int interfaceId, final int componentId) {
		final int componentIndex = SkillsDialogueOld.getItemSlot(componentId);
		if (componentIndex > INGREDIENT.length) {
			end();
			return;
		}
		player.getActionManager().setAction(new Action() {

			int ticks,
					price = TANNING_PRICES[isCanfis ? 1 : 0][componentIndex];

			@Override
			public boolean start(final Player player) {
				int leatherAmount = player.getInventory().getAmountOf(
						INGREDIENT[componentIndex]);
				if (leatherAmount == 0) {
					end();
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							player.getDialogueManager()
									.startDialogue(
											"SimpleNPCMessage",
											npcId,
											"Ahhh... novice mistake, you must bring me at least one "
													+ ItemDefinitions
															.getItemDefinitions(
																	INGREDIENT[componentIndex])
															.getName()
															.toLowerCase()
													+ " in order to tan "
													+ ItemDefinitions
															.getItemDefinitions(
																	PRODUCT[componentIndex])
															.getName()
															.toLowerCase()
													+ ".");
						}
					});
					return false;
				}
				final int levelReq = LEVELS[componentIndex];
				if (player.getSkills().getLevel(Skills.CRAFTING) < levelReq) {
					end();
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							player.getDialogueManager()
									.startDialogue(
											"SimpleNPCMessage",
											npcId,
											"Young traveler! You aren't skilled enough to make THIS yet. Come back when you have a Crafting level of at least "
													+ levelReq + ".");
						}
					});
					return false;
				}
				int requestedAmount = SkillsDialogueOld.getQuantity(player);
				int maximumQuantity = price == 0 ? requestedAmount : player
						.getInventory().getCoinsAmount() / price;
				if (maximumQuantity > 0) {
					if (requestedAmount > maximumQuantity)
						requestedAmount = maximumQuantity;
					if (requestedAmount > leatherAmount)
						requestedAmount = leatherAmount;
					ticks = requestedAmount;
					return true;
				}
				return false;
			}

			@Override
			public boolean process(Player player) {
				if (player.getInventory().getCoinsAmount() < price) {
					end();
					player.getDialogueManager().startDialogue(
							"SimpleNPCMessage",
							npcId,
							"Oh no, it looks like you've ran out of coins. Come back later once you have "
									+ price + " coins.");
					// todo npc dialouge here
					return false;
				}
				return ticks > 0;
			}

			@Override
			public int processWithDelay(Player player) {
				ticks--;
				player.getInventory().deleteItem(
						new Item(INGREDIENT[componentIndex], 1));
				player.getInventory().addItem(
						new Item(PRODUCT[componentIndex], 1));
				if (price != 0)
					player.getInventory().removeItemMoneyPouch(
							new Item(995, price));
				return 0;
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
