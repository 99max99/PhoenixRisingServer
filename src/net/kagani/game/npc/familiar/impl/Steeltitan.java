package net.kagani.game.npc.familiar.impl;

import net.kagani.game.Animation;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.Player;
import net.kagani.game.player.actions.Summoning.Pouch;

public class Steeltitan extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6377458256826528627L;

	public Steeltitan(Player owner, Pouch pouch, WorldTile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		setNextAnimation(new Animation(8188));
	}

	@Override
	public int getSpecialAmount() {
		return 12;
	}

	@Override
	public String getSpecialName() {
		return "Steel of Legends";
	}

	@Override
	public String getSpecialDescription() {
		return "Defence boost only applies to melee attacks. Scroll initiates attack on opponent, hitting four times, with either ranged or melee, depending on the distance to the target";
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ENTITY;
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public boolean submitSpecial(Object object) {
		return true;
	}
}
