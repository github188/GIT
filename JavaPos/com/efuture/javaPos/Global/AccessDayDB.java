package com.efuture.javaPos.Global;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.PayinDetailDef;
import com.efuture.javaPos.Struct.PayinHeadDef;
import com.efuture.javaPos.Struct.PrepareMoneyDef;
import com.efuture.javaPos.Struct.SaleAppendDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleGzSummaryDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SaleManSummaryDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SalePaySummaryDef;
import com.efuture.javaPos.Struct.SaleSummaryDef;
import com.efuture.javaPos.Struct.WorkLogDef;


//访问每日数据库
public class AccessDayDB
{
	public static AccessDayDB currentAccessDayDB = null;
	
    public static AccessDayDB getDefault()
    {
        if (AccessDayDB.currentAccessDayDB == null)
        {
        	AccessDayDB.currentAccessDayDB = CustomLocalize.getDefault().createAccessDayDB();
        }

        return AccessDayDB.currentAccessDayDB;
    }

    public boolean writeWorkLog(String msg)
    {
        return writeWorkLog(msg, "0000");
    }
    
    // 增加一条工作日志
    public boolean writeWorkLog(String msg, String code)
    {
    	try
    	{
	        WorkLogDef wl = new WorkLogDef();
	        wl.seqno = 0;
	        wl.netbz = 'N';
	        wl.rqsj  = new ManipulateDateTime().getDateTimeString();
	        wl.syjh  = (ConfigClass.CashRegisterCode == null || ConfigClass.CashRegisterCode.trim().length() <= 0)?"XXXX":ConfigClass.CashRegisterCode;
	        wl.syyh  = ((GlobalInfo.posLogin == null) ? "" : GlobalInfo.posLogin.gh);
	        wl.code  = code;
	        wl.memo  = msg;
	
	        // 数据库对象有效直接写入数据库
	        // 数据库对象无效则写入磁盘文件,开机连接上数据库后读入并写入数据库
	        if (GlobalInfo.dayDB != null)
	        {
	        	writeWorkLog(wl);
	        }
	        else
	        {
	        	FileOutputStream f = null;
	        	try
	        	{
	        		int maxnum = 0;
	                File file = new File(ConfigClass.LocalDBPath);
	               	File[] filename = file.listFiles();
	                for (int i = 0; i < filename.length; i++)
	                {
	                	String fname = filename[i].getName();
	                	if (fname.startsWith("Work_") && fname.endsWith(".rz"))
	                	{
	                		int n = Integer.parseInt(fname.substring(5,fname.length() - 3));
	                		if (n > maxnum) maxnum = n;
	                	}
	                }
	                maxnum++;
	                
		            String name = ConfigClass.LocalDBPath + "/Work_"+maxnum+".rz";
		            f = new FileOutputStream(name);
			        ObjectOutputStream s = new ObjectOutputStream(f);
			        s.writeObject(wl);
			        s.flush();
			        s.close();
			        f.close();
			        s = null;
			        f = null;
	        	}
		        catch (Exception e)
		        {
		            e.printStackTrace();
		            return false;
		        }
		        finally
		        {
		            if (f != null) f.close();
		        }
	        }
	        
	        return true;
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		return false;
    	}
    }

    public boolean writeWorkLogByHistory()
    {
    	try
    	{
	        if (GlobalInfo.dayDB != null)
	        {
	        	FileInputStream f = null;
	        	try
	        	{
	                File file = new File(ConfigClass.LocalDBPath);
	               	File[] filename = file.listFiles();
	                for (int i = 0; i < filename.length; i++)
	                {
	                	String fname = filename[i].getName();
	                	if (fname.startsWith("Work_") && fname.endsWith(".rz"))
	                	{
	                        String name = filename[i].getAbsolutePath();
	                        
	            	        f = new FileInputStream(name);
	            	        ObjectInputStream s = new ObjectInputStream(f);
	            	        WorkLogDef wl = (WorkLogDef) s.readObject();
	                        s.close();
	                        s = null;
	                        f.close();
	                        f = null;
	                        
	                        writeWorkLog(wl);
	                        
	            	        new File(name).delete();
	                	}
	                }
	        	}
		        catch (Exception e)
		        {
		            e.printStackTrace();
		            return false;
		        }
		        finally
		        {
		            if (f != null) f.close();
		        }	        	
	        }

	        return true;
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		return false;
    	}
    }
    
    public boolean writeWorkLog(WorkLogDef wl)
    {
        if (!GlobalInfo.dayDB.beginTrans())
        {
            return false;
        }

        long seqno;
        Object obj = GlobalInfo.dayDB.selectOneData("select max(seqno) from WORKLOG");

        if (obj == null)
        {
            seqno = 1;
        }
        else
        {
            seqno = Long.parseLong(String.valueOf(obj)) + 1;
        }

        // 按表的字段确定对象的数据,表结构不存在的数据不保存
        String[] ref = GlobalInfo.dayDB.getTableColumns("WORKLOG");
        if (ref == null || ref.length <= 0) ref = WorkLogDef.ref;
        
        String line = CommonMethod.getInsertSql("WORKLOG", ref);

        if (!GlobalInfo.dayDB.setSql(line))
        {
            return false;
        }

        //
        wl.seqno = seqno;
        if (!GlobalInfo.dayDB.setObjectToParam(wl,ref))
        {
            return false;
        }

        if (!GlobalInfo.dayDB.executeSql())
        {
            return false;
        }

        if (!GlobalInfo.dayDB.commitTrans())
        {
            return false;
        }

        // 记录发送任务
        AccessLocalDB.getDefault()
                     .writeTask(StatusType.TASK_SENDWORKLOG,
                                TaskExecute.getKeyTextByBalanceDate());
        
        return true;
    }
    
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
            
