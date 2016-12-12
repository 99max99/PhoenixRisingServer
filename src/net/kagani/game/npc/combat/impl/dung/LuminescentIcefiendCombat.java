package net.kagani.game.npc.combat.impl.dung;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.dungeonnering.LuminscentIcefiend;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class LuminescentIcefiendCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 9912 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final LuminscentIcefiend icefiend = (LuminscentIcefiend) npc;

		if (icefiend.isSpecialEnabled()) {
			npc.setNextAnimation(new Animation(13338));
			npc.setNextGraphics(new Graphics(2524));

			icefiend.commenceSpecial();
			return 20;
		}

		boolean magicAttack = Utils.random(2) == 0;

		if (magicAttack) {
			npc.setNextAnimation(new Animation(13352));
			World.sendProjectile(npc, target, 2529, 15, 16, 35, 35, 16, 0);
			delayHit(
					icefiend,
					2,
					target,
					getMagicHit(
							npc,
							getMaxHit(npc, icefiend.getMaxHit(),
									NPCCombatDefinitions.MAGE, target)));
		} else {
			npc.setNextAnimation(new Animation(13337));
			World.sendProjectile(npc, target, 2530, 30, 16, 35, 35, 0, 0);
			delayHit(
					icefiend,
					2,
					target,
					getRangeHit(
							npc,
							getMaxHit(npc, (int) (icefiend.getMaxHit() * .90),
									NPCCombatDefinitions.RANGE, target)));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					target.setNextGraphics(new Graphics(2531));
				}
			}, 2);
		}
		return 4;
	}
}
