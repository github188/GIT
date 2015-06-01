package custom.localize.Bcrm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.CmPopGiftsDef;
import com.efuture.javaPos.Struct.CmPopGoodsDef;
import com.efuture.javaPos.Struct.CmPopRuleLadderDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;
import com.efuture.javaPos.UI.Design.ApportPaymentForm;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.SalePayForm;


/**
 * @author yinl
 * @create 2010-12-21 上午10:26:52
 * @descri 文件说明
 */

public class Bcrm_SaleBS1CmPop extends Bcrm_SaleBS0CRMPop
{
    public boolean isCmPopMode()
    {
    	if (GlobalInfo.sysPara.rulepop == 'C') return true;
    	else return false;
    }
    
    public void initNewSale()
    {
    	if (isCmPopMode()) CmPop_initNewSale();
    	else super.initNewSale();
    }
    
    public void writeSellObjectToStream(ObjectOutputStream s) throws Exception
    {
    	if (isCmPopMode()) CmPop_writeSellObjectToStream(s);
    	else super.writeSellObjectToStream(s);
    }

    public void readStreamToSellObject(ObjectInputStream s) throws Exception
    {
    	if (isCmPopMode()) CmPop_readStreamToSellObject(s);
    	else super.readStreamToSellObject(s);
    }
    
    public void addSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
    {
    	if (isCmPopMode()) CmPop_addSaleGoodsObject(sg, goods, info);
    	else  super.addSaleGoodsObject(sg, goods, info);
    }
    
    public boolean delSaleGoodsObject(int index)
    {
    	if (isCmPopMode()) return CmPop_delSaleGoodsObject(index);
    	else return super.delSaleGoodsObject(index);
    }
    
