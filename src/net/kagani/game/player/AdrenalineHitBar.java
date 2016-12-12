package net.kagani.game.player;

import net.kagani.game.HitBar;

public class AdrenalineHitBar extends HitBar {

	public AdrenalineHitBar(Player player) {
		this.player = player;
	}

	private Player player;

	@Override
	public int getPercentage() {
		return player.getCombatDefinitions().getSpecialAttackPercentage() * 255 / 100;
	}

	@Override
	public int getType() {
		return 7;
	}

	@Override
	public boolean display(Player player) {
		return true;
	}
}