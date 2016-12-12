package net.kagani.game.player.dialogues.impl.clans;

import net.kagani.game.WorldTile;
import net.kagani.game.player.dialogues.Dialogue;

public class ClanPortal extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	private int option = 1;

	@Override
	public void start() {
		option = (int) parameters[0];
		switch (option) {
		case 1:
			stage = 1;
			sendOptionsDialogue("Clan citadel portal", "Find out about clans.",
					"Take a tour of a clan citadel.",
					"Visit another clan's citadel.");
			break;
		case 2:
			if (player.isConnectedClanChannel()) {
				player.getPackets().sendGameMessage("Entering citadel...");
				player.setNextWorldTile(new WorldTile(5504, 4494, 0));
			} else {
				player.getPackets().sendGameMessage(
						"You're currently not in a clan.");
			}
			end();
			break;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				if (player.isConnectedClanChannel()) {
					player.getPackets().sendGameMessage("Error.");
				} else {
					player.getPackets().sendGameMessage(
							"You're currently not in a clan.");
				}
				end();
				break;
			case OPTION_2:
				if (player.isConnectedClanChannel()) {
					player.getPackets().sendGameMessage("Error.");
				} else {
					player.getPackets().sendGameMessage(
							"You're currently not in a clan.");
				}
				end();
				break;
			case OPTION_3:
				player.getPackets().sendInputLongTextScript(
						"Please enter clan name:");
				player.getTemporaryAttributtes().put("clancitadel",
						Boolean.TRUE);
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {

	}
}