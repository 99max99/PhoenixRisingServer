package net.kagani.game.player;

import java.io.Serializable;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.item.Item;
import net.kagani.game.item.ItemsContainer;
import net.kagani.game.player.content.Magic;
import net.kagani.game.player.content.dungeoneering.DungeonConstants;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.player.content.dungeoneering.DungeonPartyManager;
import net.kagani.game.player.content.dungeoneering.DungeonUtils;
import net.kagani.game.player.controllers.Kalaboss;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Logger;

public class DungManager implements Serializable {

	private static final long serialVersionUID = 5472153493680156393L;

	private static final byte version = 2; // termporary, should be deleted once
	// files remade

	// dungeonering
	private boolean[] currentProgress;
	private int previousProgress;
	private int tokens;
	private int maxFloor;
	private int maxComplexity;
	private ItemsContainer<Item> bindedItems;
	private Item bindedAmmo;
	private boolean[] visitedResources;
	private boolean gorajo = false, edimmu = false, celestial = false;

	private byte cVersion; // termporary, should be deleted once files remade

	private Object rejoinKey;

	private transient Player player;
	public transient DungeonPartyManager party;
	private transient Player invitingPlayer;

	private static enum ResourceDungeon {
		EDGEVILLE_DUNGEON(10, 1100, 52849, new WorldTile(3132, 9933, 0), 52867,
				new WorldTile(991, 4585, 0)),

		DWARVEN_MINE(15, 1500, 52855, new WorldTile(3034, 9772, 0), 52864,
				new WorldTile(1041, 4575, 0)),

		EDGEVILLE_DUNGEON_2(20, 1600, 52853, new WorldTile(3104, 9826, 0),
				52868, new WorldTile(1135, 4589, 0)),

		KARANJA_VOLCANO(25, 2100, 52850, new WorldTile(2845, 9557, 0), 52869,
				new WorldTile(1186, 4598, 0)),

		DAEMONHEIM_PENINSULA(30, 2400, 52861, new WorldTile(3513, 3666, 0),
				52862, new WorldTile(3498, 3633, 0)),

		BAXTORIAN_FALLS(35, 3000, 52857, new WorldTile(2578, 9898, 0), 52873,
				new WorldTile(1256, 4592, 0)),

		MINING_GUILD(45, 4400, 52856, new WorldTile(3022, 9741, 0), 52866,
				new WorldTile(1052, 4521, 0)),

		TAVERLY_DUNGEON_1(55, 6200, 52851, new WorldTile(2854, 9841, 0), 52870,
				new WorldTile(1394, 4588, 0)),

		TAVERLY_DUNGEON_2(60, 7000, 52852, new WorldTile(2912, 9810, 0), 52865,
				new WorldTile(1000, 4522, 0)),

		VARRICK_SEWERS(65, 8500, 52863, new WorldTile(3164, 9878, 0), 52876,
				new WorldTile(1312, 4590, 0)),

		CHAOS_TUNNELS(70, 9600, 52858, new WorldTile(3160, 5521, 0), 52874,
				new WorldTile(1238, 4524, 0)),

		AL_KHARID(75, 11400, 52860, new WorldTile(3298, 3307, 0), 52872,
				new WorldTile(1182, 4515, 0)),

		BRIMHAVEM_DUNGEON(80, 12800, 77579, new WorldTile(2697, 9442, 0),
				77580, new WorldTile(1140, 4499, 0)),

		POLYPORE_DUNGEON(82, 13500, 64291, new WorldTile(4661, 5491, 3), 64291,
				new WorldTile(4695, 5625, 3)),

		ASGARNIAN_ICE_DUNGEON(85, 15000, 52859, new WorldTile(3033, 9599, 0),
				52875, new WorldTile(1297, 4510, 0)),

		GORAJO_DUNGEON(95, 30000, 94319, new WorldTile(2234, 3422, 1), 94321,
				new WorldTile(1316, 4634, 0)),

		EDIMMU_DUNGEON(115, 100000, 94320, new WorldTile(2234, 3396, 1), 94322,
				new WorldTile(1374, 4611, 0)),

		CELESTIAL_DRAGON_RESOURCE_DUNGEON(67, 9000, 89682, new WorldTile(3809,
				3529, 0), 89684, new WorldTile(2292, 5971, 0)),

