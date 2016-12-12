package net.kagani.cache.loaders;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import net.kagani.cache.Cache;
import net.kagani.game.player.VarsManager;
import net.kagani.stream.InputStream;

public final class VarBitDefinitions {

	private static final ConcurrentHashMap<Integer, VarBitDefinitions> varpbitDefs = new ConcurrentHashMap<Integer, VarBitDefinitions>();

	public int id;
	public int baseVar;
	public int startBit;
	public int endBit;
	public int varDomain;

	// var 1772 / 1773 / 1774 / 1775 - chat

	// player.getVarsManager().sendVar(3680, 204557)
	// 212749

	public static final void main(String[] args) throws IOException {
		Cache.init();
		System.out.println("There are currently: "
				+ Cache.STORE.getIndexes()[2].getLastFileId(69)
				+ " bitConfigs.");
		// List<BitConfigDefinitions> configs = new //4127
		// ArrayList<BitConfigDefinitions>();
		for (int i = 0; i < Cache.STORE.getIndexes()[2].getLastFileId(69) + 1; i++) {
			VarBitDefinitions cd = getVarBitDefinitions(i);
			/*
			 * if (i == 26172) {
			 * System.out.println("REVERSED: "+cd.baseVar+", "+cd.id); }
			 */
			if (cd.baseVar == 841) {
				int value = 1048576;
				int v = value >> cd.startBit
						& VarsManager.masklookup[cd.endBit - cd.startBit];
				System.out.println("BitConfig " + cd.getVarDomainName() + ": "
						+ i + ", from bitshift:" + cd.startBit
						+ ", till bitshift: " + cd.endBit + ", " + cd.baseVar
						+ ", " + v);
				// VarBitDefinitions defs =
				// VarBitDefinitions.getClientVarpBitDefinitions(id);

			}
		}
		// VarBitDefinitions cd = getClientVarpBitDefinitions(2101);
		// System.out.println("from bitshift:" + cd.startBit +
		// ", till bitshift: " + cd.endBit + ", " +
		// cd.baseVar+", "+cd.getVarDomainName());
	}

	public static final VarBitDefinitions getVarBitDefinitions(int id) {
		VarBitDefinitions script = varpbitDefs.get(id);
		if (script != null)// open new txt document
			return script;
		byte[] data = Cache.STORE.getIndexes()[2].getFile(69, id);
		script = new VarBitDefinitions();
		script.id = id;
		if (data != null)
			script.readValueLoop(new InputStream(data));
		varpbitDefs.put(id, script);
		return script;

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
		if (opcode == 1) {
			varDomain = stream.readUnsignedByte();
			baseVar = stream.readBigSmart();
			// System.out.println(varDomain);
		} else if (opcode == 2) {
			startBit = stream.readUnsignedByte();
			endBit = stream.readUnsignedByte();
		}
	}

	private VarBitDefinitions() {

	}

	private String getVarDomainName() {
		switch (varDomain) {
		case 0:
			return "PLAYER";
		case 1:
			return "NPC";
		case 2:
			return "CLIENT";
		case 3:
			return "WORLD";
		case 4:
			return "REGION";
		case 5:
			return "OBJECT";
		case 6:
			return "CLAN";
		case 7:
			return "CLAN SETTING";
		default:
			return "UNKNOWN(" + varDomain + ")";
		}

	}
}
