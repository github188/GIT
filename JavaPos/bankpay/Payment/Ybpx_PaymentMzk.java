package bankpay.Payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Payment.PaymentMzk;

/**
 * 上海亿佰葩鲜
 * @author Administrator
 *
 */
public class Ybpx_PaymentMzk extends PaymentMzk{
	
	protected String errmsg = "验证失败!";
	
	public boolean isPasswdInput()
	{
		return false;
	}
	
	protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
	{
		/*if (GlobalInfo.sysPara.cardpasswd.equals("Y"))
		{
			TextBox txt = new TextBox();

			if (!txt.open(Language.apply("请输入密码"), "PASSWORD", Language.apply("需要先输入卡密码以后才能查询卡资料"), passwd, 0, 0, false, TextBox.AllInput)) { return false; }
		}
		else if (GlobalInfo.sysPara.cardpasswd.trim().length() > 1 && GlobalInfo.sysPara.cardpasswd.trim().charAt(0) == 'Y' && GlobalInfo.sysPara.cardpasswd.indexOf(",") > 0)
		
			if ((GlobalInfo.sysPara.cardpasswd + ",").indexOf("," + paymode.code + ",") > 0)
			{
				TextBox txt = new TextBox();

				if (!txt.open(Language.apply("请输入密码"), "PASSWORD", Language.apply("需要先输入卡密码以后才能查询卡资料"), passwd, 0, 0, false, TextBox.AllInput)) { return false; }
			}
		}*/
		
		
		return execute();
	}
	
	private boolean execute()
	{
	try {
			if (PathFile.fileExist("C:\\Facepay\\request.txt"))
			{
				PathFile.deletePath("C:\\Facepay\\request.txt");
				if (PathFile.fileExist("C:\\Facepay\\reques.txt"))
				{
					errmsg = "交易“request.txt”文件删除失败，请重试！！！";
					new MessageBox(errmsg);
					
					return false;
				}				
			}
			if (PathFile.fileExist("C:\\Facepay\\result.txt"))
			{
				PathFile.deletePath("C:\\Facepay\\result.txt");
				if (PathFile.fileExist("C:\\Facepay\\result.txt"))
				{
					errmsg = "交易“result.txt”文件删除失败，请重试！！！";
					new MessageBox(errmsg);
					
					return false;
				}				
			}
			
			if(!writeRequest(mzkret.str3,mzkret.cardname,String.valueOf(mzkret.money),String.valueOf(mzkreq.seqno),mzkreq.syjh))return false;
			
			if (PathFile.fileExist("C:\\Facepay\\JavaposFacePay.exe"))
			{
				
				CommonMethod.waitForExec("C:\\Facepay\\JavaposFacePay.exe");
				
			}
			else
			{
				errmsg = "接口文件JavaposFacePay.exe不存在！！！";
				new MessageBox(errmsg);
				
				return false;
			}
			
			if(!readResult())return false;
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox("调用接口处理模块异常!!!\n" + e.getMessage() , null, false);
			
			return false;
		}
	}
	
	public boolean writeRequest(String cardPhone,String cardName, String money, String billno, String syjh)
	{
		try
		{
			if(cardPhone == null)cardPhone="";
			if(cardName == null)cardName="";
			
			String line = cardPhone+"|"+cardName+"|"+money+"|"+billno+"|"+syjh;
			PrintWriter pw = null;
			try
			{
				pw = CommonMethod.writeFile("C:\\Facepay\\request.txt");
				if (pw != null)
				{
					pw.println(line);
					pw.flush();
				}					
			}
			finally
			{
				if (pw != null)
				{
					pw.close();
				}
			}
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean readResult()
	{
		BufferedReader br = null;
		try
		{
			
			if (!PathFile.fileExist("C:\\Facepay\\result.txt") || (br = CommonMethod.readFileGB2312("C:\\Facepay\\result.txt")) == null)
			{
				errmsg = "读取应答数据失败！！！\n	";
				new MessageBox(errmsg, null, false);
				
				return false;
			}
			
			String line = null;
			line = br.readLine();
			
			if (line == null || line.length() < 0)
			{
				errmsg = "返回数据错误!";
				return false;
			}				
			String[] str = line.split("|");
			if(str.length > 1)
			{
				line = str[1];
			}
			
			String retcode = line.substring(0,1);
			if (retcode.equals("-1"))
			{
				new MessageBox("验证失败！");
				
				return false;
			}
			else if (retcode.equals("1"))
			{
				new MessageBox("验证成功！");
			}
			
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox("读取返回数据异常" + e.getMessage(), null, false);
			
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
					e.printStackTrace();
				}
			}
		}
	}
	

	/*public boolean findMzk(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && (track2 == null || track2.trim().length() <= 0) && (track3 == null || track3.trim().length() <= 0))
		{
			new MessageBox(Language.apply("磁道数据为空!"));
			return false;
		}

		// 解析磁道
		String[] s = parseTrack(track1, track2, track3);
		if (s == null)
			return false;
		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		// 设置请求数据
		setRequestDataByFind(track1, track2, track3);

		// 设置用户输入密码
		StringBuffer passwd = new StringBuffer();
		if (!getPasswdBeforeFindMzk(passwd))
		{
			return false;
		}
		else
		{
			mzkreq.passwd = passwd.toString();
		}

		//
		return false;
	}*/
	
}
