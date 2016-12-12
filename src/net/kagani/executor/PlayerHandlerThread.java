package net.kagani.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.kagani.Settings;
import net.kagani.game.World;
import net.kagani.game.player.Player;
import net.kagani.network.LoginClientChannelManager;
import net.kagani.network.Session;
import net.kagani.network.encoders.LoginChannelsPacketEncoder;
import net.kagani.stream.OutputStream;
import net.kagani.utils.IsaacKeyPair;
import net.kagani.utils.Logger;
import net.kagani.utils.MachineInformation;
import net.kagani.utils.SerializationUtilities;
import net.kagani.utils.Utils;

public final class PlayerHandlerThread extends Thread {

	/**
	 * Contains lock object.
	 */
	private static Object lock;
	/**
	 * Sessions, waiting for their login responses.
	 */
	private static Map<Integer, LoginDetails> waitingLoginSessions;
	/**
	 * Players that are logged out of this world, but still need to be sent to
	 * login server.
	 */
	private static List<Player> waitingLogoutPlayers;
	/**
	 * Player saves, waiting to be sent.
	 */
	private static List<SaveDetails> waitingPlayerSaves;

	static {
		lock = new Object();
		waitingLoginSessions = new HashMap<Integer, LoginDetails>();
		waitingLogoutPlayers = new ArrayList<Player>();
		waitingPlayerSaves = new ArrayList<SaveDetails>();
	}

	protected PlayerHandlerThread() {
		super("Player handler");
	}

	@Override
	public final void run() {
		while (!GameExecutorManager.executorShutdown) {
			try {
				synchronized (lock) {
					for (SaveDetails details : waitingPlayerSaves)
						processPlayerSave(details);
					waitingPlayerSaves.clear();

					Iterator<Map.Entry<Integer, LoginDetails>> it$ = waitingLoginSessions
							.entrySet().iterator();
					while (it$.hasNext()) {
						if (processLoginSession(it$.next().getValue()))
							it$.remove();
					}

					Iterator<Player> i$ = waitingLogoutPlayers.iterator();
					while (i$.hasNext()) {
						if (processLogoutPlayer(i$.next()))
							i$.remove();
					}
				}

				Thread.sleep(20);
			} catch (Throwable t) {
				Logger.handle(t);
			}
		}

		do { // even if shutdown occured, we must finish logouts.
			try {
				synchronized (lock) {
					for (SaveDetails details : waitingPlayerSaves)
						processPlayerSave(details);
					waitingPlayerSaves.clear();

					Iterator<Player> i$ = waitingLogoutPlayers.iterator();
					while (i$.hasNext()) {
						if (processLogoutPlayer(i$.next()))
							i$.remove();
					}

					if (waitingLogoutPlayers.size() <= 0)
						break;

					Thread.sleep(20);
				}
			} catch (Throwable t) {
				Logger.handle(t);
			}
		} while (true);
	}

	/**
	 * Processe's given player save,
	 */
	private final void processPlayerSave(SaveDetails details) {
		OutputStream[] parts = LoginChannelsPacketEncoder
				.encodePlayerFileTransmit(details.username, details.data);
		LoginClientChannelManager.sendReliablePacket(LoginChannelsPacketEncoder
				.encodePlayerFileTransmitInit(details.username,
						details.data.length).getBuffer());
		for (int i = 0; i < parts.length; i++)
			LoginClientChannelManager.sendReliablePacket(parts[i].getBuffer());
	}

	/**
	 * Processe's given player logout. Return's true if player should be
	 * removed.
	 */
	private final boolean processLogoutPlayer(Player player) {
		byte[] data = SerializationUtilities.tryStoreObject(player);
		if (data != null && data.length > 0) {
			OutputStream[] parts = LoginChannelsPacketEncoder
					.encodePlayerFileTransmit(player.getUsername(), data);
			LoginClientChannelManager
					.sendReliablePacket(LoginChannelsPacketEncoder
							.encodePlayerFileTransmitInit(player.getUsername(),
									data.length).getBuffer());
			for (int i = 0; i < parts.length; i++)
				LoginClientChannelManager.sendReliablePacket(parts[i]
						.getBuffer());
		}
		for (SaveDetails details : waitingPlayerSaves)
			processPlayerSave(details);
		LoginClientChannelManager.sendReliablePacket(LoginChannelsPacketEncoder
				.encodeLogout(player.getUsername()).getBuffer());
		return true;
	}

