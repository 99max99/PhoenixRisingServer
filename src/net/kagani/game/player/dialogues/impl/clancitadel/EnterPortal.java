package net.kagani.game.player.dialogues.impl.clancitadel;

import net.kagani.game.WorldTile;
import net.kagani.game.player.dialogues.Dialogue;

public class EnterPortal extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Visit my clan's citadel",
				"Visit another clan's citadel", "View flythrough");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			switch (componentId) {
			case OPTION_1:
				if (player.isConnectedClanChannel()) {
					player.getPackets().sendGameMessage("Entering citadel...");
					player.setNextWorldTile(new WorldTile(5504, 4494, 0));
				} else {
					player.getPackets().sendGameMessage(
							"You're currently not in a clan.");
				}
				end();
				break;
			case OPTION_2:
				player.getPackets().sendGameMessage(
						"Unable to visit another clan's citadel.");
				end();
				break;
			case OPTION_3:
				player.getPackets().sendGameMessage(
						"Unable to view flythrough.");
				end();
				break;
			}
		}
	}

	@Override
	public void finish() {
	}
}