package net.kagani.cache.loaders;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import net.kagani.cache.Cache;
import net.kagani.stream.InputStream;
import net.kagani.utils.Utils;

public class AnimationDefinitions {

	public int anInt2136;
	public int anInt2137;
	public int[] anIntArray2139;
	public int anInt2140;
	public boolean aBoolean2141 = false;
	public int anInt2142;
	public int leftHandItem;
	public int rightHandItem = -1;
	public int[][] handledSounds;
	public boolean[] aBooleanArray2149;
	public int[] anIntArray2151;
	public boolean aBoolean2152;
	public int[] anIntArray2153;
	public int anInt2155;
	public boolean aBoolean2158;
	public boolean aBoolean2159;
	public int anInt2162;
	public int anInt2163;

	// added
	public int[] soundMinDelay;
	public int[] soundMaxDelay;
	public int[] anIntArray1362;
	public boolean effect2Sound;
	public int id;

	public HashMap<Integer, Object> clientScriptData;

	private static final ConcurrentHashMap<Integer, AnimationDefinitions> animDefs = new ConcurrentHashMap<Integer, AnimationDefinitions>();

	public static void main(String[] args) throws IOException {
		Cache.init();

		for (int i = 0; i < Utils.getAnimationDefinitionsSize(); i++) {
			AnimationDefinitions defs = getAnimationDefinitions(i);
			if (defs == null)
				continue;
			if (ItemDefinitions.getItemDefinitions(defs.rightHandItem).name
					.toLowerCase().contains("null"))
				if (defs.leftHandItem != -1 && defs.leftHandItem != 65535)
					System.out.println("anim:" + i + " uses item "
							+ defs.rightHandItem);
		}
		for (int i = 0; i < Utils.getAnimationDefinitionsSize(); i++) {
			AnimationDefinitions defs = getAnimationDefinitions(i);
			if (defs == null)
				continue;
			if (defs.clientScriptData != null) {
				System.out.println(i + ", " + defs.clientScriptData);
			}
		}

		/*
		 * System.out.println(Arrays.toString(getAnimationDefinitions(23948).
		 * anIntArray2153));
		 * System.out.println(Arrays.toString(getAnimationDefinitions
		 * (4230).anIntArray2153));
		 * 
		 * ItemDefinitions defs = ItemDefinitions.getItemDefinitions(18481);
		 * GeneralRequirementMap map = defs.getCombatMap(); if (map != null) {
		 * for (long val : map.getValues().keySet()) { System.out.println(val +
		 * " = " + map.getIntValue(val)/4); } }
		 * 
		 * defs = ItemDefinitions.getItemDefinitions(25995); map =
		 * defs.getCombatMap(); if (map != null) { for (long val :
		 * map.getValues().keySet()) { System.err.println(val + " = " +
		 * map.getIntValue(val)/4); } }
		 */

		/*
		 * for (int i = 0; i < Utils.getAnimationDefinitionsSize(); i++) {
		 * AnimationDefinitions defs = getAnimationDefinitions(i); if (defs ==
		 * null) continue; if (defs.leftHandItem == 6739) {
		 * System.out.println("anim:" + i + " is playeranim"); buffer[size++] =
		 * i; } }
		 * 
		 * for (int i = 0; i < 50000; i++) { RenderAnimDefinitions defs =
		 * RenderAnimDefinitions.getRenderAnimDefinitions(i); if (defs == null)
		 * continue;
		 * 
		 * for (int x = 0; x < size; x++) { int anim = buffer[x]; if
		 * (defs.walkAnimation == anim || defs.runAnimation == anim ||
		 * defs.defaultStandAnimation == anim || defs.walkBackwardsAnimation ==
		 * anim || defs.walkLeftAnimation == anim || defs.walkRightAnimation ==
		 * anim || defs.walkUpwardsAnimation == anim) {
		 * System.out.println("Render:" + i + " uses anim " + anim); break; } }
		 * }
		 */

	}

