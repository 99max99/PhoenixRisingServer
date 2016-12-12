package net.kagani.game.player.dialogues.impl;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Action;
import net.kagani.game.player.content.SkillsDialogueOld;
import net.kagani.game.player.content.SkillsDialogueOld.ItemNameFilter;
import net.kagani.game.player.dialogues.Dialogue;

public class SpinningD extends Dialogue {

	private static final int[][] INGREDIENT = {
			{ 1737, 15415, 1779, 9436, 6051, 10814 },
			{ 17448, 17450, 17452, 17454, 17456, 17458, 17460, 17462, 17464,
					17466 } };
	private static final int[][] PRODUCT = {
			{ 1759, 15416, 1777, 9438, 6038, 954 },
			{ 17468, 17470, 17472, 17474, 17476, 17478, 17480, 17482, 17484,
					17486 } };
	private static final int[][] LEVELS = { { 1, 1, 1, 10, 19, 30 },
			{ 1, 10, 20, 30, 40, 50, 60, 70, 80, 90 } };
	private static final double[][] EXPERIENCE = {
			{ 2.5, 2.5, 15, 15, 30, 25 },
			{ 2.5, 3.0, 3.5, 3.0, 3.5, 6.5, 7.5, 9.0, 11, 12 } };

	private boolean dung;

	@Override
	public void start() {
		dung = (boolean) this.parameters[0];
		SkillsDialogueOld
				.sendSkillsDialogue(
						player,
						SkillsDialogueOld.MAKE,
						"How many strings would you like to make?<br>Choose a number, then click the string to begin.",
						28, PRODUCT[dung ? 1 : 0], new ItemNameFilter() {
							int count = 0;

							@Override
							public String rename(String name) {
								int levelRequired = LEVELS[dung ? 1 : 0][count++];
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

			int ticks;

			@Override
			public boolean start(final Player player) {
				final int levelReq = LEVELS[dung ? 1 : 0][componentIndex];
				if (player.getSkills().getLevel(Skills.CRAFTING) < levelReq) {
					end();
					player.getPackets()
							.sendGameMessage(
									"You need a Crafting level of "
											+ levelReq
											+ " in order to spin a "
											+ ItemDefinitions
													.getItemDefinitions(
															PRODUCT[dung ? 1
																	: 0][componentIndex])
													.getName().toLowerCase()
											+ ".");
					return false;
				}
				int leatherAmount = player.getInventory().getAmountOf(
						INGREDIENT[dung ? 1 : 0][componentIndex]);
				if (leatherAmount == 0) {
					player.getPackets()
							.sendGameMessage(
									"You need a piece of "
											+ ItemDefinitions
													.getItemDefinitions(
															INGREDIENT[dung ? 1
																	: 0][componentIndex])
													.getName().toLowerCase()
											+ " in order to make a "
											+ ItemDefinitions
													.getItemDefinitions(
															PRODUCT[dung ? 1
																	: 0][componentIndex])
													.getName().toLowerCase()
											+ ".");
					end();
					return false;
				}
				int requestedAmount = SkillsDialogueOld.getQuantity(player);
				if (requestedAmount > leatherAmount)
					requestedAmount = leatherAmount;
				ticks = requestedAmount;
				return true;
			}

			@Override
			public boolean process(Player player) {
				return ticks > 0;
			}

			@Override
			public int processWithDelay(Player player) {
				ticks--;
				player.setNextAnimation(new Animation(896));
				player.getSkills().addXp(Skills.CRAFTING,
						EXPERIENCE[dung ? 1 : 0][componentIndex]);
				player.getInventory().deleteItem(
						new Item(INGREDIENT[dung ? 1 : 0][componentIndex], 1));
				player.getInventory().addItem(
						new Item(PRODUCT[dung ? 1 : 0][componentIndex], 1));
				return 3;// 4;
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