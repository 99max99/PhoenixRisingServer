package net.kagani.game.npc.combat.impl.dung;

import java.util.LinkedList;
import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.dungeonnering.WarpedGulega;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class WarpedGulegaCombat extends CombatScript {

	private static final Graphics MELEE = new Graphics(2878);

	@Override
	public Object[] getKeys() {
		return new Object[] { 12737 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final WarpedGulega boss = (WarpedGulega) npc;

		int style = Utils.random(4);
		switch (style) {
		case 3:// reg aeo melee
			npc.setNextAnimation(new Animation(15004));

			final List<WorldTile> attackTiles = new LinkedList<WorldTile>();
			for (Entity t : boss.getPossibleTargets(true, true))
				attackTiles.add(new WorldTile(t));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					for (WorldTile tile : attackTiles)
						World.sendGraphics(npc, MELEE, tile);
					for (Entity t : boss.getPossibleTargets(true, true)) {
						tileLoop: for (WorldTile tile : attackTiles) {
							if (t.getX() == tile.getX()
									&& t.getY() == tile.getY()) {
								delayHit(
										npc,
										0,
										t,
										getMeleeHit(
												npc,
												getMaxHit(
														npc,
														(int) (npc
																.getMaxHit(NPCCombatDefinitions.MELEE) * 0.75),
														NPCCombatDefinitions.MELEE,
														t)));
								break tileLoop;
							}
						}
					}
				}
			});
			break;
		case 1:// reg range aeo
			npc.setNextAnimation(new Animation(15001));
			npc.setNextGraphics(new Graphics(2882));
			for (Entity t : npc.getPossibleTargets(true, true)) {
				World.sendProjectile(npc, t, 2883, 75, 25, 30, 20, 15, 3);
				t.setNextGraphics(new Graphics(2884, 90, 0));
				delayHit(
						npc,
						2,
						t,
						getRangeHit(
								npc,
								getMaxHit(
										npc,
										(int) (npc
												.getMaxHit(NPCCombatDefinitions.MELEE) * 0.75),
										NPCCombatDefinitions.RANGE, t)));
			}
			break;
		case 2:// reg magic aeo
			npc.setNextAnimation(new Animation(15007));
			for (Entity t : npc.getPossibleTargets(true, true)) {
				World.sendProjectile(npc, t, 2880, 150, 75, 30, 35, 15, 1);
				t.setNextGraphics(new Graphics(2881, 90, 0));
				delayHit(
						npc,
						2,
						t,
						getMagicHit(
								npc,
								getMaxHit(
										npc,
										(int) (npc
												.getMaxHit(NPCCombatDefinitions.MELEE) * 0.75),
										NPCCombatDefinitions.MAGE, t)));
			}
			break;
		case 0:
			npc.setNextAnimation(new Animation(15004));
			WorldTasksManager.schedule(new WorldTask() {

				WorldTile center;
				int cycles;

				@Override
				public void run() {
					cycles++;

					if (cycles == 1) {
						center = new WorldTile(target);
						sendTenticals(boss, center, 2);
					} else if (cycles == 3)
						sendTenticals(boss, center, 1);
					else if (cycles == 5)
						sendTenticals(boss, center, 0);
					else if (cycles == 6) {
						for (Entity t : npc.getPossibleTargets(true, true)) {
							if (t.getX() == center.getX()
									&& t.getY() == center.getY())
								t.applyHit(new Hit(npc, t.getHitpoints() - 1,
										HitLook.REGULAR_DAMAGE));
						}
						stop();
						return;
					}
				}
			}, 0, 0);
			return 7;
		}
		return 4;
	}

	private void sendTenticals(NPC npc, WorldTile center, int stage) {
		if (stage == 0) {
			World.sendGraphics(npc, MELEE, center);
		} else if (stage == 2 || stage == 1) {
			World.sendGraphics(npc, MELEE, center.transform(-stage, stage, 0));
			World.sendGraphics(npc, MELEE, center.transform(stage, stage, 0));
			World.sendGraphics(npc, MELEE, center.transform(-stage, -stage, 0));
			World.sendGraphics(npc, MELEE, center.transform(stage, -stage, 0));
		}
	}
}
