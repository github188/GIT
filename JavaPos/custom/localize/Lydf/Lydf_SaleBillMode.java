package custom.localize.Lydf;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;

import custom.localize.Bstd.Bstd_SaleBillMode;

public class Lydf_SaleBillMode extends Bstd_SaleBillMode
{
	protected final static int SBM_PH = 203;
	protected final static int SBM_SK = 204;
	protected final static int SBM_TAXRQ = 205;
	protected final static int SBM_TAXMEMO = 206;

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;

		switch (Integer.parseInt(item.code))
		{
			case SBM_salefphm:
			case SBM_PH:
				if (salehead.salefphm != null && salehead.salefphm.length() > 0)
					line = salehead.salefphm;

				if (SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb))
				{
					if (salehead.str3 != null && salehead.str3.length() > 0 && salehead.str3.indexOf("#") == -1)
						line = salehead.str3;
				}
				break;
			case SBM_SK:
				if (salehead.str3 != null && salehead.str3.length() > 0 && salehead.str3.indexOf("#") > -1)
				{
					String[] ary = salehead.str3.split("#");
					if (ary != null && ary.length > 0)
						line = ary[0];
				}

				break;
			case SBM_TAXRQ:
				if (salehead.str3 != null && salehead.str3.length() > 0 && salehead.str3.indexOf("#") > -1)
				{
					String[] ary = salehead.str3.split("#");
					if (ary != null && ary.length > 1)
						line = ary[1];
				}

				break;
			case SBM_TAXMEMO:
				if (salehead.str4 != null && salehead.str4.length() > 0)
				{
					if (GlobalInfo.sysPara.taxfailedtip.length() > 0)
						line = GlobalInfo.sysPara.taxfailedtip;
					else
						line = salehead.str4;
				}
				break;
		}

		return line;
	}
}
