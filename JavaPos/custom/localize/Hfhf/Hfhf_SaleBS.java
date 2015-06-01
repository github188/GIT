package custom.localize.Hfhf;


public class Hfhf_SaleBS extends Hfhf_SaleBS0Customer
{
	public void initNewSale()
	{
		Hfhf_CrmModule.getDefault().init(false);
		super.initNewSale();
	}

	public boolean checkFinalStatus()
	{
		if (curCustomer != null && curCustomer.valstr3.equals("szd"))
			saleHead.hykh = "szd" + saleHead.hykh;

		return true;
	}
}
