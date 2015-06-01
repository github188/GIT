package custom.localize.Cctd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import jpos.JposException;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBank;

import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import device.Printer.ZZBJ_IBMJPOS_Printer;

public class Cctd_SaleBillMode extends SaleBillMode
{
	public final static int jfbl = 101; // 积分倍率
	public final static int lqfq = 102; // L券余额
	public final static int fqts = 103; // 返券提示
	public final static int aqfq = 104; // A券余额
	public final static int bqfq = 105; // B券余额
	public final static int qtzk = 106; // 其他折扣
	public final static int hyzk = 107; // 其他折扣
	public final static int thdh = 108; // 其他折扣
	public final static int jfkc = 109; // 积分扣除

	public final static int lszke = 110; // 临时折扣额
	public final static int lszre = 111; // 临时折让额
	public final static int lszzk = 112; // 临时总折扣
	public final static int lszzr = 113; // 临时总折让
	public final static int hyzke = 114; // 会员折扣
	public final static int yhzke = 115; // 定期优惠折扣
	public final static int zszke = 116; // 规则折扣
	public final static int lszk = 117; // 授权折扣
	public final static int zjfkc = 118; // 本笔扣除积分总额

	protected Vector groupset = new Vector();
	protected String message = "";
	protected GroupDef curgroup = null;

	protected String[] printOrder = null; //读取打印顺序
	protected boolean isOrder = false;    //是否排序打印
	protected Vector kpInfo = null;
	protected Vector carParkInfo = null;
	protected Vector customerBill = null;
	public class GroupDef
	{
		// 分组条件 1,2,3
		public String key1 = "";
		public String key2 = "";
		public String key3 = "";
		public String yyyh = "";
		public String gz = "";
		public Vector row_set = new Vector();
		public GroupSummaryDef gsd = new GroupSummaryDef();
	}

	public class GroupSummaryDef
	{
		public double hjje = 0;
		public double hjzk = 0;
		public double hjsl = 0;
	}

	// 按营业员柜组分组
	public void groupByYyyGz()
	{
		groupset.clear();
		message = "";

		SaleGoodsDef sgd = null;
		GroupDef group = null;

		for (int i = 0; i < originalsalegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) originalsalegoods.elementAt(i);

			// 查找是否相同商品,按营业员柜组分组
			int j = 0;
			for (j = 0; j < groupset.size(); j++)
			{
				group = (GroupDef) groupset.elementAt(j);

				if (group.key1.equals(sgd.yyyh) && group.key2.equals(sgd.gz))
				{
					group.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= groupset.size())
			{
				group = new GroupDef();
				group.key1 = sgd.yyyh;
				group.key2 = sgd.gz;
				group.yyyh = sgd.yyyh;
				group.gz = sgd.gz;
				group.row_set.add(String.valueOf(i));
				groupset.add(group);
			}
		}

		message = "请将营业员([key1])的销售单放入打印机\n\n按‘确认’键后开始打印\n按‘退出’键则跳过打印";
	}

	// 按商品分组
	public void groupByGoods()
	{
		groupset.clear();
		message = "";

		SaleGoodsDef sgd = null;
		GroupDef group = null;

		for (int i = 0; i < originalsalegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) originalsalegoods.elementAt(i);

			group = new GroupDef();
			group.key1 = sgd.code;
			group.yyyh = sgd.yyyh;
			group.gz = sgd.gz;
			group.row_set.add(String.valueOf(i));
			group.key2 = String.valueOf(sgd.jg);
			group.key3 = String.valueOf(sgd.sl);
			groupset.add(group);

		}

