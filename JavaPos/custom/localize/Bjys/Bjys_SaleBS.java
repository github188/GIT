package custom.localize.Bjys;


import java.util.Vector;

import org.eclipse.swt.SWT;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.CustomerVipZklDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.BuyInfoForm;
import com.efuture.javaPos.UI.Design.DisplaySaleTicketForm;
import com.efuture.javaPos.UI.Design.LoginForm;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.PreMoneyForm;
import com.efuture.javaPos.UI.Design.SaleForm;

public class Bjys_SaleBS extends SaleBS
{
	StringBuffer buffer = null;
	int amout = 0;
	int tempsalegoods = 0;
	boolean bool = true;
	boolean islowerprice = false;
	
	//切换营业员
	public void yyyInput(int index)
    {
		try
		{
			if (GlobalInfo.syjDef.issryyy != 'N' && (!saleEvent.yyyh.getText().equals("超市") || saleGoods.size() <= 0 ))
			{
				
				if (saleEvent.yyyh == saleEvent.saleform.getFocus() || saleEvent.gz == saleEvent.saleform.getFocus()) return ;
				
				if (saleGoods.size() == 0)
				{
					amout = 0;
				}
								
				if (amout < saleGoods.size())
				{
					saleEvent.saleform.setFocus(saleEvent.yyyh);
					amout = saleGoods.size();
					
					return ;
				}
				else
				{
					buffer = new StringBuffer();
					
					new Bjys_AppendBusinessPerForm(buffer);
					
					saleEvent.saleform.setFocus(saleEvent.code);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
    }
	
	public boolean deleteGoods(int index)
    {
		if (!super.deleteGoods(index)) return false;
		
		amout = saleGoods.size() - 1;
		
		return true;
    }
	
	public boolean clearSell(int index)
    {
		if (!super.clearSell(index)) return false;
		
		amout = 0;
		
		return true;
    }
		
	public void initOneSale(String type)
    {
		amout = 0;
			
		super.initOneSale(type);
    }

    public double setGoodsDefaultPrice(GoodsDef goodsDef)
    {
    	double price = super.setGoodsDefaultPrice(goodsDef);
    	
    	// 处理削价
    	islowerprice = false;
		if (SellType.ISSALE(saletype) && !SellType.ISBATCH(saletype) && 
			goodsDef.xjjg > 0.009 && LowerPrice(goodsDef))
		{
			// 放弃优惠信息
			goodsDef.popdjbh = "";
			goodsDef.poptype = '0';

			// 使用削价价格
			price = goodsDef.xjjg;
			
			//
			islowerprice = true;
		}
		
		return price;
    }	
	
    public boolean LowerPrice(GoodsDef goodsDef)
    {
    	String[] title = {"销售方式","销售价格"};
		int[] width = {120,300};
		Vector contents = new Vector();
		contents.add(new String[]{"正常售价",ManipulatePrecision.doubleToString(goodsDef.lsj)});
		contents.add(new String[]{"削价价格",ManipulatePrecision.doubleToString(goodsDef.xjjg)});
		
		int choice = new MutiSelectForm().open("请选择销售价格", title, width, contents);
		if (choice == 1) return true;
		 
		return false;
    }
    
	public SaleGoodsDef goodsDef2SaleGoods(GoodsDef goodsDef, String yyyh,double quantity, double price,double allprice, boolean dzcm)
	{
		SaleGoodsDef saleGoodsDef = super.goodsDef2SaleGoods(goodsDef, yyyh, quantity, price, allprice, dzcm);

		// 设置商品削价标志
		if (islowerprice)
		{
			saleGoodsDef.flag = '3';				// 削价商品标志
		}
		
		// 设置附加营业员
		if (buffer != null && buffer.toString().length() > 0)
		{
			String tempstr[] = buffer.toString().split(";");
			saleGoodsDef.str3 = tempstr[0].trim(); 	// 附加营业员一
			saleGoodsDef.str1 = tempstr[1].trim();	// 附加营业员二
			saleGoodsDef.str2 = tempstr[2].trim();	// 附加营业员三
			
			buffer.delete(0,buffer.length());
			buffer = null;
		}
		else
		{
			saleGoodsDef.str3 = ""; // 附加营业员一
			saleGoodsDef.str1 = "";	// 附加营业员二
			saleGoodsDef.str2 = "";	// 附加营业员三
		}

		//
		return saleGoodsDef;
	}
	
	//打开旅游团号输入
	public void execCustomKey1(boolean keydownonsale)
    {
		try
		{
			buffer = new StringBuffer();
		
			if (!new TextBox().open("请输入旅游号","旅游团号","",buffer,0,0, false,TextBox.IntegerInput,2))
			{
				//将原来的旅游团号从小票头清除掉
				saleHead.buyerinfo = "";
				saleEvent.setGroupInputArea("输入区");
				return ;
			}
		
			//将团号,加入小票头
			
			int length = buffer.toString().trim().length();
			
			String buyervalue = "";
			
			if (length < 2)
			{
				buyervalue = "00000" + buffer.toString().trim();
			}
			else
			{
				buyervalue = "0000" + buffer.toString().trim();
			}
			
			saleHead.buyerinfo = buyervalue.trim();
			
			if (saleHead.buyerinfo.length() >= 6)
			saleEvent.setGroupInputArea("旅游团[" + saleHead.buyerinfo.substring(4,6)+"]");
		}
		finally
		{
			if (buffer != null)
			{
				buffer.delete(0,buffer.length());
				buffer = null;
			}
		}
    }
	
	 //打开红冲
	 public void execCustomKey2(boolean keydownonsale)
	 {
	    new DisplaySaleTicketForm(StatusType.MN_SALEHC);
	    
	    //	  如果小票号发生改变
        if (this.saleHead.fphm != GlobalInfo.syjStatus.fphm)
        {
            // 刷新数据
            this.refreshSaleData();

            // 刷新小票号显示
            saleEvent.setSYJInfo();
        }
	 }
	 
	 //重新登录
	 public void execCustomKey3(boolean keydownonsale)
	 {
		 MessageBox me = new MessageBox("你确定要重新登录吗?", null, true);
    	 
    	 if (me.verify() == GlobalVar.Key1)
    	 {
         	//关闭收银主窗口
            if (GlobalInfo.saleform != null && GlobalInfo.saleform.closeForm())
            {
            	GlobalInfo.background.setVersionEanble(true);
            	
            	//登录
                if (new LoginForm().open(null))
                {
                	// 输入备用金
                	new PreMoneyForm().open();

                	// 显示销售界面
                    GlobalInfo.background.setVersionEanble(false);
                    GlobalInfo.saleform = new SaleForm(GlobalInfo.saleform.getShellParent(),SWT.NONE);
                }
                else
                {
                	LoadSysInfo.getDefault().ExitSystem();
                }
            }
    	 }
	 }
	 
	 //中行外卡
	 public void execCustomKey4(boolean keydownonsale)
	 {
		 if (keydownonsale)
		 {
			sendQuickPayButton(GlobalVar.CustomKey4);
		 }
		 else
		 {
    		// 定位付款方式
    		int last = payButtonToPayModePosition(GlobalVar.CustomKey4);
    		if (last >= 0)
    		{
    			salePayEvent.gotoPayModeLocation(last);
    		}
		 }    	
	 }
	 
	 //	积分消费卡
	 public void execCustomKey5(boolean keydownonsale)
	 {
		 if (keydownonsale)
		 {
			sendQuickPayButton(GlobalVar.CustomKey5);
		 }
		 else
		 {
    		// 定位付款方式
    		int last = payButtonToPayModePosition(GlobalVar.CustomKey5);
    		if (last >= 0)
    		{
    			salePayEvent.gotoPayModeLocation(last);
    		}
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
    
    public void customerIsHy(CustomerDef cust)
    {
    	// 记录到小票  
    	saleHead.hykh = cust.code;
    }
    
    public void customerIsJf(CustomerDef cust)
    {
    	
    }
    
    public void customerIsZk(CustomerDef cust)
    {

    }
    
    public boolean memberGrantFinish(CustomerDef cust)
    {
    	if (!super.memberGrantFinish(cust)) return false;
    	
    	// 增加会员提示描述
    	if (cust.valstr1 != null && !cust.valstr1.trim().equals(""))
    	{
    		new MessageBox(cust.valstr1);
    	}
    	
    	return true;
    }
    
    //输入折扣
    public boolean inputRebate(int index)
    {
    	double grantzkl = 0;
    	
    	// A模式指定小票退货不允许修改商品
    	if (isNewUseSpecifyTicketBack()) return false;
    	
        SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
        GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);    	
       
        // 服务费 以旧换新不处理
        if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
        {
            return false;
        }

        // 不能打折
        if (!checkGoodsRebate(goodsDef))
        {
            new MessageBox("该商品不允许打折\n或者\n不在你的授权范围内");

            return false;
        }

        // 备份数据
        SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();
                
        // 授权
        if ((curGrant.dpzkl * 100) >= 100)
        {
        	//
    		OperUserDef staff = inputRebateGrant(index);
    		if (staff == null) return false;
    		
    		// 本次授权折扣
    		grantzkl = staff.dpzkl;

            // 记录授权工号
            saleGoodsDef.sqkh = staff.gh;
            saleGoodsDef.sqktype = '1';
            saleGoodsDef.sqkzkfd = staff.privje1;
            
			// 记录日志
			String log = "授权单品折扣,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",折扣权限:" + grantzkl*100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
        }
        else
        {
        	// 本次授权折扣
        	grantzkl = curGrant.dpzkl;
        	
            // 记录授权工号
            saleGoodsDef.sqkh = cursqkh; 
            saleGoodsDef.sqktype = cursqktype;
            saleGoodsDef.sqkzkfd = cursqkzkfd;
        }
        
        // 计算权限允许的最大折扣率
        double maxzkl = getMaxRebateGrant(grantzkl,goodsDef);

        // 以最大折扣率模拟计算折扣,检查打折以后商品的折扣合计是否超出权限允许的折扣率
        saleGoodsDef.lszke = 0;
        saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje,2,1);
        
        if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)),2,1))
        {
            saleGoodsDef.lszke -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)),2,1);
            saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke,2,1);
        }
        
        if (saleGoodsDef.lszke < 0) saleGoodsDef.lszke = 0;
        {
        	saleGoodsDef.lszke = getConvertRebate(index, saleGoodsDef.lszke);
        }
        
        if (saleGoodsDef.lszke <= 0)
        {
        	// 恢复数据
            saleGoods.setElementAt(oldGoodsDef, index);
            
            new MessageBox("该商品暂不能打折!");

            return false;
        }

        // 根据模拟计算得到当前最大打折比例
        double lszkl = saleGoodsDef.lszke / saleGoodsDef.hjje;
        lszkl = ManipulatePrecision.doubleConvert((1 - lszkl) * 100, 2, 1);
       
        // 输入折扣
        StringBuffer buffer = new StringBuffer();
        if (!new TextBox().open("请输入单品折扣百分比(%)","单品折扣","当前收银员最大的单品折扣权限为 "+ManipulatePrecision.doubleToString(grantzkl * 100,2,1,true) + "%\n你目前最多在成交价基础上再打折 " + ManipulatePrecision.doubleToString(lszkl,2,1,true) + "%",buffer, lszkl, 100, true))
        {
        	// 恢复数据
            saleGoods.setElementAt(oldGoodsDef, index);
            
        	return false;
        }
        
        // 得到折扣率
        grantzkl = Double.parseDouble(buffer.toString());
        
        // 计算最终折扣
        saleGoodsDef.lszke = 0;
        saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((100 - grantzkl) / 100 * saleGoodsDef.hjje,2,1);
        
        double tempzk = 0;
        
        //比较两个临时折扣,同时有促销时，取最低折扣，无折上折
        if (saleGoodsDef.lszke >= (getZZK(saleGoodsDef) - saleGoodsDef.lszke))
        {
        	tempzk = saleGoodsDef.lszke;
        	
        	clearZZK(saleGoodsDef);
        	
        	saleGoodsDef.lszke = tempzk;
        }
        else
        {
        	saleGoodsDef.lszke = 0;
        }
        
        if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)),2,1))
        {
            saleGoodsDef.lszke -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)),2,1);
            saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke,2,1);
        }
        
        if (saleGoodsDef.lszke < 0) saleGoodsDef.lszke = 0;        
        
        saleGoodsDef.lszke = getConvertRebate(index, saleGoodsDef.lszke);
        
        // 重算商品折扣合计
        getZZK(saleGoodsDef);
        
        // 重算小票应收
        calcHeadYsje();
        
        //  是否积分
        if (saleGoodsDef.lszke > 0)
        {
        	saleGoodsDef.num1 = 1;
        	SpareInfoDef info = (SpareInfoDef)goodsSpare.get(index);
        	info.num1 = saleGoodsDef.num1;
        }
        else
        {
        	saleGoodsDef.num1 = 0;
        	SpareInfoDef info = (SpareInfoDef)goodsSpare.get(index);
        	info.num1 = saleGoodsDef.num1;
        }
       
        return true;
    }
    
    //输入折让金额
    public boolean inputRebatePrice(int index)
    {
    	double grantzkl = 0;
    	
    	// A模式指定小票退货不允许修改商品
    	if (isNewUseSpecifyTicketBack()) return false;
    	
        SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
        GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);    	
        
        // 服务费 以旧换新不处理
        if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
        {
            return false;
        }

        // 不能打折
        if (!checkGoodsRebate(goodsDef))
        {
            new MessageBox("该商品不允许打折\n或者\n不在你的授权范围内");

            return false;
        }

        // 备份数据
        SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();
                
        // 授权
        if ((curGrant.dpzkl * 100) >= 100)
        {
        	//
    		OperUserDef staff = inputRebateGrant(index);
    		if (staff == null) return false;

    		// 本次授权折扣
    		grantzkl = staff.dpzkl;

            // 记录授权工号
            saleGoodsDef.sqkh = staff.gh;
            saleGoodsDef.sqktype = '1';
            saleGoodsDef.sqkzkfd = staff.privje1;
            
			// 记录日志
			String log = "授权单品折让,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",折扣权限:" + grantzkl*100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);            
        }
        else
        {
        	// 本次授权折扣
        	grantzkl = curGrant.dpzkl;
        	
            // 记录授权工号
            saleGoodsDef.sqkh = cursqkh; 
            saleGoodsDef.sqktype = cursqktype;
            saleGoodsDef.sqkzkfd = cursqkzkfd;
        }
        
        // 计算权限允许的最大折扣额
        double maxzre = ManipulatePrecision.doubleConvert((1 - getMaxRebateGrant(grantzkl,goodsDef)) * saleGoodsDef.hjje,2,1);

        // 以最大折扣率模拟计算折扣,检查打折以后商品的折扣合计是否超出权限允许的折扣率
        
        // 根据模拟计算得到当前最大打折金额
        double lszre = saleGoodsDef.hjje - maxzre;
        lszre = ManipulatePrecision.doubleConvert(lszre, 2, 1);
    
        // 输入折让
        StringBuffer buffer = new StringBuffer();
        
        //折让是否采用成交价方式输入
        if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
        {
	        if (!new TextBox().open("请输入单品折让后的成交价","单品折让","当前收银员最大单品折扣权限为 "+ManipulatePrecision.doubleToString(grantzkl*100,2,1,true)+"%\n你目前对该商品最多只能折让到 " + ManipulatePrecision.doubleToString(lszre,2,1,true) + " 元",buffer, lszre,saleGoodsDef.hjje, true))
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
	        
	        saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - lszre,2,1);
        }
        else
        {
	        if (!new TextBox().open("请输入单品折让金额","单品折让","当前收银员最大单品折扣权限为 "+ManipulatePrecision.doubleToString(grantzkl*100,2,1,true)+"%\n 你目前对该商品最多折让金额为" + ManipulatePrecision.doubleToString(maxzre,2,1,true) + " 元",buffer, 0,maxzre, true))
	        {
	        	// 恢复数据
	            saleGoods.setElementAt(oldGoodsDef, index);
	            
	        	return false;
	        }

	        // 得到折让额
	        saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(Double.parseDouble(buffer.toString()),2,1);
        }
        
        
        double tempzre = 0;
        
        //比较两个临时折让,同时有促销时，取最低折扣，无折上折
        if (saleGoodsDef.lszre >= (getZZK(saleGoodsDef) - saleGoodsDef.lszre))
        {
        	tempzre = saleGoodsDef.lszre;
        	
        	clearZZK(saleGoodsDef);
        	
        	saleGoodsDef.lszre = tempzre;
        }
        else
        {
        	saleGoodsDef.lszre = 0;
        }
        
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
        
        //是否积分
        if (saleGoodsDef.lszre > 0)
        {
        	saleGoodsDef.num1 = 1;
        	SpareInfoDef info = (SpareInfoDef)goodsSpare.get(index);
        	info.num1 = saleGoodsDef.num1;
        }
        else
        {
        	saleGoodsDef.num1 = 0;
        	SpareInfoDef info = (SpareInfoDef)goodsSpare.get(index);
        	info.num1 = saleGoodsDef.num1;
        }
        
        return true;
    }
    
    //输入总折扣
    public boolean inputAllRebate()
    {
    	
    	if (saleGoods.size() <= 0)
        {
            return false;
        }

        double grantzkl = 0;
        SaleGoodsDef saleGoodsDef = null;
        GoodsDef goodsDef = null;
        
        // A模式指定小票退货不允许修改商品
        if (isNewUseSpecifyTicketBack()) return false;
        
        // 授权
        if ((curGrant.zpzkl * 100) >= 100)
        {
        	//
    		OperUserDef staff = inputAllRebateGrant();
    		if (staff == null) return false;

    		// 本次授权折扣
    		grantzkl = staff.zpzkl;

            // 记录授权工号
            saleHead.sqkh = staff.gh;
            saleHead.sqktype = '1';
            saleHead.sqkzkfd = staff.privje1;
            
			// 记录日志
			String log = "授权总品折扣,小票号:" + saleHead.fphm + ",折扣权限:" + grantzkl*100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);              
        }
        else
        {
        	// 本次授权折扣
        	grantzkl = curGrant.zpzkl;
        	
            // 记录授权工号
        	saleHead.sqkh = cursqkh; 
        	saleHead.sqktype = cursqktype;
        	saleHead.sqkzkfd = cursqkzkfd;
        }
        
        // 计算权限允许的最大折扣率
        double zkl = grantzkl * 100;
         
        // 输入折扣
        StringBuffer buffer = new StringBuffer();
        if (!new TextBox().open("请输入总品折扣百分比(%)","总品折扣","当前收银员最大的总品折扣权限为 "+ManipulatePrecision.doubleToString(grantzkl*100,2,1,true)+"%\n你目前最多在交易额基础上再打折 " + ManipulatePrecision.doubleToString(zkl,2,1,true) + "%",buffer, zkl, 100, true))
        {
        	return false;
        }
            
        // 得到折扣率
        grantzkl = Double.parseDouble(buffer.toString());
        
        // 循环为每个单品打折
        for (int i = 0; i < saleGoods.size(); i++)
        {
            saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
            goodsDef     = (GoodsDef) goodsAssistant.elementAt(i);

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
            
            // 计算商品权限允许的折扣
            double maxzzkl = getMaxRebateGrant(zkl/100,goodsDef);

            // 计算最终折扣
            saleGoodsDef.lszzk = 0;
            saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert((100 - grantzkl) / 100 * saleGoodsDef.hjje,2,1);
            
            double tempzk = 0;
            
            //比较两个临时折扣,同时有促销时，取最低折扣，无折上折
            if (saleGoodsDef.lszzk >= (getZZK(saleGoodsDef) - saleGoodsDef.lszzk))
            {
            	tempzk = saleGoodsDef.lszzk;
            	
            	clearZZK(saleGoodsDef);
            	
            	saleGoodsDef.lszzk = tempzk;
            }
            else
            {
            	saleGoodsDef.lszzk = 0;
            }
            
            if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzzkl)),2,1))
            {
            	// 提示
            	new MessageBox("[" + saleGoodsDef.code + "]" + saleGoodsDef.name + "\n\n最多能打折 " + ManipulatePrecision.doubleToString(maxzzkl*100) + "%");
            	
            	//
                saleGoodsDef.lszzk -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzzkl)),2,1);
                saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzk,2,1);
            }
            
            if (saleGoodsDef.lszzk < 0) saleGoodsDef.lszzk = 0;
            
            saleGoodsDef.lszzk = getConvertRebate(i, saleGoodsDef.lszzk);
                        
            // 重算商品折扣合计
            getZZK(saleGoodsDef);
        }
        
