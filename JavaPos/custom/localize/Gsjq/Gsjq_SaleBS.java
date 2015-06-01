package custom.localize.Gsjq;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;

import custom.localize.Bhcm.Bhcm_SaleBS;

public class Gsjq_SaleBS extends Bhcm_SaleBS
{
//	 会员授权
	public boolean memberGrant()
	{
//		 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (saleGoods.size() > 0 && !memberAfterGoodsMode() && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox(Language.apply("必须在输入商品前进行刷会员卡\n请把商品清除后再重刷卡"));
			return false;
		}
		
		if (isPreTakeStatus())
		{
			new MessageBox(Language.apply("预售提货状态下不允许重新刷卡"));
			return false;
		}

		// 会员卡必须在商品输入前,则输入了商品以后不能刷卡,指定小票除外
		if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0 && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox(Language.apply("必须在输入商品前进行刷会员卡\n\n请把商品清除后再重刷卡"));
			return false;
		}

		// 读取会员卡
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		String track2 = bs.readMemberCard();
		if (track2 == null || track2.equals(""))
			return false;

		// 查找会员卡
		CustomerDef cust = null;
		
//		 查找会员卡
		if (track2.trim().length() != 11)
		{
			cust = bs.findMemberCard(track2);
		}
		else
		{
			ProgressBox progress = new ProgressBox();
			try{
				track2 = "@"+track2;
				progress.setText("正在查询会员卡信息，请等待.....");
				cust = new CustomerDef();
				cust = bs.findMemberCard(track2);
				saleHead.num2 = 1;
			}catch(Exception er)
			{
				er.printStackTrace();
			}
			finally
			{
				progress.close();
			}
		}
		
		

		if (cust == null)
			return false;

		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false))
		{
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;
			saleHead.str4 = cust.valstr2;
			saleHead.hymaxdate = cust.maxdate;
			return true;
		}
		else
		{
			// 记录会员卡
			return memberGrantFinish(cust);
		}
	}
}
