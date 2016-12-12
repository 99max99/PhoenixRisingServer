package net.kagani.cache.loaders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.kagani.Settings;
import net.kagani.cache.Cache;
import net.kagani.game.WorldTile;
import net.kagani.stream.InputStream;

public class WorldMap {

	public static void main(String[] args) throws IOException {
		Cache.init();
		/*
		 * Class572_Sub12_Sub5 class572_sub12_sub5 = new Class572_Sub12_Sub5(i,
		 * class572_sub15.readString(1295706626),
		 * class572_sub15.readString(1295706626),
		 * class572_sub15.readInt(629032666),
		 * class572_sub15.readInt(1917405109),
		 * (class572_sub15.readUnsignedByte(137368825) == 1),
		 * class572_sub15.readUnsignedByte(1921065850),
		 * class572_sub15.readUnsignedByte(508916645)); int i_35_ =
		 * class572_sub15.readUnsignedByte(108454788); for (int i_36_ = 0; i_36_
		 * < i_35_; i_36_++) ((Class572_Sub12_Sub5)
		 * class572_sub12_sub5).aClass675_11322 .method7940 (new
		 * Class572_Sub18(class572_sub15.readUnsignedByte(486686672),
		 * class572_sub15.readUnsignedShort(647518597),
		 * class572_sub15.readUnsignedShort(647518597),
		 * class572_sub15.readUnsignedShort(647518597),
		 * class572_sub15.readUnsignedShort(647518597),
		 * class572_sub15.readUnsignedShort(647518597),
		 * class572_sub15.readUnsignedShort(647518597),
		 * class572_sub15.readUnsignedShort(647518597),
		 * class572_sub15.readUnsignedShort(647518597)), -1802734914);
		 */

		File file = new File("coordsList.txt"); // = new
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.append("//Version = " + Settings.MAJOR_VERSION);
		writer.newLine();
		writer.flush();

		for (int i : Cache.STORE.getIndexes()[23].getTable().getArchives()[0]
				.getValidFileIds()) {
			byte[] data = Cache.STORE.getIndexes()[23].getFile(0, i);
			// byte[] data2 = Cache.STORE.getIndexes()[23].getFile(1, i);

			InputStream stream = new InputStream(data);

			String mapName = stream.readString();
			String areaName = stream.readString();

			WorldTile tile = new WorldTile(stream.readInt());

			int unknown1 = stream.readInt();
			boolean useMap = stream.readUnsignedByte() == 1;
			int unknown2 = stream.readUnsignedByte();
			int unknown3 = stream.readUnsignedByte();

			// File("information/itemlist.txt");

			writer.append("Map: " + mapName + " | Area: " + areaName
					+ " -  Coords: " + tile.toString());
			writer.newLine();
			writer.flush();

			System.out.println(i + ", map: " + mapName + ", area: " + areaName
					+ ", coords: " + tile.toString());
		}
		writer.close();
		// System.out.println(Cache.STORE.getIndexes()[23].getLastArchiveId());
	}

	public static int getDetailsArchiveId() {
		return Cache.STORE.getIndexes()[23].getArchiveId("details");
	}

}
