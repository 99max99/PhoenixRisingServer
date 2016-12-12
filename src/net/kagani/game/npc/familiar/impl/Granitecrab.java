package net.kagani.game.npc.familiar.impl;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Summoning.Pouch;

public class Granitecrab extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 649164679697311630L;

	public Granitecrab(Player owner, Pouch pouch, WorldTile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Stony Shell";
	}

	@Override
	public String getSpecialDescription() {
		return "Increases your restance to all attacks by four.";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 12;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		Player player = (Player) object;
		int newLevel = player.getSkills().getLevel(Skills.DEFENCE) + 4;
		if (newLevel > player.getSkills().getLevelForXp(Skills.DEFENCE) + 4)
			newLevel = player.getSkills().getLevelForXp(Skills.DEFENCE) + 4;
		player.setNextGraphics(new Graphics(1300));
		player.setNextAnimation(new Animation(7660));
		setNextGraphics(new Graphics(8108));
		setNextAnimation(new Animation(1326));
		player.getSkills().set(Skills.DEFENCE, newLevel);
		return true;
	}

}