		// ,BRAINDEATH_ISLAND(50, 0)

		;

		private ResourceDungeon(int level, int xp) {
			outsideId = insideId = -1;
		}

		private int level, outsideId, insideId;
		private double xp;
		private WorldTile inside, outside;

		private ResourceDungeon(int level, double xp, int outsideId,
				WorldTile outside, int insideId, WorldTile inside) {
			this.level = level;
			this.xp = xp;
			this.outsideId = outsideId;
			this.outside = outside;
			this.insideId = insideId;
			this.inside = inside;
		}
	}

	public DungManager() {
		reset();
	}

	public void setPlayer(Player player) {
		this.player = player;
		if (cVersion != version)
			reset();
		else if (visitedResources == null) // temporary
			visitedResources = new boolean[ResourceDungeon.values().length];
	}

	public boolean enterResourceDungeon(WorldObject object) {
		int i = 0;
		for (ResourceDungeon dung : ResourceDungeon.values()) {
			if (object.getId() == dung.outsideId
					|| object.getId() == dung.insideId) {
				if (player.getSkills().getLevelForXp(Skills.DUNGEONEERING) < dung.level) {
					player.getPackets().sendObjectMessage(0, 15263739, object,
							"You need Dungeoneering level " + dung.level + ".",
							false);
					player.getPackets().sendGameMessage(
							"You need a Dungeoneering level of " + dung.level
									+ " to venture in here.");
					return true;
				}
				if (dung == ResourceDungeon.POLYPORE_DUNGEON)
					Magic.sendTeleportSpell(player, 13288, 13285, 2516, 2517,
							0, 0, object.getX() == 4695
									&& object.getY() == 5626 ? dung.outside
									: dung.inside, 1, false,
							Magic.OBJECT_TELEPORT);
				else
					Magic.sendTeleportSpell(player, 13288, 13285, 2516, 2517,
							0, 0,
							object.getId() == dung.insideId ? dung.outside
									: dung.inside, 1, false,
							Magic.OBJECT_TELEPORT);
				if (i == 15 || i == 16 || i == 17) {
					if (!gorajo) {
						gorajo = true;
						player.getSkills().addXp(Skills.DUNGEONEERING, dung.xp,
								true);
					} else if (!edimmu) {
						edimmu = true;
						player.getSkills().addXp(Skills.DUNGEONEERING, dung.xp,
								true);
					} else if (!celestial) {
						celestial = true;
						player.getSkills().addXp(Skills.DUNGEONEERING, dung.xp,
								true);
					}
					return true;
				}
				if (!visitedResources[i]) {
					visitedResources[i] = true;
					player.getSkills().addXp(Skills.DUNGEONEERING, dung.xp,
							true);
				}
				return true;
			}
			i++;
		}
		return false;
	}

	public void bind(Item item, int slot) {
		ItemDefinitions defs = item.getDefinitions();
		int bindId = DungeonUtils.getBindedId(item);
		if (bindId == -1)
			return;
		if (DungeonUtils.isBindAmmo(item)) {
			if (bindedAmmo != null
					&& (!defs.isStackable() || bindedAmmo.getId() != bindId)) {
				player.getPackets()
						.sendGameMessage(
								"A currently bound item must be destroyed before another item may be bound.");
				return;
			}
			player.getInventory().deleteItem(slot, item);
			item.setId(bindId);
			player.getInventory().addItem(item);
			if (bindedAmmo == null)
				bindedAmmo = new Item(item);
			else
				bindedAmmo.setAmount(bindedAmmo.getAmount() + item.getAmount());
			if (bindedAmmo.getAmount() > 255)
				bindedAmmo.setAmount(255);
		} else {
			if (bindedItems.getUsedSlots() >= DungeonUtils
					.getMaxBindItems(player.getSkills().getLevelForXp(
							Skills.DUNGEONEERING))) {
				player.getPackets()
						.sendGameMessage(
								"A currently bound item must be destroyed before another item may be bound.");
				return;
			}
			item.setId(bindId);
			player.getInventory().refresh(slot);
			bindedItems.add(new Item(item));
		}
		player.getPackets()
				.sendGameMessage(
						"You bind the "
								+ defs.getName()
								+ " to you. Check in the smuggler to manage your bound items.");
	}

