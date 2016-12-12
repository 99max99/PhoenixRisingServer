package net.kagani.login.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.kagani.Settings;
import net.kagani.login.ForumAuthManager;
import net.kagani.login.FriendsChat;
import net.kagani.login.GameWorld;
import net.kagani.network.LoginProtocol;
import net.kagani.network.LoginServerChannelManager;
import net.kagani.network.encoders.LoginChannelsPacketEncoder;
import net.kagani.utils.Utils;

public class Account implements Serializable {
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1222222222L;

	public static final int RANK_DONATOR = 0;
	public static final int RANK_EXTREME_DONATOR = 1;
	public static final int RANK_SUPPORT_TEAM = 2;
	public static final int RANK_GFX_DESIGNER = 3;

	/**
	 * World, in which this account is logged into.
	 */
	private transient GameWorld world;
	/**
	 * Current display name.
	 */
	private transient String displayName;
	/**
	 * Previous display name.
	 */
	private transient String previousDisplayName;
	/**
	 * Whether account is muted.
	 */
	private transient boolean muted;
	/**
	 * Whether account is in lobby.
	 */
	private transient boolean lobby;
	/**
	 * Ip address.
	 */
	private transient String ip;
	/**
	 * Whether account logged in using master password.
	 */
	private transient boolean masterLogin;

	/**
	 * Current file being transfered size.
	 */
	private transient int fileTransmitLength;
	/**
	 * Current file being transfered buffer.
	 */
	private transient byte[] fileTransmitBuffer;
	/**
	 * Current file being transfered position.
	 */
	private transient int fileTransmitPosition;

	/**
	 * User name of this account.
	 */
	private String username;
	/**
	 * Password (Encrypted).
	 */
	private String password;
	/**
	 * Rights of this account.
	 */
	private int rights;
	/**
	 * Contains various ranks.
	 */
	private boolean[] ranks;
	/**
	 * Contains files associated with this account.
	 */
	private Map<Integer, byte[]> files;
	/**
	 * Contains creation date of this account.
	 */
	private long creationDate;
	/**
	 * Contains last ip.
	 */
	private String lastIp;
	/**
	 * Contains list of ips this account ever been logged from.
	 */
	private List<String> ipList;
	/**
	 * Contains list of passwords this account ever had.
	 */
	private List<String> passwordList;
	/**
	 * Contains current email or null.
	 */
	private String email;
	/**
	 * Contains date of last vote.
	 */
	private long lastVote;

	/**
	 * Contains friends and ignores data.
	 */
	private FriendsIgnores friendsIgnores;
	/**
	 * Contains current friends chat.
	 */
	private transient FriendsChat friendsChat;

	/**
	 * Contains forum auth id.
	 */
	private int forumAuthId;

	public Account(String username, String password, int rights) {
		this.username = username;
		this.password = password;
		this.rights = rights;
		this.ranks = new boolean[20]; // with space reserve for the future
		this.files = new HashMap<Integer, byte[]>();
		this.creationDate = Utils.currentTimeMillis();
		this.lastIp = null;
		this.ipList = new ArrayList<String>();
		this.passwordList = new ArrayList<String>();
		this.email = null;
		this.friendsIgnores = new FriendsIgnores();
		this.passwordList.add(password);
		this.forumAuthId = -1;
	}

	public void init(GameWorld world, String displayName, String previousDisplayName, boolean muted, boolean lobby,
			boolean master, String ip) {
		this.world = world;
		this.displayName = displayName;
		this.previousDisplayName = previousDisplayName;
		this.muted = muted;
		this.lobby = lobby;
		this.masterLogin = master;
		this.ip = ip;
		if (!ipList.contains(ip))
			ipList.add(ip);

		friendsIgnores.init(this);
	}

	/**
	 * Happens on account login
	 */
	public void onLogin() {
		friendsIgnores.onLogin();
		if (!isLobby() && !isMasterLogin())
			ForumAuthManager.syncAuth(this);
	}

	/**
	 * Happens on account logout
	 */
	public void onLogout() {
		friendsIgnores.onLogout();
		if (friendsChat != null)
			friendsChat.leave(this);
	}

	public void initFileTransmit(int file_length) {
		this.fileTransmitLength = file_length;
		this.fileTransmitBuffer = new byte[file_length];
		this.fileTransmitPosition = 0;
	}

	public void resetFileTransmit() {
		this.fileTransmitLength = 0;
		this.fileTransmitBuffer = null;
		this.fileTransmitPosition = 0;
	}

	public void processTransmit(byte[] data) {
		int amt_write = Math.min(fileTransmitLength - fileTransmitPosition, data.length);
		System.arraycopy(data, 0, fileTransmitBuffer, fileTransmitPosition, amt_write);
		fileTransmitPosition += amt_write;
	}

	public boolean isFileTransmitValid() {
		return fileTransmitLength > 0 && fileTransmitBuffer != null && fileTransmitPosition <= fileTransmitLength;
	}

	public boolean isFileTransmitFinished() {
		return fileTransmitPosition == fileTransmitLength;
	}

	public byte[] getFileTransmitBuffer() {
		return fileTransmitBuffer;
	}

	public void writeFile(int id, byte[] data) {
		files.put(id, data);
	}

	public void deleteFile(int id) {
		files.remove(id);
	}

	public byte[] getFile(int id) {
		return files.get(id);
	}

	public boolean hasRank(int rankid) {
		if (rankid < 0 || rankid >= ranks.length)
			return false;
		return ranks[rankid];
	}

