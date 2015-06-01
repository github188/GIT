package custom.localize.Hhdl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.swtdesigner.SWTResourceManager;

public class Hhdl_PaymentCouponEvent
{
	public StyledText txtStatus = null;
	public Text txtMoney = null;
	public Text txtAccount = null;
	public Text focus = null;
	public Label lblPayName = null;
	public Table table = null;
	public TableEditor tEditor = null;
	public Shell shell = null;
	public Text newEditor = null;
	public SaleBS saleBS = null;
	public Hhdl_PaymentCoupon payObj = null;
	public NewKeyListener keyNewEditor = null;
	public int[] currentPoint = new int[] { 0, 3 }; // 行号及text所在列
	public Label account = null;
	public boolean payOK = false;

	public Hhdl_PaymentCouponEvent(Hhdl_PaymentCouponForm pff, Hhdl_PaymentCoupon pay, SaleBS sale)
	{
		this.txtStatus = pff.getTxtStatus();
		this.txtMoney = pff.getTxtMoney();
		this.txtAccount = pff.getTxtAccount();
		this.lblPayName = pff.getLblPayName();
		this.table = pff.getTable();
		this.account = pff.account;
		this.shell = pff.getShell();

		this.saleBS = sale;
		this.payObj = pay;

		tEditor = new TableEditor(table);
		tEditor.horizontalAlignment = SWT.LEFT;
		tEditor.grabHorizontal = true;
		tEditor.minimumWidth = 100;

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

		// 原本在afterFormOpenDoEvent，现在改在这里，主要是payObj.getAccountInputMode()返回配置文件里的类型
		// payObj.choicFjkType();

		NewKeyListener key = new NewKeyListener();
		key.event = event;

		// key.inputMode = payObj.getAccountInputMode();
		txtAccount.addKeyListener(key);
		txtAccount.addFocusListener(listener);
		// txtAccount.setData("MSRINPUT");

		setFocus(this.txtAccount);

		keyNewEditor = new NewKeyListener();
		keyNewEditor.event = event;

		NewKeyListener key1 = new NewKeyListener();
		key1.event = event;
		key1.inputMode = key1.DoubleInput;
		txtMoney.addFocusListener(listener);
		txtMoney.addKeyListener(key1);
		txtMoney.setEditable(false);
		table.addKeyListener(keyNewEditor);

		init();
	}

	private void init()
	{
		payObj.initVetor();
		lblPayName.setText("[" + payObj.paymode.code + "]" + payObj.paymode.name);

		double needPay = saleBS.calcPayBalance();

		// 多券种的付款方式里本生就含各个券种的汇率
		txtMoney.setText(saleBS.getPayMoneyByPrecision(needPay, payObj.paymode));
		account.setText(payObj.getDisplayAccountInfo());

		this.txtStatus.setText("券录入完毕后，请按【付款键】进行券扣款处理");
	}

	public void afterFormOpenDoEvent()
	{

	}

	public void keyPressed(KeyEvent e, int key)
	{
		switch (key)
		{
			case GlobalVar.ArrowUp:
				if (table.getSelectionIndex() - 1 <= 0)
					table.setSelection(0);
				else
					table.setSelection(table.getSelectionIndex() - 1);

				if (payOK)
					this.txtStatus.setText(payObj.getCouponTypeMoneyInfo(table.getSelectionIndex()));
				else
					setEditColumn(table.getSelectionIndex());
				break;

			case GlobalVar.ArrowDown:
				if (table.getSelectionIndex() + 1 >= table.getItemCount())
					table.setSelection(table.getItemCount() - 1);
				else
					table.setSelection(table.getSelectionIndex() + 1);

				if (payOK)
					this.txtStatus.setText(payObj.getCouponTypeMoneyInfo(table.getSelectionIndex()));
				else
					setEditColumn(table.getSelectionIndex());

				break;
		}

	}

