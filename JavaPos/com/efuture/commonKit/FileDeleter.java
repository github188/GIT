package com.efuture.commonKit;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FileDeleter {
	private static final String targetPath1 = "F:\\WorkSpaces\\JavaPosSpace\\JavaPos\\PosLog";
	//private static final String targetPath2 = "C:\\Program Files\\Tencent\\QQ\\...........\\QQPhoto";
	private static long timeFlag = 90;

	public static void main(String[] args) {
//		FileDeleter fm = new FileDeleter();
//		// System.out.println(fm.timeCompare("20141201", ""));
//		String[] dir = { targetPath1 };
//		fm.delFiles(dir);
	}

	public static void delFiles(String[] dir) {
		for (int i = 0; i < dir.length; i++) {
			File f = new File(dir[i]);
			File[] files = null;
			if (f.exists()) {
				files = f.listFiles();
				// 循环得到子文件夹
				for (int j = 0; j < files.length; j++) {
					// 判断子文件时间是否超过30天
					String t1 = files[j].getName();
					if (timeCompare(t1, "") > timeFlag) {
						System.out.println(files[j].getName());
						//files[j].delete();
						delFolder(targetPath1+"\\"+files[j].getName());
					}
				}
			}
		}
	}

	/* 时间比大小 */
	public static int timeCompare(String t1, String t2) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
		Date date = new Date();
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		try {
			c1.setTime(formatter.parse(t1));
			c2.setTime(formatter.parse(formatter.format(date)));

		} catch (ParseException e) {
			e.printStackTrace();
		}
		long time1 = c1.getTimeInMillis();
		long time2 = c2.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		int result = Integer.parseInt(String.valueOf(between_days));
		// System.out.println(result);
		return result;
	}

	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	// 删除文件夹
	// param folderPath 文件夹完整绝对路径
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
