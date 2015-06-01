package custom.localize.Sdyz;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;

public class Sdyz_SaleBS extends Sdyz_SaleBSData
{
    public boolean checkGoodsRebate(GoodsDef goodsDef)
    {
        if (((goodsDef.issqkzk != 'Y') && (cursqktype == '1')) ||
            ((goodsDef.isvipzk != 'Y') && (goodsDef.isvipzk != 'C') && (cursqktype == '2')) ||
            ((curGrant.grantgz != null) &&
            !curGrant.grantgz.equals("ALL") &&
            (curGrant.grantgz.indexOf(goodsDef.gz) == -1)) ||
            ((goodsDef.maxzkl * 100) >= 100))
        {
        	return false;
        }
        else
        {
        	return true;
        }
    }
    
    public double getMaxRebateGrant(double grantzkl,GoodsDef goodsDef)
    {
    	double maxzkl = super.getMaxRebateGrant(grantzkl,goodsDef);
    	
    	// 如果是会员卡打折,maxzke记录有商品的会员卡最底打折比例
    	if (CommonMethod.noEmpty(saleHead.hysq) && goodsDef.maxzke > maxzkl)
        {
        	maxzkl = goodsDef.maxzke;
        }
        
        return maxzkl;
    }
    
	public boolean inputRebate(int index)
	{
		if (Sdyz_CustomGlobalInfo.getDefault().sysPara.isrebate != 'Y') return false;
		
		return super.inputRebate(index);
	}
	
	public boolean inputRebatePrice(int index)
	{
		if (Sdyz_CustomGlobalInfo.getDefault().sysPara.isrebate != 'Y') return false;
		
		return super.inputRebatePrice(index);
	}
	
	// 供应商打折键
    public void execCustomKey2(boolean keydownonsale)
    {
    	if (Sdyz_CustomGlobalInfo.getDefault().sysPara.isrebate != 'Y') return;
    	
        int index = saleEvent.table.getSelectionIndex();
        if (index >= 0)
        {
        	// 输入商品折扣
        	if (inputGysZk(index))
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
    
    private boolean inputGysZk(int index)
    {
        double grantzkl = 0;
        SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);   	
        
        // 服务费 以旧换新不处理
        if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
        {
            return false;
        }

        // 备份数据
        SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();
                
        // 最大折扣权限
        grantzkl = 0.01;
        	
        // 记录授权工号
        saleGoodsDef.sqkh = "####";
        saleGoodsDef.sqktype = 1;
        saleGoodsDef.sqkzkfd = 0;	// 折扣全部由供应商承担,商家则承担0
        
        // 计算权限允许的最大折扣额
        double maxzre = ManipulatePrecision.doubleConvert((1 - grantzkl) * saleGoodsDef.hjje,2,1);
        
        // 根据模拟计算得到当前最大打折金额
        double lszre = maxzre;
        lszre = ManipulatePrecision.doubleConvert(lszre, 2, 1);

        // 输入折让
        StringBuffer buffer = new StringBuffer();
        if (!new TextBox().open("请输入单品供应商折扣","供应商折扣","当前收银员最大供应商折扣权限为 " + ManipulatePrecision.doubleToString(grantzkl*100) + "%\n你目前对该商品最多能折让 " + ManipulatePrecision.doubleToString(lszre,2,1,true) + " 元",buffer, 0,lszre, true))
        {
        	// 恢复数据
            saleGoods.setElementAt(oldGoodsDef, index);
            
        	return false;
        }

        // 得到折让额
        lszre = Double.parseDouble(buffer.toString());
        
        // 
        saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(lszre,2,1);
        if (getZZK(saleGoodsDef) > maxzre)
        {
            saleGoodsDef.lszre -= getZZK(saleGoodsDef) - maxzre;
            saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre,2,1);
        }
        if (saleGoodsDef.lszre < 0) saleGoodsDef.lszre = 0;
        saleGoodsDef.lszre = getConvertRebate(index, saleGoodsDef.lszre);

        // 重算商品折扣合计
        getZZK(saleGoodsDef);
        
        // 重算小票应收
        calcHeadYsje();
        
        return true;    	
    }
    
	// 会员是否折扣
    public void execCustomKey1(boolean keydownonsale)
    {
    	if (!checkMemberSale()) return;

    	if (new MessageBox("请确认会员卡是否打折?",null,true).verify() == GlobalVar.Key1)
    		isHyzk = true;
    	else
    		isHyzk = false;

    	// 重算所有商品应收
    	for (int i=0;i<saleGoods.size();i++)
    	{
    		calcGoodsYsje(i);
    	}
    	
        // 计算小票应收
        calcHeadYsje();
        
        saleEvent.updateSaleGUI();
    }
    