	public void unbind(Item item) {
		if (bindedAmmo != null && bindedAmmo.getId() == item.getId())
			bindedAmmo = null;
		else
			bindedItems.remove(item);
	}

	public Item getBindedAmmo() {
		return bindedAmmo;
	}

	public boolean isInside() {
		return party != null && party.getDungeon() != null;
	}

	public ItemsContainer<Item> getBindedItems() {
		return bindedItems;
	}

	public void reset() {
		currentProgress = new boolean[DungeonConstants.FLOORS_COUNT];
		previousProgress = 0;
		bindedItems = new ItemsContainer<Item>(10, false);
		maxFloor = maxComplexity = 1;
		visitedResources = new boolean[ResourceDungeon.values().length];
		cVersion = version;
	}

	public boolean isTickedOff(int floor) {
		return currentProgress[floor - 1];
	}

	public int getCurrentProgres() {
		int count = 0;
		for (boolean b : currentProgress)
			if (b)
				count++;
		return count;
	}

	public int getPreviousProgress() {
		return previousProgress;
	}

	public int getPrestige() {
		int currentProgress = getCurrentProgres();
		return currentProgress > previousProgress ? currentProgress
				: previousProgress;
	}

	public void tickOff(int floor) {
		currentProgress[floor - 1] = true;
		refreshCurrentProgress();
	}

	public void resetProgress() {
		previousProgress = getCurrentProgres();
		currentProgress = new boolean[DungeonConstants.FLOORS_COUNT];
		refreshCurrentProgress();
		refreshPreviousProgress();
	}

	public void addTokens(int tokens) {
		this.tokens += tokens;
	}

	public int getTokens() {
		return tokens;
	}

	public Object getRejoinKey() {
		return rejoinKey;
	}

	public void setRejoinKey(Object rejoinKey) {
		this.rejoinKey = rejoinKey;
	}

	public int getMaxFloor() {
		return maxFloor;
	}

	public void setMaxFloor(int maxFloor) {
		this.maxFloor = maxFloor;
	}

	public void increaseMaxFloor() {
		if (maxFloor == 60)
			return;
		maxFloor++;
	}

	public void increaseMaxComplexity() {
		maxComplexity++;
	}

	public int getMaxComplexity() {
		return maxComplexity;
	}

	public void setMaxComplexity(int maxComplexity) {
		this.maxComplexity = maxComplexity;
	}

	public void openPartyInterface() {
		player.getInterfaceManager().sendMinigameTab(939);
		player.getInterfaceManager().openGameTab(InterfaceManager.MINIGAME_TAB);
		refreshFloor();
		refreshCurrentProgress();
		refreshPreviousProgress();
		refreshComplexity();
		refreshPartyDetailsComponents();
		refreshPartyGuideModeComponent();
		refreshNames();
	}

	public void refreshPartyGuideModeComponent() {
		if (!player.getInterfaceManager().containsInterface(939))
			return;
		player.getPackets().sendHideIComponent(939, 66,
				party == null || !party.getGuideMode());
	}

	/*
	 * called aswell when player added/removed to party
	 */
	public void refreshPartyDetailsComponents() {
		if (!player.getInterfaceManager().containsInterface(939))
			return;
		player.getPackets().sendHideIComponent(939, 18, party != null);// Form
		// party
		// button
		player.getPackets().sendHideIComponent(939, 14,
				party == null || !party.isLeader(player));// Leave
		// Group
		player.getPackets().sendHideIComponent(939, 20,
				party == null || !party.isLeader(player));// Invite
		// Button
		player.getPackets().sendHideIComponent(939, 77,
				party != null && party.isLeader(player));// Complexity
		// change
		player.getPackets().sendHideIComponent(939, 82,
				party != null && party.isLeader(player));// Floor
		// change
		player.getPackets().sendHideIComponent(939, 68,
				party != null && party.isLeader(player));// Guide
		// mode

		/*
		 * player.getPackets().sendHideIComponent(939, 34, party == null ||
		 * party.isLeader(player)); for (int i = 0; i < 5; i++) { Player p2 =
		 * party == null || i >= party.getTeam().size() ? null :
		 * party.getTeam().get(i); for (int i2 = 59 + i * 3; i2 < 62 + i * 3;
		 * i2++) player.getPackets().sendHideIComponent(939, i2, p2 == null); }
		 */

		for (int i = 0; i < 5; i++) {
			Player p2 = party == null || i >= party.getTeam().size() ? null
					: party.getTeam().get(i);
			for (int i2 = 35 + i * 3; i2 < 38 + i * 3; i2++)
				player.getPackets().sendHideIComponent(939, i2, p2 == null);
		}

		for (int component = 29; component < 35; component++)
			// Lines on the interface
			player.getPackets().sendHideIComponent(939, component, false);
	}

