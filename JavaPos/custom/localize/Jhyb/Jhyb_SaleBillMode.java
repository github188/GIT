package custom.localize.Jhyb;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import custom.localize.Cmls.Cmls_SaleBillMode;
import device.Printer.HisensePT900TAParallel_Printer;

public class Jhyb_SaleBillMode extends Cmls_SaleBillMode
{
	
//	 打印赠券
  public void printSaleTicketMSInfo()
  {
	  
    try
    {
      HisensePT900TAParallel_Printer pt900 = (HisensePT900TAParallel_Printer)Printer.getDefault().printer;

      if ((this.salemsinvo == 0L) || ((this.salemsinvo != 0L) && (this.salehead.fphm != this.salemsinvo)))
      {
        this.salemsinvo = 0L;
        this.zq = null;
        this.gift = null;

        return;
      }

      if ((this.zq == null) || (this.zq.size() <= 0))
      {
        return;
      }

      String line=null;
      for (int i = 0; i < this.zq.size(); ++i)
      {
        GiftGoodsDef def = (GiftGoodsDef)this.zq.elementAt(i);

        String[] infos = def.info.split("_");

        if ((!(def.type.trim().equals("1"))) && (!(def.type.trim().equals("2"))))
        {
          continue;
        }

        String lab = "";

        if (this.salehead.printnum > 0)
        {
          lab = lab + "  (重打印)";
        }

        Printer.getDefault().printLine_Normal("     " + lab);
        Printer.getDefault().printLine_Normal("       " + GlobalInfo.sysPara.mktname);

        if (infos.length > 1)
        {
          Printer.getDefault().printLine_Normal(" " + infos[1]);
        }

        Printer.getDefault().printLine_Normal("");
        Printer.getDefault().printLine_Normal("=====================================");

        Printer.getDefault().printLine_Normal("券号: " + def.code);
        Printer.getDefault().printLine_Normal("单据号:   " + this.salehead.fphm + "   收银机号:" + ConfigClass.CashRegisterCode);
        Printer.getDefault().printLine_Normal("发券日期: " + GlobalInfo.balanceDate);

        
//        if (infos.length > 0)
//        {
//          Printer.getDefault().printLine_Normal("券信息:   " + infos[0]);
//        }
        
        Printer.getDefault().printLine_Normal("");
        Printer.getDefault().printLine_Normal("-------------------------------------");
       
        
        pt900.setBigChar(true);
        
        line = Convert.appendStringSize("",def.info.substring(0,def.info.indexOf(";"))+":",0,10,8); 
        Printer.getDefault().printLine_Normal(Convert.appendStringSize(line,ManipulatePrecision.doubleToString(def.je) + "元",10,12,38));
//        Printer.getDefault().printLine_Normal(" " + ManipulatePrecision.doubleToString(def.je) + "元");
        
        pt900.setBigChar(false);

        Printer.getDefault().printLine_Normal("-------------------------------------");
        
        pt900.setBigChar(true);
        
        Printer.getDefault().printLine_Normal(def.info.substring(def.info.indexOf(";")+1,def.info.length()));
        
        pt900.setBigChar(false);

//        Printer.getDefault().printLine_Normal("def.info");

        Printer.getDefault().printLine_Normal("");

        if (infos.length > 2)
        {
          Printer.getDefault().printLine_Normal("本券使用范围：" + infos[2]);
        }

        String[] r = def.memo.split(";");
        String startdate = "";
        String enddate = "";

        if (r.length > 1)
        {
          startdate = r[0].trim();
          enddate = r[1].trim();
        }
        else
        {
          enddate = def.memo;
        }

        Printer.getDefault().printLine_Normal("·本券使用开始日期：" + startdate);
        Printer.getDefault().printLine_Normal("·本券使用有效期至：" + enddate);

        if (r.length > 2)
        {
          Printer.getDefault().printLine_Normal(r[2]);
        }

        Printer.getDefault().printLine_Normal("");
        Printer.getDefault().printLine_Normal("");
        Printer.getDefault().printLine_Normal("");
        Printer.getDefault().printLine_Normal("");
        Printer.getDefault().printLine_Normal("");
        Printer.getDefault().printLine_Normal("");
        Printer.getDefault().printLine_Normal("");

        Printer.getDefault().cutPaper_Normal();
      }
    }
    catch (Exception ex) {
      new MessageBox("打印异常:\n" + ex.getMessage());
    }
  }
  
}