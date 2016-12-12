package net.kagani.game.npc.combat.impl.dung;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.dungeonnering.DungeonBoss;
import net.kagani.game.npc.dungeonnering.IcyBones;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.utils.Utils;

public class IcyBonesCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Icy Bones" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		DungeonBoss boss = (DungeonBoss) npc;
		DungeonManager manager = boss.getManager();

		if (Utils.random(10) == 0) {
			npc.setNextAnimation(new Animation(13791, 20));
			npc.setNextGraphics(new Graphics(2594));
			boolean mage = Utils.random(2) == 0;
			if (mage && Utils.random(3) == 0) {
				target.setNextGraphics(new Graphics(2597));
				target.setBoundDelay(8, true);
			}
			if (mage)
				delayHit(
						npc,
						2,
						target,
						getMagicHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.MAGE,
										target)));
			else
				delayHit(
						npc,
						2,
						target,
						getRangeHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.RANGE,
										target)));
			World.sendProjectile(npc, target, 2595, 41, 16, 41, 40, 16, 0);
			return npc.getAttackSpeed();
		}
		if (Utils.random(3) == 0
				&& Utils.isOnRange(target.getX(), target.getY(),
						target.getSize(), npc.getX(), npc.getY(),
						npc.getSize(), 0) && ((IcyBones) npc).sendSpikes()) {
			npc.setNextGraphics(new Graphics(2596));
			npc.setNextAnimation(new Animation(13790));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
			return npc.getAttackSpeed();
		}
		boolean onRange = false;
		for (Player player : manager.getParty().getTeam()) {
			if (Utils.isOnRange(player.getX(), player.getY(), player.getSize(),
					npc.getX(), npc.getY(), npc.getSize(), 0)) {
				int damage = getMaxHit(npc, NPCCombatDefinitions.MELEE, player);
				if (damage != 0 && player.getPrayer().isMeleeProtecting())
					player.getPackets()
							.sendGameMessage(
									"Your prayer offers only partial protection against the attack.");
				delayHit(npc, 0, player, getMeleeHit(npc, damage));
				onRange = true;
			}
		}
		if (onRange) {
			npc.setNextAnimation(new Animation(defs.getAttackEmote(), 20));
			npc.setNextGraphics(new Graphics(defs.getAttackGfx()));
			return npc.getAttackSpeed();
		}
		return 0;
	}
}
