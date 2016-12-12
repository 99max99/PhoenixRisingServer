package net.kagani.game.player;

import net.kagani.cache.Cache;
import net.kagani.cache.loaders.VarBitDefinitions;

public class VarsManager {

	public static final int[] masklookup = new int[32];

	static {
		int i = 2;
		for (int i2 = 0; i2 < 32; i2++) {
			masklookup[i2] = i - 1;
			i += i;
		}
	}

	private int[] values;
	private Player player;

	public VarsManager(Player player) {
		this.player = player;
		values = new int[Cache.STORE.getIndexes()[2].getLastFileId(60) + 1];
	}

	public boolean sendVar(int id, int value) {
		return sendVar(id, value, false);
	}

	public void forceSendVar(int id, int value) {
		sendVar(id, value, true);
	}

	private boolean sendVar(int id, int value, boolean force) {
		if (id < 0 || id >= values.length) // temporarly
			return false;
		if (!force && values[id] == value)
			return false;
		setVar(id, value);
		sendVarPacket(id);
		return true;
	}

	public void setVar(int id, int value) {
		values[id] = value;
	}

	public int getValue(int id) {
		return values[id];
	}

	public void forceSendVarBit(int id, int value) {
		setVarBit(id, value, 0x1 | 0x2);
	}

	public boolean sendVarBit(int id, int value) {
		return setVarBit(id, value, 0x1);
	}

	public void setVarBit(int id, int value) {
		setVarBit(id, value, 0);
	}

	public int getBitValue(int id) {
		VarBitDefinitions defs = VarBitDefinitions.getVarBitDefinitions(id);
		return values[defs.baseVar] >> defs.startBit
				& masklookup[defs.endBit - defs.startBit];
	}

	private boolean setVarBit(int id, int value, int flag) {
		VarBitDefinitions defs = VarBitDefinitions.getVarBitDefinitions(id);
		int mask = masklookup[defs.endBit - defs.startBit];
		if (value < 0 || value > mask)
			value = 0;
		mask <<= defs.startBit;
		int varpValue = (values[defs.baseVar] & (mask ^ 0xffffffff) | value << defs.startBit
				& mask);
		if ((flag & 0x2) != 0 || varpValue != values[defs.baseVar]) {
			setVar(defs.baseVar, varpValue);
			if ((flag & 0x1) != 0)
				sendVarPacket(defs.baseVar);
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	private void sendVarPacket(int id) {
		player.getPackets().sendVar(id, values[id]);
	}
}