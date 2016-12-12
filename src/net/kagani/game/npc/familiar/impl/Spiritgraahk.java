package net.kagani.game.npc.familiar.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.Player;
import net.kagani.game.player.actions.Summoning.Pouch;

public class Spiritgraahk extends Familiar {

	private static final long serialVersionUID = 3032896343400261649L;

	public Spiritgraahk(Player owner, Pouch pouch, WorldTile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Groad";
	}

	@Override
	public String getSpecialDescription() {
		return "Attack the selected opponent at the cost of 3 special attack points.";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 3;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ENTITY;
	}

	@Override
	public boolean submitSpecial(Object object) {
		Entity entity = (Entity) object;
		if (getAttackedBy() != null) {
			getOwner().getPackets().sendGameMessage(
					"Your grahaak already has a target in its sights!");
			return false;
		}
		getOwner().setNextAnimation(new Animation(7660));
		getOwner().setNextGraphics(new Graphics(1316));
		this.getCombat().setTarget(entity);
		return false;
	}
}
