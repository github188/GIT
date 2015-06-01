package custom.localize.Sdyz;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;


public class Sdyz_SaleBSData extends com.efuture.javaPos.Logic.SaleBS
{
	double salejfbase;				//当前积分卡的积分基数
	double salejfzkl;				//当前积分卡的积分折扣率
	double salejf;					//当前积分卡的积分
	boolean isHyzk;				//会员卡是否折扣
	
	public void initNewSale()
	{
		super.initNewSale();
		
		//
		salejfbase = 1;
		salejfzkl = 0;
		salejf = 0;
		isHyzk = true;
	}
	
	public boolean writeBrokenData()
	{
		brokenAssistant.removeAllElements();
		brokenAssistant.add(new Double(salejfbase));
		brokenAssistant.add(new Double(salejfzkl));
		brokenAssistant.add(new Double(salejf));
		brokenAssistant.add(new Boolean(isHyzk));
		
		return super.writeBrokenData();
	}	
	
	public boolean readBrokenData()
	{
		if (super.readBrokenData())
		{
			salejfbase = ((Double)brokenAssistant.elementAt(0)).doubleValue();
			salejfzkl = ((Double)brokenAssistant.elementAt(1)).doubleValue();
			salejf = ((Double)brokenAssistant.elementAt(2)).doubleValue();
			isHyzk = ((Boolean)brokenAssistant.elementAt(3)).booleanValue();
			
			return true;
		}
		
		return false;
	}
	
	public boolean writeHang()
	{
		brokenAssistant.removeAllElements();
		brokenAssistant.add(new Double(salejfbase));
		brokenAssistant.add(new Double(salejfzkl));
		brokenAssistant.add(new Double(salejf));
		brokenAssistant.add(new Boolean(isHyzk));
		
		return super.writeHang();
	}
	
	public boolean readHang()
	{
		if (super.readHang())
		{
			salejfbase = ((Double)brokenAssistant.elementAt(0)).doubleValue();
			salejfzkl = ((Double)brokenAssistant.elementAt(1)).doubleValue();
			salejf = ((Double)brokenAssistant.elementAt(2)).doubleValue();
			isHyzk = ((Boolean)brokenAssistant.elementAt(3)).booleanValue();
			
			return true;
		}
		
		return false;
	}
	
    public void calcAllRebate(int index)
    {
        SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
        GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

        // 指定小票退货时不重算优惠价和会员价
        if (isSpecifyBack(saleGoodsDef))
        {
            return;
        }

        saleGoodsDef.hyzke  = 0;
        saleGoodsDef.hyzkfd = goodsDef.hyjzkfd;
        saleGoodsDef.yhzke  = 0;
        saleGoodsDef.yhzkfd = 0;
        saleGoodsDef.plzke  = 0;
        saleGoodsDef.zszke  = 0;

        // 批发销售不计算
        if (SellType.ISBATCH(saletype))
        {
            return;
        }

        // 削价商品和赠品不计算
        if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1'))
        {
            return;
        }

        // 当前是否刷会员卡
        boolean isCustomer = checkMemberSale();

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
                if ((1 >= goodsDef.poplsjzkl) && (goodsDef.poplsjzkl > 0))
                {
                    saleGoodsDef.yhzke  = saleGoodsDef.hjje * (1 - goodsDef.poplsjzkl);
                    saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
                }
            }
            
            // 如果会员折扣比促销折扣底，以会员折扣计算
            if (isCustomer && isHyzk)
            {
                if ((1 >= goodsDef.pophyjzkl) && (goodsDef.pophyjzkl > 0))
                {
                	double zkl = (1 - goodsDef.pophyjzkl) + salejfzkl;
                	if (zkl > (1 - goodsDef.maxzkl)) zkl = 1 - goodsDef.maxzkl;
                	if (zkl >= 1.00) zkl = 0.00;
    				if (ManipulatePrecision.doubleCompare(zkl * saleGoodsDef.hjje,saleGoodsDef.yhzke,2) > 0)
    				{
                        saleGoodsDef.yhzke  = saleGoodsDef.hjje * zkl;
                        saleGoodsDef.yhzkfd = goodsDef.pophyjzkfd;
    				}                	
                }
            }
        }
        else // 非促销优惠
        {
            if (isCustomer && isHyzk)
            {
                if ((1 >= goodsDef.hyj) && (goodsDef.hyj > 0))
                {
                	double zkl = (1 - goodsDef.hyj) + salejfzkl;
                	if (zkl > (1 - goodsDef.maxzkl)) zkl = 1 - goodsDef.maxzkl;
                	if (zkl >= 1.00) zkl = 0.00;
                    saleGoodsDef.hyzke  = saleGoodsDef.hjje * zkl;
                    saleGoodsDef.hyzkfd = goodsDef.hyjzkfd;
                }
            }
        }

        // 
        saleGoodsDef.yhzke  = ManipulatePrecision.doubleConvert(saleGoodsDef.yhzke,2, 1);
        saleGoodsDef.hyzke  = ManipulatePrecision.doubleConvert(saleGoodsDef.hyzke,2, 1);
        
        // 按价格精度计算折扣
        saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
        saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
    }

    public double getConvertRebate(int i, double zkje)
    {
        GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(i);

    	//只有折扣比率产生的折扣才调用该函数
        //超市不管定义成什么，一律精确到分
    	if (Sdyz_CustomGlobalInfo.getDefault().sysPara.dpsswr == 'Y' && GlobalInfo.syjDef.issryyy != 'N')
    	{
			if (GlobalInfo.syjDef.sswrfs == '0')
			{
	    		// 精确到分
	    		goodsDef.jgjd = 0.01;
			}
			if (GlobalInfo.syjDef.sswrfs == '1')
			{
	    		// 四舍五入到角
	    		goodsDef.jgjd = 0.1;
			}
			if (GlobalInfo.syjDef.sswrfs == '2')
			{
	    		// 四舍五入到角
	    		goodsDef.jgjd = 0.1;
			}
    	}
    	else
    	{
    		// 精确到分
    		goodsDef.jgjd = 0.01;
    	}

    	//
        return super.getConvertRebate(i,zkje);
    }
    
    public SaleGoodsDef goodsDef2SaleGoods(GoodsDef goodsDef, String yyyh,
            double quantity, double price,
            double allprice, boolean dzcm)
    {
    	SaleGoodsDef sg = super.goodsDef2SaleGoods(goodsDef,yyyh,quantity,price,allprice,dzcm);
    	
    	// 用str1记录促销单是否积分
    	if (goodsDef.poptype != '0')
    	{
    		if (!goodsDef.str1.equals("Y") && sg.isvipzk == 'Y')
    		{
    			sg.isvipzk = 'C';
    		}
    		if (!goodsDef.str1.equals("Y") && sg.isvipzk == 'N')
    		{
    			sg.isvipzk = 'D';
    		}
    	}
    	
    	return sg;
    }
}
