package custom.localize.Bhls;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsAmountDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;


public class Bhls_SaleBS extends Bhls_SaleBS0RulePop
{   
	public boolean readHangGrant()
	{
		// 百货无挂单授权功能控制，总是可以使用
		if (!GlobalInfo.sysPara.isJavaPosManager)
		{
			return true;	
		}
		else
		{
			return super.readHangGrant();
		}
	}
	
	public boolean writeHangGrant()
	{
		// 百货无挂单授权功能控制，总是可以使用
		if (!GlobalInfo.sysPara.isJavaPosManager)
		{
			return true;
		}
		else
		{
			return super.writeHangGrant();
		}

	}
	
	// 当前是否刷会员卡
    public boolean checkMemberSale()
    {
        boolean isCustomer = false;

        if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
        {
            isCustomer = true;
        }
        
        return isCustomer;
    }

    // true  = 先输商品，后刷VIP卡模式
    // false = 先VIP卡，后输入商品模式
    protected boolean memberAfterGoodsMode()
    {
    	return true;
    }
    
    public boolean allowEditGoods()
    {
    	if (memberAfterGoodsMode() && checkMemberSale())
    	{
    		new MessageBox(Language.apply("已刷VIP卡,不能再修改商品\n\n请付款或取消VIP卡后再输入"));
    		return false;
    	}
    	
		return super.allowEditGoods();
    }
    
	public boolean findGoods(String code, String yyyh, String gz)
	{
    	if (memberAfterGoodsMode() && checkMemberSale())
    	{
    		new MessageBox(Language.apply("已刷VIP卡,不能再修改商品\n\n请付款或取消VIP卡后再输入"));
    		return false;
    	}
		
		return super.findGoods(code, yyyh, gz);
	}
	
	public boolean clearSell(int index)
	{
		if (cancelMemberOrGoodsRebate(index))
		{
			return true;
		}
		else
		{
			return super.clearSell(index);
		}
	}

