package com.efuture.javaPos.Logic;

import java.sql.ResultSet;
import java.util.Vector;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.DownBaseTask;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentBankCMCC;
import com.efuture.javaPos.Payment.PaymentChange;
import com.efuture.javaPos.Payment.PaymentCustLczc;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.BuyerInfoDef;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.KeyValueDef;
import com.efuture.javaPos.Struct.MemoInfoDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.ApportPaymentForm;
import com.efuture.javaPos.UI.Design.BuyInfoForm;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.SaleMemoForm;
import com.efuture.javaPos.UI.Design.SalePayForm;
import com.efuture.javaPos.UI.Design.SaleShowAccountForm;

// 付款相关业务类
public class SaleBS5Pay extends SaleBS4Refund
{
	boolean isExit = false;

	public SaleBS5Pay()
	{
		super();
	}

	public boolean comfirmPay()
	{
		// 不允许0金额交易成交
		if (GlobalInfo.sysPara.issaleby0 != 'Y' && calcHeadYfje() <= 0)
		{
			new MessageBox(Language.apply("交易金额不足,不能付款!"));
			return false;
		}

		// 商品数大于0
		if (saleGoods.size() <= 0)
		{
			new MessageBox(Language.apply("商品数小于1,不能付款"));
			return false;
		}

		// 商品数量小于1
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef goodsDef = (SaleGoodsDef) saleGoods.get(i);

			if (goodsDef.sl <= 0)
			{
				new MessageBox(Language.apply("第{0}行商品 [{1}] 数量不合法，不能付款\n请修改此行商品数量或者删除此商品后重新录入", new Object[] { " " + (i + 1) + " ", goodsDef.code }));
				return false;
			}
		}

		if (GlobalInfo.payMode == null || GlobalInfo.payMode.size() <= 0)
		{
			new MessageBox(Language.apply("当前收银机没有定义付款方式,请通知信息部!"));
			return false;
		}

