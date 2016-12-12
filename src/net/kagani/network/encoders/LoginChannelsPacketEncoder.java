package net.kagani.network.encoders;

import net.kagani.cache.loaders.QuickChatOptionDefinition;
import net.kagani.game.player.Player;
import net.kagani.game.player.QuickChatMessage;
import net.kagani.network.LoginProtocol;
import net.kagani.stream.OutputStream;

public class LoginChannelsPacketEncoder {

	/**
	 * Size of truncated data chunks.
	 */
	private static final int TRUNCATED_DATA_CHUNK_SIZE = 500;

	public static OutputStream encodePingPong(int number) {
		OutputStream out = new OutputStream(3);
		out.writeByte(LoginProtocol.PACKET_PINGPONG);
		out.writeShort(number);
		return out;
	}

	public static OutputStream encodeConsoleMessage(String message) {
		OutputStream out = new OutputStream(message.length() + 2);
		out.writeByte(LoginProtocol.PACKET_CONSOLEMSG);
		out.writeString(message);
		return out;
	}

	public static OutputStream encodeLoginRequest(int sessionid,
			String username, String password, String ip, boolean isLobby) {
		OutputStream out = new OutputStream(username.length()
				+ password.length() + ip.length() + 8);
		out.writeByte(LoginProtocol.PACKET_LOGINREQ);
		out.writeInt(sessionid);
		out.writeString(username);
		out.writeString(password);
		out.writeString(ip);
		out.writeByte(isLobby ? 1 : 0);
		return out;
	}

	public static OutputStream encodeLoginResponse(int sessionid,
			String sessionuser, int status) {
		OutputStream out = new OutputStream(11 + sessionuser.length());
		out.writeByte(LoginProtocol.PACKET_LOGINRSP);
		out.writeInt(sessionid);
		out.writeString(sessionuser);
		out.writeByte(status);
		out.writeInt(0);
		return out;
	}

	public static OutputStream encodeLoginResponse(int sessionid,
			String sessionuser, int status, int file_length, int rights,
			boolean masterLogin, boolean donator, boolean extremeDonator,
			boolean support, boolean gfx, int messageIcon, boolean muted,
			long lastVote, String displayName, String email) {
		OutputStream out = new OutputStream(29 + sessionuser.length()
				+ (displayName != null ? displayName.length() : 0)
				+ (email != null ? email.length() : 0));
		out.writeByte(LoginProtocol.PACKET_LOGINRSP);
		out.writeInt(sessionid);
		out.writeString(sessionuser);
		out.writeByte(status);
		out.writeInt(file_length);
		out.writeByte(rights);
		out.writeByte(masterLogin ? 1 : 0);
		out.writeByte(donator ? 1 : 0);
		out.writeByte(extremeDonator ? 1 : 0);
		out.writeByte(support ? 1 : 0);
		out.writeByte(gfx ? 1 : 0);
		out.writeByte(messageIcon);
		out.writeByte(muted ? 1 : 0);
		out.writeLong(lastVote);
		if (displayName != null)
			out.writeString(displayName);
		else
			out.writeByte(0);
		if (email != null)
			out.writeString(email);
		else
			out.writeByte(0);
		return out;
	}

	public static OutputStream[] encodeLoginFileResponse(int sessionid,
			byte[] file) {
		int parts = (file.length + (TRUNCATED_DATA_CHUNK_SIZE - 1))
				/ TRUNCATED_DATA_CHUNK_SIZE;
		OutputStream[] packets = new OutputStream[parts];
		for (int i = 0; i < packets.length; i++) {
			int datasize = Math.min(TRUNCATED_DATA_CHUNK_SIZE, file.length
					- (i * TRUNCATED_DATA_CHUNK_SIZE));
			packets[i] = new OutputStream(5 + datasize);
			packets[i].writeByte(LoginProtocol.PACKET_LOGINRSPFILEPART);
			packets[i].writeInt(sessionid);
			packets[i]
					.writeBytes(file, i * TRUNCATED_DATA_CHUNK_SIZE, datasize);
		}
		return packets;
	}

