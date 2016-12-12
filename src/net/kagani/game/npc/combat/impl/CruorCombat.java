package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.minigames.ZarosGodwars;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class CruorCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 13453 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		npc.setNextGraphics(new Graphics(3354));
		for (final Entity t : ZarosGodwars.nex.getPossibleTargets()) {
			Projectile projectile = World.sendProjectileNew(npc, t, 374, 41,
					25, 20, 3, 15, Utils.random(5));
			delayHit(
					npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()),
					t,
					getMagicHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MAGE, t)));
			t.setNextGraphics(new Graphics(375, projectile.getEndTime(), 0));
		}
		return npc.getAttackSpeed();
	}
}