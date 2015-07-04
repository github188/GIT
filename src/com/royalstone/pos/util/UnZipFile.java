package com.royalstone.pos.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 实用类,将Zip格式的文件解压
 * @author liangxinbiao
 */
public class UnZipFile {

	/**
	 * 解压Zip格式文件
	 * @param srcFileName 源文件的详细路径
	 * @param destPath 目标路径
	 */
	public void unZip(String srcFileName, String destPath) throws IOException{
		  byte buf[]=new byte[4096];
			try {
				ZipFile zipFile = new ZipFile(srcFileName);
				Enumeration enumnew = zipFile.entries();
				while (enumnew.hasMoreElements()) {
					ZipEntry entry = (ZipEntry) enumnew.nextElement();
					if (!entry.isDirectory()) {
						FileOutputStream fos =
							new FileOutputStream(destPath + "/" + entry.getName());
						InputStream is = zipFile.getInputStream(entry);

	                    int len=is.read(buf);
						while (len!=-1) {
							fos.write(buf,0,len);
							len=is.read(buf);
						}
						is.close();
						fos.close();
					} else {
						File file = new File(destPath + "/" + entry.getName());
						file.mkdir();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new IOException(ex.getMessage());
			}
		}

		/**
		 * 主方法,测试用
		 * @param args
		 */
		public static void main(String[] args) {
			UnZipFile unZipFile = new UnZipFile();
			try {
				unZipFile.unZip("download/posv41.jar", "download");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
