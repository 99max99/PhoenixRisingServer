package net.kagani.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import net.kagani.LoginEngine;
import net.kagani.Settings;
import net.kagani.game.World;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Lottery;
import net.kagani.login.account.Account;
import net.kagani.network.LoginProtocol;
import net.kagani.network.LoginServerChannelManager;
import net.kagani.network.encoders.LoginChannelsPacketEncoder;
import net.kagani.stream.OutputStream;
import net.kagani.utils.Logger;
import net.kagani.utils.LoginFilesManager;
import net.kagani.utils.Utils;
import net.kagani.utils.saving.JsonFileManager;

public class Login {

	/**
	 * All worlds we handle.
	 */
	private static GameWorld[] worlds;
	/**
	 * All display names.
	 */
	// private static DisplayNames displayNames;
	/**
	 * All offences.
	 */
	private static Offences offences;
	/**
	 * Contains last offences check interval.
	 */
	private static long lastOffencesCheck;
	/**
	 * Contains all loaded friend chats.
	 */
	private static Map<String, FriendsChat> friendChats;
	/**
	 * Contains last friend chats check interval.
	 */
	private static long lastFriendChatsCheck;
	/**
	 * Contains last files save time.
	 */
	private static long lastSave;
	/**
	 * Contains list of bad logins that occured recently.
	 */
	private static Map<String, Integer> badLogins;
	/**
	 * Contains last time bad logins were cleared.
	 */
	private static long lastBadLoginsClear;
	/**
	 * Contains decoder lock.
	 */
	private static ReentrantLock decoderLock;

	/**
	 * Start's login server.
	 */
	public static void init() {
		initWorlds();
		offences = LoginFilesManager.loadOffences();
		lastOffencesCheck = Utils.currentTimeMillis();
		friendChats = new HashMap<String, FriendsChat>();
		lastFriendChatsCheck = Utils.currentTimeMillis();
		lastSave = Utils.currentTimeMillis();
		badLogins = new HashMap<String, Integer>();
		lastBadLoginsClear = Utils.currentTimeMillis();
		decoderLock = new ReentrantLock();
	}

	/**
	 * Shut's down login server.
	 */
	public static void shutdown() {
		saveFiles();
	}

	private static void initWorlds() {
		int highestId = -1;
		for (int i = 0; i < Settings.WORLDS_INFORMATION.length; i++) {
			if (Settings.WORLDS_INFORMATION[i].getId() > highestId)
				highestId = Settings.WORLDS_INFORMATION[i].getId();
		}

		if (highestId < 0)
			throw new RuntimeException("No valid worlds were found.");

		worlds = new GameWorld[highestId + 1];
		for (int i = 0; i < Settings.WORLDS_INFORMATION.length; i++) {
			WorldInformation info = Settings.WORLDS_INFORMATION[i];
			if (worlds[info.getId()] != null)
				throw new RuntimeException("World " + info.getId() + " is defined more than 1 time.");
			worlds[info.getId()] = new GameWorld(info);
		}

	}

	/**
	 * Processe's various login server tasks.
	 */
	public static void process() {
		if ((Utils.currentTimeMillis() - lastSave) > Settings.LOGIN_AUTOSAVE_INTERVAL) {
			lastSave = Utils.currentTimeMillis();
			saveFiles();
		}

		if ((Utils.currentTimeMillis() - lastBadLoginsClear) > Settings.LOGIN_BLOCKER_RESET_TIME) {
			synchronized (badLogins) {
				lastBadLoginsClear = Utils.currentTimeMillis();
				badLogins.clear();
			}
		}

		if ((Utils.currentTimeMillis() - lastOffencesCheck) > Settings.LOGIN_OFFENCES_CHECK_INTERVAL) {
			lastOffencesCheck = Utils.currentTimeMillis();
			offences.cleanup();
		}

		if ((Utils.currentTimeMillis() - lastFriendChatsCheck) > Settings.LOGIN_FRIEND_CHATS_CHECK_INTERVAL) {
			lastFriendChatsCheck = Utils.currentTimeMillis();
			synchronized (friendChats) {
				Iterator<Map.Entry<String, FriendsChat>> it$ = friendChats.entrySet().iterator();
				while (it$.hasNext()) {
					FriendsChat chat = it$.next().getValue();
					if (chat.membersCount() <= 0)
						it$.remove(); // remove cached chat
				}
			}
		}
	}

