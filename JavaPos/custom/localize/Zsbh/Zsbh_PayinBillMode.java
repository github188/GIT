/**
 * 
 */
package custom.localize.Zsbh;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.PrintTemplate.PayinBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;

/**
 * @author wangyong
 * 缴款模板
 */
public class Zsbh_PayinBillMode extends PayinBillMode
{

	public void printBill()
	{
		for (int i = 0; i< GlobalInfo.sysPara.printjknum; i++)
		{
			// 设置打印方式
			printSetPage();
			
			// 打印头部区域
			printHeader();
			
	        // 打印明细区域
			printDetail();
			
			// 打印汇总区域
			printTotal();
			
	        // 打印尾部区域
			printBottom();
		}

		//打印时才走纸
		if (GlobalInfo.sysPara.printjknum > 0)
		{
	        // 切纸
	        printCutPaper();
		}
	}
	
	public String getItemDataString(PrintTemplateItem item, int index)
    {
        String line = null;

        try
        {
            line = extendCase(item,index);
            if (line == null)
            {        	
	            switch (Integer.parseInt(item.code))
	            {
	                
	
	                case 5: //缴款日期 + 时间
	
	                    if (phd.rqsj != null)
	                    {
	                        line =   phd.rqsj;//phd.jkrq
	                    }
	                    else if (phd.jkrq != null)
	                    {
	                        line = phd.jkrq;
	                    }
	                    else
	                    {
	                    	line = "";
	                    }
	
	                    break;
	
	                case 101: //空面值卡张数
		                    
	                	line = String.valueOf(((Zsbh_AccessDayDB)AccessDayDB.getDefault()).getMzkEmptyCount(phd.syyh));
	                    //line = "4";
	
	                    break;
	
	                case 102: //刷面值卡总张数
	                    
	                	line = String.valueOf(((Zsbh_AccessDayDB)AccessDayDB.getDefault()).getMzkAllCount(phd.syyh));
	                    //line = "13";
	
	                    break;
	
	                default:
	                	return super.getItemDataString( item,  index);
	               
	            }
            }
            
            if (line != null && Integer.parseInt(item.code) != 0 && item.text != null && !item.text.trim().equals(""))
            {
                //line = item.text + line;
            	int maxline = item.length - Convert.countLength(item.text);
            	line = item.text + Convert.appendStringSize("",line,0,maxline,maxline,item.alignment);
            }

            return line;
            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }
    
}
