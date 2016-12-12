// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Utils.java

package net.kagani.cache.filestore.utils;

import java.math.BigInteger;

import net.kagani.cache.filestore.io.OutputStream;
import net.kagani.cache.filestore.store.Store;

public final class Utils {

	public static byte[] cryptRSA(byte data[], BigInteger exponent,
			BigInteger modulus) {
		return (new BigInteger(data)).modPow(exponent, modulus).toByteArray();
	}

	public static byte[] getArchivePacketData(int indexId, int archiveId,
			byte archive[]) {
		OutputStream stream = new OutputStream(archive.length + 4);
		stream.writeByte(indexId);
		stream.writeShort(archiveId);
		stream.writeByte(0);
		stream.writeInt(archive.length);
		int offset = 8;
		for (int index = 0; index < archive.length; index++) {
			if (offset == 512) {
				stream.writeByte(-1);
				offset = 1;
			}
			stream.writeByte(archive[index]);
			offset++;
		}

		byte packet[] = new byte[stream.getOffset()];
		stream.setOffset(0);
		stream.getBytes(packet, 0, packet.length);
		return packet;
	}

	public static int getNameHash(String name) {
		return name.toLowerCase().hashCode();
	}

	public static final int getInterfaceDefinitionsSize(Store store) {
		return store.getIndexes()[3].getLastArchiveId();
	}

	public static final int getInterfaceDefinitionsComponentsSize(Store store,
			int interfaceId) {
		return store.getIndexes()[3].getLastFileId(interfaceId);
	}

	public static final int getItemDefinitionsSize(Store store) {
		int lastArchiveId = store.getIndexes()[19].getLastArchiveId();
		return lastArchiveId * 256
				+ store.getIndexes()[19].getValidFilesCount(lastArchiveId);
	}

	private Utils() {
		
	}
}