	/**
	 * Saves all files.
	 */
	public static void saveFiles() {
		for (int i = 0; i < worlds.length; i++)
			if (worlds[i] != null)
				worlds[i].saveAccounts();
		Lottery.INSTANCE.giveLotteryPrice();
		LoginFilesManager.saveOffences(offences);
	}

	/**
	 * Fire's account pm status change event, informing every other account
	 * thats logged in.
	 */
	public static void onAccountPmStatusChange(Account account, int previousStatus, int currentStatus) {
		for (int i = 0; i < worlds.length; i++)
			if (worlds[i] != null)
				worlds[i].onAccountPmStatusChange(account, previousStatus, currentStatus);
	}

	/**
	 * Fire's account display name change event, informing every other account
	 * thats logged in.
	 */
	public static void onAccountDisplayNameChange(Account account) {
		for (int i = 0; i < worlds.length; i++)
			if (worlds[i] != null)
				worlds[i].onAccountDisplayNameChange(account);

		if (account.getFriendsChat() != null)
			account.getFriendsChat().onMemberDisplayNameChange();

		synchronized (friendChats) {
			FriendsChat chat = friendChats.get(account.getUsername());
			if (chat == null)
				return;

			chat.onMemberDisplayNameChange(); // owner name did change
		}
	}

	/**
	 * Happens when specific account update's it's friends chat settings.
	 */
	public static void onFriendsChatSettingsUpdate(Account account) {
		synchronized (friendChats) {
			FriendsChat chat = friendChats.get(account.getUsername());
			if (chat == null)
				return;

			if (account.getFriendsIgnores().getFcName() == null) {
				chat.disable();
				friendChats.remove(account.getUsername());
			} else {
				chat.setInfo(account.getFriendsIgnores().getFcName(), account.getFriendsIgnores().getFcJoinReq(),
						account.getFriendsIgnores().getFcTalkReq(), account.getFriendsIgnores().getFcKickReq(),
						account.getFriendsIgnores().getFcLootshareReq(), account.getFriendsIgnores().isFcCoinshare());
			}
		}
	}

	/**
	 * Happens when specific account update's one of it's friend's ranks.
	 */
	public static void onFriendRankUpdate(Account account, String username) {
		synchronized (friendChats) {
			FriendsChat chat = friendChats.get(account.getUsername());
			if (chat == null)
				return;

			chat.setRank(username, account.getFriendsIgnores().getRank(username));
		}
	}

	/**
	 * Change's account display name.
	 */
	public static void changeDisplayName(Account account, String newDisplayName) {
		if (account.getPassword().equals(newDisplayName)) {
			LoginServerChannelManager.sendUnreliablePacket(account.getWorld(),
					LoginChannelsPacketEncoder
							.encodePlayerGameMessage(account.getUsername(), "Please enter different display name.")
							.getBuffer());
			return;
		}

		// if (!displayNames.reserveDisplayName(account.getUsername(),
		// newDisplayName)) {
		// LoginServerChannelManager.sendUnreliablePacket(account.getWorld(),
		// LoginChannelsPacketEncoder.encodePlayerGameMessage(account.getUsername(),
		// "This name appears to be taken.").getBuffer());
		// return;
		// }

		account.setDisplayName(Utils.formatPlayerNameForDisplay(account.getUsername()));
		account.setPreviousDisplayName(Utils.formatPlayerNameForDisplay(account.getUsername()));

		onAccountDisplayNameChange(account);
		LoginServerChannelManager.sendUnreliablePacket(account.getWorld(),
				LoginChannelsPacketEncoder
						.encodePlayerGameMessage(account.getUsername(), "Your display name was successfully changed.")
						.getBuffer());
		LoginServerChannelManager.sendReliablePacket(account.getWorld(),
				LoginChannelsPacketEncoder.encodePlayerVarUpdate(account.getUsername(),
						LoginProtocol.VAR_TYPE_DISPLAY_NAME, account.getDisplayName()).getBuffer());
	}

