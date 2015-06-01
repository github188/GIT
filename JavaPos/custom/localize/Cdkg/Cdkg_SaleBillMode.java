package custom.localize.Cdkg;

import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import custom.localize.Bstd.Bstd_SaleBillMode;

public class Cdkg_SaleBillMode extends Bstd_SaleBillMode
{  
    protected String extendCase(PrintTemplateItem item, int index)
    {
    	String line = null;
    	
    	switch (Integer.parseInt(item.code))
		{
			case SBM_fphm: // 小票号码
				line = salehead.str2;

				break;
		}
    	return line;
    }
}
