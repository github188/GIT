package custom.localize.Lydf;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Vector;

import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
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
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bstd.Bstd_SaleBS;

public class Lydf_SaleBS extends Bstd_SaleBS
{
	protected SaleHeadDef ysaleHead = null;
	protected Vector ysaleGoods = null;
	protected Vector ysalePay = null;

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
				for (int j = 0; j < thsalePayment.size(); j++)
				{
					SalePayDef spay = (SalePayDef) ((SalePayDef) thsalePayment.get(j)).clone();
					ysalePay.add(spay);
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
					}
					return false;
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

				// 将退货授权保存下来
				String thsq = saleHead.thsq;
				initSellData();

				ysaleHead.fphm = saleHead.fphm;
				ysaleHead.yfphm = String.valueOf(thFphm);
				ysaleHead.num1 = ysaleHead.ysje; // 保存小票的实际金额

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

				Lydf_Util.removeGoods(ysaleGoods);

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

	protected boolean doSalePay()
	{
		double levavemoney = 0.0;
		double zl = 0.0;

		try
		{
			zl = Lydf_Util.getChangeMone(ysalePay);
			if (Lydf_Util.removeChangel(ysalePay, zl))
				zl = 0.0;

			// 匹配金额
			for (int i = 0; i < ysalePay.size(); i++)
			{
				SalePayDef pay = (SalePayDef) ysalePay.get(i);
				pay.rowno = i;
				PayModeDef paydef = DataService.getDefault().searchPayMode(pay.paycode);
				// 现金
				if (paydef.type == '1')
				{
					for (int j = 0; j < salePayment.size(); j++)
					{
						SalePayDef curpay = (SalePayDef) salePayment.get(j);
						if (curpay.paycode.equals(pay.paycode) && curpay.flag == '1')
						{
							if (levavemoney > 0)
							{
								pay.je = ManipulatePrecision.doubleConvert(pay.je - levavemoney, 2, 1);
								levavemoney = 0.0;
							}

							if (curpay.je < pay.je)
							{
								pay.je = ManipulatePrecision.doubleConvert(pay.je - curpay.je, 2, 1);
								break;
							}

							if (curpay.je >= pay.je)
							{
								levavemoney = ManipulatePrecision.doubleConvert(curpay.je - pay.je, 2, 1);
								pay.je = 0.0;

								ysalePay.remove(i);
								i--;
								break;
							}
						}
					}

				}
				// 信用卡
				else if (paydef.type == '3')
				{
					for (int j = 0; j < salePayment.size(); j++)
					{
						SalePayDef curpay = (SalePayDef) salePayment.get(j);
						if (curpay.paycode.equals(pay.paycode) && curpay.je == pay.je)
						{
							ysalePay.remove(i);
							i--;
							break;
						}
					}
				}
				// 面值卡
				else if (paydef.type == '4')
				{
					for (int j = 0; j < salePayment.size(); j++)
					{
						SalePayDef curpay = (SalePayDef) salePayment.get(j);
						if (curpay.paycode.equals(pay.paycode))
						{
							if (levavemoney > 0)
							{
								pay.je = ManipulatePrecision.doubleConvert(pay.je - levavemoney, 2, 1);
								levavemoney = 0.0;
							}

							if (curpay.je < pay.je)
							{
								pay.je = ManipulatePrecision.doubleConvert(pay.je - curpay.je, 2, 1);
								break;
							}

							if (curpay.je >= pay.je)
							{
								levavemoney = ManipulatePrecision.doubleConvert(curpay.je - pay.je, 2, 1);
								pay.je = 0.0;

								ysalePay.remove(i);
								i--;
								break;
							}
						}
					}
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
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

				// 处理交易完成后一些后续动作
				doSaleFinshed(saleHead, saleGoods, salePayment);

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

	public boolean reSend(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Lydf_TaxInfo taxinfo)
	{
		ProgressBox pb = new ProgressBox();
		try
		{
			if (SellType.ISSALE(taxinfo.saletype) && taxinfo.oprtype.equals("0"))
			{
				pb.setText("正在发送税控开票数据，请稍等...");
				System.out.println("重发开票");
				if (Lydf_Taxer.getDefault().execute(1, saleHead, saleGoods, salePayment, taxinfo))
				{
					saleHead.salefphm = taxinfo.PH;
					saleHead.str3 = taxinfo.toString();

					((Lydf_NetService) NetService.getDefault()).sendSaleTax(saleHead, taxinfo);

					SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
					SaleBillMode.getDefault(saleHead.djlb).printBill();
					return true;
				}
			}
			else if (SellType.ISBACK(taxinfo.saletype))
			{
				if (taxinfo.oprtype.equals("1"))
				{

					pb.setText("正在发送税控票据作废数据，请稍等...");
					if (saleHead.salefphm != null && saleHead.salefphm.length() > 0)
					{
						System.out.println("重发作废");
						if (Lydf_Taxer.getDefault().execute(3, saleHead, null, null, taxinfo))
						{
							SaleHeadDef tmpshd = (SaleHeadDef) saleHead.clone();
							tmpshd.ysje = 0.0;
							taxinfo.memo = tmpshd.str3;

							((Lydf_NetService) NetService.getDefault()).sendSaleTax(saleHead, taxinfo);

							pb.setText("正在重新发送税控票据开票数据，请稍等...");

							taxinfo = new Lydf_TaxInfo();
							taxinfo.saletype = saleHead.djlb;
							taxinfo.oprtype = "0"; // 开票

							System.out.println("重发作废开票0");
							if (Lydf_Taxer.getDefault().execute(1, saleHead, saleGoods, salePayment, taxinfo))
							{
								// 保存打印
								saleHead.salefphm = taxinfo.PH;
								saleHead.str3 = taxinfo.toString();

								((Lydf_NetService) NetService.getDefault()).sendSaleTax(saleHead, taxinfo);
								SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
								SaleBillMode.getDefault(saleHead.djlb).printBill();
								return true;
							}
							else
							{
								Lydf_Util.writeTaxFile(saleHead, saleGoods, salePayment, taxinfo);
								return false;
							}
						}
					}
				}
				else if (taxinfo.oprtype.equals("0"))
				{
					System.out.println("重发作废开票1");
					if (Lydf_Taxer.getDefault().execute(1, saleHead, saleGoods, salePayment, taxinfo))
					{
						// 保存打印
						saleHead.salefphm = taxinfo.PH;
						saleHead.str3 = taxinfo.toString();

						((Lydf_NetService) NetService.getDefault()).sendSaleTax(saleHead, taxinfo);
						SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
						SaleBillMode.getDefault(saleHead.djlb).printBill();
						return true;
					}
				}
				else if (taxinfo.oprtype.equals("2"))
				{
					pb.setText("正在发送税控票据红冲数据，请稍等...");
					if (saleHead.salefphm != null && saleHead.salefphm.length() > 0)
					{
						saleHead.djlb = SellType.getReFlush(saleHead.djlb);

						System.out.println("重发红冲开票");
						if (Lydf_Taxer.getDefault().execute(1, saleHead, saleGoods, salePayment, taxinfo))
						{
							// 保存打印
							saleHead.salefphm = taxinfo.PH;
							saleHead.str3 = taxinfo.toString();

							((Lydf_NetService) NetService.getDefault()).sendSaleTax(saleHead, taxinfo);
							SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
							SaleBillMode.getDefault(saleHead.djlb).printBill();
							return true;
						}
					}
				}

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
			if (pb != null)
				pb.close();
		}
	}

	public void doSaleFinshed(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		if (SellType.ISSALE(saleHead.djlb))
			return;

		if (SellType.ISBACK(saleHead.djlb))
		{
			if (ysaleHead == null)
				return;
		}

		if (!doSalePay())
		{
			new MessageBox("处理退货付款失败");
			return;
		}

		ProgressBox pb = new ProgressBox();
		try
		{
			Lydf_TaxInfo taxinfo = new Lydf_TaxInfo();
			saleHead.str3 = "";
			saleHead.str4 = "";

			if (SellType.ISBACK(saleHead.djlb))
			{
				String date = ysaleHead.rqsj.substring(0, 10);

				if (date.indexOf("-") > 0)
					date = date.replace('-', '/');

				if (date.indexOf("/") < 0)
				{
					new MessageBox("解析小票发生日期失败");
					return;
				}

				taxinfo.saletype = saleHead.djlb;
				// ysaleHead.yfphm = String.valueOf(ysaleHead.fphm);
				// ysaleHead.fphm = saleHead.fphm;

				if (Lydf_Util.isCurrentMonth(date))
				{
					pb.setText("正在发送税控票据作废数据，请稍等...");
					if (ysaleHead.salefphm != null && ysaleHead.salefphm.length() > 0)
					{
						// && ysaleHead.ysje > 0 && ysaleGoods.size() > 0 &&
						// ysalePay.size() > 0
						taxinfo.oprtype = "1"; // 作废
						if (Lydf_Taxer.getDefault().execute(3, ysaleHead, null, null, taxinfo))
						{
							saleHead.str3 = "作废:" + ysaleHead.salefphm;
							saleHead.salefphm = "";
							((Lydf_AccessDayDB) AccessDayDB.getDefault()).updateTaxInfo(3, saleHead);

							SaleHeadDef tmpshd = (SaleHeadDef) saleHead.clone();
							tmpshd.yfphm = ysaleHead.yfphm;
							tmpshd.salefphm = ysaleHead.salefphm;
							tmpshd.ysje = ysaleHead.num1;
							taxinfo.memo = tmpshd.str3;

							((Lydf_NetService) NetService.getDefault()).sendSaleTax(tmpshd, taxinfo);

							if (ysaleHead.ysje == 0 || ysaleGoods.size() == 0 || ysalePay.size() == 0)
								return;

							pb.setText("正在重新发送税控票据开票数据，请稍等...");

							taxinfo = new Lydf_TaxInfo();
							taxinfo.saletype = saleHead.djlb;
							taxinfo.oprtype = "0"; // 开票

							if (Lydf_Taxer.getDefault().execute(1, ysaleHead, ysaleGoods, ysalePay, taxinfo))
							{
								// 保存打印
								ysaleHead.salefphm = taxinfo.PH;
								ysaleHead.str3 = taxinfo.toString();
								((Lydf_NetService) NetService.getDefault()).sendSaleTax(ysaleHead, taxinfo);
							}
							else
							{
								Lydf_Util.writeTaxFile(ysaleHead, ysaleGoods, ysalePay, taxinfo);
								ysaleHead.str4 = "本发票未被税务机关采集，不能作为报销凭证!";
								taxinfo.memo = ysaleHead.str4;
							}
						}
						else
						{
							Lydf_Util.writeTaxFile(ysaleHead, ysaleGoods, ysalePay, taxinfo);
						}
					}
				}
				else
				{
					pb.setText("正在发送税控票据红冲数据，请稍等...");
					if (ysaleHead.salefphm != null && ysaleHead.salefphm.length() > 0)
					{
						ysaleHead.djlb = SellType.getReFlush(ysaleHead.djlb);
						taxinfo.oprtype = "2"; // 红冲

						if (Lydf_Taxer.getDefault().execute(1, ysaleHead, ysaleGoods, ysalePay, taxinfo))
						{
							ysaleHead.salefphm = taxinfo.PH;
							ysaleHead.str3 = taxinfo.toString();
							((Lydf_NetService) NetService.getDefault()).sendSaleTax(saleHead, taxinfo);
						}
						else
						{
							Lydf_Util.writeTaxFile(ysaleHead, ysaleGoods, ysalePay, taxinfo);
							ysaleHead.str4 = "本发票未被税务机关采集，不能作为报销凭证!";
							taxinfo.memo = ysaleHead.str4;
						}

					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (pb != null)
				pb.close();
		}
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

	public void initNewSale()
	{
		super.initNewSale();
		ysaleHead = null;
		ysaleGoods = null;
		ysalePay = null;
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

		saleHead.ysje = ManipulatePrecision.doubleConvert(saleHead.hjzje - saleHead.hjzke, 2, 1);
	}

	// 查询最后一笔
	public void execCustomKey1(boolean keydownonsale)
	{
		query(2);
	}

	// 查询指定某一笔
	public void execCustomKey2(boolean keydownonsale)
	{
		query(4);
	}

	// 调出未成功的交易
	public void execCustomKey3(boolean keydownonsale)
	{
		String taxFile = Lydf_Util.selectTaxFile();

		if (taxFile == null)
			return;

		Lydf_TaxInfo taxinfo = new Lydf_TaxInfo();

		if (!readTaxFile(taxFile, taxinfo))
		{
			new MessageBox("读取税控文件失败");
			return;
		}

		if (reSend(ysaleHead, ysaleGoods, ysalePay, taxinfo))
			Lydf_Util.deleteTaxFile(taxFile);
	}

	private void query(int cmdcode)
	{
		ProgressBox pb = new ProgressBox();
		try
		{
			Lydf_TaxInfo taxinfo = new Lydf_TaxInfo();
			pb.setText("正在发送税控查询数据，请稍等...");
			if (Lydf_Taxer.getDefault().execute(cmdcode, saleHead, saleGoods, salePayment, taxinfo))
			{
				StringBuffer sb = new StringBuffer();

				sb.append("开票日期:" + Convert.appendStringSize("", taxinfo.RQ, 1, 32, 32, 0) + "\n");
				sb.append("合计金额:" + Convert.appendStringSize("", taxinfo.JE, 1, 32, 32, 0) + "\n");
				sb.append("合计税额:" + Convert.appendStringSize("", taxinfo.SE, 1, 32, 32, 0) + "\n");
				sb.append("电子票号:" + Convert.appendStringSize("", taxinfo.PH, 1, 32, 32, 0) + "\n");
				sb.append("税 控 码:" + Convert.appendStringSize("", taxinfo.SK, 1, 32, 32, 0) + "\n");
				sb.append("小 票 号:" + Convert.appendStringSize("", taxinfo.LSH, 1, 32, 32, 0) + "\n");

				new MessageBox(sb.toString());
			}
		}
		finally
		{
			if (pb != null)
				pb.close();
		}

	}

	public boolean readTaxFile(String file, Lydf_TaxInfo taxinfo)
	{
		FileInputStream f = null;

		ysaleHead = new SaleHeadDef();
		ysaleGoods = new Vector();
		ysalePay = new Vector();

		try
		{
			f = new FileInputStream(file);
			ObjectInputStream s = new ObjectInputStream(f);
			ysaleHead = (SaleHeadDef) s.readObject();
			Vector sgd = (Vector) s.readObject();
			for (int i = 0; i < sgd.size(); i++)
				ysaleGoods.add(sgd.get(i));

			Vector sp = (Vector) s.readObject();
			for (int j = 0; j < sp.size(); j++)
				ysalePay.add(sp.get(j));

			Lydf_TaxInfo temptaxinfo = (Lydf_TaxInfo) s.readObject();
			taxinfo.saletype = temptaxinfo.saletype;
			taxinfo.oprtype = temptaxinfo.oprtype;

			s.close();
			s = null;
			f.close();
			f = null;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
}
