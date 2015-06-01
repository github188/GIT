package bankpay.Payment;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bank_PaymentMzk extends PaymentMzk
{
	PaymentBankFunc pbfunc = null;
	
	public Bank_PaymentMzk()
	{
		super();
	}
	
	public Bank_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public Bank_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	public void initPayment(PayModeDef mode,SaleBS sale)
	{
		super.initPayment(mode, sale);
		
		pbfunc = CreatePayment.getDefault().getPaymentBankFunc(mode.code);
		pbfunc.setErrorMsgShowMode(true);
		pbfunc.allowSaleBySelf(false);
	}
	
	public void initPayment(SalePayDef pay,SaleHeadDef head)
	{
		super.initPayment(pay, head);
		
		pbfunc = CreatePayment.getDefault().getPaymentBankFunc(pay.paycode);
		pbfunc.setErrorMsgShowMode(true);
		pbfunc.allowSaleBySelf(false);
	}
	
	public boolean cancelPay()
	{
		// 金卡工程总是即时记账
		ProgressBox box =  null;
		try{
			box = new ProgressBox();
			box.setText("删除付款中，请等待.....");
			boolean  ret =  mzkAccount(false);
			return ret;
		}
		catch(Exception er)
		{
			return false;
		}
		finally
		{
			if (box != null)
				box.close();
		}
	}
	
	public boolean realAccountPay()
	{
		// 金卡工程总是即时记账			
		return mzkAccount(true);
	}
	
	public boolean collectAccountPay()
	{
		// batch为空表示付款没有记账，进行集中记账
		if (salepay.batch == null || salepay.batch.trim().length() <= 0)
		{
			// 红冲等交易时才会进入集中记账,提示重新刷卡得到赤道号
			mzkreq.track1 = null;
			mzkreq.track2 = null;
			mzkreq.track3 = null;
			
			StringBuffer cardno = new StringBuffer();
			TextBox txt = new TextBox();
			if (!txt.open("请刷"+paymode.name, paymode.name, "请将" + paymode.name + "从刷卡槽刷入", cardno, 0, 0, false, getAccountInputMode())) 
			{
				return false;
			}
			
			mzkreq.track1 = txt.Track1;
			mzkreq.track2 = txt.Track2;
			mzkreq.track3 = txt.Track3;
			
			return mzkAccount(true);
		}
		else
		{
			// 已记账,直接返回
			return true;
		}
	}
	
	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		// 设置请求数据
		setRequestDataByFind(track1,track2,track3);
		
		// 调用银联接口
		boolean ret = pbfunc.callBankFunc(PaymentBank.XYKYE, 0, track1, track2, track3, null, null, null, null);
		if (!ret)
		{
			// 如果是显示ERROR消息模式，则弹出错误信息
			if (pbfunc.getErrorMsgShowMode())
			{
				new MessageBox(pbfunc.getErrorMsg());
			}
			
			return ret;
		}
		else
		{
			// 银联接口返回余额
			BankLogDef bld = pbfunc.getBankLog();
			mzkret.cardno = bld.cardno;
			mzkret.ye = (bld.kye > 0)?bld.kye:0;
			//salepay.kye = mzkret.ye;
			return true;
		}
	}
	
	public boolean findMzk(String track1, String track2, String track3)
	{
		return findMzkInfo(track1,track2,track3);
	}
	
	public boolean mzkAccount(boolean isAccount)
	{
		// 设置交易类型,isAccount=true是记账,false是撤销
		String oldseqno = null;
		int type = PaymentBank.XYKXF;
		if (isAccount)
		{
			if (SellType.SELLSIGN(salehead.djlb) > 0) type = PaymentBank.XYKXF;
			else type = SellType.ISHC(salehead.djlb)?PaymentBank.XYKCX:PaymentBank.XYKTH;
		}
		else
		{
			if (SellType.SELLSIGN(salehead.djlb) > 0) type = PaymentBank.XYKCX;
			else type = PaymentBank.XYKXF;
			
			// str1为撤销的原流水
			oldseqno = salepay.str1;
		}
		
		// 调用银联接口
		boolean ret = pbfunc.callBankFunc(type, salepay.ybje, mzkreq.track1, mzkreq.track2, mzkreq.track3, oldseqno, null, null, null);
		if (!ret)
		{
			// 如果是显示ERROR消息模式，则弹出错误信息
			if (pbfunc.getErrorMsgShowMode())
			{
				new MessageBox(pbfunc.getErrorMsg());
			}
			
			return ret;
		}
		else
		{
			// 标记记账成功,卡内交易后余额记账
			BankLogDef bld = pbfunc.getBankLog();
			mzkret.cardno = bld.cardno;
			mzkret.ye     = (bld.memo != null && bld.memo.length() > 0)?Convert.toDouble(bld.memo):0;
			
			salepay.payno = bld.cardno;
			salepay.batch = String.valueOf(bld.trace);
			salepay.str1  = salepay.batch;
			salepay.idno  = salepay.batch;
			salepay.ybje  = bld.je;
			salepay.je    = ManipulatePrecision.doubleConvert(salepay.ybje * ((paymode!=null)?paymode.hl:1));
			if (salepay.kye <= 0 || mzkret.ye > 0) salepay.kye = mzkret.ye;
	    	
			salepay.kye -= salepay.ybje;
	    	salepay.kye   = ManipulatePrecision.doubleConvert(salepay.kye);
			
			// 更新付款断点数据，标记为已付款状态,否则在记账以后如果掉电,断点读入的还是未记账状态
			if (this.saleBS != null) this.saleBS.writeBrokenData();
		
			return true;
		}
	}
	
	public boolean allowMzkOffline()
	{
		return true;
	}
	
	public String getInitMessage()
	{
		if (this.saleBS != null)
		{
			int num = 0;
			double zje = 0;
			for (int i = 0 ; i < saleBS.salePayment.size(); i++)
			{
				SalePayDef spd = (SalePayDef) saleBS.salePayment.elementAt(i);
				if (spd.paycode.equals( paymode.code))
				{
					num++;
					zje = ManipulatePrecision.doubleConvert(spd.je + zje);
				}
			}
			if (num > 0)
				return "总数量:"+num+"  "+"总金额:"+ManipulatePrecision.doubleToString(zje);
		}
		return "";
	}
	
	public boolean getExtRun()
	{
		// TODO Auto-generated method stub
		return true;
	}
}
