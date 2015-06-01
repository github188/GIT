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
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class AndroidService
{
//	安卓PAD命令
    public static final int FINDANDORIDHEAD = 9; 			//查找android的小票头
    public static final int FINDANDORIDGOODS = 10;	 			//查找android的商品信息
    public static final int SENDOK = 11;	 			//查找android的商品信息
    public static Http androidhttp = null;
    
    
    public static String[] refhead ={"str1","djlb","syyh","rqsj","hjzje","hjzke","yhzke","lszke","hyzke","hykh","hytype","hykname","ysyjh","yfphm","memo","str2","str3"};
    public static String[] refgoods ={"yyyh","code","barcode","name","gz","unit","memo","jg","sl","hjje","hjzk","yhzke","lszke","hyzke","qtzke"};
    
    public static boolean inithttp()
    {
		if (androidhttp == null)
		{
			//读取服务器地址
			BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "/Android.ini");

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
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			androidhttp = new Http(ServerIP,ServerPort,ServerPath);
			androidhttp.init();
			androidhttp.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
			androidhttp.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时
			return true;
		}
		return true;
    }
	//根据序号查询小票信息
	public static boolean getSaleHead(String seqno,String codetype,String djlb,SaleHeadDef shd){
		
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = {seqno,codetype,djlb};
		String[] args = {"seqno","type","djlb"};

		try
		{
			head = new CmdHead(FINDANDORIDHEAD);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = NetService.getDefault().HttpCall(androidhttp,line, Language.apply("小票信息不存在"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, refhead);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(shd, row,refhead)) { return true; }
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
	public static boolean getSaleGoods(String seqno,String codetype,String djlb,Vector list){
		
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = {seqno,codetype,djlb};
		String[] args = {"seqno","type","djlb"};

		try
		{
			head = new CmdHead(FINDANDORIDGOODS);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = NetService.getDefault().HttpCall(androidhttp,line, Language.apply("小票商品信息不存在"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, refgoods);

					SaleGoodsDef def = null;
					for (int i = 0; i < v.size(); i++) {
						def = new SaleGoodsDef();
						String[] row = (String[]) v.elementAt(i);
						if (Transition.ConvertToObject(def, row,refgoods))
						{
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
	
	//交易成功，改变状态
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

			result = NetService.getDefault().HttpCall(androidhttp,line, Language.apply("更新ANDROID端信息失败"));

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
	}
}
