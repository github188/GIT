package custom.localize.Djgc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Payment.PaymentBank;

import custom.localize.Cmls.Cmls_NetService;

public class Djgc_NetService extends Cmls_NetService
{
	static String filepath = "C:\\javapos\\reprint.txt";
	// 得到网络系统参数
	public boolean getSysPara()
	{
		if(GlobalInfo.isOnline)
		{
			if(new File(filepath).exists())
			{
				String[] yy = null;;
				Vector vv = readReprint();
				for(int i = 0; i < vv.size(); i++)
				{
					yy = (String[])vv.elementAt(i);
					if(!postReprint(yy) || !GlobalInfo.isOnline)
					{
						break;
					}					
				}
				new File(filepath).delete();
			}


			getReprint(GlobalInfo.localHttp, true, CmdDef.JAVA_INVOICE_POSTWHY);
		}
		
		return getSysPara(GlobalInfo.localHttp, true, CmdDef.GETSYSPARA);
	}
	
	
	// 得到小票重打原因
	public boolean getReprint(Http http, boolean done, int ID)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		String mktcode = null;
		if (ConfigClass.ck == 'N')
			mktcode = GlobalInfo.sysPara == null ? "" : GlobalInfo.sysPara.mktcode;
		else
			mktcode = "#" + ConfigClass.Market;
		
		String[] values = { mktcode };
		String[] args = { "mktcode" };

		try
		{
			aa = new CmdHead(ID);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));

			if (http != null)
				result = HttpCall(http, line, Language.apply("获取重打原因失败!"));
			else
				result = HttpCall(line, Language.apply("获取重打原因失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, ReprintDef.ref);

				// 写入本地数据库
				if (!new Djgc_AccessLocalDB().writeReprint(v, done))
				{
					new MessageBox(Language.apply("保存重打原因失败!"));
				}
				
				return true;
			}
			else
			{
				new MessageBox(Language.apply("保存重打原因失败!"));
				
				return false;
			}	
			
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return false;
		}
	}
	
	//上传小票重打原因
	public boolean postReprint( String[] yy )
	{
		return postReprint( yy ,GlobalInfo.localHttp, true, CmdDef.JAVA_INVOICE_GETWHY);
	}
	
	public boolean postReprint(String[] yy ,Http http, boolean done, int ID)
	{
		if (!GlobalInfo.isOnline) 
		{ 
			//脱网状态重印，将重印原因保存在本地
			saveReprint(yy);
			return true;
		}

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		String mktcode = null;
		if (ConfigClass.ck == 'N')
			mktcode = GlobalInfo.sysPara == null ? "" : GlobalInfo.sysPara.mktcode;
		else
			mktcode = "#" + ConfigClass.Market;
		
//		String[] values = { mktcode };
		String[] args = { "mktcode" ,"syjh" ,"fphm" ,"printype" ,"printid" ,"gh" ,"fp1" ,"fp2" ,"printtime"};

		try
		{
			aa = new CmdHead(ID);
			line.append(aa.headToString() + Transition.SimpleXML(yy, args));

			if (http != null)
				result = HttpCall(http, line, Language.apply("发送重打原因失败!"));
			else
				result = HttpCall(line, Language.apply("发送重打原因失败!"));

			if (result == 1)
			{
				return true;
			}
			else
			{
				new MessageBox(Language.apply("上传重打原因失败!"));
				
				saveReprint(yy);
				
				return false;
			}	
			
			
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return false;
		}
	}
	
	public Vector readReprint()
	{
		BufferedReader br = null;
		Vector v = new Vector();
		
		try
        {
			if (!PathFile.fileExist(filepath) || ((br = CommonMethod.readFileGBK(filepath)) == null))
            {
                new MessageBox("读取重打原因数据失败!", null, false);
                
                return null;
            }
			
			String line = null;
			
			while ((line = br.readLine()) != null)
			{
				String result[] = line.split(",");
				v.add(result);
			}
			return v;
        }
		catch (Exception ex)
		{
            new MessageBox("读取重打原因数据异常!" + ex.getMessage(), null, false);
            ex.printStackTrace();
            
			return null;
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
	}
	
	public void saveReprint(String[] yy)
	{
		File file = new File(filepath);
		if (!file.exists())
		{
			try{
				file.createNewFile();
			}
			catch(Exception e)
			{
					e.printStackTrace();
			}
		}	
		
		
		try {			
				InputStreamReader is = new InputStreamReader(new FileInputStream(filepath), "gbk");
				File f = new File(filepath);			
				
				String line = yy[0].toString();
				for(int i = 1;i<=yy.length-1;i++)
				{
					line += ","+yy[i].toString();
				}
				
				if(line != null) 
				{
					BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
					bw.write(line);	
					bw.newLine();
					bw.close();			
				}		
			}
				catch (Exception e)
			{			
				System.out.println("error:" + e);		
			}
	}
	
//	public static String salefphm()
//	{
//		BufferedReader br = null;
//		String line = null;
//		
//		int InvoiceStart = 0;
//		int InvoiceNum = 0;
//		
//		boolean flog = false;
//		try
//		{
//			if (new File(GlobalVar.ConfigPath + "//SaleFphm.ini").exists())
//			{
//				br = CommonMethod.readFile(GlobalVar.ConfigPath + "//SaleFphm.ini");
//				
//				if (br == null)
//				{
//					new MessageBox(com.efuture.javaPos.Global.Language.apply("读取配置文件SaleFphm.ini错误"), null, false);
//	
//					return null;
//				}
//				
//				
//				while (flog != true)
//				{
//					line = br.readLine();
//					
//					if ((line == null) || (line.length() <= 0))
//					{
//						continue;
//					}
//
//					String[] lines = line.split("=");
//					if (lines.length < 2)
//						continue;
//
//					if (lines[0].trim().compareToIgnoreCase("InvoiceStart") == 0)
//					{
//						InvoiceStart = Convert.toInt(lines[1].trim());
//					}
//					else if (lines[0].trim().compareToIgnoreCase("InvoiceNum") == 0)
//					{
//						InvoiceNum = Convert.toInt(lines[1].trim());
//						flog = true;
//						
//					}
//				}
//				br.close();
//				
//				return String.valueOf(InvoiceStart) + "," + String.valueOf(InvoiceNum);
//						
//			}
//		}
//		catch (IOException e)
//		{	
//			e.printStackTrace();
//			
//		}
//		return null;
//	}
	
}
