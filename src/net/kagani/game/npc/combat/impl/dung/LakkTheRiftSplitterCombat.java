package net.kagani.game.npc.combat.impl.dung;

import java.util.LinkedList;
import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.EffectsManager.Effect;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.dungeonnering.LakkTheRiftSplitter;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class LakkTheRiftSplitterCombat extends CombatScript {

	private static final int[] VOICES = { 3034, 2993, 3007 };
	private static final String[] MESSAGES = {
			"A flame portal will flush you out!", "Taste miasma!",
			"This will cut you down to size!" };

	@Override
	public Object[] getKeys() {
		return new Object[] { 9898 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final LakkTheRiftSplitter boss = (LakkTheRiftSplitter) npc;

		DungeonManager manager = boss.getManager();

		boolean smash = false;
		for (Player player : manager.getParty().getTeam()) {
			if (Utils.colides(player.getX(), player.getY(), player.getSize(),
					npc.getX(), npc.getY(), npc.getSize())) {
				smash = true;
				player.getEffectsManager().startEffect(
						new Effect(EffectType.PROTECTION_DISABLED, 8));
				delayHit(
						npc,
						0,
						player,
						getRegularHit(
								npc,
								getMaxHit(
										npc,
										(int) (npc
												.getMaxHit(NPCCombatDefinitions.MELEE) * .85),
										NPCCombatDefinitions.MELEE, player)));
				delayHit(
						npc,
						0,
						player,
						getRegularHit(
								npc,
								getMaxHit(
										npc,
										(int) (npc
												.getMaxHit(NPCCombatDefinitions.MELEE) * .60),
										NPCCombatDefinitions.MELEE, player)));
			}
		}
		if (smash) {
			npc.setNextAnimation(new Animation(14383));
			return 5;
		}

		if (Utils.random(4) == 0) {
			final int type = Utils.random(3);
			switch (type) {
			case 0:
			case 1:
			case 2:
				final List<WorldTile> boundary = new LinkedList<WorldTile>();
				for (int x = -1; x < 2; x++) {// 3x3 area
					for (int y = -1; y < 2; y++) {
						boundary.add(target.transform(x, y, 0));
					}
				}
				if (boss.doesBoundaryOverlap(boundary)) {
					regularMagicAttack(target, npc);
					return 5;
				}
				npc.playSoundEffect(VOICES[type]);
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						boss.setNextForceTalk(new ForceTalk(MESSAGES[type]));
						boss.setNextAnimation(new Animation(14398));
						boss.addPortalCluster(type,
								boundary.toArray(new WorldTile[1]));
					}
				}, 1);
				return 5;
			}
		}

		// melee or magic
		boolean onRange = Utils.isOnRange(npc.getX(), npc.getY(),
				npc.getSize(), target.getX(), target.getY(), target.getSize(),
				0);
		boolean melee = onRange && Utils.random(2) == 0;
		if (melee) {
			npc.setNextAnimation(new Animation(14375));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
		} else
			regularMagicAttack(target, npc);
		return 5;
	}

	private void regularMagicAttack(Entity target, NPC npc) {
		npc.setNextAnimation(new Animation(14398));
		World.sendProjectile(npc, target, 2579, 50, 30, 41, 40, 0, 0);
		if (target instanceof Player) {
			Player player = (Player) target;
			int damage = getMaxHit(npc, NPCCombatDefinitions.MAGE, player);
			if (player.getPrayer().getPrayerpoints() > 0
					&& player.getPrayer().isMageProtecting()) {
				player.getPrayer().drainPrayer((int) (damage * .5));
				player.getPackets().sendGameMessage(
						"Your prayer points feel drained.");
			} else
				delayHit(npc, 1, player, getMagicHit(npc, damage));
		}
		target.setNextGraphics(new Graphics(2580, 75, 0));
	}
}
