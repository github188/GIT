package custom.localize.Doug;

import java.util.Vector;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.LineDisplay;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Doug_HykInfoQueryBS extends HykInfoQueryBS
{

    public Doug_HykInfoQueryBS()
    {
    }
    
    public String readMemberCard()
    {
    	StringBuffer cardno = new StringBuffer();
    	String track2;
    	
    	String[] title = {"卡类型"};
		int[] width = {500};
		Vector contents = new Vector();
		contents.add(new String[]{"VIP卡"});
		contents.add(new String[]{"招行联名VIP卡"});
		contents.add(new String[]{"VIP金卡折上折"});
		contents.add(new String[]{"招行联名VIP金卡折上折"});
		
		int choice = new MutiSelectForm().open("请选择卡类型", title, width, contents);
		if (choice == -1) return null;
		String hint = ((String[])contents.elementAt(choice))[0];
		
		// 输入顾客卡号
    	TextBox txt = new TextBox();
        if (!txt.open("请刷" + hint, "会员号", "请将" + hint + "从刷卡槽刷入", cardno, 0, 0,false, getMemberInputMode()))
        {
            return null;
        }

        // 得到顾客卡磁道信息
		if (choice == 0 || choice == 2)
		{
			track2 = txt.Track2;
		}
		else
		{
			if (txt.Track3 == null || txt.Track3.length() < 78)
			{
				new MessageBox("无效联名VIP卡,请重新刷卡!");
				return null;
			}

			track2 = txt.Track3.substring(69,78);
		}
		
		return track2 + "," + choice;
    }    
    
    public void QueryHykInfo()
    {
    	// 读会员卡
    	String track2 = readMemberCard();
        if (track2 == null || track2.equals("")) return;

        // 解析出磁道和选择的类型
        String[] s = track2.split(",");
        track2 = s[0];
        
        // 查找会员卡
        CustomerDef cust = findMemberCard(track2);
        if (cust == null) return;
        
		// 在客显上显示卡号及余额
        LineDisplay.getDefault().displayAt(0, 1, cust.code);
        LineDisplay.getDefault().displayAt(1, 1, ManipulatePrecision.doubleToString(cust.valuememo));
		
        // 显示卡信息
		hykDisplayInfo(cust);
    }

}
