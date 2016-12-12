package net.kagani.game.player.dialogues.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.kagani.game.World;
import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.network.LoginClientChannelManager;
import net.kagani.network.LoginProtocol;
import net.kagani.network.encoders.LoginChannelsPacketEncoder;
import net.kagani.utils.Utils;

public class AddOffenceD extends Dialogue {

	private String target;
	private int type;
	private long time;

	@Override
	public void start() {
		target = (String) parameters[0];

		stage = 0;
		type = -1;
		sendOptionsDialogue("Select punishment for " + target, "Ip Ban",
				"Ip Mute", "Ban", "Mute");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 0) {
			if (componentId == OPTION_1) {
				if (player.getRights() < 2) {
					stage = -1;
					sendDialogue("You do not have permission to perform this action.");
					return;
				}
				type = LoginProtocol.OFFENCE_ADDTYPE_IPBAN;
				sendTimeChoice();
			} else if (componentId == OPTION_2) {
				if (player.getRights() < 1) {
					stage = -1;
					sendDialogue("You do not have permission to perform this action.");
					return;
				}
				type = LoginProtocol.OFFENCE_ADDTYPE_IPMUTE;
				sendTimeChoice();
			} else if (componentId == OPTION_3) {
				if (player.getRights() < 1) {
					stage = -1;
					sendDialogue("You do not have permission to perform this action.");
					return;
				}
				type = LoginProtocol.OFFENCE_ADDTYPE_BAN;
				sendTimeChoice();
			} else if (componentId == OPTION_4) {
				if (player.getRights() < 1) {
					stage = -1;
					sendDialogue("You do not have permission to perform this action.");
					return;
				}
				type = LoginProtocol.OFFENCE_ADDTYPE_MUTE;
				sendTimeChoice();
			} else
				end();
		} else if (stage == 1) {
			if (componentId == OPTION_1) {
				time = 1000 * 60 * 60 * 1;
				Player name;
				name = World.getPlayerByDisplayName(target);
				name.setMutedFor("1");
				punish();
			} else if (componentId == OPTION_2) {
				time = 1000 * 60 * 60 * 24;
				Player name;
				name = World.getPlayerByDisplayName(target);
				name.setMutedFor("24");
				punish();
			} else if (componentId == OPTION_3) {
				time = 1000 * 60 * 60 * 48;
				Player name;
				name = World.getPlayerByDisplayName(target);
				name.setMutedFor("48");
				punish();
			} else if (componentId == OPTION_4) {
				if (player.getRights() < 1) {
					stage = -1;
					sendDialogue("You do not have permission to perform this action.");
					return;
				}
				time = 1000 * 60 * 60 * 24 * 7;
				Player name;
				name = World.getPlayerByDisplayName(target);
				name.setMutedFor("168");
				punish();
			} else if (componentId == OPTION_5) {
				if (player.getRights() < 1) {
					stage = -1;
					sendDialogue("You do not have permission to perform this action.");
					return;
				}
				time = -1;
				Player name;
				name = World.getPlayerByDisplayName(target);
				name.setMutedFor(Integer.MAX_VALUE + "");
				punish();
			} else {
				end();
			}
		} else {
			end();
		}
	}

	private void sendTimeChoice() {
		stage = 1;
		sendOptionsDialogue("Select punishment for " + target, "1 hour",
				"24 hours", "48 hours", "Week", "Permanent");
	}

	private void punish() {
		if (type == -1) {
			end();
			return;
		}

		long expires = 1000l * 60l * 60l * 24l * 7l * 4l * 12l * 50l; // 50
		// years
		if (time >= 0)
			expires = Utils.currentTimeMillis() + time;
		LoginClientChannelManager
				.sendUnreliablePacket(LoginChannelsPacketEncoder
						.encodeAddOffence(type, target, player.getUsername(),
								"Offence added by OffenceAddDialogue", expires)
						.getBuffer());

		stage = -1;
		sendDialogue("You have successfully punished " + target + ".");

		try {
			DateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal2 = Calendar.getInstance();
			final String FILE_PATH = "data/logs/punish/";
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH
					+ player.getUsername() + ".txt", true));
			writer.write("[" + dateFormat2.format(cal2.getTime()) + "] IP: "
					+ player.getSession().getIP() + " punished (" + expires
					+ ") " + target);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException er) {
			er.printStackTrace();
		}
	}

	@Override
	public void finish() {

	}
}