package net.kagani.game.minigames;

import net.kagani.game.WorldObject;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.construction.HouseConstants;

public class Sawmill {

	private static final int SAWMILL_OPERATOR = 4250;
	public static final int OVERSEER = 8904;

	public static enum Plank {
		WOOD(HouseConstants.PLANK, 1511, 100), OAK(HouseConstants.OAK_PLANK,
				1521, 250), TEAK(HouseConstants.TEAK_PLANK, 6333, 500), MAHOGANY(
				HouseConstants.MAHOGANY_PLANK, 6332, 1500);
		private int id, logId, cost;

		private Plank(int id, int logId, int cost) {
			this.id = id;
			this.logId = logId;
			this.cost = cost;
		}

		public int getCost() {
			return cost;
		}

		public int getId() {
			return id;
		}
	}

	public static Plank getPlankForLog(int id) {
		for (Plank plank : Plank.values())
			if (plank.logId == id)
				return plank;
		return null;
	}

	public static Plank getPlank(int id) {
		for (Plank plank : Plank.values())
			if (plank.id == id)
				return plank;
		return null;
	}

	public static boolean hasPlanksOrLogs(Player player) {
		for (Item item : player.getInventory().getItems().getItems()) {
			if (item != null
					&& (getPlankForLog(item.getId()) != null || getPlank(item
							.getId()) != null))
				return true;
		}
		return false;
	}

	public static void openPlanksConverter(Player player) {
		player.getInterfaceManager().sendCentralInterface(403);
	}

	public static void handlePlanksConvertButtons(Player player,
			int componentId, int packetId) {
		/**
		 * Wood
		 */
		if (componentId == 41)
			convertPlanks(player, Plank.WOOD, 1);
		if (componentId == 47)
			convertPlanks(player, Plank.WOOD, 5);
		if (componentId == 53)
			convertPlanks(player, Plank.WOOD, 10);
		if (componentId == 59) {
			player.getTemporaryAttributtes().put("PlanksConvert", Plank.WOOD);
			player.getPackets().sendInputIntegerScript("Enter amount:");
			return;
		}
		if (componentId == 65)
			convertPlanks(player, Plank.WOOD, player.getInventory()
					.getAmountOf(Plank.WOOD.logId));
		/**
		 * Oak
		 */
		if (componentId == 103)
			convertPlanks(player, Plank.OAK, 1);
		if (componentId == 95)
			convertPlanks(player, Plank.OAK, 5);
		if (componentId == 87)
			convertPlanks(player, Plank.OAK, 10);
		if (componentId == 79) {
			player.getTemporaryAttributtes().put("PlanksConvert", Plank.OAK);
			player.getPackets().sendInputIntegerScript("Enter amount:");
			return;
		}
		if (componentId == 71)
			convertPlanks(player, Plank.values()[1], player.getInventory()
					.getAmountOf(Plank.OAK.logId));
		/**
		 * Teak
		 */
		if (componentId == 141)
			convertPlanks(player, Plank.TEAK, 1);
		if (componentId == 133)
			convertPlanks(player, Plank.TEAK, 5);
		if (componentId == 125)
			convertPlanks(player, Plank.TEAK, 10);
		if (componentId == 117) {
			player.getTemporaryAttributtes().put("PlanksConvert", Plank.TEAK);
			player.getPackets().sendInputIntegerScript("Enter amount:");
			return;
		}
		if (componentId == 109)
			convertPlanks(player, Plank.TEAK, player.getInventory()
					.getAmountOf(Plank.TEAK.logId));
		/**
		 * Mahogany
		 */
		if (componentId == 179)
			convertPlanks(player, Plank.MAHOGANY, 1);
		if (componentId == 171)
			convertPlanks(player, Plank.MAHOGANY, 5);
		if (componentId == 163)
			convertPlanks(player, Plank.MAHOGANY, 10);
		if (componentId == 155) {
			player.getTemporaryAttributtes().put("PlanksConvert",
					Plank.MAHOGANY);
			player.getPackets().sendInputIntegerScript("Enter amount:");
			return;
		}
		if (componentId == 147)
			convertPlanks(player, Plank.MAHOGANY, player.getInventory()
					.getAmountOf(Plank.MAHOGANY.logId));

	}

	public static void convertPlanks(Player player, Plank type, int amount) {
		int warning = 0;
		int logsAmt = player.getInventory().getAmountOf(type.logId);
		if (amount > logsAmt) {
			amount = logsAmt;
			warning = 1;
		}
		int cost = amount * type.cost;
		int invCoins = player.getInventory().getCoinsAmount();
		if (cost > invCoins) {
			amount = invCoins / type.cost;
			cost = amount * type.cost;
			warning = 2;
		}
		if (warning != 0)
			player.getPackets().sendGameMessage(
					"You've run out of " + (warning == 1 ? "logs" : "coins")
							+ ".");
		if (amount > 0) {
			player.getInventory().removeItemMoneyPouch(new Item(995, cost));
			player.getInventory().deleteItem(type.logId, amount);
			player.getInventory().addItem(type.id, amount);
		}
		player.closeInterfaces();
		player.getDialogueManager().startDialogue("SimpleNPCMessage",
				SAWMILL_OPERATOR, "Ive done as many as I could.");
	}

	public static void enter(Player player, WorldObject object) {
		if (player.getSkills().getLevelForXp(Skills.WOODCUTTING) < 80) {
			player.getDialogueManager()
					.startDialogue("SimpleNPCMessage", OVERSEER,
							"Sorry, we don't need inexperienced woodcutters.");
			return;
		}
		if (hasPlanksOrLogs(player)) {
			player.getDialogueManager()
					.startDialogue(
							"SimpleNPCMessage",
							OVERSEER,
							"Sorry, you can't bring any planks or logs in with you. You might get them muddled with ours.");
			return;
		}
		player.lock(2);
		player.addWalkSteps(object.getX() + 1, object.getY(), 1, false);
		player.getControlerManager().startControler("SawmillController");
	}

}
