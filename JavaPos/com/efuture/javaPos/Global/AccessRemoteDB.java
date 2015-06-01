package com.efuture.javaPos.Global;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

//这个类主要用于,从网上下载数据,放入到本地数据库中
public class AccessRemoteDB
{
	public static AccessRemoteDB currentAccessRemoteDB = null;

	public static AccessRemoteDB getDefault()
	{
		if (AccessRemoteDB.currentAccessRemoteDB == null)
		{
			AccessRemoteDB.currentAccessRemoteDB = CustomLocalize.getDefault().createRemoteDB();
		}

		return AccessRemoteDB.currentAccessRemoteDB;
	}
	
	/**
	 * 判断是否连接
	 * @param isCreateConnection 若无连接时,是否创建连接
	 * @return
	 */
	public boolean isConnection(boolean isCreateConnection)
	{
		try
		{
			if (GlobalInfo.RemoteDB==null || GlobalInfo.RemoteDB.getConnection()==null || GlobalInfo.RemoteDB.getIsDisConnection()==true) 
			{
				if (!isCreateConnection) return false;
				 LoadSysInfo.getDefault().loadRemoteDB(null);
				 if (GlobalInfo.RemoteDB==null || GlobalInfo.RemoteDB.getConnection()==null || GlobalInfo.RemoteDB.getIsDisConnection()==true) return false;//当连接失败时,直接返回
			}
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		
	}
	
	public int writeSale(SaleHeadDef saleHead, Vector saleGoods,Vector salePayment)
    {
        boolean done = false;
        String line = "";
        
        try
        {
	    	PublicMethod.timeStart(Language.apply("正在发送小票到远程数据库,请等待......"));
	    	
	    	// 检查数据
	    	if (saleGoods.size() <= 0 || salePayment.size() <= 0)
	    	{
	    		//new MessageBox("商品数据或者付款数据为空\n\n小票数据有异常");
	    		return 1;
	    	}

	        // 检查数据库当中是否已存在小票
	    	Object objcount = GlobalInfo.RemoteDB.selectOneData("select count(*) from salehead where syjh = '" + saleHead.syjh + "' and fphm = " + saleHead.fphm);
	    	if (objcount == null) return 1;
	    	
	    	if (Convert.toInt(objcount) > 0)
	    	{
	    		return 2;
	    	}
	        
	    	// 开始事务
	        if (!GlobalInfo.RemoteDB.beginTrans())
	        {
	            return 1;
	        }
	        
            // 按表的字段确定对象的数据,表结构不存在的数据不保存
            String[] refhead = GlobalInfo.RemoteDB.getTableColumns("SALEHEAD");
            if (refhead == null || refhead.length <= 0) refhead = SaleHeadDef.ref;
            
            refhead = CommonMethod.andCompare(refhead,SaleHeadDef.ref);
	        
	        // 插入小票头
	        line = CommonMethod.getInsertSql("SALEHEAD", refhead);
	
	        if (!GlobalInfo.RemoteDB.setSql(line))
	        {
	            return 1;
	        }
	
	        if (!GlobalInfo.RemoteDB.setObjectToParam(saleHead,refhead))
	        {
	            return 1;
	        }
	
	        if (!GlobalInfo.RemoteDB.executeSql())
	        {
	        	//new MessageBox("写入小票头失败...", null, false);
	            return 1;
	        }
	
            // 按表的字段确定对象的数据,表结构不存在的数据不保存
            String[] refgoods = GlobalInfo.RemoteDB.getTableColumns("SALEGOODS");
            if (refgoods == null || refgoods.length <= 0) refgoods = SaleGoodsDef.ref;
            
            refgoods = CommonMethod.andCompare(refgoods,SaleGoodsDef.ref);
	        
	        // 插入小票商品明细
	        line = CommonMethod.getInsertSql("SALEGOODS", refgoods);
	     
	        if (!GlobalInfo.RemoteDB.setSql(line))
	        {
	            return 1;
	        }
	
	        SaleGoodsDef saleDef = null;
	
	        for (int i = 0; i < saleGoods.size(); i++)
	        {
	            saleDef = (SaleGoodsDef) saleGoods.elementAt(i);
	
	            if (!GlobalInfo.RemoteDB.setObjectToParam(saleDef,refgoods))
	            {
	                return 1;
	            }
	
	            if (!GlobalInfo.RemoteDB.executeSql())
	            {
	            	//new MessageBox("写入小票头明细失败...", null, false);
	                return 1;
	            }
	        }
	
            // 按表的字段确定对象的数据,表结构不存在的数据不保存
            String[] refpay = GlobalInfo.RemoteDB.getTableColumns("SALEPAY");
            if (refpay == null || refpay.length <= 0) refpay = SalePayDef.ref;
            
            refpay = CommonMethod.andCompare(refpay,SalePayDef.ref);
	        
	        // 插入小票付款明细
	        line = CommonMethod.getInsertSql("SALEPAY", refpay);
	      
	        if (!GlobalInfo.RemoteDB.setSql(line))
	        {
	            return 1;
	        }
	
	        SalePayDef payDef = null;
	
	        for (int i = 0; i < salePayment.size(); i++)
	        {
	            payDef = (SalePayDef) salePayment.elementAt(i);
	
	            if (!GlobalInfo.RemoteDB.setObjectToParam(payDef,refpay))
	            {
	                return 1;
	            }
	
	            if (!GlobalInfo.RemoteDB.executeSql())
	            {
	            	//new MessageBox("写入付款明细失败...", null, false);
	                return 1;
	            }
	        }
	
	        // 提交事务
	        if (!GlobalInfo.RemoteDB.commitTrans())
	        {
	            return 1;
	        }
	        
	        return 0;
	    }
	    catch (Exception ex)
	    {
	    	ex.printStackTrace();
	    	//new MessageBox("写入小票到远程数据库异常\n\n" + ex.getMessage());
	    	return 99;
	    }
        finally
        {
            if (!done)
            {
            	if (GlobalInfo.RemoteDB.isOpen())
            	{
            		GlobalInfo.RemoteDB.rollbackTrans();
            	}
            }
            
        	//
        	PublicMethod.timeEnd(Language.apply("写入小票到远程库耗时: "));            
        }
    }
}
