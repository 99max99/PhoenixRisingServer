package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.actions.GemCutting;
import net.kagani.game.player.actions.GemCutting.Gem;
import net.kagani.game.player.content.SkillsDialogue;
import net.kagani.game.player.content.SkillsDialogue.SkillDialogueResult;
import net.kagani.game.player.dialogues.Dialogue;

public class GemCuttingD extends Dialogue {

	// private Gem gem;

	@Override
	public void start() {
		Gem gem = (Gem) parameters[0];
		SkillsDialogue.sendSkillDialogueByProduce(player, gem.getCut());

		/*
		 * killsDialogue.sendSkillsDialogue(player, SkillsDialogue.CUT,
		 * "Choose how many you wish to cut,<br>then click on the item to begin."
		 * , player.getInventory().getItems().getNumberOf(gem.getUncut()), new
		 * int[] { gem.getUncut() }, null);
		 */

	}

	@Override
	public void run(int interfaceId, int componentId) {
		SkillDialogueResult result = SkillsDialogue.getResult(player);
		end();
		Gem gem = GemCutting.getGemByProduce(result.getProduce());
		if (gem == null)
			return;
		player.getActionManager().setAction(
				new GemCutting(gem, result.getQuantity()));
	}

	@Override
	public void finish() {

	}

}
