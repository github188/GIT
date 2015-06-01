package custom.localize.Hhdl;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentChange;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.SaleShowAccountForm;

import custom.localize.Bstd.Bstd_SaleBS;

public class Hhdl_SaleBS extends Bstd_SaleBS
{
	public boolean findGoods(String code, String yyyh, String gz, String memo)
	{
		StringBuffer sbCode = new StringBuffer();

		if (!Hhdl_Util.checkBarcodeSale(code, sbCode))
			return false;

		return super.findGoods(sbCode.toString(), yyyh, gz, memo);
	}

	

	public Vector getSalePaymentDisplay()
	{
		Vector v = new Vector();
		String[] detail = null;
		SalePayDef saledef = null;

		for (int i = 0; i < salePayment.size(); i++)
		{
			saledef = (SalePayDef) salePayment.elementAt(i);
			detail = new String[3];
			detail[0] = "[" + saledef.paycode + "]" + saledef.payname;

			if (saledef.paycode.equals("0508") || saledef.paycode.equals("0555"))
			{
				detail[1] = saledef.memo;
				detail[2] = ManipulatePrecision.doubleToString(ManipulatePrecision.doubleConvert(saledef.ybje - saledef.num1), 2, 1);
			}
			else
			{
				detail[1] = saledef.payno;
				detail[2] = ManipulatePrecision.doubleToString(saledef.ybje);
			}

			v.add(detail);
		}

		// 在要刷新付款列表时,写入断点数据
		writeBrokenData();

		return v;
	}

	// 将券的分摊单独记录到str4
	public boolean paymentApportionSummary()
	{
		if (GlobalInfo.sysPara.havePayRule != 'Y' && GlobalInfo.sysPara.havePayRule != 'A')
			return true;

		// 汇总付款分摊到商品明细的str2字段
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
			if (goodsSpare == null || goodsSpare.size() <= i)
				continue;
			SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(i);
			if (spinfo == null)
				continue;

			// sg.str2 = 付款行号:付款代码:分摊金额
			// payft = 付款方式唯一序号,付款代码,付款名称(主要判断同付款代码不同付款FLAG),分摊金额
			sg.str2 = "";
			// 用于记录券分摊
			sg.str4 = "";

			System.out.println("汇总券分摊:");

			for (int j = 0; spinfo.payft != null && j < spinfo.payft.size(); j++)
			{
				String[] s = (String[]) spinfo.payft.elementAt(j);
				String rowno = "";

				if (s[0].equals("0508") || s[0].equals("0555"))
				{
					// 卡号：分摊金额
					String tmpstr4 = "," + s[1] + ":" + s[2];
					sg.str4 = sg.str4 + tmpstr4;
					System.out.println(sg.barcode + ":" + sg.str4);
					continue;
				}

				// 兼容标准版分摊记录
				for (int n = 0; n < salePayment.size(); n++)
				{
					SalePayDef sp = (SalePayDef) salePayment.elementAt(n);
					if ((int) sp.num5 == Convert.toInt(s[0]))
					{
						rowno = String.valueOf(sp.rowno);
						break;
					}
				}

				if (rowno.length() > 0)
				{
					String tmpstr2 = "," + rowno + ":" + s[1] + ":" + s[3];
					sg.str2 = sg.str2 + tmpstr2;
				}
			}

			if (sg.str2.length() > 0)
				sg.str2 = sg.str2.substring(1);

			if (sg.str4.length() > 0)
				sg.str4 = sg.str4.substring(1);
		}