	public void pressOption(int playerIndex, int option) {
		player.stopAll();
		if (party == null || playerIndex >= party.getTeam().size())
			return;
		Player player = party.getTeam().get(playerIndex);
		if (player == null)
			return;
		DungeonManager dungeon = party.getDungeon();
		if (option == 0) {
			if (dungeon == null) {
				this.player.getPackets().sendGameMessage(
						"You must be in a dungeon to do that.");
				return;
			}
			if (player == this.player) {
				this.player
						.getPackets()
						.sendGameMessage(
								"Why don't you just use your inventory and stat interfaces?");
				return;
			}
		} else if (option == 1) {
			if (player == this.player) {
				this.player.getPackets().sendGameMessage(
						"You can't kick yourself!");
				return;
			}
			if (!party.isLeader(this.player)) {
				this.player.getPackets().sendGameMessage(
						"Only your party's leader can kick a party member!");
				return;
			}
			if (player.isLocked() || dungeon != null && dungeon.isBossOpen()) {
				this.player.getPackets().sendGameMessage(
						"You can't kick this player right now.");
				return;
			}
			player.getDungManager().leaveParty();
		} else if (option == 2) {
			if (party.isLeader(player)) {
				this.player.getPackets().sendGameMessage(
						"You can't promote the party leader.");
				return;
			}
			if (!party.isLeader(this.player)) {
				this.player.getPackets().sendGameMessage(
						"Only your party's leader can promote a leader!");
				return;
			}
			party.setLeader(player);
			for (Player p2 : party.getTeam())
				party.refreshPartyDetails(p2);
		} else if (option == 3) {
			if (player != this.player) {
				this.player.getPackets().sendGameMessage(
						"You can't switch another player shared-xp.");
				return;
			}
			player.getPackets().sendGameMessage(
					"Shared xp is currently disabled.");
		}
	}

