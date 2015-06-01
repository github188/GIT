package custom.localize.Wjyt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Logic.HykInfoQueryBS;

public class Wjyt_HykInfoQueryBS extends HykInfoQueryBS
{
	public String getTrackByCustom(String track2)
	{
		//不存在磁道解析程序时，直接返回track2;
		if (!PathFile.fileExist("c:\\javapos\\MemberDecrypt.exe"))
			return track2;
		
		//当存在解析程序，但传入的track2为非法值时，直接返回track2
		if (track2==null || track2.equals(""))
			return track2;
		
		//当存在解析程序，且传入的track2合法时，但不符合解析程序调用约定时，会将传入的值直接返回
		Process process = null;
		InputStream input = null;
		BufferedReader br = null;
		String tmpCard = null;
		String retCard = null;
		
		try
		{
			//只接受 7524776077673675247760776736 长度大于14 此类的值
			process = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", "c:\\javapos\\MemberDecrypt.exe " + track2 }, null, null);
			input = process.getInputStream();
			br = new BufferedReader(new InputStreamReader(input));

			if ((tmpCard = br.readLine()) != null)
			{
				// 解析出问题时给出提示并直接将track2传回
				if (tmpCard.equals("0"))
				{
					new MessageBox("卡号解析失败");
					retCard = track2;
				}
				else if (tmpCard.equals("-1"))
				{
					new MessageBox("卡号解析产生异常");
					retCard = track2;
				}
				else
					retCard = tmpCard;
			}
			process.waitFor();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			retCard = track2;
		}
		finally
		{
			try
			{
				br.close();
				input.close();
			}
			catch (Exception ex)
			{
				br = null;
				input = null;
			}
		}
		return retCard;
	}
}
