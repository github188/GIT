package com.efuture.javaPos.Global;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.SaleEvent;

public class TestThread extends Thread
{
	public static int status = 0;// 1－执行测试脚本；2－执行交易测试；0－停止测试
	public static SaleBS saleBS = null;
	public static SaleEvent saleEvent = null;
	public String custTrack = null;

	public void run()
	{
		BufferedReader br = null;
		if (TestThread.status == 1) br = CommonMethod.readFile(GlobalVar.ConfigPath + "/AutoTest.ini");
		if (TestThread.status == 2) br = CommonMethod.readFile(GlobalVar.ConfigPath + "/StressTest.ini");

		if (br == null) { return; }

		Vector line = new Vector();
		String temp = null;

		try
		{
			while ((temp = br.readLine()) != null)
			{
				if (temp.trim().length() <= 1)
				{
					continue;
				}

				if (temp.trim().charAt(0) == ';')
				{
					continue;
				}

				line.addElement(temp);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		if (line == null) { return; }

		//
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				new MessageBox(Language.apply("系统将进入自动测试功能,请不要进行键操作\n\n如果要停止自动测试功能,请再次按下'自动测试键'"));
			}
		});

		//String line = "6,1,1,2,%2000,18,2,%500,18,2,2,5,5,7,7,%500,18,2,2,5,5,7,5,%2000,18,%2000,41,10,3,1,1,%2000,18,%2000,41,%3000,18,%2000";
		String strLine = "";
		for (int i = 0; i < line.size(); i++)
		{
			strLine = strLine + (String) line.elementAt(i);
		}
		final String[] command = strLine.split(",");

		int cs = 0;// 测试次数
		long zsc = 0;// 测试总时间

		while (true)
		{
			// 时长显示
			cs += 1;
			long start = System.currentTimeMillis();

			// 1－执行测试脚本
			if (TestThread.status == 1) test(command);

			// 2－执行交易测试
			if (TestThread.status == 2)
			{
				if (PathFile.fileExist(GlobalVar.ConfigPath + "/AutoTestCust.ini"))
				{
					BufferedReader br1 = null;
					br1 = CommonMethod.readFile(GlobalVar.ConfigPath + "/AutoTestCust.ini");
					
					String line1 = "";
					try
					{
						while ((line1 = br1.readLine()) != null)
						{
							if (line1.trim().length() <= 0 || line1.indexOf('-') < 0)
							{
								continue;
							}
							String[] trackInfo = line1.split("-");
							if (GlobalInfo.syjDef.syjh.equals(trackInfo[0].trim()))
							{
								if (trackInfo[1] != null && trackInfo[1].trim().length() > 0)
								custTrack = trackInfo[1].trim();
								break;
							}
						}
					}
					catch (IOException e)
					{
						// TODO 自动生成 catch 块
						e.printStackTrace();
					}
				}
				
				StressTest(line);
			}
				

			long end = System.currentTimeMillis();
			zsc = zsc + (end - start);
			System.out.println("本次测试耗时：" + String.valueOf(end - start) + "\n测试完成次数：" + cs + "\n测试平均时长：" + zsc / cs);

			if (TestThread.status == 0)
			{
				break;
			}
		}

		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				new MessageBox(Language.apply("系统已停止自动测试功能!"));
			}
		});
	}

	public void test(final String[] command)
	{
		for (int i = 0; i < command.length; i++)
		{

			if (TestThread.status == 0) { return; }

			try
			{
				sleep(10);
			}
			catch (InterruptedException e1)
			{
				// TODO 自动生成 catch 块
				e1.printStackTrace();
			}

			String com = command[i];
			char cha = com.charAt(0);

			if (!Character.isDigit(cha))
			{
				com = com.substring(1);
			}

			final int num = Integer.parseInt(com);

			if (cha != '%')
			{
				try
				{
					if (cha == '#')// 直接发送ASII码到界面上
					{
						Display.getDefault().syncExec(new Runnable()
						{
							public void run()
							{
								NewKeyListener.sendASII(num);
							}
						});
					}
					else
					{
						Display.getDefault().syncExec(new Runnable()
						{
							public void run()
							{
								NewKeyListener.sendKey(num);
							}
						});
					}
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}
			}
			else
			//'%'代表延迟时间
			{
				try
				{
					sleep(Integer.parseInt(com));
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void StressTest(final Vector command)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				CustomerDef cust = new CustomerDef();
				if (custTrack != null)	DataService.getDefault().getCustomer(cust, custTrack);
				if (cust != null)	saleBS.memberGrantFinish(cust);
				
				// 调试模式显示通讯时长
				for (int i = 0; i < command.size(); i++)
				{

					if (TestThread.status == 0) { return; }

					String com = (String) command.elementAt(i);
					if (com == null)
					{
						continue;
					}

					final String[] goodsNum = com.split(",");
					if (goodsNum.length >= 3)
					{
						String code = goodsNum[2];
						String yyyh = goodsNum[0];
						String gz = goodsNum[1];

						saleEvent.saleform.setFocus(saleEvent.yyyh);
						saleEvent.yyyh.setText(yyyh);
						saleBS.enterInput();

						saleEvent.saleform.setFocus(saleEvent.gz);
						saleEvent.gz.setText(gz);
						saleBS.enterInput();

						saleEvent.saleform.setFocus(saleEvent.code);
						saleEvent.code.setText(code);
						saleBS.enterInput();
					}
				}

				// 付款操作
				Vector tempPay = new Vector();
				SalePayDef tempsp = new SalePayDef();
				tempsp.syjh = saleBS.saleHead.syjh;
				tempsp.fphm = saleBS.saleHead.fphm;
				tempsp.rowno = 1;
				tempsp.flag = '1';

				// 求付款方式代码
				Vector vector = saleBS.getPayModeBySuper("0");
				if (vector != null) tempsp.paycode = ((String[]) vector.get(0))[0];
				else tempsp.paycode = "StressTestFK";

				tempsp.payname = Language.apply("交易测试虚拟付款");
				tempsp.ybje = saleBS.saleHead.ysje;
				tempsp.hl = 1;
				tempsp.je = saleBS.saleHead.ysje;
				tempPay.add(tempsp);
				saleBS.saleHead.sjfk = ManipulatePrecision.doubleConvert(tempsp.je, 2, 1);

//				System.out.println(saleBS.saleHead.hykh);
				if (AccessDayDB.getDefault().writeSale(saleBS.saleHead, saleBS.saleGoods, tempPay))
				{
					if (DataService.getDefault().sendSaleData(saleBS.saleHead, saleBS.saleGoods, tempPay))
					{
						DataService dataservice = (DataService) DataService.getDefault();
						dataservice.getSaleTicketMSInfo(saleBS.saleHead, saleBS.saleGoods, tempPay);
						// SaleBillMode.getDefault().setSaleTicketMSInfo(saleBS.saleHead, gifts);
						
						saleBS.sellFinishComplete();
						saleEvent.setCurGoodsBigInfo();
					}
					else
					{
						saleBS.paySellCancel();
					}
				}
			}
		});
	}
}
