package custom.localize.Bjys;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerTypeDef;

public class Bjys_NetService extends NetService
{
	//获得POS积分规则
    /*public boolean getPosJfRule()
    {
    	if (!GlobalInfo.isOnline)
        {
            return false;
        }
    	
    	CmdHead aa = null;
        StringBuffer line = new StringBuffer();
        int result = -1;
        
        try
        {
        	 aa = new CmdHead(CmdDef.GETPOSJFRULE);
             line.append(aa.headToString() + Transition.buildEmptyXML());
             
             result = HttpCall(line, "获取POS积分规则失败!");
             
             if (result == 0)
             {
            	 Vector v = new XmlParse(line.toString()).parseMeth(0,MemoInfoDef.ref);
            	 
            	 if (!AccessLocalDB.getDefault().writePosJfRule(v))
                 {
                     new MessageBox("保存POS积分规则失败!");
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
    }*/
    
//  得到会员卡类型
    public boolean getCustomerType()
    {
        if (!GlobalInfo.isOnline)
        {
            return false;
        }

        CmdHead aa = null;
        StringBuffer line = new StringBuffer();
        int result = -1;

        try
        {
            aa = new CmdHead(CmdDef.GETCUSTOMERTYPE);
            line.append(aa.headToString() + Transition.buildEmptyXML());

            result = HttpCall(line, "获取顾客卡类型失败!");

            if (result == 0)
            {
                Vector v = new XmlParse(line.toString()).parseMeth(0,
                                                                   CustomerTypeDef.ref);

                // 写入本地数据库
                if (!AccessLocalDB.getDefault().writeCustomerType(v))
                {
                    new MessageBox("保存顾客卡类型失败!");
                }

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
}
