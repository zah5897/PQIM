package com.recharge.ruyou;

import com.pg.db.util.TextUtils;
import com.recharge.ruyou.BytesUtil;
import com.recharge.ruyou.Log;
import com.recharge.ruyou.ServerSocketHandler;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;


public class ProtocolHandler extends Thread {
	protected Socket socket;
	private ServerSocketHandler server;

	private String jzqNO;
	private byte[] jzqNOBytes;

	public ProtocolHandler(ServerSocketHandler server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		try {
			BufferedInputStream bout = new BufferedInputStream(socket.getInputStream());
			DataInputStream udin = new DataInputStream(bout);
			byte[] head = new byte[5];
			while (true) {
				udin.readFully(head);
				Log.d("head 0="+head[0]);
				if (head[0] == 0x68) {
					// 解析长度所占字节
					// int bit3=BytesUtil.getBit(head[1], 3);
					int bit3 = BytesUtil.getBit(head[4], 3);

					String  head1=new Integer(0).toHexString(head[1] & 0xff);
					String  head2=new Integer(0).toHexString(head[2] & 0xff);
					String  head3=new Integer(0).toHexString(head[3] & 0xff);
					String  head4=new Integer(0).toHexString(head[4] & 0xff);
					String  bit3Str=new Integer(0).toBinaryString(head[4] & 0xff);

					int len = 1;
					if (bit3 == 1) {
						len = 2;
					}
					// 获取长度值
					byte[] lenBytes = new byte[len];
					udin.readFully(lenBytes);

					int protoclLen = 0;
					if (len == 1) {
						protoclLen = Integer.parseInt(new Integer(0).toHexString(lenBytes[0] & 0xff),16);
					} else {
						int n0 = Integer.parseInt(new Integer(lenBytes[0] & 0xff).toHexString(lenBytes[0] & 0xff));
						int n1 = Integer.parseInt(new Integer(0).toHexString(lenBytes[1] & 0xff));
						protoclLen = new Integer(0).parseInt(n0 + "" + n1, 16);
					}


					if(protoclLen==9) {
						Log.d("keepalive。。。。。。");
					}

					// 获取设备NO数据
					int deviceLen = BytesUtil.getDeviceLen(head[1]);
					byte[] deviceNO = new byte[deviceLen];
					udin.readFully(deviceNO);

					String temp = ProtocolUtil.getJZQFromByte(deviceNO);

					if(TextUtils.isEmpty(jzqNO)){
                        jzqNO=temp;
						jzqNOBytes=deviceNO;
					}
					Log.d("集中器编号=" + temp);
					//data 解析
					byte[] data = new byte[protoclLen - len - deviceLen];

					Log.d("数据长度=" + protoclLen);
					udin.readFully(data);

					handleData(data,head,deviceNO);



					//处理结尾
					byte crc0=udin.readByte();
					while(crc0==0) {
						crc0=udin.readByte();
					}
					byte crc1=udin.readByte();
					byte end = udin.readByte();

					if (end != 0x16) {
						Log.d("协议头有误..end="+end);
					}
				} else {
					Log.d("错误的协议头head【0】="+head[0]);
				}
			}
		} catch (EOFException e) {
			e.printStackTrace();
			server.removeClient(this);
		} catch (IOException e) {
			e.printStackTrace();
			server.removeClient(this);
		}
	}




	private void handleData(byte[] data,byte[] head,byte[] deviceNO) {
		int x=head[1] & 0xff;
		x=new Integer(0).parseInt(String.valueOf(x),16);
		String headBit24_27 = ("00000000000000"+new Integer(x).toBinaryString(x));
		String head4=headBit24_27.substring(headBit24_27.length()-8);
		headBit24_27=head4.substring(0, 4);

		int bit31=Integer.parseInt(head4.charAt(7)+"");
		int bit30=Integer.parseInt(head4.charAt(6)+"");
		int bit29=Integer.parseInt(head4.charAt(5)+"");
		int bit28=Integer.parseInt(head4.charAt(4)+"");



		filterDataType(data,bit29,deviceNO);

//			if(bit31==1) {
//				System.out.println("由从机发出的应答帧 ");
//				if(bit28==0) {
//					System.out.println("读");
//				}else {
//					System.out.println("写");
//				}
//			}else {
//				if(bit28==0) {
//					System.out.println("读");
//					filterDataType(data,bit29,deviceNO);
//				}else {
//					System.out.println("写");
//				}
//
//				System.out.println("由主机发出的命令帧 ");
//			}
		//}else {
		//System.out.println("从机对异常信息的应答 ");
		//}

	}





