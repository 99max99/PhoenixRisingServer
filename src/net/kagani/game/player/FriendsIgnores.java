package net.kagani.game.player;

import java.util.ArrayList;
import java.util.List;

import net.kagani.cache.loaders.QuickChatOptionDefinition;
import net.kagani.game.player.content.FriendsChat;
import net.kagani.network.LoginClientChannelManager;
import net.kagani.network.LoginProtocol;
import net.kagani.network.decoders.WorldPacketsDecoder;
import net.kagani.network.encoders.LoginChannelsPacketEncoder;
import net.kagani.stream.OutputStream;

public class FriendsIgnores {

	public static final int PM_STATUS_ONLINE = 0;
	public static final int PM_STATUS_FRIENDSONLY = 1;
	public static final int PM_STATUS_OFFLINE = 2;

	/**
	 * Our player instance.
	 */
	private Player player;
	/**
	 * Our pm status.
	 */
	private int pmStatus;
	/**
	 * Friends chat name.
	 */
	private String fcName;
	/**
	 * Friends chat join requirement.
	 */
	private int fcJoinReq;
	/**
	 * Friends chat talk requirement.
	 */
	private int fcTalkReq;
	/**
	 * Friends chat kick requirement.
	 */
	private int fcKickReq;
	/**
	 * Friends chat lootshare requirement.
	 */
	private int fcLootshareReq;
	/**
	 * Friends chat coinshare settings.
	 */
	private boolean fcCoinshare;
	/**
	 * Contains list of friends.
	 */
	private List<String> friends;
	/**
	 * Contains list of ignores.
	 */
	private List<String> ignores;
	/**
	 * Current friends update packet.
	 */
	private OutputStream friendsUpdatePacket;
	/**
	 * Current ignores update packet.
	 */
	private OutputStream ignoresUpdatePacket;
	/**
	 * Amount of ignores written on update.
	 */
	private int ignoresWritten;

	public FriendsIgnores(Player player) {
		this.player = player;
		this.friends = new ArrayList<String>();
		this.ignores = new ArrayList<String>();
	}

	/**
	 * Initialize's by requesting our data.
	 */
	public void initialize() {
		LoginClientChannelManager.sendReliablePacket(LoginChannelsPacketEncoder
				.encodePlayerFriendIgnoreSendAll(player.getUsername())
				.getBuffer());
	}

	/**
	 * Set's our pm status.
	 */
	public void setPmStatus(int pmStatus, boolean updateAccount) {
		this.pmStatus = pmStatus;
		player.getPackets().sendPmStatus();
		if (updateAccount)
			LoginClientChannelManager
					.sendReliablePacket(LoginChannelsPacketEncoder
							.encodeAccountVarUpdate(player.getUsername(),
									LoginProtocol.VAR_TYPE_PMSTATUS, pmStatus)
							.getBuffer());
	}

	/**
	 * Set's our friends chat name.
	 */
	public void setFcName(String newName, boolean updateAccount) {
		fcName = newName;
		if (updateAccount)
			LoginClientChannelManager
					.sendReliablePacket(LoginChannelsPacketEncoder
							.encodeAccountVarUpdate(player.getUsername(),
									LoginProtocol.VAR_TYPE_FCNAME, fcName)
							.getBuffer());
		if (!player.isLobby()
				&& player.getInterfaceManager().containsInterface(1108))
			refreshChatName();
	}

	public void openInputFriend(boolean remove) {
		player.getInterfaceManager().sendInputTextInterface();
		player.getPackets().sendExecuteScript(remove ? 104 : 103);
	}

	public void openInputIgnore(boolean remove) {
		player.getInterfaceManager().sendInputTextInterface();
		player.getPackets().sendExecuteScript(remove ? 1419 : 105);
	}

	public void sendEditNote() {
		// send dialogue inter 451
		// sendExecuteScript([9206, 29556743, 29556742, 29556758, 0, 0]);
	}

	/**
	 * Set's our friends chat join requirement.
	 */
	public void setFcJoinReq(int newReq, boolean updateAccount) {
		fcJoinReq = newReq;
		if (updateAccount)
			LoginClientChannelManager
					.sendReliablePacket(LoginChannelsPacketEncoder
							.encodeAccountVarUpdate(player.getUsername(),
									LoginProtocol.VAR_TYPE_FCJOINREQ, fcJoinReq)
							.getBuffer());
		if (!player.isLobby()
				&& player.getInterfaceManager().containsInterface(1108))
			refreshWhoCanEnterChat();
	}

