package custom.localize.Wjjy;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsAmountDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Bhls.Bhls_DataService;
import custom.localize.Bhls.Bhls_SaleBS;

public class Wjjy_SaleBS extends Bhls_SaleBS {
	
    public boolean getBackSellStatus()
    {
    	//true ,表示只能使用后台退货
    	//false,表示可以使用前台退货
    	return false;
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
                //促销折扣
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
        	// 先查询是否有限量VIP折扣定义
        	GoodsAmountDef limitzk = new GoodsAmountDef();
        	if (((Bhls_DataService)DataService.getDefault()).findLimitVIPZK(curCustomer.code,limitzk,saleGoodsDef))
        	{
        		// 检查数量是否足够销售
            	double hjsl = 0;
            	SaleGoodsDef goods = null;
                for (int j = 0; j < saleGoods.size(); j++)
                {
                    goods = (SaleGoodsDef) saleGoods.elementAt(j);
                    
                    if (goods.gz.equals(saleGoodsDef.gz))
                    {
                        hjsl += goods.sl;
                    }
                }
        		if (hjsl > limitzk.plsl)
        		{
        			new MessageBox("[" + saleGoodsDef.gz + "]柜的商品 " + saleGoodsDef.name + "\n只允许该VIP购买 " + (int)limitzk.plsl + " 个\n本笔交易该柜商品有 " + (int)hjsl + " 个,不能享受VIP折扣");
        		}
        		else
        		{
        			saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1 - limitzk.plhyj) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)),2,1);
        			saleGoodsDef.str1  = String.valueOf((long)limitzk.pllsj);
        		}
        	}
        	else
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
        	}
        	
            // 按价格精度计算折扣
            saleGoodsDef.hyzke = getConvertRebate(index,saleGoodsDef.hyzke);
        }
    }
	
    public boolean paySellStart()
    {
    	if (!super.paySellStart()) return false;	// 不允许进行付款
    	
    	//如果在制定小票退货时刷卡，需要查询退货限额
    	if (SellType.ISBACK(saletype) && thSyjh != null && thSyjh.length() > 0 && thFphm > 0)
    	{
    		if (curCustomer != null)
    		{
    			SpareInfoDef info = new SpareInfoDef();
    			if (((Wjjy_DataService)DataService.getDefault()).getBackSellLimit(thSyjh, String.valueOf(thFphm), curCustomer.code, "", info))
    			{
    				
    				if ( calcHeadYfje() > info.num1 )
    				{
    					new MessageBox("此会员卡退货最大限额为"+ManipulatePrecision.doubleToString(info.num1)+"\n请删除部分商品后再进行退货");
    					return false;
    				}
    			}
    			else
    			{
    				return false;
    			}
    		}
    	}
    	
    	return true;
    }
}