//      重算小票应收
        calcHeadYsje();  
        
        //  是否积分
        for (int i = 0; i < saleGoods.size(); i++)
        {
        	saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
        	
        	if (saleGoodsDef.lszzk > 0)
        	{
            	saleGoodsDef.num1 = 1;
            	SpareInfoDef info = (SpareInfoDef)goodsSpare.get(i);
            	info.num1= saleGoodsDef.num1;
        	}
        	else
        	{	
            	saleGoodsDef.num1 = 0;
            	SpareInfoDef info = (SpareInfoDef)goodsSpare.get(i);
            	info.num1= saleGoodsDef.num1;
        	}
        }
        
                  
                
        return true;
    }
    
    //输入总折让
    public boolean inputAllRebatePrice()
    {
     	if (saleGoods.size() <= 0)
        {
            return false;
        }

        double grantzkl = 0;
        SaleGoodsDef saleGoodsDef = null;
        GoodsDef goodsDef = null;
        
        // A模式指定小票退货不允许修改商品
        if (isNewUseSpecifyTicketBack()) return false;
        
        // 授权
        if ((curGrant.zpzkl * 100) >= 100)
        {
        	//
    		OperUserDef staff = inputAllRebateGrant();
    		if (staff == null) return false;
    		
    		// 本次授权折扣
    		grantzkl = staff.zpzkl;

            // 记录授权工号
            saleHead.sqkh = staff.gh;
            saleHead.sqktype = '1';
            saleHead.sqkzkfd = staff.privje1;
            
			// 记录日志
			String log = "授权总品折让,小票号:" + saleHead.fphm + ",折扣权限:" + grantzkl*100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);                          
        }
        else
        {
        	// 本次授权折扣
        	grantzkl = curGrant.zpzkl;
        	
            // 记录授权工号
        	saleHead.sqkh = cursqkh; 
        	saleHead.sqktype = cursqktype;
        	saleHead.sqkzkfd = cursqkzkfd;
        }

        // 计算权限允许的最大折扣额
        double zre = ManipulatePrecision.doubleConvert((1 - grantzkl) * saleHead.hjzje,2,1);

        //
        double lszzr = saleHead.hjzje - zre;
        lszzr = ManipulatePrecision.doubleConvert(lszzr, 2, 1);
        
        // 输入折让
        StringBuffer buffer = new StringBuffer();
        if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
        {
	        if (!new TextBox().open("请输入总品折让后的成交价","总品折让","当前收银员最大总品折扣权限为 "+ManipulatePrecision.doubleToString(grantzkl*100,2,1,true)+"%\n你目前对所有商品最多能折让到 " + ManipulatePrecision.doubleToString(lszzr,2,1,true) + " 元",buffer, lszzr,saleHead.hjzje, true))
	        {
	        	return false;
	        }
	        
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
	        
	        lszzr = saleHead.hjzje - saleHead.hjzke - Double.parseDouble(buffer.toString());
        }
        else
        {
        	if (!new TextBox().open("请输入总品折让金额","总品折让","当前收银员最大总品折扣权限为 "+ManipulatePrecision.doubleToString(grantzkl*100,2,1,true)+"%\n 你目前对所有商品最多能折让" + ManipulatePrecision.doubleToString(zre,2,1,true) + " 元",buffer, 0,zre, true))
	        {
	        	return false;
	        }
	        lszzr = Double.parseDouble(buffer.toString());
        }
        
        //不积分  
        for (int i = 0; i < saleGoods.size(); i++)
        {
        	saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
        	saleGoodsDef.num1 = 1;
          
          	SpareInfoDef info = (SpareInfoDef)goodsSpare.get(i);
            info.num1 = saleGoodsDef.num1;
        }
        
        // 得到折扣额
        lszzr = ManipulatePrecision.doubleConvert(lszzr, 2, 1);
        
        double hjzzr = 0,lastzzr = 0,vhjzzr = 0;
        int k = -1;
        
        // 循环为每个单品打折
        for (int i = 0; i < saleGoods.size(); i++)
        {
            saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
            goodsDef     = (GoodsDef) goodsAssistant.elementAt(i);

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
            
            // 计算商品权限允许的折扣
            double maxzzr = ManipulatePrecision.doubleConvert((1 - getMaxRebateGrant(grantzkl,goodsDef)) * saleGoodsDef.hjje,2,1);       

            // 取消其他手工折扣,计算最终折扣
            saleGoodsDef.sqkh = "";
            saleGoodsDef.sqktype = '\0';
                        
            if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
            {
	            saleGoodsDef.lszke = 0;
	            saleGoodsDef.lszre = 0;
	            saleGoodsDef.lszzk = 0;
	            saleGoodsDef.lszzr = 0;
	            saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje / saleHead.hjzje * lszzr,2,1);
            }
            else
            {
            	saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje / saleHead.hjzje * lszzr,2,1);
            }
           
            //比较两个临时总折让,同时有促销时，取最低折让，无折上折
            double tempzk = 0;
            
            if (saleGoodsDef.lszzr >= (getZZK(saleGoodsDef) - saleGoodsDef.lszzr))
            {
            	tempzk = saleGoodsDef.lszzr;
            	
            	clearZZK(saleGoodsDef);
            	
            	saleGoodsDef.lszzr = tempzk;
            }
            else
            {
            	vhjzzr += saleGoodsDef.lszzr;
            	
            	saleGoodsDef.lszzr = 0;
            }
            
            if (getZZK(saleGoodsDef) > maxzzr)
            {
                saleGoodsDef.lszzr -= getZZK(saleGoodsDef) - maxzzr;
                saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzr,2,1);
            }
            if (saleGoodsDef.lszzr < 0) saleGoodsDef.lszzr = 0;
            saleGoodsDef.lszzr = getConvertRebate(i, saleGoodsDef.lszzr);
            getZZK(saleGoodsDef);
            
            // 重算商品折扣合计
            hjzzr += saleGoodsDef.lszzr;
            
            // 找可折让金额最大的商品
            if (k == -1)
            {
            	lastzzr = maxzzr;
            	k = i;
            }
            else
            {
            	if (maxzzr > lastzzr)
            	{
            		lastzzr = maxzzr;
                	k = i;
            	}
            }
        }

        // 还有剩余的折让没有分配,则继续分配到金额最大的商品
        lszzr = ManipulatePrecision.doubleConvert(lszzr - hjzzr - vhjzzr,2,1);
        
        if (Math.abs(lszzr) > 0 && k >= 0)
		{
            saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(k);
            goodsDef     = (GoodsDef) goodsAssistant.elementAt(k);
            
            // 计算商品权限允许的折扣
            double maxzzr = ManipulatePrecision.doubleConvert((1 - getMaxRebateGrant(grantzkl,goodsDef)) * saleGoodsDef.hjje,2,1);       

            // 取消其他手工折扣,计算最终折扣
             saleGoodsDef.lszzr += lszzr; 
            
            if (getZZK(saleGoodsDef) > maxzzr)
            {
                saleGoodsDef.lszzr -= getZZK(saleGoodsDef) - maxzzr;
                saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzr,2,1);
            }
            if (saleGoodsDef.lszzr < 0) saleGoodsDef.lszzr = 0;
            saleGoodsDef.lszzr = getConvertRebate(k, saleGoodsDef.lszzr);

            // 重算商品折扣合计
            getZZK(saleGoodsDef);
		}
        
        // 重算小票应收
        calcHeadYsje();
        
        //  是否积分
        for (int i = 0; i < saleGoods.size(); i++)
        {
        	saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
        	
        	if (saleGoodsDef.lszzr > 0)
        	{
            	saleGoodsDef.num1 = 1;
            	SpareInfoDef info = (SpareInfoDef)goodsSpare.get(i);
            	info.num1= saleGoodsDef.num1;
        	}
        	else
        	{
            	saleGoodsDef.num1 = 0;
            	SpareInfoDef info = (SpareInfoDef)goodsSpare.get(i);
            	info.num1= saleGoodsDef.num1;
        	}
        }
       
               
        return true;
     }
    
    
    //输入营业员回车
    public void enterInputYYY()
    {
    	// 不输入营业员，认为超市销售
        if ((saleEvent.yyyh.getText().length() <= 0) && (GlobalInfo.syjDef.issryyy == 'A'))
        {
        	saleEvent.yyyh.setText("超市");
        	saleEvent.gz.setText("超市柜");
        	saleEvent.saleform.setFocus(saleEvent.code);
        	
            return;
        }

        //
        if (saleEvent.yyyh.getText().length() <= 0)
        {
            new MessageBox("营业员不能为空，请重新输入!", null, false);
            saleEvent.yyyh.selectAll();
            
            return;
        }

        // 查找营业员
        OperUserDef staff = null;
        if ((staff = findYYYH(saleEvent.yyyh.getText())) != null)
        {
        	// 
        	if (staff.type != '2') 
        	{
                new MessageBox("该工号不是营业员!", null, false);
                saleEvent.yyyh.selectAll();

                return;
        	}
        	
        	// 检查工号过期
            String expireDate = staff.maxdate + " 0:0:0";
            ManipulateDateTime mdt = new ManipulateDateTime();
            if (mdt.getDisDateTime(mdt.getDateBySlash() + " 0:0:0", expireDate) < 0)
            {
                new MessageBox("该工号已过期!", null, false);
                saleEvent.yyyh.selectAll();

                return;
            }
            
        	// 设置营业员柜组
        	saleEvent.gz.setText(staff.yyygz);
        	
            if (saleEvent.gz.getText().length() > 0 && (Bjys_CustomGlobalInfo.getDefault().sysPara.isinputcode == 'Y' || GlobalInfo.sysPara.yyygz == 'Y'))
            {
            	saleEvent.saleform.setFocus(saleEvent.code);               	
            }
            else
            {
            	saleEvent.saleform.setFocus(saleEvent.gz);
            }
        }
        else
        {
        	saleEvent.yyyh.selectAll();
        }
    }
    
    public int getMaxSalePayCount()
    {
    	return 1000000000;
    }
    
    public int payButtonToPayModePosition(int key)
    {
    	int k = -1;
    	PayModeDef paymode = null;
    	
		for (k = 0;k < GlobalInfo.payMode.size();k++)
		{
			paymode = (PayModeDef)(GlobalInfo.payMode.elementAt(k));
			
			if (key == GlobalVar.PayCash && paymode.type == '1') break;
			if (key == GlobalVar.PayCheque   && paymode.type == '2') break;
			if (key == GlobalVar.PayCredit  && paymode.type == '3') break;
			if (key == GlobalVar.PayMzk  && paymode.type == '4') break;
			if (key == GlobalVar.PayGift   && paymode.type == '5') break;
			if (key == GlobalVar.PayTally   && paymode.type == '6') break;
			
			String mktcode = null;
			
			if (GlobalInfo.sysPara.mktcode != null)
			{
				if (GlobalInfo.sysPara.mktcode.split(",").length >= 2)
				{
					mktcode = GlobalInfo.sysPara.mktcode.substring(GlobalInfo.sysPara.mktcode.indexOf(",")+1);
				}
				else
				{
					mktcode = GlobalInfo.sysPara.mktcode;
				}
			}
			
			//亮马桥:0001 金源:0005 太源:0006
			if (mktcode != null && !mktcode.trim().equals("0001"))
			{
				// 银联mis
				if (key == GlobalVar.PayBank && paymode.isbank == 'Y' && paymode.code.trim().equals("0341"))break;
				
				//	中行外卡
				if (key == GlobalVar.CustomKey4 && paymode.isbank == 'Y' && paymode.code.equals("0344")) break;
				
				// 积分卡
				if (key == GlobalVar.CustomKey5 && paymode.isbank == 'Y' && paymode.code.equals("0323")) break;
		
			}
			else
			{
				if (key == GlobalVar.PayBank && paymode.isbank == 'Y') break;
			}
			
		}
		
		//
		if (k >= GlobalInfo.payMode.size()) 
		{
			return super.payButtonToPayModePosition(key);
		}
		else
		{
			return k;
		}
    }
    
    
    public boolean saleSummary()
    {
    	if ((saleHead.hykh == null || saleHead.hykh.trim().length() <= 0) && curCustomer != null)
    	{	
    		saleHead.hykh = curCustomer.code;
    		
    		AccessDayDB.getDefault().writeWorkLog("会员卡出现丢失重新进行赋值:" + saleHead.fphm+","+saleHead.rqsj,"");
    	}
    	
    	for (int i = 0; i < saleGoods.size(); i++)
        {
    		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
    		
    		if (saleGoodsDef.inputbarcode != null && !saleGoodsDef.inputbarcode.equals(""))
    		{
    			saleGoodsDef.barcode	= saleGoodsDef.inputbarcode;
    		}
    		
    		if (saleGoodsDef.flag == '1')
    		{
    			new MessageBox("由于商品明细中含有赠品,不能进行退货处理\n请取消后重试!");
    			return false;
    		}
    		
        }
    	
    	return super.saleSummary();
    }
    
    //  人员授权
	public boolean operGrant()
	{
		OperUserDef staff = DataService.getDefault().personGrant();

		if (staff == null) return false;

		// 设置本笔交易授权
		curGrant.privth = staff.privth;
		curGrant.privqx = staff.privqx;
		curGrant.privdy = staff.privdy;
		curGrant.privgj = staff.privgj;
		curGrant.priv = staff.priv;
		curGrant.dpzkl = staff.dpzkl;
		curGrant.zpzkl = staff.zpzkl;
		curGrant.thxe = staff.thxe;
		curGrant.privje1 = staff.privje1;
		curGrant.privje2 = staff.privje2;
		curGrant.privje3 = staff.privje3;
		curGrant.privje4 = staff.privje4;
		curGrant.privje5 = staff.privje5;
		curGrant.grantgz = staff.grantgz;

		// 设置当前授权卡为员工卡
		cursqkh = staff.gh;
		cursqktype = '1';
		cursqkzkfd = staff.privje1;

		// 设置本笔小票员工授权卡号
		saleHead.ghsq = cursqkh;
		
		//提示
		new MessageBox("员工卡授权本笔交易");
		
		// 当前为退货交易，记录退货授权
		if (SellType.ISBACK(saletype))
		{
			saleHead.thsq = cursqkh;

			new MessageBox("授权退货,限额为 " + ManipulatePrecision.doubleToString(curGrant.thxe) + " 元");
		}

		return true;
	}
	
    public void refundMessageBox(String message)
    {
    	new MessageBox("退货积分不够扣回，请到会员中心查询");
    }
    