	public static OutputStream encodePlayerFileTransmitInit(String username,
			int file_length) {
		OutputStream out = new OutputStream(6 + username.length());
		out.writeByte(LoginProtocol.PACKET_PLAYERFILETRANSMITINIT);
		out.writeString(username);
		out.writeInt(file_length);
		return out;
	}

	public static OutputStream[] encodePlayerFileTransmit(String username,
			byte[] file) {
		int parts = (file.length + (TRUNCATED_DATA_CHUNK_SIZE - 1))
				/ TRUNCATED_DATA_CHUNK_SIZE;
		OutputStream[] packets = new OutputStream[parts];
		for (int i = 0; i < packets.length; i++) {
			int datasize = Math.min(TRUNCATED_DATA_CHUNK_SIZE, file.length
					- (i * TRUNCATED_DATA_CHUNK_SIZE));
			packets[i] = new OutputStream(2 + username.length() + datasize);
			packets[i].writeByte(LoginProtocol.PACKET_PLAYERFILETRANSMITPART);
			packets[i].writeString(username);
			packets[i]
					.writeBytes(file, i * TRUNCATED_DATA_CHUNK_SIZE, datasize);
		}
		return packets;
	}

	public static OutputStream encodeLogout(String username) {
		OutputStream out = new OutputStream(username.length() + 2);
		out.writeByte(LoginProtocol.PACKET_LOGOUT);
		out.writeString(username);
		return out;
	}

	public static OutputStream encodeLogoutRequest(String username,
			boolean immediate) {
		OutputStream out = new OutputStream(username.length() + 3);
		out.writeByte(LoginProtocol.PACKET_LOGOUTREQ);
		out.writeString(username);
		out.writeByte(immediate ? 1 : 0);
		return out;
	}

	public static OutputStream encodeWorldShutdownRequest(int delay) {
		OutputStream out = new OutputStream(5);
		out.writeByte(LoginProtocol.PACKET_WORLDSHUTDOWNREQ);
		out.writeInt(delay);
		return out;
	}

	public static OutputStream encodePlayerVarUpdate(String username, int type,
			int var) {
		OutputStream out = new OutputStream(7 + username.length());
		out.writeByte(LoginProtocol.PACKET_UPDATEPLAYERINTVAR);
		out.writeString(username);
		out.writeByte(type);
		out.writeInt(var);
		return out;
	}

	public static OutputStream encodeAccountVarUpdate(String username,
			int type, int var) {
		OutputStream out = new OutputStream(7 + username.length());
		out.writeByte(LoginProtocol.PACKET_UPDATEACCOUNTINTVAR);
		out.writeString(username);
		out.writeByte(type);
		out.writeInt(var);
		return out;
	}

	public static OutputStream encodeAddOffence(int type, String target,
			String moderator, String reason, long expires) {
		OutputStream out = new OutputStream(13 + target.length()
				+ moderator.length() + reason.length());
		out.writeByte(LoginProtocol.PACKET_ADD_OFFENCE);
		out.writeByte(type);
		out.writeString(target);
		out.writeString(moderator);
		out.writeString(reason);
		out.writeLong(expires);
		return out;
	}

	public static OutputStream encodeRemoveOffence(int type, String target,
			String moderator) {
		OutputStream out = new OutputStream(4 + target.length()
				+ moderator.length());
		out.writeByte(LoginProtocol.PACKET_REMOVE_OFFENCE);
		out.writeByte(type);
		out.writeString(target);
		out.writeString(moderator);
		return out;
	}

	public static OutputStream encodePlayerWorldListStatusRequest(
			String username, int checksum) {
		OutputStream out = new OutputStream(6 + username.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_REQWORLDSTAT);
		out.writeString(username);
		out.writeInt(checksum);
		return out;
	}

	public static OutputStream encodePlayerWorldListStatusResponse(
			String username, int checksum, int[] status) {
		OutputStream out = new OutputStream(6 + username.length()
				+ (status.length * 4));
		out.writeByte(LoginProtocol.PACKET_PLAYER_RSPWORLDSTAT);
		out.writeString(username);
		out.writeInt(checksum);
		for (int i = 0; i < status.length; i++)
			out.writeInt(status[i]);
		return out;
	}

