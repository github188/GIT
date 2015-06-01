package custom.localize.Ywjb;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerTypeDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;

import custom.localize.Bcrm.Bcrm_NetService;

public class Ywjb_NetService extends Bcrm_NetService {
	public Http hdqrs = null;
	public Http getMemCardHttp(int cmdcode)
	{
		//发送请求到独立卡服务器
		if (hdqrs == null || (hdqrs != null && !hdqrs.isSameHttp(GlobalInfo.sysPara.hdqrsCRM)))
		{
			hdqrs = new Http(GlobalInfo.sysPara.hdqrsCRM);
			hdqrs.init();
			hdqrs.setConncetTimeout(ConfigClass.ConnectTimeout); //连接超时
			hdqrs.setReadTimeout(ConfigClass.ReceiveTimeout); //处理超时        		
		}
		
		if (cmdcode == CmdDef.FINDCUSTOMER || cmdcode == CmdDef.SENDHYK || cmdcode == CmdDef.GETGOODSEXCHANGE|| cmdcode == CmdDef.SENDCRMSELL|| cmdcode == CmdDef.GETCUSTSELLJF|| cmdcode == CmdDef.GETCRMVIPZK ||cmdcode == CmdDef.FINDFJKINFO || cmdcode == CmdDef.GETACCEPTFJKRULE || cmdcode == CmdDef.SENDFJK || cmdcode ==(CmdDef.FINDCRMPOP+200))
			return hdqrs;
		
		return super.getMemCardHttp(cmdcode);
	}
	
	public boolean findKeyValue(double[] info,String code,String gz,String keyvalue)
	{
        if (!GlobalInfo.isOnline)
        {
            return false;
        }
        
        String[] values = {code,gz,keyvalue};
        String[] args   = {"code","gz","keyvalue"};
        
        CmdHead head = null;
        StringBuffer line = new StringBuffer();
        int result = -1;
        
        try
        {
            head = new CmdHead(CmdDef.GETKEYVALUE);
            line.append(head.headToString() +
                        Transition.SimpleXML(values, args));

            //不显示错误信息
            result = HttpCall(line, "查询不到KEY值");

            if (result == 0)
            {
                Vector v = new XmlParse(line.toString()).parseMeth(0,
                		new String[]{"bottom","top"});

                if (v.size() > 0)
                {
                    String[] row = (String[]) v.elementAt(0);
                    
                    if (row.length > 1)
                    {
                    	info[0] = Convert.toDouble(row[0]);
                    	info[1] = Convert.toDouble(row[1]);
                    	
                    	return true;
                    }
                    
                    return false;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
		return false;
	}
	
//	 得到会员卡类型
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

            result = HttpCall(GlobalInfo.localHttp,line, "获取顾客卡类型失败!");

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
    
    public boolean sendFjkSale(Http h, MzkRequestDef req, MzkResultDef ret)
    {
    	if (req.paycode.equals("0520"))
    		h = getMemCardHttp(CmdDef.SENDFJK);
    	else
    		h = getMemCardHttp(11);
    	return super.sendFjkSale(h, req,ret);
    }
}