//  选择客户信息
	public void selectAllCustomerInfo()
	{
		String tempbuyerinfo = "";
		try
		{
			if (GlobalInfo.sysPara.custinfo.charAt(0) == 'T')
			{
				tempbuyerinfo = selectCustomerInfo("1");
				
				BuyInfoForm bif = new BuyInfoForm();
				bif.open();
			}
			else
			{
				for (int i = 0; i < GlobalInfo.sysPara.custinfo.substring(1).length(); i++)
				{
					if (GlobalInfo.sysPara.custinfo.substring(1).charAt(i) == 'Y')
					{
						tempbuyerinfo = tempbuyerinfo + selectCustomerInfo(String.valueOf(i + 1));
					}
					else
					{
						tempbuyerinfo += "00";
					}
				}
			}

			if (saleHead.buyerinfo.trim().length() >= 6)
			{
				saleHead.buyerinfo = tempbuyerinfo + saleHead.buyerinfo.substring(4, 6);
			}
			else
			{
				saleHead.buyerinfo = tempbuyerinfo;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public CustomerVipZklDef getGoodsVIPZKL(int index)
    {
		CustomerVipZklDef zklDef = super.getGoodsVIPZKL(index);
		
		if (zklDef.zkl <= 0) zklDef.zkl = 1;
//		zklDef.inareazkl = curCustomer.zkl;
//		zklDef.dnareazkl = curCustomer.zkl;
//		zklDef.upareazkl = curCustomer.zkl;
		if(zklDef.inareazkl<=0) zklDef.inareazkl =1;
		if(zklDef.dnareazkl<=0) zklDef.dnareazkl =1;
		if(zklDef.upareazkl<=0) zklDef.upareazkl =1;
		return zklDef;
    }
}
