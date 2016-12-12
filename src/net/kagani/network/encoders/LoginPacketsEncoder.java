package net.kagani.network.encoders;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import net.kagani.Settings;
import net.kagani.game.player.Player;
import net.kagani.network.Session;
import net.kagani.stream.OutputStream;
import net.kagani.utils.Utils;

public final class LoginPacketsEncoder extends Encoder {

	public LoginPacketsEncoder(Session connection) {
		super(connection);
	}

	public final void sendStartUpPacket() {
		OutputStream stream = new OutputStream(1);
		stream.writeByte(0);
		session.write(stream);
	}

	public final void sendPacket(int opcode) {
		OutputStream stream = new OutputStream(1);
		stream.writeByte(opcode);
	}

	public final void sendPacket(int opcode, int subopcode) {
		OutputStream stream = new OutputStream(1);
		stream.writeByte(opcode);
		stream.writeByte(subopcode);
	}

	public final void sendClosingPacket(int opcode) {
		OutputStream stream = new OutputStream(1);
		stream.writeByte(opcode);
		ChannelFuture future = session.write(stream);
		if (future != null) {
			future.addListener(ChannelFutureListener.CLOSE);
		} else {
			session.getChannel().close();
		}
	}

	public final void sendClosingPacket(int opcode, int subopcode) {
		OutputStream stream = new OutputStream(1);
		stream.writeByte(opcode);
		stream.writeByte(subopcode);
		ChannelFuture future = session.write(stream);
		if (future != null) {
			future.addListener(ChannelFutureListener.CLOSE);
		} else {
			session.getChannel().close();
		}
	}

	public void sendLobbyDetails(Player player) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(player, 2);
		stream.writeByte(0); // if setted true it will read an integer aswell.
		// idk what for
		stream.writeByte(player.getRights());// rights
		stream.writeByte(0); // blackmarks
		stream.writeByte(0); // muted
		stream.write24BitInteger(0);
		stream.writeByte(0);
		stream.writeByte(0);
		stream.writeByte(0);
		stream.writeLong(-1); // subscription days left
		stream.write5ByteInteger(12); // subscription minutes and seconds left
		stream.writeByte(player.isAMember() ? 0x2 : 0); // 0x1
														// -
														// if
														// members,
														// 0x2
														// -
														// subscription
		stream.writeInt(1); // unused in client
		stream.writeInt(0); // unused in client
		stream.writeShort(player.isMuted() ? 0 : 1); // recovery questions 1 for
		// set, 0 for not set
		stream.writeShort(player.hasVotedInLast12Hours() ? 0 : 1); // Messages
		// count
		// (Overrided
		// by our
		// voting
		// stuff)

		if (player.getLastGameLogin() != 0) {
			long now = Utils.currentTimeMillis();
			long jag = 1014753880308L;
			long log = player.getLastGameLogin();
			long since_jag = (now - jag) / 1000 / 60 / 60 / 24;
			long since_log = (now - log) / 1000 / 60 / 60 / 24;
			stream.writeShort((int) (since_jag - since_log));
		} else {
			stream.writeShort(0);// last logged in date
		}
		if (player.getLastGameIp() != null) {
			String[] ipSplit = player.getLastGameIp().split("\\.");
			stream.writeInt((Integer.parseInt(ipSplit[0]) << 24)
					+ (Integer.parseInt(ipSplit[1]) << 16)
					+ (Integer.parseInt(ipSplit[2]) << 8)
					+ Integer.parseInt(ipSplit[3])); // ip
			// part
		} else {
			stream.writeInt(0);
		}

		stream.writeByte(player.getEmail() != null ? 3 : 0); // email status (0
		// - no email, 1 -
		// pending parental
		// confirmation, 2
		// - pending
		// confirmation, 3
		// - registered)
		stream.writeShort(0);
		stream.writeShort(0);
		stream.writeByte(0);
		stream.writeVersionedString(player.getDisplayName());
		stream.writeByte(0);
		stream.writeInt(1);

		int ourWorldIndex = 0;
		for (int i = 0; i < Settings.WORLDS_INFORMATION.length; i++)
			if (Settings.WORLDS_INFORMATION[i].getId() == Settings.WORLD_ID) {
				ourWorldIndex = i;
				break;
			}

		stream.writeShort(Settings.WORLDS_INFORMATION[ourWorldIndex].getId());
		stream.writeVersionedString(Settings.WORLDS_INFORMATION[ourWorldIndex]
				.getIp());
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void sendILayoutVars(Player player) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(null, 2);
		stream.writeByte(1); // tells to move to login details next
		for (int id : player.getILayoutVars().keySet()) {
			Integer value = player.getILayoutVars().get(id);
			if (value == null || value == 0)
				continue;
			stream.writeShort(id);
			stream.writeInt(value);
		}
		stream.endPacketVarShort();
		session.write(stream);
	}

	public final void sendLoginDetails(Player player) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(null, 2);
		stream.writeByte(0); // if setted true it will read an integer aswell.
		// idk what for
		stream.writeByte(player.getRights());
		stream.writeByte(0);
		stream.writeByte(0);
		stream.writeByte(0);
		stream.writeByte(1);
		stream.writeByte(0);
		stream.writeShort(player.getIndex());
		stream.writeByte(1); // is member
		stream.write24BitInteger(0);
		stream.writeByte(1); // is member world
		stream.writeString(player.getDisplayName());
		stream.endPacketVarByte();
		session.write(stream);
	}
}