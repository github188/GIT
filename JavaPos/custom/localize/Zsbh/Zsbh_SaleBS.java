/**
 * 中商百货
 */
package custom.localize.Zsbh;

import java.util.Vector;

import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.CmPopGoodsDef;
import com.efuture.javaPos.Struct.CmPopRuleLadderDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.RetSYJForm;
import com.efuture.javaPos.UI.Design.SalePayForm;

import custom.localize.Bcrm.Bcrm_DataService;
import custom.localize.Bcrm.Bcrm_SaleBS;

public class Zsbh_SaleBS extends Bcrm_SaleBS
{
	public boolean getPayModeByNeed(PayModeDef paymode)
	{
		//销售状态下，不显示“0319退信用卡”付款方式
		if (paymode.code.equals("0319") && SellType.ISSALE(saletype)) return false;
		
		return super.getPayModeByNeed(paymode);		
	}
	
	public void findGoodsCRMPop(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
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
																		saleHead.rqsj, cardno, cardtype,saletype);
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
		sg.str6 = popDef.str4;//分组号

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
						contents.add(new String[] { "D", "参与打折促销活动", "0" });
					}
					else if (i == 1)
					{
						contents.add(new String[] { "J", "参与减现促销活动", "1" });
					}
					else if (i == 2)
					{
						contents.add(new String[] { "Q", "参与返券促销活动", "2" });
					}
					else if (i == 3)
					{
						contents.add(new String[] { "Z", "参与赠品促销活动", "3" });
					}
					else if (i == 5)
					{
						contents.add(new String[] { "F", "参与积分活动", "5" });
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
				String[] title = { "代码", "描述" };
				int[] width = { 60, 400 };
				int choice = new MutiSelectForm().open("请选择参与满减满赠活动的规则", title, width, contents);

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

			if (append) info.str1 = "9"+buff.toString();
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
	
	public boolean calcGoodsCMPOPRebate(int index,CmPopGoodsDef cmp,int cmpindex)
    {
        SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
        boolean havepop = false;
        
        // 该规则已使用,不重新计算
        if (cmp.used) return havepop;
        
        // goodsgrouprow>0,表示同分组有其他商品条件,先检查同分组条件
        Vector groupvec = null;
        int groupbs = -1;
        if (cmp.goodsgrouprow > 0 || cmp.ruleinfo.isonegroup == 'Y')
        {
        	// 如果整个规则是一个分组则group标记为负,查询时找整个规则
        	int group = cmp.goodsgroup;
        	if (cmp.ruleinfo.isonegroup == 'Y') group = -1;

        	// 查找规则组内的商品范围
        	Vector grpvec = ((Bcrm_DataService)DataService.getDefault()).findCMPOPGroup(cmp.dqid,cmp.ruleid,group);
        	for (int n=0;grpvec != null && n<grpvec.size();n++)
        	{
        		CmPopGoodsDef grpcmp = (CmPopGoodsDef)grpvec.elementAt(n);
        		
        		// 商品列表中有商品符合该规则
        		double grpsl = 0;
        		double grpje = 0;
	        	for (int i=0;i<saleGoods.size();i++)
	        	{
    				SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(i);
	        		Vector popvec = (Vector)goodsCmPop.elementAt(i);
	        		for (int j=0;popvec != null && j<popvec.size();j++)
	        		{
	        			CmPopGoodsDef cp = (CmPopGoodsDef)popvec.elementAt(j);
	        			if (grpcmp.dqid.equals(cp.dqid) && grpcmp.ruleid.equals(cp.ruleid) && !cp.used &&
	        				((cmp.ruleinfo.isonegroup != 'Y' && grpcmp.goodsgroup == cp.goodsgroup && grpcmp.goodsgrouprow == cp.goodsgrouprow) ||
	        				 (cmp.ruleinfo.isonegroup == 'Y' && grpcmp.goodsgroup == cp.goodsgroup)))
	        			{
	        				// groupvec记录参与组合促销条件的商品行
	        				if (groupvec == null) groupvec = new Vector();
	        				int k = 0;
	        				for (;k<groupvec.size();k++)
	        				{
	        					PopRuleGoodsGroup prgp = (PopRuleGoodsGroup)groupvec.elementAt(k);
	        					if ((cmp.ruleinfo.isonegroup != 'Y' && grpcmp.goodsgroup == prgp.goodsgroup && grpcmp.goodsgrouprow == prgp.goodsgrouprow) ||
	        						(cmp.ruleinfo.isonegroup == 'Y' && grpcmp.goodsgroup == prgp.goodsgroup)) break;
	        				}
	        				if (k >= groupvec.size())
	        				{
	        					PopRuleGoodsGroup prgp = new PopRuleGoodsGroup();
	        					prgp.goodsgroup = grpcmp.goodsgroup;
	        					prgp.goodsgrouprow = grpcmp.goodsgrouprow;
	        					prgp.condmode = grpcmp.condmode;
	        					prgp.condsl = grpcmp.condsl;
	        					prgp.condje = grpcmp.condje;
	        					prgp.popje = grpcmp.poplsj;
	        					prgp.goodslist = new Vector();
	        					prgp.goodslist.add(String.valueOf(i));
	        					groupvec.add(prgp);
	        				}
	        				else
	        				{
	        					// 参与本组的商品行用|分隔
	        					PopRuleGoodsGroup prgp = (PopRuleGoodsGroup)groupvec.elementAt(k);
	        					int m = 0;
	        					for (;m <  prgp.goodslist.size();m++) if (i == Convert.toInt(prgp.goodslist.elementAt(m))) break;
	        					if  ( m >= prgp.goodslist.size()) prgp.goodslist.add(String.valueOf(i));
	        				}
	        				
	        				// 同组累计
	        				grpsl += sg.sl;
	        				grpje += sg.hjje - sg.hjzk;
	        				break;
	        			}
	        		}
	        	}
        		
	        	// 检查是否符合分组商品的条件及满足分组条件最少的倍数
	        	if (
	        		(grpcmp.condmode == '0' && (grpsl > 0 || grpje > 0)) ||
	        		(grpcmp.condmode == '1' && grpsl >= grpcmp.condsl && grpcmp.condsl > 0) ||
	        		(grpcmp.condmode == '2' && grpje >= grpcmp.condje && grpcmp.condje > 0) ||
	        		(grpcmp.condmode == '3' && grpsl >= grpcmp.condsl && grpje >= grpcmp.condje && grpcmp.condsl > 0 && grpcmp.condje > 0))
	        	{
	        		int bs = -1;
	        		if (grpcmp.condmode == '1')
	        		{
	        			bs = ManipulatePrecision.integerDiv(grpsl,grpcmp.condsl);
	        			
	        			// condje>=1表示赠送结果集存在和条件相同的商品,例如买2送1
	        			// 因此按单倍把结果商品计算以后不参与条件运算，剩余部分数量再次计算促销
	        			if (bs > 1 && grpcmp.condje >= 1) bs = 1;
	        		}
	        		if (grpcmp.condmode == '2') 
	        		{
	        			bs = ManipulatePrecision.integerDiv(grpje,grpcmp.condje);
	        			
	        			// condsl>=1表示赠送结果集存在和条件相同的商品,例如买2送1
	        			// 因此按单倍把结果商品计算以后不参与条件运算，剩余部分数量再次计算促销	        			
	        			if (bs > 1 && grpcmp.condsl >= 1) bs = 1;
	        		}
	        		if (grpcmp.condmode == '3')
	        		{
	        			bs = ManipulatePrecision.integerDiv(grpsl,grpcmp.condsl);
	        			if (ManipulatePrecision.integerDiv(grpje,grpcmp.condje) < bs) bs = ManipulatePrecision.integerDiv(grpje,grpcmp.condje);
	        		}
	        		if (groupbs < 0 || (bs >= 0 && bs < groupbs)) groupbs = bs;
	        	}
	        	else
	        	{
	        		// 不满足分组内其他条件
	            	return havepop;
	        	}
        	}
        }
        
		// 记录同规则的商品行
        Vector rulegoods = new Vector();
        Vector rulegifts = new Vector();
        double popje = 0,hjcxsl = 0,hjcxje = 0,hjzje = 0,hjcjj = 0;
        do 
        {
	        // 标记为已参与计算的规则
	        cmp.used = true;
	        PopRuleGoods prg = new PopRuleGoods();
	        prg.sgindex = index;
	        prg.cmindex = cmpindex;
	        rulegoods.add(prg);
	        	
	        // 对商品进行规则累计
	        hjzje  = saleGoodsDef.hjje;
	        hjcjj  = saleGoodsDef.hjje - saleGoodsDef.hjzk;
	        hjcxsl = saleGoodsDef.sl;
	    	if (cmp.ruleinfo.popzsz == 'Y') hjcxje = saleGoodsDef.hjje - saleGoodsDef.hjzk - getGoodsPaymentApportion(index,saleGoodsDef);		        
	    	else hjcxje = saleGoodsDef.hjje - getGoodsPaymentApportion(index,saleGoodsDef);

	    	// 查找需要进行规则累积的商品集合
	        if (cmp.ruleinfo.summode != '0')
	    	{
		        for (int i=0;i<saleGoods.size();i++)
		        {
		        	if (i == index) continue;
		        	
		        	SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(i);
		        	
		        	if (i >= goodsCmPop.size()) continue;
		        	
	        		Vector popvec = (Vector)goodsCmPop.elementAt(i);
	        		for (int j=0;popvec != null && j<popvec.size();j++)
	        		{
	        			CmPopGoodsDef cmpoth = (CmPopGoodsDef)popvec.elementAt(j);
	        			if (cmpoth.used) continue;
	        			
	        			if (cmp.dqid.equals(cmpoth.dqid) && cmp.ruleid.equals(cmpoth.ruleid))
	        			{
		    				// 同规则累计,同规则同柜组累计,同规则单品累计,同规则同品类累计,同规则同品牌累计
		        			if (
		        				(cmp.ruleinfo.summode == '1') || 
		        				(cmp.ruleinfo.summode == '2' && saleGoodsDef.gz.equals(sg.gz)) ||
		        				(cmp.ruleinfo.summode == '3' && saleGoodsDef.code.equals(sg.code) && saleGoodsDef.gz.equals(sg.gz)) ||
		        				(cmp.ruleinfo.summode == '4' && saleGoodsDef.catid.equals(sg.catid)) ||
		        				(cmp.ruleinfo.summode == '5' && saleGoodsDef.ppcode.equals(sg.ppcode)))
	            			{
	            		        hjzje  += sg.hjje;
	            		        hjcjj  += sg.hjje - sg.hjzk;
	            		        hjcxsl += sg.sl;
	            		        if (cmp.ruleinfo.popzsz == 'Y') hjcxje += sg.hjje - sg.hjzk - getGoodsPaymentApportion(i,sg);			
	            		        else hjcxje += sg.hjje - getGoodsPaymentApportion(i,sg); 
	            		        
	            	    		// 标记为已参与计算的规则
	            	    		cmpoth.used = true;
	            		        PopRuleGoods prgd = new PopRuleGoods();
	            		        prgd.sgindex = i;
	            		        prgd.cmindex = j;
	            	    		
	            	    		// 满金额条件按成交价、行号排序,优先计算金额大的商品促销
	            		        // 满数量条件按无折扣、行号排序,优先计算无折扣的商品促销,多出的数量行以便计算除外
	            	    		int  n=0;
	            	    		for (n=0;n<rulegoods.size();n++)
	            	    		{
	            	    			int rgindex = ((PopRuleGoods)rulegoods.elementAt(n)).sgindex;
	            	    			SaleGoodsDef rg = (SaleGoodsDef)saleGoods.elementAt(rgindex);
	            	    			
	            	    			if (cmp.ruleinfo.condmode == '2')
	            	    			{
	            	    				double sgcxje = 0,rgcxje = 0;
	            	    				if (cmp.ruleinfo.popzsz == 'Y')
	            	    				{
	            	    					sgcxje = sg.hjje - sg.hjzk - getGoodsPaymentApportion(i,sg);
	            	    					rgcxje = rg.hjje - rg.hjzk - getGoodsPaymentApportion(rgindex,rg);
	            	    				}
	    	            		        else 
	    	            		        {
	    	            		        	sgcxje = sg.hjje - getGoodsPaymentApportion(i,sg);
	    	            		        	rgcxje = rg.hjje - getGoodsPaymentApportion(rgindex,rg);
	    	            		        }
	            	    				if (sgcxje > rgcxje) break;		// 成交价格大的排前面
	            	    				if (ManipulatePrecision.doubleCompare(sgcxje,rgcxje,2) == 0 && i < rgindex) break;
	            	    			}
	            	    			else
	            	    			{
	            	    				if (sg.hjzk < rg.hjzk) break;	// 合计折扣小的排前面
	            	    				if (ManipulatePrecision.doubleCompare(sg.hjzk,rg.hjzk,2) == 0 && i < rgindex) break;
	            	    			}
	            	    		}
	                			if (n >= rulegoods.size()) rulegoods.add(prgd);
	                			else rulegoods.insertElementAt(prgd,n);
	            	    		break;
	            			}
	        			}
	        		}	        	
		        }
	    	}
	    	
    		// 组合促销去掉超过分组内条件的商品行,这些行不参与促销计算
			for (int n=0;groupvec != null && n<groupvec.size();n++)
			{
				PopRuleGoodsGroup prgp = (PopRuleGoodsGroup)groupvec.elementAt(n);
				char condmode = prgp.condmode;
				double condsl = prgp.condsl * groupbs;
				double condje = prgp.condje * groupbs;
				Vector sglist = prgp.goodslist;
				double grpsl = 0,grpje = 0;
				for (int k=0;k<sglist.size();k++)
				{
					int sgindex = Integer.parseInt((String)sglist.elementAt(k));		 
            		SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(sgindex);
            		grpsl += sg.sl;
            		grpje += sg.hjje - sg.hjzk;
            		
            		// 去掉整行商品超过分组内条件的商品行,这些行不参与促销计算,数量超分组条件的拆行
            		if ((condmode == '0') ||
		        		(condmode == '1' && ManipulatePrecision.doubleCompare(grpsl,condsl,4) > 0 ) ||
		        		(condmode == '2' && ManipulatePrecision.doubleCompare(grpje,condje,2) > 0 && ManipulatePrecision.doubleCompare(grpje-condje,(sg.hjje-sg.hjzk)/sg.sl,2) >= 0) ||
		        		(condmode == '3' && ManipulatePrecision.doubleCompare(grpsl,condsl,4) > 0 && 
		        							ManipulatePrecision.doubleCompare(grpje,condje,2) > 0 && ManipulatePrecision.doubleCompare(grpje-condje,(sg.hjje-sg.hjzk)/sg.sl,2) >= 0))
            		{
        	        	// 取消该行
            			for (int j = 0; j<rulegoods.size();j++)
    	            	{
            				PopRuleGoods prgd = (PopRuleGoods)rulegoods.elementAt(j);
    						if (sgindex != prgd.sgindex) continue;
    						
							// 无分组条件的商品行应该放到商品集合的最后，先计算有组内条件的商品行，再计算无条件的商品行
							if (condmode == '0')
							{
								rulegoods.add(prgd);
								rulegoods.remove(j);
								break;
							}

							// 加上本行商品数量超过分组条件需要拆分
							double sl = sg.sl;
							if (condmode == '1') sl = ManipulatePrecision.doubleConvert(grpsl-condsl,4,1);
							if (condmode == '2') sl = (int)((grpje-condje)/((sg.hjje-sg.hjzk)/sg.sl));
							if (condmode == '3') sl = Math.min(grpsl-condsl,(int)((grpje-condje)/((sg.hjje-sg.hjzk)/sg.sl)));
							if (ManipulatePrecision.doubleCompare(sg.sl,sl,4) > 0)
							{
								SaleGoodsDef newsg = SplitSaleGoodsRow(sgindex, sg.sl - sl);
								if (newsg != null)
								{
									// 刷新拆分商品行
		            				refreshCmPopUI();
		            				
									// 合计数据去掉该商品行
			            			hjzje  -= newsg.hjje;
			            			hjcjj  -= newsg.hjje - newsg.hjzk;
			            			hjcxsl -= newsg.sl;
		            		        if (cmp.ruleinfo.popzsz == 'Y') hjcxje -= newsg.hjje - newsg.hjzk - getGoodsPaymentApportion(saleGoods.size()-1,newsg);
		            		        else hjcxje -= newsg.hjje - getGoodsPaymentApportion(saleGoods.size()-1,newsg);
								}
								break;
							}
							else
							{
	            				// 恢复该行促销标记
    							if (goodsCmPop != null && goodsCmPop.size() > sgindex)
    							{
    				        		Vector popvec = (Vector)goodsCmPop.elementAt(sgindex);
    				        		if (popvec != null && popvec.size() > prgd.cmindex) ((CmPopGoodsDef)popvec.elementAt(prgd.cmindex)).used = false;
    							}
    							
    							// 合计数据去掉该商品行
		            			hjzje  -= sg.hjje;
		            			hjcjj  -= sg.hjje - sg.hjzk;
		            			hjcxsl -= sg.sl;
	            		        if (cmp.ruleinfo.popzsz == 'Y') hjcxje -= sg.hjje - sg.hjzk - getGoodsPaymentApportion(sgindex,sg);
	            		        else hjcxje -= sg.hjje - getGoodsPaymentApportion(sgindex,sg);

		            			rulegoods.remove(j);
		            			break;
							}
    					}
            		}
				}
			}
    		
	        // 初始化折扣金额和赠品规则
	    	popje = 0;
    		rulegifts.removeAllElements();
	    	
    		// 无消费条件,则按规则的所有阶梯进行赠送(档期ID,规则ID,阶梯ID,促销倍数)
			if (cmp.ruleinfo.condmode == '0')
			{
	    		rulegifts.add(cmp.dqid + "," + cmp.ruleid + ",%,1");
			}
			else
			{
		    	// 有消费条件,按阶梯循环计算达到消费条件的促销金额,阶梯集合已倒序排序,优先级高的先执行
				// codntype,0=够满方式,只计算一个阶梯条件/1=每满方式,剩余部分循环参与下一个阶梯
				// condmode,1=数量类促销只计算一个阶梯,超过条件部分的数量被拆分并记为未参与本次促销,去参与其他促销或再次计算时参与下一个阶梯
				boolean loopladder = ((cmp.ruleinfo.condtype == '0' || cmp.ruleinfo.condmode == '1')?false:true);
		    	double yfsl = 0,yfje = 0;
		    	CmPopRuleLadderDef poprl = null;
		    	for (int i=0;cmp.ruleladder != null && i<cmp.ruleladder.size();i++)
		    	{
		    		poprl = (CmPopRuleLadderDef)cmp.ruleladder.elementAt(i);
		    		
		    		// 计算达到条件的倍数和参与达到条件的合计，剩余的合计参与下一个阶梯
		    		int laderbs = 0;
		    		if (cmp.ruleinfo.condmode == '1' && (hjcxsl - yfsl) >= poprl.levelsl)
		    		{
		    			if (poprl.levelminus == 'Y')
		    			{
		    				laderbs = ManipulatePrecision.integerDiv((hjcxsl - yfsl - poprl.levelsl), poprl.condsl);
		    			}
		    			else
		    			{
		    				laderbs = ManipulatePrecision.integerDiv((hjcxsl - yfsl),poprl.condsl);
		    			}
		    			
	        			// condje>=1表示赠送结果集存在和条件相同的商品,例如买2送1
	        			// 因此按单倍把结果商品计算以后不参与条件运算，剩余部分数量再次计算促销
	        			if (laderbs > 1 && poprl.condje >= 1) laderbs = 1;
		    		}
		    		else 
		    		if (cmp.ruleinfo.condmode == '2' && (hjcxje - yfje) >= poprl.levelje)
		    		{
		    			if (poprl.levelminus == 'Y')
		    			{
		    				laderbs = ManipulatePrecision.integerDiv((hjcxje - yfje - poprl.levelje),poprl.condje);
		    			}
		    			else
		    			{
		    				laderbs = ManipulatePrecision.integerDiv((hjcxje - yfje), poprl.condje);
		    			}
		    			
		    			// 满金额条件但对数量有分组要求
		    			if (laderbs > 0 && poprl.condsl > 0 && poprl.memo != null && 
			    			poprl.memo.length() > 0 && poprl.memo.charAt(0) != '0' &&
			    			poprl.memo.length() > 1 &&(poprl.memo.charAt(1) == 'E' || poprl.memo.charAt(1) == 'L' || poprl.memo.charAt(1) == 'G'))
		    			{
		    				// 对参与规则的商品按分组方式进行分组,然后判断是否符合数量条件
		    				Vector grpvec = new Vector();
		    				for (int j = 0; j<rulegoods.size();j++)
		    				{
		    					PopRuleGoods prgd = ((PopRuleGoods)rulegoods.elementAt(j));
			            		SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(prgd.sgindex);
		            			GoodsDef gd = (GoodsDef)goodsAssistant.elementAt(prgd.sgindex);
		            			
		    					// 1-按单品/2-按柜组/3-按品牌/4-按品类/5-按柜组+品牌/6-按柜组+品类/7-按品牌+品类/8-按柜+品+类/9-按条码(子商品)/A-按属性1/B-按属性2/C-按属性3/D-按属性4/E-按属性5/F-按属性6/G-按属性7/H-按属性8/(中商百货专用-满减)Z-按分组号
			            		int n = 0;
			            		for (;n<grpvec.size();n++)
			            		{
			            			int grpidx = Convert.toInt(((String[])grpvec.elementAt(n))[1].split(",")[0]);
			            			SaleGoodsDef sggrp = (SaleGoodsDef)saleGoods.elementAt(grpidx);
			            			GoodsDef gdgrp = (GoodsDef)goodsAssistant.elementAt(grpidx);
			    					if ((poprl.memo.charAt(0) == '1' && sg.code.equals(sggrp.code) && sg.gz.equals(sggrp.gz)) ||
			    						(poprl.memo.charAt(0) == '2' && sg.gz.equals(sggrp.gz)) ||
			    						(poprl.memo.charAt(0) == '3' && sg.ppcode.equals(sggrp.ppcode)) ||
			    						(poprl.memo.charAt(0) == '4' && sg.catid.equals(sggrp.catid)) ||
			    						(poprl.memo.charAt(0) == '5' && sg.gz.equals(sggrp.gz) && sg.ppcode.equals(sggrp.ppcode)) ||
			    						(poprl.memo.charAt(0) == '6' && sg.gz.equals(sggrp.gz) && sg.catid.equals(sggrp.catid)) ||
			    						(poprl.memo.charAt(0) == '7' && sg.ppcode.equals(sggrp.ppcode) && sg.catid.equals(sggrp.catid)) ||
			    						(poprl.memo.charAt(0) == '8' && sg.gz.equals(sggrp.gz) && sg.ppcode.equals(sggrp.ppcode) && sg.catid.equals(sggrp.catid)) ||
			    						(poprl.memo.charAt(0) == '9' && sg.barcode.equals(sggrp.barcode)) ||
			    						(poprl.memo.charAt(0) == 'A' && gd.attr01 != null && gd.attr01.equals(gdgrp.attr01)) ||
			    						(poprl.memo.charAt(0) == 'B' && gd.attr02 != null && gd.attr02.equals(gdgrp.attr02)) ||
			    						(poprl.memo.charAt(0) == 'C' && gd.attr03 != null && gd.attr03.equals(gdgrp.attr03)) ||
			    						(poprl.memo.charAt(0) == 'D' && gd.attr04 != null && gd.attr04.equals(gdgrp.attr04)) ||
			    						(poprl.memo.charAt(0) == 'E' && gd.attr05 != null && gd.attr05.equals(gdgrp.attr05)) ||
			    						(poprl.memo.charAt(0) == 'F' && gd.attr06 != null && gd.attr06.equals(gdgrp.attr06)) ||
			    						(poprl.memo.charAt(0) == 'G' && gd.attr07 != null && gd.attr07.equals(gdgrp.attr07)) ||
			    						(poprl.memo.charAt(0) == 'H' && gd.attr08 != null && gd.attr08.equals(gdgrp.attr08)) || 
			    						(poprl.memo.charAt(0) == 'Z' && sggrp.str7 != null && sg.str7 != null &&  sggrp.str7.equalsIgnoreCase(sg.str7)))
			    					{
			    						break;
			    					}
			            		}
			            		if (n >= grpvec.size()) 
			            		{
			            			String[] s = new String[]{"0",String.valueOf(prgd.sgindex)};
			            			if (cmp.ruleinfo.popzsz == 'Y') s[0] = String.valueOf(Convert.toDouble(s[0]) + (sg.hjje - sg.hjzk - getGoodsPaymentApportion(prgd.sgindex,sg)));
    	            		        else s[0] = String.valueOf(Convert.toDouble(s[0]) + (sg.hjje - getGoodsPaymentApportion(prgd.sgindex,sg)));
			            			grpvec.add(s);
			            		}
			            		else
			            		{
			            			String[] s = (String[])grpvec.elementAt(n);
			            			if (cmp.ruleinfo.popzsz == 'Y') s[0] = String.valueOf(Convert.toDouble(s[0]) + (sg.hjje - sg.hjzk - getGoodsPaymentApportion(prgd.sgindex,sg)));
    	            		        else s[0] = String.valueOf(Convert.toDouble(s[0]) + (sg.hjje - getGoodsPaymentApportion(prgd.sgindex,sg)));
			            			s[1] = s[1] + "," + String.valueOf(prgd.sgindex);
			            			grpvec.set(n, s);
			            		}
		    				}
		    				
	            			// 当分组数量超过限制,按分组金额排序,排除金额较小分组商品不参与促销
	            			if (poprl.memo.length() > 1 && (poprl.memo.charAt(1) == 'L' || poprl.memo.charAt(1) == 'E') && grpvec.size() > poprl.condsl)
	            			{
	            				// 冒泡排序
	            				for (int j=0;j<grpvec.size();j++)
	            				{
	            					String[] s = (String[])grpvec.elementAt(j);
	            					for (int n=j+1;n<grpvec.size();n++)
	            					{
	            						String[] s1 = (String[])grpvec.elementAt(n);
	            						if (Convert.toDouble(s[0]) < Convert.toDouble(s1[0]))
	            						{
	            							// 交换,金额大的分组排前面
	            							String[] s2 = new String[]{s[0],s[1]};
	            							grpvec.set(j,s1);
	            							grpvec.set(n,s2);
	            						}
	            					}
	            				}
	            				
	            				// 去掉超过分组条件商品的促销标记
	            				for (int j=(int)poprl.condsl;j<grpvec.size();j++)
	            				{
	            					// 除开多余分组的商品
	            					String[] s = ((String[])grpvec.elementAt(j))[1].split(",");
	            					for (int n=0;n<s.length;n++)
	            					{
	            						int sgindex = Convert.toInt(s[n]);
	            						
	            						// 查找商品行号在rulegoods中的位置,将商品从参与促销的集合中去掉
	            						int k = 0;
	            						PopRuleGoods prgd = null;
	            						for (;k<rulegoods.size();k++)
	            						{
	            							prgd = ((PopRuleGoods)rulegoods.elementAt(k));
	            							if (prgd.sgindex == sgindex) break;
	            						}

	    	            				// 恢复该行促销标记并从集合中除开
	            						if (k < rulegoods.size() && prgd != null)
	            						{
	            							SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(prgd.sgindex);
		        							if (goodsCmPop != null && goodsCmPop.size() > prgd.sgindex)
		        							{
		        				        		Vector popvec = (Vector)goodsCmPop.elementAt(prgd.sgindex);
		        				        		if (popvec != null && popvec.size() > prgd.cmindex) ((CmPopGoodsDef)popvec.elementAt(prgd.cmindex)).used = false;
		        							}
	        							
		        							// 合计数据去掉该商品行
		    		            			hjzje  -= sg.hjje;
		    		            			hjcjj  -= sg.hjje - sg.hjzk;
		    		            			hjcxsl -= sg.sl;
		    	            		        if (cmp.ruleinfo.popzsz == 'Y') hjcxje -= sg.hjje - sg.hjzk - getGoodsPaymentApportion(prgd.sgindex,sg);
		    	            		        else hjcxje -= sg.hjje - getGoodsPaymentApportion(prgd.sgindex,sg);

		    	            		        // 从促销集合中排除商品
		    	            		        rulegoods.remove(k);
	            						}
	            					}
	            					
	            					// 除开分组
	            					grpvec.remove(j);
	            					j--;
	            				}
		    				}
		    				
		    				// 分组后的数量是否符合条件
	            			if ((poprl.memo.length() > 1 && poprl.memo.charAt(1) == 'E' && grpvec.size() == poprl.condsl) ||
	            				(poprl.memo.length() > 1 && poprl.memo.charAt(1) == 'G' && grpvec.size() >= poprl.condsl) ||
	            				(poprl.memo.length() > 1 && poprl.memo.charAt(1) == 'L' && grpvec.size() <= poprl.condsl))
	            			{
	            				// 分组数量已符合条件
	            				if (poprl.levelminus == 'Y') laderbs = ManipulatePrecision.integerDiv((hjcxje - yfje - poprl.levelje),poprl.condje);
	    		    			else laderbs = ManipulatePrecision.integerDiv((hjcxje - yfje), poprl.condje);
	            			}
	            			else
	            			{
	            				// 分组数量不符合条件
	            				laderbs = 0;
	            			}
		    			}
		    		}
		    		else 
		    		if (cmp.ruleinfo.condmode == '3' && (hjcxsl - yfsl) >= poprl.levelsl && (hjcxje - yfje) >= poprl.levelje)
		    		{
		    			if (poprl.levelminus == 'Y') 
		    			{
		    				laderbs = ManipulatePrecision.integerDiv((hjcxsl - yfsl - poprl.levelsl),poprl.condsl);
			    			if (ManipulatePrecision.integerDiv((hjcxje - yfje - poprl.levelje),poprl.condje) < laderbs)
							{
			    				laderbs = ManipulatePrecision.integerDiv((hjcxje - yfje - poprl.levelje),poprl.condje);
							}
		    			}
		    			else
		    			{
			    			laderbs = ManipulatePrecision.integerDiv((hjcxsl - yfsl),poprl.condsl);
			    			if (ManipulatePrecision.integerDiv((hjcxje - yfje),poprl.condje) < laderbs)
							{
			    				laderbs = ManipulatePrecision.integerDiv((hjcxje - yfje),poprl.condje);
							}
		    			}
		    		}
		    		if (laderbs <= 0) continue;
	    			if (poprl.maxfb > 0 && laderbs > poprl.maxfb) laderbs = poprl.maxfb;
	    			if (groupbs >= 0 && laderbs > groupbs) laderbs = groupbs;    			
	    			
	    			// 计算商品参与本阶梯已使用条件额,剩余部分循环参与下一个阶梯
	    			if (cmp.ruleinfo.condmode == '1')
	    			{	    			
		    			yfsl += poprl.condsl * laderbs;
	    			}
	    			else 
	    			if (cmp.ruleinfo.condmode == '2')
	    			{
		    			yfje += poprl.condje * laderbs;
	    			}
	    			else 
	    			if (cmp.ruleinfo.condmode == '3')
	    			{
		    			yfsl += poprl.condsl * laderbs;
		    			yfje += poprl.condje * laderbs;
		    		}
	    			
	    			// 只要满足了阶梯条件,就需要根据该阶梯查找对应的赠品(档期ID,规则ID,阶梯ID,促销倍数)
		    		rulegifts.add(poprl.dqid + "," + poprl.ruleid + "," + poprl.ladderid + "," + laderbs);
	    			
		    		// 促销金额采用动态表达式运算,laddername填写表达式
		    		if (poprl.laddername.toLowerCase().startsWith("calc|"))
		    		{
		    			String summarylabel = poprl.laddername.toLowerCase();
		    			String exp = summarylabel.substring(summarylabel.indexOf("calc|")+5);
		    			
						// 替换关键字字段值
						String fld = "";
						int start = 0,end = 0;
						while (exp.indexOf(":",start) >= 0)
						{
							start = exp.indexOf(":",start)+1;
							end = -1;
							for (int ii=start;ii<exp.length();ii++) 
							{
								if (!((exp.charAt(ii) >= '0' && exp.charAt(ii) <= '9') || (exp.charAt(ii) >= 'a' && exp.charAt(ii) <= 'z')))
								{
									end = ii;
									break;
								}
							}
							if (end >= 0) fld = exp.substring(start,end);
							else fld = exp.substring(start);
							String val = "0";
							if (fld.equalsIgnoreCase("hjzje"))
							{
								val = String.valueOf(hjzje);
							}
							else if (fld.equalsIgnoreCase("hjcjj"))
							{
								val = String.valueOf(hjcjj);
							}
							else if (fld.equalsIgnoreCase("hjzsl"))
							{
								val = String.valueOf(hjcxsl);
							}
							exp = ExpressionDeal.replace(exp,":"+fld,val);
						}

						// 计算表达式
						String val = ExpressionDeal.SpiltExpression(exp);
						poprl.popje = Convert.toDouble(val);
		    		}
		    		
	    			// 根据促销结果形式计算出本阶梯条件应该折扣的金额
		    		double laderje = 0;	
		    		if (
		    			(cmp.ruleinfo.popmode == '0') ||
		    			(cmp.ruleinfo.popmode == '1' && poprl.popje >= 0) ||		    			
		    			(cmp.ruleinfo.popmode == '2' && 1 > poprl.popje && poprl.popje >= 0) ||
		    			(cmp.ruleinfo.popmode == '3' && poprl.popje >= 0))
		    		{
		    			// 满数量条件的促销,超过条件部分的数量被拆分并记为未参与本次促销,去参与其他促销或再次计算时参与下一个阶梯
		    			double bhjzje = 0,bhjcjj = 0;
		    			if (cmp.ruleinfo.condmode == '1')
		    			{
				    		double sl = 0,ftsl = poprl.condsl * laderbs;
			            	for (int j = 0; j<rulegoods.size();j++)
			            	{
			            		PopRuleGoods prgd = ((PopRuleGoods)rulegoods.elementAt(j));
			            		SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(prgd.sgindex);
			            		
			            		// 拆分商品行
			            		if (ftsl-sl > 0 && ManipulatePrecision.doubleCompare(sg.sl,ftsl-sl,4) > 0)
			            		{
			            			SaleGoodsDef newsg = SplitSaleGoodsRow(prgd.sgindex,ftsl-sl);
			            			if (newsg != null)
			            			{
			            				// 刷新拆分商品行
			            				refreshCmPopUI();
			            				
			            				// 够满则拆分的商品行要标记为已运算不再计算,每满记未运算可再计算
			            				Vector popvec = (Vector)goodsCmPop.elementAt(saleGoods.size()-1);
			            				if (popvec != null && popvec.size() > prgd.cmindex) ((CmPopGoodsDef)popvec.elementAt(prgd.cmindex)).used = (cmp.ruleinfo.condtype == '0')?true:false;
			            				
			            				// 合计数据去掉被拆分的商品行
				            			hjzje  -= newsg.hjje;
				            			hjcjj  -= newsg.hjje - newsg.hjzk;
				            			hjcxsl -= newsg.sl;
			            		        if (cmp.ruleinfo.popzsz == 'Y') hjcxje -= newsg.hjje - newsg.hjzk - getGoodsPaymentApportion(saleGoods.size()-1,newsg);
			            		        else hjcxje -= newsg.hjje - getGoodsPaymentApportion(saleGoods.size()-1,newsg);
			            			}
			            		}
			            		
			            		sl += sg.sl;
			            		if (sl > ftsl && (sl-ftsl) >= sg.sl)
			            		{
			            			// 超过数量条件部分从本次促销集合中删除
			            			// 够满则拆分的商品行要标记为已运算不再计算,每满记未运算可再计算
		            				Vector popvec = (Vector)goodsCmPop.elementAt(prgd.sgindex);
		            				if (popvec != null && popvec.size() > prgd.cmindex) ((CmPopGoodsDef)popvec.elementAt(prgd.cmindex)).used = (cmp.ruleinfo.condtype == '0')?true:false;
		            				
			        	        	// 合计数据去掉该商品行
			            			hjzje  -= sg.hjje;
			            			hjcjj  -= sg.hjje - sg.hjzk;
			            			hjcxsl -= sg.sl;
			            			if (cmp.ruleinfo.popzsz == 'Y') hjcxje -= sg.hjje - sg.hjzk - getGoodsPaymentApportion(prgd.sgindex,sg);
			            			else hjcxje -= sg.hjje - getGoodsPaymentApportion(prgd.sgindex,sg);
			            			
			            			rulegoods.remove(j);
			            			j--;
			            			continue;
			            		}
		            			if (sl <= ftsl)
		            			{ 
		            				bhjzje += sg.hjje;
		            				bhjcjj += sg.hjje - sg.hjzk;
		            			}
		            			else
		            			{
		            				bhjzje += sg.jg * (sg.sl - (sl - ftsl));
		            				bhjcjj +=(sg.jg - ManipulatePrecision.doubleConvert(sg.hjzk/sg.sl)) * (sg.sl - (sl - ftsl));
		            			}
			            	}
			            	
			            	// myShop特色逢倍促销,逢倍数以后第N倍的那个商品促销,折扣只分摊到N倍的商品行,其他商品行不促销
			    			if (cmp.ruleinfo.rulename != null && cmp.ruleinfo.rulename.trim().equals("逢倍促销"))
			    			{
				    			int bs = (int)poprl.condsl;
				    			int xl = poprl.maxfb;
					    		sl = 0;
				            	for (int j = 0; j<rulegoods.size();j++)
				            	{
									PopRuleGoods prgd = (PopRuleGoods)rulegoods.elementAt(j);
				            		SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(prgd.sgindex);		            		
				    				
				            		// 不是倍数行不参与分摊
				            		sl += sg.sl;
				            		if ((sl%bs != 0 && sg.sl <= sl%bs) || (xl > 0 && sl/bs > xl))
				            		{
				            			// 合计数据去掉该商品行
				            			hjzje  -= sg.hjje;
				            			hjcjj  -= sg.hjje - sg.hjzk;
				            			hjcxsl -= sg.sl;
				            			if (cmp.ruleinfo.popzsz == 'Y') hjcxje -= sg.hjje - sg.hjzk - getGoodsPaymentApportion(prgd.sgindex,sg);
				            			else hjcxje -= sg.hjje - getGoodsPaymentApportion(prgd.sgindex,sg);
				            			
				            			rulegoods.remove(j);
				            			j--;
				            		}
				            	}
			    			}
		    			}
		    			else
		    			{
		    				// 满金额条件
		    				bhjzje = hjzje;
		    				bhjcjj = hjcjj;
		    			}
		    			
		    			// 如果是每满方式(0=够满/1=每满)且满金额条件按条件金额计算比例
		    			boolean everymoney = false;
		    			if (cmp.ruleinfo.condtype != '0' && cmp.ruleinfo.condmode != '1') everymoney = true;
		    			
		    			// 计算促销折扣
		    			if (cmp.ruleinfo.popmode == '1')			// 指定促销价格
		    			{
			    			if (cmp.ruleinfo.popzsz == 'Y')
			    			{
			    				// 折上折模式,可打折金额要除掉已折扣部分
				    			laderje = (everymoney?poprl.condje * laderbs:bhjcjj) - (poprl.popje * laderbs) - popje;
			    			}
			    			else
			    			{
			    				// 非折上折时,商品分摊时先清空已折扣金额,再折到成交价
			    				laderje = (everymoney?poprl.condje * laderbs:bhjzje) - (poprl.popje * laderbs) - popje;
			    			}
		    			}
		    			else
		    			if (cmp.ruleinfo.popmode == '2')			// 指定打折比率
		    			{
			    			// 指定打折比率,不能乘倍数
			    			if (cmp.ruleinfo.popzsz == 'Y')
			    			{
			    				// 折上折模式,在成交价基础上再打折
			    				laderje = (1 - poprl.popje) * (everymoney?poprl.condje * laderbs:bhjcjj);
			    				if (popje + laderje > bhjcjj) laderje = bhjcjj - popje;
			    			}
			    			else
			    			{
			    				// 非折上折时,在原价基础上计算折扣,并清空其他折扣
			    				laderje = (1 - poprl.popje) * (everymoney?poprl.condje * laderbs:bhjzje);
			    				if (popje + laderje > bhjzje) laderje = bhjzje - popje;
			    			}
		    			}
		    			else
		    			if (cmp.ruleinfo.popmode == '3')			// 指定减价金额
		    			{
		    				laderje = poprl.popje * laderbs;
		    				if (cmp.ruleinfo.popzsz == 'Y')
			    			{
			    				// 折上折模式,在成交价基础上再减价,如果累计减价金额超过成交价最多减到0
			    				if (popje + laderje > bhjcjj) laderje = bhjcjj - popje;
			    			}
			    			else
			    			{
			    				// 非折上折时,在原价基础上计算减价,并清空其他折扣,如果累计减价金额超过成交价最多减到0
			    				if (popje + laderje > bhjzje) laderje = bhjzje - popje;
			    			}
		    			}
		    			
		    			// 促销金额不能<0
		    			if (laderje < 0) laderje = 0;
		    		}
		    		else
		    		if ( 
		    			(cmp.ruleinfo.popmode == '4' && poprl.popje >= 0) ||
		    			(cmp.ruleinfo.popmode == '5' && 1 > poprl.popje && poprl.popje >= 0) ||
		    			(cmp.ruleinfo.popmode == '6' && poprl.popje >= 0))
		    		{
		    			// 指定单个商品的促销价值,达到条件的所有商品数量都参与促销
		    			double sgsl = 0,condsl = poprl.condsl * laderbs;
		    			for (int j=0;j<rulegoods.size();j++)
		    			{
		    				PopRuleGoods prgd = (PopRuleGoods)rulegoods.elementAt(j);
		    				SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(prgd.sgindex);	    				
		    				
		    				// 每满数量条件,超过数量部分从本次促销集合中删除,可参与其他促销或再次计算时参与下一个阶梯
		    				if (cmp.ruleinfo.condmode == '1' && cmp.ruleinfo.condtype != '0')
		    				{
		    					if (condsl-sgsl > 0 && ManipulatePrecision.doubleCompare(sg.sl,condsl-sgsl,4) > 0)
			            		{
			            			SaleGoodsDef newsg = SplitSaleGoodsRow(prgd.sgindex,condsl-sgsl);
			            			if (newsg != null)
			            			{
			            				// 刷新拆分商品行
			            				refreshCmPopUI();
			            				
			            				// 够满则拆分的商品行要标记为已运算不再计算,每满记未运算可再计算
			            				Vector popvec = (Vector)goodsCmPop.elementAt(saleGoods.size()-1);
			            				if (popvec != null && popvec.size() > prgd.cmindex) ((CmPopGoodsDef)popvec.elementAt(prgd.cmindex)).used = (cmp.ruleinfo.condtype == '0')?true:false;
			            				
			            				// 合计数据去掉被拆分的商品行
				            			hjzje  -= newsg.hjje;
				            			hjcjj  -= newsg.hjje - newsg.hjzk;
				            			hjcxsl -= newsg.sl;
			            		        if (cmp.ruleinfo.popzsz == 'Y') hjcxje -= newsg.hjje - newsg.hjzk - getGoodsPaymentApportion(saleGoods.size()-1,newsg);
			            		        else hjcxje -= newsg.hjje - getGoodsPaymentApportion(saleGoods.size()-1,newsg);
			            			}
			            		}
		    					sgsl += sg.sl;
			            		if (sgsl > condsl && (sgsl-condsl) >= sg.sl)
			            		{
			            			// 超过数量条件部分从本次促销集合中删除
			            			// 够满则拆分的商品行要标记为已运算不再计算,每满记未运算可再计算
		            				Vector popvec = (Vector)goodsCmPop.elementAt(prgd.sgindex);
		            				if (popvec != null && popvec.size() > prgd.cmindex) ((CmPopGoodsDef)popvec.elementAt(prgd.cmindex)).used = (cmp.ruleinfo.condtype == '0')?true:false;
		            				
			        	        	// 合计数据去掉该商品行
			            			hjzje  -= sg.hjje;
			            			hjcjj  -= sg.hjje - sg.hjzk;
			            			hjcxsl -= sg.sl;
			            			if (cmp.ruleinfo.popzsz == 'Y') hjcxje -= sg.hjje - sg.hjzk - getGoodsPaymentApportion(prgd.sgindex,sg);
			            			else hjcxje -= sg.hjje - getGoodsPaymentApportion(prgd.sgindex,sg);
			            			
			            			rulegoods.remove(j);
			            			j--;
			            			continue;
			            		}
		    				}
		    				else
		    				{
		    					sgsl += sg.sl;
		    				}
		    			}
		    			
		    			// 每满数量条件相当于限量,例如每满10个每个X元,则限量应该是条件10*翻倍
		    			// 指定单个商品的促销价格,poprl.maxfb表示限量个数
		    			double zsl = poprl.maxfb;
		    			if (zsl > 0) zsl = Math.min(zsl,sgsl);
		    			else zsl = sgsl;

		    			// 计算
		    			double sysl = zsl,sl = 0;
		    			double levelsl = 0,fhsl = 0;
		    			boolean fh = false;
		    			for (int j=0;j<rulegoods.size();j++)
		    			{
		    				// 已计算完限制数量退出循环
		    				if (sysl <= 0) break;
		    				
		    				// 每个商品单个计算
		    				PopRuleGoods prgd = (PopRuleGoods)rulegoods.elementAt(j);	    				
		    				SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(prgd.sgindex);
		    				
		    				// 除门槛条件的规则,未达到门槛条件的商品行不参与计算
		    				if (cmp.ruleinfo.condmode == '1' && poprl.levelminus == 'Y')
		    				{
		    					levelsl += sg.sl;
		    					if (levelsl <= poprl.levelsl) continue;
		    					else
		    					{
		    						// 只计算一次
		    						if (!fh) { fh = true; fhsl = sg.sl - (levelsl - poprl.levelsl); }
		    						else fhsl = 0;
		    					}
		    				}
		    				
		    				// 计算可促销数量
		    				if (zsl > 0 && (sg.sl-fhsl) > sysl) sl = sysl;
		    				else sl = (sg.sl-fhsl);
		    				sysl -= sl;

		    				// 拆分超过门槛数量的商品行
		    				if (ManipulatePrecision.doubleCompare(sg.sl,sl,4) > 0)
		            		{
		            			SaleGoodsDef newsg = SplitSaleGoodsRow(prgd.sgindex,sl);
		            			if (newsg != null)
		            			{
		            				// 刷新拆分商品行
		            				refreshCmPopUI();
		            				
		            				// 拆分的商品行已参与促销条件计算
		            				Vector popvec = (Vector)goodsCmPop.elementAt(saleGoods.size()-1);
		            				if (popvec != null && popvec.size() > prgd.cmindex) ((CmPopGoodsDef)popvec.elementAt(prgd.cmindex)).used = true;
		            			}
		            		}
		    				
		    				// 计算促销折扣
		    				double zke = 0;
		    				if (cmp.ruleinfo.popmode == '4')		// 指定成交价
		    				{
			    				// 商品当前折扣低于可促销的折扣则补足促销折扣
			    				if (sg.jg > poprl.popje && ManipulatePrecision.doubleCompare(getZZK(sg),(sg.jg - poprl.popje) * sl,2) < 0)
			                	{
			    					// 不允许折上折,清空其他折扣
			    					if (cmp.ruleinfo.popzsz != 'Y') clearGoodsAllRebate(prgd.sgindex);
			    					zke = ManipulatePrecision.doubleConvert((sg.jg - poprl.popje) * sl - getZZK(sg),2,1);
			    				}
		    				}
		    				else
		    				if (cmp.ruleinfo.popmode == '5')		// 指定打折率
		    				{
			    				// 促销允许折上折则在当前成交价基础上再折,不允许折上折低价优先清空其他折扣
			    				if (cmp.ruleinfo.popzsz == 'Y')
			    				{
			    					zke = ManipulatePrecision.doubleConvert((1 - poprl.popje) * (sg.hjje - getZZK(sg)) * (sl / sg.sl), 2, 1);
			    				}
			    				else if (ManipulatePrecision.doubleCompare(getZZK(sg),(1 - poprl.popje) * sl * sg.jg,2) < 0)
			    				{
			    					clearGoodsAllRebate(prgd.sgindex);
			    					zke = ManipulatePrecision.doubleConvert((1 - poprl.popje) * sl * sg.jg - getZZK(sg), 2, 1);
			    				}
		    				}
		    				else
		    				if (cmp.ruleinfo.popmode == '6')		// 指定减价额
		    				{
			    				// 允许折上折则成交价基础上再减价金额,再减不能超过商品价值
			    				// 不允许折上折则低价优先清空其他折扣,减价金额不能超过商品价值
		    					if (cmp.ruleinfo.popzsz == 'Y')
		    					{
			                    	zke = ManipulatePrecision.doubleConvert((poprl.popje * sl), 2, 1);
			                    	if (sg.hjje - (getZZK(sg) + zke) < 0) zke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg),2,1);
		    					}
		    					else if (ManipulatePrecision.doubleCompare(getZZK(sg),poprl.popje * sl,2) < 0)
		    					{
	    							clearGoodsAllRebate(prgd.sgindex);
	    							
		                			zke = ManipulatePrecision.doubleConvert(poprl.popje * sl);
		                			if (sg.hjje - zke < 0) zke = sg.hjje;
		                			zke = ManipulatePrecision.doubleConvert(zke - getZZK(sg), 2, 1);
		    					}
		    				}
		    				
		    				// 记录促销折扣明细
		    				if (zke > 0)
		    				{
		    	    			// 标记有促销
		    	    			havepop = true;
		    	    			
		    					double oldzke = sg.zszke;
			    				sg.zszke += zke;
								sg.zszke  = getConvertRebate(prgd.sgindex,sg.zszke);
			    				sg.zsdjbh = String.valueOf(cmp.cmpopseqno);
								zke = sg.zszke - oldzke;
								getZZK(sg);
		    				
								addCmPopDetail(prgd.sgindex,cmp,zke);
		    				}
		    			}
		    		}
		    		
		    		// 累计促销金额
		    		popje += laderje;
	    			
		    		// 不再执行剩余阶梯,跳出条件阶梯循环 
		    		if (!loopladder) break;
		    	}
	    	}

	    	// 检查促销折扣是否超限
	    	if (cmp.ruleinfo.maxpopje > 0  && popje > cmp.ruleinfo.maxpopje) popje = cmp.ruleinfo.maxpopje;
	    	if (cmp.ruleinfo.popzsz == 'Y' && popje > hjcjj) popje = hjcjj;
	    	if (cmp.ruleinfo.popzsz != 'Y' && popje > hjzje) popje = hjzje;
	    	
	    	// 有除外付款方式且初始合计数据满足消费条件,则进行除外付款方式的预付款
	    	// 然后再次计算除外付款以后是否还满足消费条件
	    	if (cmp.ruleinfo.payexcp != null && !cmp.ruleinfo.payexcp.trim().equals("") && 
	    		isPreparePay == payNormal && cmp.ruleinfo.summode != '0' && 
	    		(popje > 0 || rulegoods.size() > 0) &&
	    		(!cmp.ruleinfo.payexcp.trim().equalsIgnoreCase("MKTPAYEXCP") || 
	    		 (cmp.ruleinfo.payexcp.trim().equalsIgnoreCase("MKTPAYEXCP") && GlobalInfo.sysPara.mjPaymentRule != null && GlobalInfo.sysPara.mjPaymentRule.length() > 0)
	    		))
	    	{
	        	// 只有一个促销规则且所有商品都参与该促销,则可自动进行付款分摊,否则人工输入付款方式分摊
	    		if (rulegoods.size() == saleGoods.size()) this.needApportionPayment = false;
	    		else this.needApportionPayment = true;
	    		
	    		// 设置除外付款方式,=MKTPAYEXCP表示以门店参数定义受限付款方式为准,分两个变量记以便支持不同促销不同除外付款
	    		if (!cmp.ruleinfo.payexcp.trim().equalsIgnoreCase("MKTPAYEXCP")) payPopPrepareExcp = cmp.ruleinfo.payexcp.trim();
	    		else payPopPrepareExcp = GlobalInfo.sysPara.mjPaymentRule;
		    	payPopOtherExcp += payPopPrepareExcp + "|";
	    		
	            // 提示先输入券付款
	            if (new MessageBox("本笔交易有需要除券的活动促销,请先输入券付款金额\n\n如果顾客没有券付款,请直接按‘退出’键").verify() != GlobalVar.Exit)
	            {
	                // 开始预付除外付款方式
	                isPreparePay = payPopPrepare;
	
	                // 除外付款必须进行分摊,设置分摊模式
	                char temphavePayRule = GlobalInfo.sysPara.havePayRule;   
	                GlobalInfo.sysPara.havePayRule = 'A';
	                
	                // 打开付款窗口
	                new SalePayForm().open(saleEvent.saleBS);
	 
	                // 还原分摊模式
	                GlobalInfo.sysPara.havePayRule = temphavePayRule;
	                
	                // 付款完成，开始新交易
	                if (this.saleFinish)
	                {
	                	sellFinishComplete();
	
	                    // 预先付款就已足够,不再继续后续付款
	                    doCmPopExit = true;
	                    return havepop;
	                }
	            }
	
	            // 进入实付剩余付款方式,只允许非券付款方式进行付款
	            isPreparePay = payPopOther;
	            
	            // 清除记录的数据 
	            for (int j = 0; j<rulegoods.size();j++)
	            {
					PopRuleGoods prgd = (PopRuleGoods)rulegoods.elementAt(j);
	        		Vector popvec = (Vector)goodsCmPop.elementAt(prgd.sgindex);
	        		if (popvec != null && popvec.size() > prgd.cmindex)
	        		{
	        			CmPopGoodsDef cp = (CmPopGoodsDef)popvec.elementAt(prgd.cmindex);
		        		cp.used = false;
	        		}
	            }
	            rulegoods.removeAllElements();
	            
	            // 再次计算除外付款以后是否还满足消费条件
	            continue;
	    	}
	    	else
	    	{
	    		// 不再计算除外付款以后是否还满足消费条件
	    		break;
	    	}
        } while(true);
        
        // 是折扣形式的促销则先计算折扣金额
        if (cmp.ruleinfo.popmode != '0')
        {
        	// 规则中的商品不累计,则每个商品都按促销折扣计算
        	// 规则中的商品需累计,则总的促销金额分摊到各商品
	    	if (cmp.ruleinfo.summode == '0')			// 不累计方式的规则的折扣记入yhzke,取消付款才不会被清0
	    	{
	    		// 折扣金额
	    		double zke = 0;
	    		
	    		// 取商品范围设置
	    		if (cmp.ruleinfo.popmode == '7')
	    		{
	    			// 计算是否超过限量
	    			double sl = saleGoodsDef.sl;
	    			if (cmp.popmaxmode != '0' && cmp.popmax > 0)
	    			{
	        			// 联网检查会员已购买数量
	    				double maxsl = cmp.popmax;
	            		if (cmp.popmaxmode != '1')
	            		{
	            			if (curCustomer != null) maxsl = NetService.getDefault().findVIPMaxSl("POP",curCustomer.code,curCustomer.type,cmp.cmpopseqno,saleGoodsDef.code,saleGoodsDef.gz,saleGoodsDef.uid);
	            			else maxsl = NetService.getDefault().findVIPMaxSl("POP","","",cmp.cmpopseqno,saleGoodsDef.code,saleGoodsDef.gz,saleGoodsDef.uid);
	            			if (maxsl < 0) maxsl = 0;
	            		}
	            		
	        			// 计算本笔交易可销售数量	    				
	    				double yxsl = 0;
	    				for (int i=0;i<saleGoods.size();i++)
	    				{
	    					SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(i);
	    	        		Vector popvec = (Vector)goodsCmPop.elementAt(i);
	    	        		for (int j=0;popvec != null && j<popvec.size();j++)
	    	        		{
	    	        			CmPopGoodsDef cmpoth = (CmPopGoodsDef)popvec.elementAt(j);
	    	        			if (i != index && cmpoth.cmpopseqno == cmp.cmpopseqno && cmpoth.used) 
	    	        			{
	    	        				yxsl += sg.sl;
	    	        				break;
	    	        			}
	    	        		}
	    				}
	    				if (maxsl - yxsl > 0) sl = Math.min(maxsl - yxsl,saleGoodsDef.sl);
	    				else sl = 0;
	    				if (sl < 0) sl = 0;
	    			}
	    			
	    			// 有足够数量
	    			if (ManipulatePrecision.doubleCompare(sl,0,4) > 0)
	    			{
		    			// 拆分商品行
	    				if (ManipulatePrecision.doubleCompare(saleGoodsDef.sl,sl,4) > 0) SplitSaleGoodsRow(index,sl);
	    				
		    			// 指定促销价用成交价四舍五入以后再减
		    			if (cmp.popmode == '1' && saleGoodsDef.jg > cmp.poplsj && cmp.poplsj >= 0)
		    			{
		    				zke = ManipulatePrecision.doubleConvert(saleGoodsDef.jg * sl) - ManipulatePrecision.doubleConvert(cmp.poplsj * sl);
							saleGoodsDef.yhzke += zke;
				    		saleGoodsDef.yhzkfd = cmp.poplsjzkfd;
				    		
				    		// 会员价差记入会员折扣
				    		if (isMemberHyjMode() && cmp.poplsj > cmp.pophyj && cmp.pophyj >= 0)
		                    {
				    			double hyzk = ManipulatePrecision.doubleConvert(cmp.poplsj * sl) - ManipulatePrecision.doubleConvert(cmp.pophyj * sl);
		                        saleGoodsDef.hyzke += hyzk;
		                        saleGoodsDef.hyzkfd = cmp.pophyjzkfd;
				    			zke += hyzk;
		                    }
		    			}
		    			else if (cmp.popmode == '2' && 1 > cmp.poplsj && cmp.poplsj >= 0)
		    			{
		    				zke = ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert(saleGoodsDef.jg * sl) * (1 - cmp.poplsj));
							saleGoodsDef.yhzke += zke;
				    		saleGoodsDef.yhzkfd = cmp.poplsjzkfd;
							
				    		// 会员价差记入会员折扣
				    		if (isMemberHyjMode() && cmp.poplsj > cmp.pophyj && cmp.pophyj > 0)
		                    {
				    			double hyzk = ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert(saleGoodsDef.jg * sl) * (cmp.poplsj - cmp.pophyj));
		                        saleGoodsDef.hyzke += hyzk;
		                        saleGoodsDef.hyzkfd = cmp.pophyjzkfd;
				    			zke += hyzk;
		                    }			    		
		    			}
		    			else if (cmp.popmode == '3' && saleGoodsDef.jg > cmp.poplsj && cmp.poplsj > 0)
		    			{
		    				zke = ManipulatePrecision.doubleConvert(cmp.poplsj * sl);
							saleGoodsDef.yhzke += zke;
							saleGoodsDef.yhzkfd = cmp.poplsjzkfd;
							
							// 会员价差记入会员折扣
				    		if (isMemberHyjMode() && cmp.pophyj > cmp.poplsj && cmp.pophyj > 0)
		                    {
				    			double hyzk = ManipulatePrecision.doubleConvert(cmp.pophyj * sl) - ManipulatePrecision.doubleConvert(cmp.poplsj * sl);
		                        saleGoodsDef.hyzke += hyzk;
		                        saleGoodsDef.hyzkfd = cmp.pophyjzkfd;
				    			zke += hyzk;
		                    }
		    			}
	    			}
	    		}
	    		else
	    		{
		    		zke = popje;
		    		
	    			saleGoodsDef.yhzke += popje;
		    		saleGoodsDef.yhzkfd = cmp.poplsjzkfd;
	    		}

	    		if (zke > 0)
	    		{
	    			// 标记有促销
	    			havepop = true;
	    			
		    		// 记录促销序号
		    		double oldzke = saleGoodsDef.yhzke;
		    		saleGoodsDef.yhzke = getConvertRebate(index,saleGoodsDef.yhzke);
		    		zke += (saleGoodsDef.yhzke - oldzke);
		    		saleGoodsDef.yhdjbh = String.valueOf(cmp.cmpopseqno);
		    		
		    		// 汇总商品总折扣
		    		getZZK(saleGoodsDef);
		    		
					// 记录促销折扣明细
					addCmPopDetail(index,cmp,zke);
	    		}
	    	}
	    	else								// 累计方式
	    	{
	    		// 非折上折检查总折扣额和当前成交价的折扣，取低价优先
	            if (popje > 0 && cmp.ruleinfo.popzsz != 'Y')
	            {
	            	if (ManipulatePrecision.doubleCompare(hjzje - hjcjj,popje,2) < 0)
	            	{
		            	// 清除商品的其他折扣,重算累计用于分摊
	            		hjzje = hjcjj = hjcxsl = hjcxje = 0;
		            	for (int j = 0; j<rulegoods.size();j++)
		            	{
							PopRuleGoods prgd = (PopRuleGoods)rulegoods.elementAt(j);
							
							// 清除其他折扣
							clearGoodsAllRebate(prgd.sgindex);
							
							// 非折上折按原价计算累计,因为如果算出的分摊折扣比当前折扣低则当前折扣放弃
		            		SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(prgd.sgindex);
		            		hjzje  += sg.hjje;
	            			hjcjj  += sg.hjje - sg.hjzk;
	            			hjcxsl += sg.sl;
	            			hjcxje += sg.hjje - getGoodsPaymentApportion(prgd.sgindex,sg);
		            	}
	            	}
	            	else
	            	{
	            		// 当前折扣比计算的促销促销折扣低，放弃本次计算的促销折扣
	            		popje = 0;
	            	}
	            }

	    		// 将总的促销金额分摊到各个商品
	    		if (popje > 0)
	    		{
	    			// 标记有促销
	    			havepop = true;
	    			
					// 百货或调试模式提示促销规则
	    			if (GlobalInfo.syjDef.issryyy != 'N' || ConfigClass.DebugMode)
	    			{
						Vector contents = new Vector();
						for (int j = 0; j<rulegoods.size();j++)
						{
							PopRuleGoods prgd = (PopRuleGoods)rulegoods.elementAt(j);
							
							SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(prgd.sgindex);
							contents.add(new String[]{(prgd.sgindex+1)+":"+sg.code,sg.name,
									ManipulatePrecision.doubleToString(sg.sl,4,1,true),
									ManipulatePrecision.doubleToString(sg.hjje-sg.hjzk),
									ManipulatePrecision.doubleToString(sg.hjje-sg.hjzk-getGoodsPaymentApportion(prgd.sgindex,sg))});
						}
						contents.add(new String[]{"合计","",ManipulatePrecision.doubleToString(hjcxsl,4,1,true),ManipulatePrecision.doubleToString(hjcjj),ManipulatePrecision.doubleToString(hjcxje)});
			            String[] title = { "商品编码", "商品名称", "数量","成交价","活动金额"};
			            int[] width = { 130,200,60,115,115 };
			            new MutiSelectForm().open("以下商品参加["+ cmp.ruleinfo.rulename + "]活动,总共可享受 " + ManipulatePrecision.doubleToString(popje) + " 元的促销折扣", title, width, contents,false,675,319,645,192,false);
	    			}
	    			
	    			// 组合促销中组内分组折扣占比需要先保存计算折扣前的原始数据,确保分摊折扣后占比不发生变化
	    			if (cmp.ruleinfo.isonegroup == 'Y')
	    			{
	    				for (int j=0; j<rulegoods.size();j++)
	    				{
	    					PopRuleGoods prgd = (PopRuleGoods)rulegoods.elementAt(j);
	    					
	    					SaleGoodsDef sgk = (SaleGoodsDef)saleGoods.elementAt(prgd.sgindex);
							if (cmp.ruleinfo.popzsz == 'Y')
							{
								sgk.num9 = ManipulatePrecision.doubleConvert(sgk.hjje-sgk.hjzk-getGoodsPaymentApportion(prgd.sgindex,sgk));
							}
							else
							{
								sgk.num9 = ManipulatePrecision.doubleConvert(sgk.hjje-getGoodsPaymentApportion(prgd.sgindex,sgk));
							}
	    				}
	    			}
	    			
					// 分摊促销折扣
					double ftje = 0;
					for (int j = 0; j<rulegoods.size();j++)
					{
						PopRuleGoods prgd = (PopRuleGoods)rulegoods.elementAt(j);
						SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(prgd.sgindex);
		
						// 折扣金额已分摊完
						if (popje - ftje <= 0) continue;
						
						// 把剩余未分摊金额，直接分摊到最后一个商品
						double zke = 0;
						if (j == (rulegoods.size() - 1))
						{
							zke = ManipulatePrecision.doubleConvert(popje - ftje,2, 1);
						}
						else
						{
							// 计算组合促销中分组折扣占比
							double popzb = 1;
							double cjjzb = 1;
							boolean havegrpzb = false;
			    			for (int n=0;groupvec != null && n<groupvec.size();n++)
			    			{
			    				double pop = ((PopRuleGoodsGroup)groupvec.elementAt(n)).popje;
			    				if (pop > 0) { havegrpzb = true;break; }
			    			}
							if (havegrpzb && cmp.ruleinfo.isonegroup == 'Y')
							{
				    			for (int n=0;groupvec != null && n<groupvec.size();n++)
				    			{
				    				double pop = ((PopRuleGoodsGroup)groupvec.elementAt(n)).popje;
				    				Vector sglist = ((PopRuleGoodsGroup)groupvec.elementAt(n)).goodslist;
				    				
				    				// 检查本商品属于当前分组
				    				int m = 0;
				    				for (;m <  sglist.size();m++) if (prgd.sgindex == Integer.parseInt((String)sglist.elementAt(m))) break;
				    				if  ( m >= sglist.size()) continue;
				    				
				    				// 计算分组商品的合计金额在所有商品的占比
				    				double hjje = 0,gpje = 0;
				    				for (int k=0; k<rulegoods.size();k++)
				    				{
				    					PopRuleGoods prgk = (PopRuleGoods)rulegoods.elementAt(k);
				    					SaleGoodsDef sgk = (SaleGoodsDef)saleGoods.elementAt(prgk.sgindex);
				    					hjje += sgk.num9;
				    					
				    					// 属于组内商品
				    					for (m=0;m<sglist.size();m++)
				    					{
				    						if (prgk.sgindex == Integer.parseInt((String)sglist.elementAt(m)))
				    						{
				    							gpje += sgk.num9;
				    							break;
				    						}
				    					}
				    				}
				    				cjjzb = gpje / hjje;
				    				
				    				// 分组折扣占比
				    				if (cmp.ruleinfo.popmode == '1') popzb = (gpje - pop*groupbs) / popje;
				    				else if (cmp.ruleinfo.popmode == '2') popzb = pop;
				    				else if (cmp.ruleinfo.popmode == '3') popzb = (pop*groupbs) / popje;
				    				break;
				    			}
							}
							
							// 计算本行商品可分摊的折扣金额
							if (cmp.ruleinfo.popzsz == 'Y')
							{
								zke = ManipulatePrecision.doubleConvert((sg.hjje-sg.hjzk-getGoodsPaymentApportion(prgd.sgindex,sg)) / (hjcxje * cjjzb) * (popje * popzb),2, 1);
							}
							else
							{
								zke = ManipulatePrecision.doubleConvert((sg.hjje-getGoodsPaymentApportion(prgd.sgindex,sg)) / (hjcxje * cjjzb) * (popje * popzb),2, 1);						
							}
						}

						if (zke > 0)
						{
							// 折扣后成交价不能小于0
							if (sg.hjje - (getZZK(sg) + zke) < 0) zke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg),2,1);
							double oldzszke = sg.zszke;
							sg.zszke += zke;
							if (j != (rulegoods.size() - 1))	// 最后一个商品不计算价格精度
							{
								sg.zszke  = getConvertRebate(prgd.sgindex,sg.zszke);
								zke = sg.zszke - oldzszke;
							}
							sg.zsdjbh = String.valueOf(cmp.cmpopseqno);	
							getZZK(sg);
							
		    				// 记录促销折扣明细
		    				addCmPopDetail(prgd.sgindex,cmp,zke);
						}
						
						// 计算已分摊的金额
						ftje += zke;
					}
	    		}
	    	}
        }
        
        // 有促销先刷新界面
        if (havepop) refreshCmPopUI();
        	
        // 不管什么促销形式都检查是否有相应的赠送结果
		for (int j = 0; j<rulegifts.size();j++)
		{
			String[] s = ((String)rulegifts.elementAt(j)).split(",");
			if (s.length < 4) continue;
			String dqid = s[0];
			String ruleid = s[1];
			String ladderid = s[2];
			int ladderbs = Integer.parseInt(s[3]);
			if (doCmPopGift(index,saleGoodsDef,cmp,dqid,ruleid,ladderid,ladderbs))
			{
				// 标记存在促销
				havepop = true;
				
				// 将产生促销赠送的条件商品设置促销
				for (int i = 0; i<rulegoods.size();i++)
				{
					PopRuleGoods prgd = (PopRuleGoods)rulegoods.elementAt(i);
					addCmPopDetail(prgd.sgindex,cmp,0,true);
				}
			}
		}
		
		// 未计算到促销还原商品规则的使用标记
		if (!havepop)
		{
			cmp.used = false;
			for (int j = 0; j<rulegoods.size();j++)
			{
				PopRuleGoods prgd = (PopRuleGoods)rulegoods.elementAt(j);
				if (goodsCmPop != null && goodsCmPop.size() > prgd.sgindex)
				{
	        		Vector popvec = (Vector)goodsCmPop.elementAt(prgd.sgindex);
	        		if (popvec != null && popvec.size() > prgd.cmindex) ((CmPopGoodsDef)popvec.elementAt(prgd.cmindex)).used = false;
				}
			}
		}
		
    	return havepop;
    }

	 // CMPOP促销模型
    public void findGoodsCMPOPInfo(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
    {
      
    	super.findGoodsCMPOPInfo(sg, goods, info);
    	
    	//记录分组号
    	if (goodsCmPop != null && goodsCmPop.size() > 0)
    	{
    		for (int i=0;i<saleGoods.size();i++)
        	{
    			//String amj=null;//满减分组号
    			//String bms=null;//满送分组号
    			//String cmz=null;//满赠分组号
    			SaleGoodsDef sgd = (SaleGoodsDef)saleGoods.elementAt(i);
    			if (sgd.str7 != null && sgd.str7.length()>0)
    			{
    				continue;
    			}
    			else
    			{
            		Vector popvec = (Vector)goodsCmPop.elementAt(i);
            		for (int j=0;popvec != null && j<popvec.size();j++)
            		{
            			CmPopGoodsDef cp = (CmPopGoodsDef)popvec.elementAt(j);
            			if (cp.ruleinfo.popmode!='0')
            			{
            				//满减分组号
            				if (cp.str1 != null)
            				{
            					sgd.str7 = cp.str1;
            				}
            				
            			}
            			/*else if (cp.ruleinfo.popmode=='0')
            			{
            				//满送,满赠
            				if (cp.str1 != null)
            				{
            					if (cp.str1.charAt(0)=='B')
            					{            						
            						bms = cp.str1;
            					}
            					else if (cp.str1.charAt(0)=='C')
            					{            						
            						cmz = cp.str1;
            					}
            						
            				}
            			}*/
            		}
            		
            		//sgd.str6 = amj + "," + bms + "," + cmz;//分组号顺序：满减，满送，满赠
    			}
    				
    			
        	}
    		
    	}
    	
    }

    protected boolean doneDeleteGoods(int index, SaleGoodsDef old_goods)
	{
	    if ((this.curGrant.privqx != 'Y') && (this.curGrant.privqx != 'Q'))
	    {
	      OperUserDef staff = deleteGoodsGrant(index);
	      if (staff == null) { return false;
	      }

	      String log = "授权删除商品,小票号:" + this.saleHead.fphm + ",商品:" + old_goods.barcode + ",单价:" + old_goods.jg + ",授权:" + staff.gh;
	      AccessDayDB.getDefault().writeWorkLog(log, "708");
	    }
		
		// 修改老的盘点单数据 删除时记录删除标志
		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && !"A".equals(old_goods.str8))
		{
			if ("D".equals(old_goods.str8)) return false;
			old_goods.str8 = "D";
			old_goods.name += "[删除]";
			return true;
		}

		SaleGoodsDef cloneGoods = (SaleGoodsDef) old_goods.clone();
		old_goods.sl = 0;
		
		// 重算因为删除本行，对其他行商品产生的影响
		old_goods.hjje = old_goods.jg * old_goods.sl;
		clearGoodsGrantRebate(index);
		calcGoodsYsje(index);
		
		// 删除数量为零的商品
		if (0.0 == old_goods.sl)
		{
			if (!delSaleGoodsObject(index)) return false;
		}

		// 计算小票合计
		calcHeadYsje();

		// 删除上次显示列表,刷新显示列表
		if (0.0 == old_goods.sl) 
		{
			getDeleteGoodsDisplay(index, cloneGoods);
		}

		return true;
	}
    
	public void djlbBackToSale()
    {
		if (!SellType.ISEXERCISE(saletype))
		{
			//除练习模式之外，完成交易之后均转为销售模式
			saletype = "1";
		}
		else
		{
			super.djlbBackToSale();
		}
    }

	//去除 "授权退货,限额为" 的提示
	//获取退货小票信息
	public boolean findBackTicketInfo()
	{
		SaleHeadDef thsaleHead = null;
		Vector thsaleGoods = null;
		Vector thsalePayment = null;

		try
		{
			//如果是新指定小票进入
			if (saletype .equals( SellType.JDXX_BACK) || ((GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'C')
					&& ((saleGoods.size() > 0 && isbackticket) || saleGoods.size() < 1)))
			{
				thsaleHead = new SaleHeadDef();
				thsaleGoods = new Vector();
				thsalePayment = new Vector();

				// 联网查询原小票信息 
				ProgressBox pb = new ProgressBox();
				pb.setText("开始查找退货小票操作.....");
				if (!DataService.getDefault().getBackSaleInfo(thSyjh, String.valueOf(thFphm), thsaleHead, thsaleGoods, thsalePayment))
				{
					pb.close();
					pb = null;
					
					thSyjh = null;
					thFphm = 0;
					
					return false;
				}
				pb.close();
				pb = null;

				// 原交易类型和当前退货类型不对应，不能退货
				// 如果原交易为预售提货，不判断
				// 如果当前交易类型为家电退货,那么可以支持零售销售的退货
				if (!thsaleHead.djlb.equals(SellType.PREPARE_TAKE))
				{
					if (!SellType.getDjlbSaleToBack(thsaleHead.djlb).equals(this.saletype))
					{
						new MessageBox("原小票是[" + SellType.getDefault().typeExchange(thsaleHead.djlb, thsaleHead.hhflag, thsaleHead)
								+ "]交易\n\n与当前退货交易类型不匹配");
	
						// 清空原收银机号和原小票号
						thSyjh = null;
						thFphm = 0;
						return false;
					}
				}

				// 如果该小票退货次数大于0,虽然自身有退货权限,但是,并且又未进行退货授权,则进行授权后才允许退货
				if (thsaleHead.num1 > 0)
				{
					if (saleHead.thsq == null || saleHead.thsq.equals("") || saleHead.thsq.equals(GlobalInfo.posLogin.gh))
					{
						OperUserDef staff = backSellGrant();
						if (staff == null)
						{
							// 清空原收银机号和原小票号
							thSyjh = null;
							thFphm = 0;
							return false;
						}
						else
						{
							// 记录日志
							saleHead.thsq = staff.gh;
							curGrant.privth = staff.privth;
							curGrant.thxe = staff.thxe;

							String log = "授权小票多次退货,小票号:" + saleHead.fphm + ",最大退货限额:" + curGrant.thxe + ",授权:" + staff.gh;
							AccessDayDB.getDefault().writeWorkLog(log);
						}
					}
				}
				
				boolean ishb = false;
				// 显示原小票商品明细 
				Vector choice = new Vector();
				String[] title = { "序", "商品编码", "商品名称", "原数量", "原折扣", "原成交价", "退货", "退货数量" };
				int[] width = { 30, 100, 170, 80, 80, 100, 60, 100, 55 };
				String[] row = null;
				for (int i = 0; i < thsaleGoods.size(); i++)
				{
					SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);
					row = new String[8];
					row[0] = String.valueOf(sgd.rowno);
					row[1] = sgd.code;
					row[2] = sgd.name;
					row[3] = ManipulatePrecision.doubleToString(sgd.sl, 4, 1, true);
					row[4] = ManipulatePrecision.doubleToString(sgd.hjzk);
					row[5] = ManipulatePrecision.doubleToString(sgd.hjje - sgd.hjzk);
					row[6] = "";
					row[7] = "";
					choice.add(row);
				}

				String[] title1 = { "序", "付款名称", "账号", "付款金额" };
				int[] width1 = { 30, 100, 250, 180 };
				String[] row1 = null;
				Vector content2 = new Vector();
				int j = 0;
				for (int i = 0; i < thsalePayment.size(); i++)
				{
					SalePayDef spd1 = (SalePayDef) thsalePayment.get(i);
					row1 = new String[4];
					row1[0] = String.valueOf(++j);
					row1[1] = String.valueOf(spd1.payname);
					row1[2] = String.valueOf(spd1.payno);
					row1[3] = ManipulatePrecision.doubleToString(spd1.je);
					content2.add(row1);
//					是否存在红包付款
					if(spd1.paycode.equals(GlobalInfo.sysPara.hbPaymentCode))
					{
						ishb= true;
					}
				}
				int cho = -1;
				if(!ishb)
				{
					// 选择要退货的商品
						cho = new MutiSelectForm().open("在以下窗口输入单品退货数量(回车键选择商品,付款键全选,确认键保存退出)", title, width, choice, true, 780, 480, 750, 220, true,
													true, 7, true, 750, 130, title1, width1, content2, 0);
				}
				else
				{
					// 选择要退货的商品
						cho = new Zsbh_MutiSelectForm_ISHB().open(Language.apply("按确认键选定退货单)"), title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);
				}
				
				// 如果cho小于0且已经选择过退货小票        		 
				if (cho < 0 && isbackticket) return true;
				if (cho < 0)
				{
					thSyjh = null;
					thFphm = 0;
					return false;
				}

				// 清除已有商品明细,重新初始化交易变量

				// 将退货授权保存下来
				String thsq = saleHead.thsq;
				initSellData();

				// 生成退货商品明细
				for (int i = 0; i < choice.size(); i++)
				{
					row = (String[]) choice.get(i);
					if (!row[6].trim().equals("Y")) continue;

					SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);
					double thsl = ManipulatePrecision.doubleConvert(Convert.toDouble(row[7]),4,1);

					sgd.yfphm = sgd.fphm;
					sgd.ysyjh = sgd.syjh;
					sgd.yrowno = sgd.rowno;
					sgd.memonum1 = sgd.sl;
					sgd.syjh = ConfigClass.CashRegisterCode;
					sgd.fphm = GlobalInfo.syjStatus.fphm;
					sgd.rowno = saleGoods.size() + 1;
					//sgd.num2 = i+1;
					sgd.num2=sgd.yrowno;
					
					// 重算商品行折扣
					if (ManipulatePrecision.doubleCompare(sgd.sl,thsl,4) > 0)
					{
						sgd.hjje  	= ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hjje , sgd.sl),thsl), 2, 1); // 合计金额
						sgd.hyzke 	= ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hyzke, sgd.sl),thsl), 2, 1); // 会员折扣额(来自会员优惠)
						sgd.yhzke 	= ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.yhzke, sgd.sl),thsl), 2, 1); // 优惠折扣额(来自营销优惠)	
						sgd.lszke 	= ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszke, sgd.sl),thsl), 2, 1); // 零时折扣额(来自手工打折)	
						sgd.lszre 	= ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszre, sgd.sl),thsl), 2, 1); // 零时折让额(来自手工打折)	
						sgd.lszzk 	= ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszzk, sgd.sl),thsl), 2, 1); // 零时总品折扣					
						sgd.lszzr 	= ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszzr, sgd.sl),thsl), 2, 1); // 零时总品折让					
						sgd.plzke 	= ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.plzke, sgd.sl),thsl), 2, 1); // 批量折扣	
						sgd.zszke 	= ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.zszke, sgd.sl),thsl), 2, 1); // 赠送折扣
						sgd.cjzke 	= ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.cjzke, sgd.sl),thsl), 2, 1); // 厂家折扣
						sgd.ltzke	= ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.ltzke, sgd.sl),thsl), 2, 1);
						sgd.hyzklje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hyzklje, sgd.sl),thsl), 2, 1);
						sgd.qtzke   = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.qtzke, sgd.sl),thsl), 2, 1);
						sgd.qtzre   = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.qtzre, sgd.sl),thsl), 2, 1);						
						sgd.hjzk  	= getZZK(sgd);
						sgd.sl    	= thsl;
					}

					// 加入商品列表
					addSaleGoodsObject(sgd, null, new SpareInfoDef());
				}

				// 查找原交易会员卡资料
				if (thsaleHead.hykh != null && !thsaleHead.hykh.trim().equals(""))
				{
					curCustomer = new CustomerDef();
					curCustomer.code = thsaleHead.hykh;
					curCustomer.name = thsaleHead.hykh;
					curCustomer.ishy = 'Y';

					/*					业务过程只支持磁道查询,不支持卡号查询,因此无法检查原交易会员卡是否有效
					 if (!DataService.getDefault().getCustomer(curCustomer, thsaleHead.hykh))
					 {
					 curCustomer.code = thsaleHead.hykh;
					 curCustomer.name = "无效卡";
					 curCustomer.ishy = 'Y';
					 
					 new MessageBox("原交易的会员卡可能已失效!\n请重新刷卡后进行退货");
					 }
					 */
				}

				// 设置原小票头信息
				saleHead.hykh = thsaleHead.hykh;
				saleHead.hytype = thsaleHead.hytype;
				saleHead.jfkh = thsaleHead.jfkh;

				saleHead.thsq = thsq;
				saleHead.ghsq = thsaleHead.ghsq;
				saleHead.hysq = thsaleHead.hysq;
				saleHead.sqkh = thsaleHead.sqkh;
				saleHead.sqktype = thsaleHead.sqktype;
				saleHead.sqkzkfd = thsaleHead.sqkzkfd;
				saleHead.hhflag = hhflag;
				saleHead.jdfhdd = thsaleHead.jdfhdd;
				saleHead.salefphm = thsaleHead.salefphm;

				// 退货小票辅助处理
				takeBackTicketInfo(thsaleHead, thsaleGoods, thsalePayment);

				// 重算小票头
				calcHeadYsje();

				//为了写入断点,要在刷新界面之前置为true
				isbackticket = true;

				// 检查是否超出退货限额
				if (curGrant.thxe > 0 && saleHead.ysje > curGrant.thxe)
				{
					OperUserDef staff = backSellGrant();
					if (staff == null)
					{
						initSellData();
						isbackticket = false;
					}
					else
					{
						if (staff.thxe > 0 && saleHead.ysje > staff.thxe)
						{
							new MessageBox("超出退货的最大限额，不能退货");

							initSellData();
							isbackticket = false;
						}
						else
						{
							// 记录日志
							saleHead.thsq = staff.gh;
							curGrant.privth = staff.privth;
							curGrant.thxe = staff.thxe;

							String log = "授权退货,小票号:" + saleHead.fphm + ",最大退货限额:" + curGrant.thxe + ",授权:" + staff.gh;
							AccessDayDB.getDefault().writeWorkLog(log);

							//
							//new MessageBox("授权退货,限额为 " + ManipulatePrecision.doubleToString(curGrant.thxe) + " 元");
						}
					}
				}

				backPayment.removeAllElements();
				backPayment.addAll(thsalePayment);
				
				for (int i = 0; i < thsalePayment.size(); i++)
				{
					SalePayDef spd1 = (SalePayDef) thsalePayment.get(i);
					
					//存在红包付款，自动添加到付款方式。
					if(spd1.paycode.equals(GlobalInfo.sysPara.hbPaymentCode))
					{
//						salePayment.add(spd1);
						PayModeDef payMode = DataService.getDefault().searchPayMode(GlobalInfo.sysPara.hbPaymentCode);

						// 创建一个付款方式对象
						Payment pay = CreatePayment.getDefault().createPaymentByPayMode(payMode, saleEvent.saleBS);
						
//						非本机退货syjh还是销售时的收银机号，记本地库会报错
						spd1.syjh = saleHead.syjh;
						
						addSalePayObject(spd1, pay);
					}

				}


				// 刷新界面显示
				saleEvent.clearTableItem();
				saleEvent.updateSaleGUI();

				return isbackticket;
			}

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (thsaleHead != null)
			{
				thsaleHead = null;
			}

			if (thsaleGoods != null)
			{
				thsaleGoods.clear();
				thsaleGoods = null;
			}

			if (thsalePayment != null)
			{
				thsalePayment.clear();
				thsalePayment = null;
			}
		}
	}


	//去除 "授权退货,限额为" 的提示
	// 获得挂单信息
	public boolean getHang(String invno)
	{
		try
		{
			// 先初始化交易
			if (GlobalInfo.sysPara.onlineGd.equals("Y")) initNewSale();

			// 设置挂单标志 
			isonlinegdjging = true;

			SaleHeadDef salegdhead = new SaleHeadDef();
			salegdhead.djlb = saleHead.djlb;

			Vector salegdgoods = new Vector();

			if (!DataService.getDefault().getSaleGdInfo(invno, salegdhead, salegdgoods))
			{
				new MessageBox("网上没有查找到当前挂单号!");
				return false;
			}

			if (saletype != salegdhead.djlb)
			{
				new MessageBox("此挂单必须在 " + SellType.getDefault().typeExchange(salegdhead.djlb, 'N', saleHead) + " 状态下才能解挂!");
				return false;
			}

			if (SellType.ISBACK(saletype) && salegdgoods.size() > 0 && ((SaleGoodsDef) salegdgoods.get(0)).yfphm > 0
					&& ((SaleGoodsDef) salegdgoods.get(0)).ysyjh.trim().length() > 0)
			{
				thFphm = ((SaleGoodsDef) salegdgoods.get(0)).yfphm;
				thSyjh = ((SaleGoodsDef) salegdgoods.get(0)).ysyjh;

				// 指定小票退货
				for (int i = 0; i < salegdgoods.size(); i++)
				{
					SaleGoodsDef sgd = (SaleGoodsDef) salegdgoods.get(i);
					sgd.fphm = saleHead.fphm;
					sgd.syjh = saleHead.syjh;
					sgd.rowno = saleGoods.size() + 1;

					sgd.hjzk = getZZK(sgd);

					// 重算商品应收
					sgd.hjje = ManipulatePrecision.doubleConvert(sgd.sl * sgd.jg, 2, 1);

					// 加入商品列表
					addSaleGoodsObject(sgd, null, new SpareInfoDef());

					calcGoodsYsje(saleGoods.size() - 1);
				}

				// 查找原交易会员卡资料
				if (salegdhead.hykh != null && !salegdhead.hykh.trim().equals(""))
				{
					curCustomer = new CustomerDef();
					curCustomer.code = salegdhead.hykh;
					curCustomer.name = salegdhead.hykh;
					curCustomer.ishy = 'Y';
				}

				salegdhead.sqkh = saleHead.sqkh;
				salegdhead.bc = saleHead.bc;
				salegdhead.syjh = saleHead.syjh;
				salegdhead.syyh = saleHead.syyh;
				salegdhead.mkt = saleHead.mkt;
				salegdhead.rqsj = saleHead.rqsj;

				saleHead = salegdhead;

				// 重算小票头
				calcHeadYsje();

				//为了写入断点,要在刷新界面之前置为true
				isbackticket = true;

				// 检查是否超出退货限额
				if (curGrant.thxe > 0 && saleHead.ysje > curGrant.thxe)
				{
					OperUserDef staff = backSellGrant();
					if (staff == null)
					{
						initSellData();
						isbackticket = false;
					}
					else
					{
						if (staff.thxe > 0 && saleHead.ysje > staff.thxe)
						{
							new MessageBox("超出退货的最大限额,限额为:" + ManipulatePrecision.doubleToString(staff.thxe) + " 元不能退货");

							initSellData();
							isbackticket = false;
						}
						else
						{
							// 记录日志
							saleHead.thsq = staff.gh;
							curGrant.privth = staff.privth;
							curGrant.thxe = staff.thxe;

							String log = "授权退货,小票号:" + saleHead.fphm + ",最大退货限额:" + curGrant.thxe + ",授权:" + staff.gh;
							AccessDayDB.getDefault().writeWorkLog(log);

							//
							///new MessageBox("授权退货,限额为 " + ManipulatePrecision.doubleToString(curGrant.thxe) + " 元");
						}
					}
				}

				return true;
			}
			else
			{
				// 其它交易
				if (salegdhead.hykh != null && !salegdhead.hykh.trim().equals(""))
				{
					if (!memberGrant())
					{
						salegdhead.hykh = "";
						salegdhead.hysq = "";
						salegdhead.hytype = "";
					}
				}

				salegdhead.bc = saleHead.bc;
				salegdhead.syjh = saleHead.syjh;
				salegdhead.syyh = saleHead.syyh;
				salegdhead.mkt = saleHead.mkt;
				salegdhead.rqsj = saleHead.rqsj;

				saleHead = salegdhead;

				String strmsg = "";
				for (int i = 0; i < salegdgoods.size(); i++)
				{
					saleEvent.code.setText("");

					SaleGoodsDef sgd = (SaleGoodsDef) salegdgoods.get(i);

					if (!findGoods(sgd.inputbarcode, sgd.yyyh, sgd.gz))
					{
						// 未找到该商品
						strmsg += "[" + sgd.inputbarcode + "]" + sgd.name + "\n";

						continue;
					}

					// 查找商品成功
					SaleGoodsDef sgd1 = (SaleGoodsDef) saleGoods.get(saleGoods.size() - 1);

					clearGoodsGrantRebate(saleGoods.size() - 1);

					GoodsDef gd1 = (GoodsDef) goodsAssistant.get(saleGoods.size() - 1);

					if (gd1.isdzc == 'Y')
					{
						String[] codeInfo = new String[4];
						boolean isdzcm = analyzeBarcode(gd1.inputbarcode, codeInfo);

						// 如果电子称商品不是通过电子称码来找的商品，所以要将这些值赋上去
						// 否则是通过电子称码来查找商品，则数量价格可以自己解析
						if (!isdzcm)
						{
							sgd1.jg = sgd.jg;
							sgd1.sl = sgd.sl;
							sgd1.hjje = sgd.hjje;
							sgd1.lszke = sgd.lszke;
							sgd1.flag = sgd.flag;
						}
					}
					else
					{
						sgd1.jg = sgd.jg;
						sgd1.sl = sgd.sl;
						sgd1.hjje = sgd.hjje;
						sgd1.lszke = sgd.lszke;

						sgd1.hjje = ManipulatePrecision.doubleConvert(sgd1.jg * sgd1.sl, 2, 1);
					}

					sgd1.lszke = sgd.lszke;
					sgd1.lszre = sgd.lszre;
					sgd1.lszzk = sgd.lszzk;
					sgd1.lszzr = sgd.lszzr;
					sgd1.cjzke = sgd.cjzke;
					sgd1.ltzke = sgd.ltzke;
					sgd1.sqkh = sgd.sqkh;
					sgd1.sqktype = sgd.sqktype;
					sgd1.sqkzkfd = sgd.sqkzkfd;
					sgd1.batch = sgd.batch;

					calcGoodsYsje(saleGoods.size() - 1);
				}

				calcHeadYsje();

				if (strmsg.trim().length() > 0)
				{
					new MessageBox("未找到解挂单中以下商品:\n" + strmsg);
				}

				return true;
			}
		}
		finally
		{
			isonlinegdjging = false;
		}
	}

	
	public String getVipInfoLabel()
    {
    	if (curCustomer == null)
    		return "";
    	else 
    	{
    		//只显示卡号,不显示卡名
    		return "[" + curCustomer.code + "]";// + curCustomer.name;
    	}
    }
	
	//付款后,不往下移动
	public boolean goToNextPaymode(PayModeDef paymode)
	{
		//if (CreatePayment.getDefault().allowQuickInputMoney(paymode) && GlobalInfo.sysPara.payover == 'Y') { return true; }

		return false;
	}
	
	public String[] rowInfo(SaleGoodsDef goodsDef)
    {
    	String[] rowInfo = super.rowInfo(goodsDef);
    	if (!SellType.ISCHECKINPUT(saletype))
    	{
    		rowInfo[2] = goodsDef.name;
    		rowInfo[6] = ManipulatePrecision.doubleToString(goodsDef.hjzk);//不显示折扣比例 ManipulatePrecision.doubleToString(goodsDef.hjzk) + ((goodsDef.hjzk>0)&&(goodsDef.hjje-goodsDef.hjzk>0)?"(" + ManipulatePrecision.doubleToString((goodsDef.hjje - goodsDef.hjzk)/goodsDef.hjje*100,0,1) + "%)":"");
    	}
    	
    	return rowInfo;
    }

	//  返回到正常销售界面
	public void backToSaleStatus()
	{
		if (!SellType.ISEXERCISE(saletype))
		{
			saletype = SellType.RETAIL_SALE;//除练习模式外,做完业务之后都直接返回到零售销售模式			
		}		
		
	}
	
	public void execCustomKey9(boolean keydownonsale)
	{
		if (keydownonsale)//saleEvent.saleform.getFocus().equals(saleEvent.yyyh) || saleEvent.saleform.getFocus().equals(saleEvent.code) || saleEvent.saleform.getFocus().equals(saleEvent.gz))
		{
			String[] str = ConfigClass.CustomItem1.trim().split("\\|");
			if (str.length >= 2)
			{				
				String yyyh = str[0].toString().trim();
				String code = str[1].toString().trim();
				saleEvent.saleform.setFocus(saleEvent.saleform.yyyh);
				saleEvent.saleform.yyyh.setText(yyyh);
				saleEvent.saleBS.enterInputYYY();
				if (saleEvent.saleform.gz.getText().trim().length() < 1)
				{
					return;
				}
				saleEvent.saleform.setFocus(saleEvent.saleform.code);
				int ret = new MessageBox("请选择购物袋型号:\n按数字0小号\n按数字1中号\n按数字2大号",null,false).verify();
				if (ret == GlobalVar.Key0)
	 			{
					code += "1"; ;
	 			}
				else if (ret == GlobalVar.Key1)
	 			{
					code += "2"; ;
	 			}
				else if (ret == GlobalVar.Key2)
	 			{
					code += "3"; ;
	 			}
				else
				{
					new MessageBox("添加失败,款员取消添加!");
					return;
				}
				saleEvent.saleform.code.setText(code);
				saleEvent.saleBS.enterInputCODE();
			}
			else
			{
				new MessageBox("购物袋配置错误,请联系电脑部处理!");
			}
			
		}
		
	}
	
	public void initSellData()
    {
		super.initSellData();
		saleHead.sqktype = ' ';
    }
	
	public void initNewSale()
    {
		super.initNewSale();
		cursqkh = "";//GlobalInfo.posLogin.gh;
        cursqktype = ' ';//1
        //cursqkzkfd = GlobalInfo.posLogin.privje1;
    }
	
	public void backSell()
	{
		if (GlobalInfo.syjDef.isth != 'Y')
		{
			new MessageBox("该收银机不允许退货!");

			return;
		}

		// 检查发票是否打印完,打印完未设置新发票号则不能交易
		if (Printer.getDefault().getSaleFphmComplate()) { return; }

		// 已经是指定小票退货状态,再次按退货键则重新输入原小票信息
		if (isSpecifyTicketBack())
		{
			RetSYJForm frm = new RetSYJForm();

			int done = frm.open(thSyjh, thFphm);

			if (done == frm.Done)
			{
				thSyjh = RetSYJForm.syj;
				thFphm = Long.parseLong(RetSYJForm.fph);

				if (this.saletype .equals( SellType.PREPARE_BACK))
				{
					isbackticket = findPreSaleInfo();
				}
				else
				{
					isbackticket = findBackTicketInfo();
				}
			}
			else if (done == frm.Clear)
			{
				thSyjh = null;
				thFphm = 0;
			}
			else
			{
				// 放弃,不修改上次输入的原收银机号和小票号
			}

			return;
		}

		// 从销售状态切换到相应的退货状态
		if ((saleGoods.size() <= 0) && SellType.ISSALE(saletype))
		{
			// 检查权限
			thgrantuser = null;
			if (((curGrant.privth != 'Y') && (curGrant.privth != 'T')) || (curGrant.thxe <= 0))
			{
				OperUserDef staff = backSellGrant();

				if (staff == null) { return; }

				// 本次授权
				thgrantuser = staff;

				// 记录日志
				String log = "授权退货,小票号:" + GlobalInfo.syjStatus.fphm + ",最大退货限额:" + thgrantuser.thxe + ",授权:" + thgrantuser.gh;
				AccessDayDB.getDefault().writeWorkLog(log);
			}
			else
			{
				
				thgrantuser = (OperUserDef) (GlobalInfo.posLogin.clone());

				if (cursqktype == '1')
				{
					thgrantuser.gh = cursqkh;
				}
				else
				{
					thgrantuser.gh = GlobalInfo.posLogin.gh;
				}
				thgrantuser.gh="";
				
			}

			// 提示退货权限
			if (curGrant.privth != 'T')
			{
				///new MessageBox("授权退货,限额为 " + ManipulatePrecision.doubleToString(thgrantuser.thxe) + " 元");
			}

			// 切换到退货交易类型
			djlbSaleToBack();

			// 初始化交易
			initOneSale(this.saletype);
		}
		else
		{
			new MessageBox("请先完成当前交易!", null, false);
		}
	}
	
	public boolean saleSummary()
	{
		int i;
		SaleGoodsDef saleGoodsDef = null;
		SalePayDef salePayDef = null;
		int lastgoods = 0;
		double sswr_sysy = 0;
		double fk_sysy = 0;

		if (saleGoods == null || saleGoods.size() <= 0) return false;

		// 汇总商品明细
		for (i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

			// 去掉营业员小计
			if (saleGoodsDef.flag == '0')
			{
				delSaleGoodsObject(i);
				i--;
				continue;
			}

			// 非削价商品且非批发或未定价商品:lsj = jg
			// 削价商品:lsj <> jg, lsj-jg=削价损失 -> thss
			// 批发销售:lsj <> jg, lsj-jg=批发损失 -> thss
			// 议价商品:lsj <> jg, lsj-jg=批发损失 -> thss
			if (saleGoodsDef.lsj <= 0 || (saleGoodsDef.flag != '3' && saleGoodsDef.flag != '6' && !SellType.ISBATCH(saletype)))
			{
				saleGoodsDef.lsj = saleGoodsDef.jg;
			}

			// 整理数据
			saleGoodsDef.rowno = i + 1;
			saleGoodsDef.fphm = saleHead.fphm;
			
			// 零头折扣记入LSZRE
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre + saleGoodsDef.ltzke);
			saleGoodsDef.ltzke = 0;
			if (saleGoodsDef.sqkh == null || saleGoodsDef.sqkh.trim().length() <= 0)
			{
				/*saleGoodsDef.sqkh = cursqkh;
				saleGoodsDef.sqktype = cursqktype;
				saleGoodsDef.sqkzkfd = cursqkzkfd;*/
			}
			saleGoodsDef.hjzk  = getZZK(saleGoodsDef);

			// 分摊损溢金额
			if (saleGoodsDef.flag != '1' && saleGoodsDef.type != '8')
			{
				saleGoodsDef.sswr_sysy = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.hjzk) / saleHead.ysje
						* saleHead.sswr_sysy);
				saleGoodsDef.fk_sysy = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.hjzk) / saleHead.ysje * saleHead.fk_sysy);

				sswr_sysy += saleGoodsDef.sswr_sysy;
				fk_sysy += saleGoodsDef.fk_sysy;
				lastgoods = i;
			}
		}

		// 损溢差额记入最后一个商品
		if (sswr_sysy != saleHead.sswr_sysy)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastgoods);
			saleGoodsDef.sswr_sysy += ManipulatePrecision.sub(saleHead.sswr_sysy, sswr_sysy);
		}
		if (fk_sysy != saleHead.fk_sysy)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastgoods);
			saleGoodsDef.fk_sysy += ManipulatePrecision.sub(saleHead.fk_sysy, fk_sysy);
		}

		// 汇总付款明细
		for (i = 0; i < salePayment.size(); i++)
		{
			salePayDef = (SalePayDef) salePayment.elementAt(i);

			// 整理数据
			salePayDef.rowno = i + 1;
			salePayDef.fphm = saleHead.fphm;

			// 检查卡号中是否存在非法字符
			if (salePayDef.payno != null && salePayDef.payno.trim().length() > 0)
			{
				salePayDef.payno = ManipulateStr.delSpecialChar(salePayDef.payno);
			}
		}

		// 整理数据
		saleHead.rqsj = ManipulateStr.interceptExceedStr(saleHead.rqsj, 20);
		saleHead.hykh = ManipulateStr.interceptExceedStr(saleHead.hykh, 20);
		saleHead.jfkh = ManipulateStr.interceptExceedStr(saleHead.jfkh, 20);
		saleHead.thsq = ManipulateStr.interceptExceedStr(saleHead.thsq, 20);
		saleHead.ghsq = ManipulateStr.interceptExceedStr(saleHead.ghsq, 20);
		saleHead.hysq = ManipulateStr.interceptExceedStr(saleHead.hysq, 20);
		saleHead.sqkh = ManipulateStr.interceptExceedStr(saleHead.sqkh, 20);
		saleHead.buyerinfo = ManipulateStr.interceptExceedStr(saleHead.buyerinfo, 20);
		saleHead.salefphm = ManipulateStr.interceptExceedStr(saleHead.salefphm, 20);

		saleHead.jdfhdd = ManipulateStr.interceptExceedStr(saleHead.jdfhdd, 20);
		
		// 计算本次积分
		saleHead.bcjf = calcSaleBCJF();
		saleHead.ljjf = calcSaleLJJF();

		// 不重算小票主单,避免发生和之前计算结果出现误差

		// 记录商品付款分摊
		if (!paymentApportionSummary()) return false;

		return true;
	}

	
	public boolean deleteGoods(int index)
	{
		try
		{
			return super.deleteGoods(index); 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (saleGoods.size() <=0 )
			{
				//如果界面上没有商品了,就清除界面信息
				initOneSale(this.saletype);
			}			
		}
	}
	
	public void getVIPZK(int index, int type)
	{
		//不计算VIP折扣
	}
	
	public boolean saleFinishDone(Label status, StringBuffer waitKeyCloseForm)
	{
		if (curCustomer != null && curCustomer.code != null && curCustomer.code.trim().length() > 1)
		{
			//如果有会员卡号,则将授权卡类型置为顾客卡号类型
			saleHead.sqktype = '2';
		}
		
		return super.saleFinishDone(status, waitKeyCloseForm);
	}
	
