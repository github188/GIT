package custom.localize.Gbyw;

import java.util.Vector;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bstd.Bstd_SaleBS;

public class Gbyw_SaleBS extends Bstd_SaleBS
{
	public void initNewSale()
	{
		Gbyw_MzkModule.getDefault().initData();
		super.initNewSale();
	}

	public void execCustomKey0(boolean keydownonsale)
	{
		Gbyw_MzkModule.getDefault().exeOtherFunc();
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
				pb.setText(Language.apply("开始查找退货小票操作....."));
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

				// 检查此小票是否已经退货过，给出提示ADD by lwj
				if (thsaleHead.str1.trim().length() > 0)
				{
					if (new MessageBox(thsaleHead.str1 + Language.apply("\n是否继续退货？"), null, true).verify() != GlobalVar.Key1) { return false; }
				}
				// 原交易类型和当前退货类型不对应，不能退货
				// 如果原交易为预售提货，不判断
				// 如果当前交易类型为家电退货,那么可以支持零售销售的退货
				if (!thsaleHead.djlb.equals(SellType.PREPARE_TAKE))
				{
					if (!SellType.getDjlbSaleToBack(thsaleHead.djlb).equals(this.saletype))
					{
						new MessageBox(Language.apply("原小票是[{0}]交易\n\n与当前退货交易类型不匹配", new Object[] { SellType.getDefault().typeExchange(thsaleHead.djlb, thsaleHead.hhflag, thsaleHead) }));
						// new MessageBox("原小票是[" +
						// SellType.getDefault().typeExchange(thsaleHead.djlb,
						// thsaleHead.hhflag, thsaleHead) +
						// "]交易\n\n与当前退货交易类型不匹配");

						// 清空原收银机号和原小票号
						thSyjh = null;
						thFphm = 0;
						return false;
					}
				}

				// 显示原小票商品明细
				Vector choice = new Vector();
				String[] title = { Language.apply("序"), Language.apply("商品编码"), Language.apply("商品名称"), Language.apply("原数量"), Language.apply("原折扣"), Language.apply("原成交价"), Language.apply("退货"), Language.apply("退货数量") };
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

				String[] title1 = { Language.apply("序"), Language.apply("付款名称"), Language.apply("账号"), Language.apply("付款金额") };
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
					cho = new MutiSelectForm().open(Language.apply("在以下窗口输入单品退货数量(回车键选择商品,付款键全选,确认键保存退出)"), title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);
				}

