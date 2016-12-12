package net.kagani.game.npc.pest;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.WorldTile;
import net.kagani.game.minigames.pest.PestControl;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class Shifter extends PestMonsters {

	public Shifter(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned, int index,
			PestControl manager) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned,
				index, manager);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		Entity target = this.getPossibleTargets().get(0);
		if (this.getCombat().process() && !this.withinDistance(target, 10)
				|| Utils.random(15) == 0)
			teleportSpinner(target);
	}

	private void teleportSpinner(WorldTile tile) { // def 3902, death 3903
		setNextWorldTile(tile);
		setNextAnimation(new Animation(3904));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				setNextGraphics(new Graphics(654));// 1502
			}
		});
	}
}
