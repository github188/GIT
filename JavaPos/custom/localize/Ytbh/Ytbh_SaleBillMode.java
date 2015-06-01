package custom.localize.Ytbh;

import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Cmls.Cmls_SaleBillMode;


public class Ytbh_SaleBillMode extends Cmls_SaleBillMode
{
	protected final static int SBM_jfzjh = 301;//str3
	protected final static int SBM_jfpc = 302;//str4
	protected final static int SBM_xfzjh = 303;//str8
	protected final static int SBM_xfpc = 304;//str9
	protected final static int SBM_bctd = 305;//num1
	protected final static int SBM_ljtd = 306;//num2

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;
		SalePayDef spd = null;

		switch (Integer.parseInt(item.code))
		{
		case SBM_jfzjh:
			if(salehead.str3!="" && salehead.str3!=null) 
				line = "主机流水号: " + salehead.str3;
			break;
			
		case SBM_jfpc:
			if(salehead.str4!="" && salehead.str4!=null)
				line = "批 次 号: "+ salehead.str4;
			break;
		case SBM_xfzjh:
			spd=(SalePayDef)salepay.get(index);
			if(!"".equals(spd.str2.trim()))
				line = "主机流水号: "+spd.str2;
			break;
		case SBM_xfpc:
			spd=(SalePayDef)salepay.get(index);
			if(spd.str4!=null && spd.str4.trim().length()>0)
				line = "批 次 号: "+spd.str4;
			break;
		case SBM_bctd:
			if((salehead.num1!=0)||(salehead.num2!=0))
		        line ="本次积泰豆:  "+ salehead.num1;
		    break;
		case SBM_ljtd:
			if((salehead.num2!=0)||(salehead.num1!=0))
	            line ="累计积泰豆:  "+ salehead.num2;
	    
		}

		return line;
	}
}
