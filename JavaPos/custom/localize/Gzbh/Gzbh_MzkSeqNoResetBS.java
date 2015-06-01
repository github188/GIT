package custom.localize.Gzbh;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Logic.MzkSeqNoResetBS;

public class Gzbh_MzkSeqNoResetBS extends MzkSeqNoResetBS
{
	
	public boolean resetSeqNo(String seqno)
	{
		if (!checkVaild(seqno)) return false;		
		return WriteMzkSeqno(seqno);
	}
	
	public String getSeqNo(String payCode)
	{
		String seqno = getMzkSeqno(payCode);
		return seqno;
	}
	
	public boolean resetSeqNo(String seqno, String payCode)
	{
		if (!checkVaild(seqno)) return false;		
		return WriteMzkSeqno(seqno, payCode);
	}
	
	protected boolean WriteMzkSeqno(String seq, String payCode)
	{
		PrintWriter pw = null;
		try
		{
			String name = ConfigClass.LocalDBPath + "/" + payCode + "SaleSeqno.ini";
			long seql = Long.parseLong(seq);
			pw = CommonMethod.writeFile(name);
			pw.println(seql);
			pw.flush();
			pw.close();
			pw = null;
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				if (pw != null) pw.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	protected String getMzkSeqno(String payCode)
	{
		PrintWriter pw = null;
		BufferedReader br = null;
		try
		{
			// 读取消费序号
			String name = ConfigClass.LocalDBPath + "/" + payCode + "SaleSeqno.ini";
			File indexFile = new File(name);
			// 无消费序号文件，产生一个
			if (!indexFile.exists())
			{
				pw = CommonMethod.writeFile(name);
				pw.println("1");
				pw.flush();
				pw.close();
				pw = null;
			}
			// 读取消费序号
			br = CommonMethod.readFile(name);
			String line = null;
			long seq = 0;
			while ((line = br.readLine()) != null)
			{
				if (line.length() <= 0)
				{
					continue;
				}
				else
				{
					seq = Long.parseLong(line);
				}
			}
			br.close();
			br = null;
			return String.valueOf(seq);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
		finally
		{
			try
			{
				if (pw != null) pw.close();
				if (br != null) br.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
