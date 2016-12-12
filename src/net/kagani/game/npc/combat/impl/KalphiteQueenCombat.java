package net.kagani.game.npc.combat.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class KalphiteQueenCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Kalphite Queen", "Exiled Kalphite Queen" };
	}

	public static void attackMageTarget(final List<Player> arrayList,
			Entity fromEntity, final NPC startTile, Entity t,
			final int projectile, final int gfx) {
		final Entity target = t == null ? getTarget(arrayList, fromEntity,
				startTile) : t;
		if (target == null)
			return;
		if (target instanceof Player)
			arrayList.add((Player) target);
		Projectile proj = World.sendProjectileNew(fromEntity, target,
				projectile, fromEntity == startTile ? 70 : 20, 20, 30, 6, 0, 0);
		int endTime = Utils.projectileTimeToCycles(proj.getEndTime()) - 1;
		delayHit(
				startTile,
				endTime,
				target,
				getMagicHit(startTile,
						getMaxHit(startTile, NPCCombatDefinitions.MAGE, target)));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				target.setNextGraphics(new Graphics(gfx));
				attackMageTarget(arrayList, target, startTile, null,
						projectile, gfx);
			}
		}, endTime);
	}

	private static void attackMageTarget(final List<Player> arrayList,
			Entity fromEntity, final NPC npc, Entity t) {
		final Entity target = t == null ? getTarget(arrayList, fromEntity, npc)
				: t;
		if (target == null)
			return;
		if (target instanceof Player)
			arrayList.add((Player) target);
		Projectile proj = fromEntity == npc ? World.sendProjectileNew(npc,
				target, 5048, npc.getId() == 1158 || npc.getId() == 16707 ? 58
						: 68, 30, 53, 1, 20, 50) : World.sendProjectileNew(
				fromEntity, target, 5048, 30, 30, 30, 3, 0, 0);
		int endTime = Utils.projectileTimeToCycles(proj.getEndTime()) - 1;
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				int damage = Utils.random(npc
						.getMaxHit(NPCCombatDefinitions.MAGE));
				// idk why this is needed lol
				/*
				 * if (target instanceof Player && ((Player)
				 * target).getPrayer().isMageProtecting()) damage /= 2;
				 */
				target.applyHit(new Hit(npc, damage, HitLook.MAGIC_DAMAGE));
				target.setNextGraphics(new Graphics(5049, 0, 100));
				attackMageTarget(arrayList, target, npc, null);
			}
		}, endTime);
	}

	public static Player getTarget(List<Player> list, final Entity fromEntity,
			NPC startTile) {
		if (fromEntity == null)
			return null;
		ArrayList<Player> added = new ArrayList<Player>();
		for (Entity entity : startTile.getPossibleTargets()) {
			if (!(entity instanceof Player))
				continue;
			Player player = (Player) entity;
			if (player == null || list.contains(player)
					|| !player.withinDistance(fromEntity)
					|| !player.withinDistance(startTile))
				continue;
			added.add(player);
		}
		if (added.isEmpty())
			return null;
		Collections.sort(added, new Comparator<Player>() {

			@Override
			public int compare(Player o1, Player o2) {
				if (o1 == null)
					return 1;
				if (o2 == null)
					return -1;
				if (Utils.getDistance(o1, fromEntity) > Utils.getDistance(o2,
						fromEntity))
					return 1;
				else if (Utils.getDistance(o1, fromEntity) < Utils.getDistance(
						o2, fromEntity))
					return -1;
				else
					return 0;
			}
		});
		return added.get(0);

	}

	private static final Graphics FIRST_MAGIC_START = new Graphics(5046, 0, 0,
			0, true), SECOND_MAGIC_START = new Graphics(5047),
			RANGE_START = new Graphics(5041);

	@Override
	public int attack(final NPC npc, final Entity target) {
		boolean secondForm = npc.getId() != 1158 && npc.getId() != 16707;
		int style = Utils.random(!Utils.isOnRange(npc.getX(), npc.getY(),
				npc.getSize(), target.getX(), target.getY(), target.getSize(),
				0) ? 1 : 0, 3);
		switch (style) {
		case 0:// Melee
			npc.setNextAnimation(new Animation(secondForm ? 24277 : 24275));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
			break;
		case 1:// Magic
			npc.setNextAnimation(new Animation(secondForm ? 24285 : 24281));
			npc.setNextGraphics(secondForm ? SECOND_MAGIC_START
					: FIRST_MAGIC_START);
			attackMageTarget(new ArrayList<Player>(), npc, npc, target);
			break;
		case 2:// Range
			npc.setNextAnimation(new Animation(secondForm ? 24284 : 24282));
			if (!secondForm)
				npc.setNextGraphics(RANGE_START);
			Projectile projectile = World.sendProjectile(npc, target, false,
					true, 0, secondForm ? 5044 : 5042, secondForm ? 30 : 58,
					30, 25, 2, 10, 0);
			for (Entity t : npc.getPossibleTargets()) {
				t.setNextGraphics(new Graphics(secondForm ? 5045 : 5043,
						projectile.getEndTime(), 100));
				int damage = Utils.random(npc
						.getMaxHit(NPCCombatDefinitions.RANGE));
				/*
				 * if (t instanceof Player && ((Player)
				 * t).getPrayer().isRangeProtecting()) damage /= 2;
				 */
				t.applyHit(new Hit(npc, damage, HitLook.RANGE_DAMAGE,
						projectile.getEndTime()));
			}
			break;
		}
		return npc.getAttackSpeed();
	}
}
