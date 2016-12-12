package net.kagani.game.player.controllers.bossInstance;

import net.kagani.game.WorldObject;
import net.kagani.game.map.bossInstance.BossInstance;

public class EvilChickenInstanceController extends BossInstanceController {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (object.getId() == 43461) {
			getInstance().leaveInstance(player, BossInstance.EXITED);
			removeControler();
			return false;
		}
		return true;
	}
}