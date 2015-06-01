package custom.localize.Agog;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Payment.PaymentMzkForm;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Agog_VipPaymentMzk extends PaymentMzk
{
	public Agog_VipPaymentMzk()
	{
		super();
	}

	public Agog_VipPaymentMzk(PayModeDef mode, SaleBS sale)
	{
		super(mode, sale);
	}

	public Agog_VipPaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		super(pay, head);
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && (track2 == null || track2.trim().length() <= 0) && (track3 == null || track3.trim().length() <= 0))
		{
			new MessageBox("磁道数据为空!");
			return false;
		}

		// 解析磁道
		String[] s = parseTrack(track1, track2, track3);
		if (s == null)
			return false;
		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		// 设置请求数据
		setRequestDataByFind(track1, track2, track3);

		return sendMzkSale(mzkreq, mzkret);
	}

	protected boolean needFindAccount()
	{
		// 未刷卡
		if (saleBS.curCustomer != null)
			return false;
		else
			return true;
	}

	public boolean cancelPay()
	{
		if (mzkAccount(false))
			return true;

		return false;
	}

	public boolean setCustomerInfo()
	{
		if (saleBS.curCustomer != null)
		{
			mzkret.cardno = saleBS.curCustomer.code;
			mzkret.ye = saleBS.curCustomer.num1;
			mzkret.status = saleBS.curCustomer.status;
		}

		return true;
	}

	private boolean checkExistSamePay()
	{
		SalePayDef saledef = null;
		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			saledef = (SalePayDef) saleBS.salePayment.elementAt(i);

			// 查找模式时,找到付款代码和账号相同的
			// 覆盖模式时,必须付款代码和账号相同且是直接输入付款金额的付款方式才允许覆盖
			if (saledef.paycode.equals(paymode.code) && saledef.payno.trim().equals(mzkret.cardno))
			{
				if (saledef.batch != null || saledef.batch.trim().length() > 0)
				{
					// 记账,直接返回
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		return false;
	}

	public boolean autoCreateAccount()
	{
		setCustomerInfo();

		if (checkExistSamePay())
		{
			new MessageBox("会员卡[" + mzkret.cardno + "]已经付过款，请先删除原付款");
			return false;
		}

		return true;
	}

	private boolean checkCheckCode(MzkRequestDef req)
	{
		String chkcode = Agog_VipCaller.getDefault().getCheckCode(req.track2);
		if (chkcode == null)
			return false;

		TextBox txt = new TextBox();
		StringBuffer passwd = null;
		int i = 0;

		do
		{
			passwd = new StringBuffer();

			if (!txt.open("请输消费校验码", "PASSWORD", "请输入短信收到的校验码", passwd, 0, 0, false, TextBox.AllInput))
				return false;

			if (passwd.toString().equals(chkcode))
				break;

			i++;

			if (i == 3)
				return false;

			new MessageBox("校验码错误,您还有" + (3 - i) + "次机会");

		} while (i < 3);

		return Agog_VipCaller.getDefault().sendVipSale(req, passwd.toString());
	}

	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		if (req.type.equals("05"))
		{
			CustomerDef cust = new CustomerDef();
			if (!Agog_VipCaller.getDefault().queryVip(cust, req.track2, true))
				return false;

			ret.cardno = cust.code;
			ret.ye = cust.num1;

			return true;
		}
		else
		{
			// 退货状态付款
			if (req.type.equals("02") || req.type.equals("03"))
			{
				req.num2 = req.num2 * -1;
				req.je = req.je * -1;

				return Agog_VipCaller.getDefault().sendVipSale(req, "");
			}

			// 退货状态下删除付款
			if (req.type.equals("04"))
				return Agog_VipCaller.getDefault().sendVipSale(req, "");

			// 消费扣款
			if (req.type.equals("01"))
			{
				// 手输卡号
				if (Agog_VipCaller.getDefault().isCert())
					return checkCheckCode(req);

				// 包房获取会员
				if (Agog_VipCaller.getDefault().isCheckTrade())
					return checkCheckCode(req);

				TextBox txt = new TextBox();
				StringBuffer passwd = new StringBuffer();

				if (!txt.open("会员卡密码", "PASSWORD", "请输入会员卡密码", passwd, 0, 0, false, TextBox.AllInput))
					return false;

				if (!passwd.toString().trim().equals(saleBS.curCustomer.valstr1))
				{
					new MessageBox("密码错误");
					return false;
				}

				return Agog_VipCaller.getDefault().sendVipSale(req, "");
			}
		}

		return false;
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			// 打开明细输入窗口
			new PaymentMzkForm().open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	public boolean sendAccountCzData(MzkRequestDef req, String czfile, String czname)
	{
		return true;
	}

	// 保存交易数据进行交易
	protected boolean setRequestDataByAccount()
	{
		// 得到消费序号
		long seqno = getMzkSeqno();
		if (seqno <= 0)
			return false;

		// 打消费交易包
		mzkreq.seqno = seqno;
		mzkreq.fphm = GlobalInfo.syjStatus.fphm;
		mzkreq.je = salepay.ybje;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.paycode = salepay.paycode;
		mzkreq.invdjlb = ((salehead != null) ? salehead.djlb : "");

		mzkreq.num1 = salehead.hjzje; // 记录应付
		mzkreq.num2 = ManipulatePrecision.doubleConvert(salehead.hjzje - salehead.hjzke, 2, 1); // 记录实收
		// 告诉后台过程磁道信息是存放的是卡号,只采用卡号记账方式,不使用磁道记账方式
		mzkreq.track1 = "";
		mzkreq.track2 = salepay.payno;

		return true;
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
					mzkreq.type = "01"; // 消费减钱
				else
					mzkreq.type = "02"; // 退货加钱
			}
			else
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0)
					mzkreq.type = "03"; // 消费删除付款加钱
				else
					mzkreq.type = "04"; // 退货删除减钱
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

				// 交易失败后直接关闭付款框
				this.inputMoney = String.valueOf(mzkreq.je);

				return false;
			}

			// 记录应答信息, batch标记本付款方式已记账,这很重要
			saveAccountMzkResultToSalePay();

			// 记账完成操作,可用于记录记账日志或其他操作
			return mzkAccountFinish(isAccount, bld);
		} while (true);
	}

	public boolean collectAccountPay()
	{
		return true;
	}

	protected void saveAccountMzkResultToSalePay()
	{
		// batch标记本付款方式已记账,这很重要
		salepay.batch = String.valueOf(mzkreq.seqno);

		// 标记记账返回的卡号
		if (!CommonMethod.isNull(mzkret.cardno))
			salepay.payno = mzkret.cardno;

		// 该余额是查询时消费前的余额
		if (mzkret.ye > 0)
		{
			if (mzkreq.type.equals("01") || mzkreq.type.equals("04"))
				salepay.kye = mzkret.ye - mzkreq.je;

			if (mzkreq.type.equals("02") || mzkreq.type.equals("03"))
				salepay.kye += (mzkreq.je * -1);
		}

		salepay.kye = ManipulatePrecision.doubleConvert(salepay.kye);

		// 更新付款断点数据，标记为已付款状态,否则在记账以后如果掉电,断点读入的还是未记账状态
		if (this.saleBS != null)
			this.saleBS.writeBrokenData();
	}

	public double getAccountAllowPay()
	{
		if (SellType.ISSALE(saleBS.saletype))
		{
			if (this.allowpayje >= 0)
				return Math.min(this.allowpayje, mzkret.ye);
			else
				return mzkret.ye;
		}
		else
		{
			return 0.00;
		}
	}

	public String getDisplayCardno()
	{
		return mzkret.cardno;
	}

	public boolean realAccountPay()
	{
		// 会员付款即时记账
		if (mzkAccount(true))
			return true;

		return false;
	}
}
