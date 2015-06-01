package custom.localize.Zmsy;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.PrintTemplate.PayinBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;

public class Zmsy_PayinBillMode extends PayinBillMode
{
	protected final int PBM_reprint = 14;//重印标志

	public void printHeader()
	{
		// 设置打印区域
		setPrintArea("Header");
		
		/*if (phd.reprint != null && phd.reprint.equals("Y"))
		{
			printLine(Convert.appendStringSize("","**重打印**", 1, 38, 38,2));
		}*/
		// 打印
		printVector(getCollectDataString(Header,-1,Width));
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
	                
	
	                case PBM_reprint: //重印标志
	
	                	if (phd.reprint != null && phd.reprint.equals("Y"))
	            		{
	                		line = "**重印**";	            			
	            		}
	                	else
	                	{
	                		line = null;
	                	}
	
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