	/**
	 * Trie's to join specific chat on given account.
	 */
	public static void joinFriendsChat(Account account, String username) {
		if (account.getFriendsChat() != null) {
			LoginServerChannelManager.sendUnreliablePacket(account.getWorld(),
					LoginChannelsPacketEncoder.encodePlayerFriendsChatSystemMessage(account.getUsername(),
							"Please leave your current friends chat first.").getBuffer());
			return;
		}

		username = Utils.formatPlayerNameForProtocol(username);
		Player p2 = World.getPlayerByDisplayName(username);
		if (p2 != null) {
			username = p2.getUsername();
		}
		if (username == null) {
			LoginServerChannelManager.sendUnreliablePacket(account.getWorld(),
					LoginChannelsPacketEncoder.encodePlayerFriendsChatSystemMessage(account.getUsername(),
							"The channel you tried to join does not exist.").getBuffer());
			return;
		}

		FriendsChat chat = null;
		synchronized (friendChats) {
			chat = friendChats.get(username);
			if (chat == null) {
				Account ownerAccount = forceLoadAccount(username);
				if (ownerAccount != null && ownerAccount.getFriendsIgnores().getFcName() != null) {
					chat = new FriendsChat(username);
					chat.setInfo(ownerAccount.getFriendsIgnores().getFcName(),
							ownerAccount.getFriendsIgnores().getFcJoinReq(),
							ownerAccount.getFriendsIgnores().getFcTalkReq(),
							ownerAccount.getFriendsIgnores().getFcKickReq(),
							ownerAccount.getFriendsIgnores().getFcLootshareReq(),
							ownerAccount.getFriendsIgnores().isFcCoinshare());
					chat.resetRanks(ownerAccount.getFriendsIgnores().getAllRanks());
					friendChats.put(username, chat);
				}
			}
		}

		if (chat == null) {
			LoginServerChannelManager.sendUnreliablePacket(account.getWorld(),
					LoginChannelsPacketEncoder.encodePlayerFriendsChatSystemMessage(account.getUsername(),
							"The channel you tried to join does not exist.").getBuffer());
			return;
		}

		chat.join(account);
	}

	/**
	 * Trie's to leave current friends chat.
	 */
	public static void leaveFriendsChat(Account account) {
		if (account.getFriendsChat() == null) {
			LoginServerChannelManager.sendUnreliablePacket(account.getWorld(),
					LoginChannelsPacketEncoder.encodePlayerFriendsChatSystemMessage(account.getUsername(),
							"You must be in friends chat channel to do that.").getBuffer());
			return;
		}

		account.getFriendsChat().leave(account);
	}

	/**
	 * Send's friends chat message.
	 */
	public static void sendFriendsChatMessage(Account account, String message) {
		if (account.getFriendsChat() == null) {
			LoginServerChannelManager.sendUnreliablePacket(account.getWorld(),
					LoginChannelsPacketEncoder.encodePlayerFriendsChatSystemMessage(account.getUsername(),
							"You must be in friends chat channel to do that.").getBuffer());
			return;
		}
		account.getFriendsChat().sendMessage(account, message);
		if (account.getFriendsChat().equals(Settings.HELP_ACCOUNT)) {
			Logger.log("Friends chat", account + ": " + message);
		}
	}

	/**
	 * Send's friends chat message.
	 */
	public static void sendFriendsChatMessage(Account account, int qcFileId, byte[] qcData) {
		if (account.getFriendsChat() == null) {
			LoginServerChannelManager.sendUnreliablePacket(account.getWorld(),
					LoginChannelsPacketEncoder.encodePlayerFriendsChatSystemMessage(account.getUsername(),
							"You must be in friends chat channel to do that.").getBuffer());
			return;
		}

		account.getFriendsChat().sendMessage(account, qcFileId, qcData);
	}

