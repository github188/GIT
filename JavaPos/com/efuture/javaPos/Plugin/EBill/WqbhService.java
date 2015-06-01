package com.efuture.javaPos.Plugin.EBill;

import java.io.BufferedReader;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;

public class WqbhService extends CczzService
{
	 public static boolean inithttp()
	    {
			if (androidhttp == null)
			{
				//读取服务器地址
				BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "/Wqbh.ini");

				if (br == null)
				{
					new MessageBox(Language.apply("配置文件读取错误,马上退出"), null, false);

					return false;
				}

				String line;
				String[] sp;
				String ServerIP=null;
				String ServerPath=null;
				int ServerPort=0;
				
				String ServerIP_CRM=null;
				String ServerPath_CRM=null;
				int ServerPort_CRM=0;
				
				try {
					while ((line = br.readLine()) != null)
					{
						if ((line == null) || (line.length() <= 0))
						{
							continue;
						}

						String[] lines = line.split("&&");
						sp = lines[0].split("=");
						if (sp.length < 2)
							continue;

						/*if (sp[0].trim().compareToIgnoreCase("ServerOS") == 0)
						{
							ServerOS = sp[1].trim();
						}
						else*/ if (sp[0].trim().compareToIgnoreCase("ServerIP") == 0)
						{
							ServerIP = sp[1].trim();
						}
						else if (sp[0].trim().compareToIgnoreCase("ServerPath") == 0)
						{
							ServerPath = sp[1].trim();
						}
						else if (sp[0].trim().compareToIgnoreCase("ServerPort") == 0)
						{
							ServerPort = Integer.parseInt(sp[1].trim());
						}
						else if (sp[0].trim().compareToIgnoreCase("ServerIP_CRM") == 0)
						{
							ServerIP_CRM = sp[1].trim();
						}
						else if (sp[0].trim().compareToIgnoreCase("ServerPath_CRM") == 0)
						{
							ServerPath_CRM = sp[1].trim();
						}
						else if (sp[0].trim().compareToIgnoreCase("ServerPort_CRM") == 0)
						{
							ServerPort_CRM = Integer.parseInt(sp[1].trim());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				androidhttp = new Http(ServerIP,ServerPort,ServerPath);
				androidhttp.init();
				androidhttp.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
				androidhttp.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时
				
				if (ServerIP_CRM != null)
				{
					androidhttp_CRM = new Http(ServerIP_CRM,ServerPort_CRM,ServerPath_CRM);
					androidhttp_CRM.init();
					androidhttp_CRM.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
					androidhttp_CRM.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时
				}
				return true;
			}
			return true;
	    }
	 
	 //	 根据会员卡查询开票清单
		public static boolean getkplist(Vector v1,String code,String type){
			
			if (!GlobalInfo.isOnline) { return false; }

			CmdHead head = null;
			StringBuffer line = new StringBuffer();
			int result = -1;
			String[] values = {GlobalInfo.sysPara.mktcode,"",code,type,""};
			String[] args = {"mktcode","syjh","code","type","kpzt"};

			try
			{
				head = new CmdHead(GETQDLIST);
				line.append(head.headToString() + Transition.SimpleXML(values, args));

				result = NetService.getDefault().HttpCall(androidhttp,line, Language.apply("没有找到商品信息"));

				if (result == 0)
				{
					Vector v = new XmlParse(line.toString()).parseMeth(0, new String[]{"billno","shdate","sl","je","pmfcname"});
					for (int i=0; i< v.size() ; i++)
					{
						String[] row = (String[]) v.elementAt(i);
						String[] row1 = new String[6];
						row1[0] = row[0];
						if(row[1].indexOf(".")!=-1){
							row1[1] = row[1].substring(0,row[1].indexOf("."));
						}else{
							row1[1] = row[1];
						}
						
						row1[2] = row[2];
						row1[3] = row[3];
						row1[4] = row[4];
						row1[5] = "";
						v1.add(row1);
					}

					return true;
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}
			return false;

		}
}
