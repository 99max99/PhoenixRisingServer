package net.kagani.game.minigames.fistofguthix;

import net.kagani.game.Animation;
import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.controllers.FistOfGuthixControler;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class Participant {

	private Player hunter;
	private Player hunted;
	private boolean switched = false;
	private long gameTicks;
	private long lastEnteredHouse = 0;

	public long gameTicks() {
		return gameTicks;
	}

	public void gameTicks(long ticks) {
		this.gameTicks = ticks;
	}

	public Player hunter() {
		return this.hunter;
	}

	public Player hunted() {
		return this.hunted;
	}

	public boolean switched() {
		return this.switched;
	}

	public long lastEnteredHouse() {
		return lastEnteredHouse;
	}

	public void lastEnteredHouse(long ticks) {
		this.lastEnteredHouse = ticks;
	}

	public Participant(Player hunter, Player hunted) {
		this.hunter = hunter;
		this.hunted = hunted;
		this.gameTicks = 0;
	}

	public void process() {
		gameTicks++;
		if (gameTicks() % 4 == 0) {
			increaseCharges();
			if (MinigameManager.INSTANCE().fistOfGuthix().insideBarrier(hunted)) {
				hunted.applyHit(new Hit(hunted, hunted.getSkills().getLevel(
						Skills.HITPOINTS), HitLook.REGULAR_DAMAGE, 0));
			}
		}

		if ((FistOfGuthixControler) hunter().getControlerManager()
				.getControler() == null)
			hunter().getControlerManager().startControler(
					"FistOfGuthixControler");

		if ((FistOfGuthixControler) hunted().getControlerManager()
				.getControler() == null)
			hunted().getControlerManager().startControler(
					"FistOfGuthixControler");

		if (hunted().getInterfaceManager().containsInterface(731))
			hunted().getPackets().closeInterface(
					hunted().getInterfaceManager().hasRezizableScreen() ? 11
							: 10);
		if (hunter().getInterfaceManager().containsInterface(731))
			hunter().getPackets().closeInterface(
					hunter().getInterfaceManager().hasRezizableScreen() ? 11
							: 10);

		if (!hunted().getInterfaceManager().containsInterface(730))
			hunted().getInterfaceManager().sendMinigameTab(
					hunted().getInterfaceManager().hasRezizableScreen() ? 11
							: 10);

		if (!hunter().getInterfaceManager().containsInterface(730))
			hunter().getInterfaceManager().sendMinigameTab(
					hunter().getInterfaceManager().hasRezizableScreen() ? 11
							: 10);

		hunted().getPackets().sendIComponentText(730, 17,
				"Charge: " + hunted().fogCharge());
		hunter().getPackets().sendIComponentText(730, 17,
				"Charge: " + hunter().fogCharge());

		hunter().getPackets().sendIComponentText(730, 26, "Hunting:");
		hunter().getPackets().sendIComponentText(730, 18,
				hunted().getDisplayName());
		hunter().getPackets().sendIComponentText(730, 7, "");

		hunted().getPackets().sendIComponentText(730, 26, "Hunted by:");
		hunted().getPackets().sendIComponentText(730, 18,
				hunter().getDisplayName());

		if (hunted().getInventory().containsItem(12845, 1)
				|| hunted().getEquipment().getWeaponId() == 12845)
			hunted().getPackets().sendIComponentText(730, 7, "");
		else
			hunted().getPackets().sendIComponentText(730, 7,
					"<col=ff0000>Please pick up a stone!");

		hunter().getPackets().sendIComponentText(730, 7, "");

		hunter().getHintIconsManager().addHintIcon(hunted(), 0, -1, false);
		hunted().getHintIconsManager().addHintIcon(hunter(), 0, -1, false);

		hunted().getPackets().sendConfig(1215, (int) ((int) gameTicks()));
		hunter().getPackets().sendConfig(1215, (int) ((int) gameTicks()));

		if (gameTicks == 1000) {
			switchSides();
			hunted().getPackets().sendGameMessage(
					"You ran out of time"
							+ (switched ? ", switching sides."
									: ", ending game."));
			hunter().getPackets().sendGameMessage(
					"You ran out of time"
							+ (switched ? ", switching sides."
									: ", ending game."));
		}

	}

	public void increaseCharges() {
		if (hunted().getEquipment().getWeaponId() == 12845
				&& !MinigameManager.INSTANCE().fistOfGuthix()
						.insideBarrier(hunted))
			hunted().fogCharge(
					hunted().fogCharge()
							+ MinigameManager.INSTANCE().fistOfGuthix()
									.points(hunted()));
	}

	public void handleItems(final Player player, Item item) {
		switch (item.getId()) {
		case 12855:
			player.getInventory().deleteItem(item.getId(), 12855);
			player.setNextAnimation(new Animation(9012));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextAnimation(new Animation(9013));
					player.setNextWorldTile(FistOfGuthix.CENTRE);
					stop();
				}
			}, 1);
		case 12853:
			player.getInventory().deleteItem(item);
			player.heal(150);
			break;
		}
	}

	public void processDeath(Player player) {
		if (player == hunter) {
			int loc = Utils.random(FistOfGuthix.hunterLocations.length);
			hunter.setNextWorldTile(FistOfGuthix.hunterLocations[loc]);
		} else {
			if (!switched) {
				switched = true;
				int loc = Utils.random(FistOfGuthix.hunterLocations.length);
				hunter().setNextWorldTile(FistOfGuthix.hunterLocations[loc]);
				hunted().setNextWorldTile(FistOfGuthix.huntedLocations[loc]);
				switchSides();
			} else {
				endGame();
			}
		}
	}

	public void forfeit(Player player) {
		Player winner = (player == hunter ? hunted : hunter);
		Player loser = (hunted == winner ? hunter : hunted);
		winner.setNextWorldTile(new WorldTile(1698, 5600, 0));
		((FistOfGuthixControler) winner.getControlerManager().getControler())
				.exit();
		winner.getPackets()
				.sendGameMessage(
						"Your parter has left the Fist of Guthix, therefore the victory is yours by default.");
		int tokens = (int) Math.floor(winner.fogCharge() / 100);
		winner.getPackets().sendGameMessage(
				"<col=0a8451>Congratulations, you won!</col> You had "
						+ winner.fogCharge()
						+ " charges and your opponent had " + loser.fogCharge()
						+ ".");
		winner.getPackets().sendGameMessage(
				"You have gained 10 rating and " + tokens + " tokens.");
		winner.getInventory().addItem(29979, tokens);
		winner.fogRating(winner.fogRating() + 10);
		winner.setCanPvp(false);
		loser.setCanPvp(false);
		winner.getHintIconsManager().removeAll();
		winner.getHintIconsManager().removeUnsavedHintIcon();
		loser.getPackets().sendGameMessage(
				"<col=ff0000>You quit.</col> You had " + loser.fogCharge()
						+ " and your opponent had " + winner.fogCharge() + ".");
		loser.getPackets()
				.sendGameMessage(
						"You have lost "
								+ (loser.fogRating() >= 4 ? 4 : loser
										.fogRating())
								+ " ratings and have gained 1 Fist of Guthix token for good effort.");
		loser.fogRating(loser.fogRating()
				- (loser.fogRating() >= 4 ? 4 : loser.fogRating()));
		loser.getInventory().addItem(29979, 1);
		loser.getHintIconsManager().removeAll();
		loser.getHintIconsManager().removeUnsavedHintIcon();
		winner.setNextAnimation(new Animation(8996));
		MinigameManager.INSTANCE().fistOfGuthix().gameMembers().remove(this);
		winner.getControlerManager().removeControlerWithoutCheck();
		loser.getControlerManager().removeControlerWithoutCheck();
		loser.fogCharge(0);
		winner.fogCharge(0);
	}

	public void switchSides() {
		Player p = this.hunter;
		this.hunter = this.hunted;
		this.hunted = p;
		if (((FistOfGuthixControler) this.hunted.getControlerManager()
				.getControler()) != null) {
			((FistOfGuthixControler) this.hunted.getControlerManager()
					.getControler()).exit();
			((FistOfGuthixControler) this.hunted.getControlerManager()
					.getControler()).start();
		}
		if (((FistOfGuthixControler) this.hunter.getControlerManager()
				.getControler()) != null) {
			((FistOfGuthixControler) this.hunter.getControlerManager()
					.getControler()).exit();
			((FistOfGuthixControler) this.hunter.getControlerManager()
					.getControler()).start();
		}
		this.gameTicks = 0;
	}

	public void endGame() {
		hunter().setNextWorldTile(new WorldTile(1698, 5600, 0));
		hunted().setNextWorldTile(new WorldTile(1698, 5600, 0));
		if ((FistOfGuthixControler) hunted().getControlerManager()
				.getControler() != null)
			((FistOfGuthixControler) hunted().getControlerManager()
					.getControler()).exit();
		if ((FistOfGuthixControler) hunter().getControlerManager()
				.getControler() != null)
			((FistOfGuthixControler) hunter().getControlerManager()
					.getControler()).exit();
		hunter().getControlerManager().removeControlerWithoutCheck();
		hunted().getControlerManager().removeControlerWithoutCheck();
		Player winner = (hunted().fogCharge() > hunter().fogCharge() ? hunted
				: hunter);
		Player loser = (hunter() == winner ? hunted : hunter);
		int tokens = (int) Math.floor(winner.fogCharge() / 100);
		winner.getPackets().sendGameMessage(
				"<col=0a8451>Congratulations, you won!</col> You had "
						+ winner.fogCharge()
						+ " charges and your opponent had " + loser.fogCharge()
						+ ".");
		winner.getPackets().sendGameMessage(
				"You have gained 10 rating and " + tokens + " tokens.");
		winner.getInventory().addItem(29979, tokens);
		winner.fogRating(winner.fogRating() + 10);
		winner.fogWins++;// only applied in legitimate games
		winner.setCanPvp(false);
		loser.setCanPvp(false);
		winner.setNextAnimation(new Animation(8996));
		winner.getHintIconsManager().removeAll();
		winner.getHintIconsManager().removeUnsavedHintIcon();
		loser.getPackets().sendGameMessage(
				"<col=ff0000>You lost.</col> You had " + loser.fogCharge()
						+ " and your opponent had " + winner.fogCharge() + ".");
		loser.getPackets()
				.sendGameMessage(
						"You have lost "
								+ (loser.fogRating() >= 4 ? 4 : loser
										.fogRating())
								+ " ratings and have gained 1 Fist of Guthix token for good effort.");
		loser.fogRating(loser.fogRating()
				- (loser.fogRating() >= 4 ? 4 : loser.fogRating()));
		loser.getInventory().addItem(29979, 1);
		loser.getHintIconsManager().removeAll();
		loser.fogCharge(0);
		winner.fogCharge(0);
		winner.getControlerManager().removeControlerWithoutCheck();
		loser.getControlerManager().removeControlerWithoutCheck();
		loser.getHintIconsManager().removeUnsavedHintIcon();
		MinigameManager.INSTANCE().fistOfGuthix().gameMembers().remove(this);
	}

	public void shutdownServer() {
		hunted.setNextWorldTile(new WorldTile(1698, 5600, 0));
		hunted.fogCharge(0);
		hunted.getInventory().deleteItem(12850, 1000);
		hunted.getInventory().deleteItem(12851, 300);
		hunted.getInventory().deleteItem(12853, 1);
		hunted.getInventory().deleteItem(12853, 1);
		hunted.getInventory().deleteItem(12853, 1);
		hunted.getInventory().deleteItem(12853, 1);
		hunted.getInventory().deleteItem(12853, 1);
		hunted.getInventory().deleteItem(12855, 1);
		hunted.getInventory().deleteItem(12845, 1);
		hunted.getEquipment().deleteItem(12845, 1);
		hunter.setNextWorldTile(new WorldTile(1698, 5600, 0));
		hunter.fogCharge(0);
		hunter.getInventory().deleteItem(12850, 1000);
		hunter.getInventory().deleteItem(12851, 300);
		hunter.getInventory().deleteItem(12853, 1);
		hunter.getInventory().deleteItem(12853, 1);
		hunter.getInventory().deleteItem(12853, 1);
		hunter.getInventory().deleteItem(12853, 1);
		hunter.getInventory().deleteItem(12853, 1);
		hunter.getInventory().deleteItem(12855, 1);
		hunter.getInventory().deleteItem(12845, 1);
		hunter.getEquipment().deleteItem(12845, 1);
		MinigameManager.INSTANCE().fistOfGuthix().gameMembers().remove(this);
	}
}