		return true;
	}

	protected boolean checkLimitPaycode(String type, double minmoney, String limitpaycode)
	{
		double maxmoney = 0;
		String msg = "";

		try
		{
			// 未定义付款规则
			if (limitpaycode == null || limitpaycode.equals(""))
				return true;

			String[] paycode = limitpaycode.split(":");

			if (paycode == null)
				return false;

			for (int i = 0; i < paycode.length; i++)
			{
				PayModeDef mode = DataService.getDefault().searchPayMode(paycode[i]);
				if (mode == null)
					continue;

				msg += ("[" + paycode[i] + "-" + mode.name + "] ");

				for (int j = 0; j < salePayment.size(); j++)
				{
					SalePayDef pay = (SalePayDef) salePayment.get(j);

					if (!pay.paycode.equals(paycode[i]))
						continue;

					double tmpmoney = pay.je;

					// 找零毕竞都是最后一个付款产生的，所以要将找零的部分给干掉
					if (j == salePayment.size() - 1)
					{
						double curpayzlje = ManipulatePrecision.doubleConvert(saleHead.sjfk - saleyfje - salezlexception, 2, 1);
						if (curpayzlje > 0)
							tmpmoney = ManipulatePrecision.doubleConvert(pay.je - curpayzlje, 2, 1);

						PayModeDef excepmode = DataService.getDefault().searchPayMode(pay.paycode);

						// 可以溢余且不为现金
						if (excepmode.isyy == 'Y' && excepmode.type != '1')
							tmpmoney = ManipulatePrecision.doubleConvert(tmpmoney - pay.num1);
					}

					maxmoney = ManipulatePrecision.doubleConvert(maxmoney + tmpmoney, 2, 1);
				}
			}

			if (ManipulatePrecision.doubleCompare(maxmoney, minmoney, 2) < 0)
			{
				new MessageBox("券类型" + type + "不满足下列付款方式\n" + msg + "\n所定义的最低合计付款金额:" + minmoney + "元\n" + "当前" + msg + "付款总金额:" + String.valueOf(maxmoney) + "元\n");
				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			return false;
		}
	}

	public boolean payComplete()
	{
		// 存在找零
		if (ManipulatePrecision.doubleConvert(saleHead.sjfk - saleyfje - salezlexception, 2, 1) >= 0)
		{
			// 检查是否能够完成付款
			if (!checkLimitPay())
				return false;
		}

		// 检查付款是否足够
		if (!comfirmPay() || calcPayBalance() > 0 || (saleHead.sjfk <= 0 && GlobalInfo.sysPara.issaleby0 != 'Y'))
		{
			new MessageBox("付款金额不足!");
			return false;
		}

		// 付款完成处理
		if (!payCompleteDoneEvent())
			return false;

		// 找零处理
		PaymentChange pc = calcSaleChange();
		if (pc == null)
		{
			// 付款完成放弃
			payCompleteCancelEvent();

			return false;
		}

		// 付款确认
		new SaleShowAccountForm().open(saleEvent.saleBS);

		// 恢复状态，允许再次触发最后交易完成方法
		waitlab = false;

		// 交易未成功
		if (!saleFinish)
		{
			// 付款完成放弃
			payCompleteCancelEvent();

			// 清除找零
			pc.clearChange();
		}

		return saleFinish;
	}

	public boolean checkLimitPay()
	{
		boolean isOK0508 = checkLimitPay("0508");
		boolean isOK0555 = checkLimitPay("0555");

		if (isOK0508 && isOK0555)
			return true;

		return false;
	}

	public boolean checkLimitPay(String paycode)
	{
		boolean isdelcoupon = false;

		for (int i = 0; i < this.salePayment.size(); i++)
		{
			String type = null;
			String limitpaycode = null;
			double minmoney = 0.0;

			SalePayDef sp = (SalePayDef) salePayment.get(i);
			if (sp.paycode.equals(paycode))
			{
				if (sp.str1 == null || sp.str1.equals(""))
					continue;

				String[] chkcoupon = sp.str1.split("#");
				if (chkcoupon != null && chkcoupon.length > 0)
					type = chkcoupon[0];

				if (chkcoupon != null && chkcoupon.length > 1)
					minmoney = ManipulatePrecision.doubleConvert(Convert.toDouble(chkcoupon[1]), 2, 1);

				if (chkcoupon != null && chkcoupon.length > 2)
					limitpaycode = chkcoupon[2];

				if (!checkLimitPaycode(type, minmoney, limitpaycode))
					isdelcoupon = true;
			}
		}

		if (isdelcoupon)
		{
			// 存在找零，则将最后一个付款给删掉
			if (ManipulatePrecision.doubleConvert(saleHead.sjfk - saleyfje - salezlexception, 2, 1) > 0)
				deleteCouponPay(paycode, true);
			else
				deleteCouponPay(paycode, false);

			return false;
		}
		return true;
	}

	protected boolean deleteCouponPay(String paycode, boolean flag)
	{
		for (int i = 0; i < this.salePayment.size(); i++)
		{
			SalePayDef sp = (SalePayDef) salePayment.get(i);
			if (sp.paycode.equals(paycode))
			{
				int index = salePayment.indexOf(sp);
				deleteSalePay(index);
				i = i - 1;
			}
		}

		if (flag && salePayment.size() > 0)
			deleteSalePay(salePayment.size() - 1);

		this.salePayEvent.refreshSalePayment();

		return true;
	}

	public boolean findBackTicketInfo()
	{

		SaleHeadDef thsaleHead = null;
		Vector thsaleGoods = null;
		Vector thsalePayment = null;
		boolean isAllBack = true;

		try
		{
			// 如果是新指定小票进入
			if (saletype.equals(SellType.JDXX_BACK) || ((GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'C') && ((saleGoods.size() > 0 && isbackticket) || saleGoods.size() < 1)))
			{
				thsaleHead = new SaleHeadDef();
				thsaleGoods = new Vector();
				thsalePayment = new Vector();

				// 联网查询原小票信息
				ProgressBox pb = new ProgressBox();
				pb.setText("开始查找退货小票操作.....");
				if (!DataService.getDefault().getBackSaleInfo(thSyjh, String.valueOf(thFphm), thsaleHead, thsaleGoods, thsalePayment))
				{
					pb.close();
					pb = null;

					thSyjh = null;
					thFphm = 0;

					return false;
				}

				pb.close();
				pb = null;
				// 检查小票是否有满赠礼品，顾客退货，需要先退回礼品，再到收银台办理退货
				// Y为已在后台退回礼品 津乐会赠品退货
				if ((thsaleHead.str2.trim().equals("Y")))
				{
					new MessageBox("此小票有满赠礼品，请先到后台退回礼品再办理退货！");
					return false;
				}
				// 检查此小票是否已经退货过，给出提示ADD by lwj
				if (thsaleHead.str1.trim().length() > 0)
				{
					if (new MessageBox(thsaleHead.str1 + "\n是否继续退货？", null, true).verify() != GlobalVar.Key1) { return false; }
				}
				// 原交易类型和当前退货类型不对应，不能退货
				// 如果原交易为预售提货，不判断
				// 如果当前交易类型为家电退货,那么可以支持零售销售的退货
				if (!thsaleHead.djlb.equals(SellType.PREPARE_TAKE))
				{
					if (!SellType.getDjlbSaleToBack(thsaleHead.djlb).equals(this.saletype))
					{
						new MessageBox("原小票是[" + SellType.getDefault().typeExchange(thsaleHead.djlb, thsaleHead.hhflag, thsaleHead) + "]交易\n\n与当前退货交易类型不匹配");

						// 清空原收银机号和原小票号
						thSyjh = null;
						thFphm = 0;
						return false;
					}
				}

				// 显示原小票商品明细
				Vector choice = new Vector();
				String[] title = { "序", "商品编码", "商品名称", "原数量", "原折扣", "原成交价", "退货", "退货数量" };
				int[] width = { 30, 100, 170, 80, 80, 100, 60, 100, 55 };
				String[] row = null;
				for (int i = 0; i < thsaleGoods.size(); i++)
				{
					SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);
					row = new String[8];
					row[0] = String.valueOf(sgd.rowno);

					if (sgd.inputbarcode.equals(""))
					{
						if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("A"))
							sgd.inputbarcode = sgd.barcode;
						row[1] = sgd.barcode;
						if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("B"))
							sgd.inputbarcode = sgd.code;
						row[1] = sgd.code;
					}
					else
					{
						row[1] = sgd.inputbarcode;
					}

					row[2] = sgd.name;
					row[3] = ManipulatePrecision.doubleToString(sgd.sl, 4, 1, true);
					row[4] = ManipulatePrecision.doubleToString(sgd.hjzk);
					row[5] = ManipulatePrecision.doubleToString(sgd.hjje - sgd.hjzk);
					row[6] = "";
					row[7] = "";
					choice.add(row);
				}

				String[] title1 = { "序", "付款名称", "账号", "付款金额" };
				int[] width1 = { 30, 100, 250, 180 };
				String[] row1 = null;
				Vector content2 = new Vector();
				int j = 0;
				for (int i = 0; i < thsalePayment.size(); i++)
				{
					SalePayDef spd1 = (SalePayDef) thsalePayment.get(i);
					row1 = new String[4];
					row1[0] = String.valueOf(++j);
					row1[1] = String.valueOf(spd1.payname);
					row1[2] = String.valueOf(spd1.payno);
					row1[3] = ManipulatePrecision.doubleToString(spd1.je);
					content2.add(row1);
				}

				int cho = -1;
				if (EBill.getDefault().isEnable() && EBill.getDefault().isBack())
				{
					cho = EBill.getDefault().getChoice(choice);
				}
				else
				{
					// 选择要退货的商品
					cho = new MutiSelectForm().open("在以下窗口输入单品退货数量(回车键选择商品,付款键全选,确认键保存退出)", title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);
				}

				StringBuffer backYyyh = new StringBuffer();
				if (GlobalInfo.sysPara.backyyyh == 'Y')
				{
					new TextBox().open("开单营业员号：", "", "请输入有效开单营业员号", backYyyh, 0);
					// 查找营业员
					OperUserDef staff = null;
					if (backYyyh.length() != 0)
					{
						if ((staff = findYYYH(backYyyh.toString())) != null)
						{
							if (staff.type != '2')
							{
								new MessageBox("该工号不是营业员!", null, false);
								return false;
							}
						}
						else
						{
							return false;
						}
					}
					else
					{
						return false;
					}

				}

				// 如果cho小于0且已经选择过退货小票
				if (cho < 0 && isbackticket)
					return true;
				if (cho < 0)
				{
					thSyjh = null;
					thFphm = 0;
					return false;
				}

				// 清除已有商品明细,重新初始化交易变量

				// 将退货授权保存下来
				String thsq = saleHead.thsq;
				initSellData();

				// 生成退货商品明细
				for (int i = 0; i < choice.size(); i++)
				{
					row = (String[]) choice.get(i);
					if (!row[6].trim().equals("Y"))
					{
						isAllBack = false;
						continue;
					}

					SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);
					double thsl = ManipulatePrecision.doubleConvert(Convert.toDouble(row[7]), 4, 1);

					sgd.yfphm = sgd.fphm;
					sgd.ysyjh = sgd.syjh;
					sgd.yrowno = sgd.rowno;
					sgd.memonum1 = sgd.sl;
					sgd.syjh = ConfigClass.CashRegisterCode;
					sgd.fphm = GlobalInfo.syjStatus.fphm;
					sgd.rowno = saleGoods.size() + 1;
					sgd.str4 = backYyyh.toString();
					sgd.ysl = sgd.sl;

					// 重算商品行折扣
					if (ManipulatePrecision.doubleCompare(sgd.sl, thsl, 4) > 0)
					{
						sgd.hjje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hjje, sgd.sl), thsl), 2, 1); // 合计金额
						sgd.hyzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hyzke, sgd.sl), thsl), 2, 1); // 会员折扣额(来自会员优惠)
						sgd.yhzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.yhzke, sgd.sl), thsl), 2, 1); // 优惠折扣额(来自营销优惠)
						sgd.lszke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszke, sgd.sl), thsl), 2, 1); // 零时折扣额(来自手工打折)
						sgd.lszre = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszre, sgd.sl), thsl), 2, 1); // 零时折让额(来自手工打折)
						sgd.lszzk = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszzk, sgd.sl), thsl), 2, 1); // 零时总品折扣
						sgd.lszzr = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszzr, sgd.sl), thsl), 2, 1); // 零时总品折让
						sgd.plzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.plzke, sgd.sl), thsl), 2, 1); // 批量折扣
						sgd.zszke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.zszke, sgd.sl), thsl), 2, 1); // 赠送折扣
						sgd.cjzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.cjzke, sgd.sl), thsl), 2, 1); // 厂家折扣
						sgd.ltzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.ltzke, sgd.sl), thsl), 2, 1);
						sgd.hyzklje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hyzklje, sgd.sl), thsl), 2, 1);
						sgd.qtzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.qtzke, sgd.sl), thsl), 2, 1);
						sgd.qtzre = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.qtzre, sgd.sl), thsl), 2, 1);
						sgd.hjzk = getZZK(sgd);
						sgd.sl = thsl;
					}

					// 加入商品列表
					addSaleGoodsObject(sgd, null, new SpareInfoDef());
				}

				// 查找原交易会员卡资料
				if (thsaleHead.hykh != null && !thsaleHead.hykh.trim().equals(""))
				{
					curCustomer = new CustomerDef();
					curCustomer.code = thsaleHead.hykh;
					curCustomer.name = thsaleHead.hykh;
					curCustomer.ishy = 'Y';

					/*
					 * 业务过程只支持磁道查询,不支持卡号查询,因此无法检查原交易会员卡是否有效 if
					 * (!DataService.getDefault().getCustomer(curCustomer,
					 * thsaleHead.hykh)) { curCustomer.code = thsaleHead.hykh;
					 * curCustomer.name = "无效卡"; curCustomer.ishy = 'Y';
					 * 
					 * new MessageBox("原交易的会员卡可能已失效!\n请重新刷卡后进行退货"); }
					 */
				}

				// 标记不是全部退货
				if (!isAllBack)
					saleHead.str3 = "N";

				// 设置原小票头信息
				saleHead.hykh = thsaleHead.hykh;
				saleHead.hytype = thsaleHead.hytype;
				saleHead.jfkh = thsaleHead.jfkh;

				saleHead.thsq = thsq;
				saleHead.ghsq = thsaleHead.ghsq;
				saleHead.hysq = thsaleHead.hysq;
				saleHead.sqkh = thsaleHead.sqkh;
				saleHead.sqktype = thsaleHead.sqktype;
				saleHead.sqkzkfd = thsaleHead.sqkzkfd;
				saleHead.hhflag = hhflag;
				saleHead.jdfhdd = thsaleHead.jdfhdd;
				saleHead.jdfhdd = thSyjh;
				saleHead.salefphm = String.valueOf(thFphm);

				// 退货小票辅助处理
				takeBackTicketInfo(thsaleHead, thsaleGoods, thsalePayment);

				// 重算小票头
				calcHeadYsje();

				// 为了写入断点,要在刷新界面之前置为true
				isbackticket = true;

				// 检查是否超出退货限额
				if (curGrant.thxe > 0 && saleHead.ysje > curGrant.thxe)
				{
					OperUserDef staff = backSellGrant();
					if (staff == null)
					{
						initSellData();
						isbackticket = false;
					}
					else
					{
						if (staff.thxe > 0 && saleHead.ysje > staff.thxe)
						{
							new MessageBox("超出退货的最大限额，不能退货");

							initSellData();
							isbackticket = false;
						}
						else
						{
							// 记录日志
							saleHead.thsq = staff.gh;
							curGrant.privth = staff.privth;
							curGrant.thxe = staff.thxe;

							String log = "授权退货,小票号:" + saleHead.fphm + ",最大退货限额:" + curGrant.thxe + ",授权:" + staff.gh;
							AccessDayDB.getDefault().writeWorkLog(log);

							//
							new MessageBox("授权退货,限额为 " + ManipulatePrecision.doubleToString(curGrant.thxe) + " 元");
						}
					}
				}

				backPayment.removeAllElements();
				backPayment.addAll(thsalePayment);

				// 刷新界面显示
				saleEvent.clearTableItem();
				saleEvent.updateSaleGUI();

				return isbackticket;
			}

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (thsaleHead != null)
			{
				thsaleHead = null;
			}

			if (thsaleGoods != null)
			{
				thsaleGoods.clear();
				thsaleGoods = null;
			}

			if (thsalePayment != null)
			{
				thsalePayment.clear();
				thsalePayment = null;
			}
		}

	}

	public boolean deleteSalePay(int index, boolean isautodel)
	{
		// 是否允许删除当前付款方式
		if (!isautodel && !isDeletePay(index))
			return false;

		// 扣回处理
		if (isRefundStatus())
			return deleteRefundPay(index);

		try
		{
			if (index >= 0)
			{
				// 付款取消交易才能删除已付款
				Payment p = (Payment) payAssistant.elementAt(index);

				if (p.paymode.code.equals("0508") || p.paymode.code.equals("0555"))
				{
					if (new MessageBox("您确定要删除该付款项吗?\n\n由于券付款存在分摊,所以系统将删除所有该种方式付款", null, true).verify() == GlobalVar.Key1)
					{
						for (int i = 0; i < payAssistant.size(); i++)
						{
							Payment tmp = (Payment) payAssistant.elementAt(i);
							if (tmp.paymode.code.equals("0508") || tmp.paymode.code.equals("0555"))
							{
								delSalePayObject(i);
								tmp.cancelPay();
								i--;
							}
						}

						calcPayBalance();
						this.salePayEvent.refreshSalePayment();
						return true;
					}

					return false;
				}

				if (p.cancelPay())
				{
					// 删除已付款
					delSalePayObject(index);

					// 重算剩余付款
					calcPayBalance();

					// 刷新已付款，更新断点文件
					getSalePaymentDisplay();

					return true;
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}
}
