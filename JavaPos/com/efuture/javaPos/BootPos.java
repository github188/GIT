package com.efuture.javaPos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.UnicodeReader;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;

public class BootPos
{	
	public BootPos(String file)
	{
		try
		{
			UnicodeReader read = new UnicodeReader (new FileInputStream(new File(file)),"UTF-8");
			BufferedReader br = new BufferedReader(read);
			Vector cmds = new Vector();

			//
			String line = br.readLine();
			while(line != null)
			{
				if (line.trim().length() > 0)
				{
					cmds.add(line.trim());
				}
				line = br.readLine();
			}
			
			br.close();
			read.close();
			
			// 执行
			for (int i=0;i<cmds.size();i++)
			{
				try
				{
					line = (String)cmds.elementAt(i);
					if (line.startsWith("%DELAY%"))
					{
						String[] s = line.split(",");
						int n = 0;
						String msg = Language.apply("正在延时,剩余 %sec% 秒,请等待.....");
						if (s.length > 1) n = Integer.parseInt(s[1]);
						if (s.length > 2) msg = s[2];
						
						ProgressBox pb = new ProgressBox();
						while(n > 0)
						{
							pb.setText(msg.replaceAll("%sec%", String.valueOf(n)));
							while (Display.getDefault().readAndDispatch());
							Thread.sleep(1000);
							
							n--;
						}
						pb.close();
					}
					else
					{
						// 执行程序,最后一个不等待
						if (i == cmds.size() - 1) CommonMethod.waitForExec(line,false);
						else CommonMethod.waitForExec(line);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}					
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		try
		{
			String file = "bootpos.ini";
	        if (args != null)
	        {
	            if (args.length > 0)
	            {
	                file = args[0].trim();
	            }
	        }

	        new BootPos(file);
	        
			SWTResourceManager.dispose();
			System.exit(0);
		}
		catch(Exception ex)
		{
			PosLog.getLog(BootPos.class.getSimpleName()).error(ex);
		}
		
	}
}
