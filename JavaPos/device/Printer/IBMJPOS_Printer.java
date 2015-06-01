package device.Printer;

import java.util.Vector;

import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_Printer;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;

public class IBMJPOS_Printer implements Interface_Printer
{
	public POSPrinter posPrinter = null;
	EventListener event = null;
	// private int charset = 0;
	protected int statusCode = 0;
	protected String statusMsg = "";
	protected char printMode = 'N'; // N:异步 Y:同步
	protected boolean initTransactionMode = true;
	protected boolean transactionMode = initTransactionMode;
	protected boolean alreadyTransaction_Normal = false;
	protected boolean alreadyTransaction_Journal = false;
	protected boolean alreadyTransaction_Slip = false;
	protected String SlipEmptyMsg = null;
	protected boolean SlipInsertion = false;
	protected boolean SlipCutpaperMode = false;
	protected boolean bakfileopen = false;
	protected int maxrows = -1;
	protected int curRow_Normal = -1;
	protected int curRow_Journal = -1;
	protected int curRow_Slip = -1;
	protected int slpLineChar = 0;
	protected int Row_Slip = 0;
	protected Text_Printer txtprinter = null;
	boolean slpback = false;
	// 设置打印机使用字库如：1381;;998(Normal;Journal;Slip)
	protected int NormalFont = -1;
	protected int JournalFont = -1;
	protected int SlipFont = -1;
	
	protected int NormalPrint = 1;
	protected int JournalPrint = 2;
	protected int SlipPrint = 3;
	
	private int SlpLineSpacing = -1;
	
	//二维码
	String Qrcode = "#Qrcode:";
	//条码
	String Barcode = "#Barcode:";
	
    private static final int receipt = POSPrinterConst.PTR_S_RECEIPT;            //打印栈，normal
    private static final int slip = POSPrinterConst.PTR_S_SLIP;				     //打印栈，slip
    private static final int journal = POSPrinterConst.PTR_S_JOURNAL;            //打印栈，journal
    
    private static final int place_center = POSPrinterConst.PTR_BC_CENTER;       //打印位置，居中
    private static final int place_left = POSPrinterConst.PTR_BC_LEFT;           //打印位置，靠左
    private static final int place_right = POSPrinterConst.PTR_BC_RIGHT;         //打印位置，靠右
    
