package custom.localize.Cczz;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

import jpos.JposException;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_SaleBillMode;
import device.Printer.ZZBJ_IBMJPOS_Printer;

public class Cczz_SaleBillMode extends Bcrm_SaleBillMode
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
	
	protected static LinkedList saleHeadList = new LinkedList(); //记录单据号和开票类型
	protected static LinkedList dzDjList = new LinkedList(); //电子单商品
	protected static LinkedList sgDjList = new LinkedList(); //手工单商品
	protected static LinkedList fyqzDjList = new LinkedList(); //非延期提货电子单据
	
	protected Vector groupset = new Vector();
	protected String message = "";
	protected GroupDef curgroup = null;
	
	protected String[] printOrder = null; //读取打印顺序
	protected boolean isOrder = false;    //是否排序打印
	protected Vector kpInfo = null;
	protected Vector carParkInfo = null;
	protected Vector customerBill = null;
	private int ckPrintCount = 1;

	public void setSaleTicketMSInfo(SaleHeadDef sh, Vector gifts)
	{
		// 记录小票赠送清单
		this.salemsinvo = sh.fphm;
		this.salemsgift = gifts;

		// 分解赠品清单
		Vector goodsinfo = new Vector();
		Vector fj = new Vector();
		for (int i = 0; gifts != null && i < gifts.size(); i++)
		{
			GiftGoodsDef g = (GiftGoodsDef) gifts.elementAt(i);

			if (g.type.trim().equals("0"))
			{
				//无促销
				break;
			}
			else if (g.type.trim().equals("1") || g.type.trim().equals("2"))
			{
				fj.add(g);
			}
			else if (g.type.trim().equals("3"))
			{
				goodsinfo.add(g);
			}
			else if (g.type.trim().equals("4"))
			{
				fj.add(g);
			}
			else if (g.type.trim().equals("11"))
			{
				fj.add(g);
			}
			else if (g.type.trim().equals("99")) // 电子券内的券余额
			{
				fj.add(g);
			}
			else if (g.type.trim().equals("98")) // 返券参加活动的信息
			{
				goodsinfo.add(g);
			}
			else if (g.type.trim().equals("999")) // 定向积分
			{
				fj.add(g);
			}
		}

		if (fj.size() > 0)
		{
			StringBuffer buff = new StringBuffer();
			double je = 0;
			for (int i = 0; i < fj.size(); i++)
			{
				GiftGoodsDef g = (GiftGoodsDef) fj.elementAt(i);
				if (g.type.trim().equals("4"))
				{
					String rows[] = g.info.split(",");
					for (int x = 0; x < rows.length; x++)
					{
						String lines = rows[x];
						if (lines.split(":").length >= 2)
						{
							buff.append(Convert.appendStringSize("", lines.split(":")[0], 0, 14, 14, 1) + ":"
									+ Convert.appendStringSize("", lines.split(":")[1], 0, 14, 14) + "\n");
						}
					}
					je += g.je;
				}
				else if (!g.type.trim().equals("99") && !g.type.trim().equals("999"))
				{
					buff.append(Convert.appendStringSize("", g.info, 0, 14, 14, 1) + " : "
							+ Convert.appendStringSize("", String.valueOf(g.je), 0, 10, 10) + "\n");
					je += g.je;
				}
			}

			if (je > 0)
			{
				buff.append("\n返券总金额为 " + ManipulatePrecision.doubleToString(je));
				new MessageBox(buff.toString());
			}
		}

		if (fj.size() > 0) this.zq = fj;
		else this.zq = null;
		if (goodsinfo.size() > 0) this.gift = goodsinfo;
		else this.gift = null;
	}

	public boolean needMSInfoPrintGrant()
	{
		if (this.zq != null)
		{
			for (int i = 0; i < this.zq.size(); i++)
			{
				GiftGoodsDef g = (GiftGoodsDef) this.zq.elementAt(i);
				if (!g.type.trim().equals("99") && !g.type.trim().equals("4")) { return true; }
			}
		}

		if (this.gift != null)
		{
			for (int i = 0; i < this.gift.size(); i++)
			{
				GiftGoodsDef g = (GiftGoodsDef) this.gift.elementAt(i);
				if (!g.type.trim().equals("98")) { return true; }
			}
		}

		return false;
	}

	public void printBill()
	{
		
		if (GlobalInfo.syjDef.issryyy == 'N')
		{
			GlobalInfo.statusBar.setHelpMessage("本笔商品总数：" + salehead.hjzsl);
		}
		int choice = GlobalVar.Key1;

		// 非超市且是重打印时提示选择打印部分
		if (('N' != (GlobalInfo.syjDef.issryyy)) && (salehead.printnum > 0))
		{
			StringBuffer info = new StringBuffer();

			info.append(Convert.appendStringSize("", "请按键选择重打印内容", 1, 30, 30, 2) + "\n");
			info.append(Convert.appendStringSize("", "1、打印全部小票单据", 1, 30, 30, 2) + "\n");
			info.append(Convert.appendStringSize("", "2、只打印机制小票单", 1, 30, 30, 2) + "\n");
			info.append(Convert.appendStringSize("", "3、只打印营业员列印", 1, 30, 30, 2) + "\n");
			info.append(Convert.appendStringSize("", "按其他键则放弃重打印", 1, 30, 30, 2) + "\n");

			choice = new MessageBox(info.toString(), null, false).verify();
		}

		ckPrintCount = 1;
		if (GlobalInfo.sysPara.printMode == 'B')
		{
			if ((choice == GlobalVar.Key1) || (choice == GlobalVar.Key3))
			{
				// 打印营业员分单联
				printYYYBill();
			}

			if ((choice == GlobalVar.Key1) || (choice == GlobalVar.Key2))
			{
				// 打印交易小票联
				if ((GlobalInfo.syjDef.priv != null) && (GlobalInfo.syjDef.priv.trim().length() > 0))
				{
					for (int i = 0; i < 2; i++)
					{
						printSellBill();
						ckPrintCount++;
					}
				}
				else
				{
					printSellBill();
				}

//				打印退货卡联
				printMZKBill(3);

				printSaleTicketMSInfo();

				// 打印附加的各个小票联
				// printAppendBill();
			}
		}
		else
		{
			if ((choice == GlobalVar.Key1) || (choice == GlobalVar.Key2))
			{
				// 打印交易小票联
				if ((GlobalInfo.syjDef.priv != null) && (GlobalInfo.syjDef.priv.trim().length() > 0))
				{
					for (int i = 0; i < 2; i++)
					{
						printSellBill();
						ckPrintCount++;
					}
				}
				else
				{
					printSellBill();
				}

//				打印退货卡联
				printMZKBill(3);

				printSaleTicketMSInfo();

				// 打印附加的各个小票联
				// printAppendBill();
			}

			if ((choice == GlobalVar.Key1) || (choice == GlobalVar.Key3))
			{
				// 打印营业员分单联
				printYYYBill();
			}
		}
		ckPrintCount = 1;
	}

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;

		switch (Integer.parseInt(item.code))
		{
			case thdh:
				if (SellType.ISBACK(salehead.djlb))
				{
					if (salegoods.size() > 0) line = ((SaleGoodsDef) salegoods.elementAt(0)).fhdd;
				}
				else
				{
					line = null;
				}
				break;
			case SBM_payname: //付款方式名称
				SalePayDef pay1 = (SalePayDef) salepay.elementAt(index);

				line = pay1.payname;

				if (DataService.getDefault().searchPayMode(pay1.paycode).isbank == 'Y')
				{
					if (line.length() > 4)
					{
						line = line.substring(4);
					}
				}

				if (((SalePayDef) salepay.elementAt(index)).num4 > 1
						&& (CreatePayment.getDefault().isPaymentFjk(pay1.paycode) || DataService.getDefault().searchPayMode(pay1.paycode).type == '4'))
				{
					line += " * " + ManipulatePrecision.doubleToString(((SalePayDef) salepay.elementAt(index)).num4, 0, 0, true);
				}
				break;

			case SBM_payno: //付款方式帐号
				SalePayDef pay = (SalePayDef) salepay.elementAt(index);

				if (!SellType.ISSALE(salehead.djlb)) return null;

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
			case jfkc: //扣除积分情况
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
			case SBM_hjzke: //折扣

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

			case SBM_sjfk: //实收金额
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
							if(GlobalInfo.sysPara.custompayobj.indexOf("PaymentJfNew") >= 0)
							{
								jfkc_num += Convert.toDouble(pay3.idno.split(",")[1]);
							}
							else
							{
								jfkc_num += Convert.toDouble(pay3.idno.split(",")[0]);
							}
						}
					}

					if (jfkc_num > 0) line = ManipulatePrecision.doubleToString(jfkc_num * SellType.SELLSIGN(salehead.djlb));
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}

				break;
			case SBM_ybje: //付款方式金额
				SalePayDef spd = (SalePayDef) salepay.elementAt(index);
				line = ManipulatePrecision.doubleToString((spd.ybje - ManipulatePrecision.div(spd.num1, spd.hl)) * SellType.SELLSIGN(salehead.djlb));
				if (spd.paycode.equals("0508") || spd.paycode.equals("0509")) line += "(元)";
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

	// 卓展北京店列印打印
	public void printYYYBillBj()
	{
		//如果不为销售小票，不打印平推联
		if (!SellType.ISSALE(salehead.djlb) && !SellType.ISBACK(salehead.djlb)) { return; }

		int i = 0;
		SaleGoodsDef sgd = null;

		// 系统参数定义为打印分单且是营业员小票，才打印营业员联
		if (!((GlobalInfo.sysPara.fdprintyyy == 'Y') && ((GlobalInfo.syjDef.issryyy == 'Y') || ((GlobalInfo.syjDef.issryyy == 'A') && !((SaleGoodsDef) salegoods
																																								.elementAt(0)).yyyh
																																													.equals("超市"))))) { return; }

		StringBuffer sb1;
		StringBuffer sb2;
		StringBuffer sb3;
		//		StringBuffer sb4;
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
			//			sb4 = new StringBuffer();
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

			sb6.append(appendSpaceC + "VIP折扣:" + ManipulatePrecision.doubleToString(hyzke) + " 折扣:"
					+ ManipulatePrecision.doubleToString(SellType.SELLSIGN(salehead.djlb) * zk) + "\r\n");
			sb6.append(appendSpaceC + "小计:" + ManipulatePrecision.doubleToString(xj) + " 应收金额:" + ManipulatePrecision.doubleToString(ysje) + "\r\n");
			sb6.append(appendSpaceC + "本小票使用的付款方式:\r\n");

			String line2 = "";

			//如果商品不收券，不打印券付款
			double aq = 0, bq = 0;

			if (sgd.memo.trim().length() > 0)
			{
				String[] a = sgd.memo.split(",");

				if (a.length > 0) aq = Convert.toDouble(a[0]);

				if (a.length > 1) bq = Convert.toDouble(a[1]);

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

				if ((CreatePayment.getDefault().isPaymentFjk(spd.paycode) && spd.idno.trim().length() > 0 && spd.idno.charAt(0) == '1' && aq == 0)
						|| (CreatePayment.getDefault().isPaymentFjk(spd.paycode) && spd.idno.trim().length() > 0 && spd.idno.charAt(0) == '2' && bq == 0))
				{
					continue;
				}

				try
				{
					if (CreatePayment.getDefault().isPaymentFjk(spd.paycode))
					{
						// 检查商品是否可以分摊
						String ft = sgd.str2;
						if (ft == null || ft.length() <= 0) continue;

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

						if (kk >= ft1.length) continue;
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
					Printer.getDefault().printLine_Slip(
														" "
																+ "   "
																+ newSgd.barcode
																+ " "
																+ ManipulatePrecision.doubleToString(newSgd.sl * SellType.SELLSIGN(salehead.djlb), 4,
																										1, true)
																+ " "
																+ ManipulatePrecision.doubleToString(newSgd.jg)
																+ " "
																+ ManipulatePrecision.doubleToString(SellType.SELLSIGN(salehead.djlb)
																		* ManipulatePrecision.sub(newSgd.hjje, newSgd.hjzk)) + "\r\n");
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
		if (GlobalInfo.sysPara.mktcode.equals("002,205")
				|| Printer.getDefault().printer.getClass().getName().equals("device.Printer.ZZBJ_IBMJPOS_Printer"))
		{
			printYYYBillBj();
		}
		else
		{
			boolean done = false;
			//如果不为销售小票，不打印平推联
			if (!SellType.ISSALE(salehead.djlb) && !SellType.ISBACK(salehead.djlb)) { return; }

			int i = 0;
			String appendSpace = "";
			SaleGoodsDef sgd = null;
			String line;

			// 系统参数定义为打印分单且是营业员小票，才打印营业员联
			if (!((GlobalInfo.sysPara.fdprintyyy == 'Y') && ((GlobalInfo.syjDef.issryyy == 'Y') || ((GlobalInfo.syjDef.issryyy == 'A') && !((SaleGoodsDef) salegoods
																																									.elementAt(0)).yyyh
																																														.equals("超市"))))) { return; }

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
				Printer.getDefault().printLine_Slip(
													appendSpace + "收银员:" + salehead.syyh + "          "
															+ SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead));

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
					line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(newSgd.sl * SellType.SELLSIGN(salehead.djlb), 4, 1,
																								true), 24, 4, Width, 0);
					line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(newSgd.jg), 15, 8, Width, 1);
					line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(SellType.SELLSIGN(salehead.djlb)
							* ManipulatePrecision.sub(newSgd.hjje, newSgd.hjzk)), 29, 9, Width, 1);
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

				//如果商品不收券，不打印券付款
				double aq = 0, bq = 0;

				if (sgd.memo.trim().length() > 0)
				{
					String[] a = sgd.memo.split(",");

					if (a.length > 0) aq = Convert.toDouble(a[0]);

					if (a.length > 1) bq = Convert.toDouble(a[1]);

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

					if ((CreatePayment.getDefault().isPaymentFjk(spd.paycode) && spd.idno.trim().length() > 0 && spd.idno.charAt(0) == '1' && aq == 0)
							|| (CreatePayment.getDefault().isPaymentFjk(spd.paycode) && spd.idno.trim().length() > 0 && spd.idno.charAt(0) == '2' && bq == 0))
					{
						continue;
					}

					try
					{
						if (CreatePayment.getDefault().isPaymentFjk(spd.paycode))
						{
							// 检查商品是否可以分摊
							String ft = sgd.str2;
							if (ft == null || ft.length() <= 0) continue;

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

							if (kk >= ft1.length) continue;
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

			printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));

			for (int n = 0; n < GlobalInfo.sysPara.mzkbillnum; n++)
			{
				// 开始新打印
				printStart();

				if (salehead.printnum > 1)
				{
					printLine("\n           **重印**");
				}
				
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
				if (salehead.hykh.length() > 0) printLine("VIP卡号：" + salehead.hykh);
				printLine("卡号          消费金额          卡余额");
				printLine(Convert.appendStringSize("", "-------------------------------------------------", 0, 38, 38));
				int num = 0;
				double hj = 0;
				String line = null;

				for (i = 0; i < originalsalepay.size(); i++)
				{
					SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
					PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);
					/**
					 if ((type == 3) &&
					 (mode.code.equals("0702") || mode.type == '4'))
					 {
					 hj += (pay.ybje * pay.hl);
					 }*/

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
						line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(pay.ybje * SellType.SELLSIGN(salehead.djlb)), 20, 7,
														40, 0);
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
				printLine(Convert.appendStringSize("", "-------------------------------------------------", 0, 38, 38));
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
				printLine(Convert.appendStringSize("", "-------------------------------------------------", 0, 38, 38));
				line = Convert.appendStringSize("", salehead.rqsj, 0, 20, 38);
				line = Convert.appendStringSize(line, "收银机:", 22, 7, 38);
				line = Convert.appendStringSize(line, GlobalInfo.syjDef.syjh, 30, 7, 38);
				printLine(line);
				line = Convert.appendStringSize("", "收银员:", 0, 7, 38);
				line = Convert.appendStringSize(line, GlobalInfo.posLogin.gh, 9, 10, 38);
				line = Convert.appendStringSize(line, "小票号:", 22, 7, 38);
				line = Convert.appendStringSize(line, String.valueOf(salehead.fphm), 30, 7, 38);
				printLine(line);

				if (type == 3) printCutPaper();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void printTotal()
	{
		if (salehead.str2.length() > 0)
		{
			printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
			printLine(salehead.str2);
		}

		super.printTotal();
	}
	
	/*
	private double calcBxjf(double jf)
	{
		if (zq == null || zq.size() <= 0) return jf;
		GiftGoodsDef gift = null;
		for (int i = 0; i < zq.size(); i++)
		{
			gift = (GiftGoodsDef) zq.get(i);
			if (gift.type.trim().equals("999"))
			{
				//jf -= gift.je * SellType.SELLSIGN(salehead.djlb);
				jf -= gift.je;
			}
		}
		
		SalePayDef sp = null;
		for (int i = 0; i < salepay.size(); i++)
		{
			sp = (SalePayDef)salepay.get(i);
			if (sp.paycode.equals("0508") || sp.paycode.equals("0509"))
			{
//				System.out.println(sp.idno);
				String[] idno = sp.idno.split(",");
				if (idno.length > 0)
				{
					double jf1 = Double.parseDouble(idno[1]);
					//jf -= jf1 * SellType.SELLSIGN(salehead.djlb);
					jf -= jf1;
				}
			}
		}
		
		return jf;
	}
*/
	public void printBottom()
	{
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
//				double bxjf = calcBxjf(salehead.num4);
				double bxjf = salehead.num4;
				printLine("倍享积分：" + ManipulatePrecision.doubleToString(bxjf));
				printLine(salehead.memo);
				printLine("普通积分累计：" + salehead.ljjf);
				if (zq != null && zq.size() > 0)
				{
					// 定向积分明细
					boolean printTitle = false;
					for (int i = 0; i < zq.size(); i++)
					{
						GiftGoodsDef gift = (GiftGoodsDef) zq.elementAt(i);
						if (gift.type.trim().equals("999"))
						{
							String line = "";
							if (!printTitle)
							{
								line = "本笔定向积分：\n";
								printTitle = true;
							}
							line += "  " + gift.info + ":" + gift.je + "\n";
							line += "  积分有效期:" + gift.memo;
							printLine(line);
						}
					}
				}
				if (salehead.str5 != null && salehead.str5.length() > 0) printLine(salehead.str5);
			}
			else
			{
				printLine("消费积分：请到服务台查询");
			}

			if (zq == null || zq.size() <= 0)
			{
				AccessDayDB.getDefault().writeWorkLog(salehead.fphm + " 没有找到返券信息，无法打印券信息");
			}
			else
			{
				boolean su = false;
				for (int i = 0; i < zq.size(); i++)
				{
					GiftGoodsDef gift = (GiftGoodsDef) zq.elementAt(i);
					if (gift.type.trim().equals("99"))
					{
						su = true;
						String line = "有效赠券：\n";
						String rows[] = gift.info.split(",");
						for (int x = 0; x < rows.length; x++)
						{
							String lines = rows[x];
							if (lines.split(":").length >= 2)
							{
								line += Convert.appendStringSize("", lines.split(":")[0], 0, 14, 14, 1)
										+ ":"
										+ Convert.appendStringSize("", ManipulatePrecision.doubleToString(Convert.toDouble(lines.split(":")[1])), 0,
																	14, 14) + "\n";
							}
						}
						printLine(line);
					}
				}

				if (!su && ConfigClass.DebugMode) AccessDayDB.getDefault().writeWorkLog("过程中没有返回有效券信息，无法打印");

				printLine(Convert.appendStringSize("", "=================================================", 0, 38, 38));

				su = false;
				// 打印获券信息
				for (int i = 0; i < zq.size(); i++)
				{
					GiftGoodsDef gift = (GiftGoodsDef) zq.elementAt(i);
					if (gift.type.trim().equals("4"))
					{
						su = true;
						String line = "";
						if (SellType.ISCOUPON(salehead.djlb)) line = "本笔买券信息: \n";
						else line = "本笔赠券信息：\n";
						String rows[] = gift.info.split(",");
						for (int x = 0; x < rows.length; x++)
						{
							String lines = rows[x];
							if (lines.split(":").length >= 2)
							{
								line += Convert.appendStringSize("", lines.split(":")[0], 0, 14, 14, 1) + ":"
										+ Convert.appendStringSize("", lines.split(":")[1], 0, 14, 14) + "\n" + "本券有效期: "
										+ Convert.appendStringSize("", lines.split(":")[2], 0, 30, 30) + "\n";
							}
						}
						printLine(line);
						printLine("券总额: " + ManipulatePrecision.doubleToString(gift.je));

						break;
					}
				}

				if (!su && ConfigClass.DebugMode) AccessDayDB.getDefault().writeWorkLog("过程中没有返回本笔返券有效信息，无法打印");
			}

			if (gift != null)
			{
				for (int k = 0; k < gift.size(); k++)
				{
					GiftGoodsDef gift1 = (GiftGoodsDef) gift.elementAt(k);
					if (gift1.type.trim().equals("98"))
					{
						// 记录后台返回的打印信息
						String line1 = gift1.memo;

						if (line1.length() > 0)
						{
							for (int i = 0; i < line1.split(";").length; i++)
							{
								printLine(line1.split(";")[i]);
							}
						}
					}
				}
			}
			else
			{
				if (ConfigClass.DebugMode) AccessDayDB.getDefault().writeWorkLog(salehead.fphm + " 没有取到返券信息");
			}
		}
		else
		{
			if (zq == null || zq.size() <= 0)
			{
				AccessDayDB.getDefault().writeWorkLog(salehead.fphm + " 没有找到返券信息，无法打印券信息");
			}
			else
			{
				printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
				String line = "";
				double je = 0;
				String ex_date = "";
				for (int i = 0; i < zq.size(); i++)
				{
					GiftGoodsDef gift = (GiftGoodsDef) zq.elementAt(i);

					if (!gift.type.trim().equals("99") && !gift.type.trim().equals("4"))
					{
						ex_date = gift.memo;
						line += String.valueOf(Convert.appendStringSize("", gift.info, 0, 14, 14, 1) + " : "
								+ Convert.appendStringSize("", String.valueOf(gift.je), 0, 10, 10) + "\n")
								+ "本券有效期: " + Convert.appendStringSize("", ex_date, 0, 30, 30) + "\n";
						je += gift.je;
					}
				}
				printLine(line);
				printLine("券总额: " + ManipulatePrecision.doubleToString(je));
				printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
			}
		}
		
		
		if ((GlobalInfo.syjDef.priv != null) && (GlobalInfo.syjDef.priv.trim().length() > 0))
		{
			if (ckPrintCount == 2)
			{
				printLine("配餐联");
			}
			if (ckPrintCount == 1)
			{
				printLine("顾客联");
				printLine("敬请当日内携带顾客联开发票");
			}
		}
		/**
		 if (salehead.hykh.length() > 0 && SellType.ISSALE(salehead.djlb))
		 {
		 printLine(Convert.appendStringSize("", "当日凭此联可办理卓展VIP卡", 1, 37, 38,2));
		 String line = Convert.appendStringSize("", salehead.rqsj, 1, 20, 38);
		 line = Convert.appendStringSize(line, "收银机:", 22, 7, 38);
		 line = Convert.appendStringSize(line, GlobalInfo.syjDef.syjh, 30, 7, 38);
		 printLine(line);
		 line = Convert.appendStringSize("", "收银员:", 1, 7, 38);
		 line = Convert.appendStringSize(line, GlobalInfo.posLogin.gh, 9, 10, 38);
		 line = Convert.appendStringSize(line, "小票号:", 22, 7, 38);
		 line = Convert.appendStringSize(line, String.valueOf(salehead.fphm), 30, 7, 38);
		 printLine(line);
		 printLine("消费金额："+ManipulatePrecision.doubleToString(salehead.ysje));
		 printLine(Convert.appendStringSize("", "=================================================", 1, 37, 38));
		 }*/
		// 打印小票消费排名信息
		if (salehead.str3 != null && salehead.str3.trim().length() > 0)
		{
			Printer.getDefault().printLine_Normal(salehead.str3);
			printLine(Convert.appendStringSize("", "=================================================", 1, 37, 38));
		}
		
		  
        if(sgDjList.size() > 0 || fyqzDjList.size() > 0)
	     {
	    	printLine(Convert.appendStringSize("", "凭以上收银联办理退换货，请妥善保管", 0, 37, 38));
			printLine(Convert.appendStringSize("", "客服电话：88198096，88198098 ", 0, 37, 38));
			
	     }

//		//打印面值卡联
//		printMZKBill(1);
//
//		//打印返券卡联
//		printMZKBill(2);
//
////		printPopBill();
////		 办卡联
//		printBKL();
		
	}

	private void printBKL()
	{
		double fje = 0;
		if (SellType.ISSALE(salehead.djlb))
		{
			if (GlobalInfo.sysPara.mjPaymentRule.length() > 0)
			{
				for (int i = 0; i < salepay.size(); i++)
				{
					SalePayDef spd = (SalePayDef) salepay.elementAt(i);
					boolean done = false;
					for (int j = 0; j < GlobalInfo.sysPara.mjPaymentRule.split(",").length; j++)
					{
						try
						{
							if (GlobalInfo.sysPara.mjPaymentRule.split(",")[j].equals(spd.paycode))
							{
								done = true;
								break;
							}

						}
						catch (Exception er)
						{
							er.printStackTrace();
						}
					}
					if (!done) fje += ManipulatePrecision.doubleConvert(spd.je - spd.num1);
				}

				if (salehead.zl > 0 && fje > 0)
				{
					fje -= salehead.zl;
				}
			}
			else
			{
				fje = ManipulatePrecision.doubleConvert(salehead.ysje);
			}
		}

		if ((salehead.hykh == null || salehead.hykh.length() <= 0 || (salehead.hykh.length() > 0 && salehead.buyerinfo.equals("Y")))
				&& SellType.ISSALE(salehead.djlb) && fje > 0)
		{
//			printLine(Convert.appendStringSize("", "-------------------------------------------------", 1, 37, 38, 2));
			if (salehead.printnum > 0)
			{
				printLine(Convert.appendStringSize("", "  **重打" + salehead.printnum + "**", 1, 37, 38, 2));
				;
			}
			
			if (GlobalInfo.syjDef.priv == null || GlobalInfo.syjDef.priv.trim().length() == 0)
			{
				printLine(Convert.appendStringSize("", "=================================================", 1, 37, 38));
				printLine(Convert.appendStringSize("", "当日凭此联可办理卓展VIP卡", 1, 37, 38, 2));
				String line = Convert.appendStringSize("", "交易时间："+salehead.rqsj, 1, 38, 38);
				printLine(line);
				line = Convert.appendStringSize("", "收银员:", 1, 7, 38);
				line = Convert.appendStringSize(line, GlobalInfo.posLogin.gh, 9, 10, 38);
				line = Convert.appendStringSize(line, "收银机:", 22, 7, 38);
				line = Convert.appendStringSize(line, GlobalInfo.syjDef.syjh, 30, 7, 38);
				printLine(line);

				line = Convert.appendStringSize("", "小票号:", 1, 7, 38);
				line = Convert.appendStringSize(line, String.valueOf(salehead.fphm), 9, 9, 38);
				line = Convert.appendStringSize(line, "消费金额:", 20, 9, 38);
				line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(fje), 30, 8, 38);
				printLine(line);
			}
		}
	}

	// 打印赠券
	public void printSaleTicketMSInfo()
	{
		if (this.zq == null || this.zq.size() <= 0) { return; }

		if (this.salemsinvo != 0 && salehead.fphm != this.salemsinvo)
		{
			this.salemsinvo = 0;
			this.zq = null;
			this.gift = null;
			return;
		}

		for (int i = 0; i < this.zq.size(); i++)
		{
			GiftGoodsDef def = (GiftGoodsDef) this.zq.elementAt(i);
			if (!def.type.trim().equals("99") && !def.type.trim().equals("4") && !def.type.trim().equals("999"))
			{
				if (SellType.ISCOUPON(salehead.djlb)) printLine(Convert.appendStringSize("", "买券交易", 1, 37, 38, 2));
				if (salehead.printnum > 0)
				{
					printLine(Convert.appendStringSize("", "				重 打 印", 1, 37, 38));
				}
				printLine(Convert.appendStringSize("", "=================================================", 1, 37, 38));
				Printer.getDefault().printLine_Normal("== 券  号  : " + def.code);
				Printer.getDefault().printLine_Normal("== 券信息  : " + def.info);
				Printer.getDefault().printLine_Normal("== 券总额  : " + def.je);
				Printer.getDefault().printLine_Normal("== 券有效期: " + def.memo);
				printLine(Convert.appendStringSize("", "=================================================", 1, 37, 38));
				Printer.getDefault().cutPaper_Normal();
			}
		}
	}

	/**
	 //计算促销联
	 public void caclPopInfo()
	 {
	 try
	 {
	 if (salehead.djlb != SellType.RETAIL_SALE)
	 {
	 return;
	 }

	 int i = 0;

	 for (i = 0; i < salegoods.size(); i++)
	 {
	 SaleGoodsDef saleGoodsDef = (SaleGoodsDef) salegoods.elementAt(i);

	 if (saleGoodsDef.flag == '1')
	 {
	 continue;
	 }

	 if (saleGoodsDef.xxtax >= 1)
	 {
	 saleGoodsDef.num3 = saleGoodsDef.hjje - saleGoodsDef.hjzk;
	 }

	 //xxtax >=1 计算促销联 (saleGoodsDef.memo.length() > 0) &&
	 if ((saleGoodsDef.xxtax >= 1))
	 {
	 break;
	 }
	 }

	 if (i >= salegoods.size())
	 {
	 return;
	 }

	 double payje = 0;

	 //计算收券合计
	 double tsy = ManipulatePrecision.doubleConvert(salehead.fk_sysy +
	 salehead.sswr_sysy,
	 2, 1);
	 double aqpay = 0;
	 double bqpay = 0;
	 double fqpay = 0;
	 double qtpay = 0;

	 for (i = 0; i < salepay.size(); i++)
	 {
	 SalePayDef pay = (SalePayDef) salepay.elementAt(i);
	 PayModeDef def = DataService.getDefault()
	 .searchPayMode(pay.paycode);

	 if ((def.type == '5') || def.code.equals("05") || (GlobalInfo.sysPara.mjPaymentRule+",").indexOf(pay.paycode+",") >= 0)
	 {
	 payje = ManipulatePrecision.doubleConvert(pay.je, 2, 1);

	 if ((def.isyy == 'Y') && (def.iszl != 'Y') && (tsy > 0))
	 {
	 if (payje >= tsy)
	 {
	 payje -= tsy;
	 tsy = 0;
	 }
	 else
	 {
	 tsy -= payje;
	 payje = 0;
	 }
	 }
	 
	 if (pay.idno.length() > 0)
	 {
	 String strmemo = pay.idno.substring(0, 1);
	 
	 if (strmemo.trim().equals("2"))
	 {
	 bqpay += payje;
	 }
	 else if (strmemo.trim().equals("1"))
	 {
	 aqpay += payje;
	 }
	 else if (strmemo.trim().equals("3"))
	 {
	 fqpay += payje;
	 }
	 }
	 else
	 {
	 qtpay += payje;
	 }
	 }
	 
	 
	 }

	 //全部用券付款
	 if (ManipulatePrecision.doubleConvert(aqpay + bqpay+fqpay+qtpay, 2, 1) >= ManipulatePrecision.doubleConvert(salehead.ysje,
	 2,
	 1))
	 {
	 for (i = 0; i < salegoods.size(); i++)
	 {
	 SaleGoodsDef saleGoodsDef = (SaleGoodsDef) salegoods.elementAt(i);
	 
	 saleGoodsDef.num3 = 0;
	 
	 }
	 return;
	 }

	 double aqje = 0;
	 double bqje = 0;
	 double fqje = 0;
	 double qtje = 0;
	 
	 double aqhj = 0;
	 double bqhj = 0;
	 double fqhj = 0;
	 double qthj = 0;

	 for (i = 0; i < salegoods.size(); i++)
	 {
	 SaleGoodsDef saleGoodsDef = (SaleGoodsDef) salegoods.elementAt(i);
	 if (saleGoodsDef.memo.length() > 0)
	 {
	 String[] strabqje = saleGoodsDef.memo.split(",");
	 aqje = Double.parseDouble(strabqje[0]);
	 bqje = Double.parseDouble(strabqje[1]);
	 fqje = Double.parseDouble(strabqje[2]);
	 }
	 qtje = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje-saleGoodsDef.hjzk);
	 
	 aqhj += aqje;
	 bqhj += bqje;
	 fqhj += fqje;
	 qthj += qtje;
	 //不打印促销联
	 if (saleGoodsDef.xxtax < 1)
	 {
	 if ((aqje > 0) && (aqpay > 0))
	 {
	 aqhj -= aqje;
	 aqpay -= aqje;

	 if (ManipulatePrecision.doubleConvert(aqpay, 2, 1) <= 0)
	 {
	 aqpay = 0;
	 }
	 }

	 if ((bqje > 0) && (bqpay > 0))
	 {
	 bqhj -= bqje;
	 bqpay -= bqje;

	 if (ManipulatePrecision.doubleConvert(bqpay, 2, 1) <= 0)
	 {
	 bqpay = 0;
	 }
	 }
	 
	 if ((fqje > 0) && (fqpay > 0))
	 {
	 fqhj -= fqje;
	 fqpay -=fqje;

	 if (ManipulatePrecision.doubleConvert(fqpay, 2, 1) <= 0)
	 {
	 fqpay = 0;
	 }
	 }
	 
	 if ((qtje > 0) && (qtpay > 0))
	 {
	 qthj -= qtje;
	 qtpay -=qtje;

	 if (ManipulatePrecision.doubleConvert(qtpay, 2, 1) <= 0)
	 {
	 qtpay = 0;
	 }
	 }
	 }
	 }

	 if (ManipulatePrecision.doubleConvert(aqhj, 2, 1) < 0)
	 {
	 aqhj = 1;
	 }

	 if (ManipulatePrecision.doubleConvert(bqhj, 2, 1) < 0)
	 {
	 bqhj = 1;
	 }
	 
	 if (ManipulatePrecision.doubleConvert(fqhj, 2, 1) < 0)
	 {
	 fqhj = 1;
	 }
	 
	 if (ManipulatePrecision.doubleConvert(qthj, 2, 1) < 0)
	 {
	 qthj = 1;
	 }

	 double jg = 0;
	 double qfk = 0;
	 int dec = 0;

	 for (i = 0; i < salegoods.size(); i++)
	 {
	 SaleGoodsDef saleGoodsDef = (SaleGoodsDef) salegoods.elementAt(i);

	 if (saleGoodsDef.xxtax < 1)
	 {
	 continue;
	 }

	 jg = saleGoodsDef.hjje - saleGoodsDef.hjzk;

	 if (((jg * 100) % 10) == 0)
	 {
	 dec = 1;
	 }
	 else
	 {
	 dec = 2;
	 }
	 
	 if (saleGoodsDef.memo.length() > 0)
	 {
	 String[] strabqje = saleGoodsDef.memo.split(",");
	 aqje = Double.parseDouble(strabqje[0]);
	 bqje = Double.parseDouble(strabqje[1]);
	 fqje = Double.parseDouble(strabqje[2]);
	 }
	 qtje = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje-saleGoodsDef.hjzk);

	 qfk = ManipulatePrecision.doubleConvert((aqpay * aqje) / aqhj,
	 dec, 1);
	 qfk += ManipulatePrecision.doubleConvert((bqpay * bqje) / bqhj,
	 dec, 1);
	 qfk += ManipulatePrecision.doubleConvert((fqpay * fqje) / fqhj,
	 dec, 1);
	 
	 qfk += ManipulatePrecision.doubleConvert((qtpay * qtje) / qthj,
	 dec, 1);

	 jg -= qfk;

	 if (jg >= 0.01)
	 {
	 saleGoodsDef.num3 = jg;
	 }
	 else
	 {
	 saleGoodsDef.num3 = 0;
	 }
	 }
	 }
	 catch (Exception er)
	 {
	 er.printStackTrace();
	 }
	 }*/

	//打印促销联
	public void printPopBill()
	{
		try
		{
			if (GlobalInfo.sysPara.printpopbill != 'Y') { return; }

			if (!salehead.djlb.equals(SellType.RETAIL_SALE)) { return; }

			if (salehead.hhflag != 'N') { return; }

			// 先计算需要打印促销联的金额
			//caclPopInfo();

			// 检查是否有需要打印的促销联
			double je = 0;
			int i = 0;

			for (i = 0; i < salegoods.size(); i++)
			{
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) salegoods.elementAt(i);
				je = saleGoodsDef.num3;

				if (je > 0)
				{
					break;
				}
			}

			if (i >= salegoods.size()) { return; }

			// 开始打印促销联
			Printer.getDefault().setPagePrint_Normal(false, 1);

			String lab = "";
			if (salehead.printnum > 0)
			{
				lab = "  (重打印)";
			}

			printLine("                促销联" + lab);
			printLine("商品编码         品名       促销金额 ");

			double zje = 0;

			for (i = 0; i < salegoods.size(); i++)
			{
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) salegoods.elementAt(i);
				je = saleGoodsDef.num3;

				if (je > 0)
				{
					zje += je;
					String line = Convert.appendStringSize("", saleGoodsDef.code, 0, 13, 39);
					line = Convert.appendStringSize(line, saleGoodsDef.name, 15, 14, 39);
					line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(je, 2, 1), 30, 9, 39, 2);
					printLine(line);
				}
			}

