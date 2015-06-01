package com.efuture.javaPos.Device;

import java.io.BufferedReader;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.Interface.Interface_Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.InvoiceSummaryMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.UI.Design.MutiInputForm;

public class Printer
{
	private static Printer devicePrinter = null;
	public Interface_Printer printer = null;

	protected boolean connect = false;
	protected boolean enable = false;
	protected boolean iscutpaper = true;

	protected boolean pagePrint_Normal = false; // 是否分页打印
	protected boolean pagePrint_Journal = false;
	protected boolean pagePrint_Slip = false;

	protected int numRow_Normal = 0; // 已打印行数
	protected int numRow_Journal = 0;
	protected int numRow_Slip = 0;

	protected int curRow_Normal = 1; // 当前打印行号
	protected int curRow_Journal = 1;
	protected int curRow_Slip = 1;

	protected int pageRow_Normal = 1; // 每页打印行数
	protected int pageRow_Journal = 1;
	protected int pageRow_Slip = 1;

	protected int pageNum_Normal = 1; // 本次打印页数
	protected int pageNum_Journal = 1;
	protected int pageNum_Slip = 1;

	protected int areaStart_Normal = 1; // 打印区域开始行
	protected int areaStart_Journal = 1;
	protected int areaStart_Slip = 1;

	protected int areaEnd_Normal = 1; // 打印区域结束行
	protected int areaEnd_Journal = 1;
	protected int areaEnd_Slip = 1;

	protected int cutLine_Normal = 0;
	protected int cutLine_Journal = 0;
	protected int cutLine_Slip = 0;

	protected int jumpLine_Normal = 0;
	protected int jumpLine_Journal = 0;
	protected int jumpLine_Slip = 0;

	protected boolean isPrintPageHead = false; // 临时变量是否打印PageHeadPrint

	protected Vector salefpdata = null;
	protected boolean salefpcomplate = false;
	protected final String InvoicePage = "InvoicePage";
	public final String InvoiceStart = "InvoiceStart";
	public final String InvoiceCount = "InvoiceCount";
	protected final String InvoiceNum = "InvoiceNum";
	protected final String InvoiceName = "InvoiceName";
	protected final String InvoiceXsZs = "InvoiceXsZs";
	protected final String InvoiceXsJe = "InvoiceXsJe";
	protected final String InvoiceXskpJe = "InvoiceXskpJe";
	protected final String InvoiceThZs = "InvoiceThZs";
	protected final String InvoiceThJe = "InvoiceThJe";
	protected final String InvoiceThkpJe = "InvoiceThkpJe";
	protected final String InvoiceHcZs = "InvoiceHcZs";
	protected final String InvoiceHcJe = "InvoiceHcJe";
	protected final String InvoiceHckpJe = "InvoiceHckpJe";
	protected final String InvoiceStartDate = "InvoiceStartDate";// 发票的起始时间
	protected final String InvoicePrintSummary = "InvoicePrintSummary";// 是否打印发票汇总
	protected final String InvoicePrintGrant = "InvoicePrintGrant";// 发票功能是否授权

	public static Printer getDefault()
	{
		if (Printer.devicePrinter == null)
		{
			Printer.devicePrinter = CustomLocalize.getDefault().createPrinter(ConfigClass.Printer1);// new
																									// Printer(ConfigClass.Printer1);
		}

		return Printer.devicePrinter;
	}

	public Printer(String name)
	{
		try
		{
			if ((name != null) && (name.trim().length() > 0))
			{
				Class cl = Class.forName(name);

				printer = (Interface_Printer) cl.newInstance();

				// 读取自定义切纸行定义
				loadCutLine();

				// 读取发票配置
				if (PathFile.fileExist(GlobalVar.ConfigPath + "/SaleFphm.ini"))
				{
					salefpdata = CommonMethod.readFileByVector(GlobalVar.ConfigPath + "/SaleFphm.ini");
				}
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).debug(ex);
			ex.printStackTrace();
			printer = null;

//			new MessageBox("[" + name + "]\n\n打印设备对象不存在");
			new MessageBox(Language.apply("[{0}]\n\n打印设备对象不存在", new Object[]{name}));
		}
	}

