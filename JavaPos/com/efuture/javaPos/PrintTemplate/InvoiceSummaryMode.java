package com.efuture.javaPos.PrintTemplate;

import java.io.File;
import java.util.Vector;

import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.Language;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.commonKit.Convert;

public class InvoiceSummaryMode extends PrintTemplate
{
	protected static InvoiceSummaryMode invSumry = null;
	protected Vector sumData = null;
	
	protected String invSryValue;
	private long startPos,endPos;

	private final static int INVSRY_text = 0;
	
	private final static int INVSRY_qzhm = 1;	//起始号
	private final static int INVSRY_zzhm = 2;	//终止号
	
	private final static int INVSRY_xsje = 3;	//销售金额
	private final static int INVSRY_xskpje = 4;	//销售开票金额
	private final static int INVSRY_xsbs = 5;	//销售笔数
	
	private final static int INVSRY_thje = 6;	//退货金额
	private final static int INVSRY_thkpje =7; 	//退货开票金额
	private final static int INVSRY_thzs = 8;	//退货张数
	
	private final static int INVSRY_hcje = 9;	//红冲金额
	private final static int INVSRY_hckpje = 10;	//红冲开票金额
	private final static int INVSRY_hczs = 11;	//红冲张数
	
	private final static int INVSRY_fpje = 12;	
	private final static int INVSRY_fpzs = 13;	//废票张数
	
	private final static int INVSRY_startdate = 14; //起始时间
	private final static int INVSRY_enddate = 15;	//终止时间
	
	private final static int INVSRY_count = 16;	//总张数年

	
	public static InvoiceSummaryMode getDefault()
	{
		if (InvoiceSummaryMode.invSumry == null)
		{
			InvoiceSummaryMode.invSumry = CustomLocalize.getDefault().createInvoiceSummaryMode();
		}
		return InvoiceSummaryMode.invSumry;
	}

	public boolean ReadTemplateFile()
	{
		File file = new File(GlobalVar.ConfigPath + "//InvoiceSummaryMode.ini");
		if (!file.exists())
			return false;

		super.InitTemplate();

		return super.ReadTemplateFile(Title, GlobalVar.ConfigPath + "//InvoiceSummaryMode.ini");
	}

	public void setTemplateObject(Object templet)
	{
		this.sumData  = (Vector) templet;				
	}

	protected String getSaleFphmAttr(String attr)
	{
		for (int i = 0; sumData != null && i < sumData.size(); i++)
		{
			String[] s = (String[]) sumData.elementAt(i);
			if (attr.equalsIgnoreCase(s[0]))
			{
				return s[1];
			}
		}
		return null;
	}
	
	public void printBill()
	{
		printSetPage();

		printHeader();

		printCutPaper();

	}

	protected String getItemDataString(PrintTemplateItem item, int index)
	{
		String line = "";

		try
		{
			line = extendCase(item, index);
			
			if (line == null)
			{
				switch (Integer.parseInt(item.code))
				{
					case INVSRY_text:
    					if (item.text == null)
    					{
    						line = "";
    					}
    					else
    					{
    						if (item.text.trim().indexOf("calc|") == 0)
    						{
    							line = super.calString(item.text, index);
    						}
    						else
    						{
    							line = item.text;
    						}
    					}
    					break;
					case INVSRY_qzhm:	
						if ((sumData == null) || sumData.size()<=0)
							break;
						
						invSryValue = getSaleFphmAttr("InvoiceStart");
						startPos = Long.parseLong(invSryValue);
						line = invSryValue.trim();
						break;
					case INVSRY_zzhm:
						if ((sumData == null) || sumData.size()<=0)
							break;
						
						invSryValue = getSaleFphmAttr("InvoiceNum");
						endPos = startPos +  Long.parseLong(invSryValue) -1;
						line = String.valueOf(endPos).trim();
						break;
						
					case INVSRY_xsje:
						if ((sumData == null) || sumData.size()<=0)
							break;
						
						invSryValue = ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr("InvoiceXsJe")), 2, 1, false, 9);
						line = invSryValue.trim();
						
						break;
						
					case INVSRY_xskpje:
						if ((sumData == null) || sumData.size()<=0)
							break;
						
						invSryValue = ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr("InvoiceXskpJe")), 2, 1, false, 9);
						line = invSryValue.trim();
						
						break;
						
					case INVSRY_xsbs:
						if ((sumData == null) || sumData.size()<=0)
							break;
						invSryValue = ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr("InvoiceXsZs")), 0, 1, false, 9) + Language.apply(" 张");
						line = invSryValue.trim();
						break;
						
					case INVSRY_thje:
						if ((sumData == null) || sumData.size()<=0)
							break;
						invSryValue = ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr("InvoiceThJe")), 2, 1, false, 9);
						line = invSryValue.trim();
						break;
					case INVSRY_thkpje:
						if ((sumData == null) || sumData.size()<=0)
							break;
						
						invSryValue = ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr("InvoiceThkpJe")), 2, 1, false, 9);
						line = invSryValue.trim();
						
						break;
					case INVSRY_thzs:
						if ((sumData == null) || sumData.size()<=0)
							break;
						
						invSryValue = ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr("InvoiceThZs")), 0, 1, false, 9) + Language.apply(" 张");
						line = invSryValue.trim();
						break;
					case INVSRY_hcje:
						if ((sumData == null) || sumData.size()<=0)
							break;
						
						invSryValue = ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr("InvoiceHcJe")), 2, 1, false, 9);
						line = invSryValue.trim();
						break;
					case INVSRY_hckpje:
						if ((sumData == null) || sumData.size()<=0)
							break;
						
						invSryValue =ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr("InvoiceHckpJe")), 2, 1, false, 9);
						line = invSryValue.trim();
						
						break;
					case INVSRY_hczs:
						if ((sumData == null) || sumData.size()<=0)
							break;
						invSryValue = ManipulatePrecision.doubleToString(Convert.toDouble(getSaleFphmAttr("InvoiceHcZs")), 0, 1, false, 9) + Language.apply(" 张");
						line = invSryValue.trim();
						break;

					case INVSRY_enddate:
	                    ManipulateDateTime mdt = new ManipulateDateTime();
	                    line = mdt.getDateBySlash() + " " + mdt.getTime();
	                    
					case INVSRY_fpje:
						if ((sumData == null) || sumData.size()<=0)
							break;
					
						break;
					case INVSRY_fpzs:
						if ((sumData == null) || sumData.size()<=0)
							break;
						int qtzs = Convert.toInt(getSaleFphmAttr("InvoiceCount"));
						qtzs -= Convert.toInt(getSaleFphmAttr("InvoiceXsZs"));
						qtzs -= Convert.toInt(getSaleFphmAttr("InvoiceThZs"));
						qtzs -= Convert.toInt(getSaleFphmAttr("InvoiceHcZs"));
						if (qtzs > 0)
						{
							line = ManipulatePrecision.doubleToString(qtzs, 0, 1, false, 9).trim() + Language.apply(" 张");
						}
						else
						{
							line = "0"+Language.apply("张");
						}
						break;
					case INVSRY_startdate:
						if ((sumData == null) || sumData.size()<=0)
							break;
						invSryValue = getSaleFphmAttr("InvoiceStartDate");
						if (invSryValue != null)
						{
							line = invSryValue.trim();
						}
						break;
					case INVSRY_count:
						if ((sumData == null) || sumData.size()<=0)
							break;
						invSryValue = getSaleFphmAttr("InvoiceCount");
						if (invSryValue != null)
						{
							line = invSryValue.trim();
						}
						break;
					default:
						line = "";

				}
			}
			return line;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "";
		}
	}
}