	//已经取消VIP卡或者折扣 true
	//否则 false
	protected boolean cancelMemberOrGoodsRebate(int index)
	{
		if ((this.saletype.equals(SellType.PREPARE_BACK)   || this.saletype.equals(SellType.PREPARE_TAKE ))) return false;
		if (isNewUseSpecifyTicketBack(false)) return false;
        
		// true=先打折，后刷VIP卡，因此取消时先取消VIP，再取消折扣
		// false=先VIP，再打折，因此取消时先取消折扣，再取消VIP
		if (memberAfterGoodsMode())
		{
			// 取消VIP
	    	if (checkMemberSale())
	    	{
	    		if (new MessageBox(Language.apply("已经刷了VIP卡,你确定要取消VIP卡吗?"),null,true).verify()==GlobalVar.Key1)
	    		{
	    	        // 记录当前顾客卡
	    	        curCustomer = null;
	    	        
	    	    	// 记录到小票        	
	    	    	saleHead.hykh = null;
	    	    	
	    	    	// 重算所有商品应收
	    	    	for (int i=0;i<saleGoods.size();i++)
	    	    	{
	    	    		calcGoodsYsje(i);
	    	    	}
	    	    	
	    	        // 计算小票应收
	    	        calcHeadYsje();
	    	        saleEvent.updateSaleGUI();
	    		}
	    		return true;
	    	}
	    	
	    	// 取消临时折扣
			if (index >= 0)
			{
		    	SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		    	double sum = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke + saleGoodsDef.lszre + saleGoodsDef.lszzk + saleGoodsDef.lszzr); 
		    	if (sum > 0)
		    	{
			    	if (new MessageBox("【"+saleGoodsDef.name+"】" + Language.apply("存在临时折扣\n你确定要取消此商品的临时折扣吗?"),null,true).verify()==GlobalVar.Key1)
			    	{
			    		saleGoodsDef.lszke = 0;
			    		saleGoodsDef.lszre = 0;
			    		saleGoodsDef.lszzk = 0;
			    		saleGoodsDef.lszzr = 0;
			    		
		    	        // 计算小票应收
			    		calcGoodsYsje(index);
		    	        calcHeadYsje();
		    	        saleEvent.updateSaleGUI();
			    	}
					return true;
		    	}
			}
		}
		else
		{
	    	// 取消临时折扣
			if (index >= 0)
			{
		    	SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		    	double sum = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke + saleGoodsDef.lszre + saleGoodsDef.lszzk + saleGoodsDef.lszzr); 
		    	if (sum > 0)
		    	{
			    	if (new MessageBox("【"+saleGoodsDef.name+"】" + Language.apply("存在临时折扣\n你确定要取消此商品的临时折扣吗?"),null,true).verify()==GlobalVar.Key1)
			    	{
			    		saleGoodsDef.lszke = 0;
			    		saleGoodsDef.lszre = 0;
			    		saleGoodsDef.lszzk = 0;
			    		saleGoodsDef.lszzr = 0;
			    		
		    	        // 计算小票应收
			    		calcGoodsYsje(index);
		    	        calcHeadYsje();
		    	        saleEvent.updateSaleGUI();
			    	}
					return true;
		    	}
			}
			
			
			// 先刷卡状态下如果取消VIP必须取消整单
			/**
	    	if (checkMemberSale())
	    	{
	    		if (new MessageBox("已经刷了VIP卡,你确定要取消VIP卡吗?",null,true).verify()==GlobalVar.Key1)
	    		{
	    	        // 记录当前顾客卡
	    	        curCustomer = null;
	    	        
	    	    	// 记录到小票        	
	    	    	saleHead.hykh = null;
	    	    	saleHead.hytype = null;
	    	    	
	    	    	// 重算所有商品应收
	    	    	for (int i=0;i<saleGoods.size();i++)
	    	    	{
	    	    		calcGoodsYsje(i);
	    	    	}
	    	    	
	    	        // 计算小票应收
	    	        calcHeadYsje();
	    	        saleEvent.updateSaleGUI();
	    		}
	    		return true;
	    	}
	    	*/
		}
		
		// 返回false,执行基类取消交易的处理
    	return false;
	}
    
    public void calcBatchRebate(int index)
    {
    	// 百货连锁没有价随量变
    }
    
	public void calcAllRebate(int index)
    {
		char zszflag = 'Y';
        SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
        GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
        SpareInfoDef spareInfo = (SpareInfoDef) goodsSpare.elementAt(index);
        
        // 指定小票退货时不重算优惠价和会员价
        if (isSpecifyBack(saleGoodsDef))
        {
            return;
        }

        // 批发销售不计算
        if (SellType.ISBATCH(saletype))
        {
            return;
        }
        
        if (SellType.ISEARNEST(saletype))
        {
        	return;
        }

        // 削价商品和赠品不计算
        if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1'))
        {
            return;
        }
        
        saleGoodsDef.hyzke  = 0;
        saleGoodsDef.hyzkfd = goodsDef.hyjzkfd;
        saleGoodsDef.yhzke  = 0;
        saleGoodsDef.yhzkfd = 0;
        saleGoodsDef.plzke  = 0;
        saleGoodsDef.zszke  = 0;
        
        // 促销优惠
        if (goodsDef.poptype != '0')
        {
            //定价且是单品优惠
            if ((saleGoodsDef.lsj > 0) && ((goodsDef.poptype == '1') || (goodsDef.poptype == '7')))
            {
                // 促销折扣
                if ((saleGoodsDef.lsj > goodsDef.poplsj) && (goodsDef.poplsj > 0))
                {
                    saleGoodsDef.yhzke  = (saleGoodsDef.lsj - goodsDef.poplsj) * saleGoodsDef.sl;
                    saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
                }
            }
            else
            {
                // 促销折扣
                if ((1 > goodsDef.poplsjzkl) && (goodsDef.poplsjzkl > 0))
                {
                    saleGoodsDef.yhzke  = saleGoodsDef.hjje * (1 - goodsDef.poplsjzkl);
                    saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
                }
            }
            
            // 
            saleGoodsDef.yhzke  = ManipulatePrecision.doubleConvert(saleGoodsDef.yhzke,2, 1);

            // 按价格精度计算折扣
            saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);

            // 判断促销单是否允许折上折
            if (goodsDef.pophyjzkl % 10 >= 1)
            	zszflag = 'Y';
            else
            	zszflag = 'N';
        }
        
        //VIP消费
        if (checkMemberSale() && curCustomer != null && goodsDef.isvipzk == 'Y')
        {
            // 获取VIP折扣率定义
            calcVIPZK(index);
        	
            //有折扣,进行折上折
            if (getZZK(saleGoodsDef) >= 0.01 && goodsDef.hyj < 1.00)
            {
		        //只要商品会员折扣标志为不折上折时，就按[第一档处理，第二、三档不再折上折]
		        //只要有促销折扣并且促销不折上折时，也按[第一档处理，第二、三档不再折上折]
                if (spareInfo.char1 != 'Y' || (saleGoodsDef.yhzke > 0 && zszflag != 'Y'))
                {
                    zszflag = 'Y';
                    spareInfo.char1 = 'N';  //不允许折上折
                }

                // 需要折上折
                if (zszflag == 'Y')
                {
                    // 得到商品目前已打折比率
                    double zkl;
                    zkl = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef)) / saleGoodsDef.hjje, 2, 1);

                    // 商品允许折上折
                    if (spareInfo.char1 == 'Y')
                    {
                        // 得到会员折扣区间
                        double[] nvalues = { curCustomer.value1, curCustomer.value2, curCustomer.value3, curCustomer.value4, curCustomer.value5 };
                        
                        // 
                        if (zkl >= nvalues[0] && zkl <= nvalues[1])	// 折扣在区间内
                        {
                        	if (curCustomer.func.length() > 3 && curCustomer.func.charAt(3) == 'Y')
                            {
                        		saleGoodsDef.hyzke =  ManipulatePrecision.doubleConvert((1 - nvalues[2]) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)),2,1);
                            }
                        }    
                        else if (zkl > nvalues[1])                  // 折扣在区间上
                        {
                        	if (curCustomer.func.length() > 4 && curCustomer.func.charAt(4) == 'Y')
                            {
                        		if (nvalues[3] == 0) nvalues[3] = goodsDef.hyj;
                        		
                                zkl = ManipulatePrecision.doubleConvert((1 - nvalues[3]) * saleGoodsDef.hjje, 2,1);
                                
                                if (zkl > getZZK(saleGoodsDef))
                                {
                                    saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(zkl - getZZK(saleGoodsDef),2,1);
                                }
                            }
                        }
                        else if (zkl < nvalues[0])                  // 折扣在区间下
                        {
                        	if (curCustomer.func.length() > 5 && curCustomer.func.charAt(5) == 'Y')
                            {
                        		saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1 - nvalues[4]) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2,1);
                            }
                        }
                        
                        // 取商品不折上折时会员折扣率和折上折后的综合折扣,低价优先,差额补算到会员折扣
                        double zkl1 = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * saleGoodsDef.hjje, 2,1);
                        if (zkl1 > getZZK(saleGoodsDef))
                        {
                        	saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert(zkl1 - getZZK(saleGoodsDef), 2,1);
                        }
                    }
                    else
                    {
                        // 商品不折上折时，取商品的hyj和综合折扣较低者
                    	if (goodsDef.hyj < zkl )
                        {
                            zkl = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * saleGoodsDef.hjje, 2,1);
                            if (zkl > getZZK(saleGoodsDef))
                            {
                            	saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(zkl - getZZK(saleGoodsDef), 2,1);
                            }
                        }
                    }
                }
            }
            else
            {
                //无折扣,按商品缺省会员折扣打折
            	saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * saleGoodsDef.hjje, 2,1);
            }

            // 按价格精度计算折扣
            saleGoodsDef.hyzke = getConvertRebate(index,saleGoodsDef.hyzke);
        }
    }
    
    public void calcVIPZK(int index)
    {
        SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
        GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
        SpareInfoDef spareInfo = (SpareInfoDef) goodsSpare.elementAt(index);
        
        // 未刷卡
        if (!checkMemberSale() || curCustomer == null) return;
        
        // 非零售开票
        if (!saletype.equals( SellType.RETAIL_SALE )&& !saletype .equals( SellType.PREPARE_SALE)) 
        {
        	goodsDef.hyj   = 1;
        	spareInfo.char1 = 'N';
        	return;
        }
        
        // 查询商品VIP折上折定义
        GoodsAmountDef VIPZK = new GoodsAmountDef();
        if (DataService.getDefault().findAmountDef(VIPZK, saleGoodsDef.code,saleGoodsDef.gz, curCustomer.type, 0))
        {
            // 有柜组和商品的VIP折扣定义
        	goodsDef.hyj   = VIPZK.plhyj;
        	spareInfo.char1 = VIPZK.memo.charAt(0);	//折上折标志        
        }
        else
        {
            // 无柜组和商品的VIP折扣定义,以卡类别的折扣率为VIP打折标准
        	goodsDef.hyj   = curCustomer.zkl; 
        	spareInfo.char1 = 'Y';					//允许折上折  
        }
    }
	
	public boolean memberGrantFinish(CustomerDef cust)
    {
        if (cust.status == null || cust.status.trim().length() <=0 || cust.status.charAt(0) != 'Y')
        {
        	new MessageBox(Language.apply("该顾客卡已失效!"));
        	return false;
        }
        
        // 记录当前顾客卡
        curCustomer = cust;
        
    	// 记录到小票        	
    	saleHead.hykh = cust.code;
    	saleHead.hytype = cust.type;
    	saleHead.str4 = cust.valstr2;
    	saleHead.hymaxdate = cust.maxdate;
    	
    	// 重算所有商品应收
    	for (int i=0;i<saleGoods.size();i++)
    	{
    		calcGoodsYsje(i);
    	}
    	
        // 计算小票应收
        calcHeadYsje();
        
        return true;
    }
	
    public boolean getBackSellStatus()
    {
    	//true ,表示只能使用后台退货
    	//false,表示可以使用前台退货
    	if (GlobalInfo.sysPara.onlyUseBReturn == 'Y')
    	{
    		// 查询设置的例外单据类别
    		boolean hasExceptType = false;
    		String[] billtypes = GlobalInfo.sysPara.exceptBReturnType.split(",");
    		if (billtypes == null) return true;
    		// 循环查找例外单据类别
    		for (int i = 0; i < billtypes.length; i++)
    		{
    			if (billtypes[i].equalsIgnoreCase(String.valueOf(SellType.getDjlbSaleToBack(saletype))))
    			{
    				hasExceptType = true;
    			}
    		}
    		
    		if (hasExceptType)
    		{
    			return false;
    		}
    		else
    		{
    			return true;	
    		}
    	}
    	else
    	{
    		return false;
    	}
    }
    
    public void backSell()
    {
    	if (getBackSellStatus())
    	{
    		new MessageBox(Language.apply("请使用后台退货功能进行退货处理!"));
    	}
    	else
    	{
    		super.backSell();
    	}
    }
