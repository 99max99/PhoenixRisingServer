package net.kagani.game.player.cutscenes.actions;

import net.kagani.game.player.Player;
import net.kagani.game.player.cutscenes.Cutscene;

public class DestroyCachedObjectAction extends CutsceneAction {

	public DestroyCachedObjectAction(int cachedObjectIndex, int actionDelay) {
		super(cachedObjectIndex, actionDelay);
	}

	@Override
	public void process(Player player, Object[] cache) {
		Cutscene scene = (Cutscene) cache[0];
		scene.destroyCache(cache[getCachedObjectIndex()]);
	}

}
