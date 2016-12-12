package net.kagani.game.player.dialogues.impl;

import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.game.player.dialogues.impl.cities.lumbridge.LumbridgeSage;

public class JModTable extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	public static boolean PMOD_MEETING = false;

	public static boolean atPModRoom(Player player) {
		return (player.getX() > 2841 && player.getY() < 5160
				&& player.getX() < 2852 && player.getY() > 5140);
	}

	@Override
	public void start() {
		sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
				LumbridgeSage.locked ? "Unlock PMod Room" : "Lock"
						+ " PMod Room", "Kick Player", "Kick All", "Nevermind.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1) {
			LumbridgeSage.locked = !LumbridgeSage.locked;
			player.getPackets().sendGameMessage(
					"[Attempting to "
							+ (LumbridgeSage.locked ? "lock" : "unlock")
							+ " the PMod Room.]");
			end();
		}
		if (componentId == OPTION_2) {
			player.getPackets().sendGameMessage("todo...");
			end();
		}
		if (componentId == OPTION_3) {
			for (Player players : World.getPlayers()) {
				if (!atPModRoom(players))
					continue;
				players.setNextWorldTile(new WorldTile(3231, 3243, 0));
				player.setNextWorldTile(new WorldTile(3231, 3243, 0));
				player.getPackets().sendGameMessage(
						"[Attempting to kick everyone from the PMod Room.]");
				end();
				return;
			}
		}
		if (componentId == OPTION_4) {
			end();
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
	}
}