	public static final AnimationDefinitions getAnimationDefinitions(int emoteId) {
		try {
			AnimationDefinitions defs = animDefs.get(emoteId);
			if (defs != null)
				return defs;
			byte[] data = Cache.STORE.getIndexes()[20].getFile(emoteId >>> 7,
					emoteId & 0x7f);
			defs = new AnimationDefinitions();
			defs.id = emoteId;
			if (data != null)
				defs.readValueLoop(new InputStream(data));
			defs.method2394();
			animDefs.put(emoteId, defs);
			return defs;
		} catch (Throwable t) {
			return null;
		}
	}

	private void readValueLoop(InputStream stream) {
		for (;;) {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				break;
			readValues(stream, opcode);
		}
	}

	public int getEmoteTime() {
		if (anIntArray2153 == null)
			return 0;
		int ms = 0;
		for (int i : anIntArray2153)
			ms += i;
		return ms * 10;
	}

	public int getEmoteClientCycles() {
		if (anIntArray2153 == null)
			return 0;
		int r = 0;
		for (int i = 0; i < anIntArray2153.length - 3; i++) {
			r += anIntArray2153[i];
		}
		return r;
	}

	private void readValues(InputStream buffer, int opcode) {
		if (opcode == 1) {
			int i_3_ = buffer.readUnsignedShort();
			anIntArray2153 = new int[i_3_];
			for (int i_4_ = 0; i_4_ < i_3_; i_4_++)
				anIntArray2153[i_4_] = buffer.readUnsignedShort();
			anIntArray2139 = new int[i_3_];
			for (int i_5_ = 0; i_5_ < i_3_; i_5_++)
				anIntArray2139[i_5_] = buffer.readUnsignedShort();
			for (int i_6_ = 0; i_6_ < i_3_; i_6_++)
				anIntArray2139[i_6_] = (buffer.readUnsignedShort() << 16)
						+ anIntArray2139[i_6_];
		} else if (opcode == 2)
			anInt2136 = buffer.readUnsignedShort();
		else if (5 == opcode)
			anInt2142 = buffer.readUnsignedByte();
		else if (opcode == 6)
			rightHandItem = buffer.readUnsignedShort();
		else if (opcode == 7)
			leftHandItem = buffer.readUnsignedShort();
		else if (8 == opcode)
			anInt2155 = buffer.readUnsignedByte();
		else if (9 == opcode)
			anInt2140 = buffer.readUnsignedByte();
		else if (opcode == 10)
			anInt2162 = buffer.readUnsignedByte();
		else if (11 == opcode)
			anInt2155 = buffer.readUnsignedByte();
		else if (12 == opcode) {
			int i_7_ = buffer.readUnsignedByte();
			anIntArray2151 = new int[i_7_];
			for (int i_8_ = 0; i_8_ < i_7_; i_8_++)
				anIntArray2151[i_8_] = buffer.readUnsignedShort();
			for (int i_9_ = 0; i_9_ < i_7_; i_9_++)
				anIntArray2151[i_9_] = (buffer.readUnsignedShort() << 16)
						+ anIntArray2151[i_9_];
		} else if (13 == opcode) {
			int i_10_ = buffer.readUnsignedShort();
			handledSounds = new int[i_10_][];
			for (int i_11_ = 0; i_11_ < i_10_; i_11_++) {
				int i_12_ = buffer.readUnsignedByte();
				if (i_12_ > 0) {
					handledSounds[i_11_] = new int[i_12_];
					handledSounds[i_11_][0] = buffer.read24BitInt();
					for (int i_13_ = 1; i_13_ < i_12_; i_13_++)
						handledSounds[i_11_][i_13_] = buffer
								.readUnsignedShort();
				}
			}
		} else if (opcode == 14)
			aBoolean2158 = true;
		else if (opcode == 15)
			aBoolean2159 = true;
		else if (16 != opcode && opcode != 18) {
			if (opcode == 19) {
				if (null == anIntArray1362) {
					anIntArray1362 = new int[handledSounds.length];
					for (int i_14_ = 0; i_14_ < handledSounds.length; i_14_++)
						anIntArray1362[i_14_] = 255;
				}
				anIntArray1362[buffer.readUnsignedByte()] = buffer
						.readUnsignedByte();
			} else if (20 == opcode) {
				if (soundMaxDelay == null || null == soundMinDelay) {
					soundMaxDelay = new int[handledSounds.length];
					soundMinDelay = new int[handledSounds.length];
					for (int i_15_ = 0; i_15_ < handledSounds.length; i_15_++) {
						soundMaxDelay[i_15_] = 256;
						soundMinDelay[i_15_] = 256;
					}
				}
				int i_16_ = buffer.readUnsignedByte();
				soundMaxDelay[i_16_] = buffer.readUnsignedShort();
				soundMinDelay[i_16_] = buffer.readUnsignedShort();
			} else if (22 == opcode)
				buffer.readUnsignedByte();
			else if (23 == opcode)
				buffer.readUnsignedShort();
			else if (24 == opcode)
				buffer.readUnsignedShort();
			else if (opcode == 249) {
				int length = buffer.readUnsignedByte();
				if (clientScriptData == null)
					clientScriptData = new HashMap<Integer, Object>(length);
				for (int index = 0; index < length; index++) {
					boolean stringInstance = buffer.readUnsignedByte() == 1;
					int key = buffer.read24BitInt();
					Object value = stringInstance ? buffer.readString()
							: buffer.readInt();
					clientScriptData.put(key, value);
				}
			}
		}
	}

