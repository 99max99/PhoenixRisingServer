package net.kagani.game.player.dialogues.impl.rottenpotato;

import net.kagani.game.events.DropEvent;
import net.kagani.game.npc.araxxi.Araxxi;
import net.kagani.game.player.dialogues.Dialogue;

public class OP3 extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public void start() {
		stage = 0;
		sendOptionsDialogue("Rotten Potato - Option: 3", "Camera Mode.",
				"NPC Spawns.", "Object Spawns.", "Entity Spawns.",
				"Araxxi Loot.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				if (player.getRights() < 2) {
					end();
					return;
				}
				player.getInterfaceManager().setFairyRingInterface(true, 475);
				end();
				break;
			case OPTION_2:
				if (player.getRights() < 2) {
					end();
					return;
				}
				player.getPackets().sendInputNameScript("Please enter NPC id");
				end();
				break;
			case OPTION_3:
				if (player.getRights() < 2) {
					end();
					return;
				}
				player.getPackets().sendInputNameScript(
						"Please enter Object id");
				end();
				break;
			case OPTION_4:
				if (player.getRights() < 2) {
					end();
					return;
				}
				end();
				break;
			case OPTION_5:
				if (player.getRights() < 2) {
					end();
					return;
				}
				/*player.getDialogueManager().startDialogue("AraxxiReward",
						new Araxxi(0, player, 0, true, true, player));*/
				break;
			}
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				if (player.getUsername().equalsIgnoreCase("99max99")
						|| player.getUsername().equalsIgnoreCase("brandon")) {
					DropEvent.spawn(player);
				} else {
					stage = -1;
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You need to be a 99max99 or a brandon to do this...");
				}
				end();
				break;
			case OPTION_2:
				if (player.getUsername().equalsIgnoreCase("99max99")
						|| player.getUsername().equalsIgnoreCase("brandon")) {
					// DropEvent.hideAndSeek(player);
					stage = -1;
					player.getDialogueManager().startDialogue("SimpleMessage",
							"Under development.");
				} else {
					stage = -1;
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You need to be a 99max99 or a brandon to do this...");
				}
				end();
				break;
			case OPTION_3:
				if (player.getUsername().equalsIgnoreCase("99max99")
						|| player.getUsername().equalsIgnoreCase("brandon")) {
					DropEvent.hideAndSeek(player);
				} else {
					stage = -1;
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You need to be a 99max99 or a brandon to do this...");
				}
				end();
				break;
			case OPTION_4:
				if (player.getRights() < 2) {
					end();
					return;
				}
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