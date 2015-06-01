package custom.localize.Bcsf;

import java.util.Vector;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;

public class Bcsf_FeePayEvent
{
	public Shell shell;
	public PosTable table_pay;
	public PosTable table_detail;
	public CLabel lbl_payname;
	public Text txt_paymoney;
	public CLabel lbl_paytotal;
	public CLabel lbl_unpay;
	private Bcsf_FeePayBS feePayBS;

	private boolean isPayOK = false;

	public Bcsf_FeePayEvent(Bcsf_FeePayForm form, Bcsf_DailyFeeBS feeBS)
	{
		shell = form.shell;
		table_pay = form.table_pay;
		table_detail = form.table_detail;
		lbl_payname = form.lbl_payname;
		txt_paymoney = form.txt_paymoney;
		lbl_paytotal = form.lbl_paytotal;
		lbl_unpay = form.lbl_unpay;

		feePayBS = new Bcsf_FeePayBS(feeBS);

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
		key.inputMode = key.IntegerInput;

		table_pay.addKeyListener(key);
		table_detail.addKeyListener(key);
		txt_paymoney.addKeyListener(key);

		shell.setBounds(((GlobalVar.rec.x - shell.getSize().x) / 2) + 1, (GlobalVar.rec.y - shell.getSize().y) / 2, shell.getSize().x, shell.getSize().y - GlobalVar.heightPL);
		shell.setActive();

		txt_paymoney.setFocus();

		initEvent();
	}

	private void initTable()
	{
		String[] titles = { "付款编码", "付款名称" };
		table_pay.setTitle(titles);
		table_pay.setWidth(new int[] { 100, 150 });
		table_pay.initialize();

		String[] details = { "名称", "卡号", "金额" };
		table_detail.setTitle(details);
		table_detail.setWidth(new int[] { 80, 135, 100 });
		table_detail.initialize();

	}

	public void loadPaycode()
	{
		Vector paycode = new Vector();

		paycode.add(new String[] { "01", "人民币" });
	//	paycode.add(new String[] { "02", "银联卡" });

		table_pay.setTitle(new String[] { "付款编码", "付款名称" });

		for (int i = 0; i < paycode.size(); i++)
			table_pay.addRow((String[]) paycode.elementAt(i));

		table_pay.setSelection(0);
	}

	protected void initEvent()
	{
		initTable();
		loadPaycode();

		txt_paymoney.selectAll();
		txt_paymoney.setText(String.valueOf(feePayBS.getTotalmoney()));
		lbl_paytotal.setText(String.valueOf(feePayBS.getTotalmoney()));
		lbl_unpay.setText(String.valueOf(feePayBS.getLeavemoney()));

		NewKeyListener.sendKey(GlobalVar.ArrowUp);
	}

	public void keyPressed(KeyEvent e, int key)
	{

		switch (key)
		{
			case GlobalVar.ArrowUp:
				table_pay.moveUp();

				break;

			case GlobalVar.ArrowDown:
				table_pay.moveDown();

				break;

			case GlobalVar.PageUp:
				table_pay.moveUp();

				break;
			case GlobalVar.PageDown:
				table_pay.moveDown();

				break;
		}

	}

	public void freshPayDetail()
	{
		table_detail.removeAll();
		
		for(int i=0; i<feePayBS.getPay().size(); i++)
		{
			DailyFeePayDef pay = (DailyFeePayDef)feePayBS.getPay().get(i);
			table_detail.addRow(new String[] { pay.paytype, pay.cardno==null?"":pay.cardno, String.valueOf(pay.paymoney) });
		}
		
		table_detail.setSelection(table_detail.getItemCount() - 1);
		lbl_unpay.setText(String.valueOf(feePayBS.getLeavemoney()));
		txt_paymoney.setText(String.valueOf(feePayBS.getLeavemoney()));
		txt_paymoney.selectAll();
	}

	public void keyReleased(KeyEvent e, int key)
	{
		int index = table_pay.getSelectionIndex();
		if (index == -1)
		{
			table_pay.setSelection(0);
			index = 0;
		}

		// 得到当前付款代码
		String[] ax = table_pay.changeItemVar(index);
		String paycode = ax[0];

		switch (key)
		{
			case GlobalVar.Enter:
				if (isPayOK && feePayBS.calcFeePayComplete())
				{
					new Bcsf_FeeChangeForm().open(feePayBS);
					if (feePayBS.isPayOK())
					{
						shell.close();
						shell.dispose();
					}
					break;
				}
				else
				{
					isPayOK = false;
				}

				if (!isPayOK && feePayBS.addFeePay(paycode, Convert.toDouble(txt_paymoney.getText().trim())))
				{
					freshPayDetail();

					if (feePayBS.calcFeePayComplete())
					{
						isPayOK = true;
						feePayBS.calcChange();
						new Bcsf_FeeChangeForm().open(feePayBS);
						if (feePayBS.isPayOK())
						{
							shell.close();
							shell.dispose();
						}
					}
				}

				break;
			case GlobalVar.ArrowUp:
			case GlobalVar.ArrowDown:
				lbl_payname.setText(ax[1]);
				txt_paymoney.setText(String.valueOf(feePayBS.getLeavemoney()));
				txt_paymoney.selectAll();
				break;
			case GlobalVar.Del:
				if (table_detail.getItemCount() == 0)
					break;

				if (feePayBS.delFeePay(table_detail.getSelectionIndex()))
				{
					table_detail.remove(table_detail.getSelectionIndex());
					table_detail.setSelection(table_detail.getItemCount() - 1);
					lbl_unpay.setText(String.valueOf(feePayBS.getLeavemoney()));
					txt_paymoney.setText(String.valueOf(feePayBS.getLeavemoney()));
					txt_paymoney.selectAll();
				}
				break;
			case GlobalVar.Exit:
				if (table_detail.getItemCount() > 0)
				{
					new MessageBox("请先删除付款再退出");
					break;
				}

				shell.close();
				shell.dispose();
		}
	}
}
