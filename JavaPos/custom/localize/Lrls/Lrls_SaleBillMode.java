package custom.localize.Lrls;

import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;

import custom.localize.Cmls.Cmls_SaleBillMode;

public class Lrls_SaleBillMode extends Cmls_SaleBillMode
{

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = super.extendCase(item, index);
		if(line == null){
			switch (Integer.parseInt(item.code))
			{
				case SBM_hykh: // 会员卡号

					if ((salehead.hykh == null) || (salehead.hykh.length() <= 0))
					{
						line = null;
					}
					else
					{
						if(salehead.str7.equals("")||salehead.str7==null){
							line = salehead.hykh;
						}else{
							line = salehead.str7.substring(0,3)+"****"+salehead.str7.substring(7,11);
						}
						
					}

					break;
			}
		}
		return line;
		
	}

}
