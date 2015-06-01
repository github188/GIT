package bankpay.alipay.tools;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.efuture.DeBugTools.PosLog;

public class TcpUtil {
	/*
	 * -401 ~ -420
	 */
	public static int bufferSize = 10*1024;
	public static int timeOut = 30*1000;
	
	public byte[] send(String ip, int port, byte[] data)  {
		Socket socket = null;
		InputStream iStream = null;
		OutputStream oStream = null;
		byte[] bytes = null;
		int step = 0;

		try {
			socket = new Socket(ip, port);
			socket.setSoTimeout(timeOut);
			
			oStream = socket.getOutputStream();
			oStream.write(data);
			oStream.flush();
			step++;
			
			//receive
			iStream = socket.getInputStream();
			
			byte[] buffer = new byte[bufferSize];
			int len = iStream.read(buffer);
			
			if(len <= 0){
				throw new Exception("");
			}
			bytes = new byte[len];
			
			System.arraycopy(buffer, 0, bytes, 0, len);
			PosLog.getLog(this.getClass()).info("miyarequest=========>>"+new String(data,"gbk"));
			PosLog.getLog(this.getClass()).info("miyareturn=========>>"+new String(bytes,"gbk"));
		}
		
		catch (Exception e) {
			e.printStackTrace();
			
		} 
		finally {
			try {
				iStream.close();
				oStream.close();
				socket.close();
			} catch (Exception e) {
			}
		}
		
		return bytes;
	}
}
