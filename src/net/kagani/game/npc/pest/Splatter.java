package net.kagani.game.npc.pest;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.minigames.pest.PestControl;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class Splatter extends PestMonsters {

	public Splatter(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned, int index,
			PestControl manager) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned,
				index, manager);
	}

	@Override
	public void processNPC() {
		super.processNPC();
	}

	private void sendExplosion() {
		final Splatter splatter = this;
		setNextAnimation(new Animation(3888));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				setNextAnimation(new Animation(3889));
				setNextGraphics(new Graphics(649 + (getId() - 3727)));
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						finish();
						for (Entity e : getPossibleTargets())
							if (e.withinDistance(splatter, 2))
								e.applyHit(new Hit(splatter, Utils.random(400),
										HitLook.REGULAR_DAMAGE));
					}
				});
			}
		});
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					sendExplosion();
				else if (loop >= defs.getDeathDelay()) {
					reset();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
}
