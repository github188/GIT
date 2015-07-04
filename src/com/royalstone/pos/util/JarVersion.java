package com.royalstone.pos.util;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * 从Jar文件格式里提取版本号
 * @deprecated
 * @author liangxinbiao
 */
public class JarVersion {

	/**
	 * 从Jar文件格式里提取版本号
	 * @param jarName Jar文件的文件名
	 * @param attrName 版本好的名称
	 * @return 版本号
	 */
	public static String getVersion(String jarName, String attrName) {
		try {
			JarFile jarFile = new JarFile(jarName);
			Manifest mf = jarFile.getManifest();
			Attributes attrs = mf.getMainAttributes();
			return attrs.getValue(attrName);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return "";
	}

	/**
	 * 主方法,测试用
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("POS-Version="+JarVersion.getVersion("posv41.jar","POS-Version"));
		System.out.println("Loader-Version="+JarVersion.getVersion("loader.jar","Loader-Version"));
		System.out.println("Driver-Version="+JarVersion.getVersion("drv/driver.zip","Driver-Version"));
	}
}