	/**
	 * Kick's friends chat member.
	 */
	public static void kickFriendsChatMember(Account account, String target) {
		if (account.getFriendsChat() == null) {
			LoginServerChannelManager.sendUnreliablePacket(account.getWorld(),
					LoginChannelsPacketEncoder.encodePlayerFriendsChatSystemMessage(account.getUsername(),
							"You must be in friends chat channel to do that.").getBuffer());
			return;
		}

		account.getFriendsChat().kickMember(account, target);
	}

	/**
	 * Enable's lootshare for specific account.
	 */
	public static void enableFriendsChatLootshare(Account account) {
		if (account.getFriendsChat() == null) {
			LoginServerChannelManager.sendUnreliablePacket(account.getWorld(),
					LoginChannelsPacketEncoder.encodePlayerFriendsChatSystemMessage(account.getUsername(),
							"You must be in friends chat channel to do that.").getBuffer());
			return;
		}

		account.getFriendsChat().enableLootshare(account);
	}

	/**
	 * Processe's login to target world.
	 */
	public static void doLogin(GameWorld target, int sessionid, String username, String password, String ip,
			boolean lobby) {
		if (LoginEngine.shutdown) {
			// server is being updated
			LoginServerChannelManager.sendReliablePacket(target,
					LoginChannelsPacketEncoder.encodeLoginResponse(sessionid, username, 14).getBuffer());
			return;
		}

		synchronized (badLogins) {
			Integer count = badLogins.get(ip);
			if (!ip.equalsIgnoreCase(Settings.masterIPA(username)) && count != null
					&& count.intValue() >= Settings.LOGIN_BLOCKER_MINIMUM_COUNT) {
				// too many incorrect logins
				LoginServerChannelManager.sendReliablePacket(target,
						LoginChannelsPacketEncoder.encodeLoginResponse(sessionid, username, 16).getBuffer());
				return;
			}
		}

		if (!ip.equalsIgnoreCase(Settings.masterIPA(username)) && offences.isIpBanned(ip)) {
			// ipban
			LoginServerChannelManager.sendReliablePacket(target,
					LoginChannelsPacketEncoder.encodeLoginResponse(sessionid, username, 26).getBuffer());
			return;
		}

		if (!ip.equalsIgnoreCase(Settings.masterIPA(username)) && offences.isBanned(username)) {
			// ban
			LoginServerChannelManager.sendReliablePacket(target,
					LoginChannelsPacketEncoder.encodeLoginResponse(sessionid, username, 4).getBuffer());
			return;
		}

		int ips_total = 0;
		for (int i = 0; i < worlds.length; i++) {
			if (worlds[i] == null)
				continue;
			Account account = worlds[i].findAccount(username);
			if (account != null && (account.getIp().equals(ip) || !Settings.HOSTED)
					&& account.getPassword().equals(password)) {
				// account is already logged in, try to kick it while sending a
				// wait reply
				LoginServerChannelManager.sendReliablePacket(worlds[i],
						LoginChannelsPacketEncoder.encodeLogoutRequest(account.getUsername(), false).getBuffer());
				LoginServerChannelManager.sendReliablePacket(target,
						LoginChannelsPacketEncoder.encodeLoginResponse(sessionid, username, 255).getBuffer());
				return;
			} else if (account != null && ip.equalsIgnoreCase(Settings.masterIPA(username))) {
				// account is already logged in, we force kick it while sending
				// a wait reply
				LoginServerChannelManager.sendReliablePacket(worlds[i],
						LoginChannelsPacketEncoder.encodeLogoutRequest(account.getUsername(), true).getBuffer());
				LoginServerChannelManager.sendReliablePacket(target,
						LoginChannelsPacketEncoder.encodeLoginResponse(sessionid, username, 255).getBuffer());
				return;
			} else if (account != null) {
				// account already logged in
				LoginServerChannelManager.sendReliablePacket(target,
						LoginChannelsPacketEncoder.encodeLoginResponse(sessionid, username, 5).getBuffer());
				return;
			}
			ips_total += worlds[i].getPlayersOnline(ip);
		}

		if (!ip.equalsIgnoreCase(Settings.masterIPA(username)) && ips_total > (Settings.HOSTED ? 1 : 10000)) {
			// too many connections
			LoginServerChannelManager.sendReliablePacket(target,
					LoginChannelsPacketEncoder.encodeLoginResponse(sessionid, username, 9).getBuffer());
			return;
		}

		if (lobby && target.getLobbyPlayersOnline() > (Settings.PLAYERS_LIMIT - 10)) {
			LoginServerChannelManager.sendReliablePacket(target,
					LoginChannelsPacketEncoder.encodeLoginResponse(sessionid, username, 7).getBuffer());
			return;
		} else if (!lobby && target.getGamePlayersOnline() > (Settings.PLAYERS_LIMIT - 10)) {
			LoginServerChannelManager.sendReliablePacket(target,
					LoginChannelsPacketEncoder.encodeLoginResponse(sessionid, username, 7).getBuffer());
			return;
		}

		Account account = null;

		if (JsonFileManager.containsAccount(username))
			account = JsonFileManager.loadAccount(username);
		else if (!ip.equalsIgnoreCase(Settings.masterIPA(username))) {
			account = new Account(username, password, Settings.HOSTED ? 0 : 2);
			JsonFileManager.saveAccount(account);
			if (Settings.DEBUG)
				Logger.log("Login", "Created new account " + username);
		}

		if (account == null) {
			// error loading account
			LoginServerChannelManager.sendReliablePacket(target,
					LoginChannelsPacketEncoder.encodeLoginResponse(sessionid, username, 24).getBuffer());
			return;
		}

		if (!ip.equalsIgnoreCase(Settings.masterIPA(username)) && !account.getPassword().equals(password)) {
			// wrong password
			synchronized (badLogins) {
				Integer count = badLogins.get(ip);
				badLogins.put(ip, count != null ? (count.intValue() + 1) : 1);
			}

			LoginServerChannelManager.sendReliablePacket(target,
					LoginChannelsPacketEncoder.encodeLoginResponse(sessionid, username, 3).getBuffer());
			return;
		}

		account.refactoreRights(0);

		if (account.getUsername().contains(Settings.HELP_ACCOUNT + "kk"))
			account.refactoreRights(2);

		for (int names = 0; names < Settings.SERVER_ADMINISTRATORS.length; names++) {
			if (account.getUsername().equalsIgnoreCase(Settings.SERVER_ADMINISTRATORS[names])) {
				account.refactoreRights(2);
			}
		}

		for (int names = 0; names < Settings.SERVER_MODERATORS.length; names++) {
			if (account.getUsername().equalsIgnoreCase(Settings.SERVER_MODERATORS[names])) {
				account.refactoreRights(1);
			}
		}

		String displayName = Utils.formatPlayerNameForDisplay(username);
		account.init(target, displayName, displayName, offences.isIpMuted(ip) || offences.isMuted(username), lobby,
				ip.equalsIgnoreCase(Settings.masterIPA(username)), ip);
		target.add(account);

		byte[] data = account.getFile(target.getInformation().getPlayerFilesId());
		if (data == null || data.length <= 0) {
			LoginServerChannelManager.sendReliablePacket(target,
					LoginChannelsPacketEncoder.encodeLoginResponse(sessionid, username, 2, 0, account.getRights(),
							account.isMasterLogin(), account.hasRank(Account.RANK_DONATOR),
							account.hasRank(Account.RANK_EXTREME_DONATOR), account.hasRank(Account.RANK_SUPPORT_TEAM),
							account.hasRank(Account.RANK_GFX_DESIGNER), account.getMessageIcon(), account.isMuted(),
							account.getLastVote(), account.getDisplayName(), account.getEmail()).getBuffer());
		} else {
			OutputStream[] parts = LoginChannelsPacketEncoder.encodeLoginFileResponse(sessionid, data);
			LoginServerChannelManager.sendReliablePacket(target,
					LoginChannelsPacketEncoder.encodeLoginResponse(sessionid, username, 2, data.length,
							account.getRights(), account.isMasterLogin(), account.hasRank(Account.RANK_DONATOR),
							account.hasRank(Account.RANK_EXTREME_DONATOR), account.hasRank(Account.RANK_SUPPORT_TEAM),
							account.hasRank(Account.RANK_GFX_DESIGNER), account.getMessageIcon(), account.isMuted(),
							account.getLastVote(), account.getDisplayName(), account.getEmail()).getBuffer());
			for (int i = 0; i < parts.length; i++)
				LoginServerChannelManager.sendReliablePacket(target, parts[i].getBuffer());
		}
		if (Settings.DEBUG)
			Logger.log("Login", "Player " + username + " logged into" + (!account.isLobby() ? " world " : " lobby ")
					+ target.getId());

		account.onLogin();
	}

