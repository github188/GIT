package custom.localize.Zsbh;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;

public class Zsbh_MenuFuncBS extends MenuFuncBS {

	public void openBankFunc(MenuFuncDef mfd, MenuFuncEvent mffe, int type) {
		if (type >= StatusType.MN_XYKQT1) {
			CreatePayment.getDefault().getPaymentBankForm()
					.open(type - StatusType.MN_XYKXF - 1);
		} else if (Integer.parseInt(mfd.code) == StatusType.MN_XYKTH) {
			if (("," + GlobalInfo.sysPara.salesReturncodeList + ",")
					.indexOf("," + GlobalInfo.posLogin.role + ",") >= 0) {
				  CreatePayment.getDefault().getPaymentBankForm().open(type - StatusType.MN_XYKXF);
			}
		   else{
			 new MessageBox("该收银员没有退货权限！！！");
			}
			
		} else {
			CreatePayment.getDefault().getPaymentBankForm()
					.open(type - StatusType.MN_XYKXF);
		}
	}

}