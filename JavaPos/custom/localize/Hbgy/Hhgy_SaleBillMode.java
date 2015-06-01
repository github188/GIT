package custom.localize.Hbgy;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;

import custom.localize.Bstd.Bstd_SaleBillMode;

public class Hhgy_SaleBillMode extends Bstd_SaleBillMode
{
	protected final static int SBM_GWLINFO = 203;
	protected final static int SBM_CARDINFO = 204;
	protected final static int SBM_TOTALJF = 205;
	protected final static int SBM_HMCARD = 206;
	protected final static int SBM_REBATEINFO = 207;

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = super.extendCase(item, index);

		if (line != null)
			return line;

		switch (Integer.parseInt(item.code))
		{
			case SBM_CARDINFO:
				line = salehead.str9;
				break;
			case SBM_GWLINFO:
				line = salehead.str10;
				break;
			case SBM_TOTALJF:
				line = ManipulatePrecision.doubleToString(salehead.num8, 2, 1);
				break;
			case SBM_HMCARD:
				line = salehead.str3 + salehead.str4;
				break;
			case SBM_REBATEINFO:
				line = "打折总金额:"+salehead.num9+" 折扣率:" + (salehead.num10 * 100) +"%";
				break;
		}
		return line;
	}
}
