package custom.localize.Bcsf;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Device.Printer;

public class Bcsf_Printer extends Printer
{

	public Bcsf_Printer(String name)
	{
		super(name);
	}
	
	protected void jumpTo_Normal(int rowNo)
	{
		boolean bool = false;
		
		if (pagePrint_Normal)
		{
			int PassRow = 0;
			
			// 如果要走纸的行小于当前打印行，则需要走纸到下一页
			if (rowNo < curRow_Normal)
			{
				printer.printLine_Normal("接 下 页\n");
				
				// 走纸到下页，适用于黑标走纸，否则打印空行走纸到下一页
				bool = printer.passPage_Normal();
				if (bool)
				{
					// 重新开始记行数
					curRow_Normal = 1;

					// 页数加一
					pageNum_Normal++;

					// 发票页加1
					if (haveSaleFphmCfg())
					{
						int n = Convert.toInt(getSaleFphmAttr(InvoiceNum)) + 1;
						setSaleFphmAttr(InvoiceNum, String.valueOf(n));
					}

					for (int i = 0; i < jumpLine_Normal; i++)
					{
						printer.printLine_Normal("\n");
					}
				}
			}

			// 计算从当前打印行到目标行的行数
			if (rowNo < curRow_Normal)
			{
				PassRow = pageRow_Normal - curRow_Normal + rowNo;

				// 页数加一
				pageNum_Normal++;

				// 发票页加1
				if (haveSaleFphmCfg())
				{
					int n = Convert.toInt(getSaleFphmAttr(InvoiceNum)) + 1;
					setSaleFphmAttr(InvoiceNum, String.valueOf(n));
				}	
			}
			else
			{
				PassRow = rowNo - curRow_Normal + 1;//add 1
			}

			// 打印空行走纸到目标行
			for (int i = 0; i < PassRow; i++)
			{
				printer.printLine_Normal("\n");
				curRow_Normal++;
			}

			// 标记当前行以页为起始
			while (curRow_Normal > pageRow_Normal)
			{
				curRow_Normal = curRow_Normal - pageRow_Normal;
			}
			
			if (bool) printer.printLine_Normal("接 上 页\n");
		}
	}
}
