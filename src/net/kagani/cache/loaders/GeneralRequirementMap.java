package net.kagani.cache.loaders;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import net.kagani.cache.Cache;
import net.kagani.stream.InputStream;

public final class GeneralRequirementMap {

	private HashMap<Long, Object> values;
	private int id;

	private static final ConcurrentHashMap<Integer, GeneralRequirementMap> maps = new ConcurrentHashMap<Integer, GeneralRequirementMap>();

	public static void main(String[] args) throws IOException {

		Cache.init();
		System.out.println(Cache.STORE.getIndexes()[22].getLastFileId(10));
		for (int i = 0; i < 100000; i++) {
			GeneralRequirementMap map = getMap(i);
			if (map == null)
				continue;

			if (map.getIntValue(17648) != 0)
				System.out.println(i + " - " + map.getValues().toString());
		}

	}

	public static final GeneralRequirementMap getMap(int scriptId) {
		GeneralRequirementMap script = maps.get(scriptId);
		if (script != null)
			return script;
		byte[] data = Cache.STORE.getIndexes()[22].getFile(scriptId / 32,
				scriptId & 31);
		script = new GeneralRequirementMap();
		if (data != null)
			script.readValueLoop(new InputStream(data));
		script.id = scriptId;
		maps.put(scriptId, script);
		return script;

	}

	public HashMap<Long, Object> getValues() {
		return values;
	}

	public Object getValue(long key) {
		if (values == null)
			return null;
		return values.get(key);
	}

	public long getKeyForValue(Object value) {
		for (Long key : values.keySet()) {
			if (values.get(key).equals(value))
				return key;
		}
		return -1;
	}

	public int getSize() {
		if (values == null)
			return 0;
		return values.size();
	}

	public int getIntValue(long key) {
		if (values == null)
			return 0;
		Object value = values.get(key);
		if (value == null || !(value instanceof Integer))
			return 0;
		return (Integer) value;
	}

	public String getStringValue(long key) {
		if (values == null)
			return "";
		Object value = values.get(key);
		if (value == null || !(value instanceof String))
			return "";
		return (String) value;
	}

	private void readValueLoop(InputStream stream) {
		for (;;) {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				break;
			readValues(stream, opcode);
		}
	}

	private void readValues(InputStream stream, int opcode) {
		if (opcode == 249) {
			int length = stream.readUnsignedByte();
			if (values == null)
				values = new HashMap<Long, Object>(length);
			for (int index = 0; index < length; index++) {
				boolean stringInstance = stream.readUnsignedByte() == 1;
				int key = stream.read24BitInt();
				Object value = stringInstance ? stream.readString() : stream
						.readInt();
				values.put((long) key, value);
			}
		}
	}

	public int getId() {
		return id;
	}

	private GeneralRequirementMap() {

	}
}