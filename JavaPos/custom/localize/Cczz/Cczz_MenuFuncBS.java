package custom.localize.Cczz;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.UI.MenuFuncEvent;
import com.efuture.javaPos.UI.Design.SaleTicketListForm;

public class Cczz_MenuFuncBS extends MenuFuncBS {
	public boolean execExtendFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (ConfigClass.Bankfunc.equals("com.efuture.javaPos.Payment.Bank.Cczz_PaymentBankFunc")&& (Integer.parseInt(mfd.code) == StatusType.MN_XYKQT1))
		{
			PaymentBankFunc pbfunc = CreatePayment.getDefault().getPaymentBankFunc();
			pbfunc.WriteRequestLog(PaymentBank.XKQT1, 0.00, "", "", null);
			pbfunc.XYKExecute(PaymentBank.XKQT1, 0.00, "", "", "", "","", "", null);
			return true;
		}
		return false;
	}
	
	//打开当日小票列表
	public void openXsList(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		char dy = GlobalInfo.posLogin.privdy;
		try{
			if (GlobalInfo.posLogin.privdy != 'Y')
			{
				OperUserDef staff = DataService.getDefault().personGrant();
				if (staff.privdy == 'Y')
				{
					GlobalInfo.posLogin.privdy = 'Y';
				}
				else
				{
					new MessageBox("授权人员也无打印查看权限");
					return ;
				}
			}
			new SaleTicketListForm();
		}
		catch(Exception er)
		{
			er.printStackTrace();
		}
		finally
		{
			GlobalInfo.posLogin.privdy = dy;
		}
	}
}
