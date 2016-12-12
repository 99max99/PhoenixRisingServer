package net.kagani.game.player.controllers;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.ForceMovement;
import net.kagani.game.NewForceMovement;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.others.GraveStone;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Slayer.SlayerMaster;
import net.kagani.game.player.content.agility.Agility;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class KuradalDungeon extends Controller {

	public static void enter(Player player) {
		if ((player.getSlayerManager().getCurrentMaster() != SlayerMaster.KURADAL
				&& player.getSlayerManager().getCurrentMaster() != SlayerMaster.MORVAN || player
				.getSlayerManager().getCurrentTask() == null)
				&& GraveStone.getGraveStoneByUsername(player.getUsername()) == null) {
			player.getDialogueManager().startDialogue("SimpleNPCMessage", 9084,
					"You don't have businesses there. Leave please.");
			return;
		}
		player.getControlerManager().startControler("KuradalDungeon");
	}

	@Override
	public void start() {
		player.useStairs(-1, new WorldTile(1661, 5257, 0), 0, 1);
	}

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof NPC) {
			NPC npc = (NPC) target;
			if (!player.getSlayerManager().isValidTask(npc.getName())) {
				player.getPackets().sendGameMessage(
						"You're not down here to kill those.");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (object.getId() == 47231) {
			removeControler();
			player.useStairs(-1, new WorldTile(1735, 5312, 1), 0, 1);
			return false;
		}
		if (object.getId() == 47236) {
			int x = 0;
			int y = 0;
			if (object.getRotation() == 2)
				x = player.getX() == object.getX() ? 1 : -1;
			else if (object.getRotation() == 0)
				x = player.getX() == object.getX() ? -1 : 1;
			else if (object.getRotation() == 3)
				y = player.getY() == object.getY() ? -1 : 1;
			else if (object.getRotation() == 1)
				y = player.getY() == object.getY() ? 1 : -1;
			player.addWalkSteps(player.getX() + x, player.getY() + y, 1, false);
			player.lock(1);
			return false;
		}
		if (object.getId() == 47233) {
			if (!Agility.hasLevel(player, 86))
				return false;
			player.getPackets().sendGameMessage("You climb the low wall...",
					true);
			player.lock(3);
			player.setNextAnimation(new Animation(4853));
			final WorldTile toTile = new WorldTile(object.getX(),
					player.getY() < object.getY() ? object.getY() + 1
							: object.getY() - 1, object.getPlane());
			player.setNextForceMovement(new ForceMovement(player, 0, toTile, 2,
					player.getY() < object.getY() ? ForceMovement.NORTH
							: ForceMovement.SOUTH));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					player.setNextWorldTile(toTile);
				}
			}, 1);
			return false;
		}
		if (object.getId() == 47237) {
			if (!Agility.hasLevel(player, 90))
				return false;
			player.lock();
			final WorldTile tile = new WorldTile(1641,
					player.getY() > 5260 ? 5260 : 5268, 0);
			WorldTasksManager.schedule(new WorldTask() {

				int ticks = -1;

				@Override
				public void run() {
					ticks++;
					if (ticks == 0)
						player.setNextFaceWorldTile(object);
					else if (ticks == 1) {
						player.setNextAnimation(new Animation(10738));
						player.setNextForceMovement(new NewForceMovement(
								player, 2, tile, 5, Utils.getAngle(
										object.getX() - player.getX(),
										object.getY() - player.getY())));
					} else if (ticks == 3)
						player.setNextWorldTile(tile);
					else if (ticks == 4) {
						player.getPackets().sendGameMessage(
								"Your feet skid as you land floor.");
						player.unlock();
						stop();
						return;
					}
				}
			}, 0, 0);
			return false;
		}
		return true;
	}

	@Override
	public boolean canHit(Entity target) {
		if (target instanceof NPC) {
			NPC npc = (NPC) target;
			if (!player.getSlayerManager().isValidTask(npc.getName())) {
				player.getPackets().sendGameMessage(
						"You don't have permission to kill those.");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean keepCombating(Entity target) {
		if (target instanceof NPC) {
			NPC npc = (NPC) target;
			if (!player.getSlayerManager().isValidTask(npc.getName()))
				return false;
		}
		return true;
	}

	@Override
	public boolean sendDeath() {
		removeControler();
		return true;
	}

	@Override
	public boolean login() {
		return false; // so doesnt remove script
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

	@Override
	public void magicTeleported(int type) {
		removeControler();
	}
}