package net.kagani.stream;

import net.kagani.game.player.Player;
import net.kagani.utils.StringUtilities;

public final class OutputStream extends Stream {

	private static final int[] BIT_MASK = { 0, 0x1, 0x3, 0x7, 0xf, 0x1f, 0x3f,
			0x7f, 0xff, 0x1ff, 0x3ff, 0x7ff, 0xfff, 0x1fff, 0x3fff, 0x7fff,
			0xffff, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff,
			0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff,
			0x1fffffff, 0x3fffffff, 0x7fffffff, -1 };

	private int opcodeStart = 0;

	public OutputStream(int capacity) {
		this.buffer = new byte[capacity];
		this.length = capacity;
	}

	public OutputStream() {
		this.buffer = new byte[16];
		this.length = 16;
	}

	public void expand(int position) {
		if (position >= this.length) {
			byte[] newBuffer = new byte[position + 16];
			System.arraycopy(getBuffer(), 0, newBuffer, 0, getBuffer().length);
			this.buffer = newBuffer;
			this.length = newBuffer.length;
		}
	}

	public void initBitAccess() {
		bitPosition = getOffset() * 8;
	}

	public void finishBitAccess() {
		setOffset((bitPosition + 7) / 8);
	}

	public void writeBits(int numBits, int value) {
		int bytePos = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);
		bitPosition += numBits;
		for (; numBits > bitOffset; bitOffset = 8) {
			expand(bytePos);
			getBuffer()[bytePos] &= ~BIT_MASK[bitOffset];
			getBuffer()[bytePos++] |= value >> numBits - bitOffset
					& BIT_MASK[bitOffset];
			numBits -= bitOffset;
		}
		expand(bytePos);
		if (numBits == bitOffset) {
			getBuffer()[bytePos] &= ~BIT_MASK[bitOffset];
			getBuffer()[bytePos] |= value & BIT_MASK[bitOffset];
		} else {
			getBuffer()[bytePos] &= ~(BIT_MASK[numBits] << bitOffset - numBits);
			getBuffer()[bytePos] |= (value & BIT_MASK[numBits]) << bitOffset
					- numBits;
		}
	}

	public void skip(int length) {
		setOffset(getOffset() + length);
	}

	public void writeByte(int i, int position) {
		expand(position);
		getBuffer()[position] = (byte) i;
	}

	public void writeBytes(byte[] b, int offset, int length) {
		expand(this.getOffset() + length - 1);
		System.arraycopy(b, offset, getBuffer(), this.getOffset(), length);
		this.setOffset(this.getOffset() + length);
	}

	public void writeBytes(byte[] b) {
		expand(this.getOffset() + b.length - 1);
		System.arraycopy(b, 0, getBuffer(), this.getOffset(), b.length);
		this.setOffset(this.getOffset() + b.length);
	}

	public void writeBytes128Reverse(byte[] b) {
		for (int i = b.length - 1; i >= 0; i--)
			writeByte128(b[i]);
	}

	public void writeBytesReverse(byte[] b) {
		for (int i = b.length - 1; i >= 0; i--)
			writeByte(b[i]);
	}

	public void writeBytes128(byte[] buffer) {
		for (byte b : buffer)
			writeByte128(b);
	}

	public void writeVersionedString(String s) {
		writeVersionedString(s, (byte) 0);
	}

	public void writeVersionedString(String s, byte version) {
		writeByte(version);
		writeString(s);
	}

	public void writeString(String s) {
		int n = s.indexOf('\0');
		if (n >= 0)
			throw new IllegalArgumentException("NUL character at " + n + "!");
		expand(getOffset() + s.length());
		offset += StringUtilities.encodeString(buffer, getOffset(), s, 0,
				s.length());
		writeByte(0);
	}

	public void writeByte(int i) {
		writeByte(i, offset++);
	}

	public void writeByte128(int i) {
		writeByte(i + 128);
	}

	public void writeByteC(int i) {
		writeByte(-i);
	}

	public void write128Byte(int i) {
		writeByte(128 - i);
	}

	public void writeShortLE128(int i) {
		writeByte(i + 128);
		writeByte(i >> 8);
	}

	public void writeShort128(int i) {
		writeByte(i >> 8);
		writeByte(i + 128);
	}

	public void writeSmart(int i) {
		if (i >= 128) {
			writeShort(i + 32768);
		} else {
			writeByte(i);
		}
	}

	public void writeBigSmart(int i) {
		if (i >= Short.MAX_VALUE)
			writeInt(i - Integer.MAX_VALUE - 1);
		else {
			writeShort(i >= 0 ? i : 32767);
		}
	}

	public void writeShort(int i) {
		writeByte(i >> 8);
		writeByte(i);
	}

	public void writeShortLE(int i) {
		writeByte(i);
		writeByte(i >> 8);
	}

	public void write24BitInteger(int i) {
		writeByte(i >> 16);
		writeByte(i >> 8);
		writeByte(i);
	}

	public void write24BitIntegerV2(int i) {
		writeByte(i >> 16);
		writeByte(i);
		writeByte(i >> 8);
	}

	public void write24BitIntegerV3(int i) {
		writeByte(i);
		writeByte(i >> 8);
		writeByte(i >> 16);
	}

	public void writeInt(int i) {
		writeByte(i >> 24);
		writeByte(i >> 16);
		writeByte(i >> 8);
		writeByte(i);
	}

	public void writeIntV1(int i) {
		writeByte(i >> 8);
		writeByte(i);
		writeByte(i >> 24);
		writeByte(i >> 16);
	}

	public void writeIntV2(int i) {
		writeByte(i >> 16);
		writeByte(i >> 24);
		writeByte(i);
		writeByte(i >> 8);
	}

	public void writeIntLE(int i) {
		writeByte(i);
		writeByte(i >> 8);
		writeByte(i >> 16);
		writeByte(i >> 24);
	}

	public void writeLong(long l) {
		writeByte((int) (l >> 56));
		writeByte((int) (l >> 48));
		writeByte((int) (l >> 40));
		writeByte((int) (l >> 32));
		writeByte((int) (l >> 24));
		writeByte((int) (l >> 16));
		writeByte((int) (l >> 8));
		writeByte((int) l);
	}

	public void write5ByteInteger(long l) {
		writeByte((int) (l >> 32));
		writeByte((int) (l >> 24));
		writeByte((int) (l >> 16));
		writeByte((int) (l >> 8));
		writeByte((int) l);
	}

	public void writeDynamic(int bytes, long l) {
		bytes--;
		if (bytes < 0 || bytes > 7) {
			throw new IllegalArgumentException();
		}
		for (int shift = 8 * bytes; shift >= 0; shift -= 8) {
			writeByte((int) (l >> shift));
		}
	}

	public void writePacket(Player player, int id) {
		if (player == null)
			writeSmart(id);
		else if (id >= 128) {
			writeByte((id >> 8) + 128
					+ player.getIsaacKeyPair().outKey().getNextValue());
			writeByte(id + player.getIsaacKeyPair().outKey().getNextValue());
		} else
			writeByte(id + player.getIsaacKeyPair().outKey().getNextValue());
	}

	public void writePacketVarByte(Player player, int id) {
		writePacket(player, id);
		writeByte(0);
		opcodeStart = getOffset() - 1;
	}

	public void writePacketVarShort(Player player, int id) {
		writePacket(player, id);
		writeShort(0);
		opcodeStart = getOffset() - 2;
	}

	public void endPacketVarByte() {
		writeByte(getOffset() - (opcodeStart + 2) + 1, opcodeStart);
	}

	public void endPacketVarShort() {
		int size = getOffset() - (opcodeStart + 2);
		writeByte(size >> 8, opcodeStart++);
		writeByte(size, opcodeStart);
	}

}