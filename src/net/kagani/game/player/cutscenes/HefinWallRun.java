package net.kagani.game.player.cutscenes;

import java.util.ArrayList;

import net.kagani.game.player.Player;
import net.kagani.game.player.cutscenes.actions.CutsceneAction;

public class HefinWallRun extends Cutscene {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public boolean hiddenMinimap() {
		return false;
	}

	@Override
	public CutsceneAction[] getActions(Player player) {
		ArrayList<CutsceneAction> actionsList = new ArrayList<CutsceneAction>();
		return actionsList.toArray(new CutsceneAction[actionsList.size()]);
	}
}