		return true;
	}

	public boolean paySellStart()
	{
		if (!comfirmPay())
			return false;

		return true;
	}

	public void existCreditFee()
	{
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			if (saleGoodsDef.qtzke != 0)
			{
				saleGoodsDef.qtzke = 0;
				saleGoodsDef.str5 = "";
				getZZK(saleGoodsDef);
				// updateDisplay = true;
			}
		}
	}

	public void paySellCancel()
	{
		// 检查是否存在银联追送折扣额
		if (SellType.ISSALE(saletype))
		{
			// boolean updateDisplay = false;
			boolean updateDisplay = true;
			existCreditFee();

			if (updateDisplay)
			{
				calcHeadYsje();

				// 刷新商品列表
				saleEvent.updateTable(getSaleGoodsDisplay());
				saleEvent.setTotalInfo();
			}
		}

		// 放弃已计算的零头折扣
		calcSellPayMoney(false);
	}

	public void addMemoPayment()
	{
		// 在预售提货状态时检查是或否存在预售定金
		if (SellType.ISPREPARETAKE(saleHead.djlb) || saleHead.djlb.equals(SellType.PREPARE_BACK))
		{
			PayModeDef pmd = CreatePayment.getDefault().getPaymentPreDebtMode();

			if (pmd != null)
			{
				boolean append = true;
				for (int j = 0; j < salePayment.size(); j++)
				{
					SalePayDef spd = (SalePayDef) salePayment.elementAt(j);
					if (spd.paycode.equals(pmd.code))
					{
						append = false;
						break;
					}
				}

				if (append)
				{
					for (int i = 0; i < memoPayment.size(); i++)
					{
						Payment pay = (Payment) memoPayment.elementAt(i);

						if (pay.salepay != null)
							addSalePayObject(pay.salepay, pay);
					}
				}
			}
		}

	}

	// 按付款键之后自动付款
	public boolean autoPay()
	{
		if (GlobalInfo.sysPara.isAutoPayByLczc != 'Y')
			return false;
		if (!SellType.ISSALE(saletype))
			return false;
		if (curCustomer == null)
			return false;
		if (curCustomer.value1 <= 0)
			return false;

		PayModeDef payMode = DataService.getDefault().searchPayMode("0111");

		// 创建一个付款方式对象
		Payment pay = CreatePayment.getDefault().createPaymentByPayMode(payMode, saleEvent.saleBS);
		if (pay == null)
			return false;

		SalePayDef sp = null;
		((PaymentCustLczc) pay).autoCreateAccount();

		double yf = calcPayBalance();
		String lczcPay = String.valueOf(Math.min(yf, curCustomer.value1));

		if (!pay.createSalePay(lczcPay))
			return false;
		sp = pay.salepay;

		// 付款记账
		return payAccount(pay, sp);
	}

	public void paySellByZero()
	{
		if (GlobalInfo.sysPara.issaleby0 == 'Y' && calcHeadYfje() <= 0)
		{
			for (int i = 0; i < GlobalInfo.payMode.size(); i++)
			{
				PayModeDef pmd = (PayModeDef) GlobalInfo.payMode.get(i);

				if (pmd.ismj == 'Y' && pmd.type == '1' && pmd.iszl == 'Y')
				{
					// 创建一个付款方式对象
					Payment pay = CreatePayment.getDefault().createPaymentByPayMode(pmd, saleEvent.saleBS);

					if (pay == null)
						continue;

					// inputPay这个方法根据不同的付款方式进行重写
					SalePayDef sp = pay.inputPay("1");

					payAccount(pay, sp);

					sp.je = 0;
					sp.ybje = 0;
					calcPayBalance();
					break;
				}
			}
		}
	}

	public void paySell()
	{
		// aa a = new aa();
		// a.open();
		try
		{
	/*		if (GlobalInfo.sysPara.issaleby0 == 'Y' && calcHeadYfje() <= 0)
			{
				for (int i = 0; i < GlobalInfo.payMode.size(); i++)
				{
					PayModeDef pmd = (PayModeDef) GlobalInfo.payMode.get(i);

					if (pmd.ismj == 'Y' && pmd.type == '1' && pmd.iszl == 'Y')
					{
						// 创建一个付款方式对象
						Payment pay = CreatePayment.getDefault().createPaymentByPayMode(pmd, saleEvent.saleBS);

						if (pay == null)
							continue;

						// inputPay这个方法根据不同的付款方式进行重写
						SalePayDef sp = pay.inputPay("1");

						payAccount(pay, sp);

						sp.je = 0;
						sp.ybje = 0;
						calcPayBalance();
						break;
					}
				}
			}*/

			paySellByZero();
			
			if (SellType.ISBACK(saletype))
			{
				BuyInfoForm backSaleForm = new BuyInfoForm();
				backSaleForm.ismustsel = true;
				backSaleForm.isshownodata = false;
				// 选择退货理由，上传到工作日志
				backSaleForm.open(new String[] { "TH" });
				String code = "";
				if (backSaleForm.selCode.size() > 0)
				{
					code = ((String[]) backSaleForm.selCode.get(0))[1];
				}
				saleHead.buyerinfo = code;
			}

			// 需要在付款时释放打印机时
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null && Printer.getDefault().getStatus())
			{
				Printer.getDefault().close();
			}

			// 暂停实时打印,避免计算促销产生的折扣分摊到商品后,商品重新正负打印
			if (GlobalInfo.sysPara.isRealPrintPOP == 'N')
				stopRealTimePrint(true);

			// 检查付款开始
			if (!paySellStart())
				return;

			// 指定小票退货进行扣回处理,扣回在付款前进行的模式
			if (GlobalInfo.sysPara.refundByPos == 'Y' && SellType.ISBACK(saletype) && !doRefundEvent())
				return;

			// 检查在付款前是否存在特殊的功能
			custMethod();

			// 辅助付款信息
			addMemoPayment();

			// 开始自动付款
			autoPay();

			// 允许下一次快捷键付款
			quickpaystart = false;

			// 通过快捷付款键进入付款窗口(在SalePayEvent中处理，避免刷新双屏广告付款信息窗口获取焦点键值)
			// if (quickpaykey != 0) NewKeyListener.sendKey(quickpaykey);

			// 打开付款窗口
			// new
			// SalePayForm_T1(saleEvent.saleform.getCompositePay(),saleEvent.saleform.getCompositeInput(),
			// SWT.NONE).open(saleEvent.saleBS);
			new SalePayForm().open(saleEvent.saleBS);

			// 取消快捷付款键
			quickpaykey = 0;

			// 付款完成，开始新交易
			if (this.saleFinish)
			{
				sellFinishComplete();
			}
			else
			{
				// 放弃付款
				paySellCancel();
			}
		}
		finally
		{
			// 恢复实时打印
			stopRealTimePrint(false);

			// 付款结束后，重新连接打印机
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null && !Printer.getDefault().getStatus())
			{
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
			}
		}
	}

	public void custMethod()
	{

	}

	public void sellFinishComplete()
	{
		// 开始新交易
		saleEvent.initOneSale(this.saletype);

		// 在进行下笔交易前,执行一次定时器任务，检查网上通知及任务
		TaskExecute.getDefault().executeTimeTask(false);

		// 在进行下笔交易前,检查是否有一次定时下载因为销售状态被放弃,如果有则下载一次
		DownBaseTask.onceRun();
	}

	public boolean exitPaySell()
	{
		if (isExit)
			return false;
		try
		{
			// 提醒确认
			if (salePayment.size() > 0 && new MessageBox(Language.apply("你确定要放弃所有已输入的付款吗？"), null, true).verify() != GlobalVar.Key1) { return false; }

			isExit = true;

			return deleteAllSalePay();
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return false;
		}
		finally
		{
			isExit = false;
		}
	}

	public String getPayAccountInfo()
	{
		if (SellType.ISSALE(saletype))
		{
			return Language.apply("应付金额:");
		}
		else
		{
			return Language.apply("应退金额:");
		}
	}

	public String getPayBalanceLabel()
	{
		return ManipulatePrecision.doubleToString(calcPayBalance());
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
			if (paydef.flag == '1')
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

	// 设置付款金额输入框的缺省值
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

	public boolean getPayModeByNeed(PayModeDef paymode)
	{
		return true;
	}

	// 得到上级为code的所有子付款方式
	public Vector getPayModeBySuper(String sjcode)
	{
		return getPayModeBySuper(sjcode, null, null);
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

	public boolean getCouponFirst(String code)
	{
		return true;
	}

	public int payButtonToPayModePosition(int key)
	{
		int k = -1;
		PayModeDef paymode = null;

		// 先读取客户自定义快速付款键
		if (ConfigClass.QuickPay != null && ConfigClass.QuickPay.containsKey(String.valueOf(key)))
		{
			String custPayCode = (String) ConfigClass.QuickPay.get(String.valueOf(key));

			for (k = 0; k < GlobalInfo.payMode.size(); k++)
			{
				paymode = (PayModeDef) (GlobalInfo.payMode.elementAt(k));
				if (paymode.code.equals(custPayCode)) { return k; }
			}
		}

		for (k = 0; k < GlobalInfo.payMode.size(); k++)
		{
			paymode = (PayModeDef) (GlobalInfo.payMode.elementAt(k));

			if (key == GlobalVar.PayCash && paymode.type == '1')
				break;
			if (key == GlobalVar.PayCheque && paymode.type == '2')
				break;
			if (key == GlobalVar.PayCredit && paymode.type == '3')
				break;
			if (key == GlobalVar.PayMzk && paymode.type == '4')
				break;
			if (key == GlobalVar.PayGift && paymode.type == '5' && getCouponFirst(paymode.code))
				break;
			if (key == GlobalVar.PayTally && paymode.type == '6')
				break;
			if (key == GlobalVar.PayBank && paymode.isbank == 'Y')
				break;
		}

		//
		if (k >= GlobalInfo.payMode.size())
		{
			return -1;
		}
		else
		{
			return k;
		}
	}

	public int existPayment(String code, String account)
	{
		return existPayment(code, account, false);
	}

	public int existPayment(String code, String account, boolean overmode)
	{
		// 扣回处理
		if (isRefundStatus())
			return existRefund(code, account, overmode);

		//
		SalePayDef saledef = null;
		for (int i = 0; i < salePayment.size(); i++)
		{
			saledef = (SalePayDef) salePayment.elementAt(i);

			// 查找模式时,找到付款代码和账号相同的
			// 覆盖模式时,必须付款代码和账号相同且是直接输入付款金额的付款方式才允许覆盖
			if (saledef.paycode.equals(code) && saledef.payno.trim().equals(account) && ((!overmode) || (overmode && CreatePayment.getDefault().allowQuickInputMoney(DataService.getDefault().searchPayMode(saledef.paycode)))))
			{
				if (saledef.batch == null || saledef.batch.trim().length() <= 0)
				{
					// 未记账,直接返回
					return i;
				}
				else
				{
					return -1;
				}
			}
		}

		return -1;
	}

	public boolean goToNextPaymode(PayModeDef paymode)
	{
		if (CreatePayment.getDefault().allowQuickInputMoney(paymode) && GlobalInfo.sysPara.payover == 'Y') { return true; }

		return false;
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
			detail[1] = saledef.payno;
			detail[2] = ManipulatePrecision.doubleToString(saledef.ybje);
			v.add(detail);
		}

		// 在要刷新付款列表时,写入断点数据
		writeBrokenData();

		return v;
	}

	public boolean deleteSalePay(int index)
	{
		return deleteSalePay(index, false);
	}

	// 自动删除不受系统参数控制
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

	public boolean deleteAllSalePay()
	{
		// 删除所有付款方式
		for (int i = 0; i < salePayment.size(); i++)
		{
			if (!deleteSalePay(i))
			{
				return false;
			}
			else
			{
				i--;
			}
		}

		// 删除所有扣回的付款,用信用卡支付扣回时,取消所有付款也得取消扣回
		if (!deleteAllSaleRefund())
			return false;

		return true;
	}

	public boolean isDeletePay(int index)
	{
		if (SellType.ISBACK(saleHead.djlb))
			return true;

		if (GlobalInfo.sysPara.nodeletepaycode == null || GlobalInfo.sysPara.nodeletepaycode.equals("") || GlobalInfo.sysPara.nodeletepaycode.equals("0000"))
			return true;

		if (index >= 0)
		{
			Payment p = (Payment) payAssistant.elementAt(index);

			String paycodes[] = GlobalInfo.sysPara.nodeletepaycode.split(",");

			for (int i = 0; i < paycodes.length; i++)
			{
				if (paycodes[i].equals(p.salepay.paycode))
				{
					new MessageBox(Language.apply("当前 [{0}] 付款不能进行删除!", new Object[] { p.salepay.payname }));
					return false;
				}
			}
		}

		return true;
	}

	public boolean backToInit(PayModeDef pay)
	{
		if (pay.sjcode.equals("0"))
			return false;

		if (GlobalInfo.sysPara.loopInputPay != null && !GlobalInfo.sysPara.loopInputPay.equals(""))
		{
			String[] s = GlobalInfo.sysPara.loopInputPay.split(",");
			for (int i = 0; i < s.length; i++)
			{
				if (pay.code.equals(s[i].trim())) { return false; }
			}
		}

		if (GlobalInfo.sysPara.payex != null && GlobalInfo.sysPara.payex.split(",").length >= 1)
		{
			String[] paycode = GlobalInfo.sysPara.payex.split(",");
			for (int i = 0; i < paycode.length; i++)
			{
				if (pay.code.equals(paycode[i])) { return false; }
			}
		}

		return true;
	}

	public boolean checkPaymodeValid(PayModeDef mode, String money)
	{
		if (mode.code.equals("0601"))
		{
			if (SellType.ISPREPARE(saletype))
			{
				return true;
			}
			else
			{
				new MessageBox(Language.apply("预售销售状态下才能使用【预售欠款】"));
				return false;
			}
		}

		if (mode.code.equals("0602"))
		{
			new MessageBox(Language.apply("当前状态下不能使用【预售定金】"));
			return false;
		}

		// 付款方式配对使用。例如：0402只能和0401、0403一起使用。当存在0402时，其他付款方式无法使用。
		// 配置方法 0402:0401,0403
		String line = GlobalInfo.sysPara.paymentFilter;
		if (line != null)
		{
			String[] s = line.split("\\|");
			for (int j = 0; j < salePayment.size(); j++)
			{
				SalePayDef spd = (SalePayDef) salePayment.elementAt(j);
				for (int i = 0; i < s.length; i++)
				{
					if (s[i] == null || s[i].length() <= 0)
						continue;
					// :0403:0401,0401 中寻找 :0403:
					if ((":" + s[i]).indexOf(":" + spd.paycode + ":") >= 0)
					{
						// 0403:0401,0401, 中寻找 0401,如果没有找到，代表此付款方式无法与0403共同使用
						if ((s[i] + ",").indexOf(mode.code + ",") < 0) { return false; }
					}
				}
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

		// 创建一个付款方式对象
		Payment pay = CreatePayment.getDefault().createPaymentByPayMode(mode, saleEvent.saleBS);
		if (pay == null)
			return false;

		// inputPay这个方法根据不同的付款方式进行重写
		SalePayDef sp = pay.inputPay(money);

		return payAccount(pay, sp);
	}

	public boolean payAccount(Payment pay, SalePayDef sp)
	{
		// 增加到付款集合
		if (sp != null || pay.alreadyAddSalePay)
		{
			if (sp != null)
			{
				// 付款覆盖模式,删除已有的付款
				if (GlobalInfo.sysPara.payover == 'Y')
				{
					int i = existPayment(sp.paycode, sp.payno, true);
					if (i >= 0)
					{
						// 不管已有的付款是否取消成功,都要把当前付款增加到已付款中
						deleteSalePay(i);
					}
				}

				// 增加已付款
				addSalePayObject(sp, pay);
			}

			// 计算剩余付款
			calcPayBalance();

			// 如果是需要循环输入的付款方式,则自动发送ENTER键再次进入付款
			loopInputPay(pay);

			return true;
		}

		return false;
	}

	public PaymentChange calcSaleChange()
	{
		// 使用找零对象
		PaymentChange pc = CreatePayment.getDefault().getPaymentChange(saleEvent.saleBS);
		if (!pc.calcChange())
			return null;

		// 记录找零及付款损溢
		saleHead.zl = pc.getPayChange();
		saleHead.fk_sysy = ManipulatePrecision.doubleConvert(saleHead.sjfk - saleyfje - saleHead.zl, 2, 1);

		return pc;
	}

	public boolean payComplete()
	{
		// 检查付款是否足够
		if (!comfirmPay() || calcPayBalance() > 0 || (saleHead.sjfk <= 0 && GlobalInfo.sysPara.issaleby0 != 'Y'))
		{
			new MessageBox(Language.apply("付款金额不足!"));
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

	public boolean payCompleteDoneEvent()
	{
		// 扣回处理
		if (SellType.ISBACK(saletype))
		{
			if (GlobalInfo.sysPara.refundByPos == 'Y') // 扣回在付款前进行的模式
			{
				// 添加扣回明细到付款明细
				addRefundToSalePay();
			}
			else if (GlobalInfo.sysPara.refundByPos == 'B') // 扣回在付款后进行的模式
			{
				// 执行扣回处理
				if (!doRefundEvent())
					return false;
			}
		}

		return true;
	}

	public void payCompleteCancelEvent()
	{
		// 扣回处理
		if (SellType.ISBACK(saletype))
		{
			if (GlobalInfo.sysPara.refundByPos == 'Y') // 扣回在付款前进行的模式
			{
				// 从付款明细中删除扣回
				delRefundFormSalePay();
			}
			else if (GlobalInfo.sysPara.refundByPos == 'B') // 扣回在付款后进行的模式
			{
				// 从付款明细中删除扣回,同时取消扣回
				cancelRefundEvent();
			}
		}
	}

	public String getChangeTitleLabel()
	{
		String s = Language.apply("找零/溢余(") + ManipulatePrecision.doubleToString(saleHead.fk_sysy) + ")";

		// 付款扣回提示
		if (SellType.ISBACK(saletype) && GlobalInfo.sysPara.refundByPos != 'N')
		{
			String refundMsg;
			refundMsg = getRefundLabelByChange();
			if (refundMsg.length() > 0)
				s += refundMsg;
		}

		return s;
	}

	public void setZL(Label lblZL, Label lblStatus, String memo)
	{

	}

	public String getSaleSfje()
	{
		try
		{
			String result = ManipulatePrecision.doubleToString(saleHead.sjfk);
			// A模式，所有实际收款减去理当扣回
			if ('A' == GlobalInfo.sysPara.ClawBackCalcModel)
			{
				result = ManipulatePrecision.doubleToString(Calc_A_Deduct());
			}
			// B模式，实际收款和理论扣回相同付款方式的相互抵消，并保留实际付款中未被抵消的
			if ('B' == GlobalInfo.sysPara.ClawBackCalcModel || 'C' == GlobalInfo.sysPara.ClawBackCalcModel || 'D' == GlobalInfo.sysPara.ClawBackCalcModel)
			{
				result = ManipulatePrecision.doubleToString(Calc_B_Deduct(GlobalInfo.sysPara.ClawBackCalcModel));
			}

			return result;
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return ManipulatePrecision.doubleToString(saleHead.sjfk);
		}
	}

	public double Calc_A_Deduct()
	{
		double result = 0.0;

		// 计算已输入的扣回
		double khje = 0;

		if (refundPayment != null)
		{
			for (int i = 0; i < refundPayment.size(); i++)
			{
				SalePayDef refundpaydef = (SalePayDef) refundPayment.elementAt(i);
				khje += Math.abs(refundpaydef.je);
			}
		}

		result = saleHead.sjfk - khje;

		return result;
	}

	public double Calc_B_Deduct(char type)
	{
		Object[] object = salePayment.toArray();
		int len = salePayment.size();
		SalePayDef sp = null;
		double result = 0.0;

		for (int i = 0; i < len; i++)
		{
			sp = (SalePayDef) object[i];
			// 相同付款方式下保留付款减去扣回的金额返回
			if (sp.flag == '1')
			{
				SalePayDef sp_Temp = (SalePayDef) sp.clone();
				SalePayDef sp_TempKH = null;
				for (int j = 0; j < len; j++)
				{
					SalePayDef sp_KH = (SalePayDef) object[j];

					// 1、是电子卷；2、属于扣回；3、相同付款方式下
					if (sp_KH.flag == '3')
					{
						if (sp_KH.paycode.equals(sp_Temp.paycode))
						{
							// 券种标识为空的付款方式
							if ("".equals(sp_Temp.idno.trim()) && sp_Temp.idno.trim().equals(sp_KH.idno.trim()))
							{
								sp_Temp.je = sp_Temp.je + sp_KH.je;
								object[i] = sp_Temp;
								// 和付款抵消的扣回标志制为空
								sp_TempKH = (SalePayDef) sp_KH.clone();
								sp_TempKH.flag = ' ';
								object[j] = sp_TempKH;
							}
							// 券种标识不为空，并且内容相同
							if (!"".equals(sp_Temp.idno.trim()) && !"".equals(sp_KH.idno.trim()))
							{
								if (sp_Temp.idno.charAt(0) == sp_KH.idno.charAt(0))
								{
									sp_Temp.je = sp_Temp.je + sp_KH.je;

									object[i] = sp_Temp;
									// 和付款抵消的扣回标志制为空
									sp_TempKH = (SalePayDef) sp_KH.clone();
									sp_TempKH.flag = ' ';
									object[j] = sp_TempKH;
								}
							}
						}
					}
				}
			}
		}

		// 向Vector中添加未被抵消的扣回
		for (int i = 0; i < len; i++)
		{
			sp = (SalePayDef) object[i];
			if (sp.flag == '1')
			{
				if (type == 'D')
				{
					if (DataService.getDefault().searchPayMode(sp.paycode).type == '1')
					{
						result += sp.je;
					}
				}
				else
				{
					if (type == 'C' && sp.je < 0)
						continue;
					result += sp.je;

				}
			}
		}

		return result;
	}

	/**
	 * 退货扣回付款显示明细列表 处理方法
	 * 
	 * @param salePayment
	 * @return
	 */
	private Vector processChangeList(Vector salePayment)
	{
		Object[] object = salePayment.toArray();
		int len = salePayment.size();
		Vector v = new Vector();
		SalePayDef sp = null;

		// 标志为找零的直接填充返回，相同付款方式下保留付款减去扣回的金额返回
		for (int i = 0; i < len; i++)
		{
			sp = (SalePayDef) object[i];
			if (sp.flag == '2')// 标志为找零的直接填充返回
			{
				v.add(sp);
			}
			else
			{
				// 相同付款方式下保留付款减去扣回的金额返回
				if (sp.flag == '1')
				{
					SalePayDef sp_Temp = (SalePayDef) sp.clone();
					SalePayDef sp_TempKH = null;
					for (int j = 0; j < len; j++)
					{
						SalePayDef sp_KH = (SalePayDef) object[j];

						// 1、是电子卷；2、属于扣回；3、相同付款方式下
						if (sp_KH.flag == '3')// (CreatePayment.getDefault().isPaymentFjk(sp_KH.paycode)
												// && sp_KH.flag == '3')
						{
							if (sp_KH.paycode.equals(sp_Temp.paycode))
							{
								if ("".equals(sp_Temp.idno.trim()) && sp_Temp.idno.trim().equals(sp_KH.idno.trim()))
								{
									sp_Temp.ybje = sp_Temp.ybje + sp_KH.ybje;

									// 和付款抵消的扣回标志制为空
									sp_TempKH = (SalePayDef) sp_KH.clone();
									sp_TempKH.flag = ' ';
									object[j] = sp_TempKH;
								}

								if (!"".equals(sp_Temp.idno.trim()) && !"".equals(sp_KH.idno.trim()))
								{
									if (sp_Temp.idno.charAt(0) == sp_KH.idno.charAt(0))
									{
										sp_Temp.ybje = sp_Temp.ybje + sp_KH.ybje;

										// 和付款抵消的扣回标志制为空
										sp_TempKH = (SalePayDef) sp_KH.clone();
										sp_TempKH.flag = ' ';
										object[j] = sp_TempKH;
									}
								}
							}
						}
					}
					v.add(sp_Temp);
				}
			}
		}

		// 向Vector中添加未被抵消的扣回
		for (int i = 0; i < len; i++)
		{
			sp = (SalePayDef) object[i];
			if (sp.flag == '3')
			{
				v.add(sp);
			}
		}

		return returnDetailSort(v);
	}

	/**
	 * 对退货扣回付款显示明细进行排序操作
	 * 
	 * @param v
	 * @return
	 */
	private Vector returnDetailSort(Vector v)
	{
		// 进行排序
		Object[] object1 = v.toArray();
		SalePayDef sp = null;
		Object temp = null;// 冒泡运算时交换中间数
		int len = v.size();

		// 按付款方式代码进行冒泡排序(把人民币的排序提前)
		for (int i = 0; i < len; i++)
		{
			for (int j = len - 1; j > i; j--)
			{
				SalePayDef before = null;
				before = (SalePayDef) object1[j];
				if ("01".equals(before.paycode.trim()))
				{
					temp = object1[j];
					object1[j] = object1[j - 1];
					object1[j - 1] = temp;
				}
			}
		}

		// 把找零的数据项提到最前
		for (int i = 0; i < len; i++)
		{
			for (int j = len - 1; j > i; j--)
			{
				SalePayDef before = (SalePayDef) object1[j];
				if (before.flag == '2')
				{
					temp = object1[j];
					object1[j] = object1[j - 1];
					object1[j - 1] = temp;
				}
			}
		}

		v.clear();
		for (int i = 0; i < len; i++)
		{
			sp = (SalePayDef) object1[i];
			String[] baseinfo = { "[" + sp.paycode + "]" + sp.payname, ManipulatePrecision.doubleToString(sp.hl, 4, 1), ManipulatePrecision.doubleToString(sp.ybje) };
			v.add(baseinfo);
		}

		return v;
	}

	public Vector getSaleChangeList()
	{
		// 非多币种找零模式 或者 无找零 时不显示找零列表
		// 如果有扣回，用找零列表显示扣回付款列表
		if ((GlobalInfo.sysPara.paychgmore != 'Y' || saleHead.zl <= 0) && !haveRefundPayment() && GlobalInfo.sysPara.paychgmore != 'A')
		{
			return null;
		}
		else
		{
			return processChangeList(salePayment);
		}
	}

	public void setSaleFinishHint(Label status, String msg)
	{
		status.setText(msg);
		status.getDisplay().update();
	}

	// 付款完成输入小票附加信息
	public boolean inputSaleAppendInfo()
	{
		if (GlobalInfo.sysPara.isInputSaleAppend == 'N')
			return true;

		boolean isType = false;
		// 定义了指定的交易类型时，进行比较
		if (GlobalInfo.sysPara.saleAppendSaleType.length() > 0)
		{
			String[] types = GlobalInfo.sysPara.saleAppendSaleType.split(",");
			for (int i = 0; i < types.length; i++)
			{
				if (this.saletype.equals(types[i]))
				{
					isType = true;
					break;
				}
			}
		}
		// 没有指定交易类型
		else
		{
			isType = true;
		}

		if (!isType)
			return true;

		if (GlobalInfo.sysPara.isInputSaleAppend != 'M')
		{
			MessageBox me = new MessageBox(Language.apply("是否需要输入单据附加信息?\n\n任意键-是 / 退出键-否"), null, false);

			if (me.verify() == GlobalVar.Exit)
				return true;
		}

		SaleMemoForm smf = new SaleMemoForm(this.saleHead, this.saleGoods, this.salePayment, getDeliveryInfo(curCustomer), 0);

		return smf.sme.salememobs.isDataSaved.equals("Y");
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
				new MessageBox(Language.apply("小票附加信息输入失败,不能完成交易!"));
				return false;
			}

			//
			setSaleFinishHint(status, Language.apply("正在汇总交易数据,请等待....."));
			if (!saleSummary())
			{
				new MessageBox(Language.apply("交易数据汇总失败!"));

				return false;
			}

			//
			setSaleFinishHint(status, Language.apply("正在校验数据平衡,请等待....."));
			if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods, salePayment))
			{
				new MessageBox(Language.apply("交易数据校验错误!"));

				return false;
			}

			// 最终效验
			if (!checkFinalStatus()) { return false; }

			// 不是练习交易数据写盘
			if (!SellType.ISEXERCISE(saletype))
			{
				// 输入顾客信息
				setSaleFinishHint(status, Language.apply("正在输入客户信息,请等待......"));
				selectAllCustomerInfo();

				//
				setSaleFinishHint(status, Language.apply("正在打开钱箱,请等待....."));
				CashBox.getDefault().openCashBox();

				//
				setSaleFinishHint(status, Language.apply("正在记账付款数据,请等待....."));
				if (!saleCollectAccountPay())
				{
					new MessageBox(Language.apply("付款数据记账失败\n\n稍后将自动发起已记账付款的冲正!"));

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, Language.apply("正在发送冲正数据,请等待....."));
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				setSaleFinishHint(status, Language.apply("正在写入交易数据,请等待......"));
				if (!AccessDayDB.getDefault().writeSale(saleHead, saleGoods, salePayment))
				{
					new MessageBox(Language.apply("交易数据写盘失败!"));
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",发生数据写盘失败", StatusType.WORK_SENDERROR);

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, Language.apply("正在发送冲正数据,请等待....."));
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				// 小票已写盘,本次交易就要认为完成,即使后续处理异常也要返回成功
				saleFinish = true;

				// 小票保存成功以后，及时清除断点
				setSaleFinishHint(status, Language.apply("正在清除断点保护数据,请等待......"));
				clearBrokenData();

				//
				setSaleFinishHint(status, Language.apply("正在清除付款冲正数据,请等待......"));
				if (!saleCollectAccountClear())
				{
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票清除冲正数据失败,但小票已成交保存", StatusType.WORK_SENDERROR);

					new MessageBox(Language.apply("小票已成交保存,但清除冲正数据失败\n\n请完成本笔交易后重启款机尝试删除记账冲正数据!"));
				}

				// 处理交易完成后一些后续动作
				doSaleFinshed(saleHead, saleGoods, salePayment);

				// 上传当前小票
				setSaleFinishHint(status, Language.apply("正在上传交易小票数据,请等待......"));
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
				setSaleFinishHint(status, Language.apply("正在上传收银机交易汇总,请等待......"));
				DataService.getDefault().sendSyjStatus();

				doEvaluation(this.saleHead, this.saleGoods, this.salePayment);
				
				// 打印小票
				setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
				printSaleBill();
			}
			else
			{
				if (GlobalInfo.sysPara.lxprint == 'Y')
				{
					// 打印小票
					setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
					printSaleBill();
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
			setSaleFinishHint(status, Language.apply("正在等待关闭钱箱,请等待......"));
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
			setSaleFinishHint(status, Language.apply("本笔交易结束,开始新交易"));

			// 标记本次交易已完成
			saleFinish = true;

			return saleFinish;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			new MessageBox(Language.apply("完成交易时发生异常:\n\n") + ex.getMessage());

			return saleFinish;
		}
	}
	
	protected boolean doEvaluation(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	  {
	    if (!GlobalInfo.isOnline) return false;

	    if ((ConfigClass.IsOpenDisplay.equals("Y")) && (ConfigClass.DisplayText.trim().length() > 0) && (ConfigClass.DisplayChooses.trim().length() > 0))
	    {
	      ProgressBox pb = null;
	      try {
	        String Chooses = "";
	        String[] row = ConfigClass.DisplayChooses.trim().split("\\;");
	        for (int i = 0; i < row.length; i++)
	        {
	          Chooses = Chooses + "/" + row[i].split(",")[0] + "-" + row[i].split(",")[2];
	        }
	        pb = new ProgressBox();
	        pb.setText(Language.apply("正在进行评价，请稍等..."));

	        int s = new MessageBox(Language.apply(ConfigClass.DisplayText) + "\n" + Chooses.substring(1), null, false, ConfigClass.X_POSITION, ConfigClass.Y_POSITION, -1, ConfigClass.IsSecMonitorDisplay, ConfigClass.Timeout, ConfigClass.IsBotton, ConfigClass.lineheight_new, ConfigClass.textWidth, ConfigClass.textSize).verify();

	        Vector v = new Vector();
	        if (s < 0) return false;
	        for (int j = 0; j < row.length; j++) {
	          String[] r = row[j].split(",");
	          if (Integer.parseInt(r[0]) == s - 1) {
	            v.add(r);
	          }
	        }

	        pb.setText(Language.apply("正在上传评价，请稍等..."));
	        NetService.getDefault().sendEvaluation(saleHead, saleGoods, salePayment, v);

	        if (pb != null)
	        {
	          pb.close();
	          pb = null;
	        }
	      }
	      catch (Exception ex)
	      {
	        ex.printStackTrace();
	        new MessageBox(Language.apply("进行评价时发生异常:\n\n") + ex.getMessage());
	      }
	      finally
	      {
	        if (pb != null)
	        {
	          pb.close();
	          pb = null;
	        }
	      }

	    }

	    return false;
	  }

	public void doSaleFinshed(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{

	}

	public void printSaleBill()
	{
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
				realTimePrintGoods(null, i);

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

	public boolean saleSummary()
	{
		int i;
		SaleGoodsDef saleGoodsDef = null;
		SalePayDef salePayDef = null;
		int lastgoods = 0;
		double sswr_sysy = 0;
		double fk_sysy = 0;

		if (saleGoods == null || saleGoods.size() <= 0)
			return false;

		// 汇总商品明细
		for (i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

			// 去掉营业员小计
			if (saleGoodsDef.flag == '0')
			{
				delSaleGoodsObject(i);
				i--;
				continue;
			}

			// 非削价商品且非批发或未定价商品:lsj = jg
			// 削价商品:lsj <> jg, lsj-jg=削价损失 -> thss
			// 批发销售:lsj <> jg, lsj-jg=批发损失 -> thss
			// 议价商品:lsj <> jg, lsj-jg=批发损失 -> thss
			if (saleGoodsDef.lsj <= 0 || (saleGoodsDef.flag != '3' && saleGoodsDef.flag != '6' && !SellType.ISBATCH(saletype)))
			{
				saleGoodsDef.lsj = saleGoodsDef.jg;
			}

			// 整理数据
			saleGoodsDef.rowno = i + 1;
			saleGoodsDef.fphm = saleHead.fphm;

			if (saleGoodsDef.sqkh == null || saleGoodsDef.sqkh.trim().length() <= 0)
			{
				saleGoodsDef.sqkh = cursqkh;
				saleGoodsDef.sqktype = cursqktype;
				saleGoodsDef.sqkzkfd = cursqkzkfd;
			}
			saleGoodsDef.hjzk = getZZK(saleGoodsDef);

			if (saleGoodsDef.hjje != 0)
			{
				// 分摊损溢金额
				if (saleGoodsDef.flag != '1' && saleGoodsDef.type != '8')
				{
					saleGoodsDef.sswr_sysy = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.hjzk) / saleHead.ysje * saleHead.sswr_sysy);
					saleGoodsDef.fk_sysy = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.hjzk) / saleHead.ysje * saleHead.fk_sysy);

					sswr_sysy += saleGoodsDef.sswr_sysy;
					fk_sysy += saleGoodsDef.fk_sysy;
					lastgoods = i;
				}
			}
		}

		// 损溢差额记入最后一个商品
		if (sswr_sysy != saleHead.sswr_sysy)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastgoods);
			saleGoodsDef.sswr_sysy += ManipulatePrecision.sub(saleHead.sswr_sysy, sswr_sysy);
		}
		if (fk_sysy != saleHead.fk_sysy)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastgoods);
			saleGoodsDef.fk_sysy += ManipulatePrecision.sub(saleHead.fk_sysy, fk_sysy);
		}

		// 汇总付款明细
		for (i = 0; i < salePayment.size(); i++)
		{
			salePayDef = (SalePayDef) salePayment.elementAt(i);

			// 整理数据
			salePayDef.rowno = i + 1;
			salePayDef.fphm = saleHead.fphm;

			// 检查卡号中是否存在非法字符
			if (salePayDef.payno != null && salePayDef.payno.trim().length() > 0)
			{
				salePayDef.payno = ManipulateStr.delSpecialChar(salePayDef.payno);
			}
		}

		// 整理数据
		saleHead.rqsj = ManipulateStr.interceptExceedStr(saleHead.rqsj, 20);
		saleHead.hykh = ManipulateStr.interceptExceedStr(saleHead.hykh, 20);
		saleHead.jfkh = ManipulateStr.interceptExceedStr(saleHead.jfkh, 20);
		saleHead.thsq = ManipulateStr.interceptExceedStr(saleHead.thsq, 20);
		saleHead.ghsq = ManipulateStr.interceptExceedStr(saleHead.ghsq, 20);
		saleHead.hysq = ManipulateStr.interceptExceedStr(saleHead.hysq, 20);
		saleHead.sqkh = ManipulateStr.interceptExceedStr(saleHead.sqkh, 20);
		saleHead.buyerinfo = ManipulateStr.interceptExceedStr(saleHead.buyerinfo, 20);
		saleHead.salefphm = ManipulateStr.interceptExceedStr(saleHead.salefphm, 20);

		saleHead.jdfhdd = ManipulateStr.interceptExceedStr(saleHead.jdfhdd, 20);

		// 计算本次积分
		saleHead.bcjf = calcSaleBCJF();
		saleHead.ljjf = calcSaleLJJF();

		// 不重算小票主单,避免发生和之前计算结果出现误差

		// 记录商品付款分摊
		if (!paymentApportionSummary())
			return false;

		return true;
	}

	public double calcSaleLJJF()
	{
		if (curCustomer == null || curCustomer.isjf != 'Y')
			return 0.00;

		return curCustomer.valuememo;
	}

	// 计算本笔销售积分
	public double calcSaleBCJF()
	{
		return calcSaleLJJF();
	}

	public boolean saleCollectAccountPay()
	{
		Payment p = null;
		boolean czsend = true;

		// 付款对象记账
		for (int i = 0; i < payAssistant.size(); i++)
		{
			p = (Payment) payAssistant.elementAt(i);
			if (p == null)
				continue;

			// 第一次记账前先检查是否有冲正需要发送
			if (czsend)
			{
				czsend = false;
				if (!p.sendAccountCz())
					return false;
			}

			// 付款记账
			if (!p.collectAccountPay())
				return false;
		}

		// 移动充值对象记账
		if (GlobalInfo.useMobileCharge && !mobileChargeCollectAccount(true))
			return false;

		return true;
	}

	public boolean saleCollectAccountClear()
	{
		Payment p = null;
		boolean ok = true;

		for (int i = 0; i < payAssistant.size(); i++)
		{
			p = (Payment) payAssistant.elementAt(i);
			if (p == null)
				continue;

			// 取消冲正,失败继续取消剩余的冲正
			if (!p.collectAccountClear())
				ok = false;
		}

		// 删除移动充值冲正
		if (GlobalInfo.useMobileCharge && !mobileChargeCollectAccount(false))
			ok = false;

		return ok;
	}

	// 树型顾客信息选择
	public void selectAllCustomerInfoTree()
	{
		String tempbuyerinfo = "";
		BuyInfoForm bif = new BuyInfoForm();
		bif.open();

		String[] buyinfo = saleHead.buyerinfo.split(",");

		if (bif.selCode.size() <= 0)
			return;

		if (saleHead.buyerinfo.trim().length() > 0)
		{
			for (int i = 0; i < buyinfo.length; i++)
			{
				String[] buyinfos = buyinfo[i].split(":");

				int j;
				for (j = 0; j < bif.selCode.size(); j++)
				{
					String[] selinfo = (String[]) bif.selCode.get(j);

					if (buyinfos[0].equals(selinfo[0]))
					{
						break;
					}
				}

				if (j >= bif.selCode.size())
				{
					bif.selCode.add(new String[] { buyinfos[0], buyinfos[1] });
				}
			}
		}

		for (int i = 0; i < bif.selCode.size(); i++)
		{
			String[] str = (String[]) bif.selCode.get(i);
			tempbuyerinfo = tempbuyerinfo + str[0] + ":" + str[1] + ",";
		}

		if (tempbuyerinfo.length() > 0)
			tempbuyerinfo = tempbuyerinfo.substring(0, tempbuyerinfo.length() - 1);

		saleHead.buyerinfo = tempbuyerinfo;
	}

	// 选择客户信息
	public void selectAllCustomerInfo()
	{
		String tempbuyerinfo = "";
		try
		{
			if (GlobalInfo.sysPara.custinfo.charAt(0) == 'T')
			{
				selectAllCustomerInfoTree();
			}
			else if (GlobalInfo.sysPara.custinfo.charAt(0) == 'G')
			{
				// 如果存在级别菜单才进入选择窗口,以免将以前的buyinfo给冲掉,造成用按键进行选择的顾客信息没有了
				if (GlobalInfo.sysPara.custinfo.indexOf('Y') > 0)
				{
					for (int i = 0; i < GlobalInfo.sysPara.custinfo.substring(1).length(); i++)
					{
						if (GlobalInfo.sysPara.custinfo.substring(1).charAt(i) == 'Y')
						{
							tempbuyerinfo = tempbuyerinfo + selectCustomerInfo(String.valueOf(i + 1));
						}
						else
						{
							tempbuyerinfo += "00";
						}
					}

					saleHead.buyerinfo = tempbuyerinfo;
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public String selectCustomerInfo(String type)
	{
		ResultSet rs = null;
		String[] title = { Language.apply("代码"), Language.apply("顾客信息描述") };
		int[] width = { 60, 440 };
		String[] content = null;
		Vector contents = new Vector();
		String caption = "";
		BuyerInfoDef bid = null;

		try
		{
			if ((rs = GlobalInfo.localDB.selectData("select code,type,name from BuyerInfo where type = '" + type + "'")) != null)
			{
				// 生成列表
				bid = new BuyerInfoDef();
				while (rs.next())
				{
					if (rs.getString(1).trim().equals("00"))
					{
						caption = rs.getString(3).trim();
						continue;
					}

					if (GlobalInfo.localDB.getResultSetToObject(bid, BuyerInfoDef.ref))
					{
						content = new String[2];
						content[0] = bid.code;
						content[1] = bid.name;

						contents.add(content);
					}
				}

				// 选择
				int choice = new MutiSelectForm().open(Language.apply("请选择") + caption, title, width, contents, true);
				if (choice >= 0)
				{
					content = (String[]) contents.get(choice);
					return content[0];
				}
			}

			return "00";
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "00";
		}
		finally
		{
			if (rs != null)
			{
				GlobalInfo.dayDB.resultSetClose();
			}
		}
	}

	public String getBuyerInfo(BuyerInfoDef bid)
	{
		ResultSet rs = null;
		String[] title = { Language.apply("代码"), Language.apply("上级代码"), Language.apply("顾客信息描述") };
		int[] width = { 60, 100, 440 };
		String[] content = null;
		Vector contents = new Vector();

		String caption = "";
		String customerinfo = null;

		BuyerInfoDef bid1 = null;

		boolean bool = false;

		try
		{
			caption = bid.name;

			if ((rs = GlobalInfo.localDB.selectData("select code,type,sjcode,name from BuyerInfo where type <> '" + bid.type + "' and sjcode = '" + bid.code + "'")) != null)
			{
				while (rs.next())
				{
					bool = true;

					bid1 = new BuyerInfoDef();

					if (GlobalInfo.localDB.getResultSetToObject(bid1, BuyerInfoDef.ref))
					{
						content = new String[3];
						content[0] = bid1.code;
						content[1] = bid1.sjcode;
						content[2] = bid1.name;

						contents.add(content);
					}
				}

				GlobalInfo.dayDB.resultSetClose();
			}

			int choice = 0;

			if (bool)
			{
				choice = new MutiSelectForm().open(Language.apply("请选择") + caption, title, width, contents, true);
			}

			if (choice >= 0)
			{
				if (contents == null || contents.size() <= 0)
					return "";

				bid1 = new BuyerInfoDef();
				content = (String[]) contents.get(choice);
				bid1.code = content[0];
				bid1.sjcode = content[1];
				bid1.name = content[2];

				customerinfo = getBuyerInfo(bid1);

				if (customerinfo.equals(""))
				{
					customerinfo = bid1.code;
				}

			}

			return customerinfo;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "00";
		}
		finally
		{
			if (rs != null)
			{
				GlobalInfo.dayDB.resultSetClose();
			}
		}
	}

	// 零钞转存功能
	public boolean doLcZc(Label txt_zl, Group grp_zl_sy)
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
			if (CreatePayment.getDefault().isPaymentLczc(sp) || CreatePayment.getDefault().isPaymentMobileCharge(sp))
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

		// 找零充值方式
		Vector vec = new Vector();

		// 可以使用移动找零充值
		if (GlobalInfo.useMobileCharge)
		{
			if (GlobalInfo.sysPara.isAutoLczc == 'N' || GlobalInfo.sysPara.isAutoLczc == 'M')
			{
				vec.add(new String[] { Language.apply("移动找零充值"), Language.apply("将找零充值到移动手机卡上"), "MOB" });
			}
		}

		// 可以使用会员零钞转存
		if ((curCustomer != null && curCustomer.func != null && curCustomer.func.length() > 0 && curCustomer.func.charAt(0) == 'Y') && (GlobalInfo.sysPara.isAutoLczc == 'N' || GlobalInfo.sysPara.isAutoLczc == 'Y' || GlobalInfo.sysPara.isAutoLczc == 'H'))
		{
			vec.add(new String[] { Language.apply("会员零钞转存"), Language.apply("将找零存入会员卡的零钞账户"), "HY" });
		}

		// 选择零钞转存方式
		String lczcmode = null, lczcdesc = null;
		if (vec.size() <= 0)
		{
			if (showtips)
			{
				if (GlobalInfo.useMobileCharge)
					new MessageBox(Language.apply("没有定义移动付款方式\n\n无法使用移动找零充值功能"));
				else
					new MessageBox(Language.apply("没有刷会员卡 或 会员没有定义零钞转存功能\n\n无法使用会员零钞转存功能!"));
			}
			return false;
		}
		else if (vec.size() == 1)
		{
			lczcdesc = ((String[]) vec.elementAt(0))[0];
			lczcmode = ((String[]) vec.elementAt(0))[2];
		}
		else
		{
			String[] title = { Language.apply("找零转存方式"), Language.apply("描述") };
			int[] width = { 200, 350 };
			int choice = new MutiSelectForm().open(Language.apply("请选择找零转存方式"), title, width, vec);
			if (choice < 0)
				return false;
			lczcdesc = ((String[]) vec.elementAt(choice))[0];
			lczcmode = ((String[]) vec.elementAt(choice))[2];
		}

		// 强制零钞转存则自动存入参数定义的最大金额,否则提示输入找零金额
		if (GlobalInfo.sysPara.isAutoLczc == 'Y')
		{
			// 存入低于参数的找零金额的零头部分,参数表示的意义是最小的可找零面额
			double maxlczc = ManipulatePrecision.doubleConvert(zl % GlobalInfo.sysPara.lczcmaxmoney);

			// 不能超过会员卡允许的每次存入金额
			if ("HY".equals(lczcmode) && curCustomer.value4 > 0 && maxlczc > curCustomer.value4) { return false; }

			zlmoney = maxlczc;
		}
		else
		{
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
			String line = Language.apply("本笔应找零金额为{0}元\n本次最多允许进行{1}元的{2}", new Object[] { ManipulatePrecision.doubleToString(zl, 2, 1) + "", ManipulatePrecision.doubleToString(maxlczc, 2, 1), lczcdesc });
			// String line = "本笔应找零金额为 " +
			// ManipulatePrecision.doubleToString(zl, 2, 1) + " 元\n" +
			// "本次最多允许进行 " + ManipulatePrecision.doubleToString(maxlczc, 2, 1) +
			// " 元的" + lczcdesc;
			if (!new TextBox().open(Language.apply("请输入您要进{0}的金额", new Object[] { lczcdesc }), Language.apply("金额"), line, buffer, 0.01, maxlczc, true)) { return false; }
			zlmoney = Double.parseDouble(buffer.toString());
			if (zlmoney > GlobalInfo.sysPara.lczcmaxmoney)
			{
				new MessageBox(Language.apply("输入的转存充值金额大于系统定义的{0}元\n无法进行零钞转存的功能!", new Object[] { ManipulatePrecision.doubleToString(GlobalInfo.sysPara.lczcmaxmoney) }));
				return false;
			}
			if ("HY".equals(lczcmode) && (curCustomer.value2 != 0 && (zlmoney + curCustomer.value1) > curCustomer.value2))
			{
				new MessageBox(Language.apply("该会员账户的零钞余额已经到达最大的上限金额\n无法进行零钞转存的功能!"));
				return false;
			}
		}

		// 先删除已存在的零钞转存
		deleteLcZc();

		// 再增加新的转存金额付款
		if ("HY".equals(lczcmode))
		{
			PaymentCustLczc pay = CreatePayment.getDefault().getPaymentLczc(saleEvent.saleBS);
			if (pay == null || !pay.createLczcSalePay(zlmoney))
			{
				new MessageBox(Language.apply("没有零钞转存付款方式 或 零钞转存对象创建失败\n\n无法进行零钞转存的功能!"));
				return false;
			}
			addSalePayObject(pay.salepay, pay);
		}
		if ("MOB".equals(lczcmode))
		{
			PaymentBankCMCC pay = CreatePayment.getDefault().getPaymentMobileCharge(saleEvent.saleBS);
			if (pay == null || !pay.createChgChargeSalePay(zlmoney))
			{
				new MessageBox(Language.apply("没有找零充值付款方式 或 找零充值对象创建失败\n\n无法进行找零充值的功能!"));
				return false;
			}
			addSalePayObject(pay.salepay, pay);
		}

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

	// 删除零钞转存功能
	public boolean deleteLcZc()
	{
		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) salePayment.elementAt(i);

			if (CreatePayment.getDefault().isPaymentLczc(spd) || CreatePayment.getDefault().isPaymentMobileCharge(spd))
			{
				delSalePayObject(i);
			}
		}

		return true;
	}

	// 方便客户化
	public void activeLczc()
	{
		NewKeyListener.sendKey(GlobalVar.PayLcZc);
	}

	// 查找积分换购
	public void findJfExchangeGoods(int index)
	{
		new MessageBox(Language.apply("当前版本不支持这个积分换购功能!"));
	}

	// 返回到正常销售界面
	public void backToSaleStatus()
	{
		// 如果是换货销售状态返回到零售销售
		if (SellType.ISHHSALE(saletype, hhflag))
		{
			hhflag = 'N';
		}

		// 退货交易切换回销售交易
		if (SellType.ISBACK(saletype))
		{
			djlbBackToSale();
		}
	}

	// 预算满赠
	public boolean checkFinalStatus()
	{
		return true;
	}

	public void showPayApportion(int index)
	{
		try
		{
			SalePayDef spay = (SalePayDef) salePayment.elementAt(index);
			int payseqno = (int) spay.num5;

			// 商品编码,商品名称,已付金额,限制金额,分摊金额,对应商品行号
			double maxftje = 0;
			double allftje = spay.je - spay.num1;
			Vector ftvec = new Vector();
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
				if (goodsSpare == null || goodsSpare.size() <= i)
					continue;
				SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(i);
				if (spinfo == null)
					continue;

				boolean allowft = false;
				double yfje = 0, ftje = 0;
				for (int j = 0; spinfo.payft != null && j < spinfo.payft.size(); j++)
				{
					String[] s = (String[]) spinfo.payft.elementAt(j);
					if (Convert.toInt(s[0]) == payseqno)
					{
						ftje += Convert.toDouble(s[3]);
						allowft = true;
					}
					else
					{
						yfje += Convert.toDouble(s[3]);
					}
				}
				if (allowft)
				{
					double limitje = ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk - yfje);
					maxftje = ManipulatePrecision.doubleConvert(maxftje + limitje);
					String[] row = { sg.code, sg.name, ManipulatePrecision.doubleToString(yfje), ManipulatePrecision.doubleToString(limitje), ManipulatePrecision.doubleToString(ftje), String.valueOf(i) };
					ftvec.add(row);
				}
			}
			if (ftvec.size() > 0)
			{
				// 显示分摊窗口，输入分摊金额
				if (allftje > maxftje)
					allftje = maxftje;
				String info = "[" + spay.paycode + "]" + Language.apply("{0}的有效付款为{1}元", new Object[] { spay.payname, ManipulatePrecision.doubleToString(spay.je - spay.num1) });
				// String info = "[" + spay.paycode + "]" + spay.payname +
				// "的有效付款为 " + ManipulatePrecision.doubleToString(spay.je -
				// spay.num1) + " 元";
				// +((spay.num1>0)?",有效金额 "+ManipulatePrecision.doubleToString(spay.je-spay.num1)+" 元":"");
				new ApportPaymentForm().open(ftvec, info, allftje, true);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public boolean getCreditCardZK()
	{
		if (!SellType.ISSALE(saletype))
		{
			new MessageBox(Language.apply("只有在消费时才有[追送]功能"));
			return false;
		}

		// 如果发现积分换购以外的付款方式，不允许追送折扣，防止先付的金额大于折扣后的金额
		String[] pay = CreatePayment.getDefault().getCustomPaymentDefine("PaymentCustJfSale");
		if (pay != null)
		{
			for (int j = 0; j < salePayment.size(); j++)
			{
				SalePayDef spd = (SalePayDef) salePayment.elementAt(j);
				boolean done = false;
				for (int i = 1; i < pay.length; i++)
				{
					if (spd.paycode.equals(pay[i]) || spd.paycode.equals("0509"))
					{
						done = true;
						continue;
					}
				}

				if (!done)
				{
					new MessageBox(Language.apply("追送折扣前不能进行付款"));
					return true;
				}
			}
		}

		// 获取联名卡类表
		Vector v = new Vector();
		if (DataService.getDefault().getCreditCardList(v, GlobalInfo.sysPara.mktcode))
		{
			Vector con = new Vector();

			for (int i = 0; i < v.size(); i++)
			{
				CustFilterDef filterDef = (CustFilterDef) v.elementAt(i);
				con.add(new String[] { filterDef.desc });
			}
			String[] title = { Language.apply("银联卡类型") };
			int[] width = { 500 };

			int choice = new MutiSelectForm().open(Language.apply("请选择卡类型"), title, width, con);
			// 没有选择规则不进行计算
			if (choice == -1)
				return true;

			CustFilterDef rule = ((CustFilterDef) v.elementAt(choice));

			// 输入顾客卡号
			TextBox txt = new TextBox();
			StringBuffer cardno = new StringBuffer();
			if (!txt.open(Language.apply("请刷联名卡或顾客卡"), Language.apply("卡号"), Language.apply("请将联名卡或顾客卡从刷卡槽刷入"), cardno, 0, 0, false, TextBox.MsrKeyInput)) { return false; }

			String line1 = txt.Track2;
			if (rule.Trackno == 1)
			{
				line1 = txt.Track1;
			}
			else if (rule.Trackno == 2)
			{
				line1 = txt.Track2;
			}
			else if (rule.Trackno == 3)
			{
				line1 = txt.Track3;
			}
			else
			{
				new MessageBox(Language.apply("解析磁道号设定错误，磁道必须是1-3之间"));
				return true;
			}

			String line2 = "";
			if (rule.Tracklen != null && rule.Tracklen.charAt(0) == '[')
			{
				String flag1 = rule.Tracklen.trim();
				flag1 = flag1.substring(1, flag1.length() - 1);

				if (rule.Trackpos >= 0)
				{
					line2 = line1.substring(rule.Trackpos);
				}
				else if (line1.length() - rule.Trackpos >= 0)
				{
					line2 = line1.substring(line1.length() - rule.Trackpos);
				}
				else
				{
					line2 = line1;
				}

				if (line2.indexOf(flag1) <= 0)
				{
					new MessageBox(Language.apply("无效 【{0}】\n或者配置文件出错，磁道中未找到{1}", new Object[] { rule.desc.trim(), rule.Tracklen }));
					return false;
				}

				line2 = line2.substring(0, line2.indexOf(flag1));
			}
			else
			{
				if (rule.Trackpos >= 0)
				{
					line2 = line1.substring(rule.Trackpos);
				}
				else if (line1.length() - rule.Trackpos >= 0)
				{
					line2 = line1.substring(line1.length() - rule.Trackpos);
				}
				else
				{
					line2 = line1;
				}

				if (Convert.toInt(rule.Tracklen) < line2.length())
					line2 = line2.substring(0, Convert.toInt(rule.Tracklen));
			}

			boolean updateDisplay = false;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
				SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(i);
				CustFilterDef filter = new CustFilterDef();

				if ((DataService.getDefault()).getCreditCardZK(filter, saleGoodsDef.code, line2, rule.TrackFlag, saleGoodsDef.gz, saleGoodsDef.catid, saleGoodsDef.ppcode, goodsDef.specinfo, saletype))
				{
					// 检查商品的已分摊金额
					double yftje = 0;
					if (spinfo.payft != null)
					{
						for (int j = 0; j < spinfo.payft.size(); j++)
						{
							String[] row = (String[]) spinfo.payft.elementAt(j);
							yftje += Convert.toDouble(row[3]);

						}
					}

					yftje = ManipulatePrecision.doubleConvert(yftje);

					double curje = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk - yftje + saleGoodsDef.qtzke);
					if (curje > 0 && filter.zkl > 0)
					{
						saleGoodsDef.qtzke = ManipulatePrecision.doubleConvert(curje * (1 - filter.zkl));
						saleGoodsDef.qtzke = getConvertRebate(i, saleGoodsDef.qtzke);
						saleGoodsDef.str5 = filter.desc;
						getZZK(saleGoodsDef);
						updateDisplay = true;
					}
				}
			}

			if (updateDisplay)
			{
				calcHeadYsje();
				// 计算剩余付款
				calcPayBalance();

				// 刷新商品列表
				saleEvent.updateTable(getSaleGoodsDisplay());
				saleEvent.setTotalInfo();
				// 刷新付款列表
				salePayEvent.refreshSalePayment();

				new MessageBox(Language.apply("此银行卡已经进行银行折扣\n请用此卡进行银联付款"));

				NewKeyListener.sendKey(GlobalVar.ArrowUp);
			}

		}
		return true;
	}

	public void payShowRebateDetail(int key)
	{
		// 信用卡追送功能
		if ((key == GlobalVar.WholeRate || key == GlobalVar.WholeRebate))
		{
			getCreditCardZK();
		}
		else
		{
			Vector choice = new Vector();
			String[] title = { Language.apply("序"), Language.apply("商品编码"), Language.apply("商品名称"), Language.apply("数量"), Language.apply("单价"), Language.apply("成交金额") };
			int[] width = { 30, 140, 200, 75, 120, 155 };
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);

				String[] row = new String[6];
				row[0] = String.valueOf(i + 1);
				row[1] = sgd.code;
				row[2] = sgd.name;
				row[3] = ManipulatePrecision.doubleToString(sgd.sl, 4, 1, true);
				row[4] = ManipulatePrecision.doubleToString(sgd.jg, 2, 1, false, 10);
				String s = ManipulatePrecision.doubleToString(sgd.hjje - sgd.hjzk) + "(" + ManipulatePrecision.doubleToString((sgd.hjje - sgd.hjzk) / sgd.hjje * 100, 0, 1, false, 2) + "%)";
				row[5] = Convert.increaseCharForward(s, 14);
				choice.add(row);

				boolean needblank = false;
				if (sgd.hyzke > 0)
				{
					row = new String[6];
					row[0] = "";
					row[1] = "";
					row[2] = "";
					row[3] = "";
					row[4] = Language.apply("会员折off:");
					s = ManipulatePrecision.doubleToString(sgd.hyzke) + "(" + ManipulatePrecision.doubleToString(sgd.hyzke / sgd.hjje * 100, 0, 1, false, 2) + "%)";
					row[5] = Convert.increaseCharForward(s, 14);
					choice.add(row);
					needblank = true;
				}

				if (sgd.yhzke + sgd.zszke > 0)
				{
					row = new String[6];
					row[0] = "";
					row[1] = "";
					row[2] = "";
					row[3] = "";
					row[4] = Language.apply("促销折off:");
					s = ManipulatePrecision.doubleToString(sgd.yhzke + sgd.zszke) + "(" + ManipulatePrecision.doubleToString((sgd.yhzke + sgd.zszke) / sgd.hjje * 100, 0, 1, false, 2) + "%)";
					row[5] = Convert.increaseCharForward(s, 14);
					choice.add(row);
					needblank = true;
				}

				if (sgd.hjzk - (sgd.hyzke + sgd.yhzke + sgd.zszke) > 0)
				{
					row = new String[6];
					row[0] = "";
					row[1] = "";
					row[2] = "";
					row[3] = "";
					row[4] = Language.apply("其他折off:");
					s = ManipulatePrecision.doubleToString(sgd.hjzk - sgd.hyzke - sgd.yhzke - sgd.zszke) + "(" + ManipulatePrecision.doubleToString((sgd.hjzk - sgd.hyzke - sgd.yhzke - sgd.zszke) / sgd.hjje * 100, 0, 1, false, 2) + "%)";
					row[5] = Convert.increaseCharForward(s, 14);
					choice.add(row);
					needblank = true;
				}

				if (needblank)
				{
					row = new String[6];
					row[0] = "";
					row[1] = "";
					row[2] = "";
					row[3] = "";
					row[4] = "";
					row[5] = "";
					choice.add(row);
				}
			}

			new MutiSelectForm().open(Language.apply("查看商品折扣详情"), title, width, choice, false, 780, 480, false);
		}
	}

	// 获得会员的送货信息
	public Vector getDeliveryInfo(CustomerDef customer)
	{
		// str1:{顾客姓名};str2:{送货地址};str3:{联系电话};str9:{区域信息};
		// str10:{道路信息};str11:{标志性建筑};str12:{邮编}|str1:{顾客姓名};
		// str2:{送货地址};str3:{联系电话};str9:{区域信息};str10:{道路信息};
		// str11:{标志性建筑};str12:{邮编}

		// 如果有会员卡,则查找会员的送货信息放到附加信息窗口当中
		Vector vc = new Vector();

		String[] deliveryinfo = null;
		if (customer != null && customer.deliveryinfo != null && customer.deliveryinfo.trim().length() > 0)
		{
			String[] deliveryinfos = customer.deliveryinfo.trim().split("\\|");
			Vector v = new Vector();
			int count = 0;
			for (int i = 0; i < deliveryinfos.length; i++)
			{
				String[] deliveryinfotemp = deliveryinfos[i].split(";");

				if (deliveryinfotemp.length > count)
				{
					count = deliveryinfotemp.length;
				}

				for (int j = 0; j < deliveryinfotemp.length; j++)
				{
					deliveryinfotemp[j] = deliveryinfotemp[j].split(":").length > 1 ? deliveryinfotemp[j].split(":")[1] : deliveryinfotemp[j];
				}

				v.add(deliveryinfotemp);
			}

			if (v.size() > 1)
			{
				String[] title = new String[count];
				int[] width = new int[count];
				for (int i = 0; i < count; i++)
				{
					title[i] = " ";
					width[i] = 200;
				}

				int choice = new MutiSelectForm().open(Language.apply("请送货地址"), title, width, v);

				if (choice == -1)
				{
					deliveryinfo = deliveryinfos[0].split(";");
				}
				else
				{
					deliveryinfo = deliveryinfos[choice].split(";");
				}
			}
			else if (v.size() == 1)
			{
				deliveryinfo = deliveryinfos[0].split(";");
			}
		}

		if (deliveryinfo != null)
		{
			for (int i = 0; i < deliveryinfo.length; i++)
			{
				String[] temp = deliveryinfo[i].split(":");

				KeyValueDef kvd = new KeyValueDef();

				if (temp.length > 1)
				{
					kvd.key = temp[0];
					kvd.value = temp[1];
				}
				else
				{
					kvd.key = deliveryinfo[i];
					kvd.value = "";
				}

				vc.add(kvd);
			}
		}

		return vc;
	}

	// ///////////////////////////////////////////////////////////////////
	public boolean mobileChargeCollectAccount(boolean account)
	{
		if (!GlobalInfo.useMobileCharge)
			return true;

		// 清除充值冲正
		if (!account)
		{
			PaymentBankCMCC mobpay = CreatePayment.getDefault().getPaymentMobileCharge(this.saleEvent.saleBS);
			if (mobpay != null && !mobpay.collectAccountClear())
				return false;
			else
				return true;
		}

		// 商品充值
		StringBuffer offlinegoods = new StringBuffer();
		StringBuffer onlinegoods = new StringBuffer();
		int offlinenum = 0, onlinenum = 0;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
			MemoInfoDef mobcharge = AccessLocalDB.getDefault().checkMobileCharge(sg.barcode);
			if (mobcharge != null)
			{
				if (sg.batch != null && !sg.batch.equals(""))
				{
					// 在线充值
					if (onlinegoods.length() > 0)
						onlinegoods.append("|");
					onlinegoods.append(sg.barcode + ":");
					onlinegoods.append(sg.batch + ":");
					onlinegoods.append((long) ManipulatePrecision.doubleConvert((sg.hjje - sg.hjzk) * 100) + ":");
					onlinegoods.append("000000");
					onlinenum++;
				}
				else
				{
					// 离线充值
					if (offlinegoods.length() > 0)
						offlinegoods.append("|");
					offlinegoods.append(sg.barcode + ":");
					offlinegoods.append((long) ManipulatePrecision.doubleConvert((sg.hjje - sg.hjzk) * 100) + ":");
					offlinegoods.append("000000");
					offlinenum++;
				}
			}
		}
		int questgoods = 0, questchange = 0;
		PaymentBankCMCC mobpay = null;
		if (onlinenum > 0 || offlinenum > 0)
		{
			if (mobpay == null)
			{
				mobpay = CreatePayment.getDefault().getPaymentMobileCharge(this.saleEvent.saleBS);
				if (mobpay == null)
				{
					new MessageBox(Language.apply("启用移动充值失败\n\n不能完成充值交易"));
					return false;
				}
			}

			StringBuffer passwd = new StringBuffer();
			StringBuffer memo = new StringBuffer();
			if (!mobpay.questGoodsMobileCharge(offlinenum, offlinegoods.toString(), onlinenum, onlinegoods.toString(), passwd, memo))
			{
				new MessageBox(Language.apply("移动充值申请失败\n\n不能完成移动充值交易"));
				return false;
			}
			questgoods++;
		}

		// 找零充值
		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef sp = (SalePayDef) salePayment.elementAt(i);
			if (CreatePayment.getDefault().isPaymentMobileCharge(sp))
			{
				if (mobpay == null)
				{
					mobpay = CreatePayment.getDefault().getPaymentMobileCharge(this.saleEvent.saleBS);
					if (mobpay == null)
					{
						new MessageBox(Language.apply("启用找零充值失败\n\n不能完成充值交易"));
						return false;
					}
				}

				// 找零充值
				StringBuffer chggoods = new StringBuffer();
				if (chggoods.length() > 0)
					chggoods.append("|");
				chggoods.append(sp.idno + ":");
				chggoods.append(sp.payno + ":");
				chggoods.append((long) ManipulatePrecision.doubleConvert(Math.abs(sp.je) * 100));
				if (!mobpay.questChangeMobileCharge(chggoods.toString()))
				{
					new MessageBox(Language.apply("找零充值申请失败\n\n不能完成移动找零充值"));
					return false;
				}
				questchange++;
			}
		}

		// 完成记账
		if (mobpay != null && !mobpay.commitGoodsMobileCharge(questgoods, questchange))
		{
			new MessageBox(Language.apply("移动充值记账失败\n\n不能完成移动充值交易"));
			return false;
		}

		return true;
	}

	// ////////////////////////////////////////////////////////////////
}
