package custom.localize.Cjmx;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.SaleBS;

import custom.localize.Cmls.Cmls_CustomLocalize;
//重庆金美西百货连锁
public class Cjmx_CustomLocalize extends Cmls_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "17083 build 2014.05.15";
    }
    
    public MenuFuncBS createMenuFuncBS()
    {
		return new custom.localize.Cjmx.Cjmx_MenuFuncBS();
    	
    }
    
    public NetService createNetService(){
    	return new custom.localize.Cjmx.Cjmx_NetService();
    }

    public SaleBS createSaleBS(){
    	return new custom.localize.Cjmx.Cjmx_SaleBS();
    }
    
    public HykInfoQueryBS createHykInfoQueryBS(){
    	return new custom.localize.Cjmx.Cjmx_HykInfoQueryBS();
    }

}
