package custom.localize.Bxmx;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.MzkRechargeBS;
import com.efuture.javaPos.PrintTemplate.MzkRechargeBillMode;

public class Bxmx_MzkRechargeBS extends MzkRechargeBS
{
	private double curjf;

	public boolean verifyMessage(String card, String money)
	{
		StringBuffer jf = new StringBuffer();

		if (new TextBox().open("请输入老卡积分数:", "积分数", "积分   按【退出键】取消", jf, 0, 100000, true, TextBox.DoubleInput))
		{
			curjf = Convert.toDouble(jf.toString());
		}
		else
		{
			jf = null;
		}

		StringBuffer info = new StringBuffer();
		info.append("请仔细核对下列充值信息\n\n");
		info.append("卡    号:" + Convert.appendStringSize("", card, 1, 12, 16, 0) + "\n");
		info.append("金    额:" + Convert.appendStringSize("", money + " 元", 1, 12, 16, 0));

		if (jf != null)
			info.append("\n积    分:" + Convert.appendStringSize("", +curjf + " 元", 1, 12, 16, 0));

		if (new MessageBox(info.toString(), null, true).verify() != GlobalVar.Key1)
			return false;

		return true;
	}

	public void setRequestDataByFind(String track1, String track2, String track3)
	{
		// 根据磁道生成查询请求包
		mzkreq.type = "05"; // 查询类型
		mzkreq.seqno = 0;
		mzkreq.termno = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.fphm = GlobalInfo.syjStatus.fphm;
		mzkreq.invdjlb = "";
		mzkreq.paycode = "04";
		mzkreq.je = 0;
		mzkreq.track1 = "CHANGE"; // 换卡
		mzkreq.track2 = track2;
		mzkreq.track3 = track3;
		mzkreq.passwd = "";
		mzkreq.memo = "";
	}

	protected boolean setRequestDataByAccount(double money)
	{
		// 得到消费序号
		long seqno = getMzkRechargeSeqno();
		if (seqno <= 0)
			return false;

		// 打消费交易包
		mzkreq.seqno = seqno;
		mzkreq.je = money;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.paycode = "04";
		mzkreq.invdjlb = "";
		mzkreq.memo = String.valueOf(curjf);

		// 告诉后台过程磁道信息是存放的是卡号,只采用卡号记账方式,不使用磁道记账方式
		mzkreq.track1 = "CHANGE"; // 换卡
		mzkreq.track2 = mzkret.cardno;

		return true;
	}

	public boolean writeMzkRechargeCz()
	{
		//充值交易不写冲正
		if (mzkreq.type.equals("06"))
			return true;

		return super.writeMzkRechargeCz();
	}
	
	public long getMzkRechargeSeqno()
	{
		PrintWriter pw = null;
		BufferedReader br = null;

		try
		{
			// 读取消费序号
			String name = ConfigClass.LocalDBPath + "/MzkRecharge.ini";
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
			if (seq < 999999999)
				pw.println(seq + 1);
			else
				pw.println(1);
			pw.flush();
			pw.close();
			pw = null;

			 //防止日期重复，前面加上时间字段如果是2013年10月10日，记录为131010+seq
            String empty = GlobalInfo.balanceDate.replace("/", "").replace("-", "").substring(2);
            empty = empty+String.valueOf(seq);
            seq = Convert.toLong(empty);
            
			return seq;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox("读取消费序号失败!\n\n" + e.getMessage().trim());

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
	
	public boolean printRechargeBill()
	{
		try
		{
			MzkRechargeBillMode.getDefault().setTemplateObject(mzkreq, mzkret);
			
			if (MzkRechargeBillMode.getDefault().isLoad())
			{
				int x = 0;
				while (x < 2)
				{
					MzkRechargeBillMode.getDefault().printBill();
					x++;
				}
			}
				

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
}
