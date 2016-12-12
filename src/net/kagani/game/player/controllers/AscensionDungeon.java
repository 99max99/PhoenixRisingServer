package net.kagani.game.player.controllers;

import java.util.ArrayList;

import net.kagani.Settings;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.others.Legio;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

/**
 * 
 * @author Kyle Proctor
 * @contact<skype;SaviourZz><email;kylejohnproctor@gmail.com>
 */

public class AscensionDungeon {
	public static Legio[] FIGHTS_ACTIVE = new Legio[6];
	public static ArrayList<WorldObject> LINES = new ArrayList<WorldObject>();
	public static ArrayList<NPC> MINIONS = new ArrayList<NPC>();

	private static int yMin = 619;
	private static int yMax = 641;
	private static int xMin = 1193;
	private static int xMax = 1214;

	private static int LINE = 84675;
	private static int CROSS_SECTION = 84676;

	public enum AscDoors {
		Primus(new WorldTile(1023, 632, 1), new WorldTile(1025, 632, 1)), Secundus(
				new WorldTile(1106, 673, 1), new WorldTile(1106, 671, 1)), Tertius(
				new WorldTile(1099, 663, 1), new WorldTile(1099, 665, 1)), Quartus(
				new WorldTile(1174, 633, 1), new WorldTile(1176, 633, 1)), Quintus(
				new WorldTile(1193, 634, 1), new WorldTile(1191, 634, 1)), Sextus(
				new WorldTile(1184, 620, 1), new WorldTile(1184, 622, 1));

		private WorldTile enter;
		private WorldTile exit;

		private AscDoors(WorldTile enter, WorldTile exit) {
			this.enter = enter;
			this.exit = exit;
		}

		public WorldTile getEnter() {
			return enter;
		}

		public WorldTile getExit() {
			return exit;
		}
	}

	public static void handleLightning(Legio leg, WorldTile targetPosition,
			int min, int max) {
		handleLightning(leg, targetPosition, min, max, false);
	}

