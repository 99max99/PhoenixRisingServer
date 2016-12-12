package net.kagani.game.player.dialogues.impl.clans;

import net.kagani.game.player.dialogues.Dialogue;

public class ClanCreateD extends Dialogue {

	@Override
	public void start() {
		sendDialogue(
				"You must be a member of a clan in order to join their channel.",
				"Would you like to create a clan?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			player.getTemporaryAttributtes().put("setclan", Boolean.TRUE);
			player.getPackets().sendInputNameScript(
					"Enter the clan name you'd like to have.");
			end();
		}
	}

	@Override
	public void finish() {

	}
}