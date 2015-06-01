package custom.localize.Tygc;


import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;

public class Tygc_HykInfoQueryBS extends HykInfoQueryBS
{
	public String getTrackByCustom(String track2)
	{
		String hykh = "";
		if(track2.indexOf("=")==-1){
			hykh = getCardNo(track2);
		}else{
			hykh = track2;
		}
		return hykh;
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
		if (ch && rulelist != null && rulelist.size() > 0)
		{
			CustFilterDef rule = (CustFilterDef) rulelist.elementAt(0);
			if (rule.InputType != -2)
				type = rule.InputType;
		}
		
		//add 如果type = 10 则textbox中type传入3  且不需要解析磁道信息（getTrackByCustom()）
		int isTrackByCustom = type;
		if(isTrackByCustom == 10){
			type = 3;
		}
		// 输入顾客卡号
		TextBox txt = new TextBox();
		if (!txt.open("请刷会员卡或顾客打折卡", "会员号", "请将会员卡或顾客打折卡从刷卡槽刷入", cardno, 0, 0, false, type)) { return null; }

		String tr = txt.Track2;

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
//		 调用客户化会员磁道解析程序
		if(isTrackByCustom != 10){
			tr = getTrackByCustom(tr);
		}
		
		return tr;
	}

	public String  getCardNo(String track2)
	{
		System.out.println("传入:"+track2);
		PointerByReference a = new PointerByReference();  
		PointerByReference a1 = new PointerByReference();
		int i = MagRO.INSTANCE.MagDecNumChar(a,a1,track2);
		if (i == 0){
			String b = a.getValue().getString(0);
			System.out.println("传出:"+b);
			return b;
		}			
		else{
			new MessageBox("解析会员卡号失败!");
			System.out.println("读取失败");
			return null;
		}
		
	}
	
	
	public interface MagRO extends Library { 

        /**

         * 当前路径是在项目下，而不是bin输出目录下。

         */

    	MagRO INSTANCE = (MagRO)Native.loadLibrary("D:\\JavaPOS\\MagRO", MagRO.class);

        public int MagDecNumChar(PointerByReference value,PointerByReference value1,String value2);

        

}
}