	public void loadCutLine()
	{
		if (!PathFile.fileExist(GlobalVar.ConfigPath + "\\CutLine.ini"))
			return;

		try
		{
			BufferedReader br = CommonMethod.readFileGBK(GlobalVar.ConfigPath + "\\CutLine.ini");

			if (br == null)
			{
				new MessageBox(Language.apply("打开 CutLine.ini 文件失败!"));
				return;
			}

			String line = null;
			String[] cutName;
			while ((line = br.readLine()) != null)
			{
				if (line.trim().length() <= 0)
					continue;

				if (line.substring(0, 1).equals(";"))
				{
					continue;
				}

				if (line.indexOf("=") > -1 && line.trim().split("=").length >= 2)
				{
					cutName = line.trim().split("=");

					if (cutName[0].trim().equalsIgnoreCase("Normal"))
					{
						cutLine_Normal = Integer.parseInt(cutName[1].trim());
					}

					if (cutName[0].trim().equalsIgnoreCase("Journal"))
					{
						cutLine_Journal = Integer.parseInt(cutName[1].trim());
					}

					if (cutName[0].trim().equalsIgnoreCase("Slip"))
					{
						cutLine_Slip = Integer.parseInt(cutName[1].trim());
					}

					if (cutName[0].trim().equalsIgnoreCase("JumpLine_Normal"))
					{
						jumpLine_Normal = Integer.parseInt(cutName[1].trim());
					}

					if (cutName[0].trim().equalsIgnoreCase("JumpLine_Journal"))
					{
						jumpLine_Journal = Integer.parseInt(cutName[1].trim());
					}

					if (cutName[0].trim().equalsIgnoreCase("JumpLine_Slip"))
					{
						jumpLine_Slip = Integer.parseInt(cutName[1].trim());
					}
				}
			}
			br.close();
		}
		catch (Exception e)
		{
			PosLog.getLog(getClass()).debug(e);
			e.printStackTrace();
		}
	}