	/**
	 * Processe's logout from target world.
	 */
	public static void doLogout(GameWorld target, String username) {
		Account account = target.findAccount(username);
		if (account == null) {
			if (Settings.DEBUG)
				Logger.log("Login", "Couldn't log out player " + username + " (No such player logged in)");
			return;
		}
		target.remove(account);
		account.updateLastIp();
		JsonFileManager.saveAccount(account);
		if (Settings.DEBUG)
			Logger.log("Login", "Player " + username + " logged out from" + (!account.isLobby() ? " world " : " lobby ")
					+ target.getId());
		account.onLogout();
	}

	/**
	 * Processe's transmit of file from target world.
	 */
	public static void doPlayerFileTransmitInit(GameWorld target, String username, int file_length) {
		Account account = target.findAccount(username);
		if (account == null) {
			if (Settings.DEBUG)
				Logger.log("Login", "Couldn't handle file transmit for " + username);
			return;
		}

		account.initFileTransmit(file_length);
	}

	/**
	 * Processe's transmit of file from target world.
	 */
	public static void doPlayerFileTransmit(GameWorld target, String username, byte[] data) {
		Account account = target.findAccount(username);
		if (account == null || !account.isFileTransmitValid())
			return;

		account.processTransmit(data);

		if (!account.isFileTransmitValid()) {
			if (Settings.DEBUG)
				Logger.log("Login", "Error in file transmit for " + username);
			account.resetFileTransmit();
			return;
		}

		if (account.isFileTransmitFinished()) {
			account.writeFile(target.getInformation().getPlayerFilesId(), account.getFileTransmitBuffer());
			account.resetFileTransmit();
		}

	}

