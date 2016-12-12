package net.kagani.game.minigames.duel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.kagani.Engine;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Combat;
import net.kagani.game.player.content.ItemConstants;
import net.kagani.game.player.content.Drinkables.Drink;
import net.kagani.game.player.controllers.Controller;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.network.decoders.WorldPacketsDecoder;
import net.kagani.network.decoders.handlers.ButtonHandler;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class DuelArena extends Controller {

	/**
	 * script_1367() - CASTLE WARS
	 */

	private static final int LOGOUT = 0, TELEPORT = 1, DUEL_END_LOSE = 2,
			DUEL_END_WIN = 3;
	private Player target;
	private volatile boolean ifFriendly, isDueling;

	private static final int[] FUN_WEAPONS = { 4566 };
	private static final WorldTile[] LOBBY_TELEPORTS = {
			new WorldTile(3367, 3275, 0), new WorldTile(3360, 3275, 0),
			new WorldTile(3358, 3270, 0), new WorldTile(3363, 3268, 0),
			new WorldTile(3370, 3268, 0), new WorldTile(3367, 3267, 0),
			new WorldTile(3376, 3275, 0), new WorldTile(3377, 3271, 0),
			new WorldTile(3375, 3269, 0), new WorldTile(3381, 3277, 0) };

	@Override
	public void start() {
		this.target = (Player) getArguments()[0];
		ifFriendly = (boolean) getArguments()[1];
		setArguments(null);
		openDuelScreen(target, ifFriendly);
	}

	private void openDuelScreen(Player target, boolean ifFriendly) {
		synchronized (this) {
			if (!ifFriendly) {
				sendOptions(player);
				player.getDuelRules().getStake().clear();
			}
			player.getTemporaryAttributtes().put("acceptedDuel", false);
			player.getPackets().sendItems(134, false,
					player.getDuelRules().getStake());
			player.getPackets().sendItems(134, true,
					player.getDuelRules().getStake());
			player.getPackets().sendIComponentText(
					ifFriendly ? 637 : 631,
					ifFriendly ? 16 : 38,
					" "
							+ Utils.formatPlayerNameForDisplay(target
									.getUsername()));
			player.getPackets().sendIComponentText(ifFriendly ? 637 : 631,
					ifFriendly ? 18 : 40,
					"" + (target.getSkills().getCombatLevel()));
			player.getVarsManager().sendVar(286, 0);
			player.getTemporaryAttributtes().put("firstScreen", true);
			player.getInterfaceManager().sendCentralInterface(
					ifFriendly ? 637 : 631);
			refreshScreenMessage(true, ifFriendly);
			player.setCloseInterfacesEvent(new Runnable() {
				@Override
				public void run() {
					closeDuelInteraction(DuelStage.DECLINED);
				}
			});
		}
	}

	public void accept(boolean firstStage) {
		synchronized (this) {
			if (!hasTarget())
				return;
			Boolean targetAccepted = (Boolean) target.getTemporaryAttributtes()
					.get("acceptedDuel");
			if (targetAccepted == null)
				targetAccepted = false;
			DuelRules rules = player.getDuelRules();
			if (!rules.canAccept(player.getDuelRules().getStake()))
				return;
			synchronized (target.getControlerManager().getControler()) {
				if (targetAccepted) {
					if (firstStage) {
						if (nextStage())
							((DuelArena) target.getControlerManager()
									.getControler()).nextStage();
					} else {
						player.setCloseInterfacesEvent(null);
						player.closeInterfaces();
						closeDuelInteraction(DuelStage.DONE);
					}
					return;
				}
				player.getTemporaryAttributtes().put("acceptedDuel", true);
				refreshScreenMessages(firstStage, ifFriendly);
			}
		}
	}

	public void closeDuelInteraction(DuelStage stage) {
		synchronized (this) {
			final Player oldTarget = target;
			Controller controler = oldTarget == null ? null : oldTarget
					.getControlerManager().getControler();
			if (controler == null || !(controler instanceof DuelArena))
				return;
			DuelArena targetConfiguration = (DuelArena) controler;
			synchronized (controler) {
				if (hasTarget() && targetConfiguration.hasTarget()) {
					if (controler instanceof DuelArena) {
						player.setCloseInterfacesEvent(null);
						player.closeInterfaces();
						oldTarget.setCloseInterfacesEvent(null);
						oldTarget.closeInterfaces();
						if (stage != DuelStage.DONE) {
							reset();
							targetConfiguration.reset();
							for (Item item : player.getDuelRules().getStake()
									.getItems()) {
								if (item == null)
									continue;
								player.getInventory().addItemMoneyPouch(item);
							}
							for (Item item : oldTarget.getDuelRules()
									.getStake().getItems()) {
								if (item == null)
									continue;
								oldTarget.getInventory()
										.addItemMoneyPouch(item);
							}
							oldTarget.getDuelRules().getStake().clear();
							player.getDuelRules().getStake().clear();
							WorldTasksManager.schedule(new WorldTask() {

								@Override
								public void run() {
									player.getControlerManager()
											.startControler("DuelControler");
									oldTarget.getControlerManager()
											.startControler("DuelControler");
								}
							}, 1);
						} else {
							if (!removeEquipment()) {
								closeDuelInteraction(DuelStage.NO_SPACE);
								return;
							}
							if (!targetConfiguration.removeEquipment()) {
								targetConfiguration
										.closeDuelInteraction(DuelStage.NO_SPACE);
								return;
							}
							beginBattle(true);
							targetConfiguration.beginBattle(false);
						}
						if (stage == DuelStage.DONE)
							player.getPackets().sendGameMessage(
									"Your battle will begin shortly.");
						else if (stage == DuelStage.SECOND)
							player.getPackets()
									.sendGameMessage(
											"<col=ff0000>Please check if these settings are correct.");
						else if (stage == DuelStage.DECLINED)
							oldTarget
									.getPackets()
									.sendGameMessage(
											"<col=ff0000>Other player declined the duel!");
						else if (stage == DuelStage.NO_SPACE) {
							oldTarget
									.getPackets()
									.sendGameMessage(
											"You do not have enough space to continue!");
							oldTarget
									.getPackets()
									.sendGameMessage(
											"Other player does not have enough space to continue!");
						}
					}
				}
			}
		}
	}

	private void reset() {
		target = null;
		player.getTemporaryAttributtes().put("acceptedDuel", false);
	}

	public void addItem(int slot, int amount) {
		synchronized (this) {
			if (!hasTarget())
				return;
			Controller controler = target.getControlerManager().getControler();
			if (controler == null || !(controler instanceof DuelArena))
				return;
			synchronized (target.getControlerManager().getControler()) {
				Item item = player.getInventory().getItem(slot);
				if (item == null)
					return;
				if (!ItemConstants.isTradeable(item)) {
					player.getPackets().sendGameMessage(
							"That item cannot be staked!");
					return;
				}
				Item[] itemsBefore = player.getDuelRules().getStake()
						.getItemsCopy();
				int maxAmount = player.getInventory().getItems()
						.getNumberOf(item);
				item = new Item(item.getId(), amount < maxAmount ? amount
						: maxAmount);
				player.getDuelRules().getStake().add(item);
				player.getInventory().deleteItem(slot, item);
				refreshItems(itemsBefore);
				cancelAccepted();
			}
		}
	}

	public void addItem(Item item) {
		synchronized (this) {
			if (item == null || !hasTarget())
				return;
			Controller controler = target.getControlerManager().getControler();
			if (controler == null || !(controler instanceof DuelArena))
				return;
			synchronized (target.getControlerManager().getControler()) {
				if (!ItemConstants.isTradeable(item)) {
					player.getPackets().sendGameMessage(
							"That item cannot be staked!");
					return;
				}
				Item[] itemsBefore = player.getDuelRules().getStake()
						.getItemsCopy();
				player.getInventory().removeItemMoneyPouch(item);
				player.getDuelRules().getStake().add(item);
				refreshItems(itemsBefore);
				cancelAccepted();
			}
		}
	}

	public void removeItem(final int slot, int amount) {
		synchronized (this) {
			if (!hasTarget())
				return;
			Controller controler = target.getControlerManager().getControler();
			if (controler == null || !(controler instanceof DuelArena))
				return;
			synchronized (target.getControlerManager().getControler()) {
				Item item = player.getDuelRules().getStake().get(slot);
				if (item == null)
					return;
				Item[] itemsBefore = player.getDuelRules().getStake()
						.getItemsCopy();
				int maxAmount = player.getDuelRules().getStake()
						.getNumberOf(item);
				item = new Item(item.getId(), amount < maxAmount ? amount
						: maxAmount);
				player.getDuelRules().getStake().remove(slot, item);
				player.getInventory().addItemMoneyPouch(item);
				refreshItems(itemsBefore);
				cancelAccepted();
			}
		}
	}

	private void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			Item item = player.getDuelRules().getStake().getItems()[index];
			if (itemsBefore[index] != item) {
				if (itemsBefore[index] != null
						&& (item == null
								|| item.getId() != itemsBefore[index].getId() || item
								.getAmount() < itemsBefore[index].getAmount()))
					sendFlash(index);
				changedSlots[count++] = index;
			}
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
	}

	private void sendFlash(int slot) {
		player.getPackets().sendInterFlashScript(631, 47, 4, 7, slot);
		target.getPackets().sendInterFlashScript(631, 50, 4, 7, slot);
	}

	private void sendRuleFlash(int componentId, int slot) {
		// player.getPackets().sendInterFlashScript(ifFriendly ? 637 : 631,
		// componentId, 11, 2, slot);
		// target.getPackets().sendInterFlashScript(ifFriendly ? 637 : 631,
		// componentId, 11, 2, slot);
	}

	private void refresh(int... slots) {
		player.getPackets().sendUpdateItems(134,
				player.getDuelRules().getStake(), slots);
		target.getPackets().sendUpdateItems(134, true,
				player.getDuelRules().getStake().getItems(), slots);
	}

	public void cancelAccepted() {
		boolean canceled = false;
		if ((Boolean) player.getTemporaryAttributtes().get("acceptedDuel")) {
			player.getTemporaryAttributtes().put("acceptedDuel", false);
			canceled = true;
		}
		if ((Boolean) target.getTemporaryAttributtes().get("acceptedDuel")) {
			target.getTemporaryAttributtes().put("acceptedDuel", false);
			canceled = true;
		}
		if (canceled)
			refreshScreenMessages(canceled, ifFriendly);
	}

	private void openConfirmationScreen(boolean ifFriendly) {
		player.getInterfaceManager().sendCentralInterface(
				ifFriendly ? 639 : 626);
		refreshScreenMessage(false, ifFriendly);
	}

	private void refreshScreenMessages(boolean firstStage, boolean ifFriendly) {
		refreshScreenMessage(firstStage, ifFriendly);
		((DuelArena) target.getControlerManager().getControler())
				.refreshScreenMessage(firstStage, ifFriendly);
	}

	private void refreshScreenMessage(boolean firstStage, boolean ifFriendly) {
		player.getPackets().sendIComponentText(
				firstStage ? (ifFriendly ? 637 : 631)
						: (ifFriendly ? 639 : 626),
				firstStage ? (ifFriendly ? 20 : 41) : (ifFriendly ? 23 : 35),
				"<col=ff0000>" + getAcceptMessage(firstStage));
	}

	private String getAcceptMessage(boolean firstStage) {
		if (target.getTemporaryAttributtes().get("acceptedDuel") == Boolean.TRUE)
			return "Other player has accepted.";
		else if (player.getTemporaryAttributtes().get("acceptedDuel") == Boolean.TRUE)
			return "Waiting for other player...";
		return firstStage ? "" : "Please look over the agreements to the duel.";
	}

	public boolean nextStage() {
		if (!hasTarget())
			return false;
		if (player.getInventory().getItems().getUsedSlots()
				+ target.getDuelRules().getStake().getUsedSlots() > 28) {
			player.setCloseInterfacesEvent(null);
			player.closeInterfaces();
			closeDuelInteraction(DuelStage.NO_SPACE);
			return false;
		}
		player.getTemporaryAttributtes().put("acceptedDuel", false);
		openConfirmationScreen(false);
		player.getInterfaceManager().removeInventoryInterface();
		return true;
	}

	private void sendOptions(Player player) {
		player.getInterfaceManager().sendInventoryInterface(628);
		player.getPackets().sendUnlockIComponentOptionSlots(628, 0, 0, 27, 0,
				1, 2, 3, 4, 5);
		player.getPackets().sendInterSetItemsOptionsScript(628, 0, 93, 4, 7,
				"Stake 1", "Stake 5", "Stake 10", "Stake All", "Examine");
		player.getPackets().sendUnlockIComponentOptionSlots(631, 47, 0, 27, 0,
				1, 2, 3, 4, 5);
		player.getPackets().sendInterSetItemsOptionsScript(631, 0, 120, 4, 7,
				"Remove 1", "Remove 5", "Remove 10", "Remove All", "Examine");
	}

	public void endDuel(final Player victor, final Player loser, boolean isDraw) {
		startEndingTeleport(victor, false);
		startEndingTeleport(loser, false);
		if (!isDueling)
			return;
		Controller controler = target == null ? null : target
				.getControlerManager().getControler();
		if (controler == null || !(controler instanceof DuelArena))
			return;
		DuelArena targetConfiguration = (DuelArena) controler;
		targetConfiguration.isDueling = false;
		isDueling = false;
		DuelRules rules = victor == null ? loser.getDuelRules() : victor
				.getDuelRules();
		if (rules != null && !rules.hasRewardGiven()) {
			DuelRules.sendRewardGivenUpdate(victor, loser, true);
			for (Item item : victor.getDuelRules().getStake().getItems()) {
				if (item == null)
					continue;
				victor.getInventory().addItemDrop(item.getId(),
						item.getAmount());
			}
			for (Item item : loser.getDuelRules().getStake().getItems()) {
				CopyOnWriteArrayList<Item> wonItems = new CopyOnWriteArrayList<Item>();
				log(player, victor, wonItems);
				if (item == null)
					continue;
				if (isDraw)
					loser.getInventory().addItemDrop(item.getId(),
							item.getAmount());
				else
					victor.getInventory().addItemDrop(item.getId(),
							item.getAmount());
			}
			Logger.globalLog(
					victor.getUsername(),
					victor.getSession().getIP(),
					new String(" completed the duel with "
							+ loser.getUsername()
							+ " previous items are: "
							+ Arrays.toString(victor.getDuelRules().getStake()
									.getShiftedItem())
							+ " new items are "
							+ Arrays.toString(loser.getDuelRules().getStake()
									.getShiftedItem())));
			victor.getDuelRules().getStake().clear();
			loser.getDuelRules().getStake().clear();
		}
		loser.getPackets().sendGameMessage(
				isDraw ? "The battle has ended in a draw."
						: "Oh dear, it seems you have lost to "
								+ victor.getDisplayName() + ".");
		victor.getPackets().sendGameMessage(
				isDraw ? "The battle has ended in a draw."
						: "congratulations! You easily defeated "
								+ loser.getDisplayName() + ".");
		loser.setCanPvp(false);
		loser.getHintIconsManager().removeUnsavedHintIcon();
		loser.reset();
		loser.closeInterfaces();
		loser.getControlerManager().removeControlerWithoutCheck();
		victor.setCanPvp(false);
		victor.getHintIconsManager().removeUnsavedHintIcon();
		victor.reset();
		victor.closeInterfaces();
		victor.getControlerManager().removeControlerWithoutCheck();
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				loser.getControlerManager().startControler("DuelControler");
				victor.getControlerManager().startControler("DuelControler");
			}
		});
	}

	private void log(Player player, Player victor,
			CopyOnWriteArrayList<Item> wonItems) {
		try {
			final String FILE_PATH = "data/logs/stake/";
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH
					+ victor.getUsername() + ".txt", true));
			writer.write("[Stake session started]");
			writer.newLine();
			writer.write("Stake Winner Information: Username: "
					+ victor.getUsername() + ". IP "
					+ victor.getSession().getIP() + ". Location: "
					+ victor.getX() + ", " + victor.getY() + ", "
					+ victor.getPlane() + ".");
			writer.newLine();
			writer.write("Stake Loser Information: Username: "
					+ player.getUsername() + ". IP: " + player.getLastGameIp()
					+ ". Location: " + player.getX() + ", " + player.getY()
					+ ", " + player.getPlane() + ".");
			writer.newLine();
			writer.write("Time: [" + dateFormat.format(cal.getTime()) + "]");
			for (Item item : wonItems) {
				if (item == null) {
					continue;
				}
				ItemDefinitions defs = ItemDefinitions.getItemDefinitions(item
						.getId());
				String name = defs == null ? "" : defs.getName().toLowerCase();
				writer.newLine();
				writer.write(victor.getUsername() + " won: " + name
						+ ", amount: " + item.getAmount() + " from "
						+ player.getUsername() + ".");
			}
			writer.newLine();
			writer.write("[Stake session ended]");
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(player, e);
		}
	}

	private void startEndingTeleport(Player player, boolean loggedOut) {
		WorldTile tile = LOBBY_TELEPORTS[Utils.random(LOBBY_TELEPORTS.length)];
		WorldTile teleTile = tile;
		for (int trycount = 0; trycount < 10; trycount++) {
			teleTile = new WorldTile(tile, 2);
			if (World.isTileFree(tile.getPlane(), teleTile.getX(),
					teleTile.getY(), player.getSize()))
				break;
			teleTile = tile;
		}
		if (loggedOut) {
			player.setLocation(teleTile);
			return;
		}
		player.setNextWorldTile(teleTile);
	}

	private boolean removeEquipment() {
		int slot = 0;
		for (int i = 10; i < 23; i++) {
			if (i == 13 || i == 15) {// shield or wep
				if (player.getEquipment().hasTwoHandedWeapon()) {
					if (!ButtonHandler.sendRemove(target, 3, false)) {
						return false;
					}
				}
			}
			if (player.getDuelRules().getRule(i)) {
				slot = i - 10;
				if (!ButtonHandler.sendRemove(player, slot, false)) {
					return false;
				}
			}
		}
		return true;
	}

	private void beginBattle(boolean started) {
		isDueling = true;
		if (started)
			battleTeleport(player, target);
		player.stopAll();
		player.lock(2); // fixes mass click steps
		player.reset();
		player.getTemporaryAttributtes().put("startedDuel", true);
		player.getTemporaryAttributtes().put("canFight", false);
		player.setCanPvp(true);
		player.getHintIconsManager().addHintIcon(target, 1, -1, false);
		WorldTasksManager.schedule(new WorldTask() {
			int count = 3;

			@Override
			public void run() {
				if (count > 0)
					player.setNextForceTalk(new ForceTalk("" + count));
				if (count == 0) {
					player.getTemporaryAttributtes().put("canFight", true);
					player.setNextForceTalk(new ForceTalk("FIGHT!"));
					this.stop();
					return;
				}
				count--;
			}
		}, 0, 2);
	}

	@Override
	public boolean canEat(int heal) {
		if (player.getDuelRules().getRule(4) && isDueling) {
			player.getPackets().sendGameMessage(
					"You cannot eat during this duel.", true);
			return false;
		}
		return true;
	}

	@Override
	public boolean canPot(Drink pot) {
		if (player.getDuelRules().getRule(3) && isDueling) {
			player.getPackets().sendGameMessage(
					"You cannot drink during this duel.");
			return false;
		}
		return true;
	}

	@Override
	public boolean canMove(int dir) {
		if (isDueling && player.getDuelRules().getRule(25)) {
			player.getPackets().sendGameMessage(
					"You cannot move during this duel!");
			return false;
		}
		return true;
	}

	@Override
	public boolean canSummonFamiliar() {
		if (player.getDuelRules().getRule(24) && isDueling)
			return true;
		player.getPackets().sendGameMessage(
				"Summoning has been disabled during this duel!");
		return false;
	}

	@Override
	public void process() {
		if (!hasTarget()
				|| target.getControlerManager().getControler() != null
				&& !(target.getControlerManager().getControler() instanceof DuelArena)) {
			end(DUEL_END_LOSE);
			return;
		}
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getDialogueManager()
				.startDialogue("SimpleMessage",
						"A magical force prevents you from teleporting from the arena.");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getDialogueManager()
				.startDialogue("SimpleMessage",
						"A magical force prevents you from teleporting from the arena.");
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		end(TELEPORT);
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		player.getDialogueManager().startDialogue("ForfeitDialouge");
		return true;
	}

	@Override
	public boolean sendDeath() {
		player.lock(8);
		player.stopAll();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				player.stopAll();
				if (loop == 0) {
					player.setNextAnimation(player.getDeathAnimation());
				} else if (loop == 1) {
					player.getPackets().sendGameMessage(
							"Oh dear, you have died.");
				} else if (loop == 3) {
					player.setNextAnimation(new Animation(-1));
					end(DUEL_END_LOSE);
					this.stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean login() {
		return true;
	}

	@Override
	public boolean logout() {
		end(LOGOUT);
		startEndingTeleport(player, true);
		return false;
	}

	@Override
	public void forceClose() {
		end(DUEL_END_LOSE);
	}

	@Override
	public boolean keepCombating(Entity victim) {
		DuelRules rules = player.getDuelRules();
		if (victim instanceof NPC && !(victim instanceof Familiar))
			return true;
		boolean isRanging = player.getCombatDefinitions().getStyle(true) == Combat.RANGE_TYPE
				|| player.getCombatDefinitions().getStyle(false) == Combat.RANGE_TYPE;
		if (player.getTemporaryAttributtes().get("canFight") == Boolean.FALSE) {
			player.getPackets().sendGameMessage("The duel hasn't started yet.");
			return false;
		}
		if (target != victim) {
			player.getPackets()
					.sendGameMessage(
							"You may only attack your target, you can find your target by following the hint icon on your map.");
			return false;
		}
		if ((player.getEquipment().getWeaponId() == 22496 || player
				.getCombatDefinitions().getSpellId() > 0)
				&& rules.getRule(2)
				&& isDueling) {
			player.getPackets().sendGameMessage(
					"You cannot use Magic in this duel!");
			return false;
		} else if (isRanging && rules.getRule(0) && isDueling) {
			player.getPackets().sendGameMessage(
					"You cannot use Range in this duel!");
			return false;
		} else if (!isRanging && rules.getRule(1)
				&& player.getCombatDefinitions().getSpellId() <= 0 && isDueling) {
			player.getPackets().sendGameMessage(
					"You cannot use Melee in this duel!");
			return false;
		} else if (rules.getRule(8)) {// fun weps.
			for (int weapon : FUN_WEAPONS) {
				if (player.getEquipment().getWeaponId() == weapon)
					return true;
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean canEquip(int slotId, int itemId) {
		DuelRules rules = player.getDuelRules();
		if (isDueling) {
			if (rules.getRule(10 + slotId)) {
				player.getPackets().sendGameMessage(
						"You can't equip "
								+ ItemDefinitions.getItemDefinitions(itemId)
										.getName().toLowerCase()
								+ " during this duel.");
				return false;
			}
			if (slotId == 3 && player.getEquipment().hasTwoHandedWeapon()
					&& rules.getRule(15)) {
				player.getPackets().sendGameMessage(
						"You can't equip "
								+ ItemDefinitions.getItemDefinitions(itemId)
										.getName().toLowerCase()
								+ " during this duel.");
				return false;
			}
		}
		return true;
	}

	// Regular, Summoning, Obsticals
	private static final WorldTile[][] POSSIBLE_TILE_CENTRE = {
			{ new WorldTile(3346, 3251, 0), new WorldTile(3376, 3232, 0) },
			{ new WorldTile(3346, 3214, 0) },
			{ new WorldTile(3345, 3231, 0), new WorldTile(3376, 3213, 0),
					new WorldTile(3376, 3251, 0) } };

	private static void battleTeleport(Player player, Player target) {
		DuelRules rules = player.getDuelRules();
		boolean noMovement = rules.getRule(25);
		final WorldTile CENTER = rules.getRule(24) ? POSSIBLE_TILE_CENTRE[1][0]
				: rules.getRule(6) ? POSSIBLE_TILE_CENTRE[2][Utils
						.random(POSSIBLE_TILE_CENTRE[2].length)]
						: POSSIBLE_TILE_CENTRE[0][Utils
								.random(POSSIBLE_TILE_CENTRE[0].length)];
		if (noMovement) {
			WorldTile noMovementCenter = Utils.getFreeTile(CENTER, 7);
			player.setNextWorldTile(noMovementCenter);
			target.setNextWorldTile(getTile(noMovementCenter));
		} else {
			target.setNextWorldTile(Utils.getFreeTile(CENTER, 7));
			player.setNextWorldTile(Utils.getFreeTile(CENTER, 7));
		}
	}

	private static WorldTile getTile(WorldTile center) {
		List<WorldTile> possibleTiles = new ArrayList<WorldTile>(4);
		possibleTiles.add(center.transform(-1, 0, 0));
		possibleTiles.add(center.transform(0, -1, 0));
		possibleTiles.add(center.transform(1, 0, 0));
		possibleTiles.add(center.transform(0, 1, 0));
		for (WorldTile tile : possibleTiles) {
			if (World.isTileFree(tile.getPlane(), tile.getX(), tile.getY(), 1))
				return tile;
		}
		return null;
	}

	private void setRules(DuelRules rules, int componentId, int slotId) {
		sendRuleFlash(slotId, componentId);
		rules.setRules(slotId);
	}

	private void end(int type) {
		if (isDueling) {
			boolean bothDead = player.isDead() && target.isDead();
			if (type == LOGOUT || type == DUEL_END_LOSE || type == TELEPORT)
				endDuel(target, player, bothDead || Engine.shutdown
						|| Engine.delayedShutdownStart != 0);
			else if (type == DUEL_END_WIN)
				endDuel(player, target, bothDead);
		} else
			closeDuelInteraction(DuelStage.DECLINED);
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int slotId2, int packetId) {
		synchronized (this) {
			if (target == null
					|| target.getControlerManager().getControler() == null
					|| !(target.getControlerManager().getControler() instanceof DuelArena)
					|| player.getControlerManager().getControler() == null
					|| !(player.getControlerManager().getControler() instanceof DuelArena))
				return false;
			synchronized (target.getControlerManager().getControler()) {
				DuelRules rules = player.getDuelRules();
				switch (interfaceId) {
				case 271:
				case 1367:
					if (rules.getRule(5)) {
						if (interfaceId == 1367 && componentId != 38)
							return true;
						player.getPackets().sendGameMessage(
								"You can't use prayers in this duel.");
						return false;
					}
					return true;
				case 193:
				case 430:
				case 192:
					if (rules.getRule(2) && isDueling)
						return false;
					return true;
				case 884:
					if (componentId == 4) {
						if (rules.getRule(9)) {
							player.getPackets()
									.sendGameMessage(
											"You can't use special attacks in this duel.");
							return false;
						}
					}
					return true;
				case 631:
					switch (componentId) {
					case 53:
						player.getPackets().sendInputIntegerScript(
								"Enter amount:");
						player.getTemporaryAttributtes().put(
								Key.DUEL_COIN_WITHDRAWL, this);
						return true;
					case 56: // no range
						setRules(rules, componentId, 0);
						return false;
					case 57: // no melee
						setRules(rules, componentId, 1);
						return false;
					case 58: // no magic
						setRules(rules, componentId, 2);
						return false;
					case 59: // fun wep
						if (rules.getRule(15)) {// no wep
							player.getPackets()
									.sendGameMessage(
											"You can't have no weapons and fun weapons enabled.");
							return false;
						}
						boolean canContinue = false;
						for (int weapon : FUN_WEAPONS) {
							if (player.getEquipment().getWeaponId() == weapon
									|| player.getInventory().containsItem(
											weapon, 1)) {
								canContinue = true;
								break;
							}
						}
						if (!canContinue) {
							player.getPackets()
									.sendGameMessage(
											"You and your opponent both need a fun weapon in order to have fun weapons enabled.");
							return false;
						}
						setRules(rules, componentId, 8);
						return false;
					case 60: // no forfiet
						setRules(rules, componentId, 7);
						return false;
					case 61: // no drinks
						setRules(rules, componentId, 3);
						return false;
					case 62: // no food
						setRules(rules, componentId, 4);
						return false;
					case 63: // no prayer
						setRules(rules, componentId, 5);
						return false;
					case 64: // no movement
						if (rules.getRule(6)) {
							player.getPackets()
									.sendGameMessage(
											"You can't have no movement and obstacles enabled.");
							return false;
						}
						setRules(rules, componentId, 25);
						return false;
					case 65: // obstacles
						if (rules.getRule(25)) {
							player.getPackets()
									.sendGameMessage(
											"You can't have obstacles without movement.");
							return false;
						}
						setRules(rules, componentId, 6);
						return false;
					case 66: // enable summoning
						setRules(rules, componentId, 24);
						return false;
					case 67:// no spec
						setRules(rules, componentId, 9);
						return false;
					case 21:// no helm
						setRules(rules, componentId, 10);
						return false;
					case 22:// no cape
						setRules(rules, componentId, 11);
						return false;
					case 23:// no ammy
						setRules(rules, componentId, 12);
						return false;
					case 31:// arrows
						setRules(rules, componentId, 23);
						return false;
					case 24:// weapon
						setRules(rules, componentId, 13);
						return false;
					case 25:// body
						setRules(rules, componentId, 14);
						return false;
					case 26:// shield
						setRules(rules, componentId, 15);
						return false;
					case 27:// legs
						setRules(rules, componentId, 17);
						return false;
					case 28:// ring
						setRules(rules, componentId, 19);
						return false;
					case 29: // bots
						setRules(rules, componentId, 20);
						return false;
					case 30: // gloves
						setRules(rules, componentId, 22);
						return false;
					case 107:
						closeDuelInteraction(DuelStage.DECLINED);
						return false;
					case 46:
						accept(true);
						return false;
					case 47:
						switch (packetId) {
						case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
							removeItem(slotId, 1);
							return false;
						case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
							removeItem(slotId, 5);
							return false;
						case WorldPacketsDecoder.ACTION_BUTTON3_PACKET:
							removeItem(slotId, 10);
							return false;
						case WorldPacketsDecoder.ACTION_BUTTON4_PACKET:
							Item item = player.getInventory().getItems()
									.get(slotId);
							if (item == null)
								return false;
							removeItem(slotId, player.getInventory().getItems()
									.getNumberOf(item));
							return false;
						case WorldPacketsDecoder.ACTION_BUTTON5_PACKET:
							player.getInventory().sendExamine(slotId);
							return false;
						}
						return false;
					}
				case 628:
					switch (packetId) {
					case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
						addItem(slotId, 1);
						return false;
					case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
						addItem(slotId, 5);
						return false;
					case WorldPacketsDecoder.ACTION_BUTTON3_PACKET:
						addItem(slotId, 10);
						return false;
					case WorldPacketsDecoder.ACTION_BUTTON4_PACKET:
						Item item = player.getInventory().getItems()
								.get(slotId);
						if (item == null)
							return false;
						addItem(slotId, player.getInventory().getItems()
								.getNumberOf(item));
						return false;
					case WorldPacketsDecoder.ACTION_BUTTON5_PACKET:
						player.getInventory().sendExamine(slotId);
						return false;
					}
				case 626:
					switch (componentId) {
					case 43:
						accept(false);
						return false;
					}
				case 637: // friendly
					switch (componentId) {
					case 31: // no range
						setRules(rules, componentId, 0);
						return false;
					case 32: // no melee
						setRules(rules, componentId, 1);
						return false;
					case 33: // no magic
						setRules(rules, componentId, 2);
						return false;
					case 34: // fun wep
						setRules(rules, componentId, 8);
						return false;
					case 35: // no forfiet
						setRules(rules, componentId, 7);
						return false;
					case 36: // no drinks
						setRules(rules, componentId, 3);
						return false;
					case 37: // no food
						setRules(rules, componentId, 4);
						return false;
					case 38: // no prayer
						setRules(rules, componentId, 5);
						return false;
					case 39: // no movement
						if (rules.getRule(6)) {
							player.getPackets()
									.sendGameMessage(
											"You can't have no movement and obstacles enabled.");
							return false;
						}
						setRules(rules, componentId, 25);
						return false;
					case 40: // obstacles
						if (rules.getRule(25)) {
							player.getPackets()
									.sendGameMessage(
											"You can't have obstacles without movement.");
							return false;
						}
						setRules(rules, componentId, 6);
						return false;
					case 41: // enable summoning
						setRules(rules, componentId, 24);
						return false;
					case 42:// no spec/abilities
						setRules(rules, componentId, 9);
						return false;

					case 18:// no helm
						setRules(rules, componentId, 10);
						return false;
					case 19:// no cape
						setRules(rules, componentId, 11);
						return false;
					case 20:// no ammy
						setRules(rules, componentId, 12);
						return false;
					case 28:// arrows
						setRules(rules, componentId, 23);
						return false;
					case 21:// weapon
						setRules(rules, componentId, 13);
						return false;
					case 22:// body
						setRules(rules, componentId, 14);
						return false;
					case 23:// shield
						setRules(rules, componentId, 15);
						return false;
					case 24:// legs
						setRules(rules, componentId, 17);
						return false;
					case 27:// ring
						setRules(rules, componentId, 19);
						return false;
					case 26: // bots
						setRules(rules, componentId, 20);
						return false;
					case 25: // gloves
						setRules(rules, componentId, 22);
						return false;
					case 86:
						closeDuelInteraction(DuelStage.DECLINED);
						return false;
					case 57:
						accept(true);
						return false;
					}
				case 639:
					switch (componentId) {
					case 25:
						accept(false);
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean isDueling() {
		return isDueling;
	}

	public boolean hasTarget() {
		return target != null;
	}

	public boolean isConfiguring() {
		return !isDueling;
	}

	public Entity getTarget() {
		if (hasTarget())
			return target;
		return null;
	}

	enum DuelStage {
		DECLINED, NO_SPACE, SECOND, DONE
	}
}