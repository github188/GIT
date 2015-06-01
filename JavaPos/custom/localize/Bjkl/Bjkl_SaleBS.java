package custom.localize.Bjkl;

import java.util.Vector;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.DownBaseTask;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentBankCMCC;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.DzcModeDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Bjkl_SaleBS extends Bjkl_SaleBS1Goods
{
	public void enterInput()
	{
		if (SellType.ISCARD(saletype))
			sellCard();
		else
			super.enterInput();
	}

	public void backSell()
	{
		if (SellType.ISCARD(saletype))
		{
			new MessageBox("售卡交易不允许退货!");
			return;
		}
		super.backSell();
	}

	public String getLastSjfk()
	{
		return ManipulatePrecision.doubleToString(lastsaleHead.sjfk + lastsaleHead.num10);
	}

	// 获取退货小票信息
	public boolean findBackTicketInfo()
	{

		SaleHeadDef thsaleHead = null;
		Vector thsaleGoods = null;
		Vector thsalePayment = null;

		try
		{
			if (GlobalInfo.sysPara.inputydoc == 'D')
			{
				// 只记录原单小票号和款机号,但不按原单找商品
				return false;
			}

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

				// str1控制是还是可以重复退货 检查此小票是否已经退货过

				if (thsaleHead.str1 != null && thsaleHead.str1.trim().equals("Y"))
				{
					if (GlobalInfo.sysPara.backticketctrl == 'Y')
					{
						new MessageBox("该小票已退货,不允许重复退货!");
						return false;
					}

					if (new MessageBox("该小票已退过货,是否继续退货?", null, true).verify() != GlobalVar.Key1)
						return false;
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
					sgd.sl = sgd.sl - sgd.num1;
					if (sgd.sl < 0)
						sgd.sl = 0;

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
						continue;

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
					sgd.hydjbh = sgd.hydjbh;

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
						// 添加规则促销和满减促销的折扣均摊
						sgd.rulezke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.rulezke, sgd.sl), thsl), 2, 1);
						sgd.mjzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.mjzke, sgd.sl), thsl), 2, 1);
						sgd.hjzk = getZZK(sgd);
						sgd.sl = thsl;
					}

					// 加入商品列表
					addSaleGoodsObject(sgd, null, new SpareInfoDef());
				}

				// 查找原交易会员卡资料
				if (thsaleHead.hykh != null && !thsaleHead.hykh.trim().equals(""))
				{
					HykInfoQueryBS vipbs = CustomLocalize.getDefault().createHykInfoQueryBS();
					// 输入顾客卡号
					TextBox txt = new TextBox();
					int type = vipbs.getMemberInputMode();

					if (txt.open(Language.apply("请刷原交易小票中的会员卡"), Language.apply("会员号"), Language.apply("请将会员卡或顾客打折卡从刷卡槽刷入"), null, 0, 0, false, type))
					{
						curCustomer = vipbs.findMemberCard(txt.Track2);
						if (curCustomer != null)
						{
							if (curCustomer.code.equals(thsaleHead.hykh))
							{
								// 设置原小票头信息
								saleHead.hykh = thsaleHead.hykh;
								saleHead.hytype = thsaleHead.hytype;
							}
							else
							{
								new MessageBox("当前所刷会员与原交易小票中会员不符!");
							}
						}
					}
				}

				saleHead.jfkh = thsaleHead.jfkh;
				saleHead.thsq = thsq;
				saleHead.ghsq = thsaleHead.ghsq;
				saleHead.hysq = thsaleHead.hysq;
				saleHead.sqkh = thsaleHead.sqkh;
				saleHead.sqktype = thsaleHead.sqktype;
				saleHead.sqkzkfd = thsaleHead.sqkzkfd;
				saleHead.hhflag = hhflag;
				saleHead.jdfhdd = thsaleHead.jdfhdd;
				saleHead.salefphm = thsaleHead.salefphm;

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

	public boolean analyzeBarcode(String barcode, String[] key)
	{
		String code = null;
		String price = null;
		String quantity = null;
		String scsj = null;

		int i;
		int j;

		try
		{
			DzcModeDef dzc = null;

			for (i = 0; i < GlobalInfo.dzcMode.size(); i++)
			{
				dzc = (DzcModeDef) GlobalInfo.dzcMode.elementAt(i);

				int location = dzc.symbolpos - 1;

				if ((barcode.trim().length() == dzc.length) && (barcode.substring(location, location + dzc.symbollen).trim().equals(dzc.symbol.trim())))
				{
					break;
				}
			}

			if (i >= GlobalInfo.dzcMode.size())
				return false;

			// 截取编码
			code = barcode.substring(dzc.codepos - 1, (dzc.codepos + dzc.codelen) - 1);

			// 当商品编码长度小于电子称规则定义的商品编码，导致解析出的商品编码前面加 ‘0’，这时需要将前面的0去掉，否则将导致无法查询到商品
			if (Convert.isLong(code))
			{
				code = String.valueOf(Convert.toLong(code));
			}

			// 截取价格
			if (dzc.pricelen > 0)
			{
				j = dzc.pricelen - dzc.pricedec;

				StringBuffer sb = new StringBuffer();
				sb.append(barcode.substring(dzc.pricepos - 1, (dzc.pricepos + j) - 1));
				sb.append(".");
				sb.append(barcode.substring((dzc.pricepos + j) - 1, (dzc.pricepos + dzc.pricelen) - 1));
				price = sb.toString();
			}
			else
			{
				price = "0";
			}

			// 截取数量
			if (dzc.quantitylen > 0)
			{
				j = dzc.quantitylen - dzc.quantitydec;

				StringBuffer sb = new StringBuffer();
				sb.append(barcode.substring(dzc.quantitypos - 1, (dzc.quantitypos + j) - 1));
				sb.append(".");
				sb.append(barcode.substring((dzc.quantitypos + j) - 1, (dzc.quantitypos + dzc.quantitylen) - 1));
				quantity = sb.toString();
			}
			else
			{
				quantity = "0";
			}

			// 截取生产时间
			if (dzc.timelen > 0)
			{
				scsj = barcode.substring(dzc.timepos - 1, (dzc.timepos + dzc.timelen) - 1);
			}
			else
			{
				scsj = "";
			}

			// 返回
			key[0] = code;
			key[1] = price;
			key[2] = quantity;
			key[3] = scsj;

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	// 按付款键之后自动付款
	public boolean autoPay()
	{
		if (!GlobalInfo.isOnline) { return false; }

		if (!SellType.ISSALE(saletype) || curCustomer == null)
			return false;

		// 当会员卡有余额时，显示会员卡信息
		if (curCustomer.value1 > 0)
		{
			Bjkl_HykInfoQueryBS hy = new Bjkl_HykInfoQueryBS();
			StringBuffer sb = new StringBuffer();
			hy.getHykDisplayInfo(curCustomer, sb);

			new MessageBox(sb.toString());
		}

		// 如果是扫的条码，则不容许使用折扣帐户余额
		if (curCustomer.valnum1 <= 0 || curCustomer.isHandInput)
			return false;

		// 折扣帐户支付，编码固定
		PayModeDef payMode = DataService.getDefault().searchPayMode("0113");
		if (payMode == null)
		{
			new MessageBox("未定义会员支付方式!");
			return false;
		}

		// 创建一个付款方式对象
		Payment pay = CreatePayment.getDefault().createPaymentByPayMode(payMode, this);
		if (pay == null)
			return false;

		if (!pay.createSalePay(""))
			return false;

		// 付款记账
		return payAccount(pay, pay.salepay);
	}

	// 零钞转存功能
	public boolean doLcZc(Label txt_zl, Group grp_zl_sy)
	{
		if (!GlobalInfo.isOnline)
			return false;

		double zlmoney = 0;

		// 销售交易才能转存
		if (!SellType.ISSALE(saletype) || curCustomer == null)
			return false;

		// 1.先计算实际可找零金额
		double zl = 0;
		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef sp = (SalePayDef) salePayment.elementAt(i);

			// 计算找零合计
			if (sp.flag == '2')
			{
				zl = ManipulatePrecision.add(zl, sp.je);
			}

			// 计算已有的转存金额，将转存金额补回到找零合计,得到未进行转存前真实的找零
			if (CreatePayment.getDefault().isPaymentLczc(sp))
			{
				zlmoney = ManipulatePrecision.add(zlmoney, sp.je * -1);
			}
		}
		zl = ManipulatePrecision.doubleConvert(ManipulatePrecision.add(zl, zlmoney));

		if (zl <= 0)
			return false;

		// 2.在存在找零的情况下，再判断会员卡找零状态
		if ("4".equals(curCustomer.valstr10))
			return false;

		if ("0".equals(curCustomer.valstr10))
		{
			// int key = new
			// MessageBox("请选择零钞转存的处理方式\n\n1--1元以下\n2--10元以下\n3--任意键-退出").verify();
			int key = new MessageBox("请选择零钞转存的处理方式\n\n1--1元以下\n2--10元以下\n3--任意键-退出").verify();

			if (key == GlobalVar.Key1)
			{
				curCustomer.valstr10 = "1";

				// if (CardModule.getDefault().cardCustSvc(curCustomer.code,
				// curCustomer.track, " 1", " 1"))
				// GlobalInfo.sysPara.lczcmaxmoney = 1;
				// else
				// return false;

				GlobalInfo.sysPara.lczcmaxmoney = 1;
			}
			else if (key == GlobalVar.Key2)
			{
				curCustomer.valstr10 = "2";

				// if (CardModule.getDefault().cardCustSvc(curCustomer.code,
				// curCustomer.track, " 1", " 2"))
				// GlobalInfo.sysPara.lczcmaxmoney = 10;
				// else
				// return false;
				GlobalInfo.sysPara.lczcmaxmoney = 10;
			}
			else
			{
				return false;
			}
		}
		else if ("1".equals(curCustomer.valstr10))
		{
			GlobalInfo.sysPara.lczcmaxmoney = 1;
		}
		else if ("2".equals(curCustomer.valstr10))
		{
			GlobalInfo.sysPara.lczcmaxmoney = 10;
		}

		// 存入低于参数的找零金额的零头部分,参数表示的意义是最小的可找零面额
		zlmoney = ManipulatePrecision.doubleConvert(zl % GlobalInfo.sysPara.lczcmaxmoney, 2, 1);

		// 先删除已存在的零钞转存
		deleteLcZc();

		// 再增加新的转存金额付款
		PayModeDef pay1 = DataService.getDefault().searchPayMode("0112");
		Bjkl_PaymentCustMzk pay = (Bjkl_PaymentCustMzk) CreatePayment.getDefault().createPaymentByPayMode(pay1, this);
		if (pay == null || !pay.createLczcSalePay(zlmoney))
		{
			new MessageBox(Language.apply("没有零钞转存付款方式 或 零钞转存对象创建失败\n\n无法进行零钞转存的功能!"));
			return false;
		}

		// num10记录存零金额，方便显示上笔实收
		saleHead.num10 = pay.salepay.je * -1;
		addSalePayObject(pay.salepay, pay);

		// 重新计算应收应付
		calcPayBalance();

		// 重新计算找零
		calcSaleChange();

		// 刷新找零窗口显示
		grp_zl_sy.setText(getChangeTitleLabel());
		grp_zl_sy.setText(grp_zl_sy.getText() + "/" + Language.apply("零钞转存") + "(" + ManipulatePrecision.doubleToString(zlmoney) + ")");
		txt_zl.setText(ManipulatePrecision.doubleToString(saleHead.zl));

		return true;
	}

	protected void initBusiness()
	{
		if (SellType.ISCARD(saletype))
		{
			sellCard();
			return;
		}
		super.initBusiness();
	}

	protected void sellCard()
	{
		try
		{
			String checkuserid = null;
			// 安全卡认证
			if ((checkuserid = CardModule.getDefault().grantSaleCard()) == null)
				return;

			do
			{
				if (saleHead.ysje >= 10000)
				{
					new MessageBox("当笔交易金额已超限!");
					return;
				}

				String cardno = null;

				StringBuffer sb = new StringBuffer();
				TextBox txt = new TextBox();
				// 只能刷卡
				if (!txt.open(Language.apply("请刷礼品卡"), Language.apply("卡号"), Language.apply("请将礼品卡从刷卡槽刷入"), sb, 0, 0, false, 2))
					return;

				if (txt.Track2.length() == 13)
				{
					cardno = "0001" + txt.Track2.substring(0, 12);
				}
				else if (txt.Track2.length() == 24)
				{
					cardno = txt.Track2.substring(0, 16);
				}
				else
				{
					new MessageBox("磁道信息有误,请重新刷卡!");
					continue;
				}

				// 库存查询
				if (!CardModule.getDefault().checkStock(cardno))
					continue;

				boolean issamegoods = false;
				for (int i = 0; i < saleGoods.size(); i++)
				{
					SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
					if (sgd.inputbarcode.equals(cardno))
					{
						new MessageBox("卡/券号:" + cardno + "已在列表中存在\n相同卡号不允许重复售卖!");
						issamegoods = true;
						break;
					}
				}

				if (issamegoods)
					continue;

				// 面值
				// 是需要从卡号上截取7-8位，02是200元，05是500元，10是1000元,之后将对应的CARDTYPESUB写上相应的21或23或24
				// 123456 78 90123456

				double[] amtInfo = CardModule.getDefault().convertCardno(cardno);
				if (amtInfo == null || amtInfo.length < 2)
				{
					new MessageBox("获取卡面值失败!");
					continue;
				}

				GoodsDef goodsDef = new GoodsDef();

				goodsDef.barcode = goodsDef.code = cardno;// "00000000";
				goodsDef.gz = cardno; // DOSPOS售卡卡号保存在 gz 字段，和DOSPOS保持一致，
				goodsDef.inputbarcode = cardno;
				// goodsDef.gz = "00";
				goodsDef.uid = "00";
				goodsDef.name = cardno + " 面值[" + ManipulatePrecision.doubleConvert(Convert.toDouble(cardno.substring(6, 8)) * 100, 2, 1) + "]元";
				goodsDef.lsj = ManipulatePrecision.doubleConvert(Convert.toDouble(new Double(amtInfo[1])), 2, 1);
				goodsDef.unit = "张";
				goodsDef.issqkzk = 'N';
				goodsDef.isvipzk = 'N';

				SaleGoodsDef saleGoodsDef = goodsDef2SaleGoods(goodsDef, saleEvent.yyyh.getText(), 1, goodsDef.lsj, 0, false);
				saleGoodsDef.str3 = checkuserid;
				saleGoodsDef.str4 = ((int) amtInfo[0]) + "";
				addSaleGoodsObject(saleGoodsDef, goodsDef, getGoodsSpareInfo(goodsDef, saleGoodsDef));
				getZZK(saleGoodsDef);

				// 添加卡套数据
				// goodsDef = (GoodsDef) goodsDef.clone();
				// goodsDef.lsj = 0.0;
				// goodsDef.inputbarcode = goodsDef.barcode = goodsDef.code =
				// "0000";
				// goodsDef.name = cardno + " 卡套";
				// saleGoodsDef = goodsDef2SaleGoods(goodsDef,
				// saleEvent.yyyh.getText(), 1, goodsDef.lsj, 0, false);
				// addSaleGoodsObject(saleGoodsDef, goodsDef,
				// getGoodsSpareInfo(goodsDef, saleGoodsDef));

				calcHeadYsje();

				refreshSaleForm();
				saleEvent.updateSaleGUI();

			}
			while (true);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void doSaleFinshed(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		if (SellType.ISCARD(saletype))
		{
			String paymethod = "1";
			String bankno = "";

			// 判断支付方法
			if (salePayment.size() == 1)
			{
				SalePayDef spd = (SalePayDef) salePayment.get(0);
				PayModeDef pmd = DataService.getDefault().searchPayMode(spd.paycode);
				if (pmd.type == '3')
				{
					paymethod = "3";
					bankno = spd.payno;
				}
				else
				{
					paymethod = "1";
				}
			}

			// 循环发送卡激活
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);

				if (sgd.barcode.equals("0000"))
					continue;

				// 记录str4方便后期小票打印
				sgd.str4 = CardModule.getDefault().saleCard(sgd.inputbarcode, paymethod, sgd.str4, sgd.lsj + "", sgd.str3, bankno);
			}
		}
	}

	public void printSaleBill()
	{
		if (SellType.ISCARD(saletype))
		{
			// 打印收银员联
			SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
			SaleBillMode.getDefault(saleHead.djlb).printBill();

			// 打印顾客联
			saleHead.ismemo = false;
			SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
			SaleBillMode.getDefault(saleHead.djlb).printBill();

			return;
		}

		// 打印小票前先查询满赠信息并设置到打印模板供打印
		if (!SellType.ISEXERCISE(saletype))
		{
			DataService dataservice = (DataService) DataService.getDefault();
			Vector gifts = dataservice.getSaleTicketMSInfo(saleHead, saleGoods, salePayment);
			SaleBillMode.getDefault(saleHead.djlb).setSaleTicketMSInfo(saleHead, gifts);
		}

		// 恢复暂停状态的实时打印
		stopRealTimePrint(false);

		// 实时打印只打印剩余部分
		if (isRealTimePrint())
		{
			SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);

			// 标记即扫即打结束
			Printer.getDefault().enableRealPrintMode(false);

			// 打印那些即扫即打未打印的商品
			for (int i = 0; i < saleGoods.size(); i++)
				realTimePrintGoods(null, i, true);

			// 打印即扫即打剩余小票部分
			SaleBillMode.getDefault(saleHead.djlb).printRealTimeBottom();

			//
			setHaveRealTimePrint(false);
		}
		else
		{
			SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
			// 打印整张小票
			SaleBillMode.getDefault(saleHead.djlb).printBill();
		}

		// 只在交易完成时打印一次移动离线充值券,因此无需放到小票模板中
		if (GlobalInfo.useMobileCharge)
		{
			PaymentBankCMCC pay = CreatePayment.getDefault().getPaymentMobileCharge(this.saleEvent.saleBS);
			if (pay != null)
				pay.printOfflineChargeBill(saleHead.fphm);
		}
	}

	public Vector getPayModeBySuper(String sjcode, StringBuffer index, String code)
	{
		Vector child = new Vector();
		String[] temp = null;
		PayModeDef mode = null;
		int k = -1;
		for (int i = 0; i < GlobalInfo.payMode.size(); i++)
		{
			mode = (PayModeDef) GlobalInfo.payMode.elementAt(i);

			if (("," + GlobalInfo.sysPara.visiblepaycode + ",").indexOf("," + mode.code + ",") > -1)
				continue;

			if (SellType.ISCARD(saletype))
				if (("," + GlobalInfo.sysPara.limitpaytype + ",").indexOf("," + mode.code + ",") == -1)
					continue;

			if (SellType.ISBACK(saletype))
			{
				if (("," + GlobalInfo.sysPara.backPaycode + ",").indexOf("," + mode.code + ",") == -1)
					continue;
			}

			if ((mode.sjcode.trim().equals(sjcode.trim()) || (sjcode.equals("0") && mode.sjcode.trim().equals(mode.code))) && getPayModeByNeed(mode))
			{
				k++;

				// 标记code付款方式在vector中的位置
				if (index != null && code != null && mode.code.compareTo(code) == 0)
				{
					index.append(String.valueOf(k));
				}

				//
				if (GlobalInfo.sysPara.salepayDisplayRate == 'Y')
				{
					temp = new String[3];
					temp[0] = mode.code.trim();
					temp[1] = mode.name;
					temp[2] = ManipulatePrecision.doubleToString(mode.hl, 4, 1, false);
				}
				else
				{
					temp = new String[2];
					temp[0] = mode.code.trim();
					temp[1] = mode.name;
					if (mode.hl != 1)
						temp[1] = temp[1] + "<" + ManipulatePrecision.doubleToString(mode.hl, 4, 1, false) + ">";
				}
				child.add(temp);
			}

		}

		return child;

	}

	// 付款记账，此时把京客隆所有卡信息汇总提交（折扣账户付款金额 + 卡付款金额 + 储值卡 + 零钞转存金额） 发送给卡库，并积分

	public boolean saleCollectAccountPay()
	{
		if (SellType.ISCARD(saletype))
			return true;

		Payment p = null;

		boolean isexistvip = false;

		// 付款对象记账,先记录 消费积分存入的会员卡（第三方卡系统要求，消费积分存入的会员卡要在消费记录的第一笔位置）
		// 当使用多张会员卡支付时，1. 只要在销售界面刷过会员卡， 则消费积分 存入此会员卡。2.若只在支付时刷多张卡，则第一张卡做为积分会员卡
		for (int i = 0; i < payAssistant.size(); i++)
		{
			p = (Payment) payAssistant.elementAt(i);

			if (p == null)
				continue;

			// if (p.salepay.payno.startsWith("0618"))
			if (curCustomer != null && p.salepay.payno.equals(curCustomer.code))
			{
				// 付款记账
				if (!p.collectAccountPay())
					return false;

				// 折扣账户支付未记录在卡消费位置
				if (p.salepay.isused == 'Y')
					continue;

				p.salepay.str3 = "Y"; // 标记该条数据已发
				isexistvip = true;
				break;
			}
		}

		// 有会员（在销售界面刷个会员卡），未使用此会员卡支付，依然将 消费积分 存入此会员卡（此时将消费金额设置为 0 进行记录）
		if (!isexistvip && curCustomer != null)
		{
			PaymentMzk pay = CreatePayment.getDefault().getPaymentMzk();
			PayModeDef payMode = DataService.getDefault().searchPayMode("0113");
			pay.initPayment(payMode, this);
			pay.createSalePay("0");
			pay.extendAction1();
		}

		// 付款对象记账,先找到会员帐号;
		for (int i = 0; i < payAssistant.size(); i++)
		{
			p = (Payment) payAssistant.elementAt(i);

			if (p == null || p.salepay.str3.equals("Y"))
				continue;

			// 付款记账
			if (!p.collectAccountPay())
				return false;
		}

		PaymentMzk pay = CreatePayment.getDefault().getPaymentMzk();
		if (!pay.extendAction2(null, null))
			return false;

		// 记录积分到小票头上（SaleHead.bcjf,SaleHead.ljjf)

		System.out.println("本次积分:" + pay.mzkret.value1);
		System.out.println("累计积分:" + pay.mzkret.value2);
		saleHead.bcjf = pay.mzkret.value1;
		saleHead.ljjf = pay.mzkret.value2;

		// 记录会员卡零钱包余额
		for (int i = 0; i < payAssistant.size(); i++)
		{
			p = (Payment) payAssistant.elementAt(i);
			if (p != null && p.salepay.paycode.equals("0112") && p.salepay.memo != null && p.salepay.memo.equals("3") && pay.mzkret != null)
			{
				p.salepay.kye = pay.mzkret.ye;
				break;
			}
		}

		return true;
	}

	public boolean payAccount(PayModeDef mode, String money)
	{
		if (salePayment.size() >= getMaxSalePayCount())
		{
			new MessageBox(Language.apply("目前输入的付款明细已达到上限,不能继续付款!"));
			return false;
		}

		// 检查当前交易是否允许使用该付款方式
		if (!checkPaymodeValid(mode, money))
			return false;

		// 先计算出满减的金额
		double bankzke = bankRebate(mode);
		money = ManipulatePrecision.doubleConvert(Convert.toDouble(money) - bankzke, 2, 1) + "";

		// 创建一个付款方式对象
		Payment pay = CreatePayment.getDefault().createPaymentByPayMode(mode, saleEvent.saleBS);
		if (pay == null)
			return false;

		// inputPay这个方法根据不同的付款方式进行重写
		SalePayDef sp = pay.inputPay(money);

		if (!payAccount(pay, sp))
			return false;

		// 处理银联满减生成一个0108的付款对象
		if (mode.type == '3' && bankzke > 0 && isPopBankCard(sp.payno.substring(0, 6)))
		{
			PayModeDef pmd = DataService.getDefault().searchPayMode("0108");
			Payment bankzkpay = CreatePayment.getDefault().createPaymentByPayMode(pmd, saleEvent.saleBS);
			if (bankzkpay.createSalePayObject(bankzke + ""))
			{
				addSalePayObject(bankzkpay.salepay, bankzkpay);
				bankzkpay.salepay.payno = sp.payno; // 记录银行卡号
				bankzkpay.salepay.str4 = sp.rowno + ""; // 记录序号，方便删除
				calcPayBalance();
			}
		}

		return true;
	}

	public double calcPayBalance()
	{
		// 如果是扣回付款,付款余额为扣回余额
		if (isRefundStatus())
			return calcRefundBalance();

		// 计算实际付款
		SalePayDef paydef = null;
		double payje = 0;
		double sy = 0;
		for (int i = 0; i < salePayment.size(); i++)
		{
			paydef = (SalePayDef) salePayment.elementAt(i);
			if (paydef.flag == '1' || paydef.flag == '4')
			{
				payje += paydef.je;
				sy += paydef.num1; // 付款方式中不记入付款的溢余部分
			}
		}
		saleHead.sjfk = ManipulatePrecision.doubleConvert(payje, 2, 1);
		salezlexception = sy; // 所有不记入付款的溢余合计,计算找零时要减出该部分

		// 计算付款余额
		// 如果付款产生损溢超过四舍五入产生的损溢则补偿了这部分，应付金额中不应再包含这部分
		if (salezlexception >= Math.abs(saleHead.sswr_sysy))
			sy = saleHead.sswr_sysy;
		else
			sy = salezlexception;

		// 当实际付款方式的价额进度符合应付价额精度时，剩余付款不进行补偿
		if (ManipulatePrecision.getDoubleScale(saleyfje) == ManipulatePrecision.getDoubleScale(saleHead.sjfk - salezlexception))
			sy = 0;
		double ye = (saleyfje - sy) - (saleHead.sjfk - salezlexception);
		if (ye < 0)
			ye = 0;

		if (ManipulatePrecision.doubleCompare(ye, GlobalInfo.sysPara.lackpayfee, 2) < 0)
			ye = 0;
		// if (ye < GlobalInfo.sysPara.lackpayfee) ye = 0;

		return ManipulatePrecision.doubleConvert(ye, 2, 1);
	}

	protected double getBankRebateAmount()
	{
		double money = 0.0;
		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) salePayment.get(i);
			if (spd.flag == '4')
				money += spd.je;
		}
		return money;
	}

	protected boolean isPopBankCard(String cardprefix)
	{
		if (GlobalInfo.sysPara.bankreate.equals(""))
			return false;

		String[] pop = GlobalInfo.sysPara.bankreate.split("\\|");
		if (pop == null || pop.length <= 0)
			return false;

		for (int i = 0; i < pop.length; i++)
		{
			String[] item = pop[i].split(",");
			if (item == null || item.length < 3)
				continue;

			if ((";" + item[0] + ";").indexOf(";" + cardprefix + ";") >= 0)
				return true;
		}

		return false;
	}

	protected double bankRebate(PayModeDef mode)
	{
		// 售卡交易类型不容许使用银行赠送
		if (SellType.ISCARD(saletype)) { return 0; }

		if (GlobalInfo.sysPara.bankreate.equals(""))
			return 0;

		// 非银联卡不处理
		if (mode.type != '3')
			return 0;

		// 卡1bin前6位;卡2bin前6位;...,满金额/减金额,封顶金额|...
		String[] pop = GlobalInfo.sysPara.bankreate.split("\\|");
		if (pop == null || pop.length <= 0)
			return 0;

		for (int i = 0; i < pop.length; i++)
		{
			String[] item = pop[i].split(",");
			if (item == null || item.length < 3)
				continue;

			String[] rule = item[1].split("/");
			if (rule == null || rule.length < 2)
				continue;

			double popmoney = Convert.toDouble(rule[0].trim());
			if (popmoney <= 0 || popmoney > calcPayBalance())
				continue;

			double popzke = Convert.toDouble(rule[1].trim());
			if (popzke <= 0)
				continue;

			double tmpje = getBankRebateAmount();
			// 判断当前的满减金额是否超过了封顶金额
			if (tmpje < Convert.toDouble(item[2]))
			{
				// 计算剩余可减金额
				double leftje = Convert.toDouble(item[2]) - tmpje;
				popzke = Math.min(popzke, leftje);

				if (new MessageBox("银行优惠活动\n银行卡参加满【 " + rule[0] + " 】元减【" + rule[1] + "】元的活动,\n确定参加活动？", null, true).verify() != GlobalVar.Key1) { return 0; }

				return popzke;
			}
			else
			{
				popzke = 0;
				break;
			}
		}

		return 0;
	}

	public void enterInputCODE()
	{

		if (isPreTakeStatus())
		{
			new MessageBox(Language.apply("预售提货状态下不允许修改商品状态"));
			return;
		}

		boolean findok = false;

		// if (saleEvent.code.getText().trim().length() > 30)
		// {
		// new MessageBox(Language.apply("非合法的商品编码不允许进行销售\n当前编码长度") +
		// saleEvent.code.getText().length());
		// saleEvent.code.selectAll();
		// return;
		// }

		// 盘点
		if (SellType.ISCHECKINPUT(saletype))
		{
			String code = saleEvent.code.getText().trim();

			if (code.length() <= 0 && saleGoods.size() > 0)
			{

				code = ((SaleGoodsDef) saleGoods.elementAt(saleGoods.size() - 1)).barcode;
			}

			if (code.length() > 0 && findCheckGoods(code, saleEvent.yyyh.getText(), getGzCode(saleEvent.gz.getText())))
			{
				findok = true;
			}
		}
		else if (SellType.ISCOUPON(saletype))
		{
			// 买券
			if (findCoupon(saleEvent.code.getText(), saleEvent.yyyh.getText(), getGzCode(saleEvent.gz.getText())))
			{
				findok = true;
			}
		}
		else if (SellType.isJS(saletype))
		{
			// 缴费
			if (findJSDetail(saleEvent.code.getText(), saleEvent.yyyh.getText(), getGzCode(saleEvent.gz.getText())))
			{
				findok = true;
			}
		}
		else if (SellType.isJF(saletype))
		{
			// 结算
			if (findJFDetail(saleEvent.code.getText(), saleEvent.yyyh.getText(), getGzCode(saleEvent.gz.getText())))
			{
				findok = true;
			}
		}
		else if (SellType.ISJFSALE(saletype))
		{
			// 买积分
			if (findJf(saleEvent.code.getText(), saleEvent.yyyh.getText(), getGzCode(saleEvent.gz.getText())))
			{
				findok = true;
			}
		}
		else
		{
			// 超市或开发模式直接按回车 = 扫描上一个商品
			String code = saleEvent.code.getText().trim();
			// if (code.length() <= 0)
			// {
			// GoodsSearchForm window = new GoodsSearchForm();
			// window.open();
			// }

			if ((GlobalInfo.sysPara.quickinputsku == 'Y' && saleEvent.yyyh.getText().trim().equals("超市") || ConfigClass.isDeveloperMode()) && code.length() <= 0 && saleGoods.size() > 0)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(saleGoods.size() - 1);
				if (sg.inputbarcode != null && sg.inputbarcode.length() > 0)
					code = sg.inputbarcode;
				else
					code = sg.barcode;
			}

			if (code.length() > 0 && findGoods(code, saleEvent.yyyh.getText(), getGzCode(saleEvent.gz.getText())))
			{
				findok = true;
			}
		}

		// 清除输入框
		if (findok)
		{
			refreshSaleForm();
			doShowInfoFinish();
		}
		else
		{
			saleEvent.code.selectAll();
		}
	}

	public void realTimePrintStartSale()
	{
		if (!isRealTimePrint())
			return;

		// 打印即扫即打小票头
		SaleBillMode.getDefault().setTemplateObject(saleHead, saleGoods, salePayment, true);
		SaleBillMode.getDefault().printRealTimeHead();

		// 标记即扫即打开始
		Printer.getDefault().enableRealPrintMode(true);

		// 标记
		setHaveRealTimePrint(true);
	}

	public void realTimePrintGoods(SaleGoodsDef oldGoods, int index, boolean flag)
	{
		if (!isRealTimePrint())
			return;

		// 填充即扫即打模式的打印标记
		while (realTimePrintFlag.length() < saleGoods.size())
		{
			realTimePrintFlag.append('N');
		}

		// 打印以被即扫即打的商品，对应的相反商品数据
		if (oldGoods != null && (index < 0 || (index >= 0 && realTimePrintFlag.charAt(index) != 'N')))
		{
			getMinusSaleCom(oldGoods);
			SaleBillMode.getDefault().printRealTimeDetail(oldGoods);

			// 设置为未打印标志
			if (index >= 0)
			{
				realTimePrintFlag.setCharAt(index, 'N');
			}
		}

		// 打印真实商品明细
		if (index >= 0 && realTimePrintFlag.charAt(index) == 'N' && oldGoods == null)
		{
			SaleBillMode.getDefault().printRealTimeDetail(index);

			// 设置为已打印标志
			realTimePrintFlag.setCharAt(index, 'Y');
		}

		setHaveRealTimePrint(true);

	}

	public void realTimePrintGoods(SaleGoodsDef oldGoods, int index)
	{
		realTimePrintGoods(oldGoods, index, false);
	}

	public void sellFinishComplete()
	{
		// 在进行下笔交易前,执行一次定时器任务，检查网上通知及任务
		TaskExecute.getDefault().executeTimeTask(false);

		// 在进行下笔交易前,检查是否有一次定时下载因为销售状态被放弃,如果有则下载一次
		DownBaseTask.onceRun();
		// 开始新交易
		saleEvent.initOneSale(this.saletype);
	}
}
