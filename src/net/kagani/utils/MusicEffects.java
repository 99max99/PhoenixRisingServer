package net.kagani.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import net.kagani.cache.Cache;
import net.kagani.cache.filestore.io.InputStream;
import net.kagani.cache.loaders.ClientScriptMap;

public class MusicEffects {

	private static int[] musicEffectArchiveIds;
	private final static String PACKED_PATH = "data/musics/packedMusicEffects.me";

	public static final void init() {
		if (!new File(PACKED_PATH).exists())
			generateMusicEffectIds();
		loadMusicEffectIds();

	}

	public static int getArchiveId(int id) {
		if (id < 0 || id >= musicEffectArchiveIds.length)
			return -1;
		return musicEffectArchiveIds[id];
	}

	private static void loadMusicEffectIds() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			musicEffectArchiveIds = new int[buffer.limit() / 4];
			while (buffer.hasRemaining())
				musicEffectArchiveIds[buffer.getShort() & 0xffff] = buffer
						.getShort() & 0xffff;
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static void generateMusicEffectIds() {
		Logger.log(MusicEffects.class, "Generating music effect ids...");
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					PACKED_PATH));

			int i = 0;
			for (int archiveId : Cache.STORE.getIndexes()[40].getTable()
					.getValidArchiveIds()) {
				if (archiveId % 5000 == 0) {
					Cache.STORE.getIndexes()[40].resetCachedFiles();
					Cache.STORE.getIndexes()[40].getMainFile()
							.resetCachedArchives();
				}
				byte[] data = Cache.STORE.getIndexes()[40].getFile(archiveId);
				InputStream stream = new InputStream(data);
				if (stream.readUnsignedByte() != 74
						|| stream.readUnsignedByte() != 65
						|| stream.readUnsignedByte() != 71
						|| stream.readUnsignedByte() != 65)
					continue;
				if (ClientScriptMap.getMap(1351).getKeyForValue(archiveId) != -1) {
					continue;
				}
				out.writeShort(i++);
				out.writeShort(archiveId);
			}
			Cache.STORE.getIndexes()[40].resetCachedFiles();
			Cache.STORE.getIndexes()[40].getMainFile().resetCachedArchives();
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
