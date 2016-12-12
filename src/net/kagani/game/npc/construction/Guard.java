package net.kagani.game.npc.construction;

import java.util.concurrent.TimeUnit;

import net.kagani.executor.GameExecutorManager;
import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.content.construction.House;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class Guard extends NPC {

	private House house;

	public Guard(int id, House house, WorldObject object) {
		super(id, object, -1, true, false);
		setDirection(Utils.getAngle(Utils.ROTATION_DIR_Y[object.getRotation()],
				Utils.ROTATION_DIR_X[object.getRotation()]));
		setForceAgressive(true);
		setForceTargetDistance(7);
		this.house = house;
	}

	@Override
	public void processNPC() {
		Entity target = getCombat().getTarget();
		if (target != null && !withinDistance(target, 7)) {
			getCombat().reset();
			resetCombat();
			WorldTile tile = getRespawnTile();
			addWalkSteps(tile.getX(), tile.getY());
			setNextFaceEntity(null);
		}
		super.processNPC();
	}

	@Override
	public void sendDeath(final Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					reset();
					setLocation(getRespawnTile());
					finish();
					setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	@Override
	public void setRespawnTask() {
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (!house.isLoaded() || !house.isChallengeMode())
						return;
					spawn();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, getCombatDefinitions().getRespawnDelay() * 600,
				TimeUnit.MILLISECONDS);
	}

}
