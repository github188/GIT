package custom.localize.Cqhq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import bankpay.Bank.YlswCqHq_PaymentBankFunc;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Cqhq_HykInfoQueryBS extends HykInfoQueryBS
{
	String cardno = "";
	String path = "C:\\gmc";
	String crc ;
	
	
	public Vector chooseRule(Vector rulelist, boolean ispay)
	{
		//通过参数控制是否启动VIPcard.ini配置
		if (GlobalInfo.sysPara.custConfig == 'N') return null;
		
		Vector con = new Vector();
		for (int i = 0; i < rulelist.size(); i++)
		{
			CustFilterDef filterDef = (CustFilterDef) rulelist.elementAt(i);

			if (filterDef.ispay == 1 && ispay)
				continue;
			con.add(new String[]{ i + 1 +"",filterDef.desc });
		}
		String[] title = { "代码","会员卡类型" };
		int[] width = { 60, 440 };

		int choice = new MutiSelectForm().open("请选择卡类型", title, width, con,true);
		if (choice == -1)
			return null;

		CustFilterDef rule = ((CustFilterDef) rulelist.elementAt(choice));
		rulelist.removeAllElements();
		rulelist.add(rule);

		return rulelist;
	}

	public String readMemberCard(boolean ispay)
	{
		selectedRule = null;

		StringBuffer cardno = new StringBuffer();

		// 获取自定义的解析规则
		Vector rulelist = showRule();
		
		
		
		if (rulelist != null && rulelist.size() <= 0)
			rulelist = null;

		// 先选择规则后刷会员卡
		boolean ch = false;
		if (GlobalInfo.sysPara.unionVIPMode == 'A')
		{
			if (rulelist != null && rulelist.size() > 1)
			{
				rulelist = chooseRule(rulelist, ispay);
				if (rulelist != null)
					ch = true;
			}
			else if (rulelist != null && rulelist.size() == 1)
				ch = true;
		}

		int type = getMemberInputMode();
		CustFilterDef rule = null;
		if (ch && rulelist != null && rulelist.size() > 0)
		{
			rule = (CustFilterDef) rulelist.elementAt(0);
			if (rule.InputType != -2)
				type = rule.InputType;
		}

		// 输入顾客卡号
		TextBox txt = new TextBox();
		if (!txt.open("请刷会员卡或顾客打折卡", "会员号", "请将会员卡或顾客打折卡从刷卡槽刷入", cardno, 0, 0, false, type)) { return null; }

		// 调用客户化会员磁道解析程序
		String tr = getTrackByCustom(txt.Track2);
		

		// 检查磁道是否和规则相匹配
		if (rulelist != null && rulelist.size() > 0)
		{
			rulelist = chkTrack(txt.Track1, txt.Track2, txt.Track3, rulelist, ispay);
		}

		// 如果匹配的规则有多个,再次让客户选择(B模式先刷卡后选择)
		if (rulelist != null && rulelist.size() > 1)
		{
			rulelist = chooseRule(rulelist, ispay);
		}

		// 解析有效规则下的磁道号
		if (rulelist != null && rulelist.size() > 0)
		{
			selectedRule = (CustFilterDef) rulelist.get(0);
			tr = getTrackByDefine(txt.Track1, txt.Track2, txt.Track3, rulelist);
		}
		else
		{
			if (ch)
			{
				new MessageBox("刷卡与联名卡规则不匹配，该卡无效");
				return null;
			}
		}

		// 记录磁道供返立即查询券卡使用
		if (GlobalInfo.sysPara.findcustfjk == 'Y')
		{
			custtrack = new String[] { txt.Track1, (tr == null) ? txt.Track2 : tr, txt.Track3 };
		}
		
		//当输入的是手机号时，在前面加 “@”符号
		if (null != tr  && tr.length() == 11)
		{
			tr = "@" + tr;
		}

		return tr;
	}
	
}