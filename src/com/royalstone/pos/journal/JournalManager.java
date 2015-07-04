package com.royalstone.pos.journal;

import java.io.File;
import java.io.FilenameFilter;

import com.royalstone.pos.common.PosContext;
import com.royalstone.pos.common.SheetValue;
import com.royalstone.pos.core.PaymentList;
import com.royalstone.pos.core.PosSheet;
import com.royalstone.pos.core.SaleList;
import com.royalstone.pos.io.PosDevOut;
import com.royalstone.pos.ticket.CreatePosSheet;

/**
 * @author liangxinbiao
 */

public class JournalManager {

	public static void main(String[] args) {
		JournalManager uploadJournal = new JournalManager();
		uploadJournal.upload();
	}

	public void upload() {
		this.upload(null);
	}

	public void upload(PosDevOut out) {
		File dir = new File("journal");
		String[] files = dir.list(new DirFilter(".xml"));
		PosContext context = PosContext.getInstance();
		String ip = context.getServerip();
		int port = context.getPort();
		JournalWriter writer = new JournalWriter(ip, port);
		if (out != null) {
			out.prompt("正在上传脱机流水,共有 " + files.length + " 个,已上传 0 个");
		}
		int count = 0;
		for (int i = 0; i < files.length; i++) {
			if (writer.writeJournal(files[i])) {
				count++;
				if (out != null) {
					out.prompt(
						"正在上传脱机流水,共有 "
							+ files.length
							+ " 个,已上传 "
							+ count
							+ " 个");
				}
			}
		}
	}

	public void delete() {
		File path = new File("journal");
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

	public int getUnuploadCount() {
		File path = new File("journal");
		String[] files = path.list(new DirFilter(".xml"));
		return files.length;
	}

	public void printUnuploadTicket() {

		File path = new File("journal");
		String[] files = path.list(new DirFilter(".xml"));
		for (int i = 0; i < files.length; i++) {
			printTicket("journal/" + files[i]);
		}
	}

	public void printTicket(String filename) {

		PosDevOut out = PosDevOut.getInstance();

		CreatePosSheet createpossheet = new CreatePosSheet();
		SaleList falsesalelist =
			createpossheet.CreateSalelst(filename, "falsesale");
		SaleList salelist = createpossheet.CreateSalelst(filename, "sale");
		PaymentList paymentlist = createpossheet.CreatePaylst(filename);
		String storeid = createpossheet.getstoreid(filename);
		String posid = createpossheet.getposid(filename);
		String cashierid = createpossheet.getcashierid(filename);
		String sheetid = createpossheet.getsheetid(filename);
		String workdate = createpossheet.getworkdate(filename);

		PosSheet possheet = new PosSheet();
		possheet.setFalseSaleList(falsesalelist);
		possheet.setSaleList(salelist);
		possheet.setPaymentList(paymentlist);
		possheet.updateValue();
		SheetValue sheetvalue = possheet.getValue();

		possheet.print();

		out.reprintHeader(
			storeid,
			posid,
			cashierid,
			sheetid,
			workdate,
			"未上传流水");

		for (int i = 0; i < possheet.getFalseSaleLen(); i++) {
			out.reprintdisplay(possheet.getFalseSale(i), sheetvalue);

		}

		for (int i = 0; i < possheet.getPayLen(); i++) {
			out.reprintdisplay(possheet.getPayment(i), sheetvalue);
		}

		out.reprintdisplayTrail(possheet,sheetvalue, "未上传流水",true);

	}

	private void deleteDir(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDir(files[i]);
				} else {
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
