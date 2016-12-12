package net.kagani.network.decoders;

import net.kagani.Settings;
import net.kagani.cache.Cache;
import net.kagani.executor.PlayerHandlerThread;
import net.kagani.game.player.Player;
import net.kagani.network.Session;
import net.kagani.stream.InputStream;
import net.kagani.utils.AntiFlood;
import net.kagani.utils.Logger;
import net.kagani.utils.MachineInformation;
import net.kagani.utils.Utils;
import net.kagani.utils.sql.Gero;

public final class LoginPacketsDecoder extends Decoder {

	public LoginPacketsDecoder(Session session) {
		super(session);
	}

	@Override
	public int decode(InputStream stream) {
		if (stream.getRemaining() < 3)
			return 0;

		int opcode = stream.readUnsignedByte();
		int length = stream.readUnsignedShort();
		// Logger.log(this, "LOGIN PACKET = > PacketId =:" + opcode);

		if (stream.getRemaining() < length)
			return 0;

		session.setDecoder(-1);
		if (stream.readInt() != Settings.MAJOR_VERSION) {
			session.getLoginPackets().sendClosingPacket(6);
			return -1;
		}
		byte[] d = new byte[length];
		stream.readBytes(d);

		if (opcode == 16 || opcode == 18) // 16 world login
			decodeWorldLogin(new InputStream(d));
		else if (opcode == 19)
			decodeLobbyLogin(new InputStream(d));
		else {
			session.getChannel().close();
			return -1;
		}

		return stream.getOffset();
	}

	public MachineInformation decodeMachineInformation(InputStream stream) {
		if (stream.readUnsignedByte() != 7) { // personal data start
			session.getLoginPackets().sendClosingPacket(10);
			return null;
		}
		int os = stream.readUnsignedByte();
		boolean x64Arch = stream.readUnsignedByte() == 1;
		int osVersion = stream.readUnsignedByte();
		int osVendor = stream.readUnsignedByte();
		int javaVersion = stream.readUnsignedByte();
		int javaVersionBuild = stream.readUnsignedByte();
		int javaVersionBuild2 = stream.readUnsignedByte();
		boolean hasApplet = stream.readUnsignedByte() == 1;
		int heap = stream.readUnsignedShort();
		int availableProcessors = stream.readUnsignedByte();
		int ram = stream.read24BitInt();
		int cpuClockFrequency = stream.readUnsignedShort();
		String graphicCardManufactor = stream.readVersionedString();
		String graphicCardName = stream.readVersionedString();
		String empty3 = stream.readVersionedString();
		String dxVersion = stream.readVersionedString();
		int graphicCardReleaseMonth = stream.readUnsignedByte();
		int graphicCardReleaseYear = stream.readUnsignedShort();
		String cpuManufactor = stream.readVersionedString();
		String cpuName = stream.readVersionedString();
		int unused3 = stream.readUnsignedByte(); // aspect ratio i think
		int unused4 = stream.readUnsignedByte();// aspect ratio i think
		int[] u = new int[3];
		for (int i = 0; i < u.length; i++)
			u[i] = stream.readInt();
		int unused5 = stream.readInt();
		String empty4 = stream.readVersionedString();
		// System.out.println(unused3+", "+unused4+", "+Arrays.toString(u)+",
		// "+unused5+", "+empty4);
		return new MachineInformation(os, x64Arch, osVersion, osVendor,
				javaVersion, javaVersionBuild, javaVersionBuild2, hasApplet,
				heap, availableProcessors, ram, cpuClockFrequency, 0, 0, 0); // TODO
		// update
		// this
		// later
	}

