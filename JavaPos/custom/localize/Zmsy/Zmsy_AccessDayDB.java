package custom.localize.Zmsy;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Zmjc.Zmjc_AccessDayDB;

public class Zmsy_AccessDayDB extends Zmjc_AccessDayDB
{

	/**
	 * 从本地数据库取提货单信息
	 * @param thdh 根据提货单号查询提货单信息； 提货单号为""则查询所有未上传提货单信息； 
	 * @param v THDDef.ref
	 * @return  boolean 返回是否成功保存
	 */
	public boolean getTHD(String thdh, Vector v)
	{
		ResultSet rs = null;
		if(v == null) v = new Vector();
		String sql = "";
		if(thdh.equals(""))
		{
			sql = " NET_BZ <> 'Y' ";
		}
		else
		{
			sql = " THDH = '" + thdh + "' ";
		}
        try
        {
            rs = GlobalInfo.dayDB.selectData("SELECT FPHM, SYJH, THDH, NET_BZ FROM THD WHERE " + sql);
            if (rs == null)
            {
                return false;
            }
            while(rs.next())
            {
            	THDDef thd = new THDDef();
            	if (!GlobalInfo.dayDB.getResultSetToObject(thd)) return false;
                v.add(thd);
            }
            return true;
        }
        catch (Exception e)
        {
        	PosLog.getLog(this.getClass().getSimpleName()).error(e);
            return false;
        }
        finally
        {
            GlobalInfo.dayDB.resultSetClose();
        }
	}
	
	/**
	 * 单笔提货单保存到本地数据库
	 * @param v THDDef.ref
	 * @return  boolean 返回是否成功保存
	 */
	public boolean saveTHDH(THDDef thddef)
	{
		// TODO Auto-generated method stub
    	try
        {
            // 按表的字段确定对象的数据,表结构不存在的数据不保存
            String[] thdref = GlobalInfo.dayDB.getTableColumns("THD");
            if (thdref == null || thdref.length <= 0) thdref = THDDef.ref;
            
            //插入一笔提货单信息
            String line = CommonMethod.getInsertSql("THD",thdref);

            if (!GlobalInfo.dayDB.setSql(line))
            {
                return false;
            }

            if (!GlobalInfo.dayDB.setObjectToParam(thddef,thdref))
            {
                return false;
            }

            if (!GlobalInfo.dayDB.executeSql())
            {
                return false;
            }

            return true;
        }
        catch (Exception ex)
        {
        	PosLog.getLog(this.getClass().getSimpleName()).error(ex);
            return false;
        }
		
	}


	/**
	 * 修改提货单上传状态 
	 * @param NET_BZ 上传状态
	 * @param fphm 发票号码
	 * @param syjh 收银机号
	 * @param thdh 提货单号
	 * @return boolean 返回更新上传状态是否成功
	 */
	public boolean updateTHDH(char NET_BZ, String fphm, String syjh, String thdh)
    {
	    try{
	    	String line = "UPDATE THD SET NET_BZ = '" + NET_BZ + "' WHERE FPHM = " + fphm + " AND SYJH = '" + syjh + "' AND THDH = '" + thdh + "'";
//	    	StringBuffer line = new StringBuffer("UPDATE THD SET NET_BZ = '").append(NET_BZ).append("' WHERE FPHM = ").append(fphm).append(" AND SYJH = '").append(GlobalInfo.syjDef.syjh).append("' AND THDH = '").append(thdh).append("'");
	    	
	        if (!GlobalInfo.dayDB.executeSql(line)) return false;
	        
	        return true;
	    }
	    catch (Exception er)
	    {
	    	PosLog.getLog(this.getClass().getSimpleName()).error(er);
	        return false;
	    }
    }
	
