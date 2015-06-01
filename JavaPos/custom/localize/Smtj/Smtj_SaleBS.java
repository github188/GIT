package custom.localize.Smtj;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;

import custom.localize.Cmls.Cmls_SaleBS;

public class Smtj_SaleBS extends Cmls_SaleBS
{
	public void initSetYYYGZ(String type, boolean iscsinput)
	{
		// 是否输入营业员,Y-输入营业员/N-超市不输入营业员/B-百货不输入营业员/A-可输可不输,不输入时为超市,输入时为营业员
		if (SellType.ISCHECKINPUT(type))
		{
			saleEvent.yyyh.setText("盘点");
			saleEvent.gz.setText("");
			saleEvent.saleform.setFocus(saleEvent.code);
		}
		else
		{
			if (SellType.ISCOUPON(type))
			{
				saleEvent.yyyh.setText("买券");
				saleEvent.gz.setText("买券柜");
				saleEvent.saleform.setFocus(saleEvent.code);
			}
			else if (SellType.isJS(type))
			{
				saleEvent.yyyh.setText("结算");
				saleEvent.gz.setText("结算柜");
				saleEvent.saleform.setFocus(saleEvent.code);
			}
			else if (SellType.isJF(type))
			{
				saleEvent.yyyh.setText("缴费");
				saleEvent.gz.setText("缴费柜");
				saleEvent.saleform.setFocus(saleEvent.code);
			}
			else if (GlobalInfo.syjDef.issryyy == 'N')
			{
				saleEvent.yyyh.setText("超市");
				saleEvent.gz.setText("超市柜");
				saleEvent.saleform.setFocus(saleEvent.code);
			}
			else if (GlobalInfo.syjDef.issryyy == 'B')
			{
				saleEvent.yyyh.setText("购物中心");
				saleEvent.gz.setText("租户");
				saleEvent.saleform.setFocus(saleEvent.code);
			}
			else
			{
				if (iscsinput)
				{
					saleEvent.yyyh.setText("超市");
					saleEvent.gz.setText("超市柜");
					saleEvent.saleform.setFocus(saleEvent.code);
				}
				else
				{
					saleEvent.saleform.setFocus(saleEvent.yyyh);
				}
			}
		}
	}
	
	// 会员授权
	public boolean memberGrant()
	{
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
		CustomerDef cust = bs.findMemberCard(track2);

		if (cust == null)
			return false;
		
		String sign = track2.trim().substring(0, 1);
		if(sign.indexOf("@") >= 0 || sign.indexOf("#") >= 0)
		{
			saleHead.str3 = sign;
		}

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
