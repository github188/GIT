package custom.localize.Wjyt;

import java.util.TimerTask;
import org.eclipse.swt.widgets.Display;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.ElectronicScale;
import com.efuture.javaPos.Global.GlobalInfo;

public class ElecScalePolling extends TimerTask
{
	ElecScaleRealtimeUpdateEvent event;
	double weight = 0.0;
	double allprice = 0.0;
	boolean isKeepCycle = true;
	
	public ElecScalePolling(ElecScaleRealtimeUpdateEvent evt)
	{
		event = evt;
	}

	public void run()
	{
		try
		{
			if (!isKeepCycle)
			{
				event.result.removeAllElements();

				Display.getDefault().syncExec(new Runnable()
				{
					public void run()
					{
						if (event.sShell.isDisposed())
							return;
						event.tip.setText("轮询失败,请按[退出键]重新操作");
					}
				});
				cancel();
				return;
			}
			
			if (ElectronicScale.getDefault().getData())
			{
				weight = ElectronicScale.getDefault().getWeight();
				allprice =getDetailOverFlow(weight * event.price,GlobalInfo.syjDef.sswrfs);
				event.result.set(0,new Double(weight));
				event.result.set(1,new Double(allprice));

				Display.getDefault().syncExec(new Runnable()
				{
					public void run()
					{
						if (event.sShell.isDisposed())
							return;
						event.goodsweight.setText(String.valueOf(weight));
					}
				});
				
				Display.getDefault().syncExec(new Runnable()
				{
					public void run()
					{
						if (event.sShell.isDisposed())
							return;
						event.goodsprice.setText(String.valueOf(allprice));
					}
				});
			}
			else
			{
				//isKeepCycle = false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			isKeepCycle = false;
			event.result.removeAllElements();
		}
	}
	
	public double getDetailOverFlow(double allMoney,char type1)
    {
        double result = allMoney;

        try
        {
            char type = type1;

            // 收银截断方式，0-精确到分、1-四舍五入到角、2-截断到角、3-四舍五入到元、4-截断到元、5-进位到角、6-进位到元
            switch (type)
            {
                case '0':
                    result = ManipulatePrecision.doubleConvert(allMoney, 2, 1);

                    break;

                case '1':
                    result = ManipulatePrecision.doubleConvert(allMoney, 1, 1);

                    break;

                case '2':
                    result = ManipulatePrecision.doubleConvert(allMoney, 1, 0);

                    break;

                case '3':
                    result = ManipulatePrecision.doubleConvert(allMoney, 0, 1);

                    break;

                case '4':
                    result = ManipulatePrecision.doubleConvert(allMoney, 0, 0);

                    break;
                case '5':
                    result = ManipulatePrecision.doubleConvert(allMoney+0.09, 1, 0);

                    break;
                case '6':
                    result = ManipulatePrecision.doubleConvert(allMoney+0.9, 0, 0);

                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }
}
