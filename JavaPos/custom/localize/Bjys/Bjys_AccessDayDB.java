package custom.localize.Bjys;

import java.util.Vector;

import com.efuture.commonKit.ManipulateStr;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class Bjys_AccessDayDB extends AccessDayDB
{
	public boolean checkSaleData(SaleHeadDef saleHead, Vector saleGoods,Vector salePayment)
    {
		if (!super.checkSaleData(saleHead, saleGoods, salePayment)) return false;
		
		// 截取13位长条码
		for (int i = 0; i < saleGoods.size(); i++)
        {
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			
			if (saleGoodsDef.barcode.length() > 13) 
			{
				saleGoodsDef.barcode = ManipulateStr.interceptExceedStr(saleGoodsDef.barcode,14);
			}
			
			saleGoodsDef.barcode = ManipulateStr.delSpecialChar(saleGoodsDef.barcode);
			
			//将str3附加营业员一附给memo
			saleGoodsDef.memo = saleGoodsDef.str3;
			saleGoodsDef.str3 = "";
        }
		
		
		return true;
    }
	
	public boolean updateSaleJf(long fphm,int flag,double bcjf,double ljjf,double jfzk)
    {
        String line = "";
        
        try
        {
            switch(flag)
            {
            	case 1:
            		line = "update SALEHEAD set bcjf = " + bcjf + ",ljjf = " + ljjf + ",num1 =" + jfzk + " where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;
            		break;
            	case 2:
            		line = "update SALEHEAD set memo = '" + bcjf + ","+ ljjf + ","+ jfzk +"' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;
            		break;
            	default:
            		return false;
            }
        	
            if (GlobalInfo.dayDB.executeSql(line))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
            return false;
        }
    }
	
	public boolean writeSale(SaleHeadDef saleHead, Vector saleGoods,Vector salePayment)
    {
		for (int i = 0; i < saleGoods.size(); i++)
        {
    		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
    		
    		if (saleGoodsDef.flag == '1')
    		{
    			saleGoodsDef.flag = '4';
    			AccessDayDB.getDefault().writeWorkLog("小票中flag=1转换成flag=4:" + saleHead.fphm+","+saleHead.rqsj,"");
    		}
        }
		
		return super.writeSale(saleHead, saleGoods, salePayment);
    }
}
