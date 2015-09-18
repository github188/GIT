package com.royalstone.pos.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jdom.JDOMException;
import com.royalstone.pos.card.SHCardAutoRever;
import com.royalstone.pos.favor.DiscPrice;
import com.royalstone.pos.favor.DiscRate;
import com.royalstone.pos.favor.Discount;
import com.royalstone.pos.common.*;
import com.royalstone.pos.core.PosCore;
import com.royalstone.pos.core.SaleList;
import com.royalstone.pos.data.PosPriceData;
import com.royalstone.pos.data.PosTurn;
import com.royalstone.pos.data.PosTurnList;
import com.royalstone.pos.data.ShopClock;
import com.royalstone.pos.gui.Authorization;
import com.royalstone.pos.gui.CashBoxInfo;
import com.royalstone.pos.gui.DialogConfirm;
import com.royalstone.pos.gui.DialogInfo;
import com.royalstone.pos.gui.DispPrice;
import com.royalstone.pos.gui.DispWaiter;
import com.royalstone.pos.gui.HoldList;
import com.royalstone.pos.gui.Loadometer;
import com.royalstone.pos.gui.LogonDialog;
import com.royalstone.pos.gui.MainUI;
import com.royalstone.pos.gui.ModifyPassword;
import com.royalstone.pos.gui.PosFrame;
import com.royalstone.pos.gui.StartFrame;
import com.royalstone.pos.gui.UnLock;
import com.royalstone.pos.hardware.POSCashDrawer;
import com.royalstone.pos.hardware.POSPrinter;
import com.royalstone.pos.hardware.config.HardWareConfigure;
import com.royalstone.pos.invoke.realtime.RealTime;
import com.royalstone.pos.invoke.realtime.RealTimeException;
import com.royalstone.pos.io.PosDevIn;
import com.royalstone.pos.io.PosDevOut;
import com.royalstone.pos.io.PosInput;
import com.royalstone.pos.io.PosInputGoods;
import com.royalstone.pos.io.PosInputLogon;
import com.royalstone.pos.io.PosInputPayment;
import com.royalstone.pos.journal.JournalLogList;
import com.royalstone.pos.journal.JournalManager;
import com.royalstone.pos.journal.JournalUploader;
import com.royalstone.pos.journal.LogManager;
import com.royalstone.pos.keymap.AllKeyEventListener;
import com.royalstone.pos.keymap.Wait4Lock;
import com.royalstone.pos.loader.PosSynchronizer;
import com.royalstone.pos.managment.ClerkAdm;
import com.royalstone.pos.managment.WorkTurnAdm;
import com.royalstone.pos.notify.NotifyChangePriceConsumer;
import com.royalstone.pos.util.*;
import com.royalstone.pos.web.util.ConnectionFactoryDBCP;
import com.royalstone.pos.web.util.DBConnection;
import com.royalstone.pos.web.util.IConnectionFactory;
import com.royalstone.pos.workTurn.WorkTurnException;
import com.royalstone.pos.card.MemberCardMgr;

public class pos {
	public static Object uploadLock = new Object();
	public static Object Lock = new Object();
	public static Object workTurnLock = new Object();
	public static PosFrame posFrame = null;
	public static PosCore core = null;
	public static PipedOutputStream posOutStream = null;
	public static StartFrame startFrame = null;
	public static FileOutputStream logger = null;
	private static PosShell sh = null;
	public static boolean hasServer = false;			// POS���б�־λ 0 ����ִ�� 1 TOMCAT��ת
	public static boolean hasServerCard = false;		// POS���л�Ա�� 0 ����ִ�� 1 TOMCAT��ת
	public static boolean hasServerCards = false;	// POS���д�ֵ�� 0 ����ִ�� 1 TOMCAT��ת
	public static boolean IF_SALE=false;//�Ƿ�ʹ�õڶ�
	
	public static void main(String[] args) {
		FileLock fileLock = null;
		try {
			FileOutputStream fos = new FileOutputStream("lock");
			fileLock = fos.getChannel().tryLock();
			if (fileLock == null) {
				JOptionPane.showMessageDialog(null, "POS�����Ѿ����У�");
				System.exit(1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "���ش���");
			System.exit(1);
		}

		try {

			System.out.println("javapos start...\n");

			prepareDir("log");
			prepareDir("data");
			prepareDir("work");
			prepareDir("journal");
			prepareDir("journalLog");
			prepareDir("poslog");
			prepareDir("price");
            prepareDir("promo");

			redirectOutput();

			AllKeyEventListener allKeyEventListener =
				new AllKeyEventListener("winkeymap.xml");
				
			DbConfig dbConfig = new DbConfig("db.ini");

			pos.hasServer = dbConfig.hasServer();

			if (!pos.hasServer) {

				ConnectionFactoryDBCP connectionFactoryDBCP =
					new ConnectionFactoryDBCP();

				try {

					connectionFactoryDBCP.addConnection(
						"java:comp/env/dbpos",
						dbConfig.getDriver(),
						dbConfig.getUrl(),
						dbConfig.getUser(),
						dbConfig.getPassword(),
						dbConfig.getMaxActive(),
						dbConfig.getMaxIdel(),
						dbConfig.getMaxWaitTime());

				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "���Ӳ��������ݿ⣡");
					System.exit(1);
				}

				if ((new File("db_card.ini")).exists()) {

					DbConfig dbCardConfig = new DbConfig("db_card.ini");
					pos.hasServerCard = dbConfig.hasServer();

					try {

						connectionFactoryDBCP.addConnection(
							"java:comp/env/dbcard",
							dbCardConfig.getDriver(),
							dbCardConfig.getUrl(),
							dbCardConfig.getUser(),
							dbCardConfig.getPassword(),
							dbCardConfig.getMaxActive(),
							dbCardConfig.getMaxIdel(),
							dbCardConfig.getMaxWaitTime());

					} catch (Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(
							null,
							"���Ӳ��Ͽ����ݿ⣬����ع��ܿ��ܲ����ã�");
						}
				// ��ɽ��Ŀ ֧�ֿ���������� 2005��10��13��	
				if ((new File("db_cards.ini")).exists()){
					
					DbConfig dbCardsConfig = new DbConfig("db_cards.ini");
					pos.hasServerCards = dbConfig.hasServer();
					
					try {
						
						connectionFactoryDBCP.addConnection(
								"java:comp/env/dbcards",
								dbCardsConfig.getDriver(),
								dbCardsConfig.getUrl(),
								dbCardsConfig.getUser(),
								dbCardsConfig.getPassword(),
								dbCardsConfig.getMaxActive(),
								dbCardsConfig.getMaxIdel(),
								dbCardsConfig.getMaxWaitTime());
						} catch (Exception ex){
							ex.printStackTrace();
							JOptionPane.showMessageDialog(
									null,
									"���Ӳ��Ͽ����ݿ⣬����ع��ܿ��ܲ����ã�");
							}
					}
				//-----------------------------------------------
				
				}
				IConnectionFactory factory =
					DBConnection.getConnectionFactory();
				DBConnection.setConnectionFactory(connectionFactoryDBCP);
			}
			sh = new PosShell();
			sh.synchronizeData();

			sh.init();
		
			sh.run();

		} catch (Throwable t) {
			t.printStackTrace();
			generateErrLog();
			JOptionPane.showMessageDialog(
				null,
				"ϵͳ�������ش���,�뽫�����ϵ�\"RTPOS������Ϣ\"���͵�����֧�ֲ���!");
			System.exit(3);
		}
	}

	public static void activeUploader() {
		sh.activeUploader();
	}

	public static void setStateBUnlock() {
		sh.setStateBUnlock();
	}

	public static boolean isLock() {
		return sh.getState() == PosState.LOCK;
	}

	private static void redirectOutput() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			File file = new File("log/pos41.log");
			if (file.exists()) {
				FileChannel infile =
					new FileInputStream("log/pos41.log").getChannel();
				FileChannel outfile =
					new FileOutputStream(
						"log/pos41_" + sdf.format(new Date()) + "_R.log")
						.getChannel();
				infile.transferTo(0, infile.size(), outfile);
			}

			logger = new FileOutputStream("log/pos41.log");
			System.setOut(new PrintStream(logger));
			System.setErr(new PrintStream(logger));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.err.println("ERROR: Connot open log file, exit ...");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean prepareDir(String dirname) {
		File dir = new File(dirname);
		if (dir.exists() && dir.isDirectory())
			return true;
		if (dir.exists() && !dir.isDirectory())
			return false;
		return dir.mkdir();
	}

	public static void reinit() {
		sh.synchronizeData();
		try {
			sh.reinit();
		} catch (InvalidDataException e) {
			e.printStackTrace();
			System.out.println("Reinit ERROR: Invalid data!");
			System.exit(3);
		}
	}