	/**
	 * Set's our friends chat talk requirement.
	 */
	public void setFcTalkReq(int newReq, boolean updateAccount) {
		fcTalkReq = newReq;
		if (updateAccount)
			LoginClientChannelManager
					.sendReliablePacket(LoginChannelsPacketEncoder
							.encodeAccountVarUpdate(player.getUsername(),
									LoginProtocol.VAR_TYPE_FCTALKREQ, fcTalkReq)
							.getBuffer());
		if (!player.isLobby()
				&& player.getInterfaceManager().containsInterface(1108))
			refreshWhoCanTalkOnChat();
	}

	/**
	 * Set's our friends chat kick requirement.
	 */
	public void setFcKickReq(int newReq, boolean updateAccount) {
		fcKickReq = newReq;
		if (updateAccount)
			LoginClientChannelManager
					.sendReliablePacket(LoginChannelsPacketEncoder
							.encodeAccountVarUpdate(player.getUsername(),
									LoginProtocol.VAR_TYPE_FCKICKREQ, fcKickReq)
							.getBuffer());
		if (!player.isLobby()
				&& player.getInterfaceManager().containsInterface(1108))
			refreshWhoCanKickOnChat();
	}

	/**
	 * Set's our friends chat loot share requirement.
	 */
	public void setFcLootShareReq(int newReq, boolean updateAccount) {
		fcLootshareReq = newReq;
		if (updateAccount)
			LoginClientChannelManager
					.sendReliablePacket(LoginChannelsPacketEncoder
							.encodeAccountVarUpdate(player.getUsername(),
									LoginProtocol.VAR_TYPE_FCLSREQ,
									fcLootshareReq).getBuffer());
		if (!player.isLobby()
				&& player.getInterfaceManager().containsInterface(1108))
			refreshWhoCanShareloot();
	}

	/**
	 * Set's our friends chat loot share requirement.
	 */
	public void setFcCoinshare(boolean coinshare, boolean updateAccount) {
		fcCoinshare = coinshare;
		if (updateAccount)
			LoginClientChannelManager
					.sendReliablePacket(LoginChannelsPacketEncoder
							.encodeAccountVarUpdate(player.getUsername(),
									LoginProtocol.VAR_TYPE_FCCSSTATE,
									fcCoinshare ? 1 : 0).getBuffer());
	}

	/**
	 * Send's private message.
	 */
	public void sendPrivateMessage(String displayName, String message) {
		LoginClientChannelManager.sendReliablePacket(LoginChannelsPacketEncoder
				.encodePlayerSendPrivateMessage(player.getUsername(),
						displayName, message).getBuffer());
	}

	/**
	 * Send's private message.
	 */
	public void sendPrivateMessage(String displayName,
			QuickChatOptionDefinition option, long[] qcData) {
		LoginClientChannelManager.sendReliablePacket(LoginChannelsPacketEncoder
				.encodePlayerSendPrivateMessage(player, displayName, option,
						qcData).getBuffer());
	}

	/**
	 * Add's new friend.
	 */
	public void addFriend(String displayName) {
		LoginClientChannelManager.sendReliablePacket(LoginChannelsPacketEncoder
				.encodePlayerFriendIgnoreOperation(player.getUsername(),
						displayName, LoginProtocol.FRIENDIGNORE_OP_FADD)
				.getBuffer());
	}

	/**
	 * Remove's friend.
	 */
	public void removeFriend(String displayName) {
		friends.remove(displayName);

		LoginClientChannelManager.sendReliablePacket(LoginChannelsPacketEncoder
				.encodePlayerFriendIgnoreOperation(player.getUsername(),
						displayName, LoginProtocol.FRIENDIGNORE_OP_FREMOVE)
				.getBuffer());
	}

	/**
	 * Add's new ignore.
	 */
	public void addIgnore(String displayName, boolean temp) {
		LoginClientChannelManager.sendReliablePacket(LoginChannelsPacketEncoder
				.encodePlayerFriendIgnoreOperation(
						player.getUsername(),
						displayName,
						temp ? LoginProtocol.FRIENDIGNORE_OP_IADDTMP
								: LoginProtocol.FRIENDIGNORE_OP_IADD)
				.getBuffer());
	}

