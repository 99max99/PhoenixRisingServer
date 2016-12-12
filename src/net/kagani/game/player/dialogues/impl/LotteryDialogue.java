package net.kagani.game.player.dialogues.impl;

import net.kagani.Engine;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.content.Lottery;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;

public class LotteryDialogue extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	private NPC npc;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 0:
			stage = 1;
			sendOptionsDialogue("Enter lottery?", "Yes, enter the lottery.",
					"Who is participating?", "How does it work?",
					"No thank you.");
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				/*
				 * player.getPackets().sendInputLongTextScript(
				 * "Please amount of tickets to purchase (max: 10)");
				 * player.getTemporaryAttributtes().put("lottery",
				 * Boolean.TRUE);
				 */
				if (Engine.shutdown) {
					stage = -1;
					sendNPCDialogue(npcId, NORMAL, "Wait till the restart.");
					return;
				}
				Lottery.INSTANCE.addPlayer(player, npc, 1);
				end();
				break;
			case OPTION_2:
				stage = -1;
				sendNPCDialogue(
						npc.getId(),
						9827,
						Lottery.USERNAMES != null ? Lottery.USERNAMES
								+ ", and <col=35d43f>"
								+ Utils.format(Lottery.TICKETS.size())
								+ "</col> tickets bought in total."
								: "There are no one participating in the lottery.");
				break;
			case OPTION_3:
				stage = 2;
				sendNPCDialogue(
						npc.getId(),
						9827,
						"You purchase a ticket for an amount, for each ticket bought the price of a ticket increases. Once 13 tickets has been bought it will be given out within 10 minutes. Players that want to increase the jackpot now have the opportunity to do so.");
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		case 2:
			stage = -1;
			sendNPCDialogue(
					npc.getId(),
					9827,
					"Normal players can buy up to 8 tickets, bronze members; 10, silver members; 12, gold members; 14, platinum members; 16 and diamond members; 18.");
			break;
		/*
		 * case 3: stage = 4; sendPlayerChat(SCARED, "To gamble?"); break; case
		 * 4: stage = -1; sendNPCDialogue(npc.getId(), VERY_ANGRY, "Piss off.");
		 * break;
		 */
		}
	}

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		if (player.isAnIronMan()) {
			sendNPCDialogue(npc.getId(), 9827,
					"You are an " + player.getIronmanTitle(true)
							+ ", you stand alone.");
			return;
		}
		if (player.getPrize() != null) {
			if (player.getInventory().getFreeSlots() < 1) {
				stage = -1;
				player.getPackets().sendGameMessage(
						"Not enough space in inventory to claim your price.");
				sendNPCDialogue(npc.getId(), 9827,
						"You don't have enough space in your inventory!");
				return;
			}
			stage = -1;
			sendNPCDialogue(npc.getId(), HAPPY, "Here is your reward of "
					+ Utils.format(player.getPrize().getAmount()) + " coins!");
			player.getInventory().addItem(player.getPrize());
			player.setPrize(null);
			return;
		}
		stage = 0;
		sendNPCDialogue(
				npc.getId(),
				9827,
				"Hello, "
						+ player.getDisplayName()
						+ ", would like to enter the lottery? A ticket cost <col=35d43f>"
						+ Utils.format(Lottery.TICKET_PRICE().getAmount())
						+ " " + Lottery.TICKET_PRICE().getDefinitions().name
						+ "</col>. Pot is on <col=35d43f>"
						+ Utils.format(Lottery.INSTANCE.getPrize().getAmount())
						+ " " + Lottery.TICKET_PRICE().getDefinitions().name
						+ "</col>. There are <col=35d43f>"
						+ Utils.format(Lottery.TICKETS.size())
						+ "</col> tickets bought in total.");
	}
}