	public static OutputStream createUpdateFriendsPacket(String username) {
		OutputStream out = new OutputStream(2 + username.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_UPDATEFRIENDSINFO);
		out.writeString(username);
		return out;
	}

	public static void appendUpdateFriend(OutputStream packet,
			boolean isStatusUpdate, String displayName,
			String previousDisplayName, int world, int fcRank, String worldName) {
		packet.writeByte(1); // mark new block
		packet.writeByte(isStatusUpdate ? 1 : 0);
		packet.writeString(displayName);
		if (previousDisplayName != null)
			packet.writeString(previousDisplayName);
		else
			packet.writeByte(0);
		packet.writeInt(world);
		packet.writeInt(fcRank);
		if (world > 0) {
			packet.writeString(worldName);
		}
	}

	public static void finishUpdateFriendsPacket(OutputStream packet) {
		packet.writeByte(0);
	}

	public static OutputStream createUpdateIgnoresPacket(String username,
			boolean reset) {
		OutputStream out = new OutputStream(3 + username.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_UPDATEIGNORESINFO);
		out.writeString(username);
		out.writeByte(reset ? 1 : 0);
		return out;
	}

	public static void appendUpdateIgnore(OutputStream packet,
			boolean isNameUpdate, String displayName, String previousDisplayName) {
		packet.writeByte(1); // mark new block
		packet.writeByte(isNameUpdate ? 1 : 0);
		packet.writeString(displayName);
		if (previousDisplayName != null)
			packet.writeString(previousDisplayName);
		else
			packet.writeByte(0);
	}

	public static void finishUpdateIgnoresPacket(OutputStream packet) {
		packet.writeByte(0);
	}

	public static OutputStream encodePlayerGameMessage(String username,
			String message) {
		OutputStream out = new OutputStream(3 + username.length()
				+ message.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_GAMEMSG);
		out.writeString(username);
		out.writeString(message);
		return out;
	}

	public static OutputStream encodePlayerFriendIgnoreOperation(
			String username, String target, int optype) {
		OutputStream out = new OutputStream(4 + username.length()
				+ target.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_FRIENDIGNOREOP);
		out.writeString(username);
		out.writeString(target);
		out.writeByte(optype);
		return out;
	}

	public static OutputStream encodePlayerFriendIgnoreSystemMessage(
			String username, String message) {
		OutputStream out = new OutputStream(3 + username.length()
				+ message.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_FRIENDIGNORESYSMSG);
		out.writeString(username);
		out.writeString(message);
		return out;
	}

	public static OutputStream encodePlayerFriendIgnoreSendAll(String username) {
		OutputStream out = new OutputStream(2 + username.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_FRIENDIGNORESENDALL);
		out.writeString(username);
		return out;
	}

	public static OutputStream encodePlayerSendPrivateMessage(String username,
			String target, String message) {
		OutputStream out = new OutputStream(5 + username.length()
				+ target.length() + message.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_SENDPRIVATE);
		out.writeString(username);
		out.writeString(target);
		out.writeByte(0); // isqc
		out.writeString(message);
		return out;
	}

	public static OutputStream encodePlayerSendPrivateMessage(Player player,
			String target, QuickChatOptionDefinition option, long[] data) {
		OutputStream out = new OutputStream(8 + player.getUsername().length()
				+ target.length() + option.getTotalResponseSize());
		out.writeByte(LoginProtocol.PACKET_PLAYER_SENDPRIVATE);
		out.writeString(player.getUsername());
		out.writeString(target);
		out.writeByte(1); // isqc
		out.writeInt(option.id);
		QuickChatMessage.writeQuickChatData(player, out, option, data);
		return out;
	}

	public static OutputStream encodePlayerSentPrivateMessage(String username,
			String target, String message) {
		OutputStream out = new OutputStream(5 + username.length()
				+ target.length() + message.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_SENTPRIVATE);
		out.writeString(username);
		out.writeString(target);
		out.writeByte(0); // isqc
		out.writeString(message);
		return out;
	}

