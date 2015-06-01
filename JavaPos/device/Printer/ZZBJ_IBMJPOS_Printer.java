package device.Printer;

import jpos.JposException;
import jpos.POSPrinterConst;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.Language;

public class ZZBJ_IBMJPOS_Printer extends IBMJPOS_Printer
{
	public void setEnable(boolean enable)
	{
		try
		{
			if (enable)
			{
				if (!posPrinter.getClaimed())
				{
					posPrinter.claim(1000);
					posPrinter.setDeviceEnabled(true);
					// 设置行距
					posPrinter.setSlpLineSpacing(7);

					//当使用平推打印时,必须使用异步方式,否则当平推有纸时,其他打印端口打印的内容会遗失
					//异步且事务打印模式,平摊有纸时,打印栈等待,拿出平推纸以后，所有内容重新打印
					//异步非事务打印模式,平摊有纸时,打印栈等待,拿出平推纸以后，继续打印剩余内容,但打印速度较慢
					//同步且事务打印模式,平摊有纸时,打印栈transprint函数时抛异常,打印内容丢失
					//同步非事务打印模式,平摊有纸时,当前行异常,打印下一行时checkstatus等待拿出平推纸,然后继续打印
					if (printMode == 'Y')
					{
						posPrinter.setAsyncMode(false); // 同步模式
					}
					else
					{
						posPrinter.setAsyncMode(true); // 异步模式
					}

					if (slpLineChar > 0)
					{
						posPrinter.setSlpLineChars(slpLineChar);
					}

					posPrinter.addStatusUpdateListener(event);
					posPrinter.addErrorListener(event);
				}
			}
			else
			{
				if (posPrinter.getClaimed())
				{
					posPrinter.removeStatusUpdateListener(event);
					posPrinter.addErrorListener(event);
					posPrinter.setDeviceEnabled(false);
					posPrinter.release();
				}
			}
		}
		catch (JposException e)
		{
			e.printStackTrace();
		}
	}
	
	public void printLine_Slip(String printStr)
	{
		try
		{
			if (this.SlipPrint == 1)
			{
				this.printLine_Normal(printStr);
				return ;
			}
			
			if (this.SlipPrint == 2)
			{
				this.printLine_Journal(printStr);
				return ;
			}
			
			if (bakfileopen)
			{
				txtprinter.printLine_Slip(printStr);
			}

			if (!checkStatus()) { return; }

			// 不开启ER_CLEAR
			slpback = false;
			
			// 等待进纸
			while (posPrinter.getSlpEmpty())
			{
				if ((SlipEmptyMsg == null) || SlipEmptyMsg.trim().equals(""))
				{
					SlipEmptyMsg = Language.apply("请在平摊打印机中放入打印纸...");
				}

				if (SlipInsertion)
				{
					ProgressBox pb = null;

					try
					{
						pb = new ProgressBox();
						pb.setText(SlipEmptyMsg);
						posPrinter.beginInsertion(5000);
						posPrinter.endInsertion();
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
					finally
					{
						if (pb != null)
						{
							pb.close();
						}
					}
				}
				else
				{
					new MessageBox(SlipEmptyMsg);
				}
			}

			// 事务打印方式打印行数达到缓存行数，则将之前的打印内容及时打印
			if ((maxrows > 0) && alreadyTransaction_Slip && transactionMode)
			{
				curRow_Slip++;

				if (curRow_Slip >= maxrows)
				{
					curRow_Slip = -1;
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_SLIP, POSPrinterConst.PTR_TP_NORMAL);
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_SLIP, POSPrinterConst.PTR_TP_TRANSACTION);
				}
			}

			// 如果没有执行过开启事务打印方式，执行一次
			if (!alreadyTransaction_Slip)
			{
				alreadyTransaction_Slip = true;

				if (transactionMode)
				{
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_SLIP, POSPrinterConst.PTR_TP_TRANSACTION);
				}
				else
				{
					posPrinter.transactionPrint(POSPrinterConst.PTR_S_SLIP, POSPrinterConst.PTR_TP_NORMAL);
				}
			}

			boolean done = false;

			if (SlipFont > 0 && posPrinter.getCharacterSet() != SlipFont) posPrinter.setCharacterSet(SlipFont);

			if (printStr.indexOf("Big&") >= 0)
			{
				char[] value = { 0x1b, '|', '2', 'C' };
				printStr = printStr.replaceAll("Big&", String.valueOf(value));
				done = true;
			}
			
			posPrinter.printNormal(POSPrinterConst.PTR_S_SLIP, printStr);

			if (done)
			{
				char[] value = { 0x1b, '|', 'N' };
				posPrinter.printNormal(POSPrinterConst.PTR_S_SLIP, String.valueOf(value));
			}

			Row_Slip++;
		}
		catch (JposException e)
		{
			e.printStackTrace();
		}
	}
	
	public String getDiscription()
	{
		return Language.apply("卓展北京IBM的JPOS驱动方式打印机");
	}
}
