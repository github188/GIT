package com.royalstone.pos.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * 服务端要调用的类,
 * 负责检测服务器上是否有文件更新和将自己是否更新记录下来
 * @author liangxinbiao
 */
public class UpdateCheck {

	public static UpdateCheck instance;

	private UpdateCheck() {
	}

	/**
	 * @return 返回UpdateCheck的单实例
	 */
	public static UpdateCheck getInstance() {
		if (instance == null) {
			instance = new UpdateCheck();
		}
		return instance;
	}

	/**
	 * @param filename 文件更新控制文件的文件名
	 * @param posid POS机号
	 * @return 返回是否有更新
	 */
	public boolean hadUpdate(String filename, String posid) {
		try {
			Properties posList = new Properties();
			posList.load(new FileInputStream(filename));
			return posList.get(posid) == null ? false : true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 将已更新过的POS机的机号写在文件更新控制文件里
	 * @param filename 文件更新控制文件的文件名
	 * @param posid POS机号
	 */
	public synchronized void addPOS(String filename, String posid) {
		try {
			Properties posList = new Properties();
			File file = new File(filename);
			if (!file.exists())
				file.createNewFile();
			posList.load(new FileInputStream(filename));
			if (posList.get(posid) == null) {
				posList.put(posid, "");
				posList.store(new FileOutputStream(filename), "");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 主方法,测试用
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		UpdateCheck updateCheck = new UpdateCheck();
		System.out.println(
			updateCheck.hadUpdate("posupdate.properties", "P007"));
		updateCheck.addPOS("posupdate.properties", "P007");

	}
}