	public static OutputStream encodePlayerSentPrivateMessage(String username,
			String target, int qcFileId, byte[] qcData) {
		OutputStream out = new OutputStream(8 + username.length()
				+ target.length() + (qcData != null ? qcData.length : 0));
		out.writeByte(LoginProtocol.PACKET_PLAYER_SENTPRIVATE);
		out.writeString(username);
		out.writeString(target);
		out.writeByte(1); // isqc
		out.writeInt(qcFileId);
		if (qcData != null)
			out.writeBytes(qcData);
		return out;
	}

	public static OutputStream encodePlayerReceivedPrivateMessage(
			String username, String target, long messageUid, int iconId,
			String message) {
		OutputStream out = new OutputStream(17 + username.length()
				+ target.length() + message.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_RECVPRIVATE);
		out.writeString(username);
		out.writeString(target);
		out.writeLong(messageUid);
		out.writeInt(iconId);
		out.writeByte(0); // isqc
		out.writeString(message);
		return out;
	}

	public static OutputStream encodePlayerReceivedPrivateMessage(
			String username, String target, long messageUid, int iconId,
			int qcFileId, byte[] qcData) {
		OutputStream out = new OutputStream(20 + username.length()
				+ target.length() + (qcData != null ? qcData.length : 0));
		out.writeByte(LoginProtocol.PACKET_PLAYER_RECVPRIVATE);
		out.writeString(username);
		out.writeString(target);
		out.writeLong(messageUid);
		out.writeInt(iconId);
		out.writeByte(1); // isqc
		out.writeInt(qcFileId);
		if (qcData != null)
			out.writeBytes(qcData);
		return out;
	}

	public static OutputStream encodePlayerFriendsChatSystemMessage(
			String username, String message) {
		OutputStream out = new OutputStream(3 + username.length()
				+ message.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_FRIENDSCHATSYSMSG);
		out.writeString(username);
		out.writeString(message);
		return out;
	}

	public static OutputStream encodePlayerFriendsChatInformationUpdatePacket(
			String username) {
		OutputStream out = new OutputStream(2 + username.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_FRIENDSCHATUPDATEINFO);
		out.writeString(username);
		return out;
	}

	public static OutputStream encodePlayerFriendsChatInformationUpdatePacket(
			String username, OutputStream information) {
		OutputStream out = new OutputStream(2 + username.length()
				+ information.getOffset());
		out.writeByte(LoginProtocol.PACKET_PLAYER_FRIENDSCHATUPDATEINFO);
		out.writeString(username);
		out.writeBytes(information.getBuffer(), 0, information.getOffset());
		return out;
	}

	public static OutputStream createPlayerFriendsChatInformation(
			String ownerUsername, String ownerDisplayname, String name,
			int kickReq, int membersCount) {
		OutputStream out = new OutputStream(11 + ownerUsername.length()
				+ ownerDisplayname.length() + name.length());
		out.writeString(ownerUsername);
		out.writeString(ownerDisplayname);
		out.writeString(name);
		out.writeInt(kickReq);
		out.writeInt(membersCount);
		return out;
	}

	public static void appendFriendsChatMember(OutputStream stream,
			String displayName, int worldId, int rank, String worldName) {
		stream.writeByte(1); // mark new block
		stream.writeString(displayName);
		stream.writeInt(worldId);
		stream.writeInt(rank);
		stream.writeString(worldName);
	}

	public static void finishPlayerFriendsChatInformation(OutputStream stream) {
		stream.writeByte(0);
	}

	public static OutputStream encodePlayerFriendsChatMessage(String username,
			String name, String chatName, long messageUid, int iconId,
			String message) {
		OutputStream out = new OutputStream(18 + username.length()
				+ name.length() + chatName.length() + message.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_FRIENDSCHATMSG);
		out.writeString(username);
		out.writeString(name);
		out.writeString(chatName);
		out.writeLong(messageUid);
		out.writeInt(iconId);
		out.writeByte(0); // isqc
		out.writeString(message);
		return out;
	}

