package custom.localize.Gbyw;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;

public class Gbyw_MzkEvent
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
	public Gbyw_PaymentMzk payObj = null;
	public boolean ShellIsDisposed = false;

	public Gbyw_MzkEvent(Gbyw_MzkForm form, Gbyw_PaymentMzk pay, SaleBS sale)
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
		StringBuffer passwd = new StringBuffer();
		TextBox txt = new TextBox();

		if (track2 == null || track2.equals(""))// || track2.trim().length() <
												// 16)
		{
			new MessageBox("卡号非法");

			txtAcount.setText("请重新刷卡");
			txtAcount.selectAll();
			txtAcount.setFocus();
			return;
		}

		if (!txt.open("请输入密码", "PASSWORD", "请输入面值卡后6位数字", passwd, 0, 0, false, TextBox.AllInput))
		{
			shell.close();
			shell.dispose();
			return;
		}

		// 要发送的数据=“04，pos机号，商户号，卡号，密码，暗码”
		// String line = "04," + GlobalInfo.syjStatus.syjh + "," +
		// GlobalInfo.sysPara.commMerchantId + "," + track2.substring(0, 16) +
		// "," + passwd.toString() + "," + track2.substring(16);
		String line = "04," + GlobalInfo.syjStatus.syjh + "," + GlobalInfo.sysPara.commMerchantId + "," + track2 + "," + passwd.toString() + "," + track2;

		String ret = Gbyw_MzkVipModule.getDefault().sendData(line);
		String[] item = ret.split(",");

		if (item == null)
		{
			shell.close();
			shell.dispose();
			return;
		}

		if (item.length > 0)
		{
			if (!item[0].equals("0"))
			{
				new MessageBox(Gbyw_MzkVipModule.getDefault().getError(item[0]));
				shell.close();
				shell.dispose();
				return;
			}
		}

		if (item.length > 2)
		{
			if (!item[2].equals("1"))
			{
				new MessageBox("该卡不是面值卡!");
				shell.close();
				shell.dispose();
			}
		}

		if (item.length > 3)
		{
			txtScore.setText(item[3]);
			txtAcount.setText(item[1]);
			txtAcount.setEnabled(false);

			txtMoney.setText(String.valueOf(Math.min(Convert.toDouble(item[3]), saleBS.calcPayBalance())));
			txtMoney.setFocus();
			txtMoney.selectAll();

			payObj.setTrack(track2);
			payObj.setPass(passwd.toString());

			return;
		}

		shell.close();
		shell.dispose();
	}

	public void initEvent()
	{
		payName.setText("[" + payObj.paymode.code + "]" + payObj.paymode.name);

		labelYe.setText(payObj.getYeLable());
		txtScore.setEditable(false);

		if (SellType.ISBACK(saleBS.saletype))
			labelMoney.setText("退款金额");
		else
			labelMoney.setText("可付金额");

		payObj.init(this);
		statusInfo.setText("");
	}

	public void keyPressed(KeyEvent e, int key)
	{

	}

	public void keyReleased(KeyEvent e, int key)
	{
		if (ShellIsDisposed)
			return;
		switch (key)
		{
			case GlobalVar.Enter:
				if (e.widget == txtAcount)
				{
					msrRead(e, "", txtAcount.getText(), "");
				}
				else if (e.widget == txtMoney)
				{
					ShellIsDisposed = true;

					if (!SellType.ISBACK(payObj.saleBS.saletype))
					{
						if (Convert.toDouble(txtMoney.getText()) > Convert.toDouble(txtScore.getText()))
						{
							new MessageBox("消费金额不能大于卡余额!");
							break;
						}
					}

					if (!payObj.createSalePay(txtAcount.getText(), txtMoney.getText()))
					{
						ShellIsDisposed = false;
						payObj.salepay = null;

						e.data = "focus";
						txtMoney.setFocus();
						txtMoney.selectAll();

					}
					else
					{
						shell.close();
						shell.dispose();

					}
				}
				break;
			case GlobalVar.Exit:
				shell.close();
				shell.dispose();

				break;
		}
	}
}