//	输入折扣
	public boolean inputRebate(int index)
	{
		double grantzkl = 0;
		boolean grantflag = false;
		
		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack()) return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);
		// 小计、削价不处理
		if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3')) { return false; }

		// 服务费、以旧换新不处理
		if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8')) { return false; }

		// 不能打折
		if (!checkGoodsRebate(goodsDef, info))
		{
			new MessageBox("该商品不允许打折!");

			return false;
		}

		// 备份数据
		SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		// 授权
		if ((curGrant.dpzkl * 100) >= 100 || !checkGoodsGrantRange(goodsDef, curGrant.grantgz))
		{
			OperUserDef staff = inputRebateGrant(index);
			if (staff == null) return false;

			// 本次授权折扣
			grantzkl = staff.dpzkl;
			grantflag = breachRebateGrant(staff);

			// 记录授权工号
			saleGoodsDef.sqkh = staff.gh;
			saleGoodsDef.sqktype = '1';
			saleGoodsDef.sqkzkfd = staff.privje1;

			// 记录日志
			String log = "授权单品折扣,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",折扣权限:" + grantzkl * 100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}
		else
		{
			saleHead.sqktype = ' ';
			/*// 本次授权折扣
			grantzkl = curGrant.dpzkl;
			grantflag = breachRebateGrant(curGrant);

			// 记录授权工号
			saleGoodsDef.sqkh = cursqkh;
			saleGoodsDef.sqktype = cursqktype;
			saleGoodsDef.sqkzkfd = cursqkzkfd;*/
		}

		// 计算权限允许的最大折扣率
		double maxzkl = 0;
		if (grantflag)
		{
			//new MessageBox("允许突破最低折扣");
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
		if (saleGoodsDef.lszke < 0) saleGoodsDef.lszke = 0;

		// 根据模拟计算得到当前最大打折比例
		double lszkl = saleGoodsDef.lszke / (saleGoodsDef.hjje - getZZK(saleGoodsDef) + saleGoodsDef.lszke);
		lszkl = ManipulatePrecision.doubleConvert((1 - lszkl) * 100, 2, 1);
		
		// 输入折扣
		String maxzklmsg = "收银员正在对该商品进行打折";
		
		if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
		{
			maxzklmsg =  "收银员对该商品的单品折扣权限为 "+ ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true) + "%\n你目前最多在成交价基础上再打折 "+ ManipulatePrecision.doubleToString(lszkl, 2, 1, true) + "%";
		}
		
		StringBuffer buffer = new StringBuffer();
		if (!new TextBox().open("请输入单品折扣百分比(%)" + (grantflag == true ? "(允许突破商品最低折扣限制)" : ""), "单品折扣", maxzklmsg, buffer, lszkl, 100, true))
		{
			// 恢复数据
			saleGoods.setElementAt(oldGoodsDef, index);

			return false;
		}
		
		// 得到折扣率
		grantzkl = Double.parseDouble(buffer.toString());

		// 计算最终折扣
		saleGoodsDef.lszke = 0;
		saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((100 - grantzkl) / 100 * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
		if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1))
		{
			saleGoodsDef.lszke -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1);
			saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke, 2, 1);
		}
		if (saleGoodsDef.lszke < 0) saleGoodsDef.lszke = 0;
		saleGoodsDef.lszke = getConvertRebate(index, saleGoodsDef.lszke);

		// 重算商品折扣合计
		getZZK(saleGoodsDef);

		// 重算小票应收
		calcHeadYsje();

		return true;
	}

	//输入折让金额
	public boolean inputRebatePrice(int index)
	{
		double grantzkl = 0;
		boolean grantflag = false;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack()) return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);
		
		// 小计、削价不处理
		if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3')) { return false; }

		// 服务费、以旧换新不处理
		if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8')) { return false; }

		// 不能打折
		if (!checkGoodsRebate(goodsDef, info))
		{
			new MessageBox("该商品不允许打折!");

			return false;
		}

		// 备份数据
		SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		// 授权
		if ((curGrant.dpzkl * 100) >= 100 || !checkGoodsGrantRange(goodsDef, curGrant.grantgz))
		{
			OperUserDef staff = inputRebateGrant(index);
			if (staff == null) return false;

			// 本次授权折扣
			grantzkl = staff.dpzkl;
			grantflag = breachRebateGrant(staff);

			// 记录授权工号
			saleGoodsDef.sqkh = staff.gh;
			saleGoodsDef.sqktype = '1';
			saleGoodsDef.sqkzkfd = staff.privje1;

			// 记录日志
			String log = "授权单品折让,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",折扣权限:" + grantzkl * 100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}
		else
		{
			saleHead.sqktype = ' ';
			/*// 本次授权折扣
			grantzkl = curGrant.dpzkl;
			grantflag = breachRebateGrant(curGrant);

			// 记录授权工号
			saleGoodsDef.sqkh = cursqkh;
			saleGoodsDef.sqktype = cursqktype;
			saleGoodsDef.sqkzkfd = cursqkzkfd;*/
		}

		// 计算权限允许的最大折扣额
		double maxzkl = 0;
		if (grantflag)
		{
			//new MessageBox("允许突破最低折扣");
			// 允许突破最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, 0);
		}
		else
		{
			// 不允许最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
		}
		double maxzre = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);
		if (goodsDef.maxzke < saleGoodsDef.hjje && saleGoodsDef.hjje - goodsDef.maxzke < maxzre) maxzre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - goodsDef.maxzke, 2, 1);
		
		// 输入折让
		String maxzremsg = "收银员对该商品进行折让";
		
		StringBuffer buffer = new StringBuffer();
		if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
		{
			// 计算最大折让到金额
			double lszre = saleGoodsDef.hjje - maxzre;
			lszre = ManipulatePrecision.doubleConvert(lszre, 2, 1);
			
			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzremsg = "收银员对该商品的单品折扣权限为 "+ ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true) + "%\n你目前对该商品最多只能够折让到 "+ ManipulatePrecision.doubleToString(lszre, 2, 1, true) + " 元";
			}
			
			if (!new TextBox().open("请输入单品折让后的成交价" + (grantflag == true ? "(允许突破最低折扣)" : ""), "单品折让", maxzremsg, buffer, lszre, saleGoodsDef.hjje, true))
			{
				// 恢复数据
				saleGoods.setElementAt(oldGoodsDef, index);

				return false;
			}

			//得到折让额	      
			lszre = Double.parseDouble(buffer.toString());

			// 清除所有手工折扣,按输入的成交价计算最终折让
			saleGoodsDef.lszke = 0;
			saleGoodsDef.lszre = 0;
			saleGoodsDef.lszzk = 0;
			saleGoodsDef.lszzr = 0;
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - lszre, 2, 1);
		}
		else
		{
			// 计算最大可折让金额
			double lszre = maxzre - getZZK(saleGoodsDef) + saleGoodsDef.lszre;
			lszre = ManipulatePrecision.doubleConvert(lszre, 2, 1);
			if (lszre < 0) lszre = 0;
			
			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzremsg = "收银员对该商品的单品折扣权限为 "+ ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true) + "%\n你目前对该商品最多还可以再折让 "+ ManipulatePrecision.doubleToString(lszre, 2, 1, true) + " 元";
			}
			
			if (!new TextBox().open("请输入单品要折让的金额" + (grantflag == true ? "(允许突破最低折扣)" : ""), "单品折让", maxzremsg, buffer, 0, lszre, true))
			{
				// 恢复数据
				saleGoods.setElementAt(oldGoodsDef, index);

				return false;
			}

			// 得到折让额
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(Double.parseDouble(buffer.toString()), 2, 1);
		}
		if (getZZK(saleGoodsDef) > maxzre)
		{
			saleGoodsDef.lszre -= getZZK(saleGoodsDef) - maxzre;
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre, 2, 1);
		}
		if (saleGoodsDef.lszre < 0) saleGoodsDef.lszre = 0;
		saleGoodsDef.lszre = getConvertRebate(index, saleGoodsDef.lszre);

		// 重算商品折扣合计
		getZZK(saleGoodsDef);

		// 重算小票应收
		calcHeadYsje();

		return true;
	}

	
