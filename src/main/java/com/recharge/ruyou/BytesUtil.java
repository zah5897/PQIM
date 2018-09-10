package com.recharge.ruyou;

public class BytesUtil {
	public static byte[] intToBytes2(int value) {
		byte[] src = new byte[4];
		src[0] = (byte) ((value >> 24) & 0xFF);
		src[1] = (byte) ((value >> 16) & 0xFF);
		src[2] = (byte) ((value >> 8) & 0xFF);
		src[3] = (byte) (value & 0xFF);
		return src;
	}

	public static byte[] longToByte(long number) {
		long temp = number;
		byte[] b = new byte[8];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Long(temp & 0xff).byteValue();//
			temp = temp >> 8; // ������8λ
		}
		return b;
	}

	public static byte[] shortToByte(short s) {
		byte[] b = new byte[2];
		b[1] = (byte) (s >> 8);
		b[0] = (byte) (s >> 0);
		return b;
	}

	public static int getBit(byte b, int bit) {
		byte byZT = b;
		switch (bit) {
		case 0:
			return (byZT & 0x01) == 0x01 ? 1 : 0;
		case 1:
			return (byZT & 0x02) == 0x02 ? 1 : 0;
		case 2:
			return (byZT & 0x04) == 0x04 ? 1 : 0;
		case 3:
			return (byZT & 0x08) == 0x08 ? 1 : 0;
		case 4:
			return (byZT & 0x10) == 0x10 ? 1 : 0;
		case 5:
			return (byZT & 0x20) == 0x20 ? 1 : 0;
		case 6:
			return (byZT & 0x40) == 0x40 ? 1 : 0;
		default:
			return (byZT & 0x80) == 0x80 ? 1 : 0;
		}

		// n0 = (byZT & 0x01) == 0x01 ? 1 : 0;
		//
		// n1 = (byZT & 0x02) == 0x02 ? 1 : 0;
		//
		// n2 = (byZT & 0x04) == 0x04 ? 1 : 0;
		// n3 = (byZT & 0x08) == 0x08 ? 1 : 0;
		// n4 = (byZT & 0x10) == 0x10 ? 1 : 0;
		// n5 = (byZT & 0x20) == 0x20 ? 1 : 0;
		// n6 = (byZT & 0x40) == 0x40 ? 1 : 0;
		// n7 = (byZT & 0x80) == 0x80 ? 1 : 0;
	}

	public static short byteToShort(byte[] b) {
		short s = 0;
		short s0 = (short) (b[0] & 0xff);// ���λ
		short s1 = (short) (b[1] & 0xff);
		s1 <<= 8;
		s = (short) (s0 | s1);
		return s;
	}

	public static int getDeviceLen(byte b) {
		int b1 = getBit(b, 1);
		int b2 = getBit(b, 2);

		if (b1 == 1 && b2 == 1) {
			return 6;
		} else if (b1 == 1 && b2 == 0) {
			return 2;
		} else if (b1 == 0 && b2 == 1) {
			return 4;
		} else {
			return 1;
		}
	}
}
