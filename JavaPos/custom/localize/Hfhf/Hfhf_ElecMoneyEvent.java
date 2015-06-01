package custom.localize.Hfhf;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.CustomerDef;
import com.swtdesigner.SWTResourceManager;

import custom.localize.Hfhf.Hfhf_ElecMoney.ElecMoney;

public class Hfhf_ElecMoneyEvent
{
	public Shell shell = null;
	public StyledText txtRuleInfo;
	public Text accountTxt;
	public TableEditor tEditor;
	public Text newEditor;
	public Text moneyTxt;
	public Label payName;
	public Table table;
	public NewKeyListener keyNewEditor;

	private SaleBS sale;
	private Hfhf_PaymentElecMoney payObj;
	private Vector elecMoneyList;

	public Hfhf_ElecMoneyEvent(Hfhf_ElecMoneyForm form, Hfhf_PaymentElecMoney payment, SaleBS sale)
	{
		this.shell = form.shell;
		this.payName = form.payName;
		this.table = form.table;
		this.accountTxt = form.accountTxt;
		this.moneyTxt = form.moneyTxt;
		this.txtRuleInfo = form.txtRuleInfo;

		this.sale = sale;
		this.payObj = payment;

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

		keyNewEditor = new NewKeyListener();
		keyNewEditor.event = event;
		keyNewEditor.inputMode = keyNewEditor.DoubleInput;

		NewKeyListener key = new NewKeyListener();
		key.event = event;
		key.inputMode = payObj.getAccountInputMode();

		accountTxt.addKeyListener(key);
		accountTxt.setData("MSRINPUT");

		NewKeyListener key1 = new NewKeyListener();
		key1.event = event;
		key1.inputMode = key1.DoubleInput;
		moneyTxt.addKeyListener(key1);

		table.addKeyListener(keyNewEditor);
		table.addListener(SWT.MeasureItem, new Listener()
		{ // 向表格增加一个SWT.MeasureItem监听器，每当需要单元内容的大小的时候就会被调用。
			public void handleEvent(Event event)
			{
				event.width = table.getGridLineWidth(); // 设置宽度
				event.height = (int) Math.floor(event.gc.getFontMetrics().getHeight() * 1.5); // 设置高度为字体高度的1.5倍
			}
		});

		shell.setLocation((GlobalVar.rec.x - shell.getSize().x) / 2, (GlobalVar.rec.y - shell.getSize().y) / 2);

		initEvent();
	}

	public void initEvent()
	{
		payName.setText("[" + payObj.paymode.code + "]" + payObj.paymode.name);

		moneyTxt.setText(String.valueOf(sale.calcPayBalance()));
		moneyTxt.selectAll();
		moneyTxt.setFocus();

		if (sale.curCustomer != null)
		{
			accountTxt.setText(sale.curCustomer.code);
			accountTxt.setEnabled(false);
		}
		else
		{
			accountTxt.setText("请刷商之都贵宾卡");
			accountTxt.selectAll();
			accountTxt.setFocus();
		}

		txtRuleInfo.setText("回车查找电子币\n上下移动可查看各电子币收款规则\n结束付款请按【确认键】");
	}

	public void initTable()
	{
		for (int i = 0; i < elecMoneyList.size(); i++)
		{
			ElecMoney itemMoney = (ElecMoney) elecMoneyList.get(i);
			TableItem tableItem = new TableItem(table, SWT.NONE);
			String[] item = new String[4];
			item[0] = itemMoney.EmoneyId;
			item[1] = String.valueOf(itemMoney.Balance) + "/" + String.valueOf(itemMoney.UseableAmount);
			item[2] = "0.00";
			item[3] = "";
			tableItem.setText(item);
		}

	}

	public void msrRead(KeyEvent e, String track1, String track2, String track3)
	{
		String cardno = Hfhf_CrmModule.getDefault().getCardNo(track2.replace('/', '?'));

		if (cardno != null)
		{
			CustomerDef cust = new CustomerDef();
			if (Hfhf_CrmModule.getDefault().getCustomer(cust, cardno))
			{
				if (cust.code == null || cust.code.trim().equals(""))
				{
					new MessageBox("查询的会员卡信息无效!");

					accountTxt.setText("请刷商之都贵宾卡");
					accountTxt.selectAll();
					accountTxt.setFocus();

					return;
				}

				accountTxt.setText(cust.code);
				accountTxt.setEnabled(false);

				moneyTxt.selectAll();
				moneyTxt.setFocus();

			}
		}
	}

