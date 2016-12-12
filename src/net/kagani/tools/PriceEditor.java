package net.kagani.tools;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import net.kagani.cache.Cache;
import net.kagani.cache.filestore.io.OutputStream;
import net.kagani.cache.loaders.ItemDefinitions;

public class PriceEditor {

	private static long[] cachedValues;
	private static String[] cachedNames;
	private static int nextSlot;

	public static void main(String[] args) {
		try {
			Cache.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		nextSlot = 0;
		cachedValues = new long[5000];

		Scanner kbdscan = new Scanner(System.in);
		System.out.print("Enter command [use /help for instructions]: ");

		while (kbdscan.hasNext()) {

			String input = kbdscan.next();

			if (input.equalsIgnoreCase("insert")) {
				int item = -1;
				System.out.print("\nEnter item id: ");
				item = kbdscan.nextInt();

				if (item == -1)
					break;
				ItemDefinitions def = ItemDefinitions.getItemDefinitions(item);
				System.out.print("\nSelect next action for [" + def.getName()
						+ "]: ");
				switch (kbdscan.next()) {
				case "name":
					System.out.print("\nEnter new name: ");
					// cachedNames[nextSlot++] = item << 16 |
					// kbdscan.next().getBytes(); TODO
					break;
				case "price":
					System.out.print("\nEnter new price: ");
					cachedValues[nextSlot++] = item >> 16 | kbdscan.nextInt();
					break;
				}
			} else if (input.equalsIgnoreCase("exit")) {
				for (long hash : cachedValues) {
					if (hash == 0)
						continue;
					long item = hash << 16;
					long price = hash & 0xFF;

					System.out.println((int) item);

					ItemDefinitions def = ItemDefinitions
							.getItemDefinitions((int) item);
					OutputStream stream = new OutputStream();

					Arrays.toString(def.getData());

					// stream.writeBytes(def.getData());

					// Cache.STORE.getIndexes()[19].putFile(item >>> 8, item &
					// 0xff, data);
				}
				System.exit(0);
			}
		}

		kbdscan.close();
	}

	public static class EditableItemDefinitions {

		public void encode() {
			/*
			 * if (opcode == 1) modelId = stream.readBigSmart(); else if (opcode
			 * == 2) name = stream.readString(); else if (opcode == 4) modelZoom
			 * = stream.readUnsignedShort(); else if (opcode == 5)
			 * modelRotation1 = stream.readUnsignedShort(); else if (opcode ==
			 * 6) modelRotation2 = stream.readUnsignedShort(); else if (opcode
			 * == 7) { modelOffset1 = stream.readUnsignedShort(); if
			 * (modelOffset1 > 32767) modelOffset1 -= 65536; modelOffset1 <<= 0;
			 * } else if (opcode == 8) { modelOffset2 =
			 * stream.readUnsignedShort(); if (modelOffset2 > 32767)
			 * modelOffset2 -= 65536; modelOffset2 <<= 0; } else if (opcode ==
			 * 11) stackable = 1; else if (opcode == 12) value =
			 * stream.readInt(); else if (opcode == 13) { equipSlot =
			 * stream.readUnsignedByte(); } else if (opcode == 14) { equipType =
			 * stream.readUnsignedByte(); } else if (opcode == 16) membersOnly =
			 * true; else if (opcode == 18) { // added
			 * stream.readUnsignedShort(); } else if (opcode == 23) maleEquip1 =
			 * stream.readBigSmart(); else if (opcode == 24) maleEquip2 =
			 * stream.readBigSmart(); else if (opcode == 25) femaleEquip1 =
			 * stream.readBigSmart(); else if (opcode == 26) femaleEquip2 =
			 * stream.readBigSmart(); else if (opcode == 27)
			 * stream.readUnsignedByte(); else if (opcode >= 30 && opcode < 35)
			 * groundOptions[opcode - 30] = stream.readString(); else if (opcode
			 * >= 35 && opcode < 40) inventoryOptions[opcode - 35] =
			 * stream.readString(); else if (opcode == 40) { int length =
			 * stream.readUnsignedByte(); originalModelColors = new int[length];
			 * modifiedModelColors = new int[length]; for (int index = 0; index
			 * < length; index++) { originalModelColors[index] =
			 * stream.readUnsignedShort(); modifiedModelColors[index] =
			 * stream.readUnsignedShort(); } } else if (opcode == 41) { int
			 * length = stream.readUnsignedByte(); originalTextureColors = new
			 * short[length]; modifiedTextureColors = new short[length]; for
			 * (int index = 0; index < length; index++) {
			 * originalTextureColors[index] = (short)
			 * stream.readUnsignedShort(); modifiedTextureColors[index] =
			 * (short) stream.readUnsignedShort(); } } else if (opcode == 42) {
			 * int length = stream.readUnsignedByte(); unknownArray1 = new
			 * byte[length]; for (int index = 0; index < length; index++)
			 * unknownArray1[index] = (byte) stream.readByte(); } else if
			 * (opcode == 44) { int length = stream.readUnsignedShort(); int
			 * arraySize = 0; for (int modifier = 0; modifier > 0; modifier++) {
			 * arraySize++; unknownArray3 = new byte[arraySize]; byte offset =
			 * 0; for (int index = 0; index < arraySize; index++) { if ((length
			 * & 1 << index) > 0) { unknownArray3[index] = offset; } else {
			 * unknownArray3[index] = -1; } } } } else if (45 == opcode) { int
			 * i_97_ = (short) stream.readUnsignedShort(); int i_98_ = 0; for
			 * (int i_99_ = i_97_; i_99_ > 0; i_99_ >>= 1) i_98_++;
			 * unknownArray6 = new byte[i_98_]; byte i_100_ = 0; for (int i_101_
			 * = 0; i_101_ < i_98_; i_101_++) { if ((i_97_ & 1 << i_101_) > 0) {
			 * unknownArray6[i_101_] = i_100_; i_100_++; } else
			 * unknownArray6[i_101_] = (byte) -1; } } else if (opcode == 65)
			 * unnoted = true; else if (opcode == 78) maleEquipModelId3 =
			 * stream.readBigSmart(); else if (opcode == 79) femaleEquipModelId3
			 * = stream.readBigSmart(); else if (opcode == 90) unknownInt1 =
			 * stream.readBigSmart(); else if (opcode == 91) unknownInt2 =
			 * stream.readBigSmart(); else if (opcode == 92) unknownInt3 =
			 * stream.readBigSmart(); else if (opcode == 93) unknownInt4 =
			 * stream.readBigSmart(); else if (opcode == 95) unknownInt5 =
			 * stream.readUnsignedShort(); else if (opcode == 96) unknownInt6 =
			 * stream.readUnsignedByte(); else if (opcode == 97) certId =
			 * stream.readUnsignedShort(); else if (opcode == 98) certTemplateId
			 * = stream.readUnsignedShort(); else if (opcode >= 100 && opcode <
			 * 110) { if (stackIds == null) { stackIds = new int[10];
			 * stackAmounts = new int[10]; } stackIds[opcode - 100] =
			 * stream.readUnsignedShort(); stackAmounts[opcode - 100] =
			 * stream.readUnsignedShort(); } else if (opcode == 110) unknownInt7
			 * = stream.readUnsignedShort(); else if (opcode == 111) unknownInt8
			 * = stream.readUnsignedShort(); else if (opcode == 112) unknownInt9
			 * = stream.readUnsignedShort(); else if (opcode == 113)
			 * unknownInt10 = stream.readByte(); else if (opcode == 114)
			 * unknownInt11 = stream.readByte() * 5; else if (opcode == 115)
			 * teamId = stream.readUnsignedByte(); else if (opcode == 121)
			 * lendId = stream.readUnsignedShort(); else if (opcode == 122)
			 * lendTemplateId = stream.readUnsignedShort(); else if (opcode ==
			 * 125) { unknownInt12 = stream.readByte() << 2; unknownInt13 =
			 * stream.readByte() << 2; unknownInt14 = stream.readByte() << 2; }
			 * else if (opcode == 126) { unknownInt15 = stream.readByte() << 2;
			 * unknownInt16 = stream.readByte() << 2; unknownInt17 =
			 * stream.readByte() << 2; } else if (opcode == 127) { unknownInt18
			 * = stream.readUnsignedByte(); unknownInt19 =
			 * stream.readUnsignedShort(); } else if (opcode == 128) {
			 * unknownInt20 = stream.readUnsignedByte(); unknownInt21 =
			 * stream.readUnsignedShort(); } else if (opcode == 129) {
			 * unknownInt20 = stream.readUnsignedByte(); unknownInt21 =
			 * stream.readUnsignedShort(); } else if (opcode == 130) {
			 * unknownInt22 = stream.readUnsignedByte(); unknownInt23 =
			 * stream.readUnsignedShort(); } else if (opcode == 132) { int
			 * length = stream.readUnsignedByte(); unknownArray2 = new
			 * int[length]; for (int index = 0; index < length; index++)
			 * unknownArray2[index] = stream.readUnsignedShort(); } else if
			 * (opcode == 134) { int unknownValue = stream.readUnsignedByte(); }
			 * else if (opcode == 139) { bindId = stream.readUnsignedShort(); }
			 * else if (opcode == 140) { bindTemplateId =
			 * stream.readUnsignedShort(); } else if (opcode >= 142 && opcode <
			 * 147) { if (unknownArray4 == null) { unknownArray4 = new int[6];
			 * Arrays.fill(unknownArray4, -1); } unknownArray4[opcode - 142] =
			 * stream.readUnsignedShort(); } else if (opcode >= 150 && opcode <
			 * 155) { if (null == unknownArray5) { unknownArray5 = new int[5];
			 * Arrays.fill(unknownArray5, -1); } unknownArray5[opcode - 150] =
			 * stream.readUnsignedShort(); }else if (opcode >= 142 && opcode <
			 * 147) { stream.readUnsignedShort(); } else if (opcode >= 150 &&
			 * opcode < 155) { stream.readUnsignedShort(); } else if (opcode ==
			 * 242) { int oldInvModel = stream.readBigSmart(); } else if (opcode
			 * == 243) { int oldMaleEquipModelId3 = stream.readBigSmart(); }
			 * else if (opcode == 244) { int oldFemaleEquipModelId3 =
			 * stream.readBigSmart(); } else if (opcode == 245) { int
			 * oldMaleEquipModelId2 = stream.readBigSmart(); } else if (opcode
			 * == 246) { int oldFemaleEquipModelId2 = stream.readBigSmart(); }
			 * else if (opcode == 247) { int oldMaleEquipModelId1 =
			 * stream.readBigSmart(); } else if (opcode == 248) { int
			 * oldFemaleEquipModelId1 = stream.readBigSmart(); } else if (opcode
			 * == 251) { int length = stream.readUnsignedByte(); int[]
			 * oldoriginalModelColors = new int[length]; int[]
			 * oldmodifiedModelColors = new int[length]; for (int index = 0;
			 * index < length; index++) { oldoriginalModelColors[index] =
			 * stream.readUnsignedShort(); oldmodifiedModelColors[index] =
			 * stream.readUnsignedShort(); } } else if (opcode == 252) { int
			 * length = stream.readUnsignedByte(); short[]
			 * oldoriginalTextureColors = new short[length]; short[]
			 * oldmodifiedTextureColors = new short[length]; for (int index = 0;
			 * index < length; index++) { oldoriginalTextureColors[index] =
			 * (short) stream.readUnsignedShort();
			 * oldmodifiedTextureColors[index] = (short)
			 * stream.readUnsignedShort(); } } else if (opcode == 249) { int
			 * length = stream.readUnsignedByte(); if (clientScriptData == null)
			 * clientScriptData = new HashMap<Integer, Object>(length); for (int
			 * index = 0; index < length; index++) { boolean stringInstance =
			 * stream.readUnsignedByte() == 1; int key = stream.read24BitInt();
			 * Object value = stringInstance ? stream.readString() :
			 * stream.readInt(); clientScriptData.put(key, value); } } else
			 * throw new RuntimeException("MISSING OPCODE " + opcode +
			 * " FOR ITEM " + getId());
			 */
		}
	}
}