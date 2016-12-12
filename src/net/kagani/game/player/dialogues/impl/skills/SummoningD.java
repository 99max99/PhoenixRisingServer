package net.kagani.game.player.dialogues.impl.skills;

import java.io.IOException;

import net.kagani.game.WorldObject;
import net.kagani.game.player.actions.Summoning;
import net.kagani.game.player.actions.Summoning.Pouch;
import net.kagani.game.player.content.SkillsDialogue;
import net.kagani.game.player.content.SkillsDialogue.SkillDialogueResult;
import net.kagani.game.player.dialogues.Dialogue;

public class SummoningD extends Dialogue {

	private WorldObject object;

	@Override
	public void start() {
		object = (WorldObject) parameters[0];
		Pouch pouch = (Pouch) parameters[1];
		SkillsDialogue.sendSkillDialogueByProduce(player, pouch.getRealPouchId());
	}

	@Override
	public void run(int interfaceId, int componentId) {
		SkillDialogueResult result = SkillsDialogue.getResult(player);
		end();
		Pouch pouch = Pouch.getPouchByProduce(result.getProduce());
		if(pouch == null)
			return;
		player.getActionManager().setAction(new Summoning(pouch, object, result.getQuantity()));
	}

	@Override
	public void finish() {
	}
}