	@SuppressWarnings("unused")
	public void decodeLobbyLogin(InputStream stream) {
		if (stream.readInt() != Settings.MINOR_VERSION) {
			session.getLoginPackets().sendClosingPacket(6);
			return;
		}
		int rsaBlockSize = stream.readUnsignedShort();
		if (rsaBlockSize > stream.getRemaining()) {
			session.getLoginPackets().sendClosingPacket(10);
			return;
		}
		byte[] data = new byte[rsaBlockSize];
		stream.readBytes(data, 0, rsaBlockSize);
		InputStream rsaStream = new InputStream(Utils.cryptRSA(data,
				Settings.PRIVATE_EXPONENT, Settings.MODULUS));
		if (rsaStream.readUnsignedByte() != 10) {
			session.getLoginPackets().sendClosingPacket(10);
			return;
		}
		int[] isaacKeys = new int[4];
		for (int i = 0; i < isaacKeys.length; i++)
			isaacKeys[i] = rsaStream.readInt();
		int unknownType = rsaStream.readUnsignedByte(); // type of data
		int unknown = rsaStream.readInt();
		if (unknown != 0L) { // rsa block check, pass part
			session.getLoginPackets().sendClosingPacket(10);
			return;
		}

		String password = rsaStream.readString();
		if (password.length() > 30 || password.length() < 3) {
			session.getLoginPackets().sendClosingPacket(3);
			return;
		}
		rsaStream.readLong(); // idk
		rsaStream.readLong(); // random value
		stream.xteaDecrypt(isaacKeys, stream.getOffset(), stream.getLength());
		boolean stringUsername = stream.readUnsignedByte() == 1; // unknown
		String username = Utils
				.formatPlayerNameForProtocol(stringUsername ? stream
						.readString() : Utils.longToString(stream.readLong()));
		if (Settings.GERO_ENABLED) {
			int info = Gero.verifyLogin(username, password, session.getIP());
			if (Settings.DEBUG)
				Logger.log("Gero", "returning: " + info);
			if (info != 2) {
				if (Settings.CONNECTED == false) {
					session.getLoginPackets().sendClosingPacket(14);
					return;
				}
				session.getLoginPackets().sendClosingPacket(info);
				return;
			}
		}
		int game = stream.readUnsignedByte();
		int locale = stream.readUnsignedByte();
		int displayMode = stream.readUnsignedByte();
		int screenWidth = stream.readUnsignedShort();
		int screenHeight = stream.readUnsignedShort();
		int unknown2 = stream.readUnsignedByte();
		stream.skip(24);

		String settings = stream.readString();
		if (!settings.equals(Settings.CLIENT_SETTINGS)) {
			session.getLoginPackets().sendClosingPacket(10);
			System.out.println(settings + " does not equal "
					+ Settings.CLIENT_SETTINGS);
			return;
		}
		stream.skip(stream.readUnsignedByte()); // useless settings
		MachineInformation mInformation = decodeMachineInformation(stream);
		int unknown3 = stream.readInt();
		String worldServerToken = stream.readString();
		if (!worldServerToken.equals(Settings.WORLD_SERVER_TOKEN)) {
			session.getLoginPackets().sendClosingPacket(35);
			System.out.println(worldServerToken + " does not equal "
					+ Settings.CLIENT_SETTINGS);
			return;
		}
		int affId = stream.readInt();
		int clientLoginId = stream.readInt();
		if (clientLoginId != Settings.CLIENT_LOGIN_ID) {
			session.getLoginPackets().sendClosingPacket(35);
			System.out.println(clientLoginId + " does not equal "
					+ Settings.CLIENT_LOGIN_ID);
			return;
		}
		String grabServerToken = stream.readString();
		if (!grabServerToken.equals(Settings.GRAB_SERVER_TOKEN)) {
			session.getLoginPackets().sendClosingPacket(35);
			System.out.println(grabServerToken + " does not equal "
					+ Settings.GRAB_SERVER_TOKEN);
			return;
		}
		boolean unknown7 = stream.readUnsignedByte() == 1;
		for (int index = 0; index < Cache.STORE.getIndexes().length; index++) {
			if (Cache.STORE.getIndexes()[index] == null)
				continue;
			int crc = Cache.STORE.getIndexes()[index].getCRC();
			int receivedCRC = stream.readInt();
			if (crc != receivedCRC && index < 30) { // outdated
				if (Settings.DEBUG)
					Logger.log(this, "Invalid CRC at index: " + index + ", "
							+ receivedCRC + ", " + crc);
				session.getLoginPackets().sendClosingPacket(6);
				return;
			}
		}
		String MACAddress = stream.readString();
		if (Utils.invalidAccountName(username) || username.startsWith("mod")
				|| username.startsWith("m0d") || username.contains("admin")
				|| username.equalsIgnoreCase(Settings.SERVER_NAME)) {
			session.getLoginPackets().sendClosingPacket(31);
			return;
		}
		if (Settings.GERO_ENABLED) {
			if (Gero.validateAccount(username, password, session.getIP())) {
				if (Settings.CONNECTED == false) {
					session.getLoginPackets().sendClosingPacket(14);
					return;
				}
				if (Gero.checkBan(username)) {
					session.getLoginPackets().sendClosingPacket(4);
					return;
				}
			}
		}
		if (AntiFlood.getSessionsIP(session.getIP()) >= 3) {
			session.getLoginPackets().sendClosingPacket(9);
			return;
		}

		Player player = new Player();

		if (player.isHardcoreIronman() && player.getHasDied()) {
			session.getLoginPackets().sendClosingPacket(44);
			System.out.println(player.getDisplayName()
					+ " died has Hardcore Ironman.");
			return;
		}

		PlayerHandlerThread.addSession(session, isaacKeys, true, username,
				password, MACAddress, 0, 0, 0, null);
	}