            if (saleGoodsDef.sl == 0 || saleGoodsDef.sl < 0)
            {
//            	new MessageBox("第 " + (i + 1) + " 行商品 [" + saleGoodsDef.code + "] 数量不合法\n请修改此行商品数量或者删除此商品后重新录入");
            	new MessageBox(Language.apply("第") + (i + 1) + Language.apply("行商品 [{0}] 数量不合法\n请修改此行商品数量或者删除此商品后重新录入", new Object[]{saleGoodsDef.code}));
            	return false;
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
		    		if (new MessageBox(Language.apply("上笔交易小票号({0})和当前交易小票号({1})不连续\n\n你确定要继续完成交易吗？",new Object[]{lastfphm+"" ,saleHead.fphm+""}),null,true).verify() != GlobalVar.Key1)
		    		{
		    			return false;
		    		}
		    		else
		    		{
		    			already = true;
//		    			AccessDayDB.getDefault().writeWorkLog("上笔交易小票号("+lastfphm+")和当前交易小票号("+saleHead.fphm+")不连续");
		    			AccessDayDB.getDefault().writeWorkLog(Language.apply("上笔交易小票号({0})和当前交易小票号({1})不连续", new Object[]{lastfphm+"" ,saleHead.fphm+""}));
		    		}
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
	    			if (new MessageBox(Language.apply("上笔交易小票号({0})和当前交易小票号({1})不连续\n\n你确定要继续完成交易吗？",new Object[]{lastfphm+"" ,saleHead.fphm+""}),null,true).verify() != GlobalVar.Key1)
		    		{
		    			return false;
		    		}
		    		else
		    		{
		    			already = true;
		    			AccessDayDB.getDefault().writeWorkLog(Language.apply("上笔交易小票号({0})和当前交易小票号({1})不连续", new Object[]{lastfphm+"" ,saleHead.fphm+""}));
		    		}
	    		}
	    	}
	    	GlobalInfo.dayDB.resultSetClose();

	    	rs = GlobalInfo.dayDB.selectData("select max(fphm) from SALEPAY");
	    	if (rs != null && rs.next())
	    	{
	    		long lastfphm = rs.getLong(1);
	    		if (lastfphm != 0 && lastfphm != saleHead.fphm - 1 && !already)
	    		{
	    			if (new MessageBox(Language.apply("上笔交易小票号({0})和当前交易小票号({1})不连续\n\n你确定要继续完成交易吗？",new Object[]{lastfphm+"" ,saleHead.fphm+""}),null,true).verify() != GlobalVar.Key1)
		    		{
		    			return false;
		    		}
		    		else
		    		{
		    			already = true;
		    			AccessDayDB.getDefault().writeWorkLog(Language.apply("上笔交易小票号({0})和当前交易小票号({1})不连续", new Object[]{lastfphm+"" ,saleHead.fphm+""}));
		    		}
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
    
    public boolean checkSuccessInvoice(SaleHeadDef saleHead, Vector saleGoods,Vector salePayment)
    {
    	Object obj = null;
    	
    	try
    	{
    		obj = GlobalInfo.dayDB.selectOneData("select count(*) from SALEHEAD where syjh='"+saleHead.syjh+"' and fphm="+saleHead.fphm);
	    	if (obj == null || Integer.parseInt(obj.toString()) <= 0)
	    	{
//	    		new MessageBox("当前交易("+saleHead.fphm+")小票主单写入本地数据库无效");
	    		new MessageBox(Language.apply("当前交易({0})小票主单写入本地数据库无效",new Object[]{saleHead.fphm + ""}));
	    		return false;
	    	}
	    	GlobalInfo.dayDB.resultSetClose();
	    	
	    	obj = GlobalInfo.dayDB.selectOneData("select count(*) from SALEGOODS where syjh='"+saleHead.syjh+"' and fphm="+saleHead.fphm);
	    	if (obj == null || Integer.parseInt(obj.toString()) != saleGoods.size())
	    	{
//	    		new MessageBox("当前交易("+saleHead.fphm+")商品明细写入本地数据库无效");
	    		new MessageBox(Language.apply("当前交易({0})商品明细写入本地数据库无效",new Object[]{saleHead.fphm + ""}));
	    		return false;
	    	}
	    	GlobalInfo.dayDB.resultSetClose();

	    	obj = GlobalInfo.dayDB.selectOneData("select count(*) from SALEPAY where syjh='"+saleHead.syjh+"' and fphm="+saleHead.fphm);
	    	if (obj == null || Integer.parseInt(obj.toString()) != salePayment.size())
	    	{
//	    		new MessageBox("当前交易("+saleHead.fphm+")付款明细写入本地数据库无效");
	    		new MessageBox(Language.apply("当前交易({0})付款明细写入本地数据库无效",new Object[]{saleHead.fphm + ""}));
	    		return false;
	    	}
	    	GlobalInfo.dayDB.resultSetClose();
	    	
	    	return true;
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		new MessageBox(Language.apply("检查交易写盘发生异常\n\n")+ ex.getMessage());
    		return false;
    	}
    	finally
    	{
    		GlobalInfo.dayDB.resultSetClose();
    	}
    }
    
    public boolean writeSale(SaleHeadDef saleHead, Vector saleGoods,Vector salePayment)
    {
        boolean done = false;
        String line = "";
        
        try
        {
	    	PublicMethod.timeStart(Language.apply("正在写入本地小票库,请等待......"));
	    	
	    	// 检查数据
	    	if (saleGoods.size() <= 0 || salePayment.size() <= 0)
	    	{
	    		new MessageBox(Language.apply("商品数据或者付款数据为空\n\n小票数据有异常"));
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
	        	new MessageBox(Language.apply("写入小票备份流水失败..."), null, false);
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
	        	new MessageBox(Language.apply("写入小票头失败..."), null, false);
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
	            	new MessageBox(Language.apply("写入小票头明细失败..."), null, false);
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
	            	new MessageBox(Language.apply("写入付款明细失败..."), null, false);
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
	        
	        //
	        done = true;
	        return true;
	    }
	    catch (Exception ex)
	    {
	    	ex.printStackTrace();
	    	new MessageBox(Language.apply("写入小票发生异常\n\n") + ex.getMessage());
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
        	PublicMethod.timeEnd(Language.apply("写入本地小票库耗时: "));            
        }
    }

    public boolean writeSaleTrace(SaleHeadDef saleHead, Vector saleGoods,Vector salePayment,boolean delflag)
    {
    	FileOutputStream f = null;
    	
        try
        {
            String date = GlobalInfo.balanceDate.replaceAll("/","");
            String name = ConfigClass.LocalDBPath + "Invoice/" + date + "/" + "invtrace";
            if (!PathFile.fileExist(name))
            {
            	if (!PathFile.createDir(name)) return false;
            }
            name += "/"+saleHead.syjh + "_"+saleHead.fphm+".dat";
            
            // 删除文件
            if (delflag)
            {
            	PathFile.deletePath(name);
            	return true;
            }
	        
	        f = new FileOutputStream(name);
	        ObjectOutputStream s = new ObjectOutputStream(f);
	        
	        s.writeObject(saleHead);
	        s.writeObject(saleGoods);
	        s.writeObject(salePayment);
			
	        s.flush();
	        s.close();
	        f.close();
	        s = null;
	        f = null;
	        
	        return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new MessageBox(Language.apply("写入小票备份发生异常\n\n")+e.getMessage());
            return false;
        }
        finally
        {
        	try
        	{
	            if (f != null) f.close();
        	}
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public boolean writeSaleState(SaleHeadDef saleHead, Vector saleGoods,Vector salePayment)
    {
        // 生成该小票的销售统计数据
        SaleSummaryDef saleSummaryDef = new SaleSummaryDef();

        saleSummaryDef.zl   = saleHead.zl * SellType.SELLSIGN(saleHead.djlb);
        saleSummaryDef.sysy = ManipulatePrecision.add(saleHead.fk_sysy,
                                                      saleHead.sswr_sysy) * SellType.SELLSIGN(saleHead.djlb);
        saleSummaryDef.sjfk = (saleHead.sjfk - saleHead.zl) * SellType.SELLSIGN(saleHead.djlb);
        saleSummaryDef.zkje = saleHead.hjzke * SellType.SELLSIGN(saleHead.djlb);
        saleSummaryDef.ysje = saleHead.ysje * SellType.SELLSIGN(saleHead.djlb);

        if (SellType.ISSALE(saleHead.djlb))
        {
            saleSummaryDef.xsbs = 1;
            saleSummaryDef.xsje = saleHead.ysje;
        }
        else if (SellType.ISBACK(saleHead.djlb))
        {
            saleSummaryDef.thbs = 1;
            saleSummaryDef.thje = saleHead.ysje;
        }
        else if (SellType.ISHC(saleHead.djlb))
        {
            saleSummaryDef.hcbs = 1;
            saleSummaryDef.hcje = saleHead.ysje;
        }
        else if (SellType.ISClEAR(saleHead.djlb))
        {
            saleSummaryDef.qxbs = 1;
            saleSummaryDef.qxje = saleHead.ysje;
        }

        // 如果有扣回付款，把扣回要算到实收合计中
        for (int i = 0; i < salePayment.size(); i++)
        {
            SalePayDef spd = (SalePayDef) salePayment.elementAt(i);
            if (isBuckleMoney(spd))
            {
            	saleSummaryDef.sjfk += spd.je * SellType.SELLSIGN(saleHead.djlb);
            }
        }
        
        // 写入全天销售统计
        saleSummaryDef.bc   = '0';
        saleSummaryDef.syyh = Language.apply("全天");
        if (!AccessDayDB.getDefault().writeSaleSummary(saleSummaryDef))
        {
            return false;
        }

        // 写入当班收银员销售统计
        saleSummaryDef.bc   = saleHead.bc;
        saleSummaryDef.syyh = saleHead.syyh;
        if (!AccessDayDB.getDefault().writeSaleSummary(saleSummaryDef))
        {
            return false;
        }

    	// 计算当前收银状态
    	GlobalInfo.syjStatus.bs += saleSummaryDef.xsbs + saleSummaryDef.thbs + saleSummaryDef.hcbs;
    	GlobalInfo.syjStatus.je += saleSummaryDef.ysje;
    	
        // 付款统计
        if (!SellType.ISClEAR(saleHead.djlb))
        {
            SalePayDef salePayDef;
            SalePaySummaryDef salePaySummary;
            int sign = 1;
            
            for (int i = 0; i < salePayment.size(); i++)
            {
                salePaySummary = new SalePaySummaryDef();
                salePayDef     = (SalePayDef) salePayment.elementAt(i);

                // 找零减,付款/扣回加
                if (salePayDef.flag == '2') sign = -1;
                else sign = 1;
                
                // 生成付款统计数据
                String paycode,payname;
                paycode = salePayDef.paycode;
                payname = salePayDef.payname.replaceAll(Language.apply("找零"), "");

                // 按主付款方式汇总
                if (GlobalInfo.sysPara.paysummarymode == 'Y')
                {
                	PayModeDef pmd = DataService.getDefault().searchMainPayMode(paycode);
                	if (pmd != null)
                	{
		                paycode = pmd.code;
		                payname = pmd.name;
                	}
                }
                else
                {
                	// 避免交易是修改了付款名称，导致汇总显示的付款名称不一致，统一用付款方式的名称显示汇总
                	PayModeDef pmd = DataService.getDefault().searchPayMode(paycode);
                	if (pmd != null)
                	{
		                paycode = pmd.code;
		                payname = pmd.name;
                	}
                }
                
                // 如果是扣回付款,记入扣回付款方式统计
                if (isBuckleMoney(salePayDef))
                {
                	// 扣回付款方式在付款代码前加K,区别于正常付款方式,付款名称带扣回字样
                	if (paycode.length() < 4) paycode = "K" + paycode;
                	else paycode = "K" + paycode.substring(1);
                	if (payname.indexOf(Language.apply("扣回")) < 0) payname += Language.apply("扣回");
                }

                salePaySummary.paycode = paycode;
                salePaySummary.payname = payname;                
                salePaySummary.bs      = (sign >= 1)?1:0;
                salePaySummary.je      = salePayDef.ybje * SellType.SELLSIGN(saleHead.djlb) * sign;
                                
                // 写入全天付款统计
                salePaySummary.bc      = '0';
                salePaySummary.syyh    = Language.apply("全天");
                if (!AccessDayDB.getDefault().writeSalePaySummary(salePaySummary))
                {
                    return false;
                }

                // 写入当班收银员付款统计
                salePaySummary.bc   = saleHead.bc;
                salePaySummary.syyh = saleHead.syyh;
                if (!AccessDayDB.getDefault().writeSalePaySummary(salePaySummary))
                {
                    return false;
                }
                
            	// 计算当前收银状态
                PayModeDef paymode = DataService.getDefault().searchPayMode(salePayDef.paycode);
                if (paymode != null && paymode.type == '1')
                {
                	GlobalInfo.syjStatus.xjje += salePaySummary.je;		//使用salePaySummary将找零减出
                }
            }
        }

        // 柜组统计
        if (!SellType.ISClEAR(saleHead.djlb))
        {
            SaleGoodsDef saleGoodsDef;
            SaleGzSummaryDef saleGzSummary;

            for (int i = 0; i < saleGoods.size(); i++)
            {
                saleGoodsDef       = (SaleGoodsDef) saleGoods.elementAt(i);
                if (saleGoodsDef.flag == '1') continue;
                saleGzSummary      = new SaleGzSummaryDef();
                saleGzSummary.bc   = saleHead.bc;					
                saleGzSummary.syyh = saleHead.syyh;
                saleGzSummary.gz   = saleGoodsDef.gz;
                saleGzSummary.name = AccessLocalDB.getDefault().getGzName(saleGzSummary.gz);
                int num = 1;
                for (int j =0;j < i;j++)
                {
                	SaleGoodsDef saleGoodsDef1 = (SaleGoodsDef) saleGoods.elementAt(j);
                	if (saleGoodsDef1.gz.equals(saleGoodsDef.gz))
                	{
                		num = 0;
                		break;
                	}
                }
                
                if (SellType.SELLSIGN(saleHead.djlb) > 0)
                {
                    saleGzSummary.xsbs = num;
                    saleGzSummary.xsje = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk,2,1);
                    saleGzSummary.xszk = saleGoodsDef.hjzk;
                }
                else
                {
                    saleGzSummary.thbs = num;
                    saleGzSummary.thje = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk,2,1);
                    saleGzSummary.thzk = saleGoodsDef.hjzk;
                }

                if (!AccessDayDB.getDefault().writeSaleGzSummary(saleGzSummary))
                {
                    return false;
                }
            }
        }

        // 营业员统计
        if (!SellType.ISClEAR(saleHead.djlb))
        {
            SaleGoodsDef saleGoodsDef;
            SaleManSummaryDef saleManSummary;

            for (int i = 0; i < saleGoods.size(); i++)
            {
                saleGoodsDef        = (SaleGoodsDef) saleGoods.elementAt(i);
                if (saleGoodsDef.flag == '1') continue;
                saleManSummary      = new SaleManSummaryDef();
                saleManSummary.bc   = saleHead.bc;					
                saleManSummary.syyh = saleHead.syyh;
                saleManSummary.yyyh = saleGoodsDef.yyyh;
                saleManSummary.name = "";

                int num = 1;
                for (int j =0;j < i;j++)
                {
                	SaleGoodsDef saleGoodsDef1 = (SaleGoodsDef) saleGoods.elementAt(j);
                	if (saleGoodsDef1.yyyh.equals(saleGoodsDef.yyyh))
                	{
                		num = 0;
                		break;
                	}
                }
                
                if (SellType.SELLSIGN(saleHead.djlb) > 0)
                {
                	saleManSummary.xsbs = num;
                	saleManSummary.xsje = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk,2,1);
                	saleManSummary.xszk = saleGoodsDef.hjzk;
                }
                else
                {
                	saleManSummary.thbs = num;
                	saleManSummary.thje = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk,2,1);
                	saleManSummary.thzk = saleGoodsDef.hjzk;
                }

                if (!AccessDayDB.getDefault().writeSaleManSummary(saleManSummary))
                {
                    return false;
                }
            }
        }

        return true;
    }
    
    public boolean updateSaleJf(long fphm,int flag,double bcjf,double ljjf)
    {
    	return updateSaleJf(fphm,flag,bcjf,ljjf,"");
    }

    
    public boolean updateSaleHeadStr(long fphm,String colname,String value)
    {
        String line = "";
        try
        {
            line = "update SALEHEAD set "+colname+" = '" +value+"' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;

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
    
    public boolean updateSaleJf(long fphm,int flag,double bcjf,double ljjf,String memo)
    {
        String line = "";
        
        try
        {
            switch(flag)
            {
            	case 1:
            		line = "update SALEHEAD set bcjf = " + bcjf + ",ljjf = " + ljjf + " where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;
            		break;
            	case 2:
            		line = "update SALEHEAD set memo = '" + bcjf + ","+ ljjf + "' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;
            		break;
            	case 3:
            		line = "update SALEHEAD set str2 = '" + memo + "' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;
            		break;
            	case 4:
            		line = "update SALEHEAD set memo = '" +memo+"' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;
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
     
    public boolean updateSaleBz(long fphm,int flag,char bz)
    {
    	return updateSaleBz(fphm,flag,String.valueOf(bz));
    }
    
    public boolean updateSaleBz(long fphm,int flag,String bz)
    {
        String line = "";
        
        try
        {
            switch(flag)
            {
            	case 1:
            		line = "update SALEHEAD set NETBZ = '" + bz + "' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;
            		break;
            	case 2:
            		line = "update SALEHEAD set SALEFPHM = '" + bz + "' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;
            		break;
            	case 3:
            		line = "update SALEAPPEND set NETBZ = '" + bz + "' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;
            		break;
            	case 10:
                    String[] refhead = GlobalInfo.dayDB.getTableColumns("PAYINHEAD");
                    boolean done = false;
                    for (int i = 0; i < refhead.length; i++)
                    {
                    	if (refhead[i] != null && refhead[i].equals("hcbz"))
                    	{
                    		done = true;
                    		break;
                    	}
                    }
                    
                    if (done)
                    {
                    	line = "update  PAYINHEAD set hcbz = '" + bz + "' where seqno =" + fphm + " and  syyh='" + GlobalInfo.posLogin.gh + "'";
                    }
                    else
                    {
                    	return true;
                    }
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

    public boolean writeSaleSummary(SaleSummaryDef saleSummary)
    {
        boolean update = false;

        try
        {
            Object obj = GlobalInfo.dayDB.selectOneData("select count(*) from SaleSummary where bc = '" +
                                             saleSummary.bc + "' AND SYYH = '" +
                                             saleSummary.syyh + "'");
            if (obj != null && Integer.parseInt(String.valueOf(obj)) > 0)
            {
                update = true;
            }

            String line = "";
            String[] ref = null;
            
            if (update)
            {
            	ref = SaleSummaryDef.refUpdate;
            	
                line = "Update SaleSummary set ";

                for (int i = 0; i < (ref.length - 2); i++)
                {
                    line += (ref[i] + " =" + ref[i] + "+ ?,");
                }

                line = line.substring(0, line.length() - 1);

                line += "where bc = ? AND SYYH = ?";
                
            }
            else
            {
            	ref = SaleSummaryDef.ref;

            	line = CommonMethod.getInsertSql("SaleSummary",ref);
            }

            if (!GlobalInfo.dayDB.setSql(line))
            {
                return false;
            }

            if (!GlobalInfo.dayDB.setObjectToParam(saleSummary,ref))
            {
                return false;
            }

            if (!GlobalInfo.dayDB.executeSql())
            {
                return false;
            }

            return true;
        }
        catch (Exception er)
        {
            er.printStackTrace();

            return false;
        }
    }

    public boolean writeSalePaySummary(SalePaySummaryDef salePaySummary)
    {
        boolean update = false;

        try
        {
            Object obj = GlobalInfo.dayDB.selectOneData("select count(*) from SALEPAYSUMMARY where bc = '" +
                                             salePaySummary.bc +
                                             "' AND SYYH = '" +
                                             salePaySummary.syyh +
                                             "' AND paycode = '" +
                                             salePaySummary.paycode + "'");
            if (obj != null && Integer.parseInt(String.valueOf(obj)) > 0)
            {
                update = true;
            }

            String line = "";
            String[] ref = null;

            if (update)
            {
            	ref = SalePaySummaryDef.refUpdate;
            	
                line = "Update SALEPAYSUMMARY set ";

                for (int i = 0; i < (ref.length - 3); i++)
                {
                    line += (ref[i] + " =" + ref[i] + "+ ?,");
                }

                line = line.substring(0, line.length() - 1);

                line += "where bc = ? AND SYYH = ? AND paycode = ?";
            }
            else
            {
                ref  = SalePaySummaryDef.ref;
                
                line = CommonMethod.getInsertSql("SALEPAYSUMMARY",ref);
            }

            if (!GlobalInfo.dayDB.setSql(line))
            {
                return false;
            }

            if (!GlobalInfo.dayDB.setObjectToParam(salePaySummary, ref))
            {
                return false;
            }

            if (!GlobalInfo.dayDB.executeSql())
            {
                return false;
            }

            return true;
        }
        catch (Exception er)
        {
            er.printStackTrace();

            return false;
        }
    }

    public boolean writeSaleGzSummary(SaleGzSummaryDef saleGzSummary)
    {
        boolean update = false;

        try
        {
            Object obj = GlobalInfo.dayDB.selectOneData("select count(*) from SALEGZSUMMARY where bc = '" +
                                             saleGzSummary.bc +
                                             "' AND SYYH = '" +
                                             saleGzSummary.syyh +
                                             "' AND gz = '" + saleGzSummary.gz +
                                             "'");
            if (obj != null && Integer.parseInt(String.valueOf(obj)) > 0)
            {
                update = true;
            }

            String line = "";
            String[] ref = null;

            if (update)
            {
            	ref = SaleGzSummaryDef.refUpdate;
            	
                line = "Update SALEGZSUMMARY set ";

                for (int i = 0; i < (ref.length - 3); i++)
                {
                    line += (ref[i] + " =" + ref[i] + "+ ?,");
                }

                line = line.substring(0, line.length() - 1);

                line += "where bc = ? AND SYYH = ? AND gz = ?";
            }
            else
            {
                ref  = SaleGzSummaryDef.ref;
                
                line = CommonMethod.getInsertSql("SALEGZSUMMARY",ref);
            }

            if (!GlobalInfo.dayDB.setSql(line))
            {
                return false;
            }

            if (!GlobalInfo.dayDB.setObjectToParam(saleGzSummary, ref))
            {
                return false;
            }

            if (!GlobalInfo.dayDB.executeSql())
            {
                return false;
            }

            return true;
        }
        catch (Exception er)
        {
            er.printStackTrace();

            return false;
        }
    }

    public boolean writeSaleManSummary(SaleManSummaryDef saleManSummary)
    {
        boolean update = false;

        try
        {
        	Object obj = GlobalInfo.dayDB.selectOneData("select count(*) from SALEMANSUMMARY where bc = '" +
                                             saleManSummary.bc +
                                             "' AND SYYH = '" +
                                             saleManSummary.syyh +
                                             "' AND yyyh = '" +
                                             saleManSummary.yyyh + "'");

            if (obj != null && Integer.parseInt(String.valueOf(obj)) > 0)
            {
                update = true;
            }

            String line = "";
            String[] ref = null;

            if (update)
            {
            	ref = SaleManSummaryDef.refUpdate;
            	
                line = "Update SALEMANSUMMARY set ";

                for (int i = 0; i < (ref.length - 3); i++)
                {
                    line += (ref[i] + " =" + ref[i] + "+ ?,");
                }

                line = line.substring(0, line.length() - 1);

                line += "where bc = ? AND SYYH = ? AND yyyh = ?";
            }
            else
            {
                ref  = SaleManSummaryDef.ref;
                
                line = CommonMethod.getInsertSql("SALEMANSUMMARY",ref);
            }

            if (!GlobalInfo.dayDB.setSql(line))
            {
                return false;
            }

            if (!GlobalInfo.dayDB.setObjectToParam(saleManSummary, ref))
            {
                return false;
            }

            if (!GlobalInfo.dayDB.executeSql())
            {
                return false;
            }

            return true;
        }
        catch (Exception er)
        {
            er.printStackTrace();

            return false;
        }
    }
    
    public boolean readSyjSaleState()
    {
        ResultSet rs = null;

        try
        {
        	// 找全天交易统计
            rs = GlobalInfo.dayDB.selectData("select * from SaleSummary where bc = '0' and syyh = '" + Language.apply("全天") +"'");
            if (rs == null)
            {
                return false;
            }

            if (rs.next())
            {
            	SaleSummaryDef ss = new SaleSummaryDef();
            	if (!GlobalInfo.dayDB.getResultSetToObject(ss)) return false;
            	
            	// 
            	GlobalInfo.syjStatus.bs = ss.xsbs + ss.thbs + ss.hcbs;
            	GlobalInfo.syjStatus.je = ss.ysje;
            }
            GlobalInfo.dayDB.resultSetClose();
            
            // 找全天交易付款统计
            rs = GlobalInfo.dayDB.selectData("select * from SalePaySummary where bc = '0' and syyh = '" + Language.apply("全天") +"'");
            if (rs == null)
            {
                return false;
            }            
            
            GlobalInfo.syjStatus.xjje = 0;
            SalePaySummaryDef sps = new SalePaySummaryDef();
            while(rs.next())
            {
            	if (!GlobalInfo.dayDB.getResultSetToObject(sps)) return false;
            	PayModeDef pm = DataService.getDefault().searchPayMode(sps.paycode);
            	if (pm != null && pm.type == '1')
            	{
            		GlobalInfo.syjStatus.xjje += ManipulatePrecision.doubleConvert(sps.je,2,1);
            	}
            }
            GlobalInfo.dayDB.resultSetClose();
            
            // 现金存量要减去已缴款的
            rs = GlobalInfo.dayDB.selectData("select * from PAYINDETAIL");
            if (rs == null)
            {
                return false;
            }
            PayinDetailDef pdd = new PayinDetailDef();
            while(rs.next())
            {
            	if (!GlobalInfo.dayDB.getResultSetToObject(pdd)) return false;
            	PayModeDef pm = DataService.getDefault().searchPayMode(pdd.code);
            	if (pm != null && pm.type == '1')
            	{
            		GlobalInfo.syjStatus.xjje -= ManipulatePrecision.doubleConvert(pdd.je * pdd.hl,2,1);
            	}
            }
            GlobalInfo.dayDB.resultSetClose();
            if (GlobalInfo.syjStatus.xjje < 0) GlobalInfo.syjStatus.xjje = 0;
            
            // 现金存量要加上备用金
            rs = GlobalInfo.localDB.selectData("select * from PREPAREMONEY");
            if (rs == null)
            {
                return false;
            }
            PrepareMoneyDef pmd = new PrepareMoneyDef();
            while(rs.next())
            {
            	if (!GlobalInfo.localDB.getResultSetToObject(pmd)) return false;
            	PayModeDef pm = DataService.getDefault().searchPayMode(pmd.paycode);
            	if (pm != null && pm.type == '1')
            	{
            		GlobalInfo.syjStatus.xjje += ManipulatePrecision.doubleConvert(pmd.je * pm.hl,2,1);
            	}
            }
            GlobalInfo.localDB.resultSetClose();
            
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return false;
        }
        finally
        {
            GlobalInfo.dayDB.resultSetClose();
            GlobalInfo.localDB.resultSetClose();
        }
    }
    
    public boolean writePayin(PayinHeadDef phd, ArrayList payListMode)
    {
        try
        {
            //开始事务
            if (!GlobalInfo.dayDB.beginTrans())
            {
                return false;
            }

            // 按表的字段确定对象的数据,表结构不存在的数据不保存
            String[] refhead = GlobalInfo.dayDB.getTableColumns("PAYINHEAD");
            if (refhead == null || refhead.length <= 0) refhead = PayinHeadDef.ref;
            
            //插入缴款头
            String line = CommonMethod.getInsertSql("PAYINHEAD",refhead);

            if (!GlobalInfo.dayDB.setSql(line))
            {
                return false;
            }

            if (!GlobalInfo.dayDB.setObjectToParam(phd,refhead))
            {
                return false;
            }

            if (!GlobalInfo.dayDB.executeSql())
            {
                return false;
            }

            // 按表的字段确定对象的数据,表结构不存在的数据不保存
            String[] refdetail = GlobalInfo.dayDB.getTableColumns("PAYINDETAIL");
            if (refdetail == null || refdetail.length <= 0) refdetail = PayinDetailDef.ref;
            
            //插入缴款明细
            line = CommonMethod.getInsertSql("PAYINDETAIL", refdetail);

            if (!GlobalInfo.dayDB.setSql(line))
            {
                return false;
            }
            
            if (payListMode.size() < 1 )
            {
            	GlobalInfo.dayDB.rollbackTrans();
            	return false;
            }
            
            for (int i = 0; i < payListMode.size(); i++)
            {
                PayinDetailDef pdd = (PayinDetailDef) payListMode.get(i);

                if (!GlobalInfo.dayDB.setObjectToParam(pdd,refdetail))
                {
                    return false;
                }

                if (!GlobalInfo.dayDB.executeSql())
                {
                    return false;
                }
            }

            // 提交事务
            if (!GlobalInfo.dayDB.commitTrans())
            {
                return false;
            }

            // 记录发送任务
            AccessLocalDB.getDefault()
                         .writeTask(StatusType.TASK_SENDPAYJK,
                                    TaskExecute.getKeyTextByBalanceDate());

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }
    }
    
    public boolean updatePrintNum(String syjh,String fphm,String num)
    {
	    try{
	    	String line = "update SALEHEAD set printnum = " + num + " where syjh = '"+syjh+"' AND fphm = " + fphm;
	
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
    
    public boolean writeBankLog(BankLogDef bcd)
    {
    	try
    	{
            // 按表的字段确定对象的数据,表结构不存在的数据不保存
            String[] ref = GlobalInfo.dayDB.getTableColumns("BANKLOG");
            if (ref == null || ref.length <= 0) ref = BankLogDef.ref;
            
    		String line = CommonMethod.getInsertSql("BANKLOG", ref);
			
			if (!GlobalInfo.dayDB.setSql(line))
            {
                return false;
            }
			
			if (!GlobalInfo.dayDB.setObjectToParam(bcd,ref))
            {
                return false;
            }
			
			if (!GlobalInfo.dayDB.executeSql())
            {
                return false;
            }
			
			//记录发送任务
            AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDBANKLOG,TaskExecute.getKeyTextByBalanceDate());

    		return true;
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		return false;
    	}
    }
    
    public boolean updateBankLog(BankLogDef bcd)
    {
    	return updateBankLog(bcd,false,false);
    }
    
    public boolean updateBankLog(BankLogDef bcd,boolean allotflag)
    {
    	return updateBankLog(bcd,allotflag,false);
    }
    
    public boolean updateBankLogbyFirst(BankLogDef bcd)
    {
    	return updateBankLog(bcd,false,true);
    }
    
    public boolean updateBankLog(BankLogDef bcd,boolean allotflag,boolean firstupdate)
    {
    	try
    	{
    		Object obj = GlobalInfo.dayDB.selectOneData("select count(*) from BANKLOG where  rowcode = " + bcd.rowcode + " and rqsj = '"+ bcd.rqsj +"' and syjh = '" + bcd.syjh +"'");
			
			if (obj == null || Long.parseLong(String.valueOf(obj)) < 1)
	        {
				new MessageBox(Language.apply("找不到信用卡交易记录"), null, false); 
				return false;
	        }
			
			String sql;
			if (!allotflag)
			{
				// 第一次撤销或退货日志,要减少原交易的可分配金额
				if (firstupdate && GlobalInfo.sysPara.allowbankselfsale == 'Y' && (bcd.type.equals(String.valueOf(PaymentBank.XYKCX)) || bcd.type.equals(String.valueOf(PaymentBank.XYKTH))) && bcd.retbz == 'Y')
				{
					// 找原交易的日志行
					int oldrowcode = -1;
					String oldrqsj = "";
					String oldsyjh = "";
					if (bcd.oldtrace > 0)
					{
						ResultSet rs = GlobalInfo.dayDB.selectData("select * from BANKLOG where trace = " + bcd.oldtrace + " and allotje > 0");
			            if (rs != null && rs.next())
			            {
			            	oldrowcode = rs.getInt("rowcode");
			            	oldrqsj = rs.getString("rqsj");
			            	oldsyjh = rs.getString("syjh");
			            }
			            GlobalInfo.dayDB.resultSetClose();
					}
					else
					{
						ResultSet rs = GlobalInfo.dayDB.selectData("select * from BANKLOG where cardno = '" + bcd.cardno + "' and je = " + bcd.je + " and allotje > 0");
			            if (rs != null && rs.next())
			            {
			            	oldrowcode = rs.getInt("rowcode");
			            	oldrqsj = rs.getString("rqsj");
			            	oldsyjh = rs.getString("syjh");
			            }
			            GlobalInfo.dayDB.resultSetClose();
					}
					
					//
					if (!GlobalInfo.dayDB.beginTrans()) return false;
					
					
					// 更新当前交易返回信息	
					sql = "update BANKLOG set oldtrace = " + bcd.oldtrace + ",net_bz = '"+ bcd.net_bz +"' , je = " + bcd.je+" , cardno = '" + bcd.cardno + "' , trace = " + bcd.trace +" , " +
							"bankinfo = '"+ bcd.bankinfo +"' , retcode ='"+ bcd.retcode +"' ,retmsg = '"+ bcd.retmsg +"' , retbz ='"+ bcd.retbz+"' " ;			
					if (GlobalInfo.dayDB.isColumnExist("tempStr","BANKLOG"))
					{						
						sql += ", tempstr='"+ bcd.tempstr +"', tempstr1='"+ bcd.tempstr1 + "' ";	
					}
					
					if (GlobalInfo.dayDB.isColumnExist("authno", "BANKLOG"))
					{
						sql += ", authno='"+ bcd.authno +"' ";	
					}
					if (GlobalInfo.dayDB.isColumnExist("kye", "BANKLOG"))
					{
						sql += ", kye='"+ bcd.kye +"' ";	
					}
					
					if (GlobalInfo.dayDB.isColumnExist("memo1", "BANKLOG"))
					{
						sql += ", memo1='"+ bcd.memo1 +"' ";	
					}
					
					if (GlobalInfo.dayDB.isColumnExist("memo2", "BANKLOG"))
					{
						sql += ", memo2='"+ bcd.memo2 +"' ";	
					}
					
					sql += " where rowcode = " + bcd.rowcode + " and rqsj = '"+ bcd.rqsj +"' and syjh = '" + bcd.syjh +"'";
					
					if (!GlobalInfo.dayDB.executeSql(sql))
					{
		                return false;
					}
					
					// 减少原交易可分配金额
					if (oldrowcode > 0)
					{
						sql = "update BANKLOG set allotje = allotje - " + bcd.je + " where rowcode = " + oldrowcode + " and rqsj = '"+ oldrqsj +"' and syjh = '" + oldsyjh +"'";
						
						if (!GlobalInfo.dayDB.executeSql(sql))
						{
			                return false;
						}
					}
					
					if (!GlobalInfo.dayDB.commitTrans()) return false;
				}
				else
				{
					sql = "update BANKLOG set net_bz = '"+ bcd.net_bz +"' , je = " + bcd.je+" , cardno = '" + bcd.cardno + "' , trace = " + bcd.trace +" , " +
							"bankinfo = '"+ bcd.bankinfo +"' , retcode ='"+ bcd.retcode +"' ,retmsg = '"+ bcd.retmsg +"' , retbz ='"+ bcd.retbz+"' " ;
					if (GlobalInfo.dayDB.isColumnExist("tempStr","BANKLOG"))
					{						
						sql += ", tempStr='"+ bcd.tempstr +"', tempStr1='"+ bcd.tempstr1 + "'  " ;					
					}
					if (GlobalInfo.dayDB.isColumnExist("kye","BANKLOG"))
					{						
						sql += ", kye='"+ bcd.kye + "'  " ;					
					}
					sql += " where rowcode = " + bcd.rowcode + " and rqsj = '"+ bcd.rqsj +"' and syjh = '" + bcd.syjh +"'";
					
					if (!GlobalInfo.dayDB.executeSql(sql))
					{
		                return false;
					}				
				}
			}
			else
			{
				sql = "update BANKLOG set allotje = " + bcd.allotje + " where rowcode = " + bcd.rowcode + " and rqsj = '"+ bcd.rqsj +"' and syjh = '" + bcd.syjh +"'";
				
				if (!GlobalInfo.dayDB.executeSql(sql))
				{
	                return false;
				}
			}
			
    		return true;
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		return false;
    	}
    }
    
    public Vector getBankAllotLog()
    {
        BankLogDef info = null;
        Vector v = null;
        ResultSet rs = null;  
        
    	try
    	{
    		rs = GlobalInfo.dayDB.selectData("select * from BANKLOG where type = '" + PaymentBank.XYKXF + "' and retbz = 'Y' and allotje > 0");
            if (rs == null)
            {
                return null;
            }

            while (rs.next())
            {
            	info = new BankLogDef();

                if (!GlobalInfo.dayDB.getResultSetToObject(info))
                {
                    return null;
                }

                if (v == null) v = new Vector();
                v.add(info);
            }
            
            return v;
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		return null;
    	}
        finally
        {
            GlobalInfo.dayDB.resultSetClose();
        }
    }
    
    // 是否是扣回付款明细
    public boolean isBuckleMoney(SalePayDef spd)
    {
    	// 老系统 je < 0 表示扣回的付款方式
    	// 新系统由于零钞转存0111付款方式金额也要记负,但不是扣回
    	// 新系统 flag = '3' 标记扣回付款，金额记负数
    	if ((spd.je < 0 && !CreatePayment.getDefault().isPaymentLczc(spd) && !CreatePayment.getDefault().isPaymentMobileCharge(spd)) || 
    		spd.flag == '3')
    	{
    		return true;
    	}
    	
    	return false;
    }
    
    public boolean writeSaleAppend(Sqldb sql, Vector saleappendlist)
    {
    	try
        {
            if (saleappendlist.size() < 1 )
            {
            	return false;
            }
            
            // 开始事务
            if (!sql.beginTrans())
            {
                return false;
            }

        	String DelSql = "Delete from SaleAppend where "
        		+ "syjh = '" + ((SaleAppendDef)saleappendlist.elementAt(0)).syjh 
        		+ "' and fphm = " + ((SaleAppendDef)saleappendlist.elementAt(0)).fphm;
        	
            if (saleappendlist.size() == 1 && ((SaleAppendDef)saleappendlist.elementAt(0)).rowno == -1)
            {
            	DelSql += " and rowno = " + ((SaleAppendDef)saleappendlist.elementAt(0)).rowno;
            }
            
            if (!sql.setSql(DelSql))
            {
                return false;
            }
            
            if (!sql.executeSql())
            {
                return false;
            }
            
            // 按表的字段确定对象的数据,表结构不存在的数据不保存
            String[] refdetail = GlobalInfo.dayDB.getTableColumns("SALEAPPEND");
            if (refdetail == null || refdetail.length <= 0) refdetail = SaleAppendDef.ref;
            
            // 插入销售附加表
            String line = CommonMethod.getInsertSql("SALEAPPEND", refdetail);
            
            if (!sql.setSql(line))
            {
                return false;
            }
            
            for (int i = 0; i < saleappendlist.size(); i++)
            {
            	SaleAppendDef pdd = (SaleAppendDef) saleappendlist.get(i);

            	pdd.netbz = 'N';
            	
                 if (!sql.setObjectToParam(pdd,refdetail))
                {
                    return false;
                }

                if (!sql.executeSql())
                {
                    return false;
                }
            }

            // 提交事务
            if (!sql.commitTrans())
            {
                return false;
            }
            
            if (GlobalInfo.sysPara.saleAppendStatus != 'Y' && GlobalInfo.sysPara.saleAppendStatus != 'S') return true;
            // 记录发送任务
            AccessLocalDB.getDefault()
                         .writeTask(StatusType.TASK_SENDSALEAPPEND,
                                    TaskExecute.getKeyTextByBalanceDate());

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }
    } 
    
    public boolean getSaleAppendInfo(Sqldb sql, Vector v, String syjh, long fphm)
    {
        SaleAppendDef saleAppend = null;
        ResultSet rs = null;  
        
        if (sql == null)
        {
        	return false;
        }
        
    	try
    	{
    		rs = sql.selectData("select * from SALEAPPEND where SYJH = '" + syjh + "' and FPHM = " + fphm);
            if (rs == null)
            {
                return false;
            }

            while (rs.next())
            {
            	saleAppend = new SaleAppendDef();

                if (!sql.getResultSetToObject(saleAppend))
                {
                    return false;
                }

                if (v == null) v = new Vector();
                v.add(saleAppend);
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
        	sql.resultSetClose();
        }
    }
}
