package custom.localize.Bcsf;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;

public class Bscf_DailyFeeEvent
{
	public Shell shell;
	public CLabel lbl_var_sky;
	public CLabel lbl_var_skdate;
	public CLabel lbl_var_billstatus;
	public PosTable table;
	public CLabel lbl_var_yshj;
	public CLabel lbl_var_sshj;
	public Text txt_czs;
	public Text txt_month;
	public Bcsf_DailyFeeBS feeBS;

	public Bscf_DailyFeeEvent(Bcsf_DailyFeeForm form)
	{
		shell = form.shell;
		lbl_var_sky = form.lbl_var_sky;
		lbl_var_skdate = form.lbl_var_skdate;
		lbl_var_billstatus = form.lbl_var_billstatus;
		table = form.table_detail;

		lbl_var_yshj = form.lbl_var_yshj;
		lbl_var_sshj = form.lbl_var_sshj;

		txt_czs = form.txt_czs;
		txt_month = form.txt_month;

		feeBS = new Bcsf_DailyFeeBS();

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

		table.addKeyListener(key);
		txt_czs.addKeyListener(key);
		txt_month.addKeyListener(key);

		shell.setBounds((GlobalVar.rec.x - shell.getSize().x) / 2, (GlobalVar.rec.y - shell.getSize().y) / 2, shell.getSize().x, shell.getSize().y - GlobalVar.heightPL);

		initFormEvent();
	}

	protected void initFormEvent()
	{
		lbl_var_sky.setText(GlobalInfo.posLogin.gh);
		lbl_var_skdate.setText(ManipulateDateTime.getCurrentDate());// + " " +
																	// ManipulateDateTime.getCurrentTime());
		lbl_var_billstatus.setText("");

		table.removeAll();

		lbl_var_yshj.setText("");
		lbl_var_sshj.setText("");

		txt_czs.setText("");
		txt_month.setText("");

		txt_czs.setFocus();
	}

	public void keyPressed(KeyEvent e, int key)
	{
	}

	public void keyReleased(KeyEvent e, int key)
	{
		StringBuffer sb = new StringBuffer();

		switch (key)
		{

		// 输入某项金额
			case GlobalVar.Pay:
				String pay = "";

				if (!isItemModify(table.getSelectionIndex()))
				{
					new MessageBox("该项费用已处理，不允许修改");
					break;
				}

				String tip = table.getItem(table.getSelectionIndex()).getText(1);
				if (new TextBox().open("请输入项目编号为【" + tip + "】的缴费金额", "", "输入的缴费金额请勿大于该项目的应收金额", sb, 0, 0, false))
				{
					pay = sb.toString();
					updateFeePay(table.getSelectionIndex(), pay);
				}
				break;

			case GlobalVar.Enter:
				if (e.getSource() == txt_czs)
				{
					e.data = "focus";
					if (!txt_czs.getText().trim().equals(""))
					{
						feeBS.setTenantid(txt_czs.getText().trim());
						txt_month.selectAll();
						txt_month.setFocus();
					}
				}
				else if (e.getSource() == txt_month)
				{
					e.data = "focus";
					if (!txt_month.getText().trim().equals(""))
					{
						feeBS.setPaymonth(txt_month.getText().trim());
						if (feeBS.getFeeBill())
						{
							if (!showBill(feeBS.getBillContent()))
								return;
						}
						else
						{
							new MessageBox("未找到单据，请重新查找");
							txt_czs.selectAll();
							txt_czs.setFocus();
						}
					}
				}

				break;
			// 确认提交
			case GlobalVar.Validation:
				if (!isStartPay())
				{
					new MessageBox("该单中有若干项费用已缴\n为避免因修改而起帐目混乱\n请联系信息部门!");
					break;
				}
				if (feeBS.getPaymoney() == 0)
				{
					new MessageBox("总应缴金额不能为0");
					break;
				}

				new Bcsf_FeePayForm().open(feeBS);
				if (feeBS.isPayOK())
				{
					feeBS.clear();
					initFormEvent();
				}
				break;

			case GlobalVar.Clear:
				if (table.getItemCount() > 0)
				{
					if (new MessageBox("确定清除整单吗?", null, true).verify() == GlobalVar.Key1)
					{
						feeBS.clear();
						initFormEvent();
					}
				}
				break;
			// 控制表格
			case GlobalVar.ArrowUp:
				if (table.getSelectionIndex() == 0)
					table.setSelection(0);
				else
					table.setSelection(table.getSelectionIndex() - 1);

				freshItemStatus(table.getSelectionIndex());

				break;

			case GlobalVar.ArrowDown:
				if (table.getSelectionIndex() == table.getItemCount() - 1)
					table.setSelection(table.getItemCount() - 1);
				else
					table.setSelection(table.getSelectionIndex() + 1);

				freshItemStatus(table.getSelectionIndex());
				break;

			// 控制查询条件
			case GlobalVar.ArrowLeft:
				if (e.getSource() == txt_month)
				{
					e.data = "focus";
					txt_czs.selectAll();
					txt_czs.setFocus();
				}
				break;

			case GlobalVar.ArrowRight:
				if (e.getSource() == txt_czs)
				{
					e.data = "focus";
					txt_month.selectAll();
					txt_month.setFocus();
				}

				break;

			case GlobalVar.Exit:

				if (table.getItemCount() > 0 && (new MessageBox("单据未提交,确定退出?", null, true).verify() != GlobalVar.Key1))
					break;

				shell.close();
				shell.dispose();

				break;
		}
	}

