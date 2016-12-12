package net.kagani.game.npc.combat.impl.dung;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.dungeonnering.FrozenAdventurer;
import net.kagani.game.npc.dungeonnering.ToKashBloodChiller;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class ToKashBloodChillerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 10024 };
	}

	@Override
	public int attack(final NPC npc, Entity target) {
		final ToKashBloodChiller boss = (ToKashBloodChiller) npc;
		final DungeonManager manager = boss.getManager();

		boolean perfectDamage = false;

		if (target instanceof Player) {
			Player player = (Player) target;
			if (player.getAppearence().isNPC())
				perfectDamage = true;
		}

		if (perfectDamage) {
			((Player) target).getAppearence().transformIntoNPC(-1);
			target.applyHit(new Hit(npc, (int) Utils.random(
					boss.getMaxHit() * .90, boss.getMaxHit()),
					HitLook.MAGIC_DAMAGE));
		}

		boolean special = boss.canSpecialAttack() && Utils.random(10) == 0;

		if (special) {
			npc.setNextForceTalk(new ForceTalk(
					"Sleep now, in the bitter cold..."));
			npc.playSoundEffect(2896);
			boss.setSpecialAttack(true);
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					npc.setNextForceTalk(new ForceTalk("DEEP FREEZE!"));
					npc.setNextAnimation(new Animation(14396));
					npc.setNextGraphics(new Graphics(2544));
					for (Entity t : boss.getPossibleTargets())
						setSpecialFreeze((Player) t, boss, manager);
				}
			}, 3);
			return 8;
		} else {
			boolean meleeAttack = perfectDamage || Utils.random(3) == 0;

			if (meleeAttack) {
				npc.setNextAnimation(new Animation(14392));
				delayHit(
						npc,
						0,
						target,
						getMeleeHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.MELEE,
										target)));
			} else {
				npc.setNextAnimation(new Animation(14398));
				World.sendProjectile(npc, target, 2546, 16, 16, 41, 30, 0, 0);
				delayHit(
						npc,
						1,
						target,
						getMagicHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.MAGE,
										target)));
			}
			return meleeAttack ? 4 : 5;
		}
	}

	public static void setSpecialFreeze(final Player player,
			final ToKashBloodChiller boss, DungeonManager dungManager) {
		player.resetWalkSteps();
		player.stopAll();
		player.lock();
		player.setNextGraphics(new Graphics(2545));
		player.getAppearence().transformIntoNPC(10022);
		FrozenAdventurer npc = new FrozenAdventurer(10023, player, -1, false);
		npc.setPlayer(player);
		player.getPackets().sendGameMessage("You have been frozen solid!");
		WorldTasksManager.schedule(new WorldTask() {

			int counter = 0;

			@Override
			public void run() {
				boss.setSpecialAttack(false);
				for (Entity t : boss.getPossibleTargets()) {
					Player player = (Player) t;
					if (player.isLocked()) {
						counter++;
						player.getAppearence().transformIntoNPC(-1);
					}
				}
				if (counter == 0)
					return;
				boss.setNextForceTalk(new ForceTalk("I will shatter your soul!"));
				boss.setNextGraphics(new Graphics(2549, 5, 100));
			}
		}, 5 * dungManager.getParty().getTeam().size());
	}

	public static void removeSpecialFreeze(Player player) {
		player.unlock();
		player.getAppearence().transformIntoNPC(-1);
		player.setNextGraphics(new Graphics(2548));
		player.getPackets().sendGameMessage(
				"The ice encasing you shatters violently.");
	}
}