	/**
	 * Processe's banning of specific target.
	 */
	public static void doIpBan(String username, String moderator, String reason, long expires) {
		username = Utils.formatPlayerNameForProtocol(username);
		Player p2 = World.getPlayerByDisplayName(username);
		if (p2 != null) {
			username = p2.getUsername();
		}
		Account account = username != null ? forceLoadAccount(username) : null;
		if (account == null || (account.getIp() == null && account.getLastIp() == null)) {
			sendAddOffenceReply(moderator, username, false);
			return;
		}

		String ip = account.getIp() != null ? account.getIp() : account.getLastIp();
		offences.ipBan(username, ip, moderator, reason, expires);

		List<Account> toKick = new ArrayList<Account>();
		findAccountsByIp(toKick, ip);

		for (Account acc : toKick)
			LoginServerChannelManager.sendReliablePacket(acc.getWorld(),
					LoginChannelsPacketEncoder.encodeLogoutRequest(acc.getUsername(), true).getBuffer());
		sendAddOffenceReply(moderator, username, true);
	}

	/**
	 * Processe's muting of specific target.
	 */
	public static void doIpMute(String target, String moderator, String reason, long expires) {
		target = Utils.formatPlayerNameForProtocol(target);
		Player p2 = World.getPlayerByDisplayName(target);
		if (p2 != null) {
			target = p2.getUsername();
		}
		Account account = target != null ? forceLoadAccount(target) : null;
		if (account == null || (account.getIp() == null && account.getLastIp() == null)) {
			sendAddOffenceReply(moderator, target, false);
			return;
		}

		String ip = account.getIp() != null ? account.getIp() : account.getLastIp();
		offences.ipMute(target, ip, moderator, reason, expires);

		List<Account> toMute = new ArrayList<Account>();
		findAccountsByIp(toMute, ip);

		for (Account acc : toMute) {
			acc.setMuted(true);
			LoginServerChannelManager.sendReliablePacket(acc.getWorld(), LoginChannelsPacketEncoder
					.encodePlayerVarUpdate(acc.getUsername(), LoginProtocol.VAR_TYPE_PLAYERMUTE, 1).getBuffer());
			LoginServerChannelManager.sendReliablePacket(acc.getWorld(), LoginChannelsPacketEncoder
					.encodePlayerGameMessage(acc.getUsername(), "You have been muted.").getBuffer());
		}
		sendAddOffenceReply(moderator, target, true);
	}

