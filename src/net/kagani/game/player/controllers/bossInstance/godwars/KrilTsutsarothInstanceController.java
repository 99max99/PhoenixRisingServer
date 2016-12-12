package net.kagani.game.player.controllers.bossInstance.godwars;

import net.kagani.game.WorldObject;
import net.kagani.game.map.bossInstance.BossInstance;
import net.kagani.game.player.controllers.bossInstance.BossInstanceController;

public class KrilTsutsarothInstanceController extends BossInstanceController {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (object.getId() == 26428) {
			getInstance().leaveInstance(player, BossInstance.EXITED);
			removeControler();
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick2(final WorldObject object) {
		if (object.getId() == 26286) {
			getInstance().leaveInstance(player, BossInstance.EXITED);
			removeControler();
			return false;
		}
		return true;
	}
}