	public void keyPressed(KeyEvent e, int key)
	{
		switch (key)
		{
		case GlobalVar.ArrowUp:
			if (table.getSelectionIndex() != 0)
			{
				// new MessageBox("up:" + table.getSelectionIndex());
				findLocation(table.getSelectionIndex() - 1);
			}
			else
				findLocation(0);
			break;

		case GlobalVar.ArrowDown:
			if (table.getSelectionIndex() == table.getItemCount() - 1)
			{
				// new MessageBox("down:" + table.getSelectionIndex());
				findLocation(table.getItemCount() - 1);
			}
			else
				findLocation(table.getSelectionIndex() + 1);
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
				if (e.widget == accountTxt)
				{
					msrRead(e, "", accountTxt.getText(), "");
				}
				else if (e.widget == moneyTxt)
				{
					moneyTxt.setText(String.valueOf(sale.calcPayBalance()));

					if (table.getItemCount() > 0) table.removeAll();

					elecMoneyList = Hfhf_CrmModule.getDefault().getElecMoneys(accountTxt.getText().trim());
					if (elecMoneyList == null)
					{
						shell.close();
						shell.dispose();
						return;
					}

					initTable();

					if (table.getItemCount() > 0)
					{
						table.setFocus();
						table.setSelection(0);
						findLocation(0);
					}
				}
				else if (e.widget == newEditor)
				{
					Text text = (Text) tEditor.getEditor();

					if (!text.getText().trim().equals(""))
					{
						double money = Convert.toDouble(text.getText().trim());
						text.setText(payObj.saleBS.getPayMoneyByPrecision(money, payObj.paymode));

						if (createElecMoneyPay(table.getSelectionIndex(), text.getText().trim()))
						{
							if (sale.calcPayBalance() == 0)
							{
								shell.close();
								shell.dispose();
							}
							else
							{
								moneyTxt.setText(String.valueOf(sale.calcPayBalance()));
								table.getItem(table.getSelectionIndex()).setText(3, text.getText().trim());

								if (table.getSelectionIndex() != elecMoneyList.size() - 1) findLocation(table.getSelectionIndex() + 1);
							}
						}
						else
						{
							new MessageBox("创建电子币付款失败");
						}
					}
					else
					{
						text.setFocus();
						text.selectAll();
					}
				}

				break;

			case GlobalVar.MemberGrant:
				if (e.widget == moneyTxt)
				{
					accountTxt.setEnabled(true);
					accountTxt.setText("请刷商之都贵宾卡");

					e.data = "focus";
					accountTxt.setFocus();
					accountTxt.selectAll();
				}
				break;
			case GlobalVar.Validation:
			case GlobalVar.Exit:
				shell.close();
				shell.dispose();
				break;

			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("交易发生异常"+ex.getMessage());
			if (shell != null && !shell.isDisposed())
			{
				shell.close();
				shell.dispose();
			}
		}
	}

	public boolean createElecMoneyPay(int index, String money)
	{
		ElecMoney itemMoney = (ElecMoney) elecMoneyList.get(index);

		if (itemMoney == null) return false;

		if (itemMoney.ispay)
		{
			new MessageBox(itemMoney.EmoneyId + "已付过款\n请完成此次付款后,再次选择电子币支付方式!");
			return false;
		}

		String validMoney = table.getItem(index).getText(2);
		if (Convert.toDouble(money) > Convert.toDouble(validMoney))
		{
			new MessageBox("当前输入金额过大");
			return false;
		}

		if (new ManipulateDateTime().compareDate(ManipulateDateTime.getCurrentDateBySign(), itemMoney.EffectiveDate) < 0)
		{
			new MessageBox("当前日期小于生效日期(" + itemMoney.EffectiveDate + ")");
			return false;
		}

		if (new ManipulateDateTime().compareDate(ManipulateDateTime.getCurrentDateBySign(), itemMoney.InvalidDate) > 0)
		{
			new MessageBox("该张电子币已失效(" + itemMoney.InvalidDate + ")");
			return false;
		}

		if (payObj.createPayment(accountTxt.getText(), itemMoney.EmoneyId, money))
		{
			itemMoney.haspay = money;
			itemMoney.ispay = true;

			new MessageBox(itemMoney.EmoneyId + "付款成功");
			return true;
		}

		return false;
	}

	protected double getAvailableMoney(ElecMoney itemMoney)
	{
		return payObj.getAvailableMoney(Convert.toDouble(moneyTxt.getText().trim()), itemMoney.UseableAmount, itemMoney.SaleAmount, itemMoney.PayAmount);
	}

	protected String getAvailableRuleInfo(ElecMoney itemMoney)
	{
		return payObj.getElecMoneyPayRuleInfo(itemMoney.EmoneyId, Convert.toDouble(moneyTxt.getText().trim()), itemMoney.UseableAmount, itemMoney.SaleAmount, itemMoney.PayAmount);
	}

	public void findLocation(int row)
	{
		if (elecMoneyList == null || elecMoneyList.size() == 0 || elecMoneyList.size() - 1 < row)
		{
			new MessageBox("elecMoneyList error");
			return;
		}

		ElecMoney itemMoney = (ElecMoney) elecMoneyList.get(row);
		if (itemMoney == null)
		{
			new MessageBox("itemMoney is null");
			return;
		}

		Control oldEditor = tEditor.getEditor();
		if (oldEditor != null) oldEditor.dispose();

		TableItem item = table.getItem(row);
		table.setSelection(row);
		String avalMoney = String.valueOf(getAvailableMoney(itemMoney));
		item.setText(2, avalMoney);
		txtRuleInfo.setText(getAvailableRuleInfo(itemMoney));

		if (itemMoney.ispay)
		{
			item.setText(3, itemMoney.haspay);
			return;
		}

		newEditor = new Text(table, SWT.NONE | SWT.RIGHT);
		newEditor.addKeyListener(keyNewEditor);

		newEditor.setTextLimit(15);
		newEditor.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		newEditor.setText(avalMoney);

		tEditor.setEditor(newEditor, item, 3);
		newEditor.setFocus();
		newEditor.selectAll();
	}
}