	/**
	 * Remove's ignore.
	 */
	public void removeIgnore(String displayName) {
		ignores.remove(displayName);

		LoginClientChannelManager.sendReliablePacket(LoginChannelsPacketEncoder
				.encodePlayerFriendIgnoreOperation(player.getUsername(),
						displayName, LoginProtocol.FRIENDIGNORE_OP_IREMOVE)
				.getBuffer());
	}

	/**
	 * Change's rank.
	 */
	public void changeRank(String displayName, int newRank) {
		LoginClientChannelManager.sendReliablePacket(LoginChannelsPacketEncoder
				.encodePlayerFriendIgnoreSetRank(player.getUsername(),
						displayName, newRank).getBuffer());
	}

	/**
	 * Whether we have a friend with such display name.
	 */
	public boolean isFriend(String displayName) {
		return friends.contains(displayName);
	}

	/**
	 * Whether we have an ignore with such display name.
	 */
	public boolean isIgnore(String displayName) {
		return ignores.contains(displayName);
	}

	/**
	 * Send's system message.
	 */
	public void fiSystemMessage(String message) {
		player.getPackets().sendMessage(4, message, null);
	}

	/**
	 * Send's system message.
	 */
	public void fcSystemMessage(String message) {
		player.getPackets().sendMessage(11, message, null);
	}

	/**
	 * Start's friends update packet.
	 */
	public void beginFriendsUpdate() {
		friendsUpdatePacket = player.getPackets().startFriendsPacket();
	}

	/**
	 * Handle's friend update request.
	 */
	public void updateFriend(boolean isStatusUpdate, String displayName,
			String previousDisplayName, int world, int fcRank, String worldName) {
		if (isStatusUpdate && !isFriend(displayName))
			return;

		if (!isStatusUpdate && !isFriend(displayName)) {
			if (previousDisplayName != null)
				friends.remove(previousDisplayName);
			friends.add(displayName);
		}

		player.getPackets().appendFriend(friendsUpdatePacket, isStatusUpdate,
				displayName, previousDisplayName, world, fcRank, worldName);
	}

	/**
	 * End's friends update packet.
	 */
	public void endFriendsUpdate() {
		player.getPackets().endFriendsPacket(friendsUpdatePacket);
		friendsUpdatePacket = null;
	}

	/**
	 * Start's ignores update packet.
	 */
	public void beginIgnoresUpdate(boolean reset) {
		if (reset) {
			ignores.clear();
			ignoresWritten = 0;
			ignoresUpdatePacket = player.getPackets().startIgnoresPacket();
		}
	}

	/**
	 * Handle's ignore update request.
	 */
	public void updateIgnore(boolean isNameUpdate, String displayName,
			String previousDisplayName) {
		if (!isNameUpdate && isIgnore(displayName))
			return;
		else if (isNameUpdate && previousDisplayName == null)
			return;
		else if (isNameUpdate && previousDisplayName != null
				&& isIgnore(previousDisplayName))
			return;

		if (isNameUpdate) {
			ignores.remove(displayName);
			ignores.add(previousDisplayName);
		} else {
			ignores.add(displayName);
		}

		if (ignoresUpdatePacket != null) {
			player.getPackets().appendIgnore(ignoresUpdatePacket, isNameUpdate,
					displayName, previousDisplayName);
		} else {
			player.getPackets().sendPlainIgnore(isNameUpdate, displayName,
					previousDisplayName);
		}

		ignoresWritten++;

	}

	/**
	 * End's ignores update packet.
	 */
	public void endIgnoresUpdate() {
		if (ignoresUpdatePacket != null) {
			player.getPackets().endIgnoresPacket(ignoresUpdatePacket);
			ignoresUpdatePacket = null;
		}
	}

	/**
	 * Open's friends chat setup interface.
	 */
	public void openFriendChatSetup() {
		player.getInterfaceManager().sendCentralInterface(1108);
		refreshChatName();
		refreshWhoCanEnterChat();
		refreshWhoCanTalkOnChat();
		refreshWhoCanKickOnChat();
		refreshWhoCanShareloot();
		player.getPackets().sendHideIComponent(1108, 49, true);
		player.getPackets().sendHideIComponent(1108, 63, true);
		player.getPackets().sendHideIComponent(1108, 77, true);
		player.getPackets().sendHideIComponent(1108, 91, true);
	}

	public void unlockFriendsIgnore(boolean menu) {
		player.getPackets().sendIComponentSettings(menu ? 1441 : 550,
				menu ? 49 : 7, 0, 500, 510);
		player.getPackets().sendIComponentSettings(menu ? 1441 : 550,
				menu ? 61 : 57, 0, 500, 6);
	}

