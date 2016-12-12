package net.kagani.game.player.cutscenes;

import java.util.ArrayList;

import net.kagani.game.player.Player;
import net.kagani.game.player.cutscenes.actions.CutsceneAction;
import net.kagani.game.player.cutscenes.actions.DialogueAction;
import net.kagani.game.player.cutscenes.actions.LookCameraAction;
import net.kagani.game.player.cutscenes.actions.PosCameraAction;

public class CorporealBeastScene extends Cutscene {

	@Override
	public boolean hiddenMinimap() {
		return true;
	}

	@Override
	public boolean allowSkipCutscene() {
		return false;
	}

	@Override
	public CutsceneAction[] getActions(Player player) {
		ArrayList<CutsceneAction> actionsList = new ArrayList<CutsceneAction>();
		actionsList.add(new LookCameraAction(2993, 4378, 1000, -1));
		actionsList.add(new PosCameraAction(2984, 4383, 5000, -1));
		actionsList.add(new DialogueAction("You peek throught the door."));
		return actionsList.toArray(new CutsceneAction[actionsList.size()]);
	}

}