//			printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
			printLine(Convert.appendStringSize("", "--------------------------------------------------", 0, 37, 38));
			printLine("本次消费促销金额合计：" + ManipulatePrecision.doubleToString(zje));
	    	 
//	    	专柜开票新增打印
	 		String zgline = "";  
	 		 zgline = Convert.appendStringSize("", salehead.rqsj, 0, 20, 38);
	 		 zgline = Convert.appendStringSize(zgline, "收银台:", 21, 7, 38);
	 		 zgline = Convert.appendStringSize(zgline, GlobalInfo.syjDef.syjh, 28, 7, 38);
	 		printLine(zgline);
	 		zgline = Convert.appendStringSize("", "收银员:", 0, 7, 38);
	 		zgline = Convert.appendStringSize(zgline, GlobalInfo.posLogin.gh, 7, 8, 38);
	 		zgline = Convert.appendStringSize(zgline, "小票号:", 21, 7, 38);
	 		zgline = Convert.appendStringSize(zgline, String.valueOf(salehead.fphm),28, 7, 38);
	 		printLine(zgline);

	 		printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));    
//			printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
//			String line = Convert.appendStringSize("", salehead.rqsj, 0, 20, 38);
//			line = Convert.appendStringSize(line, "收银机:", 22, 7, 38);
//			line = Convert.appendStringSize(line, GlobalInfo.syjDef.syjh, 30, 7, 38);
//			printLine(line);
//			line = Convert.appendStringSize("", "收银员:", 0, 7, 38);
//			line = Convert.appendStringSize(line, GlobalInfo.posLogin.gh, 9, 10, 38);
//			line = Convert.appendStringSize(line, "小票号:", 22, 7, 38);
//			line = Convert.appendStringSize(line, String.valueOf(salehead.fphm), 30, 7, 38);
//			printLine(line);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public Vector convertPayDetail(Vector p1)
	{
		// 汇总人民币
		Vector newp1 = new Vector();
		SalePayDef spd3 = null, spd4 = null;

		// 分组汇总相同的付款方式
		for (int i = 0; i < p1.size(); i++)
		{
			spd3 = (SalePayDef) p1.elementAt(i);

			int j = 0;

			for (j = 0; j < newp1.size(); j++)
			{
				spd4 = (SalePayDef) newp1.elementAt(j);

				if (spd3.paycode.equals(spd4.paycode) && spd3.flag == spd4.flag && spd3.paycode.equals("01"))
				{
					break;
				}
			}

			if (j >= newp1.size())
			{
				spd3.num4 = 1;
				newp1.add(spd3.clone());
			}
			else
			{
				// 金额汇总
				spd4.payname = Math.abs(spd4.ybje) > Math.abs(spd3.ybje) ? spd4.payname : spd3.payname;
				spd4.ybje += spd3.ybje;
				spd4.je += spd3.je;
				spd4.num1 += spd3.num1;
			}
		}

		//打印应收金额
		for (int i = 0; i < newp1.size(); i++)
		{
			SalePayDef spd1 = (SalePayDef) newp1.elementAt(i);
			double je = spd1.je;
			double ybje = spd1.ybje;
			if (spd1.flag == '2')
			{
				for (int j = 0; j < newp1.size(); j++)
				{
					SalePayDef spd2 = (SalePayDef) newp1.elementAt(j);
					if (spd1.paycode.equals(spd2.paycode) && spd2.flag == '1')
					{
						if (spd2.ybje >= spd1.ybje)
						{
							spd2.ybje -= spd1.ybje;
							spd2.je -= je;
							break;
						}
						else
						{

							ybje = ManipulatePrecision.doubleConvert(ybje - spd2.ybje);
							je = ManipulatePrecision.doubleConvert(je - spd2.je);
							spd2.ybje = 0;
							spd2.je = 0;
						}
					}
				}
			}
		}

		if (GlobalInfo.sysPara.printpaysummary.equals("N")) return newp1;

		Vector newp = new Vector();
		SalePayDef spd = null, spd1 = null;

		// 分组汇总相同的付款方式
		for (int i = 0; i < newp1.size(); i++)
		{
			spd = (SalePayDef) newp1.elementAt(i);

			int j = 0;

			for (j = 0; j < newp.size(); j++)
			{
				spd1 = (SalePayDef) newp.elementAt(j);

				if (spd.paycode.equals(spd1.paycode) && spd.flag == spd1.flag)
				{
					if (convertPayDetail(spd, spd1))
					{
						break;
					}
				}
			}

			if (j >= newp.size())
			{
				spd.num4 = 1;
				newp.add(spd.clone());
			}
			else
			{
				// 金额汇总
				spd1.ybje += spd.ybje;
				spd1.je += spd.je;
				spd1.num1 += spd.num1;
				spd1.num4++;
				// 如果汇总的付款方式帐号不一致,则清除记录的付款方式帐号
				if (!spd.payno.equals(spd1.payno))
				{
					spd1.payno = "";
				}
			}
		}

		return newp;
	}

	public boolean convertPayDetail(SalePayDef spd, SalePayDef spd1)
	{
		// 退货时不进行汇总
		if (!SellType.ISSALE(salehead.djlb)) return false;

		String s[] = GlobalInfo.sysPara.printpaysummary.split(",");
		for (int i = 0; i < s.length; i++)
		{
			if (spd.paycode.equals(s[i].trim()))
			{
				if (CreatePayment.getDefault().isPaymentFjk(spd.paycode) && !spd.idno.equals(spd1.idno)) { return false; }
				return true;
			}

		}

		return false;
	}

	public void group()
	{
		//	打印营业员联分组方式,1-营业员+柜组,2-单品,3-柜组,4-营业员
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

			if (sgd.str9 != null && sgd.str9.length() > 0) continue;
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

			if (sgd.str9 != null && sgd.str9.length() > 0) continue;
			
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

			if (sgd.str9 != null && sgd.str9.length() > 0) continue;
			
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

	//	 按营业员
	public void groupByYyy()
	{
		groupset.clear();
		message = "";

		SaleGoodsDef sgd = null;
		GroupDef group = null;

		for (int i = 0; i < originalsalegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) originalsalegoods.elementAt(i);

			if (sgd.str9 != null && sgd.str9.length() > 0) continue;
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

			if (sgd.str9 != null && sgd.str9.length() > 0) continue;
			
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

	public class GroupDef
	{
		//分组条件 1,2,3
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
	
	/**
	 * 一期打印模板：延期提货单据最先打并切纸，非延期提货单据和手工商品分开打，不切纸打虚线。
	 */
/**	protected void printSellBill()
    {
		// GlobalInfo.sysPara.fdprintyyy = (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
    	// 非超市小票且系统参数定义只打印营业员分单，则不打印机制小票
		if (!(
			(GlobalInfo.syjDef.issryyy == 'N') || 
			(GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef)salegoods.elementAt(0)).yyyh.equals("超市"))) &&
		    (GlobalInfo.sysPara.fdprintyyy == 'A')
			)
    	{
    		return;
    	}
		try{
			for(int i =0;i<salegoods.size();i++){
				SaleGoodsDef salegoodsdef = (SaleGoodsDef) salegoods.elementAt(i);
				if(salegoodsdef.str9 != null && !"".equals(salegoodsdef.str9))
				{
					//添加电子单据商品
					dzDjList.add(salegoodsdef);
					
	//				if(saleHeadList == null)
	//				{
	//					saleHeadList.add(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2));
	//					continue;
	//				}
					if(saleHeadList.contains(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2)))
					{
						saleHeadList.remove(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2));
						//如果开单类型为延期提货(2)，放到第一位
						if(salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2).equals("2")){
							saleHeadList.addFirst(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2));
						}
						else
						{
							saleHeadList.add(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2));
						}
					}
					else
					{
	//					如果开单类型为延期提货(2)，放到第一位
						if(salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2).equals("2")){
							saleHeadList.addFirst(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2));
						}
						else
						{
						saleHeadList.add(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2));
						}
					}
				}
				else
				{
					//添加手工单据商品
					sgDjList.add(salegoodsdef);
				}
			}
			
			
//	        // 设置打印方式
//	        printSetPage();
//	
//	        // 多联小票打印不同抬头
//			printDifTitle();
			
			String line = null;
			//电子单打印
			if(saleHeadList.size()>0)
			{
				
				for(int i =0;i<saleHeadList.size();i++){
					printLine("");
					printLine(Convert.appendStringSize("", "             卓展购物中心", 0, 37, 38));
					printLine(Convert.appendStringSize("","             美与文明同行", 0, 37, 38));
					printLine("");
					
					if(GlobalInfo.sysPara.mktcode.equals("002,205")){
						line = Convert.appendStringSize("", "门店名:", 0, 7, 38);
						line = Convert.appendStringSize(line, "北京店", 8, 7, 38);
						printLine(line);
					}
//					line = Convert.appendStringSize("", "门店号:", 0, 7, 38);
//					line = Convert.appendStringSize(line, GlobalInfo.sysPara.mktcode, 8, 7, 38);
//					printLine(line);
					line = Convert.appendStringSize("", "交易时间:",0, 9, 38);
					line = Convert.appendStringSize(line, salehead.rqsj, 10, 28, 38);
					printLine(line);
					line = Convert.appendStringSize("", "收银机:", 0, 7, 38);
					line = Convert.appendStringSize(line, GlobalInfo.syjDef.syjh, 8, 7, 38);
					line = Convert.appendStringSize(line, "小票号:", 15, 7, 38);
					line = Convert.appendStringSize(line, String.valueOf(salehead.fphm), 23, 7, 38);
					printLine(line);
					line = Convert.appendStringSize("", "收银员:", 0, 7, 38);
					line = Convert.appendStringSize(line, GlobalInfo.posLogin.gh, 8, 6, 38);
					line = Convert.appendStringSize(line, "交易类型:", 14, 9, 38);
					line = Convert.appendStringSize(line, SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead), 23, 10, 38);
					printLine(line);
					if(saleHeadList.get(i).toString().substring(saleHeadList.get(i).toString().indexOf(";")+1).equals("2")){
						line = Convert.appendStringSize("", "提货号:", 0, 7, 38);
						line = Convert.appendStringSize(line, String.valueOf(saleHeadList.get(i)).substring(0,saleHeadList.get(i).toString().indexOf(";")), 8, 16, 38);
						printLine(line);
					}
					
					if (salehead.printnum > 0)
					{
						printLine(Convert.appendStringSize("", "  **重打" + salehead.printnum + "**", 1, 37, 38, 2));
					}
					
					printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
					printLine(Convert.appendStringSize("", " 商品名称     数量    单价    成交价", 0, 37, 38));
					
					double ss = 0;
					double vipzk = 0;
					for(int j=0;j<dzDjList.size();j++){
						SaleGoodsDef goodsDef = (SaleGoodsDef) dzDjList.get(j);
	//					if(saleHeadList.contains(goodsDef.str9));
						if(String.valueOf(saleHeadList.get(i)).substring(0,saleHeadList.get(i).toString().indexOf(";")).equals(goodsDef.str9)){
							ss = ss+(goodsDef.hjje-goodsDef.hjzk);//实收
							vipzk = vipzk+goodsDef.hyzke;//VIP折扣
							line = Convert.appendStringSize("", goodsDef.code, 0, 13, 38);
							line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(goodsDef.sl * SellType.SELLSIGN(salehead.djlb))), 13, 7, 38);
							line = Convert.appendStringSize(line, String.valueOf(goodsDef.lsj), 20, 8, 38);
							line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString((goodsDef.hjje-goodsDef.hjzk) * SellType.SELLSIGN(salehead.djlb))), 28, 10, 38);
							printLine(line);
							line = Convert.appendStringSize("", goodsDef.name, 0, 18, 38);
							if(!"".equals(goodsDef.str7)){
								line = Convert.appendStringSize(line,"+"+goodsDef.str7, 19, 19, 38);
							}
							printLine(line);
						}
						else
						{
							continue;
						}
					}
					line = Convert.appendStringSize("", " VIP折扣:", 0, 9, 38);
					line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(vipzk * SellType.SELLSIGN(salehead.djlb))), 8, 10, 38);
					line = Convert.appendStringSize(line, " 实收:", 19, 6, 38);
					line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(ss * SellType.SELLSIGN(salehead.djlb))), 25, 10, 38);
					printLine(line);
					printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
					printLine(Convert.appendStringSize("", "凭本收银联办理退换货，请妥善保管", 0, 37, 38));
					printLine(Convert.appendStringSize("", "客服电话：400-065-0809", 0, 37, 38));
					if(saleHeadList.get(i).toString().substring(saleHeadList.get(i).toString().indexOf(";")+1).equals("2")){
						//切纸
			     	  printCutPaper();
					}
					else
					{
						printLine(Convert.appendStringSize("","",0,37,38));
						printLine(Convert.appendStringSize("","",0,37,38));
						printLine(Convert.appendStringSize("", "--------------------------------------------------------------------------------------------------", 0, 37, 38));
						printLine(Convert.appendStringSize("","",0,37,38));
						printLine(Convert.appendStringSize("","",0,37,38));
					}
				}
			}
	
				//手工单打印
				if(sgDjList.size() > 0){
					printLine("");
					printLine(Convert.appendStringSize("", "            卓展购物中心", 0, 37, 38));
					printLine(Convert.appendStringSize("","            美与文明同行", 0, 37, 38));
					printLine("");
					if(GlobalInfo.sysPara.mktcode.equals("002,205")){
						line = Convert.appendStringSize("", "门店名:", 0, 7, 38);
						line = Convert.appendStringSize(line, "北京店", 8, 7, 38);
						printLine(line);
					}
					line = Convert.appendStringSize("", "交易时间:",0, 9, 38);
					line = Convert.appendStringSize(line, salehead.rqsj, 10, 28, 38);
					printLine(line);
					line = Convert.appendStringSize("", "收银机:", 0, 7, 38);
					line = Convert.appendStringSize(line, GlobalInfo.syjDef.syjh, 8, 7, 38);
					line = Convert.appendStringSize(line, "小票号:", 15, 7, 38);
					line = Convert.appendStringSize(line, String.valueOf(salehead.fphm), 23, 7, 38);
					printLine(line);
					line = Convert.appendStringSize("", "收银员:", 0, 7, 38);
					line = Convert.appendStringSize(line, GlobalInfo.posLogin.gh, 8, 6, 38);
					line = Convert.appendStringSize(line, "交易类型:", 14, 9, 38);
					line = Convert.appendStringSize(line, SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead), 23, 10, 38);
					printLine(line);
					if (salehead.printnum > 0)
					{
						printLine(Convert.appendStringSize("", "  **重打" + salehead.printnum + "**", 1, 37, 38, 2));
					}
					
					printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
					printLine(Convert.appendStringSize("", " 商品名称     数量    单价    成交价", 0, 37, 38));
					
					double ss = 0;
					double vipzk = 0;
					for(int j=0;j<sgDjList.size();j++){
						SaleGoodsDef goodsDef = (SaleGoodsDef) sgDjList.get(j);
							ss = ss+(goodsDef.hjje-goodsDef.hjzk);//实收
							vipzk = vipzk+goodsDef.hyzke;//VIP折扣
							line = Convert.appendStringSize("", goodsDef.code, 0, 13, 38);
							line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(goodsDef.sl * SellType.SELLSIGN(salehead.djlb))), 13, 7, 38);
							line = Convert.appendStringSize(line, String.valueOf(goodsDef.lsj), 20, 8, 38);
							line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString((goodsDef.hjje-goodsDef.hjzk) * SellType.SELLSIGN(salehead.djlb))), 28, 10, 38);
							printLine(line);
							line = Convert.appendStringSize("", goodsDef.name, 0, 18, 38);
							printLine(line);
						}
					
					line = Convert.appendStringSize("", " VIP折扣:", 0, 9, 38);
					line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(vipzk * SellType.SELLSIGN(salehead.djlb))), 8, 10, 38);
					line = Convert.appendStringSize(line, " 实收:", 19, 6, 38);
					line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(ss * SellType.SELLSIGN(salehead.djlb))), 25, 10, 38);
					printLine(line);
					printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
					printLine(Convert.appendStringSize("", "凭本收银联办理退换货，请妥善保管", 0, 37, 38));
					printLine(Convert.appendStringSize("", "客服电话：400-065-0809", 0, 37, 38));
	//				 切纸
	//		        printCutPaper();
					printLine(Convert.appendStringSize("","",0,37,38));
					printLine(Convert.appendStringSize("","",0,37,38));
					printLine(Convert.appendStringSize("", "--------------------------------------------------------------------------------------------------", 0, 37, 38));
					printLine(Convert.appendStringSize("","",0,37,38));
					printLine(Convert.appendStringSize("","",0,37,38));
				}
				
				  line = Convert.appendStringSize("", "累计", 0, 10, 38);
					printLine(line);
					
					  // 打印汇总区域
			        printTotal();
			        
			        printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
					printLine(Convert.appendStringSize("", " 商品编码     品名   实收金额", 0, 37, 38));
			        
			    	for(int j=0;j<salegoods.size();j++){
			    		SaleGoodsDef goodsDef = (SaleGoodsDef) salegoods.elementAt(j);
			    		line = Convert.appendStringSize("", goodsDef.code, 0, 12, 38);
						line = Convert.appendStringSize(line, String.valueOf(goodsDef.name), 12, 18, 38);
						line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString((goodsDef.hjje-goodsDef.hjzk) * SellType.SELLSIGN(salehead.djlb))), 30, 8, 38);
						printLine(line);
			    	}
			    	
			    	  printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
			    	  
			    	  	// 打印付款区域
				        printPay();
				        
				        // 打印尾部区域
				        printBottom();
			
				        // 打印赠品联
				        printGift();
			
				        printLine(Convert.appendStringSize("", "--------------------------------------------------------------------------------------------------", 0, 37, 38));
				        
				        // 切纸
				        printCutPaper();
				        
		}catch(Exception er)
		{
			er.printStackTrace();
		}
		finally
		{
			if(saleHeadList.size()>0)
			{
				saleHeadList.clear();
			}
			if(dzDjList.size()>0)
			{
				dzDjList.clear();
			}
			if(sgDjList.size()>0)
			{
				sgDjList.clear();
			}
		}
    }
    */
	
	/**
	 * 二期打印模板：延期提货单据最先打并切纸，非延期提货单据和手工商品一起打，不切纸打虚线。
	 */
	protected void printSellBill()
    {
		// GlobalInfo.sysPara.fdprintyyy = (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
    	// 非超市小票且系统参数定义只打印营业员分单，则不打印机制小票
		if (!(
			(GlobalInfo.syjDef.issryyy == 'N') || 
			(GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef)salegoods.elementAt(0)).yyyh.equals("超市"))) &&
		    (GlobalInfo.sysPara.fdprintyyy == 'A')
			)
    	{
    		return;
    	}
		try{
			saleHeadList = new LinkedList();
			dzDjList = new LinkedList();
			sgDjList = new LinkedList();
			fyqzDjList = new LinkedList();
			if(printOrder==null || !(printOrder.length>0)) isOrder = getPrintOrder();
			
			for(int i =0;i<salegoods.size();i++){
				SaleGoodsDef salegoodsdef = (SaleGoodsDef) salegoods.elementAt(i);
				if(salegoodsdef.str9 != null && !"".equals(salegoodsdef.str9))
				{
					//添加所有电子单据商品
					dzDjList.add(salegoodsdef);
					//添加非延期提货电子单据商品
					if(!salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2).equals("2")){
						fyqzDjList.add(salegoodsdef);
					}
					
					if(saleHeadList.contains(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2)))
					{
						saleHeadList.remove(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2));
						//如果开单类型为延期提货(2)，放到第一位
						if(salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2).equals("2")){
							saleHeadList.addFirst(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2));
						}
						else
						{
							saleHeadList.add(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2));
						}
					}
					else
					{
	//					如果开单类型为延期提货(2)，放到第一位
						if(salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2).equals("2")){
							saleHeadList.addFirst(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2));
						}
						else
						{
						saleHeadList.add(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2));
						}
					}
				}
				else
				{
					//添加手工单据商品
					sgDjList.add(salegoodsdef);
				}
			}
			
			
