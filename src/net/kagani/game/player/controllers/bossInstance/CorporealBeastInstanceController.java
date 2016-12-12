package net.kagani.game.player.controllers.bossInstance;

import net.kagani.game.WorldObject;
import net.kagani.game.map.bossInstance.BossInstance;

public class CorporealBeastInstanceController extends BossInstanceController {

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (object.getId() == 38811) {
			getInstance().leaveInstance(player, BossInstance.EXITED);
			removeControler();
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick2(final WorldObject object) {
		if (object.getId() == 38811) {
			player.getPackets()
					.sendGameMessage(
							"You can't peak when you are already inside the encounter!");
			return false;
		}
		return true;
	}
}
