package net.kagani.game.player.controllers.trollinvasion;

import net.kagani.game.Entity;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

@SuppressWarnings("serial")
public class Cliff_Troll extends NPC {

	private TrollInvasion controler;

	public Cliff_Troll(int id, WorldTile tile, TrollInvasion controler) {
		super(id, tile, -1, true, true);
		this.controler = controler;
		setForceMultiArea(true);
		setNoDistanceCheck(true);
		setForceAgressive(true);
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		controler.addKill();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
				} else if (loop >= defs.getDeathDelay()) {
					reset();
					finish();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

}
