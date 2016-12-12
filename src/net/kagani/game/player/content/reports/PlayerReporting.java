package net.kagani.game.player.content.reports;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.kagani.game.World;
import net.kagani.game.player.Player;
import net.kagani.network.LoginClientChannelManager;
import net.kagani.network.LoginProtocol;
import net.kagani.network.encoders.LoginChannelsPacketEncoder;
import net.kagani.utils.Utils;

public class PlayerReporting {

	public static boolean report(Player player) {
		if (player.getInterfaceManager().containsScreenInterface()
				|| player.getInterfaceManager().containsBankInterface()) {
			player.getPackets()
					.sendGameMessage(
							"Please close the interface that you opened before activating the 'Report' system.");
			return false;
		}
		player.getInterfaceManager().sendCentralInterface(1406);
		return true;
	}

	public static void reportABug(Player player) {
		player.getInterfaceManager().sendCentralInterface(1405);
	}

	public static void reportAPlayer(Player player, String name) {
		player.getInterfaceManager().sendCentralInterface(594);
		if (name != null)
			player.getPackets().sendCSVarString(2578, name);
		if (player.getRights() > 0)
			player.getPackets().sendHideIComponent(594, 9, false);

	}

	public static void report(Player player, String name) {
		if (player.getInterfaceManager().containsScreenInterface()
				|| player.getInterfaceManager().containsBankInterface()) {
			player.getPackets()
					.sendGameMessage(
							"Please close the interface that you opened before activating the 'Report' system.");
			return;
		}
		reportAPlayer(player, name);
	}

	public static void report(Player player, String reportedName, int type,
			boolean mute) {
		if (mute && player.getRights() < 1)
			return;
		Player reported = World.getPlayerByDisplayName(reportedName);
		if (reported == null)
			return;
		if (mute) {
			LoginClientChannelManager
					.sendUnreliablePacket(LoginChannelsPacketEncoder
							.encodeAddOffence(
									LoginProtocol.OFFENCE_ADDTYPE_MUTE,
									reported.getDisplayName(),
									player.getUsername(),
									"Mute by report interface(" + getType(type)
											+ ")",
									Utils.currentTimeMillis()
											+ (1000 * 60 * 60 * 48))
							.getBuffer());
		}
		player.getPackets().sendGameMessage(
				"Thank-you, your abuse report has been received.");
		try {
			DateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal2 = Calendar.getInstance();
			final String FILE_PATH = "data/logs/reports/";
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH
					+ player.getUsername() + ".txt", true));
			writer.write("[" + dateFormat2.format(cal2.getTime()) + "] IP: "
					+ player.getSession().getIP() + " reported "
					+ reported.getDisplayName() + " IP: "
					+ reported.getSession().getIP() + " for " + type);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException er) {
			er.printStackTrace();
		}
	}

	private static String getType(int id) {
		switch (id) {
		case 6:
			return "Buying or selling account";
		case 9:
			return "Encouraging rule breaking";
		case 5:
			return "Staff impersonation";
		case 7:
			return "Macroing or use of bots";
		case 15:
			return "Scamming";
		case 4:
			return "Exploiting a bug";
		case 16:
			return "Seriously offensive language";
		case 17:
			return "Solicitation";
		case 18:
			return "Disruptive behaviour";
		case 19:
			return "Offensive account name";
		case 20:
			return "Real-life threats";
		case 13:
			return "Asking for or providing contact information";
		case 21:
			return "Breaking real-world laws";
		case 11:
			return "Advertising websites";
		}
		return "Unknown";
	}

}
