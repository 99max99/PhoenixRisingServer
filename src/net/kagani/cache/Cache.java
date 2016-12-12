package net.kagani.cache;

import java.io.IOException;

import net.kagani.Settings;
import net.kagani.cache.filestore.io.OutputStream;
import net.kagani.cache.filestore.store.ReferenceTable;
import net.kagani.cache.filestore.store.Store;
import net.kagani.cache.filestore.util.whirlpool.Whirlpool;
import net.kagani.utils.Utils;

public final class Cache {

	public static Store STORE;

	private Cache() {

	}

	public static void init() throws IOException {
		ReferenceTable.NEW_PROTOCOL = true;
		STORE = new Store(Settings.CACHE_PATH);
	}

	public static final byte[] generateUkeysFile() {
		OutputStream stream = new OutputStream();
		stream.writeByte(STORE.getIndexes().length);
		for (int index = 0; index < STORE.getIndexes().length; index++) {
			if (STORE.getIndexes()[index] == null) {
				stream.writeInt(0);
				stream.writeInt(0);
				stream.writeInt(0);
				stream.writeInt(0);
				stream.writeBytes(new byte[64]);
				continue;
			}
			stream.writeInt(STORE.getIndexes()[index].getCRC());
			stream.writeInt(STORE.getIndexes()[index].getTable().getRevision());
			stream.writeInt(0);
			stream.writeInt(0);
			stream.writeBytes(STORE.getIndexes()[index].getWhirlpool());
			// Logger.log("Cache", "Whirlpool Length: " +
			// STORE.getIndexes()[index].getWhirlpool().length);
		}

		byte[] archive = new byte[stream.getOffset()];
		stream.setOffset(0);
		stream.getBytes(archive, 0, archive.length);
		OutputStream hashStream = new OutputStream(65);
		hashStream.writeByte(0);
		hashStream.writeBytes(Whirlpool.getHash(archive, 0, archive.length));

		byte[] hash = new byte[hashStream.getOffset()];
		hashStream.setOffset(0);
		hashStream.getBytes(hash, 0, hash.length);

		hash = Utils.cryptRSA(hash, Settings.GRAB_SERVER_PRIVATE_EXPONENT,
				Settings.GRAB_SERVER_MODULUS);
		// Logger.log("Cache", "Hash length: " + hash.length);
		stream.writeBytes(hash);
		archive = new byte[stream.getOffset()];
		stream.setOffset(0);
		stream.getBytes(archive, 0, archive.length);
		return archive;
	}
}