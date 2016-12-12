package net.kagani.game.player.controllers;

import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class RuneEssenceController extends Controller {

	private static final WorldTile[] ESSENCE_COORDS = new WorldTile[] {
			new WorldTile(2911, 4832, 0), new WorldTile(2924, 4818, 0),
			new WorldTile(2900, 4818, 0), new WorldTile(2900, 4843, 0),
			new WorldTile(2922, 4844, 0) };

	@Override
	public void start() {

	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

	@Override
	public boolean login() {
		return false; // so doesnt remove script
	}

	@Override
	public boolean sendDeath() {
		removeControler();
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		removeControler();
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 2503) {
			player.lock();
			player.setNextGraphics(new Graphics(110));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					player.useStairs(-1, (WorldTile) getArguments()[0], 0, 1);
					removeControler();
				}
			}, 2);
			return false;
		}
		return true;
	}

	public static void teleport(final Player player, NPC npc) {
		player.lock();
		npc.setNextForceTalk(new ForceTalk("Seventior disthiae molenko!"));
		npc.setNextGraphics(new Graphics(108));
		player.setNextGraphics(new Graphics(110));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.getControlerManager().startControler(
						"RuneEssenceController", new WorldTile(player));
				player.useStairs(-1,
						ESSENCE_COORDS[Utils.random(ESSENCE_COORDS.length)], 0,
						1);
			}
		}, 2);
	}

}
