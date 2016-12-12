package net.kagani.game.player.content;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.kagani.game.ForceTalk;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public class TicketSystem {

	private static final List<TicketEntry> openTickets = new LinkedList<TicketEntry>();
	private static int TICKET_COUNT;

	public static void addTicket(Player player, TicketEntry ticket) {
		openTickets.add(ticket);
		for (Player mod : World.getPlayers()) {
			if (mod == null || mod.hasFinished() || !mod.hasStarted()
					|| (mod.getRights() < 1))
				continue;
			mod.getPackets().sendGameMessage("A ticket has been submitted.");
		}
	}

	public static void openNextTicket(final Player player) {
		closeCurrentTicket(player);
		filterTicket();
		if (openTickets.isEmpty()) {
			player.getPackets().sendGameMessage(
					"There are no more available tickets.");
			return;
		}
		final TicketEntry ticket = openTickets.get(0);
		final Player target = ticket.getPlayer();
		if (target.getInterfaceManager().containsDialogueInterface()
				|| target.getControlerManager().getControler() != null
				|| target.getInterfaceManager().containsInventoryInter()
				|| target.getInterfaceManager().containsScreenInterface()) {
			openTickets.remove(0);
			target.getPackets().sendGameMessage(
					"Your ticket has been filtered.");
			return;
		}
		ticket.tile = new WorldTile(target);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextForceTalk(new ForceTalk(
						"Hello there, I'm prepared to answer your questions."));
				player.getTemporaryAttributtes().put("selected_ticket", ticket);
				target.useStairs(-1, new WorldTile(player, 1), 1, 2);
			}
		});
		openTickets.remove(0);
		player.getPackets().sendGameMessage(
				"There is " + openTickets.size()
						+ " tickets left, this is ticket # " + TICKET_COUNT++
						+ ".");
		player.getPackets().sendGameMessage(
				"The ticket was issued for the following reason: "
						+ ticket.getReason() + ".");
	}

	public static void closeCurrentTicket(Player player) {
		filterTicket();
		TicketEntry ticket = (TicketEntry) player.getTemporaryAttributtes()
				.get("selected_ticket");
		if (ticket == null)
			return;
		player.setNextForceTalk(new ForceTalk(
				"Goodbye, don't hesitate to open another ticket at any time."));
		Player target = ticket.getPlayer();
		// System.out.println("X "+ticket.getTile().getX()+ " Y
		// "+ticket.getTile().getY());
		target.useStairs(-1, ticket.tile, 1, 2);
		player.getTemporaryAttributtes().remove("selected_ticket");
	}

	private static void filterTicket() {
		for (Iterator<TicketEntry> it = openTickets.iterator(); it.hasNext();) {
			TicketEntry entry = it.next();
			Player player = entry.player;
			if (player.hasFinished()
					|| player.getControlerManager().getControler() != null) {
				player.getPackets().sendGameMessage(
						"Your ticket has been filtered.");
				it.remove();
			}
		}
	}

	public static class TicketEntry {
		private Player player;
		private String reason;
		private WorldTile tile;

		public TicketEntry(Player player, String reason) {
			this.player = player;
			this.reason = reason;
		}

		public Player getPlayer() {
			return player;
		}

		public WorldTile getTile() {
			return tile;
		}

		public String getReason() {
			return reason;
		}
	}
}