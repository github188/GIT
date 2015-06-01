package custom.localize.Lrtd;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.BuyerInfoDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bstd.Bstd_SaleBS;


public class Lrtd_SaleBS extends Bstd_SaleBS
{
	public void addSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
//		sg.isvipzk = 'N';
//		if (goods != null) goods.isvipzk = 'N';
		super.addSaleGoodsObject(sg, goods, info);

		if (SellType.ISCHECKINPUT(saletype)) { return; }

		if (goods != null)
		{
			// 查找该商品是否能够积分消费
			getGoodsIsJFXF(goods, info);
		}
	}
	
	private boolean getGoodsIsJFXF(GoodsDef goods, SpareInfoDef info)
	{
    	if (GlobalInfo.isOnline)
    	{
    		Lrtd_NetService netservice = (Lrtd_NetService)NetService.getDefault();
			return netservice.getGoodsIsJFXF(saleHead,goods, info, NetService.getDefault().getMemCardHttp(131));
    	}
    	else
    	{
    		return false;
    	}
	}
	
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
	/*
	public void findGoodsRuleFromCRM(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		String newyhsp = "90000000";
		String cardno = null;
		String cardtype = null;

		if ((curCustomer != null && curCustomer.iszk == 'Y'))
		{
			cardno = curCustomer.code;
			cardtype = curCustomer.type;
		}

		GoodsPopDef popDef = new GoodsPopDef();

		// 非促销商品 或者在退货时，不查找促销信息
		((Bcrm_DataService) DataService.getDefault()).findPopRuleCRM(popDef, sg.code, sg.gz, sg.uid, goods.specinfo, sg.catid, sg.ppcode,
																		saleHead.rqsj, cardno, cardtype, saletype);
		// 换货状态下，不使用任何促销
		if (popDef.yhspace == 0 || hhflag == 'Y')
		{
			popDef.yhspace = 0;
			popDef.memo = "";
			popDef.poppfjzkfd = 1;
		}

		//将收券规则放入GOODSDEF 列表
		goods.memo = popDef.str2;
		goods.num1 = popDef.num1;
		goods.num2 = popDef.num2;
		goods.str4 = popDef.mode;
		info.char3 = popDef.type;

		// 促销联比例
		sg.xxtax = Convert.toDouble(popDef.ksrq); // 促销联比例
		goods.xxtax = Convert.toDouble(popDef.ksrq);
		if (goods.memo == null) goods.memo = "";

		// 增加CRM促销信息
		crmPop.add(popDef);

		// 标志是否为9开头扩展的控制
		boolean append = false;
		// 无促销,此会员不允许促销
		if (popDef.yhspace == 0)
		{
			append = false;
			info.str1 = "0000";
		}
		else if (popDef.yhspace == Integer.parseInt(newyhsp))
		{
			append = true;
			info.str1 = newyhsp;
		}
		else
		{

			if (String.valueOf(popDef.yhspace).charAt(0) != '9')
			{
				if (GlobalInfo.sysPara.iscrmtjprice == 'Y') info.str1 = Convert.increaseInt(popDef.yhspace, 5).substring(0, 4);
				else info.str1 = Convert.increaseInt(popDef.yhspace, 4);

				append = false;
			}
			else
			{
				info.str1 = String.valueOf(popDef.yhspace);

				append = true;
			}
			//询问参加活动类型 满减或者满增
			String yh = info.str1;

			if (append) yh = yh.substring(1);

			StringBuffer buff = new StringBuffer(yh);
			Vector contents = new Vector();

			for (int i = 0; i < buff.length(); i++)
			{
				// 2-任选促销/1-存在促销/0-无促销
				if (buff.charAt(i) == '2')
				{
					if (i == 0)
					{
						contents.add(new String[] { "D", Language.apply("参与打折促销活动"), "0" });
					}
					else if (i == 1)
					{
						contents.add(new String[] { "J", Language.apply("参与减现促销活动"), "1" });
					}
					else if (i == 2)
					{
						contents.add(new String[] { "Q", Language.apply("参与返券促销活动"), "2" });
					}
					else if (i == 3)
					{
						contents.add(new String[] { "Z", Language.apply("参与赠品促销活动"), "3" });
					}
					else if (i == 5)
					{
						contents.add(new String[] { "F", Language.apply("参与积分活动"), "5" });
					}
				}
			}

			if (contents.size() <= 1)
			{
				if (contents.size() > 0)
				{
					String[] row = (String[]) contents.elementAt(0);
					int i = Integer.parseInt(row[2]);
					buff.setCharAt(i, '1');
				}
			}
			else
			{
				String[] title = { Language.apply("代码"), Language.apply("描述") };
				int[] width = { 60, 400 };
				int choice = new MutiSelectForm().open(Language.apply("请选择参与满减满赠活动的规则"), title, width, contents);

				for (int i = 0; i < contents.size(); i++)
				{
					if (i != choice)
					{
						String[] row = (String[]) contents.elementAt(i);
						int j = Integer.parseInt(row[2]);
						buff.setCharAt(j, '0');
					}
					else
					{
						String[] row = (String[]) contents.elementAt(i);
						int j = Integer.parseInt(row[2]);
						buff.setCharAt(j, '1');
					}
				}
			}

			if (append) info.str1 = "9" + buff.toString();
			else info.str1 = buff.toString();
		}

		String line = "";

		String yh = info.str1;
		if (append) yh = info.str1.substring(1);

		if (yh.charAt(0) != '0')
		{
			line += "D";
		}

		if (yh.charAt(1) != '0')
		{
			line += "J";
		}

		if (yh.charAt(2) != '0')
		{
			line += "Q";
		}

		if (yh.charAt(3) != '0')
		{
			line += "Z";
		}

		if (yh.length() > 5 && yh.charAt(5) != '0')
		{
			line += "F";
		}

		if (line.length() > 0)
		{
			sg.name = "(" + line + ")" + sg.name;
		}

		if (!append)
		{
			// str3记录促销组合码
			if (GlobalInfo.sysPara.iscrmtjprice == 'Y') sg.str3 = info.str1 + String.valueOf(Convert.increaseInt(popDef.yhspace, 5).substring(4));
			else sg.str3 = info.str1;
		}
		else
		{
			sg.str3 = info.str1;
		}
		// 将商品属性码,促销规则加入SaleGoodsDef里
		sg.str3 += (";" + goods.specinfo);
		sg.str3 += (";" + popDef.memo);
		sg.str3 += (";" + popDef.poppfjzkl);
		sg.str3 += (";" + popDef.poppfjzkfd);
		sg.str3 += (";" + popDef.poppfj);

		// 只有找到了规则促销单，就记录到小票
		if (!info.str1.equals("0000") || !info.str1.equals(newyhsp))
		{
			sg.zsdjbh = popDef.djbh;
			sg.zszkfd = popDef.poplsjzkfd;
		}
	}
	*/
	
	public void customerIsZk(CustomerDef cust)
	{
		// 具有折扣功能
		if (cust.iszk == 'Y')
		{
			// 记录到小票
			saleHead.hysq = cust.code;

			// 设置当前授权卡为顾客卡
			cursqkh = cust.code;
			cursqktype = '2';
			cursqkzkfd = 1;

			// 授权
			//String msg = "";
			if (cust.func == null || cust.func.length() <= 0)
				cust.func = "A";
			if (cust.func.charAt(0) != 'Y' && cust.func.charAt(0) != 'N')
			{
				curGrant.zpzkl = cust.zkl;
				curGrant.dpzkl = cust.zkl;
			//	msg = Language.apply("顾客卡授权打折\n\n总品及单品折扣:") + ManipulatePrecision.doubleToString(cust.zkl * 100) + "%";
			}
			if (cust.func.charAt(0) == 'Y')
			{
				curGrant.zpzkl = cust.zkl;
			//	msg = Language.apply("顾客卡授权打折\n\n总品折扣:") + ManipulatePrecision.doubleToString(cust.zkl * 100) + "%";
			}
			if (cust.func.charAt(0) == 'N')
			{
				curGrant.dpzkl = cust.zkl;
			//	msg = Language.apply("顾客卡授权打折\n\n单品折扣:") + ManipulatePrecision.doubleToString(cust.zkl * 100) + "%";
			}

			// 提示
			//new MessageBox(msg);
		}
	}
	
	public void execCustomKey1(boolean keydownonsale)
	{
		try
		{
			if (!allowEditGoods()) { return; }

			int index = saleEvent.table.getSelectionIndex();

			if (index >= 0)
			{
				// 输入商品折扣
				if (setRebate(index))
				{
					// 刷新商品列表
					saleEvent.updateTable(getSaleGoodsDisplay());
					saleEvent.table.setSelection(index);

					// 显示汇总
					saleEvent.setTotalInfo();

					// 显示商品大字信息
					saleEvent.setCurGoodsBigInfo();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("输入折扣时发生异常\n\n") + ex.getMessage());
		}
	}
	
//	 直接取单品折扣率
	public boolean setRebate(int index)
	{
		double grantzkl = 0;
		boolean grantflag = false;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack())
			return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);
		// 小计、削价不处理
		if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3')) { return false; }

		// 服务费、以旧换新、换退商品不处理
		if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8') || (this.isHHGoods(saleGoodsDef))) { return false; }
		
		if((GlobalInfo.sysPara.isShowCatid).indexOf(saleGoodsDef.catid.charAt(0))<0) 
		{ 
			new MessageBox(Language.apply("该商品不允许使用出清折扣!"));
			return false; 
		}
		
		// 不能打折
		if (!checkGoodsRebate(goodsDef, info))
		{
			new MessageBox(Language.apply("该商品不允许打折!"));

			return false;
		}

		// 备份数据
		//SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		// 授权

		OperUserDef staff = inputRebateGrantNew(index);
		if (staff == null)
			return false;

		// 本次授权折扣
		grantzkl = staff.dpzkl;
		//grantflag = breachRebateGrant(staff);

		// 记录授权工号
		saleGoodsDef.sqkh = staff.gh;
		saleGoodsDef.sqktype = '1';
		saleGoodsDef.sqkzkfd = staff.privje1;

		// 记录日志
		String log = "授权单品折扣,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",折扣权限:" + grantzkl * 100 + "%,授权:" + staff.gh;
		AccessDayDB.getDefault().writeWorkLog(log);
	


		// 计算权限允许的最大折扣率
		double maxzkl = 0;
		if (grantflag)
		{
			// new MessageBox("允许突破最低折扣");
			// 允许突破最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, 0);
		}
		else
		{
			// 不允许最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
		}

		// 以最大折扣率模拟计算折扣,检查打折以后商品的折扣合计是否超出权限允许的折扣率
		saleGoodsDef.lszke = 0;
		saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((1 - maxzkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
		if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1))
		{
			saleGoodsDef.lszke -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1);
			saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke, 2, 1);
		}
		if (saleGoodsDef.lszke < 0)
			saleGoodsDef.lszke = 0;

		// 根据模拟计算得到当前最大打折比例
		double lszkl = saleGoodsDef.lszke / (saleGoodsDef.hjje - getZZK(saleGoodsDef) + saleGoodsDef.lszke);
		lszkl = ManipulatePrecision.doubleConvert((1 - lszkl) * 100, 2, 1);

		// 得到折扣率
		//grantzkl = Double.parseDouble(buffer.toString());
		grantzkl = Double.parseDouble(ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true));
		// 计算最终折扣
		saleGoodsDef.lszke = 0;
		saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((100 - grantzkl) / 100 * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
		if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1))
		{
			saleGoodsDef.lszke -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1);
			saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke, 2, 1);
		}
		if (saleGoodsDef.lszke < 0)
			saleGoodsDef.lszke = 0;
		saleGoodsDef.lszke = getConvertRebate(index, saleGoodsDef.lszke);

		// 重算商品折扣合计
		getZZK(saleGoodsDef);

		// 重算小票应收
		calcHeadYsje();

		return true;
	}
	
	public void custMethod()
	{
		if(GlobalInfo.sysPara.isShowCatid.equals("")||GlobalInfo.sysPara.isShowCatid.equals("0")) return;
		String line = "";
		for(int a = 1;a<6;a++)
		{
			if((GlobalInfo.sysPara.isShowCatid).indexOf(String.valueOf(a))!=-1)
			{
				double num = 0;   //记录部类合计金额
				for(int i = 0;i<saleGoods.size();i++){
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
					if(String.valueOf(a).equals(String.valueOf(saleGoodsDef.catid.charAt(0))))
						num = num +(saleGoodsDef.hjje-saleGoodsDef.hjzk);
				}
				if(num >0){
					//部类：1=食品 2=生鲜 3=非食品 4=硬百 5 软百
					 switch (a)
			         {
			        	 case 1:line = line+"食品";
			        	 	break;
			        	 case 2:line = line+"生鲜";	break;
			        	 case 3:line = line+"非食品";	break;
			        	 case 4:line = line+"硬百";	break;
			        	 case 5:line = line+"软百";	break;
			         }
					 line = line+":合计金额"+num+"元\n";
				}
			}
		}
		if(!line.trim().equals("")) new MessageBox(line);
		
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
	
	public OperUserDef inputRebateGrantNew(int index)
	{
		//
		OperUserDef staff = new OperUserDef();

    	try
    	{
    		StringBuffer cardno = new StringBuffer();
//    		 输入顾客卡号
    		TextBox txt = new TextBox();
    		if (!txt.open(Language.apply("请使用扫描出清条码或刷卡进行折扣"), Language.apply("出清条码"), Language.apply(""), cardno, 0, 0, false, 2)) { return null; }

	    	String id = txt.Track2;
	    	
	    	if (id.equals(GlobalInfo.posLogin.gh))
	    	{
	    		new MessageBox(Language.apply("不允许收银员进行自身授权"));
	    		return null;
	    	}
	    	
	    	// 查找人员
	        if (!DataService.getDefault().getOperUser(staff, id))
	        {
	            return null;
	        }
	
	    	if (staff.isgrant != 'Y')
	    	{
	    		new MessageBox(Language.apply("该员工卡不能授权"));
	    		
	    		return null;
	    	}
	    	
	        // 检查工号过期
	        String expireDate = staff.maxdate + " 0:0:0";
	        ManipulateDateTime mdt = new ManipulateDateTime();
	        if (mdt.getDisDateTime(mdt.getDateBySlash() + " 0:0:0", expireDate) < 0)
	        {
	            new MessageBox(Language.apply("该工号已过期!"), null, false);
	
	            return null;
	        }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return null;
    	}
		
		if (staff == null)
			return null;
		if (staff.dpzkl * 100 >= 100)
		{
			new MessageBox(Language.apply("该员工授权卡无法授权单品打折"));
			return null;
		}
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		if (!checkGoodsGrantRange(goodsDef, staff.grantgz))
		{
			new MessageBox(Language.apply("该商品不在员工授权卡授权范围内"));
			return null;
		}
		return staff;
	}
}
