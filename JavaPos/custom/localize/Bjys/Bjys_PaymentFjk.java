package custom.localize.Bjys;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentFjk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;


public class Bjys_PaymentFjk extends PaymentFjk
{
	public Bjys_PaymentFjk()
	{
		super();
	}
	
	public Bjys_PaymentFjk(PayModeDef mode,SaleBS sale)
	{
		initPayment(mode,sale);
	}

	public Bjys_PaymentFjk(SalePayDef pay,SaleHeadDef head)
	{
		initPayment(pay,head);
	}

	public int choicFjkType()
	{
        String[] title = { "返券卡类型" };
        int[] width = { 500 };
        Vector contents = new Vector();
        contents.add(new String[] { "友谊卡" });
        contents.add(new String[] { "理财卡" });
        contents.add(new String[] { "联名信用卡" });

        fjktypeChoice = new MutiSelectForm().open("请选择卡类型", title, width, contents);
		
		return fjktypeChoice;
	}
	
	public int getAccountInputMode()
	{
		return TextBox.MsrInput;
	}
	
	public String[] parseFjkTrack(String track1, String track2, String track3)
	{
        //解析磁道
        if (fjktypeChoice == 0)
        {
    		String[] s = new String[3];
			s[0] = track1;
			s[1] = track2;
			s[2] = track3;
			return s;
        }
        else if (fjktypeChoice == 1)
        {
        	if (!track2.substring(0,6).trim().equals("622691") && track2.trim().length() > 20)
        	{
        		new MessageBox("无效理财卡,请重新刷入!");
        		return null;
        	}
        	
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
    		String[] s = new String[3];
			s[0] = track1;
			s[1] = track2;
			s[2] = track3;
			return s;
        }
        else if (fjktypeChoice == 2)
        {
        	if (!track2 .substring(0,6).trim().equals("518212"))
        	{
        		new MessageBox("无效联名信用卡,请重新刷入!");
        		return null;
        	}
        	
            track2 = track3;
            if (track2 == null || track2.length() < 94)
            {
                new MessageBox("无效联名信用卡,请重新刷入!");
                return null;
            }
            track2 = track2.substring(86, 94);
    		String[] s = new String[3];
			s[0] = track1;
			s[1] = track2;
			s[2] = track3;
			return s;
        }
        else
        {
        	new MessageBox("请选择有效的返券卡类型!");
        	return null;
        }
	}	
}
