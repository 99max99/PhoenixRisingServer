package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.others.Legio;
import net.kagani.game.player.controllers.AscensionDungeon;

public class LegioBoss extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 17149, 17150, 17151, 17152, 17153, 17154 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		if (npc instanceof Legio) {
			Legio leg = (Legio) npc;
			int rate = 3;
			if (leg.getOrdinal() == 1) {
				if (leg.getHealthStage() == 1)
					rate = 4;
				else if (leg.getHealthStage() == 2)
					rate = 5;
				else if (leg.getHealthStage() == 3)
					rate = 200;
			}
			if (leg.getAttackStage() % rate == 0) {
				Hit damage = getMagicHit(npc,
						getMaxHit(npc, 4300, NPCCombatDefinitions.MAGE, target));
				leg.setNextAnimation(new Animation(20277));
				leg.setNextGraphics(new Graphics(3977));
				delayHit(
						npc,
						World.sendProjectileNew(leg, target, 3978, 30, 30, 15,
								4, 3, 3).getEndTime() / 30, npc, damage);
			} else {
				leg.setNextAnimation(new Animation(20260));
				leg.setNextGraphics(new Graphics(3975));
				final WorldTile targetPosition = new WorldTile(target.getX(),
						target.getY(), target.getPlane());
				if (leg.getOrdinal() == 0) {
					if (leg.getHealthStage() == 0)
						AscensionDungeon.handleLightning(leg, targetPosition,
								1500, 2500);
					else if (leg.getHealthStage() == 1)
						AscensionDungeon.handleLightning(leg, targetPosition,
								1800, 2700);
					else if (leg.getHealthStage() == 2)
						AscensionDungeon.handleLightning(leg, targetPosition,
								2000, 2800);
					else if (leg.getHealthStage() == 3)
						AscensionDungeon.handleLightning(leg, targetPosition,
								2200, 3000);
					else if (leg.getOrdinal() == 5)
						AscensionDungeon.handleLightning(leg, targetPosition,
								1500, 2050, true);
					else
						AscensionDungeon.handleLightning(leg, targetPosition,
								1500, 2050);
				}
			}
			leg.incrementAttackStage();
		} else
			npc.setNextForceTalk(new ForceTalk("I'm not REAL! memes."));
		return 4;
	}
}