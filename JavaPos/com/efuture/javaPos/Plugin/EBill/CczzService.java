package com.efuture.javaPos.Plugin.EBill;

import java.io.BufferedReader;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class CczzService extends AndroidService
{
	    public static final int FINDCCZZHEAD = 141;	 			//查找android的商品信息
	    public static final int GETQDLIST = 140; 			//查询清单列表
	    public static final int FINDCCZZGOODS = 139;	 			//查找android的商品信息
	    public static final int SENDOK = 11;	 			//查找android的商品信息
	    public static Http androidhttp = null;
	    public static Http androidhttp_CRM = null;
	    
	    public static String[] refhead ={"str1","djlb","syyh","rqsj","hjzje","hjzke","yhzke","lszke","hyzke","hykh","hytype","hykname","ysyjh","yfphm","memo","str2","str3"};
	    public static String[] refgoods ={"str1","rowno","yyyh","code","barcode","name","gz","unit","memo","jg","sl","hjje","hjzk","yhzke","lszke","hyzke","qtzke"};
	    
	    public static boolean inithttp()
	    {
			if (androidhttp == null)
			{
				//读取服务器地址
				BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "/Cczz.ini");

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
	    
	    public static boolean inithttp_JH100()
	    {
			if (androidhttp == null)
			{
				//读取服务器地址
				BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "/JH100.ini");

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
					Vector v = new XmlParse(line.toString()).parseMeth(0, new String[]{"billno","type","sl","je","pmfcname"});
					for (int i=0; i< v.size() ; i++)
					{
						String[] row = (String[]) v.elementAt(i);
						String[] row1 = new String[6];
						row1[0] = row[0];
						row1[1] = row[1];
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

		
		//根据序号查询小票信息
		public static boolean getSaleHead(String code,String type,SaleHeadDef shd){
			
			if (!GlobalInfo.isOnline) { return false; }

			CmdHead head = null;
			StringBuffer line = new StringBuffer();
			int result = -1;
			String[] values = {"",code,type};
			String[] args = {"syjh","code","type"};

			try
			{
				head = new CmdHead(FINDCCZZHEAD);
				line.append(head.headToString() + Transition.SimpleXML(values, args));

				result = NetService.getDefault().HttpCall(androidhttp,line, Language.apply("小票头信息不存在"));

				if (result == 0)
				{
					String[] ref1 = {"syjh","fphm","djlb","mkt","bc","rqsj","syyh","hykh","hytype","jfkh","thsq","ghsq","hysq"
					     			,"sqkh","sqktype","sqkzkfd","ysje","sjfk","zl","sswr_sysy","fk_sysy","hjzje","hjzsl","hjzke","hyzke"
					    			,"yhzke","lszke","netbz","printbz","hcbz","hhflag","buyerinfo","jdfhdd","salefphm","printnum","bcjf","ljjf"
					    			,"memo","str1","str2","str3","str4","str5","num1","num2","num3","num4","num5","hykname","cczz_zktmp","cczz_custID"};
					
					Vector v = new XmlParse(line.toString()).parseMeth(0,ref1);

					if (v.size() > 0)
					{
						String[] row = (String[]) v.elementAt(0);

						if (Transition.ConvertToObject(shd, row,ref1)) { return true; }
					}
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}
			return false;

		}
		
		//根据序号得到小票商品
		public static boolean getSaleGoods(String code,String type,Vector list){
			
			if (!GlobalInfo.isOnline) { return false; }

			CmdHead head = null;
			StringBuffer line = new StringBuffer();
			int result = -1;
			String[] values = {"",code,type,""};
			String[] args = {"syjh","code","type","kpzt"};

			try
			{
				head = new CmdHead(FINDCCZZGOODS);
				line.append(head.headToString() + Transition.SimpleXML(values, args));

				result = NetService.getDefault().HttpCall(androidhttp,line, Language.apply("小票商品信息不存在"));

				if (result == 0)
				{
					Vector v = new XmlParse(line.toString()).parseMeth(0, SaleGoodsDef.ref);//refgoods

						SaleGoodsDef def = null;
						for (int i = 0; i < v.size(); i++) {
							def = new SaleGoodsDef();
							String[] row = (String[]) v.elementAt(i);
							if (Transition.ConvertToObject(def, row))//refgoods
							{
								def.fph=String.valueOf(def.rowno) + ";" + def.str3;//行号+是否跟随
								def.rowno=0;
								def.str3="";
								list.add(def);
							}
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
		
		public static boolean findcustId(String code,StringBuffer buff)
		{
			if (!GlobalInfo.isOnline) { return false; }

			StringBuffer line = new StringBuffer();
			int result = -1;
			CmdHead head = null;
			String[] values = { GlobalInfo.sysPara.mktcode,code};
			String[] args = { "mktcode","track" };

			try
			{
				head = new CmdHead(142);
				line.append(head.headToString() + Transition.SimpleXML(values, args));

				result = NetService.getDefault().HttpCall((androidhttp_CRM == null?androidhttp:androidhttp_CRM),line, Language.apply("查找账号失败"));

				if (result == 0)
				{
					Vector v1 = new XmlParse(line.toString()).parseMeth(0, new String[] {"code"});

					String[] row = (String[]) v1.elementAt(0);
					
					buff.append(row[0]);

					return true;
				}
				else
				{
					return false;
				}
			}
			catch (Exception ex)
			{
				

				return false;
			}
		}
		
		/*//交易成功，改变状态
		public static boolean SendInvoiceOK(String seqno,String syjh,long fphm)
		{
			
			if (!GlobalInfo.isOnline) { return false; }

			CmdHead head = null;
			StringBuffer line = new StringBuffer();
			int result = -1;
			String[] values = {seqno,syjh,String.valueOf(fphm)};
			String[] args = {"seqno","syjh","fphm"};

			try
			{
				head = new CmdHead(SENDOK);
				line.append(head.headToString() + Transition.SimpleXML(values, args));

				result = NetService.getDefault().HttpCall(androidhttp,line, "更新ANDROID端信息失败");

				if (result == 0)
				{
					return true;
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}
			return false;
		}*/
}
