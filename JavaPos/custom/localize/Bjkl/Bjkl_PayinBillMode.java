package custom.localize.Bjkl;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.PrintTemplate.PayinBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.PayinDetailDef;

public class Bjkl_PayinBillMode extends PayinBillMode
{

    protected String extendCase(PrintTemplateItem item, int index)
    {
		String line = null;

		try
		{
			    line = super.extendCase(item, index);
			    
				switch (Integer.parseInt(item.code))
				{
					// PBM_hjzs
					case 10: // 合计张数

						int countZS = 0;

						if (payListMode != null)
						{
							for (int i = 0; i < payListMode.size(); i++)
							{
								PayinDetailDef pdd = (PayinDetailDef) payListMode.get(i);
				                // 京客隆要求   15 - 会员零钱包  19 - 积分折扣  不统计
				                if (pdd.code.trim().equals("15") || pdd.code.trim().equals("19"))
				                {
				                	continue;
				                }
								countZS = countZS + pdd.zs;
							}

							line = String.valueOf(countZS);
						}
						else
						{
							line = "0";
						}
						// 打印空白缴款单
						if (Convert.toDouble(line.trim()) == 0 && phd != null && phd.seqno == -1)
							line = null;
						break;
				}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return line;
    }

}
