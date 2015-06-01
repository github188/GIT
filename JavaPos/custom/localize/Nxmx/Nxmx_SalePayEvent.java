package custom.localize.Nxmx;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.FontData;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.SaleBS;
import custom.localize.Nxmx.Nxmx_SalePayForm;

import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.UI.SalePayEvent;
import com.swtdesigner.SWTResourceManager;

public class Nxmx_SalePayEvent extends SalePayEvent
{
	public Nxmx_SalePayEvent(SaleBS saleBS, Nxmx_SalePayForm pay)
	{
		super(saleBS, null);

		saleBS.saleEvent.pay = pay.composite_pay;
		saleBS.saleEvent.saleform.composite_pay = pay.composite_pay;
		saleBS.saleEvent.category.setVisible(false);
		saleBS.saleEvent.saleform.code.setEnabled(false);
		
		this.table = pay.table_paymode;
		this.table1 = pay.table_paylist;
		this.txt = pay.text_money;
		this.lbl_ysje = pay.lbl_ysje;
		this.saleBS = saleBS;
		this.saleBS.setSalePayEvent(this);
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

	public void RowSelected(int index)
	{
		// 得到当前付款代码
		String[] ax = table.changeItemVar(index);
		String paycode = ax[0];

		// 得到付款方式,显示付款方式缺省付款金额
		PayModeDef paymode = DataService.getDefault().searchPayMode(paycode);
		saleBS.setMoneyInputDefault(txt, paymode);

		// 字体大小
		int height = 0;
		FontData[] fd = lbl_money.getFont().getFontData();
		if (fd.length > 0)
			height = fd[0].getHeight();

		// 主付款显示付款名
		if (paymode.sjcode.equals("0") || paymode.sjcode.equals(paymode.code) || paymode.isbank == 'N')
		{
			int newheight = height;
			if (ax[1].length() <= 3)
				newheight = 25;
			else if (ax[1].length() <= 4)
				newheight = 19;
			else
				newheight = 15;

			if (newheight != height)
				lbl_money.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NONE));
			lbl_money.setText(ax[1]);
		}
		else
		{
			if (height != 25)
				lbl_money.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NONE));
			lbl_money.setText("付款码");
		}

		txt.setFocus();
	}

	public void close()
	{
		ShellIsDisposed = true;

		saleBS.saleEvent.pay.dispose();
		saleBS.saleEvent.category.setVisible(true);
		saleBS.saleEvent.saleform.code.setEnabled(true);
	}
}
