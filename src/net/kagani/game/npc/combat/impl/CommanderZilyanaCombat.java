package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class CommanderZilyanaCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6247 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		if (!Utils.isOnRange(npc, target, 0))
			return 0;
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.random(4) == 0) {
			switch (Utils.random(10)) {
			case 0:
				npc.setNextForceTalk(new ForceTalk(
						"Death to the enemies of the light!"));
				npc.playSoundEffect(3247);
				break;
			case 1:
				npc.setNextForceTalk(new ForceTalk("Slay the evil ones!"));
				npc.playSoundEffect(3242);
				break;
			case 2:
				npc.setNextForceTalk(new ForceTalk(
						"Saradomin lend me strength!"));
				npc.playSoundEffect(3263);
				break;
			case 3:
				npc.setNextForceTalk(new ForceTalk("By the power of Saradomin!"));
				npc.playSoundEffect(3262);
				break;
			case 4:
				npc.setNextForceTalk(new ForceTalk("May Saradomin be my sword."));
				npc.playSoundEffect(3251);
				break;
			case 5:
				npc.setNextForceTalk(new ForceTalk("Good will always triumph!"));
				npc.playSoundEffect(3260);
				break;
			case 6:
				npc.setNextForceTalk(new ForceTalk(
						"Forward! Our allies are with us!"));
				npc.playSoundEffect(3245);
				break;
			case 7:
				npc.setNextForceTalk(new ForceTalk("Saradomin is with us!"));
				npc.playSoundEffect(3266);
				break;
			case 8:
				npc.setNextForceTalk(new ForceTalk("In the name of Saradomin!"));
				npc.playSoundEffect(3250);
				break;
			case 9:
				npc.setNextForceTalk(new ForceTalk("Attack! Find the Godsword!"));
				npc.playSoundEffect(3258);
				break;
			}
		}
		if (Utils.random(2) == 0) { // mage magical attack 50/50
			npc.setNextAnimation(new Animation(6967));
			for (Entity t : npc.getPossibleTargets()) {
				int damage = getMaxHit(npc, NPCCombatDefinitions.MAGE, t);
				if (damage > 0) {
					delayHit(npc, 1, t, getMagicHit(npc, damage));
					t.setNextGraphics(new Graphics(1194));
				} else
					t.setNextGraphics(new Graphics(85, 0, 96));
			}
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
