package com.royalstone.pos.util;

import java.io.File;
import java.util.Date;

import javax.swing.JOptionPane;

/**
 * 封装了一些文件改名方法的实用类
 * @author liangxinbiao
 */
public class FileUtil {

	/**
	 * 文件改名
	 * @param fnew 新文件名
	 * @param fcur 当前文件名
	 */
	public static void rename(String fnew, String fcur) {
		File curFile = new File(fcur);
		curFile.renameTo(new File(fnew));
	}

	/**
	 * 在当前文件名的基础上加上日期时间做后缀
	 * @param fcur
	 */
	public static void bakup(String fcur) {
		File curFile = new File(fcur);
		curFile.renameTo(
			new File(fcur + "." + Formatter.getDateFile(new Date())));
	}

	public static boolean bakup(String fcur, String fnew) {
		File curFile = new File(fcur);
		return curFile.renameTo(new File(fnew));
	}

	public static void fileError(String fname) {
		String fbak = fname + "." + Formatter.getDateFile(new Date());

		if (bakup(fname, fbak)) {
			JOptionPane.showMessageDialog(
				null,
				"文件" + fname + "损坏,已备份为" + fbak + ".");
			System.out.println("文件" + fname + "损坏,已备份为" + fbak + ".");
		} else {
			JOptionPane.showMessageDialog(
				null,
				"文件" + fname + "损坏,备份为" + fbak + "失败.");
			System.out.println("文件" + fname + "损坏,备份为" + fbak + "失败.");
		}
	}
	
	public static boolean journalFileError(String fname) {

		String dirName="journal"+File.separator+Formatter.getDate(new Date())+"#bad";
		File dir = new File(dirName);
		if(!dir.exists()){
			dir.mkdir();						
		}
		
		String fbak = dirName+File.separator+fname + "." + Formatter.getDateFile(new Date());
		fname="journal"+File.separator+fname;

		if (bakup(fname, fbak)) {
			JOptionPane.showMessageDialog(
				null,
				"文件" + fname + "损坏,已备份为" + fbak + ".");
			System.out.println("文件" + fname + "损坏,已备份为" + fbak + ".");
			return true;
		} else {
			JOptionPane.showMessageDialog(
				null,
				"文件" + fname + "损坏,备份为" + fbak + "失败.");
			System.out.println("文件" + fname + "损坏,备份为" + fbak + "失败.");
			return false;
		}
	}

}