	private static void generateErrLog() {

		try {
			Properties prop = new Properties();

			try {
				prop.load(new FileInputStream("ErrorLog.properties"));
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			String destPath =
				prop.getProperty(
					"Path",
					new String(
						(System.getProperty("user.home") + "/����/RTPOS������Ϣ")
							.getBytes(),
						"ISO8859-1"));

			destPath = new String(destPath.getBytes("ISO8859-1"), "GB2312");

			String destDir[] = destPath.split("/");
			String path = null;
			for (int i = 0; i < destDir.length; i++) {
				if (path != null) {
					path = path + "/" + destDir[i];
					prepareDir(path);
				} else {
					path = destDir[i];
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			FileChannel infile =
				new FileInputStream("log/pos41.log").getChannel();
			FileChannel outfile =
				new FileOutputStream(
					destPath
						+ File.separator
						+ "error"
						+ sdf.format(new Date())
						+ ".log")
					.getChannel();
			infile.transferTo(0, infile.size(), outfile);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}

class PosShell {
	Wait4Lock lock;

	private Thread journalUploader;
	public void activeUploader() {
		if (journalUploader != null) {
			journalUploader.interrupt();
		}
	}

	public void init() throws InvalidDataException {
		System.out.println("javapos init...\n");
     
		// Init Context
		initContext();
		context.setOnLine(true);

		// Init PosCore
		pos.core = core = new PosCore();
		core.init();
//-----����POS��Ӳ������--------------------------
		PosConfig posconfig=PosConfig.getInstance();
		HardWareConfigure hwConfig=new HardWareConfigure(posconfig); 
		    hwConfig.initHardWareConfig();
//----------------------------------
	//��ʼ��Ǯ��	    
	    this.cashDrawer=POSCashDrawer.getInstance();
	    
	    POSPrinter posprinter=POSPrinter.getInstance();
	    
		if(posconfig.getString("PRINT_ONLINE").equals("ON")){
			posprinter.setEnable(true);
		}else{
			posprinter.setEnable(false);
		}
		    
		state = new PosState(PosState.PRELOGON);

//		Thread t = new Thread(new LoanCardAutoRever());
//		t.start();

        Thread t = new Thread(new SHCardAutoRever());
		t.start();

		Thread t_log = new Thread(new LoanLog());
		t_log.start();

		lock = new Wait4Lock();
		lock.start();
//TODO   ����POS�ͻ��˵�ʵʱ���۽����߳�   ���ݸ��� by fire  2005_5_11 
//		Thread r = new Thread(new NotifyReceiver());
//		r.start();

		journalUploader = new Thread(new JournalUploader(context));
		journalUploader.start();
	}

	public void reinit() throws InvalidDataException {
		initContext();
		core.init();
	}

	private void generateLog() {
		if (pos.logger != null) {
			try {
				deleteLog();

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				FileChannel infile =
					new FileInputStream("log/pos41.log").getChannel();
				FileChannel outfile =
					new FileOutputStream(
						"log/pos41_" + sdf.format(new Date()) + ".log")
						.getChannel();
				infile.transferTo(0, infile.size(), outfile);

				pos.logger = new FileOutputStream("log/pos41.log");
				System.setOut(new PrintStream(pos.logger));
				System.setErr(new PrintStream(pos.logger));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void deleteLog() {
		File path = new File("log");
		File[] dirList = path.listFiles();
		if (dirList.length > 1500) {
			for (int i = 0; i < dirList.length; i++) {
				if (!dirList[i].getName().equals("pos41.log")) {
					dirList[i].delete();
				}
			}
		}
	}

	public void synchronizeData() {

		System.out.println("load pos.xml, price.xml, operator.lst ... ");
		try {
			PosSynchronizer s = new PosSynchronizer("pos.ini");
			s.synchronize();
		} catch (FileNotFoundException e) {
			System.err.println("ERROR: Connot open pos.ini, exit ...");
			System.exit(2);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(
				"WARNING: Downloading new data failed, go on working with dated data.");
			if (!confirm("�����ѻ�����ʧ�ܣ��Ƿ������")) {
				System.exit(2);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			System.err.println(
				"WARNING: Downloading new data failed, go on working with dated data.");
			if (!confirm("�����ѻ�����ʧ�ܣ��Ƿ������")) {
				System.exit(2);
			}
		}
	}

	public int getState() {
		//System.out.println("state.getState() in posShell : "+state.getState());
		return state.getState();
	}

	public PosState get_state() {
		return state;
	}

	private void initContext() {
		if ((new File(PosContext.file4context)).exists()) {
			context = PosContext.load();
			context.fromIni("pos.ini");
			context.fromXML("pos.xml");
			context.setWarning("");
		} else {
			context = PosContext.getInstance();
			context.fromIni("pos.ini");
			context.fromXML("pos.xml");
            context.setWorkDate(new Day());
		}
	}

	public void run() {
		System.out.println("javapos run...");

		while (true) {

			switch (state.getState()) {
				case PosState.PRELOGON :
					if (context.isOnLine()) {
						logon_online();
					} else {
						logon();
					}
					break;
				case PosState.PRESALE :
					presale();
					break;
				case PosState.SALE :
					sale();
					break;
				case PosState.DRUG :	// ��  ҩ
					Drug();
					break;
				case PosState.FIND :
					find();
					break;
				case PosState.MAXCASH :
					maxcash();
					break;
				case PosState.CORRECT :
					correct();
					break;
				case PosState.WITHDRAW :
					withdraw();
					break;
				case PosState.DISCOUNT :
					singleDisc();
					break;
				case PosState.DISCTOTAL :
					totalDisc();
					break;
				case PosState.DISCMONEY :
					moneyDisc();
					break;
				case PosState.ALTPRICE :
					altPrice();
					break;
				case PosState.LOCK :
					UnLock unLock = new UnLock();
					unLock.show();
					//wait_unlock();
					break;
				case PosState.OPENSHEET :
					break;
				case PosState.CLOSESHEET :
					commitSheet(inputpayment);
					String printtype = PosContext.getInstance().getPrintType();
					if (printtype != null || !printtype.equals("")) {
						if (printtype.compareToIgnoreCase("INVOICE") == 0) {
							printInvoice();
						}
					}
					break;
				case PosState.CASHIN :
					cashin();
					break;
				case PosState.CASHOUT :
					cashout();
					break;
				case PosState.NETWORKERROR :
					networkError();
					break;
				case PosState.ERROR :
					in.waitCancel(context.getWarning());
					context.setWarning("");
					if (state.getOldState() == PosState.FIND) {
						state.setState(PosState.FIND);
					} if(state.getOldState() == PosState.DRUG){
						state.setState(PosState.DRUG);
						}else {
						if (core.isSheetEmpty())
							state.setState(PosState.PRESALE);
						else
							state.setState(PosState.SALE);
					}
					break;
				default :
					;
			}
		}
	}

	private void networkError() {

		out.displayState("�������");
		out.clearInputLine();

		do {
			out.prompt("�������,������������������������л����л����ѻ�!");
			boolean reconnect = in.waitReConnect();

			if (reconnect) {
				out.prompt("���ڳ�����������");
				if (RealTime.getInstance().testOnLine()) {
					notice("�����ɹ�!");
					break;
				} else {
					in.clearKey();
					notice("����ʧ��!");
				}
			} else {
				notice("�ɹ�ת��Ϊ�ѻ�������ʽ!");
				context.setOnLine(false);
				out.display(context);
				break;
			}
		} while (true);
		context.setWarning("");
		state.setState(state.getOldState());

	}

	/*
	 * Make a requestLogon with ID, PIN, shiftid.
	 * Send request to server.
	 * Receive reply from server.
	 * Set Work Environment: workdate, shiftid, sheetid, etc.
	 */
	private void logon_online() {

		do {
			if (!RealTime.getInstance().testOnLine()) {
				if (!confirm("���̨����������ʧ�ܣ�����(ȷ��)���ѻ�����(ȡ��)��")) {
					context.setOnLine(false);
					state.setState(PosState.PRELOGON);
					return;
				}
			} else {
				break;
			}
		} while (true);

		initLogonIO();

		synchronized (pos.uploadLock) {
			JournalManager journalManager = new JournalManager();
			journalManager.upload(out);
		}

		out.displayState("���¼");
		out.clear();

		String note = context.getWarning();

		if (note.length() == 0)
			note = "����������Ա��ź�����";
		PosInput inp = in.getInputLogon(note);
		if (inp != null && inp.key() == PosFunction.EXIT) {
			System.err.println("User exit...");
			core.exit();
			System.exit(0);
		}
		if (inp == null || !(inp instanceof PosInputLogon))
			return;

		String server = context.getServerip();
		int port = context.getPort();
		String posid = context.getPosid();

		PosInputLogon input = (PosInputLogon) inp;

		String cashierid = input.getID();
		String pin = input.getPIN();
		//TODO  ���ݸ��� by fire  2005_5_11 
		//int shiftid = input.getShiftid();
		/**
		 * updated begin by huangxuean 25 Jun 2004
		 * */
		String localip = null;
		try {
			InetAddress iadd = InetAddress.getLocalHost();
			localip = iadd.getHostAddress();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		/**
		 * updated end by huangxuean 25 Jun 2004
		 * */

		ClerkAdm adm = new ClerkAdm(server, port);
		Response r = adm.getClerk(posid, cashierid, pin);
		Operator op = (Operator) r.getObject();
		if (op != null) {
            context.updateWorkDay(r.getListNO());
			System.out.println("��¼�ɹ�......");
			lock.setState(true);

			cashier = op;
			core.setCashierid(cashierid);

			logonDialog.dispose();

			openPosWindow();

			int getid = Integer.parseInt(op.getId());
			core.writelog("��¼", "0", getid);
			out.print("����Ա:" + op.getId() + " ��¼");
			out.printFeed(out.getFeedLines());
			out.cutPaper();

			// check current core and decide which state to go.
			// if current sheet is empty, go to PRESALE, else go to SALE.
			////out.displayHeader(context);
			out.displayWelcome();
			
			out.displayPrinterStatus();
			
			in.clearKey();
			if (core.isSheetEmpty()) {
				state.setState(PosState.PRESALE);
			} else {
                if(core.getPosSheet().getMemberCard()!=null)
                 out.dispMemberCardHeader(core.getPosSheet().getMemberCard());
				out.displayHeader(context);
				displaySheet();
				state.setState(PosState.SALE);
			}
			return;
		} else {
			core.writelog("��¼", "1", Integer.parseInt(cashierid));
			context.setWarning(r.getNote());
			state.setState(PosState.PRELOGON);
			logonDialog.getLogonPanel().clear();
		}
	}

	private void logon() {

		if (RealTime.getInstance().testOnLine()) {
			if (confirm("�������ɹ����ӣ���������(ȷ��)���ѻ�����(ȡ��)��")) {
				context.setOnLine(true);
				state.setState(PosState.PRELOGON);
				return;
			}
		}

		initLogonIO();
		out.displayState("���¼");
		out.clear();

		String note = context.getWarning();
		if (note.length() == 0)
			note = "����������Ա��ź�����";

		PosInput inp = in.getInputLogon(note + "(�ѻ�״̬)");
		if (inp == null)
			return;
		if (inp.key() == PosFunction.CANCEL)
			return;
		if (inp.key() == PosFunction.EXIT)
			System.exit(0);

		if (!(inp instanceof PosInputLogon)) {
			System.out.println("Canceled!");
			return;
		}

		PosInputLogon input = (PosInputLogon) inp;

		String id = input.getID();
		String pin = input.getPIN();
		//TODO  ���ݸ��� by fire  2005_5_11 
		//int work_turn = input.getShiftid();

		OperatorList lst = new OperatorList();
		lst.load("operator.lst");

		Operator op = lst.get(id, pin);
		if (op == null) {
			core.writelog("��¼", "1", 0);
			context.setWarning("�û��������벻��ȷ");
			state.setState(PosState.PRELOGON);
			logonDialog.getLogonPanel().clear();
		} else {
          //TODO    �޸ĵ�½����  ���ݸ��� by fire  2005_5_11
            context.updateWorkDay();
        	cashier = op;
			core.setCashierid(id);

			logonDialog.dispose();

			openPosWindow();
			lock.setState(true);

			int getid = Integer.parseInt(op.getId());
			core.writelog("��¼", "0", getid);

			out.print("����Ա:" + op.getId() + " ��¼");
			out.printFeed(out.getFeedLines());
			out.cutPaper();
			in.clearKey();
			if (core.isSheetEmpty()) {
				////out.displayHeader(context);
				out.displayWelcome();
				state.setState(PosState.PRESALE);
			} else {
				////out.displayHeader(context);
				out.displayWelcome();
				if(core.getPosSheet().getMemberCard()!=null)
                 out.dispMemberCardHeader(core.getPosSheet().getMemberCard());
                displaySheet();
				state.setState(PosState.SALE);
			}

		}
	}

	private void initLogonIO() {
		pos.posOutStream = null;
		PipedInputStream posInputStream = null;

		try {
			pos.posOutStream = new PipedOutputStream();
			posInputStream = new PipedInputStream(pos.posOutStream);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// init logonDislog ...
		if (logonDialog == null) {
			logonDialog = new LogonDialog(pos.posOutStream);
			if (pos.startFrame != null) {
				pos.startFrame.dispose();
			}
		} else {
			logonDialog.getLogonPanel().setOutputStream(pos.posOutStream);
		}
		logonDialog.show();
		logonDialog.requestFocus();

		// Init PosIO
		out = PosDevOut.getInstance();
		out.setMainUI(logonDialog.getLogonPanel());
		in = PosDevIn.getInstance();
		in.setOut(out);
		in.setPosInputStream(posInputStream);
		in.init();

	}

	private void openPosWindow() {
		pos.posFrame = new PosFrame(pos.posOutStream);
		out.setMainUI(pos.posFrame);
		pos.posFrame.setUndecorated(true);
		pos.posFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		pos.posFrame.show();
	}

	private void displaySheet() {
		int temp = 0;
		for (int i = 0; i < core.getFalseSaleLen(); i++) {
			
				out.displayUnhold(core.getFalseSale(i), core.getValue());

		}

		for (int i = 0; i < core.getPayLen(); i++) {
			out.display(core.getPayment(i), core.getValue());
		}
		out.display(core.getValue(), context.getExchange());
	}
	/*
		private void wait_unlock() {
			out.displayState("������");
			PosInput inp = in.getInputPin("POS��������������������Ա���������");
			if (inp == null || !(inp instanceof PosInputPin))
				return;
	
			PosInputPin input = (PosInputPin) inp;
	
			String pin = input.getPIN();
	
			ClerkAdm adm = new ClerkAdm(context.getServerip(), context.getPort());
			Response r =
				adm.getClerk(context.getPosid(), context.getCashierid(), pin);
			Operator op = (Operator) r.getObject();
			if (op != null) {
				if (core.isSheetEmpty())
					state.setState(PosState.PRESALE);
				else
					state.setState(PosState.SALE);
			}
		}
	*/
	public void setStateBUnlock() {
		if (core.isSheetEmpty())
			state.setState(PosState.PRESALE);
		else
			state.setState(PosState.SALE);
	}

	private boolean getAuthority(GetAuthor author) throws UserCancelException {
		Authorization authrization = new Authorization();
		MainUI oldMainUI = out.getMainUI();
		out.setMainUI(authrization);
		authrization.show();
		try {

			PosInput inp;
			inp = in.getInputAuthority("��������Ȩ���ܵı�ź�����");

			out.setMainUI(oldMainUI);
			authrization.dispose();

			if (inp == null || !(inp instanceof PosInputLogon)) {
				System.out.println("Canceled!");
				// �޸�
				return false;
			}

			PosInputLogon input = (PosInputLogon) inp;

			String id = input.getID();
			String pin = input.getPIN();

			if (author != null) {
				author.setid(id);
			}

			Operator op = null;

			if (context.isOnLine()) {
				ClerkAdm adm =
					new ClerkAdm(context.getServerip(), context.getPort());
				Response r = adm.getClerk(context.getPosid(), id, pin);

				if (r != null) {
					if (r.retCode() != -1) {
						op = (Operator) r.getObject();
					} else {
						OperatorList lst = new OperatorList();
						lst.load("operator.lst");
						op = lst.get(id, pin);
					}
				}
			} else {
				OperatorList lst = new OperatorList();
				lst.load("operator.lst");
				op = lst.get(id, pin);
			}

			if (op != null) {
				cashier.addPrivilege(op);
				context.setAuthorizerid(op.getId());
				authorizeCashier = op;
			}
			//�޸�
			else return false;
			
			return true;
		} catch (UserCancelException e) {
			out.setMainUI(oldMainUI);
			authrization.dispose();
			throw new UserCancelException("User Cancel!");
		}

	}

	private boolean checkPrivilege(int fun) throws UserCancelException {

		return checkPrivilege(fun, null);
	}

	private boolean checkPrivilege(int fun, GetAuthor author)
		throws UserCancelException {
		//�����ж��Ƿ���Ҫ��Ȩ
/*
		if(this.needAuth(fun)){
			if(!getAuthority(author)){
				context.setWarning("��Ȩʧ��,�����������!");
				state.setState(PosState.ERROR);
				return false;
			}
		}
		if (!cashier.hasPrivilege(fun)) {
			if(!getAuthority(author)){
				context.setWarning("��Ȩʧ��,�����������!");
				state.setState(PosState.ERROR);
				return false;
			}
		} else {
			authorizeCashier = cashier;
			if (author != null) {
				author.setid(cashier.getId());
			}
		}

		if (!cashier.hasPrivilege(fun)) {
			context.setWarning("��Ȩʧ��,�����������!");
			state.setState(PosState.ERROR);
		}

		return cashier.hasPrivilege(fun);
 */
       //-------------------------
        if (this.needAuth(fun)) {
                  if (!getAuthority(author)) {
                      context.setWarning("��Ȩʧ��,�����������!");
                      state.setState(PosState.ERROR);
                      return false;
                  }
        }else if (!cashier.hasPrivilege(fun)) {
                  if (!getAuthority(author)) {
                      context.setWarning("��Ȩʧ��,�����������!");
                      state.setState(PosState.ERROR);
                      return false;
                  }
        }
         else {
                      authorizeCashier = cashier;
                  if (author != null) {
                      author.setid(cashier.getId());
                  }
         }

         if (!cashier.hasPrivilege(fun)) {
                  context.setWarning("��Ȩʧ��,�����������!");
                  state.setState(PosState.ERROR);
              }


        return cashier.hasPrivilege(fun);



	}
 /**
  * �жϲ���������ʹ�õĹ����Ƿ���Ҫ��Ȩ
  * @param fun
  * @return
  */
   private boolean needAuth(int fun){
   	String authStr=this.getAuthStr(fun); 
   	PosConfig config=PosConfig.getInstance();
   	String authFlag=config.getString(authStr);
   	if(authFlag.equals("ON"))
   		return true;
   	return false;
   }
   private String getAuthStr(int fun){
   	if(fun==38)return "AUTH_BLANKTRAN";
   	if(fun==41)return "AUTH_DiscMoney";
   	if(fun==43)return "AUTH_LASTPRINT";
   	if(fun==60)return "IF_INCR_AUTH";
   	if(fun==62)return "IF_DECR_AUTH";
   	if(fun==67)return "AUTH_VOIDITEM";
   	if(fun==70)return "AUTH_Return";
   	if(fun==77)return "AUTH_AltPrice";
   	if(fun==123)return "AUTH_Discount";
    if (fun == 125) return "AUTH_DiscTotal";
    return "";
   }
//-----------------------------------	
	private void showWaiter() {
		String waiter_show;

		DispWaiter dispWaiter = new DispWaiter();
		dispWaiter.show();

		if (dispWaiter.isConfirm()) {

			waiter_show = dispWaiter.getShowWaiter();
			System.out.println("����ӪҵԱ���");
			out.dispWaiter("ӪҵԱ:" + waiter_show);
			out.setWaiter(waiter_show);

			state.setState(PosState.PRESALE);
		}

	}

	public boolean prepareDir(String dirname) {
		File dir = new File(dirname);
		if (dir.exists() && dir.isDirectory())
			return true;
		if (dir.exists() && !dir.isDirectory())
			return false;
		return dir.mkdir();
	}

	public boolean prepareFile(String filename) {
		File file = new File(filename);
		if (file.exists() == true) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * �ش���һ������reprintsheet.xml�ļ�������һ���������۽��д�ӡ��ֻ���ش���һ������
	 * */
	private void printLastSheet(PosInputPayment payment) {
		prepareDir("reprint");
		boolean fileexist = prepareFile("reprint/reprintsheet.xml");
		if (fileexist == false) {

			core.setWarning("�ش�СƱ�ļ�������,�����������!");
			state.setState(PosState.ERROR);
		} else {

			out.printFeed(out.getFeedLines());
			out.cutPaper();
			//out.dispLastPrint(context,payment);
			out.dispLastPrint(context);
			////out.displayHeader(context);
		}
	}
	/**
	 * ��ӡ�����嵥
	 * */
	private void printInvoice() {
		out.printFeed(out.getFeedLines());
		out.cutPaper();
		out.printInvoice();
	}

	/**
	 * �޸����룬ֻ��������״̬�½����޸���Ϊ
	 * */
	private void alterPin() {
		String pin_old, pin_new, pin_confirm;
		String server = context.getServerip();
		int port = context.getPort();
		String posid = context.getPosid();
		String cashierid = context.getCashierid();

		ModifyPassword modifyPassword = new ModifyPassword();
		modifyPassword.show();

		if (modifyPassword.isConfirm()) {
			pin_old = modifyPassword.getOldPassword();
			pin_new = modifyPassword.getNewPassword();
			pin_confirm = modifyPassword.getConfirmPassword();

			if (pin_new.equals(pin_old))
				core.setWarning("������Ч�����������������ͬ,�����������!");
			if (!pin_new.equals(pin_confirm))
				core.setWarning("������Ч��������������벻һ��,�����������!");
			if (!pin_new.equals(pin_old) && pin_new.equals(pin_confirm)) {
				ClerkAdm adm = new ClerkAdm(server, port);
				Response r = adm.alterPin(posid, cashierid, pin_old, pin_new);
				if (r != null && r.succeed())
					core.setWarning("�����޸ĳɹ�,�����������!");
				else
					core.setWarning("�����޸�ʧ��,�����������!");
			}

			state.setState(PosState.ERROR);
		} else {
			//			core.setWarning("ȡ��");
			//			state.setState(PosState.ERROR);
		}
	}

	private void logoff() {

		int count = context.getHeldCount();
		if (count > 0) {
			context.setWarning("�йҵ������˳�,�����������!");
			core.writelogexit("�˳�", "1", 0);
			state.setState(PosState.ERROR);
		} else if (confirm("�Ƿ��˳�ϵͳ��")) {
			if (context.isOnLine()) {
				WorkTurnAdm w = new WorkTurnAdm(context);
				Response rep = (Response) w.logoff();

				synchronized (pos.uploadLock) {
					JournalManager journalManager = new JournalManager();
					journalManager.upload(out);
				}
			}

			lock.setState(false);
			pos.posFrame.exit();
			state.setState(PosState.PRELOGON);
			core.writelogexit("�˳�", "0", 0);
			System.out.println("�˳��ɹ�......");
			out.print("����Ա:" + context.getCashierid() + " �˳�");
			out.printFeed(out.getFeedLines());
			out.cutPaper();
			context.setWarning("");
			generateLog();
		}

	}

	private void closeWorkTurn() {

		int count = context.getHeldCount();
		if (count > 0) {
			context.setWarning("�йҵ����������,�����������!");
			state.setState(PosState.ERROR);
			return;
		}

		if (context.getShiftid() == 3) {
			if (context
				.getWorkDate()
				.getGregorian()
				.getTime()
				.compareTo((new Date()))
				>= 0) {
				context.setWarning("�������ڲ��ܴ���ϵͳ����,�����������!");
				state.setState(PosState.ERROR);
				return;
			}
		}


		if (RealTime.getInstance().testOnLine()) {
			context.setOnLine(true);
		} else {
			context.setOnLine(false);
		}
		out.displayConnStatus(context);
		
		DialogConfirm confirm;
		if (context.isOnLine()) {
			confirm = new DialogConfirm();
			confirm.setMessage("��������������ȷʵҪ�������");
		}else{
			confirm = new DialogConfirm(580, 160);
			confirm.setMessage("ϵͳ�����ѻ�״̬�������������ݿ��ܻ�û�ϴ���ȷʵҪ���ѻ������");
		}

		confirm.show();
		if (!confirm.isConfirm())
			return;


		synchronized (pos.uploadLock) {
			JournalManager journalManager = new JournalManager();

			int unloadCount1 = journalManager.getUnuploadCount();
			int unloadCount2 = JournalLogList.getUnuploadCount();

			if (unloadCount1 != unloadCount2) {
				System.out.println("�ѻ���ˮ�ļ���Ŀ����ˮ��־��¼����ˮ����һ��!");
				System.out.println(
					Formatter.getDateFile(new Date())
						+ " FileCount="
						+ unloadCount1
						+ " LogCount="
						+ unloadCount2);
			} else {
				System.out.println(
					Formatter.getDateFile(new Date())
						+ " FileCount="
						+ unloadCount1);
			}

			DialogConfirm confirm2 = new DialogConfirm(550, 160);
			while (unloadCount1 > 0) {

				confirm2.setMessage(
					"���� " + unloadCount1 + " ���ѻ���ˮû�ϴ������³����ϴ�(ȷ��)��������(ȡ��)��");
				confirm2.setWarning("���������ܻᵼ����Щ�ѻ���ˮ������һ������������У�");
				confirm2.show();

				if (!confirm2.isConfirm()) {
					System.out.println(
						Formatter.getDateFile(new Date())
							+ " �û�ѡ���ϴ��ѻ���ˮ,�������.");
					break;
				} else {
					System.out.println(
						Formatter.getDateFile(new Date())
							+ " �û�ѡ������,�����ϴ��ѻ���ˮ.");
				}

				if (RealTime.getInstance().testOnLine()) {
					context.setOnLine(true);
					journalManager.upload(out);
				} else {
					context.setOnLine(false);
				}

				unloadCount1 = journalManager.getUnuploadCount();
			}

			if (unloadCount1 == 0) {

				System.out.println(
					Formatter.getDateFile(new Date())
						+ " Rename JournalLogList");
				JournalLogList.rename();
			}

		}

		if (context.isOnLine()) {
			closeWorkTurn_online();
		} else {

			try {
				if (checkPrivilege(PosFunction.OFFLINECLOSEWORKTURN, null)) {
					closeWorkTurn_offline();
					cashier.resetPrivilege();
					context.setAuthorizerid("");
					authorizeCashier = null;
				} else {
					state.setState(PosState.ERROR);
				}
			} catch (UserCancelException e) {
				e.printStackTrace();
			}

		}
	}

	private void closeWorkTurn_online() {

		synchronized (pos.workTurnLock) {

			com.royalstone.pos.workTurn.WorkTurnAdm w =
				com.royalstone.pos.workTurn.WorkTurnAdm.getInstance();
			try {
				w.closeWorkTurn(context, context.getPosid());
			} catch (WorkTurnException e1) {
				context.setWarning(e1.getMessage());
				state.setState(PosState.ERROR);
				return;
			}

		}

		LogManager logManager = new LogManager();
		logManager.delete();

		// ��ӡ����� ...
		if (PosConfig.getInstance().getString("CLEAR_POS") != null
			&& PosConfig.getInstance().getString("CLEAR_POS").equals("ON")) {
			CashBasket basket = core.getCashBasket();
			out.printFeed(10);
			out.print("�����");
			out.printWithoutSeperator("����Ա:" + context.getCashierid());
			out.printWithoutSeperator(
				"��  ��:" + context.getWorkDate().toString());
			out.printWithoutSeperator(
				"��  ��:" + context.getWorkTurn().getShiftid());
			for (int i = 0; i < basket.size(); i++) {
				CashBox box = basket.get(i);
				System.out.println(box.toString());
				out.display(box);
			}
			out.print("");
			out.printFeed(out.getFeedLines());
			out.cutPaper();
		}

		try {
			core.resetCashBasket();
			core.dump();
		} catch (IOException e) {
			e.printStackTrace();
		}

		out.prompt("���ڸ����ѻ����ϡ���");
		pos.reinit();

		DialogInfo notice = new DialogInfo();

		notice.setMessage("���ɹ���");
		notice.show();

		pos.posFrame.exit();
		out.print("���");
		out.print("����Ա:" + context.getCashierid() + " �˳�");
		out.printFeed(out.getFeedLines());
		out.cutPaper();
		state.setState(PosState.PRELOGON);
		context.setWarning("");
		generateLog();
	}

	private void closeWorkTurn_offline() {

		synchronized (pos.workTurnLock) {

			PosTurnList turnList = PosTurnList.getInstance();
			ArrayList activeTurnList = turnList.findByState(0);
			if (activeTurnList.size() > 0) {

				PosTurn activeTurn = (PosTurn) activeTurnList.get(0);
				activeTurn.setEndOffLine(true);
				activeTurn.setEndTime(new Date());
				activeTurn.setStat(1);
				turnList.dump();

				ShopClock shopClock = ShopClock.getInstance();
				shopClock.nextTurn(context);

				DialogInfo notice = new DialogInfo();
				// ��ӡ����� ...
				if (PosConfig.getInstance().getString("CLEAR_POS") != null
					&& PosConfig.getInstance().getString("CLEAR_POS").equals(
						"ON")) {
					CashBasket basket = core.getCashBasket();
					out.printFeed(10);
					out.print("�����");
					out.printWithoutSeperator("����Ա:" + context.getCashierid());
					out.printWithoutSeperator(
						"��  ��:" + context.getWorkDate().toString());
					out.printWithoutSeperator(
						"��  ��:" + context.getWorkTurn().getShiftid());
					for (int i = 0; i < basket.size(); i++) {
						CashBox box = basket.get(i);
						System.out.println(box.toString());
						out.display(box);
					}
					out.print("");
					out.printFeed(out.getFeedLines());
					out.cutPaper();
				}

				try {
					core.resetCashBasket();
					core.dump();
				} catch (IOException e) {
					e.printStackTrace();
				}

				notice.setMessage("�ѻ����ɹ���");
				notice.show();

				pos.posFrame.exit();
				out.print("�ѻ����");
				out.print("����Ա:" + context.getCashierid() + " �˳�");
				out.printFeed(out.getFeedLines());
				out.cutPaper();
				state.setState(PosState.PRELOGON);
				context.setWarning("");
				generateLog();
			} else {
				context.setWarning("������ݴ���,�����������!");
				state.setState(PosState.ERROR);
			}
		}

	}

	private void commitSheet(PosInputPayment input) {
		core.setCurrency("RMB");
		out.display(core.getValue(), context.getExchange());
		out.clear();
		out.displayChange(core.getValue());
		//add by lichao
		for (int i = 0; i < core.getPayLen(); i++) {
			Payment p = core.getPayment(i);
			if (p.getType() == 'C' || p.getType() == 'D') {
				//System.out.println("ֻ���ֽ���ߴ���ȯ�Ŵ�Ǯ��");
				cashDrawer.open();
				break;
			}
		}
		
		if (core.getValue().getDiscTotal() != 0) {
			out.displaySaleInfo(core.getPosSheet(),core.getValue());
			}
		// ��ӡ��Ա������
//		out.displayTrail(core.getPosSheet());
		out.display(core.getPosSheet().getMemberCard());
		// cashDrawer.open();
		out.displayTrail(core.getValue());
		core.closeSheet(input);
		try {
			core.openSheet();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		////out.displayHeader(context);
		out.dispWaiter("");
		out.setWaiter("");
		out.displayWelcome();
		// ��������
		context.drupnumber = 0;

		//System.out.println("�ȽϽ��Ϊ:"+core.CompareLimit());

		if (core.CompareLimit() == 1) {
			DialogInfo notice = new DialogInfo();
			notice.setMessage("�뽫�ֽ���⡣");
			notice.show();
			state.setState(PosState.PRESALE);
		} else if (core.CompareLimit() == 2) {
			DialogInfo notice = new DialogInfo();
			notice.setMessage("�ѳ�����޶�뽫�ֽ���⡣");
			notice.show();
			state.setState(PosState.MAXCASH);
		} else {
			state.setState(PosState.PRESALE);
		}
	}

	private void deletefindresult() {
		out.clear();
		//out.print("ȡ��");
		out.printFeed(out.getFeedLines());
		out.cutPaper();
		core.deleteFindresult();
		try {
			core.findpriceSheet();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		state.setState(PosState.PRESALE);
		////out.displayHeader(context);
	}

	private void deleteSheet() {
		out.clear();
		out.print("����ȡ��");
		out.printFeed(out.getFeedLines());
		out.cutPaper();
		core.deleteSheet();
		try {
			core.openSheet();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		////out.displayHeader(context);
		out.displayWelcome();
		state.setState(PosState.PRESALE);
	}

	private Payment closeSheetWithoutMoney(int k) {
		int paytype;
		if (k == PosFunction.FLEE)
			paytype = Payment.FLEE;
		else if (k == PosFunction.SAMPLE)
			paytype = Payment.SAMPLE;
		else if (k == PosFunction.OILTEST)
			paytype = Payment.OILTEST;
		else {
			context.setWarning("��Ч����,�����������!");
			return null;
		}

		Payment p = core.closeSheetWithoutMoney(paytype);
		return p;
	}

	private void presale() {
		context.drupnumber = 0;
		out.display(context);
		out.displayState("����");
		out.prompt("��������Ʒ���������룬�򰴹��ܼ���");
		PosInput input = in.getInput();

		NotifyChangePriceConsumer.getInstance().consume();

		switch (input.key()) {
			case PosFunction.CANCEL :
				break;
            case PosFunction.FIND:
                this.find();
                break;
			case PosFunction.WITHDRAW :

				try {
					if (checkPrivilege(PosFunction.WITHDRAW))
						state.setState(PosState.WITHDRAW);
					else
						state.setState(PosState.ERROR);
				} catch (UserCancelException e) {
					// do nothing.
				}

				break;
			case PosFunction.GOODS :
				sellGoods((PosInputGoods) input);
				break;

				/*case PosFunction.FIND :
					state.setState(PosState.FIND);
					break;
				*/
			case PosFunction.LOCK :
				state.setState(PosState.LOCK);
				//UnLock unLock = new UnLock();
				//unLock.show();
				break;
				
			case PosFunction.NumberDrug:	// ��ҩ����
				state.setState(PosState.DRUG);
				break;

			case PosFunction.HOLD :
				unhold();
				break;

			case PosFunction.SHIFT :
				closeWorkTurn();
				break;

			case PosFunction.EXIT :
				logoff();
				break;

			case PosFunction.NEWPASS :
				if (context.isOnLine()) {
					alterPin();
				} else {
					context.setWarning("�������ѻ�״̬���޸����룬�����������!");
					state.setState(PosState.ERROR);
				}

				break;

			case PosFunction.WAITER :
				showWaiter();
				break;

			case PosFunction.PRINTLASTSHEET :
//				printLastSheet(inputpayment);
//				break;
			  //�����ش���һ����Ȩ�޿��ƹ���	
				try {
					if (checkPrivilege(PosFunction.PRINTLASTSHEET))
						printLastSheet(inputpayment);
					else
						state.setState(PosState.ERROR);
				} catch (UserCancelException e) {
					// do nothing.
				}

				break;

			case PosFunction.CASHIN :
//				state.setState(PosState.CASHIN);
//				break;
				//����Ǯ�����ܵ�Ȩ��
				try {
					if (checkPrivilege(PosFunction.CASHIN))
						state.setState(PosState.CASHIN);
					else
						state.setState(PosState.ERROR);
				} catch (UserCancelException e) {
					// do nothing.
				}

				break;

			case PosFunction.CASHOUT :
//				state.setState(PosState.CASHOUT);
//				break;
//				����Ǯ�����ܵ�Ȩ��
				try {
					if (checkPrivilege(PosFunction.CASHOUT))
						state.setState(PosState.CASHOUT);
					else
						state.setState(PosState.ERROR);
				} catch (UserCancelException e) {
					// do nothing.
				}

				break;
			case PosFunction.OPENCASHBOX :

				try {
					GetAuthor author = new GetAuthor();
					if (checkPrivilege(PosFunction.OPENCASHBOX, author)) {
						//�ڴ������µ��Ǵ�Ǯ������Ǯ��
						cashDrawer.open();
						cashier.resetPrivilege();
						context.setAuthorizerid("");
						authorizeCashier = null;

					} else {
						state.setState(PosState.ERROR);
					}

				} catch (UserCancelException e) {
					// do nothing.
				}

				break;

			case PosFunction.SHOWCASHBOX :
				try {
					GetAuthor author = new GetAuthor();
					if (checkPrivilege(PosFunction.SHOWCASHBOX, author)) {
						CashBoxInfo cashBoxInfo = new CashBoxInfo();
						CashBasket basket = core.getCashBasket();
						out.printFeed(out.getFeedLines());
						out.cutPaper();
						out.printFeed(3);
						//������
						out.displayHeader(context);
						out.print("�տ���");
						out.printWithoutSeperator("���Ա:" + author.getid());
						out.printWithoutSeperator(
							"����Ա:" + context.getCashierid());
						out.printWithoutSeperator(
							"��  ��:" + Formatter.getDate(new Date()));
						for (int i = 0; i < basket.size(); i++) {
							CashBox box = basket.get(i);
							out.display(box);
							cashBoxInfo.addCashBox(box);
						}
						out.print(Formatter.getTime(new Date()));
						out.printFeed(out.getFeedLines());
						out.cutPaper();
						//out.displayHeader(context);
						cashBoxInfo.show();
						cashier.resetPrivilege();
						context.setAuthorizerid("");
					} else {
						state.setState(PosState.ERROR);
					}

				} catch (UserCancelException e) {
					// do nothing.
				}

				break;

			case PosFunction.HKD :
				if (context.getCurrenCode().equals("HKD"))
					core.setCurrency("RMB");
				else
					core.setCurrency("HKD");
				out.display(core.getValue(), context.getExchange());
				break;

			case PosFunction.OFFLINE :
				if (context.isOnLine()) {
					try {
						if (checkPrivilege(PosFunction.OFFLINE)) {
							notice("�ɹ�ת��Ϊ�ѻ�������ʽ!");
							context.setOnLine(false);
							out.display(context);
							cashier.resetPrivilege();
							context.setAuthorizerid("");
						}
					} catch (UserCancelException e1) {
					}
				} else {
					out.prompt("���ڳ�����������");
					if (RealTime.getInstance().testOnLine()) {
						notice("�����ɹ�!");
						context.setOnLine(true);
						out.display(context);
					} else {
						in.clearKey();
						notice("����ʧ��!");
					}
				}
				break;
				
			case PosFunction.PRINTCONTROL:
				printcontrol();
				break;

			case PosFunction.PAYMENT :
			case PosFunction.CORRECT :
			case PosFunction.QUICKCORRECT :
			default :
				context.setWarning("��Ч����,�����������!");
				state.setState(PosState.ERROR);
		}
	}
	
	private void printcontrol(){
		
		POSPrinter p=POSPrinter.getInstance();
		p.setEnable(!p.isEnable());
		
		out.displayPrinterStatus();
		
	}

	private void maxcash() {
		try {
			out.displayState("����");
			out.prompt("���������������ֽ����");

			PosInput input = in.getInput();
			if (input.key() == PosFunction.CANCEL) {
				state.setState(PosState.MAXCASH);
			} else if (input.key() == PosFunction.HKD) {
				if (context.getCurrenCode().equals("HKD"))
					core.setCurrency("RMB");
				else {
					core.setCurrency("HKD");
					out.display(core.getValue(), context.getExchange());
				}
			} else if (input.key() == PosFunction.SHOWCASHBOX) {
				try {
					if (checkPrivilege(PosFunction.SHOWCASHBOX)) {
						CashBoxInfo cashBoxInfo = new CashBoxInfo();
						CashBasket basket = core.getCashBasket();
						out.printFeed(10);
						out.displayHeader(context);
						out.print("�տ���");
						out.printWithoutSeperator(
							"����Ա:" + context.getCashierid());
						out.printWithoutSeperator(
							"��  ��:" + Formatter.getDate(new Date()));
						for (int i = 0; i < basket.size(); i++) {
							CashBox box = basket.get(i);
							out.display(box);
							cashBoxInfo.addCashBox(box);
						}
						out.print(Formatter.getTime(new Date()));
						out.printFeed(out.getFeedLines());
						out.cutPaper();
						////out.displayHeader(context);
						cashBoxInfo.show();
						cashier.resetPrivilege();
						context.setAuthorizerid("");
					} else {
						state.setState(PosState.ERROR);
					}

				} catch (UserCancelException e) {
					// do nothing.
				}
			} else if (
				input.key() == PosFunction.PAYMENT
					&& ((PosInputPayment) input).getCents() > 0) {
				PosInputPayment p = (PosInputPayment) input;
				Payment pay_info = core.cashout(p);
				if (pay_info == null) {
					state.setState(PosState.MAXCASH);
				} else {
					String s;
					cashDrawer.open();
					s =
						(context.getCurrenCode() == "RMB")
							? ""
							: context.getCurrenCode();

					s += " " + new Value(-pay_info.getValue()).toString();
					notice("���Ǯ��ȡ�����½�" + s);
					out.displayCash("����", s);
					core.openSheet();
					out.displayHeader(context);
					out.displayWelcome();
					if (core.exceedCashMaxLimit()) {
						core.setWarning("�뽫�ֽ����");
						state.setState(PosState.MAXCASH);
					} else {
						state.setState(PosState.PRESALE);
					}
				}
			} else {
				context.setWarning("��Ч����,�����������!");
				state.setState(PosState.MAXCASH);
			}
		} catch (IOException e) {
			state.setState(PosState.ERROR);
		}
	}
	/**
	 * @deprecated
	 *
	 * */
	private void find() {
		Sale s;
		out.display(context);
		out.displayState("��ѯ");
		out.prompt("������Ҫ��ѯ��Ʒ���������룬�򰴹��ܼ���");

		PosInput input = in.getInput();
		switch (input.key()) {
			case PosFunction.CANCEL :
				// do nothing.
				break;

			case PosFunction.DISCOUNT :
				findpricesingleDisc();

				break;

			case PosFunction.DISCTOTAL :
				findpricetotalDisc();

				break;

			case PosFunction.DISCMONEY :
				findpricemoneyDisc();

				break;

			case PosFunction.GOODS :
				FindGoodsPrice((PosInputGoods) input);
				break;

			case PosFunction.HKD :
				if (context.getCurrenCode().equals("HKD"))
					core.setCurrency("RMB");
				else
					core.setCurrency("HKD");

				out.display(core.getValue(), context.getExchange());
				break;

			case PosFunction.TOTAL :
				showTotal();
				break;

			case PosFunction.EXIT :
				deletefindresult();
				break;
			default :
				;
		}
	}

	private void sale() {
		Sale s;
		context.drupnumber = 0;
		out.display(context);
		out.displayState("����");
		out.prompt("��������Ʒ���������룬�򰴹��ܼ���");

		PosInput input = in.getInput();
		switch (input.key()) {
			case PosFunction.CANCEL :
				// do nothing.
				break;

			case PosFunction.ALTPRICE :
			  //���ӱ�۵Ŀ���Ȩ��
//				try {
//					if (checkPrivilege(PosFunction.ALTPRICE))
						state.setState(PosState.ALTPRICE);
//					else
//						state.setState(PosState.ERROR);
//				} catch (UserCancelException e3) {
//					// do nothing
//				}
				break;
			case PosFunction.DISCOUNT :
				 //���ӵ����ۿ۵Ŀ���Ȩ��
//				try {
//					if (checkPrivilege(PosFunction.DISCOUNT))
						state.setState(PosState.DISCOUNT);
//					else
//						state.setState(PosState.ERROR);
//				} catch (UserCancelException e3) {
//					// do nothing
//				}
				break;
			case PosFunction.DISCTOTAL :
				 //�����ܶ��ۿ۵Ŀ���Ȩ��
//				try {
//					if (checkPrivilege(PosFunction.DISCTOTAL))
						state.setState(PosState.DISCTOTAL);
//					else
//						state.setState(PosState.ERROR);
//				} catch (UserCancelException e3) {
//					// do nothing
//				}
				break;
			case PosFunction.DISCMONEY :
				 //���ӽ���ۿ۵Ŀ���Ȩ��
//				try {
//					if (checkPrivilege(PosFunction.DISCMONEY))
						state.setState(PosState.DISCMONEY);
//					else
//						state.setState(PosState.ERROR);
//				} catch (UserCancelException e3) {
//					// do nothing
//				}
				break;
				
			case PosFunction.NumberDrug:	// ��ҩ����
				state.setState(PosState.DRUG);
				break;
				
			case PosFunction.Salestate:      
				IF_SALE();
			    break;
				
			case PosFunction.OPENCASHBOX :
				context.setWarning("�ǿյ�״̬�²��ܴ�Ǯ�䣬�����������!");
				state.setState(PosState.ERROR);
				break;
			case PosFunction.QUICKCORRECT :
				try {
				if (checkPrivilege(PosFunction.CORRECT)){
					s = core.quick_correct();
					if (s == null) {
						state.setState(PosState.ERROR);
					} else {
						sale_rec = s;
						state.setState(PosState.SALE);
						out.displayUnhold(s, core.getValue());
						out.displayDiscount(s, core.getValue());
						out.display(core.getValue(), context.getExchange());
					}
				} else {
	                context.setWarning("û�м�����Ȩ��,�밴�����������");
	                state.setState(PosState.ERROR);
					}
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (RealTimeException ex) {
					state.setState(PosState.NETWORKERROR);
				} catch (UserCancelException e) {
					// TODO �Զ����� catch ��
				}
				
				break;
			case PosFunction.CORRECT:

				try {
			// ���Ӹ�����Ȩ�޿���		
			  if (checkPrivilege(PosFunction.CORRECT)){
					//s = core.correct();
//					if (s == null) {
//						state.setState(PosState.ERROR);
//					} else {
//						sale_rec = s;
//						state.setState(PosState.SALE);
//						out.displayUnhold(s, core.getValue());
//						out.displayDiscount(s, core.getValue());
//						out.display(core.getValue(), context.getExchange());
						state.setState(PosState.CORRECT);
//					}
			  }else{
                context.setWarning("û�и�����Ȩ��,�밴�����������");
                state.setState(PosState.ERROR);
              }
				} 
//				catch (FileNotFoundException e1) {
//					e1.printStackTrace();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				} catch (RealTimeException ex) {
//					state.setState(PosState.NETWORKERROR);
//				}
				catch (UserCancelException e3) {
					// do nothing
				} 
				break;
			case PosFunction.WITHDRAW :
				try {
					if (checkPrivilege(PosFunction.WITHDRAW))
						state.setState(PosState.WITHDRAW);
					else
						state.setState(PosState.ERROR);
				} catch (UserCancelException e3) {
					// do nothing
				}

				break;

			case PosFunction.DELETE :
				if (core.getPosSheet().getPayLen() > 0) {
					context.setWarning("֧������������ȡ��,�����������!");
					state.setState(PosState.ERROR);
				} else if (confirm(PosFunction.toString(input.key()) + "?"))
					try {
						if (checkPrivilege(input.key())) {
							// ��������
							context.drupnumber = 0;
							deleteSheet();
							cashier.resetPrivilege();
							context.setAuthorizerid("");
						}
					} catch (UserCancelException e4) {
						// do nothing
					}

				break;

			case PosFunction.GOODS :
				sellGoods((PosInputGoods) input);
				break;
				
			case PosFunction.HKD :
				if (context.getCurrenCode().equals("HKD"))
					core.setCurrency("RMB");
				else
					core.setCurrency("HKD");

				out.display(core.getValue(), context.getExchange());
				break;

			case PosFunction.LOCK :
				state.setState(PosState.LOCK);
				//UnLock unLock = new UnLock();
				//unLock.show();
				break;

			case PosFunction.HOLD :
				System.out.println("�ҵ� ...");
				hold();
				break;

				/*case PosFunction.TOTAL :
					showTotal();
					break;
				*/
			case PosFunction.PAYMENT :
				sale_rec = null;
				PosInputPayment p = (PosInputPayment) input;
				inputpayment = p; //��֧����Ϣ���ݸ�ȫ�ֱ��������ش�СƱʹ��

				Payment pay_info;
                Payment cardPay=null;
				try {
//                  if(core.getPosSheet().getMemberCard()!=null){
//                    PosInputPayment cp=new PosInputPayment(0,'h',core.getPosSheet().getMemberCard().getCardNo());
//                    cardPay=core.pay(cp);
//                  }
				    pay_info = core.pay(p);

					if (pay_info == null) {
						state.setState(PosState.ERROR);
					} else {
						out.display(core.getValue(), context.getExchange());

						out.display(pay_info, core.getValue());
                        //------------
                        if(core.getPosSheet().getShopCard()!=null)
                         out.display(core.getPosSheet().getShopCard());
                         //  out.
						//��ӡ֧����Ϣ��֧�����ͺͽ��
//						out.display(core.getPosSheet().getMemberCard()); //��ӡ��Ա����Ϣ�����ź����
						if (core.getValue().getValueToPay() <= 0) {
							state.setState(PosState.CLOSESHEET);
							// zhouzhou add
			                  if(core.getPosSheet().getMemberCard()!=null){
			                    PosInputPayment cp=new PosInputPayment(0,'h',core.getPosSheet().getMemberCard().getCardNo());
			                    cardPay=core.pay(cp);
			                  }
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch(RealTimeException e){
                    state.setState(PosState.NETWORKERROR);
                }

				break;

			case PosFunction.OILTEST :
			case PosFunction.SAMPLE :
			case PosFunction.FLEE :
				if (core.getValue().getValueTotal()
					> core.getValue4Indicator()) {
					context.setWarning("�������ֻ��������Ʒ,�����������!");
					state.setState(PosState.ERROR);
				} else if (confirm(PosFunction.toString(input.key()) + "?")) {
					try {
						if (checkPrivilege(input.key())) {
							Payment pay = closeSheetWithoutMoney(input.key());
							if (pay != null) {
								out.display(
									core.getValue(),
									context.getExchange());
								out.display(pay, core.getValue());
								//out.displayTrail(core.getValue());
								out.displayTrail(new SheetValue());
								out.clear();

								try {
									core.openSheet();
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								state.setState(PosState.PRESALE);

								out.printFeed(out.getFeedLines());
								out.cutPaper();
								out.displayHeader(context);
								out.displayWelcome();
							}
							cashier.resetPrivilege();
							context.setAuthorizerid("");
						} else {
							context.setWarning("Ȩ����֤ʧ�ܣ��������Ա������Ȩ��,�����������!");
							state.setState(PosState.ERROR);
						}
					} catch (UserCancelException e5) {
						// do nothing
					}
				}

				break;
            case PosFunction.WAITER :
				showWaiter();
				break;

			case PosFunction.EXIT :
				context.setWarning("���Ƚ������˳�,�����������!");
				state.setState(PosState.ERROR);
				break;
				
			case PosFunction.PRINTCONTROL:
				printcontrol();
				break;

			default :
				;
		}
	}

	void sellGoods(PosInputGoods pig) {
		try {
			// ��Ʒ����С����
			if(pig.getCode().length()<6){
				StringBuffer buf=new StringBuffer();
				
				for(int i=6;i>pig.getCode().length();i--){
					buf.append("0");	
				}
				buf.append(pig.getCode());
				pig.setCode(buf.toString());
			}
			// end
			PosInputGoods input_goods = pig;
		    /*TODO ҩƷ�޹�*/
		    PosPriceData codenew = new PosPriceData();
		    codenew.setSaleCode(pig.getCode());
			Goods goods = core.findGoods(codenew);
			Volume init_volume = pig.getVolume();
			int disccount = 0;
			int baseprice = 0;
			PosConfig config = PosConfig.getInstance();

			/* add by lichao 09/07/2004*/
			if (goods != null
				&& goods.getType() == 8
				&& goods.getDeptid() != null) {
				String BASE_PRICE =
					(String) PosConfig.getInstance().getString("PRICE_FLAG");
				if (BASE_PRICE.equals("ON")) {
					DispPrice dispPrice = new DispPrice(pos.posOutStream);
					dispPrice.show();
					MainUI oldMainUI = out.getMainUI();
					out.setMainUI(dispPrice);
					dispPrice.show();
					baseprice = in.getbaseprice();
					System.out.println("basepriceΪ:" + baseprice);
					goods.setPrice(baseprice);
					out.setMainUI(oldMainUI);
					dispPrice.dispose();
					out.clearInputLine();
					if (!dispPrice.getconfirm()) {
						return;
					}
				}
			}

			Sale s;
			if (input_goods != null)
				try {
					s =
						core.sell(
							input_goods,
							baseprice,
							pos.core.getPosSheet().getMemberCard());
					if (s == null) {
						state.setState(PosState.ERROR);
					} else {
						sale_rec = s;
						out.display(s, core.getValue());
						out.display(core.getValue(), context.getExchange());
						/*add by lichao 08/25/2004*/
						int discDelta =
							pos.core.getPosSheet().getValue().getDiscDelta();
 
						if (discDelta != 0) {
							try {
								out.displayprom(s, core.getValue());
								int value = (int) discDelta;
								if (s.getDiscType() == Discount.COMPLEX) {
									String name = s.getFavorName();
									core.sell(Sale.AUTODISC, name, value);
								} else {
									String name =
										new Discount(s.getDiscType())
											.getTypeName();
									core.sell(Sale.AUTODISC, name, value);
								}

								core.dump();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						/* end */
						state.setState(PosState.SALE);
					}

				} catch (RealTimeException ex) {
					state.setState(PosState.NETWORKERROR);
				}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RealTimeException ex) {
			state.setState(PosState.NETWORKERROR);
		}
	}

	void FindGoodsPrice(PosInputGoods pig) {
		try {
			PosInputGoods input_goods = pig;
		    /*TODO ҩƷ�޹�*/
		    PosPriceData codenew = new PosPriceData();
		    codenew.setSaleCode(pig.getCode());
			Goods goods = core.findGoods(codenew);
			Volume init_volume = pig.getVolume();
			int disccount = 0;

			PosConfig config = PosConfig.getInstance();


			Sale s;
			if (input_goods != null)
				try {
					s = core.findprice(input_goods);
					if (s == null) {
						state.setState(PosState.ERROR);
					} else {
						sale_rec = s;
						out.display(s, core.getValue());
						out.display(core.getValue(), context.getExchange());
						state.setState(PosState.FIND);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		} catch (RealTimeException ex) {
			state.setState(PosState.NETWORKERROR);
		}

	}

	/**
	 * ��Ȩ����ɵ�����¶Ե�ǰ��Ʒ���б�۴������Դﵽ����Ʒ�ۿ۵Ĺ���
     *
	 */
	private void altPrice() {
		// get last sale record;
		// get Fact price;
		// set Fact Price for last goods;
		// compute sheet value;
		// display sheet value;
		if (sale_rec != null
			&& sale_rec.getDiscValue() == 0
			&& (sale_rec.getType() == Sale.SALE
				|| sale_rec.getType() == Sale.WITHDRAW)) {

			try {
				if (checkPrivilege(PosFunction.ALTPRICE)) {
					double price_org =
						((double) sale_rec.getGoods().getPrice() / 100.0);
					double price_new;
					out.displayState("�ļ۸�");
					out.prompt("��������Ʒ�ۼ�");
					price_new = in.getDouble(6, 2);

					double deltaValue = 0;
					double alterPrice = price_org;

					if (authorizeCashier != null) {
						deltaValue =
							(price_org
								* authorizeCashier.getMax_Disc()
								/ 100.0);
						alterPrice =
							price_org
								* (100 - authorizeCashier.getMax_Disc())
								/ 100.0;
					}

					if ((price_org - price_new) > deltaValue) {

						context.setWarning(
							"��۳�����Χ,��ͱ�۵�"
								+ (new Value((int) Math.rint(alterPrice * 100)))
									.toString()
								+ "�������������!");

						state.setState(PosState.ERROR);
						cashier.resetPrivilege();
						context.setAuthorizerid("");
						authorizeCashier = null;

						return;
					}
					sale_rec.setFactPrice(
						Discount.ALTPRICE,
						(int) Math.rint(100 * price_new));
					core.updateValue();
					out.displayDiscount(sale_rec, core.getValue());
					out.display(core.getValue(), context.getExchange());
					cashier.resetPrivilege();
					context.setAuthorizerid("");
					authorizeCashier = null;
					/* add by lichao 8/24/2004*/
					try {
						//SheetValue v = core.getValue();

						int value = (int) sale_rec.getDiscValue();
						String name = "���";
						core.sell(Sale.AlTPRICE, name, value);
						core.dump();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					/*end*/
					state.setState(PosState.SALE);
				} else {
					state.setState(PosState.ERROR);
					cashier.resetPrivilege();
					context.setAuthorizerid("");
					authorizeCashier = null;
				}
			} catch (UserCancelException e) {
				state.setState(PosState.SALE);
				cashier.resetPrivilege();
				context.setAuthorizerid("");
				authorizeCashier = null;
			}

		} else {
			context.setWarning("��Ч����,�����������!");
			state.setState(PosState.ERROR);
		}

	}
	/**	The following codes were updated by huangxuean in July,2004
	 * this method is used by singleDisc() totalDisc() and moneyDisc()
	 *  it can make certain whether there are Disc in this salelst that calls it
	 * @param salelst
	 * @return boolean ,false means has not been discounted
	 * @deprecated
	 */
	private boolean isDisc(SaleList salelst) {
		boolean disc = false;
		Sale sale = null;
		for (int i = 0; i < salelst.size(); i++) {
			sale = salelst.get(i);
			if (sale.getDiscType() == Discount.SINGLE
				|| sale.getDiscType() == Discount.TOTAL
				|| sale.getDiscType() == Discount.MONEY)
				return false;
			else
				disc = true;
		}
		return disc;
	}



	private void findpricesingleDisc() {
		salelst = core.getPosSheet().getSalelst();
		int nums = salelst.size();
		if (sale_rec != null && (sale_rec.getType() == Sale.SALE)) {
			if (sale_rec.getDiscValue() == 0) {
				try {
					if (checkPrivilege(PosFunction.DISCOUNT)) {
						double disc;
						out.displayState("�����ۿ�");
						out.prompt("�������ۿ��ʣ���:�������,����80; ����������,����75");
						disc = in.getDouble(6, 2);
						if (new Double(disc).intValue() == 0) {
							context.setWarning("�ۿ۲���Ϊ��,�����������!");
							state.setState(PosState.ERROR);
							return;
						}
						int idisc = new Double(disc).intValue();
						DiscRate discrate =
							new DiscRate(Discount.SINGLE, 100 - idisc);
						sale_rec.setDiscount(discrate);
						core.updateValue();
						out.displayDiscount(sale_rec, core.getValue());
						out.display(core.getValue(), context.getExchange());
						core.getPosSheet().updateValue();
						cashier.resetPrivilege();
						context.setAuthorizerid("");
						state.setState(PosState.FIND);
					} else {
						state.setState(PosState.ERROR);
					}
				} catch (UserCancelException e) {
					state.setState(PosState.FIND);
					cashier.resetPrivilege();
					context.setAuthorizerid("");
				}
			} else {
				context.setWarning("��ǰ��Ʒ�Ѿ��ۿ�,���ܽ��е����ۿ�,�����������!");
				state.setState(PosState.ERROR);
			}

		} else {
			context.setWarning("��Ч����,�����������!");
			state.setState(PosState.ERROR);
		}

	}

	/**
	 * ��Ȩ����ɵ�����¶Ե�ǰû���ۿ۵���Ʒ���е����ۿ۴���
	 *
	 **/
	private void singleDisc() {
		salelst = core.getPosSheet().getSalelst();
		int nums = salelst.size();
		if (sale_rec != null && (sale_rec.getType() == Sale.SALE)) {

			if (sale_rec.getDiscValue() == 0) {
				try {
					if (checkPrivilege(PosFunction.DISCOUNT)) {
						double disc;
						out.displayState("�����ۿ�");
						out.prompt("�������ۿ��ʣ���:�������,����80; ����������,����75");
						disc = in.getDouble(6, 2);
						int idisc = new Double(disc).intValue();
						if (idisc > 100) {
							context.setWarning("�����ۿ۲��ܴ���100%,�����������!");
							state.setState(PosState.ERROR);
							cashier.resetPrivilege();
							context.setAuthorizerid("");
							authorizeCashier = null;
							return;
						}

						if (authorizeCashier != null
							&& (100 - idisc) > authorizeCashier.getMax_Disc()) {
							context.setWarning(
								"���ۿ۳���������ۿۣ�����ۿ�"
									+ (100 - authorizeCashier.getMax_Disc())
									+ "%,�����������!");
							state.setState(PosState.ERROR);
							cashier.resetPrivilege();
							context.setAuthorizerid("");
							authorizeCashier = null;
							return;
						}

						DiscRate discrate =
							new DiscRate(Discount.SINGLE, 100 - idisc);
						sale_rec.setDiscount(discrate);
						core.updateValue();
						out.displayDiscount(sale_rec, core.getValue());
						out.display(core.getValue(), context.getExchange());
						cashier.resetPrivilege();
						context.setAuthorizerid("");
						authorizeCashier = null;
						/* add by lichao 8/24/2004*/
						try {
							//SheetValue v = core.getValue();

							int value = (int) sale_rec.getDiscValue();
							String name = "�����ۿ�";
							core.sell(Sale.AlTPRICE, name, value);
							core.dump();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						/*end*/
						state.setState(PosState.SALE);
					} else {
						state.setState(PosState.ERROR);
					}
				} catch (UserCancelException e) {
					state.setState(PosState.SALE);
					cashier.resetPrivilege();
					context.setAuthorizerid("");
					authorizeCashier = null;
				}
			} else {
				context.setWarning("��ǰ��Ʒ�Ѿ��ۿ�,���ܽ��е����ۿ�,�����������!");
				state.setState(PosState.ERROR);
			}

		} else {
			context.setWarning("��Ч����,�����������!");
			state.setState(PosState.ERROR);
		}

	}
	/**	The following codes were updated by huangxuean in July,2004
	 * this method is used by totalDisc() specially
	 *  it can make certain whether there are Disc in this salelst that calls it
	 * @param salelst
	 * @return boolean��true ��ʾ��ǰ���Ѿ������ۿۣ������ٴβ����κ��ۿ�ҵ�񣬷�֮�����ԡ�
	 */
	private boolean isDisc4Total(SaleList salelst) {
		boolean disc = false;
		Sale sale = null;
		for (int i = 0; i < salelst.size(); i++) {
			sale = salelst.get(i);
			if (sale.getDiscValue() == 0) {
				disc =  false;
			} else
				return true;
		}
		return disc;
	}
	
	
	/**	The following codes were updated by huangxuean in July,2004
	 * this method is used by totalDisc() specially
	 *  it can make certain whether there are Disc in this salelst that calls it
	 * @param salelst
	 * @return boolean��true ��ʾ��ǰ���Ѿ������ۿۣ������ٴβ����κ��ۿ�ҵ�񣬷�֮�����ԡ�
	 * �����ܶ��ۿ�ҵ����֤����zhouzhou add 20070205
	 */
	private boolean isDisc4Total_s(SaleList salelst) {
		boolean disc = false;
		Sale sale = null;
		for (int i = 0; i < salelst.size(); i++) {
			sale = salelst.get(i);
			if (sale.getDiscValue() == 0) {
				return  false;
			} else
				disc =  true;
		}
		return disc;
	}

	/**
	* ��Ȩ����ɵ�����¶Ե�ǰ��û���ۿ۹�����Ʒ�����ܶ��ۿ۴���
	* �����ǰ����Ʒ�����й��ۿۣ���ǰ����Ʒ�����ٴβ����ܶ��ۿ�ҵ��
	* ���򣬶Ե�ǰ��������û���ۿ۵���Ʒ�����ܶ��ۿ�
	**/
	private void totalDisc() {
		salelst = core.getPosSheet().getSalelst();
		int totalValue = salelst.getTotalValue();
		Vector vnums = salelst.getNumsWithoutDisc();

		if (salelst != null) {

			try {
				if (checkPrivilege(PosFunction.DISCTOTAL)) {
					double disc;
					Sale sale = null;
					if (isDisc4Total_s(salelst)) {
						context.setWarning("��ǰ����Ʒ�Ѿ��ۿ�,���ܽ����ܶ��ۿ�,�����������!");
						state.setState(PosState.ERROR);
						return;
					} else {
						out.displayState("�ܶ��ۿ�");
						out.prompt("�������ۿ��ʣ���:�������,����80; ����������,����75");
						disc = in.getDouble(6, 2);

						if (disc > 100) {
							context.setWarning("�ܶ��ۿ۲��ܳ���100%,�����������!");
							state.setState(PosState.ERROR);
							cashier.resetPrivilege();
							context.setAuthorizerid("");
							authorizeCashier = null;
							return;
						}

						if (authorizeCashier != null
							&& (100 - disc) > authorizeCashier.getMax_Disc()) {
							context.setWarning(
								"�ܶ��ۿ۳�������ۿۣ�����ۿ۵�"
									+ (100 - authorizeCashier.getMax_Disc())
									+ "%,�����������!");
							state.setState(PosState.ERROR);
							cashier.resetPrivilege();
							context.setAuthorizerid("");
							authorizeCashier = null;
							return;
						}

						for (int i = 0; i < vnums.size(); i++) {
							int num = ((Integer) vnums.elementAt(i)).intValue();
							DiscRate discrate =
								new DiscRate(Discount.TOTAL, 100 - (int) disc);
							sale = salelst.get(num);
							if (!(sale.getType() == Sale.TOTAL
								|| sale.getType() == Sale.AlTPRICE
								|| sale.getType() == Sale.SINGLEDISC
								|| sale.getType() == Sale.TOTALDISC
								|| sale.getType() == Sale.MONEYDISC
								|| sale.getType() == Sale.AUTODISC)) {
								if (sale.getDiscValue()==0){// zhouzhou add �ܶ��ۿ۱���û���ۿ۽��
									sale.setDiscount(discrate);
								}
							}
						}
					}
					core.updateValue();
					out.displayDiscount(sale, core.getValue());
					out.display(core.getValue(), context.getExchange());
					cashier.resetPrivilege();
					context.setAuthorizerid("");
					authorizeCashier = null;
					/* add by lichao 8/24/2004*/
					try {
						//SheetValue v = core.getValue();

						int value = (int) sale.getDiscValue();
						String name = "�ܶ��ۿ�";
						core.sell(Sale.AlTPRICE, name, value);
						core.dump();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					/*end*/
					state.setState(PosState.SALE);
				} else {
					state.setState(PosState.ERROR);
				}
			} catch (UserCancelException e) {
				state.setState(PosState.SALE);
				cashier.resetPrivilege();
				context.setAuthorizerid("");
				authorizeCashier = null;
			}
		} else {
			context.setWarning("��Ч����,�����������!");
			state.setState(PosState.ERROR);
		}
	}

	private void findpricetotalDisc() {
		salelst = core.getPosSheet().getSalelst();
		int totalValue = salelst.getTotalValue();
		Vector vnums = salelst.getNumsWithoutDisc();

		if (salelst != null
			&& sale_rec != null
			&& (sale_rec.getType() == Sale.SALE)) {

			try {
				if (checkPrivilege(PosFunction.DISCTOTAL)) {
					double disc;
					Sale sale = null;
					if (isDisc4Total(salelst)) {
						context.setWarning("��ǰ����Ʒ�Ѿ��ۿ�,���ܽ����ܶ��ۿ�,�����������!");
						state.setState(PosState.ERROR);
						return;
					} else {
						out.displayState("�ܶ��ۿ�");
						out.prompt("�������ۿ��ʣ���:�������,����80; ����������,����75");
						disc = in.getDouble(6, 2);
						if (new Double(disc).intValue() == 0) {
							context.setWarning("�ۿ۲���Ϊ��,�����������!");
							state.setState(PosState.ERROR);
							return;
						}
						for (int i = 0; i < vnums.size(); i++) {
							int num = ((Integer) vnums.elementAt(i)).intValue();
							DiscRate discrate =
								new DiscRate(Discount.TOTAL, 100 - (int) disc);
							sale = salelst.get(num);
							sale.setDiscount(discrate);
						}
					}
					core.updateValue();
					out.displayDiscount(sale, core.getValue());
					out.display(core.getValue(), context.getExchange());
					cashier.resetPrivilege();
					context.setAuthorizerid("");
					state.setState(PosState.FIND);
				} else {
					state.setState(PosState.ERROR);
				}
			} catch (UserCancelException e) {
				//state.setState(PosState.FIND);
				state.setState(PosState.SALE);
				cashier.resetPrivilege();
				context.setAuthorizerid("");
			}

		} else {
			context.setWarning("��Ч����,�����������!");
			state.setState(PosState.ERROR);
		}
	}

    /**
     *
     * @param totalValue
     * @param discValue
     * @param vnums
     * @return
     * @deprecated
     */
	private int afterDisc(int totalValue, int discValue, Vector vnums) {
		int itemdisc = 0;
		if ((totalValue - discValue) % vnums.size() == 0) {
			itemdisc = (totalValue - discValue) / vnums.size();
			return itemdisc;
		} else {
			for (int i = 0; i < vnums.size() - 1; i++) {
				itemdisc = Math.round((totalValue - discValue) / vnums.size());
				itemdisc += (totalValue - discValue) % vnums.size();
				return itemdisc;
			}
			return itemdisc;
		}
		//return itemdisc;
	}

	/**
		* ��Ȩ����ɵ�����¶Ե�ǰ��û���ۿ۹�����Ʒ���н���ۿ۴���
		* �����ǰ����Ʒ�����й��ۿۣ���ǰ����Ʒ�����ٴβ������ۿ�ҵ��
		* ���򣬶Ե�ǰ��������û���ۿ۵���Ʒ���н���ۿ�
		* @param
		* @return void
		**/
	private void moneyDisc() {
		salelst = core.getPosSheet().getSalelst();
		int unPaid = core.getPosSheet().getValue().getValueToPay();
		int total = salelst.getTotalValue();
		int totalValue = salelst.getValueWithoutDisc();
		Vector vnums = salelst.getNumsWithoutDisc();
		int numAll = salelst.size();
		if (salelst != null
			&& sale_rec != null
			&& (sale_rec.getType() == Sale.SALE)) {

			try {
				if (checkPrivilege(PosFunction.DISCMONEY)) {
					double disc;
					Sale sale = null;
					//					if(vnums.size()>0){
					if (isDisc4Total(salelst)) {
						context.setWarning("��ǰ����Ʒ�Ѿ��ۿ�,���ܽ��н���ۿ�,�����������!");
						state.setState(PosState.ERROR);
						cashier.resetPrivilege();
						context.setAuthorizerid("");
						authorizeCashier = null;
						return;
					} else {
						int temp = 0;
						out.displayState("����ۿ�");
						out.prompt("�������ۺ���");
						disc = in.getDouble(6, 2);

						if (Math.rint(disc * 100) > unPaid) {
							context.setWarning("�ۿۺ���ܴ���Ӧ�տ�,�����������!");
							state.setState(PosState.ERROR);

							cashier.resetPrivilege();
							context.setAuthorizerid("");
							authorizeCashier = null;

							return;
						}

						//û�д��۵���Ʒ����Ҫ���۵Ľ��
						double totalDisc = 0.0;
						totalDisc = total - disc * 100.0;
						//����Ա����ܴ�Ľ��
						double discAvailable = 0.0;
						if (authorizeCashier != null) {
							discAvailable =
								totalValue
									* authorizeCashier.getMax_Disc()
									/ 100.0;
						}

						if (totalDisc > discAvailable) {
							context.setWarning(
								"�ۿ۽�������ۿ۽��,��ͱ��"
									+ (new Value((int) Math
										.rint((total - discAvailable))))
										.toString()
									+ ",�����������!");
							state.setState(PosState.ERROR);

							cashier.resetPrivilege();
							context.setAuthorizerid("");
							authorizeCashier = null;

							return;
						}

						// int tempValue=0;
						for (int i = 0; i < vnums.size() - 1; i++) {
							int num = ((Integer) vnums.elementAt(i)).intValue();
							sale = salelst.get(num);
							long orgvalue = sale.getStdValue();
							//int itemvalue = orgvalue - new Double((orgvalue*1.0/totalValue)*(total-disc*100)).intValue();
							long itemvalue =
								orgvalue
									- (int) Math.rint(
										(orgvalue * 1.0 / totalValue)
											* (total - disc * 100));
							DiscPrice discprice =
								new DiscPrice(Discount.MONEY, itemvalue);
							sale.setDiscValue(discprice);
							//temp += new Double((orgvalue*1.0/totalValue)*(total-disc*100)).intValue();
							temp
								+= (int) Math.rint(
									(orgvalue * 1.0 / totalValue)
										* (total - disc * 100));

						}

						if (vnums.size() > 0) {
							int num =
								((Integer) vnums.elementAt(vnums.size() - 1))
									.intValue();
							sale = salelst.get(num);
							long orgvalue = sale.getStdValue();
							//int itemvalue = orgvalue -(totalValue-(int)(disc*100)-temp);
							long itemvalue =
								orgvalue
									- (total
										- (int) Math.rint((disc * 100))
										- temp);
							//int itemvalue = orgvalue -(total-new Double((disc*100)-temp).intValue());

							DiscPrice discprice =
								new DiscPrice(Discount.MONEY, itemvalue);
							sale.setDiscValue(discprice);
						}
						core.updateValue();
						out.displayDiscount(sale, core.getValue());
						out.display(core.getValue(), context.getExchange());
						core.getPosSheet().updateValue();
						cashier.resetPrivilege();
						context.setAuthorizerid("");
						authorizeCashier = null;
						/* add by lichao 8/24/2004*/
						try {
							//SheetValue v = core.getValue();

							int value = (int) sale.getDiscValue();
							String name = "����ۿ�";
							core.sell(Sale.AlTPRICE, name, value);
							core.dump();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						/*end*/
						state.setState(PosState.SALE);
					}
				} else {
					state.setState(PosState.ERROR);
					cashier.resetPrivilege();
					context.setAuthorizerid("");
					authorizeCashier = null;
				}
			} catch (UserCancelException e) {
				state.setState(PosState.SALE);
				cashier.resetPrivilege();
				context.setAuthorizerid("");
				authorizeCashier = null;
			}

		} else {
			context.setWarning("��Ч����,�����������!");
			state.setState(PosState.ERROR);
		}

	}

	/**
	 * @param
	 * @return
	 * @deprecated
	 * */
	private void findpricemoneyDisc() {
		salelst = core.getPosSheet().getSalelst();
		int total = salelst.getTotalValue();
		int totalValue = salelst.getValueWithoutDisc();
		Vector vnums = salelst.getNumsWithoutDisc();
		int numAll = salelst.size();
		if (salelst != null
			&& sale_rec != null
			&& (sale_rec.getType() == Sale.SALE)) {
			try {
				if (checkPrivilege(PosFunction.DISCMONEY)) {
					double disc;
					Sale sale = null;
					//					if(vnums.size()>0){
					if (isDisc4Total(salelst)) {
						context.setWarning("��ǰ����Ʒ�Ѿ��ۿ�,���ܽ��н���ۿ�,�����������!");
						state.setState(PosState.ERROR);
						return;
					} else {
						int temp = 0;
						out.displayState("����ۿ�");
						out.prompt("�������ۺ���");
						disc = in.getDouble(6, 2);
						if (new Double(disc).intValue() == 0) {
							context.setWarning("�ۿ۲���Ϊ��,�����������!");
							state.setState(PosState.ERROR);
							return;
						}
						for (int i = 0; i < vnums.size() - 1; i++) {
							int num = ((Integer) vnums.elementAt(i)).intValue();
							sale = salelst.get(num);
							long orgvalue = sale.getStdValue();
							//int itemvalue = orgvalue - new Double((orgvalue*1.0/totalValue)*(total-disc*100)).intValue();
							long itemvalue =
								orgvalue
									- (int) Math.rint(
										(orgvalue * 1.0 / totalValue)
											* (total - disc * 100));
							DiscPrice discprice =
								new DiscPrice(Discount.MONEY, itemvalue);
							sale.setDiscValue(discprice);
							temp
								+= new Double(
									(orgvalue * 1.0 / totalValue)
										* (total - disc * 100))
									.intValue();
						}
						if (vnums.size() > 0) {
							int num =
								((Integer) vnums.elementAt(vnums.size() - 1))
									.intValue();
							sale = salelst.get(num);
							long orgvalue = sale.getStdValue();
							//int itemvalue = orgvalue -(totalValue-(int)(disc*100)-temp);
							long itemvalue =
								orgvalue
									- (total
										- (int) Math.rint((disc * 100))
										- temp);
							//int itemvalue = orgvalue -(total-new Double((disc*100)-temp).intValue());
							DiscPrice discprice =
								new DiscPrice(Discount.MONEY, itemvalue);
							sale.setDiscValue(discprice);
						}
						core.updateValue();
						out.displayDiscount(sale, core.getValue());
						out.display(core.getValue(), context.getExchange());
						cashier.resetPrivilege();
						context.setAuthorizerid("");
						state.setState(PosState.FIND);
					}
				} else {
					state.setState(PosState.ERROR);
				}
			} catch (UserCancelException e) {
				state.setState(PosState.FIND);
				cashier.resetPrivilege();
				context.setAuthorizerid("");
			}
			//		}
		} else {
			context.setWarning("��Ч����,�����������!");
			state.setState(PosState.ERROR);
		}

	}

	/**
	 * @param
	 * @return
	 * @deprecated
	 * */

	private void correct() {
		out.displayState("����");
		out.prompt("��������Ʒ���������룬�������.");
		PosInput input = in.getInput();
		switch (input.key()) {
			case PosFunction.CANCEL :
				core.writelog("����", "1", 0);
				state.setState(PosState.SALE);
				break;

			case PosFunction.CORRECT :
				core.writelog("����", "1", 0);
				state.setState(PosState.CORRECT);
				break;

			case PosFunction.WITHDRAW :
				core.writelog("����", "1", 0);
				state.setState(PosState.WITHDRAW);
				break;

			case PosFunction.GOODS :
				try {
					PosInputGoods g = (PosInputGoods) input;
					// ������λ���룬������λ
					if(g.getCode().length()<6){
						StringBuffer buf=new StringBuffer();
						
						for(int i=6;i>g.getCode().length();i--){
							buf.append("0");	
						}
						
						buf.append(g.getCode());
						g.setCode(buf.toString());
						
					}
					PosInputGoods input_goods = g;
					int disccount = 0;
				    /*TODO ҩƷ�޹�*/
				    PosPriceData codenew = new PosPriceData();
				    codenew.setSaleCode(g.getCode());
					Goods goods = core.findGoods(codenew);
					int init_qty = g.getQty();
					Volume init_volume = g.getVolume();
					PosConfig config = PosConfig.getInstance();

					Sale s;
					if (input_goods != null)
						try {
							s = core.correct(input_goods);
							if (s == null) {
								core.writelog("����", "1", 0);
								System.out.println("�����������ɹ�.");
								state.setState(PosState.ERROR);
							} else {
								core.writelog("����", "0", 0);
								sale_rec = s;
								out.display(s, core.getValue());
								out.display(
									core.getValue(),
									context.getExchange());
									
								/*add by lichao 08/25/2004*/
								int discDelta =
									pos.core.getPosSheet().getValue().getDiscDelta();

								if (discDelta != 0) {
									try {
										out.displayprom(s, core.getValue());
										int value = (int) discDelta;
										if (s.getDiscType() == Discount.COMPLEX) {
											String name = s.getFavorName();
											core.sell(Sale.AUTODISC, name, value);
										} else {
											String name =
												new Discount(s.getDiscType())
													.getTypeName();
											core.sell(Sale.AUTODISC, name, value);
										}

										core.dump();
									} catch (FileNotFoundException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								/* end */

								state.setState(PosState.SALE);
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
				} catch (RealTimeException ex) {
					state.setState(PosState.NETWORKERROR);
				}

				break;

			default :
				core.writelog("����", "1", 0);
				out.prompt("��Ч����,�����������!");
				in.waitCancel();
				state.setState(PosState.SALE);
		}
		cashier.resetPrivilege();
		context.setAuthorizerid("");
	}

	private void withdraw() {
		out.displayState("�˻�");
		out.prompt("��������Ʒ���������룬�򰴷�����.");
		PosInput input = in.getInput();
		switch (input.key()) {

			case PosFunction.EXIT :
			case PosFunction.CANCEL :
				core.writelog("�˻�", "1", 0);
				state.setState(state.getOldState());
				break;

				//			case PosFunction.CORRECT :
				//				try {
				//					if (checkPrivilege(PosFunction.CORRECT)) {
				//						core.writelog("�˻�", "1", 0);
				//						state.setState(PosState.CORRECT);
				//					} else {
				//						core.writelog("�˻�", "1", 0);
				//						state.setState(PosState.ERROR);
				//					}
				//				} catch (UserCancelException e2) {
				//					// do nothing
				//				}
				//
				//				break;

			case PosFunction.WITHDRAW :
				core.writelog("�˻�", "1", 0);
				state.setState(PosState.WITHDRAW);
				break;

			case PosFunction.GOODS :
				int baseprice = 0;
				try {
					PosInputGoods g = (PosInputGoods) input;
					if(g.getCode().length()<6){
						StringBuffer buf=new StringBuffer();
						
						for(int i=6;i>g.getCode().length();i--){
							buf.append("0");	
						}
						
						buf.append(g.getCode());
						g.setCode(buf.toString());
						
					}
				    /*TODO ҩƷ�޹�*/
				    PosPriceData codenew = new PosPriceData();
				    codenew.setSaleCode(g.getCode());
					Goods goods = core.findGoods(codenew);
					if (goods != null
						&& goods.getType() == 8
						&& goods.getDeptid() != null
						&& !goods.getDeptid().equals(Goods.LOADOMETER)) {
						String BASE_PRICE =
							(String) PosConfig.getInstance().getString(
								"PRICE_FLAG");
						if (BASE_PRICE.equals("ON")) {

							DispPrice dispPrice =
								new DispPrice(pos.posOutStream);
							dispPrice.show();
							MainUI oldMainUI = out.getMainUI();
							out.setMainUI(dispPrice);
							dispPrice.show();
							baseprice = in.getbaseprice();
							goods.setPrice(baseprice);
							out.setMainUI(oldMainUI);
							dispPrice.dispose();
							out.clearInputLine();
							if (!dispPrice.getconfirm()) {
								return;
							}
						}
					}
					Sale s;
					if (g != null) {
						try {
							sale_rec = s = core.withdraw(g, baseprice);
							if (s == null) {
								core.writelog("�˻�", "1", 0);
								state.setState(PosState.ERROR);
							} else {
								core.writelog("�˻�", "0", 0);
								out.display(s, core.getValue());
								out.display(
									core.getValue(),
									context.getExchange());
								state.setState(PosState.SALE);
							}
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				} catch (RealTimeException ex) {
					state.setState(PosState.NETWORKERROR);
				}
				break;
			case PosFunction.PAYMENT :
				try {
					PosInputPayment p = (PosInputPayment) input;
					Payment pay_info = core.pay(p);
					inputpayment = p;
					if (pay_info == null) {
						state.setState(PosState.ERROR);
					} else {
						out.display(core.getValue(), context.getExchange());
						out.display(pay_info, core.getValue());
						out.display(core.getPosSheet().getMemberCard());
						if (core.getValue().getValueToPay() <= 0) {
							state.setState(PosState.CLOSESHEET);
							out.clear();
							out.displayChange(core.getValue());
							cashDrawer.open();
						}
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch(RealTimeException e){
                    state.setState(PosState.NETWORKERROR);
                }
				break;

			default :
				core.writelog("�˻�", "0", 0);
				out.prompt("��Ч����,�����������!");
				in.waitCancel();
				state.setState(PosState.SALE);
		}
		cashier.resetPrivilege();
		context.setAuthorizerid("");
	}

	public void unhold() {
		int count = context.getHeldCount();
		if (count < 1) {
			core.writelog("��ҵ�", "1", 0);
			context.setWarning("���ܹҿյ�,�����������!");
			state.setState(PosState.ERROR);
			return;
		} else if (count == 1) {
			core.unholdFirst();
//			out.printFeed(out.getFeedLines());
//			out.cutPaper();
			out.print("��ҵ�: " + Formatter.getTime(new Date()));
			out.displayHeader(context);
            //��ʾ��ҵ���Ա����Ϣ
            if(core.getPosSheet().getMemberCard()!=null){
                 out.dispMemberCardHeader(core.getPosSheet().getMemberCard());
            }
			displaySheet();
			core.writelog("��ҵ�", "0", 0);
			state.setState(PosState.SALE);
			return;
		}

		HoldList holdList = new HoldList(core.getSheetBrief());
		holdList.show();

		if (holdList.isConfrim()) {
			core.unholdSheet(holdList.getHoldNo());
			out.printFeed(out.getFeedLines());
			out.cutPaper();
			out.print("��ҵ�: " + Formatter.getTime(new Date()));
			out.displayHeader(context);
            //��ʾ��ҵ���Ա����Ϣ
            if(core.getPosSheet().getMemberCard()!=null){
                 out.dispMemberCardHeader(core.getPosSheet().getMemberCard());
            }

			displaySheet();
			state.setState(PosState.SALE);
		}

	}

	public void hold() {
		if (core.getPosSheet().getPayLen() > 0) {
			core.writelog("�ҵ�", "1", 0);
			context.setWarning("��֧��������ҵ�,�����������!");
			state.setState(PosState.ERROR);
			return;
		}

		int count = context.getHeldCount();
		if (count >= core.MAX_SHEETS - 1) {
			core.writelog("�ҵ�", "1", 0);
			context.setWarning("�ҵ����Ѿ��ﵽ����,�����������!");
			state.setState(PosState.ERROR);
			return;
		} else {
			core.writelog("�ҵ�", "0", 0);
			int n = core.holdSheet();
			state.setState(PosState.PRESALE);
			out.clear();
			out.print("�ҵ���δ���: " + Formatter.getTime(new Date()));
			out.printFeed(out.getFeedLines());
			out.cutPaper();
			////out.displayHeader(context);
		}
	}
	
	private void Drug(){
		out.displayState("�� �� ״ ̬");
		out.prompt("��������Ʒ���������룬�򰴷�����.");
		PosInput input = in.getInput();
		switch (input.key()) {
				case PosFunction.EXIT :
				case PosFunction.CANCEL :
					break;
				case PosFunction.NumberDrug :
					try {
					String drugNu;
					int drugNuInt;
					int Money = 0;
					drugNu = in.getBankCardNo_new(2,"�����������:");
					if (drugNu == null || drugNu.equals("0")){
						in.waitCancel();
						state.setState(PosState.DRUG);
						break;
						}
					drugNuInt = Integer.parseInt(drugNu);
					for (int i = (core.getSaleLen()-context.drupnumber); i < core.getSaleLen(); i++){
						core.getSale(i).setStdValue(core.getSale(i).getStdValue() * drugNuInt);
						core.getSale(i).setQty(core.getSale(i).getQty() * drugNuInt);
						core.getSale(i).setFactValue(core.getSale(i).getFactValue() * drugNuInt);
						// zhouzhou add 20070306
						core.getSale(i).setDiscValue(core.getSale(i).getDiscValue() * drugNuInt);
						// end;
						Money += core.getSale(i).getFactValue();
					}
					core.updateValue();
					String name = "��ҩ1*" + drugNu + "���� ";
					core.sell(Sale.AlTPRICE, name, -Money);
					SheetValue tmpSheetValue = new SheetValue();
					tmpSheetValue.setValue(-Money, 0, 0, 0,0);
					out.display(core.getFalseSale(core.getFalseSaleLen() - 1),
						tmpSheetValue);
					out.display(core.getValue(), context.getExchange());
					core.dump();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UserCancelException e) {
				e.printStackTrace();
			}
					state.setState(PosState.SALE);
					break;
				case PosFunction.GOODS :
					sellGoods((PosInputGoods) input);
					if (state.getState() != PosState.ERROR){
						context.drupnumber ++;
						state.setState(PosState.DRUG);
						}
					break;
				default :
					in.waitCancel();
					state.setState(PosState.DRUG);
			}
			cashier.resetPrivilege();
			context.setAuthorizerid("");
		}
	/**�ϼƹ���
	 * @deprecated
	 * */
	public void showTotal() {
		try {
			out.disptotal(core.getValue());
			SheetValue v = core.getValue();

			int value = v.getValueTotal();
			String name = "�ϼ�";
			core.sell(Sale.TOTAL, name, value);
			core.dump();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void cashin() {
		try {
			out.displayState("���");
			out.prompt("���������������ֽ����");

			PosInput input = in.getInput();
			if (input.key() == PosFunction.CANCEL) {
				core.writelog("���", "1", 0);
				state.setState(PosState.PRESALE);
			} else if (
				input.key() == PosFunction.PAYMENT
					&& ((PosInputPayment) input).getCents() > 0) {
				PosInputPayment p = (PosInputPayment) input;

				Payment pay_info = core.cashin(p);
				if (pay_info == null) {
					core.writelog("���", "1", 0);
					state.setState(PosState.ERROR);
				} else {
					cashDrawer.open();
					String s =
						pay_info.getCardno()
							+ " "
							+ new Value(pay_info.getValue()).toString();
					notice("�뽫���½�����Ǯ�䣺" + s);
					out.displayHeader(context);
					out.displayCash("���", s);
					core.openSheet();
					//out.displayHeader(context);
					out.displayWelcome();
					core.writelog("���", "0", 0);
					state.setState(PosState.PRESALE);
				}
			} else {
				core.writelog("���", "1", 0);
				context.setWarning("��Ч����,�����������!");
				state.setState(PosState.ERROR);
			}
		} catch (IOException e) {
			state.setState(PosState.ERROR);
		}

	}
	
	public void IF_SALE(){
		out.prompt("ת����ѯ,�����������!");
		pos.IF_SALE=true;	
		}

	public void cashout() {
		try {
			out.displayState("����");
			out.prompt("���������������ֽ����");

			PosInput input = in.getInput();
			if (input.key() == PosFunction.CANCEL) {
				core.writelog("����", "1", 0);
				state.setState(PosState.PRESALE);
			} else if (
				input.key() == PosFunction.PAYMENT
					&& ((PosInputPayment) input).getCents() > 0) {
				PosInputPayment p = (PosInputPayment) input;
				Payment pay_info = core.cashout(p);
				if (pay_info == null) {
					core.writelog("����", "1", 0);
					state.setState(PosState.ERROR);
				} else {
					String s;
					cashDrawer.open();
					s =
						(context.getCurrenCode() == "RMB")
							? ""
							: context.getCurrenCode();

					s += " " + new Value(-pay_info.getValue()).toString();

					notice("���Ǯ��ȡ�����½�" + s);
					out.displayHeader(context);
					out.displayCash("����", s);
					core.openSheet();
					//out.displayHeader(context);
					out.displayWelcome();
					core.writelog("����", "0", 0);
					state.setState(PosState.PRESALE);
				}
			} else {
				core.writelog("����", "1", 0);
				context.setWarning("��Ч����,�����������!");
				state.setState(PosState.ERROR);
			}
		} catch (IOException e) {
			state.setState(PosState.ERROR);
		}
	}

	private boolean confirm(String s) {
		DialogConfirm confirm = new DialogConfirm();
		confirm.setMessage(s);
		confirm.show();

		return (confirm.isConfirm());
	}

	private void notice(String note) {
		DialogInfo notice = new DialogInfo();
		notice.setMessage(note);
		notice.show();
	}

	private PosState state = null;
	private PosCore core = null;
	private PosDevIn in = null;
	private PosDevOut out = null;
	private Operator cashier = null;
	private Operator authorizeCashier = null;
	private Sale sale_rec = null;
	private SaleList salelst = null;
	private LogonDialog logonDialog;
	private POSCashDrawer cashDrawer=null;
	private PosContext context;
	private PosInputPayment inputpayment = null;
}

/**
 * @version 1.0 2004.05.11
 * @author  Mengluoyi, Royalstone  Co., Ltd.
 */

final class PosState {
	public PosState(int state) {
		CurrentState = state;
		OldState = state;
	}

	public int getState() {
		return CurrentState;
	}

	public int getOldState() {
		return OldState;
	}

	public void setState(int state) {
		OldState = CurrentState;
		CurrentState = state;
	}

	public int restoreState() {
		CurrentState = OldState;
		return CurrentState;
	}

	public boolean equals(int state) {
		return (CurrentState == state);
	}

	public boolean equals(PosState state) {
		return (CurrentState == state.CurrentState);
	}

	public String toString() {
		if (CurrentState == PRELOGON)
			return "PRELOGON";
		if (CurrentState == PRESALE)
			return "PRESALE";
		if (CurrentState == SALE)
			return "SALE";
		if (CurrentState == MAXCASH)
			return "MAXCASH";
		if (CurrentState == CORRECT)
			return "CORRECT";
		if (CurrentState == WITHDRAW)
			return "WITHDRAW";
		if (CurrentState == DISCOUNT)
			return "DISCOUNT";
		if (CurrentState == DISCTOTAL)
			return "DISCTOTAL";
		if (CurrentState == LOCK)
			return "LOCK";
		if (CurrentState == ALTPRICE)
			return "ALTPRICE";
		if (CurrentState == OPENSHEET)
			return "OPENSHEET";
		if (CurrentState == CLOSESHEET)
			return "CLOSESHEET";
		if (CurrentState == HOLD)
			return "HOLD";
		if (CurrentState == ERROR)
			return "ERROR";
		if (CurrentState == EXIT)
			return "EXIT";
		if (CurrentState == DRUG)
			return "DRUG";
		return "UNDEFINED";
	}

	final public static int PRELOGON = 'L';
	final public static int PRESALE = 'S';
	final public static int SALE = 's';
	final public static int FIND = 'F';
	final public static int MAXCASH = 'B';
	final public static int CORRECT = 'C';
	final public static int WITHDRAW = 'W';
	final public static int DISCOUNT = 'd';
	final public static int DISCTOTAL = 'Y';
	final public static int DISCMONEY = 'M';
	final public static int LOCK = 'K';
	final public static int ALTPRICE = 'A';
	final public static int OPENSHEET = 'O';
	final public static int CLOSESHEET = 'T';
	final public static int HOLD = 'H';
	final public static int ERROR = 'E';
	final public static int EXIT = 'X';
	final public static int CASHOUT = 'U';
	final public static int CASHIN = 'I';
	final public static int NETWORKERROR = 'N';
	final public static int DRUG	= 'D';
	final public static int IF_SALE='n';

	private int CurrentState;
	private int OldState;
}
