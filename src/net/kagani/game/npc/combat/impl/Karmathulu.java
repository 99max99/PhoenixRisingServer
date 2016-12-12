package net.kagani.game.npc.combat.impl;

import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.utils.Utils;

public class Karmathulu extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "karmathulu" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		int attackStyle = Utils.random(3);
		switch (attackStyle) {
		case 0:
			sendRangeAttack(target, npc);
			break;
		case 1:
			if (npc.withinDistance(target, 1))
				sendMeleeAttack(target, npc);
			else
				sendRandomAttack(target, npc);
			break;
		case 2:
			sendMagicAttack(target, npc);
			break;
		}
		if (Utils.random(2) == 0)
			sendRandomAttack(target, npc);
		return npc.getAttackSpeed();
	}

	private void sendRandomAttack(Entity target, NPC npc) {
		int attackStyle = Utils.random(3);
		switch (attackStyle) {
		case 0:
		case 1:
			sendMagicAttack(target, npc);
			break;
		case 2:
			sendRangeAttack(target, npc);
			break;
		}
	}

	private void sendMagicAttack(Entity target, NPC npc) {
		//
	}

	private void sendMeleeAttack(Entity target, NPC npc) {
		//
	}

	private void sendRangeAttack(Entity target, NPC npc) {
		//
	}
}
