package custom.localize.Czdn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import net.sf.json.JSONObject;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.UnicodeReader;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Payment.PaymentCoupon;

public class Czdn_PaymentCoupon extends PaymentCoupon {

	public boolean findFjk(String track1, String track2, String track3) 
	{
		BufferedReader br = null;
		String line = null;
		String cardNo = null;
		boolean done = false ;
		try {
			String filePath = "D://CARD.INF";
			// 判断文件是否存在
			File file = new File(filePath);
			if (!file.exists()) {
				new MessageBox("IC卡文件不存在，请刷卡！");
				return false;
			}
			// 读取文件
			//br = CommonMethod.readFileGBK(filePath);
			//br =  new BufferedReader(new UnicodeReader(new FileInputStream(new File(filePath)), "GBK"));
			FileInputStream fs = new FileInputStream(new File(filePath));
			UnicodeReader ur = new UnicodeReader(fs,"GBK");
			br =  new BufferedReader(ur);
			line = br.readLine().toString();
			if (null == line || ("").equals(line)) {
				new MessageBox("IC卡文件内容为空，请刷卡！");
				return false;
			}
			// 获取会员卡号
			JSONObject retJson = JSONObject.fromObject(line);
			cardNo = retJson.getString("id");
			fs.close();
			ur.close();
			br.close();

			track1 = "";
			track2 = cardNo;
			track3 = "";

			done = super.findFjk(track1, track2, track3);
		} catch (Exception e) 
		{
			PosLog.getLog(this.getClass()).error(e);
			new MessageBox("读取IC卡异常:" + e.getMessage());
		} finally
		{
			try 
			{
				if (br != null)
					br.close();
				//System.gc();
				if(PathFile.isPathExists("D://CARD.INF"))
				{
					PathFile.deletePath("D://CARD.INF");
				}
				File file = new File("D://CARD.INF");
		    	
			    if (file.exists())
			    {
			    	new MessageBox(Language.apply("会员卡文件没有删除。"));
			    }
				
			} catch (Exception ex) 
			{
				PosLog.getLog(this.getClass()).error(ex);
			}
		}
		return done;
	}

}
