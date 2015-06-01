package custom.localize.Agog;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;

public class Agog_Util
{
	public static String getSeqno()
	{
		PrintWriter pw = null;
		BufferedReader br = null;

		try
		{
			// 读取消费序号
			String name = ConfigClass.LocalDBPath + "/aggSeqno.ini";
			File indexFile = new File(name);

			// 无消费序号文件，产生一个
			if (!indexFile.exists())
			{
				pw = CommonMethod.writeFile(name);
				pw.println("0");
				pw.flush();
				pw.close();
				pw = null;
			}

			// 读取消费序号
			br = CommonMethod.readFile(name);
			String line = null;
			int seq = 0;

			while ((line = br.readLine()) != null)
			{
				if (line.length() <= 0)
				{
					continue;
				}
				else
				{
					seq = Integer.parseInt(line.trim());
				}
			}
			br.close();
			br = null;

			// 消费序号+1
			pw = CommonMethod.writeFile(name);
			if (seq < 999)
				pw.println(seq + 1);
			else
				pw.println(0);
			pw.flush();
			pw.close();
			pw = null;

			return ManipulateStr.PadLeft(String.valueOf(seq), 3, '0');
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox("读取会员消费序号失败!\n\n" + e.getMessage().trim());

			return "";
		}
		finally
		{
			try
			{
				if (pw != null)
					pw.close();
				if (br != null)
					br.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static String getSaleDate()
	{
		Date myDate = new Date(System.currentTimeMillis());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		//System.out.println(sdf.format(myDate));
		return sdf.format(myDate);
	}

	
	
	public static void main(String[] args)
	{
		System.out.println(Agog_Util.getSaleDate());
	}
}
