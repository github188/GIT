package custom.localize.Nnmk;


import com.efuture.javaPos.Logic.FjkInfoQueryBS;
import com.efuture.javaPos.UI.Design.CouponQueryInfoForm;

public class Nnmk_FjkInfoQueryBS extends FjkInfoQueryBS
{
	public boolean PaymentCouponQuery(String[] pay,String[] track)
	{
		if(Integer.parseInt(Nnmk_MenuFuncBS.code)==302){
			CouponQueryInfoForm window = new CouponQueryInfoForm();
			window.open();
			return true;
		}else if(Integer.parseInt(Nnmk_MenuFuncBS.code)==310){
			Nnmk_MzkInfoQueryBS fjk = new Nnmk_MzkInfoQueryBS();
			fjk.QueryFjkInfo();
			
			return true;
		}
		return false;
		
	}
	
}
