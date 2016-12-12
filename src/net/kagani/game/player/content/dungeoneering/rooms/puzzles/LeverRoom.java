package net.kagani.game.player.content.dungeoneering.rooms.puzzles;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.dungeoneering.rooms.PuzzleRoom;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public class LeverRoom extends PuzzleRoom {

	/*
	 * private static final int[] SWITCH_UP = { 49381, 49382, 49383, 54333,
	 * 33675 };
	 */

	private static final int[] SWITCH_DOWN = { 49384, 49385, 49386, 49386,
			49386 // TODO
					// find
					// down
					// of
					// 54333,
					// 33675
	};

	private int leverCount, leverTicks, maxTicks;
	private WorldTask resetTask;

	@Override
	public void openRoom() {
		manager.spawnRandomNPCS(reference);
	}

	@Override
	public boolean processObjectClick1(Player player, WorldObject object) {
		if (object.getDefinitions().name.equals("Switch")) {
			player.setNextAnimation(new Animation(3611));
			if (isComplete()) {
				player.getPackets().sendGameMessage(
						"The lever doesn't seem to respond.");
				return false;
			}
			if (resetTask == null)
				addResetTask();
			WorldObject down = new WorldObject(object);
			down.setId(SWITCH_DOWN[type]);
			World.spawnObjectTemporary(down, (maxTicks - leverTicks) * 600,
					true, true);
			leverCount++;
			return false;
		}
		return true;
	}

	private void addResetTask() {
		// Still want it to be possible when people leave a 5:5 (4:5), and very
		// easy on a 5:1
		int size = manager.getParty().getTeam().size();
		int difficulty = Math.min(manager.getParty().getDificulty(), size);
		// 5 - 2.4 seconds
		// 4 - 3.6 seconds
		// 3 - 5.4 seconds
		// 2 - 7.2 seconds
		// 1 - 14.4 seconds
		maxTicks = (6 - difficulty) + ((size == 1 ? 23 : 20) / difficulty);
		resetTask = new ResetTask();
		WorldTasksManager.schedule(resetTask, 0, 0);
	}

	private void resetTask() {
		leverTicks = 0;
		maxTicks = 0;
		resetTask = null;
	}

	private class ResetTask extends WorldTask {

		@Override
		public void run() {

			if (leverCount == 5) {
				setComplete();
				resetTask();
				stop();
				return;
			}

			leverTicks++;
			if (leverTicks == maxTicks) {
				resetTask();
				if (leverCount != 5) {
					leverCount = 0;

					for (Player player : manager.getParty().getTeam()) {
						player.getPackets()
								.sendGameMessage(
										"You hear a loud noise and all the switches toggle back off.");
						if (player.withinDistance(
								manager.getTile(reference, 7, 8), 2)
								|| !manager.getCurrentRoomReference(
										new WorldTile(player))
										.equals(reference))
							continue;
						World.sendGraphics(player, new Graphics(2759),
								new WorldTile(player));
						player.setNextAnimation(new Animation(13694));
						player.applyHit(new Hit(player, (int) (player
								.getMaxHitpoints() * .3),
								HitLook.REGULAR_DAMAGE));
					}
					stop();
					return;
				}
			}
		}
	}

	@Override
	public String getCompleteMessage() {
		return "As the last lever is pulled, you hear a click. All the doors in the room are now unlocked.";
	}
}
