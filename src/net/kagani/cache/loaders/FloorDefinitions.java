package net.kagani.cache.loaders;

import java.util.concurrent.ConcurrentHashMap;

import net.kagani.cache.Cache;
import net.kagani.stream.InputStream;

public final class FloorDefinitions {

	private static final ConcurrentHashMap<Integer, FloorDefinitions> defs = new ConcurrentHashMap<Integer, FloorDefinitions>();

	public int anInt6001;
	public int anInt6002;
	public int anInt6003;
	public boolean aBool6004;
	public boolean aBool6005;
	public int anInt6006 = 0;
	public int anInt6007;
	public int anInt6008;
	public int anInt6009;
	private int id;

	public static final FloorDefinitions getFloorDefinitions(int id) {
		FloorDefinitions script = defs.get(id);
		if (script != null)// open new txt document
			return script;
		byte[] data = Cache.STORE.getIndexes()[2].getFile(1, id);
		script = new FloorDefinitions();
		script.id = id;
		if (data != null)
			script.readValueLoop(new InputStream(data));
		defs.put(id, script);
		return script;

	}

	private FloorDefinitions() {
		anInt6003 = -1;
		anInt6009 = -1;
		aBool6004 = true;
		aBool6005 = true;
	}

	private void readValueLoop(InputStream stream) {
		for (;;) {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				break;
			readValues(stream, opcode);
		}
	}

	void method7486(int i) {
		/*
		 * double red = (double) (i >> 16 & 0xff) / 256.0; double green =
		 * (double) (i >> 8 & 0xff) / 256.0; double blue = (double) (i & 0xff) /
		 * 256.0; double minorC = red; if (green < minorC) minorC = green; if
		 * (blue < minorC) minorC = blue; double majorC = red; if (green >
		 * majorC) majorC = green; if (blue > majorC) majorC = blue; double
		 * d_14_ = 0.0; double d_15_ = 0.0; double d_16_ = (majorC + minorC) /
		 * 2.0; if (minorC != majorC) { if (d_16_ < 0.5) d_15_ = (majorC -
		 * minorC) / (majorC + minorC); if (d_16_ >= 0.5) d_15_ = (majorC -
		 * minorC) / (2.0 - majorC - minorC); if (red == majorC) d_14_ = (green
		 * - blue) / (majorC - minorC); else if (majorC == green) d_14_ = 2.0 +
		 * (blue - red) / (majorC - minorC); else if (blue == majorC) d_14_ =
		 * 4.0 + (red - green) / (majorC - minorC); } d_14_ /= 6.0; anInt6007 =
		 * (int) (256.0 * d_15_); anInt6002 = (int) (d_16_ * 256.0); if
		 * (anInt6007 < 0) anInt6007 = 0; else if (anInt6007> 255) anInt6007 =
		 * 255; if ( anInt6002 < 0) anInt6002 = 0; else if (anInt6002 > 255)
		 * anInt6002 = 255; if (d_16_ > 0.5) anInt6008 = (int) ((1.0 - d_16_) *
		 * d_15_ * 512.0); else anInt6008 = (int) (512.0 * (d_15_ * d_16_)); if
		 * (anInt6008 < 1) anInt6008 = 1; anInt6001 = (int) (d_14_ * (double)
		 * (anInt6008));
		 */

		/*
		 * int r = new Color(anInt6006).getRed(); int g = new
		 * Color(anInt6006).getGreen(); int b = new Color(anInt6006).getBlue();
		 * float[] hsb = Color.RGBtoHSB(r, g, b, null); for(int i2 = 0; i2 <
		 * hsb.length; i2++) hsb[i2] *= 256;
		 * System.out.println(Arrays.toString(hsb));
		 * System.out.println(anInt6002+", "+anInt6007+", "+anInt6001);
		 */

		/*
		 * a1[y] += defs.anInt6001; a2[y] += defs.anInt6007; a3[y] +=
		 * defs.anInt6002;
		 */

		int red = (i >> 16 & 0xff);
		int green = (i >> 8 & 0xff);
		int blue = (i & 0xff);
		anInt6001 = red;
		anInt6007 = green;
		anInt6002 = blue;
		/*
		 * float[] hsb = Color.RGBtoHSB(red, green, blue, null); anInt6001 =
		 * (int) (hsb[0]*256); System.out.println(); anInt6007 = (int)
		 * (hsb[1]*256); anInt6002 = (int) (hsb[2]*256);
		 */

	}

	private void readValues(InputStream stream, int i) {
		if (1 == i) {
			this.anInt6006 = stream.read24BitInt();
			method7486(anInt6006);
		} else if (i == 2) {
			anInt6009 = stream.readUnsignedShort();
			if (65535 == anInt6009)
				anInt6009 = -1;
		} else if (3 == i)
			anInt6003 = (stream.readUnsignedShort() << 2);
		else if (i == 4)
			aBool6004 = false;
		else if (i == 5)
			aBool6005 = false;
	}

}
