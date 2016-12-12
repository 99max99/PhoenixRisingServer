package net.kagani.game.player.controllers;

import java.util.List;

import net.kagani.Settings;
import net.kagani.executor.GameExecutorManager;
import net.kagani.game.Animation;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.map.MapBuilder;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.araxxi.Araxxi;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Magic;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class AraxxiControler extends Controller {

	private static WorldTile EXIT = new WorldTile(3700, 3418, 0),
			LOBBY = new WorldTile(3700, 3418, 0), GRAVEYARD = new WorldTile(
					3700, 3418, 0);

	@SuppressWarnings("unused")
	private int regionId;
	@SuppressWarnings("unused")
	private boolean processNPCs;
	private Player host;
	int time;
	private int[] chunks;
	private boolean canPro;

	private Araxxi araxxi;

	// north east chunk x392 y767
	// south west chunk x384 y744
	// player 79 81 1
	// ara 78 89 1
	@Override
	public void start() {
		host = player;
		processNPCs = false;
		player.setForceMultiArea(true);
		chunks = MapBuilder.findEmptyChunkBound(8, 8);
		MapBuilder.copyAllPlanesMap(572, 780, chunks[0], chunks[1], 8);
		player.setNextWorldTile(new WorldTile(getX() + 15, getY() + 17, 1));
		WorldTile tile = new WorldTile(getX() + 14, getY() + 25, 1);
		araxxi = new Araxxi(19464, tile, 0, true, true, player);
		if (player.araxxiEnrage != 0
				&& (Utils.currentTimeMillis() - player.araxxiEnrageTimer > 43200000)) {
			player.getPackets().sendGameMessage("Your Enrage level has reset!");
			player.araxxiEnrage = 0;
		}
		// araxxi.setForceFollowClose(true);
		araxxi.playerEnrageLevel = player.araxxiEnrage;
		// set araxxi's enrage multipliers
		araxxi.EnrageNumbers();
		if (Settings.DEBUG) {
			player.getPackets().sendGameMessage(
					"<br><br>--Enrage Level " + player.araxxiEnrage + "--");
			player.getPackets().sendGameMessage(
					"Damage Multiplier: " + araxxi.damageMulti);
			player.getPackets().sendGameMessage(
					"Healing Multiplier: " + araxxi.healingMulti);
			player.getPackets().sendGameMessage(
					"Max Minions: " + araxxi.minionNumber);
			player.getPackets().sendGameMessage(
					"Total HP: " + araxxi.startingHp);
		}
		araxxi.setHitpoints(araxxi.startingHp);
		araxxi.setNoClipWalking(true);
		araxxi.removeClipping();
		regionId = player.getRegionId();
		player.setForceMultiArea(true);

		WorldTasksManager.schedule(new WorldTask() {
			int stage;

			@Override
			public void run() {
				if (stage == 5) {
					canPro = true;
					stop();
				}
				stage++;
			}

		}, 0, 1);

	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int slotId2, int packetId) {
		if (araxxi == null)
			return true;
		switch (interfaceId) {
		case 1284:
			switch (componentId) {
			case 8:
				player.getBank().addItems(araxxi.getRewards().toArray(), true);
				araxxi.getRewards().clear();
				player.getPackets().sendGameMessage(
						"All the items were moved to your bank.");
				break;
			case 9:
				araxxi.getRewards().clear();
				player.getPackets().sendGameMessage(
						"All the items were removed from the chest.");
				break;
			case 10:
				for (int slot = 0; slot < araxxi.getRewards().toArray().length; slot++) {
					Item item = araxxi.getRewards().get(slot);
					if (item == null) {
						continue;
					}
					boolean added = true;
					if (item.getDefinitions().isStackable()
							|| item.getAmount() < 2) {
						added = player.getInventory().addItem(item);
						if (added) {
							araxxi.getRewards().toArray()[slot] = null;
						}
					} else {
						for (int i = 0; i < item.getAmount(); i++) {
							Item single = new Item(item.getId());
							if (!player.getInventory().addItem(single)) {
								added = false;
								break;
							}
							araxxi.getRewards().remove(single);
						}
					}
					if (!added) {
						player.getPackets()
								.sendGameMessage(
										"You only had enough space in your inventory to accept some of the items.");
						break;
					}
				}
				break;
			case 7:
				Item item = araxxi.getRewards().get(slotId);
				if (item == null)
					return true;
				switch (packetId) {
				case 52:
					player.getPackets().sendGameMessage(
							"It's a " + item.getDefinitions().getName());
					return false;
				case 4:
					araxxi.getRewards().toArray()[slotId] = null;
					break;
				case 64:
					player.getBank()
							.addItems(
									new Item[] { araxxi.getRewards().toArray()[slotId] },
									true);
					araxxi.getRewards().toArray()[slotId] = null;
					break;
				case 61:
					boolean added = true;
					if (item.getDefinitions().isStackable()
							|| item.getAmount() < 2) {
						added = player.getInventory().addItem(item);
						if (added) {
							araxxi.getRewards().toArray()[slotId] = null;
						}
					} else {
						for (int i = 0; i < item.getAmount(); i++) {
							Item single = new Item(item.getId());
							if (!player.getInventory().addItem(single)) {
								added = false;
								break;
							}
							araxxi.getRewards().remove(single);
						}
					}
					if (!added) {
						player.getPackets()
								.sendGameMessage(
										"You only had enough space in your inventory to accept some of the items.");
						break;
					}
					break;
				default:
					return true;
				}
				break;
			default:
				return true;
			}
			player.getInterfaceManager().removeCentralInterface();
			return false;
		}
		player.getInterfaceManager().removeCentralInterface();
		return true;
	}

	private boolean canFinish() {
		if (player.getControlerManager().getControler() == this)
			return true;
		return false;
	}

	@Override
	public void process() {
		if (canPro) {
			int regId = player.getRegionId();
			@SuppressWarnings("unused")
			List<Integer> npcs = World.getRegion(regId).getNPCsIndexes();
			if (araxxi.isFinished) {
				canPro = false;
				WorldTasksManager.schedule(new WorldTask() {
					int stage;

					@Override
					public void run() {

						if (stage == 1) {
							player.getPackets()
									.sendGameMessage(
											"You have 60 seconds before the room collapses.");

						} else if (stage == 30) {
							player.getPackets()
									.sendGameMessage(
											"You have 30 seconds before the room collapses.");

						} else if (stage == 60) {
							if (canFinish()) {
								endGame();
								completeGame();
							}

							stop();
						}
						stage++;
					}

				}, 0, 1);
			}
		}
	}

	public void completePlayer(Player player) {
		player.getControlerManager().forceStop();
		player.setNextWorldTile(LOBBY);
	}

	public void completeGame() {
		destroyMap();
		int rId = player.getRegionId();
		deleteNPCs(rId);
	}

	public void endGame() {
		player.setForceMultiArea(false);
		player.getControlerManager().forceStop();
		player.setNextWorldTile(EXIT);
		player.stopAll();
	}

	public void endGame(WorldTile tile) {
		player.setForceMultiArea(false);
		player.getControlerManager().forceStop();
		Magic.sendNormalTeleportSpell(player, 0, 0, tile);
		player.stopAll();
	}

	public void cleanupGame() {
		destroyMap();
		int rId = player.getRegionId();
		deleteNPCs(rId);
	}

	public void deleteNPCs(int regionId) {
		List<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
		if (npcsIndexes != null) {
			for (int npcIndex : npcsIndexes) {
				NPC npc = World.getNPCs().get(npcIndex);
				if (npc == null/* || npc.isDead() */|| npc.hasFinished()) {
					continue;
				}
				if (npc.getId() == 17182) {
					npc.finish();
				}

			}
		}
	}

	public void destroyMap() {
		// if()
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

	@Override
	public boolean sendDeath() {
		final int rId = player.getRegionId();
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
					/*
					 * if (host.getVoragoPartyMembers().isEmpty()) {
					 * destroyMap(); deleteNPCs(rId); }
					 */
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	public int getX() {
		return chunks[0] << 3;
	}

	public int getY() {
		return chunks[1] << 3;
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

	@Override
	public boolean processObjectClick2(WorldObject object) {
		int objectId = object.getId();
		switch (objectId) {
		case 91673:
			player.setNextWorldTile(LOBBY);
			endGame();
			completeGame();

			return false;
		}
		return false;
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		int objectId = object.getId();
		switch (objectId) {
		case 91673:
			player.getDialogueManager().startDialogue("AraxxiReward", araxxi);
			// npc.openRewardChest(true);
			return true;
		}
		return true;

	}
}