package net.kagani.game.player.cutscenes.actions;

import net.kagani.game.Graphics;
import net.kagani.game.player.Player;

public class PlayerGraphicAction extends CutsceneAction {

	private Graphics gfx;

	public PlayerGraphicAction(Graphics gfx, int actionDelay) {
		super(-1, actionDelay);
		this.gfx = gfx;
	}

	@Override
	public void process(Player player, Object[] cache) {
		player.setNextGraphics(gfx);
	}

}