				StringBuffer backYyyh = new StringBuffer();
				if (GlobalInfo.sysPara.backyyyh == 'Y')
				{
					new TextBox().open(Language.apply("开单营业员号："), "", Language.apply("请输入有效开单营业员号"), backYyyh, 0);
					// 查找营业员
					OperUserDef staff = null;
					if (backYyyh.length() != 0)
					{
						if ((staff = findYYYH(backYyyh.toString())) != null)
						{
							if (staff.type != '2')
							{
								new MessageBox(Language.apply("该工号不是营业员!"), null, false);
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

				/*
				 * if (thsaleHead.hykh != null &&
				 * !thsaleHead.hykh.trim().equals(""))
				 * {
				 * HykInfoQueryBS vipbs =
				 * CustomLocalize.getDefault().createHykInfoQueryBS();
				 * 
				 * String track2 = vipbs.readMemberCard(false);
				 * 
				 * // 查找会员卡
				 * curCustomer = vipbs.findMemberCard(track2);
				 * if (curCustomer != null)
				 * {
				 * if (curCustomer.code.equals(thsaleHead.hykh))
				 * {
				 * // 设置原小票头信息
				 * saleHead.hykh = thsaleHead.hykh;
				 * saleHead.hytype = thsaleHead.hytype;
				 * saleHead.str10 = curCustomer.valstr4;
				 * }
				 * else
				 * {
				 * new MessageBox("当前所刷会员与原交易小票中会员不符!");
				 * }
				 * }
				 * 
				 * }
				 */

				// 查找原交易会员卡资料
				if (thsaleHead.hykh != null && !thsaleHead.hykh.trim().equals(""))
				{
					curCustomer = new CustomerDef();
					curCustomer.code = thsaleHead.hykh;
					curCustomer.name = thsaleHead.hykh;
					curCustomer.ishy = 'Y';
				}

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
				saleHead.salefphm = thsaleHead.salefphm;

				saleHead.str2 = thsaleHead.str2; // 返回的会员内卡号
				saleHead.num1 = thsaleHead.num1; // 返回的卡型标志

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
							new MessageBox(Language.apply("超出退货的最大限额，不能退货"));

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
							new MessageBox(Language.apply("授权退货,限额为 {0} 元", new Object[] { ManipulatePrecision.doubleToString(curGrant.thxe) }));
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

	public boolean checkFinalStatus()
	{
		if (this.curCustomer != null)
		{
			saleHead.ljjf = this.curCustomer.valuememo;
		}

		return true;
	}

	public void customerIsHy(CustomerDef cust)
	{
		if (cust != null && cust.str1 != null)
			saleHead.str2 = cust.str1;

		if (cust != null)
			saleHead.num1 = cust.value6;

		super.customerIsHy(cust);
	}

	public boolean doBankLcZc(Label txt_zl, Group grp_zl_sy)
	{
		double zlmoney = 0;
		boolean showtips = !(GlobalInfo.sysPara.isAutoLczc == 'Y'); // 是否强制存入零钞

		// 销售交易才能转存
		if (!SellType.ISSALE(saletype))
		{
			if (showtips)
				new MessageBox(Language.apply("必须是销售模式才能进行零钞转存的功能!"));
			return false;
		}

		if (GlobalInfo.sysPara.lczcmaxmoney <= 0)
		{
			if (showtips)
				new MessageBox(Language.apply("系统参数定义最大零钞转存金额小于等于0\n\n无法进行零钞转存的功能!"));
			return false;
		}

		// 计算实际可找零金额
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
			if (sp.paycode.equals("0111") && sp.memo.trim().equals("3"))
			{
				zlmoney = ManipulatePrecision.add(zlmoney, sp.je * -1);
			}
		}
		zl = ManipulatePrecision.doubleConvert(ManipulatePrecision.add(zl, zlmoney));
		if (zl <= 0)
		{
			if (showtips)
				new MessageBox(Language.apply("当前无找零金额\n\n无法进行零钞转存的功能!"));

			return false;
		}

		// 强制零钞转存则自动存入参数定义的最大金额,否则提示输入找零金额
		if (GlobalInfo.sysPara.isAutoLczc == 'Y')
		{
			// 存入低于参数的找零金额的零头部分,参数表示的意义是最小的可找零面额
			double maxlczc = ManipulatePrecision.doubleConvert(zl % GlobalInfo.sysPara.lczcmaxmoney);

			zlmoney = maxlczc;
		}
		else
		{
			double maxlczc = zl;

			// 输入转存金额
			StringBuffer buffer = new StringBuffer();
			buffer.append(ManipulatePrecision.doubleToString(Math.min(zl, 0.00)));
			String line = "本笔应找零金额为 " + ManipulatePrecision.doubleToString(zl, 2, 1) + " 元\n" + "本次最多允许进行 " + ManipulatePrecision.doubleToString(maxlczc, 2, 1) + " 元的零钞转存";
			if (!new TextBox().open("请输入您要进行零钞转存的金额", "金额", line, buffer, 0.01, maxlczc, true))
				return false;

			zlmoney = Double.parseDouble(buffer.toString());

			if (zlmoney == 0.00)
				return false;

			if (zlmoney > GlobalInfo.sysPara.lczcmaxmoney)
			{
				new MessageBox(Language.apply("输入的转存充值金额大于系统定义的{0}元\n无法进行零钞转存的功能!", new Object[] { ManipulatePrecision.doubleToString(GlobalInfo.sysPara.lczcmaxmoney) }));
				return false;
			}
		}

		// 先删除已存在的零钞转存
		deleteLcZc();

		// 再增加新的转存金额付款

		PayModeDef pmd = DataService.getDefault().searchPayMode("0111");
		if (pmd == null)
		{
			new MessageBox("未定义编码为0111的付款方式!");
			return false;
		}

		Gbyw_PaymenBanktLczc pay = new Gbyw_PaymenBanktLczc(pmd, this);
		if (pay == null || !pay.createLczcSalePay(zlmoney))
		{
			new MessageBox("零钞转存对象创建失败!");
			return false;
		}

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

	public boolean doLcZc(Label txt_zl, Group grp_zl_sy)
	{
		if (saleHead.num1 == 1)
			return doBankLcZc(txt_zl, grp_zl_sy);

		if (GlobalInfo.sysPara.isAutoLczc != 'Y')
		{
			new MessageBox("零钞转存功能未启用");
			return false;
		}

		double zlmoney = 0;

		// 销售交易才能转存
		if (!SellType.ISSALE(saletype))
		{
			new MessageBox("必须是销售模式才能进行零钞转存的功能!");
			return false;
		}

		if (GlobalInfo.sysPara.lczcmaxmoney <= 0)
		{
			new MessageBox("系统参数定义最大零钞转存金额小于等于0\n\n无法进行零钞转存的功能!");
			return false;
		}

		// 计算实际可找零金额
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
			if (Gbyw_PaymentCoin.isPaymentLczc(sp))
			{
				zlmoney = ManipulatePrecision.add(zlmoney, sp.je * -1);
			}
		}

		zl = ManipulatePrecision.doubleConvert(ManipulatePrecision.add(zl, zlmoney));
		if (zl <= 0)
		{
			// new MessageBox("当前无找零金额\n\n无法进行零钞转存的功能!");
			return false;
		}

		// 找零充值方式
		Vector vec = new Vector();
		// 可以使用会员零钞转存
		if (curCustomer != null)
		{
			vec.add(new String[] { "会员零钞转存", "将找零存入会员卡的零钞账户", "HY" });
		}

		// 选择零钞转存方式
		String lczcmode = null, lczcdesc = null;
		if (vec.size() <= 0)
		{
			new MessageBox("没有刷会员卡 或 会员没有定义零钞转存功能\n\n无法使用会员零钞转存功能!");
			return false;
		}
		else if (vec.size() == 1)
		{
			lczcdesc = ((String[]) vec.elementAt(0))[0];
			lczcmode = ((String[]) vec.elementAt(0))[2];
		}

		double maxlczc = zl;

		if ("HY".equals(lczcmode))
		{
			// value2表示会员卡零钞账户的余额上限,value1表示会员卡零钞账户的当前余额,value4表示会员卡零钞账户每次存入上限
			if (curCustomer.value2 != 0)
				maxlczc = Math.min(maxlczc, ManipulatePrecision.doubleConvert(curCustomer.value2 - curCustomer.value1));
			if (curCustomer.value4 > 0)
				maxlczc = Math.min(maxlczc, curCustomer.value4);
		}

		// 输入转存金额
		StringBuffer buffer = new StringBuffer();
		buffer.append(ManipulatePrecision.doubleToString(zl));
		String line = "本笔应找零金额为 " + ManipulatePrecision.doubleToString(zl, 2, 1) + " 元\n" + "本次最多允许进行 " + ManipulatePrecision.doubleToString(maxlczc, 2, 1) + " 元的" + lczcdesc;
		if (!new TextBox().open("请输入您要进行" + lczcdesc + "的金额", "金额", line, buffer, 0.01, maxlczc, true)) { return false; }
		zlmoney = Double.parseDouble(buffer.toString());
		if (zlmoney > GlobalInfo.sysPara.lczcmaxmoney)
		{
			new MessageBox("输入的转存充值金额大于系统定义的 " + ManipulatePrecision.doubleToString(GlobalInfo.sysPara.lczcmaxmoney) + " 元\n无法进行零钞转存的功能!");
			return false;
		}
		if ("HY".equals(lczcmode) && (curCustomer.value2 != 0 && (zlmoney + curCustomer.value1) > curCustomer.value2))
		{
			new MessageBox("该会员账户的零钞余额已经到达最大的上限金额\n无法进行零钞转存的功能!");
			return false;
		}

		// 先删除已存在的零钞转存
		if (!deleteLcZc())
		{
			new MessageBox("取消之前的零钞转存失败!");
			return false;
		}

		// 再增加新的转存金额付款
		if ("HY".equals(lczcmode))
		{
			PayModeDef paymode = DataService.getDefault().searchPayMode("0403");
			if (paymode == null)
			{
				new MessageBox("未定义0403付款方式\n无法进行零钞转存的功能!");
				return false;
			}
			Gbyw_PaymentCoin pay = new Gbyw_PaymentCoin(paymode, saleEvent.saleBS);

			if (pay == null || !pay.createLczcSalePay(zlmoney))
			{
				new MessageBox("没有零钞转存付款方式 或 零钞转存对象创建失败\n\n无法进行零钞转存的功能!");
				return false;
			}
		}

		// 重新计算应收应付
		calcPayBalance();

		// 重新计算找零
		calcSaleChange();

		// 刷新找零窗口显示
		grp_zl_sy.setText(getChangeTitleLabel());
		grp_zl_sy.setText(grp_zl_sy.getText() + "/零钞转存(" + ManipulatePrecision.doubleToString(zlmoney) + ")");
		txt_zl.setText(ManipulatePrecision.doubleToString(saleHead.zl));

		return true;
	}

	public boolean deleteLcZc()
	{
		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) salePayment.elementAt(i);

			if (spd.paycode.equals("0403") && spd.memo.trim().equals("3"))
			{
				String line = "05," + spd.payno + "," + saleHead.ysje + "," + 0 + "," + GlobalInfo.syjStatus.syjh + "," + ManipulateStr.PadLeft(String.valueOf(spd.fphm), 12, '0') + "," + GlobalInfo.sysPara.commMerchantId + "," + String.valueOf(spd.je);

				line = Gbyw_MzkVipModule.getDefault().sendData(line);

				if (line == null)
					return false;

				String[] item = line.split(",");

				if (item == null)
					return false;

				if (item.length > 0)
				{
					if (!item[0].equals("0"))
					{
						new MessageBox(Gbyw_MzkVipModule.getDefault().getError(item[0]));
						return false;
					}
				}
				delSalePayObject(i);
			}
			else if (spd.paycode.equals("0111") && spd.memo.trim().equals("3"))
			{
				Payment p = (Payment) payAssistant.elementAt(i);

				if (p.cancelPay())
				{
					delSalePayObject(i);
					return true;
				}
			}
		}

		return true;
	}
}
