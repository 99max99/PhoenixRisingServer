package net.kagani.cache.loaders;

import java.util.concurrent.ConcurrentHashMap;

import net.kagani.cache.Cache;
import net.kagani.stream.InputStream;

public final class ItemsContainerDefinitions {

	private int length;
	private int[] ids, amounts;

	private static final ConcurrentHashMap<Integer, ItemsContainerDefinitions> maps = new ConcurrentHashMap<Integer, ItemsContainerDefinitions>();

	public static void main(String[] args) throws Throwable {
		Cache.init();

		/*
		 * BufferedWriter writer = new BufferedWriter(new
		 * FileWriter("item_containers.txt")); for (int i = 0; i <
		 * Cache.STORE.getIndexes()[2].getLastFileId(5); i++) {
		 * ItemsContainerDefinitions def = getContainer(i);
		 * writer.write("RS Shop " + i + ":"); for (int a = 0; def.ids != null
		 * && a < def.length && a < def.ids.length; a++) writer.write(" " +
		 * def.ids[a] + " " + def.amounts[a]); writer.write("\r\n"); }
		 * writer.close();
		 */
		System.out.println(getContainer(623).length);
	}

	public static final ItemsContainerDefinitions getContainer(int id) {
		ItemsContainerDefinitions def = maps.get(id);
		if (def != null)
			return def;
		byte[] data = Cache.STORE.getIndexes()[2].getFile(5, id);
		def = new ItemsContainerDefinitions();
		if (data != null)
			def.decode(new InputStream(data));
		maps.put(id, def);
		return def;
	}

	private void decode(InputStream stream) {
		l: while (true) {
			switch (stream.readUnsignedByte()) {
			case 2:
				length = stream.readUnsignedShort();
				break;
			case 4:
				int size = stream.readUnsignedByte();
				ids = new int[size];
				amounts = new int[size];
				for (int i = 0; i < size; i++) {
					ids[i] = stream.readUnsignedShort();
					amounts[i] = stream.readUnsignedShort();
				}
				break;
			default:
				break l;
			}
		}
	}

}