//	输入总折扣
	public boolean inputAllRebate()
	{
		if (saleGoods.size() <= 0) { return false; }

		double grantzkl = 0;
		String grantgz = null;
		boolean grantflag = false;
		SaleGoodsDef saleGoodsDef = null;
		GoodsDef goodsDef = null;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack()) return false;

		// 授权
		if ((curGrant.zpzkl * 100) >= 100)
		{
			OperUserDef staff = inputAllRebateGrant();
			if (staff == null) return false;

			// 本次授权折扣
			grantzkl = staff.zpzkl;
			grantgz = staff.grantgz;
			grantflag = breachRebateGrant(staff);

			// 记录授权工号
			saleHead.sqkh = staff.gh;
			saleHead.sqktype = '1';
			saleHead.sqkzkfd = staff.privje1;

			// 记录日志
			String log = "授权整单折扣,小票号:" + saleHead.fphm + ",折扣权限:" + grantzkl * 100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}
		else
		{
			saleHead.sqktype = ' ';
			/*
			// 本次授权折扣
			grantzkl = curGrant.zpzkl;
			grantgz = curGrant.grantgz;
			grantflag = breachRebateGrant(curGrant);

			// 记录授权工号
			saleHead.sqkh = cursqkh;
			saleHead.sqktype = cursqktype;
			saleHead.sqkzkfd = cursqkzkfd;*/
		}

		// 计算商品能否打折
		boolean rebate = false;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
			
			// 小记、削价不处理
			if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
			{
				continue;
			}

			// 服务费、以旧换新不处理
			if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
			{
				continue;
			}

			// 不能打折
			if (!checkGoodsRebate(goodsDef))
			{
				continue;
			}

			// 不在授权范围
			if (!checkGoodsGrantRange(goodsDef, grantgz))
			{
				continue;
			}

			rebate = true;
			break;
		}

		if (!rebate)
		{
			new MessageBox("整单没有可打折的商品，不能手工折扣");
			return false;
		}
		
		String maxzzklmsg = "该收银员正在进行整单打折";
		
		// 总折扣计算模式为批量单品折扣模式
		if (GlobalInfo.sysPara.batchtotalrebate == 'Y')
		{
			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzzklmsg = "收银员对权限范围内商品的总折扣权限为 " + ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%\n你目前最多在权限内交易额基础上再打折 " + ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%";
			}
			
			// 输入折扣
			StringBuffer buffer = new StringBuffer();
			buffer.append(grantzkl * 100);
			if (!new TextBox().open("请输入整单折扣百分比(%)", "整单折扣", maxzzklmsg, buffer,grantzkl * 100, 100, true)) { return false; }

			// 得到折扣率
			double zkl = Double.parseDouble(buffer.toString());

			// 循环为每个单品打折
			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
				
				// 小记，削价 不处理
				if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
				{
					continue;
				}

				// 服务费,以旧换新 不处理
				if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
				{
					continue;
				}

				// 不能打折
				if (!checkGoodsRebate(goodsDef))
				{
					continue;
				}

				// 不在授权范围
				if (!checkGoodsGrantRange(goodsDef, grantgz))
				{
					continue;
				}

				// 计算权限允许的最大折扣额
				double maxzzkl = 0;
				if (grantflag)
				{
					// 允许突破最低折扣
					maxzzkl = getMaxRebateGrant(grantzkl, 0);
				}
				else
				{
					// 不允许最低折扣
					maxzzkl = getMaxRebateGrant(grantzkl, goodsDef);
				}

				// 计算最终折扣
				saleGoodsDef.lszzk = 0;
				saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert((100 - zkl) / 100 * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
				if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzzkl)), 2, 1))
				{
					// 提示
					new MessageBox("[" + saleGoodsDef.code + "]" + saleGoodsDef.name + "\n\n最多能打折 "
							+ ManipulatePrecision.doubleToString(maxzzkl * 100) + "%");

					//
					saleGoodsDef.lszzk -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzzkl)), 2, 1);
					saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzk, 2, 1);
				}
				if (saleGoodsDef.lszzk < 0) saleGoodsDef.lszzk = 0;
				saleGoodsDef.lszzk = getConvertRebate(i, saleGoodsDef.lszzk);

				// 重算商品折扣合计
				getZZK(saleGoodsDef);
			}
		}
		else 
		{
			// 计算整单最打可打折金额        
			double sumzzk = 0, sumlszzk = 0, lastzzk = 0,hjcjj = 0,hjzke = 0;
			int lastzzkrow = -1;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
				
				// 小记、削价不处理
				if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
				{
					continue;
				}

				// 服务费、以旧换新不处理
				if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
				{
					continue;
				}
				
				// 不在授权范围,只要是权限范围内的商品不管商品本身能不能打折，都参与总折计算，然后分摊时不分摊
				if (!checkGoodsGrantRange(goodsDef, grantgz))
				{
					continue;
				}

				// 累计可折扣金额
				sumzzk += ManipulatePrecision.doubleConvert((1 - grantzkl) * saleGoodsDef.hjje, 2, 1);
				sumlszzk += saleGoodsDef.lszzk;
				hjcjj += saleGoodsDef.hjje - saleGoodsDef.hjzk;
				hjzke += saleGoodsDef.hjzk;
				
				// 不能打折
				if (!checkGoodsRebate(goodsDef))
				{
					continue;
				}
				
				// 计算商品权限允许的最大折扣额,找可折让金额最大的商品
				double maxzkl = 0;
				if (grantflag)
				{
					// 允许突破最低折扣
					maxzkl = getMaxRebateGrant(grantzkl, 0);
				}
				else
				{
					// 不允许最低折扣
					maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
				}
				double maxzzk = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);
				if (maxzzk > lastzzk)
				{
					lastzzk = maxzzk;
					lastzzkrow = i;
				}
			}

			// 反算得到当前最大打折比例
			double lszkl = (sumzzk - hjzke + sumlszzk) / (hjcjj + sumlszzk);
			if (lszkl < 0) lszkl = 0;
			lszkl = ManipulatePrecision.doubleConvert((1 - lszkl) * 100, 2, 1);

			// 输入折扣
			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzzklmsg = "收银员对权限范围内商品的总折扣权限为 "+ ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%\n你目前最多在权限内交易额基础上再打折 "+ ManipulatePrecision.doubleToString(lszkl, 2, 1, true) + "%";
			}
			
			StringBuffer buffer = new StringBuffer();
			buffer.append(lszkl);
			if (!new TextBox().open("请输入整单折扣百分比(%)" + (grantflag == true ? "(允许突破最低折扣)" : ""), "整单折扣",maxzzklmsg, buffer, lszkl, 100, true)) { return false; }

			// 得到折扣金额,打折后按收银机定义四舍五入
			double zkl = Double.parseDouble(buffer.toString());
			double zzkje = ManipulatePrecision.doubleConvert((100 - zkl) / 100 * (hjcjj + sumlszzk), 2, 1);
			double tempysje = (saleHead.hjzje - saleHead.hjzke + sumlszzk) - zzkje;
			double tempyfje = getDetailOverFlow(tempysje);
			zzkje = ManipulatePrecision.sub(zzkje, ManipulatePrecision.sub(tempyfje, tempysje));

			// 把总折扣额分摊到每个商品
			double hjzzk = 0;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
				
				// 小记、削价不处理
				if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
				{
					continue;
				}

				// 服务费、以旧换新不处理
				if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
				{
					continue;
				}

				// 不在授权范围
				if (!checkGoodsGrantRange(goodsDef, grantgz))
				{
					continue;
				}

				// 不能打折
				if (!checkGoodsRebate(goodsDef))
				{
					continue;
				}
				
				// 计算商品权限允许的最大折扣额
				double maxzkl = 0;
				if (grantflag)
				{
					// 允许突破最低折扣
					maxzkl = getMaxRebateGrant(grantzkl, 0);
				}
				else
				{
					// 不允许最低折扣
					maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
				}
				double maxzzk = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);

				// 取消其他手工折扣,计算最终折扣
				saleGoodsDef.sqkh = "";
				saleGoodsDef.sqktype = '\0';

				// 每个商品分摊的折让按金额占比计算
				if (i != lastzzkrow)
				{
					if (GlobalInfo.sysPara.batchtotalrebate == 'N')
					{
						saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje-saleGoodsDef.hjzk+saleGoodsDef.lszzk) / (hjcjj+sumlszzk) * zzkje, 2, 1);
					}
					else
					{
						saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(maxzzk / sumzzk * zzkje, 2, 1);
					}
					if (getZZK(saleGoodsDef) > maxzzk)
					{
						saleGoodsDef.lszzk -= getZZK(saleGoodsDef) - maxzzk;
						saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzk, 2, 1);
					}
					if (saleGoodsDef.lszzk < 0) saleGoodsDef.lszzk = 0;
					saleGoodsDef.lszzk = getConvertRebate(i, saleGoodsDef.lszzk);
					saleGoodsDef.lszzk = getConvertRebate(i, saleGoodsDef.lszzk,getGoodsApportionPrecision());
					
					// 重算商品折扣合计
					getZZK(saleGoodsDef);

					// 计算已分摊的总折让
					hjzzk += saleGoodsDef.lszzk;
				}
			}

			// 可折让金额最大商品的折扣用减法直接等于剩余的分摊,最后计算
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastzzkrow);
			saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(zzkje - hjzzk, 2, 1);
			if (getZZK(saleGoodsDef) > lastzzk)
			{
				saleGoodsDef.lszzk -= getZZK(saleGoodsDef) - lastzzk;
				saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzk, 2, 1);
			}
			if (saleGoodsDef.lszzk < 0) saleGoodsDef.lszzk = 0;			
			getZZK(saleGoodsDef);
		}

		// 重算小票应收
		calcHeadYsje();

		return true;
	}

	//输入总折让
	public boolean inputAllRebatePrice()
	{
		if (saleGoods.size() <= 0) { return false; }

		double grantzkl = 0;
		String grantgz = null;
		boolean grantflag = false;
		SaleGoodsDef saleGoodsDef = null;
		GoodsDef goodsDef = null;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack()) return false;

		// 授权
		if ((curGrant.zpzkl * 100) >= 100)
		{
			OperUserDef staff = inputAllRebateGrant();
			if (staff == null) return false;

			// 本次授权折扣
			grantzkl = staff.zpzkl;
			grantgz = staff.grantgz;
			grantflag = breachRebateGrant(staff);

			// 记录授权工号
			saleHead.sqkh = staff.gh;
			saleHead.sqktype = '1';
			saleHead.sqkzkfd = staff.privje1;

			// 记录日志
			String log = "授权整单折让,小票号:" + saleHead.fphm + ",折扣权限:" + grantzkl * 100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}
		else
		{
			saleHead.sqktype = ' ';
			/*
			// 本次授权折扣
			grantzkl = curGrant.zpzkl;
			grantgz = curGrant.grantgz;
			grantflag = breachRebateGrant(curGrant);

			// 记录授权工号
			saleHead.sqkh = cursqkh;
			saleHead.sqktype = cursqktype;
			saleHead.sqkzkfd = cursqkzkfd;
			*/
		}

		// 计算整单最打可打折金额        
		double sumzzr = 0, summxzzr = 0,sumlszzr = 0, lastzre = 0,hjzke = 0;
		int lastzrerow = -1;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
			SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);
			
			// 小记、削价不处理
			if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
			{
				continue;
			}

			// 服务费、以旧换新不处理
			if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
			{
				continue;
			}

			// 不在授权范围
			if (!checkGoodsGrantRange(goodsDef, grantgz))
			{
				continue;
			}

			// 累计可折让金额
			summxzzr += ManipulatePrecision.doubleConvert((1 - grantzkl) * saleGoodsDef.hjje, 2, 1);
			sumlszzr += saleGoodsDef.lszzr;
			hjzke += saleGoodsDef.hjzk;
			
			// 不能打折
			if (!checkGoodsRebate(goodsDef, info))
			{
				continue;
			}
			
			// 计算每个商品权限允许的最大折扣额,找可折让金额最大的商品
			double maxzkl = 0;
			if (grantflag)
			{
				// 允许突破最低折扣
				maxzkl = getMaxRebateGrant(grantzkl, 0);
			}
			else
			{
				// 不允许最低折扣
				maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
			}
			double maxzzr = 0;
			if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
			{
				maxzzr = ManipulatePrecision.doubleConvert((1 - maxzkl) * (saleGoodsDef.hjje - (saleGoodsDef.hjzk - saleGoodsDef.lszke - saleGoodsDef.lszre - saleGoodsDef.lszzk - saleGoodsDef.lszzr)) , 2, 1);
			}
			else
			{
				maxzzr = ManipulatePrecision.doubleConvert((1 - maxzkl) * (saleGoodsDef.hjje - (saleGoodsDef.hjzk - saleGoodsDef.lszzr)) , 2, 1);
			}
			sumzzr += maxzzr;
			if (maxzzr > lastzre)
			{
				lastzre = maxzzr;
				lastzrerow = i;
			}
		}

		if (summxzzr <= 0)
		{
			new MessageBox("整单没有可打折的商品，不能手工折扣");
			return false;
		}

		// 输入折让
		double zzrje = 0;
		
		String maxzzrmsg = "该收银员正在进行整单折让";
		
		StringBuffer buffer = new StringBuffer();
		if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
		{
			// 计算最大折让到金额
			double lszzr = saleHead.hjzje - summxzzr;
			lszzr = ManipulatePrecision.doubleConvert(lszzr, 2, 1);
			
			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzzrmsg = "收银员对权限内商品的总折扣权限为 "+ ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%\n你目前对整单成交价最多只能折让到 "+ ManipulatePrecision.doubleToString(lszzr, 2, 1, true) + " 元";
			}
			
			if (!new TextBox().open("请输入整单折让后的成交价" + (grantflag == true ? "(允许突破最低折扣)" : ""), "整单折让", maxzzrmsg, buffer, lszzr, saleHead.hjzje, true)) { return false; }

			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

				saleGoodsDef.lszke = 0;
				saleGoodsDef.lszre = 0;
				saleGoodsDef.lszzk = 0;
				saleGoodsDef.lszzr = 0;
				getZZK(saleGoodsDef);
			}
			calcHeadYsje();

			zzrje = saleHead.hjzje - saleHead.hjzke - Double.parseDouble(buffer.toString());
			zzrje = ManipulatePrecision.doubleConvert(zzrje, 2, 1);
		}
		else
		{
			// 计算最大可折让金额
			double lszzr = summxzzr - hjzke + sumlszzr;
			lszzr = ManipulatePrecision.doubleConvert(lszzr, 2, 1);
			if (lszzr < 0) lszzr = 0;
			
			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzzrmsg = "收银员对权限范围内商品的总折扣权限为 "+ ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%\n你目前对权限范围内商品最多还可以折让 "+ ManipulatePrecision.doubleToString(lszzr, 2, 1, true) + " 元";
			}
			
			if (!new TextBox().open("请输入整单要折让的金额" + (grantflag == true ? "(允许突破最低折扣)" : ""), "整单折让", maxzzrmsg, buffer, 0, lszzr, true)) { return false; }
			zzrje = Double.parseDouble(buffer.toString());
			zzrje = ManipulatePrecision.doubleConvert(zzrje, 2, 1);
		}

		// 把总折让额分摊到每个商品
		double hjzzr = 0;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
			SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);

			// 小记、削价不处理
			if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
			{
				continue;
			}

			// 服务费、以旧换新不处理
			if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
			{
				continue;
			}

			// 不在授权范围
			if (!checkGoodsGrantRange(goodsDef, grantgz))
			{
				continue;
			}

			// 不能打折
			if (!checkGoodsRebate(goodsDef, info))
			{
				continue;
			}

			// 取消其他手工折扣,计算最终折扣
			saleGoodsDef.sqkh = "";
			saleGoodsDef.sqktype = '\0';

			// 每个商品分摊的折让按金额占比计算
			if (i != lastzrerow)
			{
				// 计算商品权限允许的最大折扣额
				double maxzkl = 0;
				if (grantflag)
				{
					// 允许突破最低折扣
					maxzkl = getMaxRebateGrant(grantzkl, 0);
				}
				else
				{
					// 不允许最低折扣
					maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
				}
				saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert((1 - maxzkl) * (saleGoodsDef.hjje - (saleGoodsDef.hjzk - saleGoodsDef.lszzr)) / sumzzr * zzrje, 2, 1);
				double maxzzr = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);
				if (getZZK(saleGoodsDef) > maxzzr)
				{
					saleGoodsDef.lszzr -= getZZK(saleGoodsDef) - maxzzr;
					saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzr, 2, 1);
				}
				if (saleGoodsDef.lszzr < 0) saleGoodsDef.lszzr = 0;
				saleGoodsDef.lszzr = getConvertRebate(i, saleGoodsDef.lszzr);
				saleGoodsDef.lszzr = getConvertRebate(i, saleGoodsDef.lszzr,getGoodsApportionPrecision());
				
				// 重算商品折扣合计
				getZZK(saleGoodsDef);

				// 计算已分摊的总折让
				hjzzr += saleGoodsDef.lszzr;
			}
		}

		// 可折让金额最大商品的折扣用减法直接等于剩余的分摊,最后计算
		if (lastzrerow >=0)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastzrerow);
			saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(zzrje - hjzzr, 2, 1);
			if (getZZK(saleGoodsDef) > lastzre)
			{
				saleGoodsDef.lszzr -= getZZK(saleGoodsDef) - lastzre;
				saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzr, 2, 1);
			}
			if (saleGoodsDef.lszzr < 0) saleGoodsDef.lszzr = 0;
			getZZK(saleGoodsDef);
		}
		
		// 重算小票应收
		calcHeadYsje();

		return true;
	}

	protected boolean cancelMemberOrGoodsRebate(int index)
	{
		boolean blnRet = false;
		blnRet = super.cancelMemberOrGoodsRebate(index);
		if (saleHead.jfkh != null && saleHead.jfkh.length() > 0 )
		{
			saleHead.jfkh = "";
		}
		return blnRet;
	}
	
	public void calcHandVIPDiscount(int Goodsindex)
	{
		if (curCustomer == null || curCustomer.code.trim().equals("") || curCustomer.code.trim().length()<1)
		{
			new MessageBox("请先刷会员卡!");
			return;
		}
		
		double maxzkl = 100;
		double custzkl = 100;
		GoodsDef gd1 = null;
		custzkl=curCustomer.zkl * 100;
		StringBuffer buffer = new StringBuffer();
		buffer.append(custzkl);//GlobalInfo.sysPara.handVIPDiscount * 100);
		if (!new TextBox().open("请输入VIP折扣率", "折扣率", "", buffer, custzkl, 100, true)) { return; }

		double inputDiscount = Double.parseDouble(buffer.toString().trim());
		maxzkl = custzkl >= inputDiscount ? custzkl : inputDiscount;
		
		if (inputDiscount < maxzkl)
		{
			new MessageBox("你输入的折扣率小于最低VIP折扣率，请重新输入!");
			return;
		}
		
		for (int i = 0; i < goodsAssistant.size(); i++)
		{
			gd1 = (GoodsDef)goodsAssistant.get(Goodsindex);//saleGoods.elementAt(i);
			

			// 求商品记录中的最底折扣率
			double  goodsmaxzkl = gd1.maxzkl * 100;
			if (goodsmaxzkl == 100)
			{
				//new MessageBox("此商品不允许VIP折扣");
				continue;
			}

		/*	StringBuffer buffer = new StringBuffer();
			if (!new TextBox().open("请输入该商品VIP折扣率", "折扣率", "", buffer, GlobalInfo.sysPara.handVIPDiscount, 100, true)) { return; }

			double inputDiscount = Double.parseDouble(buffer.toString().trim());
*/
			maxzkl = goodsmaxzkl >= maxzkl ? goodsmaxzkl : maxzkl;
			if (inputDiscount < maxzkl)
			{
				new MessageBox("你输入的折扣率小于最低VIP折扣率，该商品无法享受VIP折扣:\n商品条码:" + gd1.barcode + "\n商品名称:" + gd1.name);
				continue;
			}
			else
			{
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);//Goodsindex);
				//double sjje = saleGoodsDef.hjje - saleGoodsDef.hjzk + saleGoodsDef.hyzke;

				saleGoodsDef.lszke = 0;
				saleGoodsDef.lszre = 0;
				saleGoodsDef.lszzk = 0;
				saleGoodsDef.lszzr = 0;
				saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.jg - (saleGoodsDef.jg * inputDiscount / 100), 2, 1); ;//sjje - (sjje * inputDiscount / 100);
				//saleGoodsDef.sqkh = curCustomer.code;

				// 不允许参加后台VIP折扣
				gd1.isvipzk = 'N';

				// 重算商品折扣合计
				getZZK(saleGoodsDef);
				
				//后台过程根据会员卡号判断是否为VIP折扣
				saleHead.jfkh = curCustomer.code;//saleHead.jfkh;

				
			}
		}

		
