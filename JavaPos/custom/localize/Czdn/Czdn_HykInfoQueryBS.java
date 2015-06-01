package custom.localize.Czdn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import net.sf.json.JSONObject;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.UnicodeReader;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;

public class Czdn_HykInfoQueryBS extends HykInfoQueryBS
{
	public String readMemberCard()
	{
		// return super.readMemberCard();
		BufferedReader br = null;
		String line = null;
		String cardNo = null;
		try
		{
			String filePath = "D://CARD.INF";
			// 判断文件是否存在
			File file = new File(filePath);
			if (!file.exists())
			{
				new MessageBox("会员卡文件不存在，请刷卡！");
				return null;
			}
			// 读取文件
			//br = CommonMethod.readFileGBK(filePath);
			FileInputStream fs = new FileInputStream(new File(filePath));
			UnicodeReader ur = new UnicodeReader(fs,"GBK");
			br =  new BufferedReader(ur);
			line = br.readLine().toString();
			if (null == line || ("").equals(line))
			{
				new MessageBox("会员卡文件内容为空，请刷卡！");
				return null;
			}
			// 获取会员卡号
			JSONObject retJson = JSONObject.fromObject(line);
			cardNo = retJson.getString("id");
			fs.close();
			ur.close();
			br.close();
		}
		catch (Exception e)
		{
			PosLog.getLog(this.getClass()).error(e);
			new MessageBox("读取会员卡异常:" + e.getMessage());
		}
		finally
		{
			try
			{
				if (br != null)
					br.close();
			}
			catch (Exception ex)
			{
				PosLog.getLog(this.getClass()).error(ex);
			}
		}
		return cardNo;

	}

	public CustomerDef findMemberCard(String track2)
	{
		ProgressBox progress = null;
		CustomerDef cust = null;
		try
		{
			progress = new ProgressBox();
			progress.setText(Language.apply("正在查询会员卡信息，请等待....."));

			// 查找会员卡
			cust = new CustomerDef();
			if (!DataService.getDefault().getCustomer(cust, track2)) { return null; }
			if (cust.code == null || cust.code.trim().equals(""))
			{
				new MessageBox(Language.apply("查询的会员卡信息无效!\n请找后台人员"));
				return null;
			}
		}
		finally
		{
		/*	try
			{
				PrintWriter pw = new PrintWriter(new File("D://CARD.INF"));
				pw.write("");
				pw.flush();
				pw.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}*/
			
			if (progress != null)
				progress.close();
			//System.gc();
			if(PathFile.isPathExists("D://CARD.INF"))
			{
				PathFile.deletePath("D://CARD.INF");
			}
			
			File file = new File("D://CARD.INF");
		    	
		    if (file.exists())
		    {
		    	new MessageBox(Language.apply("会员卡文件没有删除，请再按一次[F5]键"));
		    }
				
		}
			

		return cust;

	}
	
}
