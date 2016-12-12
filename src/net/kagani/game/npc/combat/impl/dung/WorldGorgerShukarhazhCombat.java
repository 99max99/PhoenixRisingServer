package net.kagani.game.npc.combat.impl.dung;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.dungeonnering.WorldGorgerShukarhazh;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.utils.Utils;

public class WorldGorgerShukarhazhCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 12478 };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		final WorldGorgerShukarhazh boss = (WorldGorgerShukarhazh) npc;
		final DungeonManager manager = boss.getManager();

		boolean smash = false;
		for (Player player : manager.getParty().getTeam()) {
			if (Utils.colides(player.getX(), player.getY(), player.getSize(),
					npc.getX(), npc.getY(), npc.getSize())) {
				smash = true;
				player.getPackets().sendGameMessage(
						"The creature crushes you as you move underneath it.");
				delayHit(
						npc,
						0,
						player,
						getRegularHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.MELEE,
										player)));
			}
		}
		if (smash) {
			npc.setNextAnimation(new Animation(14894));
			return 6;
		}

		if (Utils.random(manager.getParty().getTeam().size() > 1 ? 20 : 5) == 0
				&& Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(),
						target.getX(), target.getY(), target.getSize(), 0)) {
			npc.setNextAnimation(new Animation(14892));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
		} else {
			npc.setNextAnimation(new Animation(14893));
			npc.setNextGraphics(new Graphics(2846, 0, 100));
			target.setNextGraphics(new Graphics(2848, 75, 100));
			delayHit(
					npc,
					2,
					target,
					getMagicHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MAGE, target)));
		}
		return 6;
	}
}
