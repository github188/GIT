/**
 * 
 */
package custom.localize.Cstd;


import com.efuture.javaPos.Logic.SaleBS;

/**
 * 
 *
 */
public class Cstd_SaleBS extends SaleBS {
	public void calcBatchRebate(int index)
	{
		return ;
	}
	/*public void enterInput()//enterInputCODE()
	{		
		try
		{
			if (saleGoods.size()>0)
			{
				return;
			}
			else
			{
				super.enterInput();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (saleGoods.size()>0)
			{
				
				//NewKeyListener.sendKey(GlobalVar.Enter);
				if (saleEvent.saleform.getFocus().equals(saleEvent.code))
				{
					new MessageBox("test");
				}
				NewKeyListener.sendKey(GlobalVar.Pay);
				//
			}
			
		}
	}
	public boolean findGoods(String code, String yyyh, String gz)
	{
		return super.findGoods(code, yyyh, gz);
		
		boolean blnRet = false;
		
		try
		{
			if (saleGoods.size()>0)
				blnRet = true;
			else				
				blnRet = super.findGoods(code, yyyh, gz);
			
			NewKeyListener.sendKey(GlobalVar.Pay);
			NewKeyListener.sendKey(GlobalVar.Enter);
		}
		finally
		{
			return blnRet;
		}
	}*/

}
