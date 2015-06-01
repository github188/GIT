package custom.localize.Bjkl;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bstd.Bstd_MenuBS;

public class Bjkl_MenuFuncBS extends Bstd_MenuBS
{
    public final static int MN_JKLKF	 = 311;						//京客隆会员卡客户通道
    
	public void execFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		try
		{
			// 记录菜单功能日志
			if (mfd != null && mfd.workflag == 'Y')
				AccessDayDB.getDefault().writeWorkLog("进入 \"[" + mfd.code + "]" + mfd.name + "\" 菜单", mfd.code);
			
			if (Integer.parseInt(mfd.code) == MN_JKLKF)
			{
				String cardno = "";
				String pass = "";
				StringBuffer sb = new StringBuffer();
				TextBox txt = new TextBox();
				// 只能刷卡
				if (!txt.open(Language.apply("请刷会员卡"), Language.apply("卡号"), Language.apply("请将会员从刷卡槽刷入"), sb, 0, 0, false, 2)) { return ; }
				String track = txt.Track2;
				
				if (track.length() == 24)
				{
					cardno = track.substring(0, 16);
					pass = track.substring(16, 24);
				}
				else
				{
					new MessageBox("客服功能只允许采用刷卡方式!");
					return ;
				}
				
				String type = "";
				String flag = "";
				
				String code = "";
				String[] title = { "编号", "零钱包开通类型" };
				int[] width = { 60, 440 };
				
				Vector contents = new Vector();
				contents.add(new String[] { "1", " 询问存入 -0" });
				contents.add(new String[] { "2", " 1元零存 - 1" });
				contents.add(new String[] { "3", " 10元零存 - 2" });
				contents.add(new String[] { "4", " 设置不存入 - 3" });
				
				int choice = new MutiSelectForm().open("请选择开通零钱包类型", title, width, contents, true);
				if (choice == -1)
				{
					return ;
				}
				else
				{
					if (choice >= contents.size())
					{
						new MessageBox("选择类型有误!");
						return ;
					}
					type = " 1";
					
					code = ((String[]) (contents.elementAt(choice)))[0];
					if (code.equals("1"))
					{
						flag = " 0";
					}
					else if (code.equals("2"))
					{
						flag = " 1";
					}
					else if (code.equals("3"))
					{
						flag = " 2";
					}
					else if (code.equals("4"))
					{
						flag = " 4";
					}
					else
					{
						new MessageBox("选择的开通类型有问题。。");
					}
				}				
				CardModule.getDefault().cardCustSvc(cardno,pass, type, flag);			
			}
			else
			{
				super.execFuncMenu(mfd, mffe);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox("客服通道调用出现异常：" + e.getMessage());
		}

	}
}
