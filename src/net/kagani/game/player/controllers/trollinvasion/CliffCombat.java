package net.kagani.game.player.controllers.trollinvasion;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class CliffCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Cliff", 13381 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.random(2) == 0) { // range
			npc.setNextAnimation(new Animation(1933));
			delayHit(
					npc,
					1,
					target,
					getRangeHit(
							npc,
							getMaxHit(npc, 335,
									NPCCombatDefinitions.RANGE, target)));
			target.setNextGraphics(new Graphics(755));
		} else { // melee attack
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(
							npc,
							getMaxHit(npc, defs.getMaxHit(),
									NPCCombatDefinitions.MELEE, target)));
		}
		return defs.getAttackGfx();
	}
}
