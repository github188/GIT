/**
 * 
 */
package custom.localize.Zsbh;

import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;

/**
 * @author wangyong
 *
 */
public class Zsbh_Printer extends Printer
{

/*	public void setPagePrint_Normal(boolean flag, int row)
	{
		// 是否分页打印
		pagePrint_Normal = flag;

		// 每页行数
		pageRow_Normal = row;
		if (pageRow_Normal <= 0) pageRow_Normal = 1;

		// 缺省打印区域为整页
		areaStart_Normal = 1;
		areaEnd_Normal = pageRow_Normal;
	}*/
	
	public void startPrint_Normal()
	{
		//
		//numRow_Normal = 0;
		//curRow_Normal = 1;
		//pageNum_Normal = 1;
		
		//setPagePrint_Normal(false, 1);

		
		if (ConfigClass.RepPrintTrack != 3)
		{;
			pagePrint_Normal = true;
		}
		else
		{			
			numRow_Normal = 0;
			curRow_Normal = 1;
			pageNum_Normal = 1;
			
			setPagePrint_Normal(false, 1);		
		}
		
	}

	public Zsbh_Printer(String name)
	{		
		super(name);
	}
	
	//获取打印区域结束行
	public int getAreaEnd_Slip()
	{		
		return areaEnd_Slip;
	}

	public boolean getPagePrint_Slip()
	{
		return pagePrint_Slip;
	}
	
	protected void jumpTo_Slip(int rowNo)
	{
		if (pagePrint_Slip)
		{
			int PassRow = 0;

			// 如果要走纸的行小于当前打印行，则需要走纸到下一页
			if (rowNo < curRow_Slip)
			{
				// 走纸到下页，适用于黑标走纸，否则打印空行走纸到下一页
				if (printer.passPage_Slip())
				{
					// 重新开始记行数
					curRow_Slip = 1;

					// 页数加一
					pageNum_Slip++;
					
					for (int i = 0 ; i < jumpLine_Slip; i++)
					{
						printer.printLine_Normal("\n");
					}
				}
			}

			// 计算从当前打印行到目标行的行数
			if (rowNo < curRow_Slip)
			{
				PassRow = pageRow_Slip - curRow_Slip + rowNo;

				// 页数加一
				pageNum_Slip++;
			}
			else
			{
				PassRow = rowNo - curRow_Slip;
			}

			// 打印空行走纸到目标行
			for (int i = 0; i < PassRow; i++)
			{
				//printer.printLine_Slip("\n");
				curRow_Slip++;
			}

			// 标记当前行以页为起始
			while (curRow_Slip > pageRow_Slip)
			{
				curRow_Slip = curRow_Slip - pageRow_Slip;
			}
		}
	}
	
}