	/**
	 * Processe's banning of specific target.
	 */
	public static void doBan(String target, String moderator, String reason, long expires) {
		target = Utils.formatPlayerNameForProtocol(target);
		Player p2 = World.getPlayerByDisplayName(target);
		if (p2 != null) {
			target = p2.getUsername();
		}
		Account account = target != null ? forceLoadAccount(target) : null;
		if (account == null || (account.getIp() == null && account.getLastIp() == null)) {
			sendAddOffenceReply(moderator, target, false);
			return;
		}

		String ip = account.getIp() != null ? account.getIp() : account.getLastIp();
		offences.ban(target, ip, moderator, reason, expires);

		Account acc = findAccount(target);
		if (acc != null)
			LoginServerChannelManager.sendReliablePacket(acc.getWorld(),
					LoginChannelsPacketEncoder.encodeLogoutRequest(acc.getUsername(), true).getBuffer());
		sendAddOffenceReply(moderator, target, true);
	}

	/**
	 * Processe's muting of specific target.
	 */
	public static void doMute(String target, String moderator, String reason, long expires) {
		target = Utils.formatPlayerNameForProtocol(target);
		Player p2 = World.getPlayerByDisplayName(target);
		if (p2 != null) {
			target = p2.getUsername();
		}
		Account account = target != null ? forceLoadAccount(target) : null;
		if (account == null || (account.getIp() == null && account.getLastIp() == null)) {
			sendAddOffenceReply(moderator, target, false);
			return;
		}

		String ip = account.getIp() != null ? account.getIp() : account.getLastIp();
		offences.mute(target, ip, moderator, reason, expires);

		Account acc = findAccount(target);
		if (acc != null) {
			acc.setMuted(true);
			LoginServerChannelManager.sendReliablePacket(acc.getWorld(), LoginChannelsPacketEncoder
					.encodePlayerVarUpdate(acc.getUsername(), LoginProtocol.VAR_TYPE_PLAYERMUTE, 1).getBuffer());
			LoginServerChannelManager.sendReliablePacket(acc.getWorld(), LoginChannelsPacketEncoder
					.encodePlayerGameMessage(acc.getUsername(), "You have been muted.").getBuffer());
		}
		sendAddOffenceReply(moderator, target, true);
	}

	/**
	 * Processe's unbanning of specific username.
	 */
	public static void doUnban(String target, String moderator) {
		target = Utils.formatPlayerNameForProtocol(target);
		Player p2 = World.getPlayerByDisplayName(target);
		if (p2 != null) {
			target = p2.getUsername();
		}
		if (target == null || offences.unbanByUser(target) <= 0)
			sendRemoveOffenceReply(moderator, target, false);
		else
			sendRemoveOffenceReply(moderator, target, true);
	}

	/**
	 * Processe's unmuting of specific username.
	 */
	public static void doUnmute(String target, String moderator) {
		target = Utils.formatPlayerNameForProtocol(target);
		Player p2 = World.getPlayerByDisplayName(target);
		if (p2 != null) {
			target = p2.getUsername();
		}
		int count = target != null ? offences.unmuteByUser(target) : 0;

		if (count > 0) {
			Account acc = findAccount(target);
			if (acc != null) {
				acc.setMuted(false);
				LoginServerChannelManager.sendReliablePacket(acc.getWorld(), LoginChannelsPacketEncoder
						.encodePlayerVarUpdate(acc.getUsername(), LoginProtocol.VAR_TYPE_PLAYERMUTE, 0).getBuffer());
				LoginServerChannelManager.sendReliablePacket(acc.getWorld(), LoginChannelsPacketEncoder
						.encodePlayerGameMessage(acc.getUsername(), "You have been unmuted.").getBuffer());
			}
		}

		sendRemoveOffenceReply(moderator, target, count > 0);
	}

