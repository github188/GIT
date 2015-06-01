package com.efuture.javaPos.PrintTemplate;

import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;

public class MzkRechargeBillMode extends PrintTemplate
{

	protected static MzkRechargeBillMode mzkRechargeBillMode = null;
	protected MzkRequestDef mzkreq = null;
	protected MzkResultDef mzkret = null;
	private boolean isLoad = false;

	protected final static int MRBM_text = 0;
	protected final static int MRBM_mktname = 101;
	protected final static int MRBM_mktcode = 102;
	protected final static int MRBM_syjh = 103;
	protected final static int MRBM_syyh = 104;
	protected final static int MRBM_seqno = 105;

	protected final static int MRBM_cardno = 106;
	protected final static int MRBM_money = 107;
	protected final static int MRBM_datetime = 108;
	protected final static int MRBM_memo = 109;
	
	// 记录打印内容用于重打印
	public String pwName = null;
	public PrintWriter pw = null;
	public int printnum = 0; //记录打印次数


	public static MzkRechargeBillMode getDefault()
	{
		if (MzkRechargeBillMode.mzkRechargeBillMode == null)
		{
			MzkRechargeBillMode.mzkRechargeBillMode = CustomLocalize.getDefault().createMzkRechargeBillMode();
		}

		return MzkRechargeBillMode.mzkRechargeBillMode;
	}

	public boolean ReadTemplateFile()
	{
		if (!CommonMethod.isFileExist(GlobalVar.ConfigPath + "//MzkRechargePrintMode.ini"))
			return true;

		super.InitTemplate();

		isLoad = super.ReadTemplateFile(Title, GlobalVar.ConfigPath + "//MzkRechargePrintMode.ini");
		return isLoad;
	}

	public void setTemplateObject(MzkRequestDef req, MzkResultDef ret)
	{
		this.mzkreq = req;
		this.mzkret = ret;
		printnum = 0;
		// SaleBillMode.getDefault().setTemplateObject(h, s, p);
	}

	public String getItemDataString(PrintTemplateItem item, int index)
	{
		String line = null;
		line = extendCase(item, index);

		try
		{
			String text = item.text;

			if (line == null)
			{
				switch (Integer.parseInt(item.code))
				{
					case MRBM_text:
						if (text == null)
							line = "";
						else
							line = text;
						break;
						
					case MRBM_mktname:
						line = GlobalInfo.sysPara.mktname;
						break;
					case MRBM_mktcode:
						line = GlobalInfo.sysPara.mktcode;

					case MRBM_syjh:
						line = GlobalInfo.syjStatus.syjh;
						break;
					case MRBM_syyh:
						line = GlobalInfo.syjStatus.syyh;
						break;
					case MRBM_seqno:
						line = String.valueOf(mzkreq.seqno);
						break;
					case MRBM_cardno:
						line = mzkret.cardno;
						break;
					case MRBM_money:
						line = String.valueOf(mzkreq.je);
						break;
					case MRBM_datetime:
						line = ManipulateDateTime.getCurrentDate() + " " + ManipulateDateTime.getCurrentTime();
						break;
					case MRBM_memo:
						if (mzkreq.memo != null)line = mzkreq.memo;
						else line = "";
						break;
				}
			}

			if (line != null && Integer.parseInt(item.code) != 0 && item.text != null && !item.text.trim().equals(""))
			{
				// line = item.text + line;
				int maxline = item.length - Convert.countLength(item.text);
				line = item.text + Convert.appendStringSize("", line, 0, maxline, maxline, item.alignment);
			}

			return line;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public void setLoad(boolean isload)
	{
		this.isLoad = isload;
	}

	public boolean isLoad()
	{
		return isLoad;
	}

	public void printBill()
	{
		// 设置打印方式
		printSetPage();

		// 打印头部区域
		printHeader();

		// 打印明细区域
		printDetail();

		// 打印尾部区域
		printBottom();

		// 打印付款区域
		// printPay();

		// 打印汇总区域
		// printTotal();

		// 切纸
		printCutPaper();
	}
	
	public void printHeader()
	{
		try{
			if (pw == null && printnum <=0)
			{
				pwName = ManipulateDateTime.getCurrentTime();
				String date = ExpressionDeal.replace(GlobalInfo.balanceDate, "/", "");
				pw = CommonMethod.writeFileAppendGBK(ConfigClass.LocalDBPath + "Invoice\\"+date+"\\"+pwName.replaceAll(":", "")+".txt");
				
			}
		}catch(Exception er)
		{
			er.printStackTrace();
		}
		super.printHeader();
	}
	
	public void printCutPaper()
	{
		PrintWriter pw1 = null;
		try{
			
			if (pw != null)
			{
				pw.flush();
				pw.close();
				pw = null;
				printnum ++;
				String date = ExpressionDeal.replace(GlobalInfo.balanceDate, "/", "");
				
				// 记录为
				
				pw1 = CommonMethod.writeFileAppendGBK(ConfigClass.LocalDBPath + "Invoice\\"+date+"\\list.txt");
				pw1.println(mzkret.cardno+"   "+Convert.increaseCharForward(ManipulatePrecision.doubleToString(mzkreq.je), 9)+"  "+pwName);
				pw1.flush();
			}
		}catch(Exception er)
		{
			er.printStackTrace();
		}
		finally
		{
			if (pw1 != null)
			{
				pw1.close();
			}
		}
		super.printCutPaper();
	}
	
	protected void printVector(Vector v)
	{
		try{
			if (pw != null)
			{
				
				for (int i = 0; i < v.size(); i++)
				{
					pw.println(v.elementAt(i));
				}
			}
		}catch(Exception er)
		{
			er.printStackTrace();
		}
		
		super.printVector(v);
	}
	
	public void Reprint(Vector v)
	{
		//设置打印方式
		printSetPage();
		printVector(v);
		printCutPaper();
	}
}
