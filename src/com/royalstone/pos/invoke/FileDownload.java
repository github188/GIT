package com.royalstone.pos.invoke;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import sun.net.www.protocol.http.HttpURLConnection;

/**
 * 下载文件的客户端，它调用服务器上的DownloadServlet完成文件的下载
 * @author liangxinbiao
 */
public class FileDownload {

	private static final String REQUEST_CONTENT_TYPE = "application/file";

	private HttpURLConnection conn;
	private String strUrl;

	/**
	 * @param host 服务器的地址
	 * @param port 端口
	 */
	public FileDownload(String host, String port) {
		try {
			strUrl = "http://" + host + ":" + port + "/pos41/DownloadServlet";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @param src  文件在服务器上的路径(相对于Tomcat安装目录\download)
	 * @param dest 下载后的文件的路径（相对于POS的安装目录）
	 * @return 是否下载成功
	 */
	public boolean download(String src, String dest) {
		try {
			URL url = new URL(strUrl + "?fileName=" + src);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("ContentType", REQUEST_CONTENT_TYPE);
			conn.setRequestMethod("POST");
			InputStream is = conn.getInputStream();
			if (is.available() > 0) {
				File file = new File(dest);
				String strPath = file.getParent();
				if(strPath!=null){
					File dir = new File(strPath);
					if (!dir.exists()) {
						dir.mkdir();
					}
				}
				FileOutputStream fos = new FileOutputStream(dest);
				byte[] buf = new byte[4096];
				int length;
				while (((length = is.read(buf)) != -1)) {
					fos.write(buf, 0, length);
				}
				fos.flush();
				fos.close();
				is.close();
				return true;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return false;
	}

	/**
	 * 测试用的主方法
	 * @param args
	 */
	public static void main(String[] args) {

		FileDownload fileDownload = new FileDownload("localhost", "9090");
		fileDownload.download("program/posv41.jar", "download/posv41.jar");

	}
}
