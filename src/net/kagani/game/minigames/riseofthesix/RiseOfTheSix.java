package net.kagani.game.minigames.riseofthesix;

import java.util.List;

import net.kagani.executor.GameExecutorManager;
import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.map.MapBuilder;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.others.RiseOfTheSixNPC;
import net.kagani.game.player.Player;
import net.kagani.game.player.TimersManager.RecordKey;
import net.kagani.game.player.content.Magic;
import net.kagani.game.player.controllers.Controller;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class RiseOfTheSix extends Controller {

	private boolean stopEvent = false;

	private int loop;

	/**
	 * Rewards for the Rise of the Six
	 */

	private static Item[] commonRewards = { new Item(29759, 1),
			new Item(1128, Utils.random(5, 20)),
			new Item(1514, Utils.random(15, 45)),
			new Item(15271, Utils.random(15, 45)),
			new Item(1748, Utils.random(15, 50)),
			new Item(9245, Utils.random(15, 50)),
			new Item(1392, Utils.random(15, 50)),
			new Item(452, Utils.random(15, 100)),
			new Item(5316, Utils.random(5, 10)),
			new Item(5303, Utils.random(5, 10)),
			new Item(5302, Utils.random(5, 20)) };

	private static Item[] rareRewards = { new Item(30014, 1),
			new Item(30018, 1), new Item(30022, 1) };

	/**
	 * Dynamic region chunk data.
	 */
	private int[] chunks;

	/**
	 * Minigame coordinates.
	 */
	private static WorldTile EXIT = new WorldTile(3540, 3311, 0),
			GRAVEYARD = new WorldTile(2327, 5908, 0);

	private static WorldTile[] LOBBY = { new WorldTile(2326, 5910, 0),
			new WorldTile(2317, 5910, 0) };

	/**
	 * Other variables.
	 */
	private int regionId;
	private boolean processNPCs;
	private Player host;

	@Override
	public void start() {
		boolean isHost = (boolean) getArguments()[0];
		host = (Player) getArguments()[1];
		processNPCs = false;
		player.setForceMultiArea(true);
		player.getInventory().deleteItem(30004, 1);
		if (isHost) {
			chunks = MapBuilder.findEmptyChunkBound(8, 8);
			MapBuilder.copyAllPlanesMap(290, 753, chunks[0], chunks[1], 8);
			player.setNextWorldTile(new WorldTile(getX() + 10, getY() + 1, 1));
			regionId = player.getRegionId();
			player.setForceMultiArea(true);
			for (int i = 18538; i <= 18545; i++) {
				if (i == 18539 || i == 18542)
					continue;
				WorldTile tile = new WorldTile(getX() + Utils.random(6, 16),
						getY() + 10, 1);
				NPC npc = new RiseOfTheSixNPC(i, tile);
				npc.setForceMultiArea(true);
				npc.setForceMultiAttacked(true);
				npc.setForceAgressive(true);
				npc.setForceTargetDistance(50);
				npc.setNoClipWalking(true);
			}
			WorldTasksManager.schedule(new WorldTask() {
				int stage;

				@Override
				public void run() {
					if (stage == 60) {
						processNPCs = true;
						stop();
					}
					stage++;
				}

			}, 0, 1);
		} else {
			player.setNextWorldTile(host.getNextWorldTile());
			WorldTasksManager.schedule(new WorldTask() {
				int stage;

				@Override
				public void run() {
					if (stage == 60) {
						processNPCs = true;
						stop();
					}
					stage++;
				}

			}, 0, 1);
		}
	}

	public int getX() {
		return chunks[0] << 3;
	}

	public int getY() {
		return chunks[1] << 3;
	}

	@Override
	public void process() {
		if (processNPCs && stopEvent == false) {
			int regId = player.getRegionId();
			List<Integer> npcs = World.getRegion(regId).getNPCsIndexes();
			if (npcs == null || npcs.size() <= 0) {
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						if (loop == 0) {
							player.setNextWorldTile(new WorldTile(2327, 5907, 0));
							for (int i = 0; i < 6; i++)
								receiveDrop(player);
							World.spawnObject(new WorldObject(4407, 10, 0,
									2330, 5906, 0));
							player.getPackets()
									.sendGameMessage(
											"You have 59 seconds before the portal collapses - your reward is on the floor.");
							player.getTimersManager().increaseKills(
									RecordKey.THE_BARROWS_RISE_OF_THE_FIX,
									false);
							stopEvent = true;
						} else if (loop == 60) {
							completeGame();
							stop();
						}
						loop++;
					}
				}, 0, 1);
			}
		} else
			cleanupGame();
	}

	public static void receiveDrop(Player killer) {
		if (Utils.random(500) == 1)
			killer.getInventory()
					.addItemDrop(
							rareRewards[Utils.random(0, rareRewards.length - 1)]
									.getId(), 1);
		else
			killer.getInventory().addItemDrop(
					new Item(commonRewards[Utils.random(0,
							commonRewards.length - 1)]));
		if (Utils.random(6) == 1)
			killer.getInventory().addItemDrop(30027, 1);
	}

	public void completeGame() {
		player.riseOfTheSix++;
		destroyMap();
		deleteNPCs(regionId);
		stopEvent = true;
		removeControler();
		player.reset();
		player.getControlerManager().forceStop();
		player.setNextWorldTile(EXIT);
	}

	public static boolean processObjectClick(Player player, int objectId) {
		switch (objectId) {
		case 87994:
			player.useStairs(-1, new WorldTile(LOBBY[0]), 1, 2);
			return true;
		case 87997:
			player.getDialogueManager().startDialogue("RiseOfTheSixD");
			return true;
		}
		return false;
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		switch (object.getId()) {
		case 87994:
			player.useStairs(-1, new WorldTile(LOBBY[0]), 1, 2);
			return true;
		case 87997:
			player.getDialogueManager().startDialogue("RiseOfTheSixD");
			return true;
		case 4407:
			loop = 59;
			return true;
		}
		return false;
	}

	@Override
	public boolean sendDeath() {
		if (player.getInventory().containsItem(30026, 1)) {
			player.getInventory().deleteItem(30026,
					player.getInventory().getAmountOf(30026));
			player.getPackets().sendGameMessage(
					"Your malevolent energy crumbles to dust.");
		}
		player.lock(7);
		player.stopAll();
		if (player.getFamiliar() != null) {
			player.getFamiliar().sendDeath(player);
		}
		final WorldTile graveTile = GRAVEYARD;
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage(
							"Oh dear, you have died.");
				} else if (loop == 3) {
					player.reset();
					player.getControlerManager().startControler("DeathEvent",
							graveTile, player.hasSkull());
				} else if (loop == 4) {
					player.killstreak = 0;
					player.getPackets().sendMusicEffect(90, 1);
					host.removeROSPartyMember(player);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	public void cleanupGame() {
		if (host.getROSPartyMembers().isEmpty()) {
			destroyMap();
			deleteNPCs(regionId);
		}
	}

	public void destroyMap() {
		GameExecutorManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					MapBuilder.destroyMap(chunks[0], chunks[1], 8, 8);
				} catch (Exception e) {
					e.printStackTrace();
				} catch (Error e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void deleteNPCs(int regionId) {
		List<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
		if (npcsIndexes != null) {
			for (int npcIndex : npcsIndexes) {
				NPC npc = World.getNPCs().get(npcIndex);
				if (npc == null/* || npc.isDead() */|| npc.hasFinished()) {
					continue;
				}
				for (int i = 18538; i <= 18545; i++) {
					if (npc.getId() == i) {
						npc.finish();
					}
				}
			}
		}
	}

	public void endGame() {
		host.removeROSPartyMember(player);
		player.setForceMultiArea(false);
		if (player.getInventory().containsItem(30026, 1)) {
			player.getInventory().deleteItem(30026,
					player.getInventory().getAmountOf(30026));
			player.getPackets().sendGameMessage(
					"Your malevolent energy crumbles to dust.");
		}
		player.getControlerManager().forceStop();
		player.setNextWorldTile(EXIT);
		player.stopAll();
	}

	public void endGame(WorldTile tile) {
		host.removeROSPartyMember(player);
		player.setForceMultiArea(false);
		if (player.getInventory().containsItem(30026, 1)) {
			player.getInventory().deleteItem(30026,
					player.getInventory().getAmountOf(30026));
			player.getPackets().sendGameMessage(
					"Your malevolent energy crumbles to dust.");
		}
		player.getControlerManager().forceStop();
		Magic.sendNormalTeleportSpell(player, 0, 0, tile);
		player.stopAll();
	}

	@Override
	public boolean login() {
		endGame();
		return false;
	}

	@Override
	public boolean logout() {
		endGame();
		return false;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		endGame(toTile);
		return true;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		endGame(toTile);
		return true;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		endGame(toTile);
		return true;
	}

}