package custom.localize.Nxmx;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;
public class Nxmx_SaleBS extends Nxmx_SaleBSPay
{
	public boolean deleteGoods(int index)
	{
		if(super.deleteGoods(index))
		{
			doShowInfoFinish();
			return true;
		}
		return false;
	}
	
	public boolean doShowInfoFinish()
	{
		try
		{
			saleEvent.poptable.clearRow();
			
			int index = saleEvent.table.getSelectionIndex();
			
			if (index <0)
				return false;
			
			if (popinfo !=null && popinfo.size()!=0)
			{
				Vector item =(Vector) popinfo.get(index);
				if (item!=null && item.size() !=0)
				{
					for (int i=0; i<item.size(); i++)
					{
						TableItem  popitem = new TableItem(saleEvent.poptable,SWT.BORDER);
						popitem.setText((String[])item.get(i));
						saleEvent.poptable.setSelection(i);
					}
				}
			}
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
}