	public void handleFriendListButtons(int interfaceId, int componentId) {
		if ((interfaceId == 1441 && componentId == 28)
				|| (interfaceId == 550 && componentId == 25)
				|| (interfaceId == 550 && componentId == 41)
				|| (interfaceId == 235 && componentId == 16))
			openInputFriend(false);
		else if ((interfaceId == 1441 && componentId == 22)
				|| (interfaceId == 550 && componentId == 33)
				|| (interfaceId == 235 && componentId == 10))
			openInputFriend(true);
		else if ((interfaceId == 1441 && componentId == 10)
				|| (interfaceId == 550 && componentId == 67))
			openInputIgnore(false);
		else if ((interfaceId == 1441 && componentId == 4)
				|| (interfaceId == 550 && componentId == 75))
			openInputIgnore(true);
		else if ((interfaceId == 1441 && componentId == 78)
				|| (interfaceId == 550 && componentId == 43)) {
			// TODO recruit a friend. opens website
		} else if (interfaceId == 550 && componentId == 16) {
			player.getInterfaceManager().sendExpandOptionsInterface(235,
					interfaceId, componentId, 64, 40);
		}
	}

	public void openInputFriendChat() {
		player.getInterfaceManager().sendInputTextInterface();
		player.getPackets().sendExecuteScript(8537);
		// sendExecuteScript([194, 1]); forces last name entered(no need its
		// auto)
		// sendCSVarString(2508, fcname) sets last name entered(no need its
		// auto)
	}

	public void openKickFriendChat() {
		player.getInterfaceManager().sendInputTextInterface();
		player.getPackets().sendExecuteScript(2688);
	}

