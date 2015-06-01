package custom.localize.Nbbh;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.ManipulateDateTime;
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
import com.efuture.javaPos.Global.Language;

import custom.localize.Cmls.Cmls_NetService;

public class Nbbh_NetService extends Cmls_NetService {
	//银行卡号，银行行号，折扣信息
	public boolean getNewBankZsInfo(String bankCardNo, String bankNo, String[] zkInfo)
	{		
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { ManipulateDateTime.getCurrentDate(), GlobalInfo.sysPara.jygs, GlobalInfo.sysPara.mktcode, bankCardNo, bankNo, "", "", "" };
		String[] args = { "i_Date", "Jygs", "Mkt", "Bankno", "bankcode", "instr1", "instr2", "instr3" };

		try
		{
			head = new CmdHead(CmdDef.FINDCREDITZK_BANKZS);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			PosLog.getLog(getClass()).info("getNewBankZsInfo() START i_Date=[" + ManipulateDateTime.getCurrentDate() + "],Jygs=[" + GlobalInfo.sysPara.jygs + "],Mkt" +  GlobalInfo.sysPara.mktcode + "],Bankno=[" + bankCardNo + "],bankcode=[" + bankNo + "]");
			result = HttpCall(getMemCardHttp(CmdDef.FINDCREDITZK_BANKZS), line, Language.apply("没有找到该银行卡的追送折扣率"));

			if (result == 0)
			{
				String[] retname = {"Retzkl", "Retzkxe", "retbillno", "restr1", "restr2", "restr3"};
				Vector v = new XmlParse(line.toString()).parseMeth(0, retname);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);
					zkInfo[0] = row[0];//"0.9";
					zkInfo[1] = row[1];//"20";
					zkInfo[2] = row[2];//"DH0001";
					PosLog.getLog(getClass()).info("getNewBankZsInfo() 成功 Retzkl=[" + row[0] + "],Retzkxe=[" + row[1] + "],retbillno=[" + row[2] + "]");
					return true;
				}
			}
			PosLog.getLog(getClass()).info("getNewBankZsInfo() 失败");
		}
		catch (Exception ex)
		{
			PosLog.getLog(getClass()).error(ex);
			PosLog.getLog(getClass()).info(ex);
		}

		return false;
	}
	public boolean getTktrule_gs()
	{
		//可参考 getSyjGrange() 函数
		if (!GlobalInfo.isOnline) { return false; }
		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = {GlobalInfo.sysPara.mktcode};
		String[] args = { "rfmkt" };

		try
		{
			aa = new CmdHead(Nbbh_CmdDef.NBBH_FINDTKTRULE_GS);
			line.append(aa.headToString() + Transition.SimpleXML(values, args));		
			result = HttpCall(line, Language.apply("获取打印规则信息失败!"));

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, RulesDef.ref);

				Nbbh_AccessLocalDB  accessLocalDB = (Nbbh_AccessLocalDB)AccessLocalDB.getDefault();
				// 写入本地数据库
				if (!accessLocalDB.writeRules(v))
				{
					new MessageBox(Language.apply("保存打印规则信息失败!"));
				}

				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return false;
		}
	}
	
	
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
				if (!new Nbbh_AccessLocalDB().writeReprint(v, done))
				{
					new MessageBox(Language.apply("保存重打原因失败!"));
				}
				
				return true;
			}
			else
			{
				new MessageBox(Language.apply("下载重打原因失败!"));
				
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

			if (result == 0)
			{
//				String line1 = "";
//				for(int i=0;i<yy.length;i++)
//				{
//					line1 += yy[i] + ",";
//				}
//				new MessageBox("发票信息："+ line1);
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
