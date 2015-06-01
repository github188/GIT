package custom.localize.Bhls;

import java.util.Vector;

import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsAmountDef;
import com.efuture.javaPos.Struct.GoodsPopDef;

public class Bhls_NetService extends NetService 
{
	// 查找规则促销
    public boolean findPopRule(GoodsPopDef popDef,String code,String gz,String uid,String rulecode,String catid,String ppcode,String time, String custType, String custNo)
    {
    	 if (!GlobalInfo.isOnline)
         {
             return false;
         }
    	 
    	 CmdHead head = null;
         StringBuffer line = new StringBuffer();
         int result = -1;
         String[] values = {code,gz,uid,rulecode,catid,ppcode,time,custType,custNo};
         String[] args = { "code","gz","uid","rule","catid","ppcode","rqsj","custType","custNo"};
         
         try
         {
        	 head = new CmdHead(CmdDef.FINDRULEPOP);
        	 line.append(head.headToString() + Transition.SimpleXML(values, args));

             result = HttpCall(line, "");
             
             if (result == 0)
             {
            	 Vector v = new XmlParse(line.toString()).parseMeth(0,GoodsPopDef.ref);

            	 if (v.size() > 0)
            	 {
            		 String[] row = (String[]) v.elementAt(0);

            		 if (Transition.ConvertToObject(popDef, row))
            		 {
            			 return true;
            		 }
            	 }
             }
         }
         catch (Exception ex)
         {
        	 ex.printStackTrace();
         }
         
         return false;
    }

    public boolean findRulePopGift(Vector giftGoods,String djbh)
    {
    	if (!GlobalInfo.isOnline)
        {
    		return false;
        }
    	
    	CmdHead head = null;
    	StringBuffer line = new StringBuffer();
    	int result = -1;
        String[] values = {djbh};
        String[] args = {"djbh"};
         
    	try
    	{
    		head = new CmdHead(CmdDef.FINDRULEPOPGIFT);
       	 	line.append(head.headToString() + Transition.SimpleXML(values, args));
    		
       	 	result = HttpCall(line, "");
       	 
       	 	if (result == 0)
       	 	{
       	 		Vector v = new XmlParse(line.toString()).parseMeth(0,GoodsPopDef.ref);

       	 		if (v.size() > 0)
       	 		{
       	 			for (int i = 0; i < v.size(); i++)
       	 			{
       	 				String[] row = (String[]) v.elementAt(i);
         			
       	 				GoodsPopDef gpd = new GoodsPopDef();
         			
       	 				if (Transition.ConvertToObject(gpd, row))
       	 				{
       	 					giftGoods.add(gpd);
       	 				}
       	 				else
       	 				{
       	 					return false;
       	 				}
       	 			}
       	 		}
       	 		else
       	 		{
       	 			return false;
       	 		}
         	
       	 		return true;
       	 	}
       	 	else
       	 	{
       	 		return false;
       	 	}
       	 
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		return false;
    	}
    }
        
	// 查找规则促销
    public boolean findPopRuleNew(GoodsPopDef popDef,String code,String gz,String uid,String rulecode,String catid,String ppcode,String time)
    {
    	 if (!GlobalInfo.isOnline)
         {
             return false;
         }
    	 
    	 CmdHead head = null;
         StringBuffer line = new StringBuffer();
         int result = -1;
         String[] values = {code,gz,uid,rulecode,catid,ppcode,time};
         String[] args = { "code","gz","uid","rule","catid","ppcode","rqsj"};
         
         try
         {
        	 head = new CmdHead(CmdDef.FINDRULEPOPNEW);
        	 line.append(head.headToString() + Transition.SimpleXML(values, args));

             result = HttpCall(line, "");
             
             if (result == 0)
             {
            	 Vector v = new XmlParse(line.toString()).parseMeth(0,GoodsPopDef.ref);

            	 if (v.size() > 0)
            	 {
            		 String[] row = (String[]) v.elementAt(0);

            		 if (Transition.ConvertToObject(popDef, row))
            		 {
            			 return true;
            		 }
            	 }
             }
         }
         catch (Exception ex)
         {
        	 ex.printStackTrace();
         }
         
         return false;
    }
    
	// 查找规则促销
    public boolean findGiftRuleNew(GoodsPopDef popDef,String code,String gz,String uid,String rulecode,String catid,String ppcode,String time,String cardno,String cardtype)
    {
    	 if (!GlobalInfo.isOnline)
         {
             return false;
         }
    	 
    	 CmdHead head = null;
         StringBuffer line = new StringBuffer();
         int result = -1;
         String[] values = {code,gz,uid,rulecode,catid,ppcode,time,cardno,cardtype};
         String[] args = { "code","gz","uid","rule","catid","ppcode","rqsj","cardno","cardtype"};
         
         try
         {
        	 head = new CmdHead(CmdDef.FINDGIFTPOPNEW);
        	 line.append(head.headToString() + Transition.SimpleXML(values, args));

             result = HttpCall(line, "");
             
             if (result == 0)
             {
            	 Vector v = new XmlParse(line.toString()).parseMeth(0,GoodsPopDef.ref);

            	 if (v.size() > 0)
            	 {
            		 String[] row = (String[]) v.elementAt(0);

            		 if (Transition.ConvertToObject(popDef, row))
            		 {
            			 return true;
            		 }
            	 }
             }
         }
         catch (Exception ex)
         {
        	 ex.printStackTrace();
         }
         
         return false;
    }
    
    // 查找VIP限量折扣定义
    public boolean findLimitVIPZK(GoodsAmountDef limitzk, String vipno,String code,String gz,String uid)
    {
        if (!GlobalInfo.isOnline)
        {
            return false;
        }

        String[] values = { vipno,code, gz, uid };
        String[] arg = { "vipno","code", "gz", "spec"};
        CmdHead aa = null;
        StringBuffer line = new StringBuffer();
        int result = -1;

        try
        {
            aa = new CmdHead(CmdDef.FINDLIMITVIPZK);
            line.append(aa.headToString() + Transition.SimpleXML(values, arg));

            result = HttpCall(line, "");

            if (result == 0)
            {
                Vector v = new XmlParse(line.toString()).parseMeth(0,
                                                                   GoodsAmountDef.ref);

                if (v.size() > 0)
                {
                    values = (String[]) v.elementAt(0);

                    if (Transition.ConvertToObject(limitzk, values))
                    {
                        return true;
                    }
                }
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }

        return false;
    }
        
}
