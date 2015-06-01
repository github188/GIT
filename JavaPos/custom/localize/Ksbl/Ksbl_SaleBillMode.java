package custom.localize.Ksbl;

import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhcm.Bhcm_SaleBillMode;

public class Ksbl_SaleBillMode extends Bhcm_SaleBillMode {
	protected final static int SBM_custItem1 = 201;//银联折扣金额
	protected final static int SBM_custItem2 = 202;//实际刷卡金额
	
	protected String extendCase(PrintTemplateItem item, int index)
    {
        String line = null;
        
        
        switch (Integer.parseInt(item.code))
        {
        	
        	case SBM_custItem1:
        		if(!((SalePayDef) salepay.elementAt(index)).paycode.substring(0, 2).equals("03"))break;
        		double zkje = ((SalePayDef) salepay.elementAt(index)).num3;
        		if(zkje>0){
        			line = "银行折扣:"+String.valueOf(zkje);
        		}
        		
    		break;
        	case SBM_custItem2:
        		if(!((SalePayDef) salepay.elementAt(index)).paycode.substring(0, 2).equals("03"))break;
        		double zkje1 = ((SalePayDef) salepay.elementAt(index)).num3;
        		if(zkje1>0){
        			line = "实际刷卡金额:"+String.valueOf(((SalePayDef) salepay.elementAt(index)).je-zkje1);
        		}
        		
    		break;
        }
        
        return line;
    }

}
