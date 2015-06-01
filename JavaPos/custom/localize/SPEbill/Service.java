package custom.localize.SPEbill;

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
import com.efuture.javaPos.Plugin.EBill.AndroidService;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class Service extends AndroidService
{
	   
	    public static final int FINDCCZZGOODS = 139;	 			//查找android的商品信息

	    public static Http androidhttp = null;
	    public static String[] refgoods ={"barcode","code","yyyh","type","gz","unit","name","sl","jg","hjzk","hjje","yhzke","lszke","qtzke","hyzke","str7","memo","fph","str8","str9"};
	    
	    
	    public static boolean inithttp()
	    {
			if (androidhttp == null)
			{
				//读取服务器地址
				BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "\\Ebill.ini");

				if (br == null)
				{
					new MessageBox("配置文件读取错误,马上退出", null, false);

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
				
/*				if (ServerIP_CRM != null)
				{
					androidhttp_CRM = new Http(ServerIP_CRM,ServerPort_CRM,ServerPath_CRM);
					androidhttp_CRM.init();
					androidhttp_CRM.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
					androidhttp_CRM.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时
				}*/
				return true;
			}
			return true;
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

				result = NetService.getDefault().HttpCall(androidhttp,line, "小票商品信息不存在");

				if (result == 0)
				{
					Vector v = new XmlParse(line.toString()).parseMeth(0, refgoods);//refgoods

						SaleGoodsDef def = null;
						for (int i = 0; i < v.size(); i++) {
							def = new SaleGoodsDef();
							String[] row = (String[]) v.elementAt(i);
							if (Transition.ConvertToObject(def, row,refgoods))//refgoods
							{
								//def.fph=String.valueOf(def.rowno) + ";" + def.str3;//行号+是否跟随
								//def.rowno=0;
								//def.str3="";
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
}
