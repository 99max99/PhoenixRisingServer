package net.kagani.network.decoders;

import net.kagani.Engine;
import net.kagani.executor.PlayerHandlerThread;
import net.kagani.game.World;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.FriendsChat;
import net.kagani.network.LoginClientChannelManager;
import net.kagani.network.LoginProtocol;
import net.kagani.network.encoders.LoginChannelsPacketEncoder;
import net.kagani.stream.InputStream;
import net.kagani.stream.OutputStream;
import net.kagani.utils.Censor;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class LoginClientPacketsDecoder {

	public static int number;

	public static void decodeIncomingPacket(InputStream stream) {
		int opcode = stream.readUnsignedByte();
		// if (Settings.DEBUG)
		// Logger.log("Login client", "Received packet:" +
		// LoginProtocol.getOpcodeName(opcode));
		switch (opcode) {
		case LoginProtocol.PACKET_PINGPONG:
			int num = stream.readUnsignedShort();
			Logger.log(LoginClientPacketsDecoder.class, "Pong! (" + num + ")");
			LoginClientChannelManager
					.sendReliablePacket(LoginChannelsPacketEncoder
							.encodePingPong(number++).getBuffer());
			break;
		case LoginProtocol.PACKET_CONSOLEMSG:
			String msg = stream.readString();
			Logger.log("Login server", msg);
			break;
		case LoginProtocol.PACKET_LOGINRSP:
			int sessionid = stream.readInt();
			String sessionuser = stream.readString();
			int status = stream.readUnsignedByte();
			int file_length = stream.readInt();
			if (status == 2) {
				PlayerHandlerThread.handleLoginResponse(sessionid, sessionuser,
						status, file_length, stream.readUnsignedByte(),
						stream.readUnsignedByte() == 1,
						stream.readUnsignedByte() == 1,
						stream.readUnsignedByte() == 1,
						stream.readUnsignedByte() == 1,
						stream.readUnsignedByte() == 1,
						stream.readUnsignedByte(),
						stream.readUnsignedByte() == 1, stream.readLong(),
						stream.readNullString(), stream.readNullString());
			} else {
				PlayerHandlerThread.handleLoginResponse(sessionid, sessionuser,
						status, file_length, 0, false, false, false, false,
						false, 0, false, 0, null, null);
			}
			break;
		case LoginProtocol.PACKET_LOGOUTREQ:
			String username = stream.readString();
			boolean immediate = stream.readByte() == 1;
			Player player = World.getPlayer(username);
			if (player == null)
				player = World.getLobbyPlayer(username);
			if (player != null) {
				player.disconnect(immediate, false);
			}
			break;
		case LoginProtocol.PACKET_LOGINRSPFILEPART:
			sessionid = stream.readInt();
			byte[] data = new byte[stream.getRemaining()];
			stream.readBytes(data);
			PlayerHandlerThread.handleLoginFileResponse(sessionid, data);
			break;
		case LoginProtocol.PACKET_WORLDSHUTDOWNREQ:
			int delay = stream.readInt();
			if (delay > 0)
				Engine.shutdown(delay, false, true);
			else
				Engine.initShutdown(true);
			break;
		case LoginProtocol.PACKET_UPDATEPLAYERINTVAR:
			username = stream.readString();
			int type = stream.readUnsignedByte();
			int var = stream.readInt();
			player = World.getPlayer(username);
			if (player == null)
				player = World.getLobbyPlayer(username);
			if (player != null) {
				switch (type) {
				case LoginProtocol.VAR_TYPE_PLAYERMUTE:
					player.setMuted(var == 1);
					break;
				case LoginProtocol.VAR_TYPE_PMSTATUS:
					player.getFriendsIgnores().setPmStatus(var, false);
					break;
				case LoginProtocol.VAR_TYPE_LOOTSHARE:
					if (var == 1)
						player.enableLootShare();
					else
						player.disableLootShare();
					break;
				case LoginProtocol.VAR_TYPE_FCJOINREQ:
					player.getFriendsIgnores().setFcJoinReq(var, false);
					break;
				case LoginProtocol.VAR_TYPE_FCTALKREQ:
					player.getFriendsIgnores().setFcTalkReq(var, false);
					break;
				case LoginProtocol.VAR_TYPE_FCKICKREQ:
					player.getFriendsIgnores().setFcKickReq(var, false);
					break;
				case LoginProtocol.VAR_TYPE_FCLSREQ:
					player.getFriendsIgnores().setFcLootShareReq(var, false);
					break;
				case LoginProtocol.VAR_TYPE_FCCSSTATE:
					player.getFriendsIgnores().setFcCoinshare(var == 1, false);
					break;
				case LoginProtocol.VAR_TYPE_RIGHTS:
					player.setRights(var);
					break;
				/*
				 * case LoginProtocol.VAR_TYPE_RANKS: int id = var & 0xFFFF;
				 * boolean value = (var >> 16) == 1; if (id == 0)
				 * player.setDonator(value); else if (id == 1)
				 * player.setExtremeDonator(value); else if (id == 2)
				 * player.setSupporter(value); else if (id == 3)
				 * player.setGraphicDesigner(value); break;
				 */
				case LoginProtocol.VAR_TYPE_MESSAGEICON:
					player.setMessageIcon(var);
					break;
				default:
					Logger.log(LoginClientPacketsDecoder.class,
							"Unknown player var type!");
					break;
				}
			}
			break;
		case LoginProtocol.PACKET_PLAYER_RSPWORLDSTAT:
			username = stream.readString();
			int checksum = stream.readInt();
			int[] stat = new int[stream.getRemaining() / 4];
			for (int i = 0; i < stat.length; i++)
				stat[i] = stream.readInt();
			player = World.getPlayer(username);
			if (player == null)
				player = World.getLobbyPlayer(username);
			if (player != null) {
				player.getPackets().sendWorldList(checksum, stat);
			}
			break;
		case LoginProtocol.PACKET_PLAYER_UPDATEFRIENDSINFO:
			username = stream.readString();
			player = World.getPlayer(username);
			if (player == null)
				player = World.getLobbyPlayer(username);
			if (player != null && player.hasStarted()) {
				player.getFriendsIgnores().beginFriendsUpdate();
				while (stream.readByte() == 1) {
					boolean isStatusUpdate = stream.readByte() == 1;
					String displayName = stream.readString();
					String previousDisplayName = stream.readNullString();
					int world = stream.readInt();
					int fcRank = stream.readInt();
					String worldName = world > 0 ? stream.readString() : null;
					player.getFriendsIgnores().updateFriend(isStatusUpdate,
							displayName, previousDisplayName, world, fcRank,
							worldName);
				}
				player.getFriendsIgnores().endFriendsUpdate();
			}
			break;
		case LoginProtocol.PACKET_PLAYER_UPDATEIGNORESINFO:
			username = stream.readString();
			boolean reset = stream.readByte() == 1;
			player = World.getPlayer(username);
			if (player == null)
				player = World.getLobbyPlayer(username);
			if (player != null && player.hasStarted()) {
				player.getFriendsIgnores().beginIgnoresUpdate(reset);
				while (stream.readByte() == 1) {
					boolean isNameUpdate = stream.readByte() == 1;
					String displayName = stream.readString();
					String previousDisplayName = stream.readNullString();
					player.getFriendsIgnores().updateIgnore(isNameUpdate,
							displayName, previousDisplayName);
				}
				player.getFriendsIgnores().endIgnoresUpdate();
			}
			break;
		case LoginProtocol.PACKET_PLAYER_GAMEMSG:
			username = stream.readString();
			String message = stream.readString();
			player = World.getPlayer(username);
			if (player == null)
				player = World.getLobbyPlayer(username);
			if (player != null && player.hasStarted()) {
				player.getPackets().sendGameMessage(message);
			}
			break;
		case LoginProtocol.PACKET_PLAYER_FRIENDIGNORESYSMSG:
			username = stream.readString();
			message = stream.readString();
			player = World.getPlayer(username);
			if (player == null)
				player = World.getLobbyPlayer(username);
			if (player != null && player.hasStarted()) {
				player.getFriendsIgnores().fiSystemMessage(message);
			}
			break;
		case LoginProtocol.PACKET_PLAYER_SENTPRIVATE:
			username = stream.readString();
			String name = stream.readString();
			boolean isQc = stream.readByte() == 1;
			if (isQc) {
				int qcFileId = stream.readInt();
				byte[] qcData = stream.getRemaining() > 0 ? new byte[stream
						.getRemaining()] : null;
				if (qcData != null)
					stream.readBytes(qcData);
				player = World.getPlayer(username);
				if (player == null)
					player = World.getLobbyPlayer(username);
				if (player != null && player.hasStarted())
					player.getPackets().sendPrivateMessage(name, qcFileId,
							qcData);
			} else {
				message = stream.readString();
				player = World.getPlayer(username);
				if (player == null)
					player = World.getLobbyPlayer(username);
				if (player != null && player.hasStarted()) {
					if (player.isFilteringProfanity())
						message = Censor.getFilteredMessage(message);
					message = Utils.fixChatMessage(message);
					player.getPackets().sendPrivateMessage(name, message);
				}
			}
			break;
		case LoginProtocol.PACKET_PLAYER_RECVPRIVATE:
			username = stream.readString();
			name = stream.readString();
			long messageUid = stream.readLong();
			int iconId = stream.readInt();
			isQc = stream.readByte() == 1;
			if (isQc) {
				int qcFileId = stream.readInt();
				byte[] qcData = stream.getRemaining() > 0 ? new byte[stream
						.getRemaining()] : null;
				if (qcData != null)
					stream.readBytes(qcData);
				player = World.getPlayer(username);
				if (player == null)
					player = World.getLobbyPlayer(username);
				if (player != null && player.hasStarted())
					player.getPackets().receivePrivateMessage(name, messageUid,
							iconId, qcFileId, qcData);
			} else {
				message = stream.readString();
				player = World.getPlayer(username);
				if (player == null)
					player = World.getLobbyPlayer(username);
				if (player != null && player.hasStarted()) {
					if (player.isFilteringProfanity())
						message = Censor.getFilteredMessage(message);
					message = Utils.fixChatMessage(message);
					player.getPackets().receivePrivateMessage(name, messageUid,
							iconId, message);
				}
			}
			break;
		case LoginProtocol.PACKET_PLAYER_FRIENDSCHATSYSMSG:
			username = stream.readString();
			message = stream.readString();
			player = World.getPlayer(username);
			if (player == null)
				player = World.getLobbyPlayer(username);
			if (player != null && player.hasStarted()) {
				player.getFriendsIgnores().fcSystemMessage(message);
			}
			break;
		case LoginProtocol.PACKET_PLAYER_FRIENDSCHATUPDATEINFO:
			username = stream.readString();
			player = World.getPlayer(username);
			if (player == null)
				player = World.getLobbyPlayer(username);
			if (player != null && player.hasStarted()
					&& stream.getRemaining() > 0) {
				String ownerUsername = stream.readString();
				String ownerDisplayname = stream.readString();
				name = stream.readString();
				int kickReq = stream.readInt();
				int membersCount = stream.readInt();
				int selfRank = -1;
				OutputStream packet = player.getPackets()
						.startFriendsChatChannel(ownerDisplayname, name,
								kickReq, membersCount);
				while (stream.readByte() == 1) {
					String displayName = stream.readString();
					int worldId = stream.readInt();
					int rank = stream.readInt();
					String worldName = stream.readString();
					if (displayName.equals(player.getDisplayName()))
						selfRank = rank;
					player.getPackets().appendFriendsChatMember(packet,
							displayName, worldId, rank, worldName);
				}
				player.getPackets().endFriendsChatChannel(packet);

				if (player.getCurrentFriendsChat() == null
						|| !player.getCurrentFriendsChat().getChannel()
								.equals(ownerUsername))
					FriendsChat.attach(player, ownerUsername);
				player.setLastFriendsChat(ownerDisplayname);
				player.setLastFriendsChatRank(selfRank);
			} else if (player != null && player.hasStarted()) {
				if (player.getCurrentFriendsChat() != null)
					FriendsChat.detach(player);
				player.setLastFriendsChat(null);
				player.setLastFriendsChatRank(-1);
				player.getPackets().sendEmptyFriendsChatChannel();
			}
			break;
		case LoginProtocol.PACKET_PLAYER_FRIENDSCHATMSG:
			username = stream.readString();
			name = stream.readString();
			String chatName = stream.readString();
			messageUid = stream.readLong();
			iconId = stream.readInt();
			isQc = stream.readByte() == 1;
			if (isQc) {
				int qcFileId = stream.readInt();
				byte[] qcData = stream.getRemaining() > 0 ? new byte[stream
						.getRemaining()] : null;
				if (qcData != null)
					stream.readBytes(qcData);
				player = World.getPlayer(username);
				if (player == null)
					player = World.getLobbyPlayer(username);
				if (player != null && player.hasStarted())
					player.getPackets().receiveFriendChatMessage(name,
							chatName, messageUid, iconId, qcFileId, qcData);
			} else {
				message = stream.readString();
				player = World.getPlayer(username);
				if (player == null)
					player = World.getLobbyPlayer(username);
				if (player != null && player.hasStarted()) {
					if (player.isFilteringProfanity())
						message = Censor.getFilteredMessage(message);
					message = Utils.fixChatMessage(message);
					player.getPackets().receiveFriendChatMessage(name,
							chatName, messageUid, iconId, message);
				}
			}
			break;
		case LoginProtocol.PACKET_UPDATEPLAYERSTRINGVAR:
			username = stream.readString();
			type = stream.readUnsignedByte();
			String svar = null;
			if (stream.readUnsignedByte() == 1)
				svar = stream.readString();
			player = World.getPlayer(username);
			if (player == null)
				player = World.getLobbyPlayer(username);
			if (player != null) {
				switch (type) {
				case LoginProtocol.VAR_TYPE_FCNAME:
					player.getFriendsIgnores().setFcName(svar, false);
					break;
				case LoginProtocol.VAR_TYPE_DISPLAY_NAME:
					player.setDisplayName(svar);
					break;
				default:
					Logger.log(LoginClientPacketsDecoder.class,
							"Unknown player var type!");
					break;
				}
			}
			break;
		case LoginProtocol.PACKET_STOREPURCHASE:
			username = stream.readString();
			String item = stream.readString();
			player = World.getPlayer(username);
			break;
		default:
			Logger.log(LoginClientPacketsDecoder.class,
					"Received unknown packet from login server, op:" + opcode);
			break;
		}

	}

}
