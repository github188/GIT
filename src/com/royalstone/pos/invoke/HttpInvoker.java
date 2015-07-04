package com.royalstone.pos.invoke;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * 客户端调用服务器程序的入口
 * @author liangxinbiao
 */

public class HttpInvoker {

	private static final String REQUEST_CONTENT_TYPE =
		"application/x-java-serialized-object";

	/**
	 * 调用远程方法的方法，调用过程中所产生的异常都捕获
	 * @param conn 到服务器的HTTP连接 
	 * @param mvI 传递到服务器的用MarshalledValue封装的参数
	 * @return 服务器返回的经过MarshalledValue封装的值
	 */
	public static MarshalledValue invoke(
		HttpURLConnection conn,
		MarshalledValue mvI) throws IOException{

		MarshalledValue result = null;

		try {
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("ContentType", REQUEST_CONTENT_TYPE);
			conn.setRequestMethod("POST");

			OutputStream os = conn.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);

			oos.writeObject(mvI);
			oos.flush();

			InputStream is = conn.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			result = (MarshalledValue) ois.readObject();

			ois.close();
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
            throw(ex);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
            throw new IOException(ex.getMessage());
		}

		return result;
	}

	/**
	 * 调用远程方法的方法,，调用过程中所产生的IO异常会抛出来让上级调用来处理
	 * @param conn 到服务器的HTTP连接
	 * @param mvI 传递到服务器的用MarshalledValue封装的参数
	 * @return 服务器返回的经过MarshalledValue封装的值
	 * @throws IOException 调用过程中所产生的IO异常
	 */
	public static MarshalledValue invokeWithException(
		HttpURLConnection conn,
		MarshalledValue mvI)
		throws IOException {

		MarshalledValue result = null;

		try {
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("ContentType", REQUEST_CONTENT_TYPE);
			conn.setRequestMethod("POST");

			OutputStream os = conn.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);

			oos.writeObject(mvI);
			oos.flush();

			InputStream is = conn.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			result = (MarshalledValue) ois.readObject();

			ois.close();
			oos.close();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		return result;
	}
    //-----获取商品的脱机价格数据------------------
     public static String getOfflinePriceSize (HttpURLConnection conn)throws IOException {

		String result = null;

		try {
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("ContentType", REQUEST_CONTENT_TYPE);
			conn.setRequestMethod("POST");
  		    InputStream is = conn.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			result = (String) ois.readObject();

			ois.close();

		} catch (IOException ex) {
			ex.printStackTrace();
            throw ex;
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
            throw new IOException(ex.getMessage());
		}

		return result;
	}
}
