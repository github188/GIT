package custom.localize.Bcrm;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentCustJfSale;
import com.efuture.javaPos.Payment.PaymentJfNew;
import com.efuture.javaPos.Struct.JfSaleRuleDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;


public class Bcrm_SaleBS2JfExchange extends Bcrm_SaleBS1CmPop
{	
	// 查找新的积分换购规则（积分促销）
    public void findNewJfExchangeGoods(int index)
    {
    	if (hhflag == 'Y')
    	{
    		new MessageBox(Language.apply("换货状态不允许使用积分换购"));
    		return ;
    	}
    	// 无会员卡不进行积分换购
    	if (curCustomer == null)
    	{
    		new MessageBox(Language.apply("没有刷会员卡不允许积分换购"));
    		return;
    	}

    	// 无0509付款方式,不能进行积分换购
		PayModeDef paymode = DataService.getDefault().searchPayMode("0509");
		if (paymode == null) 
		{
			new MessageBox(Language.apply("没有找到0509付款方式"));
			return;
		}
		
    	// 查找积分换购商品规则
    	JfSaleRuleDef jfrd = new JfSaleRuleDef();
    	SaleGoodsDef saleGoodsDef = (SaleGoodsDef)saleGoods.get(index);
    	
    	if (!((Bcrm_DataService)DataService.getDefault()).getJfExchangeGoods(jfrd,saleGoodsDef.code,saleGoodsDef.gz,curCustomer.code,curCustomer.type))
    	{
    		return;
    	}
		if ((saleGoodsDef.hjje - saleGoodsDef.hjzk) <= jfrd.money * saleGoodsDef.sl)
		{
			new MessageBox(Language.apply("当前商品销售金额小于等于兑换金额\n不能进行换购"));
			return;
		}
		
		if (saleGoodsDef.name.indexOf(Language.apply("【换】")) < 0) saleGoodsDef.name += Language.apply("【换】"); 
		
		double maxsl = -1;
		// 判断换购数量
		if (String.valueOf(jfrd.char1).length() > 0 && jfrd.char1 == 'Y')
		{
			double sum = 0;
	    	// 按商品行号查找对应的积分换购付款
	        for (int i = 0; i < saleGoods.size(); i++)
	        {
	            SpareInfoDef info = (SpareInfoDef)goodsSpare.elementAt(i);
	            SaleGoodsDef salegoods = (SaleGoodsDef) saleGoods.elementAt(i);
	            if (i == index) continue;
	            
	            if (info.char2 == 'Y' && salegoods.code.equals(saleGoodsDef.code))
	            {
	            	sum += salegoods.sl;
	            }
	        } 
	        
	        maxsl = ManipulatePrecision.doubleConvert(jfrd.num1 - sum);
	        
	        if (ManipulatePrecision.doubleConvert(sum + saleGoodsDef.sl) > jfrd.num1)
	        {
	        	// "包含此商品后，"+jfrd.str1+" 此规则已换购数量"+ManipulatePrecision.doubleConvert(sum + saleGoodsDef.sl)+"\n"
//	        	new MessageBox( saleGoodsDef.code+" 超出最大可换购数量【"+jfrd.num1+"】");
	        	new MessageBox( Language.apply("{0}超出最大可换购数量【{1}】" ,new Object[]{saleGoodsDef.code ,jfrd.num1+""}));
	        	return;
	        }
		}
		
		// 提示是否进行换购
//		MessageBox me = new MessageBox("您目前可用" + jfrd.jf + "积分加上" + ManipulatePrecision.doubleToString(jfrd.money) + "元\n换购该商品\n是否要进行换购?", null, true);
		MessageBox me = new MessageBox(Language.apply("您目前可用{0}积分加上{1}元\n换购该商品\n是否要进行换购?" ,new Object[]{jfrd.jf+"" ,ManipulatePrecision.doubleToString(jfrd.money)}), null, true);
 		if (me.verify() != GlobalVar.Key1)
		{
 			return;
		}
 		
		// 弹出提示框			
		StringBuffer buffer = new StringBuffer();
		double max = ManipulatePrecision.doubleConvert((int)(jfrd.num2 / jfrd.jf));
		
		// 如果存在限量
		if (maxsl > 0) max = Math.min(max, maxsl);
		
		if (max < 1) 
		{
			new MessageBox(Language.apply("换购类型积分余额不足"));
			return;
		}
		
		buffer.append(max);
		do{
//			if (new TextBox().open("请输入要兑换的数量","数量","目前最大可兑换的数量为"+ManipulatePrecision.doubleToString(max), buffer, 1,max, true, TextBox.IntegerInput, -1))
			if (new TextBox().open(Language.apply("请输入要兑换的数量"),Language.apply("数量"),Language.apply("目前最大可兑换的数量为{0}" ,new Object[]{ManipulatePrecision.doubleToString(max)}), buffer, 1,max, true, TextBox.IntegerInput, -1))
			{
				double inputsl = Convert.toDouble(buffer.toString());
				if (!inputQuantity(index,inputsl))
				{
					continue;
				}
					
			}
			else
			{
				return ;
			}
			break;
		}while(true);
 		
 		//先删除换购付款
 		delJfExchangeByGoods(index);
 		
 		SaleGoodsDef sgd = (SaleGoodsDef)saleGoodsDef.clone();
 		
		// 生成积分换购付款方式
 		PaymentJfNew pay = new PaymentJfNew(paymode,this); 
 		 
        // 记录单号，折扣及分担
 		double jfyhje;
 		sgd.yhdjbh = jfrd.str1;
 		jfyhje = ManipulatePrecision.doubleConvert(sgd.hjje - jfrd.num4 * sgd.sl - jfrd.money * sgd.sl);
 		sgd.yhzke = jfyhje;
 		if (sgd.yhzke < 0)
		{
			new MessageBox(Language.apply("负折扣 不允许换购"));
			return;
		}
 		sgd.yhzkfd = jfrd.num3;
        getZZK(sgd);
		
		double jf = getDetailOverFlow(ManipulatePrecision.doubleConvert((sgd.hjje - sgd.hjzk - jfrd.money * sgd.sl)));
		if (pay != null && pay.createJfExchangeSalePay(jf,ManipulatePrecision.mul(jfrd.jf,sgd.sl),jfrd))
		{
	 		// 转换名称用于显示
//	 		sgd.name = "(积分" + jfrd.jf + "+" + ManipulatePrecision.doubleToString(jfrd.money) + "元换购);" + sgd.name;
	 		sgd.name = Language.apply("(积分{0}+{1}元换购);" ,new Object[]{jfrd.jf+"" ,ManipulatePrecision.doubleToString(jfrd.money)}) + sgd.name;
	 		
			// 在付款对象记录商品信息(要扣的积分,XX积分,兑单个商品XX金额	，换购规则单号,商品编码，商品数量)
			// pay.salepay.str2 = String.valueOf(saleGoods.size()) + "," + sgd.code;
	 		// 扣减的积分,积分价值,规则单号,加价金额,原收银机,原小票,数量,商品编码
			pay.salepay.idno = jfrd.str2 + "," + ManipulatePrecision.mul(jfrd.jf,sgd.sl) + "," + jf +","+ jfrd.str1 +","+ ManipulatePrecision.mul(jfrd.money,sgd.sl) + ","  + sgd.sl + "," + sgd.code;
			// 积分种类
			pay.salepay.str4 = jfrd.str2;
			
			pay.salepay.payno = curCustomer.code;
			
			// 增加已付款
			addSalePayObject(pay.salepay,pay);
			
            SpareInfoDef info = (SpareInfoDef)goodsSpare.elementAt(index);
            
            // 积分换购商品标志
            info.char2 = 'Y';
            info.str3  = String.valueOf(pay.salepay.num5)+","+jfrd.str1;
            //记录积分扣回的分摊
            if (info.payft == null) info.payft = new Vector();
            String[] ft = new String[] {String.valueOf(pay.salepay.num5),pay.salepay.paycode,pay.salepay.payname,String.valueOf(jf)};
            info.payft.add(ft);
            
            // 记录单号，折扣及分担
     		saleGoodsDef.yhdjbh = jfrd.str1;
     		saleGoodsDef.yhzke = jfyhje;
     		saleGoodsDef.yhzkfd = jfrd.num3;
            getZZK(saleGoodsDef);
            
            calcHeadYsje();
			// 计算剩余付款
			calcPayBalance();
    		// 刷新商品列表
    		saleEvent.updateTable(getSaleGoodsDisplay());
    		saleEvent.setTotalInfo();
			saleEvent.table.modifyRow(rowInfo(sgd), index);
		}
		else
		{
			new MessageBox(Language.apply("积分换购付款对象创建失败\n请删除商品后重新试一次!"));
		}
		
		sgd = null;
    }
    
