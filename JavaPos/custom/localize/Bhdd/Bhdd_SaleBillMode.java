package custom.localize.Bhdd;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;

import custom.localize.Bhls.Bhls_SaleBillMode;


public class Bhdd_SaleBillMode extends Bhls_SaleBillMode
{
    protected final int SBM_ljjf = 101; //累计积分
    
    protected String extendCase(PrintTemplateItem item, int index)
    {
        String line = null;

        switch (Integer.parseInt(item.code))
        {
            case SBM_ljjf:	//累计积分
            	if (salehead.ljjf == 0)
            		line = null;
            	else
            		line = ManipulatePrecision.doubleToString(salehead.ljjf);
            	break;
        }

        return line;
    }
}
