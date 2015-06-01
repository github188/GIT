package custom.localize.Szxw;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Szxw_HykInfoQueryBS extends HykInfoQueryBS
{

	private String[] track;

	public Szxw_HykInfoQueryBS()
	{
	}

	public int getMemberInputModeByType(String cardType)
	{
		if (cardType.equals("西武卡"))
		{
			return TextBox.MsrInput;
		}
		else if (cardType.equals("电  话"))
		{
			return TextBox.IntegerInput;
		}
		else
		{
			return TextBox.MsrKeyInput;
		}
	}

	public String readMemberCard()
	{
		StringBuffer cardno = new StringBuffer();
		String track2;

		String[] title = { "会员卡类型" };
		int[] width = { 500 };
		Vector contents = new Vector();
		contents.add(new String[] { "西武卡" });
		contents.add(new String[] { "联名卡" });
		contents.add(new String[] { "电  话" });
		
		int choice = new MutiSelectForm().open("请选择卡类型", title, width, contents);
		if (choice == -1) return null;
		String hint = ((String[]) contents.elementAt(choice))[0];

		//输入顾客卡号
		TextBox txt = new TextBox();
		if (!txt.open("请刷" + hint, "会员号", "请将" + hint + "从刷卡槽刷入", cardno, 0, 0, false, getMemberInputModeByType(hint.trim()))) { return null; }

		//得到顾客卡磁道信息
		
		if (choice == 2) track2 = cardno.toString();
		else track2 = txt.Track2;

		//解析磁道

		if (choice == 2)
		{
			track2 = "@"+track2;
		}
		//判断是否存在“=”
		if (track2.indexOf('=') != -1)
		{
			//截取“=”前的号码
			track2 = track2.substring(0, track2.indexOf('='));
		}

		track = new String[] { txt.Track1, txt.Track2, txt.Track3, };

		return track2;
	}

	protected void hykDisplayInfo(CustomerDef cust)
	{
		super.hykDisplayInfo(cust);

		MessageBox me = new MessageBox("是否立即查询卡中电子券的余额?", null, true);

		if (me.verify() == GlobalVar.Key1)
		{
			CustomLocalize.getDefault().createFjkInfoQueryBS().QueryFjkInfo(track);
		}
	}

}
