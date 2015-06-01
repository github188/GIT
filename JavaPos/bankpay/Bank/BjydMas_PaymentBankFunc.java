package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;

public class BjydMas_PaymentBankFunc extends BjydSzwq_PaymentBankFunc
{
	public boolean XYKReadResult()
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist("C:\\BMP\\PFACE.TXT") || ((br = CommonMethod.readFileGBK("C:\\BMP\\PFACE.TXT")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			// 读取请求数据
			String line = br.readLine();

			//
			bld.retcode = Convert.newSubString(line, 0, 2);
			bld.retmsg = Convert.newSubString(line, 2, 42).trim();
			bld.bankinfo = Convert.newSubString(line, 42, 54);
			bld.cardno = Convert.newSubString(line, 54, 74).trim();

			String je = Convert.newSubString(line, 74, 86);
			double j = Double.parseDouble(je);
			j = ManipulatePrecision.mul(j, 0.01);
			bld.je = j;
			
			if (Convert.newSubString(line, 76, 92).trim().length() > 0)
			{
				bld.trace = Convert.toLong(Convert.newSubString(line, 76, 92));
			}
				
//			if (Convert.newSubString(line, 82, 88).length() > 0)
//			{
//				bld.trace = Long.parseLong(Convert.newSubString(line, 82, 88).trim());
//			}

			errmsg = bld.retmsg;
			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
			ex.printStackTrace();
			return false;
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
					// TODO 自动生成 catch 块
					new MessageBox("PFACE.TXT 关闭失败\n重试后如果仍然失败，请联系信息部");
					e.printStackTrace();
				}
			}
		}
	}
}
