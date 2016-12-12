package net.kagani.game.player.actions;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.player.Player;

public class DreamSpellAction extends Action {

	private boolean doneCycle;

	@Override
	public boolean start(Player player) {
		if (!process(player))
			return false;
		player.setNextAnimation(new Animation(6295));
		setActionDelay(player, 6);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (player.getHitpoints() == player.getMaxHitpoints())
			return false;
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		if (!doneCycle) {
			doneCycle = !doneCycle;
			player.setResting(-1);// sleep mode
		}
		player.setNextAnimation(new Animation(6296));
		player.setNextGraphics(new Graphics(277, 0, 80));
		return 3;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 1);
		player.setNextAnimation(new Animation(6297));// reset it.
		player.getEmotesManager().setNextEmoteEnd();
		player.setResting(0);
	}
}
