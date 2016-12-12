package net.kagani.game.player.dialogues.impl;

import net.kagani.game.Animation;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.SkillsDialogueOld;
import net.kagani.game.player.content.SkillsDialogueOld.ItemNameFilter;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public class RobustGlassD extends Dialogue {

	@Override
	public void start() {
		int[] ids = { 23193 };
		SkillsDialogueOld
				.sendSkillsDialogue(
						player,
						SkillsDialogueOld.MAKE,
						"Choose how many you wish to make,<br>then click on the item to begin.",
						28, ids, new ItemNameFilter() {

							@Override
							public String rename(String name) {
								if (player.getSkills()
										.getLevel(Skills.CRAFTING) < 80)
									name = "<col=ff0000>" + name
											+ "<br><col=ff0000>Level " + 89;
								return name;
							}
						});
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
		int requestedAmount = SkillsDialogueOld.getQuantity(player);
		int stoneAmount = player.getInventory().getAmountOf(23194);
		if (requestedAmount > stoneAmount)
			requestedAmount = stoneAmount;
		if (requestedAmount == 0)
			return;
		player.lock();
		player.setNextAnimation(new Animation(883));
		player.getPackets()
				.sendGameMessage(
						"You insert the red sandstone and the machine begins to hum...");
		final int calculatedAmount = requestedAmount;
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.getPackets().sendGameMessage(
						"You collect the finished results.");
				player.unlock();
				for (int index = 0; index < calculatedAmount; index++)
					player.getInventory().replaceItem(
							23193,
							1,
							player.getInventory().getItems()
									.getThisItemSlot(23194));
			}
		}, 3);
	}

	@Override
	public void finish() {

	}
}