package custom.localize.Lrtd;

import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;

import custom.localize.Bstd.Bstd_SaleBillMode;

public class Lrtd_SaleBillMode extends Bstd_SaleBillMode
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
						if(salehead.str7==null||salehead.str7.trim().length() <= 0){
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
