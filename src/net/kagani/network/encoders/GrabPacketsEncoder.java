package net.kagani.network.encoders;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import net.kagani.Settings;
import net.kagani.cache.Cache;
import net.kagani.grab.ArchiveRequest;
import net.kagani.network.Session;
import net.kagani.stream.OutputStream;

public final class GrabPacketsEncoder extends Encoder {

	private static byte[] F255_255;

	private int encryptionValue;

	public GrabPacketsEncoder(Session connection) {
		super(connection);
	}

	public final void sendOutdatedClientPacket() {
		OutputStream stream = new OutputStream(1);
		stream.writeByte(6);
		ChannelFuture future = session.write(stream);
		if (future != null)
			future.addListener(ChannelFutureListener.CLOSE);
		else
			session.getChannel().close();
	}

	public final void sendStartUpPacket() {
		OutputStream stream = new OutputStream(
				1 + Settings.GRAB_SERVER_KEYS.length * 4);
		stream.writeByte(0);
		for (int key : Settings.GRAB_SERVER_KEYS)
			stream.writeInt(key);
		session.write(stream);
	}

	public final void sendCacheArchiveWeb(ArchiveRequest request) {
		byte[] archive = getArchive(request.getIndex(), request.getArchive());
		if (archive == null) { // test
			// Logger.log(GrabPacketsEncoder.class,
			// "null web archive request : "+request.getIndex()+", "+request.getArchive());
			return;
		}
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		int length = ((archive[1] & 0xff) << 24) + ((archive[2] & 0xff) << 16)
				+ ((archive[3] & 0xff) << 8) + (archive[4] & 0xff) + 5;
		if (archive[0] != 0)
			length += 4;
		// header
		buffer.writeBytes(("HTTP/1.1 200 OK" + "\n" + "Content-Type: text/html"
				+ "\n" + "Content-Length: " + length + "\n\n").getBytes());
		// content
		buffer.writeBytes(archive, 0, length);
		session.write(buffer).addListener(ChannelFutureListener.CLOSE);
	}

	public final boolean sendCacheArchive(ArchiveRequest request,
			boolean priority) {
		byte[] archive = getArchive(request.getIndex(), request.getArchive());
		if (archive == null) { // test
			// Logger.log(GrabPacketsEncoder.class,
			// "null js5 archive request : "+request.getIndex()+", "+request.getArchive());
			return true;
		}
		ChannelBuffer buffer = ChannelBuffers.buffer(512);
		int length = ((archive[1] & 0xff) << 24) + ((archive[2] & 0xff) << 16)
				+ ((archive[3] & 0xff) << 8) + (archive[4] & 0xff) + 5;
		if (archive[0] != 0)
			length += 4;
		buffer.writeByte(request.getIndex());
		buffer.writeInt(request.getArchive() | (!priority ? ~0x7fffffff : 0));

		for (int index = request.getBytesSent(); index < length; index++) {
			buffer.writeByte(archive[index]);
			if (buffer.writerIndex() == 512 || index == length - 1) {
				if (encryptionValue != 0) {
					for (int i = 0; i < buffer.writerIndex(); i++)
						buffer.setByte(i, buffer.getByte(i) ^ encryptionValue);
				}
				request.setBytesSent(index + 1);
				session.write(buffer);
				return request.getBytesSent() == length;
			}
		}
		return false;
	}

	public void setEncryptionValue(int encryptionValue) {
		this.encryptionValue = encryptionValue;
	}

	public int getEncryptionValue() {
		return encryptionValue;
	}

	private static byte[] getArchive(int index, int id) {
		return index == 255 && id == 255 ? getArchive255_255()
				: (index == 255 ? Cache.STORE.getIndex255() : Cache.STORE
						.getIndexes()[index].getMainFile())
						.getCachedArchiveData(id);
	}

	private static byte[] getArchive255_255() {
		if (F255_255 == null) {
			byte[] file = Cache.generateUkeysFile();
			OutputStream stream = new OutputStream();
			stream.writeByte(0);
			stream.writeInt(file.length);
			stream.writeBytes(file);
			byte[] data = new byte[stream.getOffset()];
			// System.out.println("Archive 255 Data: " + Arrays.toString(data));
			stream.setOffset(0);
			stream.getBytes(data, 0, data.length);
			F255_255 = data;
		}
		return F255_255;
	}

}