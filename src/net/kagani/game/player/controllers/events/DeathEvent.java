package net.kagani.game.player.controllers.events;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import net.kagani.Settings;
import net.kagani.cache.loaders.BodyDefinitions;
import net.kagani.executor.GameExecutorManager;
import net.kagani.game.Animation;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.FloorItem;
import net.kagani.game.item.Item;
import net.kagani.game.map.MapBuilder;
import net.kagani.game.npc.others.GraveStone;
import net.kagani.game.player.InterfaceManager;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Magic;
import net.kagani.game.player.controllers.Controller;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.network.decoders.WorldPacketsDecoder;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class DeathEvent extends Controller {
	public static final WorldTile[] HUBS = {
			// Lumbridge
			new WorldTile(3222, 3219, 0)
			// Varrock
			, new WorldTile(3212, 3422, 0)
			// EDGEVILLE
			, new WorldTile(3101, 3492, 0)
			// FALADOR
			, new WorldTile(2965, 3386, 0)
			// SEERS VILLAGE
			, new WorldTile(2725, 3491, 0)
			// ARDOUDGE
			, new WorldTile(2662, 3305, 0)
			// YANNILE
			, new WorldTile(2605, 3093, 0)
			// KELDAGRIM
			, new WorldTile(2845, 10210, 0)
			// DORGESH-KAN
			, new WorldTile(2720, 5351, 0)
			// LYETYA
			, new WorldTile(2341, 3171, 0)
			// ETCETERIA
			, new WorldTile(2609, 3891, 0)
			// DAEMONHEIM
			, new WorldTile(3450, 3718, 0)
			// CANIFIS
			, new WorldTile(3496, 3489, 0)
			// THZAAR AREA
			, new WorldTile(4651, 5151, 0)
			// BURTHORPE
			, new WorldTile(2889, 3528, 0)
			// ALKARID
			, new WorldTile(3275, 3166, 0)
			// DRAYNOR VILLAGE
			, new WorldTile(3079, 3250, 0) };

	public static final WorldTile[] RESPAWN_LOCATIONS = {
			new WorldTile(3222, 3219, 0), new WorldTile(2971, 3343, 0),
			new WorldTile(2758, 3486, 0), new WorldTile(1891, 3177, 0),
			new WorldTile(2889, 3528, 0) };

	public static int getCurrentHub(WorldTile tile) {
		int nearestHub = -1;
		int distance = 0;
		for (int i = 0; i < HUBS.length; i++) {
			int d = Utils.getDistance(HUBS[i], tile);
			if (nearestHub == -1 || d < distance) {
				distance = d;
				nearestHub = i;
			}
		}
		return nearestHub;
	}

	public static WorldTile getRespawnHub(Player player) {
		return HUBS[getCurrentHub(player)];
	}

	private int[] boundChuncks;
	private Stages stage;
	private Integer[][] slots;

	@Override
	public void start() {
		loadRoom();
	}

	@Override
	public boolean login() {
		loadRoom();
		return false;
	}

	@Override
	public boolean logout() {
		player.setLocation(new WorldTile(1978, 5302, 0));
		destroyRoom();
		return false;
	}

	@Override
	public boolean canTakeItem(FloorItem item) {
		return false;
	}

	@Override
	public boolean canEquip(int slotId, int itemId) {
		return false;
	}

	@Override
	public boolean canPlayerOption1(Player target) {
		return false;
	}

	@Override
	public boolean canPlayerOption2(Player target) {
		return false;
	}

	@Override
	public boolean canPlayerOption3(Player target) {
		return false;
	}

	@Override
	public boolean canPlayerOption4(Player target) {
		return false;
	}

	private static enum Stages {
		LOADING, RUNNING, DESTROYING
	}

	@Override
	public void sendInterfaces() {
		lockInterfaces(true);
	}

	/*
	 * 0 - skill inter 1 - active task 2 - inventory 3 - equipment 4 - prayer
	 * book 5 - abilities 9 - emotes 14 - friend list 15 - friend chat info 16 -
	 * clan 18 - chat 19 - friend chat
	 */
	public void lockInterfaces(boolean lock) {
		player.getInterfaceManager().sendLockGameTab(
				InterfaceManager.SKILLS_TAB, lock);
		player.getInterfaceManager().sendLockGameTab(
				InterfaceManager.ACTIVE_TASK_TAB, lock);
		player.getInterfaceManager().sendLockGameTab(
				InterfaceManager.INVENTORY_TAB, lock);
		player.getInterfaceManager().sendLockGameTab(
				InterfaceManager.EQUIPMENT_TAB, lock);
		player.getInterfaceManager().sendLockGameTab(
				InterfaceManager.PRAYER_BOOK_TAB, lock);
		player.getInterfaceManager().sendLockGameTab(
				InterfaceManager.MAGIC_ABILITIES_TAB, lock);
		player.getInterfaceManager().sendLockGameTab(
				InterfaceManager.MELEE_ABILITIES_TAB, lock);
		player.getInterfaceManager().sendLockGameTab(
				InterfaceManager.RANGE_ABILITIES_TAB, lock);
		player.getInterfaceManager().sendLockGameTab(
				InterfaceManager.DEFENCE_ABILITIES_TAB, lock);
		player.getInterfaceManager().sendLockGameTab(
				InterfaceManager.EMOTES_TAB, lock);
	}

	public void loadRoom() {
		stage = Stages.LOADING;
		player.lock(); // locks player
		GameExecutorManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					boundChuncks = MapBuilder.findEmptyChunkBound(2, 2);
					MapBuilder.copyMap(246, 662, boundChuncks[0],
							boundChuncks[1], 2, 2, new int[1], new int[1]);
					player.giveXP();
					player.reset();
					player.setNextWorldTile(new WorldTile(
							boundChuncks[0] * 8 + 10, boundChuncks[1] * 8 + 6,
							0));
					player.getDialogueManager().startDialogue("ReaperDialogue",
							1);
					player.lock();
					// 1delay because player cant walk while teleing :p, +
					// possible
					// issues avoid
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.setNextAnimation(new Animation(-1));
							player.getMusicsManager().playMusic(683);
							player.isInDeathRoom = true;
							player.getPackets().sendBlackOut(2);
							sendInterfaces();
							stage = Stages.RUNNING;
						}

					}, 1);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		});

		if (player.isHardcoreIronman() && player.getDivineCoin() == false) {
			player.setHasDied(true);
			World.sendIgnoreableWorldMessage(
					player,
					"<img=12><col=FE0000>"
							+ player.getDisplayName()
							+ " just died in Hardcore Ironman mode with a skill total of "
							+ player.getSkills().getTotalLevel() + ".", false);
			player.disconnect(true, false);
			return;
		} else if (player.isHardcoreIronman() && player.getDivineCoin() == true) {
			player.setDivineCoin(false);
			player.getPackets()
					.sendGameMessage(
							"<col=FF0000>You have used your Divine coin. The next time you die, you will lose your account. Unless you purchase another Divine coin.");
		}
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		return false;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 45803) {
			if (getArguments() == null || getArguments().length < 2)
				Magic.sendObjectTeleportSpell(player, true,
						Settings.HOME_LOCATION);
			else
				getReadyToRespawn();
			return false;
		}
		return true;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int slotId2, int packetId) {
		if (interfaceId == 18) {
			if (componentId == 9) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					unprotect(slotId);
			} else if (componentId == 17) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					protect(slotId2);
			} else if (componentId == 45) {
				// slotid - 1
				if (slotId > RESPAWN_LOCATIONS.length || slotId == 0)
					return false;
			}
			return false;
		}
		return true;
	}

	public void getReadyToRespawn() {
		slots = GraveStone.getItemSlotsKeptOnDeath(player, false, hadSkull(),
				player.getPrayer().isProtectingItem());
		WorldTile respawnTile = Settings.HOME_LOCATION;
		synchronized (slots) {
			// player.sendItemsOnDeath(null, getDeathTile(), respawnTile, false,
			// slots, true);
		}
		player.setHitpoints(player.getMaxHitpoints());
		player.isInDeathRoom = false;
		player.setCloseInterfacesEvent(null);
		Magic.sendObjectTeleportSpell(player, true, respawnTile);
	}

	public void sendProtectedItems() {
		for (int i = 0; i < getProtectSlots(); i++)
			player.getVarsManager().sendVarBit(9222 + i,
					i >= slots[0].length ? -1 : slots[0][i]);
	}

	public void protect(int itemId) {
		synchronized (slots) {
			int slot = -1;
			for (int i = 0; i < slots[1].length; i++) {

				Item item = slots[1][i] >= (BodyDefinitions
						.getEquipmentContainerSize() + 1) ? player
						.getInventory()
						.getItem(
								slots[1][i]
										- (BodyDefinitions
												.getEquipmentContainerSize() + 1))
						: player.getEquipment().getItem(slots[1][i] - 1);
				if (item == null)
					continue;
				if (item.getId() == itemId) {
					slot = i;
					break;
				}
			}

			if (slot == -1 || getProtectSlots() <= slots[0].length)
				return;

			slots[0] = Arrays.copyOf(slots[0], slots[0].length + 1);
			slots[0][slots[0].length - 1] = slots[1][slot];

			Integer[] lItems = new Integer[slots[1].length - 1];
			System.arraycopy(slots[1], 0, lItems, 0, slot);
			System.arraycopy(slots[1], slot + 1, lItems, slot, lItems.length
					- slot);

			slots[1] = lItems;
			sendProtectedItems();
		}

	}

	public void unprotect(int slot) {
		synchronized (slots) {
			if (slot >= slots[0].length)
				return;
			slots[1] = Arrays.copyOf(slots[1], slots[1].length + 1);
			slots[1][slots[1].length - 1] = slots[0][slot];
			Integer[] pItems = new Integer[slots[0].length - 1];
			System.arraycopy(slots[0], 0, pItems, 0, slot);
			System.arraycopy(slots[0], slot + 1, pItems, slot, pItems.length
					- slot);
			slots[0] = pItems;
			sendProtectedItems();
		}

	}

	public int getProtectSlots() {
		return player.getVarsManager().getBitValue(9227);
	}

	public WorldTile getDeathTile() {
		if (getArguments() == null || getArguments().length < 2)
			return Settings.HOME_LOCATION;
		return (WorldTile) getArguments()[0];
	}

	public boolean hadSkull() {
		if (getArguments() == null || getArguments().length < 2)
			return false;
		return (boolean) getArguments()[1];
	}

	@Override
	public void magicTeleported(int type) {
		destroyRoom();
		player.getPackets().sendBlackOut(0);
		lockInterfaces(false);
		removeControler();
	}

	public void destroyRoom() {
		if (stage != Stages.RUNNING)
			return;
		stage = Stages.DESTROYING;
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8,
							8);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 1200, TimeUnit.MILLISECONDS);
	}

	@Override
	public void forceClose() {
		destroyRoom();
	}
}