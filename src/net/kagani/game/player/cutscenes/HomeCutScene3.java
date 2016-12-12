package net.kagani.game.player.cutscenes;

import java.util.ArrayList;

import net.kagani.game.Animation;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;
import net.kagani.game.player.controllers.DungeonController;
import net.kagani.game.player.controllers.HouseControler;
import net.kagani.game.player.cutscenes.actions.CreateNPCAction;
import net.kagani.game.player.cutscenes.actions.CutsceneAction;
import net.kagani.game.player.cutscenes.actions.CutsceneCodeAction;
import net.kagani.game.player.cutscenes.actions.MovePlayerAction;
import net.kagani.game.player.cutscenes.actions.NPCFaceTileAction;
import net.kagani.game.player.cutscenes.actions.NPCForceTalkAction;
import net.kagani.game.player.cutscenes.actions.PlayerAnimationAction;
import net.kagani.game.player.cutscenes.actions.PlayerFaceTileAction;
import net.kagani.game.player.cutscenes.actions.PlayerForceTalkAction;
import net.kagani.game.player.cutscenes.actions.PlayerGraphicAction;

public class HomeCutScene3 extends Cutscene {

	@Override
	public boolean hiddenMinimap() {
		return false;
	}

	@Override
	public boolean showYourselfToOthers() {
		return false;
	}

	private static final int ADVERTISER = 1;
	private static final int ORACLE = 2;

