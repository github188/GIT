package custom.localize.Nxmx;

import org.eclipse.swt.events.KeyEvent;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.UI.SaleRefundEvent;

public class Nxmx_SaleRefundEvent extends SaleRefundEvent
{
	public Nxmx_SaleRefundEvent(SaleBS saleBS, Nxmx_SalePayForm pay)
	{
		super(saleBS, null);

		saleBS.saleEvent.pay = pay.composite_pay;
		saleBS.saleEvent.category.setVisible(false);

		this.table = pay.table_paymode;
		this.table1 = pay.table_paylist;
		this.txt = pay.text_money;
		this.lbl_ysje = pay.lbl_ysje;
		this.saleBS = saleBS;
		// this.saleBS.setSalePayEvent(this);
		this.payReqFee = pay.payReqFee;
		this.unpayfee = pay.unpayfee;
		this.lbl_money = pay.lbl_money;

		mouseModeInit();

		// 设定键盘事件
		NewKeyEvent event = new NewKeyEvent()
		{
			public void keyDown(KeyEvent e, int key)
			{
				keyPressed(e, key);
			}

			public void keyUp(KeyEvent e, int key)
			{
				keyReleased(e, key);
			}
		};

		NewKeyListener key = new NewKeyListener();
		key.event = event;
		key.inputMode = key.DoubleInput;
		key.isControl = true;

		txt.addKeyListener(key);
		table.addKeyListener(key);
		table1.addKeyListener(key);

		txt.setFocus();

		// 初始化
		initPayment();
	}

	public void close()
	{
		ShellIsDisposed = true;

		saleBS.saleEvent.pay.dispose();
		saleBS.saleEvent.category.setVisible(true);
	}
}