//	        // 设置打印方式
//	        printSetPage();
//	
//	        // 多联小票打印不同抬头
//			printDifTitle();
			
			String line = null;
			//延期提货单据打印
			if(saleHeadList.size()>0)
			{
				
				for(int i =0;i<saleHeadList.size();i++){
					if(!saleHeadList.get(i).toString().substring(saleHeadList.get(i).toString().indexOf(";")+1).equals("2")){continue;}
					printLine(Convert.appendStringSize(""," 卓展购物中心", 0, 38, 38,2));
					printLine(Convert.appendStringSize(""," 展现新视野", 0, 38, 38,2));
					printLine("");
					//长春店是 002,201 沈阳店是 002,202，  哈尔滨店002,203，卓越店002,204 北京店是002,205
					if(GlobalInfo.sysPara.mktcode.equals("002,201")){
						line = Convert.appendStringSize("", "门店名:", 0, 7, 38);
						line = Convert.appendStringSize(line, "长春店", 8, 7, 38);
						printLine(line);
					}
					if(GlobalInfo.sysPara.mktcode.equals("002,202")){
						line = Convert.appendStringSize("", "门店名:", 0, 7, 38);
						line = Convert.appendStringSize(line, "沈阳店", 8, 7, 38);
						printLine(line);
					}
					if(GlobalInfo.sysPara.mktcode.equals("002,203")){
						line = Convert.appendStringSize("", "门店名:", 0, 7, 38);
						line = Convert.appendStringSize(line, "哈尔滨店", 8, 7, 38);
						printLine(line);
					}
					if(GlobalInfo.sysPara.mktcode.equals("002,204")){
						line = Convert.appendStringSize("", "门店名:", 0, 7, 38);
						line = Convert.appendStringSize(line, "卓越店", 8, 7, 38);
						printLine(line);
					}
					if(GlobalInfo.sysPara.mktcode.equals("002,205")){
						line = Convert.appendStringSize("", "门店名:", 0, 7, 38);
						line = Convert.appendStringSize(line, "北京店", 8, 7, 38);
						printLine(line);
					}
//					line = Convert.appendStringSize("", "门店号:", 0, 7, 38);
//					line = Convert.appendStringSize(line, GlobalInfo.sysPara.mktcode, 8, 7, 38);
//					printLine(line);
					line = Convert.appendStringSize("", "交易时间:",0, 9, 38);
					line = Convert.appendStringSize(line, salehead.rqsj, 10, 28, 38);
					printLine(line);
					line = Convert.appendStringSize("", "收银机:", 0, 7, 38);
					line = Convert.appendStringSize(line, GlobalInfo.syjDef.syjh, 8, 7, 38);
					line = Convert.appendStringSize(line, "小票号:", 15, 7, 38);
					line = Convert.appendStringSize(line, String.valueOf(salehead.fphm), 23, 7, 38);
					printLine(line);
					line = Convert.appendStringSize("", "收银员:", 0, 7, 38);
					line = Convert.appendStringSize(line, GlobalInfo.posLogin.gh, 8, 6, 38);
					line = Convert.appendStringSize(line, "交易类型:", 15, 9, 38);
					line = Convert.appendStringSize(line, SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead), 24, 10, 38);
					printLine(line);
					if(saleHeadList.get(i).toString().substring(saleHeadList.get(i).toString().indexOf(";")+1).equals("2")){
						line = Convert.appendStringSize("", "提货号:", 0, 7, 38);
						line = Convert.appendStringSize(line, String.valueOf(saleHeadList.get(i)).substring(0,saleHeadList.get(i).toString().indexOf(";")), 8, 16, 38);
						printLine(line);
					}
					
					if (salehead.printnum > 0)
					{
						printLine(Convert.appendStringSize("", "  **重打" + salehead.printnum + "**", 1, 37, 38, 2));
					}
					
					printLine(Convert.appendStringSize("", "--------------------------------------------------", 0, 37, 38));
					printLine(Convert.appendStringSize("", "商品名称    数量     单价    成交价", 0, 37, 38));
					
					double ss = 0;
					double vipzk = 0;
					for(int j=0;j<dzDjList.size();j++){
						SaleGoodsDef goodsDef = (SaleGoodsDef) dzDjList.get(j);
	//					if(saleHeadList.contains(goodsDef.str9));
						if(String.valueOf(saleHeadList.get(i)).substring(0,saleHeadList.get(i).toString().indexOf(";")).equals(goodsDef.str9)){
							ss = ss+(goodsDef.hjje-goodsDef.hjzk);//实收
							vipzk = vipzk+goodsDef.hyzke;//VIP折扣
							line = Convert.appendStringSize("", goodsDef.code, 0, 13, 38);
							line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(goodsDef.sl * SellType.SELLSIGN(salehead.djlb))), 13, 7, 38);
							line = Convert.appendStringSize(line, String.valueOf(goodsDef.lsj), 20, 8, 38);
							line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString((goodsDef.hjje-goodsDef.hjzk) * SellType.SELLSIGN(salehead.djlb))), 28, 10, 38);
							printLine(line);
							line = Convert.appendStringSize("", goodsDef.name, 0, 18, 38);
							if(!"".equals(goodsDef.str7)){
								line = Convert.appendStringSize(line,goodsDef.str7, 19, 19, 38);
							}
							printLine(line);
						}
						else
						{
							continue;
						}
					}
					line = Convert.appendStringSize("", " VIP折扣:", 0, 9, 38);
					line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(vipzk * SellType.SELLSIGN(salehead.djlb))), 8, 10, 38);
					line = Convert.appendStringSize(line, " 实收:", 19, 6, 38);
					line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(ss * SellType.SELLSIGN(salehead.djlb))), 25, 10, 38);
					printLine(line);
					printLine(Convert.appendStringSize("", "--------------------------------------------------", 0, 37, 38));
					printLine(Convert.appendStringSize("", "凭此提货并办理退换货业务，请妥善保管", 0, 37, 38));
					printLine(Convert.appendStringSize("", "客服电话：88198096，88198098 ", 0, 37, 38));
					if(saleHeadList.get(i).toString().substring(saleHeadList.get(i).toString().indexOf(";")+1).equals("2")){
						//切纸
			     	  printCutPaper();
					}
