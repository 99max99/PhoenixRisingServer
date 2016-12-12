package net.kagani.game.player.dialogues.impl.rottenpotato;

import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Magic;
import net.kagani.game.player.dialogues.Dialogue;

public class OPTeletome extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	/** The target. */
	private Player target;

	@Override
	public void finish() {
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			switch (componentId) {
			case OPTION_1:
				if (player.getRights() < 2) {
					end();
					return;
				}
				target.setNextWorldTile(player);
				player.getPackets().sendGameMessage(
						"tele:  " + target.getDisplayName() + " to "
								+ player.getX() + ", " + player.getY() + ", "
								+ player.getPlane() + ".");
				end();
				break;
			case OPTION_2:
				if (player.getRights() < 2) {
					end();
					return;
				}
				Magic.sendNormalTeleportSpell(target, 0, 0, new WorldTile(
						player.getX(), player.getY(), player.getPlane()));
				player.getPackets().sendGameMessage(
						"tele:  " + target.getDisplayName() + " to "
								+ player.getX() + ", " + player.getY() + ", "
								+ player.getPlane() + ".");
				end();
				break;
			case OPTION_3:
				if (player.getRights() < 2) {
					end();
					return;
				}
				end();
				break;
			}
		}

	}

	@Override
	public void start() {
		target = (Player) parameters[0];
		stage = 1;
		sendOptionsDialogue(
				"Rotten Potato - Option: Teleport to me",
				"Ft Tele " + target.getDisplayName() + " to " + player.getX()
						+ ", " + player.getY() + ", " + player.getPlane() + "",
				"Magic Tele " + target.getDisplayName() + " to "
						+ player.getX() + ", " + player.getY() + ", "
						+ player.getPlane() + "", "Nevermind");
	}
}