package custom.localize.Wdgc;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Wdgc_AccessDayDB extends AccessDayDB
{
	public boolean writeSale(SaleHeadDef saleHead, Vector saleGoods,Vector salePayment)
    {
        boolean done = false;
        String line = "";
        
        try
        {
	    	PublicMethod.timeStart("正在写入本地小票库,请等待......");
	    	
	    	// 检查数据
	    	if (saleGoods.size() <= 0 || salePayment.size() <= 0)
	    	{
	    		new MessageBox("商品数据或者付款数据为空\n\n小票数据有异常");
	    		return false;
	    	}
	    	
	    	// 检查上笔小票
	    	if (!checkLastInvoice(saleHead))
	    	{
	    		return false;
	    	}

	    	// 先写入备份流水文件
	    	if (!writeSaleTrace(saleHead,saleGoods,salePayment,false))
	    	{
	        	new MessageBox("写入小票备份流水失败...", null, false);
	            return false;
	    	}
	    	
	        // 开始事务
	        if (!GlobalInfo.dayDB.beginTrans())
	        {
	            return false;
	        }
	
            // 按表的字段确定对象的数据,表结构不存在的数据不保存
            String[] refhead = GlobalInfo.dayDB.getTableColumns("SALEHEAD");
            if (refhead == null || refhead.length <= 0) refhead = SaleHeadDef.ref;
            
	        // 插入小票头
	        line = CommonMethod.getInsertSql("SALEHEAD", refhead);
	
	        if (!GlobalInfo.dayDB.setSql(line))
	        {
	            return false;
	        }
	
	        if (!GlobalInfo.dayDB.setObjectToParam(saleHead,refhead))
	        {
	            return false;
	        }
	
	        if (!GlobalInfo.dayDB.executeSql())
	        {
	        	new MessageBox("写入小票头失败...", null, false);
	            return false;
	        }
	
            // 按表的字段确定对象的数据,表结构不存在的数据不保存
            String[] refgoods = GlobalInfo.dayDB.getTableColumns("SALEGOODS");
            if (refgoods == null || refgoods.length <= 0) refgoods = SaleGoodsDef.ref;
            
	        // 插入小票商品明细
	        line = CommonMethod.getInsertSql("SALEGOODS", refgoods);
	     
	        if (!GlobalInfo.dayDB.setSql(line))
	        {
	            return false;
	        }
	
	        SaleGoodsDef saleDef = null;
	
	        for (int i = 0; i < saleGoods.size(); i++)
	        {
	            saleDef = (SaleGoodsDef) saleGoods.elementAt(i);
	
	            if (!GlobalInfo.dayDB.setObjectToParam(saleDef,refgoods))
	            {
	                return false;
	            }
	
	            if (!GlobalInfo.dayDB.executeSql())
	            {
	            	new MessageBox("写入小票头明细失败...", null, false);
	                return false;
	            }
	        }
	
            // 按表的字段确定对象的数据,表结构不存在的数据不保存
            String[] refpay = GlobalInfo.dayDB.getTableColumns("SALEPAY");
            if (refpay == null || refpay.length <= 0) refpay = SalePayDef.ref;
            
	        // 插入小票付款明细
	        line = CommonMethod.getInsertSql("SALEPAY", refpay);
	      
	        if (!GlobalInfo.dayDB.setSql(line))
	        {
	            return false;
	        }
	
	        SalePayDef payDef = null;
	
	        for (int i = 0; i < salePayment.size(); i++)
	        {
	            payDef = (SalePayDef) salePayment.elementAt(i);
	
	            if (!GlobalInfo.dayDB.setObjectToParam(payDef,refpay))
	            {
	                return false;
	            }
	
	            if (!GlobalInfo.dayDB.executeSql())
	            {
	            	new MessageBox("写入付款明细失败...", null, false);
	                return false;
	            }
	        }
	
	        // 写入汇总数据
	        if (!writeSaleState(saleHead, saleGoods, salePayment))
	        {
	            return false;
	        }
	
	        // 提交事务
	        if (!GlobalInfo.dayDB.commitTrans())
	        {
	            return false;
	        }

	        // 检查小票是否写入成功
	        if (!checkSuccessInvoice(saleHead,saleGoods,salePayment))
	        {
	        	return false;
	        }
	        
	        // 小票号累加 
	        GlobalInfo.syjStatus.fphm = GlobalInfo.syjStatus.fphm + 1;
	        AccessLocalDB.getDefault().writeSyjStatus();
	        	        
	        // 记录发送任务
	        AccessLocalDB.getDefault()
	                     .writeTask(StatusType.TASK_SENDINVOICE,
	                                TaskExecute.getKeyTextByBalanceDate());
	        
	        //备份DAY信息
	        
	        String date = ExpressionDeal.replace(GlobalInfo.balanceDate, "/", "");
	        PathFile.deletePath(ConfigClass.LocalDBPath + "Invoice//" + date + "//Bak//" + "Day.db3");
			PathFile.copyPath(ConfigClass.LocalDBPath + "Invoice//" + date + "//" + "Day.db3", ConfigClass.LocalDBPath + "Invoice//"
					+ date + "//Bak//" + "Day.db3");
			AccessDayDB.getDefault().writeWorkLog("备份本地DAY数据库成功 "+saleHead.fphm, StatusType.WORK_SENDERROR);
			
	        done = true;
	        return true;
	    }
	    catch (Exception ex)
	    {
	    	ex.printStackTrace();
	    	new MessageBox("写入小票发生异常\n\n" + ex.getMessage());
	    	return false;
	    }
        finally
        {
            if (!done)
            {
                GlobalInfo.dayDB.rollbackTrans();
                
                // 删除备份流水文件
                writeSaleTrace(saleHead,saleGoods,salePayment,true);
            }
            
        	//
        	PublicMethod.timeEnd("写入本地小票库耗时: ");            
        }
    }
}
