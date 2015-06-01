package com.efuture.javaPos.Payment;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosClock;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Bank.CMCC_PaymentBankFunc;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.MemoInfoDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

//移动积分消费和充值
public class PaymentBankCMCC extends PaymentBank
{
	CMCC_PaymentBankFunc pbfunc = null;

	public PaymentBankCMCC()
	{
		super();
	}

	public PaymentBankCMCC(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public PaymentBankCMCC(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public void initPayment(PayModeDef mode, SaleBS sale)
	{
		super.initPayment(mode, sale);

		pbfunc = new CMCC_PaymentBankFunc();
	}

	public void initPayment(SalePayDef pay, SaleHeadDef head)
	{
		super.initPayment(pay, head);

		pbfunc = new CMCC_PaymentBankFunc();
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			PosClock.setKeepActive(false);
			if (SellType.ISBACK(salehead.djlb))
			{
				new MessageBox(Language.apply("退货交易不能使用移动积分消费"));
				return null;
			}
			else
			{
				if (!pbfunc.sp_sendAccountCz())
					return null;

				// 检查付款方式是否是移动找零充值付款
				MemoInfoDef info = AccessLocalDB.getDefault().checkMobileCharge(null);
				if (info != null && paymode.code.equals(info.text))
				{
					new MessageBox(Language.apply("不能使用移动找零充值方式进行支付"));
					return null;
				}

				// 输入移动积分
				return super.inputPay(money);
			}
		}
		finally
		{
			PosClock.setKeepActive(true);
		}
	}

	// 取消当前付款方式
	public boolean cancelPay()
	{
		// 找零充值直接返回
		if (salepay.memo.equals("3"))
			return true;
		else
		{
			return pbfunc.sp_cancelShopper(salepay.fphm, salehead.syyh, salepay.payno);
		}
	}

	// 集中提交积分扣款
	public boolean collectAccountPay()
	{
		// 找零充值直接返回,由业务部分统一提交商品充值和找零充值
		if (salepay.memo.equals("3"))
			return true;
		else
		{
			boolean ret = pbfunc.sp_commitShopper(salepay.fphm, salehead.syyh, salepay.payno, salepay.je);
			if (!ret)
				// new MessageBox("[" + salepay.payno +
				// "]积分券扣款失败\n\n可删除积分付款方式后重新认证积分密码");
				new MessageBox(Language.apply("[{0}]积分券扣款失败\n\n可删除积分付款方式后重新认证积分密码", new Object[] { salepay.payno }));
			return ret;
		}
	}

	// 清除移动积分冲正
	public boolean collectAccountClear()
	{
		return pbfunc.sp_deleAccountCz(null);
	}

	public boolean sendAccountCz()
	{
		return pbfunc.sp_sendAccountCz();
	}

	public void accountPay(boolean ret, BankLogDef bld, PaymentBankFunc pbf)
	{
		try
		{
			super.accountPay(ret, bld, pbf);
			if (!ret)
				return;

			// 移动积分消费支付
			salepay.idno = bld.memo; // 卡类别,卡发行商
			salepay.memo = "1"; // 积分消费
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public String inputChargePhone(double money)
	{
		// 先发送未发送的冲正
		if (!pbfunc.sp_sendAccountCz())
			return null;

		String cardno = null;

		// 调用接口输入充值手机号
		if (ConfigClass.CustomItem4 != null && ConfigClass.CustomItem4.toString().split("\\,")[0].toString().equals("0"))
			// cardno = pbfunc.sp_inputPhone("请顾客在小键盘输入要充值 " +
			// ManipulatePrecision.doubleToString(money) +
			// " 元的手机号码...","注意：手机号码长度不得小于11位");
			cardno = pbfunc.sp_inputPhone(Language.apply("请顾客在小键盘输入要充值 {0} 元的手机号码...", new Object[] { ManipulatePrecision.doubleToString(money) }), Language.apply("注意：手机号码长度不得小于11位"));
		else
			// cardno = pbfunc.sp_inputPhone("请顾客在小键盘输入要充值 " +
			// ManipulatePrecision.doubleToString(money) + " 元的手机号码...");
			cardno = pbfunc.sp_inputPhone(Language.apply("请顾客在小键盘输入要充值 {0} 元的手机号码...", new Object[] { ManipulatePrecision.doubleToString(money) }));
		if (cardno == null || cardno.trim().length() != 11)
		{
			new MessageBox(Language.apply("输入的充值手机号为空或不是11位有效手机号码!"));
			return null;
		}

		return cardno;
	}

	public boolean createChgChargeSalePay(double zl)
	{
		// 先发送未发送的冲正
		if (!pbfunc.sp_sendAccountCz())
			return false;

		// 先找到找零充值商品
		MemoInfoDef info = AccessLocalDB.getDefault().checkMobileCharge(null);
		if (info == null)
		{
			new MessageBox(Language.apply("找不到找零充值商品,无法进行找零充值"));
			return false;
		}

		// 创建SalePay
		if (!createSalePayObject(String.valueOf((zl))))
			return false;

		// 输入充值手机号
		String cardno = inputChargePhone(zl);
		if (cardno == null || cardno.trim().length() <= 0)
			return false;
		salepay.payno = cardno;
		salepay.idno = info.code; // 找零充值商品

		// 零钞转存付款方式金额记负数
		salepay.ybje *= -1;
		salepay.je *= -1;

		// 代表零钞转存
		salepay.memo = "3";

		return true;
	}

	public boolean questGoodsMobileCharge(int offlinenum, String offlinegoods, int onlinenum, String onlinegoods, StringBuffer passwd, StringBuffer memo)
	{
		// 先发送未发送的冲正
		if (!pbfunc.sp_sendAccountCz())
			return false;

		return pbfunc.sp_questGoods(salehead.fphm, salehead.syyh, offlinenum, offlinegoods, onlinenum, onlinegoods, passwd, memo);
	}

	public boolean questChangeMobileCharge(String chggoods)
	{
		// 先发送未发送的冲正
		if (!pbfunc.sp_sendAccountCz())
			return false;

		return pbfunc.sp_questChange(salehead.fphm, salehead.syyh, chggoods);
	}

	public boolean commitGoodsMobileCharge(int questgoods, int questchange)
	{
		// 先发送未发送的冲正
		if (!pbfunc.sp_sendAccountCz())
			return false;

		return pbfunc.sp_commitGoods(salehead.fphm, salehead.syyh, questgoods, questchange);
	}

	public boolean printOfflineChargeBill(long fphm)
	{
		return pbfunc.sp_printOfflineChargeBill(fphm);
	}
}
