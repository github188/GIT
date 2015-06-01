package custom.localize.Wdgc;

import java.io.File;
import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Struct.InvoiceInfoDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bhcm.Bhcm_DataService;


public class Wdgc_DataService extends Bhcm_DataService {
	 public void checkInvoiceNo()
	    {
	        Object obj;

	        // 先检查当前小票号是否小于当天最大一笔小票号
	        obj = GlobalInfo.dayDB.selectOneData("select max(fphm) from SALEHEAD where syjh = '" + ConfigClass.CashRegisterCode + "'");

	        if (obj != null)
	        {
	            if (GlobalInfo.syjStatus.fphm <= Long.parseLong(String.valueOf(obj)))
	            {
	                //记录日志
	                AccessDayDB.getDefault().writeWorkLog("本地小票号 " + String.valueOf(GlobalInfo.syjStatus.fphm) + " 比交易小票号 " + String.valueOf(obj) + " 小");

	                //改写最大小票号
	                GlobalInfo.syjStatus.fphm = Long.parseLong(String.valueOf(obj)) + 1;
	                AccessLocalDB.getDefault().writeSyjStatus();
	            }
	        }
	        else
	        {
	            // 再检查当前小票号是否小于前一个工作日最大一笔小票号
	            ManipulateDateTime mdt = new ManipulateDateTime();
	            String date = mdt.getDateByEmpty();
	            File invoice = new File(ConfigClass.LocalDBPath + "Invoice//");
	            String[] list = invoice.list();
	            int max = 0;

	            for (int i = 0; i < list.length; i++)
	            {
	                if ((date.compareTo(list[i]) == 0) || (list[i].length() != 8))
	                {
	                    continue;
	                }
	                else if (list[i].compareTo(list[max]) > 0)
	                {
	                    max = i;
	                }
	            }

	            date = list[max];

	            Sqldb sql =  LoadSysInfo.getDefault().loadDayDB(date);

	            if (sql != null)
	            {
	                try
	                {
	                    obj = sql.selectOneData("select max(fphm) from SALEHEAD where syjh = '" + ConfigClass.CashRegisterCode + "'");

	                    if (obj != null)
	                    {
	                        if (GlobalInfo.syjStatus.fphm <= Long.parseLong(String.valueOf(obj)))
	                        {
	                            //记录日志
	                            AccessDayDB.getDefault()
	                                       .writeWorkLog("本地小票号 " + String.valueOf(GlobalInfo.syjStatus.fphm) + " 比交易小票号 " + String.valueOf(obj) + " 小");

	                            //改写最大小票号
	                            GlobalInfo.syjStatus.fphm = Long.parseLong(String.valueOf(obj)) + 1;
	                            AccessLocalDB.getDefault().writeSyjStatus();
	                        }
	                    }
	                    else{
	                    	PathFile.deletePath(ConfigClass.LocalDBPath + "Invoice//" + date + "day.db3");
	                    	PathFile.copyPath(ConfigClass.LocalDBPath + "Invoice//" + date + "//" + "day.db3", ConfigClass.LocalDBPath + "Invoice//" + date +"//Err//" + "day.db3");
	            			PathFile.copyPath(ConfigClass.LocalDBPath + "Invoice//" + date + "//Bak//" + "day.db3", ConfigClass.LocalDBPath + "Invoice//" + date + "//" + "day.db3");
	                    }
	                }
	                catch (Exception er)
	                {
	                    er.printStackTrace();
	                }
	                finally
	                {
	                    sql.Close();
	                }
	            }

	            sql = null;
	        }

	        // 再检查当前小票号是否小于网上最大小票号,网上最大小票号返回的是最大加1
	        InvoiceInfoDef inv = new InvoiceInfoDef();

	        if (GlobalInfo.isOnline && NetService.getDefault().getInvoiceInfo(inv))
	        {
	            if (GlobalInfo.syjStatus.fphm < inv.maxinv)
	            {
	                //记录日志
	                AccessDayDB.getDefault()
	                           .writeWorkLog("本地小票号 " + String.valueOf(GlobalInfo.syjStatus.fphm) + " 比网上小票号 " + String.valueOf(inv.maxinv) + " 小");

	                //改写最大小票号
	                GlobalInfo.syjStatus.fphm = inv.maxinv;
	                AccessLocalDB.getDefault().writeSyjStatus();
	            }
	        }

	        // 再检查当前小票号是否达到最大值
	        if (GlobalInfo.syjStatus.fphm > 9999999)
	        {
	            new MessageBox("目前系统小票号已达到最大值\n\n系统将重新设置小票号");

	            //记录错误日志
	            AccessDayDB.getDefault().writeWorkLog("系统小票号已达到最大值999999");

	            //改写最大小票号
	            GlobalInfo.syjStatus.fphm = 1;
	            AccessLocalDB.getDefault().writeSyjStatus();
	        }
	    }
	 
           //万达会员会员积分由他们自己计算，不需要上传
		public void getCustomerSellJf(SaleHeadDef saleHead, Vector saleGoods, Vector salePay)
		{
			
		}
}
