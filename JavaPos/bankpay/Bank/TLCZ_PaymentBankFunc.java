package bankpay.Bank;

import com.efuture.javaPos.Payment.PaymentBank;

//万商通联预付卡(中免三亚）
public class TLCZ_PaymentBankFunc extends TLBank_PaymentBankFunc
{
	public String[] getFuncItem()
    {
        String[] func = new String[8];
        
        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "退货";
        func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
        func[4] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
        func[5] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[6] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
        func[7] = "[" + PaymentBank.XKQT1 + "]" + "重打结算单";
        
        return func;
    } 
	
	public String getBankType()
	{
		return "02";
	}
}
