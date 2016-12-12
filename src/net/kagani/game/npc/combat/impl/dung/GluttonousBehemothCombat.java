package net.kagani.game.npc.combat.impl.dung;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.dungeonnering.DungeonBoss;
import net.kagani.game.npc.dungeonnering.GluttonousBehemoth;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.player.content.dungeoneering.RoomReference;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class GluttonousBehemothCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Gluttonous behemoth" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		DungeonBoss boss = (DungeonBoss) npc;
		DungeonManager manager = boss.getManager();
		boolean lessThanHalf = npc.getHitpoints() < npc.getMaxHitpoints() * .5;
		if (lessThanHalf
				&& npc.getTemporaryAttributtes().get(Key.GLUTTONOUS_HEALING) == null) {
			RoomReference reference = manager.getCurrentRoomReference(npc);
			WorldObject food1 = manager.getObject(reference, 49283, 0, 11);
			WorldObject food2 = manager.getParty().getTeam().size() <= 1 ? null
					: manager.getObject(reference, 49283, 11, 11);
			WorldObject food = food1;
			if (food1 != null) {
				for (Player player : manager.getParty().getTeam()) {
					if (player.withinDistance(food1, food1.getDefinitions()
							.getSizeX() + 1)) {
						food = null;
						break;
					}
				}
			}
			if (food == null && food2 != null) {
				food = food2;
				for (Player player : manager.getParty().getTeam()) {
					if (player.withinDistance(food2, food1.getDefinitions()
							.getSizeX() + 1)) {
						food = null;
						break;
					}
				}
			}
			if (food != null) {
				npc.getTemporaryAttributtes().put(Key.GLUTTONOUS_HEALING, true);
				((GluttonousBehemoth) npc).setHeal(food);
				return 0;
			}
		}
		boolean stomp = false;
		for (Player player : manager.getParty().getTeam()) {
			if (Utils.colides(player.getX(), player.getY(), player.getSize(),
					npc.getX(), npc.getY(), npc.getSize())) {
				stomp = true;
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
		if (stomp) {
			npc.setNextAnimation(new Animation(13718));
			return npc.getAttackSpeed();
		}
		int attackStyle = Utils.random(2);
		if (attackStyle == 2) {
			if (!Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(),
					target.getX(), target.getY(), target.getSize(), 0))
				attackStyle = Utils.random(1);
			else {
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				delayHit(
						npc,
						0,
						target,
						getMeleeHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.MELEE,
										target)));
				return npc.getAttackSpeed();
			}
		}
		if (attackStyle == 0) {
			npc.setNextAnimation(new Animation(13719));
			World.sendProjectile(npc, target, 2612, 41, 16, 41, 35, 16, 0);
			int damage = getMaxHit(npc, NPCCombatDefinitions.MAGE, target);
			delayHit(npc, 2, target, getMagicHit(npc, damage));
			if (damage != 0) {
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						target.setNextGraphics(new Graphics(2613));
					}
				}, 1);
			}
		} else if (attackStyle == 1) {
			npc.setNextAnimation(new Animation(13721));
			World.sendProjectile(npc, target, 2610, 41, 16, 41, 35, 16, 0);
			delayHit(
					npc,
					2,
					target,
					getRangeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.RANGE, target)));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					target.setNextGraphics(new Graphics(2611));
				}
			}, 1);
		}
		return npc.getAttackSpeed();
	}
}
