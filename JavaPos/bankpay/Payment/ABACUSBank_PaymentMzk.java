package bankpay.Payment;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.PaymentMzkForm;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class ABACUSBank_PaymentMzk extends Bank_PaymentMzk
{
	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (SellType.ISBACK(salehead.djlb))
			{
				new MessageBox("退货时不能使用" + paymode.name);
				return null;
			}
			
			// 关闭IC卡
			GlobalInfo.isStartICCard = false;
			
			// 打开明细输入窗口
			new PaymentMzkForm().open(this,saleBS);
			
			
			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
        }
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			//	恢复IC卡
			GlobalInfo.isStartICCard = true;
		}
		
		return null;
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
			
			return true;
		}
	}
	
	protected boolean needFindAccount()
	{
		return false;
	}
	
	public boolean autoCreateAccount()
	{
		//findMzk("","","");
		return true;
	}
	
	public String getDisplayCardno()
	{
		return "";
	}
}
