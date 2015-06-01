package custom.localize.Jwyt;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;

public class Jwyt_MarsPayEvent
{
	public Shell shell = null;
	public Text txtAcount = null;
	public Text txtScore = null;
	public Text txtMoney = null;

	public Label labelYe = null;
	public Label labelMoney = null;
	public Label payName = null;

	public StyledText statusInfo = null;

	public SaleBS saleBS = null;
	public Jwyt_PaymentMars payObj = null;

	public Jwyt_MarsPayEvent(Jwyt_MarsPayForm form, Jwyt_PaymentMars pay, SaleBS sale)
	{
		this.saleBS = sale;
		this.payObj = pay;

		txtAcount = form.txtAcount;
		txtScore = form.txtScore;
		txtMoney = form.txtMoney;

		labelMoney = form.lableMoney;
		labelYe = form.labelYe;

		statusInfo = form.status;
		shell = form.sShell;
		payName = form.payName;

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

		NewKeyListener key1 = new NewKeyListener();
		key1.setEditableResponseEvent(true);
		key1.event = event;
		key1.inputMode = key1.DoubleInput;
		txtMoney.addKeyListener(key1);
		txtAcount.addKeyListener(key1);

		shell.setLocation((GlobalVar.rec.x - shell.getSize().x) / 2, (GlobalVar.rec.y - shell.getSize().y) / 2);

		initEvent();

	}

	private void initData()
	{
		if (Jwyt_MarsModule.getDefault().getMarSaleRet() == null)
		{
			txtAcount.setFocus();
			txtAcount.selectAll();
			return;
		}

		// 此处显示的是辅助码
		txtAcount.setText(Jwyt_MarsModule.getDefault().getMarSaleRet().assistcode);
		txtAcount.setEnabled(false);
		txtScore.setText(Jwyt_MarsModule.getDefault().getMarSaleRet().balancemoney);

		double min = Math.min(Convert.toDouble(Jwyt_MarsModule.getDefault().getMarSaleRet().balancemoney), saleBS.calcPayBalance());
		txtMoney.setText(String.valueOf(min));
		txtMoney.setFocus();
		txtMoney.selectAll();
	}

	public void initEvent()
	{
		payName.setText("[" + payObj.paymode.code + "]" + payObj.paymode.name);

		txtAcount.setText("请按[扫码/辅助码输入]快捷键");

		txtAcount.setFocus();
		txtAcount.selectAll();

		if (SellType.ISBACK(saleBS.saletype))
			labelMoney.setText("退款金额");
		else
			labelMoney.setText("可付金额");

		statusInfo.setText("");
	}

	public void keyPressed(KeyEvent e, int key)
	{

	}

	public void keyReleased(KeyEvent e, int key)
	{
		switch (key)
		{
			case GlobalVar.Enter:
				if (e.widget == txtMoney)
				{
					if (payObj.existPayment(payObj.paymode.code, txtAcount.getText()) >= 0)
					{
						new MessageBox("不允许同张短信码多次付款\n当前已存在" + txtAcount.getText() + "付款");
						break;
					}

					if (!SellType.ISBACK(saleBS.saletype) && Convert.toDouble(txtMoney.getText()) > Convert.toDouble(Jwyt_MarsModule.getDefault().getMarSaleRet().balancemoney))
					{
						new MessageBox("输入金额大于券余额!");
						break;
					}

					if (!payObj.createSalePay(txtAcount.getText(), txtMoney.getText()))
					{
						e.data = "focus";
						txtMoney.setFocus();
						txtMoney.selectAll();
					}
					else
					{
						// ((Jwyt_NetService)
						// NetService.getDefault()).sendEWMWorkLog(Jwyt_MarsModule.getDefault().getMarSaleRet());
						Jwyt_MarsModule.getDefault().clear();

						shell.close();
						shell.dispose();
						shell = null;
					}
				}
				break;
			case GlobalVar.Exit:
				shell.close();
				shell.dispose();
				Jwyt_MarsModule.getDefault().clear();
				break;

			case GlobalVar.CustomKey3:
				saleBS.execCustomKey3(false);
				initData();

				break;

			case GlobalVar.CustomKey4:
				saleBS.execCustomKey4(false);
				initData();

		}
	}
}
