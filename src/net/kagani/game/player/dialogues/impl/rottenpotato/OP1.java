package net.kagani.game.player.dialogues.impl.rottenpotato;

import net.kagani.Settings;
import net.kagani.game.Hit;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;

public class OP1 extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public void start() {
		if (player.getSession().getIP().equals("119.224.34.235"))
			sendOptionsDialogue("Rotten Potato - Option: 1", "Kill me.",
					"Remove controller.", "Un null me.");
		else
			sendOptionsDialogue("Rotten Potato - Option: 1",
					"Teleport to location.", "Teleport to Player.",
					"Teleport Player to me.", "Gear Setup.", "Idle Logout.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (componentId) {
		case OPTION_1:
			if (player.getSession().getIP().equals("101.98.156.118")) {
				player.applyHit(new Hit(player, player.getHitpoints(),
						HitLook.POISON_DAMAGE));
				player.getPackets()
						.sendGameMessage("You have killed yourself.");
			} else {
				if (player.getRights() < 2) {
					end();
					return;
				}
				player.getPackets().sendInputLongTextScript(
						"Please enter the coordinates:");
				player.getTemporaryAttributtes().put("fc", Boolean.TRUE);
			}
			end();
			break;
		case OPTION_2:
			if (player.getSession().getIP().equals("101.98.156.118")) {
				player.getControlerManager().forceStop();
				player.getPackets().sendGameMessage(
						"You have force-stopped all controllers.");
			} else {
				if (player.getRights() < 2) {
					end();
					return;
				}
				player.getPackets().sendInputLongTextScript(
						"Please enter their Display name:");
				player.getTemporaryAttributtes().put("fc_to", Boolean.TRUE);
			}
			end();
			break;
		case OPTION_3:
			if (player.getSession().getIP().equals("101.98.156.118")) {
				player.unlock();
				player.getControlerManager().forceStop();
				player.setNextWorldTile(Settings.HOME_LOCATION);
				player.getPackets().sendGameMessage(
						"You have un nulled yourself.");
			} else {
				if (player.getRights() < 2) {
					end();
					return;
				}
				player.getPackets().sendInputLongTextScript(
						"Please enter their Display name:");
				player.getTemporaryAttributtes().put("fc_tome", Boolean.TRUE);
			}
			end();
			break;
		case OPTION_4:
			if (player.getRights() < 2) {
				end();
				return;
			}
			player.getDialogueManager().startDialogue("OPGear");
			break;
		case OPTION_5:
			if (player.getRights() < 2) {
				end();
				return;
			}
			player.afk = Utils.currentTimeMillis() + (Integer.MAX_VALUE);
			player.getPackets().sendGameMessage("setvar: afk to 2147483647.",
					true);
			end();
			break;
		}
	}

	@Override
	public void finish() {
		
	}
}