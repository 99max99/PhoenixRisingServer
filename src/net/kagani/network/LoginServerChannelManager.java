package net.kagani.network;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Arrays;

import net.kagani.Settings;
import net.kagani.login.GameWorld;
import net.kagani.login.Login;
import net.kagani.network.decoders.LoginServerPacketsDecoder;
import net.kagani.stream.InputStream;
import net.kagani.stream.OutputStream;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class LoginServerChannelManager {

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
	private static byte[][][] outgoingQueue;
	/**
	 * Count of outgoing packets.
	 */
	private static int[] outgoingQueueCount;
	/**
	 * Contains last request time.
	 */
	private static long[] lastSync;
	/**
	 * Number of the last packet that our client successfully received from the
	 * server.
	 */
	private static long[] in_syncnum;
	/**
	 * Number of the last packet that the server successfully received.
	 */
	private static long[] out_syncnum;

	public static void init() throws SocketException {
		lastSync = new long[Login.getWorldsSize()];
		in_syncnum = new long[Login.getWorldsSize()];
		out_syncnum = new long[Login.getWorldsSize()];
		outgoingQueue = new byte[Login.getWorldsSize()][1024 * 1024][];
		outgoingQueueCount = new int[Login.getWorldsSize()];
		queueLock = new Object();
		device = new UDPDevice(1024 * 1024, 1024 * 1024);
		boss = new Thread("login->game manager") {
			@Override
			public void run() {
				LoginServerChannelManager.run();
			}
		};
		boss.start();
		device.bind(Settings.LOGIN_SERVER_ADDRESS_BASE);
	}

	public static void awaitQueue() {
		main: while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

			synchronized (queueLock) {
				for (int i = 0; i < outgoingQueueCount.length; i++)
					if (outgoingQueueCount[i] != 0)
						continue main;
				return;
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
	public static void sendReliablePacket(GameWorld world, byte[] data) {
		synchronized (queueLock) {
			outgoingQueue[world.getId()][outgoingQueueCount[world.getId()]++] = data;
		}
	}

	/**
	 * Send's packet which is not guaranteed to arrive to it's destination.
	 */
	public static void sendUnreliablePacket(GameWorld world, byte[] data) {
		writeUnreliablePacket(world, data);
	}

	/**
	 * Run method for boss network thread.
	 */
	private static void run() {
		while (true) {
			try {
				for (int i = 0; i < Login.getWorldsSize(); i++) {
					GameWorld world = Login.getWorld(i);
					if (world == null)
						continue;

					if ((Utils.currentTimeMillis() - lastSync[i]) > 50L) {
						lastSync[i] = Utils.currentTimeMillis();
						writeLastReceivedPacketNum(world);
					}
				}

				handleDevice();
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
				}
			} catch (ThreadDeath death) {
				// we are getting killed by shutdown()
				throw death;
			} catch (Throwable t) {
				Logger.handle(t);
			}
		}
	}

	/**
	 * Send's number of last received packet.
	 */
	private static void writeLastReceivedPacketNum(GameWorld world) {
		OutputStream output = new OutputStream(9);
		output.writeByte(LoginProtocol.MSG_SYNC_LAST_RECEIVED); // sync msg
		output.writeLong(in_syncnum[world.getId()]);

		LoginProtocol.cipherBuffer(output.getBuffer(), 0, 9);
		DatagramPacket packet = new DatagramPacket(output.getBuffer(), 0, 9);
		packet.setSocketAddress(world.getLoginAddress());
		device.write(packet);
	}

	/**
	 * Send's number of last sent packet.
	 */
	private static void writeLastSentPacketNum(GameWorld world) {
		OutputStream output = new OutputStream(9);
		output.writeByte(LoginProtocol.MSG_SYNC_LAST_SENT); // sync msg
		output.writeLong(out_syncnum[world.getId()]);

		LoginProtocol.cipherBuffer(output.getBuffer(), 0, 9);
		DatagramPacket packet = new DatagramPacket(output.getBuffer(), 0, 9);
		packet.setSocketAddress(world.getLoginAddress());
		device.write(packet);
	}

	/**
	 * Send's reliable packet.
	 */
	private static void writeReliablePacket(GameWorld world, byte[] data,
			long syncnum) {
		OutputStream out = new OutputStream(data.length + 9);
		out.writeByte(LoginProtocol.MSG_DATA_PACKET_R);
		out.writeLong(syncnum);
		out.writeBytes(data);

		LoginProtocol.cipherBuffer(out.getBuffer(), 0, out.getBuffer().length);
		DatagramPacket dataout = new DatagramPacket(out.getBuffer(), 0,
				out.getBuffer().length);
		dataout.setSocketAddress(world.getLoginAddress());
		device.write(dataout);
	}

	/**
	 * Send's unreliable packet.
	 */
	private static void writeUnreliablePacket(GameWorld world, byte[] data) {
		OutputStream out = new OutputStream(data.length + 9);
		out.writeByte(LoginProtocol.MSG_DATA_PACKET_U);
		out.writeLong(0);
		out.writeBytes(data);

		LoginProtocol.cipherBuffer(out.getBuffer(), 0, out.getBuffer().length);
		DatagramPacket dataout = new DatagramPacket(out.getBuffer(), 0,
				out.getBuffer().length);
		dataout.setSocketAddress(world.getLoginAddress());
		device.write(dataout);
	}

	/**
	 * Handle's device.
	 */
	private static void handleDevice() {
		int amount = device.getBufferedAmount();
		for (int i = 0; i < amount; i++) {
			DatagramPacket packet = device.read();
			// first, find from which world this packet came
			GameWorld world = Login.getWorld(packet.getPort()
					- Settings.LOGIN_CLIENT_ADDRESS_BASE.getPort());
			if (packet.getLength() < 9
					|| world == null
					|| !Arrays.equals(world.getLoginAddress().getAddress()
							.getAddress(), packet.getAddress().getAddress())) { // the
																				// world
																				// doesn't
																				// exist
																				// or
																				// the
																				// ip
																				// doesn't
																				// match
				if (Settings.DEBUG)
					Logger.log(LoginServerChannelManager.class,
							"Game->Login packet was rejected");
				continue; // discard it
			}

			byte[] data = packet.getData();
			LoginProtocol.cipherBuffer(data, 0, data.length);
			InputStream stream = new InputStream(data);
			switch (stream.readUnsignedByte()) {
			case LoginProtocol.MSG_SYNC_LAST_RECEIVED:
				synchronized (queueLock) {
					long syncnum = stream.readLong();
					if (syncnum < out_syncnum[world.getId()]
							|| syncnum > (out_syncnum[world.getId()] + outgoingQueueCount[world
									.getId()])) {
						// we don't have this packet buffered
						if (Settings.DEBUG)
							Logger.log(LoginServerChannelManager.class,
									"Sync error, reliable packet loss expected");
						writeLastSentPacketNum(world);
						break;
					}

					int drop_count = (int) (syncnum - out_syncnum[world.getId()]);
					for (int a = drop_count, x = 0; a < outgoingQueueCount[world
							.getId()]; a++)
						outgoingQueue[world.getId()][x++] = outgoingQueue[world
								.getId()][a];
					outgoingQueueCount[world.getId()] -= drop_count;
					out_syncnum[world.getId()] += drop_count;

					for (int a = 0; a < outgoingQueueCount[world.getId()]; a++)
						writeReliablePacket(world,
								outgoingQueue[world.getId()][a],
								out_syncnum[world.getId()] + a + 1);
					break;
				}
			case LoginProtocol.MSG_SYNC_LAST_SENT:
				in_syncnum[world.getId()] = stream.readLong();
				if (Settings.DEBUG)
					Logger.log(LoginServerChannelManager.class,
							"Received incoming resync request, reliable packet loss expected");
				break;
			case LoginProtocol.MSG_DATA_PACKET_R: // reliable packet
				long datasyncnum = stream.readLong()
						- in_syncnum[world.getId()];
				if (datasyncnum != 1) {
					// some packets need to arrive first before this packet, so
					// drop it
					break;
				}
				lastSync[world.getId()] = Utils.currentTimeMillis();
				in_syncnum[world.getId()] += 1; // increase our syncnum so next
												// packets can go thru
				try {
					Login.getDecoderLock().lock();
					LoginServerPacketsDecoder.decodeIncomingPacket(world,
							stream);
				} finally {
					Login.getDecoderLock().unlock();
				}
				break;
			case LoginProtocol.MSG_DATA_PACKET_U: // unreliable packet
				datasyncnum = stream.readLong();
				if (datasyncnum != 0) {
					if (Settings.DEBUG)
						Logger.log(
								LoginServerChannelManager.class,
								"Received unreliable packet with wrong syncnum, does gameserver use different encryption?");
					break;
				}
				try {
					Login.getDecoderLock().lock();
					LoginServerPacketsDecoder.decodeIncomingPacket(world,
							stream);
				} finally {
					Login.getDecoderLock().unlock();
				}
				break;
			default:
				if (Settings.DEBUG)
					Logger.log(
							LoginServerChannelManager.class,
							"Unknown packet received from gameserver, does gameserver use different encryption?");
				break;
			}
		}
	}

}
