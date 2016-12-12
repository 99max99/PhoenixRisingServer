package net.kagani.game.player.controllers.bossInstance;

import net.kagani.game.Animation;
import net.kagani.game.WorldObject;
import net.kagani.game.map.bossInstance.BossInstance;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public class KalphiteQueenInstanceController extends BossInstanceController {

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (object.getId() == 3832) { // kq
			player.lock();
			player.setNextAnimation(new Animation(828));
			WorldTasksManager.schedule(new WorldTask() { // to remove at same
															// time it teleports
						@Override
						public void run() {
							getInstance().leaveInstance(player,
									BossInstance.EXITED);
							removeControler();
						}
					});
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick5(final WorldObject object) {
		if (object.getId() == 82018) { // exiled kq
			player.lock();
			player.setNextAnimation(new Animation(19499));
			WorldTasksManager.schedule(new WorldTask() { // to remove at same
															// time it teleports
						@Override
						public void run() {
							player.setNextAnimation(new Animation(-1));
							getInstance().leaveInstance(player,
									BossInstance.EXITED);
							removeControler();
						}
					}, 3);
			return false;
		}
		return true;
	}

}