	// 查找商品是否存在换购规则
    public void findJfExchangeGoods(int index)
    {
        if (GlobalInfo.sysPara.custompayobj.indexOf("PaymentJfNew") >=0)
        {
        	findNewJfExchangeGoods(index);
        }
        else
        {
        	if (hhflag == 'Y')
        	{
        		new MessageBox(Language.apply("换货状态不允许使用积分换购"));
        		return ;
        	}
        	// 无会员卡不进行积分换购
        	if (curCustomer == null)
        	{
        		new MessageBox(Language.apply("没有刷会员卡不允许积分换购"));
        		return;
        	}

        	// 无0509付款方式,不能进行积分换购
    		PayModeDef paymode = DataService.getDefault().searchPayMode("0509");
    		if (paymode == null) 
    		{
    			new MessageBox(Language.apply("没有找到0509付款方式"));
    			return;
    		}
    		
        	// 查找积分换购商品规则
        	JfSaleRuleDef jfrd = new JfSaleRuleDef();
        	SaleGoodsDef saleGoodsDef = (SaleGoodsDef)saleGoods.get(index);
        	
        	if (!((Bcrm_DataService)DataService.getDefault()).getJfExchangeGoods(jfrd,saleGoodsDef.code,saleGoodsDef.gz,curCustomer.code,curCustomer.type))
        	{
        		return;
        	}
    		if ((saleGoodsDef.hjje - saleGoodsDef.hjzk) <= jfrd.money * saleGoodsDef.sl)
    		{
    			new MessageBox(Language.apply("当前商品销售金额小于等于兑换金额\n不能进行换购"));
    			return;
    		}
    		
    		if (curCustomer.valuememo < jfrd.jf)
     		{
    			new MessageBox(Language.apply("当前会员卡的积分小于换购积分\n不能进行换购"));
    			return;
     		}

    		if (saleGoodsDef.name.indexOf(Language.apply("【换】")) < 0) saleGoodsDef.name += Language.apply("【换】"); 
    		
    		double maxsl = -1;
    		// 判断换购数量
    		if (String.valueOf(jfrd.char1).length() > 0 && jfrd.char1 == 'Y')
    		{
    			double sum = 0;
    	    	// 按商品行号查找对应的积分换购付款
    	        for (int i = 0; i < saleGoods.size(); i++)
    	        {
    	            SpareInfoDef info = (SpareInfoDef)goodsSpare.elementAt(i);
    	            SaleGoodsDef salegoods = (SaleGoodsDef) saleGoods.elementAt(i);
    	            if (i == index) continue;
    	            
    	            if (info.char2 == 'Y' && salegoods.code.equals(saleGoodsDef.code))
    	            {
    	            	sum += salegoods.sl;
    	            }
    	        } 
    	        
    	        maxsl = ManipulatePrecision.doubleConvert(jfrd.num1 - sum);
    	        
    	        if (ManipulatePrecision.doubleConvert(sum + saleGoodsDef.sl) > jfrd.num1)
    	        {
    	        	// "包含此商品后，"+jfrd.str1+" 此规则已换购数量"+ManipulatePrecision.doubleConvert(sum + saleGoodsDef.sl)+"\n"
//    	        	new MessageBox( saleGoodsDef.code+" 超出最大可换购数量【"+jfrd.num1+"】");
    	        	new MessageBox( Language.apply("{0}超出最大可换购数量【{1}】" ,new Object[]{saleGoodsDef.code ,jfrd.num1+""}));
    	        	return;
    	        }
    		}
    		
    		// 提示是否进行换购
//    		MessageBox me = new MessageBox("您目前可用" + jfrd.jf + "积分加上" + ManipulatePrecision.doubleToString(jfrd.money) + "元\n换购该商品\n是否要进行换购?", null, true);
    		MessageBox me = new MessageBox(Language.apply("您目前可用{0}积分加上{1}元\n换购该商品\n是否要进行换购?" ,new Object[]{jfrd.jf+"" ,ManipulatePrecision.doubleToString(jfrd.money)}), null, true);
     		if (me.verify() != GlobalVar.Key1)
    		{
     			return;
    		}
     		
    		// 弹出提示框			
    		StringBuffer buffer = new StringBuffer();
    		double max = ManipulatePrecision.doubleConvert((int)(curCustomer.valuememo/jfrd.jf));
    		
    		// 如果存在限量
    		if (maxsl > 0) max = Math.min(max, maxsl);
    		
    		buffer.append(max);
    		do{
//    			if (new TextBox().open("请输入要兑换的数量","数量", "目前最大可兑换的数量为"+ManipulatePrecision.doubleToString(max), buffer, 1,max, true, TextBox.IntegerInput, -1))
    			if (new TextBox().open(Language.apply("请输入要兑换的数量"),Language.apply("数量"),Language.apply("目前最大可兑换的数量为{0}" ,new Object[]{ManipulatePrecision.doubleToString(max)}), buffer, 1,max, true, TextBox.IntegerInput, -1))
    			{
    				double inputsl = Convert.toDouble(buffer.toString());
    				if (!inputQuantity(index,inputsl))
    				{
    					continue;
    				}
    					
    			}
    			else
    			{
    				return ;
    			}
    			break;
    		}while(true);
     		
     		//先删除换购付款
     		delJfExchangeByGoods(index);
     		
     		SaleGoodsDef sgd = (SaleGoodsDef)saleGoodsDef.clone();
     		
    		// 生成积分换购付款方式
//    		PaymentCustJfSale pay = new PaymentCustJfSale(paymode,this);
     		PaymentCustJfSale pay = CreatePayment.getDefault().getPaymentJfChange(paymode,this);
    		
    		double jf = getDetailOverFlow(ManipulatePrecision.doubleConvert((sgd.hjje - sgd.hjzk) - (jfrd.money * sgd.sl)));
    		if (pay != null && pay.createJfExchangeSalePay(jf,ManipulatePrecision.mul(jfrd.jf,sgd.sl),jfrd,index))
    		{
    	 		// 转换名称用于显示
//    	 		sgd.name = "(积分" + jfrd.jf + "+" + ManipulatePrecision.doubleToString(jfrd.money) + "元换购);" + sgd.name;
    			sgd.name = Language.apply("(积分{0}+{1}元换购);" ,new Object[]{jfrd.jf+"" ,ManipulatePrecision.doubleToString(jfrd.money)}) + sgd.name;
    	 		
    			// 在付款对象记录商品信息(要扣的积分,XX积分,兑单个商品XX金额	，换购规则单号,商品编码，商品数量)
    			// pay.salepay.str2 = String.valueOf(saleGoods.size()) + "," + sgd.code;
    			pay.salepay.idno += ","+jfrd.str1+","+sgd.code+","+sgd.sl;
    			
    			// 增加已付款
    			addSalePayObject(pay.salepay,pay);
    			
                SpareInfoDef info = (SpareInfoDef)goodsSpare.elementAt(index);
                
                // 积分换购商品标志
                info.char2 = 'Y';
                info.str3  = String.valueOf(pay.salepay.num5)+","+jfrd.str1;
                //记录积分扣回的分摊
                if (info.payft == null) info.payft = new Vector();
                String[] ft = new String[] {String.valueOf(pay.salepay.num5),pay.salepay.paycode,pay.salepay.payname,String.valueOf(jf)};
                info.payft.add(ft);
                
    			// 计算剩余付款
    			calcPayBalance();
    			
    			saleEvent.table.modifyRow(rowInfo(sgd), index);
    		}
    		else
    		{
    			new MessageBox(Language.apply("积分换购付款对象创建失败\n请删除商品后重新试一次!"));
    		}
    		
    		sgd = null;
        }
    }
    