	@Override
	public CutsceneAction[] getActions(final Player player) {
		ArrayList<CutsceneAction> actionsList = new ArrayList<CutsceneAction>();

		/*
		 * only player can see it
		 */
		actionsList.add(new CreateNPCAction(ADVERTISER, 15786, 3448, 3707, 0,
				"CorruptionX Welcomer", 0, -1));
		actionsList.add(new NPCFaceTileAction(ADVERTISER, 3449, 3707, -1));
		actionsList.add(new MovePlayerAction(3450, 3700, 0,
				Player.TELE_MOVE_TYPE, 0));
		actionsList.add(new MovePlayerAction(3450, 3707, false, 5));
		actionsList.add(new NPCForceTalkAction(ADVERTISER, player
				.getDisplayName() + " has arrived at CorruptionX!", 3));
		actionsList.add(new PlayerFaceTileAction(3449, 3707, 1));
		actionsList.add(new PlayerAnimationAction(new Animation(857), 2));
		actionsList.add(new PlayerForceTalkAction("And who are you?", 5));
		actionsList.add(new NPCForceTalkAction(ADVERTISER, "Hmm....", 2));
		actionsList.add(new NPCForceTalkAction(ADVERTISER, "I am...", 1));
		actionsList.add(new NPCForceTalkAction(ADVERTISER, "am...", 0));
		actionsList
				.add(new NPCForceTalkAction(
						ADVERTISER,
						"Doesn't matter! My job is to introduce new players to the Oracle of Dawn.",
						10));
		actionsList.add(new PlayerForceTalkAction("And who's that?", 3));
		actionsList.add(new NPCForceTalkAction(ADVERTISER,
				"Stop asking so many questions... I don't have time for that!",
				5));
		actionsList.add(new NPCForceTalkAction(ADVERTISER,
				"Just come closer and I will show you.", 3));
		actionsList.add(new MovePlayerAction(3449, 3707, false, 2));
		actionsList.add(new PlayerGraphicAction(new Graphics(184), 1));
		actionsList.add(new PlayerAnimationAction(new Animation(12413), 1));
		actionsList.add(new NPCForceTalkAction(ADVERTISER,
				"Time to start, hehe.", 0));

		actionsList.add(new CutsceneCodeAction(new Runnable() {
			@Override
			public void run() {
				player.getInterfaceManager().sendFadingInterface(115);
			}
		}, 3));

		actionsList.add(new MovePlayerAction(3129, 3711, 0,
				Player.TELE_MOVE_TYPE, -1));
		actionsList.add(new PlayerFaceTileAction(3128, 3711, -1));
		actionsList.add(new CreateNPCAction(ORACLE, 15158, 3126, 3710, 0,
				"Oracle of Dawn", 0, -1));
		actionsList.add(new NPCFaceTileAction(ORACLE, 3129, 3711, -1));
		actionsList.add(new PlayerAnimationAction(new Animation(-1), -1));
		actionsList.add(new CutsceneCodeAction(new Runnable() {
			@Override
			public void run() {
				player.getInterfaceManager().sendFadingInterface(660);
			}
		}, 2));
		actionsList.add(new NPCForceTalkAction(ORACLE, "Welcome "
				+ player.getDisplayName() + ".", 3));
		actionsList.add(new PlayerForceTalkAction("To hell rofl?", 3));
		actionsList.add(new NPCForceTalkAction(ORACLE,
				"As you may have noticed, you're dreaming.", 3));
		actionsList.add(new NPCForceTalkAction(ORACLE,
				"Allow me to show you the world of CorruptionX.", 3));
		actionsList.add(new PlayerForceTalkAction(
				"But I don't want... Let me play already!", 3));
		actionsList.add(new NPCForceTalkAction(ORACLE, "BE QUIET!", 3));
		actionsList.add(new CutsceneCodeAction(new Runnable() {
			@Override
			public void run() {
				player.getAppearence().switchHidden();
			}
		}, -1));
		actionsList.add(new MovePlayerAction(2683, 3728, 0,
				Player.TELE_MOVE_TYPE, -1));
		actionsList.add(new PlayerForceTalkAction(
				"Stop teleporting me already...", 3));
		actionsList
				.add(new NPCForceTalkAction(
						ORACLE,
						"This is where new players train their combat skills. The legendary rock crabs!",
						5));

		actionsList.add(new MovePlayerAction(3319, 3494, 0,
				Player.TELE_MOVE_TYPE, -1));
		actionsList.add(new PlayerForceTalkAction(
				"I don't even care anymore....", 3));
		actionsList
				.add(new NPCForceTalkAction(
						ORACLE,
						"Sawmill. If your goal is to become the #1 woodcutter, this is where you belong. Let's continue.",
						5));

		actionsList.add(new MovePlayerAction(3668, 5082, 0,
				Player.TELE_MOVE_TYPE, -1));
		actionsList
				.add(new NPCForceTalkAction(
						ORACLE,
						"Living Rock Caverns. Here the most enslaved players pass their day mining and fishing for the others.",
						5));

		actionsList.add(new MovePlayerAction(3187, 3425, 0,
				Player.TELE_MOVE_TYPE, -1));
		actionsList
				.add(new NPCForceTalkAction(
						ORACLE,
						"Wherever we are, if you want to smith, this is the place.",
						5));

		actionsList.add(new MovePlayerAction(3057, 3308, 0,
				Player.TELE_MOVE_TYPE, -1));
		actionsList.add(new NPCForceTalkAction(ORACLE,
				"Lets not forget the plants for the farmers.", 5));

		actionsList.add(new MovePlayerAction(2995, 4379, 2,
				Player.TELE_MOVE_TYPE, -1));
		actionsList.add(new NPCForceTalkAction(ORACLE,
				"CorruptionX has many beasts (beast players too) aswell.", 5));

		actionsList.add(new CutsceneCodeAction(new Runnable() {
			@Override
			public void run() {
				for (Player p2 : World.getPlayers()) {
					if (p2.getControlerManager().getControler() instanceof HouseControler) {
						player.setNextWorldTile(p2);
						return;
					}
				}
				player.setNextWorldTile(new WorldTile(3241, 3474, 0));
			}
		}, -1));
		actionsList.add(new NPCForceTalkAction(ORACLE,
				"Make your own palace to make your friends jealous.", 5));

		actionsList.add(new CutsceneCodeAction(new Runnable() {
			@Override
			public void run() {
				for (Player p2 : World.getPlayers()) {
					if (p2.getControlerManager().getControler() instanceof DungeonController) {
						player.setNextWorldTile(p2);
						return;
					}
				}
				player.setNextWorldTile(new WorldTile(3450, 3728, 0));
			}
		}, -1));
		actionsList.add(new NPCForceTalkAction(ORACLE,
				"Raid the underground of Daemonheim like a true explorer.", 5));

		actionsList.add(new MovePlayerAction(3039, 4835, 0,
				Player.TELE_MOVE_TYPE, -1));
		actionsList.add(new NPCForceTalkAction(ORACLE,
				"Visit other dimensions (you might even find a god!).", 5));

		actionsList.add(new MovePlayerAction(2968, 9706, 0,
				Player.TELE_MOVE_TYPE, -1));
		actionsList.add(new NPCForceTalkAction(ORACLE,
				"Play minigames with your loyal friends.", 5));

		actionsList.add(new MovePlayerAction(2925, 5203, 0,
				Player.TELE_MOVE_TYPE, -1));
		/*
		 * actionsList.add(new CutsceneCodeAction(new Runnable() {
		 * 
		 * @Override public void run() { ZarosGodwars.addPlayer(player);
		 * WorldTasksManager.schedule(new WorldTask() {
		 * 
		 * @Override public void run() { ZarosGodwars.removePlayer(player); } },
		 * 9); } }, -1));
		 */
		actionsList.add(new NPCForceTalkAction(ORACLE,
				"Fight for your faction at godwars.", 8));

		actionsList.add(new MovePlayerAction(3129, 3711, 0,
				Player.TELE_MOVE_TYPE, -1));
		actionsList.add(new CutsceneCodeAction(new Runnable() {
			@Override
			public void run() {
				player.getAppearence().switchHidden();
			}
		}, -1));
		actionsList
				.add(new NPCForceTalkAction(
						ORACLE,
						"Actually, we have everything, from milking cows to picking potatos but you will have to try it all yourself.",
						5));
		actionsList.add(new NPCForceTalkAction(ORACLE,
				"Talk to me anytime, if you need any help!", 5));
		actionsList.add(new NPCForceTalkAction(ORACLE, "Time to wake up.", -1));
		actionsList.add(new PlayerGraphicAction(new Graphics(184), 1));
		actionsList.add(new PlayerAnimationAction(new Animation(12413), 3));
		actionsList.add(new CutsceneCodeAction(new Runnable() {
			@Override
			public void run() {
				player.getInterfaceManager().sendFadingInterface(115);
			}
		}, 3));

		actionsList.add(new CutsceneCodeAction(new Runnable() {
			@Override
			public void run() {
				player.getInterfaceManager().removeFadingInterface();
				player.setNextAnimation(new Animation(-1));
				player.setNextForceTalk(new ForceTalk(
						"That was a strange dream."));
			}
		}, -1));
		// 2937 3430 0

		// actionsList.add(new NPCForceTalkAction(ADVERTISER, "Dragonkk!@!@!@!",
		// -1));

		/*
		 * actionsList.add(new CutsceneCodeAction(new Runnable() {
		 * 
		 * @Override public void run() {
		 * player.getInterfaceManager().setOverlay(660, true);
		 * //player.getDialogueManager().startDialogue("SimpleNPCMessage",
		 * 15158, "Welcome to CorruptionX!",
		 * "If you have any questions make sure to read guide book in your inventory."
		 * );
		 * 
		 * } }, 10));
		 */

		return actionsList.toArray(new CutsceneAction[actionsList.size()]);
	}
}
