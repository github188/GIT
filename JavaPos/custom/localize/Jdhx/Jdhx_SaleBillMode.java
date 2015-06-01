package custom.localize.Jdhx;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bstd.Bstd_SaleBillMode;

public class Jdhx_SaleBillMode extends Bstd_SaleBillMode
{
	
	protected int  infoLength = 0; // U51促销信息第一打印内容的长度
	protected double xjje = 0; // 统计发票小计金额
	protected double je = 0; // 保存发票小计金额
	protected final static int SBM_fpxj = 301; // 发票小计金额
	protected final static int SBM_fpxjdx = 302; // 发票小计金额大写
	protected final static int SBM_U51Info1 = 303; // U51促销信息
	protected final static int SBM_U51Info2 = 304; // 一行放不下，换一行
	protected final static int SBM_U51Info3 = 305; // 一行放不下，换一行

	
	public void printDetail()
	{
		// 设置打印区域
		setPrintArea("Detail");

		// 循环打印商品明细
		for (int i = 0; i < salegoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) salegoods.elementAt(i);

			// 赠品商品不打印
			if (sgd.flag == '1')
			{
				continue;
			}

			printVector(getCollectDataString(Detail, i, Width));
		}
	}
	
	public void printTotal()
	{
		Vector v = getCollectDataString(Total,-1,Width);
		// 设置打印区域
		setPrintArea("Total");
		
		//设置打印 Total部分内容的起始位置
		int num = 0; // 记录 Total部分打印内容的行数
		for(int i = 0; i < v.size(); i++)
		{
			String str = (String) v.get(i);
			if (str != null && !"".equals(str))
			{
				num++;
			}
		}
		//重置 Total部分 打印起始位置（刚开始没写好，只好这里修改）
		Area_Total = Area_Bottom - num + 1;
		
//		Printer.getDefault().printLine_Normal("\n");
		
		//
        printVector(v);
	}
	
	// 以下方法只在即扫即打方式使用
	public void printRealTimeHead()
	{
		//在开始即扫即打前，初始化为 false		
		PrintEnd = false;
		//发票小计归零，防止上笔小票最后没打印小票小计，小票小计没归零，导致小票小计统计有误
		xjje = 0;
		// 开始打印前的发票号
		salefph = Printer.getDefault().getCurrentSaleFphm();

		// 设置打印方式
		printSetPage();

		// 打印头部区域
		printHeader();
	}
	
	// 即扫即打时
	public void printRealTimeBottom()
	{
		//由于付款信息长度不一定，对最后打印 Total内容有影响，因此改到   Total前面打印
		// 打印付款区域
		printPay();
		
		//标记开始打印  Total内容，则不打印每页页尾内容 PageBottom
		PrintEnd = true;
		// 打印汇总区域
		printTotal();

		// 打印尾部区域
		printBottom();

		// 切纸
		printCutPaper();

		// 打印附加的各个小票联
		printAppendBill();

		// 记录本笔小票用的发票张数
		saveSaleFphm(salefph);
	}
	
	protected void printSellBill()
	{
		// GlobalInfo.sysPara.fdprintyyy =
		// (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
		// 非超市小票且系统参数定义只打印营业员分单，则不打印机制小票
		if (!((GlobalInfo.syjDef.issryyy == 'N') || (GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef) salegoods.elementAt(0)).yyyh.equals(Language.apply("超市")))) && (GlobalInfo.sysPara.fdprintyyy == 'A')) { return; }

		if (!SellType.ISEXERCISE(salehead.djlb) && printnum < 1 && salehead.printnum < 1 && !getFaxInfo())
			new MessageBox(Language.apply("获取税控信息失败！"));

		// 初始化为 false
		PrintEnd = false;
		//发票小计归零，防止上笔小票最后没打印小票小计，小票小计没归零，导致小票小计统计有误
		xjje = 0;
		
		// 设置打印方式
		printSetPage();

		// 多联小票打印不同抬头
		printDifTitle();

		// 打印头部区域
		printHeader();

		// 打印明细区域
		printDetail();

		//由于付款信息长度不一定，对最后打印 Total内容有影响，因此改到   Total前面打印
		// 打印付款区域
		printPay();
		
		//标记开始打印  Total内容，则不打印每页页尾内容 PageBottom
		PrintEnd = true;
