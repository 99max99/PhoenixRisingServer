package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.EffectsManager;
import net.kagani.game.Entity;
import net.kagani.game.Hit;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.others.Dreadnip;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

public class DreadNipCombat extends CombatScript {

	private String[] DREADNIP_ATTACK_MESSAGE = {
			"Your dreadnip stunned its target!",
			"Your dreadnip poisened its target!" };

	@Override
	public Object[] getKeys() {
		return new Object[] { 14416 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		Dreadnip dreadNip = (Dreadnip) npc;
		if (dreadNip.getTicks() <= 3)
			return 0;
		npc.setNextAnimation(new Animation(-1));
		int attackStyle = Utils.random(3);
		switch (attackStyle) {
		case 0:
			break;
		case 1:
			int secondsDelay = 5 + Utils.random(4);
			target.setStunDelay((int) (secondsDelay / 0.6));
			if (target instanceof Player) {
				Player player = (Player) target;
				player.getActionManager().addActionDelay(secondsDelay);
			} else {
				NPC npcTarget = (NPC) target;
				npcTarget.getCombat().setCombatDelay(
						npcTarget.getCombat().getCombatDelay() + secondsDelay);
			}
			break;
		case 2:
			EffectsManager.makePoisoned(target, 108);
			break;
		}
		if (attackStyle != 0)
			dreadNip.getOwner().getPackets()
					.sendGameMessage(DREADNIP_ATTACK_MESSAGE[attackStyle - 1]);
		delayHit(
				npc,
				0,
				target,
				new Hit(npc, getMaxHit(npc, 550, NPCCombatDefinitions.MELEE,
						target), HitLook.REGULAR_DAMAGE));
		return 5;
	}
}
