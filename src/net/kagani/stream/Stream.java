package net.kagani.stream;

public abstract class Stream {

	protected int offset;
	protected int length;
	protected byte[] buffer;
	protected int bitPosition;

	public int getLength() {
		return length;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public final void xteaDecrypt(int keys[], int start, int end) {
		int l = offset;
		offset = start;
		int i1 = (end - start) / 8;
		for (int j1 = 0; j1 < i1; j1++) {
			offset += 4;
			int k1 = ((0xff & buffer[-3 + offset]) << 16)
					+ ((((0xff & buffer[-4 + offset]) << 24) + ((buffer[-2
							+ offset] & 0xff) << 8)) + (buffer[-1 + offset] & 0xff));
			offset += 4;
			int l1 = ((0xff & buffer[-3 + offset]) << 16)
					+ ((((0xff & buffer[-4 + offset]) << 24) + ((buffer[-2
							+ offset] & 0xff) << 8)) + (buffer[-1 + offset] & 0xff));
			int sum = 0xc6ef3720;
			for (int k2 = 32; k2-- > 0;) {
				l1 -= keys[(sum & 0x1c84) >>> 11] + sum ^ (k1 >>> 5 ^ k1 << 4)
						+ k1;
				sum -= 0x9e3779b9;
				k1 -= (l1 >>> 5 ^ l1 << 4) + l1 ^ keys[sum & 3] + sum;
			}

			offset -= 8;
			buffer[offset++] = (byte) (k1 >> 24);
			buffer[offset++] = (byte) (k1 >> 16);
			buffer[offset++] = (byte) (k1 >> 8);
			buffer[offset++] = (byte) k1;
			buffer[offset++] = (byte) (l1 >> 24);
			buffer[offset++] = (byte) (l1 >> 16);
			buffer[offset++] = (byte) (l1 >> 8);
			buffer[offset++] = (byte) l1;
		}
		offset = l;
	}

	public final void xteaEncrypt(int keys[], int start, int end) {
		int l = offset;
		offset = start;
		int i1 = (end - start) / 8;
		for (int j1 = 0; j1 < i1; j1++) {
			offset += 4;
			int k1 = ((0xff & buffer[-3 + offset]) << 16)
					+ ((((0xff & buffer[-4 + offset]) << 24) + ((buffer[-2
							+ offset] & 0xff) << 8)) + (buffer[-1 + offset] & 0xff));
			offset += 4;
			int l1 = ((0xff & buffer[-3 + offset]) << 16)
					+ ((((0xff & buffer[-4 + offset]) << 24) + ((buffer[-2
							+ offset] & 0xff) << 8)) + (buffer[-1 + offset] & 0xff));
			int sum = 0;
			for (int k2 = 32; k2-- > 0;) {
				k1 += (sum + keys[sum & 0x3] ^ (l1 << 4 ^ l1 >>> 5) + l1);
				sum += 0x9e3779b9;
				l1 += (sum + keys[(sum & 0x1d2f) >>> 11] ^ k1
						+ (k1 << 4 ^ k1 >>> 5));
			}

			offset -= 8;
			buffer[offset++] = (byte) (k1 >> 24);
			buffer[offset++] = (byte) (k1 >> 16);
			buffer[offset++] = (byte) (k1 >> 8);
			buffer[offset++] = (byte) k1;
			buffer[offset++] = (byte) (l1 >> 24);
			buffer[offset++] = (byte) (l1 >> 16);
			buffer[offset++] = (byte) (l1 >> 8);
			buffer[offset++] = (byte) l1;
		}
		offset = l;
	}

	public final void getBytes(byte data[], int off, int len) {
		for (int k = off; k < len + off; k++) {
			data[k] = buffer[offset++];
		}
	}
}