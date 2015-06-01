package custom.localize.Shhl;

import java.util.Vector;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBankCMCC;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SaleSummaryDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Shhl_SaleBS extends Shhl_SaleBS1Goods
{
	protected SaleHeadDef ysaleHead = null;
	protected Vector ysaleGoods = null;
	protected Vector ysalePay = null;

	public void setMoneyInputDefault(Text txt, PayModeDef paymode)
	{
		if (CreatePayment.getDefault().allowQuickInputMoney(paymode) || (GlobalInfo.sysPara.isInputPayMoney == 'Y' && paymode.ismj == 'Y'))
		{
			// 一级主付款方式,允许直接输入付款金额
			txt.setEditable(true);

			// 付款覆盖模式,找已有的付款金额
			if (GlobalInfo.sysPara.payover == 'Y')
			{
				int i = existPayment(paymode.code, "", true);
				if (i >= 0)
				{
					SalePayDef salepay = (SalePayDef) salePayment.elementAt(i);
					txt.setText(ManipulatePrecision.doubleToString(salepay.ybje));
					txt.selectAll();
					return;
				}
			}

			// 计算剩余付款
			double needPay = calcPayBalance();
			if (paymode.hl <= 0)
				paymode.hl = 1;

			// 非整单退货时，默认带出
			if (SellType.ISBACK(saletype) && (thFphm == 0 && thSyjh == null) && paymode.code.equals("0101"))
			{
				txt.setText(String.valueOf(ManipulatePrecision.doubleConvert(needPay / paymode.hl + 0.09, 1, 0)));
			}
			else
			{
				if (GlobalInfo.sysPara.isMoneyInputDefault == 'Y')
				{
					txt.setText(getPayMoneyByPrecision(needPay / paymode.hl, paymode));
				}
				else
				{
					if (GlobalInfo.sysPara.MoneyInputDefaultPay == null || GlobalInfo.sysPara.MoneyInputDefaultPay.equals(""))
					{
						txt.setText("0");
					}
					else
					{
						boolean isexist = false;

						String[] paycodes = GlobalInfo.sysPara.MoneyInputDefaultPay.split(",");

						for (int i = 0; i < paycodes.length; i++)
						{
							if (paycodes[i].trim().equals(paymode.code))
							{
								txt.setText("0");
								isexist = true;
								break;
							}
						}

						if (!isexist)
							txt.setText(getPayMoneyByPrecision(needPay / paymode.hl, paymode));
					}
				}
			}
			txt.selectAll();
		}
		else
		{
			// 一级主付款方式,不允许直接输入金额
			// 二级辅付款方式,允许输入付款代码
			if (paymode.level <= 1)
			{
				txt.setText("");
				txt.setEditable(false);
			}
			else
			{
				if (GlobalInfo.sysPara.isusepaySelect == 'Y')
				{
					txt.setText("");
					txt.setEditable(true);
				}
				else
				{
					txt.setText("");
					txt.setEditable(false);
				}

			}
		}
	}

	public void initNewSale()
	{
		super.initNewSale();
		ysaleHead = null;
		ysaleGoods = null;
		ysalePay = null;
	}

	public boolean findBackTicketInfo()
	{
		SaleHeadDef thsaleHead = null;
		Vector thsaleGoods = null;
		Vector thsalePayment = null;

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

				ysaleHead = (SaleHeadDef) thsaleHead.clone();
				ysaleGoods = new Vector();
				ysalePay = new Vector();

				for (int i = 0; i < thsaleGoods.size(); i++)
				{
					SaleGoodsDef sgd = (SaleGoodsDef) ((SaleGoodsDef) thsaleGoods.get(i)).clone();
					ysaleGoods.add(sgd);
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
				// 选择要退货的商品
				cho = new MutiSelectForm().open("在以下窗口输入单品退货数量(回车键选择商品,付款键全选,确认键保存退出)", title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);

				// 如果cho小于0且已经选择过退货小票
				if (cho < 0 && isbackticket)
					return true;

				if (cho < 0)
				{
					thSyjh = null;
					thFphm = 0;
					return false;
				}

				// 将退货授权保存下来
				String thsq = saleHead.thsq;
				initSellData();

				ysaleHead.fphm = saleHead.fphm;
				ysaleHead.yfphm = String.valueOf(thFphm);

				// 生成退货商品明细
				for (int i = 0; i < choice.size(); i++)
				{
					row = (String[]) choice.get(i);
					if (!row[6].trim().equals("Y"))
						continue;

					SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);
					SaleGoodsDef tmpSgd = (SaleGoodsDef) ysaleGoods.get(i);
					double thsl = ManipulatePrecision.doubleConvert(Convert.toDouble(row[7]), 4, 1);

					sgd.yfphm = sgd.fphm;
					sgd.ysyjh = sgd.syjh;
					sgd.yrowno = sgd.rowno;
					sgd.memonum1 = sgd.sl;
					sgd.syjh = ConfigClass.CashRegisterCode;
					sgd.fphm = GlobalInfo.syjStatus.fphm;
					sgd.rowno = saleGoods.size() + 1;
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

					tmpSgd.ysl = tmpSgd.sl;
					tmpSgd.sl = tmpSgd.sl - thsl;

					// 重算商品行折扣
					tmpSgd.hjje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(tmpSgd.hjje, tmpSgd.ysl), tmpSgd.sl), 2, 1); // 合计金额
					tmpSgd.hyzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(tmpSgd.hyzke, tmpSgd.ysl), tmpSgd.sl), 2, 1); // 会员折扣额(来自会员优惠)
					tmpSgd.yhzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(tmpSgd.yhzke, tmpSgd.ysl), tmpSgd.sl), 2, 1); // 优惠折扣额(来自营销优惠)
					tmpSgd.lszke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(tmpSgd.lszke, tmpSgd.ysl), tmpSgd.sl), 2, 1); // 零时折扣额(来自手工打折)
					tmpSgd.lszre = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(tmpSgd.lszre, tmpSgd.ysl), tmpSgd.sl), 2, 1); // 零时折让额(来自手工打折)
					tmpSgd.lszzk = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(tmpSgd.lszzk, tmpSgd.ysl), tmpSgd.sl), 2, 1); // 零时总品折扣
					tmpSgd.lszzr = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(tmpSgd.lszzr, tmpSgd.ysl), tmpSgd.sl), 2, 1); // 零时总品折让
					tmpSgd.plzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(tmpSgd.plzke, tmpSgd.ysl), tmpSgd.sl), 2, 1); // 批量折扣
					tmpSgd.zszke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(tmpSgd.zszke, tmpSgd.ysl), tmpSgd.sl), 2, 1); // 赠送折扣
					tmpSgd.cjzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(tmpSgd.cjzke, tmpSgd.ysl), tmpSgd.sl), 2, 1); // 厂家折扣
					tmpSgd.ltzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(tmpSgd.ltzke, tmpSgd.ysl), tmpSgd.sl), 2, 1);
					tmpSgd.hyzklje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(tmpSgd.hyzklje, tmpSgd.ysl), tmpSgd.sl), 2, 1);
					tmpSgd.qtzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(tmpSgd.qtzke, tmpSgd.ysl), tmpSgd.sl), 2, 1);
					tmpSgd.qtzre = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(tmpSgd.qtzre, tmpSgd.ysl), tmpSgd.sl), 2, 1);
					tmpSgd.hjzk = getZZK(tmpSgd);

				}

				removeGoods(ysaleGoods);

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

				// 退货小票辅助处理
				takeBackTicketInfo(thsaleHead, thsaleGoods, thsalePayment);

				// 重算小票头
				calcHeadYsje();
				calcLeaveYfje(ysaleHead, ysaleGoods);

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

	public boolean removeGoods(Vector salegoods)
	{
		for (int i = 0; i < salegoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) salegoods.get(i);
			if (sgd.sl == 0)
			{
				salegoods.removeElement(sgd);
				i--;
			}
		}
		return true;
	}

	public void calcLeaveYfje(SaleHeadDef saleHead, Vector saleGoods)
	{
		int sign = 1;

		saleHead.hjzje = 0;
		saleHead.hjzsl = 0;
		saleHead.hjzke = 0;
		saleHead.hyzke = 0;
		saleHead.yhzke = 0;
		saleHead.lszke = 0;

		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

			if (!statusCond(saleGoodsDef))
				continue;

			// 合计商品件数(电子秤商品总是按1件记数)
			int spjs = (int) saleGoodsDef.sl;
			if (saleGoodsDef.flag == '2')
				spjs = 1;

			saleHead.hjzsl += spjs;

			// 以旧换新商品,合计要减
			if (saleGoodsDef.type == '8')
			{
				sign = -1;
			}
			else
			{
				sign = 1;
			}

			// 计算小票头汇总
			saleHead.hjzje = ManipulatePrecision.doubleConvert(saleHead.hjzje + (saleGoodsDef.hjje * sign), 2, 1); // 合计总金额
			saleHead.hjzke = ManipulatePrecision.doubleConvert(saleHead.hjzke + (saleGoodsDef.hjzk * sign), 2, 1); // 合计折扣额

			saleHead.hyzke = ManipulatePrecision.doubleConvert(saleHead.hyzke + (saleGoodsDef.hyzke * sign), 2, 1); // 会员折扣额(来自会员优惠)
			saleHead.hyzke = ManipulatePrecision.doubleConvert(saleHead.hyzke + (saleGoodsDef.hyzklje * sign), 2, 1); // 会员折扣率金额(来自会员优惠)

			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.yhzke * sign), 2, 1); // 优惠折扣额(来自营销优惠)
			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.zszke * sign), 2, 1); // 赠送折扣
			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.rulezke * sign), 2, 1); // 超市规则促销折扣（非整单折扣）
			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.mjzke * sign), 2, 1); // 超市规则促销折扣（整单折扣）

			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszke * sign), 2, 1); // 零时折扣额(来自手工打折)
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszre * sign), 2, 1); // 零时折让额(来自手工打折)
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszzk * sign), 2, 1); // 零时总品折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszzr * sign), 2, 1); // 零时总品折让
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.plzke * sign), 2, 1); // 批量折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.cjzke * sign), 2, 1); // 厂家折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.ltzke * sign), 2, 1); // 零头折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.qtzke * sign), 2, 1); // 其他折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.qtzre * sign), 2, 1); // 其他折扣
		}

		saleHead.num1 = saleHead.ysje;
		saleHead.sjfk = saleHead.ysje = ManipulatePrecision.doubleConvert(saleHead.hjzje - saleHead.hjzke, 2, 1);
		saleHead.sswr_sysy = ManipulatePrecision.sub(getDetailOverFlow(saleHead.ysje), saleHead.ysje);

	}

	public boolean clearSell(int index)
	{
		// 先取消VIP或临时折扣
		if (cancelMemberOrGoodsRebate(index)) { return true; }

		if (saleGoods.size() <= 0)
		{
			// 退货交易切换回销售交易
			if (SellType.ISBACK(saletype))
			{
				djlbBackToSale();
			}

			//
			initOneSale(this.saletype);

			return true;
		}

		if (new MessageBox(Language.apply("你确定要取消本笔交易输入吗?"), null, true).verify() != GlobalVar.Key1)
			return false;

		// 没有取消权限
		String grantgh;

		if (operPermission(clearPermission, curGrant))
		{
			OperUserDef staff = clearSellGrant();

			if (staff == null) { return false; }

			grantgh = staff.gh;
		}
		else
		{
			grantgh = saleHead.syyh;
		}

		//
		if (!SellType.ISEXERCISE(this.saletype))
		{
			// 记录日志
			String log = "取消交易,小票号:" + Convert.increaseLong(saleHead.fphm, 7) + ",金额:" + Convert.increaseChar(ManipulatePrecision.doubleToString(saleHead.ysje), '0', 10) + ",授权:" + grantgh;
			AccessDayDB.getDefault().writeWorkLog(log, StatusType.WORK_CLEARSALE);

			// 记汇总
			SaleSummaryDef saleSummaryDef = new SaleSummaryDef();
			saleSummaryDef.zl = 0;
			saleSummaryDef.sysy = 0;
			saleSummaryDef.sjfk = 0;
			saleSummaryDef.zkje = 0;
			saleSummaryDef.ysje = 0;
			saleSummaryDef.qxbs = 1;
			saleSummaryDef.qxje = saleHead.ysje;

			// 写入全天销售统计
			saleSummaryDef.bc = '0';
			saleSummaryDef.syyh = "全天";
			AccessDayDB.getDefault().writeSaleSummary(saleSummaryDef);

			// 写入当班收银员销售统计
			saleSummaryDef.bc = saleHead.bc;
			saleSummaryDef.syyh = saleHead.syyh;
			AccessDayDB.getDefault().writeSaleSummary(saleSummaryDef);
		}

		SaleHeadDef shd = (SaleHeadDef) this.saleHead.clone();
		shd.djlb = "CANCEL";

		Vector sgds = new Vector();
		for (int i = 0; i < this.saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) ((SaleGoodsDef) saleGoods.get(i)).clone();
			sgds.add(sgd);

		}
		Vector spys = new Vector();
		for (int i = 0; i < this.salePayment.size(); i++)
		{
			SalePayDef sgd = (SalePayDef) ((SalePayDef) salePayment.get(i)).clone();
			spys.add(sgd);
		}

		SaleBillMode.getDefault().setTemplateObject(shd, sgds, spys);
		SaleBillMode.getDefault().printBill();

		// 退货交易切换回销售交易
		if (SellType.ISBACK(saletype))
		{
			djlbBackToSale();
		}

		// 初始化新交易
		initOneSale(this.saletype);

		return true;
	}

	public boolean saleFinishDone(Label status, StringBuffer waitKeyCloseForm)
	{
		try
		{
			// 如果没有连接打印机则连接
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null && !Printer.getDefault().getStatus())
			{
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
			}

			// 标记最后交易完成方法已开始，避免重复触发
			if (!waitlab)
				waitlab = true;
			else
				return false;

			// 输入小票附加信息
			if (!inputSaleAppendInfo())
			{
				new MessageBox("小票附加信息输入失败,不能完成交易!");
				return false;
			}

			//
			setSaleFinishHint(status, "正在汇总交易数据,请等待.....");
			if (!saleSummary())
			{
				new MessageBox("交易数据汇总失败!");

				return false;
			}

			//
			setSaleFinishHint(status, "正在校验数据平衡,请等待.....");
			if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods, salePayment))
			{
				new MessageBox("交易数据校验错误!");

				return false;
			}

			// 最终效验
			if (!checkFinalStatus()) { return false; }

			// 不是练习交易数据写盘
			if (!SellType.ISEXERCISE(saletype))
			{
				// 输入顾客信息
				setSaleFinishHint(status, "正在输入客户信息,请等待......");
				selectAllCustomerInfo();

				//
				setSaleFinishHint(status, "正在打开钱箱,请等待.....");
				CashBox.getDefault().openCashBox();

				//
				setSaleFinishHint(status, "正在记账付款数据,请等待.....");
				if (!saleCollectAccountPay())
				{
					new MessageBox("付款数据记账失败\n\n稍后将自动发起已记账付款的冲正!");

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, "正在发送冲正数据,请等待.....");
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				setSaleFinishHint(status, "正在写入交易数据,请等待......");
				if (!AccessDayDB.getDefault().writeSale(saleHead, saleGoods, salePayment))
				{
					new MessageBox("交易数据写盘失败!");
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",发生数据写盘失败", StatusType.WORK_SENDERROR);

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, "正在发送冲正数据,请等待.....");
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				// 小票已写盘,本次交易就要认为完成,即使后续处理异常也要返回成功
				saleFinish = true;

				if (SellType.ISBACK(saletype))
					doSalePay();

				// 小票保存成功以后，及时清除断点
				setSaleFinishHint(status, "正在清除断点保护数据,请等待......");
				clearBrokenData();

				//
				setSaleFinishHint(status, "正在清除付款冲正数据,请等待......");
				if (!saleCollectAccountClear())
				{
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票清除冲正数据失败,但小票已成交保存", StatusType.WORK_SENDERROR);

					new MessageBox("小票已成交保存,但清除冲正数据失败\n\n请完成本笔交易后重启款机尝试删除记账冲正数据!");
				}

				// 上传当前小票
				setSaleFinishHint(status, "正在上传交易小票数据,请等待......");
				boolean bsend = GlobalInfo.isOnline;
				if (!DataService.getDefault().sendSaleData(saleHead, saleGoods, salePayment))
				{
					// 联网时发送小票却失败才记录日志
					if (bsend)
					{
						AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",联网销售时小票送网失败", StatusType.WORK_SENDERROR);
					}
				}

				// 发送当前收银状态
				setSaleFinishHint(status, "正在上传收银机交易汇总,请等待......");
				DataService.getDefault().sendSyjStatus();

				// 打印小票
				setSaleFinishHint(status, "正在打印交易小票,请等待......");
				printSaleBill(true);

				if (SellType.ISBACK(saletype))
				{
					// 打印小票
					setSaleFinishHint(status, "正在打印交易小票,请等待......");
					printSaleBill(false);
				}
			}
			else
			{
				if (GlobalInfo.sysPara.lxprint == 'Y')
				{
					// 打印小票
					setSaleFinishHint(status, "正在打印交易小票,请等待......");
					printSaleBill();

					if (SellType.ISBACK(saletype))
					{
						// 打印小票
						setSaleFinishHint(status, "正在打印交易小票,请等待......");
						printSaleBill(false);
					}
				}

				// 标记本次交易已完成
				saleFinish = true;
			}

			// 返回到正常销售界面
			backToSaleStatus();

			// 保存本次的小票头
			if (saleFinish && saleHead != null)
			{
				lastsaleHead = saleHead;
			}

			// 清除本次交易数据
			this.initNewSale();

			// 关闭钱箱
			setSaleFinishHint(status, "正在等待关闭钱箱,请等待......");
			if (GlobalInfo.sysPara.closedrawer == 'Y')
			{
				// 如果钱箱能返回状态，采用等待钱箱关闭的方式来关闭找零窗口
				if (CashBox.getDefault().canCheckStatus())
				{
					// 等待钱箱关闭,最多等待一分钟
					int cnt = 0;
					while (CashBox.getDefault().getOpenStatus() && cnt < 30)
					{
						Thread.sleep(2000);

						cnt++;
					}

					// 等待一分钟后,钱箱还未关闭，标记为要等待按键才关闭找零窗口
					if (CashBox.getDefault().getOpenStatus() && cnt >= 30)
					{
						waitKeyCloseForm.delete(0, waitKeyCloseForm.length());
						waitKeyCloseForm.append("Y");
					}
				}
				else
				{
					// 标记为要等待按键才关闭找零窗口
					waitKeyCloseForm.delete(0, waitKeyCloseForm.length());
					waitKeyCloseForm.append("Y");
				}
			}

			// 交易完成
			setSaleFinishHint(status, "本笔交易结束,开始新交易");

			// 标记本次交易已完成
			saleFinish = true;

			return saleFinish;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			new MessageBox("完成交易时发生异常:\n\n" + ex.getMessage());

			return saleFinish;
		}
	}

	protected boolean doSalePay()
	{
		if (ysaleHead == null || ysaleGoods == null || ysaleGoods.size() == 0)
			return false;

		SalePayDef salepay = null;
		PayModeDef paymode = DataService.getDefault().searchPayMode("1001");
		if (paymode == null)
			return false;

		salepay = new SalePayDef();
		salepay.syjh = saleHead.syjh;
		salepay.fphm = saleHead.fphm;
		salepay.paycode = paymode.code;
		salepay.payname = paymode.name;
		salepay.flag = '1';
		salepay.ybje = Convert.toDouble(getPayMoneyByPrecision(ysaleHead.num1 - saleHead.ysje, paymode));
		salepay.hl = paymode.hl;
		salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2, 1);
		salepay.payno = "";
		salepay.batch = "";
		salepay.kye = 0;
		salepay.idno = "";
		salepay.memo = "";
		salepay.str1 = "";
		salepay.str2 = "";
		salepay.str3 = "";
		salepay.str4 = "";
		salepay.str5 = "";
		salepay.num1 = 0;
		salepay.num2 = 0;
		salepay.num3 = 0;
		salepay.num4 = 0;
		salepay.num5 = 0;
		salepay.num6 = 0;

		ysalePay.add(salepay);
		ysaleHead.num1 = ManipulatePrecision.doubleConvert(Convert.toDouble(getPayMoneyByPrecision(saleHead.ysje, paymode)), 2, 1); // 记录实退金额
		ysaleHead.memo = "AllBack";

		return true;

	}

	public void printSaleBill(boolean flag)
	{
		if (!flag)
		{
			if (ysaleHead == null || ysaleGoods == null || ysaleGoods.size() == 0 || ysalePay == null || ysalePay.size() == 0)
				return;
		}
		// 打印小票前先查询满赠信息并设置到打印模板供打印
		if (!SellType.ISEXERCISE(saletype))
		{
			if (flag)
			{
				DataService dataservice = (DataService) DataService.getDefault();
				Vector gifts = dataservice.getSaleTicketMSInfo(saleHead, saleGoods, salePayment);
				SaleBillMode.getDefault(saleHead.djlb).setSaleTicketMSInfo(saleHead, gifts);
			}
		}

		// 恢复暂停状态的实时打印
		stopRealTimePrint(false);

		// 实时打印只打印剩余部分
		if (isRealTimePrint())
		{
			if (flag)
			{
				SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
				// 标记即扫即打结束
				Printer.getDefault().enableRealPrintMode(false);
				// 打印那些即扫即打未打印的商品
				for (int i = 0; i < saleGoods.size(); i++)
					realTimePrintGoods(null, i);

				// 打印即扫即打剩余小票部分
				SaleBillMode.getDefault(saleHead.djlb).printRealTimeBottom();

				setHaveRealTimePrint(false);
			}
			else
			{
				SaleBillMode.getDefault(ysaleHead.djlb).setTemplateObject(ysaleHead, ysaleGoods, ysalePay);
				// 标记即扫即打结束
				Printer.getDefault().enableRealPrintMode(false);
				// 打印那些即扫即打未打印的商品
				for (int i = 0; i < ysaleGoods.size(); i++)
					realTimePrintGoods(null, i);

				// 打印即扫即打剩余小票部分
				SaleBillMode.getDefault(ysaleHead.djlb).printRealTimeBottom();

				setHaveRealTimePrint(false);
			}
		}
		else
		{
			if (flag)
			{
				SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
				// 打印整张小票
				SaleBillMode.getDefault(saleHead.djlb).printBill();
			}
			else
			{
				SaleBillMode.getDefault(ysaleHead.djlb).setTemplateObject(ysaleHead, ysaleGoods, ysalePay);
				// 打印整张小票
				SaleBillMode.getDefault(ysaleHead.djlb).printBill();
			}
		}

		// 只在交易完成时打印一次移动离线充值券,因此无需放到小票模板中
		if (GlobalInfo.useMobileCharge)
		{
			PaymentBankCMCC pay = CreatePayment.getDefault().getPaymentMobileCharge(this.saleEvent.saleBS);
			if (pay != null)
			{
				if (flag)
					pay.printOfflineChargeBill(saleHead.fphm);
				else
					pay.printOfflineChargeBill(ysaleHead.fphm);
			}
		}
	}

	public void exitSell()
	{
		// 检查是否允许退出
		if (!checkAllowExit()) { return; }

		// 如果是退货则回到销售状态
		if (SellType.ISBACK(this.saletype) && (GlobalInfo.posLogin.privth != 'T'))
		{
			if (new MessageBox(Language.apply("你确定从退货切换到销售状态吗?"), null, true).verify() == GlobalVar.Key1)
			{
				// 退回对应的销售类型
				djlbBackToSale();

				// 初始化交易
				initOneSale(this.saletype);
			}
		}
		else if (!SellType.getDefault().COMMONBUSINESS(this.saletype, this.hhflag, this.saleHead) && ManipulateStr.textInString("0101", GlobalInfo.posLogin.funcmenu, ",", false))
		{
			if (new MessageBox(Language.apply("你确定返回到正常销售状态吗?"), null, true).verify() == GlobalVar.Key1)
			{
				// 退回对应的销售类型
				this.saletype = SellType.RETAIL_SALE;
				this.hhflag = 'N';

				// 初始化交易
				initOneSale(this.saletype);
			}
		}
		else
		{
			if (!isExistHangBill())
				return;

			if (new MessageBox(Language.apply("你确定要退出收银系统吗?"), null, true).verify() == GlobalVar.Key1)
			{
				// 关闭销售界面
				saleEvent.saleform.dispose();

				// 退出系统
				AccessDayDB.getDefault().writeWorkLog(Language.apply("收银员登出"), StatusType.WORK_RELOGIN);
				GlobalInfo.background.quitSysInfo();
			}

		}
	}
}
