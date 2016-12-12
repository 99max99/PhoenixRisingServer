package net.kagani.game.player.cutscenes;

import java.util.ArrayList;

import net.kagani.game.player.Player;
import net.kagani.game.player.cutscenes.actions.CutsceneAction;
import net.kagani.game.player.cutscenes.actions.CutsceneCodeAction;
import net.kagani.game.player.cutscenes.actions.DialogueAction;
import net.kagani.game.player.cutscenes.actions.LookCameraAction;
import net.kagani.game.player.cutscenes.actions.MovePlayerAction;
import net.kagani.game.player.cutscenes.actions.PosCameraAction;

public class DZGuideScene extends Cutscene {

	@Override
	public boolean hiddenMinimap() {
		return true;
	}

	@Override
	public boolean showYourselfToOthers() {
		return false;
	}

	@Override
	public int getMapSize() {
		return 3;
	}

	// exeption
	@Override
	public void stopCutscene(Player player) {
		super.stopCutscene(player);
		player.getMusicsManager().reset();
	}

	@Override
	public CutsceneAction[] getActions(Player player) {
		ArrayList<CutsceneAction> actionsList = new ArrayList<CutsceneAction>();

		actionsList.add(new CutsceneCodeAction(new Runnable() {

			@Override
			public void run() {
				player.getMusicsManager().forcePlayMusic(1207);
			}

		}, -1));

		// player starts walking and camera goes ahead
		actionsList.add(new MovePlayerAction(3787, 4360, 1,
				Player.TELE_MOVE_TYPE, 0));
		actionsList.add(new MovePlayerAction(3787, 4379, false, -1));

		actionsList.add(new LookCameraAction(3787, 4365, 1000, 6, 6, -1));
		actionsList.add(new PosCameraAction(3787, 4350, 5000, 6, 6, 3));

		actionsList.add(new LookCameraAction(3787, 4385, 1000, 6, 6, -1));
		actionsList.add(new PosCameraAction(3787, 4370, 5000, 6, 6, 10));

		actionsList
				.add(new DialogueAction(
						"Zone 1 - Entrance: You can find grand exchange and teleports here."));

		actionsList.add(new LookCameraAction(3753, 4387, 1000, 6, 6, -1));
		actionsList.add(new PosCameraAction(3767, 4385, 5000, 6, 6, 3));

		actionsList.add(new MovePlayerAction(3768, 4385, 1,
				Player.TELE_MOVE_TYPE, 0));
		actionsList.add(new MovePlayerAction(3757, 4394, false, 10));

		actionsList
				.add(new DialogueAction(
						"Zone 2 - Mining site: You can find all kind of rocks here. Plus, mineral deposits wont deplete."));

		actionsList.add(new LookCameraAction(3757, 4418, 1000, 6, 6, -1));
		actionsList.add(new PosCameraAction(3757, 4406, 1500, 6, 6, 3));

		actionsList.add(new MovePlayerAction(3757, 4403, 1,
				Player.TELE_MOVE_TYPE, 0));
		actionsList.add(new MovePlayerAction(3757, 4419, false, 10));

		actionsList
				.add(new DialogueAction(
						"Zone 3 - Runecrafting / Construction / Prayer: You can find rune essence mines, zmi altar, portals to runespan, guilded altar and set your house here."));

		actionsList.add(new LookCameraAction(3768, 4429, 1000, -1, -1, -1));
		actionsList.add(new PosCameraAction(3758, 4429, 1500, -1, -1, -1));

		actionsList.add(new LookCameraAction(3785, 4429, 1000, 6, 6, -1));
		actionsList.add(new PosCameraAction(3779, 4429, 5000, 6, 6, 3));

		actionsList.add(new MovePlayerAction(3768, 4429, 1,
				Player.TELE_MOVE_TYPE, 0));
		actionsList.add(new MovePlayerAction(3785, 4429, false, 10));

		actionsList
				.add(new DialogueAction(
						"Zone 4 - Woodcutting. Every tree you could hope for is here! You can always go back to sawmill if you don't like it here."));

		actionsList.add(new LookCameraAction(3807, 4422, 1000, -1, -1, -1));
		actionsList.add(new PosCameraAction(3807, 4427, 1500, -1, -1, -1));

		actionsList.add(new LookCameraAction(3807, 4399, 1000, 6, 6, -1));
		actionsList.add(new PosCameraAction(3807, 4411, 5000, 6, 6, 3));

		actionsList.add(new MovePlayerAction(3807, 4417, 1,
				Player.TELE_MOVE_TYPE, 0));
		actionsList.add(new MovePlayerAction(3807, 4405, false, 10));

		actionsList.add(new DialogueAction(
				"Zone 5 - Fishing. Fish, fish, fish!"));

		actionsList.add(new MovePlayerAction(3810, 4393, 1,
				Player.TELE_MOVE_TYPE, 0));
		actionsList.add(new LookCameraAction(3811, 4380, 1000, -1, -1, -1));
		actionsList.add(new PosCameraAction(3811, 4380, 10000, -1, -1, -1));

		actionsList
				.add(new DialogueAction(
						"Zone 6 - Crafting/Smith/Cooking/Firemaking. Lots of things to do here!"));

		actionsList.add(new MovePlayerAction(3787, 4388, 1,
				Player.TELE_MOVE_TYPE, 0));

		actionsList.add(new LookCameraAction(3787, 4402, 1000, -1, -1, -1));
		actionsList.add(new PosCameraAction(3787, 4393, 1500, -1, -1, -1));

		actionsList.add(new DialogueAction(
				"Zone 7 - Obelisk! Train your summoning here!"));

		actionsList.add(new LookCameraAction(3787, 4391, 1000, -1, -1, -1));
		actionsList.add(new PosCameraAction(3787, 4380, 2500, 6, 6, 10));

		actionsList.add(new DialogueAction(
				"And this concludes donator zone tutorial."));
		actionsList.add(new DialogueAction(
				"If you want to replay this scene talk to me again."));

		return actionsList.toArray(new CutsceneAction[actionsList.size()]);
	}
}