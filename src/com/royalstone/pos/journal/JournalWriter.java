/*
 * Created on 2004-6-4
 *
 */
package com.royalstone.pos.journal;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Date;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.royalstone.pos.common.PosContext;
import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.invoke.MarshalledValue;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.util.FileUtil;
import com.royalstone.pos.util.Formatter;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.util.WorkTurn;
import com.royalstone.pos.web.command.PosJournalCommand;

/**
 * JournalWriter 负责向后台传递流水. 其工作机制如下:
 * 打开保存流水数据的XML文档,根据其中的流水节点构造 PosJournal 对象.
 * 连接后台的DispatchServlet, 指定其实例化 com.royalstone.pos.web.command.PosJournalCommand.
 * 传递PosJournal 对象到后台,由PosJournalCommand 对象写入数据库中.
 * @author Mengluoyi
 *
 */

public class JournalWriter {
	
	private PosJournalCommand posJournalCommand=new PosJournalCommand();

	public static void main(String[] args) {
		JournalWriter w = new JournalWriter("localhost", 9090);
		w.writeJournal("001.xml");
	}

	/**
	 * Constructs a JournalWriter.
	 * @param server	后台服务器IP地址.
	 * @param port		服务端口号.
	 */
	public JournalWriter(String server, int port) {
		try {
			servlet =
				new URL(
					"http://" + server + ":" + port + "/pos41/DispatchServlet");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将存放在文件file 中的流水写入后台服务器. 如果写后台成功,把流水文件移动到以班次命名的子目录下.
	 * 每生成一笔交易的流水,都会在JournalLogList中有所记录.
	 * @param file	存放XML流水的文件.
	 */
	public boolean writeJournal(final String file) {
		JournalLog journalLog = JournalLogList.getInstance().find(file);

		if (journalLog == null) {
			System.out.println(file + " not in JournalLog!");
			journalLog = new JournalLog();
			journalLog.setJournalName(file);
			journalLog.setStatus(JournalLog.CREATE);
			JournalLogList.getInstance().addLog(journalLog);
			JournalLogList.dump();
		}

		if (journalLog.getStatus() == JournalLog.UPLOADED) {
			System.out.println(file + " in JournalLog is Uploaded!");
			try {
				backupJournalFile(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}

		journalLog.setStatus(JournalLog.BEGIN_UPLOAD);
		JournalLogList.dump();

		System.out.println("Begin writeJournal(" + file + ")... ");
		Element root = getRootElement(file);

		if (root == null) {
			System.out.println("root is null!");
			return true;
		}

		PosJournal journal = new PosJournal(root);

		Response result = null;
		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[2];

			params[0] = "com.royalstone.pos.web.command.PosJournalCommand";
			params[1] = journal;
			
			Object[] results = null;

			if(pos.hasServer){

				MarshalledValue mvI = new MarshalledValue(params);
				System.out.println("Invoke! Begin upload journal... ");

				journalLog.setStatus(JournalLog.UPLOADING);
				JournalLogList.dump();

				MarshalledValue mvO = HttpInvoker.invoke(conn, mvI);


				System.out.println("if mvO is null?" + (mvO == null));
				if (mvO != null) {
					results = mvO.getValues();
				}
				
			}else{
				results=posJournalCommand.excute(params);
			}


			System.out.println("if results is null?" + (results == null));
			if (results != null)
				System.out.println("if results.length=" + results.length);

			if (results != null && results.length > 0) {
				result = (Response) results[0];
				System.out.println(result);

				System.out.println("if result is null?" + (result == null));
				if (result != null)
					System.out.println("if result.succeed=" + result.succeed());

				if (result != null && result.succeed()) {

					try {
						backupJournalFile(file);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					journalLog.setStatus(JournalLog.UPLOADED);
					journalLog.setUploadTime(Formatter.getDateFile(new Date()));
					JournalLogList.dump();

					return true;
				}
			}
			System.out.println("Finish upload journal... ");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		journalLog.setStatus(JournalLog.UPLOAD_FAIL);
		JournalLogList.dump();

		System.out.println("End writeJournal... ");

		return false;
	}

	/**
	 * 备份流水文件.
	 * @param file		流水文件名.
	 * @throws Exception	
	 */
	private void backupJournalFile(String file) throws Exception {

		PosContext context = PosContext.getInstance();
		WorkTurn workTurn = context.getWorkTurn();

		String dirName;
		if (workTurn == null) {
			dirName =
				"journal"
					+ File.separator
					+ Formatter.getDate(new Date());
				//TODO 沧州富达 by fire  2005_5_11  "#unknown";
					
			System.out.println("workTurn is null");
		} else {
			dirName = "journal" + File.separator + workTurn.toString();
		}

		File dir = new File(dirName);

		if (!dir.exists()) {
			System.out.println(dir.getName() + " not exists");
			dir.mkdir();
		}

		String srcfile = "journal" + File.separator + file;
		String destfile = dirName + File.separator + file;

		System.out.println("srcfile=" + srcfile);
		System.out.println("destfile=" + destfile);

		FileChannel in = new FileInputStream(srcfile).getChannel();
		FileChannel out = new FileOutputStream(destfile).getChannel();
		long transbytes = in.transferTo(0, in.size(), out);

		System.out.println("transbytes=" + transbytes);

		in.close();
		out.close();

		System.out.println("Begin delete file \"" + srcfile + "\"");
		boolean delresult = (new File(srcfile)).delete();
		System.out.println(
			"Delete file \"" + srcfile + "\" return " + delresult);

	}

	/**
	 * 解析XML流水文件,取其根节点.
	 * @param file	XML流水文件名.
	 * @return		根节点.
	 */
	public static Element getRootElement(String file) {
		FileInputStream fin = null;
		try {
			fin = new FileInputStream("journal" + File.separator + file);
			Document doc = (new SAXBuilder()).build(fin);
			Element root = doc.getRootElement();
			try {
				fin.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return root;
		} catch (Exception e) {
			e.printStackTrace();
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (!FileUtil.journalFileError(file)) {
				System.exit(1);
			}
		}
		return null;
	}

	/**
	 * <code>servlet</code>	URL for servlet.
	 */
	private URL servlet;
	/**
	 * <code>conn</code>	http connection to servlet.
	 */
	private HttpURLConnection conn;

}
