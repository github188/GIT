package com.efuture.javaPos.Payment;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class PaymentFjkPaper extends PaymentFjk
{
	public PaymentFjkPaper()
	{
	}
	
	public PaymentFjkPaper(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public PaymentFjkPaper(SalePayDef pay,SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	public int getAccountInputMode()
	{
		return TextBox.IntegerInput;
	}
	
	protected String getDisplayAccountInfo()
	{
		return Language.apply("请输入券");
	}
	
	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		boolean done = DataService.getDefault().sendFjkSale(req,ret);
		
		// 根据券号查找返回券的类型
		if (req.type.equals("05") && ret.ispw == 'N' && ret.cardpwd != null && ret.cardpwd.trim().length() > 0)
		{
			this.setAccountYeType(ret.cardpwd);
		}
		
		return done;
	}
    
	public boolean checkMzkMoneyValid()
	{
        if (!super.checkMzkMoneyValid())
        {
            return false;
        }
        
		// 券必须一次付完
		if (mzkreq.memo.equals(FJK_A) && ManipulatePrecision.doubleCompare(salepay.ybje,Double.parseDouble(this.getAccountYeA()),2) != 0)
		{
//			if (new MessageBox(salepay.payname + "的每张券必须一次性付完!\n是否将剩余部分计入损溢？",null,true).verify() == GlobalVar.Key1)
			if (new MessageBox(Language.apply("{0}的每张券必须一次性付完!\n是否将剩余部分计入损溢？" ,new Object[]{salepay.payname}),null,true).verify() == GlobalVar.Key1)
			{
				// num1记录券付款溢余部分
				//salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(Double.parseDouble(this.getAccountYeA()), salepay.hl), ManipulatePrecision.mul(salepay.ybje, salepay.hl));
				salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(Double.parseDouble(this.getAccountYeA()), salepay.hl), Math.min(salepay.je,fjkAMaxJe));
				salepay.ybje = Double.parseDouble(this.getAccountYeA());
				salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2,1);
				return true;
			}
			else
			{
				return false;
			}
		}

		if (mzkreq.memo.equals(FJK_B) && ManipulatePrecision.doubleCompare(salepay.ybje,Double.parseDouble(this.getAccountYeB()),2) != 0)
		{
//			if (new MessageBox(salepay.payname + "的每张券必须一次性付完!\n是否将剩余部分计入损溢？",null,true).verify() == GlobalVar.Key1)
			if (new MessageBox(Language.apply("{0}的每张券必须一次性付完!\n是否将剩余部分计入损溢？" ,new Object[]{salepay.payname}),null,true).verify() == GlobalVar.Key1)
			{
				// num1记录券付款溢余部分
				//salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(Double.parseDouble(this.getAccountYeB()), salepay.hl), ManipulatePrecision.mul(salepay.ybje, salepay.hl));
				salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(Double.parseDouble(this.getAccountYeB()), salepay.hl), Math.min(salepay.je,fjkBMaxJe));
				salepay.ybje = Double.parseDouble(this.getAccountYeB());
				salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2,1);
				return true;
			}
			else
			{
				return false;
			}
		}

		if (mzkreq.memo.equals(FJK_F) && ManipulatePrecision.doubleCompare(salepay.ybje,Double.parseDouble(this.getAccountYeF()),2) != 0)
		{
//			if (new MessageBox(salepay.payname + "的每张券必须一次性付完!\n是否将剩余部分计入损溢？",null,true).verify() == GlobalVar.Key1)
			if (new MessageBox(Language.apply("{0}的每张券必须一次性付完!\n是否将剩余部分计入损溢？" ,new Object[]{salepay.payname}),null,true).verify() == GlobalVar.Key1)
			{
				// num1记录券付款溢余部分
				//salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(Double.parseDouble(this.getAccountYeF()), salepay.hl), ManipulatePrecision.mul(salepay.ybje, salepay.hl));
				salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(Double.parseDouble(this.getAccountYeF()), salepay.hl), Math.min(salepay.je,fjkFMaxJe));
				salepay.ybje = Double.parseDouble(this.getAccountYeF());
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