    public void addMemoPayment()
    {
    	super.addMemoPayment();
    	
    	if (!SellType.ISPREPARETAKE(saleHead.djlb) && !saleHead.djlb .equals( SellType.PREPARE_BACK))
    	{
	    	// 在存在积分换购时，积分换购的付款方式放入memoPayment里， 如果salePayment大于0代表已经放入付款类表里，不重新放入
	    	if (isNewUseSpecifyTicketBack(false) && salePayment.size() <= 0)
	    	{
	    		for (int i = 0; i < memoPayment.size(); i ++)
	    		{
	    			Payment pay = (Payment) memoPayment.elementAt(i);
	    			
	    			if (pay.salepay != null) addSalePayObject(pay.salepay, pay);
	    		}
	    	}
    	}
    }
    
    public void takeBackTicketInfo(SaleHeadDef thsaleHead,Vector thsaleGoods,Vector thsalePayment)
    {
    	//在断点保护的情况下，可能出现退货付款方式存入2次的情况，加以判断
    	memoPayment.removeAllElements();
    	
    	//goodsSpare = new Vector();
    	for (int i=0;i<saleGoods.size();i++)
    	{
    		SaleGoodsDef sgd = (SaleGoodsDef)saleGoods.elementAt(i);
    		
    		//liwenjin Add 在退货时添加对应的分摊类 
    		//goodsSpare.add(new SpareInfoDef());
    		
    		// 小票头记录原单号
			saleHead.yfphm = String.valueOf(sgd.yfphm);
			saleHead.ysyjh = sgd.ysyjh;
		
			// 积分换购付款方式处理
			if (sgd.str2.indexOf("0509") >= 0)
			{
				int st = sgd.str2.lastIndexOf(",",sgd.str2.indexOf("0509"));
				int end = sgd.str2.indexOf(",", sgd.str2.indexOf("0509"));
				if (st <= 0) st = 0;
				if (end <= 0) end = sgd.str2.length();
				String line = sgd.str2.substring(st, end);
				
				if (line.charAt(0) == ',') line .substring(1);
				
				String rowno = line.substring(0, line.indexOf(":"));
				int rowno1 = -1;
				try
				{
					rowno1 = Integer.parseInt(rowno);
					rowno1 --;
				}
				catch(Exception er)
				{
					er.printStackTrace();
				}
				
				if (rowno1 >= 0)
				{
					SalePayDef spd1 = (SalePayDef) thsalePayment.elementAt(rowno1);
					spd1.syjh = ConfigClass.CashRegisterCode;
					if (spd1.paycode.equals("0509"))
					{
						PayModeDef pmd = DataService.getDefault().searchPayMode("0509");
						Payment pay;
				        if (GlobalInfo.sysPara.custompayobj.indexOf("PaymentJfNew") >=0)
				        {
				        	pay = new PaymentJfNew(pmd, this);
				        }
				        else
				        {
				        	pay = new PaymentCustJfSale(pmd,this);
				        }
//						PaymentCustJfSale pay = new PaymentCustJfSale(pmd,this);
						
						if (sgd.sl < sgd.memonum1)
						{
							String salepaylist[] = spd1.idno.split(",");
							double jf = Double.parseDouble(salepaylist[0]);
							jf = ManipulatePrecision.doubleConvert(jf * (sgd.sl/sgd.memonum1));
							spd1.je = ManipulatePrecision.doubleConvert(spd1.je * (sgd.sl/sgd.memonum1));
							spd1.idno = jf+","+spd1.idno.substring(spd1.idno.indexOf(",")+1);
						}
						
						pay.salepay = spd1;
						memoPayment.add(pay);
					}
				}				
			}
			
			// 满抵付款方式处理
			boolean hasMdPay = true;
			String mdCode[] = GlobalInfo.sysPara.mdcode.split(",");
			if (mdCode[0].trim().equals(""))
			{
				hasMdPay = false;
			}
			PayModeDef paymode = DataService.getDefault().searchPayMode(mdCode[0]);
			if (paymode == null)
			{
				hasMdPay = false;
			}

			if (hasMdPay && sgd.str2.trim().length() > 0)
			{
				String[] total = sgd.str2.trim().split(",");
				if (total.length > 0)
				{
					String[] deatil;
					for (int k = 0; k < total.length; k++)
					{
						deatil = total[k].split(":");
						if (deatil.length == 3 && deatil[1].equals(mdCode[0]))
						{
							sgd.num6 = Double.parseDouble(deatil[2]);
						}
					}
				}
			}
			
			// 将原付款分摊删除
			sgd.str2 = "";
    	}
    }