	//检查小票平衡
	public boolean checkSaleData(SaleHeadDef saleHead, Vector saleGoods,Vector salePayment)
    {
        int i;
        double je,zl;
        SaleGoodsDef saleGoodsDef = null;
        SalePayDef salePayDef = null;
        boolean isOK = false;
		String msg="";
        try
        {        	
            // 检查交易类型
            if (!SellType.VALIDTYPE(saleHead.djlb))
            {
                new MessageBox("[" + saleHead.djlb + "]交易类型无效!\n不允许进行【"+SellType.getDefault().typeExchange( saleHead.djlb, saleHead.hhflag, saleHead)+"】");

                return isOK;
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
            	msg="交易主单数据相互不平!\n\n实际付款 - 找零 = "+
            	ManipulatePrecision.doubleToString(saleHead.sjfk - saleHead.zl)+
            	"\n应收金额 + 损溢 = "+ManipulatePrecision.doubleToString(saleHead.ysje + saleHead.sswr_sysy + saleHead.fk_sysy);
            	new MessageBox(msg);
                return isOK;
            }
            double je_gkcd=(saleHead.num2-saleHead.num3-saleHead.num6) + saleHead.num9;//(顾客承担的税金)+即购即提_暂缴税金
            if (ManipulatePrecision.doubleCompare(saleHead.ysje - je_gkcd,saleHead.hjzje - saleHead.hjzke,2) != 0)
            {
            	msg="交易主单数据相互不平!\n\n应收金额 = "+
            	ManipulatePrecision.doubleToString(saleHead.ysje - je_gkcd)+//wangyong add " - je_gkcd" by 2014.5.29 
            	"\n合计金额 - 合计折扣 = "+ManipulatePrecision.doubleToString(saleHead.hjzje - saleHead.hjzke);            	
            	new MessageBox(msg);
                return isOK;
            }
            if (ManipulatePrecision.doubleCompare(saleHead.hjzke,saleHead.yhzke + saleHead.hyzke + saleHead.lszke,2) != 0)
            {
            	msg="交易主单数据相互不平!\n\n合计折扣 = "+
            	ManipulatePrecision.doubleToString(saleHead.hjzke)+
            	"\n折扣明细 = "+ManipulatePrecision.doubleToString(saleHead.yhzke + saleHead.hyzke + saleHead.lszke);
            	new MessageBox(msg);
                return isOK;
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
                	new MessageBox("第 " + (i + 1) + " 行商品 [" + saleGoodsDef.code + "] 数量不合法\n请修改此行商品数量或者删除此商品后重新录入");
                	return isOK;
                }
                
                if (saleGoodsDef.type == '8')
                {
                    je -= saleGoodsDef.hjje - saleGoodsDef.hjzk;
                }
                else
                {
                    je += saleGoodsDef.hjje - saleGoodsDef.hjzk;
                }
            }
            if (ManipulatePrecision.doubleCompare(saleHead.ysje - je_gkcd,je,2) != 0)
            {
            	msg="交易主单和商品明细不平!\n\n主单应收金额 = "+
            	ManipulatePrecision.doubleToString(saleHead.ysje)+
                "\n商品合计金额 = "+ManipulatePrecision.doubleToString(je);
            	new MessageBox(msg);
                return isOK;
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
            	msg="交易主单和付款明细不平!\n\n主单实际付款 = "+
            	ManipulatePrecision.doubleToString(saleHead.sjfk)+
                "\n付款合计金额 = "+ManipulatePrecision.doubleToString(je);
            	new MessageBox(msg);
                return isOK;
            }
            
            isOK=true;
        }
        catch(Exception ex)
        {
        	PosLog.getLog(this.getClass().getSimpleName()).error(ex);
        	PosLog.getLog(this.getClass().getSimpleName()).info(ex);
        }
        finally
        {
        	if (isOK==false)
        	{
        		PosLog.getLog(this.getClass().getSimpleName()).error("小票不平衡：syjh=[" + saleHead.syjh + "],fphm=[" + saleHead.fphm + "],ysje=[" + saleHead.ysje + "],msg=[" + msg + "].");
        		writeSaleTraceLog(saleHead, saleGoods, salePayment, false);
        	}
        }
        return isOK;
        
