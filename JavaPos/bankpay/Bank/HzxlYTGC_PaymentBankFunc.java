package bankpay.Bank;

import com.efuture.javaPos.Payment.PaymentBank;


public class HzxlYTGC_PaymentBankFunc extends HzxlDzbh_PaymentBankFunc
{
	 public boolean getFuncLabel(int type,String[] grpLabelStr)
	  {
		  //0-4对应FORM中的5个输入框
		  //null表示该不用输入
		  switch(type)
		  {
				case PaymentBank.XYKXF://消费
					grpLabelStr[0] = null;
					grpLabelStr[1] = null;
					grpLabelStr[2] = null;
					grpLabelStr[3] = null;
					grpLabelStr[4] = "交易金额";
					break;
				case PaymentBank.XYKCX://消费撤销
					grpLabelStr[0] = "原流水号";
					grpLabelStr[1] = null;
					grpLabelStr[2] = null;
					grpLabelStr[3] = null;
					grpLabelStr[4] = "交易金额";
					break;
				case PaymentBank.XYKTH://隔日退货   
					grpLabelStr[0] = "原流水号";
					grpLabelStr[1] = "原终端号";
					grpLabelStr[2] = "原交易日";
					grpLabelStr[3] = null;
					grpLabelStr[4] = "交易金额";
					break;				
				case PaymentBank.XYKYE://余额查询    
					grpLabelStr[0] = null;
					grpLabelStr[1] = null;
					grpLabelStr[2] = null;
					grpLabelStr[3] = null;
					grpLabelStr[4] = "余额查询";
					break;           				
				case PaymentBank.XYKCD://签购单重打
					grpLabelStr[0] = "原流水号";
					grpLabelStr[1] = null;
					grpLabelStr[2] = null;
					grpLabelStr[3] = null;
					grpLabelStr[4] = "重打签单";
					break;
				default:
                   return false;
			}
			
			return true;
	  }
}
