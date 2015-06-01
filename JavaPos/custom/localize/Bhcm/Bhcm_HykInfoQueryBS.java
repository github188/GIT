package custom.localize.Bhcm;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Bhcm_HykInfoQueryBS extends HykInfoQueryBS
{
	public Bhcm_HykInfoQueryBS()
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

		contents.add(new String[] { "借记卡" });
		contents.add(new String[] { "信用卡" });
		contents.add(new String[] { "会员卡" });

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
		PosLog.getLog(getClass()).info("track2Old========>"+track2);
		String [] Trackso = txt.Track2.split(";");
		if (choice == 0)
		{
			String [] Tracks = Trackso[1].split("=");
			String cardCode = txt.Track2.substring(Tracks[0].trim().length()-7, Tracks[0].trim().length());
			
			track2 = cardCode.substring(0,cardCode.length()-1);
		}
		// 解析磁道
		else if (choice == 1)
		{
			String [] Tracks =Trackso[1].split("=");
			String cardCode = txt.Track2.substring(Tracks[0].trim().length()-7, Tracks[0].trim().length());
			
			track2 = cardCode.substring(0,cardCode.length()-1);
		}
		else if (choice == 2)
		{
			track2 = Trackso[1];
		}
		else
		{
			return null;
		}
		PosLog.getLog(getClass()).info("track2========>"+track2);
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
