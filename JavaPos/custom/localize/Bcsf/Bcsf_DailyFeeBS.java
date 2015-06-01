package custom.localize.Bcsf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;


public class Bcsf_DailyFeeBS
{
	private Vector billContent;
	private double payablemoney;
	private double paymoney;

	private long billno = 0;
	private String tenantid;
	private String paymonth;

	private boolean isPayOK = false;

	String printName = "c:\\JavaPOS\\feeprint.txt";

	private long getListNo()
	{
		ManipulateDateTime mdt = new ManipulateDateTime();
		String date = mdt.getDateByEmpty() + mdt.getTimeByEmpty();
		return Convert.toLong(date);
	}

	public void clear()
	{
		if (billContent != null)
		{
			billContent.removeAllElements();
			billContent.clear();
		}
		billno = 0;
		payablemoney = 0;
		paymoney = 0;
		tenantid ="";
		paymonth = "";
		isPayOK = false;
	}

	public boolean getFeeBill()
	{
		if (billContent != null)
		{
			billContent.removeAllElements();
			billContent.clear();
		}
		else
		{
			billContent = new Vector();
		}

		Bcsf_NetService netService = (Bcsf_NetService) NetService.getDefault();
		if (netService.getDailyFeeBill(tenantid, paymonth, billContent))
		{
			billno = getListNo();
			return true;
		}

		else
			return false;

	}

	public boolean sendFeeBill(Vector pay)
	{
		if (pay == null || billContent == null)
			return false;

		Bcsf_NetService netService = (Bcsf_NetService) NetService.getDefault();
		return netService.sendDailyFeeBill(billno, billContent, pay);
	}

	public boolean writeBill(Vector pay)
	{
		try
		{
			PrintWriter pw = null;

			if (PathFile.fileExist(printName))
				PathFile.deletePath(printName);

			try
			{
				pw = CommonMethod.writeFile(printName);

				if (pw != null)
				{
					pw.println(billContent(pay));
					pw.flush();
				}
			}
			finally
			{
				if (pw != null)
				{
					pw.close();
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("写入打印数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		}
	}

	private String billContent(Vector pay)
	{
		StringBuffer br = new StringBuffer();
		double sjfee = 0;
		double zlfee = 0;

		try
		{
			br.append("             欢 迎 光 临" + "\r\n");
			br.append("           "+ GlobalInfo.sysPara.mktname + "\r\n");

			br.append("门店号:" + GlobalInfo.sysPara.mktcode + "       ");
			br.append(ManipulateDateTime.getCurrentDate() + "  " + ManipulateDateTime.getCurrentTime() + "\r\n");

			br.append("收银机:" + GlobalInfo.syjDef.syjh + "       ");
			br.append("单  号:" + String.valueOf(billno) + "\r\n");

			br.append("收款员:" + GlobalInfo.posLogin.gh + "       ");
			br.append("类  型:缴费" + "\r\n");

			br.append("承租人号:" + this.tenantid + "     ");
			br.append("缴费月分:" + this.paymonth + "\n\n");

			br.append("序    编码    名称     应收    实收" + "\r\n");
			br.append("======================================\r\n");
			for (int i = 0; i < billContent.size(); i++)
			{
				DailyFeeItemDef feeItem = (DailyFeeItemDef) billContent.get(i);
				br.append(String.valueOf(i) + "     " + String.valueOf(feeItem.incomeid) + "    " +
				feeItem.tenantname + "    " + String.valueOf(feeItem.payablemoney) + "   " + String.valueOf(feeItem.paymoney) + "\r\n");

			}

			br.append("======================================\r\n");

			if (pay == null || pay.size() == 0)
			{
				sjfee = 0;
				zlfee = 0;
			}
			else
			{
				for (int j = 0; j < pay.size(); j++)
				{
					DailyFeePayDef payItem = (DailyFeePayDef) pay.get(j);
					if (payItem.paytype.equals("01") || payItem.paytype.equals("02"))
						sjfee += payItem.paymoney;
					else if (payItem.paytype.equals("-1"))
						zlfee += payItem.paymoney;
				}
			}

			br.append("实  缴:" + String.valueOf(sjfee) + "          " + "找  零:"+ String.valueOf(zlfee) + "\n");
			br.append("======================================\r\n");
			
			for(int k =0; k<pay.size(); k++)
			{
				DailyFeePayDef payItem = (DailyFeePayDef) pay.get(k);
				if(payItem.paytype.equals("01"))
				{
					br.append("现  金:"+ String.valueOf(payItem.paymoney) +"\r\n");
				}
				else if(payItem.paytype.equals("02"))
				{
					br.append("银联卡:  "+ String.valueOf(payItem.paymoney));
					br.append("卡  号:  "+(payItem.cardno==null?"":payItem.cardno)+"\r\n");
				}
			}
			
			br.append("\r\n");
			br.append("        谢谢惠顾，欢迎再次光临\r\n");
			br.append("  THANK YOU!  WELCOME  BACK  AGEIN!\r\n");

			return br.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "";
		}

	}

	public void printBill()
	{
		ProgressBox pb = null;
		BufferedReader br = null;

		try
		{

			if (!PathFile.fileExist(printName))
			{
				new MessageBox("未发现缴费打印文件!");
				return;
			}

			pb = new ProgressBox();
			pb.setText("正在打印缴费凭证,请稍等...");

			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
			{
				Printer.getDefault().startPrint_Journal();

				// 由于发现在windows环境下,用GBK读取文件会产生BUG,改为GB2310
				br = CommonMethod.readFileGBK(printName);

				if (br == null)
				{
					new MessageBox("打开打印文件失败!");

					return;
				}

				String line = null;

				while ((line = br.readLine()) != null)
				{
					if (line.length() <= 0)
					{
						continue;
					}

					Printer.getDefault().printLine_Journal(line);
				}

				// 切纸
				Printer.getDefault().cutPaper_Journal();
			}
		}
		catch (Exception ex)
		{
			new MessageBox("打印发生异常\n\n" + ex.getMessage());
			ex.printStackTrace();
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
			if (br != null)
			{
				try
				{
					br.close();
					br = null;
				}
				catch (IOException e)
				{
					e.printStackTrace();
					br = null;
				}
			}
		}
	}

	public Vector getBillContent()
	{
		return billContent;
	}

	public void setBillContent(Vector billContent)
	{
		this.billContent = billContent;
	}

	public double getPayablemoney()
	{
		payablemoney = 0;

		if (billContent == null || billContent.size() == 0)
			return 0;

		for (int i = 0; i < billContent.size(); i++)
		{
			DailyFeeItemDef item = (DailyFeeItemDef) billContent.get(i);
			payablemoney += item.payablemoney;
		}
		return payablemoney;
	}

	public double getPaymoney()
	{
		paymoney = 0;

		if (billContent == null || billContent.size() == 0)
			return 0;

		for (int i = 0; i < billContent.size(); i++)
		{
			DailyFeeItemDef item = (DailyFeeItemDef) billContent.get(i);
			paymoney += item.paymoney;
		}
		return paymoney;
	}

	public boolean isPayOK()
	{
		return isPayOK;
	}

	public void setPayOK(boolean isPayOK)
	{
		this.isPayOK = isPayOK;
	}

	public void setTenantid(String tenantid)
	{
		this.tenantid = tenantid;
	}

	public void setPaymonth(String paymonth)
	{
		this.paymonth = paymonth;
	}

}