//					else
//					{
//						printLine(Convert.appendStringSize("", "--------------------------------------------------------------------------------------------------", 0, 37, 38));
//
//					}
				}
			}
			
			
//			非延期提货单据打印
			if(sgDjList.size() > 0 || fyqzDjList.size() > 0)
			{
					printLine(Convert.appendStringSize(""," 卓展购物中心", 0, 38, 38,2));
					printLine(Convert.appendStringSize(""," 展现新视野", 0, 38, 38,2));
					printLine("");
//					长春店是 002,201 沈阳店是 002,202，  哈尔滨店002,203，卓越店002,204 北京店是002,205
					if(GlobalInfo.sysPara.mktcode.equals("002,201")){
						line = Convert.appendStringSize("", "门店名:", 0, 7, 38);
						line = Convert.appendStringSize(line, "长春店", 8, 7, 38);
						printLine(line);
					}
					if(GlobalInfo.sysPara.mktcode.equals("002,202")){
						line = Convert.appendStringSize("", "门店名:", 0, 7, 38);
						line = Convert.appendStringSize(line, "沈阳店", 8, 7, 38);
						printLine(line);
					}
					if(GlobalInfo.sysPara.mktcode.equals("002,203")){
						line = Convert.appendStringSize("", "门店名:", 0, 7, 38);
						line = Convert.appendStringSize(line, "哈尔滨店", 8, 7, 38);
						printLine(line);
					}
					if(GlobalInfo.sysPara.mktcode.equals("002,204")){
						line = Convert.appendStringSize("", "门店名:", 0, 7, 38);
						line = Convert.appendStringSize(line, "卓越店", 8, 7, 38);
						printLine(line);
					}
					if(GlobalInfo.sysPara.mktcode.equals("002,205")){
						line = Convert.appendStringSize("", "门店名:", 0, 7, 38);
						line = Convert.appendStringSize(line, "北京店", 8, 7, 38);
						printLine(line);
					}
					line = Convert.appendStringSize("", "交易时间:",0, 9, 38);
					line = Convert.appendStringSize(line, salehead.rqsj, 10, 28, 38);
					printLine(line);
					line = Convert.appendStringSize("", "收银机:", 0, 7, 38);
					line = Convert.appendStringSize(line, GlobalInfo.syjDef.syjh, 8, 7, 38);
					line = Convert.appendStringSize(line, "小票号:", 15, 7, 38);
					line = Convert.appendStringSize(line, String.valueOf(salehead.fphm), 23, 7, 38);
					printLine(line);
					line = Convert.appendStringSize("", "收银员:", 0, 7, 38);
					line = Convert.appendStringSize(line, GlobalInfo.posLogin.gh, 8, 6, 38);
					line = Convert.appendStringSize(line, "交易类型:", 15, 9, 38);
					line = Convert.appendStringSize(line, SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead), 24, 10, 38);
					printLine(line);
					if (salehead.printnum > 0)
					{
						printLine(Convert.appendStringSize("", "  **重打" + salehead.printnum + "**", 1, 37, 38, 2));
					}
					
					printLine(Convert.appendStringSize("", "--------------------------------------------------", 0, 37, 38));
					printLine(Convert.appendStringSize("", "商品名称     数量   单价     成交价", 0, 37, 38));
					
					double ss = 0;
					double vipzk = 0;
