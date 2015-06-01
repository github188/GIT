package com.efuture.javaPos.Payment;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

//零钞转成对象
public class PaymentCustLczc extends PaymentCust
{
	public PaymentCustLczc()
	{
	}

	public PaymentCustLczc(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public PaymentCustLczc(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	protected boolean needFindAccount()
	{
		if (SellType.ISSALE(salehead.djlb))
		{
			if (GlobalInfo.sysPara.isReMSR == 'Y')
				return true;
			if (saleBS.curCustomer != null)
				return false;
			else
				return true;
		}
		else
		{
			return true;
		}
	}

	public boolean autoCreateAccount()
	{
		setCustomerInfo();

		// 查询是否已刷此卡
		if (saleBS.existPayment(paymode.code, mzkret.cardno) >= 0)
		{
			boolean ret = false;
			if (new MessageBox(Language.apply("此卡已进行付款,你要取消原付款重新输入吗？"), null, true).verify() == GlobalVar.Key1)
			{
				ret = true;
				int n = -1;
				do
				{
					n = saleBS.existPayment(paymode.code, mzkret.cardno);
					if (n >= 0)
					{
						if (!saleBS.deleteSalePay(n))
						{
							ret = false;
							break;
						}
					}
				} while (n >= 0);

				// 重新刷新付款余额及已付款列表
				saleBS.salePayEvent.refreshSalePayment();
			}
			if (!ret)
			{
				new MessageBox(Language.apply("此卡已经付款，请先删除原付款"));
				return ret;
			}
		}
		return true;
	}

	// 用会员卡信息赋给面值卡结构
	public boolean setCustomerInfo()
	{

		if (saleBS.curCustomer != null)
		{
			setRequestDataByFind("", saleBS.curCustomer.code, "");

			mzkret.cardno = saleBS.curCustomer.code;
			mzkret.cardname = saleBS.curCustomer.name;
			mzkret.ye = saleBS.curCustomer.value1; // 零钞余额
			mzkret.money = saleBS.curCustomer.value2; // 最大零钞余额
			mzkret.pwdje = saleBS.curCustomer.pwdje;//零钞免密限额
			mzkret.cardpwd = saleBS.curCustomer.valstr6;//零钞转存支付密码
		}

		return true;
	}

	protected String getDisplayAccountInfo()
	{
		return Language.apply("会员卡号");
	}

	protected String getDisplayStatusInfo()
	{
//		return "当前账户零钞余额是:" + ManipulatePrecision.doubleToString(mzkret.ye);
		return Language.apply("当前账户零钞余额是:{0}" ,new Object[]{ManipulatePrecision.doubleToString(mzkret.ye)});
	}

	public boolean createLczcSalePay(double zl)
	{
		// 初始设置
		setCustomerInfo();

		// 创建SalePay
		if (!createSalePayObject(String.valueOf((zl))))
			return false;

		// 记录账号信息到SalePay
		if (!saveFindMzkResultToSalePay())
			return false;

		// 零钞转存付款方式金额记负数

		salepay.ybje *= -1;
		salepay.je *= -1;

		// 代表零钞转存
		salepay.memo = "3";

		return true;
	}

	protected void saveAccountMzkResultToSalePay()
	{
		// batch标记本付款方式已记账,这很重要
		salepay.batch = String.valueOf(mzkreq.seqno);

		// 标记记账返回的卡号
		if (!CommonMethod.isNull(mzkret.cardno))
			salepay.payno = mzkret.cardno;

		// new MessageBox("salepay.kye="+salepay.kye+" mzkret.ye="+mzkret.ye);
		// 后台退货时没有刷卡所以记录后台返回的卡余额,或者记账过程返回了最终余额
		// 让零钞转存的返回不更新kye，在后面的业务中统一进行更新
		if (salepay.memo != null && !salepay.memo.equals("3"))
		{
			if (salepay.kye <= 0 || mzkret.ye > 0 || (mzkret.status != null && mzkret.status.equals("RETURNYE")))
			{
				salepay.kye = mzkret.ye;
			}
			else
			{
				// 记账过程没有返回最终余额，以查询的余额做基准加减计算新余额
				// 因为零钞转存也会发送一个01交易请求过去，所以会将余额再多加一遍
				if (mzkreq.type == "01")
					salepay.kye -= mzkreq.je;
				else
					salepay.kye += mzkreq.je;
			}
			salepay.kye = ManipulatePrecision.doubleConvert(salepay.kye);
		}

		// new MessageBox("salepay.kye="+salepay.kye);
		// 更新付款断点数据，标记为已付款状态,否则在记账以后如果掉电,断点读入的还是未记账状态
		if (this.saleBS != null)
			this.saleBS.writeBrokenData();
	}

	public boolean mzkAccountFinish(boolean isAccount, BankLogDef bld)
	{
		if (super.mzkAccountFinish(isAccount, bld))
		{
			// 如果一笔交易付款中即有零钞转存，又有零钞付款，余额处理比较麻烦
			if (salepay.memo != null && salepay.memo.equals("3")) // memo为"3"时证明是零钞转存
			{
				// 对零钞转存卡余额进行处理
				// 如果付款列表中没有零钞转存付款，则将卡余额置为正数，方便小票打印零钞转存余额（余额为负是不打印的）
			
				//该余额为原始余额+转存的余额
				salepay.kye +=  salepay.je * -1;

				if (saleBS != null)
				{
					for (int i = 0; i < saleBS.salePayment.size(); i++)
					{
						SalePayDef pay = (SalePayDef) saleBS.salePayment.elementAt(i);
						
						if (salepay != pay && salepay.paycode.equals(pay.paycode) && salepay.payno.equals(pay.payno))
							salepay.kye -= pay.je;  //原始余额+转存的金额减去钱包消费的金额
					}
				}

			}
			return true;
		}

		return false;
	}
	public boolean createSalePay(String money)
	{
		if(super.createSalePay(money))
		{
			//mzkret.pwdje=1;//test data
			double ybje = Double.parseDouble(saleBS.getPayMoneyByPrecision(Double.parseDouble(money), paymode));
			if (mzkret.pwdje>0 && ybje>mzkret.pwdje)
			{
				while(true)
				{
					//当消费金额大于免密限额时，则要输入支付密码
					StringBuffer passwd = new StringBuffer(); 
					TextBox txt = new TextBox();
					if (!txt.open("请输入密码", "PASSWORD",  "超过免密限额(" + String.valueOf(mzkret.pwdje) + ")，需要输入支付密码", passwd, 0, 0, false, TextBox.AllInput)) 
					{ 
						salepay = null;
						return false; 
					}
					if (!mzkret.cardpwd.equals(passwd.toString()))
					{
						new MessageBox("支付密码错误！");
						continue;
					}
					break;
				}
				
			}
			return true;
		}
		
		return false;
	}
}
