package custom.localize.Wqls;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.FjkInfoQueryBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.OperUserDef;

import custom.localize.Bhls.Bhls_SaleBS;

public class Wqls_SaleBS extends Bhls_SaleBS
{
	public void printHang(int maxGD)
	{
	}

	public OperUserDef inputRebateGrant(int index)
	{
		OperUserDef staff = DataService.getDefault().personGrant();
		if (staff == null) return null;
		if (staff.dpzkl * 100 >= 100)
		{
			new MessageBox("该员工授权卡无法授权单品打折");
			return null;
		}

		return staff;
	}

	public boolean allowQuickExitSell()
	{
		if (NewKeyListener.searchKeyCode(GlobalVar.MainList) > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public void execCustomKey1(boolean keydownonsale)
	{
		new FjkInfoQueryBS().QueryFjkInfo();
	}

	public boolean memberGrant()
	{
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();

		// 读取会员卡
		String track2 = bs.readMemberCard();
		if (track2 == null || track2.equals("")) return false;

		// 解析出磁道和选择的类型
		String[] s = track2.split(",");
		track2 = s[0];

		// 查找会员卡
		CustomerDef cust = bs.findMemberCard(track2);
		if (cust == null) return false;

		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false))
		{
			if (cust.status == null || cust.status.trim().length() <= 0 || cust.status.charAt(0) != 'Y')
			{
				new MessageBox("该顾客卡已失效!");
				return false;
			}
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;

			return true;
		}
		else
		{
			// 记录会员卡
			return memberGrantFinish(cust);
		}
	}
}