	public static OutputStream encodePlayerFriendsChatMessage(String username,
			String name, String chatName, long messageUid, int iconId,
			int qcFileId, byte[] qcData) {
		OutputStream out = new OutputStream(21 + username.length()
				+ name.length() + chatName.length()
				+ (qcData != null ? qcData.length : 0));
		out.writeByte(LoginProtocol.PACKET_PLAYER_FRIENDSCHATMSG);
		out.writeString(username);
		out.writeString(name);
		out.writeString(chatName);
		out.writeLong(messageUid);
		out.writeInt(iconId);
		out.writeByte(1); // isqc
		out.writeInt(qcFileId);
		if (qcData != null)
			out.writeBytes(qcData);
		return out;
	}

	public static OutputStream encodePlayerFriendsChatJoinLeaveRequest(
			String username, String target) {
		OutputStream out = new OutputStream(2 + username.length()
				+ (target != null ? (target.length() + 1) : 0));
		out.writeByte(LoginProtocol.PACKET_PLAYER_FRIENDSCHATREQJOINLEAVE);
		out.writeString(username);
		if (target != null)
			out.writeString(target);
		return out;
	}

	public static OutputStream encodePlayerFriendsChatMessageRequest(
			String username, String message) {
		OutputStream out = new OutputStream(4 + username.length()
				+ message.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_FRIENDSCHATREQMSG);
		out.writeString(username);
		out.writeByte(0); // isqc
		out.writeString(message);
		return out;
	}

	public static OutputStream encodePlayerFriendsChatMessageRequest(
			Player player, QuickChatOptionDefinition option, long[] qcData) {
		OutputStream out = new OutputStream(7 + player.getUsername().length()
				+ option.getTotalResponseSize());
		out.writeByte(LoginProtocol.PACKET_PLAYER_FRIENDSCHATREQMSG);
		out.writeString(player.getUsername());
		out.writeByte(1); // isqc
		out.writeInt(option.id);
		QuickChatMessage.writeQuickChatData(player, out, option, qcData);
		return out;
	}

	public static OutputStream encodePlayerFriendsChatKickRequest(
			String username, String target) {
		OutputStream out = new OutputStream(3 + username.length()
				+ target.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_FRIENDSCHATREQKICK);
		out.writeString(username);
		out.writeString(target);
		return out;
	}

	public static OutputStream encodePlayerFriendsChatLootshareRequest(
			String username) {
		OutputStream out = new OutputStream(2 + username.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_FRIENDSCHATREQLS);
		out.writeString(username);
		return out;
	}

	public static OutputStream encodePlayerFriendIgnoreSetRank(String username,
			String target, int rank) {
		OutputStream out = new OutputStream(7 + username.length()
				+ target.length());
		out.writeByte(LoginProtocol.PACKET_PLAYER_FRIENDIGNORESETRANK);
		out.writeString(username);
		out.writeString(target);
		out.writeInt(rank);
		return out;
	}

	public static OutputStream encodePlayerVarUpdate(String username, int type,
			String var) {
		OutputStream out = new OutputStream(4 + username.length()
				+ (var != null ? (var.length() + 1) : 0));
		out.writeByte(LoginProtocol.PACKET_UPDATEPLAYERSTRINGVAR);
		out.writeString(username);
		out.writeByte(type);
		out.writeByte(var != null ? 1 : 0);
		if (var != null)
			out.writeString(var);
		return out;
	}

	public static OutputStream encodeAccountVarUpdate(String username,
			int type, String var) {
		OutputStream out = new OutputStream(4 + username.length()
				+ (var != null ? (var.length() + 1) : 0));
		out.writeByte(LoginProtocol.PACKET_UPDATEACCOUNTSTRINGVAR);
		out.writeString(username);
		out.writeByte(type);
		out.writeByte(var != null ? 1 : 0);
		if (var != null)
			out.writeString(var);
		return out;
	}

	public static OutputStream encodeStorePurchase(String username, String item) {
		OutputStream out = new OutputStream(3 + username.length()
				+ item.length());
		out.writeByte(LoginProtocol.PACKET_STOREPURCHASE);
		out.writeString(username);
		out.writeString(item);
		return out;
	}
}