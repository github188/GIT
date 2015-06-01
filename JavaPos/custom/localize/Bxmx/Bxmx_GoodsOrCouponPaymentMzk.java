package custom.localize.Bxmx;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bxmx_GoodsOrCouponPaymentMzk extends PaymentMzk
{
	public Bxmx_GoodsOrCouponPaymentMzk()
	{
		super();
	}

	public Bxmx_GoodsOrCouponPaymentMzk(PayModeDef mode, SaleBS sale)
	{
		super(mode, sale);
	}

	public Bxmx_GoodsOrCouponPaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		super(pay, head);
	}

	protected boolean needFindAccount()
	{
		if (((Bxmx_SaleBS) saleBS).isgoodscoupon)
			return false;

		return true;
	}

	public boolean createSalePay(String money)
	{
		if (((Bxmx_SaleBS) saleBS).isgoodscoupon && Convert.toDouble(money) != salehead.ysje)
		{
			new MessageBox("提货券付款不允许修改金额");
			return false;
		}
		return super.createSalePay(money);
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
		if (((Bxmx_SaleBS) saleBS).isgoodscoupon)
		{
			mzkreq.track3 = "A"; // A是暗码
			mzkreq.je = ((Bxmx_SaleBS) saleBS).goodscouponye;

		}
		else
		{
			mzkreq.track3 = "B"; // B是明码
			mzkreq.je = salepay.ybje;
		}

		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.paycode = salepay.paycode;
		mzkreq.invdjlb = ((salehead != null) ? salehead.djlb : "");

		// 告诉后台过程磁道信息是存放的是卡号,只采用卡号记账方式,不使用磁道记账方式
		mzkreq.track1 = "CARDNO";
		mzkreq.track2 = salepay.payno;

		return true;
	}

	public boolean autoCreateAccount()
	{
		mzkret.cardno = ((Bxmx_SaleBS) saleBS).goodscouponcode;
		mzkret.ye = salehead.ysje;//

		return true;
	}

	public boolean checkMzkMoneyValid()
	{
		if (!super.checkMzkMoneyValid())
			return false;

		// 券必须一次付完
		if (ManipulatePrecision.doubleCompare(salepay.ybje, this.getAccountYe(), 2) != 0)
		{
			new MessageBox("提货券必须一次性付完!\n剩余部分计入损溢");

			salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(this.getAccountYe(), salepay.hl), Math.min(salepay.je, this.saleBS.calcPayBalance()));
			salepay.ybje = this.getAccountYe();
			salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2, 1);
			return true;
		}
		return true;
	}
	
	public void showAccountYeMsg()
	{
/*		if(!messDisplay) return;
		
	    StringBuffer info = new StringBuffer();
	    
	    String text = "付";
	    double ye = getAccountYe() - salepay.je;
	    if (checkMzkIsBackMoney())
	    {
	    	text = "退";
	    	ye = getAccountYe() + salepay.je;
	    }	   
	    info.append("卡内余额为: " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(getAccountYe()),0,12,12,1) + "\n");
	    info.append("本次"+text+"款额: " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(salepay.je),0,12,12,1) + "\n");	    
	    if (ye > 0)info.append(text+"款后余额: " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(ye),0,12,12,1) + "\n");
	    
	    new MessageBox(info.toString());*/
	}
}
