package net.kagani.game.player.dialogues.impl.skills;

import net.kagani.game.WorldObject;
import net.kagani.game.player.actions.Cooking;
import net.kagani.game.player.actions.Cooking.Cookables;
import net.kagani.game.player.content.SkillsDialogue;
import net.kagani.game.player.content.SkillsDialogue.SkillDialogueResult;
import net.kagani.game.player.dialogues.Dialogue;

public class CookingD extends Dialogue {

	// private Cookables cooking;
	private WorldObject object;

	@Override
	public void start() {
		Cookables cooking = (Cookables) parameters[0];
		this.object = (WorldObject) parameters[1];
		if (cooking == Cookables.RAW_MEAT) {
			end();
			player.getDialogueManager().startDialogue("MeatDrying", object);
			return;
		}
		SkillsDialogue.sendSkillDialogueByProduce(player, cooking.getProduct()
				.getId());
	}

	@Override
	public void run(int interfaceId, int componentId) {
		SkillDialogueResult result = SkillsDialogue.getResult(player);
		end();
		Cookables cooking = Cooking.getCookForProduce(result.getProduce());
		if (cooking == null)
			return;
		player.getActionManager().setAction(
				new Cooking(object, cooking.getRawItem(), result.getQuantity(),
						cooking));
	}

	@Override
	public void finish() {

	}

}
