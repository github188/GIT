package custom.localize.Cbbh;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Cbbh_AccessDayDB extends AccessDayDB
{
	public boolean checkLastInvoice(SaleHeadDef saleHead)
    {
    	ResultSet rs = null;
    	boolean already = false;
    	
    	try
    	{
	    	rs = GlobalInfo.dayDB.selectData("select max(fphm) from SALEHEAD");
	    	if (rs != null && rs.next())
	    	{
	    		long lastfphm = rs.getLong(1);
	    		if (lastfphm != 0 && lastfphm != saleHead.fphm - 1 && !already)
	    		{
//		    		if (new MessageBox("上笔交易小票号("+lastfphm+")和当前交易小票号("+saleHead.fphm+")不连续\n\n你确定要继续完成交易吗？",null,true).verify() != GlobalVar.Key1)
		    		/*if (new MessageBox(Language.apply("上笔交易小票号({0})和当前交易小票号({1})不连续\n\n你确定要继续完成交易吗？",new Object[]{lastfphm+"" ,saleHead.fphm+""}),null,true).verify() != GlobalVar.Key1)
		    		{
		    			return false;
		    		}
		    		else
		    		{
		    			already = true;
//		    			AccessDayDB.getDefault().writeWorkLog("上笔交易小票号("+lastfphm+")和当前交易小票号("+saleHead.fphm+")不连续");
		    			AccessDayDB.getDefault().writeWorkLog(Language.apply("上笔交易小票号({0})和当前交易小票号({1})不连续", new Object[]{lastfphm+"" ,saleHead.fphm+""}));
		    		}*/
		    		new MessageBox("提示：\n\n上笔交易小票号("+lastfphm+")和当前交易小票号("+saleHead.fphm+")不连续");
		    		already = true;//	    			
	    			AccessDayDB.getDefault().writeWorkLog(Language.apply("上笔交易小票号({0})和当前交易小票号({1})不连续", new Object[]{lastfphm+"" ,saleHead.fphm+""}));
	    		
	    		}
	    	}
	    	GlobalInfo.dayDB.resultSetClose();
	    	
	    	rs = GlobalInfo.dayDB.selectData("select max(fphm) from SALEGOODS");
	    	if (rs != null && rs.next())
	    	{
	    		long lastfphm = rs.getLong(1);
	    		if (lastfphm != 0 && lastfphm != saleHead.fphm - 1 && !already)
	    		{
//		    		if (new MessageBox("上笔交易小票号("+lastfphm+")和当前交易小票号("+saleHead.fphm+")不连续\n\n你确定要继续完成交易吗？",null,true).verify() != GlobalVar.Key1)
	    			/*if (new MessageBox(Language.apply("上笔交易小票号({0})和当前交易小票号({1})不连续\n\n你确定要继续完成交易吗？",new Object[]{lastfphm+"" ,saleHead.fphm+""}),null,true).verify() != GlobalVar.Key1)
		    		{
		    			return false;
		    		}
		    		else
		    		{
		    			already = true;
		    			AccessDayDB.getDefault().writeWorkLog(Language.apply("上笔交易小票号({0})和当前交易小票号({1})不连续", new Object[]{lastfphm+"" ,saleHead.fphm+""}));
		    		}*/
	    			
	    			new MessageBox("提示：\n\n上笔交易小票号("+lastfphm+")和当前交易小票号("+saleHead.fphm+")不连续");
		    		already = true;//	    			
	    			AccessDayDB.getDefault().writeWorkLog(Language.apply("上笔交易小票号({0})和当前交易小票号({1})不连续", new Object[]{lastfphm+"" ,saleHead.fphm+""}));
	    		
	    		}
	    	}
	    	GlobalInfo.dayDB.resultSetClose();

	    	rs = GlobalInfo.dayDB.selectData("select max(fphm) from SALEPAY");
	    	if (rs != null && rs.next())
	    	{
	    		long lastfphm = rs.getLong(1);
	    		if (lastfphm != 0 && lastfphm != saleHead.fphm - 1 && !already)
	    		{
	    			/*if (new MessageBox(Language.apply("上笔交易小票号({0})和当前交易小票号({1})不连续\n\n你确定要继续完成交易吗？",new Object[]{lastfphm+"" ,saleHead.fphm+""}),null,true).verify() != GlobalVar.Key1)
		    		{
		    			return false;
		    		}
		    		else
		    		{
		    			already = true;
		    			AccessDayDB.getDefault().writeWorkLog(Language.apply("上笔交易小票号({0})和当前交易小票号({1})不连续", new Object[]{lastfphm+"" ,saleHead.fphm+""}));
		    		}*/
	    			
	    			new MessageBox("提示：\n\n上笔交易小票号("+lastfphm+")和当前交易小票号("+saleHead.fphm+")不连续");
		    		already = true;//	    			
	    			AccessDayDB.getDefault().writeWorkLog(Language.apply("上笔交易小票号({0})和当前交易小票号({1})不连续", new Object[]{lastfphm+"" ,saleHead.fphm+""}));
	    		
	    		}
	    	}
	    	GlobalInfo.dayDB.resultSetClose();
	    	
	    	return true;
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		new MessageBox(Language.apply("检查上笔交易时发生异常\n\n")+ ex.getMessage());
    		return false;
    	}
    	finally
    	{
    		GlobalInfo.dayDB.resultSetClose();
    	}
    }
	/*public boolean getlasthhbackinfo(String syjh,StringBuffer buff)
	{
		try{
			Object obj = GlobalInfo.dayDB.selectOneData("select fphm from salehead where syjh = '"+syjh+"'  order by fphm desc");
			
	        if (obj == null)
	        {
	            return false;
	        }
	        else
	        {
	            long fphm = Long.parseLong(String.valueOf(obj));
	            
	            if (fphm > 0)
	            {
	            	buff.append(String.valueOf(fphm));
	            	return gethhbackinfo(syjh,String.valueOf(fphm));
	            }
	            else
	            {
	            	return false;
	            }
	        }
		}catch(Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}
	
	public boolean gethhbackinfo(String syjh,String fphm)
	{
		boolean done = false;
		try{
			Object obj = GlobalInfo.dayDB.selectOneData("select count(*) from salehead where syjh = '"+syjh+"' and fphm = "+fphm+" and hhflag = 'Y' and djlb = '4'");
			
	        if (obj == null)
	        {
	            return done;
	        }
	        else
	        {
	            long seqno = Long.parseLong(String.valueOf(obj));
	            
	            if (seqno > 0)
	            {
	            	done = true;
	            	return done;
	            }
	            else
	            {
	            	return false;
	            }
	        }
		}
		catch(Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}
	
	public double gethhbackYsje(String syjh,String fphm)
	{
		try{
			Object obj = GlobalInfo.dayDB.selectOneData("select ysje from salehead where syjh = '"+syjh+"' and fphm = "+fphm+" and hhflag = 'Y' and djlb = '4'");
			
	        if (obj == null)
	        {
	            return -1;
	        }
	        else
	        {
	            double ysje = Double.parseDouble(String.valueOf(obj));
	            if (ysje > 0)
	            {
	            	return ysje;
	            }
	            else
	            {
	            	return -1;
	            }
	        }
		}
		catch(Exception er)
		{
			er.printStackTrace();
			return -1;
		}
	}
	public boolean getlasthhbackHead(SaleHeadDef salehead,String syjh)
	{
		ResultSet rs = null;  
		 if ((rs = GlobalInfo.dayDB.selectData("select * from salehead where syjh = '"+syjh+"'  order by fphm desc")) != null)
	     {
	         try
			{
				if (rs.next())
				 {
				 	if (!GlobalInfo.dayDB.getResultSetToObject(salehead))
				     {
				 		return false;
				     }
				 }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				return false;
			}
	         
	         return true;
	     }
		 return false;
	}
	public long getHcHHbackinfo(String syjh,String fphm)
	{
		long ret = -1;
		try{
			Object obj = GlobalInfo.dayDB.selectOneData("select fphm from salehead where syjh = '"+syjh+"'  order by fphm desc");
	        if (obj == null)
	        {
	            return ret;
	        }
	        else
	        {
	            fphm = String.valueOf(Long.parseLong(String.valueOf(obj)));
	        }
			
			obj = GlobalInfo.dayDB.selectOneData("select a.yfphm from salegoods a,salehead b where a.fphm = b.fphm and b.syjh = '"+syjh+"' and b.fphm = "+fphm+" and b.hhflag = 'Y' and b.djlb = '2'");
			
	        if (obj == null)
	        {
	            return ret;
	        }
	        else
	        {
	            long fphm1 = Long.parseLong(String.valueOf(obj));
	            
	            if (fphm1 > 0)
	            {
	            	ret = fphm1;
	            	return ret;
	            }
	            else
	            {
	            	return ret;
	            }
	        }
		}
		catch(Exception er)
		{
			er.printStackTrace();
			return ret;
		}
	}*/
	
	public boolean checkSaleData(SaleHeadDef saleHead, Vector saleGoods,Vector salePayment)
    {
        int i;
        double je,zl;
        SaleGoodsDef saleGoodsDef = null;
        SalePayDef salePayDef = null;
        
        // 检查交易类型
        if (!SellType.VALIDTYPE(saleHead.djlb))
        {
//            new MessageBox("[" + saleHead.djlb + "]交易类型无效!\n不允许进行【"+SellType.getDefault().typeExchange( saleHead.djlb, saleHead.hhflag, saleHead)+"】");
            new MessageBox(Language.apply("[{0}]交易类型无效!\n不允许进行【{1}】", new Object[]{saleHead.djlb,SellType.getDefault().typeExchange( saleHead.djlb, saleHead.hhflag, saleHead)}));
            
            return false;
        }
        if(SellType.HH_BACK.equals(saleHead.djlb) || SellType.HH_SALE.equals(saleHead.djlb))
//        if(SellType.HH_BACK.equals(saleHead.djlb))
        {
        	PosLog.getLog(this.getClass()).info("换货[大换小]不检查平衡");
        	return true;
        }
        // 反算合计折扣额
        if (saleHead.hjzje == 0)
        {
        	saleHead.hjzje = ManipulatePrecision.doubleConvert(saleHead.ysje + saleHead.hjzke,2,1);
        }

        // 检查销售主单平衡
        if (salePayment.size() > 0 &&
        	ManipulatePrecision.doubleCompare(saleHead.sjfk - saleHead.zl,saleHead.ysje + saleHead.sswr_sysy + saleHead.fk_sysy,2) != 0) 
        {
        	new MessageBox(Language.apply("交易主单数据相互不平!\n\n实际付款 - 找零") + " = "+
        	ManipulatePrecision.doubleToString(saleHead.sjfk - saleHead.zl)+
        	Language.apply("\n应收金额 + 损溢 ") + " = "+ManipulatePrecision.doubleToString(saleHead.ysje + saleHead.sswr_sysy + saleHead.fk_sysy));
            return false;
        }
        if (ManipulatePrecision.doubleCompare(saleHead.ysje,saleHead.hjzje - saleHead.hjzke,2) != 0)
        {
        	new MessageBox(Language.apply("交易主单数据相互不平!\n\n应收金额") + " = "+
        	ManipulatePrecision.doubleToString(saleHead.ysje)+
        	Language.apply("\n合计金额 - 合计折扣") + " = "+ManipulatePrecision.doubleToString(saleHead.hjzje - saleHead.hjzke));
            return false;
        }
        
        if (ManipulatePrecision.doubleCompare(saleHead.hjzke,saleHead.yhzke + saleHead.hyzke + saleHead.lszke,2) != 0)
        {
        	new MessageBox(Language.apply("交易主单数据相互不平!\n\n合计折扣") + " = "+
        	ManipulatePrecision.doubleToString(saleHead.hjzke)+
        	Language.apply("\n折扣明细") + " = "+ManipulatePrecision.doubleToString(saleHead.yhzke + saleHead.hyzke + saleHead.lszke));
            return false;
        }
        
        // 检查主单和商品明细之间的平衡
        je = 0;
        for (i = 0; i < saleGoods.size(); i++)
        {
            saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
            
			// 零头折扣记入LSZRE
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre + saleGoodsDef.ltzke);
			saleGoodsDef.ltzke = 0;
            
            if (saleGoodsDef.flag == '0')
            {
            	continue;
            }
            
//          退货数量为0，为不退商品，需要传给后台计算促销
			if(!SellType.ISBACK(saleHead.djlb))
			{
	            if (saleGoodsDef.sl == 0 || saleGoodsDef.sl < 0)
	            {
	//            	new MessageBox("第 " + (i + 1) + " 行商品 [" + saleGoodsDef.code + "] 数量不合法\n请修改此行商品数量或者删除此商品后重新录入");
	            	new MessageBox(Language.apply("第") + (i + 1) + Language.apply("行商品 [{0}] 数量不合法\n请修改此行商品数量或者删除此商品后重新录入", new Object[]{saleGoodsDef.code}));
	            	return false;
	            }
			}
            
            if (saleGoodsDef.type == '8' || (saleGoodsDef.str13!=null && saleGoodsDef.str13.equalsIgnoreCase("T")))
            {
                je -= saleGoodsDef.hjje - saleGoodsDef.hjzk;
            }
            else
            {
                je += saleGoodsDef.hjje - saleGoodsDef.hjzk;
            }
        }
        if (ManipulatePrecision.doubleCompare(saleHead.ysje,je,2) != 0)
        {
        	new MessageBox(Language.apply("交易主单和商品明细不平!\n\n主单应收金额") + " = "+
        	ManipulatePrecision.doubleToString(saleHead.ysje)+
        	Language.apply("\n商品合计金额") + " = "+ManipulatePrecision.doubleToString(je));
            return false;
        }
        
        // 检查主单和付款明细
        je = 0;
        zl = 0;
        for (i = 0; i < salePayment.size(); i++)
        {
        	salePayDef = (SalePayDef) salePayment.elementAt(i);
        	
        	if (salePayDef.flag == '2')
        	{
        		zl += salePayDef.je;
        	}
        	else
        	{
        		// 不是扣回记入付款汇总
        		if (!isBuckleMoney(salePayDef))
        		{
        			je += salePayDef.je;
        		}
        	}
        }
        if (ManipulatePrecision.doubleCompare(saleHead.sjfk,je,2) != 0)
        {
        	new MessageBox(Language.apply("交易主单和付款明细不平!\n\n主单实际付款") + " = "+
        	ManipulatePrecision.doubleToString(saleHead.sjfk)+
        	Language.apply("\n付款合计金额") + " = "+ManipulatePrecision.doubleToString(je));
            return false;
        }
        
        return true;
    }
}
