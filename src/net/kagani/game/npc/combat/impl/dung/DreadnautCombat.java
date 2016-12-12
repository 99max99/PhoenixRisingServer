package net.kagani.game.npc.combat.impl.dung;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.EffectsManager.Effect;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.dungeonnering.Dreadnaut;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.utils.Utils;

public class DreadnautCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 12848 };// GFX 2859 Poop bubbles that drain prayer
	}

	@Override
	public int attack(NPC npc, Entity target) {
		Dreadnaut boss = (Dreadnaut) npc;

		if (!Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(),
				target.getX(), target.getY(), target.getSize(), 0))
			return 0;

		if (Utils.random(5) == 0) {
			npc.setNextAnimation(new Animation(14982));
			npc.setNextGraphics(new Graphics(2865));
			int damage = getMaxHit(boss, boss.getMaxHit(),
					NPCCombatDefinitions.MELEE, target);
			if (damage > 0) {
				target.setNextGraphics(new Graphics(2866, 75, 0));
				sendReductionEffect(boss, target, damage);
			}
			if (target instanceof Player) {
				Player player = (Player) target;
				player.getPackets()
						.sendGameMessage(
								"You have been injured and are unable to use protection prayers.");
				player.getEffectsManager().startEffect(
						new Effect(EffectType.PROTECTION_DISABLED, 8));
			}
			delayHit(npc, 1, target, getMeleeHit(npc, damage));
		} else {
			npc.setNextAnimation(new Animation(14973));
			npc.setNextGraphics(new Graphics(2856));

			for (Entity t : boss.getPossibleTargets()) {
				if (!t.withinDistance(target, 2))
					continue;
				int damage = getMaxHit(boss, boss.getMaxHit(),
						NPCCombatDefinitions.MELEE, t);
				World.sendProjectile(boss, t, 2857, 30, 30, 25, 35, 15, 1);
				if (damage > 0) {
					sendReductionEffect(boss, t, damage);
					boss.addSpot(new WorldTile(t));
				} else
					t.setNextGraphics(new Graphics(2858, 75, 0));
				delayHit(npc, 1, t, getMeleeHit(npc, damage));
			}
		}
		return 5;
	}

	private void sendReductionEffect(Dreadnaut boss, Entity target, int damage) {
		if (!boss.canReduceMagicLevel() || !(target instanceof Player))
			return;
		Player player = (Player) target;
		player.getSkills()
				.set(Skills.MAGIC,
						(int) (player.getSkills().getLevel(Skills.MAGIC) - (damage * .10)));
	}
}
