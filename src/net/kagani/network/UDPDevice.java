package net.kagani.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import net.kagani.utils.Logger;

public class UDPDevice {
	/**
	 * Contains read buffer.
	 */
	private DatagramPacket[] readBuffer;
	/**
	 * Contains read buffer length.
	 */
	private int readBufferLength;
	/**
	 * Contains write buffer.
	 */
	private DatagramPacket[] writeBuffer;
	/**
	 * Contains write buffer length.
	 */
	private int writeBufferLength;
	/**
	 * Contains io read lock object.
	 */
	private Object ioReadLock;
	/**
	 * Contains io write lock object.
	 */
	private Object ioWriteLock;
	/**
	 * Contains our socket.
	 */
	private DatagramSocket socket;
	/**
	 * Contains io read thread.
	 */
	private Thread ioReadThread;
	/**
	 * Contains io write thread.
	 */
	private Thread ioWriteThread;

	public UDPDevice(int maxReadBufferPackets, int maxWriteBufferPackets) {
		this.ioReadLock = new Object();
		this.ioWriteLock = new Object();
		this.readBuffer = new DatagramPacket[maxReadBufferPackets];
		this.writeBuffer = new DatagramPacket[maxWriteBufferPackets];
	}

	/**
	 * Prepares for sending and receiving data.
	 */
	public void bind(InetSocketAddress addr) throws SocketException {
		if (socket != null)
			throw new RuntimeException("Already bound.");
		socket = new DatagramSocket(addr);

		ioReadThread = new Thread("udpdeviceread-" + hashCode()) {
			@Override
			public void run() {
				try {
					byte[] personalBuffer = new byte[65535];
					while (true) {
						int space = 0;
						synchronized (ioReadLock) {
							space = readBuffer.length - readBufferLength;
						}

						DatagramPacket packet = new DatagramPacket(
								personalBuffer, personalBuffer.length);
						socket.receive(packet);
						if (space > 0) {
							// if there's not enough space the packet will get
							// dropped
							byte[] buf = new byte[packet.getLength()];
							System.arraycopy(personalBuffer, 0, buf, 0,
									packet.getLength());

							DatagramPacket received = new DatagramPacket(buf,
									0, buf.length, packet.getAddress(),
									packet.getPort());
							synchronized (ioReadLock) {
								readBuffer[readBufferLength++] = received;
							}
						}

						// try { Thread.sleep(20); }
						// catch (InterruptedException i) { }
					}
				} catch (IOException e) {
					Logger.handle(e);
				}
			}
		};
		ioReadThread.start();

		ioWriteThread = new Thread("udpdevicewrite-" + hashCode()) {
			@Override
			public void run() {
				try {
					DatagramPacket[] personalBuffer = new DatagramPacket[writeBuffer.length];
					while (true) {
						int length = 0;
						synchronized (ioWriteLock) {
							length = writeBufferLength;
							for (int i = 0; i < writeBufferLength; i++)
								personalBuffer[i] = writeBuffer[i];
							writeBufferLength = 0;
						}

						if (length > 0) {
							for (int i = 0; i < length; i++)
								socket.send(personalBuffer[i]);
						}

						try {
							Thread.sleep(20);
						} catch (InterruptedException i) {
						}
					}
				} catch (IOException e) {
					Logger.handle(e);
				}
			}
		};
		ioWriteThread.start();
	}

	/**
	 * Halt's all operations.
	 */

	public void unbind() {
		try {
			if (ioReadThread != null) {
				ioReadThread.interrupt();
				ioReadThread = null;
			}
			if (ioWriteThread != null) {
				ioWriteThread.interrupt();
				ioWriteThread = null;
			}
			if (socket != null) {
				socket.close();
				socket = null;
			}
			synchronized (ioReadLock) {
				readBufferLength = 0;
			}
			synchronized (ioWriteLock) {
				writeBufferLength = 0;
			}
		} catch (Throwable t) {
		}
	}

	/**
	 * Return's amount of packets buffered.
	 * 
	 * @return
	 */
	public int getBufferedAmount() {
		synchronized (ioReadLock) {
			return readBufferLength;
		}
	}

	/**
	 * Discard's all buffered packets.
	 */
	public void discardBuffer() {
		synchronized (ioReadLock) {
			readBufferLength = 0;
		}
	}

	/**
	 * Discard's given amount of packets.
	 */
	public int discardBuffer(int amount) {
		synchronized (ioReadLock) {
			int count = Math.min(amount, readBufferLength);
			for (int i = count, a = 0; i < readBufferLength; i++)
				readBuffer[a++] = readBuffer[i];
			readBufferLength -= count;
			return count;
		}
	}

	/**
	 * Tries to read next buffered packet, may be null if none was received yet.
	 */
	public DatagramPacket read() {
		synchronized (ioReadLock) {
			if (readBufferLength < 1)
				return null;
			DatagramPacket packet = readBuffer[0];
			for (int i = 1; i < readBufferLength; i++)
				readBuffer[i - 1] = readBuffer[i];
			readBufferLength -= 1;
			return packet;
		}
	}

	/**
	 * Buffer's packet to be sent.
	 */
	public void write(DatagramPacket packet) {
		synchronized (ioWriteLock) {
			int capacity = writeBuffer.length - writeBufferLength;
			if (capacity < 1)
				return; // drop packets, no capacity
			writeBuffer[writeBufferLength++] = packet;
		}
	}
}