    private static int qrcode_height = 30;                                       //二维码高度
    private static int barcode_height = 100;                                     //条码高度
    private static final int printmode = POSPrinterConst.PTR_BC_TEXT_BELOW;      // 条码数字显示在下面
    																			 //PTR_BC_TEXT_NONE  条码数字不显示
    									                                         //PTR_BC_TEXT_ABOVE 条码数字显示在上面
    private static int barcode_type = POSPrinterConst.PTR_BCS_UPCE;              //条码打印类型
//    POSPrinterConst.PTR_BCS_EAN8    103    
//    POSPrinterConst.PTR_BCS_UPCE    102
//    POSPrinterConst.PTR_BCS_UPCA    101
//    POSPrinterConst.PTR_BCS_EAN13   104
//    POSPrinterConst.PTR_BCS_Code39  108
//    POSPrinterConst.PTR_BCS_Code93  109
//    POSPrinterConst.PTR_BCS_Code128 110
	public boolean open()
	{
		if (DeviceName.devicePrinter.length() <= 0) { return false; }

		posPrinter = new POSPrinter();

		event = new EventListener();

		String[] arg = DeviceName.devicePrinter.split(",");

		if ((arg.length > 1) && (arg[1].length() > 0))
		{
			String[] printFont = arg[1].split(";");
			//System.out.println(printFont.length+" "+arg[1]);
			if (printFont.length <= 1) printFont = arg[1].split("Z");
			System.out.println(printFont.length+" "+arg[1]);
			if ((printFont.length > 0) && (printFont[0].length() > 0))
			{
				NormalFont = Convert.toInt(printFont[0]);
			}

			if ((printFont.length > 1) && (printFont[1].length() > 0))
			{
				JournalFont = Convert.toInt(printFont[1]);
			}
			else
			{
				JournalFont = NormalFont;
			}

			if ((printFont.length > 2) && (printFont[2].length() > 0))
			{
				SlipFont = Convert.toInt(printFont[2]);
			}
			else
			{
				SlipFont = NormalFont;
			}
		}

		if ((arg.length > 2) && (arg[2].length() > 0))
		{
			printMode = arg[2].charAt(0);
		}

		if ((arg.length > 3) && (arg[3].length() > 0))
		{
			SlipInsertion = arg[3].equals("Y");
		}

		if ((arg.length > 4) && (arg[4].length() > 0))
		{
			SlipCutpaperMode = arg[4].equals("Y");
		}

		if ((arg.length > 5) && (arg[5].length() > 0))
		{
			initTransactionMode = arg[5].equals("Y");
			transactionMode = initTransactionMode;
		}

		if ((arg.length > 6) && (arg[6].length() > 0))
		{
			maxrows = Convert.toInt(arg[6]);
		}

		if ((arg.length > 7) && (arg[7].length() > 0))
		{
			slpLineChar = Convert.toInt(arg[7]);
			System.out.println("slip :" + slpLineChar);
		}

		if ((arg.length > 8) && (arg[8].length() > 0))
		{
			bakfileopen = arg[8].equals("Y");

			if (bakfileopen)
			{
				txtprinter = new Text_Printer();
			}
		}
		
		if ((arg.length > 9) && (arg[9].length() > 0))
		{
			String flg = arg[9].trim();
			
			if (flg.length() > 0) NormalPrint = Convert.toInt(String.valueOf(flg.charAt(0)));
			
			if (flg.length() > 1) JournalPrint = Convert.toInt(String.valueOf(flg.charAt(1)));
			
			if (flg.length() > 2) SlipPrint = Convert.toInt(String.valueOf(flg.charAt(2)));
		}
		
		if (arg.length > 10 && arg[10].length() > 0)
		{
			SlpLineSpacing = Convert.toInt(arg[10]);
		}
		if (arg.length > 11 && arg[11].length() > 0)
		{
			barcode_type = Convert.toInt(arg[11]);
		}
		

		// Open
		try
		{
			posPrinter.open(arg[0]);

			return true;
		}
		catch (JposException e)
		{
			e.printStackTrace();
			new MessageBox(Language.apply("打开JPOS打印机异常:\n") + e.getMessage());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox(Language.apply("打开JPOS打印机异常:\n") + e.getMessage());
		}

		return false;
	}

	public void close()
	{
		try
		{
			if (posPrinter != null) posPrinter.close();
		}
		catch (JposException e)
		{
			e.printStackTrace();
		}
	}

	public void setEnable(boolean enable)
	{
		try
		{
			if (enable)
			{
				if (!posPrinter.getClaimed())
				{
					posPrinter.claim(1000);
					posPrinter.setDeviceEnabled(true);

					//当使用平推打印时,必须使用异步方式,否则当平推有纸时,其他打印端口打印的内容会遗失
					//异步且事务打印模式,平摊有纸时,打印栈等待,拿出平推纸以后，所有内容重新打印
					//异步非事务打印模式,平摊有纸时,打印栈等待,拿出平推纸以后，继续打印剩余内容,但打印速度较慢
					//同步且事务打印模式,平摊有纸时,打印栈transprint函数时抛异常,打印内容丢失
					//同步非事务打印模式,平摊有纸时,当前行异常,打印下一行时checkstatus等待拿出平推纸,然后继续打印
					if (printMode == 'Y')
					{
						posPrinter.setAsyncMode(false); // 同步模式
					}
					else
					{
						posPrinter.setAsyncMode(true); // 异步模式
					}

					if (slpLineChar > 0)
					{
						posPrinter.setSlpLineChars(slpLineChar);
					}
					
					if (SlpLineSpacing >= 0)
					{
						posPrinter.setSlpLineSpacing(SlpLineSpacing);
					}
					
					

					posPrinter.addStatusUpdateListener(event);
					posPrinter.addErrorListener(event);
				}
			}
			else
			{
				if (posPrinter.getClaimed())
				{
					posPrinter.removeStatusUpdateListener(event);
					posPrinter.addErrorListener(event);
					posPrinter.setDeviceEnabled(false);
					posPrinter.release();
				}
			}
		}
		catch (JposException e)
		{
			e.printStackTrace();
		}
	}

