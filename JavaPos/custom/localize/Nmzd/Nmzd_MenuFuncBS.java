package custom.localize.Nmzd;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Nmzd_MenuFuncBS extends MenuFuncBS
{
	public void openDjSale(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (GlobalInfo.syjDef.issryyy == 'N')
		{
			new MessageBox("超市模式下不允许使用【定金消费】");
		}
		else
		{
			super.openDjSale(mfd,mffe);
		}
		
	}
	
	public boolean execExtendFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (Convert.toInt(mfd.code) == 414 || Convert.toInt(mfd.code) == 415 || Convert.toInt(mfd.code) == 416)
		{
			try
			{
				String cfg_file = "/GWKList.ini";
				String fileName = "C:\\GWK\\gwk_trans.ini";
				if (Convert.toInt(mfd.code) == 414)
				{
					cfg_file = "/GWKList.ini";
					fileName = "C:\\GWK\\gwk_trans.ini";
				}
				else if (Convert.toInt(mfd.code) == 415)
				{
					cfg_file = "/ZHKList.ini";
					fileName = "C:\\ZHK\\trans.ini";
				}
				else if (Convert.toInt(mfd.code) == 416)
				{
					cfg_file = "/GMCList.ini";
					fileName = "C:\\GMC\\trans.ini";
				}
				
				if (!PathFile.fileExist(GlobalVar.ConfigPath + cfg_file))
				{
					// 通过菜单调用进行提示,否则不提示
					if (mfd != null || mffe != null)
					{
						new MessageBox("没有找到服务器IP列表清单文件 "+cfg_file);
					}
				}
				else
				{
					Vector v = CommonMethod.readFileByVector(GlobalVar.ConfigPath + cfg_file);
					if (v.size() > 0)
					{

					    BufferedReader br = null;

					    br = CommonMethod.readFileGB2312(fileName);

				        if (br == null)
				        {
				        	new MessageBox("没有找到文件"+fileName);
				            return true;
				        }
				        String ip = null;
					    Vector v1 = new Vector();
					    String line;
				        while ((line = br.readLine()) != null)
				        {
				        	if (line.indexOf("IP_ADDRESS") == 0)
				        	{
				        		ip = line.substring(11);
				        	}
				            v1.add(line);
				        }
				        br.close();
				        
						String[] title = { "购物卡服务器描述", "服务器地址" };
						int[] width = { 200, 400 };
						int choice = new MutiSelectForm().open("请选择购物服务器,当前为"+ip, title, width, v,false,660,319,false);
						if (choice < 0) return true;
						ip = ((String[])v.elementAt(choice))[1];
						
						Vector v2 = new Vector();
				        for (int i = 0; i < v1.size();i++)
				        {
				        	line = (String) v1.elementAt(i);
				        	if (line.indexOf("IP_ADDRESS") == 0)
				        	{
				        		line = "IP_ADDRESS="+ip.trim();
				        	}
				            v2.add(line);
				        }

				        //写入文件
				        PrintWriter pw = null; 
				        pw = CommonMethod.writeFile(fileName);
				        for (int i = 0 ; i < v2.size(); i++)
				        {
				        	pw.println((String)v2.elementAt(i));
				        }
				        pw.flush();
				        pw.close();
				        new MessageBox("购物卡服务器的IP地址改为 "+ip);
					}
				}
				return true;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				new MessageBox(ex.getMessage());
			}
		}
		return false;
	}
}
