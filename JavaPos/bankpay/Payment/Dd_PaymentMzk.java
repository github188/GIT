package bankpay.Payment;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Dd_PaymentMzk  extends Shop_PaymentMzk{

	 public Dd_PaymentMzk()
	    {
		 messDisplay = false;
	    }

	    public Dd_PaymentMzk(PayModeDef mode, SaleBS sale)
	    {
	    	messDisplay = false;
	    	
	    	initPayment(mode, sale);
	    }

	    public Dd_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	    {
	    	messDisplay = false;
	    	
	    	initPayment(pay, head);
	    }
	    
		protected String getDisplayAccountInfo()
		{
			return "输入卡号";
		}
	    
	    public boolean checkMzkMoneyValid()
	    {
	        if (!super.checkMzkMoneyValid())
	        {
	            return false;
	        }

	        // 券必须一次付完
	        if (ManipulatePrecision.doubleCompare(salepay.ybje,this.getAccountYe(),2) != 0)
	        {
				if (new MessageBox(salepay.payname + "的每张券必须一次性付完!\n是否继续进行？",null,true).verify() == GlobalVar.Key1)
				{
					// num1记录券付款溢余部分
					//salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(Double.parseDouble(this.getAccountYe()), salepay.hl), ManipulatePrecision.mul(salepay.ybje, salepay.hl));
					salepay.ybje = this.getAccountYe();
					salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2,1);
					return true;
				}
				else
				{
					return false;
				}
	        }

	        return true;
	    }
	    
		// 自动计算付款金额,并生成付款方式
		public boolean AutoCalcMoney()
		{
			return true;
		}
}