	@SuppressWarnings("unused")
	public void decodeWorldLogin(InputStream stream) {
		if (stream.readInt() != Settings.MINOR_VERSION) {
			session.getLoginPackets().sendClosingPacket(6);
			return;
		}
		boolean unknownEquals14 = stream.readUnsignedByte() == 1;
		int rsaBlockSize = stream.readUnsignedShort();
		if (rsaBlockSize > stream.getRemaining()) {
			session.getLoginPackets().sendClosingPacket(10);
			return;
		}
		byte[] data = new byte[rsaBlockSize];
		stream.readBytes(data, 0, rsaBlockSize);
		InputStream rsaStream = new InputStream(Utils.cryptRSA(data,
				Settings.PRIVATE_EXPONENT, Settings.MODULUS));
		if (rsaStream.readUnsignedByte() != 10) {
			session.getLoginPackets().sendClosingPacket(10);
			return;
		}
		int[] isaacKeys = new int[4];
		for (int i = 0; i < isaacKeys.length; i++)
			isaacKeys[i] = rsaStream.readInt();
		int unknownType = rsaStream.readUnsignedByte(); // type of data
		int unknown = rsaStream.readInt();
		if (unknown != 0L) { // rsa block check, pass part
			session.getLoginPackets().sendClosingPacket(10);
			return;
		}
		String password = rsaStream.readString();
		if (password.length() > 30 || password.length() < 3) {
			session.getLoginPackets().sendClosingPacket(3);
			return;
		}
		rsaStream.readLong(); // idk
		rsaStream.readLong(); // random value
		stream.xteaDecrypt(isaacKeys, stream.getOffset(), stream.getLength());
		boolean stringUsername = stream.readUnsignedByte() == 1; // unknown
		String username = Utils
				.formatPlayerNameForProtocol(stringUsername ? stream
						.readString() : Utils.longToString(stream.readLong()));
		int displayMode = stream.readUnsignedByte();
		int screenWidth = stream.readUnsignedShort();
		int screenHeight = stream.readUnsignedShort();
		int unknown2 = stream.readUnsignedByte();
		stream.skip(24); // 24bytes directly from a file, no idea whats there
		String settings = stream.readString();
		if (!settings.equals(Settings.CLIENT_SETTINGS)) {
			session.getLoginPackets().sendClosingPacket(10);
			return;
		}
		int affid = stream.readInt();
		stream.skip(stream.readUnsignedByte()); // useless settings
		MachineInformation mInformation = decodeMachineInformation(stream);
		int unknown3 = stream.readInt();
		int userFlow = stream.readInt();
		int unknown9 = stream.readInt();
		String worldServerToken = stream.readString();
		if (!worldServerToken.equals(Settings.WORLD_SERVER_TOKEN)) {
			session.getLoginPackets().sendClosingPacket(35);
			return;
		}
		boolean hasAditionalInformation = stream.readUnsignedByte() == 1;
		String aditionalInformation = hasAditionalInformation ? stream
				.readString() : "";
		boolean hasJagtheora = stream.readUnsignedByte() == 1;
		boolean js = stream.readUnsignedByte() == 1;
		int unknown4 = stream.readByte();
		int unknown5 = stream.readInt();
		String grabServerToken = stream.readString();
		if (!grabServerToken.equals(Settings.GRAB_SERVER_TOKEN)) {
			session.getLoginPackets().sendClosingPacket(35);
			return;
		}
		boolean differentServer = stream.readUnsignedByte() == 1;
		int serverId1 = stream.readUnsignedShort();
		int serverId2 = stream.readUnsignedShort();
		for (int index = 0; index < Cache.STORE.getIndexes().length; index++) {
			if (Cache.STORE.getIndexes()[index] == null)
				continue;
			int crc = Cache.STORE.getIndexes()[index].getCRC();
			int receivedCRC = stream.readInt();

			if (crc != receivedCRC && index < 30) { // outdated
				if (Settings.DEBUG)
					Logger.log(this, "Invalid CRC at index: " + index + ", "
							+ receivedCRC + ", " + crc);
				session.getLoginPackets().sendClosingPacket(6);
				return;
			}
		}
		String MACAddress = stream.readString();
		if (!session.getIP().equals("127.0.0.1")
				&& !session.getIP().equals("128.77.48.200")
				&& !session.getIP().equals("77.101.84.136")
				&& !session.getIP().equals("75.155.65.227")
				&& !session.getIP().equals("101.98.156.118")//Template Old
				&& !session.getIP().equals("219.88.237.98")//Template New #1
				&& !session.getIP().equals("219.88.238.170")//Template New #2
				&& !session.getIP().equals("101.98.41.75")) {//Template's GF house
			if (Settings.WORLD_ID != 1 && !Gero.checkPermission(username)) {
				if (Settings.CONNECTED == false) {
					session.getLoginPackets().sendClosingPacket(14);
					return;
				}
				session.getLoginPackets().sendClosingPacket(40);
				return;
			}
		}

		Player player = new Player();

		if (player.isHardcoreIronman() && player.getHasDied())

		{
			session.getLoginPackets().sendClosingPacket(44);
			Logger.log("LoginPacketsDecoder", player.getDisplayName()
					+ " died has Hardcore Ironman.");
			return;
		}

		PlayerHandlerThread.addSession(session, isaacKeys, false, username,
				password, MACAddress, displayMode, screenWidth, screenHeight,
				mInformation);
	}
}