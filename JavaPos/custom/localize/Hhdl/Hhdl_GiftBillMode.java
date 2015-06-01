package custom.localize.Hhdl;

import java.io.File;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.PrintTemplate.PrintTemplate;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class Hhdl_GiftBillMode extends PrintTemplate
{
	protected static Hhdl_GiftBillMode GiftMode = new Hhdl_GiftBillMode();

	public SaleHeadDef salehead;
	public Hhdl_CouponGiftDef gift;

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
	protected final int GBM_starttime = 16;
	protected final int GBM_endtime = 17;
	protected final int GBM_barcode = 18;
	protected final int GBM_printflag = 19;
	protected final int GBM_printdate = 20;
	protected final int GBM_printtime = 21;
	protected final int GBM_giftadv1 = 22;
	protected final int GBM_giftadv2 = 23;

	public static Vector v = new Vector();

	public static Hhdl_GiftBillMode getDefault()
	{
		return GiftMode;
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

	public void setTemplateObject(SaleHeadDef h, Hhdl_CouponGiftDef s)
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
				case GBM_printflag:
					if (salehead.printnum > 0)
						line = "******重印" + String.valueOf(salehead.printnum) + "*****";
					break;
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
				case GBM_giftadv1:
					line = this.getAdvert(gift.memo, gift.type, true);
					break;
				case GBM_giftadv2:
					line = this.getAdvert(gift.memo, gift.type, false);
					break;

				case GBM_sl: // 数量
					line = ManipulatePrecision.doubleToString(gift.sl);
					break;

				case GBM_je: // 金额
					line = ManipulatePrecision.doubleToString(gift.je);
					break;

				case GBM_memo: // 备注
					if (gift.type.equals("1"))
					{
						if (gift.memo != null && gift.memo.indexOf(",") != -1)
						{
							String[] goods = gift.memo.split(",");

							if (goods.length > 0)
								line = "编码: " + goods[0] + " ";
							if (goods.length > 1)
								line += "名称: " + goods[1] + " ";
							if (goods.length > 2)
								line += "规格: " + goods[2] + " ";
							if (goods.length > 3)
								line += "数量: " + goods[3] + " ";
						}
						return line;
					}

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
						line = "**重 印 单 据" + "**";
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

				case GBM_starttime:
					line = gift.starttime;
					break;
				case GBM_endtime:
					line = gift.endtime;
					break;
				case GBM_printdate:
					line = ManipulateDateTime.getCurrentDate();
					break;
				case GBM_printtime:
					line = ManipulateDateTime.getCurrentTime();
					break;
				case GBM_barcode:
					char[] barcode = { 0x1D, 0x68, 0x30, 0x1D, 0x77, 0x02, 0x1D, 0x6B, 0x04 };
					char[] endcode = { 0x00, 0x0A };
					line = String.valueOf(barcode) + gift.code + String.valueOf(endcode);
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

	private String getAdvert(String memo, String type, boolean flag)
	{
		try
		{
			if (memo == null || memo.trim().equals(""))
				return "";

			if (flag)
			{
				if (type.equals("0"))
				{
					String[] adver = memo.split("#");
					if (adver != null && adver.length > 0)
						return adver[0];
				}

				if (type.equals("1"))
				{
					if (memo.indexOf(",") != -1)
					{
						String[] info = memo.split(",");
						if (info.length > 4)
						{
							String[] adver = info[4].split("#");
							if (adver != null && adver.length > 0)
								return adver[0];
						}
					}
				}
			}
			else
			{
				if (type.equals("0"))
				{
					String[] adver = memo.split("#");
					if (adver != null && adver.length > 1)
						return adver[1];
				}

				if (type.equals("1"))
				{
					if (memo.indexOf(",") != -1)
					{
						String[] info = memo.split(",");
						if (info.length > 4)
						{
							String[] adver = info[4].split("#");
							if (adver != null && adver.length > 1)
								return adver[1];
						}
					}
				}
			}
			return "";
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "";
		}
	}

	public void PrintGiftBill()
	{
		printStart();
		printVector(getCollectDataString(Total, -1, Width));
		printCutPaper();
	}

}