	/**
	 * Handle's interface clicks.
	 */
	public void handleFriendChatButtons(int interfaceId, int componentId,
			int packetId) {
		if (interfaceId == 1427 || interfaceId == 1109 || interfaceId == 1472
				|| interfaceId == 1468) {
			if ((interfaceId == 1427 && componentId == 10)
					|| (interfaceId == 1109 && componentId == 10)
					|| (interfaceId == 1472 && componentId == 227)
					|| (interfaceId == 1468 && componentId == 2)) {
				if (player.getCurrentFriendsChat() != null) {
					FriendsChat.requestLeave(player);
					return;
				}
				openInputFriendChat();
			} else if ((interfaceId == 1427 && componentId == 35)
					|| (interfaceId == 1109 && componentId == 38)) {
				if (player.getCurrentFriendsChat() == null) {
					player.getPackets()
							.sendGameMessage(
									"You need to be in a Friends Chat channel to activate LootShare.");
					player.refreshLootShare();
					return;
				}
				player.getCurrentFriendsChat().toogleLootshare(player);
			} else if ((interfaceId == 1427 && componentId == 4)
					|| (interfaceId == 1109 && componentId == 4)) {
				if (player.getInterfaceManager().containsScreenInterface()) {
					player.getPackets()
							.sendGameMessage(
									"Please close the interface you have opened before using Friends Chat setup.");
					return;
				}
				player.stopAll();
				openFriendChatSetup();
			} else if ((interfaceId == 1427 && componentId == 37)
					|| (interfaceId == 1109 && componentId == 40)) {
				if (player.getCurrentFriendsChat() == null)
					return;
				openKickFriendChat();
			}
		} else if (interfaceId == 1108) {
			if (componentId == 1) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					player.getPackets().sendInputNameScript(
							"Enter chat prefix:");
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					setFcName(null, true);
				}
			} else if (componentId == 2) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					setFcJoinReq(-1, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					setFcJoinReq(0, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					setFcJoinReq(1, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					setFcJoinReq(2, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)
					setFcJoinReq(3, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
					setFcJoinReq(4, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON6_PACKET)
					setFcJoinReq(5, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON7_PACKET)
					setFcJoinReq(6, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET)
					setFcJoinReq(7, true);
			} else if (componentId == 3) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					setFcTalkReq(-1, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					setFcTalkReq(0, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					setFcTalkReq(1, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					setFcTalkReq(2, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)
					setFcTalkReq(3, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
					setFcTalkReq(4, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON6_PACKET)
					setFcTalkReq(5, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON7_PACKET)
					setFcTalkReq(6, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET)
					setFcTalkReq(7, true);
			} else if (componentId == 4) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					setFcKickReq(-1, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					setFcKickReq(0, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					setFcKickReq(1, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					setFcKickReq(2, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)
					setFcKickReq(3, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
					setFcKickReq(4, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON6_PACKET)
					setFcKickReq(5, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON7_PACKET)
					setFcKickReq(6, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET)
					setFcKickReq(7, true);
			} else if (componentId == 5) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					setFcLootShareReq(7, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					setFcLootShareReq(0, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					setFcLootShareReq(1, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					setFcLootShareReq(2, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)
					setFcLootShareReq(3, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
					setFcLootShareReq(4, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON6_PACKET)
					setFcLootShareReq(5, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON7_PACKET)
					setFcLootShareReq(6, true);
			}
		}
	}

	/**
	 * Set's our chat prefix.
	 */
	public void setChatPrefix(String name) {
		if (name.length() < 1 || name.length() > 20)
			return;
		setFcName(name, true);
	}

	/**
	 * Refreshes our chat name
	 */
	public void refreshChatName() {
		player.getPackets().sendIComponentText(1108, 1,
				fcName == null ? "Chat disabled" : fcName);
	}

	/**
	 * Refreshes enter requirement
	 */
	public void refreshWhoCanEnterChat() {
		String text;
		if (fcJoinReq == 0)
			text = "Any friends";
		else if (fcJoinReq == 1)
			text = "Recruit+";
		else if (fcJoinReq == 2)
			text = "Corporal+";
		else if (fcJoinReq == 3)
			text = "Sergeant+";
		else if (fcJoinReq == 4)
			text = "Lieutenant+";
		else if (fcJoinReq == 5)
			text = "Captain+";
		else if (fcJoinReq == 6)
			text = "General+";
		else if (fcJoinReq == 7)
			text = "Only Me";
		else
			text = "Anyone";
		player.getPackets().sendIComponentText(1108, 2, text);
	}

	/**
	 * Refreshes talk requirement
	 */
	public void refreshWhoCanTalkOnChat() {
		String text;
		if (fcTalkReq == 0)
			text = "Any friends";
		else if (fcTalkReq == 1)
			text = "Recruit+";
		else if (fcTalkReq == 2)
			text = "Corporal+";
		else if (fcTalkReq == 3)
			text = "Sergeant+";
		else if (fcTalkReq == 4)
			text = "Lieutenant+";
		else if (fcTalkReq == 5)
			text = "Captain+";
		else if (fcTalkReq == 6)
			text = "General+";
		else if (fcTalkReq == 7)
			text = "Only Me";
		else
			text = "Anyone";
		player.getPackets().sendIComponentText(1108, 3, text);
	}

	/**
	 * Refreshes kick requirement
	 */
	public void refreshWhoCanKickOnChat() {
		String text;
		if (fcKickReq == 0)
			text = "Any friends";
		else if (fcKickReq == 1)
			text = "Recruit+";
		else if (fcKickReq == 2)
			text = "Corporal+";
		else if (fcKickReq == 3)
			text = "Sergeant+";
		else if (fcKickReq == 4)
			text = "Lieutenant+";
		else if (fcKickReq == 5)
			text = "Captain+";
		else if (fcKickReq == 6)
			text = "General+";
		else if (fcKickReq == 7)
			text = "Only Me";
		else
			text = "Anyone";
		player.getPackets().sendIComponentText(1108, 4, text);
	}

	/**
	 * Refreshes ls requirement
	 */
	public void refreshWhoCanShareloot() {
		String text;
		if (fcLootshareReq == 0)
			text = "Any friends";
		else if (fcLootshareReq == 1)
			text = "Recruit+";
		else if (fcLootshareReq == 2)
			text = "Corporal+";
		else if (fcLootshareReq == 3)
			text = "Sergeant+";
		else if (fcLootshareReq == 4)
			text = "Lieutenant+";
		else if (fcLootshareReq == 5)
			text = "Captain+";
		else if (fcLootshareReq == 6)
			text = "General+";
		else
			text = "No-one";
		player.getPackets().sendIComponentText(1108, 5, text);
	}

	public int getPmStatus() {
		return pmStatus;
	}

	public String getFcName() {
		return fcName;
	}

	public int getFcJoinReq() {
		return fcJoinReq;
	}

	public int getFcTalkReq() {
		return fcTalkReq;
	}

	public int getFcKickReq() {
		return fcKickReq;
	}

	public int getFcLootshareReq() {
		return fcLootshareReq;
	}

	public boolean isFcCoinshare() {
		return fcCoinshare;
	}
}