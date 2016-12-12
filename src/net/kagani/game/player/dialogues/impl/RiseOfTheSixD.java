package net.kagani.game.player.dialogues.impl;

import net.kagani.Settings;
import net.kagani.game.Animation;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class RiseOfTheSixD extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	public RiseOfTheSixD() {

	}

	@Override
	public void start() {
		stage = 1;
		player.setNextAnimation(new Animation(21924));
		sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Start.", "Join.", "Rejoin.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stop();
			end();
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				stage = 2;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Solo.", "Party.");
				break;
			case OPTION_2:
			case OPTION_3:
				player.getPackets()
						.sendInputLongTextScript("Please enter the name of the player whose party you wish to join:");
				player.getTemporaryAttributtes().put("risesix", Boolean.TRUE);
				end();
				break;
			}
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				if (player.getInventory().containsItem(30004, 1)) {
					player.clearROSPartyMembers();
					player.addROSPartyMember(player);
					player.getControlerManager().startControler("RiseOfTheSix", true, player);
					end();
				} else {
					stage = -1;
					sendDialogue("You need a Barrows totem to enter.");
				}
				break;
			case OPTION_2:
				if (player.getInventory().containsItem(30004, 1)) {
					player.clearROSPartyMembers();
					player.addROSPartyMember(player);
					if (player.getROSPartyMembers().isEmpty() || player.getROSPartyMembers() == null)
						player.getPackets().sendGameMessage("You must create a party first.");
					else if (!player.withinDistance(new WorldTile(2326, 5910, 0), 50))
						player.getPackets()
								.sendGameMessage("You must be in the Shadow Realm in order to start the game.");
					else {
						for (Player member : player.getROSPartyMembers()) {
							if (!member.withinDistance(new WorldTile(2326, 5910, 0), 50)) {
								member.getPackets()
										.sendGameMessage("You must be in the Shadow Realm in order to start the game.");
								player.getPackets().sendGameMessage(member.getDisplayName()
										+ " must be in the Shadow Realm in order to start the game.");
								end();
							}
						}
						player.getControlerManager().startControler("RiseOfTheSix", true, player);
						WorldTasksManager.schedule(new WorldTask() {
							int stage;

							@Override
							public void run() {
								if (stage == 3) {
									for (Player member : player.getROSPartyMembers())
										member.getControlerManager().startControler("RiseOfTheSix", false, player);
									this.stop();
									stop();
								}
								stage++;
							}

						}, 0, 1);
					}
					end();
				} else {
					stage = -1;
					sendDialogue("You need a Barrows totem to enter.");
				}
				break;
			}
			break;
		default:
			stop();
			end();
			break;
		}
	}

	public void stop() {
		player.setNextAnimation(new Animation(21922));
	}

	public static void joinParty(Player player, String username) {
		Player host = World.getPlayerByDisplayName(username);
		if (player.getUsername().equalsIgnoreCase(username))
			player.getPackets().sendGameMessage("You can't join your own party.");
		else if (!World.containsPlayer(username) || host == null)
			player.getPackets().sendGameMessage("That player is offline.");
		else if (host.isLocked())
			player.getPackets().sendGameMessage("The player you have entered is busy.");
		else if (host.getROSPartyMembers().size() >= 4)
			player.getPackets().sendGameMessage("The player you have entered already has a full party of 4.");
		else {
			host.addROSPartyMember(player);
			player.getPackets().sendGameMessage("You have joined " + Utils.fixChatMessage(username) + "'s party.");
			host.getPackets().sendGameMessage(player.getDisplayName() + " has joined your party.");
		}
		player.setNextAnimation(new Animation(21922));
	}

	@Override
	public void finish() {

	}
}