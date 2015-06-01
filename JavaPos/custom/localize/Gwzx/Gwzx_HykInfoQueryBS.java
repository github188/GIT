package custom.localize.Gwzx;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

import bankpay.Bank.KgABC1_PaymentBankFunc;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Gwzx_HykInfoQueryBS extends HykInfoQueryBS {

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
		if (ch && rulelist != null && rulelist.size() > 0)
		{
			CustFilterDef rule = (CustFilterDef) rulelist.elementAt(0);
			if (rule.InputType != -2)
				type = rule.InputType;
			

			if(rule.str1 != null && rule.str1.equals("Y"))
			{
				String funcCard = readFuncCard(true);
				
				if(funcCard != null && !funcCard.equals("")){
					return funcCard;
				}
			}
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

		return tr;
	}
	
	public Vector showRule()
	{
//		通过参数控制是否启动VIPcard.ini配置
		if (GlobalInfo.sysPara.custConfig == 'N') return null;
		
		if (!PathFile.fileExist(GlobalVar.ConfigPath + "\\VIPCard.ini"))
			return null;

		BufferedReader br = CommonMethod.readFile(GlobalVar.ConfigPath + "\\VIPCard.ini");
		Vector v = new Vector();

		if (br == null)
			return null;

		String line = null;

		CustFilterDef filterDef = null;
		try
		{
			while ((line = br.readLine()) != null)
			{
				if (line.trim().length() <= 0)
					continue;

				if (line.charAt(0) == ';' || line.charAt(1) == ';')
					continue;

				filterDef = new CustFilterDef();
				String[] rule = line.replaceAll(",,", ", ,").split(",");

				if (rule.length > 0)
				{
					filterDef.desc = rule[0].trim();
				}

				if (rule.length > 1)
				{
					filterDef.chkTrackno = Convert.toInt(rule[1]);
				}

				if (rule.length > 2)
				{
					filterDef.chkLength = rule[2];
				}

				if (rule.length > 3)
				{
					filterDef.chkKeypos = Convert.toInt(rule[3]);
				}

				if (rule.length > 4)
				{
					filterDef.chkkeylen = rule[4];
				}

				if (rule.length > 5)
				{
					filterDef.chkKeyBeginValue = rule[5];
				}

				if (rule.length > 6)
				{
					filterDef.chkKeyEndValue = rule[6];
				}

				if (rule.length > 7)
				{
					filterDef.Trackno = Convert.toInt(rule[7]);
				}

				if (rule.length > 8)
				{
					filterDef.Trackpos = Convert.toInt(rule[8]);
				}

				if (rule.length > 9)
				{
					filterDef.Tracklen = rule[9];
				}

				if (rule.length > 10)
				{
					filterDef.TrackFlag = rule[10];
				}

				if (rule.length > 11)
				{
					filterDef.InputType = Convert.toInt(rule[11]);
				}

				if (rule.length > 12)
				{
					filterDef.ispay = Convert.toInt(rule[12]);
				}
				
				if (rule.length > 13)
				{
					filterDef.str1 = rule[13];//是否选择会员卡刷卡方式 （Y/N）
				}

				v.add(filterDef);
			}

			return v;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String readFuncCard(boolean ispay){
		
		String card="";

		String[] title = { "类型", "会员名称" };
		String[] pthy = {"01","普通会员"};
		String[] mmhy = {"02","密码键盘会员"};//POS密码键盘刷会员卡
		Vector v = new Vector();
		v.add(pthy);
		v.add(mmhy);
		int[] width = { 200, 400 };
		int choice = new MutiSelectForm().open("请选择会员卡类型("+GlobalInfo.ModuleType+")", title, width, v,false,660,319,false);
		if (choice < 0) return null;
		String market = ((String[]) v.elementAt(choice))[0];
		if(market.equals("02")){
			boolean input;
			KgABC1_PaymentBankFunc func = new KgABC1_PaymentBankFunc();
			func.WriteRequestLog(PaymentBank.XKQT3, 0.00, "", "", null);
			input = func.XYKExecute(PaymentBank.XKQT3, 0.00, "", "", "", "","", "", null);
			if (input)
			{
				card = func. getBankLog().cardno;
				if(card!=null){
					card = card.replace("e","=");
				}
			}


			}
		return card;
	}
	
}