	protected void freshItemStatus(int index)
	{
		try
		{
			Vector bill = feeBS.getBillContent();

			if (bill == null || bill.size() == 0)
				return;

			DailyFeeItemDef fee = (DailyFeeItemDef) bill.get(index);
			if (fee != null)
				lbl_var_billstatus.setText(fee.flag == '0' ? "未记帐" : "已记帐");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	protected void freshHJLabel()
	{
		try
		{
			Vector bill = feeBS.getBillContent();

			if (bill == null || bill.size() == 0)
				return;
			lbl_var_yshj.setText(String.valueOf(feeBS.getPayablemoney()));
			lbl_var_sshj.setText(String.valueOf(feeBS.getPaymoney()));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			lbl_var_yshj.setText("");
			lbl_var_sshj.setText("");
		}
	}

	public boolean isItemModify(int index)
	{
		Vector bill = feeBS.getBillContent();

		if (bill == null || bill.size() == 0)
			return false;

		DailyFeeItemDef fee = (DailyFeeItemDef) bill.get(index);

		if (fee.flag == '1' || (fee.listno > 0 && String.valueOf(fee.listno).length() > 8))
			return false;
		return true;
	}

	public boolean isStartPay()
	{
		Vector bill = feeBS.getBillContent();

		if (bill == null || bill.size() == 0)
			return false;

		for (int i = 0; i < bill.size(); i++)
		{
			DailyFeeItemDef fee = (DailyFeeItemDef) bill.get(i);
			if ((fee.listno > 0 && String.valueOf(fee.listno).length() > 8))
				return false;
		}
		return true;
	}

	protected boolean updateFeePay(int index, String value)
	{
		try
		{
			if (index < 0 || Convert.toDouble(value.trim()) <= 0)
				return false;

			Vector bill = feeBS.getBillContent();

			if (bill == null || bill.size() == 0)
				return false;

			DailyFeeItemDef fee = (DailyFeeItemDef) bill.get(index);

			if (fee == null)
				return false;

			// 对金额作校验,当应付为0时，不对实付进行任何控制
			if (fee.payablemoney != 0 && Convert.toDouble(value.trim()) > fee.payablemoney)
			{
				new MessageBox("输入的实付金额大于应收金额,请重新输入");
				return false;
			}

			TableItem item = table.getItem(index);
			item.setText(8, String.valueOf(Convert.toDouble(value.trim())));
			fee.paymoney = Convert.toDouble(value.trim());
			fee.editor = GlobalInfo.posLogin.name;
			fee.editdate = ManipulateDateTime.getCurrentDate() + " " + ManipulateDateTime.getCurrentTime();

			freshHJLabel();
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	protected boolean showBill(Vector bill)
	{
		try
		{

			table.removeAll();

			if (bill == null || bill.size() == 0)
				return false;

			for (int i = 0; i < bill.size(); i++)
			{
				DailyFeeItemDef fee = (DailyFeeItemDef) bill.get(i);

				TableItem item = new TableItem(table, SWT.BORDER);

				String value[] = { String.valueOf(i + 1), fee.incomeid, fee.name, fee.tenantid, fee.tenantname, fee.shopid, fee.payablemonth, String.valueOf(fee.payablemoney), String.valueOf(fee.paymoney) };

				item.setText(value);
			}

			table.setFocus();
			if (table.getItemCount() > 0)
				table.setSelection(0);

			freshItemStatus(table.getSelectionIndex());
			freshHJLabel();

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	protected boolean isModifyFee()
	{
		return true;
	}
}
