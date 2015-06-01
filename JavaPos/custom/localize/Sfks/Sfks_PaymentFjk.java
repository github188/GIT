package custom.localize.Sfks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentFjk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Sfks_PaymentFjk extends PaymentFjk
{
	public Sfks_PaymentFjk()
	{
		super();
	}
	
	public Sfks_PaymentFjk(PayModeDef mode,SaleBS sale)
	{
		super(mode,sale);
	}
	
	public Sfks_PaymentFjk(SalePayDef pay,SaleHeadDef head)
	{
		super(pay,head);
	}
	
	public int getAccountInputMode()
	{
		return TextBox.MsrKeyInput;
	}

    public boolean setYeShow(Table table)
    {
    	// 计算最大允许收券金额
    	if (!calcFjkMaxJe()) return false;
    	
    	// 设置余额列表
		table.removeAll();
		
		String str [] = new String[3];
		TableItem item = null;
		str[0] = this.getAccountNameB();
		str[1] = this.getAccountYeB();
		str[2] = "0.00";
		item = new TableItem(table, SWT.NONE);
		item.setText(str);
		
		FJKYETYPE = "B";
		
		return true;
    }
    
	public boolean checkMzkMoneyValid()
	{
		boolean salepayok = true;
	
		// 券必须一次付完
		if (ManipulatePrecision.doubleCompare(salepay.ybje,Double.parseDouble(this.getAccountYeB()),2) != 0)
		{
			new MessageBox(salepay.payname + "的每张券必须一次性付完!");
			
			salepayok = false;
		}

		return salepayok;
	}
}
