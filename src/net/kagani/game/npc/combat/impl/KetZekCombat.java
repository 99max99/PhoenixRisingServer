package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class KetZekCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Ket-Zek", 15207 };
	}// anims: DeathEmote: 9257 DefEmote: 9253 AttackAnim: 9252 gfxs: healing:
		// 444 - healer

	@Override
	public int attack(final NPC npc, final Entity target) {
		boolean isDistanced = !Utils.isOnRange(npc, target, 0);
		int style = Utils.random(isDistanced ? 1 : 0, 2);
		switch (style) {
		case 0:// MELEE
			npc.setNextAnimation(new Animation(npc.getCombatDefinitions()
					.getAttackEmote()));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
			break;
		case 1:// MAGIC
			npc.setNextAnimation(new Animation(16136));
			Projectile projectile = World.sendProjectileNew(npc, target, 2984,
					34, 16, 35, 2, 16, 0);
			delayHit(
					npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target,
					getMagicHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MAGE, target)));
			target.setNextGraphics(new Graphics(2983, projectile.getEndTime(),
					96 << 16));
			break;
		}
		return npc.getAttackSpeed();
	}
}
