package custom.localize.Wjjy;

import java.util.Vector;

import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Bhls.Bhls_NetService;

public class Wjjy_NetService extends Bhls_NetService {

	public boolean getBackSellLimit(String oldsyjh,String oldfphm,String cardno,String memo,SpareInfoDef info)
	{
	   	 if (!GlobalInfo.isOnline)
         {
             return false;
         }

    	 CmdHead head = null;
         StringBuffer line = new StringBuffer();
         int result = -1;
         String[] values = {oldsyjh,oldfphm,GlobalInfo.sysPara.mktcode,cardno,memo};
         String[] args = { "oldsyjh","oldfphm","mktcode","cardno","memo"};
         
         try
         {
        	 head = new CmdHead(CmdDef.GETBACKSELLLIMIT);
        	 line.append(head.headToString() + Transition.SimpleXML(values, args));
        	 
             result = HttpCall(getMemCardHttp(CmdDef.GETBACKSELLLIMIT),line, "");
             
             if (result == 0)
             {
            	 
            	 Vector v = new XmlParse(line.toString()).parseMeth(0,new String[]{"limitje","memo"});

            	 if (v.size() > 0)
            	 {
            		 String[] row = (String[]) v.elementAt(0);
            		 try{
            		 info.num1 = Double.parseDouble(row[0]);
            		 }catch(Exception er)
            		 {
            			 er.printStackTrace();
            		 }
            		 info.str1 = row[1];
            	 }
             }
             return true;
         }
         catch (Exception ex)
         {
        	 ex.printStackTrace();
         }
         
         return false;
	}
}