	public void printLine_Journal(String printStr)
	{
		try
		{
			if (this.JournalPrint == 1)
			{
				printLine_Normal(printStr);
				return ;
			}
			
			if (this.JournalPrint == 3)
			{
				printLine_Slip(printStr);
				return ;
			}
			
			if (bakfileopen)
			{
				txtprinter.printLine_Journal(printStr);
			}

////////////////////////////////////////////
			if(printStr.indexOf(Qrcode) >= 0)
			{
				String contents = printStr.substring(printStr.indexOf(Qrcode)+Qrcode.length());
				printQrcode(journal ,contents ,qrcode_height ,qrcode_height ,place_center ,printmode);
				return;
			}else if(printStr.indexOf(Barcode) >= 0)
			{
				String contents = printStr.substring(printStr.indexOf(Barcode)+Barcode.length());
				printBarcode(journal ,contents ,barcode_type ,barcode_height ,place_center ,printmode);
				return;
			}
////////////////////////////////////////////
			
			if (!checkStatus()) { return; }

			// 事务打印方式打印行数达到缓存行数，则将之前的打印内容及时打印
			if ((maxrows > 0) && alreadyTransaction_Journal && transactionMode)
			{
				curRow_Journal++;

				if (curRow_Journal >= maxrows)
				{
					curRow_Journal = -1;
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_JOURNAL, POSPrinterConst.PTR_TP_NORMAL);
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_JOURNAL, POSPrinterConst.PTR_TP_TRANSACTION);			
				}
			}

