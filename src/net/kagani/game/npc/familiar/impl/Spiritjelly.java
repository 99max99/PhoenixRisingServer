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
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Summoning.Pouch;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class Spiritjelly extends Familiar {

	private static final long serialVersionUID = 3986276126369633942L;

	public Spiritjelly(Player owner, Pouch pouch, WorldTile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Dissolve";
	}

	@Override
	public String getSpecialDescription() {
		return "A magic attack that does up to 136 magic damage and drains the target's attack stat.";
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
	public boolean submitSpecial(Object object) {// TODO get special anim
		final Entity target = (Entity) object;
		Player player = getOwner();
		final int damage = Utils.random(100);
		player.setNextAnimation(new Animation(7660));
		player.setNextGraphics(new Graphics(1316));
		World.sendProjectile(this, target, 1359, 34, 16, 30, 35, 16, 0);
		if (damage > 20)
			if (target instanceof Player)
				((Player) target).getSkills().set(
						Skills.ATTACK,
						((Player) target).getSkills().getLevel(Skills.ATTACK)
								- (damage / 20));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				target.applyHit(new Hit(getOwner(), damage,
						HitLook.MAGIC_DAMAGE));
				target.setNextGraphics(new Graphics(1360));
			}
		}, 2);
		return true;
	}
}