	public void invite() {
		if (party == null || !party.isLeader(player))
			return;
		player.stopAll();
		if (party.getDungeon() != null) {
			player.getPackets().sendGameMessage("You can't do that right now.");
			return;
		}
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.getPackets().sendInputNameScript("Enter name:");
				player.getTemporaryAttributtes().put(Key.DUNGEON_INVITE,
						Boolean.TRUE);
			}
		});
	}

	public void acceptInvite() {
		Player invitedBy = (Player) player.getTemporaryAttributtes().remove(
				Key.DUNGEON_INVITED_BY);
		if (invitedBy == null)
			return;
		DungeonPartyManager party = invitedBy.getDungManager().getParty();
		if (invitedBy.getDungManager().invitingPlayer != player
				|| party == null || !party.isLeader(invitedBy)) {
			player.closeInterfaces();
			player.getPackets().sendGameMessage("You can't do that right now.");
			return;
		}
		if (party.getTeam().size() >= 5) {
			player.closeInterfaces();
			player.getPackets().sendGameMessage("The party is full.");
			return;
		}
		if (party.getComplexity() > maxComplexity) {
			player.closeInterfaces();
			player.getPackets()
					.sendGameMessage("You can't do this complexity.");
			return;
		}
		if (party.getFloor() > maxFloor) {
			player.closeInterfaces();
			player.getPackets().sendGameMessage("You can't do this floor.");
			return;
		}
		invitedBy.getDungManager().resetInvitation();
		invitedBy.getDungManager().getParty().setDificulty(0);
		invitedBy.getDungManager().getParty().add(player);
		player.stopAll();
		invitedBy.stopAll();
	}

	public void invite(String name) {
		player.stopAll();
		if (party == null) {
			final Player p2 = World.getPlayerByDisplayName(name);
			if (p2 == null) {
				player.getPackets().sendGameMessage("Unable to find " + name);
				return;
			}
			DungeonPartyManager party = p2.getDungManager().getParty();
			if (p2.getDungManager().invitingPlayer != player
					|| player.getPlane() != 0 || party == null
					|| !party.isLeader(p2)) {
				player.getPackets().sendGameMessage(
						"You can't do that right now.");
				return;
			}
			player.getTemporaryAttributtes().put(Key.DUNGEON_INVITED_BY, p2);
			player.getInterfaceManager().sendCentralInterface(949);
			for (int i = 0; i < 5; i++) {
				Player teamMate = i >= party.getTeam().size() ? null : party
						.getTeam().get(i);
				player.getPackets().sendCSVarString(1148 + i,
						teamMate == null ? "" : teamMate.getDisplayName());
				player.getPackets().sendCSVarInteger(
						1153 + i,
						teamMate == null ? -1 : teamMate.getSkills()
								.getCombatLevelWithSummoning());
				player.getPackets().sendCSVarInteger(
						1158 + i,
						teamMate == null ? -1 : teamMate.getSkills()
								.getLevelForXp(Skills.DUNGEONEERING));
				player.getPackets().sendCSVarInteger(
						1163 + i,
						teamMate == null ? -1 : teamMate.getSkills()
								.getHighestSkillLevel());
				player.getPackets().sendCSVarInteger(
						1168 + i,
						teamMate == null ? -1 : teamMate.getSkills()
								.getTotalLevel());
			}
			player.getPackets().sendCSVarInteger(1173, party.getFloor());
			player.getPackets().sendCSVarInteger(1174, party.getComplexity());
			player.setCloseInterfacesEvent(new Runnable() {

				@Override
				public void run() {
					p2.getDungManager().expireInvitation();
					player.getTemporaryAttributtes().remove(
							Key.DUNGEON_INVITED_BY);
				}

			});
		} else {
			if (!party.isLeader(player) || party.getDungeon() != null) {
				player.getPackets().sendGameMessage(
						"You can't do that right now.");
				return;
			}
			if (party.getSize() >= 5) {
				player.getPackets().sendGameMessage("Your party is full.");
				return;
			}
			Player p2 = World.getPlayerByDisplayName(name);
			if (p2 == null) {
				player.getPackets().sendGameMessage(
						"That player is offline, or has privacy mode enabled.");
				return;
			}
			if (player.isAnIronMan()) {
				player.getPackets().sendGameMessage(
						"You are an " + player.getIronmanTitle(true)
								+ ", you stand alone.");
				return;
			}
			if (p2.isAnIronMan()) {
				player.getPackets().sendGameMessage(
						p2.getDisplayName() + " is an "
								+ p2.getIronmanTitle(true)
								+ ", and cannot join your party.");
				return;
			}
			if (!(p2.getControlerManager().getControler() instanceof Kalaboss)) {
				player.getPackets()
						.sendGameMessage(
								"You can only invite a player in or around Daemonheim.");
				return;
			}
			if (p2.getDungManager().party != null) {
				player.getPackets().sendGameMessage(
						p2.getDisplayName() + " is already in a party.");
				return;
			}
			if (p2.getInterfaceManager().containsScreenInterface()
					|| p2.isCantTrade() || p2.isLocked()) {
				player.getPackets()
						.sendGameMessage("The other player is busy.");
				return;
			}
			expireInvitation();
			invitingPlayer = p2;
			player.getPackets().sendGameMessage(
					"Sending party invitation to " + p2.getDisplayName()
							+ "...");
			p2.getPackets().sendDungeonneringRequestMessage(player);
		}

	}

	public void openResetProgress() {
		player.stopAll();
		player.getDialogueManager().startDialogue("PrestigeReset");
	}

	public void switchGuideMode() {
		if (party == null) {
			player.getPackets().sendGameMessage(
					"You must be in a party to do that.");
			return;
		}
		if (party.getDungeon() != null) {
			player.getPackets()
					.sendGameMessage(
							"You cannot change the guide mode once the dungeon has started.");
			return;
		}
		if (!party.isLeader(this.player)) {
			this.player.getPackets().sendGameMessage(
					"Only your party's leader can switch guide mode!");
			return;
		}
		player.stopAll();
		party.setGuideMode(!party.getGuideMode());
		if (party.getGuideMode())
			player.getPackets()
					.sendGameMessage(
							"Guide mode enabled. Your map will show you the critical path, but you will receive an xp penalty.");
		else
			player.getPackets()
					.sendGameMessage(
							"Guide mode disabled. Your map will no longer show the critical path.");
		for (Player p2 : party.getTeam())
			p2.getDungManager().refreshPartyGuideModeComponent();
	}

	public void changeFloor() {
		if (party == null) {
			player.getPackets().sendGameMessage(
					"You must be in a party to do that.");
			return;
		}
		if (party.getDungeon() != null) {
			player.getPackets().sendGameMessage(
					"You cannot change these settings while in a dungeon.");
			return;
		}
		if (!party.isLeader(this.player)) {
			this.player.getPackets().sendGameMessage(
					"Only your party's leader can change floor!");
			return;
		}
		player.stopAll();
		player.getInterfaceManager().sendCentralInterface(947);
		Logger.log(this, "Max Floors: " + party.getMaxFloor());
		for (int i = 0; i < party.getMaxFloor(); i++)
			player.getPackets().sendHideIComponent(947, 16 + i, false);
		for (int index = party.getTeam().size() - 1; index >= 0; index--) {
			Player teamMate = party.getTeam().get(index);
			for (int floor = 1; floor < teamMate.getDungManager().getMaxFloor(); floor++) {
				player.getPackets().sendHideIComponent(947,
						88 + floor + (4 - index) * 122, false);
				if (teamMate.getDungManager().currentProgress[floor - 1])
					player.getPackets().sendHideIComponent(947,
							148 + floor + (4 - index) * 122, false);
			}
		}
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				player.getTemporaryAttributtes().remove(Key.DUNG_FLOOR);
			}
		});
	}

	public void selectFloor(int floor) {
		if (party == null) {
			player.getPackets().sendGameMessage(
					"You must be in a party to do that.");
			return;
		}
		/*
		 * cant happen, cuz u cant click anyway but oh well
		 */
		if (party.getMaxFloor() < party.getMaxFloor()) {
			player.getPackets().sendGameMessage(
					"A member in your party can't do this floor.");
			return;
		}
		player.getPackets().sendIComponentText(947, 721, "" + floor);
		player.getTemporaryAttributtes().put(Key.DUNG_FLOOR, floor);
	}

	public void confirmFloor() {
		Integer selectedFloor = (Integer) player.getTemporaryAttributtes()
				.remove(Key.DUNG_FLOOR);
		player.stopAll();
		if (party == null) {
			player.getPackets().sendGameMessage(
					"You must be in a party to do that.");
			return;
		}
		if (selectedFloor == null)
			selectedFloor = party.getMaxFloor();
		if (party.getMaxFloor() < party.getMaxFloor()) {
			player.getPackets().sendGameMessage(
					"A member in your party can't do this floor.");
			return;
		}
		if (party.getDungeon() != null) {
			player.getPackets().sendGameMessage(
					"You cannot change these settings while in a dungeon.");
			return;
		}
		if (!party.isLeader(player)) {
			player.getPackets().sendGameMessage(
					"Only your party's leader can change floor!");
			return;
		}
		party.setFloor(selectedFloor);
	}

	public void changeComplexity() {
		if (party == null) {
			player.getPackets().sendGameMessage(
					"You must be in a party to do that.");
			return;
		}
		if (party.getDungeon() != null) {
			player.getPackets().sendGameMessage(
					"You cannot change these settings while in a dungeon.");
			return;
		}
		if (!party.isLeader(player)) {
			player.getPackets().sendGameMessage(
					"Only your party's leader can change complexity!");
			return;
		}
		player.stopAll();
		player.getInterfaceManager().sendCentralInterface(938);
		selectComplexity(party.getMaxComplexity());
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				player.getTemporaryAttributtes().remove(Key.DUNG_COMPLEXITY);
			}
		});
	}

	public void selectComplexity(int complexity) {
		if (party == null) {
			player.getPackets().sendGameMessage(
					"You must be in a party to do that.");
			return;
		}
		if (party.getMaxComplexity() < complexity) {
			player.getPackets().sendGameMessage(
					"A member in your party can't do this complexity.");
			return;
		}
		Integer selectedComplexity = (Integer) player.getTemporaryAttributtes()
				.remove(Key.DUNG_COMPLEXITY);
		if (selectedComplexity != null)
			markComplexity(selectedComplexity, false);
		markComplexity(complexity, true);
		hideSkills(complexity);
		int penalty = complexity == 6 ? 0 : ((6 - complexity) * 5 + 25);
		player.getPackets().sendIComponentText(938, 10, "" + complexity);
		player.getPackets().sendIComponentText(938, 78,
				penalty + "% XP Penalty");
		player.getTemporaryAttributtes().put(Key.DUNG_COMPLEXITY, complexity);
	}

	public void confirmComplexity() {
		Integer selectedComplexity = (Integer) player.getTemporaryAttributtes()
				.remove(Key.DUNG_COMPLEXITY);
		player.stopAll();
		if (selectedComplexity == null)
			return;
		if (party == null) {
			player.getPackets().sendGameMessage(
					"You must be in a party to do that.");
			return;
		}
		if (party.getMaxComplexity() < selectedComplexity) {
			player.getPackets().sendGameMessage(
					"A member in your party can't do this complexity.");
			return;
		}
		if (party.getDungeon() != null) {
			player.getPackets().sendGameMessage(
					"You cannot change these settings while in a dungeon.");
			return;
		}
		if (!party.isLeader(player)) {
			player.getPackets().sendGameMessage(
					"Only your party's leader can change complexity!");
			return;
		}
		party.setComplexity(selectedComplexity);
	}

	private void markComplexity(int complexity, boolean mark) {
		player.getPackets().sendHideIComponent(938,
				14 + ((complexity - 1) * 5), !mark);
	}

	private static final String[] COMPLEXITY_SKILLS = { "Combat", "Cooking",
			"Firemaking", "Woodcutting", "Fishing", "Creating Weapons",
			"Mining", "Runecrafting", "Farming Textiles", "Hunting",
			"Creating Armour", "Farming Seeds", "Herblore", "Thieving",
			"Summoning", "Construction", "Divination" };

	private void hideSkills(int complexity) {
		int count = 0;
		if (complexity >= 1)
			count += 1;
		if (complexity >= 2)
			count += 4;
		if (complexity >= 3)
			count += 3;
		if (complexity >= 4)
			count += 3;
		if (complexity >= 5)
			count += 5;
		if (complexity >= 6)
			count += 1;
		for (int i = 0; i < COMPLEXITY_SKILLS.length; i++) {
			player.getPackets().sendIComponentText(938, 45 + i * 2,
					(i >= count ? "<col=383838>" : "") + COMPLEXITY_SKILLS[i]);
		}
	}

	public void expireInvitation() {
		if (invitingPlayer == null)
			return;
		player.getPackets().sendGameMessage(
				"Your dungeon party invitation to "
						+ invitingPlayer.getDisplayName() + " has expired.");
		invitingPlayer.getPackets().sendGameMessage(
				"A dungeon party invitation from " + player.getDisplayName()
						+ " has expired.");
		invitingPlayer = null;
	}

	public void enterDungeon(boolean selectSize) {
		player.stopAll();
		expireInvitation();
		if (party == null) {
			player.getDialogueManager().startDialogue("DungeonPartyStart");
			return;
		}
		if (party.getDungeon() != null) // cant happen
			return;
		if (!party.isLeader(player)) {
			player.getPackets().sendGameMessage(
					"Only your party's leader can start a dungeon!");
			return;
		}
		if (party.getFloor() == 0) {
			changeFloor();
			return;
		}
		if (party.getComplexity() == 0) {
			changeComplexity();
			return;
		}
		if (party.getTeam().size() > 1) {
			for (Player p2 : party.getTeam()) {
				if (!p2.isAnIronMan())
					continue;

				player.getPackets().sendGameMessage(
						p2.getDisplayName() + " is an "
								+ p2.getIronmanTitle(true)
								+ " and cannot participate.");
				return;
			}
		}

		if (party.getDificulty() == 0) {
			if (party.getTeam().size() == 1)
				party.setDificulty(1);
			else {
				player.getDialogueManager().startDialogue("DungeonDificulty",
						party.getTeam().size());
				return;
			}
		}
		boolean solo = party.getTeam().size() == 1;
		if (solo)
			party.setKeyShare(true);
		else if (party.getKeyType() == 0) {
			player.getDialogueManager().startDialogue("PreShareD");
			return;
		}
		if (selectSize) {
			if (party.getComplexity() == 6) {
				player.getDialogueManager().startDialogue("DungeonSize");
				return;
			} else
				party.setSize(DungeonConstants.SMALL_DUNGEON);
		}
		for (Player p2 : party.getTeam()) {
			for (Item item : p2.getInventory().getItems().getItems()) {
				if (item != null && item.getId() != 15707) {
					player.getPackets()
							.sendGameMessage(
									p2.getDisplayName()
											+ " is carrying items that cannot be taken into Daemonheim.");
					return;
				}
			}
			for (Item item : p2.getEquipment().getItems().getItems()) {
				if (item != null && item.getId() != 15707) {
					player.getPackets()
							.sendGameMessage(
									p2.getDisplayName()
											+ " is carrying items that cannot be taken into Daemonheim.");
					return;
				}
			}
			if (p2.getFamiliar() != null || p2.getPet() != null) {
				player.getPackets()
						.sendGameMessage(
								p2.getDisplayName()
										+ " is carrying a familiar that cannot be taken into Daemonheim.");
				return;
			}
			if (p2.getPlane() != 0
					|| p2.getInterfaceManager().containsScreenInterface()
					|| p2.isLocked()
					|| !(p2.getControlerManager().getControler() instanceof Kalaboss)) {
				player.getPackets().sendGameMessage(
						p2.getDisplayName() + " is busy.");
				return;
			}
		}
		party.start();
	}

	public void setSize(int size) {
		if (party == null || !party.isLeader(player)
				|| party.getComplexity() != 6)
			return;
		party.setSize(size);
	}

	public void setDificulty(int dificulty) {
		if (party == null || !party.isLeader(player))
			return;
		party.setDificulty(dificulty);
	}

	public void setKeyShare(boolean isKeyShare) {
		if (party == null || !party.isLeader(player))
			return;
		party.setKeyShare(isKeyShare);
	}

	public void resetInvitation() {
		if (invitingPlayer == null)
			return;
		invitingPlayer = null;
	}

	public void refreshNames() {
		if (party == null)
			return;
		int index = 0;
		for (Player p2 : party.getTeam())
			player.getPackets().sendCSVarString(2376 + (index++),
					p2.getDisplayName());
	}

	public void refreshFloor() {
		player.getPackets().sendCSVarInteger(1180,
				party == null ? 0 : party.getFloor());
	}

	public void refreshComplexity() {
		player.getPackets().sendCSVarInteger(1183,
				party == null ? 0 : party.getComplexity());
	}

	public void refreshCurrentProgress() {
		player.getPackets().sendCSVarInteger(1181, getCurrentProgres());
	}

	public void refreshPreviousProgress() {
		player.getPackets().sendCSVarInteger(1182, previousProgress);
	}

	public DungeonPartyManager getParty() {
		return party;
	}

	public void setParty(DungeonPartyManager party) {
		this.party = party;
	}

	public void formParty() {
		if (party != null)
			return;
		if (!(player.getControlerManager().getControler() instanceof Kalaboss)
				|| player.getPlane() != 0) {
			player.getPackets().sendGameMessage(
					"You can only form a party in or around Daemonheim.");
			return;
		}
		player.stopAll();
		new DungeonPartyManager().add(player);
	}

	public void finish() {
		if (party != null)
			party.leaveParty(player, true);
	}

	public void checkLeaveParty() {
		if (party == null)
			return;
		if (party.getDungeon() != null)
			player.getDialogueManager().startDialogue("DungeonLeaveParty");
		else
			leaveParty();
	}

	public void leaveParty() {
		if (party != null)
			party.leaveParty(player, false);
	}

	public void setTokens(int tokens) {
		this.tokens = tokens;
	}
}