	private void filterDataType(byte[] data,int bit29,byte[] deviceNO) {

		if(data==null||data.length<2){
          return;
		}
		String funVar=ProtocolUtil.toHexFmr(new byte[] {data[0],data[1]});//电表地址;
		Log.d("变量码="+funVar);
		if("0400".equals(funVar)) { //集中器平均记录   //记录 说明：电表地址 【6】+总有功电量（4字节）+剩余电量（带符号4字节）+电流（3字节）+电压（2字节）+继电器状态(BIT7)&功率因素（1字节）
			readaverageVal(deviceNO,data,2);
			replay(bit29==0?0:1,deviceNO);
		}
	}


	private void readaverageVal(byte[] devideAddr,byte[] data,int startIndex) {
		//设备地址
		int n=startIndex;
		byte[] dbdz=new byte[6];
		System.arraycopy(data, startIndex, dbdz, 0, dbdz.length);
		n+=dbdz.length;
		String dbNO=ProtocolUtil.toHexFmr(dbdz);//电表地址
		if(dbNO.equals("000000000000")) { //解析结束
			return;
		}
		//总有功电量
		byte[] zygdl=new byte[4];
		System.arraycopy(data, n, zygdl, 0, zygdl.length);
		n+=zygdl.length;
		//剩余电量
		byte[] sydl=new byte[4];
		System.arraycopy(data, n, sydl, 0, sydl.length);
		n+=sydl.length;

		String hexNum=ProtocolUtil.toHexFmr(sydl);
		int sydlNum  = new Integer(0).parseInt(hexNum, 16);
		//电流
		byte[] dl=new byte[3];
		System.arraycopy(data, n, dl, 0, dl.length);
		n+=dl.length;
		//电压

		byte[] dy=new byte[2];
		System.arraycopy(data, n, dy, 0, dy.length);
		n+=dy.length;
		//继电器状态
		byte lastOne=data[n];
		n+=1;
		n+=2; //空2字节

		uploadVal(devideAddr,dbdz,sydlNum/10);
		readaverageVal(devideAddr,data,n);
		//
	}
	private void uploadVal(byte[] jzq,byte[] dbNO,float sydlNum) {
		RechargeManager.instance.uploadVal(jzq, dbNO, sydlNum);
	}


	private void replay(int type,byte[] deviceNO) {
		Log.d("回复集中器 type="+type);

		byte[] replay = null;
		if(type==0) {
			replay=new byte[] {0x68,0x04,0x75,0x01,0x06,0x0D,9, (byte) 0x90, 0x00, 0x00, 0x00, (byte) 0x93, 0x04 ,0x00, 0x6C, 0x21, 0x16 };
		}else if(type==1) {
			replay=new byte[] {0x68, 0x24 , 0x75 , 0x01 , 0x06 , 9 , 0x09 , (byte) 0x90 , 0x00 , 0x00 , 0x00 , (byte) 0x93 , 0x04 , 0x00 , 0x13 , 0x41 , 0x16};
		}

//		teddy. ji:
//			主站应答集中器平均记录(无后续)：68 04 75 01 06 09 09 90 00 00 00 93 04 00 6C 21 16  (集中器不返回)
//			主站应答集中器平均记录(有后续)：68 24 75 01 06 09 09 90 00 00 00 93 04 00 13 41 16  (集中器不返回)
//			主站应答集中器心跳包  (无后续)：68 04 75 01 06 09 09 90 00 00 00 93 03 F0 6E 55 16  (集中器不返回)




		System.arraycopy(deviceNO, 0, replay, 6, deviceNO.length);

		byte[] toCrc16 = new byte[replay.length - 1 - 3];
		System.arraycopy(replay, 1, toCrc16, 0, toCrc16.length);
		byte[] crc = ProtocolUtil.getCRC(toCrc16);
		System.arraycopy(crc, 0, replay, replay.length - 3, crc.length);

		try {
			writeData(replay);
			Log.d("应答集中器发送结束。");
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("应答集中器失败。");
		}
	}


	public void keepAlive() {
		byte[] replay = {0x68,0x04,0x75,0x01,0x06,9,0x09,(byte) 0x90,0x00,0x00,0x00,(byte) 0x93,0x03,(byte) 0xF0,0x6E,0x55 ,0x16};
		System.arraycopy(jzqNOBytes, 0, replay, 6, jzqNOBytes.length);
		byte[] toCrc16 = new byte[replay.length - 1 - 3];
		System.arraycopy(replay, 1, toCrc16, 0, toCrc16.length);
		byte[] crc = ProtocolUtil.getCRC(toCrc16);
		System.arraycopy(crc, 0, replay, replay.length - 3, crc.length);
		try {
			writeData(replay);
			Log.d("发送心跳数据 成功。");
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("发送心跳数据失败。");
		}
	}

	public void stopConnect() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			ProtocolHandler temp = (ProtocolHandler) obj;
			InetAddress addr = temp.socket.getInetAddress();
			return addr.getHostAddress().equals(socket.getInetAddress().getHostAddress());
		}
		return false;
	}

	public void writeData(byte[] protocl) throws IOException {
		if (socket != null && socket.isConnected()) {
			socket.getOutputStream().write(protocl);
		}
	}

}