//		Printer.getDefault().printLine_Normal("");
		// 打印汇总区域
		printTotal();


		
		// 打印尾部区域
		printBottom();

		// 切纸
		printCutPaper();		
		
	}
	
	
	
	public void printPageHead()
	{
		// 是否启用打印页头
		if (PageHeadPrint != 1)
			return;

		// 分页且套打时，不打印
		if (PagePrint == 1 && AreaPrint == 1)
			return;

		// 设置打印区域
		printPageHeader();
	}
	
    protected String extendCase(PrintTemplateItem item, int index)
    {
		String line = null;

		SaleGoodsDef sgd = null;

		switch (Integer.parseInt(item.code))
		{
			case SBM_sl: // 数量
				line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).sl * SellType.SELLSIGN(salehead.djlb), 4, 1, true);
				
				double cjje = (((SaleGoodsDef) salegoods.elementAt(index)).hjje - ((SaleGoodsDef) salegoods.elementAt(index)).hjzk) * SellType.SELLSIGN(salehead.djlb);
				//打印商品信息肯定要打印数量，因次在这里统计小票合计
				xjje += cjje;

				break;
			case SBM_fpxj:
				je = xjje;
				xjje = 0; // 小计统计完 ，归零
				
				line = ManipulatePrecision.doubleToString(ManipulatePrecision.doubleConvert(je, 1, 1));
//				line = ManipulatePrecision.doubleToString(je);
				break;
			case SBM_fpxjdx:
				line = ManipulatePrecision.getFloatConverChinese(ManipulatePrecision.doubleConvert(je, 1, 1));
//				line = ManipulatePrecision.getFloatConverChinese(je);
				break;
//			case SBM_salefphm:// 打印收银员的发票编号
//				// 获得最新发票编号
//				salefph = Printer.getDefault().getCurrentSaleFphm();
//				
//				line = Convert.increaseLong(this.salefph, item.length);
//				// 添加发票编号记录日志
//				AccessDayDB.getDefault().writeWorkLog(Language.apply("打印收银员的发票编号:(" + this.salefph + ")"), String.valueOf(StatusType.TASK_SENDWORKLOG));
//				break;
			case SBM_sjfkfpjedx:
				line = ManipulatePrecision.getFloatConverChinese(this.calcPayFPMoney() * SellType.SELLSIGN(salehead.djlb));
				break;
			case SBM_goodnamebreak: // 需要换行打印的商品名称

				String goodname = ((SaleGoodsDef) salegoods.elementAt(index)).name;

				// 商品行不够打印商品名称的时候
				if (goodnamemaxlength < Convert.countLength(goodname))
				{
					// 将打不出来的部分赋值给商品名称换行打印项
					line = Convert.newSubString(goodname, goodnamemaxlength, goodname.getBytes().length);
				}
				else
				{
					line = "";
				}

				break;
				
			case SBM_ye: // 付款余额

				SalePayDef pay = (SalePayDef) salepay.elementAt(index);
				if (pay.kye <= 0)
				{
					String payno = ((SalePayDef) salepay.elementAt(index)).payno;
					if (payno != null && !payno.equals("") && pay.kye == 0)
					{
						line = String.valueOf(pay.kye);
					}
					else
					{
						line = null;
					}
				}
				else
				{
					line = ManipulatePrecision.doubleToString(((SalePayDef) salepay.elementAt(index)).kye);
				}

				break;
			case SBM_U51Info1: // 促销信息
				if (!"".equals(salehead.str5.trim()))
				{
					if (item.length < Convert.countLength(salehead.str5.trim()))
					{
						line = Convert.newSubString(salehead.str5, 0,item.length);	
						infoLength = item.length;
					}
					else
					{
						line = salehead.str5.trim();
						infoLength = Convert.countLength(salehead.str5.trim());
					}
				}
				break;
			case SBM_U51Info2: // 促销信息
				if (!"".equals(salehead.str5.trim()) && (infoLength < Convert.countLength(salehead.str5.trim())))
				{
					if ((infoLength + item.length) < Convert.countLength(salehead.str5.trim()))
					{
						line = Convert.newSubString(salehead.str5, infoLength, infoLength + item.length);					
						infoLength = infoLength + item.length;
					}
					else
					{
						line = Convert.newSubString(salehead.str5, infoLength );
						infoLength = Convert.countLength(salehead.str5.trim());
					}
				}
				break;
			case SBM_U51Info3: // 促销信息
				if (!"".equals(salehead.str5.trim()) && (infoLength < Convert.countLength(salehead.str5.trim())) )
				{
					line = Convert.newSubString(salehead.str5, infoLength);					
				}
				break;
		}
		
		return line;
    }
    
}