	public boolean isValid()
	{
		if (printer == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public boolean open()
	{
		if (printer == null) { return false; }

		connect = printer.open();
		
		return connect;
	}

	public boolean getStatus()
	{
		if (connect && enable)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public void close()
	{
		if (connect)
		{
			setEnable(false);

			printer.close();

			connect = false;
		}
	}

	public void setEnable(boolean enable)
	{
		if (connect)
		{
			if (this.enable != enable)
			{
				printer.setEnable(enable);
			}

			this.enable = enable;
		}
	}

	public void setPagePrint_Normal(boolean flag, int row)
	{
		// 是否分页打印
		pagePrint_Normal = flag;

		// 每页行数
		pageRow_Normal = row;
		if (pageRow_Normal <= 0)
			pageRow_Normal = 1;

		// 缺省打印区域为整页
		areaStart_Normal = 1;
		areaEnd_Normal = pageRow_Normal;

		checkPrinterStatus(true);
	}

	public void setPagePrint_Journal(boolean flag, int row)
	{
		// 是否分页打印
		pagePrint_Journal = flag;

		// 每页行数
		pageRow_Journal = row;
		if (pageRow_Journal <= 0)
			pageRow_Journal = 1;

		// 缺省打印区域为整页
		areaStart_Journal = 1;
		areaEnd_Journal = pageRow_Journal;

		checkPrinterStatus(true);
	}
	
	public void setEnableCutPaper(boolean flag)
	{
		this.iscutpaper = flag;
	}

	public void setPagePrint_Slip(boolean flag, int row)
	{
		// 是否分页打印
		pagePrint_Slip = flag;

		// 每页行数
		pageRow_Slip = row;
		if (pageRow_Slip <= 0)
			pageRow_Slip = 1;

		// 缺省打印区域为整页
		areaStart_Slip = 1;
		areaEnd_Slip = pageRow_Slip;

		checkPrinterStatus(true);
	}

	public void setPrintArea_Normal(int start, int end)
	{
		areaStart_Normal = start;
		areaEnd_Normal = end;
	}

	public void setPrintArea_Journal(int start, int end)
	{
		areaStart_Journal = start;
		areaEnd_Journal = end;
	}

	public void setPrintArea_Slip(int start, int end)
	{
		areaStart_Slip = start;
		areaEnd_Slip = end;
	}

	public int getCurRow_Normal()
	{
		return curRow_Normal;
	}

	public int getCurRow_Journal()
	{
		return curRow_Journal;
	}

	public int getCurRow_Slip()
	{
		return curRow_Slip;
	}

	public int getPageNum_Normal()
	{
		return pageNum_Normal;
	}

	public int getPageNum_Journal()
	{
		return pageNum_Journal;
	}

	public int getPageNum_Slip()
	{
		return pageNum_Slip;
	}

	public void startPrint_Normal()
	{
		//
		numRow_Normal = 0;
		curRow_Normal = 1;
		pageNum_Normal = 1;

		//
		setPagePrint_Normal(false, 1);
	}

	public void startPrint_Journal()
	{
		//
		numRow_Journal = 0;
		curRow_Journal = 1;
		pageNum_Journal = 1;

		//
		setPagePrint_Journal(false, 1);
	}

	public void startPrint_Slip()
	{
		//
		numRow_Slip = 0;
		curRow_Slip = 1;
		pageNum_Slip = 1;

		//
		setPagePrint_Slip(false, 1);
	}

	public void cutPaper_Normal()
	{
		if (connect && enable)
		{
			// 分页打印时走纸到页尾
			if (pagePrint_Normal)
			{
				if (!printer.passPage_Normal())
				{
					jumpTo_Normal(pageRow_Normal);
				}
			}

			System.err.println("CutNormal : " + cutLine_Normal);
			// 切纸前走纸
			for (int i = 0; i < cutLine_Normal; i++)
			{
				printer.printLine_Normal("\n");
			}

			// 切纸
			if(this.iscutpaper)
				printer.cutPaper_Normal();

			// 恢复计数
			numRow_Normal = 0;
			curRow_Normal = 1;
			pageNum_Normal = 1;

			// 发票页加1
			if (haveSaleFphmCfg())
			{
				int n = Convert.toInt(getSaleFphmAttr(InvoiceNum)) + 1;
				setSaleFphmAttr(InvoiceNum, String.valueOf(n));
				setSaleFphmAttr(InvoiceName, "个人");
			}
		}
		checkPrinterStatus(false);
	}

	protected void checkPrinterStatus(boolean flag)
	{
		if (!ConfigClass.MultiInstanceMode.equals("Y"))
			return;
			
		if (GlobalInfo.sysPara == null || GlobalInfo.sysPara.disablePrinterCounter == null || GlobalInfo.sysPara.disablePrinterCounter.equals(""))
			return;
			
		// flag == true,则开始打印时检查打印机是否开启
		String syjhid = "," + GlobalInfo.sysPara.disablePrinterCounter + ",";
		
		if (syjhid.indexOf("," + GlobalInfo.syjDef.syjh + ",") == -1)
			return;

		if (flag)
		{
			if (Printer.getDefault() != null && !Printer.getDefault().getStatus())
			{
				Printer.getDefault().setEnable(Printer.getDefault().open());
			}
		}
		else
		{
			if (Printer.getDefault() != null && Printer.getDefault().getStatus())
			{
				Printer.getDefault().close();
			}
		}
	}

	public void cutPaper_Journal()
	{
		if (connect && enable)
		{
			// 分页打印时走纸到页尾
			if (pagePrint_Journal)
			{
				if (!printer.passPage_Journal())
				{
					jumpTo_Journal(pageRow_Journal);
				}
			}

			// 切纸前走纸
			for (int i = 0; i < cutLine_Journal; i++)
			{
				printer.printLine_Journal("\n");
			}

			// 切纸
			printer.cutPaper_Journal();

			// 恢复计数
			numRow_Journal = 0;
			curRow_Journal = 1;
			pageNum_Journal = 1;
		}
		checkPrinterStatus(false);
	}

	public void cutPaper_Slip()
	{
		if (connect && enable)
		{
			// 分页打印时走纸到页尾
			if (pagePrint_Slip)
			{
				if (!printer.passPage_Slip())
				{
					jumpTo_Slip(pageRow_Slip);
				}
			}

			// 切纸前走纸
			for (int i = 0; i < cutLine_Slip; i++)
			{
				printer.printLine_Slip("\n");
			}

			// 切纸
			printer.cutPaper_Slip();

			// 恢复计数
			numRow_Slip = 0;
			curRow_Slip = 1;
			pageNum_Slip = 1;
		}
		checkPrinterStatus(false);
	}

	public void printLine_Normal(String printStr)
	{
		if (printStr == null)
			return;

		if (connect && enable)
		{
			// 分页套打打印时，当前打印行不在打印区域内，走纸到下一页的打印区域
			if (pagePrint_Normal)
			{
				if ((curRow_Normal < areaStart_Normal) || (curRow_Normal > areaEnd_Normal))
				{
					jumpTo_Normal(areaStart_Normal);
				}
			}

			// 分页且非套打时，在每一页上打印Memo
			if (isPrintPageHead == false && curRow_Normal == areaStart_Normal && pageNum_Normal > 1)
			{
				isPrintPageHead = true;
				try
				{
					SaleBillMode.getDefault().printPageHead();
				}
				catch (Exception ex)
				{
					PosLog.getLog(getClass()).debug(ex);
					ex.printStackTrace();
				}
				finally
				{
					isPrintPageHead = false;
				}

			}

			// 去掉尾部空格
			printStr = ManipulateStr.trimRight(printStr);

			// 添加尾部换行
			if (printStr.length() <= 0)
			{
				printStr += "\n";
			}
			else if (printStr.charAt(printStr.length() - 1) != '\n')
			{
				printStr += "\n";
			}

			// 打印到打印机
			printer.printLine_Normal(printStr);

			// 打印行计数
			curRow_Normal++;

			// 打印延迟
			numRow_Normal++;
			if (GlobalInfo.sysPara != null && GlobalInfo.sysPara.printdelayline > 0 && GlobalInfo.sysPara.printdelaysec > 0 && numRow_Normal >= GlobalInfo.sysPara.printdelayline)
			{
				numRow_Normal = 0;
				try
				{
					Thread.sleep(GlobalInfo.sysPara.printdelaysec);
				}
				catch (Exception e)
				{
					PosLog.getLog(getClass()).debug(e);
				}
			}
		}
	}

	public void printLine_Journal(String printStr)
	{
		if (printStr == null)
			return;

		if (connect && enable)
		{
			// 分页套打打印时，当前打印行不在打印区域内，走纸到下一页的打印区域
			if (pagePrint_Journal)
			{
				if ((curRow_Journal < areaStart_Journal) || (curRow_Journal > areaEnd_Journal))
				{
					jumpTo_Journal(areaStart_Journal);
				}
			}

			// 去掉尾部空格
			printStr = ManipulateStr.trimRight(printStr);

			// 添加尾部换行
			if (printStr.length() <= 0)
			{
				printStr += "\n";
			}
			else if (printStr.charAt(printStr.length() - 1) != '\n')
			{
				printStr += "\n";
			}

			// 打印到打印机
			printer.printLine_Journal(printStr);

			// 打印行计数
			curRow_Journal++;

			// 打印延迟
			numRow_Journal++;
			if (GlobalInfo.sysPara.printdelayline > 0 && GlobalInfo.sysPara.printdelaysec > 0 && numRow_Journal >= GlobalInfo.sysPara.printdelayline)
			{
				numRow_Journal = 0;
				try
				{
					Thread.sleep(GlobalInfo.sysPara.printdelaysec);
				}
				catch (Exception e)
				{
				}
			}
		}
	}

	public void printLine_Slip(String printStr)
	{
		if (printStr == null)
			return;

		if (connect && enable)
		{
			// 分页套打打印时，当前打印行不在打印区域内，走纸到下一页的打印区域
			if (pagePrint_Slip)
			{
				if ((curRow_Slip < areaStart_Slip) || (curRow_Slip > areaEnd_Slip))
				{
					jumpTo_Slip(areaStart_Slip);
				}
			}

			// 去掉尾部空格
			printStr = ManipulateStr.trimRight(printStr);

			// 添加尾部换行
			if (printStr.length() <= 0)
			{
				printStr += "\n";
			}
			else if (printStr.charAt(printStr.length() - 1) != '\n')
			{
				printStr += "\n";
			}

			// 打印到打印机
			printer.printLine_Slip(printStr);

			// 打印行计数
			curRow_Slip++;

			// 打印延迟
			numRow_Slip++;
			if (GlobalInfo.sysPara.printdelayline > 0 && GlobalInfo.sysPara.printdelaysec > 0 && numRow_Slip >= GlobalInfo.sysPara.printdelayline)
			{
				numRow_Slip = 0;
				try
				{
					Thread.sleep(GlobalInfo.sysPara.printdelaysec);
				}
				catch (Exception e)
				{
				}
			}
		}
	}

	public void setEmptyMsg_Slip(String msg)
	{
		if (connect && enable)
		{
			printer.setEmptyMsg_Slip(msg);
		}
	}

	protected void jumpTo_Normal(int rowNo)
	{
		if (pagePrint_Normal)
		{
			int PassRow = 0;

			// 如果要走纸的行小于当前打印行，则需要走纸到下一页
			if (rowNo < curRow_Normal)
			{
				// 走纸到下页，适用于黑标走纸，否则打印空行走纸到下一页
				if (printer.passPage_Normal())
				{
					// 重新开始记行数
					curRow_Normal = 1;

					// 页数加一
					pageNum_Normal++;

					// 发票页加1
					if (haveSaleFphmCfg())
					{
						int n = Convert.toInt(getSaleFphmAttr(InvoiceNum)) + 1;
						setSaleFphmAttr(InvoiceNum, String.valueOf(n));
						setSaleFphmAttr(InvoiceName, "个人");
					}

					for (int i = 0; i < jumpLine_Normal; i++)
					{
						printer.printLine_Normal("\n");
					}
				}
			}

			// 计算从当前打印行到目标行的行数
			if (rowNo < curRow_Normal)
			{
				PassRow = pageRow_Normal - curRow_Normal + rowNo;

				// 页数加一
				pageNum_Normal++;

				// 发票页加1
				if (haveSaleFphmCfg())
				{
					int n = Convert.toInt(getSaleFphmAttr(InvoiceNum)) + 1;
					setSaleFphmAttr(InvoiceNum, String.valueOf(n));
				}
			}
			else
			{
				PassRow = rowNo - curRow_Normal + 1;// add 1
			}

			// 打印空行走纸到目标行
			for (int i = 0; i < PassRow; i++)
			{
				printer.printLine_Normal("\n");
				curRow_Normal++;
			}

			// 标记当前行以页为起始
			while (curRow_Normal > pageRow_Normal)
			{
				curRow_Normal = curRow_Normal - pageRow_Normal;
			}
		}
	}

	protected void jumpTo_Journal(int rowNo)
	{
		if (pagePrint_Journal)
		{
			int PassRow = 0;

			// 如果要走纸的行小于当前打印行，则需要走纸到下一页
			if (rowNo < curRow_Journal)
			{
				// 走纸到下页，适用于黑标走纸，否则打印空行走纸到下一页
				if (printer.passPage_Journal())
				{
					// 重新开始记行数
					curRow_Journal = 1;

					// 页数加一
					pageNum_Journal++;

					for (int i = 0; i < jumpLine_Journal; i++)
					{
						printer.printLine_Normal("\n");
					}
				}
			}

			// 计算从当前打印行到目标行的行数
			if (rowNo < curRow_Journal)
			{
				PassRow = pageRow_Journal - curRow_Journal + rowNo;

				// 页数加一
				pageNum_Journal++;
			}
			else
			{
				PassRow = rowNo - curRow_Journal;
			}

			// 打印空行走纸到目标行
			for (int i = 0; i < PassRow; i++)
			{
				printer.printLine_Journal("\n");
				curRow_Journal++;
			}

			// 标记当前行以页为起始
			while (curRow_Journal > pageRow_Journal)
			{
				curRow_Journal = curRow_Journal - pageRow_Journal;
			}
		}
	}

	protected void jumpTo_Slip(int rowNo)
	{
		if (pagePrint_Slip)
		{
			int PassRow = 0;

			// 如果要走纸的行小于当前打印行，则需要走纸到下一页
			if (rowNo < curRow_Slip)
			{
				// 走纸到下页，适用于黑标走纸，否则打印空行走纸到下一页
				if (printer.passPage_Slip())
				{
					// 重新开始记行数
					curRow_Slip = 1;

					// 页数加一
					pageNum_Slip++;

					for (int i = 0; i < jumpLine_Slip; i++)
					{
						printer.printLine_Normal("\n");
					}
				}
			}

			// 计算从当前打印行到目标行的行数
			if (rowNo < curRow_Slip)
			{
				PassRow = pageRow_Slip - curRow_Slip + rowNo;

				// 页数加一
				pageNum_Slip++;
			}
			else
			{
				PassRow = rowNo - curRow_Slip;
			}

			// 打印空行走纸到目标行
			for (int i = 0; i < PassRow; i++)
			{
				printer.printLine_Slip("\n");
				curRow_Slip++;
			}

			// 标记当前行以页为起始
			while (curRow_Slip > pageRow_Slip)
			{
				curRow_Slip = curRow_Slip - pageRow_Slip;
			}
		}
	}

	public void enableRealPrintMode(boolean flag)
	{
		if (connect && enable)
		{
			printer.enableRealPrintMode(flag);
		}
	}

	private boolean InvoicePrintGrant()
	{
		if (GlobalInfo.posLogin.operrange == 'Y')
			return true;

		OperUserDef staff = DataService.getDefault().personGrant();
		if (staff == null)
			return false;
		if (staff.operrange == 'Y')
			return true;
		else
			return false;
	}

	public void inputSaleFphm()
	{
		if (!haveSaleFphmCfg())
			return;

		StringBuffer fphm = new StringBuffer();
		StringBuffer fpzs = new StringBuffer();

		int ret = new MessageBox(Language.apply("请选择发票处理功能？\n\n1 - 新上一卷发票\n2 - 调整发票号码\n3 - 打印发票汇总\n4 - 重设剩余张数\n5 - 设置发票抬头"), null, false).verify();
		if (ret != GlobalVar.Key1 && ret != GlobalVar.Key2 && ret != GlobalVar.Key3 && ret != GlobalVar.Key4 && ret != GlobalVar.Key5)
			return;

		String grant = getSaleFphmAttr(InvoicePrintGrant);
		if (ret == GlobalVar.Key1)
		{
			if (grant != null && grant.indexOf("1") >= 0 && !InvoicePrintGrant())
				return;

			fpzs.append(String.valueOf(Convert.toLong(getSaleFphmAttr(InvoicePage))));
			if (!new MutiInputForm().open(Language.apply("请输入新发票的起始发票号和本卷发票张数"), Language.apply("起始号码"), fphm, Language.apply("发票张数"), fpzs, TextBox.IntegerInput)) { return; }

			ManipulateDateTime mdt = new ManipulateDateTime();

			// 初始化一卷新发票联，最后一次性写盘
			setSaleFphmAttr(InvoiceStart, fphm.toString(), false);
			setSaleFphmAttr(InvoiceCount, fpzs.toString(), false);
			setSaleFphmAttr(InvoiceStartDate, mdt.getDateBySlash() + " " + mdt.getTime(), false);
			setSaleFphmAttr(InvoiceNum, "0", false);
			setSaleFphmAttr(InvoiceName, "个人", false);
			setSaleFphmAttr(InvoiceXsZs, "0", false);
			setSaleFphmAttr(InvoiceXsJe, "0", false);
			setSaleFphmAttr(InvoiceXskpJe, "0", false);
			setSaleFphmAttr(InvoiceThZs, "0", false);
			setSaleFphmAttr(InvoiceThJe, "0", false);
			setSaleFphmAttr(InvoiceThkpJe, "0", false);
			setSaleFphmAttr(InvoiceHcZs, "0", false);
			setSaleFphmAttr(InvoiceHcJe, "0", false);
			setSaleFphmAttr(InvoiceHckpJe, "0", true);

			salefpcomplate = false;
			new MessageBox(Language.apply("新一卷发票设置已生效!\n\n起始号为 ") + fphm.toString() + Language.apply(",共 ") + fpzs.toString() + Language.apply(" 张"));
		}
		else if (ret == GlobalVar.Key2)
		{
			if (grant != null && grant.indexOf("2") >= 0 && !InvoicePrintGrant())
				return;

			String curfph = String.valueOf(Convert.toLong(getSaleFphmAttr(InvoiceStart)) + Convert.toLong(getSaleFphmAttr(InvoiceNum)));
			fphm.append(curfph);
			StringBuffer sb = new StringBuffer();
			sb.append(Language.apply("本卷发票起始号码是 "));
			sb.append(getSaleFphmAttr(InvoiceStart));
			sb.append(Language.apply(",总共 "));
			sb.append(getSaleFphmAttr(InvoiceCount));
			sb.append(Language.apply(" 张\n\n当前发票号已打印到 "));
			sb.append(curfph);
			sb.append(Language.apply(",已打 "));
			sb.append(getSaleFphmAttr(InvoiceNum) + Language.apply(" 张"));
			long num = 0;
			while (true)
			{
				if (!new TextBox().open(Language.apply("请输入调整后的当前发票号码"), Language.apply("发票号码"), sb.toString(), fphm, TextBox.IntegerInput)) { return; }

				num = Convert.toLong(fphm.toString()) - Convert.toLong(getSaleFphmAttr(InvoiceStart));
				if (num >= Convert.toLong(getSaleFphmAttr(InvoiceCount)) || num < 0)
				{
					new MessageBox(Language.apply("调整的当前发票号不是本卷发票内的号码"));
					continue;
				}
				if (Convert.toLong(fphm.toString()) - Convert.toLong(curfph) < 0)
				{
					if (new MessageBox(Language.apply("调整的当前发票号比已打印的发票号码小\n\n你确定要设置吗？"), null, true).verify() != GlobalVar.Key1)
					{
						continue;
					}
				}
				break;
			}
			setSaleFphmAttr(InvoiceNum, String.valueOf(num));

			new MessageBox(Language.apply("发票号码调整已生效!\n\n本卷发票起始号为 ") + getSaleFphmAttr(InvoiceStart) + Language.apply(",总共 ") + getSaleFphmAttr(InvoiceCount) + Language.apply(" 张\n当前发票已打印到 ") + fphm.toString() + Language.apply(",剩余 ") + String.valueOf(Convert.toLong(getSaleFphmAttr(InvoiceCount)) - Convert.toLong(getSaleFphmAttr(InvoiceNum)) + Language.apply(" 张")));
		}
		else if (ret == GlobalVar.Key3)
		{
			if (new MessageBox(Language.apply("你是要打印发票联汇总吗？"), null, true).verify() == GlobalVar.Key1)
			{
				if (grant != null && grant.indexOf("3") >= 0 && !InvoicePrintGrant())
					return;

				printSaleFphmSummary();
			}
		}
		else if (ret == GlobalVar.Key4)
		{
			if (grant != null && grant.indexOf("4") >= 0 && !InvoicePrintGrant())
				return;

			fphm.append(String.valueOf(Convert.toLong(getSaleFphmAttr(InvoiceStart)) + Convert.toLong(getSaleFphmAttr(InvoiceNum))));
			long zs = Convert.toLong(getSaleFphmAttr(InvoiceCount)) - Convert.toLong(getSaleFphmAttr(InvoiceNum));
			if (zs < 0)
				zs = 0;
			fpzs.append(String.valueOf(zs));
			if (!new MutiInputForm().open(Language.apply("输入当前发票号和剩余张数,保留发票汇总"), Language.apply("当前号码"), fphm, Language.apply("剩余发票"), fpzs, TextBox.IntegerInput)) { return; }

			// 初始化一卷新发票联，保留发票汇总，最后一次性写盘
			setSaleFphmAttr(InvoiceStart, fphm.toString(), false);
			setSaleFphmAttr(InvoiceCount, fpzs.toString(), false);
			setSaleFphmAttr(InvoiceNum, "0", true);

			new MessageBox(Language.apply("当前发票号设置已生效!\n\n当前发票号为 ") + fphm.toString() + Language.apply(",剩余 ") + fpzs.toString() + Language.apply(" 张"));
		}
		else if (ret == GlobalVar.Key5)
		{
			StringBuffer cardno = new StringBuffer(getSaleFphmAttr("InvoiceName"));
			
//			 输入发票抬头
			TextBox txt = new TextBox();
			if (!txt.open(Language.apply("请输入所开发票抬头"), Language.apply("发票"), Language.apply("请输入所开发票抬头，从文本框中刷入"), cardno, 0, 0, false, -1)) { return; }

			if(cardno.toString().equals("0"))cardno =  new StringBuffer();
			
			setSaleFphmAttr(InvoiceName,cardno.toString().replace("-","").trim(), true);

		}
	}

	public String getSaleFphmAttr(String attr)
	{
		for (int i = 0; salefpdata != null && i < salefpdata.size(); i++)
		{
			String[] s = (String[]) salefpdata.elementAt(i);
			if (attr.equalsIgnoreCase(s[0])) { return s[1]; }
		}
		return null;
	}

	protected void setSaleFphmAttr(String attr, String value)
	{
		setSaleFphmAttr(attr, value, true);
	}

	protected void setSaleFphmAttr(String attr, String value, boolean save)
	{
		boolean have = false;
		for (int i = 0; salefpdata != null && i < salefpdata.size(); i++)
		{
			String[] s = (String[]) salefpdata.elementAt(i);
			if (attr.equalsIgnoreCase(s[0]))
			{
				s[1] = value;
				have = true;
				break;
			}
		}
		if (!have)
		{
			if (salefpdata == null)
				salefpdata = new Vector();
			salefpdata.add(new String[] { attr, value });
		}
		if (save)
		{
			CommonMethod.writeFileByVector(GlobalVar.ConfigPath + "/SaleFphm.ini", salefpdata);
		}
	}

	public boolean haveSaleFphmCfg()
	{
		for (int i = 0; salefpdata != null && i < salefpdata.size(); i++)
		{
			String[] s = (String[]) salefpdata.elementAt(i);
			if (InvoicePage.equalsIgnoreCase(s[0]))
			{
				if (Convert.toInt(s[1]) > 0)
					return true;
				else
					return false;
			}
		}
		return false;
	}

	public long getCurrentSaleFphm()
	{
		if (!haveSaleFphmCfg())
			return 0;

		return Convert.toLong(getSaleFphmAttr(InvoiceStart)) + Convert.toLong(getSaleFphmAttr(InvoiceNum));
	}
	
	public String getCurrentSaleFpName()
	{
		if (!haveSaleFphmCfg())
			return "";

		return String.valueOf(getSaleFphmAttr("InvoiceName"));
	}

	public boolean getSaleFphmComplate()
	{
		if (!haveSaleFphmCfg())
			return false;

		if (salefpcomplate && new MessageBox(Language.apply("发票已经打印完毕,你还没有重设新发票的起始号码\n\n你确定继续交易吗？"), null, true).verify() == GlobalVar.Key1)
		{
			salefpcomplate = false;
		}

		return salefpcomplate;
	}

	public void saveSaleFphmSummary(String djlb, int zs, double je, double kpje)
	{
		if (!haveSaleFphmCfg())
			return;

		if (SellType.ISHC(djlb))
		{
			setSaleFphmAttr(InvoiceHcZs, String.valueOf(Convert.toInt(getSaleFphmAttr(InvoiceHcZs)) + zs), false);
			setSaleFphmAttr(InvoiceHcJe, String.valueOf(Convert.toDouble(getSaleFphmAttr(InvoiceHcJe)) + je), false);
			setSaleFphmAttr(InvoiceHckpJe, String.valueOf(Convert.toDouble(getSaleFphmAttr(InvoiceHckpJe)) + kpje), true);
		}
		else if (SellType.ISBACK(djlb))
		{
			setSaleFphmAttr(InvoiceThZs, String.valueOf(Convert.toInt(getSaleFphmAttr(InvoiceThZs)) + zs), false);
			setSaleFphmAttr(InvoiceThJe, String.valueOf(Convert.toDouble(getSaleFphmAttr(InvoiceThJe)) + je), false);
			setSaleFphmAttr(InvoiceThkpJe, String.valueOf(Convert.toDouble(getSaleFphmAttr(InvoiceThkpJe)) + kpje), true);
		}
		else
		{
			setSaleFphmAttr(InvoiceXsZs, String.valueOf(Convert.toInt(getSaleFphmAttr(InvoiceXsZs)) + zs), false);
			setSaleFphmAttr(InvoiceXsJe, String.valueOf(Convert.toDouble(getSaleFphmAttr(InvoiceXsJe)) + je), false);
			setSaleFphmAttr(InvoiceXskpJe, String.valueOf(Convert.toDouble(getSaleFphmAttr(InvoiceXskpJe)) + kpje), true);
		}
		
		setSaleFphmAttr(InvoiceName, String.valueOf(getSaleFphmAttr(InvoiceName)), true);

		// 本卷发票已打印完,打印汇总
		if (Convert.toInt(getSaleFphmAttr(InvoiceNum)) >= Convert.toInt(getSaleFphmAttr(InvoiceCount)))
		{
			if (getSaleFphmAttr(InvoicePrintSummary) == null || "Y".equalsIgnoreCase(getSaleFphmAttr(InvoicePrintSummary)))
			{
				printSaleFphmSummary();
			}

			// 提醒换发票
			salefpcomplate = true;
			new MessageBox(Language.apply("本卷发票打印完毕,请重新更换一卷新发票"));
		}
	}

	protected void printSaleFphmSummary()
	{
		if (!haveSaleFphmCfg())
			return;

		if (PathFile.isPathExists(GlobalVar.ConfigPath + "//InvoiceSummaryMode.ini"))
		{
			InvoiceSummaryMode.getDefault().setTemplateObject(salefpdata);
			InvoiceSummaryMode.getDefault().printBill();
		}
		else
		{
			printLine_Normal(Language.apply("============= 发票联汇总 ============"));
			printLine_Normal(Language.apply("销售发票:") + ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr(InvoiceXsZs)), 0, 1, false, 9) + Language.apply(" 张"));
			printLine_Normal(Language.apply("销售金额:") + ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr(InvoiceXsJe)), 2, 1, false, 9) + Language.apply(" 开票金额:") + ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr(InvoiceXskpJe)), 2, 1, false, 9));
			printLine_Normal(Language.apply("退货发票:") + ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr(InvoiceThZs)), 0, 1, false, 9) + Language.apply(" 张"));
			printLine_Normal(Language.apply("退货金额:") + ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr(InvoiceThJe)), 2, 1, false, 9) + Language.apply(" 开票金额:") + ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr(InvoiceThkpJe)), 2, 1, false, 9));
			printLine_Normal(Language.apply("红冲发票:") + ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr(InvoiceHcZs)), 0, 1, false, 9) + Language.apply(" 张"));
			printLine_Normal(Language.apply("红冲金额:") + ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr(InvoiceHcJe)), 2, 1, false, 9) + Language.apply(" 开票金额:") + ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr(InvoiceHckpJe)), 2, 1, false, 9));

			if (getSaleFphmAttr(InvoiceStart) != null)
			{
				printLine_Normal(Language.apply("发票起始:") + getSaleFphmAttr(InvoiceStart));
			}
			if (getSaleFphmAttr(InvoiceCount) != null)
			{
				printLine_Normal(Language.apply("发票总数:") + ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr(InvoiceCount)), 0, 1, false, 0));
			}
			if (getSaleFphmAttr(InvoiceStartDate) != null)
			{
				printLine_Normal(Language.apply("开始时间:") + getSaleFphmAttr(InvoiceStartDate));
			}
			if (getSaleFphmAttr(InvoiceStartDate) != null)
			{
				if (Convert.toInt(getSaleFphmAttr(InvoiceNum)) >= Convert.toInt(getSaleFphmAttr(InvoiceCount)))
				{
					ManipulateDateTime mdt = new ManipulateDateTime();
					printLine_Normal(Language.apply("结束时间:") + mdt.getDateBySlash() + " " + mdt.getTime());
				}
			}

			int qtzs = Convert.toInt(getSaleFphmAttr(InvoiceCount));
			qtzs -= Convert.toInt(getSaleFphmAttr(InvoiceXsZs));
			qtzs -= Convert.toInt(getSaleFphmAttr(InvoiceThZs));
			qtzs -= Convert.toInt(getSaleFphmAttr(InvoiceHcZs));
			if (qtzs > 0)
			{
				printLine_Normal(Language.apply("作废发票:") + ManipulatePrecision.doubleToString(qtzs, 0, 1, false, 9) + Language.apply(" 张 \n"));
			}

			// 切纸
			cutPaper_Normal();
		}
	}
}
