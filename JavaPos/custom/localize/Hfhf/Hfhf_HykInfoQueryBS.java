package custom.localize.Hfhf;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bstd.Bstd_HykInfoQueryBS;
import custom.localize.Hfhf.Hfhf_CoinPurse.VipCoinPurse;

public class Hfhf_HykInfoQueryBS extends Bstd_HykInfoQueryBS
{
	public String readMemberCard(boolean ispay)
	{
		if (!GlobalInfo.isOnline)
		{
			new MessageBox("断网状态下无法使用此功能!");
			return null;
		}

		try
		{
			Vector content = new Vector();
			content.add(new String[] { "商之都贵宾卡" });
			content.add(new String[] { "红府卡" });

			String[] title = { "请选择会员卡" };
			int[] width = { 530 };

			int choice = new MutiSelectForm().open("请选择所要查询的会员卡类型", title, width, content);

			if (choice == -1)
				return null;

			if (choice == 1)
			{
				Hfhf_CrmModule.getDefault().init(false);
				return super.readMemberCard(ispay);
			}

			if (choice == 0)
			{
				StringBuffer cardno = new StringBuffer();
				// 输入顾客卡号
				TextBox txt = new TextBox();
				if (!txt.open("请刷会员卡或顾客打折卡", "会员号", "请将会员卡或顾客打折卡从刷卡槽刷入", cardno, 0, 0, false, TextBox.MsrRetTracks))
					return null;

				if (txt.Track2 == null || txt.Track2.trim().equals(""))
				{
					new MessageBox("未获取到轨道数据");
					return null;
				}

				System.out.println("track:" + txt.Track2);
				txt.Track2 = txt.Track2.replace('/', '?');
				txt.Track2 = txt.Track2.replace("+", "=");

				String AbacusCard = Hfhf_CrmModule.getDefault().getCardNo(txt.Track2);
				if (AbacusCard != null && !AbacusCard.equals(""))
					return "szd" + AbacusCard;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	public CustomerDef findMemberCard(String track2)
	{
		ProgressBox progress = null;
		CustomerDef cust = null;
		try
		{
			progress = new ProgressBox();
			progress.setText("正在查询会员卡信息，请等待.....");

			// 查找会员卡
			cust = new CustomerDef();

			if (track2.startsWith("szd"))
			{
				if (!Hfhf_CrmModule.getDefault().getCustomer(cust, track2.substring(3)))
					return null;

				cust.value2 = GlobalInfo.sysPara.hflczcacctuplimit;
				cust.value4 = GlobalInfo.sysPara.hflczcperje;
			}
			else
			{
				if (!DataService.getDefault().getCustomer(cust, track2))
					return null;
			}

			if (cust.code == null || cust.code.trim().equals(""))
			{
				new MessageBox("查询的会员卡信息无效!");
				return null;
			}
		}
		finally
		{
			if (progress != null)
				progress.close();
		}

		return cust;
	}

	protected void hykDisplayInfo(CustomerDef cust)
	{
		VipCoinPurse payRet = Hfhf_CrmModule.getDefault().queryChangePocket(cust.code, false);
		StringBuffer info = new StringBuffer();

		info.append("卡    号: " + Convert.appendStringSize("", cust.code, 1, 16, 16, 0) + "\n");
		info.append("持 卡 人: " + Convert.appendStringSize("", cust.name, 1, 16, 16, 0) + "\n");
		info.append("卡 状 态: " + Convert.appendStringSize("", cust.status, 1, 16, 16, 0) + "\n");
		info.append("卡 积 分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.valuememo), 1, 16, 16, 0) + "\n");

		if (payRet != null)
			info.append("零钱余额: " + Convert.appendStringSize("", String.valueOf(payRet.Balance), 1, 16, 16, 0) + "\n");

		info.append("会员功能: " + Convert.appendStringSize("", getFuncText(cust.ishy), 1, 16, 16, 0) + "\n");
		info.append("积分功能: " + Convert.appendStringSize("", getFuncText(cust.isjf), 1, 16, 16, 0) + "\n");
		info.append("折扣功能: " + Convert.appendStringSize("", getFuncText(cust.iszk), 1, 16, 16, 0) + "\n");

		new MessageBox(info.toString());
	}
}
