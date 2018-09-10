package com.recharge.ruyou;

public class ProtocolUtil {

	public static byte[] createReadValueProtocl(String deviceNO) {
		byte[] fundata = getFunData(); // ������
		byte[] deviceData = getDeviceData(deviceNO); // �豸��ַ

		byte[] data = new byte[2]; // Э��data������Ϊ��ȡʣ�����
		data[0] = 0x0c;
		data[1] = 0x74;

		// fill protocl data

		byte[] protoclData = new byte[fundata.length + deviceData.length + data.length];
		System.arraycopy(fundata, 0, protoclData, 0, fundata.length);
		System.arraycopy(deviceData, 0, protoclData, fundata.length, deviceData.length);
		System.arraycopy(data, 0, protoclData, fundata.length + deviceData.length, data.length);

		// CRC16 ??
		byte[] crc16 = getCRC(protoclData);

		byte start = 0x68; // Ĭ�Ͽ�ʼΪ0x68
		byte end = 0x16; // ��β�̶�Ϊ016

		byte[] fullProtoclData = new byte[1 + protoclData.length + 2 + 1];
		fullProtoclData[0] = start;
		fullProtoclData[fullProtoclData.length - 1] = end;
		System.arraycopy(protoclData, 0, fullProtoclData, 1, protoclData.length);
		System.arraycopy(crc16, 0, fullProtoclData, 1 + protoclData.length, 2);

		return fullProtoclData;
	}

	public static byte[] createChargeValueProtocl(byte[] deviceNO, byte[] token) {
		byte[] fullData = new byte[] { 0x68, // ͷ�̶�
				0x1E, 0x71, 0x01, 0x06, // ������4�ֽ�
				0x20, // ����
				0x04, 0x20, 0x00, 0x00, 0x06, (byte) 0x89, // ����� �����Ӧ4000000689

				0x06, 0x41, 0x50, 0x53, (byte) 0x80, (byte) 0x90, (byte) 0xA0, // ���� 7�ֽڣ����� Ĭ��000000 ������Ϊʲô�����

				0x14, 0x0F, // ����
				0x71, 0x60, 0x69, 0x31, 0x57, (byte) 0x96, 0x10, 0x17, 0x59, 0x16,

				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // ��6�ֽ�0
				(byte) 0xBA, (byte) 0x94, // crc16
				0x16 // �̶���β
		};

		System.arraycopy(deviceNO, 0, fullData, 6, deviceNO.length);
		System.arraycopy(token, 0, fullData, 21, token.length);

		byte[] toCrc16 = new byte[fullData.length - 1 - 3];

		System.arraycopy(fullData, 1, toCrc16, 0, toCrc16.length);
		byte[] crc = getCRC(toCrc16);
		System.arraycopy(crc, 0, fullData, fullData.length - 3, crc.length);
		return fullData;
	}

	
	public static byte[] createReadChargeValueProtocl(byte[] deviceNO, byte[] token,int x) {
		byte[] fullData = new byte[] { 0x68, // ͷ�̶�
				0x0E, 0x71, 0x01, 0x06, // ������4�ֽ�
				0x1E, // ����
				0x04, 0x20, 0x00, 0x00, 0x06, (byte) 0x89, // ����� �����Ӧ4000000689

				0x06, 0x41, 0x50, 0x53, (byte) 0x80, (byte) 0x90, (byte) 0xA0, // ���� 7�ֽڣ����� Ĭ��000000 ������Ϊʲô�����

				0x14, 0x00, // ����
				0x71, 0x60, 0x69, 0x31, 0x57, (byte) 0x96, 0x10, 0x17, 0x59, 0x16,

				0x00, 0x00, 0x00, 0x00, // ��6�ֽ�0
				(byte) 0xBA, (byte) 0x94, // crc16
				0x16 // �̶���β
		};

		System.arraycopy(deviceNO, 0, fullData, 6, deviceNO.length);
		System.arraycopy(token, 0, fullData, 21, token.length);

		
		byte[] xx=ProtocolUtil.intStrtoByte(x);
		
		System.arraycopy(xx, 0, fullData, 31, xx.length);
		
		
		byte[] toCrc16 = new byte[fullData.length - 1 - 3];

		System.arraycopy(fullData, 1, toCrc16, 0, toCrc16.length);
		byte[] crc = getCRC(toCrc16);
		System.arraycopy(crc, 0, fullData, fullData.length - 3, crc.length);
		return fullData;
	}

	
	