//					非延期提货电子单打印
					if(fyqzDjList.size() > 0){
						for(int k=0;k<fyqzDjList.size();k++){
							SaleGoodsDef goodsDef = (SaleGoodsDef) fyqzDjList.get(k);
								ss = ss+(goodsDef.hjje-goodsDef.hjzk);//实收
								vipzk = vipzk+goodsDef.hyzke;//VIP折扣
								line = Convert.appendStringSize("", goodsDef.code, 0, 13, 38);
								line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(goodsDef.sl * SellType.SELLSIGN(salehead.djlb))), 13, 7, 38);
								line = Convert.appendStringSize(line, String.valueOf(goodsDef.lsj), 20, 8, 38);
								line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString((goodsDef.hjje-goodsDef.hjzk) * SellType.SELLSIGN(salehead.djlb))), 28, 10, 38);
								printLine(line);
								line = Convert.appendStringSize("", goodsDef.name, 0, 18, 38);
								if(!"".equals(goodsDef.str7)){
									line = Convert.appendStringSize(line,goodsDef.str7, 19, 19, 38);
								}
								printLine(line);
						}
					}
						
//					手工单打印
					if(sgDjList.size() > 0){
						for(int j=0;j<sgDjList.size();j++){
							SaleGoodsDef goodsDef = (SaleGoodsDef) sgDjList.get(j);
								ss = ss+(goodsDef.hjje-goodsDef.hjzk);//实收
								vipzk = vipzk+goodsDef.hyzke;//VIP折扣
								line = Convert.appendStringSize("", goodsDef.code, 0, 13, 38);
								line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(goodsDef.sl * SellType.SELLSIGN(salehead.djlb))), 13, 7, 38);
								line = Convert.appendStringSize(line, String.valueOf(goodsDef.lsj), 20, 8, 38);
								line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString((goodsDef.hjje-goodsDef.hjzk) * SellType.SELLSIGN(salehead.djlb))), 28, 10, 38);
								printLine(line);
								line = Convert.appendStringSize("", goodsDef.name, 0, 18, 38);
								printLine(line);
							}
					}
					
					line = Convert.appendStringSize("", " VIP折扣:", 0, 9, 38);
					line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(vipzk * SellType.SELLSIGN(salehead.djlb))), 8, 10, 38);
					line = Convert.appendStringSize(line, " 实收:", 19, 6, 38);
					line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(ss * SellType.SELLSIGN(salehead.djlb))), 25, 10, 38);
					printLine(line);
				
	//				 切纸
	//		        printCutPaper();
					printLine(Convert.appendStringSize("", "--------------------------------------------------------------------------------------------------", 0, 37, 38));
					
			}
				
					line = Convert.appendStringSize("", "累计", 0, 10, 38);
					printLine(line);
					
					// 打印汇总区域
			        printTotal();
			        
			      /*  
			        if(sgDjList.size() > 0 || fyqzDjList.size() > 0)
				     {
				    	printLine(Convert.appendStringSize("", "凭以上收银联办理退换货，请妥善保管", 0, 37, 38));
						printLine(Convert.appendStringSize("", "客服电话：88198096，88198098 ", 0, 37, 38));
						
				     }*/

			        // 打印尾部区域
			        printBottom();
