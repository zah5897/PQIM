package com.recharge.ruyou;

/**
 * �й�byteת���Ĺ�����
 */
public class DataUtil {

	/** Gets int value */
	public static int getInt(byte b) {
		return b & 0xff;
	}

	/**
	 * ע�ͣ��ֽ����鵽short��ת����
	 * 
	 * @param b
	 * @return
	 */
	public static int byteToShort(byte one, byte two) {
		return (((two & 0xff) << 8) | one & 0xff);
	}

	public static short[] bytesToShorts(byte[] b) {
		short[] s = new short[b.length / 2];
		for (int i = 0, j = 0; i < b.length - 1; i += 2, j++) {
			s[j] = (short) byteToShort(b[i], b[i + 1]);
		}
		return s;
	}

	public static int getInt(byte[] buff) {
		return ((buff[0] & 0xff) << 24) | ((buff[1] & 0xff) << 16) | ((buff[2] & 0xff) << 8) | (buff[3] & 0xff);
	}

	public static byte[] shortToBytes(short val) {
		byte[] buff = new byte[2];
		buff[1] = (byte) (val >> 8);
		buff[0] = (byte) val;
		return buff;
	}

	public static byte getBit(int index, byte c) {
		switch (index) {
		case 0:
			c = (byte) (c >> 6);
			return (byte) (c & 3);
		case 1:
			c = (byte) (c >> 4);
			return (byte) (c & 3);
		case 2:
			c = (byte) (c >> 2);
			return (byte) (c & 3);
		case 3:
			break;
		}
		return (byte) (c & 3);
	}

	public static byte setBit(int index, byte c, byte val) {
		switch (index) {
		case 0:
			val <<= 6;
			break;
		case 1:
			val <<= 4;
			break;
		case 2:
			val <<= 2;
			break;
		case 3:
			break;
		}
		c += val;
		return c;
	}

	public static byte setBits(int index, byte c, byte val) {
		// 12345
		switch (index) {
		case 0:
			val *= 81;
			break;
		case 1:
			val *= 27;
			break;
		case 2:
			val *= 9;
			break;
		case 3:
			val *= 3;
			break;
		case 4:
		}
		c = (byte) (val + (c & 0xff));
		return c;
	}

	public static byte getBits(int index, byte c) {
		// 12345
		switch (index) {
		case 0:
			return (byte) ((c & 0xff) / 81); // 12345 / 10000
		case 1:
			return (byte) ((c & 0xff) % 81 / 27); // 12345 % 10000 / 1000
		case 2:
			return (byte) ((c & 0xff) % 27 / 9); // 12345 % 1000 / 100
		case 3:
			return (byte) ((c & 0xff) % 9 / 3); // 12345 % 100 / 10
		case 4:
			return (byte) ((c & 0xff) % 3); // 12345 % 10
		}
		return 0;
	}
	//
	// public static long byteToInt(byte[] bytes) {
	// // TODO 4���ֽ�תint
	// return 3;
	// }

	public static long byteToInt(byte a, byte b, byte c, byte d) {
		// TODO 4���ֽ�תint
		return ((d & 0xff) << 24) | ((c & 0xff) << 16) | ((b & 0xff) << 8) | (a & 0xff);
	}

	public static byte[] intToBytes(long val) {
		// TODO ת��
		byte[] data = new byte[4];
		data[0] = (byte) (val);
		data[1] = (byte) (val >> 8);
		data[2] = (byte) (val >> 16);
		data[3] = (byte) (val >> 24);
		return data;
	}

	public static byte[] intsToBytes(long val[]) {
		// TODO ת��
		byte[] data = new byte[val.length * 4];
		for (int i = 0; i < val.length; i++) {
			System.arraycopy(intToBytes(val[i]), 0, data, i * 4, 4);
		}
		return data;
	}

	public static byte[] shortToBytes(int value) {
		// TODO ת��
		byte[] data = new byte[2];
		data[1] = (byte) (value >> 8);
		data[0] = (byte) value;
		return data;
	}

	public static byte[] shortArray2ByteArray(short[] data, int items) {
		byte[] retVal = new byte[items];
		for (int i = 0; i < data.length; i++) {
			retVal[2 * i] = (byte) data[i];
			retVal[2 * i + 1] = (byte) (data[i] >> 8);
		}

		return retVal;
	}

	

}
