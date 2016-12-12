package net.kagani.game.player.actions;

import net.kagani.game.Animation;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public class Portables {

	/**
	 * @author: Dylan Page
	 */

	protected static Player player;

	public enum Portable {

		PORTABLEWELL(89770), PORTABLEFORGE(89767), PORTABLERANGE(89768), PORTABLESAWMILL(
				89769), PORTABLEBANK(75932);

		private int id;

		Portable(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}

	public static void deployPortable(final Player owner, final int item,
			final int fobject, final int lobject) {
		if (owner.isCanPvp()) {
			owner.getPackets().sendGameMessage(
					"You cant deploy a portable while doing this action.");
			return;
		}
		if (World.getStandartObject(owner) != null) {
			owner.getPackets().sendGameMessage(
					"You can't deploy a portable here.");
			return;
		}
		if (!owner.getInventory().containsItem(item, 1)) {
			owner.getPackets().sendGameMessage(
					"You don't have a portable in your inventory.");
			return;
		}
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				owner.stopAll();
				owner.getInventory().deleteItem(item, 1);
				checkAll(owner, item);
				final WorldObject portablefinalstage = new WorldObject(lobject,
						10, 0, owner.getX() + 1, owner.getY(),
						owner.getPlane(), owner);
				World.spawnTemporarPortableObject(portablefinalstage, 250000,
						owner);
				owner.faceObject(portablefinalstage);
				owner.setNextAnimation(new Animation(21217));
				owner.portable = lobject;
				owner.portables = owner;
				player = owner;
				owner.portableLimit++;
				owner.getPackets().sendGameMessage(
						"The portable will last 5 minutes.", true);
				stop();
				return;
			}
		}, 0, 0);
	}

	private static void checkAll(Player owner, int portable) {
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (owner.portable > 1)
					return;
				if (owner.portable == 0) {
					owner.getPackets().sendGameMessage(
							"Your portable has vanished.", true);
					owner.portableLimit = 0;
					stop();
				}
				return;
			}
		}, 0, 0);
	}
}