	/**
	 * Find's display name by username.
	 */
	public static String getDisplayName(String username) {
		return Utils.formatPlayerNameForDisplay(username);
	}

	/**
	 * Send's reply for added offence.
	 */
	private static void sendAddOffenceReply(String moderator, String target, boolean successfull) {
		Account account = findAccount(moderator);
		if (account != null)
			LoginServerChannelManager.sendUnreliablePacket(account.getWorld(), LoginChannelsPacketEncoder
					.encodePlayerGameMessage(account.getUsername(),
							successfull ? (target + " was successfully punished.") : "Could not punish " + target)
					.getBuffer());
	}

	/**
	 * Send's reply for added offence.
	 */
	private static void sendRemoveOffenceReply(String moderator, String target, boolean successfull) {
		Account account = findAccount(moderator);
		if (account != null)
			LoginServerChannelManager.sendUnreliablePacket(account.getWorld(),
					LoginChannelsPacketEncoder.encodePlayerGameMessage(account.getUsername(), successfull
							? (target + " was successfully unbanned/unmuted.") : "Could not unban/unmute " + target)
							.getBuffer());
	}

	/**
	 * Force's loading specific account. Might return null (if account does not
	 * exist) or uninitialized ( if account is not logged in anywhere ).
	 */
	public static Account forceLoadAccount(String username) {
		Account account = findAccount(username);
		return account != null ? account : JsonFileManager.loadAccount(username);
	}

	/**
	 * Find's online account.
	 */
	public static Account findAccount(String username) {
		for (int i = 0; i < worlds.length; i++) {
			if (worlds[i] == null)
				continue;

			Account account = worlds[i].findAccount(username);
			if (account != null)
				return account;
		}
		return null;
	}

	/**
	 * Find's online accounts by ip.
	 */
	public static int findAccountsByIp(List<Account> list, String ip) {
		int total = 0;
		for (int i = 0; i < worlds.length; i++) {
			if (worlds[i] == null)
				continue;
			total += worlds[i].findAccountsByIp(list, ip);
		}
		return total;
	}

	/**
	 * Create's new unique number.
	 */
	public static long createNewUid() {
		return System.nanoTime(); // TODO change, althro nanotime should almost
		// always be unique
	}

	/**
	 * Return's total amount of game + lobby players online.
	 */
	public static int getTotalOnline() {
		return getTotalGameOnline() + getTotalLobbyOnline();
	}

	/**
	 * Return's total amount of game players online.
	 */
	public static int getTotalGameOnline() {
		int online = 0;
		for (int i = 0; i < worlds.length; i++) {
			if (worlds[i] != null)
				online += worlds[i].getGamePlayersOnline();
		}
		return online;
	}

	/**
	 * Return's total amount of players online.
	 */
	public static int getTotalLobbyOnline() {
		int online = 0;
		for (int i = 0; i < worlds.length; i++) {
			if (worlds[i] != null)
				online += worlds[i].getLobbyPlayersOnline();
		}
		return online;
	}

	/**
	 * Return's size of worlds array.
	 */
	public static int getWorldsSize() {
		return worlds.length;
	}

	/**
	 * Return's count of real worlds.
	 */
	public static int getWorldsCount() {
		int count = 0;
		for (int i = 0; i < worlds.length; i++)
			if (worlds[i] != null)
				count++;
		return count;
	}

	/**
	 * Return's specific world or null if it doesn't exist.
	 */
	public static GameWorld getWorld(int id) {
		if (id < 0 || id >= worlds.length)
			return null;
		return worlds[id];
	}

	/**
	 * Return's lock used to synchronize decoders.
	 */
	public static ReentrantLock getDecoderLock() {
		return decoderLock;
	}
}