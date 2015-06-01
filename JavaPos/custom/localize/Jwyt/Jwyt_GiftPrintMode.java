package custom.localize.Jwyt;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplate;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class Jwyt_GiftPrintMode extends PrintTemplate
{
	protected static Jwyt_GiftPrintMode checkGoodsMode = null;
	protected boolean isLoad = false;

	protected SaleHeadDef salehead;
	protected Vector salegoods;

	protected final static int SBM_text = 0;
	protected final static int SBM_mktname = 1;
	protected final static int SBM_syjh = 2;
	protected final static int SBM_gh = 3;
	protected final static int SBM_name = 4;
	protected final static int SBM_fphm = 5;
	protected final static int SBM_rq = 6;
	protected final static int SBM_sj = 7;
	protected final static int SBM_index = 8;
	protected final static int SBM_code = 9;
	protected final static int SBM_goodname = 10;
	protected final static int SBM_sl = 11;
	protected final static int SBM_mktcode = 36;
	protected final static int SBM_printrq = 39;
	protected final static int SBM_printsj = 50;

	public static Jwyt_GiftPrintMode getDefault()
	{
		if (Jwyt_GiftPrintMode.checkGoodsMode == null)
		{
			Jwyt_GiftPrintMode.checkGoodsMode = new Jwyt_GiftPrintMode();
		}

		return Jwyt_GiftPrintMode.checkGoodsMode;
	}

	public boolean ReadTemplateFile()
	{
		if (!CommonMethod.isFileExist(GlobalVar.ConfigPath + "//WytGiftPrintMode.ini"))
			return true;

		super.InitTemplate();

		isLoad = super.ReadTemplateFile(Title, GlobalVar.ConfigPath + "//WytGiftPrintMode.ini");

		return isLoad;
	}

	public void setTemplateObject(SaleHeadDef h, Vector s, Vector p)
	{
		salehead = h;
		salegoods = convertGoodsDetail(s);
	}

	protected Vector convertGoodsDetail(Vector s)
	{
		return s;
	}

	public void setLoad(boolean isload)
	{
		this.isLoad = isload;
	}

	public boolean isLoad()
	{
		return isLoad;
	}

	public SaleHeadDef getSalehead()
	{
		return salehead;
	}

	public Vector getSalegoods()
	{
		return salegoods;
	}

	public String getItemDataString(PrintTemplateItem item, int index)
	{

		String line = null;

		line = extendCase(item, index);
		if (line == null)
		{
			switch (Integer.parseInt(item.code))
			{
				case SBM_text: // 文本
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

				case SBM_printrq: // 打印日期
					line = ManipulateDateTime.getCurrentDate();

					break;

				case SBM_printsj: // 打印时间
					line = ManipulateDateTime.getCurrentTime();

				case SBM_mktcode: // 卖场代码
					line = GlobalInfo.sysPara.mktcode;

					break;

				case SBM_mktname: // 商场名称

					if (GlobalInfo.sysPara.mktname != null)
					{
						line = GlobalInfo.sysPara.mktname;
					}
					else
					{
						line = "";
					}

					break;

				case SBM_syjh: // 收银机号
					line = GlobalInfo.syjStatus.syjh;

					break;

				case SBM_gh: // 收银员号
					line = salehead.syyh;

					break;

				case SBM_name: // 收银员名称

					if (salehead.syyh.trim().equals(GlobalInfo.posLogin.gh.trim()))
					{
						line = GlobalInfo.posLogin.name;
					}
					else
					{
						OperUserDef staff = new OperUserDef();

						if (!DataService.getDefault().getOperUser(staff, salehead.syyh.trim()))
						{
							line = "";
						}
						else
						{
							line = staff.name;
						}
					}

					break;

				case SBM_fphm: // 小票号码
					line = Convert.increaseLong(salehead.fphm, 8);

					break;

				case SBM_rq: // 交易日期
					line = salehead.rqsj.split(" ")[0];

					break;

				case SBM_sj: // 交易时间
					line = salehead.rqsj.split(" ")[1];

					break;

				case SBM_index: // 商品序号
					line = String.valueOf(index + 1);

					break;

				case SBM_code: // 商品代码
					line = ((SaleGoodsDef) salegoods.elementAt(index)).code;

					break;

				case SBM_goodname: // 商品名称
					line = ((SaleGoodsDef) salegoods.elementAt(index)).name;

					break;

				case SBM_sl: // 数量
					line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).sl * SellType.SELLSIGN(salehead.djlb), 4, 1, true);

					break;
			}

			if (line != null && Integer.parseInt(item.code) != 0 && item.text != null && !item.text.trim().equals(""))
			{
				int maxline = item.length - Convert.countLength(item.text);
				line = item.text + Convert.appendStringSize("", line, 0, maxline, maxline, item.alignment);
			}
		}
		return line;
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
		printPay();

		// 打印汇总区域
		printTotal();

		// 切纸
		printCutPaper();
	}

	public void printDetail()
	{
		// 设置打印区域
		setPrintArea("Detail");

		// 循环打印商品明细
		for (int i = 0; i < salegoods.size(); i++)
		{
			printVector(getCollectDataString(Detail, i, Width));
		}
	}
}
