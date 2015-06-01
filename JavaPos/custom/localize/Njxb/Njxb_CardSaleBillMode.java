package custom.localize.Njxb;

import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.SalePayDef;

public class Njxb_CardSaleBillMode extends CardSaleBillMode
{
	protected final static int CSBM_valid_date = 107;// 金鹰卡有效期

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;
		if (Integer.parseInt(item.code) == CSBM_valid_date)
		{
			line = ((SalePayDef) originalsalepay.get(index)).str2;
		}
		return line;
	}
}