	public void setRank(int rankid, boolean hasrank) {
		if (rankid < 0 || rankid >= ranks.length)
			return;
		ranks[rankid] = hasrank;
	}

	public void refactoreRights(int rights) {
		this.rights = rights;
	}

	public int getMessageIcon() {
		for (int names = 0; names < Settings.SERVER_ADMINISTRATORS.length; names++) {
			if (getUsername().equalsIgnoreCase(Settings.SERVER_ADMINISTRATORS[names])) {
				refactoreRights(2);
			}
		}

		for (int names = 0; names < Settings.SERVER_MODERATORS.length; names++) {
			if (getUsername().equalsIgnoreCase(Settings.SERVER_MODERATORS[names])) {
				refactoreRights(1);
			}
		}

		return getRights() == 2 || getRights() == 1 ? getRights()
				: hasRank(RANK_SUPPORT_TEAM) ? 12
						: hasRank(RANK_GFX_DESIGNER) ? 9
								: hasRank(RANK_EXTREME_DONATOR) ? 11 : hasRank(RANK_DONATOR) ? 8 : getRights();
	}

	public void setPassword(String newPassword) {
		password = newPassword;
		if (!passwordList.contains(newPassword))
			passwordList.add(newPassword);
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setPreviousDisplayName(String previousDisplayName) {
		this.previousDisplayName = previousDisplayName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void updateLastIp() {
		this.lastIp = ip;
	}

	public void updateLastVote() {
		this.lastVote = Utils.currentTimeMillis();
	}

	public void syncRanksFromForumGroups(String groupString) {
		ArrayList<Integer> groups = new ArrayList<Integer>();
		String[] spl = groupString.split("\\@");
		groups.add(Integer.parseInt(spl[0])); // primary
		if (spl.length > 1) {
			String[] secondary = spl[1].split("\\,"); // secondary
			for (String sec : secondary)
				groups.add(Integer.parseInt(sec));
		}
		int[] groupIDS = new int[groups.size()];
		int write = 0;
		for (Integer group : groups)
			groupIDS[write++] = group.intValue();
		syncRanksFromForumGroups(groupIDS);
	}

	public void syncRanksFromForumGroups(int[] groupIDS) {
		int prevIcon = getMessageIcon();
		int prevRights = rights;
		boolean[] prevRanks = new boolean[4];
		for (int i = 0; i < 4; i++)
			prevRanks[i] = ranks[i];

		refactoreRights(0);
		for (int i = 0; i < 4; i++)
			ranks[i] = false;

		/*
		 * Administrator - 5, 6, 51 Global modetator - 7 Game moderator - 25
		 * Graphics designer - 10 Support member - 52 Donator - 53 Extreme
		 * donator - 54
		 */
		for (int forumGroupID : groupIDS) {
			if (forumGroupID == 5 || forumGroupID == 6 || forumGroupID == 51) {
				refactoreRights(2);
			} else if (forumGroupID == 7 || forumGroupID == 25) {
				refactoreRights(1);
			} else if (forumGroupID == 10) {
				setRank(RANK_GFX_DESIGNER, true);
			} else if (forumGroupID == 52) {
				setRank(RANK_SUPPORT_TEAM, true);
			} else if (forumGroupID == 53 || forumGroupID == 15) {
				setRank(RANK_DONATOR, true);
			} else if (forumGroupID == 54 || forumGroupID == 14) {
				setRank(RANK_EXTREME_DONATOR, true);
			}
		}

		if (rights >= 1)
			setRank(RANK_DONATOR, true);
		if (rights >= 2)
			setRank(RANK_EXTREME_DONATOR, true);

		if (prevRights != rights)
			LoginServerChannelManager.sendReliablePacket(world, LoginChannelsPacketEncoder
					.encodePlayerVarUpdate(username, LoginProtocol.VAR_TYPE_RIGHTS, rights).getBuffer());

		for (int i = 0; i < 4; i++) {
			if (prevRanks[i] != ranks[i])
				LoginServerChannelManager.sendReliablePacket(world, LoginChannelsPacketEncoder
						.encodePlayerVarUpdate(username, LoginProtocol.VAR_TYPE_RANKS, i + (ranks[i] ? (1 << 16) : 0))
						.getBuffer());
		}

		if (prevIcon != getMessageIcon()) {
			LoginServerChannelManager.sendReliablePacket(world, LoginChannelsPacketEncoder
					.encodePlayerVarUpdate(username, LoginProtocol.VAR_TYPE_MESSAGEICON, getMessageIcon()).getBuffer());
		}
	}

	public GameWorld getWorld() {
		return world;
	}

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	public boolean isLobby() {
		return lobby;
	}

	public String getIp() {
		return ip;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public int getRights() {
		return rights;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getPreviousDisplayName() {
		return previousDisplayName;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public String getLastIp() {
		return lastIp;
	}

	public List<String> getIpList() {
		return ipList;
	}

	public List<String> getPasswordList() {
		return passwordList;
	}

	public FriendsIgnores getFriendsIgnores() {
		return friendsIgnores;
	}

	public FriendsChat getFriendsChat() {
		return friendsChat;
	}

	public void setFriendsChat(FriendsChat friendsChat) {
		this.friendsChat = friendsChat;
	}

	public int getForumAuthId() {
		return forumAuthId;
	}

	public void setForumAuthId(int forumAuthId) {
		this.forumAuthId = forumAuthId;
	}

	public String getEmail() {
		return email;
	}

	public long getLastVote() {
		return lastVote;
	}

	public boolean isMasterLogin() {
		return masterLogin;
	}
}