	/*
	 * private void readValues(InputStream stream, int opcode) { if ((opcode ^
	 * 0xffffffff) == -2) { int i = stream.readUnsignedShort(); anIntArray2153 =
	 * new int[i]; for (int i_16_ = 0; (i ^ 0xffffffff) < (i_16_ ^ 0xffffffff);
	 * i_16_++) anIntArray2153[i_16_] = stream.readUnsignedShort();
	 * anIntArray2139 = new int[i]; for (int i_17_ = 0; (i_17_ ^ 0xffffffff) >
	 * (i ^ 0xffffffff); i_17_++) anIntArray2139[i_17_] =
	 * stream.readUnsignedShort(); for (int i_18_ = 0; i_18_ < i; i_18_++)
	 * anIntArray2139[i_18_] = ((stream.readUnsignedShort() << 16) +
	 * anIntArray2139[i_18_]); } else if ((opcode ^ 0xffffffff) != -3) { if
	 * ((opcode ^ 0xffffffff) != -4) { if ((opcode ^ 0xffffffff) == -5)
	 * aBoolean2152 = true; else if (opcode == 5) anInt2142 =
	 * stream.readUnsignedByte(); else if (opcode != 6) { if ((opcode ^
	 * 0xffffffff) == -8) leftHandItem = stream.readUnsignedShort(); else if
	 * ((opcode ^ 0xffffffff) != -9) { if (opcode != 9) { if ((opcode ^
	 * 0xffffffff) != -11) { if ((opcode ^ 0xffffffff) == -12) anInt2155 =
	 * stream.readUnsignedByte(); else if (opcode == 12) { int i =
	 * stream.readUnsignedByte(); anIntArray2151 = new int[i]; for (int i_19_ =
	 * 0; ((i_19_ ^ 0xffffffff) > (i ^ 0xffffffff)); i_19_++)
	 * anIntArray2151[i_19_] = stream.readUnsignedShort(); for (int i_20_ = 0; i
	 * > i_20_; i_20_++) anIntArray2151[i_20_] = ((stream.readUnsignedShort() <<
	 * 16) + anIntArray2151[i_20_]); } else if ((opcode ^ 0xffffffff) != -14) {
	 * if (opcode != 14) { if (opcode != 15) { if (opcode == 16) aBoolean2158 =
	 * true; // added opcode else if (opcode == 17) {
	 * 
	 * @SuppressWarnings("unused") int anInt2145 = stream.readUnsignedByte(); //
	 * added opcode } else if (opcode == 18) { effect2Sound = true; } else if
	 * (opcode == 19) { if (anIntArray1362 == null) { anIntArray1362 = new
	 * int[handledSounds.length]; for (int index = 0; index <
	 * handledSounds.length; index++) anIntArray1362[index] = 255; }
	 * anIntArray1362[stream.readUnsignedByte()] = stream.readUnsignedByte(); //
	 * added opcode } else if (opcode == 20) { if ((soundMaxDelay == null) ||
	 * (soundMinDelay == null)) { soundMaxDelay = (new
	 * int[handledSounds.length]); soundMinDelay = (new
	 * int[handledSounds.length]); for (int i_34_ = 0; (i_34_ <
	 * handledSounds.length); i_34_++) { soundMaxDelay[i_34_] = 256;
	 * soundMinDelay[i_34_] = 256; } } int index = stream.readUnsignedByte();
	 * soundMaxDelay[index] = stream.readUnsignedShort(); soundMinDelay[index] =
	 * stream.readUnsignedShort(); } else if (opcode == 22) { //rs3 added
	 * stream.readUnsignedByte(); //idk } else if (opcode == 23) { //rs3 added
	 * stream.readUnsignedByte(); //unused } else if (opcode == 24) { //rs3
	 * added stream.readUnsignedShort(); //idk, calls for data from cache. idx
	 * 2, 77 } else if (opcode == 249) { int length = stream.readUnsignedByte();
	 * if (clientScriptData == null) clientScriptData = new HashMap<Integer,
	 * Object>(length); for (int index = 0; index < length; index++) { boolean
	 * stringInstance = stream.readUnsignedByte() == 1; int key =
	 * stream.read24BitInt(); Object value = stringInstance ?
	 * stream.readString() : stream.readInt(); clientScriptData.put(key, value);
	 * } }
	 * 
	 * } else aBoolean2159 = true; } else aBoolean2141 = true; } else { //
	 * opcode 13 int i = stream.readUnsignedShort(); handledSounds = new
	 * int[i][]; for (int i_21_ = 0; i_21_ < i; i_21_++) { int i_22_ =
	 * stream.readUnsignedByte(); if ((i_22_ ^ 0xffffffff) < -1) {
	 * handledSounds[i_21_] = new int[i_22_]; handledSounds[i_21_][0] =
	 * stream.readUnsignedShort(); for (int i_23_ = 1; ((i_22_ ^ 0xffffffff) <
	 * (i_23_ ^ 0xffffffff)); i_23_++) { handledSounds[i_21_][i_23_] =
	 * stream.readUnsignedShort(); } } } } } else anInt2162 =
	 * stream.readUnsignedByte(); } else anInt2140 = stream.readUnsignedByte();
	 * } else anInt2136 = stream.readUnsignedByte(); } else rightHandItemId =
	 * stream.readUnsignedShort(); } else { aBooleanArray2149 = new
	 * boolean[256]; int i = stream.readUnsignedByte(); for (int i_24_ = 0; (i ^
	 * 0xffffffff) < (i_24_ ^ 0xffffffff); i_24_++)
	 * aBooleanArray2149[stream.readUnsignedByte()] = true; } } else anInt2163 =
	 * stream.readUnsignedShort(); }
	 */

	public void method2394() {
		if (anInt2140 == -1) {
			if (aBooleanArray2149 == null)
				anInt2140 = 0;
			else
				anInt2140 = 2;
		}
		if (anInt2162 == -1) {
			if (aBooleanArray2149 == null)
				anInt2162 = 0;
			else
				anInt2162 = 2;
		}
	}

	public AnimationDefinitions() {
		anInt2136 = 99;
		leftHandItem = -1;
		anInt2140 = -1;
		aBoolean2152 = false;
		anInt2142 = 5;
		aBoolean2159 = false;
		anInt2163 = -1;
		anInt2155 = 2;
		aBoolean2158 = false;
		anInt2162 = -1;
	}

}
