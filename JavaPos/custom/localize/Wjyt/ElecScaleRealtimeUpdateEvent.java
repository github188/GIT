package custom.localize.Wjyt;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.KeyAdapter;

import com.efuture.javaPos.Device.ElectronicScale;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import java.util.Timer;
import java.util.Vector;

public class ElecScaleRealtimeUpdateEvent
{
	public Shell sShell;
	private Button defaultbutton;
	Timer timerTask = new Timer();
	public double price;
	public Label goodsname;
	public Label goodsweight;
	public Label goodsprice;
	public Label tip;
	public Vector result;

	public ElecScaleRealtimeUpdateEvent(ElecScaleRealtimeUpdateForm form)
	{
		sShell = form.shell;
		defaultbutton = form.button;
		goodsname = form.lbgoodsname;
		goodsweight = form.lbgoodsweight;
		goodsprice = form.lbgoodsprice;
		tip = form.lbtip;
		price = form.price;
		result = form.data;

		defaultbutton.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent keyevent)
			{
				int key = NewKeyListener.searchKey(keyevent.keyCode);

				if (key == GlobalVar.Exit || key == GlobalVar.Validation)
					exitPolling();
			}
		});

		sShell.setLocation((GlobalVar.rec.x - sShell.getSize().x) / 2, (GlobalVar.rec.y - sShell.getSize().y) / 2);

		// 发送称指令
		ElectronicScale.getDefault().startPolling();

		// 启动任务接收数据
		timerTask.schedule(new ElecScalePolling(this), 100, 50);
	}

	private void exitPolling()
	{
		try
		{
			ElectronicScale.getDefault().stopPolling();
			if (timerTask != null)
			{
				timerTask.cancel();
				timerTask = null;
			}

			sShell.close();
			sShell.dispose();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			timerTask = null;
		}
	}
}
