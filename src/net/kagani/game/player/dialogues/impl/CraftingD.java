package net.kagani.game.player.dialogues.impl;

import net.kagani.game.item.Item;
import net.kagani.game.player.content.SkillsDialogue;
import net.kagani.game.player.content.SkillsDialogue.SkillDialogueResult;
import net.kagani.game.player.dialogues.Dialogue;

public class CraftingD extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	private Item item;

	@Override
	public void start() {
		item = (Item) parameters[0];
		SkillsDialogue.sendSkillDialogueByProduce(player, 0);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		SkillDialogueResult result = SkillsDialogue.getResult(player);
		end();
	}

	@Override
	public void finish() {

	}
}