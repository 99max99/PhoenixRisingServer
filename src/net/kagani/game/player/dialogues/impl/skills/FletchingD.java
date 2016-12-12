package net.kagani.game.player.dialogues.impl.skills;

import net.kagani.game.player.actions.Fletching;
import net.kagani.game.player.actions.Fletching.FletchData;
import net.kagani.game.player.content.SkillsDialogue;
import net.kagani.game.player.content.SkillsDialogue.SkillDialogueResult;
import net.kagani.game.player.dialogues.Dialogue;

public class FletchingD extends Dialogue {

	private FletchData items;

	@Override
	public void start() {
		items = (FletchData) parameters[0];
		SkillsDialogue.sendSkillDialogueByProduce(player, items.getProduct());
	}

	@Override
	public void run(int interfaceId, int componentId) {
		SkillDialogueResult result = SkillsDialogue.getResult(player);
		player.getActionManager()
				.setAction(
						new Fletching(items, result.getProduce(), result
								.getQuantity()));
		end();
	}

	@Override
	public void finish() {
	}
}