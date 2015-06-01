package device.Printer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_Printer;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;

public class Muti_Printer implements Interface_Printer
{
	Interface_Printer printer1 = null;
	Interface_Printer printer2 = null;
	Interface_Printer printer3 = null;
	
	private int NormalPrint = 1;
	private int JournalPrint = 2;
	private int SlipPrint = 3;

	public String configInfo = null;

	public void setConfigInfo(String info)
	{
		configInfo = info;
	}

	public String getConfigInfo()
	{
		if (configInfo == null)
		{
			return DeviceName.devicePrinter;
		}
		else
		{
			return configInfo;
		}
	}

	public boolean open()
	{
		if (DeviceName.devicePrinter.length() <= 0) return false;
		boolean done = false;
		String line = DeviceName.devicePrinter;
		try
		{
			String[] arg = getConfigInfo().split("\\|");

			for (int i = 0; i < arg.length; i++)
			{
				if (i >= 3)
				{
					done = true;
					return done;
				}

				if (arg[i] == null || arg[i].trim().length() <= 0)
				{
					if (i == 0)
					{
						new MessageBox(Language.apply("必须配置第一个打印"));
						return false;
					}
					
					if (i == 1) printer2 = printer1;
					if (i == 2) printer3 = printer1;
					continue;
				}

				if (arg[i].indexOf(";") < 0)
				{
					//new MessageBox("第" + (i + 1) + "个打印机的配置无效\n" + arg[i]);
					new MessageBox(Language.apply("第{0}个打印机的配置无效\n", new Object[]{(i + 1)+""}) + arg[i]);
				}
				else
				{
					int exist = -1;
					//检查打印机是否重复
					for (int x = 0; x < i; x++)
					{
						if (arg[x].trim().equals(arg[i].trim())) 
						{
							exist  = x;
							break;
						}
					}
					
					//如果存在重复
					if (exist != -1)
					{
						Interface_Printer p = null;
						if (exist == 0) p = printer1;
						if (exist == 1) p = printer2;
						
						if (i == 1) printer2 = p;
						if (i == 2) printer3 = p;
						
						continue;
					}
					
					String printname = arg[i].substring(0, arg[i].indexOf(";"));

					Class cl = null;

					for(int j=0;j<2;j++)
					{
						try
						{
							if (j == 0) cl = Class.forName("device.Printer." + printname);
							else cl = Class.forName(printname);
							if (cl != null) break;
						}
						catch(Exception ex)
						{
						}
					}
					if (cl != null)
					{
						Interface_Printer p = (Interface_Printer) cl.newInstance();
						DeviceName.devicePrinter = arg[i].substring(arg[i].indexOf(";") + 1);
						if (!p.open())
						{
							new MessageBox(Language.apply("打开第{0}个打印机失败\n", new Object[]{(i + 1)+""}) + arg[i]);
						}
						else
						{
							if (i == 0) printer1 = p;
							if (i == 1) printer2 = p;
							if (i == 2) printer3 = p;
						}
					}
					else
					{
						//new MessageBox("第" + (i + 1) + "个打印机配置类名 " + printname + " 无效\n" + arg[i]);
						new MessageBox(Language.apply("第{0}个打印机配置类名 {1} 无效\n", new Object[]{(i + 1)+"", printname}) + arg[i]);
					}
				}
			}

			done = true;
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("打开打印机异常:\n") + ex.getMessage());
			done = false;
			return done;
		}
		finally
		{
			DeviceName.devicePrinter = line;

			if (!done)
			{
				close();
				
				// 通过配置文件设定多打印机情况下，各个不同打印机调用不同的栈
				if (new File(GlobalVar.ConfigPath+"\\muti_printer.ini").exists())
				{
					BufferedReader br = CommonMethod.readFile("muti_printer.ini");
					String line1 = null;
					try
					{
						while((line1 = br.readLine())!= null)
						{
							if (line1.trim().length() <= 0 ||line1.charAt(0) == ';') continue;
							
							if (line1.length() > 0) NormalPrint = Convert.toInt(String.valueOf(line1.charAt(0)));
							
							if (line1.length() > 1) JournalPrint = Convert.toInt(String.valueOf(line1.charAt(1)));
							
							if (line1.length() > 2) SlipPrint = Convert.toInt(String.valueOf(line1.charAt(2)));
						}
						
						br.close();
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
	}

	public void close()
	{
		if (printer3 != null && !printer3.equals(printer1) && !printer3.equals(printer2))
		{
			printer3.close();
		}
		
		if (printer2 != null && !printer2.equals(printer1))
		{
			printer2.close();
		}
		
		if (printer1 != null)
		{
			printer1.close();
		}
	}

	public void setEnable(boolean enable)
	{
		if (printer1 != null)
		{
			printer1.setEnable(enable);
		}

		if (printer2 != null && !printer2.equals(printer1))
		{
			printer2.setEnable(enable);
		}

		if (printer3 != null && !printer3.equals(printer1) && !printer3.equals(printer2))
		{
			printer3.setEnable(enable);
		}
	}

	public void cutPaper_Journal()
	{
		if (printer2 != null)
		{
			if (this.JournalPrint == 1) printer2.cutPaper_Normal();
			
			if (this.JournalPrint == 2) printer2.cutPaper_Journal();
			
			if (this.JournalPrint == 3) printer2.cutPaper_Slip();
		}
	}

	public void cutPaper_Normal()
	{
		if (printer1 != null)
		{
			if (this.NormalPrint == 1) printer1.cutPaper_Normal();
			
			if (this.NormalPrint == 2) printer1.cutPaper_Journal();
			
			if (this.NormalPrint == 3) printer1.cutPaper_Slip();
		}
	}

	public void cutPaper_Slip()
	{
		if (printer3 != null)
		{
			if (this.SlipPrint == 1) printer3.cutPaper_Normal();
			
			if (this.SlipPrint == 2) printer3.cutPaper_Journal();
			
			if (this.SlipPrint == 3) printer3.cutPaper_Slip();
		}
	}

	public void printLine_Normal(String printStr)
	{
		if (printer1 != null)
		{
			if (this.NormalPrint == 1) printer1.printLine_Normal(printStr);
			
			if (this.NormalPrint == 2) printer1.printLine_Journal(printStr);
			
			if (this.NormalPrint == 3) printer1.printLine_Slip(printStr);
		}
	}

	public void printLine_Journal(String printStr)
	{
		if (printer2 != null)
		{
			if (this.JournalPrint == 1) printer2.printLine_Normal(printStr);
			
			if (this.JournalPrint == 2) printer2.printLine_Journal(printStr);
			
			if (this.JournalPrint == 3) printer2.printLine_Slip(printStr);
		}
	}

	public void printLine_Slip(String printStr)
	{
		if (printer3 != null)
		{
			if (this.SlipPrint == 1) printer3.printLine_Normal(printStr);
			
			if (this.SlipPrint == 2) printer3.printLine_Journal(printStr);
			
			if (this.SlipPrint == 3) printer3.printLine_Slip(printStr);
		}
	}

	public boolean passPage_Normal()
	{
		if (printer1 != null) 
		{
			if (this.NormalPrint == 1) return printer1.passPage_Normal();
			
			if (this.NormalPrint == 2) return printer1.passPage_Journal();
			
			if (this.NormalPrint == 3) return printer1.passPage_Slip();
		}

		return true;
	}

	public boolean passPage_Journal()
	{
		if (printer2 != null) 
		{ 
			if (this.JournalPrint == 1) return printer2.passPage_Normal();
			
			if (this.JournalPrint == 2) return printer2.passPage_Journal();
			
			if (this.JournalPrint == 3) return printer2.passPage_Slip();
		}

		return true;
	}

	public boolean passPage_Slip()
	{
		if (printer3 != null) 
		{
			if (this.SlipPrint == 1) return printer3.passPage_Normal();
			
			if (this.SlipPrint == 2) return printer3.passPage_Journal();
			
			if (this.SlipPrint == 3) return printer3.passPage_Slip(); 
		}

		return false;
	}

	public void enableRealPrintMode(boolean flag)
	{
	}

	public Vector getPara()
	{
		/**
		 Vector v = new Vector();
		 String comlist = "端口号";
		 Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		 while(portList != null)
		 {
		 CommPortIdentifier p = (CommPortIdentifier)portList.nextElement();
		 if (p == null) break;
		 else
		 {
		 comlist +=","+p.getName();
		 }
		 }
		 
		 v.add(comlist.split(","));
		 v.add(new String[]{"波特率","9600","110","300","600","1200","2400","4800","19200"});
		 v.add(new String[]{"奇偶效验位","None","Odd","Even"});
		 v.add(new String[]{"数据位","8","7","6","5","4"});
		 v.add(new String[]{"停止位","1","1.5","2"});
		 v.add(new String[]{"是否显示切纸提示","N","Y"});
		 v.add(new String[]{"切纸前走纸的行数","0"});
		 v.add(new String[]{"切纸命令"});
		 v.add(new String[]{"初始化命令"});
		 v.add(new String[]{"分页走纸命令"});
		 */
		return null;
	}

	public String getDiscription()
	{
		return Language.apply("复式打印机");
	}

	public void setEmptyMsg_Slip(String msg)
	{
	}
}
