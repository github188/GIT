package custom.localize.Bzhx;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;

import custom.localize.Cmls.Cmls_SaleBS;

public class Bzhx_SaleBS extends Cmls_SaleBS
{
	public boolean memberGrant()
	{
		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
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
		CustomerDef cust = bs.findMemberCard(track2);

		if (cust == null){
			if(new MessageBox("未查询到相关会员信息,是否创建临时会员?", null, true).verify() == GlobalVar.Key1){
				cust = createNewCustomer();
			}
			if(cust == null) return false;
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
	
	private CustomerDef createNewCustomer()
	{
		CustomerDef cust = new CustomerDef();
		TextBox txt = new TextBox();
		StringBuffer cardno = new StringBuffer();
		//cardno.append("无卡手机号") ;
		while(true){
			if (!txt.open(Language.apply("临时会员办理"), Language.apply(""), Language.apply("请收银员重复手机号与顾客验证手机号码,谢谢!"), cardno, 0, 0, false, 0)) 
			{ return null; }
			if(cardno.toString().length()!=11) {
				new MessageBox("输入的手机号长度非11位,请正确填写手机号");
			}else{
				break;
			}
		}
		
		Bzhx_NetService bn = new Bzhx_NetService();
		bn.sendNewCustomer(cust, cardno.toString());
		if (cust.code == null || cust.code.trim().equals("")){
			new MessageBox("临时会员办理失败!");
			return null;
		}
		if(cust!=null) {
			new MessageBox("临时会员办理成功!");
			return cust;
		}
		
		return null;
	}
	
	public void execCustomKey0(boolean keydownonsale)
	{
//		 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (saleGoods.size() > 0 && !memberAfterGoodsMode() && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox(Language.apply("必须在输入商品前进行临时会员办理\n请把商品清除后再办理"));
			return ;
		}
		if (isPreTakeStatus())
		{
			new MessageBox(Language.apply("预售提货状态下不允许临时会员办理"));
			return ;
		}

		// 会员卡必须在商品输入前,则输入了商品以后不能刷卡,指定小票除外
		if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0 && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox(Language.apply("必须在输入商品前进行临时会员办理\n\n请把商品清除后再办理"));
			return ;
		}
		
		if(createNewCustomer()==null){
		}else{
			new MessageBox("临时会员办理成功!");
		}
	}

	//增加显示卡级别
	public String getVipInfoLabel()
    {
    	if (curCustomer == null)
    		return "";
    	else 
    	{
    		String typename = "";
    		if(curCustomer.type.trim().equals("01")) typename = "普卡";
    		if(curCustomer.type.trim().equals("02")) typename = "银卡";
    		if(curCustomer.type.trim().equals("03")) typename = "金卡";
    		if(curCustomer.type.trim().equals("04")) typename = "白金卡";
    		if(curCustomer.type.trim().equals("05")) typename = "团购客户卡";
    		if(typename.trim().equals("")){
    			return "[" + curCustomer.code + "]" + curCustomer.name+curCustomer.type;
    		}else{
    			return "[" + curCustomer.code + "]" + curCustomer.name+typename;
    		}
    		
    	}
    }
    
}
