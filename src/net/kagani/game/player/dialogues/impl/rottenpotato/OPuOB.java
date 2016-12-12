package net.kagani.game.player.dialogues.impl.rottenpotato;

import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.player.dialogues.Dialogue;

public class OPuOB extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	private Object object;

	@Override
	public void start() {
		object = parameters[0];
		stage = 1;
		sendOptionsDialogue("Rotten Potato - Option: Object",
				"Duplicate Object.", "Remove Object.",
				"Transmogrify into Object.", "Check all Object locations.",
				"More.");
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
				end();
				break;
			case OPTION_2:
				stage = 2;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
						"Remove this Object.", "Remove all (-1) objects.",
						"Nevermind.");
				break;
			case OPTION_3:
				end();
				break;
			case OPTION_4:
				stage = -1;
				sendDialogue("Total -1<br><br>error");
				break;
			case OPTION_5:
				end();
				break;
			}
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				World.spawnObject(new WorldObject(-1, 10, 0, player.getX(),
						player.getY(), player.getPlane()));
				end();
				break;
			case OPTION_2:
				World.spawnObject(new WorldObject(-1, 10, 0, player.getX(),
						player.getY(), player.getPlane()));
				end();
				break;
			case OPTION_3:
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