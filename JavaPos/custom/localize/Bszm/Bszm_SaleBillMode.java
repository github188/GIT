package custom.localize.Bszm;

import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.SaleGoodsDef;

public class Bszm_SaleBillMode extends SaleBillMode
{
	protected final static int SBM_Attr1 = 202;
	protected final static int SBM_Attr2 = 203;
	protected final static int SBM_Attr3 = 204;
	protected final static int SBM_Attr4 = 205;
	protected final static int SBM_Attr5 = 206;
	protected final static int SBM_Attr6 = 207;
	protected final static int SBM_Attr7 = 208;
	protected final static int SBM_Attr8 = 209;

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;
		
		switch (Integer.parseInt(item.code))
		{
			case SBM_Attr1:
				line = ((SaleGoodsDef) salegoods.elementAt(index)).str6;			
				break;
				
			case SBM_Attr2:
				line = ((SaleGoodsDef) salegoods.elementAt(index)).str7;		
				break;
				//专卖采用1块钱一个积分的方式统计积分
			case SBM_Attr3:
				break;
			case SBM_Attr4:
				break;
			case SBM_Attr5:
				break;
			case SBM_Attr6:
				break;
			case SBM_Attr7:
				break;
			case SBM_Attr8:
				break;
		}

		return line;
	}
}
