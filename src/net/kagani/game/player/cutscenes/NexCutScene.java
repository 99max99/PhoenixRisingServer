package net.kagani.game.player.cutscenes;

import java.util.ArrayList;

import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;
import net.kagani.game.player.cutscenes.actions.CutsceneAction;
import net.kagani.game.player.cutscenes.actions.LookCameraAction;
import net.kagani.game.player.cutscenes.actions.PosCameraAction;

public class NexCutScene extends Cutscene {

	private WorldTile dir;
	private int selected;

	public NexCutScene(WorldTile dir, int selected) {
		this.dir = dir;
		this.selected = selected;
	}

	@Override
	public boolean hiddenMinimap() {
		return false;
	}

	@Override
	public boolean allowSkipCutscene() {
		return false;
	}

	@Override
	public CutsceneAction[] getActions(Player player) {
		int xExtra = 0;
		int yExtra = 0;
		if (selected == 0)
			yExtra -= 10;
		else if (selected == 2)
			yExtra += 10;
		else if (selected == 1)
			xExtra -= 10;
		else
			xExtra += 10;
		ArrayList<CutsceneAction> actionsList = new ArrayList<CutsceneAction>();
		actionsList.add(new PosCameraAction((2925 + xExtra), 5203 + yExtra,
				2600, -1));
		actionsList
				.add(new LookCameraAction((dir.getX()), dir.getY(), 2500, 3));
		return actionsList.toArray(new CutsceneAction[actionsList.size()]);
	}

}