	public static void handleLightning(Legio leg, WorldTile targetPosition,
			int min, int max, boolean sextus) {
		if (leg == null || leg.hasFinished() || leg.isDead())
			return;
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (leg == null || leg.hasFinished() || leg.isDead())
					return;
				World.sendGraphics(null, new Graphics(3974), targetPosition);
				for (Entity player : leg.getPossibleTargets()) {
					if (Utils.getDistance(targetPosition, player) <= 1) {
						player.applyHit(new Hit(leg, Utils.random(min, max),
								HitLook.MAGIC_DAMAGE));
					}
				}
			}
		}, 3);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (leg == null || leg.hasFinished() || leg.isDead())
					return;
				for (Entity player : leg.getPossibleTargets()) {
					if (Utils.getDistance(targetPosition, player) <= 1) {
						player.applyHit(new Hit(leg, Utils.random(min, max),
								HitLook.MAGIC_DAMAGE));
					}
				}
			}
		}, 5);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (leg == null || leg.hasFinished() || leg.isDead())
					return;
				for (Entity player : leg.getPossibleTargets()) {
					if (Utils.getDistance(targetPosition, player) <= 1) {
						player.applyHit(new Hit(leg, Utils.random(min, max),
								HitLook.MAGIC_DAMAGE));
					}
				}
			}
		}, 7);
		if (sextus) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (leg == null || leg.hasFinished() || leg.isDead())
						return;
					World.sendGraphics(null, new Graphics(3974), targetPosition);
					for (Entity player : leg.getPossibleTargets()) {
						if (Utils.getDistance(targetPosition, player) <= 1) {
							player.applyHit(new Hit(leg,
									Utils.random(min, max),
									HitLook.MAGIC_DAMAGE));
						}
					}
				}
			}, 9);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (leg == null || leg.hasFinished() || leg.isDead())
						return;
					for (Entity player : leg.getPossibleTargets()) {
						if (Utils.getDistance(targetPosition, player) <= 1) {
							player.applyHit(new Hit(leg,
									Utils.random(min, max),
									HitLook.MAGIC_DAMAGE));
						}
					}
				}
			}, 11);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (leg == null || leg.hasFinished() || leg.isDead())
						return;
					for (Entity player : leg.getPossibleTargets()) {
						if (Utils.getDistance(targetPosition, player) <= 1) {
							player.applyHit(new Hit(leg,
									Utils.random(min, max),
									HitLook.MAGIC_DAMAGE));
						}
					}
				}
			}, 13);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (leg == null || leg.hasFinished() || leg.isDead())
						return;
					World.sendGraphics(null, new Graphics(3974), targetPosition);
					for (Entity player : leg.getPossibleTargets()) {
						if (Utils.getDistance(targetPosition, player) <= 1) {
							player.applyHit(new Hit(leg,
									Utils.random(min, max),
									HitLook.MAGIC_DAMAGE));
						}
					}
				}
			}, 15);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (leg == null || leg.hasFinished() || leg.isDead())
						return;
					for (Entity player : leg.getPossibleTargets()) {
						if (Utils.getDistance(targetPosition, player) <= 1) {
							player.applyHit(new Hit(leg,
									Utils.random(min, max),
									HitLook.MAGIC_DAMAGE));
						}
					}
				}
			}, 17);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (leg == null || leg.hasFinished() || leg.isDead())
						return;
					for (Entity player : leg.getPossibleTargets()) {
						if (Utils.getDistance(targetPosition, player) <= 1) {
							player.applyHit(new Hit(leg,
									Utils.random(min, max),
									HitLook.MAGIC_DAMAGE));
						}
					}
				}
			}, 19);
		}
	}

	public static void spawnLine(WorldTile tile, int rot) {
		WorldObject object;
		if (World.getObjectWithId(tile, LINE) != null)
			object = new WorldObject(CROSS_SECTION, 4, rot, tile.getX(),
					tile.getY(), tile.getPlane());
		else
			object = new WorldObject(LINE, 4, rot, tile.getX(), tile.getY(),
					tile.getPlane());
		World.spawnObject(object);
		LINES.add(object);
	}

	public static void spawnMinion(WorldTile tile, int id) {
		NPC npc = World.spawnNPC(id, tile, -1, true, true);
		npc.setForceAgressive(true);
		MINIONS.add(npc);
	}

	public static void clearMinions() {
		if (MINIONS != null && !MINIONS.isEmpty()) {
			for (NPC object : MINIONS) {
				object.finish();
			}
			MINIONS.clear();
		}
	}

	public static void clearLines() {
		if (LINES != null && !LINES.isEmpty()) {
			for (WorldObject object : LINES) {
				World.removeObject(object);
			}
			LINES.clear();
		}
	}

	public static void generateLine(Entity player, boolean vertical) {
		int rotation = vertical ? 0 : 1;
		if (vertical) {
			for (int y = yMin; y < yMax; y++)
				spawnLine(new WorldTile(player.getX(), y, player.getPlane()),
						rotation);
		} else {
			for (int x = xMin; x < xMax; x++)
				spawnLine(new WorldTile(x, player.getY(), player.getPlane()),
						rotation);
		}
	}

	public static int getKey(int ordinal) {
		return 28445 + (ordinal * 2);
	}

	public static boolean spawnBoss(Player player, int ordinal) {
		if (FIGHTS_ACTIVE[ordinal] != null) {
			player.getPackets()
					.sendGameMessage(
							"There is already a fight going on inside. Join them when it's over.");
			return false;
		}
		if (player.getInventory().containsItem(getKey(ordinal), 1)) {
			player.getInventory().deleteItem(getKey(ordinal), 1);
			FIGHTS_ACTIVE[ordinal] = new Legio(player.getUsername(), ordinal);
			return true;
		} else {
			player.getPackets().sendGameMessage(
					"You lack the required key to fight this boss.");
			if (Settings.DEBUG)
				Logger.log("AscensionDungeon", "key id: " + getKey(ordinal));
			return false;
		}
	}

	public static void onLogout(Player player) {
		for (int i = 0; i < FIGHTS_ACTIVE.length; i++) {
			if (FIGHTS_ACTIVE[i] != null) {
				if (FIGHTS_ACTIVE[i].getOwner().equals(player.getUsername())) {
					exit(player, i, true, false);
				}
			}
		}
	}

	public static void onDeath(Player player) {
		for (int i = 0; i < FIGHTS_ACTIVE.length; i++) {
			if (FIGHTS_ACTIVE[i] != null) {
				if (FIGHTS_ACTIVE[i].getOwner().equals(player.getUsername())) {
					exit(player, i, false, true);
				}
			}
		}
	}

	public static void enter(Player player, int ordinal) {
		if (spawnBoss(player, ordinal))
			player.setNextWorldTile(AscDoors.values()[ordinal].enter);
	}

	public static void exit(Player player, int ordinal, boolean logout,
			boolean isDeath) {
		if (FIGHTS_ACTIVE[ordinal] != null
				&& FIGHTS_ACTIVE[ordinal].getOwner().equals(
						player.getUsername())) {
			FIGHTS_ACTIVE[ordinal].finish();
			FIGHTS_ACTIVE[ordinal] = null;
			if (ordinal == 4)
				clearLines();
			if (ordinal == 2)
				clearMinions();
		}
		if (!isDeath) {
			if (logout)
				player.setLocation(AscDoors.values()[ordinal].exit);
			else
				player.setNextWorldTile(new WorldTile(
						AscDoors.values()[ordinal].exit.getX(), AscDoors
								.values()[ordinal].exit.getY(), AscDoors
								.values()[ordinal].exit.getPlane()));
		}
	}

}