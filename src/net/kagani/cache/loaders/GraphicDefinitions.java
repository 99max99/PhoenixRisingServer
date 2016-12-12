package net.kagani.cache.loaders;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import net.kagani.cache.Cache;
import net.kagani.stream.InputStream;
import net.kagani.utils.Utils;

public class GraphicDefinitions {

	public short[] aShortArray1435;
	public short[] aShortArray1438;
	public int anInt1440;
	public boolean aBoolean1442;
	public int modelId;
	public int anInt1446;
	public boolean aBoolean1448 = false;
	public int anInt1449;
	public int emoteId;
	public int anInt1451;
	public int graphicsId;
	public int anInt1454;
	public short[] aShortArray1455;
	public short[] aShortArray1456;

	// added
	public byte byteValue;
	// added
	public int intValue;
	// added
	private byte[] aByteArray8432;
	private byte[] aByteArray8431;

	private static final ConcurrentHashMap<Integer, GraphicDefinitions> animDefs = new ConcurrentHashMap<Integer, GraphicDefinitions>();

	public static final GraphicDefinitions getGraphicDefinitions(int emoteId) {
		GraphicDefinitions defs = animDefs.get(emoteId);
		if (defs != null)
			return defs;
		byte[] data = Cache.STORE.getIndexes()[21].getFile(
				emoteId >>> 735411752, emoteId & 0xff);
		defs = new GraphicDefinitions();
		defs.graphicsId = emoteId;
		if (data != null)
			defs.readValueLoop(new InputStream(data));
		animDefs.put(emoteId, defs);
		return defs;
	}

