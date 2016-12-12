package net.kagani.game.player.cutscenes;

import java.util.ArrayList;

import net.kagani.game.player.Player;
import net.kagani.game.player.cutscenes.actions.CutsceneAction;
import net.kagani.game.player.cutscenes.actions.LookCameraAction;
import net.kagani.game.player.cutscenes.actions.PosCameraAction;

public class DTPreview extends Cutscene {

	@Override
	public CutsceneAction[] getActions(Player player) {
		ArrayList<CutsceneAction> actionsList = new ArrayList<CutsceneAction>();
		actionsList.add(new LookCameraAction((3386), (3104), 1000, 6, 6, -1));
		actionsList.add(new PosCameraAction((3395), (3104), 5000, 8, 7, 5));
		actionsList.add(new LookCameraAction((3390), (3115), 6000, 6, 6, -1));
		actionsList.add(new PosCameraAction((3395), (3115), 6000, 8, 7, 5));
		actionsList.add(new LookCameraAction((3380), (3125), 6000, 6, 6, -1));
		actionsList.add(new PosCameraAction((3380), (3125), 6000, 6, 7, 5));
		actionsList.add(new LookCameraAction((3375), (3125), 6000, 6, 6, -1));
		actionsList.add(new PosCameraAction((3375), (3125), 6000, 6, 7, 5));
		actionsList.add(new LookCameraAction((3370), (3125), 6000, 6, 6, -1));
		actionsList.add(new PosCameraAction((3370), (3125), 6000, 6, 7, 5));
		actionsList.add(new LookCameraAction((3358), (3120), 5500, 6, 6, -1));
		actionsList.add(new PosCameraAction((3358), (3125), 5500, 6, 7, 5));
		actionsList.add(new LookCameraAction((3358), (3095), 4000, 6, 6, -1));
		actionsList.add(new PosCameraAction((3358), (3095), 4000, 8, 7, 5));
		actionsList.add(new LookCameraAction((3374), (3084), 2500, 6, 6, -1));
		actionsList.add(new PosCameraAction((3374), (3084), 2500, 8, 7, 5));
		actionsList.add(new LookCameraAction((3374), (3097), 2300, 9, 9, -1));
		actionsList.add(new PosCameraAction((3374), (3097), 2300, 8, 7, 5));
		return actionsList.toArray(new CutsceneAction[actionsList.size()]);
	}

	@Override
	public boolean hiddenMinimap() {
		return false;
	}

}
