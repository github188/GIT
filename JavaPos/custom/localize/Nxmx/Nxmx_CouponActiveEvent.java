package custom.localize.Nxmx;

import java.util.Vector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.VerifyEvent;      
import org.eclipse.swt.events.VerifyListener;

import org.eclipse.swt.widgets.Table;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;

public class Nxmx_CouponActiveEvent
{
	protected Text couponCode;
	protected Text txtlimitMoney;
	protected StyledText txtStatus;
	protected Table table;
	public Vector result = new Vector();
	protected Shell shell;
	protected Nxmx_CouponActiveBS couponListBS = null;
	protected boolean isActived = false;
	protected boolean isActiveFromMenu = false;

	public Nxmx_CouponActiveEvent(Nxmx_CouponActiveForm form)
	{
		this.couponCode = form.txtcouponCode;
		this.txtlimitMoney = form.txtlimitMoney;
		this.table = form.table;
		this.txtStatus = form.txtStatus;
		this.shell = form.shell;
		this.isActiveFromMenu = form.isActiveFromMenu;

		/*
		 * if (!isActiveFromMenu) this.maxje =
		 * Double.parseDouble(txtlimitMoney.getText().trim());
		 */

		couponListBS = new Nxmx_CouponActiveBS();

		FocusListener listener = new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				if (isActiveFromMenu && !couponCode.getEnabled())
					txtlimitMoney.setFocus();
				else
					couponCode.setFocus();
			}

			public void focusLost(FocusEvent e)
			{
			}
		};

		table.addFocusListener(listener);

		txtlimitMoney.addVerifyListener(new VerifyListener()
		{
			public void verifyText(VerifyEvent e)
			{
				boolean b = ("0123456789".indexOf(e.text) >= 0);
				e.doit = b;
			}
		});

		// 增加监听器
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

		couponCode.addKeyListener(key);
		txtlimitMoney.addKeyListener(key);

		if (isActiveFromMenu)
		{
			couponCode.setEnabled(false);
			txtStatus.setText("请输入限制金额");
			
			txtlimitMoney.selectAll();
			txtlimitMoney.setFocus();
		}

	}

	public void keyPressed(KeyEvent e, int key)
	{

	}

	public void keyReleased(KeyEvent e, int key)
	{
		switch (key)
		{
			case GlobalVar.ArrowUp:
				if (table.getSelectionIndex() == 0)
					table.setSelection(0);
				else
					table.setSelection(table.getSelectionIndex() - 1);

				break;

			case GlobalVar.ArrowDown:
				if (table.getSelectionIndex() == table.getItemCount() - 1)
					table.setSelection(table.getItemCount() - 1);
				else
					table.setSelection(table.getSelectionIndex() + 1);
				break;
			case GlobalVar.Enter:

				if (e.widget == txtlimitMoney || isActiveFromMenu)
				{
					if (txtlimitMoney.getText().trim().equals(""))
					{
						txtStatus.setText("请输入限制金额");
						return;
					}
					couponCode.setEnabled(true);
					couponCode.selectAll();
					couponCode.setFocus();
					txtlimitMoney.setEnabled(false);
					isActiveFromMenu = false;
					return;
				}

				if (isActived)
				{
					txtStatus.setText("券已全部激活,无法录入\n请按【退出键】关闭窗口");
					return;
				}
				else
					txtStatus.setText("按【确认键】进行券激活");

				// 加参数控制
				if (result != null && result.size() > 4)
				{
					txtStatus.setText("一次只能激活5张");
					return;
				}

				String value = couponCode.getText().trim();
				if (value.equals(""))
					return;
				if (result!=null && result.size()>0)
				{
					if (result.indexOf(value.trim())!=-1)
					{
						txtStatus.setText("当前输入券号已存在,请重新输入");
						return;
					}
				}
				int index = 0;
				int curAddIndex = table.getSelectionIndex();

				if (curAddIndex != -1)
					index = curAddIndex;
				else
					++curAddIndex;

				if (table.getItemCount() == 0)
					index = curAddIndex + 1;
				else
					index = ++curAddIndex + 1;

				TableItem item = new TableItem(table, SWT.BORDER);
				item.setText(new String[] { String.valueOf(index), couponCode.getText().trim(), "待激活" });
				result.add(value);

				table.setSelection(curAddIndex);
				couponCode.setText("");
				break;

			case GlobalVar.Validation: // 对卡券进行激活验证
				try
				{
					if (result == null || result.size() == 0)
					{
						txtStatus.setText("请输入券条码");
						return;
					}

					txtStatus.setText("正在发送激活信息，请稍候......");
					couponCode.setEnabled(false);

					String[] retValue = new String[result.size()];
					boolean retFlag = couponListBS.activeCoupon(result, Double.parseDouble(txtlimitMoney.getText().trim()), retValue);

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem tmpItem = table.getItem(i);
						if (retValue[i] != null)
							tmpItem.setText(2, retValue[i].equals("1") ? "已激活" : "激活失败");
					}

					if (retFlag)
					{
						isActived = true;
						txtStatus.setText("激活成功,请按【退出键】关闭窗口");
					}
					else
					{
						txtStatus.setText("激活失败,请删除激活失败的券,再次重新激活");
					}
					couponCode.setEnabled(true);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					txtStatus.setText("激活发生异常");
				}
				break;
			case GlobalVar.Del:
				if (isActived)
				{
					txtStatus.setText("券已全部激活,无法删除\n请按【退出键】关闭窗口");
					return;
				}

				int curDelIndex = table.getSelectionIndex();

				if (curDelIndex >= 0 && result.size() > 0)
				{
					table.remove(curDelIndex);
					result.removeElementAt(curDelIndex);
					if (table.getItemCount() > 0)
					{
						if (--curDelIndex < 0)
							table.setSelection(0);
						else
							table.setSelection(curDelIndex);
					}
					else
						table.setSelection(0);
				}

				break;

			case GlobalVar.Exit:
				if (result != null && result.size() > 0)
					result.removeAllElements();
				try
				{
					shell.close();
					shell.dispose();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
				break;
		}
	}
}
