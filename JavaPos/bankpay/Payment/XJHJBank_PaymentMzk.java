package bankpay.Payment;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Struct.BankLogDef;

public class XJHJBank_PaymentMzk extends Bank_PaymentMzk
{
	
	public boolean mzkAccount(boolean isAccount)
	{
		// 设置交易类型,isAccount=true是记账,false是撤销
		String oldseqno = null;
		int type = PaymentBank.XYKXF;
		if (isAccount)
		{
			if (SellType.SELLSIGN(salehead.djlb) > 0) type = PaymentBank.XYKXF;
			else type = SellType.ISHC(salehead.djlb)?PaymentBank.XYKTH:PaymentBank.XYKTH;
		}
		else
		{
			if (SellType.SELLSIGN(salehead.djlb) > 0) type = PaymentBank.XYKTH;
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
			mzkret.cardname = bld.tempstr;
//			new MessageBox(mzkret.cardno+"+"+mzkret.ye+"+"+mzkret.cardname);
			return true;
		}
	}

}