	/**
	 * Processe's given session. Returns true if session should be removed.
	 */
	private final boolean processLoginSession(LoginDetails details) {
		if (details.step == 0) { // send request to login server
			LoginClientChannelManager
					.sendUnreliablePacket(LoginChannelsPacketEncoder
							.encodeLoginRequest(details.hashCode(),
									details.username, details.password,
									details.session.getIP(), details.lobby)
							.getBuffer());
			details.step = 1;
		} else if (details.step == 1) { // while waiting request from login
			// server
			if ((Utils.currentTimeMillis() - details.creationTime) > Settings.LOGIN_SERVER_REQUEST_TIMEOUT) {
				try {

					Player player = World
							.getPlayerByDisplayName(details.username);

					player.disconnect(true, false);
					details.session.getLoginPackets().sendClosingPacket(3);

				} catch (Exception e) {
				}
				details.session.getLoginPackets().sendClosingPacket(23);
				return true;
			}
		} else if (details.step == 2) { // login server response received
			if (details.response_status == 255) { // we need to wait bit more
				// and then retry
				details.waitTime = Utils.currentTimeMillis()
						+ Settings.LOGIN_SERVER_RETRY_DELAY;
				details.step = 3;
			} else if (details.response_status == 2) { // login accepted
				if (details.response_filelength <= 0) { // new player
					Player player = new Player();
					initPlayer(player, details);
					return true;
				} else {
					// we need to wait for the file to arrive
					details.waitTime = Utils.currentTimeMillis()
							+ Settings.LOGIN_SERVER_FILE_TIMEOUT;
					details.step = 4;
				}
			} else { // generic error reply
				details.session.getLoginPackets().sendClosingPacket(
						details.response_status);
				return true;
			}
		} else if (details.step == 3) { // waiting before retry
			if (Utils.currentTimeMillis() > details.waitTime) {
				try {
					Player player = World
							.getPlayerByDisplayName(details.username);
					player.disconnect(true, false);
					details.session.getLoginPackets().sendClosingPacket(3);

				} catch (Exception e) {
				}
				details.session.getLoginPackets().sendClosingPacket(23); // no
				// reply
				// from
				// login
				// server
				return true;
			}
		} else if (details.step == 4) { // waiting for file to arrive.
			if (Utils.currentTimeMillis() > details.waitTime) {
				// we waited for the file too long
				LoginClientChannelManager
						.sendReliablePacket(LoginChannelsPacketEncoder
								.encodeLogout(details.username).getBuffer());
				details.session.getLoginPackets().sendClosingPacket(13);
				return true;
			}

			if (details.response_receivedamount == details.response_filelength) {
				// we received full file
				Object o = SerializationUtilities
						.tryLoadObject(details.response_filebuffer);
				if (o == null || !(o instanceof Player)) {
					LoginClientChannelManager
							.sendReliablePacket(LoginChannelsPacketEncoder
									.encodeLogout(details.username).getBuffer());
					details.session.getLoginPackets().sendClosingPacket(24);
					return true;
				}
				initPlayer((Player) o, details);
				return true;
			} else if (details.response_receivedamount > details.response_filelength) {
				// we received too much??
				LoginClientChannelManager
						.sendReliablePacket(LoginChannelsPacketEncoder
								.encodeLogout(details.username).getBuffer());
				details.session.getLoginPackets().sendClosingPacket(22);
				return true;
			}
		} else {
			details.session.getLoginPackets().sendClosingPacket(25); // unexpected
			// response
			return true;
		}

		return false;
	}