    public boolean isJfExchangeSalePay(SalePayDef spd)
    {
    	if (spd.paycode.trim().equals("0509") && spd.memo.trim().equals("2"))
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    public boolean delSaleGoodsObject(int index)
    {
    	// 删除商品前先删除对应的积分换购付款
    	if (!delJfExchangeByGoods(index)) return false;

        // 删除商品
        if (!super.delSaleGoodsObject(index))
        {
            return false;
        }

        return true;
    }
    
    public boolean delJfExchangeByGoods(int index)
    {
    	SpareInfoDef info = (SpareInfoDef)goodsSpare.elementAt(index);
    	
    	if (info != null && info.char2 == 'Y')
    	{
    		int seq = -1;
    		seq = Convert.toInt(info.str3.split(",")[0]);
	        if (seq > -1)
	        {
	        	// 检查付款方式里面对应的唯一键
	            for (int i = 0; i < salePayment.size(); i++)
	            {
	                SalePayDef spd = (SalePayDef)salePayment.get(i);
	                if (isJfExchangeSalePay(spd) && spd.num5 == seq)
	                {
	                	if (!deleteSalePay(i))
	                    {
	                        new MessageBox(Language.apply("删除商品的积分换购付款失败!"));
	
	                        return false;
	                    }
	                }
	            } 
	        }
    	}
        
        return true;
    }
        
    public boolean deleteAllSalePay()
    {
    	Vector tempSalePayment = null;
    	Vector tempPayAssistant = null;
    	
    	try
    	{
	    	tempSalePayment = new Vector();
	    	tempPayAssistant = new Vector();
	    	
	    	// 先保存换购付款
	    	for (int i = 0;i < salePayment.size();i++)
	    	{
	    		SalePayDef tempspay = (SalePayDef)salePayment.elementAt(i);
	    		Payment tempp = (Payment)payAssistant.elementAt(i);
	    		
	    		if (isJfExchangeSalePay(tempspay))
	    		{
	    			tempSalePayment.add(tempspay);
	    			tempPayAssistant.add(tempp);
	    		}
	    	}
	    	
	    	// 删除所有付款
	    	if (!super.deleteAllSalePay()) return false;
	    	
	    	// 恢复换购的付款
	    	for (int i = 0;i < tempSalePayment.size() ; i++)
	    	{
	    		salePayment.add(tempSalePayment.elementAt(i));
	    		payAssistant.add(tempPayAssistant.elementAt(i));
	    		
	    		// 重新检查是否有积分换购的商品，重新记录分摊
	    		for (int j = 0; j < saleGoods.size(); j++)
	    		{
	    			SpareInfoDef info = (SpareInfoDef)goodsSpare.elementAt(j);
	    			if (info.char2 == 'Y')
	    			{
	    				int seqnum = Convert.toInt(info.str3.split(",")[0]);
	    		    	// 先保存换购付款
	    		    	for (int k = 0;k < salePayment.size();k++)
	    		    	{
	    		    		SalePayDef tempspay = (SalePayDef)salePayment.elementAt(i);
	    		    		if (tempspay.num5 == seqnum)
	    		    		{
	    		    			if (info.payft == null) info.payft = new Vector();
	    		                String[] ft = new String[] {String.valueOf(tempspay.num5),tempspay.paycode,tempspay.payname,ManipulatePrecision.doubleToString(tempspay.je - tempspay.num1)};
	    		                info.payft.add(ft);
	    		                break;
	    		    		}
	    		    	}
	    			}
	    		}
	    	}
	    	
	    	return true;
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		return false;
    	}
    	finally
    	{
    		if (tempSalePayment != null)
    		{
    			tempSalePayment.clear();
    			tempSalePayment = null;
    		}
    		
    		if (tempPayAssistant != null)
    		{
    			tempPayAssistant.clear();
    			tempPayAssistant = null;
    		}
    	}
    }

    public boolean inputQuantity(int index)
    {
    	if (goodsSpare != null && goodsSpare.size() > index)
    	{
	    	SpareInfoDef info = (SpareInfoDef)goodsSpare.elementAt(index);
	    	if (info != null && info.char2 == 'Y')
	    	{
	    		new MessageBox(Language.apply("此商品为换购商品，不允许修改数量"));
	    		return false;
	    	}
    	}
    	return super.inputQuantity(index);
    }
}
