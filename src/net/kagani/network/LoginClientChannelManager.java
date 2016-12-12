package net.kagani.network;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

import net.kagani.Settings;
import net.kagani.network.decoders.LoginClientPacketsDecoder;
import net.kagani.stream.InputStream;
import net.kagani.stream.OutputStream;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class LoginClientChannelManager {

	/**
	 * Our device.
	 */
	private static UDPDevice device;
	/**
	 * Our manager thread.
	 */
	private static Thread boss;
	/**
	 * Contains queue lock object.
	 */
	private static Object queueLock;
	/**
	 * Contains outgoing packets queue.
	 */
	private static byte[][] outgoingQueue;
	/**
	 * Count of outgoing packets.
	 */
	private static int outgoingQueueCount;
	/**
	 * Contains last request time.
	 */
	private static long lastSync;
	/**
	 * Number of the last packet that our client successfully received from the
	 * server.
	 */
	private static long in_syncnum;
	/**
	 * Number of the last packet that the server successfully received.
	 */
	private static long out_syncnum;

	public static void init() throws SocketException {
		in_syncnum = 0;
		out_syncnum = 0;
		outgoingQueue = new byte[1024 * 1024][];
		outgoingQueueCount = 0;
		queueLock = new Object();
		device = new UDPDevice(1024 * 1024, 1024 * 1024);
		boss = new Thread("game->login manager") {
			@Override
			public void run() {
				LoginClientChannelManager.run();
			}
		};
		boss.start();
		device.bind(new InetSocketAddress(Settings.LOGIN_CLIENT_ADDRESS_BASE
				.getPort() + Settings.WORLD_ID));
	}

	public static void awaitQueue() {
		while (true) {
			synchronized (queueLock) {
				if (outgoingQueueCount <= 0)
					return;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void shutdown() {
		try {
			boss.stop();
		} catch (Throwable t) {
		}
		boss = null;
		device.unbind();
		device = null;
	}

	/**
	 * Send's reliable packet, this packet is guaranteed to arrive.
	 */
	public static void sendReliablePacket(byte[] data) {
		synchronized (queueLock) {
			outgoingQueue[outgoingQueueCount++] = data;
		}
	}

	/**
	 * Send's packet which is not guaranteed to arrive to it's destination.
	 */
	public static void sendUnreliablePacket(byte[] data) {
		writeUnreliablePacket(data);
	}

	/**
	 * Run method for boss network thread.
	 */
	private static void run() {
		while (true) {
			try {
				if ((Utils.currentTimeMillis() - lastSync) > 50L) {
					lastSync = Utils.currentTimeMillis();
					writeLastReceivedPacketNum();
				}

				handleDevice();
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
				}
			} catch (ThreadDeath death) {
				throw death;
			} catch (Throwable t) {
				Logger.handle(t);
			}
		}
	}

	/**
	 * Send's number of last received packet.
	 */
	private static void writeLastReceivedPacketNum() {
		OutputStream output = new OutputStream(9);
		output.writeByte(LoginProtocol.MSG_SYNC_LAST_RECEIVED); // sync msg
		output.writeLong(in_syncnum);

		LoginProtocol.cipherBuffer(output.getBuffer(), 0, 9);
		DatagramPacket packet = new DatagramPacket(output.getBuffer(), 0, 9);
		packet.setSocketAddress(Settings.LOGIN_SERVER_ADDRESS_BASE);
		device.write(packet);
	}

	/**
	 * Send's number of last sent packet.
	 */
	private static void writeLastSentPacketNum() {
		OutputStream output = new OutputStream(9);
		output.writeByte(LoginProtocol.MSG_SYNC_LAST_SENT); // sync msg
		output.writeLong(out_syncnum);

		LoginProtocol.cipherBuffer(output.getBuffer(), 0, 9);
		DatagramPacket packet = new DatagramPacket(output.getBuffer(), 0, 9);
		packet.setSocketAddress(Settings.LOGIN_SERVER_ADDRESS_BASE);
		device.write(packet);
	}

	/**
	 * Send's reliable packet.
	 */
	private static void writeReliablePacket(byte[] data, long syncnum) {
		OutputStream out = new OutputStream(data.length + 9);
		out.writeByte(LoginProtocol.MSG_DATA_PACKET_R);
		out.writeLong(syncnum);
		out.writeBytes(data);

		LoginProtocol.cipherBuffer(out.getBuffer(), 0, out.getBuffer().length);
		DatagramPacket dataout = new DatagramPacket(out.getBuffer(), 0,
				out.getBuffer().length);
		dataout.setSocketAddress(Settings.LOGIN_SERVER_ADDRESS_BASE);
		device.write(dataout);
	}

	/**
	 * Send's unreliable packet.
	 */
	private static void writeUnreliablePacket(byte[] data) {
		OutputStream out = new OutputStream(data.length + 9);
		out.writeByte(LoginProtocol.MSG_DATA_PACKET_U);
		out.writeLong(0);
		out.writeBytes(data);

		LoginProtocol.cipherBuffer(out.getBuffer(), 0, out.getBuffer().length);
		DatagramPacket dataout = new DatagramPacket(out.getBuffer(), 0,
				out.getBuffer().length);
		dataout.setSocketAddress(Settings.LOGIN_SERVER_ADDRESS_BASE);
		device.write(dataout);
	}

	/**
	 * Handle's device.
	 */
	private static void handleDevice() {
		int amount = device.getBufferedAmount();
		for (int i = 0; i < amount; i++) {
			DatagramPacket packet = device.read();
			if (packet.getLength() < 9
					|| packet.getPort() != Settings.LOGIN_SERVER_ADDRESS_BASE
							.getPort()
					|| !Arrays.equals(Settings.LOGIN_SERVER_ADDRESS_BASE
							.getAddress().getAddress(), packet.getAddress()
							.getAddress())) {
				if (Settings.DEBUG)
					Logger.log(LoginClientChannelManager.class,
							"Login->Game packet was rejected");
				continue; // discard packets that came not from loginserver
			}

			byte[] data = packet.getData();
			LoginProtocol.cipherBuffer(data, 0, data.length);
			InputStream stream = new InputStream(data);
			switch (stream.readUnsignedByte()) {
			case LoginProtocol.MSG_SYNC_LAST_RECEIVED:
				synchronized (queueLock) {
					long syncnum = stream.readLong();
					if (syncnum < out_syncnum
							|| syncnum > (out_syncnum + outgoingQueueCount)) {
						// we don't have this packet buffered
						if (Settings.DEBUG)
							Logger.log(LoginClientChannelManager.class,
									"Sync error, reliable packet loss expected");
						writeLastSentPacketNum();
						break;
					}

					int drop_count = (int) (syncnum - out_syncnum);
					for (int a = drop_count, x = 0; a < outgoingQueueCount; a++)
						outgoingQueue[x++] = outgoingQueue[a];
					outgoingQueueCount -= drop_count;
					out_syncnum += drop_count;

					for (int a = 0; a < outgoingQueueCount; a++)
						writeReliablePacket(outgoingQueue[a], out_syncnum + a
								+ 1);
					break;
				}
			case LoginProtocol.MSG_SYNC_LAST_SENT:
				in_syncnum = stream.readLong();
				if (Settings.DEBUG)
					Logger.log(LoginClientChannelManager.class,
							"Received incoming resync request, reliable packet loss expected");
				break;
			case LoginProtocol.MSG_DATA_PACKET_R: // reliable packet
				long datasyncnum = stream.readLong() - in_syncnum;
				if (datasyncnum != 1) {
					// some packets need to arrive first before this packet, so
					// drop it
					break;
				}
				lastSync = Utils.currentTimeMillis(); // reset sync counter
				in_syncnum += 1; // increase our syncnum so next packets can go
				// thru
				LoginClientPacketsDecoder.decodeIncomingPacket(stream);
				break;
			case LoginProtocol.MSG_DATA_PACKET_U: // unreliable packet
				datasyncnum = stream.readLong();
				if (datasyncnum != 0) {
					if (Settings.DEBUG)
						Logger.log(
								LoginClientChannelManager.class,
								"Received unreliable packet with wrong syncnum, does login use different encryption?");
					break;
				}
				LoginClientPacketsDecoder.decodeIncomingPacket(stream);
				break;
			default:
				if (Settings.DEBUG)
					Logger.log(LoginClientChannelManager.class,
							"Unknown packet received from login, does login use different encryption?");
				break;
			}
		}
	}
}