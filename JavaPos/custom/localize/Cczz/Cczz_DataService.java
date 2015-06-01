package custom.localize.Cczz;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.AssemblyInfo;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.LineDisplay;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SyjMainDef;

import custom.localize.Bcrm.Bcrm_DataService;


public class Cczz_DataService extends Bcrm_DataService
{
	public int sendSaleWebService(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		return 0;
	}
    public boolean getHHback(String ysyjh, StringBuffer yfphm)
    {
        if (((Cczz_AccessDayDB) Cczz_AccessDayDB.getDefault()).getlasthhbackinfo(ysyjh, yfphm))
        {
            return true;
        }

        return false;
    }
    
    public boolean getServerTime(boolean settime)
    {
    	if (super.getServerTime(false))
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    public boolean checkSyjValid()
    {
    	if (!GlobalInfo.isOnline)
    	{
            if (new MessageBox("连接网络失败,系统进入脱网状态!\n当前时间为"+ManipulateDateTime.getCurrentDateTime()+"\n请检查时间是否正确？",null,true).verify() != GlobalVar.Key1)           
            {
            	return false;
            }
    	}
    	
        if (GlobalInfo.isOnline)
        {
        	// 调试模式不上传IP地址
        	String ipaddr = GlobalInfo.ipAddr;
        	if (ConfigClass.DebugMode) ipaddr = "";        	
            if (!NetService.getDefault().getSyjMain(ConfigClass.CashRegisterCode, ipaddr, ConfigClass.CDKey, AssemblyInfo.AssemblyVersion))
            {
                // 如果还是联网状态,则说明款机定义有错;如果脱网继续从本地读取款机定义
                if (GlobalInfo.isOnline)
                {
                    return false;
                }
            }
        }

        //
        SyjMainDef syjDef = new SyjMainDef();

        if (!AccessLocalDB.getDefault().readSyjDef(syjDef))
        {
            new MessageBox("读取收银机定义时发生错误，系统马上退出!", null, false);

            return false;
        }

        //
        if (!syjDef.syjh.equals(ConfigClass.CashRegisterCode))
        {
        	AccessDayDB.getDefault().writeWorkLog("收银机号[" + ConfigClass.CashRegisterCode + "]和上次使用配置[" + syjDef.syjh + "]不一致,自动更新");
            if (new MessageBox("收银机号[" + ConfigClass.CashRegisterCode + "]和上次使用配置[" + syjDef.syjh + "]不一致\n\n你确定要以新的设置进入系统吗?\n\n任意键-是 / 2-否", null, false).verify() == GlobalVar.Key2)
            {
                return false;
            }

            // 以配置文件中的收银机号为准
            syjDef.syjh = ConfigClass.CashRegisterCode;
        }

        if (!syjDef.ipaddr.equals("") && !syjDef.ipaddr.equals(GlobalInfo.ipAddr))
        {
        	AccessDayDB.getDefault().writeWorkLog("收银机IP[" + GlobalInfo.ipAddr + "]和上次使用配置[" + syjDef.ipaddr + "]不一致,自动更新");
            // 以当前设置的IP为准
            syjDef.ipaddr = GlobalInfo.ipAddr;
        }

        //
        GlobalInfo.syjDef = syjDef;

        //
        if (String.valueOf(GlobalInfo.syjDef.ists).length() <= 0)
        {
            GlobalInfo.syjDef.ists = 'Y';
        }

        if (GlobalInfo.syjDef.isprint != 'Y')
        {
            Printer.getDefault().setEnable(false);
        }

        if (GlobalInfo.syjDef.isdisp != 'Y')
        {
            LineDisplay.getDefault().setEnable(false);
        }

        if (GlobalInfo.syjDef.datatime <= 0)
        {
            GlobalInfo.syjDef.datatime = 30;
        }

        if (GlobalInfo.syjDef.dataspace <= 0)
        {
            GlobalInfo.syjDef.dataspace = 100;
        }

        return true;
    }

    public void updateSendSaleData(SaleHeadDef saleHead, String memo, double value, Sqldb sql)
    {
        super.updateSendSaleData(saleHead, memo, value, sql);

        if ((memo != null) && memo.trim().equals(""))
        {
            return;
        }

        if (sql != null)
        {
            return;
        }

        saleHead.str3 = memo;

        String line = "update SALEHEAD set STR3 = '" + saleHead.str3 + "' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " +
                      saleHead.fphm;

        GlobalInfo.dayDB.executeSql(line);
    }

    // 获取小票实时积分
    public void getCustomerSellJf(SaleHeadDef saleHead)
    {
        String[] row = new String[5];

        if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
        {
            if (NetService.getDefault().getCustomerSellJf(row, saleHead.mkt, saleHead.syjh, String.valueOf(saleHead.fphm)))
            {
                saleHead.bcjf = Convert.toDouble(row[0]);
                saleHead.ljjf = Convert.toDouble(row[1]);
                saleHead.num4 = Convert.toDouble(row[3]);
                saleHead.memo = row[4];
                saleHead.str5 = row[2];

                AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 1, saleHead.bcjf, saleHead.ljjf,String.valueOf(saleHead.num4));
                AccessDayDB.getDefault().updateSaleHeadStr(saleHead.fphm,"str5",saleHead.str5);

                StringBuffer sb = new StringBuffer();
                sb.append("累计积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.ljjf), 0, 10, 10, 1) + "\n");
                sb.append("本次积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.bcjf), 0, 10, 10, 1) + "\n");
                sb.append("倍享积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.num4), 0, 10, 10, 1) + "\n");
                sb.append(saleHead.memo);
                new MessageBox(sb.toString());
                
            }
            else
            {
                saleHead.bcjf = 0;
                new MessageBox("计算本笔交易小票积分失败\n请到会员中心查询积分!");
            }
            
            // 打印会员信息
            String[] row1 = new String[10];
            if (((Cczz_NetService)NetService.getDefault()).javaGetCustXF(row1, saleHead.hykh, saleHead.syjh, String.valueOf(saleHead.fphm)))
            {
            	new MessageBox("月消费："+row1[1]+"\n年消费："+row1[0]);
            	
            	saleHead.str5 = saleHead.str5 + "\n" + "月消费："+row1[1]+"\n年消费："+row1[0];
            	AccessDayDB.getDefault().updateSaleHeadStr(saleHead.fphm,"str5",saleHead.str5);
            }
        }
    }
    
    public Vector getSaleTicketMSInfo(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
    {
    	Vector gifts = super.getSaleTicketMSInfo(saleHead, saleGoods, salePayment);
		// 获取定向积分明细
		gifts = getSellJfList(gifts, saleHead, saleGoods, salePayment);
		return gifts;
    }
}
