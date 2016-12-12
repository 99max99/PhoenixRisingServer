package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.dungeonnering.DungeonSlayerNPC;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.utils.Utils;

public class Soulgazer extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 10705 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		DungeonSlayerNPC dungeonNPC = (DungeonSlayerNPC) npc;
		DungeonManager manager = dungeonNPC.getManager();
		if (manager.isDestroyed())
			return -1;
		npc.setNextAnimation(new Animation(13779));
		for (Player player : manager.getParty().getTeam()) {
			if (!player.withinDistance(npc, 8)
					|| !npc.clipedProjectile(target, true))
				continue;
			Projectile projectile = World.sendProjectileNew(npc, target, 2615,
					41, 16, 35, 2, 10, 0);
			delayHit(
					npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target,
					getMagicHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MAGE, target)));
		}
		return npc.getAttackSpeed();
	}
}