	public void keyReleased(KeyEvent e, int key)
	{
		try
		{
			switch (key)
			{
				case GlobalVar.Enter:
					enterInput(e);

					break;

				case GlobalVar.Pay:
					if (e.widget == txtAccount)
					{
						if (payOK)
						{
							shell.close();
							shell.dispose();
						}
						else
						{
							ProgressBox tip = new ProgressBox();

							try
							{
								tip.setText("正在计算用券，请稍候...");
								if (!payObj.createCouponPay())
								{ //
									new MessageBox("券计算出现错误");

									shell.close();
									shell.dispose();
								}
								else
								{
									payOK = true;
									txtAccount.setText("券已处理完毕,禁止录入");
									txtAccount.selectAll();

									if (!payObj.getUseabledcoupon())
										new MessageBox("当前交易不满足收券条件");

									refreshTable();
									this.txtStatus.setText("券计算完毕,请上下移动光标仔细核对扣款数据\n\n按【付款键】结束付款");
								}
							}
							finally
							{
								tip.close();
								tip = null;
							}
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

	private void setEditColumn(int index)
	{
		try
		{
			if (index == -1)
				return;

			if (tEditor.getEditor() != null)
				tEditor.getEditor().dispose();

			if (table.getItemCount() <= 0)
				return;

			TableItem item = table.getItem(index);

			if (item == null)
				return;

			newEditor = new Text(table, SWT.NONE | SWT.RIGHT);
			newEditor.setTextLimit(15);
			newEditor.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

			// 将第四列值显示上去
			newEditor.setText(table.getItem(index).getText(3));

			newEditor.setFocus();
			newEditor.selectAll();

			tEditor.setEditor(newEditor, item, 3);

			keyNewEditor.inputMode = keyNewEditor.DoubleInput;
			newEditor.addKeyListener(keyNewEditor);

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
		if (!payObj.needFindFjk(track1, track2, track3))
		{
			setFocus(txtMoney);
			txtMoney.setSelection(0, txtMoney.getText().length());
			return;
		}

		// 查询返券卡
		if (payObj.findFjk(track1, track2, track3))
		{
			refreshTable();

			txtAccount.setText("请刷下一张卡");
			setFocus(txtAccount);
			txtAccount.selectAll();

		}
		else
		{
			txtAccount.setText("请重新刷卡");
			setFocus(txtAccount);
			txtAccount.selectAll();
		}

	}

	public void refreshTable()
	{
		table.removeAll();

		if (payObj.getCoupons().size() == 0)
			return;

		for (int i = 0; i < payObj.getCoupons().size(); i++)
		{
			Hhdl_CouponDef tmpCoupon = (Hhdl_CouponDef) payObj.getCoupons().get(i);
			String[] line = new String[] { tmpCoupon.type, tmpCoupon.cardno, String.valueOf(tmpCoupon.money), String.valueOf(ManipulatePrecision.doubleConvert(tmpCoupon.isused ? tmpCoupon.money - tmpCoupon.excep : 0.0, 2, 1)), String.valueOf(tmpCoupon.excep) };
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(line);
			table.setSelection(table.getItemCount() - 1);
		}
	}

	public void enterInput(KeyEvent e)
	{
		if (e.widget == txtAccount)
		{
			if (payOK)
				return;

			msrRead(e, "", txtAccount.getText(), "");
		}
		else if (e.widget == newEditor)
		{
			Text text = (Text) tEditor.getEditor();
			if (text == null)
				return;

			String inputmoney = text.getText().trim();

			if (!inputmoney.equals(""))
			{
				if (Convert.toDouble(inputmoney) > payObj.getCouponMoney(table.getSelectionIndex()))
				{
					new MessageBox("当前输入的金额大于券面值");
					table.getItem(table.getSelectionIndex()).setText(3, "0.0");

					newEditor.setFocus();
					newEditor.selectAll();
					return;
				}

				table.getItem(table.getSelectionIndex()).setText(3, text.getText());
				payObj.setCouponEnablemoney(table.getSelectionIndex(), Convert.toDouble(inputmoney));

				if (tEditor.getEditor() != null)
					tEditor.getEditor().dispose();

				txtAccount.setFocus();
				txtAccount.selectAll();
			}
		}
	}
}
