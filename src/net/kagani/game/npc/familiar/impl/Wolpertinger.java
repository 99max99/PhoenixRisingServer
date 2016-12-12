package net.kagani.game.npc.familiar.impl;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Summoning.Pouch;

public class Wolpertinger extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4097036858996221680L;

	public Wolpertinger(Player owner, Pouch pouch, WorldTile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Magic Focus";
	}

	@Override
	public String getSpecialDescription() {
		return "Boosts your restistance towards magic by 5% while also boosting your magic by 7%.";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 20;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		Player player = (Player) object;
		int newLevel = player.getSkills().getLevel(Skills.MAGIC) + 7;
		if (newLevel > player.getSkills().getLevelForXp(Skills.MAGIC) + 7)
			newLevel = player.getSkills().getLevelForXp(Skills.MAGIC) + 7;
		player.setNextGraphics(new Graphics(1300));
		player.setNextAnimation(new Animation(7660));
		player.getSkills().set(Skills.MAGIC, newLevel);
		return true;
	}
}