			// 如果没有执行过开启事务打印方式，执行一次
			if (!alreadyTransaction_Journal)
			{
				alreadyTransaction_Journal = true;

				if (transactionMode)
				{
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_JOURNAL, POSPrinterConst.PTR_TP_TRANSACTION);
				}
				else
				{
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_JOURNAL, POSPrinterConst.PTR_TP_NORMAL);
				}
			}

			boolean done = false;

			if (JournalFont > 0 && posPrinter.getCharacterSet() != JournalFont) posPrinter.setCharacterSet(JournalFont);

			if (printStr.indexOf("Big&") >= 0)
			{
				done = true;

				char[] value = { 0x1b, '|', '2', 'C' };
				printStr = printStr.replaceAll("Big&", String.valueOf(value));
			}

			posPrinter.printNormal(POSPrinterConst.PTR_S_JOURNAL, printStr);

			if (done)
			{
				char[] value = { 0x1b, '|', 'N' };
				posPrinter.printNormal(POSPrinterConst.PTR_S_JOURNAL, String.valueOf(value));
			}
		}
		catch (JposException e)
		{
			e.printStackTrace();
		}
	}

	public boolean checkPrinterIdle()
	{
		int time = 30;
		// 2=emtpy 等待打印机空闲,才继续往打印机里输入信息
		while (true)
		{
			int i = 0;
			//String msg = "";
			ProgressBox pb = null;
			try
			{
				int intflg = 0;
				while ( (intflg = posPrinter.getState()) != 2)
				{
					System.out.println("posPrinter.getState() -------------"+intflg);
					if (pb == null)
					{
						pb = new ProgressBox();
						pb.setText(Language.apply("打印机正在处于打印工作状态,请等待..."));
					}

					Thread.sleep(500);
					i++;
					if (i >= 120)
					{
						//msg = "等待1分钟后打印机还未空闲";
						break; //等待1分钟
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				i = 120;
				//msg = "等待打印机空闲异常:" + ex.getMessage();
			}
			finally
			{
				if (pb != null) pb.close();
	            if (i >= time)
	            {
            		if (new MessageBox(Language.apply("检查打印机状态超时，是否重新初始化打印机设备\n"),null,true).verify() == GlobalVar.Key1)
            		{
            			System.out.println("检测打印机异常，close打印机");
            			close();
            			
            			try
						{
							Thread.sleep(2000);
						}
						catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            			
						System.out.println("检测打印机异常，OPEN打印机");
            			while (!open())
            			{
            				if (new MessageBox(Language.apply("打印机初始化失败\n请检查打印机线路或打印机盖内是否存在纸削..."),null,true).verify() == GlobalVar.Key2)
            				{
            					new MessageBox(Language.apply("打印机没有初始化，以后将无法打印"));
            					break;
            				}
            			}
            			
            			System.out.println("检测打印机异常，ENABLE打印机");
            			setEnable(true);
            			
            			try
						{
							Thread.sleep(2000);
						}
						catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            		}
            		else
            		{
            			System.out.println("是否重新初始化打印机设备 ，收银员按 2");
            		}
	            }
	            else
	            {
	            	break;
	            }
	        
			}
		}

		return true;
	}

	public void printLine_Normal(String printStr)
	{
		try
		{
			if (this.NormalPrint == 2)
			{
				this.printLine_Journal(printStr);
				return ;
			}
			
			if (this.NormalPrint == 3)
			{
				this.printLine_Slip(printStr);
				return ;
			}
			
			if (!checkPrinterIdle()) { return; }

			if (bakfileopen)
			{
				txtprinter.printLine_Normal(printStr);
			}

////////////////////////////////////////////
			if(printStr.indexOf(Qrcode) >= 0)
			{
				String contents = printStr.substring(printStr.indexOf(Qrcode)+Qrcode.length());
				printQrcode(receipt ,contents ,qrcode_height ,qrcode_height ,place_center ,printmode);
				return;
			}else if(printStr.indexOf(Barcode) >= 0)
			{
				String contents = printStr.substring(printStr.indexOf(Barcode)+Barcode.length());
				printBarcode(receipt ,contents ,barcode_type ,barcode_height ,place_center ,printmode);
				return;
			}			
////////////////////////////////////////////
			
			if (!checkStatus()) { return; }

			// 事务打印方式打印行数达到缓存行数，则将之前的打印内容及时打印
			if ((maxrows > 0) && alreadyTransaction_Normal && transactionMode)
			{
				curRow_Normal++;

				if (curRow_Normal >= maxrows)
				{
					curRow_Normal = -1;
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_NORMAL);
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_TRANSACTION);
				}
			}

			// 如果没有执行过开启事务打印方式，执行一次
			if (!alreadyTransaction_Normal)
			{
				alreadyTransaction_Normal = true;

				if (transactionMode)
				{
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_TRANSACTION);
				}
				else
				{
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_NORMAL);
				}
			}

			boolean done = false;

			if (NormalFont > 0 && posPrinter.getCharacterSet() != NormalFont) posPrinter.setCharacterSet(NormalFont);

			if (printStr.indexOf("Big&") >= 0)
			{
				done = true;

				char[] value = { 0x1b, '|', '2', 'C' };
				printStr = printStr.replaceAll("Big&", String.valueOf(value));
			}

			posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, printStr);

			if (done)
			{
				char[] value = { 0x1b, '|', 'N' };
				posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, String.valueOf(value));
			}
		}
		catch (JposException e)
		{
			System.out.println("PrintNormal errors");
			e.printStackTrace();
		}
	}

	public void printLine_Slip(String printStr)
	{
		try
		{
			if (this.SlipPrint == 1)
			{
				this.printLine_Normal(printStr);
				return ;
			}
			
			if (this.SlipPrint == 2)
			{
				this.printLine_Journal(printStr);
				return ;
			}
			
			if (bakfileopen)
			{
				txtprinter.printLine_Slip(printStr);
			}
			
////////////////////////////////////////////
			if(printStr.indexOf(Qrcode) >= 0)
			{
				String contents = printStr.substring(printStr.indexOf(Qrcode)+Qrcode.length());
				printQrcode(slip ,contents ,qrcode_height ,qrcode_height ,place_center ,printmode);
				return;
			}else if(printStr.indexOf(Barcode) >= 0)
			{
				String contents = printStr.substring(printStr.indexOf(Barcode)+Barcode.length());
				printBarcode(slip ,contents ,barcode_type ,barcode_height ,place_center ,printmode);
				return;
			}			
////////////////////////////////////////////


			if (!checkStatus()) { return; }

			// 不开启ER_CLEAR
			slpback = false;

			/**
			if (SlipInsertion && curRow_Slip == -1)
			{
				posPrinter.beginInsertion(3000);
				posPrinter.endInsertion();
			}*/
			
			// 等待进纸
			while (posPrinter.getSlpEmpty())
			{
				if ((SlipEmptyMsg == null) || SlipEmptyMsg.trim().equals(""))
				{
					SlipEmptyMsg = Language.apply("请在平摊打印机中放入打印纸...");
				}

				if (SlipInsertion)
				{
					ProgressBox pb = null;

					try
					{
						pb = new ProgressBox();
						pb.setText(SlipEmptyMsg);
						//System.out.println(SlipEmptyMsg);
						posPrinter.beginInsertion(5000);
						posPrinter.endInsertion();
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
					finally
					{
						if (pb != null)
						{
							pb.close();
						}
					}
				}
				else
				{
					new MessageBox(SlipEmptyMsg);
				}
			}

			// 事务打印方式打印行数达到缓存行数，则将之前的打印内容及时打印
			if ((maxrows > 0) && alreadyTransaction_Slip && transactionMode)
			{
				curRow_Slip++;

				if (curRow_Slip >= maxrows)
				{
					curRow_Slip = -1;
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_SLIP, POSPrinterConst.PTR_TP_NORMAL);
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_SLIP, POSPrinterConst.PTR_TP_TRANSACTION);
				}
			}

			// 如果没有执行过开启事务打印方式，执行一次
			if (!alreadyTransaction_Slip)
			{
				alreadyTransaction_Slip = true;

				if (transactionMode)
				{
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_SLIP, POSPrinterConst.PTR_TP_TRANSACTION);
				}
				else
				{
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_SLIP, POSPrinterConst.PTR_TP_NORMAL);
				}
			}

			boolean done = false;

			if (SlipFont > 0 && posPrinter.getCharacterSet() != SlipFont) posPrinter.setCharacterSet(SlipFont);

			if (printStr.indexOf("Big&") >= 0)
			{
				char[] value = { 0x1b, '|', '2', 'C' };
				printStr = printStr.replaceAll("Big&", String.valueOf(value));
				done = true;
			}

			posPrinter.printNormal(POSPrinterConst.PTR_S_SLIP, printStr);

			if (done)
			{
				char[] value = { 0x1b, '|', 'N' };
				posPrinter.printNormal(POSPrinterConst.PTR_S_SLIP, String.valueOf(value));
			}

			Row_Slip++;
		}
		catch (JposException e)
		{
			e.printStackTrace();
		}
	}

	protected boolean checkStatus()
	{
		while (statusCode != 0)
		{
			if (new MessageBox(statusMsg + Language.apply("\n\n 1-重试 / 2-放弃当前打印行")).verify() != GlobalVar.Key1) { return false; }
		}

		return true;
	}

	public void cutPaper_Journal()
	{
		try
		{
			if (this.JournalPrint == 1)
			{
				this.cutPaper_Normal();
				return ;
			}
			
			if (this.JournalPrint == 3)
			{
				this.cutPaper_Slip();
				return;
			}
			
			if (bakfileopen)
			{
				txtprinter.cutPaper_Journal();
			}

			for (int i = 0; i < 3; i++)
			{
				printLine_Journal("\n");
			}

			// 如果当前是事务打印模式,切换模式,将之前的打印内容打印
			if (transactionMode)
			{
				curRow_Journal = -1;
				posPrinter.transactionPrint(POSPrinterConst.PTR_S_JOURNAL, POSPrinterConst.PTR_TP_NORMAL);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			transactionMode = initTransactionMode;
			alreadyTransaction_Journal = false;
		}
	}

	public void cutPaper_Normal()
	{
		try
		{
			if (this.NormalPrint == 2)
			{
				this.cutPaper_Journal();
				return ;
			}
			
			if (this.NormalPrint == 3)
			{
				this.cutPaper_Slip();
				return ;
			}
			
			if (bakfileopen)
			{
				txtprinter.cutPaper_Normal();
			}

			for (int i = 0; i < 5; i++)
			{
				printLine_Normal("\n");
			}

			// 如果当前是事务打印模式,切换模式,将之前的打印内容打印
			if (transactionMode)
			{
				curRow_Normal = -1;
				posPrinter.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_NORMAL);
			}

			// 切纸
			posPrinter.cutPaper(90);
		}
		catch (JposException e)
		{
			System.err.println("Cut Paper error");
			e.printStackTrace();

			try
			{
				posPrinter.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_NORMAL);
			}
			catch (JposException e1)
			{
				e1.printStackTrace();
			}
		}
		finally
		{
			transactionMode = initTransactionMode;
			alreadyTransaction_Normal = false;
		}
	}

	public void cutPaper_Slip()
	{
		try
		{
			if (this.SlipPrint == 1)
			{
				this.cutPaper_Normal();
				return ;
			}
			
			if (this.SlipPrint == 2)
			{
				this.cutPaper_Journal();
				return ;
			}
			
			if (bakfileopen)
			{
				txtprinter.cutPaper_Slip();
			}

			// 退纸命令
			if (SlipCutpaperMode)
			{
				slpback = true;

				// 如果当前是事务打印模式,切换模式,将之前的打印内容打印
				if (transactionMode)
				{
					curRow_Slip = -1;
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_SLIP, POSPrinterConst.PTR_TP_NORMAL);
				}

				// 等待打印机完成打印,空闲后
				if (!checkPrinterIdle()) return;

				// 向下退纸时强制设定为同步
				boolean async = posPrinter.getAsyncMode();
				try
				{
					posPrinter.setAsyncMode(false);
					char[] value = { 0x1b, '|', '8', '0', 'r', 'F' };
					posPrinter.printNormal(POSPrinterConst.PTR_S_SLIP, String.valueOf(value)); //向下出纸
				}
				catch (JposException er)
				{
					er.printStackTrace();
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}
				finally
				{
					posPrinter.setAsyncMode(async);
				}
			}
			else
			{
				// 如果当前是事务打印模式,切换模式,将之前的打印内容打印
				if (transactionMode)
				{
					curRow_Slip = -1;
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_SLIP, POSPrinterConst.PTR_TP_NORMAL);
				}

				// 结束
				posPrinter.beginRemoval(5000);
				posPrinter.endRemoval();
			}

			// 等待纸拿出表示打印完毕,确保在未打印完前不再发送新的打印数据
			ProgressBox pb = null;
			try
			{
				pb = new ProgressBox();
				pb.setText(Language.apply("平摊联打印完毕,请等待自动退纸完毕后再拿出..."));
				while (!posPrinter.getSlpEmpty())
					Thread.sleep(500);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if (pb != null) pb.close();
			}
		}
		catch (JposException e)
		{
			System.err.println(e.getErrorCode());
			e.printStackTrace();
		}
		finally
		{
			Row_Slip = 0;
			transactionMode = initTransactionMode;
			alreadyTransaction_Slip = false;
		}
	}

	public boolean passPage_Journal()
	{
		return false;
	}

	public boolean passPage_Normal()
	{
		return false;
	}

	public boolean passPage_Slip()
	{
		cutPaper_Slip();

		return true;
	}

	public void enableRealPrintMode(boolean flag)
	{
		// 当前是事务打印模式，如果要切换为非事务打印模式，将之前的数据打印出来
		if (transactionMode && (!flag == false))
		{
			if (alreadyTransaction_Normal)
			{
				try
				{
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_NORMAL);
				}
				catch (JposException e)
				{
					e.printStackTrace();
				}
			}

			if (alreadyTransaction_Journal)
			{
				try
				{
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_JOURNAL, POSPrinterConst.PTR_TP_NORMAL);
				}
				catch (JposException e)
				{
					e.printStackTrace();
				}
			}

			if (alreadyTransaction_Slip)
			{
				try
				{
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_SLIP, POSPrinterConst.PTR_TP_NORMAL);
				}
				catch (JposException e)
				{
					e.printStackTrace();
				}
			}
		}

		// 即扫即打方式设置为非事务模式，非即扫即打方式设置为事务模式
		transactionMode = !flag;

		//
		alreadyTransaction_Normal = false;
		alreadyTransaction_Journal = false;
		alreadyTransaction_Slip = false;
	}

	public Vector getPara()
	{
		Vector v = new Vector();
		v.add(new String[] { Language.apply("JPOS逻辑名"), "POSPrinter1" });
		v.add(new String[] { Language.apply("打印机字库CharacterSet(Normal;Journal;Slip)"), "" });// 设置打印机使用字库如：1381;;998(Normal;Journal;Slip)
		v.add(new String[] { Language.apply("是否同步打印"), "N", "Y" });
		v.add(new String[] { Language.apply("平摊是否自动进纸"), "N", "Y" });
		v.add(new String[] { Language.apply("平摊打印向后退纸"), "N", "Y" });
		v.add(new String[] { Language.apply("是否高速打印模式"), "Y", "N" });
		v.add(new String[] { Language.apply("高速打印缓存大小"), "-1" });
		v.add(new String[] { Language.apply("平推打印起始位置"), "38" });
		v.add(new String[] { Language.apply("同步输入到文本"), "N", "Y" });
		v.add(new String[] { Language.apply("打印栈设定"), "123"});
		v.add(new String[] { Language.apply("行距设定SlpLineSpacing"), ""});
		v.add(new String[] { Language.apply("条码打印类型"), "101","102","103","104","108","109","110"});

		return v;
	}

	public String getDiscription()
	{
		return Language.apply("IBM的JPOS驱动方式打印机");
	}

	public void setEmptyMsg_Slip(String msg)
	{
		SlipEmptyMsg = msg;
	}

	public void setBigChar(boolean status)
	{
		if (status)
		{
		}
	}

	public void setLogo()
	{
		//posPrinter.setLogo(arg0, arg1)
	}

	public class EventListener implements StatusUpdateListener, ErrorListener
	{
		private int getSUEMessage(int code)
		{
			int value = 0;

			try
			{
				switch (code)
				{
					case POSPrinterConst.PTR_SUE_COVER_OPEN: //打印机盖打开
						value = POSPrinterConst.PTR_SUE_COVER_OPEN;
						statusMsg = Language.apply("打印机盖打开");

						break;

					case POSPrinterConst.PTR_SUE_JRN_EMPTY: //日志打印机缺纸
						value = POSPrinterConst.PTR_SUE_JRN_EMPTY;
						statusMsg = Language.apply("日志打印机缺纸");

						break;

					case POSPrinterConst.PTR_SUE_JRN_NEAREMPTY: //日志打印机临近缺纸
						value = POSPrinterConst.PTR_SUE_JRN_NEAREMPTY;
						statusMsg = Language.apply("日志打印机临近缺纸");

						break;

					case POSPrinterConst.PTR_SUE_JRN_PAPEROK: //日志打印机纸张异常状态解除
						value = POSPrinterConst.PTR_SUE_JRN_PAPEROK;
						statusMsg = Language.apply("日志打印机纸张异常状态解除");

						break;

					case POSPrinterConst.PTR_SUE_REC_EMPTY: //票据打印机缺纸
						value = POSPrinterConst.PTR_SUE_REC_EMPTY;
						statusMsg = Language.apply("票据打印机缺纸");

						break;

					case POSPrinterConst.PTR_SUE_REC_NEAREMPTY: //票据打印机临近缺纸
						value = POSPrinterConst.PTR_SUE_REC_NEAREMPTY;
						statusMsg = Language.apply("票据打印机临近缺纸");

						break;

					case POSPrinterConst.PTR_SUE_REC_PAPEROK: //票据打印机纸张异常状态解除
						value = 0;
						statusMsg = Language.apply("票据打印机纸张异常状态解除");

						break;

					case POSPrinterConst.PTR_SUE_SLP_EMPTY: //平推打印机缺纸
						value = 0;
						statusMsg = Language.apply("平推打印机缺纸");

						break;

					case POSPrinterConst.PTR_SUE_SLP_NEAREMPTY: //平推打印机临时缺纸
						value = 0;
						statusMsg = Language.apply("平推打印机临时缺纸");

						break;

					case POSPrinterConst.PTR_SUE_SLP_PAPEROK: //平推打印机纸张异常状态解除
						value = 0;
						statusMsg = Language.apply("平推打印机纸张异常状态解除");

						break;

					case POSPrinterConst.PTR_SUE_IDLE: //打印机等待指令状态
						value = POSPrinterConst.PTR_SUE_IDLE;
						statusMsg = Language.apply("打印机等待指令状态");

						break;

					case POSPrinterConst.PTR_SUE_JRN_CARTRIDGE_EMPTY: //日志打印机墨盒缺墨状态
						value = POSPrinterConst.PTR_SUE_JRN_CARTRIDGE_EMPTY;
						statusMsg = Language.apply("日志打印机墨盒缺墨状态");

						break;

					case POSPrinterConst.PTR_SUE_JRN_HEAD_CLEANING: //日志打印机清洗打印磁头状态
						value = POSPrinterConst.PTR_SUE_JRN_HEAD_CLEANING;
						statusMsg = Language.apply("日志打印机清洗打印磁头状态");

						break;

					case POSPrinterConst.PTR_SUE_JRN_CARTRIDGE_NEAREMPTY: //日志打印机临近缺默状态
						value = POSPrinterConst.PTR_SUE_JRN_CARTRIDGE_NEAREMPTY;
						statusMsg = Language.apply("日志打印机临近缺默状态");

						break;

					case POSPrinterConst.PTR_SUE_JRN_CARTDRIGE_OK: //日志打印机墨盒异常状态解除
						value = 0;
						statusMsg = Language.apply("日志打印机墨盒异常状态解除");

						break;

					case POSPrinterConst.PTR_SUE_REC_CARTRIDGE_EMPTY: //票据打印机墨盒缺墨状态
						value = POSPrinterConst.PTR_SUE_REC_CARTRIDGE_EMPTY;
						statusMsg = Language.apply("票据打印机墨盒缺墨状态");

						break;

					case POSPrinterConst.PTR_SUE_REC_HEAD_CLEANING: //票据打印机清洗打印磁头状态
						value = POSPrinterConst.PTR_SUE_REC_HEAD_CLEANING;
						statusMsg = Language.apply("票据打印机清洗打印磁头状态");

						break;

					case POSPrinterConst.PTR_SUE_REC_CARTRIDGE_NEAREMPTY: //票据打印机临近缺墨状态
						value = POSPrinterConst.PTR_SUE_REC_CARTRIDGE_NEAREMPTY;
						statusMsg = Language.apply("票据打印机临近缺墨状态");

						break;

					case POSPrinterConst.PTR_SUE_REC_CARTDRIGE_OK: //票据打印机墨盒异常状态解除
						value = 0;
						statusMsg = Language.apply("票据打印机墨盒异常状态解除");

						break;

					case POSPrinterConst.PTR_SUE_SLP_CARTRIDGE_EMPTY: //平推打印机缺墨状态
						value = POSPrinterConst.PTR_SUE_SLP_CARTRIDGE_EMPTY;
						statusMsg = Language.apply("平推打印机缺墨状态");

						break;

					case POSPrinterConst.PTR_SUE_SLP_HEAD_CLEANING: //平推打印机清洗打印磁头状态
						value = POSPrinterConst.PTR_SUE_SLP_HEAD_CLEANING;
						statusMsg = Language.apply("平推打印机清洗打印磁头状态");

						break;

					case POSPrinterConst.PTR_SUE_SLP_CARTRIDGE_NEAREMPTY: //平推打印机临近缺墨状态
						value = POSPrinterConst.PTR_SUE_SLP_CARTRIDGE_NEAREMPTY;
						statusMsg = Language.apply("平推打印机临近缺墨状态");

						break;

					case POSPrinterConst.PTR_SUE_SLP_CARTRIDGE_OK: //平推打印机墨盒异常状态解除
						value = 0;
						statusMsg = Language.apply("平推打印机墨盒异常状态解除");

						break;

					default:
						value = 0; //当前打印机活动正常状态
				}

				return value;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				statusMsg = Language.apply("打印机未知异常");

				return -1;
			}
			finally
			{
				if (value != 0)
				{
					try
					{
						posPrinter.clearPrintArea();
					}
					catch (JposException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		public void statusUpdateOccurred(StatusUpdateEvent sue)
		{
			try
			{
				statusCode = getSUEMessage(sue.getStatus());
			}
			catch (Exception ex)
			{
				new MessageBox("UpdateOccurred" + ex.getMessage());
			}
		}

		public void errorOccurred(ErrorEvent arg0)
		{
			System.err.println("*********** " + arg0.getErrorCode() + "   " + arg0.getErrorCodeExtended());
			if (slpback && (arg0.getErrorCode() == 114))
			{
				// +1则设置为ER_CLEAR
				int code = arg0.getErrorResponse();
				arg0.setErrorResponse(code + 1);
			}
		}
	}
	
	public void printBarcode(int printzhan ,String contents ,int type ,int height ,int place ,int printmode)
	{
		try{
			if(posPrinter.getCapRecBarCode()){
	        	posPrinter.printBarCode(
	        	        printzhan,
	        	        contents,
	        	        type,
	        			height,
	        			(int)(posPrinter.getRecLineWidth()*.75),
	        			place,
	        			printmode);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	public void printQrcode(int printzhan ,String contents ,int height ,int weight ,int place ,int printmode)
	{
		try{
			if(posPrinter.getCapRecBarCode()){
	        	posPrinter.printBarCode(
	        	        printzhan,
	        	        contents,
	        			204,
	        			height,
	        			weight,
	        			place,
	        			printmode);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
}
