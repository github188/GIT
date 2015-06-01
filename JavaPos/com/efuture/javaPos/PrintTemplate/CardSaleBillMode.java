package com.efuture.javaPos.PrintTemplate;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class CardSaleBillMode extends PrintTemplate
{
	protected static CardSaleBillMode cardSaleBillMode = null;
	
	protected SaleHeadDef salehead;
    protected Vector originalsalepay;
   
	protected int num = 0;
	private String paycodes = null;
	private String payname = null;
	protected double hj = 0;
	private boolean isLoad =  false;
	
	protected final static int CSBM_cardname = 100;
	protected final static int CSBM_amount = 101;
	protected final static int CSBM_hjje = 102;
	protected final static int CSBM_cardcode = 103;
	protected final static int CSBM_salemoney = 104;
	protected final static int CSBM_payye = 105;//扣款后余额
	protected final static int CSBM_payYye = 106;//扣款前余额
	protected final static int CSBM_batch = 107;//交易批次号
	
	
	public static CardSaleBillMode getDefault()
    {
        if (CardSaleBillMode.cardSaleBillMode == null)
        {
        	CardSaleBillMode.cardSaleBillMode = CustomLocalize.getDefault().createCardSaleBillMode();
        }

        return CardSaleBillMode.cardSaleBillMode;
    }
	
	public boolean ReadTemplateFile()
    {
		if (!CommonMethod.isFileExist(GlobalVar.ConfigPath + "//CardSalePrintMode.ini")) return true;
			
        super.InitTemplate();
        
        isLoad = super.ReadTemplateFile(Title,GlobalVar.ConfigPath + "//CardSalePrintMode.ini");
        return isLoad;
    }
	
	public void setTemplateObject(SaleHeadDef h, Vector s, Vector p)
    { 
		salehead = h;
    	originalsalepay = p;
    	
    	SaleBillMode.getDefault().setTemplateObject(h, s, p);
    }
		
	public String getItemDataString(PrintTemplateItem item, int index)
    {
        String line = null;
        SalePayDef pay = null;
        PayModeDef mode = null;
        
        try
        {
        	 line = extendCase(item, index);
        	 
        	 if (line == null)
             {
        		 switch (Integer.parseInt(item.code))
                 {
        		 	case CSBM_cardname:
	                	line = getPayName();
	                	break;
	                	
        		 	case CSBM_cardcode:        
        		 		pay = (SalePayDef) originalsalepay.elementAt(index);
                        mode = DataService.getDefault().searchPayMode(pay.paycode);
                        
		                if (isExistPaycode(mode.code))
		                {
		                	line = pay.payno;
		                	
		                	num = num + 1;
		                }
		                
		                break;
		                
        		 	case CSBM_salemoney:
        		 		pay = (SalePayDef) originalsalepay.elementAt(index);
                        mode = DataService.getDefault().searchPayMode(pay.paycode);
                        
		                if (isExistPaycode(mode.code))
		                {
        		 			line =  ManipulatePrecision.doubleToString(pay.ybje * SellType.SELLSIGN(salehead.djlb));
        		 			
        		 			if (pay.hl == 0)
		                    {
		                        pay.hl = 1;
		                    }
		
		                    hj += (pay.ybje * pay.hl * SellType.SELLSIGN(salehead.djlb));
		                }
		                break;
		                
        		 	case CSBM_amount :
    		 			line = ManipulatePrecision.doubleToString(num);	
    		 			break;	
        		 			
        		 	case CSBM_hjje:
        		 		line = ManipulatePrecision.doubleToString(hj);	 
        		 		break;
        		 		
        		 	case CSBM_payye:
        		 		pay = (SalePayDef) originalsalepay.elementAt(index);
                        mode = DataService.getDefault().searchPayMode(pay.paycode);
		                if (isExistPaycode(mode.code))
		                {
		                	line = ManipulatePrecision.doubleToString(pay.kye);	 
		                }
                        break;
                        
        		 	case CSBM_payYye:
        		 		pay = (SalePayDef) originalsalepay.elementAt(index);
                        mode = DataService.getDefault().searchPayMode(pay.paycode);
		                if (isExistPaycode(mode.code))
		                {
		                	line = ManipulatePrecision.doubleToString(pay.ybje * SellType.SELLSIGN(salehead.djlb) + pay.kye);	 
		                }
                        break;
        		 	case CSBM_batch:
        		 		pay = (SalePayDef) originalsalepay.elementAt(index);
                        mode = DataService.getDefault().searchPayMode(pay.paycode);
		                if (isExistPaycode(mode.code))
		                {
	        		 		if (null != pay.batch && !pay.batch.equals(""))
	            		 		line = pay.batch;
	        		 		else
	        		 			line = "";
		                }
                        break;
                        
        		 	default:
        		 		line = SaleBillMode.getDefault().getItemDataString(item, index);
                 }
             }
        	 
        	 return line;
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        	return null;
        }
    }
	
	public void setPayCodes(String paycodes)
	{
		this.paycodes = paycodes;
	}
	
	private String getPayCodes()
	{
		return paycodes;
	}
	
	public void setLoad(boolean isload)
	{
		this.isLoad = isload;
	}
	
	public boolean isLoad()
	{
		return isLoad;
	}
	
	public void setPayName(String payname)
	{
		this.payname = payname;
	}
	
	public String getPayName()
	{
		return payname;
	}
	
	public void printBill()
    {
		num = 0;
		hj = 0 ;
		
		// 设置打印方式
        printSetPage();

        // 打印头部区域
        printHeader();

        // 打印明细区域
        printDetail();
        
        // 打印尾部区域
        printBottom();
        
        // 打印付款区域
		printPay();
		
        // 打印汇总区域
        printTotal();

        // 切纸
        printCutPaper();
    }
	
	
	public void printPay()
    {
        // 设置打印区域
        setPrintArea("Pay");
        
        for (int i = 0;i < originalsalepay.size();i++)
        {	
        	printVector(getCollectDataString(Pay, i, Width));
        }
           
    }
	
	public boolean isExistPaycode(String paycode)
	{
		String paycodes[] = getPayCodes().split(",");
		
		for (int i = 0;i < paycodes.length;i++)
		{
			if (paycode.equals(paycodes[i])) return true;
		}
		
		return false;
	}
}
