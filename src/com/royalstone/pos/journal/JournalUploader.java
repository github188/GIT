package com.royalstone.pos.journal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.royalstone.pos.common.PosContext;
import com.royalstone.pos.invoke.realtime.RealTime;
import com.royalstone.pos.io.PosDevOut;
import com.royalstone.pos.shell.pos;

/**
 * @author liangxinbiao
 */

public class JournalUploader implements Runnable {

	private PosContext context;
	private PosDevOut out;
	private long interval = 60000;

	public JournalUploader(PosContext context) {
		this.context = context;
		this.out = PosDevOut.getInstance();
		fromIni("pos.ini");
	}

	public void run() {

		while (true) {

			try{
				
				if (RealTime.getInstance().testOnLine()) {
					context.setOnLine(true);

					synchronized (pos.uploadLock) {
						JournalManager journalManager = new JournalManager();
						journalManager.upload();
					}
//TODO ²×ÖÝ¸»´ï by fire  2005_5_11
//					synchronized (pos.workTurnLock) {
//					
//						PosTurnList posTurnList = PosTurnList.getInstance();
//						ArrayList turnList = posTurnList.findByState(1);
//
//						if (turnList.size() > 0) {
//							try {
//								WorkTurnAdm workTurnAdm = WorkTurnAdm.getInstance();
//
//								ArrayList activeTurnList =
//									PosTurnList.getInstance().findByState(0);
//								if (activeTurnList.size() > 0) {
//									workTurnAdm.uploadNewPosTurn(
//										(PosTurn) activeTurnList.get(0));
//								}
//								workTurnAdm.uploadCompletePosTurn(turnList);
//							
//								posTurnList.deletePosTurnByState(1);
//								posTurnList.dump();
//
//							} catch (WorkTurnException e1) {
//								e1.printStackTrace();
//							}
//						}
//					}

				} else {
					context.setOnLine(false);
				}

				if (out != null) {
					out.displayConnStatus(context);
				}				
			}catch(Throwable t){
				t.printStackTrace();
			}

			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public void fromIni(String file) {

		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(file));
			if (prop.getProperty("upload_interval") != null) {
				interval = Long.parseLong(prop.getProperty("upload_interval"));
			}

		} catch (NumberFormatException ex) {
			interval = 60000;
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			interval = 60000;
		} catch (IOException ex) {
			ex.printStackTrace();
			interval = 60000;
		}
	}

}
