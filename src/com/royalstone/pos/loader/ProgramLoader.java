package com.royalstone.pos.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

import javax.swing.JOptionPane;

import com.royalstone.pos.gui.DialogConfirm;
import com.royalstone.pos.gui.StartFrame;

/**
 * 程序下载更新
 * @author liangxinbiao
 */
public class ProgramLoader {

	private String host;

	public static void rename(String fnew, String fcurrent, String fbak) {

		File newFile = new File(fnew);
		if (newFile.exists()) {

			File currentFile = new File(fcurrent);
			if (currentFile.exists()) {

				File bakFile = new File(fbak);
				if (bakFile.exists()) {
					bakFile.delete();
				}

				currentFile.renameTo(new File(fbak));
				newFile.renameTo(new File(fcurrent));

			} else {
				newFile.renameTo(new File(fcurrent));
			}
		}
	}

	public static void main(String[] args) {
		FileLock lock = null;
		try {
			FileOutputStream fos = new FileOutputStream("lock");
			lock = fos.getChannel().tryLock();
			if (lock == null) {
				JOptionPane.showMessageDialog(null, "POS程序已经运行！");
				System.exit(1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "严重错误！");
			System.exit(1);
		}

		StartFrame startFrame = new StartFrame();
		startFrame.show();

		try {

			lock.release();
			Process p = Runtime.getRuntime().exec("main.bat");

			Thread.sleep(10000);
			startFrame.dispose();
			System.exit(0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("ERROR: Connot open pos.ini, exit ...");
			System.exit(2);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("ERROR: Connot read pos.ini, exit ...");
			System.exit(2);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}
	}

	private String mytrim(String str) {
		if (str == null) {
			return "";
		} else {
			return str.trim();
		}
	}

	private static boolean confirm(String s) {
		DialogConfirm confirm = new DialogConfirm();
		confirm.setMessage(s);
		confirm.show();

		return (confirm.isConfirm());
	}
}