	private static byte[] getFunData() {
		byte[] fun = new byte[32];
		fun[3] = 0;
		byte[] jzq = BytesUtil.intToBytes2(101);

		fun[18] = jzq[3];

		fun[22] = jzq[3];

		fun[24] = 0;
		fun[25] = 0;
		fun[26] = 0;
		fun[27] = 1;

		fun[28] = 0;

		fun[29] = 0;
		fun[30] = 0; // �ӻ���ȷӦ��
		fun[31] = 0; // ����������������֡
		return fun;
	}

	private static byte[] getDeviceData(String deviceNo) {

		byte[] b = new byte[6];

		int len = deviceNo.length();
		String end = deviceNo.substring(len - 2, len);
		byte[] s = BytesUtil.shortToByte(Short.parseShort(end));
		b[5] = s[0];

		end = deviceNo.substring(len - 4, len - 2);
		s = BytesUtil.shortToByte(Short.parseShort(end));
		b[4] = s[0];

		end = deviceNo.substring(len - 6, len - 4);
		s = BytesUtil.shortToByte(Short.parseShort(end));
		b[3] = s[0];

		end = deviceNo.substring(len - 8, len - 6);
		s = BytesUtil.shortToByte(Short.parseShort(end));
		b[2] = s[0];

		end = deviceNo.substring(len - 10, len - 8);
		s = BytesUtil.shortToByte(Short.parseShort(end));
		b[1] = s[0];

		end = deviceNo.substring(len - 12, len - 10);
		s = BytesUtil.shortToByte(Short.parseShort(end));
		b[0] = s[0];
		return b;
	}

	/**
	 * ����CRC16У����
	 *
	 * @param bytes
	 *            �ֽ�����
	 * @return {@link String} У����
	 * @since 1.0
	 */
	public static byte[] getCRC(byte[] bytes) {
		int CRC = 0x0000ffff;
		int POLYNOMIAL = 0x0000a001;
		int i, j;
		for (i = 0; i < bytes.length; i++) {
			CRC ^= ((int) bytes[i] & 0x000000ff);
			for (j = 0; j < 8; j++) {
				if ((CRC & 0x00000001) != 0) {
					CRC >>= 1;
					CRC ^= POLYNOMIAL;
				} else {
					CRC >>= 1;
				}
			}
		}
		String crc = Integer.toHexString(CRC);
		if(crc.length()<3) {
			crc="000"+crc;
		}
		String byteStr0 = crc.substring(2, 4);
		String byteStr1 = crc.substring(0, 2);

		byte[] crcData = new byte[2];

		crcData[0] = (byte) Integer.parseInt(byteStr0, 16);
		crcData[1] = (byte) Integer.parseInt(byteStr1, 16);
		return crcData;
	}

	public static String getJZQFromByte(byte[] data) {
		String temp = "";
		for (int i = 0, l = data.length; i < l; i++) {
			int x = data[i] & 0xff;
			String t = new Integer(x).toHexString(x);
			if (t.length() == 1) {
				t  = "0"+t;
			}
			temp  = temp+t;
		}
		return temp;
	}

	public static String toHexFmr(byte[] data) {
		String val = "";
		for (int i = 0, len = data.length; i < len; i++) {
			String x = new Integer(0).toHexString(data[i] & 0xff);
			if (x.length() < 2) {
				val += "0" + x;
			} else {
				val += x;
			}
		}
		return val;
	}

	public static byte[] jzqStrtoByte(String jzq) {
		int len = jzq.length();
		byte[] no = new byte[6];
		for (int i = 0; i < 6; i++) {
			String str2 = jzq.substring(len - 2 - i * 2, len - i * 2);
			no[6 - i - 1] = (byte) Integer.parseInt(str2, 16);
		}
		return no;
	}


	public static byte[] dbNOStrtoByte(String jzq) {
		int len = jzq.length();
		byte[] no = new byte[6];
		for (int i = 0; i < 6; i++) {
			String str2 = jzq.substring(len - 2 - i * 2, len - i * 2);
			no[6 - i - 1] = (byte) Integer.parseInt(str2, 16);
		}
		return no;
	}
	public static byte[] toTokenBytes(String token) {
		token=token.replace(" ", "");
		int len = token.length();
		byte[] no = new byte[10];
		for (int i = 0; i < 10; i++) {
			String str2 = token.substring(len - 2 - i * 2, len - i * 2);
			no[no.length-i-1]=(byte) Integer.parseInt(str2, 16);
		}
		return no;
	}
	public static byte[] intStrtoByte(int x) {
		
		String xs="000000000"+x;
		
		int len = xs.length();
		byte[] no = new byte[4];
		for (int i = 0; i < 4; i++) {
			String str2 = xs.substring(len - 2 - i * 2, len - i * 2);
			no[4 - i - 1] = (byte) Integer.parseInt(str2, 16);
		}
		return no;
	}
}
