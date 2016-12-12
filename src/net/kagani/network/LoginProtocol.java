package net.kagani.network;

public class LoginProtocol {

	public static final int ENCRYPTION_MASK = 0xBEEFBEEF;
	public static final int MSG_SYNC_LAST_RECEIVED = 0x0;
	public static final int MSG_SYNC_LAST_SENT = 0x1;
	public static final int MSG_DATA_PACKET_R = 0x2;
	public static final int MSG_DATA_PACKET_U = 0x3;

	public static final int PACKET_PINGPONG = 0x1;
	public static final int PACKET_CONSOLEMSG = 0x2;
	public static final int PACKET_LOGINREQ = 0x3;
	public static final int PACKET_LOGINRSP = 0x4;
	public static final int PACKET_LOGOUT = 0x5;
	public static final int PACKET_LOGOUTREQ = 0x6;
	public static final int PACKET_LOGINRSPFILEPART = 0x7;
	public static final int PACKET_PLAYERFILETRANSMITINIT = 0x8;
	public static final int PACKET_PLAYERFILETRANSMITPART = 0x9;
	public static final int PACKET_WORLDSHUTDOWNREQ = 0x0A;
	public static final int PACKET_UPDATEPLAYERINTVAR = 0x0B;
	public static final int PACKET_ADD_OFFENCE = 0x0C;
	public static final int PACKET_REMOVE_OFFENCE = 0x0D;
	public static final int PACKET_PLAYER_REQWORLDSTAT = 0x0E;
	public static final int PACKET_PLAYER_RSPWORLDSTAT = 0x0F;
	public static final int PACKET_PLAYER_UPDATEFRIENDSINFO = 0x10;
	public static final int PACKET_PLAYER_UPDATEIGNORESINFO = 0x11;
	public static final int PACKET_PLAYER_GAMEMSG = 0x12;
	public static final int PACKET_PLAYER_FRIENDIGNOREOP = 0x13;
	public static final int PACKET_PLAYER_FRIENDIGNORESYSMSG = 0x14;
	public static final int PACKET_UPDATEACCOUNTINTVAR = 0x15;
	public static final int PACKET_PLAYER_FRIENDIGNORESENDALL = 0x16;
	public static final int PACKET_PLAYER_SENDPRIVATE = 0x17;
	public static final int PACKET_PLAYER_SENTPRIVATE = 0x18;
	public static final int PACKET_PLAYER_RECVPRIVATE = 0x19;
	public static final int PACKET_PLAYER_FRIENDSCHATSYSMSG = 0x20;
	public static final int PACKET_PLAYER_FRIENDSCHATUPDATEINFO = 0x21;
	public static final int PACKET_PLAYER_FRIENDSCHATMSG = 0x22;
	public static final int PACKET_PLAYER_FRIENDSCHATREQJOINLEAVE = 0x23;
	public static final int PACKET_PLAYER_FRIENDSCHATREQMSG = 0x24;
	public static final int PACKET_PLAYER_FRIENDSCHATREQKICK = 0x25;
	public static final int PACKET_PLAYER_FRIENDSCHATREQLS = 0x26;
	public static final int PACKET_PLAYER_FRIENDIGNORESETRANK = 0x27;
	public static final int PACKET_UPDATEPLAYERSTRINGVAR = 0x28;
	public static final int PACKET_UPDATEACCOUNTSTRINGVAR = 0x29;
	public static final int PACKET_STOREPURCHASE = 0x30;

	public static final int VAR_TYPE_PLAYERMUTE = 0x0;
	public static final int VAR_TYPE_PMSTATUS = 0x1;
	public static final int VAR_TYPE_LOOTSHARE = 0x2;
	public static final int VAR_TYPE_FCNAME = 0x3;
	public static final int VAR_TYPE_FCJOINREQ = 0x4;
	public static final int VAR_TYPE_FCTALKREQ = 0x5;
	public static final int VAR_TYPE_FCKICKREQ = 0x6;
	public static final int VAR_TYPE_FCLSREQ = 0x7;
	public static final int VAR_TYPE_FCCSSTATE = 0x8;
	public static final int VAR_TYPE_DISPLAY_NAME = 0x10;
	public static final int VAR_TYPE_RIGHTS = 0x11;
	public static final int VAR_TYPE_RANKS = 0x12;
	public static final int VAR_TYPE_MESSAGEICON = 0x13;
	public static final int VAR_TYPE_AUTH = 0x14;

	public static final int OFFENCE_ADDTYPE_IPBAN = 0x0;
	public static final int OFFENCE_ADDTYPE_IPMUTE = 0x2;
	public static final int OFFENCE_ADDTYPE_BAN = 0x3;
	public static final int OFFENCE_ADDTYPE_MUTE = 0x4;
	public static final int OFFENCE_ADDTYPE_IMPRISON = 0x5;

	public static final int OFFENCE_REMOVETYPE_BANS = 0x0;
	public static final int OFFENCE_REMOVETYPE_MUTES = 0x1;
	public static final int OFFENCE_REMOVETYPE_IMPRISON = 0x2;

	public static final int FRIENDIGNORE_OP_FADD = 0x0;
	public static final int FRIENDIGNORE_OP_FREMOVE = 0x1;
	public static final int FRIENDIGNORE_OP_IADD = 0x2;
	public static final int FRIENDIGNORE_OP_IREMOVE = 0x3;
	public static final int FRIENDIGNORE_OP_IADDTMP = 0x4;

	public static void cipherBuffer(byte[] buffer, int offset, int length) {
		for (int i = 0; i < length; i++)
			buffer[i + offset] ^= ENCRYPTION_MASK;
	}

	public static String getOpcodeName(int opcode) {
		try {
			for (java.lang.reflect.Field field : LoginProtocol.class
					.getFields())
				if (field.getName().startsWith("PACKET_")
						&& field.getInt(null) == opcode)
					return field.getName();
		} catch (Exception e) {

		}
		return "N/A";
	}

}
