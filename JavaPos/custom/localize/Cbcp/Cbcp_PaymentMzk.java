package custom.localize.Cbcp;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.ICCard;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Payment.PaymentMzk;

public class Cbcp_PaymentMzk extends PaymentMzk
{

	protected String autoTrack1 = null;
	protected String autoTrack2 = null;
	protected String autoTrack3 = null;
	
	public boolean autoFindCard()
	{
		ProgressBox pb = null;
		try
		{
			if(!isAutoFindCard())return true;
			
			this.autoTrack1 = null;
			this.autoTrack2 = null;
			this.autoTrack3 = null;
			
			//读取卡号
			pb = new ProgressBox();
			pb.setText("请在银联设备上刷卡...");
			String strTrack = ICCard.getDefault().findCard();
			PosLog.getLog(this.getClass().getSimpleName()).info("银联设备上刷卡 strTrack=[" + String.valueOf(strTrack) + "].");
			if(strTrack==null)
			{
				new MessageBox("从银联设备上读卡失败！");
				return false;
			}
			String[] arrTrack = strTrack.split(";");
			this.autoTrack1 = arrTrack[0].trim();
			if(arrTrack.length>1) this.autoTrack2 = arrTrack[1].trim();
			if(arrTrack.length>2) this.autoTrack3 = arrTrack[2].trim();
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		finally
		{
			if(pb!=null) 
			{
				pb.close();
				pb=null;
			}
		}
		return false;
	}
	
	public boolean isAutoFindCard()
	{
		try
		{
			if(GlobalInfo.sysPara.isUseBankReadTrack=='Y') return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		if(isAutoFindCard())
		{
			track1=this.autoTrack1;
			track2=this.autoTrack2;
			track3=this.autoTrack3;
			PosLog.getLog(this.getClass().getSimpleName()).info("findMzk() track1=[" + String.valueOf(autoTrack1) + "],track2=[" + String.valueOf(autoTrack2) + "],track3=[" + String.valueOf(autoTrack3) + "].");
		}
		return super.findMzk(track1, track2, track3);
	}
	
	protected boolean saveFindMzkResultToSalePay()
	{
		if(super.saveFindMzkResultToSalePay())
		{

			salepay.str1 = mzkret.str1;
			return true;
		}
		return false;
	}

	protected boolean setRequestDataByAccount()
	{
		if(super.setRequestDataByAccount())
		{
			mzkreq.track2 = salepay.str1;
			return true;
		}
		return false;
	}
	
	public long getMzkSeqno()
	{
		PrintWriter pw = null;
		BufferedReader br = null;

		try
		{
			// 读取消费序号
			String name = ConfigClass.LocalDBPath + "/SaleSeqno.ini";
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
					seq = Convert.toLong(line.trim());
				}
			}
			br.close();
			br = null;

			// 消费序号+1
			pw = CommonMethod.writeFile(name);

			//9999够用一天了
			if (seq < 9999)
				pw.println(seq + 1);
			else
				pw.println(1);
			pw.flush();
			pw.close();
			pw = null;

			// 防止日期重复，前面加上时间字段如果是2013年10月10日，记录为31010+seq
			//如果按seq为9位，那再加上日期6位，就15位了，但是R5那边记录seqno为Int型，最大只能保存10位数
			//所以，动不动就会出现[Microsoft][SQLServer 2000 Driver for JDBC][SQLServer]从数据类型 bigint 转换为 int 时出错
			//综合考虑，改成5位日期加4位seq，共计9位
			String empty = GlobalInfo.balanceDate.replace("/", "").replace("-", "").substring(3);
			empty = empty + String.valueOf(seq);
			seq = Convert.toLong(empty);
			
			//重百现在序号为6位，超过6位会有问题。
			if(String.valueOf(seq).length() > 6)
			{
				seq = Convert.toLong(String.valueOf(seq).substring(String.valueOf(seq).length()-6,String.valueOf(seq).length()));
			}
			
			return seq;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox(Language.apply("读取消费序号失败!\n\n") + e.getMessage().trim());

			return -1;
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
}
