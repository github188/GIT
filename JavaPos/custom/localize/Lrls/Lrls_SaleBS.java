package custom.localize.Lrls;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.BuyerInfoDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Cmls.Cmls_SaleBS;


public class Lrls_SaleBS extends Cmls_SaleBS
{
	public void execCustomKey0(boolean keydownonsale)
	{	
		saleHead.str6 = selectBuyerInfo();
	}
	
	public String selectBuyerInfo()
	{
		ResultSet rs = null;
		String[] title = { Language.apply("代码"), Language.apply("顾客信息描述") };
		int[] width = { 60, 240 };
		String[] content = null;
		Vector contents = new Vector();
		BuyerInfoDef bid = null;
		
		String selectStr = "";
		try
		{
			if ((rs = GlobalInfo.localDB.selectData("select code,type,name from BuyerInfo")) != null)
			{
				// 生成列表
				bid = new BuyerInfoDef();
				while (rs.next())
				{
					/*if (rs.getString(1).trim().equals("00"))
					{
						caption = rs.getString(3).trim();
						continue;
					}
*/
					if (GlobalInfo.localDB.getResultSetToObject(bid, BuyerInfoDef.ref))
					{
						if(bid.code.length()==1){
							content = new String[2];
							content[0] = bid.code;
							content[1] = bid.name;

							contents.add(content);
						}
						
					}
				}
				
				if(saleHead.str6.trim().length()>0){
					String dispInfo = "";
					for(int i =0 ; i<contents.size(); i++){
						content = (String[]) contents.get(i);
						
						for(int j =0 ; j<saleHead.str6.length() ; j++){
							if(content[0].trim().equals(saleHead.str6.substring(j,j+1)))
							{
								dispInfo = dispInfo +"\n"+ content[1];
								break;
							}
								
							
						}
					}
					
					MessageBox me = new MessageBox("已采集信息内容:"+dispInfo+"\n\n是否重新进行信息采集?", null, true);
					if (me.verify() != GlobalVar.Key1)
						return saleHead.str6;
				}
				
				MutiSelectForm msf = new MutiSelectForm();
				// 选择
				msf.open(Language.apply("请选择信息采集内容")  , title, width, contents, true ,389,419,360,292,false,false,-1,false,0,0,null,null,null,-100,false);
				
				String info = msf.InputText;
				if (info!=null && !info.equals(""))
				{
					for(int i =0 ; i<contents.size(); i++){
						content = (String[]) contents.get(i);
						
						for(int j =0 ; j<info.length() ; j++){
							if(content[0].trim().equals(info.substring(j,j+1)))
							selectStr = selectStr +","+ content[0];
						}
					}
					
				} 
				
				return selectStr.substring(1);
			}

			return "";
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "";
		}
		finally
		{
			if (rs != null)
			{
				GlobalInfo.dayDB.resultSetClose();
			}
		}
	}
	
//	 会员授权
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
		
		if(bs.selectedRule.desc.indexOf("手机号")>-1)
		{
			saleHead.str7 = track2;
		}else{
			saleHead.str7 = "";
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
