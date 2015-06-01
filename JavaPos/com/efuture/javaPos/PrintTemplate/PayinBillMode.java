package com.efuture.javaPos.PrintTemplate;

import java.util.ArrayList;
import java.util.HashMap;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayinDetailDef;
import com.efuture.javaPos.Struct.PayinHeadDef;
import com.efuture.javaPos.Struct.PayinModeDef;
import com.efuture.javaPos.Struct.PosTimeDef;

public class PayinBillMode extends PrintTemplate
{
	protected static PayinBillMode payinBillMode = null;

	protected PayinHeadDef phd = null;
	protected PayinDetailDef pdd = null;
	protected ArrayList payListMode = null;
	protected HashMap hmPayMode = null;

	protected final int PBM_text = 0;
	protected final int PBM_seqno = 1;
	protected final int PBM_syjh = 2;
	protected final int PBM_syyh = 3;
	protected final int PBM_syhname = 4;
	protected final int PBM_jkrq = 5;
	protected final int PBM_time = 6;
	protected final int PBM_jkname = 7;
	protected final int PBM_zs = 8;
	protected final int PBM_je = 9;
	protected final int PBM_hjzs = 10;
	protected final int PBM_hjje = 11;
	protected final int PBM_jkbc = 12;
	protected final int PBM_jkbcname = 13;
	protected final int PBM_mktcode = 14;
	protected final int PBM_mktname = 15;

	public static PayinBillMode getDefault()
	{
		if (PayinBillMode.payinBillMode == null)
		{
			PayinBillMode.payinBillMode = CustomLocalize.getDefault().createPayinBillMode();
		}

		return PayinBillMode.payinBillMode;
	}

	public boolean ReadTemplateFile()
	{
		super.InitTemplate();

		return super.ReadTemplateFile(Title, GlobalVar.ConfigPath + "//PayinPrintMode.ini");
	}

	public void setTemplateObject(PayinHeadDef phd, ArrayList payListMode, HashMap hmPayMode)
	{
		this.phd = phd;
		this.payListMode = payListMode;
		this.hmPayMode = hmPayMode;
	}

	public String getItemDataString(PrintTemplateItem item, int index)
	{
		String line = null;

		try
		{
			line = extendCase(item, index);
			if (line == null)
			{
				switch (Integer.parseInt(item.code))
				{
				case PBM_text: // 文本
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

				case PBM_seqno: // 缴款单号

					if (phd.seqno <= 0)
					{
						line = "";
					}
					else
					{
						line = String.valueOf(phd.seqno);
					}

					break;

				case PBM_mktcode: // 门店编号
					line = GlobalInfo.sysPara.mktcode;
					break;

				case PBM_mktname: // 门店名称
					line = GlobalInfo.sysPara.mktname;
					break;

				case PBM_syjh: // 收银机号
					line = phd.syjh;

					break;

				case PBM_syyh: // 收银员号
					line = phd.syyh;

					break;

				case PBM_syhname: // 收银员名

					if (phd.syyh.trim().equals(GlobalInfo.posLogin.gh.trim()))
					{
						line = GlobalInfo.posLogin.name;
					}
					else
					{
						OperUserDef staff = new OperUserDef();

						if (!DataService.getDefault().getOperUser(staff, phd.syyh.trim()))
						{
							line = "";
						}
						else
						{
							line = staff.name;
						}
					}

					break;

				case PBM_jkrq: // 缴款日期

					if (phd.jkrq != null)
					{
						line = phd.jkrq;
					}
					else
					{
						line = "";
					}

					break;

				case PBM_time: // 打印时间

					ManipulateDateTime mdt = new ManipulateDateTime();
					line = mdt.getDateBySlash() + " " + mdt.getTime();

					break;

				case PBM_jkname: // 缴款名称
					pdd = (PayinDetailDef) payListMode.get(index);

					PayinModeDef pmd = (PayinModeDef) hmPayMode.get(pdd.code);
					line = pmd.name;

					break;

				case PBM_zs: // 张数
					line = ManipulatePrecision.doubleToString(((PayinDetailDef) payListMode.get(index)).zs, 0, 1, false);

					// 打印空白缴款单
					if (Convert.toDouble(line.trim()) == 0 && phd != null && phd.seqno == -1)
						line = null;
					break;

				case PBM_je: // 金额
					pdd = (PayinDetailDef) payListMode.get(index);
					line = ManipulatePrecision.doubleToString(pdd.je);
					// 打印空白缴款单
					if (Convert.toDouble(line.trim()) == 0 && phd != null && phd.seqno == -1)
						line = null;
					break;

				case PBM_hjzs: // 合计张数

					int countZS = 0;

					if (payListMode != null)
					{
						for (int i = 0; i < payListMode.size(); i++)
						{
							PayinDetailDef pdd = (PayinDetailDef) payListMode.get(i);
							countZS = countZS + pdd.zs;
						}

						line = String.valueOf(countZS);
					}
					else
					{
						line = "0";
					}
					// 打印空白缴款单
					if (Convert.toDouble(line.trim()) == 0 && phd != null && phd.seqno == -1)
						line = null;
					break;

				case PBM_hjje: // 合计金额
					line = ManipulatePrecision.doubleToString(phd.je);
					// 打印空白缴款单
					if (Convert.toDouble(line.trim()) == 0 && phd != null && phd.seqno == -1)
						line = null;
					break;
				case PBM_jkbc: // 缴款班次
					line = String.valueOf(phd.jkbc).trim();

					break;
				case PBM_jkbcname: // 缴款班次名称
					if (GlobalInfo.posTime != null)
					{
						for (int i = 0; i < GlobalInfo.posTime.size(); i++)
						{
							PosTimeDef ptd = (PosTimeDef) GlobalInfo.posTime.get(i);
							if (ptd.code == phd.jkbc)
							{
								line = ptd.name;
							}
						}
					}

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

	public void printHeader()
	{
		// 设置打印区域
		setPrintArea("Header");

		if (phd.reprint != null && phd.reprint.equals("Y"))
		{
			printLine(Convert.appendStringSize("", Language.apply("**重打印**"), 1, 38, 38, 2));
		}
		// 打印
		printVector(getCollectDataString(Header, -1, Width));
	}

	public void printBill()
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

	public void printDetail()
	{
		// 设置打印区域
		setPrintArea("Detail");

		// 循环打印明细
		for (int i = 0; i < payListMode.size(); i++)
		{
			printVector(getCollectDataString(Detail, i, Width));
		}
	}
}
