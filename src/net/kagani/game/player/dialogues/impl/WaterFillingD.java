package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.actions.WaterFilling;
import net.kagani.game.player.actions.WaterFilling.Fill;
import net.kagani.game.player.content.SkillsDialogue;
import net.kagani.game.player.content.SkillsDialogue.SkillDialogueResult;
import net.kagani.game.player.dialogues.Dialogue;

public class WaterFillingD extends Dialogue {

	@Override
	public void start() {
		Fill fill = (Fill) parameters[0];
		SkillsDialogue.sendSkillDialogueByProduce(player, fill.getFull());
	}

	@Override
	public void run(int interfaceId, int componentId) {

		SkillDialogueResult result = SkillsDialogue.getResult(player);
		end();
		Fill fill = WaterFilling.getFillByProduce(result.getProduce());
		if (fill == null)
			return;
		player.getActionManager().setAction(
				new WaterFilling(fill, result.getQuantity()));

	}

	@Override
	public void finish() {

	}
}