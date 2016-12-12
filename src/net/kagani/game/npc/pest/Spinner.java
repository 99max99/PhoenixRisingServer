package net.kagani.game.npc.pest;

import net.kagani.game.Animation;
import net.kagani.game.EffectsManager;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.minigames.pest.PestControl;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

@SuppressWarnings("serial")
public class Spinner extends PestMonsters {

	private byte healTicks;

	public Spinner(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned, int index,
			PestControl manager) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned,
				index, manager);
	}

	@Override
	public void processNPC() {
		PestPortal portal = manager.getPortals()[portalIndex];
		if (portal.isDead()) {
			explode();
			return;
		}
		if (!portal.isLocked()) {
			healTicks++;
			if (!withinDistance(portal, 1))
				this.addWalkSteps(portal.getX(), portal.getY());
			else if (healTicks % 6 == 0)
				healPortal(portal);
		}
	}

	private void healPortal(final PestPortal portal) {
		setNextFaceEntity(portal);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				setNextAnimation(new Animation(3911));
				setNextGraphics(new Graphics(658, 0, 96 << 16));
				if (portal.getHitpoints() != 0)
					portal.heal((portal.getMaxHitpoints() / portal
							.getHitpoints()) * 45);
			}
		});
	}

	private void explode() {
		final NPC npc = this;
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				for (Player player : manager.getPlayers()) {
					if (!withinDistance(player, 7))
						continue;
					EffectsManager.makePoisoned(player, 50);
					player.applyHit(new Hit(npc, 50, HitLook.REGULAR_DAMAGE));
					npc.reset();
					npc.finish();
				}
			}
		}, 1);
	}
}
