package net.kagani.game.npc.combat.impl.dung;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.EffectsManager.Effect;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.dungeonnering.Rammernaut;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.utils.Utils;

public class RammernautCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 9767 };
	}

	public static int getChargeCount(NPC npc) {

		Integer charge = (Integer) npc.getTemporaryAttributtes().get(
				Key.RAMMERNAUT_CHARGE);

		return charge == null ? 0 : charge;

	}

	public static void setChargeCount(NPC npc, int count) {
		npc.getTemporaryAttributtes().put(Key.RAMMERNAUT_CHARGE, count);

	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int chargeCount = getChargeCount(npc);

		if (chargeCount > 1 && target instanceof Player) {
			((Rammernaut) npc).setChargeTarget((Player) target);
			setChargeCount(npc, 0);
			return 0;
		}

		if (!Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(),
				target.getX(), target.getY(), target.getSize(), 0)) {
			setChargeCount(npc, chargeCount + 1);
			return 3;
		}
		setChargeCount(npc, Utils.random(10) == 0 ? 2 : 0); // 1 in 10 change
															// charging next att

		if (Utils.random(5) == 0) {
			npc.setNextAnimation(new Animation(13705));
			for (Entity entity : npc.getPossibleTargets()) {
				if (!Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(),
						entity.getX(), entity.getY(), entity.getSize(), 0))
					continue;
				((Rammernaut) npc).applyStunHit(entity,
						npc.getMaxHit(NPCCombatDefinitions.MELEE));
			}
			return npc.getAttackSpeed();
		}

		if (((Rammernaut) npc).isRequestSpecNormalAttack()
				&& target instanceof Player) {
			((Rammernaut) npc).setRequestSpecNormalAttack(false);
			Player player = (Player) target;
			player.getPackets().sendGameMessage(
					"Your prayers have been disabled.");
			player.getEffectsManager().startEffect(
					new Effect(EffectType.PROTECTION_DISABLED, 8));
			player.getPackets().sendGameMessage("Your defence been reduced.");
			player.getSkills().drainLevel(Skills.DEFENCE, Utils.random(3) + 1);

		}

		// default melee attack can be protected with prayer
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		delayHit(
				npc,
				0,
				target,
				getMeleeHit(npc,
						getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
		return npc.getAttackSpeed();
	}
}