	/**
	 * Initialize's given player.
	 */
	private final void initPlayer(Player player, LoginDetails details) {
		try {
			player.init(details.session, details.lobby, details.username,
					details.displayName, details.macAddress, details.email,
					details.rights, details.messageIcon, details.masterLogin,
					details.donator, details.extremeDonator, details.support,
					details.gfxDesigner, details.muted, details.lastVote,
					details.displayMode, details.screenWidth,
					details.screenHeight, details.machineInfo,
					new IsaacKeyPair(details.sessionKeys));
			if (details.lobby) {
				details.session.getLoginPackets().sendLobbyDetails(player);
				details.session.setDecoder(3, player);
				details.session.setEncoder(2, player);
				player.startLobby();
			} else {
				details.session.getLoginPackets().sendILayoutVars(player);
				details.session.getLoginPackets().sendLoginDetails(player);
				details.session.setDecoder(4, player); // 3
				details.session.setEncoder(2, player); // 2
				player.start();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handle's login response packet.
	 */
	public static final void handleLoginResponse(int sessionid,
			String sessionuser, int status, int file_length, int rights,
			boolean masterLogin, boolean donator, boolean extremeDonator,
			boolean support, boolean gfxDesigner, int messageIcon,
			boolean muted, long lastVote, String displayName, String email) {
		synchronized (lock) {
			LoginDetails details = waitingLoginSessions.get(sessionid);
			if (details == null && status == 2) {
				// we did not actually log in
				LoginClientChannelManager
						.sendReliablePacket(LoginChannelsPacketEncoder
								.encodeLogout(sessionuser).getBuffer());
			} else if (details != null) {
				details.response_status = status;
				details.response_filelength = file_length;

				details.rights = rights;
				details.donator = donator;
				details.masterLogin = masterLogin;
				details.extremeDonator = extremeDonator;
				details.support = support;
				details.gfxDesigner = gfxDesigner;
				details.messageIcon = messageIcon;
				details.muted = muted;
				details.lastVote = lastVote;
				details.displayName = displayName;
				details.email = email;
				details.step++;
			}
		}
	}

	/**
	 * Handle's login file response packet.
	 */
	public static final void handleLoginFileResponse(int sessionid, byte[] data) {
		synchronized (lock) {
			LoginDetails details = waitingLoginSessions.get(sessionid);
			if (details == null || details.response_filelength <= 0)
				return;

			if (details.response_filebuffer == null) {
				details.response_filebuffer = new byte[details.response_filelength];
				details.response_receivedamount = 0;
			}

			int amt_write = Math.min(details.response_filelength
					- details.response_receivedamount, data.length);
			System.arraycopy(data, 0, details.response_filebuffer,
					details.response_receivedamount, amt_write);
			details.response_receivedamount += amt_write;
		}
	}

	/**
	 * Add's session to processing queue.
	 */
	public static final void addSession(Session session, int[] sessionKeys,
			boolean lobby, String username, String password, String MACAddress,
			int displayMode, int screenWidth, int screenHeight,
			MachineInformation machineInfo) {
		LoginDetails details = new LoginDetails(session, sessionKeys, lobby,
				username, password, MACAddress, displayMode, screenWidth,
				screenHeight, machineInfo);
		synchronized (lock) {
			waitingLoginSessions.put(details.hashCode(), details);
		}
	}

	/**
	 * Add's player to logout queue.
	 */
	public static final void addLogout(Player player) {
		synchronized (lock) {
			waitingLogoutPlayers.add(player);
		}
	}

	/**
	 * Add's player save to sending queue.
	 */
	public static final void addSave(String username, byte[] data) {
		synchronized (lock) {
			waitingPlayerSaves.add(new SaveDetails(username, data));
		}
	}

	private static class SaveDetails {
		private String username;
		private byte[] data;

		public SaveDetails(String username, byte[] data) {
			this.username = username;
			this.data = data;
		}
	}

	private static class LoginDetails {
		private volatile int step;
		private long creationTime;
		private long waitTime;
		private Session session;
		private int[] sessionKeys;
		private boolean lobby;
		private String username;
		private String password;
		private String macAddress;
		private int displayMode;
		private int screenWidth;
		private int screenHeight;
		private MachineInformation machineInfo;

		private String displayName;
		private String email;

		private int rights;
		private int messageIcon;
		private boolean masterLogin;
		private boolean donator;
		private boolean extremeDonator;
		private boolean support;
		private boolean gfxDesigner;
		private boolean muted;
		private long lastVote;

		private int response_status;
		private int response_filelength;
		private byte[] response_filebuffer;
		private int response_receivedamount;

		public LoginDetails(Session session, int[] sessionKeys, boolean lobby,
				String username, String password, String MACAddress,
				int displayMode, int screenWidth, int screenHeight,
				MachineInformation machineInfo) {
			this.step = 0;
			this.creationTime = Utils.currentTimeMillis();
			this.waitTime = 0;
			this.session = session;
			this.sessionKeys = sessionKeys;
			this.lobby = lobby;
			this.username = username;
			this.password = password;
			this.macAddress = MACAddress;
			this.displayMode = displayMode;
			this.screenWidth = screenWidth;
			this.screenHeight = screenHeight;
			this.machineInfo = machineInfo;

			this.response_status = 0;
			this.response_filelength = 0;
			this.response_filebuffer = null;
			this.response_receivedamount = 0;
		}
	}
}