    public void calcAllRebate(int index)
    {
    	super.calcAllRebate(index);
    	
    	// 促销模型处理
    	if (isCmPopMode())
    	{
    		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
    		
    		// 计算促销模型的分期促销
    		CmPop_calcGoodsPOPRebate(index);
    		
            // 按价格精度计算折扣
            saleGoodsDef.yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.yhzke, 2, 1);
            saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hyzke, 2, 1);
            if (saleGoodsDef.yhzke > 0) saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
            if (saleGoodsDef.hyzke > 0) saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
    	}
    }
    
    public void calcBatchRebate(int index)
    {
    	super.calcBatchRebate(index);
    	
    	// 促销模型处理
    	if (isCmPopMode()) CmPop_calcBatchRebate(index);    	
    }

    public void calcGoodsPOPRebate(int index)
    {
    	super.calcGoodsPOPRebate(index);
    	
    	// 促销模型处理
    	if (isCmPopMode()) CmPop_calcGoodsPOPRebate(index);
    }
    
    public void customerIsHy(CustomerDef cust)
    {
    	if (isCmPopMode()) CmPop_customerIsHy(cust);
    	else super.customerIsHy(cust);
    }
    
    public boolean paySellStart()
    {
    	if (isCmPopMode()) return CmPop_paySellStart();
    	else return super.paySellStart();
    }
    
    public void paySellCancel()
    {
    	if (isCmPopMode()) CmPop_paySellCancel();
    	else super.paySellCancel();
    }
    
    public boolean getPayModeByNeed(PayModeDef paymode)
    {
    	if (isCmPopMode()) return CmPop_getPayModeByNeed(paymode);
    	else return super.getPayModeByNeed(paymode);
    }
    
    public boolean saleSummary()
    {
    	if (isCmPopMode()) return CmPop_saleSummary();
    	else return super.saleSummary();
    }
    
    public void clearGoodsAllRebate(int index)
    {
    	if (isCmPopMode()) CmPop_clearGoodsAllRebate(index);
    	else super.clearGoodsAllRebate(index);
    }

    // 上面为嫁接部分,执行完原促销,再执行促销模型
    public class PopRuleGoods
	{
		public int sgindex;
		public int cmindex;
	};
	
	public	class PopRuleGoodsGroup
	{
		public int goodsgroup;
		public int goodsgrouprow;
		public char condmode;
		public double condsl;
		public double condje;
		public double popje;
		public Vector goodslist;
	};
	
    public Vector goodsCmPop = null;
    protected boolean haveCmPop = false;
    protected boolean doCmPopExit = false;
    protected boolean needApportionPayment = false;
    protected String payPopPrepareExcp = "";		//每个促销规则预受限付款列表				
    protected String payPopOtherExcp = "";			//最后付款受限付款列表		
    
    public void CmPop_initNewSale()
    {
        if (goodsCmPop != null) goodsCmPop.removeAllElements();
        else goodsCmPop = new Vector();
        
        payPopPrepareExcp = "";
        payPopOtherExcp = "";
        
        super.initNewSale();
    }
    
    public void CmPop_writeSellObjectToStream(ObjectOutputStream s) throws Exception
    {
        brokenAssistant.insertElementAt(goodsCmPop, 0);
        
        super.writeSellObjectToStream(s);
    }

    public void CmPop_readStreamToSellObject(ObjectInputStream s) throws Exception
    {
        super.readStreamToSellObject(s);

        goodsCmPop = (Vector)brokenAssistant.remove(0);
    }
    
    public void CmPop_addSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
    {
        super.addSaleGoodsObject(sg, goods, info);
        
        // goods不为空才是销售的商品,查找商品对应促销情况
        if (goods != null && info != null) findGoodsCMPOPInfo(sg, goods, info);
        else goodsCmPop.add(null);
    }
    
    public boolean CmPop_delSaleGoodsObject(int index)
    {
        // 删除商品
        if (!super.delSaleGoodsObject(index)) return false;

        // 删除相应CMPOP
        if (goodsCmPop.size() > index) goodsCmPop.removeElementAt(index);

        return true;
    }
    
    public void CmPop_calcBatchRebate(int index)
    {
    	// 自动计算本商品的累计模式促销
    	if (!SellType.ISSALE(this.saletype)) return;

    	// 累计模式的促销对其他行商品的影响
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef)saleGoods.elementAt(index);
        Vector popvec = (Vector)goodsCmPop.elementAt(index);
    	for (int i=0;popvec != null && i<popvec.size();i++)
    	{
    		CmPopGoodsDef cmp = (CmPopGoodsDef)popvec.elementAt(i);
    		
			// 特别指定允许立即计算折扣的累计促销才自动计算(例如批量等),否则都在按付款键以后统一计算
			if (cmp.ruleinfo.summode == '0') continue;
			if (cmp.ruleinfo.memo == null || !(cmp.ruleinfo.memo.length() > 0 && cmp.ruleinfo.memo.charAt(0) == 'Y')) continue;
			
			// 先清除所有有相同促销的商品的促销折扣
			boolean havedel = false;
	        for (int j=0;j<saleGoods.size();j++)
	        {
	        	SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(j);
	        	if (j >= goodsCmPop.size()) continue;
	        	
	        	Vector popvec1 = (Vector)goodsCmPop.elementAt(j);
        		for (int n=0;popvec1 != null && n<popvec.size();n++)
        		{
        			CmPopGoodsDef cmpoth = (CmPopGoodsDef)popvec1.elementAt(n);
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
	        				// 恢复促销使用标志
	        				cmpoth.used = false;
	    					
	        				// 清除本促销产生的折扣
	        				if (sg.zszke > 0) havedel = true;
	    		        	sg.zsdjbh = null;
	    		        	sg.zszke  = 0;
	    		        	sg.zszkfd = 0;
	    					getZZK(sg);
	    					
	    					// 清除本促销产生的赠品
	        			}
        			}
        		}
	    	}
		    	
	        // 再计算累计以后产生的折扣
	        boolean havepop = doCmPop(index);
	        
	        // 如果doCmPop未刷新才执行取消折扣刷新,否则不执行
	    	if (!havepop && havedel) refreshCmPopUI();
		}
    }

    public void CmPop_calcGoodsPOPRebate(int index)
    {
    	if (!SellType.ISSALE(this.saletype)) return;
    	
        // 计算不进行累计的促销规则
        Vector popvec = (Vector)goodsCmPop.elementAt(index);
    	for (int i=0;popvec != null && i<popvec.size();i++)
    	{
    		CmPopGoodsDef cmp = (CmPopGoodsDef)popvec.elementAt(i);
    		
    		// 不累计的促销允许立即计算折扣的促销,则找到商品后立即计算促销折扣     		
    		if (cmp.ruleinfo.summode == '0')
    		{
    			// 恢复促销使用标志
    			cmp.used = false;

    			// 计算分期促销
    			calcGoodsCMPOPRebate(index,cmp,i);
    			
    			// 促销单是否允许VIP继续折上折
    			this.popvipzsz = cmp.ruleinfo.popzsz;
    		}
    	}
    }
    	
    public void CmPop_clearGoodsAllRebate(int index)
    {
    	SpareInfoDef sginfo = (SpareInfoDef)goodsSpare.elementAt(index);
    	if (sginfo.popzk != null) sginfo.popzk.removeAllElements();
    	
    	super.clearGoodsAllRebate(index);
    }    
        
	public void CmPop_customerIsHy(CustomerDef cust)
	{
		// 需要重算商品折扣,重新刷卡以后把商品的促销重新查询一次
		if (cust.ishy == 'Y' || cust.ishy == 'V' || cust.ishy == 'H')
		{
			goodsCmPop.removeAllElements();
			for (int i=0;i<saleGoods.size();i++)
			{
				SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(i);
				GoodsDef goods = null;
				if (goodsAssistant.size() > i) goods = (GoodsDef)goodsAssistant.elementAt(i);
				SpareInfoDef info = null;
				if (goodsSpare.size() > i) info = (SpareInfoDef)goodsSpare.elementAt(i);
				
		        // goods不为空才是销售的商品,查找商品对应促销情况
		        if (goods != null && info != null) findGoodsCMPOPInfo(sg, goods, info);
		        else goodsCmPop.add(null);
			}
		}
		
		// 基类处理重算促销折扣
		super.customerIsHy(cust);
	}
	
    public SaleGoodsDef SplitSaleGoodsRow(int index,double splitsl)
    {
    	SaleGoodsDef newsg = super.SplitSaleGoodsRow(index, splitsl);
    	if (newsg != null && goodsCmPop != null && goodsCmPop.size() > index)
    	{
    		// 新拆分出的商品行促销结果集如果查找有误,clone原促销信息
    		Vector pop = (Vector)goodsCmPop.elementAt(index);
    		Vector newpop = (Vector)goodsCmPop.elementAt(goodsCmPop.size()-1);
    		if (pop != null && (newpop == null || pop.size() != newpop.size()))
    		{
    			if (newpop == null) newpop = new Vector();
    			else newpop.removeAllElements();
    			for (int i=0;i<pop.size();i++) 
    			{
    				CmPopGoodsDef cp = (CmPopGoodsDef)((CmPopGoodsDef)pop.elementAt(i)).clone();
    				cp.used = false;
    				newpop.add(cp);
    			}
    			goodsCmPop.setElementAt(newpop,goodsCmPop.size()-1);
    		}
    		
    		// 分拆商品行的折扣分摊
    		SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(index);
    		SpareInfoDef spinfo = (SpareInfoDef)goodsSpare.elementAt(index);
    		SpareInfoDef newspinfo = (SpareInfoDef)spinfo.clone();
    		goodsSpare.setElementAt(newspinfo, goodsSpare.size()-1);    		
    		for (int j=0;spinfo.popzk != null && j<spinfo.popzk.size();j++)
    		{
    			double zk = 0,newzk = 0;
    			String[] s = (String[])spinfo.popzk.elementAt(j);
    			if (s.length > 1) newzk = Convert.toDouble(s[1]);
    			
    			zk = ManipulatePrecision.doubleConvert(newzk / (sg.sl + newsg.sl) * sg.sl,2,1);
    			newzk = ManipulatePrecision.doubleConvert(newzk - zk,2,1);
    			
    			// 原商品行的折扣明细
    			s[1] = ManipulatePrecision.doubleToString(zk);
    			spinfo.popzk.setElementAt(s, j);
    			
    			// 新商品行的折扣明细
    			String[] news = (String[])newspinfo.popzk.elementAt(j);
    			news[1] = ManipulatePrecision.doubleToString(newzk);
    			newspinfo.popzk.setElementAt(news, j);
    		}
    	}
    	
    	return newsg;
    }
        
    public void rebateDetail(int index)
    {
    	super.rebateDetail(index);
    	
    	// 调试模式显示商品参与的促销规则
    	if (ConfigClass.DebugMode && goodsCmPop != null && goodsCmPop.size() > index)
    	{
    		Vector popvec = (Vector)goodsCmPop.elementAt(index);
    		if (popvec == null || popvec.size() <= 0) return;
    		
    		Vector choice = new Vector();
			String[] title = { Language.apply("促销序号"), Language.apply("档期代码"), Language.apply("档期描述"), Language.apply("规则代码"), Language.apply("规则描述")};
			int[] width = { 120, 100, 200, 120, 200 };
			for (int i = 0; i < popvec.size(); i++)
			{
				CmPopGoodsDef cmp = (CmPopGoodsDef)popvec.elementAt(i);
				choice.add(new String[]{String.valueOf(cmp.cmpopseqno),cmp.dqid,cmp.dqinfo.name,cmp.ruleid,cmp.ruleinfo.rulename});
			}
			new MutiSelectForm().open(Language.apply("查看商品参与的促销详情"), title, width, choice, false, 780, 300, false);
    	}
    }
    
	public void payShowRebateDetail(int key)
	{
		// 信用卡追送功能
		if ((key == GlobalVar.WholeRate || key == GlobalVar.WholeRebate))
		{
			getCreditCardZK();
		}
		else
		{
			Vector choice = new Vector();
			String[] title = { Language.apply("序"), Language.apply("商品编码"), Language.apply("商品名称"), Language.apply("数量"), Language.apply("单价"), Language.apply("成交金额") };
			int[] width = { 30, 140, 200, 75, 120, 155 };
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);

				String[] row = new String[6];
				row[0] = String.valueOf(i + 1);
				row[1] = sgd.code;
				row[2] = sgd.name;
				row[3] = ManipulatePrecision.doubleToString(sgd.sl, 4, 1, true);
				row[4] = ManipulatePrecision.doubleToString(sgd.jg, 2, 1, false, 10);
				String s = ManipulatePrecision.doubleToString(sgd.hjje - sgd.hjzk) + "("
						+ ManipulatePrecision.doubleToString((sgd.hjje - sgd.hjzk) / sgd.hjje * 100, 0, 1, false, 2) + "%)";
				row[5] = Convert.increaseCharForward(s, 14);
				choice.add(row);

				boolean needblank = false;
				if (sgd.hyzke > 0)
				{
					row = new String[6];
					row[0] = "";
					row[1] = "";
					row[2] = sgd.hydjbh != null?sgd.hydjbh:"";
					row[3] = "";
					row[4] = Language.apply("会员折off:");
					s = ManipulatePrecision.doubleToString(sgd.hyzke) + "(" + 
						ManipulatePrecision.doubleToString(sgd.hyzke / sgd.hjje * 100, 0, 1, false, 2) + "%)";
					row[5] = Convert.increaseCharForward(s, 14);
					choice.add(row);
					needblank = true;
				}

				if (sgd.yhzke + sgd.zszke > 0)
				{
					// 分项折扣明细
					double zkhj = 0;
		    		SpareInfoDef spinfo = (SpareInfoDef)goodsSpare.elementAt(i);
		    		for (int j=0;spinfo.popzk != null && j<spinfo.popzk.size();j++)
		    		{
		    			double zk = 0;
		    			String[] ss = (String[])spinfo.popzk.elementAt(j);
		    			if (ss.length > 1) zk = Convert.toDouble(ss[1]);
		    			
						row = new String[6];
						row[0] = "";
						row[1] = "";
						row[2] = ss[0];
						row[3] = "";
						row[4] = Language.apply("促销折off:");
						s = ManipulatePrecision.doubleToString(zk) + "(" + 
							ManipulatePrecision.doubleToString(zk / sgd.hjje * 100, 0, 1, false, 2) + "%)";
						row[5] = Convert.increaseCharForward(s, 14);
						choice.add(row);
						needblank = true;
						
						// 分项折扣合计
						zkhj += zk;
		    		}

		    		// 其他促销折扣
		    		if (ManipulatePrecision.doubleCompare(sgd.yhzke + sgd.zszke,zkhj,2) > 0)
		    		{
						row = new String[6];
						row[0] = "";
						row[1] = "";
						row[2] = "";
						row[3] = "";
						row[4] = Language.apply("促销折off:");
						s = ManipulatePrecision.doubleToString(sgd.yhzke + sgd.zszke - zkhj) + "("
								+ ManipulatePrecision.doubleToString((sgd.yhzke + sgd.zszke - zkhj) / sgd.hjje * 100, 0, 1, false, 2) + "%)";
						row[5] = Convert.increaseCharForward(s, 14);
						choice.add(row);
						needblank = true;
		    		}
				}

				if (sgd.hjzk - (sgd.hyzke + sgd.yhzke + sgd.zszke) > 0)
				{
					row = new String[6];
					row[0] = "";
					row[1] = "";
					row[2] = "";
					row[3] = "";
					row[4] = Language.apply("其他折off:");
					s = ManipulatePrecision.doubleToString(sgd.hjzk - sgd.hyzke - sgd.yhzke - sgd.zszke) + "(" + 
						ManipulatePrecision.doubleToString((sgd.hjzk - sgd.hyzke - sgd.yhzke - sgd.zszke) / sgd.hjje * 100, 0, 1, false, 2) + "%)";
					row[5] = Convert.increaseCharForward(s, 14);
					choice.add(row);
					needblank = true;
				}

				if (needblank)
				{
					row = new String[6];
					row[0] = "";
					row[1] = "";
					row[2] = "";
					row[3] = "";
					row[4] = "";
					row[5] = "";
					choice.add(row);
				}
			}

			new MutiSelectForm().open(Language.apply("查看商品折扣详情"), title, width, choice, false, 780, 480, false);
		}
	}
	
    public boolean CmPop_paySellStart()
    {
        if (!super.paySellStart()) return false; 	// 不允许进行付款
        
        if (GlobalInfo.sysPara.isSuperMarketPop == 'Y') doSuperMarketCrmPop();
        
        // 处理CRM促销
        doCmPopExit = false;

        haveCmPop = doCmPop(-1);

        if (doCmPopExit) return false; 				// 不再继续进行付款

        return true;
    }
    
    public void CmPop_paySellCancel()
    {
    	// 放弃CM促销
    	if (haveCmPop) delCmPop();
    	
    	super.paySellCancel();
    }
    	
    public boolean CmPop_getPayModeByNeed(PayModeDef paymode)
    {
    	if (!super.getPayModeByNeed(paymode)) return false;
    	
        // 无满减的实际付款，所有付款方式都可以
        if (isPreparePay == payNormal)
        {
            return true;
        }

        // 满减预先付款只先付券类付款方式
        if (isPreparePay == payPopPrepare)
        {
			String[] pay = null;
			if (payPopPrepareExcp.indexOf(",") >= 0) pay = payPopPrepareExcp.split(",");
			else pay = payPopPrepareExcp.split("\\|");

            for (int i = 0; i < pay.length; i++)
            {
                if (paymode.code.equals(pay[i].trim()) || DataService.getDefault().isChildPayMode(paymode.code, pay[i].trim()))
                {
                    return true;
                }
            }
            
            return false;
        }

        // 满减后再付款只允许付非券类付款方式
        // 券类的付款方式必须在满减前输入完成
        if (isPreparePay == payPopOther)
        {
			String[] pay = null;
			if (payPopOtherExcp.indexOf(",") >= 0) pay = payPopOtherExcp.split(",");
			else pay = payPopOtherExcp.split("\\|");

            for (int i = 0; i < pay.length; i++)
            {
                if (paymode.code.equals(pay[i].trim()))
                {
                    return false;
                }
            }

            return true;
        }

        return true;
    }
    
    public Vector paymentApportionBySale(SalePayDef spay, Payment payobj)
    {
    	// 无需手工分配
    	if (isPreparePay != payPopPrepare) return null;
    	if (!needApportionPayment) return null;
    		    	
    	// 商品编码,商品名称,已付金额,限制金额,分摊金额,对应商品行号
        Vector v = payobj.getGoodsListByPayRule();
        if (v == null) return null;
        
        // 如果付款金额比限制金额的合计大，说明有溢余，溢余部分不进行分摊
        double allftje = spay.je - spay.num1;
        double maxftje = 0;
        for (int i = 0; i < v.size(); i++)
        {
        	String[] row = (String[]) v.elementAt(i);
        	maxftje += Convert.toDouble(row[3]);
        }
        if (allftje > maxftje) allftje = maxftje;
        
        // 显示分摊窗口，输入分摊金额
        new ApportPaymentForm().open(v, spay.payname + Language.apply("共付款") + ManipulatePrecision.doubleToString(spay.je) + Language.apply("元"), allftje);

        return v;
    }
    
    public boolean CmPop_saleSummary()
    {
    	if (!super.saleSummary()) return false;
    	
    	// 记录促销明细到商品str3
    	for (int i=0;i<saleGoods.size();i++)
    	{
    		SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(i);
    		if (goodsSpare == null || goodsSpare.size() <= i) continue;
    		SpareInfoDef spinfo = (SpareInfoDef)goodsSpare.elementAt(i);
    		if (spinfo == null) continue;
    		
    		// sg.str3 = 促销序号:促销金额
    		// popzk   = 促销序号,促销金额,促销备注
    		sg.str3 = "";
    		for (int j=0;spinfo.popzk != null && j<spinfo.popzk.size();j++)
    		{
    			String[] s = (String[])spinfo.popzk.elementAt(j);
    			sg.str3 += "," + s[0] + ":" + s[1];
    			
    			if (s.length >= 3 && s[2] != null && !s[2].equals(""))
    			{
    				sg.str3 += ":" + s[2];
    			}
    		}
    		
    		if (sg.str3.length() > 0) sg.str3 = sg.str3.substring(1);
    	}
    	
	    return true;
    }
        
    public double getGoodsPaymentApportion(int index,SaleGoodsDef sg)
    {
    	SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);
    	if (info == null || info.payft == null) return 0;

    	// 该商品分摊到的不参与计算的付款金额
    	double payje = 0;
        for (int j = 0; j < info.payft.size(); j++)
        {
            String[] s = (String[])info.payft.elementAt(j);
            if (s.length > 3)
            {
            	payje += Double.parseDouble(s[3]);
            }
        }
        
    	return payje;
    }
    
    // CMPOP促销模型
    public void findGoodsCMPOPInfo(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
    {
        if (!SellType.ISSALE(this.saletype)) return;

        // 会员状态
        String cardno = null;
        String cardtype = null;
        if (curCustomer != null)
        {
            cardno   = curCustomer.code;
            cardtype = curCustomer.type;
        }

        // 查找商品的促销结果集
        Vector popvec = ((Bcrm_DataService)DataService.getDefault()).findCMPOPGoods(saleHead.rqsj,goods,cardno,cardtype);
        goodsCmPop.add(popvec);
        
        // 按活动档期分组
        if (popvec != null)
        {
        	// 只保留同类别最后一个档期,倒序搜索
        	for (int i = popvec.size()-1;i>=0;i--)
        	{
        		CmPopGoodsDef cmp = (CmPopGoodsDef)popvec.elementAt(i);
        		
        		// 忽略的档期规则
        		if (cmp.popmode == 'C' || cmp.ruleinfo.popmode == 'C')
        		{
        			popvec.remove(i);
        			i = popvec.size();
        			continue;
        		}
        		
        		if (i-1 >=0 && !((CmPopGoodsDef)popvec.elementAt(i-1)).dqid.equals(cmp.dqid))
        		{
        			// 找到集合中前面同类别的档期
        			int j = i-1;
        			for (;j >= 0;j--)
        			{
        				CmPopGoodsDef cmp1 = (CmPopGoodsDef)popvec.elementAt(j);
        				if (cmp.dqinfo.dqtype != null && !cmp.dqinfo.dqtype.trim().equals("") && 
        					cmp.dqinfo.dqtype.equals(cmp1.dqinfo.dqtype))
        				{
        					popvec.remove(j);
        					break;
        				}
        			}
        			
        			// 重新找需要选择的规则
        			if (j >= 0)
        			{
	        			i = popvec.size();
	        			continue;
	        		}
        		}
        	}
        	
        	// 选择规则或去掉需要放弃的规则
        	for (int i = 0;i < popvec.size();i++)
        	{
        		CmPopGoodsDef cmp = (CmPopGoodsDef)popvec.elementAt(i);
        		
        		// 同档期活动存在多种规则
        		if (i+1 < popvec.size() && ((CmPopGoodsDef)popvec.elementAt(i+1)).dqid.equals(cmp.dqid))
        		{
        			// 手工选择一个规则
        			if (cmp.dqinfo.ruleselmode == '0')
        			{
	        			Vector contents = new Vector();
	        			for (int j = i;j < popvec.size();j++)
	        			{
	        				CmPopGoodsDef cmp1 = (CmPopGoodsDef)popvec.elementAt(j);
	        				if (!cmp1.dqid.equals(cmp.dqid)) break;
	        				contents.add(new String[]{cmp1.ruleid,cmp1.ruleinfo.rulename});
	        			}
	        			if (contents.size() <= 1) continue;
	                    String[] title = { Language.apply("规则代码"), Language.apply("规则描述") };
	                    int[] width = { 100, 400 };
	                    int choice = -1;
	                    do {
//	                    	choice = new MutiSelectForm().open("请选择该商品参与["+ cmp.dqinfo.name + "]活动的促销形式", title, width, contents);
	                    	choice = new MutiSelectForm().open(Language.apply("请选择该商品参与[{0}]活动的促销形式" ,new Object[]{cmp.dqinfo.name}), title, width, contents);
	                    }while(choice == -1);
	                    
	                    // 删除未选择的规则 
	                    String choicerule = ((String[])contents.elementAt(choice))[0];
	        			for (int j=i;j<popvec.size();j++)
	        			{
	        				CmPopGoodsDef cmp1 = (CmPopGoodsDef)popvec.elementAt(j);
	        				if (!cmp1.dqid.equals(cmp.dqid)) break;
	        				if (!cmp1.ruleid.equals(choicerule))
	        				{
	        					popvec.remove(j);
	        					j--;
	        				}
	        			}
	        			
	        			// 重新找需要选择的规则
	        			i = -1;
	        			continue;
        			}
        			else if (cmp.dqinfo.ruleselmode == '1')		// 只参加最后一个规则
        			{
        				// 删除同档期前面的规则,保留最后一个规则
	        			for (int j = i;j < popvec.size();j++)
	        			{
	        				CmPopGoodsDef cmp1 = (CmPopGoodsDef)popvec.elementAt(j);
	        				if (!cmp1.dqid.equals(cmp.dqid)) break;
	        				if (j+1 < popvec.size() && ((CmPopGoodsDef)popvec.elementAt(j+1)).dqid.equals(cmp1.dqid))
	        				{
	        					popvec.remove(j);
	        					j--;
	        				}
	        			}
	        			
	        			// 重新找需要选择的规则
	        			i = -1;
	        			continue;
        			}
        			else if (cmp.dqinfo.ruleselmode == '3')		// 3-按分组规则选择
        			{
        			}
        			else if (cmp.dqinfo.ruleselmode == '4')		// 4-手工选择多个规则
        			{
        			}
        		}
        	}
        	
        	// 从集合中去掉设置为取消促销的规则
        	for (int i = 0;i < popvec.size();i++)
        	{
        		CmPopGoodsDef cmp = (CmPopGoodsDef)popvec.elementAt(i);
        		if (cmp.popmode == 'N' || cmp.ruleinfo.popmode == 'N')
        		{
        			popvec.remove(i);
        			i--;
        		}
        	}
        	
        	// 按规则的优先级倒序排，优先级大的排前面先执行
        	if (popvec.size() > 1)
        	{
        		boolean sort = false;
        		Vector newpopvec = new Vector();
        		for (int i = 0;i < popvec.size();i++)
        		{
        			CmPopGoodsDef cmp = (CmPopGoodsDef)popvec.elementAt(i);
        		
	        		// 找到比自己优先级低的规则，并把自己插到该规则前
        			int j = 0;
        			for (;j<newpopvec.size();j++)
        			{
        				CmPopGoodsDef cmp1 = (CmPopGoodsDef)newpopvec.elementAt(j);
        				if (cmp.ruleinfo.pri > cmp1.ruleinfo.pri) break;
        			}
        			if (j >= newpopvec.size()) newpopvec.add(cmp);
        			else { newpopvec.insertElementAt(cmp,j); sort = true; }
        		}
        		if (sort) goodsCmPop.setElementAt(newpopvec, goodsCmPop.size()-1);
        	}
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
		            			
		    					// 1-按单品/2-按柜组/3-按品牌/4-按品类/5-按柜组+品牌/6-按柜组+品类/7-按品牌+品类/8-按柜+品+类/9-按条码(子商品)/A-按属性1/B-按属性2/C-按属性3/D-按属性4/E-按属性5/F-按属性6/G-按属性7/H-按属性8
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
			    						(poprl.memo.charAt(0) == 'H' && gd.attr08 != null && gd.attr08.equals(gdgrp.attr08)))
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
			    			if (cmp.ruleinfo.rulename != null && cmp.ruleinfo.rulename.trim().equals(Language.apply("逢倍促销")))
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
	            if (new MessageBox(Language.apply("本笔交易有需要除券的活动促销,请先输入券付款金额\n\n如果顾客没有券付款,请直接按‘退出’键")).verify() != GlobalVar.Exit)
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
						contents.add(new String[]{Language.apply("合计"),"",ManipulatePrecision.doubleToString(hjcxsl,4,1,true),ManipulatePrecision.doubleToString(hjcjj),ManipulatePrecision.doubleToString(hjcxje)});
			            String[] title = { Language.apply("商品编码"), Language.apply("商品名称"), Language.apply("数量"),Language.apply("成交价"),Language.apply("活动金额")};
			            int[] width = { 130,200,60,115,115 };
//			            new MutiSelectForm().open("以下商品参加["+ cmp.ruleinfo.rulename + "]活动,总共可享受 " + ManipulatePrecision.doubleToString(popje) + " 元的促销折扣", title, width, contents,false,675,319,645,192,false);
			            new MutiSelectForm().open(Language.apply("以下商品参加[{0}]活动,总共可享受 {1} 元的促销折扣" ,new Object[]{cmp.ruleinfo.rulename ,ManipulatePrecision.doubleToString(popje)}), title, width, contents,false,675,319,645,192,false);
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

    public void addCmPopDetail(int index,CmPopGoodsDef cmp,double zk)
    {
    	addCmPopDetail(index,cmp,zk,false);
    }
    
    public void addCmPopDetail(int index,CmPopGoodsDef cmp,double zk,boolean giftcond)
    {
		if (!giftcond && zk <= 0) return;

		SpareInfoDef sginfo = (SpareInfoDef)goodsSpare.elementAt(index);
		if (sginfo.popzk == null) sginfo.popzk = new Vector();
		if (!giftcond) sginfo.popzk.add(new String[]{String.valueOf(cmp.cmpopseqno),ManipulatePrecision.doubleToString(zk),cmp.popmemo});
		else
		{
			// 检查条件商品是否已经同序号的促销折扣,没有则要加入
			int i=0;
			for (;i<sginfo.popzk.size();i++)
			{
				String[] s = (String[])sginfo.popzk.elementAt(i);
				if (s[0].equals(String.valueOf(cmp.cmpopseqno))) break;
			}
			if (i >= sginfo.popzk.size())
			{
				sginfo.popzk.add(new String[]{String.valueOf(cmp.cmpopseqno),ManipulatePrecision.doubleToString(zk),cmp.popmemo});
			}
		}
    }
    
    public void addCmGiftDetail(int index,CmPopGoodsDef cmp,CmPopGiftsDef cmgift,double zk)
    {
		if (zk <= 0) return;

		SpareInfoDef sginfo = (SpareInfoDef)goodsSpare.elementAt(index);
		if (sginfo.popzk == null) sginfo.popzk = new Vector();
        sginfo.popzk.add(new String[]{String.valueOf(cmp.cmpopseqno)+"|"+String.valueOf("G"+cmgift.giftseqno),ManipulatePrecision.doubleToString(zk),cmp.popmemo});
    }        

    public void refreshCmPopUI()
    {
    	// 重算应收
    	calcHeadYsje();
    	
    	// 刷新商品列表
    	saleEvent.updateTable(getSaleGoodsDisplay());
        saleEvent.table.setSelection(saleEvent.table.getItemCount() - 1);
        saleEvent.table.showSelection();
    	saleEvent.setTotalInfo();
        saleEvent.setCurGoodsBigInfo();
    }
    
    public boolean doCmPopGift(int sgindex,SaleGoodsDef cursg,CmPopGoodsDef cmp,String dqid,String ruleid,String ladderid,int ladderbs)
    {
    	Vector giftvec = ((Bcrm_DataService)DataService.getDefault()).findCMPOPGift(dqid,ruleid,ladderid);
    	if (giftvec == null) return false;
    	
    	boolean havegift = false;
    	for (int i=0;i<giftvec.size();i++)
    	{
    		CmPopGiftsDef cmgift = (CmPopGiftsDef)giftvec.elementAt(i);
    		if (cmgift.joinmode == 'N') continue;
    		
    		// 同分组内任选一个赠品
    		if (i+1 < giftvec.size() && ((CmPopGiftsDef)giftvec.elementAt(i+1)).giftgroup == cmgift.giftgroup)
    		{
    			// 找出同分组需要任选的赠品集合
    			Vector contents = new Vector();
    			int j = i;
    			for (;j<giftvec.size();j++)
    			{
    				CmPopGiftsDef cmgift1 = (CmPopGiftsDef)giftvec.elementAt(j);
    				if (cmgift1.giftgroup != cmgift.giftgroup) break;
    				if (cmgift1.joinmode == 'N') continue;
    				contents.add(new String[]{String.valueOf(j),cmgift1.giftname,ManipulatePrecision.doubleToString(cmgift1.giftsl,4,1,true)});
    			}
    			
    			// 计算下一个分组赠品的集合位置,循环还要i++,因此i=j-1
    			i = j-1;
    			if (contents.size() <= 0) continue;
    			
				// 确定任选模式是任选组内某一项赠品还是组内赠品任意XX件
				boolean selmode = false;	//	组内赠品任意XX件
				for (j=0;j<contents.size();j++)
				{
					// giftsl <= 0 任选XX模式
					if (Convert.toDouble(((String[])contents.elementAt(j))[2]) > 0)
					{
						selmode = true;
						break;
					}
				}
				
				// 选择赠品项
				// 任选组内某一项,按达到条件的倍数循环进行任选,每次翻倍可以选择不同的赠品集合
				// 组内赠品任意件,按可促销数量进行循环,每1件都可以是一个集合项
				int choice = 0;
                String[] title = { Language.apply("序号"), Language.apply("赠品描述"),Language.apply("赠品数量") };
                int[] width = { 60, 460,100 };
            	int maxsl = ladderbs;
            	if (!selmode) maxsl = (int)cmgift.giftmaxsl*ladderbs;
    			j = 0;
    			while(j < maxsl)
    			{
//    				if (selmode) choice = new MutiSelectForm().open("第" + (sgindex+1) + "行商品参与的["+cmp.ruleinfo.rulename+"]活动可分("+j+"/"+maxsl+")"+"次选择以下任意一项进行促销", title, width, contents,false,680,319,false);
//    				else choice = new MutiSelectForm().open("第" + (sgindex+1) + "行商品参与的["+cmp.ruleinfo.rulename+"]活动可促销以下商品任意("+j+"/"+maxsl+")件", title, width, contents,false,680,319,false);
    				if (selmode) choice = new MutiSelectForm().open(Language.apply("第{0}行商品参与的[{1}]活动可分({2}/{3})次选择以下任意一项进行促销", new Object[]{(sgindex+1)+"" ,cmp.ruleinfo.rulename ,j+"" ,maxsl+""}), title, width, contents,false,680,319,false);
    				else choice = new MutiSelectForm().open(Language.apply("第{0}行商品参与的[{1}]活动可促销以下商品任意({2}/{3})件" ,new Object[]{(sgindex+1)+"" ,cmp.ruleinfo.rulename ,j+"" ,maxsl+""}), title, width, contents,false,680,319,false);
                	if (choice >= 0)
                	{
                		// 只有一个选择条件,则一次性促销所有翻倍,不存在重新选择赠品规则
                		int sl = 1;
                		if (contents.size() <= 1) sl = ladderbs;
                		
    	    			// 执行赠品处理
    	    			int choicerow = Integer.parseInt(((String[])contents.elementAt(choice))[0]);
    	    			if (calcCmPopGift(giftvec,choicerow,cursg,cmp,sl))
    	    			{
    	    				havegift = true;
    	    				
    	    				j += sl;
    	    			}
    	    			else
    	    			{
    	    				if (selmode) new MessageBox(Language.apply("你选择的促销活动未能实现,请重新选择促销项进行促销"));
    	    			}
                	}
                	else
                	{
	                	if (new MessageBox(Language.apply("你确定要放弃可选择的促销活动吗？"), null, true).verify() == GlobalVar.Key1) break;
                	}
                }
    		}
    		else
    		{
    			// 执行赠品处理
    			if (calcCmPopGift(giftvec,i,cursg,cmp,ladderbs)) havegift = true;
    		}
    	}
    	
    	return havegift;
    }
    
    public boolean calcCmPopGift(Vector giftvec,int index,SaleGoodsDef cursg,CmPopGoodsDef cmp,int ladderbs)
    {
    	boolean havegift = false;
    	CmPopGiftsDef cmgift = (CmPopGiftsDef)giftvec.elementAt(index);
    	
		// 可进行赠送的数量
		double giftsl = ladderbs * cmgift.giftsl;
		if (cmgift.giftmaxsl > 0 && giftsl > cmgift.giftmaxsl) giftsl = cmgift.giftmaxsl;
		if (giftsl <= 0) giftsl = 1;
			
		// 根据赠品类型进行处理
		if (cmgift.gifttype == '1' || cmgift.gifttype == '5')					//已销售的商品,进行打折优惠
		{
			for (int j=0;j<saleGoods.size();j++)
			{
				if (giftsl <= 0) break;
				SaleGoodsDef sg = (SaleGoodsDef)saleGoods.elementAt(j);
				
				// 不匹配商品
				if (!mathingCmPopGift(cmgift,sg)) continue;
				
				// 不参加的赠品
				if (isNoJoinGift(giftvec,index,sg)) continue;

		        // 要促销的商品如果是条件商品行则不能作为被促销商品，继续找下一个可优惠商品
				int popindex = -1;
				boolean nopop = false;
		        Vector popvec = (Vector)goodsCmPop.elementAt(j);
		    	for (int i=0;popvec != null && i<popvec.size();i++)
		    	{
		    		CmPopGoodsDef cmp1 = (CmPopGoodsDef)popvec.elementAt(i);
		    		if ((cmp1.ruleinfo.summode == 0 && cmp1.cmpopseqno == cmp.cmpopseqno) ||
		    			(cmp1.ruleinfo.summode != 0 && cmp1.dqid.equals(cmp.dqid) && cmp1.ruleid.equals(cmp.ruleid)))
		    		{
		    			popindex = i;
		    			if (cmp1.used) nopop = true;
		    			break;
		    		}
		    	}
		    	if (cmgift.gifttype == '1' &&  nopop) continue;
			    if (cmgift.gifttype == '5' && !nopop) continue;
				
				// 计算可参与数量,如果本行商品数量大于可参与促销数量,拆分商品行
				double sl = sg.sl;
				if (ManipulatePrecision.doubleCompare(sl, giftsl, 4) > 0)
				{
					// 按剩余可参与数量计算,并拆分商品行
					sl = giftsl;
					SplitSaleGoodsRow(j,sl);
				}
				
				// 被促销的商品不能做为促销条件继续计算本促销
				if (popindex >= 0 && popvec != null) ((CmPopGoodsDef)popvec.elementAt(popindex)).used = true;
				
    			// 促销打折
    			double zke = 0;
    			if (cmgift.popmode == '1' && sg.jg > cmgift.poplsj && cmgift.poplsj >= 0)
    			{
    				if (isMemberHyjMode() && cmgift.poplsj > cmgift.pophyj && cmgift.pophyj > 0) zke = (sg.jg - cmgift.pophyj) * sl;
    				else zke = (sg.jg - cmgift.poplsj) * sl;
    				zke = ManipulatePrecision.doubleConvert(zke - sg.hjzk);	    				
    			}
    			else if (cmgift.popmode == '2' && 1 > cmgift.poplsj && cmgift.poplsj >= 0)
    			{
    				if (isMemberHyjMode() && cmgift.poplsj > cmgift.pophyj && cmgift.pophyj > 0) zke = (sg.jg * sl) * (1 - cmgift.pophyj);
    				else zke = (sg.jg * sl) * (1 - cmgift.poplsj);
    				zke = ManipulatePrecision.doubleConvert(zke - sg.hjzk);
    			}
    			else if (cmgift.popmode == '3' && sg.jg > cmgift.poplsj && cmgift.poplsj > 0)
    			{
		    		if (isMemberHyjMode() && cmgift.pophyj > cmgift.poplsj && cmgift.pophyj > 0) zke = cmgift.pophyj * sl;
		    		else zke = cmgift.poplsj * sl;
		    		if (sg.hjje - sg.hjzk < zke) zke = ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk);
    			}

    			// 促销折扣
    			if (zke > 0)
    			{
    				// 减少可送数量
    				giftsl = ManipulatePrecision.doubleConvert(giftsl - sl,4,1);
    				
    	    		// 记录商品折扣
    	    		double oldzszke = sg.zszke;
    	    		sg.zszke += zke;
    	    		sg.zszke  = getConvertRebate(j,sg.zszke);
    	    		sg.zsdjbh = String.valueOf(cmp.cmpopseqno)+"|"+String.valueOf("G"+cmgift.giftseqno);
    	    		zke = sg.zszke - oldzszke;
    	    		getZZK(sg);
    	    		
    				// 记录促销折扣明细
    				addCmGiftDetail(j,cmp,cmgift,zke);
    				havegift = true;
    			}
			}
		}
		else
		if (cmgift.gifttype == '2')
		{
			// 是赠送单品则直接查找商品,否则要求收银员输入商品
			GoodsDef goodsDef = null;
			SaleGoodsDef gift = null;				
			if (cmgift.codemode == '1' || cmgift.codemode == '9')
			{
				goodsDef = findGoodsInfo(cmgift.codeid,cursg.yyyh,cmgift.codegz,"",false,null,false);
				if (goodsDef == null)
				{
//					new MessageBox("找不到商品["+cmgift.codeid+"]的信息\n\n不能赠送此商品!");
					new MessageBox(Language.apply("找不到商品[{0}]的信息\n\n不能赠送此商品!" ,new Object[]{cmgift.codeid}));
	                return false;
				}
				
				// 生成商品
				if (cmgift.popmode == '0')
				{
					goodsDef.lsj = cmgift.poplsj;
					gift = goodsDef2SaleGoods(goodsDef,cursg.yyyh,giftsl,goodsDef.lsj,giftsl*goodsDef.lsj,false);
				}
				else
				{
					gift = goodsDef2SaleGoods(goodsDef,cursg.yyyh,giftsl,goodsDef.lsj,giftsl*goodsDef.lsj,false);
					
					if (cmgift.popmode == '1' && gift.jg > cmgift.poplsj && cmgift.poplsj >= 0) gift.zszke = gift.hjje - cmgift.poplsj * giftsl;
					else if (cmgift.popmode == '2' && 1 > cmgift.poplsj && cmgift.poplsj >= 0) gift.zszke = gift.hjje * (1 - cmgift.poplsj);
					else if (cmgift.popmode == '3' && gift.jg > cmgift.poplsj && cmgift.poplsj > 0) gift.zszke = cmgift.poplsj * giftsl;
				}
		        gift.zszkfd = cmgift.poplsjzkfd;
				gift.zsdjbh = String.valueOf(cmp.cmpopseqno)+"|"+String.valueOf("G"+cmgift.giftseqno);
		        gift.flag   = '5';		//普通赠品
				getZZK(gift);

				// 提示顾客是否购买
				String tips = Language.apply("免费获赠");
//				if (gift.hjje - gift.hjzk > 0) tips = "用 "+ManipulatePrecision.doubleToString(gift.hjje - gift.hjzk)+" 元购买";
				if (gift.hjje - gift.hjzk > 0) tips = Language.apply("用 {0} 元购买" ,new Object[]{ManipulatePrecision.doubleToString(gift.hjje - gift.hjzk)});
				if (new MessageBox(Language.apply("有促销活动可")+ tips + "\n\n"+
									ManipulatePrecision.doubleToString(giftsl,4,1,true) + " X ["+cmgift.codeid+"]"+goodsDef.name + 
									Language.apply("\n\n顾客需要吗？\n\n任意键-是 / 2-否"),null,false).verify() != GlobalVar.Key2)
				{
    				// 不允许销红,检查库存
    		        if ((SellType.ISSALE(saletype) && GlobalInfo.sysPara.isxh != 'Y' && goodsDef.isxh != 'Y'))
    		        {
    		            // 统计商品销售数量
    		            double hjsl = giftsl + calcSameGoodsQuantity(goodsDef.code,goodsDef.gz);
    		            if (goodsDef.kcsl < hjsl)
    		            {
    		            	if(GlobalInfo.sysPara.xhisshowsl == 'Y')
//    		            		 new MessageBox("该商品库存为 "+ManipulatePrecision.doubleToString(goodsDef.kcsl)+"\n库存不足,不能销售");
    		            		 new MessageBox(Language.apply("该商品库存为{0}\n库存不足,不能销售" ,new Object[]{ManipulatePrecision.doubleToString(goodsDef.kcsl)}));
    						else
    							new MessageBox(Language.apply("销售数量已大于该商品库存,不能销售"));
    		               
    		                return false;
    		            }
    		        }
    		       
    		        // 增加到商品列表,加入的赠品无促销
					addSaleGoodsObject(gift,goodsDef,getGoodsSpareInfo(goodsDef, gift));
					goodsCmPop.set(saleGoods.size()-1,null);
					
    				// 记录促销折扣明细
					if (gift.zszke > 0) addCmGiftDetail(saleGoods.size()-1,cmp,cmgift,gift.zszke);
					havegift = true;
				}
			}
			else
			{
				double yssl = 0;
            	StringBuffer sb = new StringBuffer();
            	do 
            	{
            		String text = null; 
            		if (cmgift.codemode == '2') text = "[" + cmgift.codeid + "]"+Language.apply("柜组");
            		else if (cmgift.codemode == '3') text = "[" + cmgift.codeid + "]"+Language.apply("品牌");
            		else if (cmgift.codemode == '4') text = "[" + cmgift.codeid + "]"+Language.apply("品类");
            		else if (cmgift.codemode == '5') text = "[" + cmgift.codeid + "]"+Language.apply("柜组")+"["+cmgift.codegz+"]"+Language.apply("品牌");
            		else if (cmgift.codemode == '6') text = "[" + cmgift.codeid + "]"+Language.apply("柜组")+"["+cmgift.codegz+"]"+Language.apply("品类");
            		else if (cmgift.codemode == '7') text = "[" + cmgift.codeid + "]"+Language.apply("品牌")+"["+cmgift.codegz+"]"+Language.apply("品类");
            		else if (cmgift.codemode == '8') text = "[" + cmgift.codeid + "]"+Language.apply("柜组")+"["+cmgift.codegz+"]"+Language.apply("品牌")+"["+cmgift.codeuid+"]"+Language.apply("品类");
            		else if (cmgift.codemode == '0') text = Language.apply("全场");
            		else break;

            		// 促销提示描述
            		String poptips = null;
            		String zkjtips = null;
            		if (cmgift.popmode == 0) zkjtips = Language.apply("可用") + ManipulatePrecision.doubleToString(cmgift.poplsj) + Language.apply("元");
            		else if (cmgift.popmode == '1' && cmgift.poplsj >= 0) zkjtips = Language.apply("可用") + ManipulatePrecision.doubleToString(cmgift.poplsj * (giftsl-yssl)) + Language.apply("元");
//					else if (cmgift.popmode == '2' && 1 > cmgift.poplsj && cmgift.poplsj >= 0) zkjtips = "可用 " + ManipulatePrecision.doubleToString(cmgift.poplsj * 100) + "% 的折扣";
					else if (cmgift.popmode == '2' && 1 > cmgift.poplsj && cmgift.poplsj >= 0) zkjtips = Language.apply("可用 {0}% 的折扣" ,new Object[]{ManipulatePrecision.doubleToString(cmgift.poplsj * 100)});
					else if (cmgift.popmode == '3' && cmgift.poplsj > 0) zkjtips = Language.apply("可减价") + ManipulatePrecision.doubleToString(cmgift.poplsj * (giftsl-yssl)) + Language.apply("元");
					else zkjtips = Language.apply("可用原价");
            		if (cmgift.poppfj > 0)
            		{
//            			poptips = "有促销活动"+zkjtips+"购买"+text+"的\n价值 " + ManipulatePrecision.doubleToString(cmgift.poppfj)+" 元以内的任意 "+ManipulatePrecision.doubleToString((giftsl-yssl),4,1,true)+" 件商品";
            			poptips = Language.apply("有促销活动{0}购买{1}的\n价值 {2} 元以内的任意 {3} 件商品" ,new Object[]{zkjtips ,text ,ManipulatePrecision.doubleToString(cmgift.poppfj) ,ManipulatePrecision.doubleToString((giftsl-yssl),4,1,true)});
            		}
            		else
            		{
//            			poptips = "有促销活动"+zkjtips+"购买"+text+"的\n任意 "+ManipulatePrecision.doubleToString((giftsl-yssl),4,1,true)+" 件商品";
            			poptips = Language.apply("有促销活动{0}购买{1}的\n任意 {2} 件商品" ,new Object[]{zkjtips ,text ,ManipulatePrecision.doubleToString((giftsl-yssl),4,1,true)});
            		}
//            		if (new TextBox().open("请输入"+text+"的某件商品", "商品编码", poptips , sb,TextBox.AllInput))
            		if (new TextBox().open(Language.apply("请输入{0}的某件商品" ,new Object[]{text}), Language.apply("商品编码"), poptips , sb,TextBox.AllInput))
            		{
						double bcsl = 1;
						String barcode = sb.toString();
						if (barcode.indexOf("*") > 0 && barcode.indexOf("*") < barcode.length()-1)
				        {
				            bcsl = Convert.toDouble(barcode.substring(0,barcode.indexOf("*")));
				            barcode = barcode.substring(barcode.indexOf("*") + 1);
				            if (bcsl <= 0) bcsl = 1;
				            if (ManipulatePrecision.doubleCompare(yssl+bcsl,giftsl,4) > 0) bcsl = ManipulatePrecision.doubleConvert(giftsl - yssl,4,1);
				        }
						
            			if (cmgift.codemode == '2' || cmgift.codemode == '5' || cmgift.codemode == '6' || cmgift.codemode == '8') goodsDef = findGoodsInfo(barcode,cursg.yyyh,cmgift.codeid,"",false,null,true);
            			else goodsDef = findGoodsInfo(barcode,cursg.yyyh,"","",false,null,false);
    					if (goodsDef == null) continue;
    					
    					// 检查商品是否匹配
                		if (!mathingCmPopGift(cmgift,goodsDef))
                		{
//    						new MessageBox("该商品不是"+text+"的商品");
    						new MessageBox(Language.apply("该商品不是{0}的商品" ,new Object[]{text}));
    						continue;
                		}
                		
                		// 检查商品价值是否匹配
    					if (cmgift.poppfj > 0 && goodsDef.lsj <= 0) goodsDef.lsj = cmgift.poppfj;
    					if (cmgift.poppfj > 0 && goodsDef.lsj > cmgift.poppfj)
    					{					
//    						new MessageBox("该商品价值 " + ManipulatePrecision.doubleToString(goodsDef.lsj) + " 元\n\n促销活动只能赠价值 "+ManipulatePrecision.doubleToString(cmgift.poppfj)+" 元以内的商品");
    						new MessageBox(Language.apply("该商品价值 {0} 元\n\n促销活动只能赠价值 {1} 元以内的商品" ,new Object[]{ManipulatePrecision.doubleToString(goodsDef.lsj) ,ManipulatePrecision.doubleToString(cmgift.poppfj)}));
    						continue;
    					}
    					else
    					{
            				// 不允许销红,检查库存
            		        if ((SellType.ISSALE(saletype) && GlobalInfo.sysPara.isxh != 'Y' && goodsDef.isxh != 'Y'))
            		        {
            		            // 统计商品销售数量
            		            double hjsl = bcsl + calcSameGoodsQuantity(goodsDef.code,goodsDef.gz);
            		            if (goodsDef.kcsl < hjsl)
            		            {
            		            	if(GlobalInfo.sysPara.xhisshowsl == 'Y')
//            		            		new MessageBox("该商品库存为 "+ManipulatePrecision.doubleToString(goodsDef.kcsl)+"\n库存不足,不能销售");
            		            		new MessageBox(Language.apply("该商品库存为 {0}\n库存不足,不能销售" ,new Object[]{ManipulatePrecision.doubleToString(goodsDef.kcsl)}));
            						else
            							new MessageBox(Language.apply("销售数量已大于该商品库存,不能销售"));
            		               
            		                continue;
            		            }
            		        }
            		        
    						// 生成商品
    						if (cmgift.popmode == '0')
    						{
    							goodsDef.lsj = cmgift.poplsj;
    							gift = goodsDef2SaleGoods(goodsDef,cursg.yyyh,bcsl,goodsDef.lsj,bcsl*goodsDef.lsj,false);
    						}
    						else
    						{
    							gift = goodsDef2SaleGoods(goodsDef,cursg.yyyh,bcsl,goodsDef.lsj,bcsl*goodsDef.lsj,false);
    							
    							if (cmgift.popmode == '1' && gift.jg > cmgift.poplsj && cmgift.poplsj >= 0) gift.zszke = gift.hjje - cmgift.poplsj * bcsl;
    							else if (cmgift.popmode == '2' && 1 > cmgift.poplsj && cmgift.poplsj >= 0) gift.zszke = gift.hjje * (1 - cmgift.poplsj);
    							else if (cmgift.popmode == '3' && gift.jg > cmgift.poplsj && cmgift.poplsj > 0) gift.zszke = cmgift.poplsj * bcsl;
    						}
    				        gift.zszkfd = cmgift.poplsjzkfd;
    						gift.zsdjbh = String.valueOf(cmp.cmpopseqno)+"|"+String.valueOf("G"+cmgift.giftseqno);
    				        gift.flag   = '5';		//普通赠品
    						getZZK(gift);
            		        
        					// 不参加的赠品
        					if (isNoJoinGift(giftvec,index,gift))
        					{
        						new MessageBox(Language.apply("该商品是不参与活动的例外商品"));
        						continue;
        					}
        					
    	    		        // 增加到商品列表,加入的赠品无促销
    						addSaleGoodsObject(gift,goodsDef,getGoodsSpareInfo(goodsDef, gift));
    						goodsCmPop.set(saleGoods.size()-1,null);

    						// 记录促销折扣明细
    						if (gift.zszke > 0) addCmGiftDetail(saleGoods.size()-1,cmp,cmgift,gift.zszke);
    						havegift = true;
    						yssl = ManipulatePrecision.add(yssl,bcsl);
    						
        					// 促销数量足够则跳出编码输入循环
    						if (ManipulatePrecision.doubleCompare(yssl,giftsl,4) >= 0) break;
    						
    						// 刷新UI界面,显示新加入商品列表的商品
    						refreshCmPopUI();
    					}
            		}
            		else
            		{
            			if (new MessageBox(Language.apply("顾客放弃促销活动赠送的商品吗？"),null,true).verify() == GlobalVar.Key1)
            			{
            				// 跳出编码输入循环
            				break;
            			}
            		}
            	} while(true);
			}
		}
		else
		if (cmgift.gifttype == '3')
		{
			new MessageBox(Language.apply("有促销活动可到服务台领取礼品\n\n") + ManipulatePrecision.doubleToString(giftsl,4,1,true)+ " X " + cmgift.giftname);
			
			GoodsDef goodsDef = new GoodsDef();
			if (cmgift.codemode == '1') goodsDef.barcode = cmgift.codeid;
			else goodsDef.barcode = "GIFT"+saleGoods.size();	
			goodsDef.code = goodsDef.barcode;
			goodsDef.gz = cmgift.codegz;
			goodsDef.uid = cmgift.codeuid;
//			goodsDef.name = "请到服务台领取"+cmgift.giftname;
			goodsDef.name = Language.apply("请到服务台领取{0}" ,new Object[]{cmgift.giftname});
			goodsDef.type = '1';
			goodsDef.unit = "件";
			goodsDef.lsj = 0;
			
			SaleGoodsDef gift = goodsDef2SaleGoods(goodsDef,cursg.yyyh,giftsl,goodsDef.lsj,0,false);
			gift.zszke = gift.hjje;
	        gift.zszkfd = cmgift.poplsjzkfd;
			gift.zsdjbh = String.valueOf(cmp.cmpopseqno)+"|"+String.valueOf("G"+cmgift.giftseqno);
			gift.flag = '1';		//赠品联商品,顾客凭赠品联到服务台领取赠品
			getZZK(gift);
			
			addSaleGoodsObject(gift,null,null);
			havegift = true;
		}
		else
		if (cmgift.gifttype == '4')
		{
			// 调用过程得到本小票可送券并打印
		}
    	
		// 发生促销刷新界面
		if (havegift) refreshCmPopUI();
		
    	return havegift;
    }
    
    public boolean isNoJoinGift(Vector giftvec,int index,SaleGoodsDef gift)
    {
    	CmPopGiftsDef curcmg = (CmPopGiftsDef)giftvec.elementAt(index);
		for (int j=index;j<giftvec.size();j++)
		{
			CmPopGiftsDef cmgift = (CmPopGiftsDef)giftvec.elementAt(j);
			if (cmgift.giftgroup != curcmg.giftgroup) break;
			if (cmgift.joinmode != 'N') continue;
			
			// 商品符合不参加条件
			if (mathingCmPopGift(cmgift,gift))
			{
				return true;
			}
		}
    	
    	return false;
    }
    
    public boolean mathingCmPopGift(CmPopGiftsDef cmgift,SaleGoodsDef sg)
    {
		if (
			(cmgift.codemode == '0') ||
			(cmgift.codemode == '1' && sg.code.equals(cmgift.codeid) && 
			(sg.gz.equals(cmgift.codegz) || cmgift.codegz == null || cmgift.codegz.equals("%") || cmgift.codegz.equals("")) &&
			(sg.uid.equals(cmgift.codeuid) || cmgift.codeuid == null || cmgift.codeuid.equals("%") || cmgift.codeuid.equals(""))) ||
			(cmgift.codemode == '2' && sg.gz.equals(cmgift.codeid)) ||
			(cmgift.codemode == '3' && sg.ppcode.equals(cmgift.codeid)) ||
			(cmgift.codemode == '4' && sg.catid.equals(cmgift.codeid)) ||
			(cmgift.codemode == '5' && sg.gz.equals(cmgift.codeid) && sg.ppcode.equals(cmgift.codegz)) ||
			(cmgift.codemode == '6' && sg.gz.equals(cmgift.codeid) && sg.catid.equals(cmgift.codegz)) ||
			(cmgift.codemode == '7' && sg.ppcode.equals(cmgift.codeid) && sg.catid.equals(cmgift.codegz)) ||
			(cmgift.codemode == '8' && sg.gz.equals(cmgift.codeid) && sg.ppcode.equals(cmgift.codegz) && sg.catid.equals(cmgift.codeuid)) ||
			(cmgift.codemode == '9' && sg.barcode.equals(cmgift.codeid) && (sg.gz.equals(cmgift.codegz) || cmgift.codegz == null || cmgift.codegz.equals("%") || cmgift.codegz.equals("")))
			)
		{
			return true;
		}
		else
		{
			return false;
		}
    }
    
    public boolean mathingCmPopGift(CmPopGiftsDef cmgift,GoodsDef gd)
    {
		if (
			(cmgift.codemode == '0') ||
			(cmgift.codemode == '1' && gd.code.equals(cmgift.codeid) && 
			(gd.gz.equals(cmgift.codegz) || cmgift.codegz == null || cmgift.codegz.equals("%") || cmgift.codegz.equals("")) &&
			(gd.uid.equals(cmgift.codeuid) || cmgift.codeuid == null || cmgift.codeuid.equals("%") || cmgift.codeuid.equals(""))) ||
			(cmgift.codemode == '2' && gd.gz.equals(cmgift.codeid)) ||
			(cmgift.codemode == '3' && gd.ppcode.equals(cmgift.codeid)) ||
			(cmgift.codemode == '4' && gd.catid.equals(cmgift.codeid)) ||
			(cmgift.codemode == '5' && gd.gz.equals(cmgift.codeid) && gd.ppcode.equals(cmgift.codegz)) ||
			(cmgift.codemode == '6' && gd.gz.equals(cmgift.codeid) && gd.catid.equals(cmgift.codegz)) ||
			(cmgift.codemode == '7' && gd.ppcode.equals(cmgift.codeid) && gd.catid.equals(cmgift.codegz)) ||
			(cmgift.codemode == '8' && gd.gz.equals(cmgift.codeid) && gd.ppcode.equals(cmgift.codegz) && gd.catid.equals(cmgift.codeuid)) ||
			(cmgift.codemode == '9' && gd.barcode.equals(cmgift.codeid) && (gd.gz.equals(cmgift.codegz) || cmgift.codegz == null || cmgift.codegz.equals("%") || cmgift.codegz.equals("")))
			)
		{
			return true;
		}
		else
		{
			return false;
		}
    }
    
    // 删除CMPOP促销结果
    public void delCmPop()
    {	
		// 恢复所有商品的促销计算标志
    	for (int i=0;i<goodsCmPop.size();i++)
    	{
    		Vector popvec = (Vector)goodsCmPop.elementAt(i);
    		if (popvec == null) continue;
    		for (int j=0;j<popvec.size();j++)
    		{
    			CmPopGoodsDef cmp = (CmPopGoodsDef)popvec.elementAt(j);
    			
    			// 不累计的促销允许立即计算折扣的促销,则找到商品后立即计算促销折扣,付款时不处理
    			if (cmp.ruleinfo.summode == '0') continue;
        		
        		// 未使用
        		cmp.used = false;
    		}
    	}
 	        	
    	// 取消赠品及折扣
    	boolean havedel = false;
    	for (int i=0;i<saleGoods.size();i++)
    	{
    		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
    		
    		// 删除赠送促销产生的商品
    		if (saleGoodsDef.flag == '1' || saleGoodsDef.flag == '5' || goodsAssistant.elementAt(i) == null)
    		{
    			havedel = true;
    			
    			delSaleGoodsObject(i);
    			getDeleteGoodsDisplay(i,saleGoodsDef);
    			i--;
    			continue;
    		}

    		if (saleGoodsDef.zszke > 0) havedel = true;
    		
    		// 恢复按下付款键时计算的折扣
			saleGoodsDef.zsdjbh = null;
			saleGoodsDef.zszke	= 0;
			saleGoodsDef.zszkfd = 0;
			getZZK(saleGoodsDef);
    	}

    	// 恢复商品信息
    	if (havedel) delCmPopReadData();
    	
    	// 恢复了商品折扣刷新界面
    	if (havedel) refreshCmPopUI();
    }
    
    public boolean doCmPop(int sgindex)
    {
    	// 先总是无满减规则方式的付款
    	isPreparePay = payNormal;
    	
    	// 不参与促销计算的交易类型
    	if (SellType.NOPOP(saletype) || !SellType.ISSALE(saletype) || SellType.ISEARNEST(saletype) || SellType.ISPREPARETAKE(saletype))
    	{
    		return false;
    	}
    	
    	// 先备份当前商品信息,以便放弃时付款时恢复
    	doCmPopWriteData();
    	
    	// 对档期按优先级排序,优先级大的先执行,先把商品同档期的所有促销执行完再执行下一个档期各商品的所有促销
    	Vector dqvec = new Vector();
    	for (int i=0;i<goodsCmPop.size();i++)
    	{
    		Vector popvec = (Vector)goodsCmPop.elementAt(i);
    		for (int j=0;popvec != null && j<popvec.size();j++)
    		{
    			CmPopGoodsDef cmp = (CmPopGoodsDef)popvec.elementAt(j);
    			
    			// 是否已加入到档期列表
    			int n = 0;
    			for (n=0;n<dqvec.size();n++)
    			{
    				String dq = (String)dqvec.elementAt(n);
    				String[] s= dq.split(",");
    				if (s[0].equals(cmp.dqid)) break;
    			}
    			if (n >= dqvec.size())
    			{
        			for (n=0;n<dqvec.size();n++)
        			{
        				String dq = (String)dqvec.elementAt(n);
        				String[] s= dq.split(",");
        				int pri = Convert.toInt(s[1]);
        				if (cmp.dqinfo.pri > pri || (cmp.dqinfo.pri == pri && cmp.dqinfo.dqid.compareTo(s[0]) > 0))
        				{
        					break;
        				}
        			}
        			if (n >= dqvec.size()) dqvec.add(cmp.dqinfo.dqid + ","+ cmp.dqinfo.pri);
        			else dqvec.insertElementAt(cmp.dqinfo.dqid + "," + cmp.dqinfo.pri,n);
    			}
    		}
    	}
    	
    	// 计算需要进行累计的促销所产生的折扣
    	boolean havepop = false;
    	for (int n=0;n<dqvec.size();n++)
    	{
			String dqid = ((String)dqvec.elementAt(n)).split(",")[0];
			
	    	for (int i=0;i<goodsCmPop.size();i++)
	    	{
	    		// 指定执行某行商品的累计促销
	    		if (sgindex >= 0 && i != sgindex) continue;
	    		
	    		Vector popvec = (Vector)goodsCmPop.elementAt(i);
	    		for (int j=0;popvec != null && j<popvec.size();j++)
	    		{
	    			CmPopGoodsDef cmp = (CmPopGoodsDef)popvec.elementAt(j);
	    			
	        		// 不累计的促销允许立即计算折扣的促销,则找到商品后立即计算促销折扣,付款时不处理     		
	    			if (cmp.ruleinfo.summode == '0') continue;
	        		
	    			// 按档期顺序执行
	    			if (!cmp.dqid.equals(dqid)) continue;
	    			
	        		// 计算促销折扣
	        		if (calcGoodsCMPOPRebate(i,cmp,j))
	        		{
		        		// 调试模式提示促销计算结果，便于了解促销计算规则
	        			if (ConfigClass.DebugMode)
	        			{
	        				refreshCmPopUI();
	        	            
//	        	            new MessageBox("刚计算完的促销是第 " + (i+1) + " 行商品在\n\n["+dqid+" - "+cmp.dqinfo.name+"]促销档期内的\n\n["+cmp.ruleinfo.ruleid+" - " + cmp.ruleinfo.rulename + "]促销规则\n\n请核对促销活动的结果");
	        	            new MessageBox(Language.apply("刚计算完的促销是第 {0} 行商品在\n\n[{1} - {2}]促销档期内的\n\n[{3} - {4}]促销规则\n\n请核对促销活动的结果" ,new Object[]{(i+1)+"" ,dqid ,cmp.dqinfo.name ,cmp.ruleinfo.ruleid ,cmp.ruleinfo.rulename}));
	        			}
	        			
	        			// 设置存在CM促销标记
	        			havepop = true;
	        			
		        		// 如果计算出有促销,但又未标记促销,则说明本次自身被除外,需要再计算一次
		        		if (!cmp.used) j--;
	        		}
	        		
	        		// 计算促销时进了预付款,预付款已足够,交易已完成
	        		if (doCmPopExit) return havepop;
	    		}
	    	}
    	}
    	
    	// 计算出CM促销折扣,重算小票
    	if (havepop) refreshCmPopUI();
    	
		return havepop;
    }

    public boolean doCmPopWriteData()
    {
    	FileOutputStream f = null;
    	
        try
        {
            String name = ConfigClass.LocalDBPath + "/Cmpop.dat";
            
	        f = new FileOutputStream(name);
	        ObjectOutputStream s = new ObjectOutputStream(f);
	        
	        // 将交易对象写入对象文件
	        s.writeObject(saleGoods);
	        s.writeObject(goodsSpare);
	        s.writeObject(goodsCmPop);
	        
	        s.flush();
	        s.close();
	        f.close();
	        s = null;
	        f = null;
	        
	        return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            return false;
        }
        finally
        {
        	try
        	{
	            if (f != null) f.close();
        	}
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public boolean delCmPopReadData()
    {
    	FileInputStream f = null;
    	
        try
        {
            String name = ConfigClass.LocalDBPath + "/Cmpop.dat";
            if (!new File(name).exists()) return true;
            
	        f = new FileInputStream(name);
	        ObjectInputStream s = new ObjectInputStream(f);
	        
	        // 读交易对象
	        Vector saleGoods1 = (Vector) s.readObject();
	        Vector spare1 = (Vector) s.readObject();
	        Vector goodsCmPop1 = (Vector) s.readObject();
	        
			// 赋对象
	    	saleGoods = saleGoods1;
	    	goodsSpare = spare1;
	    	goodsCmPop = goodsCmPop1;
	    	
            // 关闭断点文件
            s.close();
            s = null;
            f.close();
            f = null;

        	return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            return false;
        }
        finally
        {
        	try
        	{
	            if (f != null) f.close();
        	}
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }  
    
    public void sortSalegoods()
    {
		if (saleGoods.size() > 1)
		{
			SaleGoodsDef sgd = null;
			GoodsDef gd = null;
			SpareInfoDef sid = null;
			GoodsPopDef gpd = null;
			for (int i = 1; i < saleGoods.size(); i++)
			{
				for (int j = 0; j < saleGoods.size() - i; j++)
				{
					String code = ((SaleGoodsDef)saleGoods.get(j)).code;
					String code1 = ((SaleGoodsDef)saleGoods.get(j + 1)).code;
					
					if (code.equals(code1)) continue;
					
					if (code1.length() < code.length() || (code1.length() == code.length() && code1.compareTo(code) < 0))
					{
						sgd = (SaleGoodsDef)saleGoods.get(j);
						saleGoods.setElementAt(saleGoods.get(j + 1), j);
						saleGoods.setElementAt(sgd, j + 1);
						
						gd = (GoodsDef)goodsAssistant.get(j);
						goodsAssistant.setElementAt(goodsAssistant.get(j + 1), j);
						goodsAssistant.setElementAt(gd, j + 1);
						
						sid = (SpareInfoDef)goodsSpare.get(j);
						goodsSpare.setElementAt(goodsSpare.get(j + 1), j);
						goodsSpare.setElementAt(sid, j + 1);
						
						gpd = (GoodsPopDef)crmPop.get(j);
						crmPop.setElementAt(crmPop.get(j + 1), j);
						crmPop.setElementAt(gpd, j + 1);
					}
				}
			}
		}
    }
    
	public boolean doSuperMarketCrmPop()
	{
		if (!SellType.ISSALE(saletype)) { return false; }

		if (SellType.NOPOP(saletype)) { return false; }

		if (SellType.ISEARNEST(saletype)) { return false; }

		if (SellType.ISPREPARETAKE(saletype)) { return false; }

		// 初始化超市促销标志
		for (int i = 0; i < saleGoods.size(); i++)
			((SaleGoodsDef) saleGoods.get(i)).isSMPopCalced = 'Y';
		
		// 排序
		sortSalegoods();
		
		// 查找规则
		SuperMarketPopRuleDef ruleDef = null;
		Vector notRuleDjbh = new Vector();
		int calcCount = saleGoods.size();
		int k, j, l, m, n;
		double zje, je, t_zje;
		double or_yhsl = 0 ;//结果为OR关系的时候,存放第一个结果的数量
		long bs, minbs, t_minbs;
		
		String cardNo = "";
		if (curCustomer != null)
		{
			cardNo = curCustomer.code;
		}

		SaleGoodsDef saleGoodsDef = null;
		GoodsDef goodsDef = null;
		//SpareInfoDef spareInfoDef = null;
		for (int i = 0; i < calcCount + 1; i++)
		{
			// 查找单品优惠单号
			if (i != calcCount)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.get(i);
				goodsDef = (GoodsDef) goodsAssistant.get(i);
				//	spareInfoDef = (SpareInfoDef) goodsSpare.get(i);
				// 只有普通商品才能参与
				if (saleGoodsDef.flag != '2' && saleGoodsDef.flag != '4') continue;
				// 判断是否曾参与规则促销
				if (saleGoodsDef.isSMPopCalced == 'N') continue;
				// 标准百货crm促销，跳过
//				if (goodsDef.str4.equals("Y")) continue;

				//已经查过的商品无需重复查规则促销
				for (k = 0; k < i; k++)
				{
					if (goodsDef.code.equals(((GoodsDef) goodsAssistant.get(k)).code) && goodsDef.gz.equals(((GoodsDef) goodsAssistant.get(k)).gz)
							&& goodsDef.uid.equals(((GoodsDef) goodsAssistant.get(k)).uid)) break;
				}
				if (k < i) continue;

			}
			// 超找整单超市促销单号
			else
			{
				goodsDef = new GoodsDef();
				goodsDef.code = "ALL";
				goodsDef.gz = ManipulatePrecision.doubleToString(saleHead.ysje);
				goodsDef.catid = "";
				goodsDef.ppcode = "";
				goodsDef.uid = "";
			}

			// 首先查找超市规则促销单号
			ruleDef = new SuperMarketPopRuleDef();
			Bcrm_DataService bcrm_DataService = new Bcrm_DataService();
			if (!bcrm_DataService.findSuperMarketPopBillNo(ruleDef, goodsDef.code, goodsDef.gz, goodsDef.catid,
																						goodsDef.ppcode, goodsDef.uid, saleHead.rqsj,
																						saleHead.rqsj, cardNo))
			{
				continue;
			}
			
			System.out.println("商品：" + goodsDef.code + " 对应规则单号：" + ruleDef.djbh);

			//检查该单据是否已经运算过，如果已经运行过则无需重复运算
			for (k = 0; k < notRuleDjbh.size(); k++)
			{
				if (((String)notRuleDjbh.get(k)).equals(ruleDef.djbh)) break;
			}
			if (k < notRuleDjbh.size()) continue;

			// 查找超市促销规则明细
			ruleReqList = new Vector();
			rulePopList = new Vector();
			if (!bcrm_DataService.findSuperMarketPopRule(ruleReqList, rulePopList, ruleDef) || ruleReqList.size() == 0
					|| rulePopList.size() == 0)
			{
				continue;
			}

			// 初始化条件参数
			for (k = 0; k < saleGoods.size(); k++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
				// 标志为A表示在上一轮规则中被条件排除的商品，因此可以参与本轮规则促销
				sg.isPopExc = ' ';
				if (sg.isSMPopCalced == 'A') ((SaleGoodsDef) saleGoods.get(k)).isSMPopCalced = 'Y';
			}

			for (j = 0; j < ruleReqList.size(); j++)
			{
				for (k = 0; k < saleGoods.size(); k++)
				{
					//商品是否条件匹配
					if (isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(j), k))
					{
						((SaleGoodsDef)saleGoods.get(k)).isPopExc = 'Y';//表示条件满足
					}
				}
			}

			//先将规则条件中要排除的商品排除掉
			for (j = 0; j < ruleReqList.size(); j++)
			{
				//presentsl为1表示该条件是排除的。
				if (((SuperMarketPopRuleDef) ruleReqList.get(j)).presentsl == 1)
				{
					for (k = 0; k < saleGoods.size(); k++)
					{
						SaleGoodsDef sg = (SaleGoodsDef)saleGoods.get(k);
						//商品是否条件匹配
						if (sg.isPopExc == 'Y' && isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(j), k))
						{
							//将排除商品的标志置为N,表示不参与规则促销
							if (((SuperMarketPopRuleDef) ruleReqList.get(j)).istjn.charAt(0) == '0') //排除条件
							sg.isPopExc = ' ';
							else if (((SuperMarketPopRuleDef) ruleReqList.get(j)).istjn.charAt(0) == '1') //排除结果
							sg.isPopExc = 'N';
							else
							//条件和结果都排除
							{
								sg.isPopExc = ' ';
								sg.isSMPopCalced = 'A';
							}
						}
					}
					//如果是排除条件。那么无论结果是排除条件还是排除结果都不纳入条件计算 
					//例如,单据是一行类别的条件和一行排除结果的条件，如果此处不删除排除结果的那一行数据，
					//并且输入的商品中没有买这个排除结果的商品，会导致后面计算AND条件的时候算出倍数为0的情况。
					//如果是排除条件，则需将条件从条件列表中删除，这样是为了后面算多级
					ruleReqList.remove(j);
					j--;
				}
			}
			
			minbs = 0;
			zje = 0;
			
			for (n = 0;n < ((SuperMarketPopRuleDef) ruleReqList.get(0)).jc;n++)
			{
				t_minbs = 0;
				t_zje = 0;

				//得到当前级次
				getCurRuleJc(n+1);

				//匹配规则条件中属于必须满足的条件
				for ( l= 0, j = 0; j < ruleReqList.size(); j++)
				{
					SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef)ruleReqList.get(j);
					//条件为AND
					if (ruleReq.presentjs == 1)
					{
						l++;
						je = 0;
						for (k = 0;k < saleGoods.size(); k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef)saleGoods.get(k);
							//商品是否条件匹配
							if ((sg.isPopExc == 'Y' || sg.isPopExc == 'N') && isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(j),k))
							{
								//yhhyj = 0，表示yhlsj中记录的是数量
								//yhhyj = 1，表示yhlsj中记录的是金额
								if (ruleReq.yhhyj == 0)
									je += sg.sl;
								else
									je += sg.hjje - sg.yhzke - sg.hyzke - sg.plzke;
//								此处如果该变cominfo[k].infostr1[5] = 'A',在计算第2级别的时候,程序不能进入上面的IF条件进行统计，导致无法计算一级以上的级别
//								避免后面的or判断时又找到该条件
							}
						}

						bs = 0;
						if (ManipulatePrecision.doubleCompare(je, ruleReq.yhlsj, 2) >= 0 && ManipulatePrecision.doubleCompare(ruleReq.yhlsj, 0, 2) >= 0)
						{
							bs = new Double(je / ruleReq.yhlsj).longValue();			
						}
						if (l == 1)
							t_minbs = bs;
						else
							t_minbs = t_minbs>bs?bs:t_minbs;

						t_zje += je;
					}
				}
				//有必须全满足的条件，并且未全满足时，则认为条件不满足
				if (l > 0 && t_minbs <= 0)
				{
					//还原上一级
					if (n > 0) getCurRuleJc(n);
					break;
				}
				//匹配规则条件中属于非必须满足的条件
				for (je = 0, m = -1, j = 0; j < ruleReqList.size(); j++)
				{
					SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef)ruleReqList.get(j);
					//条件为OR
					if (ruleReq.presentjs == 0)
					{
						m = j;
						for (k = 0; k < saleGoods.size(); k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef)saleGoods.get(k);
							//商品是否条件匹配
							if ((sg.isPopExc == 'Y' || sg.isPopExc == 'N') && isMatchCommod(ruleReq, k))
							{
								//yhhyj = 0，表示yhlsj中记录的是数量
								//yhhyj = 1，表示yhlsj中记录的是金额
								if (ManipulatePrecision.doubleCompare(ruleReq.yhhyj, 0, 2) == 0)
									je += sg.sl;
								else
									je += sg.hjje - sg.yhzke - sg.hyzke - sg.plzke;
							}
						}
					}
				}	
				t_zje += je;

				//计算or条件的倍数
				
				if (m >= 0)
				{
					SuperMarketPopRuleDef ruleReqM = (SuperMarketPopRuleDef)ruleReqList.get(m);
					if (ManipulatePrecision.doubleCompare(je, ruleReqM.yhlsj, 2) >= 0 && ManipulatePrecision.doubleCompare(ruleReqM.yhlsj, 0, 2) > 0); 
					{
						bs = new Double(je / ruleReqM.yhlsj).longValue();
						if (l > 0)
							t_minbs = t_minbs>bs?bs:t_minbs;
						else
							t_minbs = bs;
					}
				}
				if (t_minbs > 0)
				{
					minbs = t_minbs;
					zje = t_zje;
				}
				else
				{
					//还原上一级
					if (n > 0) getCurRuleJc(n);
					break;
				}
			}
			
			//有必须全满足的条件，并且未全满足时，则认为条件不满足
			if (minbs <= 0)
			{
				//记录下不匹配的单据号，以便后面的商品再找到该单据时不用再次进行匹配运算
				notRuleDjbh.add(ruleDef.djbh);
				continue;
			}
			else
			{
			}		
			//ppistr6中的第1个字符为1时，表示1倍封顶
			if (((SuperMarketPopRuleDef)ruleReqList.get(0)).ppistr5.charAt(0) == '1') minbs = 1;

			//计算促销的结果
			for (j = 0; j < rulePopList.size(); j++)
			{
				SuperMarketPopRuleDef rulePop = (SuperMarketPopRuleDef)rulePopList.get(j);
				//商品优惠 商品优惠对应 一级商品 也对应多级的商品
				if (rulePop.yhdjlb == 'A' || rulePop.yhdjlb == 'E')
				{
					double yhsl = minbs * rulePop.yhhyj;//优惠数量
//					double yhje = ManipulatePrecision.doubleConvert(minbs * rulePop.yhlsj, 2, 0);

					//结果为OR关系的时候 按照第一个结果的优惠数量来优惠剩余商品
					if (rulePop.presentjs == 0)
					{
						if (j == 0)
							or_yhsl = yhsl;
						else
							yhsl = or_yhsl;
					}

					for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(yhsl, 0, 4) > 0; k++)
					{
						SaleGoodsDef sg = (SaleGoodsDef)saleGoods.get(k);
						//商品是否结果匹配 排除结果
						if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
						{
							//商品拆分
							if (ManipulatePrecision.doubleCompare(sg.sl, yhsl, 4) <= 0)
							{
								yhsl -= sg.sl;
								or_yhsl -= sg.sl;
							}
							else
							{
								//拆分商品行
								splitSalecommod(k,yhsl);
								yhsl = 0;
								or_yhsl = 0;
							}
							double misszke = 0;
							//整单满减
							if (ruleDef.type == '8')
							{
								misszke = sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;//记录可能被清零的折扣额
							}
							else
							{
								misszke = sg.yhzke + sg.hyzke + sg.plzke;//记录可能被清零的折扣额
							}
							//取价方式判断
							if (rulePop.presentjg == 0)//取价方式 取价格
							{
								//如果是折上折，那么折后金额 = 一般优惠后的金额 * 现在的规则定价 /商品本身的价格
								if (rulePop.iszsz.charAt(0) == '1')
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0) - ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2,0);
										sg.mjzke = calcComZkxe(k,sg.mjzke);

										sg.mjdjbh = rulePop.djbh;
										sg.mjzkfd = rulePop.zkfd;
										//统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.mjzke;	
									}
									else
									{
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0) - ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
										sg.rulezke = calcComZkxe(k,sg.rulezke);

										sg.ruledjbh = rulePop.djbh;
										sg.rulezkfd = rulePop.zkfd;
										//统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.rulezke;	
									}
								}
								else
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke, 2, 0) - ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
										if (sg.mjzke > misszke)//如果规则优惠大于一般优惠，一般优惠清零
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd =0;
											sg.yhzkfd =0;
											sg.rulezke =0;
											sg.rulezkfd =0;
											sg.yhdjbh = "";
											sg.ruledjbh = "";
											sg.mjdjbh = rulePop.djbh;
											sg.mjzke = calcComZkxe(k,sg.mjzke);
											sg.mjzkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.mjzke - misszke;	
										}
										else
										{
											sg.mjzke = 0;
										}
									}
									else
									{
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + sg.yhzke + sg.plzke + sg.hyzke, 2, 0) - ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);

										if (sg.rulezke > misszke)//如果规则优惠大于一般优惠，一般优惠清零
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd =0;
											sg.yhzkfd =0;
											sg.yhdjbh = "";
											sg.ruledjbh = rulePop.djbh;
											sg.rulezke = calcComZkxe(k,sg.rulezke);
											sg.rulezkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.rulezke-misszke;	
										}
										else
										{
											sg.rulezke = 0;
										}
									}
								}
							}
							else if (rulePop.presentjg == 1)//取折扣率
							{
								if (rulePop.iszsz.charAt(0) == '1')
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = 0;
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)-ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg))*rulePop.yhlsj, 2, 0);
										sg.mjzke = calcComZkxe(k,sg.mjzke);

										sg.mjdjbh = rulePop.djbh;
										sg.mjzkfd = rulePop.zkfd;
										//统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.mjzke;
									}
									else
									{
										sg.rulezke = 0;
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg),2, 0)-ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg)) * rulePop.yhlsj, 2, 0);
										sg.rulezke = calcComZkxe(k,sg.rulezke);
										sg.ruledjbh = rulePop.djbh;
										sg.rulezkfd = rulePop.zkfd;
										//统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.rulezke;	
									}
								}
								else
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = 0;
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0)-ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg) + misszke)* rulePop.yhlsj, 2, 0);

										if (sg.mjzke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd =0;
											sg.yhzkfd =0;//清除普通优惠和会员优惠
											sg.rulezke = 0;
											sg.rulezkfd =0;
											sg.yhdjbh = "";
											sg.ruledjbh = "";
											sg.mjzke = calcComZkxe(k,sg.mjzke);
											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.mjzke - misszke;		
										}
										else
										{
											sg.mjzke = 0;
										}
									}
									else
									{
										sg.rulezke = 0;
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0) - ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg) + misszke) * rulePop.yhlsj, 2, 0);

										if (sg.rulezke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd =0;
											sg.yhzkfd =0;//清除普通优惠和会员优惠
											sg.yhdjbh = "";
											sg.rulezke = calcComZkxe(k,sg.rulezke);
											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.rulezke-misszke;		
										}
										else
										{
											sg.rulezke = 0;
										}
									}				
								}
							}
							else if (rulePop.presentjg == 2)//取折扣额
							{
								if (rulePop.iszsz.charAt(0) == '1')
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke  = rulePop.yhlsj;
										sg.mjzke = calcComZkxe(k,sg.mjzke);

										sg.mjdjbh = rulePop.djbh;
										sg.mjzkfd = rulePop.zkfd;
										superMarketRuleyhje += sg.mjzke;//统计规则促销的折扣金额
									}
									else
									{	
										sg.rulezke  = rulePop.yhlsj;
										sg.rulezke = calcComZkxe(k,sg.rulezke);

										sg.ruledjbh = rulePop.djbh;
										sg.rulezkfd = rulePop.zkfd;
										superMarketRuleyhje += sg.rulezke;//统计规则促销的折扣金额
									}
								}
								else
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke  = rulePop.yhlsj;

										if (sg.mjzke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd =0;
											sg.yhzkfd =0;//清除普通优惠和会员优惠
											sg.rulezke = 0;
											sg.rulezkfd =0;
											
											sg.ruledjbh = "";
											sg.yhdjbh = "";
											sg.mjzke = calcComZkxe(k,sg.mjzke);
											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											superMarketRuleyhje += sg.mjzke - misszke;//统计规则促销的折扣金额	
										}
										else
										{
											sg.mjzke = 0;
										}
									}
									else
									{
										sg.rulezke  = rulePop.yhlsj;

										if (sg.rulezke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd =0;
											sg.yhzkfd =0;//清除普通优惠和会员优惠
											sg.yhdjbh = "";
											sg.rulezke = calcComZkxe(k,sg.rulezke);
											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											superMarketRuleyhje += sg.rulezke - misszke;//统计规则促销的折扣金额	
										}
										else
										{
											sg.rulezke = 0;
										}
									}
								}
							}
							else//用于其它用途
							{
							}
							sg.isPopExc = 'Y';
						}
					}
				}
				//赠品
				if (rulePop.yhdjlb == 'B' || rulePop.yhdjlb == 'F')
				{
					//'4'表示买赠，该赠品是小票列表中的正常商品，要将其改成正赠品
					if (rulePop.ppistr6.charAt(0) == '4')
					{
						//赠品数量
						quantity = minbs * rulePop.yhlsj;

						for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(quantity,0,4)>0;k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef)saleGoods.get(k);
							//商品是否结果匹配  排除结果
							if (isMatchCommod(rulePop, k)  && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
							{
								//商品拆分
								if (ManipulatePrecision.doubleCompare(sg.sl,quantity,4) <= 0)
								{
									quantity -= sg.sl;
								}
								else
								{
									//拆分商品行
									splitSalecommod(k,quantity);
									quantity = 0;
								}
								//将该商品改为赠品
								sg.flag = '1';
								sg.batch = rulePop.ppistr6;
								
								sg.xxtax = ManipulatePrecision.doubleConvert(minbs * rulePop.yhhyj, 2, 0);
								sg.rulezke += sg.hjje - getZZK(sg) - ManipulatePrecision.doubleConvert(minbs * rulePop.yhhyj, 2, 0);

								sg.ruledjbh = rulePop.djbh;  //记录优惠单据编号
								sg.rulezkfd = rulePop.zkfd;

								//该商品的应收金额都记为优惠金额
								superMarketRuleyhje += sg.zszke;
							}
						}
					}
				}
				//任意几个定应收金额 只分多级
				if (rulePop.yhdjlb == 'X')
				{
					if (ManipulatePrecision.doubleCompare(zje,((SuperMarketPopRuleDef)ruleReqList.get(0)).yhlsj,2) >= 0 && ManipulatePrecision.doubleCompare(((SuperMarketPopRuleDef)ruleReqList.get(0)).yhlsj,0,2) > 0)
					{
						double yhje = ManipulatePrecision.doubleConvert(minbs * rulePop.yhlsj, 2, 0);
						long yhsl = new Double(minbs * ((SuperMarketPopRuleDef)ruleReqList.get(0)).yhlsj).longValue();
						double t_zyhje=0;

						t_zje = 0;
						t_zyhje = 0;

						//yhje应小于小票的应收金额，不然话，小票金额有成负数的可能。
						if (ManipulatePrecision.doubleCompare(yhje,saleHead.ysje,2) < 0)
						{
							int t_maxjerow = -1;
							long t_yhsl = yhsl;
							//计算本次参与优惠商品的总金额
							for (l = 0; l < ruleReqList.size(); l++)
							{
								SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef)ruleReqList.get(l);
								for (k=0; k < saleGoods.size(); k++)
								{
									SaleGoodsDef sg = (SaleGoodsDef)saleGoods.get(k);
									if (t_yhsl > 0 && isMatchCommod(ruleReq, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
									{
										//商品拆分
										if (ManipulatePrecision.doubleCompare(sg.sl,t_yhsl,4) <= 0)
										{
											t_yhsl -= (long)sg.sl;
										}
										else
										{
											//拆分商品行
											splitSalecommod(k,t_yhsl);
											t_yhsl = 0;
										}
										//如果是折上折
										if (rulePop.iszsz.charAt(0) == '1')
										{
											t_zje += sg.hjje - getZZK(sg);
										}
										else
										{
											if (ruleDef.type == '8')
											{	
												t_zje += sg.hjje - getZZK(sg) + sg.yhzke+sg.hyzke+sg.plzke+sg.rulezke;
												t_zyhje +=sg.yhzke+sg.hyzke+sg.plzke+sg.rulezke;
											}
											else
											{	
												t_zje += sg.hjje - getZZK(sg) + sg.yhzke+sg.plzke+sg.hyzke;
												t_zyhje +=sg.yhzke+sg.hyzke+sg.plzke;
											}
										}
									}
								}
							}

							//计算出优惠金额
							yhje = t_zje - yhje;

							double t_je=0;
							//将优惠金额按金额占比分摊到本次参与的商品上面
							for (l = 0; l < ruleReqList.size(); l++)
							{
								SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef)ruleReqList.get(l);
								for (k = 0; k < saleGoods.size(); k++)
								{
									SaleGoodsDef sg = (SaleGoodsDef)saleGoods.get(k);
									if (yhsl > 0 && isMatchCommod(ruleReq ,k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
									{
										//商品拆分
										if (ManipulatePrecision.doubleCompare(sg.sl,yhsl,4) <= 0)
										{
											yhsl -= sg.sl;
										}
										else
										{
											//拆分商品行
											splitSalecommod(k,yhsl);
											yhsl = 0;
										}
										double misszke = 0;
										if (ruleDef.type == '8')
										{
											misszke = sg.yhzke + sg.yhzke + sg.plzke + sg.rulezke;
										}
										else
										{
											misszke = sg.yhzke + sg.yhzke + sg.plzke;
										}
										//根据折上折来判断，如果非折上折，取低价优先
										if (rulePop.iszsz.charAt(0) == '1')
										{
											if (ruleDef.type == '8')
											{
												sg.mjzke = 0;
												sg.mjzke =(sg.hjje - getZZK(sg)) / t_zje * yhje;
												calcComZkxe(k, sg.mjzke);
												//记录折扣分担
												sg.mjzkfd = rulePop.zkfd;
												//记录优惠单据编号
												sg.mjdjbh = rulePop.djbh;
												//统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.mjzke;

											}
											else
											{
												sg.rulezke = 0;
												sg.rulezke =(sg.hjje - getZZK(sg))/t_zje * yhje;
												calcComZkxe(k,sg.rulezke);
												//记录折扣分担
												sg.rulezkfd = rulePop.zkfd;
												//记录优惠单据编号
												sg.ruledjbh = rulePop.djbh;
												//统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.rulezke;
											}
										}
										else 
										{
											if (yhje > t_zyhje)
											{	
												if (ruleDef.type == '8')
												{	
													sg.mjzke = 0;
													sg.mjzke=(sg.hjje - getZZK(sg) + misszke) / t_zje * yhje;

													sg.yhzke = 0;
													sg.hyzke = 0;
													sg.plzke = 0;
													sg.spzkfd =0;
													sg.yhzkfd =0;
													sg.rulezke =0;
													sg.rulezkfd =0;
													sg.yhdjbh = "";
													sg.ruledjbh = "";
													calcComZkxe(k, sg.mjzke);
													//记录折扣分担
													sg.mjzkfd = rulePop.zkfd;
													//记录优惠单据编号
													sg.ruledjbh = rulePop.djbh;
													//统计当前规则促销的折扣金额
													superMarketRuleyhje += sg.mjzke - misszke;

												}
												else
												{
													sg.rulezke =0;
													sg.rulezke = (sg.hjje - getZZK(sg) + misszke) / t_zje * yhje;
													sg.yhzke = 0;
													sg.hyzke = 0;
													sg.plzke = 0;
													sg.spzkfd =0;
													sg.yhzkfd =0;
													sg.yhdjbh = "";
													calcComZkxe(k, sg.rulezke);
													//记录折扣分担
													sg.rulezkfd = rulePop.zkfd;
													//记录优惠单据编号
													sg.ruledjbh = rulePop.djbh;
													//统计当前规则促销的折扣金额
													superMarketRuleyhje += sg.rulezke - misszke;
												}
											}
											else
											{
												t_je = yhje;
											}
										}
										// 将参与优惠的商品打上标记
										sg.isPopExc = 'Y';
										if (ruleDef.type == '8')
										{
											t_je += sg.mjzke;
										}
										else
										{	
											t_je += sg.rulezke;
										}
										
										//记下金额最大的行号
										if (t_maxjerow >= 0 && ManipulatePrecision.doubleCompare(sg.hjje, ((SaleGoodsDef)saleGoods.get(t_maxjerow)).hjje, 2) > 0 || t_maxjerow < 0 )
											t_maxjerow = k;
									}
								}
								if (yhsl <= 0) break;
							}
							if (ManipulatePrecision.doubleCompare(Math.abs(yhje-t_je),0,2) > 0)
							{
								if (ruleDef.type == '8')
								{
									((SaleGoodsDef)saleGoods.get(t_maxjerow)).mjzke += yhje - t_je;
									calcComZkxe(t_maxjerow,((SaleGoodsDef)saleGoods.get(t_maxjerow)).mjzke);
								}
								else
								{
									((SaleGoodsDef)saleGoods.get(t_maxjerow)).rulezke += yhje - t_je;
									calcComZkxe(t_maxjerow,((SaleGoodsDef)saleGoods.get(t_maxjerow)).rulezke);
								}
								superMarketRuleyhje += yhje - t_je;
								
//								if (t_maxjerow >= winfirst && t_maxjerow < winfirst + PAGE_SALE_COM - 1) DispOneSaleCommod(t_maxjerow - winfirst,t_maxjerow);
//								//显示汇总信息
//								DispPay();
							}
						}
					}
					//任意多级的，只判断一个结果
					break;
				}
				//数量促销 数量促销没有折上折(取的单价)，没有分级，取单价
				if (rulePop.yhdjlb == 'N')
				{
					//flag = 1 是全量优惠
					//flag = 2 是超量促销
					//flag = 3 是第n件促销
					//flag = 4 是整箱促销
					long flag = new Double(rulePop.yhhyj).longValue();
					//统计本单参与优惠的商品数量
					double kyhsl = 0;
					for (k = 0; k < calcCount; k++)
					{
						SaleGoodsDef sg = (SaleGoodsDef)saleGoods.get(k);
						//条件排除的商品应该参与结果计算
						if (/*cominfo[k].infostr1[5] != ' ' && */isMatchCommod(rulePop ,k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
						{
							kyhsl += sg.sl;
							//如果前一个商品已经满足了折扣,
							//那么第二个商品必须标示为已经参与促销（此处为标示其已经促销的条件）。
						}
					}

					//优惠数量
					double yhsl = Double.parseDouble(rulePop.ppistr3);
					if (ManipulatePrecision.doubleCompare(yhsl, 0, 2) <= 0) yhsl = 1;//防止后面计算时除0错误

					//超量促销
					if (flag == 2) kyhsl -= yhsl;
					//第n件促销
					if (flag == 3) kyhsl = new Double(kyhsl/yhsl).longValue();
					//整箱促销
					if (flag == 4)
					{
						minbs = new Double(kyhsl/yhsl).longValue();
						long zyhsl = new Double(minbs*yhsl).longValue();//整箱总的优惠数量
						kyhsl = kyhsl > zyhsl?zyhsl : kyhsl;
					}

					//开始计算优惠
					if (ManipulatePrecision.doubleCompare(kyhsl, 0, 2) > 0)
					{
						for (k = 0; k < calcCount; k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef)saleGoods.get(k);
							if (kyhsl > 0 && isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
							{
								//优惠价低于零售价
								if (sg.lsj > rulePop.yhlsj && rulePop.yhlsj > 0)
								{
									if (kyhsl >= sg.sl)
									{
										kyhsl -= sg.sl;
									}
									else
									{
										//拆分商品行
										splitSalecommod(k,kyhsl);
										kyhsl = -1;
									}
									sg.rulezke =0;
									double misszke = sg.yhzke + sg.hyzke+ sg.plzke;
									sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg)+ misszke, 2, 0) - ManipulatePrecision.doubleConvert(rulePop.yhlsj * sg.sl, 2, 0);

									//优惠价低于一般促销
									if (ManipulatePrecision.doubleCompare(sg.rulezke,misszke,2) > 0)
									{
										sg.yhzke = 0;
										sg.hyzke = 0;
										sg.plzke = 0;
										sg.spzkfd =0;
										sg.yhzkfd =0;//清除普通优惠和会员优惠
										sg.yhdjbh = "";
										sg.rulezkfd = rulePop.zkfd;
										calcComZkxe(k, sg.rulezke);
										//记录优惠单据编号
										sg.ruledjbh = rulePop.djbh;
										superMarketRuleyhje += sg.rulezke - misszke;

//										if (k >= winfirst && k < winfirst + PAGE_SALE_COM - 1) DispOneSaleCommod(k - winfirst,k);
//
//										//显示汇总信息
//										DispPay();

										//参与满减促销的标志
										sg.isPopExc = 'Y';
									}
									else sg.rulezke = 0;
								}
							}
						}
					}
				}
				//任意几个定单价 只分多级
				if (rulePop.yhdjlb == 'Z')
				{
					if (ManipulatePrecision.doubleCompare(zje, ((SuperMarketPopRuleDef)ruleReqList.get(0)).yhlsj, 2) >= 0 && ManipulatePrecision.doubleCompare(((SuperMarketPopRuleDef)ruleReqList.get(0)).yhlsj, 0, 2) > 0)
					{
						//参与优惠的商品明细数量
						long yhsl = new Double(minbs * ((SuperMarketPopRuleDef)ruleReqList.get(0)).yhlsj).longValue();

						for (l = 0; l < ruleReqList.size(); l++)	
						{
							SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef)ruleReqList.get(l);
							for (k = 0; k < saleGoods.size(); k++)
							{
								SaleGoodsDef sg = (SaleGoodsDef)saleGoods.get(k);
								if (yhsl > 0 && isMatchCommod(ruleReq, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A')) 
								{
									//商品拆分
									if (ManipulatePrecision.doubleCompare(sg.sl,yhsl,4) <= 0)
									{
										yhsl -= sg.sl;
									}
									else
									{
										//拆分商品行
										splitSalecommod(k,yhsl);

										yhsl = 0;
									}
									double misszke = 0 ;
									if (i == calcCount + 1)
										misszke = sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;//记录可能被清零的折扣额
									else
										misszke = sg.yhzke + sg.hyzke + sg.plzke;//记录可能被清零的折扣额

									if (rulePop.iszsz.charAt(0) == '1')
									{
										if (i == calcCount + 1)
										{	
											sg.mjzke = 0;
											sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0) - ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
											calcComZkxe(k, sg.mjzke);
											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.mjzke;		
										}
										else
										{
											sg.rulezke = 0;
											sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0) - ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);										
											calcComZkxe(k, sg.rulezke);
											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.rulezke;		
										}
									}
									else
									{
										if (i == calcCount + 1)
										{	
											sg.mjzke = 0;
											sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0) - ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);

											if (sg.mjzke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
											{
												sg.yhzke = 0;
												sg.hyzke = 0;
												sg.plzke = 0;
												sg.spzkfd =0;
												sg.yhzkfd =0;
												sg.rulezke = 0;
												sg.rulezkfd =0;
												sg.ruledjbh = "";
												sg.yhdjbh = "";
												calcComZkxe(k, sg.mjzke);
												sg.mjdjbh = rulePop.djbh;
												sg.mjzkfd =rulePop.zkfd;
												//统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.mjzke - misszke;
											}
											else
											{
												sg.mjzke = 0;
											}
										}
										else
										{
											sg.rulezke = 0;
											sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0) - ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);

											if (sg.rulezke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
											{
												sg.yhzke = 0;
												sg.hyzke = 0;
												sg.plzke = 0;
												sg.spzkfd =0;
												sg.yhzkfd =0;
												sg.yhdjbh = "";
												sg.ruledjbh = rulePop.djbh;
												calcComZkxe(k, sg.rulezke);
												sg.rulezkfd =rulePop.zkfd;
												//统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.rulezke - misszke;
											}
											else
											{
												sg.rulezke = 0;
											}
										}
									}
									sg.isPopExc = 'Y';

//									if (k >= winfirst && k < winfirst + PAGE_SALE_COM - 1) DispOneSaleCommod(k - winfirst,k);
//
//									//显示汇总信息
//									DispPay();
								}
							}
							if (yhsl <= 0) break;
						}
					}
					//任意多级的，只判断一个结果
					break;
				}
				//对指定商品固定优惠金额
				if (rulePop.yhdjlb == 'V')
				{
					double yhsl = minbs * rulePop.yhhyj;//优惠数量
					double yhje = ManipulatePrecision.doubleConvert(minbs * rulePop.yhlsj, 2, 0);

					//结果为OR关系的时候 按照第一个结果的优惠数量来优惠剩余商品
					long and_flag = new Double(rulePop.presentjs).longValue();

					or_yhsl = yhsl;
					zje = 0;
					t_zje = 0;

					while (and_flag == 1 || and_flag == 0 && ManipulatePrecision.doubleCompare(or_yhsl,0,4) > 0 && j < rulePopList.size())
					{
						//统计参与优惠金额分配的商品总金额
						for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(or_yhsl,0,4) > 0; k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef)saleGoods.get(k);
							//商品是否结果匹配
							if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
							{
								//商品拆分
								if (ManipulatePrecision.doubleCompare(sg.sl,or_yhsl,4) <= 0)
								{
									or_yhsl -= sg.sl;
								}
								else
								{
									//拆分商品行
									splitSalecommod(k,or_yhsl);
									or_yhsl = 0;
								}
								zje += sg.hjje;
								t_zje += sg.hjje - getZZK(sg);
							}
						}
						if (and_flag == 1) break;
						j++;
					}
					if (ManipulatePrecision.doubleCompare(yhje,t_zje,2) > 0) yhje = t_zje;

					//将优惠金额分担到商品明细上
					if (ManipulatePrecision.doubleCompare(zje,0,2) > 0 && ManipulatePrecision.doubleCompare(yhje,0,2) > 0 && ManipulatePrecision.doubleCompare(zje - yhje,0,2) >= 0)
					{
						int maxrow = -1;
						j = 0;
						t_zje = 0;
						while (and_flag == 1 || and_flag == 0 && ManipulatePrecision.doubleCompare(yhsl,0,4) > 0 && j < rulePopList.size())
						{
							for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(yhsl,0,4) > 0; k++)
							{
								SaleGoodsDef sg = (SaleGoodsDef)saleGoods.get(k);
								//商品是否结果匹配
								if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
								{
									yhsl -= sg.sl;

									je = ManipulatePrecision.doubleConvert(sg.hjje/zje*yhje, 2, 0);
									if (ruleDef.type == '8')
									{
										sg.mjzke += je;
									}
									else
									{
										sg.rulezke += je;
									}
									//记录折扣分担
									sg.rulezkfd = rulePop.zkfd;
									//记录优惠单据编号
									sg.ruledjbh = rulePop.djbh;
									sg.isPopExc = 'Y';

									superMarketRuleyhje += je;
									t_zje += je;
//									if (k >= winfirst && k < winfirst + PAGE_SALE_COM - 1) DispOneSaleCommod(k - winfirst,k);
//									//显示汇总信息
//									DispPay();

									//记下金额最大的行号
									if (maxrow >= 0 && ManipulatePrecision.doubleCompare(sg.hjje, ((SaleGoodsDef)saleGoods.get(maxrow)).hjje,2) > 0 || maxrow < 0 )
										maxrow = k;
								}
							}
							if (and_flag == 1) break;
							j++;
						}
						//将未分配完的金额分配到金额最大的商品上
						if (ManipulatePrecision.doubleCompare(Math.abs(yhje - t_zje),0,2) > 0)
						{
							k = maxrow;
							SaleGoodsDef sg = (SaleGoodsDef)saleGoods.get(k);
							if (ruleDef.type == '8')
							{
								sg.mjzke += yhje - t_zje;
							}
							else
							{
								sg.rulezke += yhje - t_zje;
							}
							superMarketRuleyhje += yhje - t_zje;
//							if (k >= winfirst && k < winfirst + PAGE_SALE_COM - 1) DispOneSaleCommod(k - winfirst,k);
//							//显示汇总信息
//							DispPay();
						}
					}
					//或条件时，已经优惠完了，所以要退出
					if (and_flag == 0) break;
				}
			}
			
			//将已参与规则促销的商品打上标志
			for (k = 0; k < saleGoods.size(); k++)
			{
				SaleGoodsDef sg = (SaleGoodsDef)saleGoods.get(k);
				//商品是否条件匹配
				if (sg.isPopExc != ' ')
				{
					sg.isSMPopCalced = 'N';
					sg.isPopExc = ' ';
				}
			}
			//类别为8表示整单满减
			if (ruleDef.type == '8') break;
		}
		/*
		//打印汇总的折扣额
		if (fabs(ruleyhje) > 0 && GSysSyj.printfs == '1' && (!ISLXSALE(saletype) || GSysPara.lxprint == 'Y'))
		{
			//将最后一个商品打印出来
			if (printflag[old_salecom_num-1]!= 'Y')
			{
				PrintSaleLocal(PRT_SECTION_COMMOD,old_salecom_num-1);   //打印商品明细
				printflag[old_salecom_num-1] = 'Y';
			}
			char prtbuf[MAX_PAPER_WIDTH+1];
			sprintf(prtbuf,"以上商品优惠 %8.2f",ruleyhje);
			PrinterDraft(prtbuf);
		}
		*/
		for (k = 0; k < saleGoods.size(); k++)
		{
			getZZK((SaleGoodsDef)saleGoods.get(k));
		}
		
		// 重算应收
		calcHeadYsje();

		// 刷新商品列表
		saleEvent.updateTable(getSaleGoodsDisplay());
		saleEvent.setTotalInfo();
		return true;
	}

	protected boolean splitSalecommod(int n,double newsl)
	{
		SaleGoodsDef sg = (SaleGoodsDef)saleGoods.get(n);
		GoodsDef goods = (GoodsDef)goodsAssistant.get(n);
		SpareInfoDef spare = (SpareInfoDef)goodsSpare.get(n);
		GoodsPopDef goodsPop = (GoodsPopDef)crmPop.get(n);
		
		if (saleGoods.size() <= 0 || n < 0 || n >= saleGoods.size()) return false;
		if (newsl >= sg.sl) return false;

		SaleGoodsDef newsg = (SaleGoodsDef)sg.clone();
		GoodsDef newGoods = (GoodsDef)goods.clone();
		SpareInfoDef newSpare = (SpareInfoDef)spare.clone();
		GoodsPopDef newGoodsPop = (GoodsPopDef)goodsPop.clone();

		double zje = sg.hjje;
		double rulezke = sg.rulezke;
		double mjzke = sg.mjzke;

		//重算金额
		sg.sl = ManipulatePrecision.doubleConvert(newsl,4,1);
		sg.hjje = ManipulatePrecision.doubleConvert(newsg.hjje / newsg.sl * sg.sl,2,1);
        sg.hyzke = ManipulatePrecision.doubleConvert(newsg.hyzke / newsg.sl * sg.sl,2,1);
        sg.yhzke = ManipulatePrecision.doubleConvert(newsg.yhzke / newsg.sl * sg.sl,2,1);
        sg.plzke = ManipulatePrecision.doubleConvert(newsg.plzke / newsg.sl * sg.sl,2,1);
        sg.zszke = ManipulatePrecision.doubleConvert(newsg.zszke / newsg.sl * sg.sl,2,1);
        sg.hyzklje = ManipulatePrecision.doubleConvert(newsg.hyzklje / newsg.sl * sg.sl,2,1);			        
        sg.lszke = ManipulatePrecision.doubleConvert(newsg.lszke / newsg.sl * sg.sl,2,1);
        sg.lszre = ManipulatePrecision.doubleConvert(newsg.lszre / newsg.sl * sg.sl,2,1);
        sg.lszzk = ManipulatePrecision.doubleConvert(newsg.lszzk / newsg.sl * sg.sl,2,1);
        sg.lszzr = ManipulatePrecision.doubleConvert(newsg.lszzr / newsg.sl * sg.sl,2,1);
        sg.cjzke = ManipulatePrecision.doubleConvert(newsg.cjzke / newsg.sl * sg.sl,2,1);
        sg.ltzke = ManipulatePrecision.doubleConvert(newsg.ltzke / newsg.sl * sg.sl,2,1);
        sg.qtzke = ManipulatePrecision.doubleConvert(newsg.qtzke / newsg.sl * sg.sl,2,1);
        sg.qtzre = ManipulatePrecision.doubleConvert(newsg.qtzre / newsg.sl * sg.sl,2,1);
        
        newsg.sl = ManipulatePrecision.doubleConvert(newsg.sl - sg.sl,4,1);
        newsg.hjje = ManipulatePrecision.doubleConvert(newsg.hjje - sg.hjje ,2,1);
        newsg.hyzke = ManipulatePrecision.doubleConvert(newsg.hyzke - sg.hyzke,2,1);
        newsg.yhzke = ManipulatePrecision.doubleConvert(newsg.yhzke - sg.yhzke,2,1);
        newsg.plzke = ManipulatePrecision.doubleConvert(newsg.plzke - sg.plzke,2,1);
        newsg.zszke = ManipulatePrecision.doubleConvert(newsg.zszke - sg.zszke,2,1);
        newsg.hyzklje = ManipulatePrecision.doubleConvert(newsg.hyzklje - sg.hyzklje,2,1);			        
        newsg.lszke = ManipulatePrecision.doubleConvert(newsg.lszke - sg.lszke,2,1);
        newsg.lszre = ManipulatePrecision.doubleConvert(newsg.lszre - sg.lszre,2,1);
        newsg.lszzk = ManipulatePrecision.doubleConvert(newsg.lszzk - sg.lszzk,2,1);
        newsg.lszzr = ManipulatePrecision.doubleConvert(newsg.lszzr - sg.lszzr,2,1);
        newsg.cjzke = ManipulatePrecision.doubleConvert(newsg.cjzke - sg.cjzke,2,1);
        newsg.ltzke = ManipulatePrecision.doubleConvert(newsg.ltzke - sg.ltzke,2,1);
        newsg.qtzke = ManipulatePrecision.doubleConvert(newsg.qtzke - sg.qtzke,2,1);
        newsg.qtzre = ManipulatePrecision.doubleConvert(newsg.qtzre - sg.qtzre,2,1);

		//将拆分的商品的规则促销折扣金额进行分摊，此处必须分摊，不然会导致在计算整单的时候，出现成交金额为负数的情况
		sg.rulezke = (sg.hjje / zje) * rulezke;	
		newsg.rulezke = (newsg.hjje / zje) * rulezke; 
		sg.mjzke = (sg.hjje / zje) * mjzke;	
		newsg.mjzke = (newsg.hjje / zje) * mjzke;

		getZZK(sg);
		getZZK(newsg);
		saleGoods.add(newsg);
		goodsAssistant.add(newGoods);
		goodsSpare.add(newSpare);
		crmPop.add(newGoodsPop);
		/*
		//计算会员折扣和优惠折扣
		CalculateAllRebate(n);
		//批量销售折扣处理,重算优惠折扣和会员折扣
		CalculateBatchRebate(n);

		//计算会员折扣和优惠折扣
		CalculateAllRebate(salecom_num-1);
		//批量销售折扣处理,重算优惠折扣和会员折扣
		CalculateBatchRebate(salecom_num-1);

		//记录打印标志(打印标志取原行上的标志)
		printflag[salecom_num-1] = printflag[n];

		//	//记录断点
		//	WriteBroken(n,BROKEN_UPDATE);
		//	WriteBroken(salecom_num-1,BROKEN_APPEND);

		//刷新被拆分的原商品行
		if (n >= winfirst && n < winfirst + PAGE_SALE_COM - 1) DispOneSaleCommod(n - winfirst,n);

		//刷新商品列表
		DispSaleCommod();

		CreateInputLine(2);
*/
		// 刷新商品列表
		saleEvent.updateTable(getSaleGoodsDisplay());
		saleEvent.setTotalInfo();
		
		return true;
	}
	protected double calcComZkxe(int k, double zke)
	{
		if (k < 0 || k >= saleGoods.size()) return zke;

		SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
		if (ManipulatePrecision.doubleCompare(sg.hjje - getZZK(sg), 0, 2) <= 0)
		{
			zke = 0;//引用数据类型,将实参清零
			zke = sg.hjje - getZZK(sg);
		}
		//计算价格精度
		zke = getConvertPrice(zke, (GoodsDef) goodsAssistant.elementAt(k));
		return zke;
	}

	protected void getCurRuleJc(int jc)
	{
		int i;
		// 获得条件在jc传入值的级别所对应的级别值
		for (i = 0; i < ruleReqList.size(); i++)
		{
			SuperMarketPopRuleDef reqDef = (SuperMarketPopRuleDef) ruleReqList.get(i);
			double a = Double.parseDouble(reqDef.ppistr1.split("\\|")[jc - 1]);
			if (a > 0) reqDef.yhlsj = a;
			
		}
		for (i = 0; i < rulePopList.size(); i++)
		{
			SuperMarketPopRuleDef reqPop = (SuperMarketPopRuleDef) rulePopList.get(i);
			double a = Double.parseDouble(reqPop.ppistr1.split("\\|")[jc - 1]);
			if (a > 0) reqPop.yhlsj = a;
			double b = Double.parseDouble(reqPop.ppistr2.split("\\|")[jc - 1]);
			if (b > 0) reqPop.yhhyj = b;
		}
	}

	protected boolean isMatchCommod(SuperMarketPopRuleDef ruleDef, int index)
	{
		SaleGoodsDef sg = ((SaleGoodsDef) saleGoods.get(index));
		GoodsDef goodsDef = ((GoodsDef) goodsAssistant.get(index));

		// 整单的规则,整单优先级最高 
		if (ruleDef.type == '8') return true;

		//只有正常的商品才参与规则促销
		if (sg.flag != '4' && sg.flag != '2') { return false; }

		//如果电子称商品不是排除条件
		if (ruleDef.presentsl != 1 || sg.flag != '2')
		{
			//如果电子称商品条件不是满减/满返，结果也不是满减/满返 
			if (!(ruleDef.yhdjlb == '8' && (ruleDef.ppistr3.charAt(0) == 'G' || ruleDef.ppistr3.charAt(0) == 'C') || ruleDef.yhdjlb == 'G' || ruleDef.yhdjlb == 'C')
					&& sg.flag == '2') { return false; }
		}

		//条件为整单的时候如果是结果为非整单。此处就不判断商品是否参与了规则促销，
		//不然在结果匹配的时候会因为商品参与了非整单规则促销而无法参与整单的规则促销
		//在整单规则的时候初始化商品标识的时候使用
		if (((SuperMarketPopRuleDef) ruleReqList.get(0)).type != '8')
		{
			//不参与规则促销
			if (sg.isSMPopCalced != 'Y') { return false; }
		}

		switch (ruleDef.type)
		{
			case '1'://单品
				if (!ruleDef.code.equals(goodsDef.code)) break;
				if ((ruleDef.gz.equals(goodsDef.gz) || ruleDef.gz.equals("0")) && (ruleDef.spec.equals(goodsDef.uid) || ruleDef.spec.equals("AL")))
				{
					return true;
				}
				break;
			case '2'://柜组
				if (!ruleDef.code.equals(goodsDef.gz)) break;
				return true;
			case '3'://类别
				if (!ruleDef.code.equals(Convert.increaseChar(goodsDef.catid,ruleDef.code.length()).substring(0, ruleDef.code.length()))) break;
				return true;
			case '4'://柜组品牌
				if (!ruleDef.code.equals(goodsDef.gz)) break;
				if (ruleDef.pp.equals(goodsDef.ppcode))
				{
					return true;
				}
				break;
			case '5'://类别品牌
				if (!ruleDef.code.equals(Convert.increaseChar(goodsDef.catid,ruleDef.code.length()).substring(0, ruleDef.code.length()))) break;
				if (ruleDef.pp.equals(goodsDef.ppcode))
				{
					return true;
				}
				break;
			case '6'://品牌
				if (!ruleDef.code.equals(goodsDef.ppcode)) break;
				return true;
			case '7'://生鲜单品
				if (!ruleDef.code.equals(goodsDef.code)) break;
				if ((ruleDef.gz.equals(goodsDef.gz) || ruleDef.gz.equals("0")) && (ruleDef.spec.equals(goodsDef.uid) || ruleDef.spec.equals("AL")))
				{
					return true;
				}
				break;
		}
		return false;
	}
}