		message = "请将商品([key1])的销售单放入打印机\n商品数量：[key3]\n商品单价：[key2]\n按‘确认’键后开始打印\n按‘退出’键则跳过打印";
	}

	// 按柜组
	public void groupByGz()
	{
		groupset.clear();
		message = "";

		SaleGoodsDef sgd = null;
		GroupDef group = null;

		for (int i = 0; i < originalsalegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) originalsalegoods.elementAt(i);

			// 查找是否相同商品,按营业员柜组分组
			int j = 0;
			for (j = 0; j < groupset.size(); j++)
			{
				group = (GroupDef) groupset.elementAt(j);

				if (group.key1.equals(sgd.gz))
				{
					group.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= groupset.size())
			{
				group = new GroupDef();
				group.key1 = sgd.gz;
				group.yyyh = sgd.yyyh;
				group.gz = sgd.gz;
				group.row_set.add(String.valueOf(i));
				groupset.add(group);
			}
		}

		message = "请将柜组([key1])的销售单放入打印机\n\n按‘确认’键后开始打印\n按‘退出’键则跳过打印";
	}

	// 按营业员
	public void groupByYyy()
	{
		groupset.clear();
		message = "";

		SaleGoodsDef sgd = null;
		GroupDef group = null;

		for (int i = 0; i < originalsalegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) originalsalegoods.elementAt(i);

			// 查找是否相同商品,按营业员柜组分组
			int j = 0;
			for (j = 0; j < groupset.size(); j++)
			{
				group = (GroupDef) groupset.elementAt(j);

				if (group.key1.equals(sgd.yyyh))
				{
					group.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= groupset.size())
			{
				group = new GroupDef();
				group.key1 = sgd.yyyh;
				group.yyyh = sgd.yyyh;
				group.gz = sgd.gz;
				group.row_set.add(String.valueOf(i));
				groupset.add(group);
			}
		}

		message = "请将营业员([key1])的销售单放入打印机\n\n按‘确认’键后开始打印\n按‘退出’键则跳过打印";
	}

	public void groupByFph()
	{
		groupset.clear();
		message = "";

		SaleGoodsDef sgd = null;
		GroupDef group = null;

		for (int i = 0; i < originalsalegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) originalsalegoods.elementAt(i);

			int j = 0;
			for (j = 0; j < groupset.size(); j++)
			{
				group = (GroupDef) groupset.elementAt(j);

				if (group.key1.equals(sgd.fph))
				{
					group.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= groupset.size())
			{
				group = new GroupDef();
				group.key1 = sgd.fph;
				group.yyyh = sgd.yyyh;
				group.gz = sgd.gz;
				group.row_set.add(String.valueOf(i));
				groupset.add(group);
			}
		}

		message = "请将发票号([key1])的销售单放入打印机\n\n按‘确认’键后开始打印\n按‘退出’键则跳过打印";
	}

	public void group()
	{
		// 打印营业员联分组方式,1-营业员+柜组,2-单品,3-柜组,4-营业员
		if (GlobalInfo.sysPara.printyyygrouptype == '1')
		{
			this.groupByYyyGz();
		}
		else if (GlobalInfo.sysPara.printyyygrouptype == '2')
		{
			this.groupByGoods();
		}
		else if (GlobalInfo.sysPara.printyyygrouptype == '3')
		{
			this.groupByGz();
		}
		else if (GlobalInfo.sysPara.printyyygrouptype == '4')
		{
			this.groupByYyy();
		}
		else if (GlobalInfo.sysPara.printyyygrouptype == '5')
		{
			this.groupByFph();
		}

		groupsummary();
	}

	public void groupsummary()
	{
		for (int i = 0; i < groupset.size(); i++)
		{
			GroupDef group = (GroupDef) groupset.elementAt(i);

			GroupSummaryDef gsd = group.gsd;

			for (int j = 0; j < group.row_set.size(); j++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) originalsalegoods.elementAt(Integer.parseInt((String) group.row_set.elementAt(j)));

				gsd.hjje += sgd.hjje;
				gsd.hjzk += sgd.hjzk;
				gsd.hjsl += sgd.sl;
			}
		}
	}

	// 卓展北京店列印打印
	public void printYYYBillBj()
	{
		// 如果不为销售小票，不打印平推联
		if (!SellType.ISSALE(salehead.djlb) && !SellType.ISBACK(salehead.djlb)) { return; }

		int i = 0;
		SaleGoodsDef sgd = null;

		// 系统参数定义为打印分单且是营业员小票，才打印营业员联
		if (!((GlobalInfo.sysPara.fdprintyyy == 'Y') && ((GlobalInfo.syjDef.issryyy == 'Y') || ((GlobalInfo.syjDef.issryyy == 'A') && !((SaleGoodsDef) salegoods.elementAt(0)).yyyh.equals("超市"))))) { return; }

		StringBuffer sb1;
		StringBuffer sb2;
		StringBuffer sb3;
		// StringBuffer sb4;
		StringBuffer sb5;
		StringBuffer sb6;

		String appendSpaceE = "       ";
		String appendSpaceC = "   ";

		// 按分组进行分单打印
		for (i = 0; i < groupset.size(); i++)
		{
			// 小计
			double xj = 0;
			// 折扣
			double zk = 0;
			// 应收金额
			double ysje = 0;
			// 会员折扣
			double hyzke = 0;

			boolean done = false;
			// 设置当前分组信息
			curgroup = (GroupDef) groupset.elementAt(i);

			Vector newSgds = new Vector();
			// 设置当前分组的salegoods
			newSgds.clear();
			for (int j = 0; j < curgroup.row_set.size(); j++)
			{
				int index = Convert.toInt(curgroup.row_set.get(j));
				sgd = (SaleGoodsDef) salegoods.get(index);
				newSgds.add(sgd);

				xj += SellType.SELLSIGN(salehead.djlb) * sgd.hjje;
				zk += sgd.lszke + sgd.lszre + sgd.lszzk + sgd.lszzr;
				ysje += (sgd.hjje - sgd.hjzk) * SellType.SELLSIGN(salehead.djlb);
				hyzke += SellType.SELLSIGN(salehead.djlb) * sgd.hyzke;
			}

			// 从第3栈打印才进行提示
			if (!message.equals("") && GlobalInfo.sysPara.fdprintyyytrack == '3')
			{
				String str = "";
				str = ExpressionDeal.replace(message, "[key1]", curgroup.key1);
				str = ExpressionDeal.replace(str, "[key2]", curgroup.key2);
				str = ExpressionDeal.replace(str, "[key3]", curgroup.key3);
				str = ExpressionDeal.replace(str, "[yyyh]", curgroup.yyyh);
				str = ExpressionDeal.replace(str, "[gz]", curgroup.gz);

				while (true)
				{
					int retvalue = new MessageBox(str).verify();
					if (retvalue == GlobalVar.Exit)
					{
						done = true;
						break;
					}
					else if (retvalue == GlobalVar.Validation)
					{
						break;
					}
				}

				if (done)
				{
					done = false;
					continue;
				}
			}

			sb1 = new StringBuffer();
			sb2 = new StringBuffer();
			sb3 = new StringBuffer();
			// sb4 = new StringBuffer();
			sb5 = new StringBuffer();

			sb6 = new StringBuffer();

			Printer.getDefault().setEmptyMsg_Slip("请将打印纸放入平推");
			Printer.getDefault().startPrint_Slip();

			sb1.append(appendSpaceE + salehead.rqsj + " NO." + salehead.syjh + "-" + salehead.fphm);
			sb2.append(appendSpaceC + "收银员:" + salehead.syyh + " " + SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead));
			sb3.append("   ------------------------");
			sb5.append("   ------------------------");
			sb6.append(appendSpaceC + "营业员:" + sgd.yyyh + " 柜组:" + sgd.gz + "\r\n");

			if (salehead.hykh.length() > 0)
			{
				sb6.append(appendSpaceC + "VIP卡号:" + salehead.hykh + "\r\n");
			}

			sb6.append(appendSpaceC + "VIP折扣:" + ManipulatePrecision.doubleToString(hyzke) + " 折扣:" + ManipulatePrecision.doubleToString(SellType.SELLSIGN(salehead.djlb) * zk) + "\r\n");
			sb6.append(appendSpaceC + "小计:" + ManipulatePrecision.doubleToString(xj) + " 应收金额:" + ManipulatePrecision.doubleToString(ysje) + "\r\n");
			sb6.append(appendSpaceC + "本小票使用的付款方式:\r\n");

			String line2 = "";

			// 如果商品不收券，不打印券付款
			double aq = 0, bq = 0;

			if (sgd.memo.trim().length() > 0)
			{
				String[] a = sgd.memo.split(",");

				if (a.length > 0)
					aq = Convert.toDouble(a[0]);

				if (a.length > 1)
					bq = Convert.toDouble(a[1]);

			}
			// 循环打印付款明细
			for (int j = 0; j < salepay.size(); j++)
			{
				SalePayDef spd = (SalePayDef) salepay.elementAt(j);

				// 找零付款不打印
				if (spd.flag == '2')
				{
					continue;
				}

				if ((CreatePayment.getDefault().isPaymentFjk(spd.paycode) && spd.idno.trim().length() > 0 && spd.idno.charAt(0) == '1' && aq == 0) || (CreatePayment.getDefault().isPaymentFjk(spd.paycode) && spd.idno.trim().length() > 0 && spd.idno.charAt(0) == '2' && bq == 0))
				{
					continue;
				}

				try
				{
					if (CreatePayment.getDefault().isPaymentFjk(spd.paycode))
					{
						// 检查商品是否可以分摊
						String ft = sgd.str2;
						if (ft == null || ft.length() <= 0)
							continue;

						String[] ft1 = ft.split(",");
						int kk = 0;
						for (kk = 0; kk < ft1.length; kk++)
						{
							String[] ftj = ft1[kk].split(":");
							int rowno = Convert.toInt(ftj[0]);
							if (rowno == spd.rowno)
							{
								break;
							}
						}

						if (kk >= ft1.length)
							continue;
					}
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}

				if (line2.indexOf(spd.payname) < 0)
				{
					line2 += ("," + spd.payname);
				}
			}

			if (line2.length() > 0)
			{
				line2 = line2.substring(1);
			}
			sb6.append(appendSpaceC + line2);

			try
			{
				((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setSlpLineSpacing(5);
				((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setCharacterSet(437);
				((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setSlpLineChars(52);
				Printer.getDefault().printLine_Slip(" " + sb1.toString());

				((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setCharacterSet(1381);
				((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setSlpLineChars(59);
				Printer.getDefault().printLine_Slip(" " + sb2.toString());

				((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setCharacterSet(437);
				((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setSlpLineChars(52);
				Printer.getDefault().printLine_Slip(" " + sb3.toString());

				SaleGoodsDef newSgd = null;
				for (int n = 0; n < newSgds.size(); n++)
				{
					newSgd = (SaleGoodsDef) newSgds.get(n);
					((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setCharacterSet(1381);
					((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setSlpLineChars(59);
					((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setSlpLineSpacing(7);
					Printer.getDefault().printLine_Slip(" " + appendSpaceC + newSgd.name);
					Printer.getDefault().printLine_Slip("  ");

					((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setCharacterSet(437);
					((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setSlpLineChars(52);
					((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setSlpLineSpacing(5);
					Printer.getDefault().printLine_Slip(" " + "   " + newSgd.barcode + " " + ManipulatePrecision.doubleToString(newSgd.sl * SellType.SELLSIGN(salehead.djlb), 4, 1, true) + " " + ManipulatePrecision.doubleToString(newSgd.jg) + " " + ManipulatePrecision.doubleToString(SellType.SELLSIGN(salehead.djlb) * ManipulatePrecision.sub(newSgd.hjje, newSgd.hjzk)) + "\r\n");
				}

				((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setCharacterSet(437);
				((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setSlpLineChars(52);
				((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setSlpLineSpacing(5);
				Printer.getDefault().printLine_Slip(" " + sb5.toString());

				((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setCharacterSet(1381);
				((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setSlpLineChars(59);
				((ZZBJ_IBMJPOS_Printer) Printer.getDefault().printer).posPrinter.setSlpLineSpacing(5);
				Printer.getDefault().printLine_Slip(" " + sb6.toString());
			}
			catch (JposException e)
			{
				e.printStackTrace();
			}
			Printer.getDefault().cutPaper_Slip();
			Printer.getDefault().setEmptyMsg_Slip("");
		}
	}

	public void printYYYBill()
	{
		group();
		if (GlobalInfo.sysPara.mktcode.equals("002,205") || Printer.getDefault().printer.getClass().getName().equals("device.Printer.ZZBJ_IBMJPOS_Printer"))
		{
			printYYYBillBj();
		}
		else
		{
			boolean done = false;
			// 如果不为销售小票，不打印平推联
			if (!SellType.ISSALE(salehead.djlb) && !SellType.ISBACK(salehead.djlb)) { return; }

			int i = 0;
			String appendSpace = "";
			SaleGoodsDef sgd = null;
			String line;

			// 系统参数定义为打印分单且是营业员小票，才打印营业员联
			if (!((GlobalInfo.sysPara.fdprintyyy == 'Y') && ((GlobalInfo.syjDef.issryyy == 'Y') || ((GlobalInfo.syjDef.issryyy == 'A') && !((SaleGoodsDef) salegoods.elementAt(0)).yyyh.equals("超市"))))) { return; }

			// 按分组进行分单打印
			for (i = 0; i < groupset.size(); i++)
			{
				// 小计
				double xj = 0;
				// 折扣
				double zk = 0;
				// 应收金额
				double ysje = 0;
				// 会员折扣
				double hyzke = 0;

				// 设置当前分组信息
				curgroup = (GroupDef) groupset.elementAt(i);

				Vector newSgds = new Vector();
				// 设置当前分组的salegoods
				newSgds.clear();
				for (int j = 0; j < curgroup.row_set.size(); j++)
				{
					int index = Convert.toInt(curgroup.row_set.get(j));
					sgd = (SaleGoodsDef) salegoods.get(index);
					newSgds.add(sgd);
					xj += SellType.SELLSIGN(salehead.djlb) * sgd.hjje;
					zk += sgd.lszke + sgd.lszre + sgd.lszzk + sgd.lszzr;
					ysje += (sgd.hjje - sgd.hjzk) * SellType.SELLSIGN(salehead.djlb);
					hyzke += SellType.SELLSIGN(salehead.djlb) * sgd.hyzke;
				}

				// 从第3栈打印才进行提示
				if (!message.equals("") && GlobalInfo.sysPara.fdprintyyytrack == '3')
				{
					String str = "";
					str = ExpressionDeal.replace(message, "[key1]", curgroup.key1);
					str = ExpressionDeal.replace(str, "[key2]", curgroup.key2);
					str = ExpressionDeal.replace(str, "[key3]", curgroup.key3);
					str = ExpressionDeal.replace(str, "[yyyh]", curgroup.yyyh);
					str = ExpressionDeal.replace(str, "[gz]", curgroup.gz);

					while (true)
					{
						int retvalue = new MessageBox(str).verify();
						if (retvalue == GlobalVar.Exit)
						{
							done = true;
							break;
						}
						else if (retvalue == GlobalVar.Validation)
						{
							break;
						}
					}

					if (done)
					{
						done = false;
						continue;
					}
				}

				Printer.getDefault().setEmptyMsg_Slip("请将打印纸放入平推联");
				Printer.getDefault().startPrint_Slip();

				Printer.getDefault().printLine_Slip(appendSpace + "\n" + salehead.rqsj + " NO." + salehead.syjh + "-" + salehead.fphm);
				Printer.getDefault().printLine_Slip(appendSpace + "收银员:" + salehead.syyh + "          " + SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead));

				if (salehead.printnum > 0)
				{
					Printer.getDefault().printLine_Slip(appendSpace + "---------------重打印-----------------");
				}
				else
				{
					Printer.getDefault().printLine_Slip(appendSpace + "--------------------------------------");
				}

				SaleGoodsDef newSgd = null;
				for (int n = 0; n < newSgds.size(); n++)
				{
					newSgd = (SaleGoodsDef) newSgds.get(n);
					Printer.getDefault().printLine_Slip(appendSpace + newSgd.name);

					line = Convert.appendStringSize("", newSgd.barcode, 0, 13, Width);
					line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(newSgd.sl * SellType.SELLSIGN(salehead.djlb), 4, 1, true), 24, 4, Width, 0);
					line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(newSgd.jg), 15, 8, Width, 1);
					line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(SellType.SELLSIGN(salehead.djlb) * ManipulatePrecision.sub(newSgd.hjje, newSgd.hjzk)), 29, 9, Width, 1);
					Printer.getDefault().printLine_Slip(appendSpace + line);
				}

				Printer.getDefault().printLine_Slip(appendSpace + "--------------------------------------");

				String line1 = "";
				line1 = Convert.appendStringSize(line1, "营业员:", 0, 8, 40);
				line1 = Convert.appendStringSize(line1, sgd.yyyh, 9, 10, 40);
				line1 = Convert.appendStringSize(line1, "柜组:", 21, 7, 40);
				line1 = Convert.appendStringSize(line1, sgd.gz, 28, 11, 40);

				Printer.getDefault().printLine_Slip(appendSpace + line1);

				if (salehead.hykh.length() > 0)
				{
					Printer.getDefault().printLine_Slip(appendSpace + "VIP卡号：" + salehead.hykh);
				}

				line1 = "";
				line1 = Convert.appendStringSize(line1, "VIP折扣:", 0, 8, 40);
				line1 = Convert.appendStringSize(line1, ManipulatePrecision.doubleToString(hyzke), 9, 10, 40);
				line1 = Convert.appendStringSize(line1, "折扣:", 21, 7, 40);
				line1 = Convert.appendStringSize(line1, ManipulatePrecision.doubleToString(SellType.SELLSIGN(salehead.djlb) * zk), 28, 11, 40);

				Printer.getDefault().printLine_Slip(appendSpace + line1);

				line1 = "";
				line1 = Convert.appendStringSize(line1, "小计:", 0, 8, 40);
				line1 = Convert.appendStringSize(line1, ManipulatePrecision.doubleToString(xj), 9, 10, 40);
				line1 = Convert.appendStringSize(line1, "应收金额:", 21, 7, 40);
				line1 = Convert.appendStringSize(line1, ManipulatePrecision.doubleToString(ysje), 28, 11, 40);

				Printer.getDefault().printLine_Slip(appendSpace + line1);

				Printer.getDefault().printLine_Slip(appendSpace + "本小票使用的付款方式：");

				String line2 = "";

				// 如果商品不收券，不打印券付款
				double aq = 0, bq = 0;

				if (sgd.memo.trim().length() > 0)
				{
					String[] a = sgd.memo.split(",");

					if (a.length > 0)
						aq = Convert.toDouble(a[0]);

					if (a.length > 1)
						bq = Convert.toDouble(a[1]);

				}
				// 循环打印付款明细
				for (int j = 0; j < salepay.size(); j++)
				{
					SalePayDef spd = (SalePayDef) salepay.elementAt(j);

					// 找零付款不打印
					if (spd.flag == '2')
					{
						continue;
					}

					if ((CreatePayment.getDefault().isPaymentFjk(spd.paycode) && spd.idno.trim().length() > 0 && spd.idno.charAt(0) == '1' && aq == 0) || (CreatePayment.getDefault().isPaymentFjk(spd.paycode) && spd.idno.trim().length() > 0 && spd.idno.charAt(0) == '2' && bq == 0))
					{
						continue;
					}

					try
					{
						if (CreatePayment.getDefault().isPaymentFjk(spd.paycode))
						{
							// 检查商品是否可以分摊
							String ft = sgd.str2;
							if (ft == null || ft.length() <= 0)
								continue;

							String[] ft1 = ft.split(",");
							int kk = 0;
							for (kk = 0; kk < ft1.length; kk++)
							{
								String[] ftj = ft1[kk].split(":");
								int rowno = Convert.toInt(ftj[0]);
								if (rowno == spd.rowno)
								{
									break;
								}
							}

							if (kk >= ft1.length)
								continue;
						}
					}
					catch (Exception er)
					{
						er.printStackTrace();
					}

					if (line2.indexOf(spd.payname) < 0)
					{
						line2 += ("," + spd.payname);
					}
				}

				if (line2.length() > 0)
				{
					line2 = line2.substring(1);
				}

				Printer.getDefault().printLine_Slip(appendSpace + line2);
				Printer.getDefault().cutPaper_Slip();
				Printer.getDefault().setEmptyMsg_Slip("");
			}
		}
	}

	public void printGift()
	{
		boolean first = false;

		for (int i = 0; i < salegoods.size(); i++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) salegoods.elementAt(i);

			if (saleGoodsDef.flag == '1' || saleGoodsDef.flag == '5')
			{
				if (!first)
				{
					printLine(Convert.appendStringSize("", "\n赠品栏", 0, Width, Width, 2));
				}

				first = true;
				printLine("商品编码:" + saleGoodsDef.barcode);
				printLine("商品柜组:" + saleGoodsDef.gz);

				String line = Convert.appendStringSize("", "赠送价值:", 0, 9, Width);
				line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(saleGoodsDef.lsj), 10, 10, Width);
				line = Convert.appendStringSize(line, "赠送数量:", 21, 9, Width);
				line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(saleGoodsDef.sl, 4, 1), 30, 8, Width);
				printLine(line);
				printLine("\n");
			}
		}
	}

	// 打印头部区域
	public void printHeader()
	{

		super.printHeader();
	}

	public void printBottom()
	{
		if(printOrder == null || !(printOrder.length>0)) isOrder = getPrintOrder();
		
		if (salehead.fk_sysy > 0)
		{
			printLine("不找零金额: " + salehead.fk_sysy);
		}

		// 存在会员卡
		if (salehead.hykh.length() > 0)
		{
			printLine("VIP卡：" + salehead.hykh);
			if (GlobalInfo.isOnline)
			{
				printLine("本笔消费积分：" + salehead.bcjf);
				printLine("倍享积分：" + ManipulatePrecision.doubleToString(salehead.num4));
				printLine("累计积分：" + salehead.ljjf);
				if (salehead.str5 != null && salehead.str5.length() > 0)
					printLine(salehead.str5);
			}
			else
			{
				printLine("消费积分：请到服务台查询");
			}
		}
		if(!isOrder){
			printMZKBill(1);

			// 打印返券卡联
			printMZKBill(2);
			printLine(Convert.appendStringSize("", "=================================================", 1, 37, 38));

			printBKL();
		}
		
		super.printBottom();
		if(!isOrder){
			printKpInfo();
		}else{
			printSetOrder();
		}
		
		
	}

	private void printSetOrder()
	{
		for(int i =0;i<printOrder.length;i++){
			if(printOrder[i].trim().equals("MzkBill")) {
				printMZKBill(1);
				continue;
			}
			if(printOrder[i].trim().equals("FqkBill")){
				printMZKBill(2);
				continue;
			}
			if(printOrder[i].trim().equals("CarParkInfo")) {
				if (GlobalInfo.sysPara.isPrintCPR == 'Y')
	 			{
					printCarParkInfo();
	 				printLine("");
	 			}
				continue;
			}
			if(printOrder[i].trim().equals("BkBill")) {
				printBKL();
	 			printLine("");
	 			continue;
			}
			if(printOrder[i].trim().equals("CustomerBill")){
				PrintCustomer();
				continue;
			}
			if(printOrder[i].trim().equals("KpInfo")) {
				printKpInfo();
				continue;
			}
		}
	}

	private void PrintCustomer()
	{
    	if(GlobalInfo.sysPara.isprintgkl == 'Y' )
    	{
    		if(customerBill!=null && customerBill.size()>0){
    			printLine(Convert.appendStringSize("", "===============================================", 0, 37, 38, 2));
        		printLine(Convert.appendStringSize("", "顾客意见联", 0, 37, 38, 2));
        		
        		for(int i = 0 ;i<customerBill.size();i++)printLine(String.valueOf(customerBill.elementAt(i)));
        		String line1 = "";
        		line1 = Convert.appendStringSize("", "收银机:", 0, 7, 38);
        		line1 = Convert.appendStringSize(line1, GlobalInfo.syjDef.syjh, 8, 10, 38);
        		line1 = Convert.appendStringSize(line1, "收银员:",19 , 7, 38);
        		line1 = Convert.appendStringSize(line1, GlobalInfo.posLogin.gh, 27, 10, 38);
        		printLine(line1);
        		line1 = Convert.appendStringSize(line1, "小票号:", 0, 7, 38);
        		line1 = Convert.appendStringSize(line1, String.valueOf(salehead.fphm), 8, 10, 38);
        		line1 = Convert.appendStringSize(line1, salehead.rqsj, 19, 19, 38);
        		printLine(line1);
    		}
    	}
	
	}

	private void printCarParkInfo()
	{
		if(carParkInfo != null && carParkInfo.size()>0){
			printLine("==============停车联==================");
			
			for(int i = 0;i<carParkInfo.size();i++)printLine(String.valueOf(carParkInfo.elementAt(i)));
			printLine("实收金额:" + ManipulatePrecision.doubleToString(salehead.ysje * SellType.SELLSIGN(salehead.djlb)));
			printLine(salehead.rqsj.split(" ")[0]);
		}
		
	}

	private void printKpInfo()
	{
		printLine(Convert.appendStringSize("", "=================================================", 1, 37, 38));
		printLine(Convert.appendStringSize("", "以下为您的付款明细", 0, 38, 38));
		printLine(Convert.appendStringSize("", "可凭此在一个月内开具发票", 0, 38, 38));

		// 打印付款区域
		printPay();

		// 打印赠品联
		printGift();

		printLine(Convert.appendStringSize("", "--------------------------------------------------------------------------------------------------", 0, 37, 38));

		String line = Convert.appendStringSize("", salehead.rqsj, 0, 10, 38);
		line = Convert.appendStringSize(line, "单据号:" + GlobalInfo.syjDef.syjh + "-" + String.valueOf(salehead.fphm), 11, 38, 38);
		printLine(line);
		// printLine(Convert.appendStringSize("",
		// "=================================================", 0, 37, 38));
		printLine(Convert.appendStringSize("", "品名                            实收金额", 0, 37, 38));

		for (int j = 0; j < salegoods.size(); j++)
		{
			SaleGoodsDef goodsDef = (SaleGoodsDef) salegoods.elementAt(j);
			// line = Convert.appendStringSize("", goodsDef.barcode, 0, 13, 38);
			line = Convert.appendStringSize("", String.valueOf(goodsDef.name), 0, 25, 38);
			line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString((goodsDef.hjje - goodsDef.hjzk) * SellType.SELLSIGN(salehead.djlb))), 30, 8, 38);
			printLine(line);
		}

		printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));

		printLine("谢谢惠顾，欢迎再次光临");
		printLine("THANK  YOU！ WELCOME  BACK  AGAIN！");
		printLine("======================================");

		
	}

	private boolean getPrintOrder()
	{
		//不存在文件时，默认原打印顺序
		if (!(new File(GlobalVar.ConfigPath + "//PrintOrder.ini").exists()))
			return false;

		BufferedReader br;
		//存在文件，但无内容时，不打印任何内容
		br = CommonMethod.readFile(GlobalVar.ConfigPath + "/PrintOrder.ini","GBK");
		if (br == null)
			return true;
		String line;
		String sp[];
		kpInfo = new Vector();
		carParkInfo = new Vector();
		customerBill = new Vector();
		try
		{
			while ((line = br.readLine()) != null)
			{
				if ((line == null) || (line.length() <= 0))
				{
					continue;
				}
				if((line.trim().indexOf(";"))==0) continue;
				
				String[] lines = line.split("=");
				if (lines.length < 1)
					continue;
				
				if(lines[0].trim().equals("[PrintOrder]")){
					sp = lines[1].split("\\|");
					if (sp.length < 1)
						continue;
					printOrder = sp;
					continue;
				}
				if(lines[0].trim().equals("[KpInfo]")){
					kpInfo.add(lines[1]);
					continue;
				}
				if(lines[0].trim().equals("[CarParkInfo]")){
					carParkInfo.add(lines[1]);
					continue;
				}
				if(lines[0].trim().equals("[CustomerBill]")){
					customerBill.add(lines[1]);
					continue;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return true;
	}

	private void printBKL()
	{
		if (salehead.hykh.length() <= 0 && SellType.ISSALE(salehead.djlb))
		{
			printLine(Convert.appendStringSize("", "当日凭此联可办理卓展VIP卡", 1, 37, 38, 2));
			String line = Convert.appendStringSize("", salehead.rqsj, 1, 20, 38);
			line = Convert.appendStringSize(line, "收银机:", 22, 7, 38);
			line = Convert.appendStringSize(line, GlobalInfo.syjDef.syjh, 30, 7, 38);
			printLine(line);
			line = Convert.appendStringSize("", "收银员:", 1, 7, 38);
			line = Convert.appendStringSize(line, GlobalInfo.posLogin.gh, 9, 10, 38);
			line = Convert.appendStringSize(line, "小票号:", 22, 7, 38);
			line = Convert.appendStringSize(line, String.valueOf(salehead.fphm), 30, 7, 38);
			printLine(line);
			printLine("消费金额：" + ManipulatePrecision.doubleToString(salehead.ysje));
			printLine(Convert.appendStringSize("", "=================================================", 1, 37, 38));
		}
	}

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;

		if ((Integer.parseInt(item.code)) == SBM_sl) // 数量
		{
			if (((SaleGoodsDef) salegoods.elementAt(index)).flag == '2') { return ""; }
			line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).sl * SellType.SELLSIGN(salehead.djlb), 4, 1, true);
			return line;
		}
		if ((Integer.parseInt(item.code)) == SBM_jg) // 数量
		{
			if (((SaleGoodsDef) salegoods.elementAt(index)).flag == '2')
				return "";
			line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).jg);
			return line;
		}

		switch (Integer.parseInt(item.code))
		{
		case thdh:
			if (SellType.ISBACK(salehead.djlb))
			{
				if (salegoods.size() > 0)
					line = ((SaleGoodsDef) salegoods.elementAt(0)).fhdd;
			}
			else
			{
				line = null;
			}
			break;
		case SBM_payname: // 付款方式名称
			SalePayDef pay1 = (SalePayDef) salepay.elementAt(index);

			line = pay1.payname;

			if (DataService.getDefault().searchPayMode(pay1.paycode).isbank == 'Y')
			{
				if (line.length() > 4)
				{
					line = line.substring(4);
				}
			}

			if (((SalePayDef) salepay.elementAt(index)).num4 > 1 && (CreatePayment.getDefault().isPaymentFjk(pay1.paycode) || DataService.getDefault().searchPayMode(pay1.paycode).type == '4'))
			{
				line += " * " + ManipulatePrecision.doubleToString(((SalePayDef) salepay.elementAt(index)).num4, 0, 0, true);
			}
			break;

		case SBM_payno: // 付款方式帐号
			SalePayDef pay = (SalePayDef) salepay.elementAt(index);

			if (!SellType.ISSALE(salehead.djlb))
				return null;

			if (CreatePayment.getDefault().isPaymentFjk(pay.paycode) || DataService.getDefault().searchPayMode(pay.paycode).type == '4')
			{
				line = "&!";
			}
			else
			{
				line = ((SalePayDef) salepay.elementAt(index)).payno;
				if (line == null || line.trim().length() <= 0)
				{
					line = "&!";
				}
			}

			break;
		case jfkc: // 扣除积分情况
			SalePayDef pay2 = (SalePayDef) salepay.elementAt(index);

			if (pay2.paycode.equals("0509") || pay2.paycode.equals("0508"))
			{
				line = pay2.idno.split(",")[0];
			}
			else
			{
				line = "&!";
			}

			break;
		case SBM_hjzke: // 折扣

			if (salehead.hjzke == 0)
			{
				line = null;
			}
			else
			{
				line = ManipulatePrecision.doubleToString(SellType.SELLSIGN(salehead.djlb) * salehead.hjzke);
			}

			break;
		case jfbl: // 积分倍率

			if (salehead.num4 == 0)
			{
				line = "&!";
			}
			else
			{
				line = ManipulatePrecision.doubleToString(salehead.num4);
			}

			break;

		case lqfq:

			try
			{
				if ((salehead.str2 != null) && (salehead.str2.split(",").length > 1))
				{
					String[] row1 = salehead.str2.split(",");
					double lje = Convert.toDouble(row1[2]);

					if (lje > 0)
					{
						line = ManipulatePrecision.doubleToString(lje);
					}
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}

			break;

		case SBM_sjfk: // 实收金额
			line = ManipulatePrecision.doubleToString((salehead.sjfk + salehead.num3) * SellType.SELLSIGN(salehead.djlb));

			break;
		case fqts:

			if ((salehead.str2 != null) && (salehead.str2.split(",").length > 3))
			{
				line = salehead.str2.split(",")[3];
			}

			break;

		case aqfq:

			try
			{
				if ((salehead.str2 != null) && (salehead.str2.split(",").length > 1))
				{
					String[] row1 = salehead.str2.split(",");
					double lje = Convert.toDouble(row1[0]);

					if (lje > 0)
					{
						line = ManipulatePrecision.doubleToString(lje);
					}
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}

			break;

		case bqfq:

			try
			{
				if ((salehead.str2 != null) && (salehead.str2.split(",").length > 1))
				{
					String[] row = salehead.str2.split(",");
					double lje = Convert.toDouble(row[1]);

					if (lje > 0)
					{
						line = ManipulatePrecision.doubleToString(lje);
					}
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}

			break;

		case qtzk:

			try
			{
				double qtzk = ((SaleGoodsDef) salegoods.elementAt(index)).hjzk - ((SaleGoodsDef) salegoods.elementAt(index)).hyzke;

				if (qtzk > 0)
				{
					line = ManipulatePrecision.doubleToString(SellType.SELLSIGN(salehead.djlb) * qtzk);
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}

			break;
		case lszke:

			try
			{
				double hyzke = ((SaleGoodsDef) salegoods.elementAt(index)).lszke;

				if (hyzke > 0)
				{
					line = ManipulatePrecision.doubleToString(SellType.SELLSIGN(salehead.djlb) * hyzke);
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}
		case lszre:

			try
			{
				double hyzke = ((SaleGoodsDef) salegoods.elementAt(index)).lszre;

				if (hyzke > 0)
				{
					line = ManipulatePrecision.doubleToString(SellType.SELLSIGN(salehead.djlb) * hyzke);
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}

			break;
		case lszzk:

			try
			{
				double hyzke = ((SaleGoodsDef) salegoods.elementAt(index)).lszzk;

				if (hyzke > 0)
				{
					line = ManipulatePrecision.doubleToString(SellType.SELLSIGN(salehead.djlb) * hyzke);
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}

			break;
		case yhzke:

			try
			{
				double hyzke = ((SaleGoodsDef) salegoods.elementAt(index)).yhzke;

				if (hyzke > 0)
				{
					line = ManipulatePrecision.doubleToString(SellType.SELLSIGN(salehead.djlb) * hyzke);
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}

			break;

		case zszke:

			try
			{
				double hyzke = ((SaleGoodsDef) salegoods.elementAt(index)).zszke;

				if (hyzke > 0)
				{
					line = ManipulatePrecision.doubleToString(SellType.SELLSIGN(salehead.djlb) * hyzke);
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}

			break;
		case hyzke:

			try
			{
				double hyzke = ((SaleGoodsDef) salegoods.elementAt(index)).hyzke;

				if (hyzke > 0)
				{
					line = ManipulatePrecision.doubleToString(SellType.SELLSIGN(salehead.djlb) * hyzke);
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}

			break;
		case zjfkc:

			try
			{
				double jfkc_num = 0;
				for (int i = 0; i < originalsalepay.size(); i++)
				{
					SalePayDef pay3 = (SalePayDef) originalsalepay.elementAt(i);

					if (pay3.paycode.equals("0509") || pay3.paycode.equals("0508"))
					{
						if (GlobalInfo.sysPara.custompayobj.indexOf("PaymentJfNew") >= 0)
						{
							jfkc_num += Convert.toDouble(pay3.idno.split(",")[1]);
						}
						else
						{
							jfkc_num += Convert.toDouble(pay3.idno.split(",")[0]);
						}
					}
				}

				if (jfkc_num > 0)
					line = ManipulatePrecision.doubleToString(jfkc_num * SellType.SELLSIGN(salehead.djlb));
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}

			break;
		case SBM_ybje: // 付款方式金额
			SalePayDef spd = (SalePayDef) salepay.elementAt(index);
			line = ManipulatePrecision.doubleToString((spd.ybje - ManipulatePrecision.div(spd.num1, spd.hl)) * SellType.SELLSIGN(salehead.djlb));

			break;

		case SBM_ye: // 付款余额
			if (((SalePayDef) salepay.elementAt(index)).kye <= 0 || "99".equals(((SalePayDef) salepay.elementAt(index)).str4))
			{
				line = null;
			}
			else
			{
				line = ManipulatePrecision.doubleToString(((SalePayDef) salepay.elementAt(index)).kye);
			}
			break;

		case SBM_inputbarcode:
			if (GlobalInfo.syjDef.priv != null && GlobalInfo.syjDef.priv.trim().length() > 0)
			{
				line = ((SaleGoodsDef) salegoods.elementAt(index)).code;
			}
			else
			{
				if (((SaleGoodsDef) salegoods.elementAt(index)).inputbarcode != null && ((SaleGoodsDef) salegoods.elementAt(index)).inputbarcode.trim().length() > 0)
				{
					line = ((SaleGoodsDef) salegoods.elementAt(index)).inputbarcode;
				}
				else if (GlobalInfo.syjDef.issryyy == 'N')
				{
					line = ((SaleGoodsDef) salegoods.elementAt(index)).barcode;
				}
				else
				{
					line = ((SaleGoodsDef) salegoods.elementAt(index)).code;
				}
			}
			break;
		}

		return line;
	}

	protected void printAppendBill()
	{
		// 检查是否有未打印的银联签购单
		if (PaymentBank.haveXYKDoc)
		{
			printBankBill();
		}

		// 打印赠券联
		printSaleTicketMSInfo();
	}

	// 打印面值卡联
	public void printMZKBill(int type)
	{
		int i = 0;

		if (SellType.ISSALE(salehead.djlb) && type == 3) { return; }

		if (!SellType.ISSALE(salehead.djlb) && type != 3) { return; }

		if (GlobalInfo.sysPara.mzkbillnum <= 0) { return; }

		try
		{
			// 先检查是否有需要打印的付款方式
			for (i = 0; i < originalsalepay.size(); i++)
			{
				SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
				PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

				if (mode == null)
				{
					continue;
				}

				if ((type == 1) && (mode.type == '4'))
				{
					break;
				}

				if ((type == 2) && CreatePayment.getDefault().isPaymentFjk(mode.code))
				{
					break;
				}

				if ((type == 3) && (mode.code.equals("0702") || mode.type == '4'))
				{
					break;
				}
			}

			if (i >= originalsalepay.size()) { return; }

			for (int n = 0; n < GlobalInfo.sysPara.mzkbillnum; n++)
			{
				// 开始新打印
				printStart();

				if (type == 1)
				{
					printLine("\n             预收款联");
				}

				if (type == 2)
				{
					printLine("\n             返券卡联");
				}

				if (type == 3)
				{
					printLine("\n             退货卡联");
				}

				printLine("门店号:" + GlobalInfo.sysPara.mktcode);
				if (salehead.hykh.length() > 0)
					printLine("VIP卡号：" + salehead.hykh);
				printLine("卡号          消费金额          卡余额");
				printLine(Convert.appendStringSize("", "=================================================", 0, 38, 38));
				int num = 0;
				double hj = 0;
				String line = null;

				for (i = 0; i < originalsalepay.size(); i++)
				{
					SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
					PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);
					/**
					 * if ((type == 3) &&
					 * (mode.code.equals("0702") || mode.type == '4'))
					 * {
					 * hj += (pay.ybje * pay.hl);
					 * }
					 */

					if (((type == 1 || type == 3) && (mode.type == '4')) || ((type == 2) && (CreatePayment.getDefault().isPaymentFjk(mode.code))))
					{
						num++;
						String ty = "";
						if (type == 2 && pay.idno.length() > 0)
						{
							try
							{
								if (pay.idno.charAt(0) == '1')
								{
									ty = "(A)";
								}
								else if (pay.idno.charAt(0) == '2')
								{
									ty = "(B)";
								}
								else if (pay.idno.charAt(0) == '3')
								{
									ty = "(F)";
								}
								else
								{
									ty = String.valueOf(pay.idno.charAt(0));
								}
							}
							catch (Exception er)
							{
								er.printStackTrace();
							}
						}

						line = Convert.appendStringSize("", pay.payno + ty, 0, 17, 40, 0);
						line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(pay.ybje * SellType.SELLSIGN(salehead.djlb)), 20, 7, 40, 0);
						if (!"99".equals(pay.str4))
						{
							line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(pay.kye), 31, 7, 40, 0);
						}
						printLine(line);

						if (pay.hl == 0)
						{
							pay.hl = 1;
						}

						hj += (pay.ybje * pay.hl);
					}
				}
				printLine(Convert.appendStringSize("", "=================================================", 0, 38, 38));
				if (type == 1)
				{
					printLine("本次共         " + num + " 次预收款消费");
				}

				if (type == 2)
				{
					printLine("本次共         " + num + " 次返券卡消费");
				}

				if (type == 3)
				{
					printLine("本次共         " + num + " 次退货卡退货");
				}

				printLine("合计消费金额    " + ManipulatePrecision.doubleToString(hj * SellType.SELLSIGN(salehead.djlb)));
				printLine(Convert.appendStringSize("", "=================================================", 0, 38, 38));
				line = Convert.appendStringSize("", salehead.rqsj, 0, 20, 38);
				line = Convert.appendStringSize(line, "收银机:", 22, 7, 38);
				line = Convert.appendStringSize(line, GlobalInfo.syjDef.syjh, 30, 7, 38);
				printLine(line);
				line = Convert.appendStringSize("", "收银员:", 0, 7, 38);
				line = Convert.appendStringSize(line, GlobalInfo.posLogin.gh, 9, 10, 38);
				line = Convert.appendStringSize(line, "小票号:", 22, 7, 38);
				line = Convert.appendStringSize(line, String.valueOf(salehead.fphm), 30, 7, 38);
				printLine(line);
				printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));

				if (type == 3)
					printCutPaper();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	protected Vector convertPayDetail(Vector p)
	{
		// 减找零付款
		boolean calcSjfk = false;
		for (int i = 0; i < Pay.size(); i++)
		{
			PrintTemplateItem item = (PrintTemplateItem) Pay.elementAt(i);
			if (item.code.trim().equals(String.valueOf(SBM_payfkje)))
			{
				calcSjfk = true;
			}
		}

		if (calcSjfk)
		{
			SalePayDef pay1 = null;
			SalePayDef pay2 = null;

			for (int k = 0; k < p.size(); k++)
			{
				pay1 = (SalePayDef) p.elementAt(k);

				if (pay1.flag == '1')
				{
					for (int i = 0; i < p.size(); i++)
					{
						pay2 = (SalePayDef) p.elementAt(i);

						if ((pay2.flag == '2') && pay2.paycode.equals(pay1.paycode) && (pay2.ybje != 0))
						{
							if (pay1.ybje > pay2.ybje)
							{
								pay1.ybje = pay1.ybje - pay2.ybje;
								pay2.ybje = 0;
							}
							else if (pay1.ybje <= pay2.ybje)
							{
								pay2.ybje = pay2.ybje - pay1.ybje;
								pay1.ybje = 0;
								p.removeElementAt(k);
								k--;
							}

							break;
						}
					}
				}
			}
		}

		// 分组汇总相同的付款方式
		Vector newp = new Vector();
		SalePayDef spd = null;
		SalePayDef spd1 = null;

		for (int i = 0; i < p.size(); i++)
		{
			spd = (SalePayDef) p.elementAt(i);

			int j = 0;

			for (j = 0; j < newp.size(); j++)
			{
				spd1 = (SalePayDef) newp.elementAt(j);

				if (spd.paycode.equals(spd1.paycode) && (spd.flag == spd1.flag))
				{
					if (convertPayDetail(spd, spd1))
					{
						break;
					}
				}
			}

			if (j >= newp.size())
			{
				newp.add(spd.clone());
			}
			else
			{
				// 金额汇总
				spd1.ybje += spd.ybje;
				spd1.je += spd.je;
				spd1.num1 += spd.num1;

				// 如果汇总的付款方式帐号不一致,则清除记录的付款方式帐号
				if (!spd.payno.equals(spd1.payno))
				{
					spd1.payno = "";
				}
			}
		}

		return newp;
	}
}
