package custom.localize.Nnmk;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.AssemblyInfo;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.LineDisplay;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SyjMainDef;

import custom.localize.Bcrm.Bcrm_AccessBaseDB;
import custom.localize.Cmls.Cmls_DataService;
import custom.localize.Cmls.Cmls_NetService;

public class Nnmk_DataService extends Cmls_DataService
{
	public boolean checkSyjValid()
    {
        if (GlobalInfo.isOnline)
        {
        	// 调试模式不上传IP地址
        	String ipaddr = GlobalInfo.ipAddr;
        	
        	
        	// 由于数据库只定义了40位长,避免版本号长度超过40
        	String version = AssemblyInfo.AssemblyVersion + " , " + CustomLocalize.getDefault().getAssemblyVersion();
        	version = version.replaceAll(" bulid ", "-");
        	version = version.replaceAll(" build ", "-");
        	if (version.length() > 40) version = version.substring(0,40);
            if (!NetService.getDefault().getSyjMain(ConfigClass.CashRegisterCode, ipaddr , ConfigClass.CDKey, version))
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
            if (new MessageBox("收银机号[" + ConfigClass.CashRegisterCode + "]和上次使用配置[" + syjDef.syjh + "]不一致\n\n你确定要以新的设置进入系统吗?\n\n任意键-是 / 2-否", null, false).verify() == GlobalVar.Key2)
            {
                return false;
            }

            // 以配置文件中的收银机号为准
            syjDef.syjh = ConfigClass.CashRegisterCode;
        }

        if (!syjDef.ipaddr.equals("") && !syjDef.ipaddr.equals(GlobalInfo.ipAddr))
        {
            if (new MessageBox("收银机IP[" + GlobalInfo.ipAddr + "]和上次使用配置[" + syjDef.ipaddr + "]不一致\n\n你确定要以新的设置进入系统吗?\n\n任意键-是 / 2-否", null, false).verify() == GlobalVar.Key2)
            {
                return false;
            }
        	
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
        	if (Printer.getDefault() != null) Printer.getDefault().close();
        }

        if (GlobalInfo.syjDef.isdisp != 'Y')
        {
        	if (LineDisplay.getDefault() != null) LineDisplay.getDefault().close();
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
	
	 // 获取小票实时积分
    public void getCustomerSellJf(SaleHeadDef saleHead)
    {
        String[] row = new String[4];

        if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
        {
            if (NetService.getDefault().getCustomerSellJf(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm)))
            {
            	
            	//System.out.println(row[0]+"  "+row[1]+" "+row[2]+" "+row[3]);
                saleHead.bcjf = Double.parseDouble(row[0]);
                saleHead.ljjf = Double.parseDouble(row[1]);
                saleHead.num5 = Convert.toDouble(row[3]);
                saleHead.str5 = row[2];

                AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 1, saleHead.bcjf, saleHead.ljjf);
                AccessDayDB.getDefault().updateSaleHeadStr(saleHead.fphm,"num5",String.valueOf(saleHead.num5));
                
                if (GlobalInfo.sysPara.sendhyjf == 'Y')
                {
                	if (!sendHykJf(saleHead))
                	{
                		 new MessageBox("本笔积分同步失败无法获得累计积分\n请到会员中心查询累计积分!");
                	}
                }
                
                if ((Math.abs(saleHead.bcjf) > 0 || Math.abs(saleHead.ljjf) > 0 || Math.abs(saleHead.num5) > 0) && GlobalInfo.sysPara.calcjfbyconnect == 'Y')
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append("本笔交易有存在积分\n");
                    sb.append("本次积分: " + Convert.appendStringSize("", String.valueOf(saleHead.bcjf), 0, 10, 10, 1) + "\n");
                    sb.append("印花积分: " + Convert.appendStringSize("", String.valueOf(saleHead.num5), 0, 10, 10, 1) + "\n");
                    sb.append("累计积分: " + Convert.appendStringSize("", String.valueOf(saleHead.ljjf), 0, 10, 10, 1));
                    
                    new MessageBox(sb.toString());
                } 
            }
            else
            {
                saleHead.bcjf = 0;
                new MessageBox("计算本笔交易小票积分失败\n请到会员中心查询积分!");
            }
        }
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
    
    public Vector getSaleTicketMSInfo(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
    {
    	if (GlobalInfo.sysPara.calcfqbyreal != 'A') return null;
    	
        // 查询小票实时赠品信息
        Vector v = new Vector();
        NetService netservice = NetService.getDefault();

        if (netservice.getSaleTicketMSInfo(v, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm), "N", NetService.getDefault().getMemCardHttp(CmdDef.GETMSINFO)))
        {
        	return v;
        }
        
        return null;
    }
    
	//查找满减满增促销
    public boolean findPopRuleCRM(GoodsPopDef popDef,String code,String gz,String uid,String rulecode,String catid,String ppcode,String time,String cardno,String cardtype,String isfjk,String grouplist,String djlb)
    {
    	if (GlobalInfo.isOnline)
    	{
    		Cmls_NetService netservice = (Cmls_NetService)NetService.getDefault();
			boolean suc =  netservice.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype,isfjk,grouplist,djlb,NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP),CmdDef.FINDCRMPOP);
			
	   		if (GlobalInfo.sysPara.searchPosAndCUST.equals("Y"))
    		{
	   			GoodsPopDef popDef1 = new GoodsPopDef();
	   			boolean suc1 = netservice.findPopRuleCRM(popDef1, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype,isfjk,grouplist,djlb,NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP + 200),CmdDef.FINDCRMPOP + 200);
	   			popDef.type = popDef1.type;
	   			popDef.mode = popDef.mode+"|"+popDef1.mode;
	   			popDef.jsrq = popDef1.jsrq;
	   			popDef.rule = popDef1.rule + popDef.rule;
	   			
	   			suc = suc || suc1;
    		}
	   		
	   		return suc;
    	}
    	else
    	{
    		Bcrm_AccessBaseDB accessbasedb = (Bcrm_AccessBaseDB)AccessBaseDB.getDefault();
			return accessbasedb.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time,cardno,cardtype);
    	}	
    }
}
