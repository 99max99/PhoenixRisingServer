package net.kagani.game.player.cutscenes.actions;

import net.kagani.game.player.Player;

public class EntityDialogueAction extends CutsceneAction {

	private int id;
	private String message;

	public EntityDialogueAction(String message) {
		this(-1, message);
	}

	public EntityDialogueAction(int id, String message) {
		super(-1, -1);
		this.id = id;
		this.message = message;
	}

	@Override
	public void process(Player player, Object[] cache) {
		if (id == -1)
			player.getDialogueManager().startDialogue("SimplePlayerMessage",
					message);
		else
			player.getDialogueManager().startDialogue("SimpleNPCMessage", id,
					message);
	}

}
