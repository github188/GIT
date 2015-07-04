package com.royalstone.pos.journal;

import java.util.Vector;

/**
 * @author liangxinbiao
 */

public class ShowJournalLogList {

	public static void main(String[] args) {

		JournalLogList journalLogList = JournalLogList.getInstance();

		Vector v = journalLogList.getLogList();


		System.out.println("JournalName                     CreateTime    UploadTime    Status  ");


		for (int i = 0; i < v.size(); i++) {
			JournalLog log = (JournalLog) v.elementAt(i);
			String status = null;
			switch (log.getStatus()) {
				case JournalLog.BEGIN_UPLOAD :
					status = "BEGIN_UPLOAD";
					break;
				case JournalLog.CREATE :
					status = "CREATE";
					break;
				case JournalLog.UPLOAD_FAIL :
					status = "UPLOAD_FAIL";
					break;
				case JournalLog.UPLOADED :
					status = "UPLOADED";
					break;
				case JournalLog.UPLOADING :
					status = "UPLOADING";
					break;
			}
			System.out.println(
				"--------------------------------------------------");
			System.out.println(
				log.getJournalName()
					+ " "
					+ log.getCreateTime()
					+ " "
					+ log.getUploadTime()
					+ " "
					+ status);
		}
		
		System.exit(0);
	}
}
