package custom.localize.Jnyz;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

public class Jnyz_Printer extends Printer
{

	protected int numRow_Normal = 0; // 已打印行数
	protected int newpageRow_Normal = 1; // 每页打印行数
	protected int pageNum_Normal = 0;//页数
	boolean bool = false;
	
	public Jnyz_Printer(String name)
	{
		super(name);
	}
	
	
	protected void jumpTo_Normal(int rowNo)
	{
		newpageRow_Normal = pageRow_Normal;
		bool = true;
		if (pagePrint_Normal)
		{
			int PassRow = 0;

//			numRow_Normal++;
			if(numRow_Normal == pageRow_Normal)
			{
				printer.printLine_Normal("\n");
				printer.printLine_Normal("\n");
				printer.printLine_Normal("\n");
				printer.printLine_Normal("\n");
				printer.printLine_Normal("\n");
				printer.printLine_Normal("\n");
				printer.printLine_Normal("\n");
				
				//已打印行数设为0
				numRow_Normal = 0;

				// 页数加一
				pageNum_Normal++;
			}
			
		/*	// 如果要走纸的行小于当前打印行，则需要走纸到下一页
			if (rowNo < curRow_Normal)
			{
//				printer.printLine_Normal("接 下 页\n");
				
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
			}*/

		/*	// 计算从当前打印行到目标行的行数
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
			}*/
			
//			if (bool) printer.printLine_Normal("接 上 页\n");
		}
	}
	
	public void printLine_Normal(String printStr)
	{
		if (printStr == null)
			return;

		if (getStatus())
		{
			// 分页套打打印时，当前打印行不在打印区域内，走纸到下一页的打印区域
			if (pagePrint_Normal)
			{
				if ((curRow_Normal < areaStart_Normal) || (curRow_Normal > areaEnd_Normal))
				{
					jumpTo_Normal(areaStart_Normal);
					bool = true;
				}
			}

			// 分页且非套打时，在每一页上打印Memo
			if (isPrintPageHead == false && curRow_Normal == areaStart_Normal && pageNum_Normal > 1)
			{
				isPrintPageHead = true;
				try
				{
					SaleBillMode.getDefault().printPageHead();
				}
				catch (Exception ex)
				{
					PosLog.getLog(getClass()).debug(ex);
					ex.printStackTrace();
				}
				finally
				{
					isPrintPageHead = false;
				}

			}

			// 去掉尾部空格
			printStr = ManipulateStr.trimRight(printStr);

			// 添加尾部换行
			if (printStr.length() <= 0)
			{
				printStr += "\n";
			}
			else if (printStr.charAt(printStr.length() - 1) != '\n')
			{
				printStr += "\n";
			}

			// 打印到打印机
			printer.printLine_Normal(printStr);

			// 打印行计数
			curRow_Normal++;

			// 打印延迟
			numRow_Normal++;
			if (GlobalInfo.sysPara != null && GlobalInfo.sysPara.printdelayline > 0 && GlobalInfo.sysPara.printdelaysec > 0 && numRow_Normal >= GlobalInfo.sysPara.printdelayline)
			{
				numRow_Normal = 0;
				try
				{
					Thread.sleep(GlobalInfo.sysPara.printdelaysec);
				}
				catch (Exception e)
				{
					PosLog.getLog(getClass()).debug(e);
				}
			}
		}
	}
	

 }
