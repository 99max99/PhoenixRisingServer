package net.kagani.game.player.controllers.bossInstance;

import net.kagani.game.WorldObject;
import net.kagani.game.map.bossInstance.BossInstance;

public class DagannothKingsInstanceController extends BossInstanceController {

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (object.getId() == 10229) {
			getInstance().leaveInstance(player, BossInstance.EXITED);
			removeControler();
			return false;
		}
		return true;
	}
}
