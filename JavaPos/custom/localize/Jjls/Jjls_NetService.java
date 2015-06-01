package custom.localize.Jjls;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;

import custom.localize.Cmls.Cmls_NetService;

public class Jjls_NetService extends Cmls_NetService
{
	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		if (new File(GlobalVar.ConfigPath + "\\mzklist.ini").exists())
		{
			if (cardhttplist == null || cardhttplist.size() <= 0)
			{
				cardhttplist = new Vector();
				BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "\\mzklist.ini");
				String line = null;
				try
				{
					while ((line = br.readLine()) != null)
					{
						if (line.trim().length() < 1)
							continue;

						if (line.trim().charAt(0) == ';')
							continue;

						if (line.trim().indexOf("=") > 0)
						{
							String[] iplist = line.trim().split("=");
							Http a = new Http(iplist[1]);
							a.init();
							a.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
							a.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时
							Object[] obj = new Object[] { iplist[0], a };
							cardhttplist.add(obj);
						}
					}
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (cardhttplist != null && cardhttplist.size() > 0)
			{
				for (int i = 0; i < cardhttplist.size(); i++)
				{
					Object[] row = (Object[]) cardhttplist.elementAt(i);
					String list = (String) row[0];
					int x = ("," + list.trim() + ",").indexOf("," + req.paycode + ",");

					if (x > -1)
					{
						Http htp = (Http) row[1];
						return sendMzkSale(htp, req, ret);
					}
				}
			}
		}
		return sendMzkSale(getCardHttp(), req, ret);
	}

	public boolean getMzkInfo(MzkRequestDef req, MzkResultDef ret)
	{
		if (new File(GlobalVar.ConfigPath + "\\mzklist.ini").exists())
		{
			if (cardhttplist == null || cardhttplist.size() <= 0)
			{
				cardhttplist = new Vector();
				BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "\\mzklist.ini");
				String line = null;
				try
				{
					while ((line = br.readLine()) != null)
					{
						if (line.trim().length() < 1)
							continue;

						if (line.charAt(0) == ';')
							continue;

						if (line.trim().indexOf("=") > 0)
						{
							String[] iplist = line.trim().split("=");
							Http a = new Http(iplist[1]);
							a.init();
							a.setConncetTimeout(ConfigClass.ConnectTimeout); // 连接超时
							a.setReadTimeout(ConfigClass.ReceiveTimeout); // 处理超时
							Object[] obj = new Object[] { iplist[0], a };
							cardhttplist.add(obj);
						}
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			if (cardhttplist != null && cardhttplist.size() > 0)
			{
				for (int i = 0; i < cardhttplist.size(); i++)
				{
					Object[] row = (Object[]) cardhttplist.elementAt(i);
					String list = (String) row[0];
					int x = ("," + list.trim() + ",").indexOf("," + req.paycode + ",");

					if (x > -1)
					{
						Http htp = (Http) row[1];
						return sendMzkSale(htp, req, ret);
					}
				}
			}
		}

		return getMzkInfo(getCardHttp(), req, ret);
	}
}
