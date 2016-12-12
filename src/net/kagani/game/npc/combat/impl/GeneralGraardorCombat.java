package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class GeneralGraardorCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6260 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		if (!Utils.isOnRange(npc, target, 0))
			return 0;
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.random(5) == 0) {
			switch (Utils.random(11)) {
			case 0:
				npc.setNextForceTalk(new ForceTalk("Death to our enemies!"));
				npc.playSoundEffect(3219);
				break;
			case 1:
				npc.setNextForceTalk(new ForceTalk("Brargh!"));
				npc.playSoundEffect(3209);
				break;
			case 2:
				npc.setNextForceTalk(new ForceTalk("Break their bones!"));
				npc.playSoundEffect(3221);
				break;
			case 3:
				npc.setNextForceTalk(new ForceTalk("For the glory of Bandos!"));
				npc.playSoundEffect(3205);
				break;
			case 4:
				npc.setNextForceTalk(new ForceTalk("Split their skulls!"));
				npc.playSoundEffect(3229);
				break;
			case 5:
				npc.setNextForceTalk(new ForceTalk(
						"We feast on the bones of our enemies tonight!"));
				npc.playSoundEffect(3206);
				break;
			case 6:
				npc.setNextForceTalk(new ForceTalk("CHAAARGE!"));
				npc.playSoundEffect(3220);
				break;
			case 7:
				npc.setNextForceTalk(new ForceTalk("Crush them underfoot!"));
				npc.playSoundEffect(3224);
				break;
			case 8:
				npc.setNextForceTalk(new ForceTalk("All glory to Bandos!"));
				npc.playSoundEffect(3205);
				break;
			case 9:
				npc.setNextForceTalk(new ForceTalk("GRAAAAAAAAAR!"));
				npc.playSoundEffect(3207);
				break;
			case 10:
				npc.setNextForceTalk(new ForceTalk(
						"FOR THE GLORY OF THE BIG HIGH WAR GOD!"));
				npc.playSoundEffect(3228);
				break;
			}
		}
		if (Utils.random(3) == 0) { // range magical attack
			npc.setNextAnimation(new Animation(17391));
			for (Entity t : npc.getPossibleTargets())
				delayHit(
						npc,
						Utils.projectileTimeToCycles(World.sendProjectileNew(
								npc, t, 3352, 0, 0, 30, 2, 0, 0).getEndTime()) - 1,
						t,
						getRangeHit(npc,
								getMaxHit(npc, NPCCombatDefinitions.RANGE, t)));
		} else { // melee attack
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
		}
		return npc.getAttackSpeed();
	}
}
