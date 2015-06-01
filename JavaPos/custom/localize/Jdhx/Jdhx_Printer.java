package custom.localize.Jdhx;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

public class Jdhx_Printer extends Printer
{
	protected boolean isPrintTotal = true;

	public Jdhx_Printer(String name)
	{
		super(name);
	}
	
	
	public void printLine_Normal(String printStr)
	{
		if (printStr == null)
			return;

		if (connect && enable)
		{

			//开始打印 汇总， isPrintTotal = false;
			if (isPrintTotal && SaleBillMode.getDefault().PrintEnd 
					&& curRow_Normal == (SaleBillMode.getDefault().getTotal()) )
					{
				          isPrintTotal = false;
					}
			
			
			// 跳页放在最开始检查。 
			// 1.分页打印每次打印完页头和页尾 就检查一次，否则打印完页尾 多打印一行
			// 2.放到打印 
			// 分页套打打印时，当前打印行不在打印区域内，走纸到下一页的打印区域
			
			// 当明细区打印完PrintEnd = true，当前行刚好等于curRow_Normal = SaleBillMode.getDefault().getPageBottom()， 
			// 那么在打印完 每页页尾时，满足 curRow_Normal > (SaleBillMode.getDefault().getTotal()),导致在下 一页页头打印页尾
			// 这时如果提前跳到下一页，就不会 满足 curRow_Normal > (SaleBillMode.getDefault().getTotal())
			if (pagePrint_Normal)
			{
				if ((curRow_Normal < areaStart_Normal) || (curRow_Normal > areaEnd_Normal))
				{
					jumpTo_Normal(areaStart_Normal);
				}
			}
			
			//PrintEnd = true 说明小票明细已经打印完，准备打印小票汇总, 
			//1.当在页尾打印 汇总不够时，跳至打印每页页尾位置，然后在当前页继续打印 每页页尾，
			//2. isPrintTotal = false ,说明开始打印汇总， 此时不判断
			if (isPrintPageHead == false && isPrintTotal 
					&& SaleBillMode.getDefault().PrintEnd 
					&& curRow_Normal > (SaleBillMode.getDefault().getTotal()) )
			{
				//当前行未到打印页尾的位置，则跳到打印页尾的位置，否则不跳
				if (curRow_Normal < SaleBillMode.getDefault().getPageBottom())
				{
					jumpTo_Normal(SaleBillMode.getDefault().getPageBottom() - 1);
				}
					
				isPrintPageHead = true;
				
				isPrintTotal = false;
				try
				{
					SaleBillMode.getDefault().printPageBottom();
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

			
			
			// 跳页放在最开始检查。 
			// 1.分页打印每次打印完页头和页尾 就检查一次，否则打印完页尾 多打印一行
			// 2.放到打印 
			// 分页套打打印时，当前打印行不在打印区域内，走纸到下一页的打印区域
			
			// 当明细区打印完PrintEnd = true, curRow_Normal > (SaleBillMode.getDefault().getTotal()
			// 此时打印完每页也尾时，如果不接着跳到下一页，将会将 getTotal() 第一行内容打印在当前页页尾 
			if (pagePrint_Normal)
			{
				if ((curRow_Normal < areaStart_Normal) || (curRow_Normal > areaEnd_Normal))
				{
					jumpTo_Normal(areaStart_Normal);
				}
			}
			
			// 分页且非套打时，在每一页上打印 PageHead
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
			
			// 这个检查一定要放到打印小票 PageHead 后，否则引起其他问题
			// PrintEnd = true 说明小票明细已经打印完，准备打印小票汇总 。
			// 当前行小于打印汇总的位置时， 跳至打印汇总的位置。打印完页头后再检查
		    if (isPrintPageHead == false && SaleBillMode.getDefault().PrintEnd && curRow_Normal < SaleBillMode.getDefault().getTotal())
			{				
				isPrintPageHead = true;

				isPrintTotal = false;
				try
				{
					jumpTo_Normal(SaleBillMode.getDefault().getTotal() - 1);
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
			
			   
			// 这个一定要放到打印完  打印内容后，否则导致统计的发票每页小计不准
			// 在未打印 Buttom，分页且非套打时，在每一页上打印 PageButtom
			if (isPrintPageHead == false && SaleBillMode.getDefault().PrintEnd == false && curRow_Normal == SaleBillMode.getDefault().getPageBottom() )
			{
				isPrintPageHead = true;
				try
				{
					SaleBillMode.getDefault().printPageBottom();
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
						
		}  // end if (connect && enable)		
		    	 
	}
	
	
	protected void jumpTo_Normal(int rowNo)
	{
		if (pagePrint_Normal)
		{
			int PassRow = 0;

			// 如果要走纸的行小于当前打印行，则需要走纸到下一页
			if (rowNo < curRow_Normal)
			{
				// 走纸到下页，适用于黑标走纸，否则打印空行走纸到下一页
				if (printer.passPage_Normal())
				{
					// 重新开始记行数
					curRow_Normal = 1;

					// 页数加一
					pageNum_Normal++;

					//江苏宏信要求每张小票对应一个 发票号
//					// 发票页加1
//					if (haveSaleFphmCfg())
//					{
//						int n = Convert.toInt(getSaleFphmAttr(InvoiceNum)) + 1;
//						setSaleFphmAttr(InvoiceNum, String.valueOf(n));
//					}

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

//				// 发票页加1
//				if (haveSaleFphmCfg())
//				{
//					int n = Convert.toInt(getSaleFphmAttr(InvoiceNum)) + 1;
//					setSaleFphmAttr(InvoiceNum, String.valueOf(n));
//				}
			}
			else
			{
				PassRow = rowNo - curRow_Normal + 1;// add 1
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
		}
	}
	
	public void cutPaper_Normal()
	{
		if (connect && enable)
		{
			// 分页打印时走纸到页尾
			if (pagePrint_Normal)
			{
				if (!printer.passPage_Normal())
				{
					jumpTo_Normal(pageRow_Normal);
				}
				//1.分页打印时，走纸为 0, 2 - 非分页打印，走纸
				cutLine_Normal = 0;
			}
			else if (cutLine_Normal <= 0)
			{
				cutLine_Normal = 6;
			}

			System.err.println("CutNormal : " + cutLine_Normal);
			
			// 切纸前走纸
			for (int i = 0; i < cutLine_Normal; i++)
			{
				printer.printLine_Normal("\n");
			}

			// 切纸
			if(this.iscutpaper)
				printer.cutPaper_Normal();

			// 恢复计数
			numRow_Normal = 0;
			curRow_Normal = 1;
			pageNum_Normal = 1;

			// 发票页加1
			if (haveSaleFphmCfg())
			{
				int n = Convert.toInt(getSaleFphmAttr(InvoiceNum)) + 1;
				setSaleFphmAttr(InvoiceNum, String.valueOf(n));
			}
		}
		checkPrinterStatus(false);
		
		// 还原标记
		isPrintTotal = true;
	}
	
}