	public static void main(String... s) {
		try {
			Cache.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(getGraphicDefinitions(3752).emoteId);
		// System.out.println(getGraphicByModel(82794));
		// System.out.println(ItemDefinitions.getItemDefinitions(29617).maleEquip1);

		for (int i = 19496 - 50; i < 19496 + 50; i++) {
			int gfx = getGfxByAnimation(i);
			if (gfx == -1)
				continue;
			System.out.println(i + ", " + gfx);
		}

		/*
		 * System.out.println(AnimationDefinitions.getAnimationDefinitions(366).
		 * leftHandItem);
		 * System.out.println(AnimationDefinitions.getAnimationDefinitions
		 * (366).rightHandItemId);
		 * System.out.println(ItemDefinitions.getItemDefinitions(2476).modelId);
		 * 
		 * System.out.println(getGfxByAnimation(23837));
		 */
		/*
		 * int gfx = getGfxByAnimation(anim); System.out.println(gfx);
		 */
	}

	public static int getAnimationBySound(int id, boolean effect2Sound) {
		for (int i = 0; i < Utils.getAnimationDefinitionsSize(); i++) {
			AnimationDefinitions defs = AnimationDefinitions
					.getAnimationDefinitions(i);
			if (defs == null || defs.handledSounds == null
					|| defs.effect2Sound != effect2Sound)
				continue;
			for (int[] sounds : defs.handledSounds) {
				if (sounds != null)
					for (int s : sounds)
						if (s == id)
							return i;
			}
		}
		return -1;
	}

	public static int getGraphicByModel(int id) {
		for (int i = 0; i < Utils.getGraphicDefinitionsSize(); i++) {
			GraphicDefinitions defs = getGraphicDefinitions(i);
			if (defs == null || defs.modelId != id)
				continue;
			System.out.println(i);
			// return i;
		}
		return -1;
	}

	public static int getGfxByAnimation(int id) {
		for (int i = 0; i < Utils.getGraphicDefinitionsSize(); i++) {
			GraphicDefinitions defs = getGraphicDefinitions(i);
			if (defs == null || defs.emoteId != id)
				continue;
			return i;
		}
		return -1;
	}

	public static final void main2(String... s) {
		try {
			Cache.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		GraphicDefinitions original = GraphicDefinitions
				.getGraphicDefinitions(1896);
		int model = original.modelId;// NPCDefinitions.getNPCDefinitions(3200).modelIds[0];
		int emoteId = original.emoteId;

		AnimationDefinitions defs = AnimationDefinitions
				.getAnimationDefinitions(emoteId);
		if (defs.handledSounds != null)
			for (int[] sounds : defs.handledSounds)
				if (sounds != null)
					System.out.print(Arrays.toString(sounds) + ", ");

		System.out.println(model + ", " + emoteId);
		int offset = 50;
		int offset2 = 50;
		for (int i = 0; i < Utils.getGraphicDefinitionsSize(); i++) {
			GraphicDefinitions def = GraphicDefinitions
					.getGraphicDefinitions(i);
			if (def == null)
				continue;
			if ((def.modelId >= model - offset && def.modelId <= model + offset)
			/*
			 * (def.emoteId >= emoteId - offset2 && def.emoteId <= emoteId +
			 * offset2)
			 */) {
				System.out.println("Possible match [id=" + i + ", model="
						+ def.modelId + "]." + ", " + def.emoteId + ", "
						+ def.anInt1446 + ", " + def.anInt1449 + ", "
						+ def.anInt1454);
				AnimationDefinitions defs2 = AnimationDefinitions
						.getAnimationDefinitions(def.emoteId);
				if (defs2.handledSounds != null) {
					for (int[] sounds : defs2.handledSounds) {
						if (sounds != null)
							System.out.print(Arrays.toString(sounds) + ", ");
					}
					System.out.println();
				}
			}
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

	private void readValues(InputStream buffer, int opcode) {
		do {
			if (1 == opcode)
				modelId = buffer.readBigSmart();
			else if (2 == opcode)
				emoteId = buffer.readBigSmart();
			else if (4 == opcode)
				anInt1446 = buffer.readUnsignedShort();
			else if (5 == opcode)
				anInt1449 = buffer.readUnsignedShort();
			else if (6 == opcode)
				anInt1454 = buffer.readUnsignedShort();
			else if (7 == opcode)
				anInt1440 = buffer.readUnsignedByte();
			else if (8 == opcode)
				anInt1451 = buffer.readUnsignedByte();
			else if (10 == opcode)
				aBoolean1442 = true;
			else if (9 == opcode) {
				byteValue = (byte) 3;
				intValue = 8224;
			} else if (opcode == 15) {
				byteValue = (byte) 3;
				intValue = buffer.readUnsignedShort();
			} else if (16 == opcode) {
				byteValue = (byte) 3;
				intValue = buffer.readInt();
			} else if (40 == opcode) {
				int i_60_ = buffer.readUnsignedByte();
				aShortArray1438 = new short[i_60_];
				aShortArray1456 = new short[i_60_];
				for (int i_61_ = 0; i_61_ < i_60_; i_61_++) {
					aShortArray1438[i_61_] = (short) buffer.readUnsignedShort();
					aShortArray1456[i_61_] = (short) buffer.readUnsignedShort();
				}
			} else if (41 == opcode) {
				int i_62_ = buffer.readUnsignedByte();
				aShortArray1455 = new short[i_62_];
				aShortArray1435 = new short[i_62_];
				for (int i_63_ = 0; i_63_ < i_62_; i_63_++) {
					aShortArray1455[i_63_] = (short) buffer.readUnsignedShort();
					aShortArray1435[i_63_] = (short) buffer.readUnsignedShort();
				}
			} else if (44 == opcode) {
				int i_64_ = buffer.readUnsignedShort();
				int i_65_ = 0;
				for (int i_66_ = i_64_; i_66_ > 0; i_66_ >>= 1)
					i_65_++;
				aByteArray8432 = new byte[i_65_];
				byte i_67_ = 0;
				for (int i_68_ = 0; i_68_ < i_65_; i_68_++) {
					if ((i_64_ & 1 << i_68_) > 0) {
						aByteArray8432[i_68_] = i_67_;
						i_67_++;
					} else
						aByteArray8432[i_68_] = (byte) -1;
				}
			} else if (opcode == 45) {
				int i_69_ = buffer.readUnsignedShort();
				int i_70_ = 0;
				for (int i_71_ = i_69_; i_71_ > 0; i_71_ >>= 1)
					i_70_++;
				aByteArray8431 = new byte[i_70_];
				byte i_72_ = 0;
				for (int i_73_ = 0; i_73_ < i_70_; i_73_++) {
					if ((i_69_ & 1 << i_73_) > 0) {
						aByteArray8431[i_73_] = i_72_;
						i_72_++;
					} else
						aByteArray8431[i_73_] = (byte) -1;
				}
			} else if (opcode == 46)
				break;
		} while (false);
	}

	/*
	 * public void readValues(InputStream stream, int opcode) { if (opcode != 1)
	 * { if ((opcode ^ 0xffffffff) == -3) emoteId = stream.readBigSmart(); else
	 * if (opcode == 4) anInt1446 = stream.readUnsignedShort(); else if (opcode
	 * != 5) { if ((opcode ^ 0xffffffff) != -7) { if (opcode == 7) anInt1440 =
	 * stream.readUnsignedByte(); else if ((opcode ^ 0xffffffff) == -9)
	 * anInt1451 = stream.readUnsignedByte(); else if (opcode != 9) { if (opcode
	 * != 10) { if (opcode == 11) { // added opcode // aBoolean1442 = true;
	 * byteValue = (byte) 1; } else if (opcode == 12) { // added opcode //
	 * aBoolean1442 = true; byteValue = (byte) 4; } else if (opcode == 13) { //
	 * added opcode // aBoolean1442 = true; byteValue = (byte) 5; } else if
	 * (opcode == 14) { // added opcode // aBoolean1442 = true; // aByte2856 =
	 * 2; byteValue = (byte) 2; intValue = stream.readUnsignedByte() * 256; }
	 * else if (opcode == 15) { // aByte2856 = 3; byteValue = (byte) 3; intValue
	 * = stream.readUnsignedShort(); } else if (opcode == 16) { // aByte2856 =
	 * 3; byteValue = (byte) 3; intValue = stream.readInt(); } else if (opcode
	 * == 44) { //rs3 new stream.readUnsignedShort(); //count } else if (opcode
	 * == 45) { //rs3 new stream.readUnsignedShort(); //count } else if (opcode
	 * == 46) { //rs3 new
	 * 
	 * } else if (opcode != 40) { if ((opcode ^ 0xffffffff) == -42) { int i =
	 * stream.readUnsignedByte(); aShortArray1455 = new short[i];
	 * aShortArray1435 = new short[i]; for (int i_0_ = 0; i > i_0_; i_0_++) {
	 * aShortArray1455[i_0_] = (short) (stream.readUnsignedShort());
	 * aShortArray1435[i_0_] = (short) (stream.readUnsignedShort()); } } } else
	 * { int i = stream.readUnsignedByte(); aShortArray1438 = new short[i];
	 * aShortArray1456 = new short[i]; for (int i_1_ = 0; ((i ^ 0xffffffff) <
	 * (i_1_ ^ 0xffffffff)); i_1_++) { aShortArray1438[i_1_] = (short)
	 * stream.readUnsignedShort(); aShortArray1456[i_1_] = (short)
	 * stream.readUnsignedShort(); } } } else aBoolean1448 = true; } else { //
	 * aBoolean1442 = true; byteValue = (byte) 3; intValue = 8224; } } else
	 * anInt1454 = stream.readUnsignedShort(); } else anInt1449 =
	 * stream.readUnsignedShort(); } else modelId = stream.readBigSmart(); }
	 */

	public GraphicDefinitions() {
		byteValue = 0;
		intValue = -1;
		anInt1446 = 128;
		aBoolean1442 = false;
		anInt1449 = 128;
		anInt1451 = 0;
		emoteId = -1;
		anInt1454 = 0;
		anInt1440 = 0;
	}

}
