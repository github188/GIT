package com.efuture.javaPos.PrintTemplate;

import java.io.File;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class GiftBillMode extends PrintTemplate
{
	protected static GiftBillMode GiftMode = null;

	public SaleHeadDef salehead;
	public GiftGoodsDef gift;

	protected final int GBM_text = 0;
	protected final int GBM_code = 1;
	protected final int GBM_info = 2;
	protected final int GBM_sl = 3;
	protected final int GBM_je = 4;
	protected final int GBM_memo = 5;
	protected final int GBM_syjh = 6;
	protected final int GBM_fphm = 7;
	protected final int GBM_printnum = 8;
	protected final int GBM_mktname = 9;
	protected final int GBM_syyh = 10;
	protected final int GBM_rqsj = 11;
	protected final int GBM_xfje = 12;
	protected final int GBM_xfsj = 13;
	protected final int GBM_startdate = 14;
	protected final int GBM_enddate = 15;
	protected final int GBM_barcode1 = 16;//epson条码打印

	public static Vector v = new Vector();

	public static GiftBillMode getDefault()
	{
		return getDefault("GiftBillMode.ini");
	}

	public static GiftBillMode getDefault(String type)
	{
		if (GiftBillMode.GiftMode == null)
		{
			GiftBillMode.GiftMode = CustomLocalize.getDefault().createGiftMode();

			// 加载辅助模板
			String rows[] = PathFile.getAllDirName(GlobalVar.ConfigPath);
			for (int i = 0; i < rows.length; i++)
			{
				if (rows[i].indexOf("GiftBillMode") >= 0)
				{
					GiftBillMode gift = CustomLocalize.getDefault().createGiftMode();
					if (rows[i].indexOf("GiftBillMode_") >= 0)
					{
						gift.ReadTemplateFile(GlobalVar.ConfigPath + "//" + rows[i]);
						v.add(new Object[] { rows[i], gift });
					}
				}
			}
		}

		for (int i = 0; v != null && i < v.size(); i++)
		{
			Object[] element = (Object[]) v.elementAt(i);
			if (element[0].toString().indexOf("_" + type + ".") >= 0)
				return (GiftBillMode) element[1];
		}

		return GiftBillMode.GiftMode;
	}

	public boolean checkTemplateFile()
	{
		File file = new File(GlobalVar.ConfigPath + "//GiftBillMode.ini");
		if (!file.exists())
			return false;

		return true;
	}

	public boolean ReadTemplateFile(String path)
	{
		File file = new File(path);
		if (!file.exists())
			return false;

		super.InitTemplate();

		return super.ReadTemplateFile(Title, path);
	}

	public boolean ReadTemplateFile()
	{
		String path = GlobalVar.ConfigPath + "//GiftBillMode.ini";
		return ReadTemplateFile(path);
	}

	public void setTemplateObject(SaleHeadDef h, GiftGoodsDef s)
	{
		salehead = h;
		gift = s;
	}

	public String getItemDataString(PrintTemplateItem item, int index)
	{
		String line = null;

		line = extendCase(item, index);
		if (line == null)
		{
			switch (Integer.parseInt(item.code))
			{
				case GBM_text: // 文本
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

				case GBM_code: // 礼品号
					line = gift.code;

					break;

				case GBM_info: // 信息
					line = gift.info;

					break;

				case GBM_sl: // 数量
					line = ManipulatePrecision.doubleToString(gift.sl);
					break;

				case GBM_je: // 金额
					line = ManipulatePrecision.doubleToString(gift.je);
					break;

				case GBM_memo: // 备注
					line = gift.memo;

					break;

				case GBM_syjh: // 收银机
					line = salehead.syjh;

					break;

				case GBM_fphm: // 实收金额
					line = Convert.increaseLong(salehead.fphm, 8);

					break;
				case GBM_printnum: // 重打印标志
					if (salehead.printnum == 0)
					{
						line = null;
					}
					else
					{
						line = "** "+ Language.apply("重 印 单 据") + " **";
					}

					break;
				case GBM_mktname:
					line = GlobalInfo.sysPara.mktname;
					break;
				case GBM_syyh:
					line = salehead.syyh;
					break;
				case GBM_rqsj:
					line = ManipulateDateTime.getCurrentDateTime();
					break;
				case GBM_xfje:
					line = salehead == null ? "0.00" : ManipulatePrecision.doubleToString(salehead.ysje);
					break;
				case GBM_xfsj:
					line = salehead == null ? ManipulateDateTime.getCurrentDateTime() : salehead.rqsj;
					break;
				case GBM_startdate:
					line = gift.startdate;
					break;
				case GBM_enddate:
					line = gift.enddate;
					break;
				case GBM_barcode1:
					char[] barcode = { 0x1D,0x6B,0x49,0x0F,0x7B,0x41};
					line = String.valueOf(barcode) + String.valueOf(ManipulateStr.getHexFromNumberStr(gift.code));
					break;
				default:
					line = extendCase(item, index);
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

	public void PrintGiftBill()
	{
		printStart();
		printVector(getCollectDataString(Total, -1, Width));
		printCutPaper();
	}

}
