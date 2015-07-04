package com.royalstone.pos.journal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import com.royalstone.pos.util.FileUtil;
import com.royalstone.pos.util.Formatter;

/**
 * 流水日志的列表,每生成一个流水都会将生成一个流水日志,
 * 然后将它保存在流水日志列表里,在写到文件里.
 * @author liangxinbiao
 */

public class JournalLogList implements Serializable {

	private static JournalLogList instance;

	private static String logfile =
		"journalLog" + File.separator + "journalLog.dat";

	private Vector list;
	private HashMap map;

	private JournalLogList() {
		list = new Vector();
		map = new HashMap();
	}

	/**
	 * @return 取得流水日志列表的单实例
	 */
	public synchronized static JournalLogList getInstance() {
		if (instance == null) {
			load();
		}
		return instance;
	}

	/**
	 * 根据流水名字在流水日志列表里找出流水日志,
	 * 找不到的话返回 null
	 * @param journalName 流水名字
	 * @return 流水日志
	 */
	public synchronized JournalLog find(String journalName) {
		return (JournalLog) map.get(journalName);
	}

	/**
	 * 将流水日志加到流水日志列表里
	 * @param log 流水日志
	 */
	public synchronized void addLog(JournalLog log) {
		if (log != null) {
			list.add(log);
			map.put(log.getJournalName(), log);
		}
	}

	/**
	 * 将流水日志列表写到文件里 
	 */
	public synchronized static void dump() {
		if (instance != null) {
			try {
				ObjectOutputStream out =
					new ObjectOutputStream(new FileOutputStream(logfile));
				out.writeObject(instance);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将流水日志列表从文件加载到内存中 
	 */
	public synchronized static void load() {
		FileInputStream fin = null;
		ObjectInputStream in = null;
		try {
			fin = new FileInputStream(logfile);
			in = new ObjectInputStream(fin);
			instance = (JournalLogList) in.readObject();
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			instance = new JournalLogList();
			JournalLogList.dump();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (fin != null) {
					fin.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			FileUtil.fileError(logfile);
			instance = new JournalLogList();
		}
	}

	/**
	 * 在流水日志列表文件改名保存,在原来文件名后加上日期时间 
	 */
	public synchronized static void rename() {
		if (instance != null) {
			for (int i = 0; i < instance.list.size(); i++) {
				JournalLog journalLog = (JournalLog) instance.list.elementAt(i);
				if (journalLog.getStatus() != JournalLog.UPLOADED) {
					return;
				}
			}
			File file = new File(logfile);
			file.renameTo(
				new File(
					"journalLog"
						+ File.separator
						+ "journalLog"
						+ Formatter.getDateFile(new Date())
						+ ".dat"));
		}
	}

	/**
	 * @return 流水日志列表里的没上传成功的流水总数
	 */
	public synchronized static int getUnuploadCount() {
		int count = 0;
		if (instance != null) {
			for (int i = 0; i < instance.list.size(); i++) {
				JournalLog journalLog = (JournalLog) instance.list.elementAt(i);
				if (journalLog.getStatus() != JournalLog.UPLOADED) {
					count++;
				}
			}
		}
		return count;
	}

	public Vector getLogList() {
		return list;
	}
}
