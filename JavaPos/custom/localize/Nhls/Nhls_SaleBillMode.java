package custom.localize.Nhls;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GiftGoodsDef;

import custom.localize.Bhcm.Bhcm_SaleBillMode;

public class Nhls_SaleBillMode extends Bhcm_SaleBillMode
{
	public void printBottom()
	{
		if (zq != null)
		{
			StringBuffer line = new StringBuffer();
			double je = 0;
			for (int i = 0; i < this.zq.size(); i++)
			{
				GiftGoodsDef def = (GiftGoodsDef) this.zq.elementAt(i);
				
				if (def.type.equals("4"))
				{
					String[] infos = def.info.split("&");
					String strje = ManipulatePrecision.doubleToString(def.je);
					line.append(Convert.appendStringSize("",infos[0],1,16,16,1)+":"+ Convert.appendStringSize("",strje,1,10,10,0)+"\n");
					if (GlobalInfo.sysPara.printYXQ == 'Y') line.append(Convert.appendStringSize("","券有效期",1,16,16,1)+":"+ Convert.appendStringSize("",def.memo,1,24,24,0)+"\n");
					je +=def.je;
				}
			}
			
			if (je > 0)
			Printer.getDefault().printLine_Normal("本次小票返券金额:" + ManipulatePrecision.doubleToString(je));
			Printer.getDefault().printLine_Normal(line.toString());
		}
		
//		 设置打印区域
		setPrintArea("Bottom");
		
		//
        printVector(getCollectDataString(Bottom,-1,Width));	
	}
}
