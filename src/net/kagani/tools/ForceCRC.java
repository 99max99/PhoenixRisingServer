package net.kagani.tools;

/* 
 * CRC-32 forcer
 * 
 * Copyright (c) 2014 Project Nayuki
 * All rights reserved. Contact Nayuki for licensing.
 * http://www.nayuki.io/page/forcing-a-files-crc-to-any-value
 */

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class ForceCRC {

	public static void main(String[] args) {
		// Handle arguments
		long offset = 0;
		int newCrc;
		long temp = 1169742478;
		if ((temp & 0xFFFFFFFFL) != temp)
			return;
		newCrc = 1169742478;// Integer.reverse((int)temp);

		// Process the file

		// Read entire file and calculate original CRC-32 value
		int crc = -970787043;
		System.out.printf("Original CRC-32: %08X%n", Integer.reverse(crc));

		// Compute the change to make
		int delta = crc ^ newCrc;
		delta = (int) multiplyMod(reciprocalMod(powMod(2, (0 - offset) * 8)),
				delta & 0xFFFFFFFFL);

		// Patch 4 bytes in the file

		byte[] bytes4 = new byte[4];

		for (int i = 0; i < bytes4.length; i++)
			bytes4[i] ^= Integer.reverse(delta) >>> (i * 8);
		System.out.println("Computed and wrote patch "
				+ Arrays.toString(bytes4));

	}

	/* Utilities */

	private static long POLYNOMIAL = 0x104C11DB7L; // Generator polynomial. Do

	// not modify, because there
	// are many dependencies

	private static int getCrc32(RandomAccessFile raf) throws IOException {
		raf.seek(0);
		int crc = 0xFFFFFFFF;
		byte[] buffer = new byte[32 * 1024];
		while (true) {
			int n = raf.read(buffer);
			if (n == -1)
				return ~crc;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < 8; j++) {
					crc ^= (buffer[i] >>> j) << 31;
					if ((crc & (1 << 31)) != 0)
						crc = (crc << 1) ^ (int) POLYNOMIAL;
					else
						crc <<= 1;
				}
			}
		}
	}

	/* Polynomial arithmetic */

	// Returns polynomial x multiplied by polynomial y modulo the generator
	// polynomial.
	private static long multiplyMod(long x, long y) {
		// Russian peasant multiplication algorithm
		long z = 0;
		while (y != 0) {
			z ^= x * (y & 1);
			y >>>= 1;
			x <<= 1;
			if ((x & (1L << 32)) != 0)
				x ^= POLYNOMIAL;
		}
		return z;
	}

	// Returns polynomial x to the power of natural number y modulo the
	// generator polynomial.
	private static long powMod(long x, long y) {
		// Exponentiation by squaring
		long z = 1;
		while (y != 0) {
			if ((y & 1) != 0)
				z = multiplyMod(z, x);
			x = multiplyMod(x, x);
			y >>>= 1;
		}
		return z;
	}

	// Computes polynomial x divided by polynomial y, returning the quotient and
	// remainder.
	private static long[] divideAndRemainder(long x, long y) {
		if (y == 0)
			throw new IllegalArgumentException("Division by zero");
		if (x == 0)
			return new long[] { 0, 0 };

		int ydeg = getDegree(y);
		long z = 0;
		for (int i = getDegree(x) - ydeg; i >= 0; i--) {
			if ((x & (1 << (i + ydeg))) != 0) {
				x ^= y << i;
				z |= 1 << i;
			}
		}
		return new long[] { z, x };
	}

	// Returns the reciprocal of polynomial x with respect to the generator
	// polynomial.
	private static long reciprocalMod(long x) {
		// Based on a simplification of the extended Euclidean algorithm
		long y = x;
		x = POLYNOMIAL;
		long a = 0;
		long b = 1;
		while (y != 0) {
			long[] divRem = divideAndRemainder(x, y);
			long c = a ^ multiplyMod(divRem[0], b);
			x = y;
			y = divRem[1];
			a = b;
			b = c;
		}
		if (x == 1)
			return a;
		else
			throw new IllegalArgumentException("Reciprocal does not exist");
	}

	private static int getDegree(long x) {
		return 63 - Long.numberOfLeadingZeros(x);
	}

}