		//行邮税 待加
		//return super.checkSaleData(saleHead, saleGoods, salePayment);
    }
	
	public boolean writeSaleTraceLog(SaleHeadDef saleHead, Vector saleGoods,Vector salePayment,boolean delflag)
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
            name += "/error_"+saleHead.syjh + "_"+saleHead.fphm+ "_" + ManipulateDateTime.getCurrentTime().replace(":", "") + ".dat";
            
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

	/**
	 * 单笔提货单保存到本地数据库
	 * @param syjh 收银机号
	 * @param fphm 发票号码
	 * @param sbSJSeq 税金号
	 * @param sbHBSeq 航班号
	 * @return boolean 返回是否成功保存
	 */
	public boolean savePrintSeq(String syjh,long fphm, double sbSJSeq,double sbHBSeq )
	{
		try
		{

			String line = "UPDATE SALEHEAD SET NUM7 = " + sbSJSeq + ", NUM8 = " + sbHBSeq + " WHERE FPHM = "
					+ fphm + " AND SYJH = '" + syjh + "' ";
			//StringBuffer line = new StringBuffer("UPDATE SALEHEAD SET NUM7 = '").append(sbSJSeq).append("', NUM8 = '").append(sbHBSeq).append("' WHERE FPHM = ").append(fphm).append(" AND SYJH = '").append(syjh).append("' ");

			if (!GlobalInfo.dayDB.executeSql(line)) return false;

			return true;
		}
		catch (Exception er)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(er);
			return false;
		}
	}
	
	 public boolean deleteSale(String syjh, long fphm)
	    {
	        boolean done = false;
	        String line = "";
	        
	        try
	        {
		    	PublicMethod.timeStart("正在删除本地小票[" + String.valueOf(fphm) + "],请等待......");
		    	PosLog.getLog(this.getClass().getSimpleName()).info("正在删除本地小票[" + String.valueOf(fphm) + "],请等待......");
		    	
		        // 开始事务
		        if (!GlobalInfo.dayDB.beginTrans())
		        {
		            return false;
		        }
		
		        //删除小票头
		        line = "delete from SALEHEAD where fphm = " + fphm + " AND SYJH = '" + syjh + "' ";
		
		        if (!GlobalInfo.dayDB.executeSql(line))
		        {
			    	PosLog.getLog(this.getClass().getSimpleName()).info("删除小票头失败,line=[" + line + "].");
		        	new MessageBox("删除小票头失败...", null, false);
		            return false;
		        }
		
		        //删除小票商品明细
		        line = "delete from SALEGOODS where fphm = " + fphm + " AND SYJH = '" + syjh + "' ";
		
		        if (!GlobalInfo.dayDB.executeSql(line))
		        {
			    	PosLog.getLog(this.getClass().getSimpleName()).info("删除小票商品明细失败,line=[" + line + "].");
		        	new MessageBox("删除小票商品明细失败...", null, false);
		            return false;
		        }
		        
		        //删除小票付款明细
		        line = "delete from SALEPAY where fphm = " + fphm + " AND SYJH = '" + syjh + "' ";
		
		        if (!GlobalInfo.dayDB.executeSql(line))
		        {
			    	PosLog.getLog(this.getClass().getSimpleName()).info("删除小票付款明细失败,line=[" + line + "].");
		        	new MessageBox("删除小票付款明细失败...", null, false);
		            return false;
		        }
		        
		        //删除小票顾客信息
		        line = "delete from SALECUST where fphm = " + fphm + " AND SYJH = '" + syjh + "' ";
		
		        if (!GlobalInfo.dayDB.executeSql(line))
		        {
			    	PosLog.getLog(this.getClass().getSimpleName()).info("删除小票顾客信息失败,line=[" + line + "].");
		        	new MessageBox("删除小票顾客信息失败...", null, false);
		            return false;
		        }
		        
		        //删除提货单信息
		        
		
		        // 撤销汇总数据 暂时不支持
		        /*if (!writeSaleState(saleHead, saleGoods, salePayment))
		        {
		            return false;
		        }*/
		
		        // 提交事务
		        if (!GlobalInfo.dayDB.commitTrans())
		        {
		            return false;
		        }
         

		    	PosLog.getLog(this.getClass().getSimpleName()).info("删除小票成功.");
		        //
		        done = true;
		        return true;
		    }
		    catch (Exception ex)
		    {
		    	PosLog.getLog(this.getClass().getSimpleName()).info("删除小票发生异常：line=" + line);
		    	PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		    	new MessageBox("删除小票发生异常\n\n" + ex.getMessage());
		    	return false;
		    }
	        finally
	        {
	            if (!done)
	            {
	                GlobalInfo.dayDB.rollbackTrans();
	            }
	            
	        	//
	        	PublicMethod.timeEnd("删除本地小票库耗时: ");            
	        }
	    }


		//读取当天最后一笔小票的航班号
		public boolean checkSale(String syjh, long fphm)
		{
			Object obj = null;
			String line = "";
			try
			{
				line = "select count(*) from SALEHEAD where fphm = " + fphm + " AND SYJH = '" + syjh + "' ";
				obj = GlobalInfo.dayDB.selectOneData(line);
				if (obj != null && Convert.toInt(obj)>0) return true;				 
			}
			catch (Exception e)
			{
				PosLog.getLog(this.getClass().getSimpleName()).info("检查是否存在时小票发生异常：line=" + line);
				PosLog.getLog(this.getClass().getSimpleName()).error(e);

			}
			return false;
		}

}
