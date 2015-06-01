package com.efuture.javaPos.PrintTemplate;

import java.io.File;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;

public class StoredCardStatisticsMode extends PrintTemplate
{
	protected static StoredCardStatisticsMode storedCardStatisticsMode = null;

	String payname = "", paycode = ""; // 报表标题
	Vector detail = null;// 面值卡明细
	String bs, ske;// 收款笔数、收款金额
	String outje, inje;// 零钱包支出，存入金额

	protected final int SCSM_text = 0;// 标题
	protected final int SCSM_date = 1;// 日期
	protected final int SCSM_bc = 2;// 班次
	protected final int SCSM_syyh = 3;// 收银员号
	protected final int SCSM_syjh = 4;// 收银机号
	protected final int SCSM_kh = 5;// 储值卡号
	protected final int SCSM_kje = 6;// 卡金额
	protected final int SCSM_kye = 7;// 卡余额
	protected final int SCSM_skbs = 8;// 储值卡收款笔数
	protected final int SCSM_ske = 9;// 储值卡收款额
	protected final int SCSM_printDate = 10;// 打印日期时间
											// ManipulateDateTime.getCurrentDateTime();
	protected final int SCSM_cbc = 11;// 中文班次

	protected final int SCSM_lczcoutje = 12;// 零钱包支出金额
	protected final int SCSM_lczcinje = 13;// 零钱包存入金额

	protected final int SCSM_paytitle = 14;// 自动生成相应付款名称打印标题
	protected final int SCSM_paybs = 15;// 自动生成相应付款名称笔数
	protected final int SCSM_payje = 16;// 自动生成相应付款名称合计金额
	
	protected final int SCSM_payoutje = 17;// 自动生成相应付款名称支出标题
	protected final int SCSM_payinje = 18;// 自动生成相应付款名称存入标题

	public static StoredCardStatisticsMode getDefault()
	{
		if (StoredCardStatisticsMode.storedCardStatisticsMode == null)
		{
			StoredCardStatisticsMode.storedCardStatisticsMode = CustomLocalize.getDefault().createStoredCardStatisticsMode();
		}

		return StoredCardStatisticsMode.storedCardStatisticsMode;
	}

	public boolean checkTemplateFile()
	{
		File file = new File(GlobalVar.ConfigPath + "//StoredCardStatisticsMode.ini");
		if (!file.exists())
			return false;

		return true;
	}

	public boolean ReadTemplateFile()
	{
		File file = new File(GlobalVar.ConfigPath + "//StoredCardStatisticsMode.ini");
		if (!file.exists())
			return false;

		super.InitTemplate();

		return super.ReadTemplateFile(Title, GlobalVar.ConfigPath + "//StoredCardStatisticsMode.ini");
	}

	public void setTemplateObject(String[] pay, Vector h, int bs, double ske, double outje, double inje)
	{
		this.payname = pay[0];
		this.paycode = pay[1];

		detail = h;
		this.bs = String.valueOf(bs);
		this.ske = ManipulatePrecision.doubleToString(ske);

		this.outje = ManipulatePrecision.doubleToString(outje);
		this.inje = ManipulatePrecision.doubleToString(inje);
	}

	public String getItemDataString(PrintTemplateItem item, int index)
	{
		String line = null;
		String[] record;

		line = extendCase(item, index);
		if (line == null)
		{
			switch (Integer.parseInt(item.code))
			{
				case SCSM_text: // 文本
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

				case SCSM_date:
					if (detail.size() > 0)
						record = (String[]) detail.elementAt(index);
					else
						record = new String[] { "", "", "", "", "", "", "" };
					line = record[0];
					break;

				case SCSM_bc:
					if (detail.size() > 0)
						record = (String[]) detail.elementAt(index);
					else
						record = new String[] { "", "", "", "", "", "", "" };
					line = record[1];
					break;

				case SCSM_cbc:
					if (detail.size() > 0)
						record = (String[]) detail.elementAt(index);
					else
						record = new String[] { "", "", "", "", "", "", "" };
					line = record[1] == "" ? "" : DataService.getDefault().getTimeNameByCode(record[1].charAt(0));
					break;

				case SCSM_syyh:
					if (detail.size() > 0)
						record = (String[]) detail.elementAt(index);
					else
						record = new String[] { "", "", "", "", "", "", "" };
					line = record[2];
					break;

				case SCSM_syjh:
					if (detail.size() > 0)
						record = (String[]) detail.elementAt(index);
					else
						record = new String[] { "", "", "", "", "", "", "" };
					line = record[3];
					break;

				case SCSM_kh:
					if (detail.size() > 0)
						record = (String[]) detail.elementAt(index);
					else
						record = new String[] { "", "", "", "", "", "", "" };
					line = record[4];
					break;

				case SCSM_kje:
					if (detail.size() > 0)
						record = (String[]) detail.elementAt(index);
					else
						record = new String[] { "", "", "", "", "", "", "" };
					line = record[5];
					break;

				case SCSM_kye:
					if (detail.size() > 0)
						record = (String[]) detail.elementAt(index);
					else
						record = new String[] { "", "", "", "", "", "", "" };
					line = record[6];
					break;

				case SCSM_skbs:
					line = bs;
					break;

				case SCSM_ske:
					line = ske;
					break;
					
				case SCSM_paytitle:
					line = payname+Language.apply("收款统计");
				break;
				
				case SCSM_paybs:
					line = payname+Language.apply("交易笔数:");
				break;
				
				case SCSM_payje:
					line = payname+Language.apply("合计金额:");
				break;
				
				case SCSM_lczcoutje:
					if (!paycode.equals("0111"))
						line = "";
					else
						line = outje;
					break;

				case SCSM_lczcinje:
					if (!paycode.equals("0111"))
						line = "";
					else
						line = inje;
					break;

				case SCSM_payoutje:
					if (!paycode.equals("0111"))
						line = "";
					else
						line = payname + Language.apply("支出金额:");
					break;

				case SCSM_payinje:
					if (!paycode.equals("0111"))
						line = "";
					else
						line = payname + Language.apply("存入金额:");
					break;
					
				case SCSM_printDate:
					line = ManipulateDateTime.getCurrentDateTime();
					break;

				default:
					line = extendCase(item, index);
					break;
			}
		}

		if (line != null && Integer.parseInt(item.code) != 0 && item.text != null && !item.text.trim().equals(""))
		{
			int maxline = item.length - Convert.countLength(item.text);
			line = item.text + Convert.appendStringSize("", line, 0, maxline, maxline, item.alignment);
		}

		return line;
	}

	public void printDetail()
	{
		// 设置打印区域
		setPrintArea("Detail");

		// 循环打印明细
		for (int i = 0; i < detail.size(); i++)
		{
			printVector(getCollectDataString(Detail, i, Width));
		}
	}

	public void printHeader()
	{
		// 设置打印区域
		setPrintArea("Header");

		// 打印
		printVector(getCollectDataString(Header, 0, Width));
	}

	public void PrintStoredCardStatistics()
	{
		// 设置打印方式
		printSetPage();
		// 打印头部区域
		printHeader();
		// 打印明细区域
		printDetail();
		// 打印汇总区域
		printTotal();
		// 打印尾部区域
		printBottom();
		// 切纸
		printCutPaper();
	}
}