//		 重算小票应收
		calcHeadYsje();
		
		/*	GoodsDef gd1 = (GoodsDef) goodsAssistant.get(Goodsindex);

		// 求商品记录中的最底折扣率
		double maxzkl = gd1.maxzkl * 100;
		if (maxzkl == 100)
		{
			new MessageBox("此商品不允许VIP折扣");
			return;
		}

		StringBuffer buffer = new StringBuffer();
		if (!new TextBox().open("请输入该商品VIP折扣率", "折扣率", "", buffer, GlobalInfo.sysPara.handVIPDiscount, 100, true)) { return; }

		double inputDiscount = Double.parseDouble(buffer.toString().trim());

		maxzkl = GlobalInfo.sysPara.handVIPDiscount >= maxzkl ? GlobalInfo.sysPara.handVIPDiscount : maxzkl;
		if (inputDiscount < maxzkl)
		{
			new MessageBox("你输入的折扣率小于最低VIP折扣率，请重新输入!");
		}
		else
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Goodsindex);
			double sjje = saleGoodsDef.hjje - saleGoodsDef.hjzk;

			saleGoodsDef.hyzke = sjje - (sjje * inputDiscount / 100);

			// 不允许参加后台VIP折扣
			gd1.isvipzk = 'N';

			// 重算商品折扣合计
			getZZK(saleGoodsDef);

			// 重算小票应收
			calcHeadYsje();
		}*/
	}
	

	/*//test
	//会员授权
	public boolean memberGrant()
	{
		boolean ret =false;
		try
		{
			if (isPreTakeStatus())
			{
				new MessageBox("预售提货状态下不允许重新刷卡");
				return false;
			}

			// 会员卡必须在商品输入前,则输入了商品以后不能刷卡,指定小票除外
			if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0 && !isNewUseSpecifyTicketBack(false))
			{
				new MessageBox("必须在输入商品前进行刷会员卡\n\n请把商品清除后再重刷卡");
				return false;
			}
					
			// 读取会员卡
			HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
			String track2 = bs.readMemberCard();
			new MessageBox("track2=" + track2);//test window
			
			if (track2 == null || track2.equals("")) return false;

			// 查找会员卡
			CustomerDef cust = bs.findMemberCard(track2);
			if (cust == null) return false;

			// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
			if (isNewUseSpecifyTicketBack(false))
			{
				// 指定小票退仅记录卡号,不执行商品重算等处理
				curCustomer = cust;
				saleHead.hykh = cust.code;
				saleHead.hytype = cust.type;
				ret=true;
				return true;
			}
			else
			{
				// 记录会员卡
				ret= memberGrantFinish(cust);
				return ret;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (ret)
			{
				saleHead.hysq = curCustomer.code;
				
				if (curCustomer.iszk == 'Y')
				{				
					saleHead.sqkh = curCustomer.code;
				
			}
			}
		}
		
	}*/
	

	// 遍历查找CRM促销
	protected void calcMenberCrmPop()
	{
		if (saleGoods.size() > 0)
		{
			ProgressBox pb = null;
			try
			{
				pb = new ProgressBox();
				pb.setText("正在查找CRM促销信息,请等待...");
				
				for (int i = 0; i < saleGoods.size(); i++)
				{					
					// 查找CRM促销
					findGoodsCRMPop((SaleGoodsDef)saleGoods.elementAt(i), 
										(GoodsDef)goodsAssistant.elementAt(i), 
											(SpareInfoDef)goodsSpare.elementAt(i));
				}

		        // 计算小票应收
		        calcHeadYsje();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if (pb != null)
				{
					pb.close();
					pb = null;
				}
			}
			
		}
	}
	
	public boolean memberGrant()
	{

		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (saleGoods.size() > 0  && isNewUseSpecifyTicketBack(false))
		{
			//new MessageBox("必须在输入商品前进行刷会员卡\n请把商品清除后再重刷卡");
			return false;
		}
		
		boolean blnRet = false;
		if (isPreTakeStatus())
		{
			new MessageBox("预售提货状态下不允许重新刷卡");
			return false;
		}

		// 会员卡必须在商品输入前,则输入了商品以后不能刷卡,指定小票除外
		if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0 && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox("必须在输入商品前进行刷会员卡\n\n请把商品清除后再重刷卡");
			return false;
		}
				
		// 读取会员卡
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		String track2 = bs.readMemberCard();
		if (track2 == null || track2.equals("")) return false;

		// 查找会员卡
		CustomerDef cust = bs.findMemberCard(track2);
		
		if (cust == null) return false;

		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false))
		{
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;
			saleHead.str4 = cust.valstr2;

			//return true;
			blnRet = true;
		}
		else
		{
			// 记录会员卡
			blnRet = memberGrantFinish(cust);
		}
		
		if (blnRet)
		{
			saleHead.hysq = curCustomer.code;
			
			if (curCustomer.iszk == 'Y')
			{				
				saleHead.sqkh = curCustomer.code;
			}
			
			//遍历查找已录入商品的CRM促销
			calcMenberCrmPop();
			
			return true;
		}
		else
		{
			return false;
		}
	}
   
	public void execCustomKey0(boolean keydownonsale) {
		if (keydownonsale)
		{
			// 调出原交易的指定小票退货模式允许重新输入手机号改变当前会员卡(原卡可能失效、换卡等情况)
			if (saleGoods.size() > 0 && isNewUseSpecifyTicketBack(false)) {
				return;
			}

			boolean blnRet = false;
			if (isPreTakeStatus()) {
				new MessageBox("预售提货状态下不允许重新输入手机号");
				return;
			}

			// 会员卡必须在商品输入前,则输入了商品以后不能输手机号,指定小票除外
			if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0
					&& !isNewUseSpecifyTicketBack(false)) {
				new MessageBox("必须在输入商品前进行输手机号\n\n请把商品清除后再重输手机号");
				return;
			}
            try{
			// 读取手机号
			HykInfoQueryBS bs = CustomLocalize.getDefault()
					.createHykInfoQueryBS();
			StringBuffer phoneno = new StringBuffer();
			boolean done = new TextBox().open(Language.apply("请输入手机号"),
					Language.apply("手机号码"), Language.apply("请输入会员卡绑定的手机号"),
					phoneno, 0, 0, false, TextBox.IntegerInput);
			if (done) {
				String track2 = "@" + phoneno.toString();// bs.readMemberCard();

				if (track2 == null || track2.equals(""))
					return;
				// 查找会员卡
				CustomerDef cust = bs.findMemberCard(track2);
				if (cust == null)
					return;

				// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
				if (isNewUseSpecifyTicketBack(false)) {
					// 指定小票退仅记录卡号,不执行商品重算等处理
					curCustomer = cust;
					saleHead.hykh = cust.code;
					saleHead.hytype = cust.type;
					saleHead.str4 = cust.valstr2;
					blnRet = true;
				} else {
					// 记录会员卡
					blnRet = memberGrantFinish(cust);
				}
			}
			if (blnRet) {
				saleHead.hysq = curCustomer.code;

				if (curCustomer.iszk == 'Y') {
					saleHead.sqkh = curCustomer.code;
				}
				// 遍历查找已录入商品的CRM促销
				calcMenberCrmPop();
				// 显示VIP顾客卡信息
				saleEvent.setVIPInfo(getVipInfoLabel());

				// 刷新商品列表
				saleEvent.updateTable(getSaleGoodsDisplay());
				saleEvent.table.setSelection(saleEvent.table.getItemCount() - 1);

				// 显示汇总
				saleEvent.setTotalInfo();

				// 显示商品大字信息
				saleEvent.setCurGoodsBigInfo();
			}
            }
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
            
	}
	
	public boolean payAccount(PayModeDef mode, String money)
	{
		if (SellType.ISEXERCISE(saletype) && !mode.code.equals("01"))
		{
			new MessageBox("练习模式下只能使用人民币付款!");
			return false;
		}
				
		return super.payAccount(mode,money);
	}
	
	
	public boolean saleCollectAccountPay()
	{
		Payment p = null;
		boolean czsend = true;

		// 付款对象记账
		for (int i = 0; i < payAssistant.size(); i++)
		{
			p = (Payment) payAssistant.elementAt(i);
			//电子红包付款交易这里不需要记账
			if(p != null && p.paymode.code.equals(GlobalInfo.sysPara.hbPaymentCode) && SellType.ISSALE(saleHead.djlb)) continue;
			if (p == null)
				continue;

			// 第一次记账前先检查是否有冲正需要发送
			if (czsend)
			{
				czsend = false;
				if (!p.sendAccountCz())
					return false;
			}

			// 付款记账
			if (!p.collectAccountPay())
				return false;
		}

		// 移动充值对象记账
		if (GlobalInfo.useMobileCharge && !mobileChargeCollectAccount(true))
			return false;

		return true;
	}
	
	public boolean checkIsSalePay(String code)
	{
		//退货不允许修改货选择红包支付
		if(!SellType.ISSALE(saleHead.djlb) && code.equals(GlobalInfo.sysPara.hbPaymentCode))return true;
		return false;
	}
	
	public boolean checkDeleteSalePay(String string, boolean isDelete)
	{
//		//红包付款方式不允许被删除
//		if(string.indexOf(GlobalInfo.sysPara.hbPaymentCode) != -1) 
//			return true;
//		else
			return false;
	}
	
