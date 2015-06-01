package custom.localize.Doug;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bhls.Bhls_DataService;
import custom.localize.Sfks.Sfks_AccessBaseDB;
import custom.localize.Sfks.Sfks_NetService;

public class Doug_DataService extends Bhls_DataService
{

	public boolean findFwqRange(String code,String gz,String uid)
	{
    	if (GlobalInfo.isOnline)
    	{
    		Sfks_NetService netservice = (Sfks_NetService)NetService.getDefault();
    		return netservice.findFwqRange(code,gz,uid);	
    	}
    	else
    	{
    		Sfks_AccessBaseDB accessbasedb = (Sfks_AccessBaseDB)AccessBaseDB.getDefault();
    		return accessbasedb.findFwqRange(code,gz,uid);
    	}		
	}

}
