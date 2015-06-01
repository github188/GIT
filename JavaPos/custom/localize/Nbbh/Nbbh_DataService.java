package custom.localize.Nbbh;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.CustFilterDef;

import custom.localize.Cmls.Cmls_DataService;
import custom.localize.Nbbh.Nbbh_NetService;
public class Nbbh_DataService extends Cmls_DataService {
	public boolean getNetMemoInfo()
	{
		if (GlobalInfo.isOnline)
		{
			Nbbh_NetService netService = (Nbbh_NetService)NetService.getDefault();
			//从POSSERVER获取打印小票信息,并保存到local.db3
			netService.getTktrule_gs();
		}

		return true;
	}
	
	public boolean getNewBankZsInfo(String bankCardNo, String bankNo, String[] zkInfo)
	{
		if(1==2)
		{//test
			zkInfo[0] = "0.9";
			zkInfo[1] = "20";
			zkInfo[2] = "DH0001";
			return true;
		}		
		
		if (GlobalInfo.isOnline)
		{
			Nbbh_NetService netService = (Nbbh_NetService)NetService.getDefault();
			return netService.getNewBankZsInfo(bankCardNo, bankNo, zkInfo);
		}
		else
		{
			new MessageBox(Language.apply("脱网下不支持此功能"));
			return false;
		}
	}
	
	public boolean getCreditCardList(Vector v, String mktcode)
	{
		if(1==2)
		{//test data
			CustFilterDef filter = new CustFilterDef();
			filter.desc = "建设银行";
			filter.TrackFlag ="01";
			v.add(filter);
			filter = new CustFilterDef();
			filter.desc = "工商银行";
			filter.TrackFlag ="02";
			v.add(filter);
			return true;
		}
		return super.getCreditCardList(v, mktcode);
	}
}
