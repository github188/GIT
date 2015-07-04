/*
 * Created on 2004-8-5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.royalstone.pos.common;

import java.io.File;
import java.io.FilenameFilter;



import com.royalstone.pos.journal.LogManager;


public class LoanLog  implements Runnable {

	/* £¨·Ç Javadoc£©
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true) {
			try {
				Thread.sleep(60000);
				if(PosContext.getInstance().isOnLine()){
					Logup();	
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void Logup() {
		File path = new File("poslog");
		File[] files = path.listFiles(new DirFilter(".xml"));
		for (int i = 0; i < files.length; i++) {
			LogManager logManager = new LogManager();
			logManager.upload();
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
