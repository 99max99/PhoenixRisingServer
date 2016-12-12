package net.kagani.game.npc.familiar.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.Player;
import net.kagani.game.player.actions.Summoning.Pouch;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class Giantent extends Familiar {

	private static final long serialVersionUID = -743802297641852412L;

	public Giantent(Player owner, Pouch pouch, WorldTile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Acorn Missile";
	}

	@Override
	public String getSpecialDescription() {
		return "Hits all players around a tile radius (not including you) with damage that could inflict up to 187 points.";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 6;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ENTITY;
	}

	@Override
	public boolean submitSpecial(Object object) {
		final Entity target = (Entity) object;
		final Familiar npc = this;
		Player player = getOwner();
		player.setNextAnimation(new Animation(7858));
		player.setNextGraphics(new Graphics(1316));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						target.applyHit(new Hit(getOwner(), Utils.random(150),
								HitLook.MAGIC_DAMAGE));
						target.setNextGraphics(new Graphics(1363));
					}
				}, 2);
				World.sendProjectile(npc, target, 1362, 34, 16, 30, 35, 16, 0);
			}
		}, 1);
		return true;
	}
}
