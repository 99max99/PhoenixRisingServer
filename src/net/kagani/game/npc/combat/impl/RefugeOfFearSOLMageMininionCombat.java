package net.kagani.game.npc.combat.impl;

import java.util.Random;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public class RefugeOfFearSOLMageMininionCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 15172 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		boolean player = target instanceof Player; // shouldn't have player as
		// target unless bind spell.
		startAttack(npc, target, player, player ? 4 : new Random().nextInt(4));
		return npc.getAttackSpeed();
	}

	private void startAttack(final NPC npc, final Entity entity,
			boolean player, int attack) {
		switch (attack) {
		case 0: // Vengeance other - to be casted on one of the minions.
			if (entity.getTemporaryAttributtes().get("vengeance_activated") == Boolean.TRUE) {
				startAttack(npc, entity, player, new Random().nextInt(3) + 1);
				return;
			}
			npc.setNextAnimation(new Animation(4411));
			entity.setNextGraphics(new Graphics(725, 0, 96));
			break;
		case 1: // Heal other - to be casted on one of the minions.
		case 2: // ?
		case 3: // ?
		case 4: // Entangle - to be casted on the player.
			if (!player) {
				startAttack(npc, entity, player, new Random().nextInt(4));
				return;
			}
			final Player p = (Player) entity;
			npc.setNextGraphics(new Graphics(177, 0, 96));
			npc.setNextAnimation(new Animation(710));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					super.stop();
					p.setNextGraphics(new Graphics(179, 0, 96));
				}
			}, 2);
			p.setBoundDelay(33);
			World.sendProjectile(npc, p, 178, 36, 32, 50, 70, 0, 0);
			break;
		}
	}
}
