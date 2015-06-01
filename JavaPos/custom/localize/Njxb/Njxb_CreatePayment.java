package custom.localize.Njxb;

import custom.localize.Bcrm.Bcrm_CreatePayment;

public class Njxb_CreatePayment extends Bcrm_CreatePayment
{
	/*
	 public boolean sendAllPaymentCz()
	 {
	 // 按付款方式发送各自的冲正数据
	 boolean ok = true;
	 boolean hasGecrmRush = false;
	 for (int i = 0; GlobalInfo.payMode != null && i < GlobalInfo.payMode.size(); i++)
	 {
	 PayModeDef pmd = (PayModeDef) GlobalInfo.payMode.elementAt(i);
	 
	 // 金鹰卡冲正
	 if (pmd.code.equals("0401") || pmd.code.equals("0402"))
	 {
	 hasGecrmRush = true;
	 continue;
	 }
	 ok = createPaymentAll(true, pmd, null, null, null).sendAccountCz();
	 }
	 
	 if (hasGecrmRush)
	 {
	 if (!new GecrmFunc().sendRush())
	 {
	 return false;
	 }
	 }
	 return ok;
	 }
	 */
}
