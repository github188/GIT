package com.efuture.javaPos.Payment;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;


public class PaymentMzkPaper extends PaymentMzk
{
    public PaymentMzkPaper()
    {
    }

    public PaymentMzkPaper(PayModeDef mode, SaleBS sale)
    {
    	initPayment(mode, sale);
    }

    public PaymentMzkPaper(SalePayDef pay, SaleHeadDef head)
    {
    	initPayment(pay, head);
    }
    
	protected String getDisplayAccountInfo()
	{
		return Language.apply("输入券号");
	}

    public int getAccountInputMode()
    {
        return TextBox.IntegerInput;		        //允许键盘和刷卡输入	
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
//			if (new MessageBox(salepay.payname + "的每张券必须一次性付完!\n是否将剩余部分计入损溢？",null,true).verify() == GlobalVar.Key1)
			if (new MessageBox(Language.apply("{0}的每张券必须一次性付完!\n是否将剩余部分计入损溢？" ,new Object[]{salepay.payname}),null,true).verify() == GlobalVar.Key1)
			{
				// num1记录券付款溢余部分
				//salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(Double.parseDouble(this.getAccountYe()), salepay.hl), ManipulatePrecision.mul(salepay.ybje, salepay.hl));
				salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(this.getAccountYe(), salepay.hl), Math.min(salepay.je,this.saleBS.calcPayBalance()));
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
}
