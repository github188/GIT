package custom.localize.Hfhf;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;

public class Hfhf_ScoreEvent
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
	public Hfhf_PaymentScore payObj = null;

	public Hfhf_ScoreEvent(Hfhf_ScoreForm form, Hfhf_PaymentScore pay, SaleBS sale)
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

			public void msrFinish(KeyEvent e, String track1, String track2, String track3)
			{
				msrRead(e, track1, track2, track3);
			}
		};

		NewKeyListener key = new NewKeyListener();

		key.event = event;
		key.inputMode = payObj.getMsrInputMode();
		txtAcount.addKeyListener(key);
		txtAcount.setData("MSRINPUT");

		NewKeyListener key1 = new NewKeyListener();
		key1.setEditableResponseEvent(true);
		key1.event = event;
		key1.inputMode = key1.DoubleInput;
		txtMoney.addKeyListener(key1);

		shell.setLocation((GlobalVar.rec.x - shell.getSize().x) / 2, (GlobalVar.rec.y - shell.getSize().y) / 2);

		initEvent();

	}

	public void msrRead(KeyEvent e, String track1, String track2, String track3)
	{
		String cardno = Hfhf_CrmModule.getDefault().getCardNo(track2);

		txtAcount.setFocus();
		txtAcount.selectAll();

		txtAcount.setText(cardno);
	}

	public void initEvent()
	{
		payName.setText("[" + payObj.paymode.code + "]" + payObj.paymode.name);

		if (saleBS.curCustomer != null)
			txtAcount.setText(saleBS.curCustomer.code);

		labelYe.setText(payObj.getYeLable());
		txtScore.setEditable(false);

		if (SellType.ISBACK(saleBS.saletype))
			labelMoney.setText("退款金额");
		else
			labelMoney.setText("可付金额");

		statusInfo.setText("");

		if (payObj.getCrmCardData(txtAcount.getText()))
		{
			txtScore.setText(String.valueOf(payObj.getYeInfo()));
			txtMoney.setText(String.valueOf(payObj.getValidMoney()));
			statusInfo.setText(payObj.getStatusInfo());

			txtMoney.setFocus();
			txtMoney.selectAll();
		}
		else
		{
			shell.close();
			shell.dispose();
		}
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
				if (!payObj.createSalePay(txtAcount.getText(), txtMoney.getText()))
				{
					e.data = "focus";
					txtMoney.setFocus();
					txtMoney.selectAll();
				}
				else
				{
					shell.close();
					shell.dispose();
					shell = null;
				}
			}
			break;
		case GlobalVar.Exit:
			shell.close();
			shell.dispose();
			shell = null;
			break;
		}
	}

}
