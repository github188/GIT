package custom.localize.Bjys;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Bjys_HykInfoQueryBS extends HykInfoQueryBS
{
	public Bjys_HykInfoQueryBS()
	{
	}

	public int getMemberInputMode()
	{
		return TextBox.MsrKeyInput;
	}

	public String readMemberCard()
	{
		StringBuffer cardno = new StringBuffer();
		String track2;

		String[] title = { "会员卡类型" };
		int[] width = { 500 };
		Vector contents = new Vector();

		contents.add(new String[] { "友谊卡" });
		contents.add(new String[] { "会员手机号" });
		contents.add(new String[] { "理财卡" });
		contents.add(new String[] { "联名信用卡" });

		int choice = new MutiSelectForm().open("请选择卡类型", title, width, contents);
		if (choice == -1)
			return null;

		String hint = ((String[]) contents.elementAt(choice))[0];

		// 输入顾客卡号
		TextBox txt = new TextBox();
		if (!txt.open("请刷" + hint, "会员号", "请将" + hint + "从刷卡槽刷入", cardno, 0, 0, false, getMemberInputMode()))
			return null;

		// 得到顾客卡磁道信息
		track2 = txt.Track2;

		if (choice == 0)
		{
			track2 = txt.Track2;
		}
		// 解析磁道
		else if (choice == 1)
		{
			track2 = "@" + txt.Track2;
		}
		else if (choice == 2)
		{
			if (!txt.Track2.substring(0, 6).trim().equals("622691") && txt.Track2.trim().length() > 20)
			{
				new MessageBox("无效理财卡,请重新刷入!");
				return null;
			}

			track2 = txt.Track2;

			String[] temptrack = track2.split("=");

			track2 = temptrack[0];

			if (track2.length() < 7)
			{
				new MessageBox("无效理财卡,请重新刷入!");

				return null;
			}
			else if (track2.length() == 7)
			{
				track2 = track2.substring(0, track2.length());
			}
			else
			{
				track2 = track2.substring(track2.length() - 8, track2.length() - 1);
			}
		}
		else if (choice == 3)
		{
			if (!txt.Track2.substring(0, 6).trim().equals("518212"))
			{
				new MessageBox("无效联名信用卡,请重新刷入!");
				return null;
			}

			track2 = txt.Track3;

			if (track2 == null || track2.length() < 94)
			{
				new MessageBox("无效联名信用卡,请重新刷入!");

				return null;
			}

			track2 = track2.substring(86, 94);

		}
		else
		{
			return null;
		}

		return track2;
	}

	protected void getHykDisplayInfo(CustomerDef cust, StringBuffer info)
	{
		super.getHykDisplayInfo(cust, info);

		// 增加会员提示描述
		if (cust.valstr1 != null && !cust.valstr1.trim().equals(""))
		{
			info.append(cust.valstr1);
		}
	}
}