//	 自动删除不受系统参数控制
	public boolean deleteSalePay(int index, boolean isautodel)
	{
		// 是否允许删除当前付款方式
		if (!isautodel && !isDeletePay(index))
			return false;

		// 扣回处理
		if (isRefundStatus())
			return deleteRefundPay(index);

		try
		{
			if (index >= 0)
			{
				boolean flag = false;
				// 付款取消交易才能删除已付款
				Payment p = (Payment) payAssistant.elementAt(index);
				if(p.paymode.code.equals(GlobalInfo.sysPara.hbPaymentCode))
				{
					flag = ((Zsbh_PaymentHb)p).sendhbcancel(salePayment);

					if(flag)
					{
					  for(int i = 0;i<salePayment.size();i++)
					  {
						  SalePayDef spd = (SalePayDef)salePayment.elementAt(i);
							if(spd.paycode.equals(GlobalInfo.sysPara.hbPaymentCode))
							{
								delSalePayObject(i);
								//刷新已付款列表
								salePayEvent.showSalePaymentDisplay();
								i =i-1;
							}
					  }
					  
					  	//重算剩余付款
						calcPayBalance();

						// 刷新已付款，更新断点文件
						getSalePaymentDisplay();

						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					flag = p.cancelPay();
					if(!flag) return false;
					// 删除已付款
					delSalePayObject(index);
					
					// 重算剩余付款
					calcPayBalance();

					// 刷新已付款，更新断点文件
					getSalePaymentDisplay();

					return true;
				}	
					
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}
	
	public boolean deleteAllSalePay()
	{
//		 删除所有付款方式
		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef sp = (SalePayDef) salePayment.elementAt(i);
			if(sp.paycode.equals(GlobalInfo.sysPara.hbPaymentCode) && !SellType.ISSALE(saleHead.djlb))continue;
			if (!deleteSalePay(i))
			{
				return false;
			}
			else
			{
				i--;
			}
		}

		// 删除所有扣回的付款,用信用卡支付扣回时,取消所有付款也得取消扣回
		if (!deleteAllSaleRefund())
			return false;

		return true;
	}

}
