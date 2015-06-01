package custom.localize.Jplm;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;

public class Jplm_PaymentCouponEvent
{
	public StyledText txtStatus = null;
	public Text txtMoney = null;
	public Text txtAccount = null;
	public Text txtScore = null;
	public Text focus = null;
	public Label lblPayName = null;
	public Table table = null;
	public TableEditor tEditor = null;
	public Shell shell = null;
	public Text newEditor = null;
	public SaleBS saleBS = null;
	public Jplm_NewPaymentCoupon payObj = null;
	public NewKeyListener keyNewEditor = null;
	public int[] currentPoint = new int[] { 0, 3 }; // 行号及text所在列
	public Label account = null;
	public boolean payOK = false;

	public Jplm_PaymentCouponEvent(Jplm_PaymentCouponForm pff, Jplm_NewPaymentCoupon pay, SaleBS sale)
	{
		this.txtStatus = pff.status;
		this.txtMoney = pff.txtMoney;
		this.txtAccount = pff.txtAcount;
		this.txtScore = pff.txtScore;
		this.lblPayName = pff.payName;
		this.shell = pff.sShell;

		this.saleBS = sale;
		this.payObj = pay;

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

			public void msrFinish(KeyEvent e, String track1, String track2, String track3)
			{
				msrRead(e, track1, track2, track3);
			}
		};

		FocusListener listener = new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				if (focus != e.widget)
				{
					focus.setFocus();
				}
			}

			public void focusLost(FocusEvent e)
			{
			}
		};

		NewKeyListener key = new NewKeyListener();
		key.event = event;

		// key.inputMode = payObj.getAccountInputMode();
		txtAccount.addKeyListener(key);
		txtAccount.addFocusListener(listener);
		// txtAccount.setData("MSRINPUT");

		keyNewEditor = new NewKeyListener();
		keyNewEditor.event = event;

		NewKeyListener key1 = new NewKeyListener();
		key1.event = event;
		key1.inputMode = key1.DoubleInput;
		txtMoney.addFocusListener(listener);
		txtMoney.addKeyListener(key1);
		txtMoney.setEditable(false);

		init();
	}

	private void init()
	{
		lblPayName.setText("[" + payObj.paymode.code + "]" + payObj.paymode.name);

		double needPay = saleBS.calcPayBalance();

		// 多券种的付款方式里本生就含各个券种的汇率
		txtMoney.setText(saleBS.getPayMoneyByPrecision(needPay, payObj.paymode));
		account.setText(payObj.getDisplayAccountInfo());

		setFocus(this.txtAccount);
	}

	public void afterFormOpenDoEvent()
	{

	}

	public void keyPressed(KeyEvent e, int key)
	{
	}

	public void keyReleased(KeyEvent e, int key)
	{
		try
		{
			switch (key)
			{
			case GlobalVar.Enter:
				if (e.widget == txtAccount)
				{
					msrRead(e, "", txtAccount.getText(), "");
				}
				break;

			case GlobalVar.Pay:
				if (e.widget == txtAccount)
				{
					if (payObj.createPayment(txtAccount.getText().trim(), txtMoney.getText()))
					{
						new MessageBox("创建券付款失败!");

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
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	private void setFocus(Text focus)
	{
		this.focus = focus;

		focus.setFocus();
	}

	public void msrRead(KeyEvent e, String track1, String track2, String track3)
	{
		// 查询返券卡
		if (payObj.findFjk(track1, track2, track3))
		{
			txtScore.setText(payObj.getAccountYe() + "");
			
			txtMoney.setText(String.valueOf(Math.min(saleBS.calcPayBalance(), payObj.getAccountYe())));
			txtMoney.setFocus();
			txtMoney.selectAll();
		}
		else
		{
			txtAccount.setText("请重新刷卡");
			setFocus(txtAccount);
			txtAccount.selectAll();
		}

	}
}
