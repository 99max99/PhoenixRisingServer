package net.kagani.game.player.content;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.kagani.Settings;
import net.kagani.executor.GameExecutorManager;
import net.kagani.game.ForceTalk;
import net.kagani.game.World;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

public class Lottery {

	/**
	 * @author: Dylan Page
	 */

	public final static Lottery INSTANCE = new Lottery();

	public final static ArrayList<Player> TICKETS = new ArrayList<Player>();

	public final static ArrayList<String> USERNAMES = new ArrayList<String>();

	public final static boolean active = false;

	/**
	 * Establishes this {@link Lottery}.
	 */
	public void establish() {
		GameExecutorManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {

			/**
			 * The amount of minutes that uses the {@link AtomicInteger}.
			 */
			private final AtomicInteger MINUTES = new AtomicInteger(10);

			@Override
			public void run() {
				switch (MINUTES.getAndDecrement()) {
				case 10:
				case 5:
					if (TICKETS.size() > 12)
						message("It is less than " + (MINUTES.get() + 1)
								+ " minutes till jackpot is being given out.");
					break;
				case 0:
					if (TICKETS.size() > 12) {
						giveLotteryPrice();
					}
					MINUTES.set(10);
					break;
				}
			}
		}, 0, 1, Settings.DEBUG ? TimeUnit.SECONDS : TimeUnit.MINUTES);
	}

	/**
	 * Stop the lottery and gives out the price.
	 */
	public void giveLotteryPrice() {
		final ArrayList<Player> POSSIBLE_WINNERS = new ArrayList<Player>(
				TICKETS.size());
		for (final Player e : TICKETS) {
			if (e != null && e.getPrize() == null)
				POSSIBLE_WINNERS.add(e);
		}
		if (POSSIBLE_WINNERS.size() > 0) {
			Player winner = POSSIBLE_WINNERS.get(Utils.random(POSSIBLE_WINNERS
					.size()));
			final Item prize = getPrize();
			message(winner.getDisplayName()
					+ " has won the lottery with a price of "
					+ Utils.format(prize.getAmount()) + " "
					+ prize.getDefinitions().name + "!");
			Player copy = World.getPlayer(winner.getUsername());
			if (copy != null) {
				copy.setPrize(prize);
				copy.getPackets()
						.sendGameMessage(
								"<col=ff0000>You won the lottery! Speak to Gambler to claim your prize.");
			} else {
				winner.setPrize(prize);
			}
		}
		TICKETS.clear();
		USERNAMES.clear();
	}

	/**
	 * Add a player to this {@link Lottery}.
	 * 
	 * @param player
	 *            The player.
	 * @param npc
	 *            The npc.
	 */
	public void addPlayer(Player player, NPC npc, int amount) {
		if (canEnter(player)
				&& player.getInventory().containsItem(TICKET_PRICE().getId(),
						TICKET_PRICE().getAmount())) {
			player.getInventory().deleteItem(TICKET_PRICE().getId(),
					TICKET_PRICE().getAmount());
			TICKETS.add(player);
			player.getPackets().sendGameMessage(
					"You have bought a lottery ticket.", true);
			USERNAMES
					.add(Utils.formatPlayerNameForDisplay(player.getUsername()));
			final Item prize = getPrize();
			checkForMessage(prize);
			String msg = "";
			switch (Utils.random(4)) {
			case 0:
				msg = "Good luck, " + player.getDisplayName() + "!";
				break;
			case 1:
				msg = player.getDisplayName()
						+ ", I wish you the best of luck!";
				break;
			case 2:
				msg = player.getDisplayName()
						+ ", I have a feeling that you are going to win!";
				break;
			case 3:
				msg = "Best of luck, " + player.getDisplayName() + "!";
				break;
			}
			if (npc != null)
				npc.setNextForceTalk(new ForceTalk(msg));
			return;
		} else if (!player.getInventory().containsItem(TICKET_PRICE().getId(),
				TICKET_PRICE().getAmount())) {
			if (canEnter(player)
					&& player.getMoneyPouch().contains(
							TICKET_PRICE().getAmount())) {
				player.getMoneyPouch().setAmount(TICKET_PRICE().getAmount(),
						true);
				TICKETS.add(player);
				player.getPackets().sendGameMessage(
						"You have bought a lottery ticket.", true);
				USERNAMES.add(Utils.formatPlayerNameForDisplay(player
						.getUsername()));
				final Item prize = getPrize();
				checkForMessage(prize);
				String msg = "";
				switch (Utils.random(4)) {
				case 0:
					msg = "Good luck, " + player.getDisplayName() + "!";
					break;
				case 1:
					msg = player.getDisplayName()
							+ ", I wish you the best of luck!";
					break;
				case 2:
					msg = player.getDisplayName()
							+ ", I have a feeling that you are going to win!";
					break;
				case 3:
					msg = "Best of luck, " + player.getDisplayName() + "!";
					break;
				}
				if (npc != null)
					npc.setNextForceTalk(new ForceTalk(msg));
				return;
			}
			player.getPackets().sendGameMessage(
					"You do not have enough coins.", true);
		}
	}

	private void checkForMessage(final Item item) {
		if (TICKETS.size() > 24)
			message("The jackpot is now at " + Utils.format(item.getAmount())
					+ "!");
	}

	/**
	 * Checks if the player can enter this {@link Lottery}.
	 * 
	 * @param player
	 *            The player to enter.
	 * @return {@code true} If entering is possible.
	 */
	private boolean canEnter(Player player) {
		int amountOfTickets = 0;
		for (final String e : USERNAMES) {
			if (e != null
					&& e.equals(Utils.formatPlayerNameForDisplay(player
							.getUsername()))
					&& ++amountOfTickets == MAX_TICKET_EACH_PLAYER(player)) {
				player.getPackets().sendGameMessage(
						"You can only have a maximum of "
								+ MAX_TICKET_EACH_PLAYER(player) + " tickets!",
						true);
				return false;
			}
		}
		if (player.getSkills().getTotalLevel() < 200) {
			player.getPackets()
					.sendGameMessage(
							"You need a total level of 200 to participate in Gambling.",
							true);
			return false;
		}
		if (player.isBeginningAccount()) {
			player.getPackets()
					.sendGameMessage(
							"You need to have played for more than an hour to participate in Gambling.",
							true);
			return false;
		}
		return true;
	}

	/**
	 * Get the formatted number of amount.
	 * 
	 * @param amount
	 *            The amount.
	 * @return The formatted number of amount.
	 */
	public final String getFormattedNumber(int amount) {
		return Utils.format(amount);
	}

	/**
	 * Sends a message to all the players in the gameserver.
	 * 
	 * @param message
	 *            The message to sent.
	 */
	private void message(final String message) {
		World.sendNews(message, 4);
	}

	/**
	 * Get the final prize.
	 */
	public final Item getPrize() {
		return new Item(
				TICKET_PRICE().getId(),
				(int) Math.floor((TICKET_PRICE().getAmount() * TICKETS.size()) / 2.5D));
	}

	/**
	 * Cancel this {@link Lottery}.
	 */
	public void cancelLottery() {
		for (final Player e : TICKETS) {
			if (e == null)
				return;
			if (!e.hasFinished())
				e.getPackets()
						.sendGameMessage(
								"<col=FF0000>"
										+ Settings.SERVER_NAME
										+ " is about to restart, you can reclaim your tickets by talking to Gambler.");
			e.setPrize(TICKET_PRICE());
		}
		TICKETS.clear();
	}

	public static Item TICKET_PRICE() {
		if (TICKETS.size() > 24)
			return new Item(995, TICKETS.size() * 2 * 100 * 100);
		return new Item(995, TICKETS.size() <= 2 ? 2 * 100 * 100
				: TICKETS.size() * 100 * 100);
	}

	public static int MAX_TICKET_EACH_PLAYER(Player player) {
		if (player.isBronzeMember())
			return 10;
		else if (player.isSilverMember())
			return 12;
		else if (player.isGoldMember())
			return 14;
		else if (player.isPlatinumMember())
			return 16;
		else if (player.isDiamondMember())
			return 18;
		return 8;
	}
}