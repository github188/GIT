package custom.localize.Gbyw;

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

import custom.localize.Gbyw.Gbyw_MzkModule.RetInfoDef;

public class Gbyw_ScoreEvent
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
	public Gbyw_PaymentBankMzk payObj = null;

	public Gbyw_ScoreEvent(Gbyw_ScoreForm form, Gbyw_PaymentBankMzk pay, SaleBS sale)
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

		NewKeyListener key = new NewKeyListener();

		key.event = event;
		txtAcount.addKeyListener(key);

		NewKeyListener key1 = new NewKeyListener();
		key1.setEditableResponseEvent(true);
		key1.event = event;
		key1.inputMode = key1.DoubleInput;
		txtMoney.addKeyListener(key1);

		shell.setLocation((GlobalVar.rec.x - shell.getSize().x) / 2, (GlobalVar.rec.y - shell.getSize().y) / 2);

		initEvent();

	}

	public void initEvent()
	{
		payName.setText("[" + payObj.paymode.code + "]" + payObj.paymode.name);

		if (SellType.ISBACK(saleBS.saletype))
			labelMoney.setText("退款金额");
		else
			labelMoney.setText("可付金额");

		txtAcount.setText("请按回车进行刷卡.....");
		txtAcount.selectAll();
		txtAcount.setFocus();

		txtMoney.setText(saleBS.calcPayBalance() + "");

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
			if (e.widget == txtAcount)
			{
				RetInfoDef retinfo = Gbyw_MzkModule.getDefault().cardQuery(true);

				if (retinfo == null)
				{
					e.data = "focus";
					initEvent();

					break;
				}

				txtAcount.setText(retinfo.tradeCardno);
				
				txtScore.setText(retinfo.scoreYe + "");
				txtScore.setEditable(false);

				StringBuffer sb = new StringBuffer();
				sb.append("储值余额:" + retinfo.ye).append("\n");
				sb.append("积分余额:" + retinfo.scoreYe).append("\n");
				sb.append("红包余额:" + retinfo.elecbagYe).append("\n");
				sb.append("积返余额:" + retinfo.scoreRebateYe).append("\n");

				statusInfo.setText(sb.toString());

				txtMoney.setFocus();
				txtMoney.selectAll();
			}
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
