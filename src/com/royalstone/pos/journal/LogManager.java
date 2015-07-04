/*
 * Created on 2004-8-4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.royalstone.pos.journal;

import java.io.File;
import java.io.FilenameFilter;

import com.royalstone.pos.common.PosContext;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LogManager {
	public static void main(String[] args) {
		LogManager uploadLog = new LogManager();
		uploadLog.upload();
	}

	public synchronized void upload() {

		File dir = new File("poslog");
		String[] files = dir.list(new DirFilter(".xml"));
		PosContext context = PosContext.getInstance();
		String ip = context.getServerip();
		int port = context.getPort();
		LogWriter log = new LogWriter(ip, port);
		for (int i = 0; i < files.length; i++) {
			log.writeLog(files[i]);
		}

	}

	public synchronized void delete() {
		File path = new File("poslog");
		String[] files = path.list(new DirFilter(".xml"));
		if (files.length == 0) {
			File[] dirList = path.listFiles();
			for (int i = 0; i < dirList.length; i++) {
				if (dirList[i].isDirectory()) {
					deleteDir(dirList[i]);
				}
			}

		}
	}
	
	private void deleteDir(File dir){
		if(dir.isDirectory()){
			File[] files=dir.listFiles();
			for(int i=0;i<files.length;i++){
				if(files[i].isDirectory()){
					deleteDir(files[i]);	
				}else{
					files[i].delete();
				}
			}
			dir.delete();
		}
		
	}

	private class DirFilter implements FilenameFilter {
		String afn;
		DirFilter(String afn) {
			this.afn = afn;
		}
		public boolean accept(File dir, String name) {
			return name.indexOf(afn) != -1;
		}
    }
}
