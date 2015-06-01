package custom.localize.Yzlj;

import java.util.Vector;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;

import custom.localize.Bstd.Bstd_NetService;

public class Yzlj_NetService extends Bstd_NetService
{
	public static final int SENDMZKCX = 830; 			//上传面值卡撤销日志
	
	public boolean sendMzkSale(Http h, MzkRequestDef req, MzkResultDef ret)
	{
		return sendMzkSale(h, req, ret, CmdDef.SENDMZK);
	}
	
	public boolean sendMzkSale(Http h, MzkRequestDef req, MzkResultDef ret, int cmdCode)
	{
		return super.sendMzkSale(h, req, ret, sendGgkAddress(req,cmdCode));
	}
	
	
	//发送面值卡消费撤销日志
	public boolean sendMzkSaleCx(MzkRequestDef req, MzkResultDef ret)
	{
		return sendMzkSaleCx(getCardHttp(),req,ret);
	}
	
	public boolean sendMzkSaleCx(Http h, MzkRequestDef req, MzkResultDef ret)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			head = new CmdHead(SENDMZKCX);

			
			line.append(head.headToString() + Transition.ConvertToXML(req, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } }));
			
			
			result = HttpCall(h, line, "上传储值卡撤销失败!");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, MzkResultDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(ret, row)) { return true; }
				}
		}
	}
	catch (Exception er)
		{
			er.printStackTrace();
		}
		
	return false;
	}
	
	public boolean getMzkInfo(Http h, MzkRequestDef req, MzkResultDef ret, int cmdCode)
	{

		return super.getMzkInfo(h, req, ret, sendGgkAddress(req,cmdCode));
	}
	
	
	//访问刮刮卡的posserver地址
	protected int sendGgkAddress(MzkRequestDef req,int cmdCode)
	{
//		是否为刮刮卡查询
		if(req.str2 != null && req.str2.equals("8") && (Yzlj_CustomGlobalInfo.getDefault().sysPara.ggkUrl != null && Yzlj_CustomGlobalInfo.getDefault().sysPara.ggkUrl.length() > 0))
		{
				cmdCode = 143;
				String ggkUrl = Yzlj_CustomGlobalInfo.getDefault().sysPara.ggkUrl;
				String[] s = new String[] { ggkUrl, "," + cmdCode + "," };
				for(int i = 0;i<GlobalInfo.otherHttp.size();i++)
				{
					String[] ss = (String[]) GlobalInfo.otherHttp.get(i);
					if(ss[1].equals(","+cmdCode+","))continue;
					GlobalInfo.otherHttp.add(s);
				}
				
				if(GlobalInfo.otherHttp.size() <= 0)GlobalInfo.otherHttp.add(s);
		}
		
		return cmdCode;
		
	}

}
