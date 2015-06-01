package custom.localize.Tygc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;

import custom.localize.Cmls.Cmls_NetService;
import custom.localize.Tygc.Tygc_AccessLocalDB;
import custom.localize.Tygc.ReprintDef;

public class Tygc_NetService extends Cmls_NetService
{
	public boolean getSaleGoodsApportion(String syjh, String fphm, Vector saleApportionDetailList)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode,syjh, fphm };
		String[] args = { "mkt","syjh", "fphm" };

		try
		{
			// 查询退货小票头
			head = new CmdHead(CmdDef.GETSALEGOODSAPPORTION);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox("商品付款方式分摊查询失败!");
				return false;
			}
                                                                                //商品编码 商品行号 付款行号 付款方式代码 商品分摊金额
			Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "gdcode","gdrow","rowno","paycode","gdmoney" });

			if (v.size() < 1)
			{
				//new MessageBox("没有查询到商品付款方式分摊!");
				return false;
			}
			saleApportionDetailList.removeAllElements();
			for (int i = 0; i < v.size(); i++)
			{
				String[] row = (String[]) v.elementAt(i);
				saleApportionDetailList.add(row);
			}

			return true;
		}
		catch (Exception ex)
		{

			if (saleApportionDetailList != null)
			{
				saleApportionDetailList.clear();
				saleApportionDetailList = null;
			}
			ex.printStackTrace();
			return false;
		}
		finally
		{
			head = null;
			line = null;
		}
	}
	
	static String filepath = "C:\\javapos\\reprint.txt";
	
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
				if (!new Tygc_AccessLocalDB().writeReprint(v, done))
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
		String[] args = { "mktcode" ,"syjh" ,"fphm" ,"printype" ,"printid" ,"gh" ,"fp1" ,"fp2" ,"printtime","kpje"};

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
}