	// 招商银行联名卡授权键
    public void execCustomKey3(boolean keydownonsale)
    {
    	if (newMemberGrant(true))
    	{
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
    
    public boolean memberGrant()
    {
    	return newMemberGrant(false);
    }
    
    private boolean newMemberGrant(boolean isZsHy)
    {
    	StringBuffer cardno = new StringBuffer();
    	String track2,track3;
    	
    	//
    	if (!GlobalInfo.isOnline)
    	{
    		new MessageBox("顾客会员卡必须联网使用");
    		return false;
    	}
    	
    	//
    	String hint;
    	if (isZsHy)
    		hint = "请刷招商银行联名卡"; 
    	else
    		hint = "请刷会员卡或顾客打折卡";
    			
    	// 输入顾客卡号
    	TextBox txt = new TextBox();
        if (!txt.open(hint, "会员号", "请将会员卡或顾客打折卡从刷卡槽刷入", cardno, 0, 0,false, TextBox.MsrInput))
        {
            return false;
        }
        
        // 得到顾客卡磁道信息
        track2 = txt.Track2;
        track3 = txt.Track3;
    	        
    	// 山东银座会员卡要截取
    	int l = track2.length();
    	if (l >= 13)
    	{
    		if (track2.charAt(0) < '0' || track2.charAt(0) > '9')
    		{
    			track2 = track2.substring(1);
    		}
    		
    		StringBuffer s = new StringBuffer();
    		s.append(track2.charAt(0));
    		s.append(track2.charAt(2));
    		s.append(track2.charAt(3));
    		s.append(track2.charAt(5));
    		s.append(track2.charAt(6));
    		s.append(track2.charAt(8));
    		s.append(track2.charAt(9));
    		s.append(track2.charAt(11));
    		s.append(track2.charAt(12));
    		track2 = s.toString();
    	}
        
    	// 招行联名卡
    	if (isZsHy)
    	{
    		if (track3.length() > 70)
    		{
    			track2 = track3.substring(69,78);
    		}
    		else	//银座要求，招行联名卡键只能刷招行联名卡
    		{
    			new MessageBox("该卡不是招商银行联名卡");
    			return false;
    		}
    	}
    	
    	//
        ProgressBox progress = null;
        CustomerDef cust = null;
        
        try
        {
        	progress = new ProgressBox();
        	progress.setText("正在查询会员卡信息，请等待.....");
        	
	        // 查找会员卡
	        cust = new CustomerDef();
	        if (!DataService.getDefault().getCustomer(cust, track2))
	        {
	        	return false;
	        }
        }
        finally
        {
        	if (progress != null) progress.close();
        }
        
        // 记录当前顾客卡
        curCustomer = cust;
        
        //
		salejfbase = 1;
		salejfzkl = 0;
		salejf = 0;
		
        // 具有积分功能
        if (cust.isjf == 'Y')
        {
        	// 记录到小票        	
        	saleHead.jfkh = cust.code;
        	
			// 积分卡的积分基数和积分折扣率
			salejfbase = cust.value1;
			if (salejfbase <= 0.00) salejfbase = 1.00;
			salejfzkl  = cust.value2;
			if (salejfzkl  >= 1.00) salejfzkl  = 0.00;
			salejf	   = Math.abs(cust.value3);
        }
        
        // 具有会员功能
        if (cust.ishy == 'Y')
        {
        	// 记录到小票        	
        	saleHead.hykh = cust.code;
        	
        	// 重算所有商品应收
        	for (int i=0;i<saleGoods.size();i++)
        	{
        		calcGoodsYsje(i);
        	}
        	
            // 计算小票应收
            calcHeadYsje();
        }
        
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
            String msg = "";
            if (cust.func == null || cust.func.length() <= 0) cust.func = "A";
    		if (cust.func.charAt(0) != 'Y' && cust.func.charAt(0) != 'N')
    		{
    			curGrant.zpzkl = cust.zkl;
    			curGrant.dpzkl = cust.zkl;
    			msg = "顾客卡授权打折\n\n总品及单品折扣:" + ManipulatePrecision.doubleToString(cust.zkl*100) + "%";
    		}
    		if (cust.func.charAt(0) == 'Y')
    		{
    			curGrant.zpzkl = cust.zkl;
    			msg = "顾客卡授权打折\n\n总品折扣:" + ManipulatePrecision.doubleToString(cust.zkl*100) + "%";
    		}
    		if (cust.func.charAt(0) == 'N')
    		{
    			curGrant.dpzkl  = cust.zkl;
    			msg = "顾客卡授权打折\n\n单品折扣:" + ManipulatePrecision.doubleToString(cust.zkl*100) + "%";
    		}
    		
    		// 提示
    		new MessageBox(msg);
        }

        return true;
    }
    
    private double calcSaleBcjf()
    {
		double salejfje = 0;
		SaleGoodsDef saleGoodsDef = null;
		int sign = 1;

        for (int i = 0; i < saleGoods.size(); i++)
        {
            saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
            
            if (saleGoodsDef.flag == '0')
            {
                continue;
            }

            // 以旧换新商品,合计要减
            if (saleGoodsDef.type == '8')
            {
                sign = -1;
            }
            else
            {
                sign = 1;
            }
            
            if (saleGoodsDef.isvipzk == 'Y' || saleGoodsDef.isvipzk == 'N')
            {
            	salejfje = salejfje + (saleGoodsDef.hjje - saleGoodsDef.hjzk) * sign; 
            }
        }
        
    	return salejfje / (salejfbase>0.0?salejfbase:1.0);
    }
    
    public void paySell()
    {
    	if (SellType.ISBACK(saletype) && CommonMethod.noEmpty(saleHead.jfkh) && calcSaleBcjf() > salejf)
		{
			new MessageBox("卡内积分不足,不允许退货");
			return;
		}
    	
    	//
    	super.paySell();
    }
    
    public boolean saleSummary()
    {
    	if (!super.saleSummary()) return false;
    	
    	// 计算本次积分
    	if (CommonMethod.noEmpty(saleHead.jfkh))
    		saleHead.bcjf  = calcSaleBcjf() * SellType.SELLSIGN(this.saletype);
    	else
    		saleHead.bcjf = 0;
    	
    	return true;
    }
}