/*    
    public double calcPayBalance()
    {
    	double ye = super.calcPayBalance();
    	
    	// 如果返券付款方式允许溢余,如果有溢余,由于溢余部分不能分摊到不收券商品
    	// 因此已付款的金额只能按最大允许收券的金额来算，剩余付款金额不能算这部分溢余
    	// 从而保证其他付款方式的付款金额足够支付不收券商品
    	PayModeDef paymode = DataService.getDefault().searchPayMode("0500");
    	if (paymode != null && paymode.type == '5' && paymode.isyy == 'Y')
    	{	        
    		// 计算返券卡已付款金额
    		SalePayDef spd = null;
    		PaymentFjk payobj = null;
    		for (int i=0;i<salePayment.size();i++)
    		{
    			spd = (SalePayDef)salePayment.elementAt(i);
    			if (CreatePayment.getDefault().isPaymentFjk(spd.paycode))
    			{
    				payobj = (PaymentFjk)payAssistant.elementAt(i);
    				break;
    			}
    		}
    		String[] s = null;
    		double fjkaje = 0;
    		double fjkbje = 0;
    		double fjkfje = 0;
    		if (payobj != null)
    		{
    			s = payobj.getFjkPayTotal(salePayment).split(",");
    			fjkaje = Double.parseDouble(s[0]);
    			fjkbje = Double.parseDouble(s[1]);
    			fjkfje = Double.parseDouble(s[2]);
    		}
    		
	        // 有电子券付款
	        if (fjkaje > 0 || fjkbje > 0 || fjkfje > 0)
    		{
    			// 计算允许收券商品的最大收券合计
	        	SaleGoodsDef sgd = null;
	        	double maxaqje = 0;
	        	double maxbqje = 0;
	        	double maxfqje = 0;
    			for (int i=0;i<saleGoods.size();i++)
    			{
    				sgd = (SaleGoodsDef)saleGoods.elementAt(i);
    				s = sgd.memo.split(",");
    				if (s.length >= 2)
    				{
    					maxaqje = ManipulatePrecision.add(maxaqje,Double.parseDouble(s[0]));
    					maxbqje = ManipulatePrecision.add(maxbqje,Double.parseDouble(s[1]));
    					if (s.length >= 3)
    					{
    						maxfqje = ManipulatePrecision.add(maxfqje,Double.parseDouble(s[2]));
    					}
    					else
    					{
    						maxfqje = ManipulatePrecision.add(maxfqje,sgd.hjje-sgd.hjzk);
    					}
    				}
    			}
    			
    			// 计算是否有溢余
    			fjkaje = ManipulatePrecision.sub(fjkaje,maxaqje);
    			if (fjkaje < 0) fjkaje = 0;
    			fjkbje = ManipulatePrecision.sub(fjkbje,maxbqje);
    			if (fjkbje < 0) fjkbje = 0;
    			fjkfje = ManipulatePrecision.sub(fjkfje,maxfqje);
    			if (fjkfje < 0) fjkfje = 0;
    			
    			// 重新计算付款余额
    			if (fjkaje > 0 || fjkbje > 0 || fjkfje > 0)
    			{
    				salezlexception = fjkaje + fjkbje + fjkfje;	// 这部分溢余找零和计算剩余应付时都要除开
    		        ye = saleyfje - (saleHead.sjfk - salezlexception);
    		        ye = ManipulatePrecision.doubleConvert(ye,2,1);
    		        if (ye < 0) ye = 0;
    			}		
    		}  		
    	}
    	
    	return ye;
    }
*/    
}
