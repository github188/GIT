package custom.localize.Bstd;

import org.eclipse.swt.events.KeyEvent;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentCustJfSale;
import com.efuture.javaPos.Payment.PaymentMzkEvent;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bstd_PaymentCustJfSale extends PaymentCustJfSale
{
	private double allowchgje = 0.0;

	public Bstd_PaymentCustJfSale()
	{
	}

	public Bstd_PaymentCustJfSale(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Bstd_PaymentCustJfSale(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public void specialDeal(PaymentMzkEvent event)
	{
		event.yeTips.setText("积分余额");

		if (saleBS.curCustomer != null)
		{
			if (GlobalInfo.sysPara.isReMSR != 'Y')
			{
				// M:修改积分消费界面焦点问题，当JP为Y 付款时需要输入会员卡，积分消费界面焦点会在moneyText中
				event.moneyTxt.setText(ManipulatePrecision.doubleToString(allowchgje)); //重写此函数目的就在于要显示正确的积分折算后金额
				event.moneyTxt.setFocus();
				event.moneyTxt.selectAll();
			}
		}
	}

	public void setPwdAndYe(PaymentMzkEvent event, KeyEvent e)
	{
		if (isPasswdInput())
		{
			// 显示密码
			event.yeTips.setText(getPasswdLabel());
			event.yeTxt.setVisible(false);
			event.pwdTxt.setVisible(true);
			event.yeTxt.setText(ManipulatePrecision.doubleToString(getAccountYe()));

			if (e != null)
				e.data = "focus";
			event.pwdTxt.setFocus();
			event.pwdTxt.selectAll();
		}
		else
		{
			// 显示余额
			event.yeTips.setText("积分余额");
			event.yeTxt.setVisible(true);
			event.pwdTxt.setVisible(false);
			event.yeTxt.setText(ManipulatePrecision.doubleToString(getAccountYe()));

			// 输入金额
			if (e != null)
				e.data = "focus";
			event.moneyTxt.setFocus();
			event.moneyTxt.selectAll();

		}
	}

	public double getPayJe(double moneyText)
	{
		return this.allowchgje;
		/*
		 * String memo = null; // 先判断当前刷卡的积分规则，再判断会员卡内的积分消费规则 if (mzkret.memo !=
		 * null && mzkret.memo.length() > 0) { memo = mzkret.memo; } else { memo
		 * = saleBS.curCustomer.memo; } String[] num = memo.split(",");
		 * 
		 * double payje = getAccountAllowPay() / Convert.toDouble(num[0]) *
		 * Convert.toDouble(num[1]); return payje;
		 */}

	public boolean createSalePay(String money)
	{
		if (Convert.toDouble(money) > this.allowpayje)
		{
			new MessageBox("当前可支付金额为" + allowpayje);
			return false;
		}

		String memo = null;
		// 先判断当前刷卡的积分规则，再判断会员卡内的积分消费规则
		if (mzkret.memo != null && mzkret.memo.length() > 0)
			memo = mzkret.memo;
		String[] num = memo.split(",");

		if (ManipulatePrecision.mod(Convert.toDouble(money), Convert.toDouble(num[1])) != 0)
		{
			new MessageBox("请输入" + num[1] + "的整数倍");
			return false;
		}

		return super.createSalePay(money);
	}

	public double getAccountAllowPay()
	{
		String memo = null;
		double totaljf = 0;
		// 先判断当前刷卡的积分规则，再判断会员卡内的积分消费规则
		if (mzkret.memo != null && mzkret.memo.length() > 0)
		{
			memo = mzkret.memo;
		}
		else
		{
			if (saleBS.curCustomer == null)
				return 0.0;

			memo = saleBS.curCustomer.memo;
		}

		if (memo.indexOf(",") < 0)
			return 0;

		if (saleBS.curCustomer != null)
		{
			totaljf = saleBS.curCustomer.valuememo;
		}
		else
		{
			totaljf = mzkret.ye;
		}

		String[] num = memo.split(",");

		if (totaljf < Convert.toInt(num[0]))
		{
			new MessageBox("积分总数不符合折现比例");
			return 0;
		}

		int times = ManipulatePrecision.integerDiv(saleBS.calcPayBalance(), Convert.toInt(num[1]));

		if (times < 1)
		{
			new MessageBox("当前整单金额不符合折现比例，不能用此付款方式付款");
			return 0;
		}

		int i = times;

		for (; i > 0; i--)
		{
			if (i * Convert.toInt(num[0]) <= totaljf)
				break;
		}

		allowchgje = i * Convert.toInt(num[1]);
		return i * Convert.toInt(num[0]);

	}

	public double getAccountYe()
	{
		if (saleBS.curCustomer != null)
			return saleBS.curCustomer.valuememo;
		else
			return mzkret.ye;
	}

	protected String getDisplayStatusInfo()
	{
		String line = "";

		if (SellType.ISSALE(salehead.djlb))
		{
			double totaljf = 0;

			if (saleBS.curCustomer != null)
				totaljf = saleBS.curCustomer.valuememo;
			else
				totaljf = mzkret.ye;

			allowpayje = Math.min(getAccountAllowPay(), totaljf);

			if (mzkret.memo != null && mzkret.memo.split(",").length == 2)
			{
				String[] num = mzkret.memo.split(",");

				line += "当前会员的积分是:" + totaljf + "\n" + ManipulatePrecision.doubleToString(Convert.toDouble(num[0]), 2, 1) + " 积分兑换 " + ManipulatePrecision.doubleToString(Double.parseDouble(num[1])) + "元\n最大可收积分数: " + allowpayje + "分";
			}
		}
		return line;
	}

	public boolean createJfExchangeSalePay(double je, double jf, String jfinfo, int index)
	{
		// 设置PaymentJfSale对象初始值
		// super.setCustomerInfo();

		// 创建SalePay对象
		if (!createSalePayObject(String.valueOf(je)))
			return false;

		// 记录账号信息到SalePay
		if (!saveFindMzkResultToSalePay())
			return false;

		salepay.payno = saleBS.curCustomer.code;
		salepay.idno = jfinfo;
		salepay.num4 = jf;

		// 标记为积分换购
		salepay.payname = "积分换购";
		salepay.memo = "2";

		return true;
	}

	protected boolean setRequestDataByAccount()
	{
		// 得到消费序号
		long seqno = getMzkSeqno();
		if (seqno <= 0)
			return false;

		// 打消费交易包
		mzkreq.seqno = seqno;
		mzkreq.je = salepay.ybje;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.fphm = GlobalInfo.syjStatus.fphm;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.paycode = salepay.paycode;
		mzkreq.invdjlb = ((salehead != null) ? salehead.djlb : "");

		if (salepay.paycode.equals("0508"))
		{
			String[] row = salepay.idno.split(",");
			mzkreq.je = Convert.toDouble(row[0]);
		}
		if (salepay.paycode.equals("0509"))
		{
			mzkreq.num3 = salepay.num4;
			mzkreq.memo = salepay.idno;
		}

		// 告诉后台过程磁道信息是存放的是卡号,只采用卡号记账方式,不使用磁道记账方式
		mzkreq.track1 = "CARDNO";
		mzkreq.track2 = salepay.payno;

		return true;
	}

	public void showAccountYeMsg()
	{
		if (!messDisplay)
			return;

		if (mzkret.memo != null && mzkret.memo.length() > 0)
		{
			StringBuffer info = new StringBuffer();
			String[] num = mzkret.memo.split(",");
			String text = "付";

			double je = ManipulatePrecision.div(salepay.ybje, Convert.toDouble(num[1]));
			je = ManipulatePrecision.mul(je, Convert.toDouble(num[0]));
			double ye = getAccountYe() - je;
			if (checkMzkIsBackMoney())
			{
				text = "退";
				ye = getAccountYe() + getAccountAllowPay();// salepay.je;
			}
			info.append("卡内积分余额为: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(getAccountYe()), 0, 12, 12, 1) + "\n");
			info.append("本次积分" + text + "款额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(je), 0, 12, 12, 1) + "\n");
			if (ye > 0)
				info.append(text + "款后积分余额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(ye), 0, 12, 12, 1) + "\n");

			new MessageBox(info.toString());
		}
	}

	protected void saveAccountMzkResultToSalePay()
	{
		// batch标记本付款方式已记账,这很重要
		salepay.batch = String.valueOf(mzkreq.seqno);

		// 标记记账返回的卡号
		if (!CommonMethod.isNull(mzkret.cardno))
			salepay.payno = mzkret.cardno;

		/*
		 * // new
		 * MessageBox("salepay.kye="+salepay.kye+" mzkret.ye="+mzkret.ye);
		 * // 后台退货时没有刷卡所以记录后台返回的卡余额,或者记账过程返回了最终余额
		 * if (salepay.kye <= 0 || mzkret.ye > 0 || (mzkret.status != null &&
		 * mzkret.status.equals("RETURNYE")))
		 * {
		 * salepay.kye = mzkret.ye;
		 * }
		 * else
		 * {
		 * // 记账过程没有返回最终余额，以查询的余额做基准加减计算新余额
		 * if (mzkreq.type == "01")
		 * salepay.kye -= mzkreq.je;
		 * else
		 * salepay.kye += mzkreq.je;
		 * }
		 * salepay.kye = ManipulatePrecision.doubleConvert(salepay.kye);
		 */
		// new MessageBox("salepay.kye="+salepay.kye);
		// 更新付款断点数据，标记为已付款状态,否则在记账以后如果掉电,断点读入的还是未记账状态
		if (this.saleBS != null)
			this.saleBS.writeBrokenData();
	}

	public boolean mzkAccount(boolean isAccount)
	{
		do
		{
			// 退货交易卡号为空时提示刷卡
			paynoMsrflag = false;
			if (!paynoMSR())
				return false;

			// 设置交易类型,isAccount=true是记账,false是撤销
			if (isAccount)
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0)
					mzkreq.type = "01"; // 消费,减
				else
					mzkreq.type = "03"; // 退货,加
			}
			else
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0)
					mzkreq.type = "03"; // 退货,加
				else
					mzkreq.type = "01"; // 消费,减
			}

			// 保存交易数据进行交易
			if (!setRequestDataByAccount())
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}

			// 先写冲正文件 //积分换购不做冲正
			// if (!writeMzkCz())
			// {
			// if (paynoMsrflag)
			// {
			// salepay.payno = "";
			// continue;
			// }
			// return false;
			// }

			// 记录面值卡交易日志
			BankLogDef bld = mzkAccountLog(false, null, mzkreq, mzkret);

			// 发送交易请求
			if (!sendMzkSale(mzkreq, mzkret))
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}

			// 记录应答信息, batch标记本付款方式已记账,这很重要
			saveAccountMzkResultToSalePay();

			// 记账完成操作,可用于记录记账日志或其他操作
			return mzkAccountFinish(isAccount, bld);
		}
		while (true);
	}
}