//			      没有printorder.ini文件则还原原来打印顺序
			        if(!isOrder){
//					      打印面值卡联
						printMZKBill(1);

						//打印返券卡联
						printMZKBill(2);

//						printPopBill();
//						 办卡联
						printBKL();
				        printKpInfo();
					  
				    	//促销联
				    	printPopBill();
		
			        }
		    	
			 		super.printBottom();
 	
			 		if(isOrder){
			 			printSetOrder();
			 		}
				     // 切纸
				     printCutPaper();
				        
		}catch(Exception er)
		{
			er.printStackTrace();
		}
		finally
		{
			if(saleHeadList.size()>0)
			{
				saleHeadList.clear();
			}
			if(dzDjList.size()>0)
			{
				dzDjList.clear();
			}
			if(sgDjList.size()>0)
			{
				sgDjList.clear();
			}
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
			if(printOrder[i].trim().equals("PopBill")){
				printPopBill();
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
    			printLine(Convert.appendStringSize("", "===============================================", 0, 37, 38, 2));
        		printLine(Convert.appendStringSize("", "顾客意见联", 0, 37, 38, 2));
        		if(customerBill!=null && customerBill.size()>0){
        			for(int i = 0 ;i<customerBill.size();i++)printLine(String.valueOf(customerBill.elementAt(i)));
        		}else{
        			printLine("");
            		printLine(Convert.appendStringSize("", "欢迎参加我们的服务计划", 0, 37, 38, 2));
            		printLine(Convert.appendStringSize("", "本次购物您的评价是：", 0, 37, 38, 2));
            		printLine("");
            		printLine(Convert.appendStringSize("", "  (   )非常满意     (   )一般 ", 0, 37, 38, 0));
            		printLine("");
            		printLine(Convert.appendStringSize("", "  (   )不满意     ", 0, 37, 38, 0));
            		printLine("");
            		printLine(Convert.appendStringSize("", "请您将意见联撕下投递至商场服务", 0, 37, 38, 2));
            		printLine(Convert.appendStringSize("", "监督箱内", 0, 37, 38, 2));

        		}
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

	private void printCarParkInfo()
	{
		if(carParkInfo!=null&&carParkInfo.size()>0){
		printLine("==============停车联==================");
		
		for(int i = 0;i<carParkInfo.size();i++)printLine(String.valueOf(carParkInfo.elementAt(i)));
		printLine("实收金额:" + ManipulatePrecision.doubleToString(salehead.ysje * SellType.SELLSIGN(salehead.djlb)));
		printLine(salehead.rqsj.split(" ")[0]);
		}
	}

	private void printKpInfo()
	{
    	   printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
		     printLine(Convert.appendStringSize("","以下为您的付款明细", 0, 38, 38));
			 printLine(Convert.appendStringSize("","可凭此在一个月内开具发票", 0, 38, 38));
			
//			 打印付款区域
	        printPay();

	        // 打印赠品联
	        printGift();

	        printLine(Convert.appendStringSize("", "--------------------------------------------------------------------------------------------------", 0, 37, 38));
	        
	        String line = null;
			line = Convert.appendStringSize("", salehead.rqsj,0, 10, 38);
			line = Convert.appendStringSize(line,"单据号:"+GlobalInfo.syjDef.syjh+"-"+String.valueOf(salehead.fphm),11, 38, 38);
			printLine(line);
//	        printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
			printLine(Convert.appendStringSize("", "商品编码     品名           实收金额", 0, 37, 38));
	        
	    	for(int j=0;j<salegoods.size();j++){
	    		SaleGoodsDef goodsDef = (SaleGoodsDef) salegoods.elementAt(j);
	    		line = Convert.appendStringSize("", goodsDef.code, 0, 12, 38);
				line = Convert.appendStringSize(line, String.valueOf(goodsDef.name), 12, 18, 38);
				line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString((goodsDef.hjje-goodsDef.hjzk) * SellType.SELLSIGN(salehead.djlb))), 30, 8, 38);
				printLine(line);
	    	}

	    	 printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
	    	 
		
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

	public void printRealTimeBottom()
    {
        // 打印汇总区域
        printTotal();

        // 打印付款区域
        printPay();

        // 打印尾部区域
        printBottom();

        // 打印赠品联
        printGift();

        // 切纸
        printCutPaper();

        // 打印附加的各个小票联
        printAppendBill();
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
                printLine("商品编码:" + saleGoodsDef.code);
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
	
}
