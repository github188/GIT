package bankpay.alipay.tools;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalVar;

public class ParseIni 
{
	private BufferedReader br = null;
	
	public HashMap Parse()
	{
		String configName = GlobalVar.ConfigPath + "//AliPayConfig.ini";
		String line = null;
		Map map = new HashMap();
		br = CommonMethod.readFile(configName);
		
		try 
		{
			while ((line = br.readLine()) != null)
			{
				String[] row = line.split("=");
				if(row[0].equals("aliConfigUrl"))
				{
					map.put("aliConfigUrl", row[1]);
				}
				else if(row[0].equals("aliPayUrl"))
				{
					map.put("aliPayUrl", row[1]);
				}
				else if(row[0].equals("printFlag"))
				{
					map.put("printFlag", row[1]);
				}
			}
			return (HashMap) map;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String [] args)
	{
		ParseIni p = new ParseIni();
		Map map = p.Parse();
		
		System.out.println(map.get("printFlag").toString());
	}
	
}

